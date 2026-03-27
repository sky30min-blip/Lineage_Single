package lineage.network;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import lineage.bean.event.Event;
import lineage.network.netty.lineage.CodecFactory;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.client.C_ServerVersion;
import lineage.network.packet.server.S_Cryptkey;
import lineage.network.packet.server.S_Disconnect;
import lineage.share.Lineage;
import lineage.share.Socket;
import lineage.share.System;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public final class LineageServer {
	// netty
	static private ServerBootstrap sb;
	// 접속한 클라이언트 목록.
	private static Map<Channel, LineageClient> list;
	// 사용후 버려진 클라이언트 객체들 재사용을위해 사용.
	private static List<LineageClient> pool;
	private static List<Event> pool_pakcet;
	// 서버 날씨
	static public int weather;
	
	/**
	 * 서버 활성화에 사용되는 초기화 함수.
	 */
	static public void init() throws Exception {
		TimeLine.start("Server..");

		list = new HashMap<Channel, LineageClient>();
		pool = new ArrayList<LineageClient>();
		pool_pakcet = new ArrayList<Event>();

		sb = new ServerBootstrap( new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()) );
		sb.setPipelineFactory( new CodecFactory() );
		// http에서 사용하는건데.. 기본값이 먼지 몰라서 인위적으로 false로 설정.
		// 서버 성능을 높이기위해 클라가 접속을 끊더라도 세션을 유지하는 알고리즘임..필요없으므로 false
		sb.setOption("child.keepAlive", false);
		// Naggle 비활성.
		sb.setOption("child.tcpNoDelay", true);
		// 받을 패킷의 최대양.
		sb.setOption("child.receiveBufferSize", Socket.packet_recv_max);
		// 기본 ChannelBuffer를 Little Endian으로 설정
		//  : Netty는 자바 기반이라서 기본이 Big Endian이다.
		//  : C로 구성된 서버의 경우 기본이 Little Endian이다.
		//  : ascii 문자를 주고 받을 경우에는 Endian이 무관해 보이지만, integer 등을 보내려면 Endian을 반드시 고려해야 한다.
		sb.setOption("bufferFactory", HeapChannelBufferFactory.getInstance(ChannelBuffers.LITTLE_ENDIAN));
		// Connection Timeout 설정 ddos때문에 1.0초로 설정.
		sb.setOption("connectTimeoutMillis", 1000);
		// 서버 활성화.
		sb.bind(new InetSocketAddress(Socket.PORT));

		TimeLine.end();
	}

	/**
	 * 서버 소켓 닫기 처리 함수.
	 * @throws Exception
	 */
	static public void close() {
		TimeLine.start("LineageServer Close...");
		
		try {
			//
			for(LineageClient c : getList())
				c.close();
			list.clear();
			sb = null;
		} catch (Exception e) { }
		
		TimeLine.end();
	}
	
	/**
	 * 클라이언트 접속처리.
	 * @param c
	 */
	public static void connect(Channel socket) {
		// 키 생성
		Long key = Util.random(0, Long.MAX_VALUE);
		// Client 객체 생성.
		LineageClient c = getPool();
		socket.setAttachment(c);
		// 관련 정보 초기화.
		c.update(socket, key);
		// 관리목록에 등록.
		append(c);
		//
		if(Lineage.server_version > 200) {
			// 블로우피쉬에 사용되는키 전송.
			c.toSender( S_Cryptkey.clone(BasePacketPooling.getPool(S_Cryptkey.class), key) );
		} else {
			BasePacketPooling.setPool( C_ServerVersion.clone(BasePacketPooling.getPool(C_ServerVersion.class), null, 0).init(c) );
		}
	}
	
	/**
	 * 클라이언트 종료처리.
	 * @param c
	 */
	public static void close(LineageClient c) {
		// 관리목록 제거.
		LineageClient temp = remove(c);
		if(temp == null)
			return;
		// 클라 메모리 처리.
		c.close();
		// 재사용 등록.
		setPool(c);
	}
	
	/**
	 * 로그인한 사용자 계정 uid값으로 클라 찾기.
	 * @param account_uid
	 * @return
	 */
	static public LineageClient find(int account_uid) {
		if (account_uid > 0) {
			for (LineageClient c : getList()) {
				if (c.getAccountUid() == account_uid)
					return c;
			}
		}
		return null;
	}

	private static LineageClient getPool(){
		if (Lineage.pool_client) {
			synchronized (pool) {
				LineageClient c = null;
				if(pool.size()>0){
					c = pool.get(0);
					pool.remove(0);
				}else{
					c = new LineageClient();
				}
				return c;
			}
		} else {
			return new LineageClient();
		}
	}
	
	private static void setPool(LineageClient c) {
		if (Lineage.pool_client) {
			synchronized (pool) {
				if(pool.contains(c) == false)
					pool.add(c);
			}
		}
	}
	
	private static void append(LineageClient c) {
		synchronized (list) {
			list.put(c.getSocket(), c);
		}
	}
	
	private static LineageClient remove(LineageClient c) {
		synchronized (list) {
			return list.remove(c.getSocket());
		}
	}
	
	public static List<LineageClient> getList() {
		synchronized (list) {
			return new ArrayList<LineageClient>(list.values());
		}
	}
	
	public static Event getPoolPacket(Class<?> c) {
		synchronized (pool_pakcet) {
			Event e = null;
			for(Event temp : pool_pakcet){
				if(temp.getClass().equals(c)) {
					e = temp;
					break;
				}
			}
			if(e != null)
				pool_pakcet.remove(e);
			return e;
		}
	}
	
	public static void setPoolPacket(List<Event> list) {
		synchronized (pool_pakcet) {
			pool_pakcet.addAll(list);
		}
	}

	/**
	 * 타이머에서 주기적으로 호출되는 함수.
	 */
	public static void toTimer(long time) {
		for (PcInstance pc : World.getPcList())
			findTwoClient(pc);
		
		// 제거해도 되는 객체가 존재하는지 확인.
		for (LineageClient c : getList()) {
			if (c.isDelete(time))
				close(c);
		}				
	}
	
	/**
	 * 주기적으로 한개의 계정이 두개의 클라이언트에서 접속되었는지 체크
	 * @param
	 * @return
	 * 2017-09-04
	 * by all_night.
	 */
	static public void findTwoClient(PcInstance pc) {
		int check;
		if (pc.getAccountUid() > 0) {
			check = 0;
			for (LineageClient c : getList()) {
				if (c != null) {
					if (c.getAccountUid() == pc.getAccountUid())
						check += 1;
					// 클라이언트 종료
					if (check > 1) {
						System.println(String.format("[동시 접속시도] 계정: %s  캐릭터: %s", pc.getAccountId(), pc.getName()));
						pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
						break;
					}
				}
			}
		}
	}
	
	static public boolean findWorldPc(String ip) {
		if (Lineage.ip_in_game_count > 0) {
			int check;
			if (ip != null) {
				check = 0;
				for (PcInstance use : World.getPcList()) {
					if (use != null && use.getClient() != null) {
						if (use.getClient().getAccountIp().equalsIgnoreCase(ip)) {
							if (++check >= Lineage.ip_in_game_count) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 로그에 기록할 정보 만들어서 리턴.
	 * 	사용된 정보 초기화 처리도 함게함.
	 * @return
	 */
	static public String getLogNetwork(){
		StringBuffer sb = new StringBuffer();
		for(LineageClient c : getList()){
			int recv = c.getRecvLength();
			int send = c.getSendLength();

			// 기록.
			sb.append( String.format("%s|%d|%d\t", c.getAccountIp(), recv, send) );

			// 초당 전송량을 오바햇을경우 제거처리.
			if(send > Socket.packet_recv_max){
				System.println( String.format("%s 초당 패킷량 제한 오바로 강제 종료됨.", c.getAccountIp()) );
				System.println( String.format(" : %d => %d", Socket.packet_recv_max, c.getSendLength()) );
				close(c);
			}else{
				c.setRecvLength(0);
				c.setSendLength(0);
			}
		}
		return sb.toString();
	}

	static public int getPoolSize(){
		return pool.size();
	}

	static public int getClientSize(){
		return list.size();
	}

}
