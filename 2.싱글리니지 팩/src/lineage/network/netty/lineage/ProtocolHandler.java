package lineage.network.netty.lineage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.Main;
import lineage.bean.database.Ip;
import lineage.database.BadIpDatabase;
import lineage.gui.GuiMain;
import lineage.network.LineageClient;
import lineage.network.LineageServer;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ProtocolHandler extends SimpleChannelUpstreamHandler {

	// 무한접속형 섭폭 필터링 목록.
	static private Map<String, Ip> list_dos = new HashMap<String, Ip>();
	
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		// 서버가 종료되는 상황에서는 클라 요청을 차단.
		if(Main.running == false) {
			e.getChannel().close();
			return;
		}
		// 접속방식 체크. 잘못된 접근이면 클라 요청 차단.
		if(isBadClient(e.getChannel())) {
			e.getChannel().close();
			return;
		}
		// 클라이언트 최대접속수용 확인.
		if(LineageServer.getClientSize() >= GuiMain.CLIENT_MAX){
			e.getChannel().close();
			return;
		}
	}
	
	/**
	 * 클라 접속처리 함수.
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		// 접속 허용된 클라만 처리.
		if(e.getChannel().isConnected())
			LineageServer.connect(e.getChannel());
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent me) {
		//
		if (me.getMessage() == null)
			return;
		//
		Object o = me.getChannel().getAttachment();
		if (o == null)
			return;
		//
		if (me.getMessage() instanceof List) {
			LineageClient c = (LineageClient) o;
			List<byte[]> list = (List<byte[]>) me.getMessage();
			for (byte[] data : list)
				try {
					c.toPacket(data);
				} catch (Exception e) {
				}
			list.clear();
			list = null;
		}
	}

	/**
	 * 클라 종료처리 함수.
	 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		Object o = e.getChannel().getAttachment();
		if(o == null)
			return;

		LineageServer.close( (LineageClient)o );
//		System.out.println("channelClosed : "+e);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Object o = e.getChannel().getAttachment();
		if(o == null)
			return;

		LineageServer.close( (LineageClient)o );
//		System.out.println("exceptionCaught : "+e);
	}

	/**
	 * 
	 * @param socket
	 * @return
	 */
	static protected Boolean isBadClient(Channel socket) {
		String[] address = socket.getRemoteAddress().toString().substring(1).split(":");
		String ip = address[0];
		Integer port = Integer.valueOf(address[1]);
		Long time = System.currentTimeMillis();
		
		// 배드 아이피들 차단.
		if(BadIpDatabase.find(ip) != null)
			return true;
		// ddos 공격 방어
		if(port <= 0)
			return true;
		// 무한접속형 섭폭 필터링 (dos공격)
		Ip IP = null;
		synchronized (list_dos) {
			IP = list_dos.get(ip);
			if(IP == null){
				IP = new Ip();
				IP.setBlock(false);
				IP.setIp( ip );
				IP.setTime( time );
				list_dos.put(IP.getIp(), IP);
				return false;
			}
		}
		// 블럭된 놈은 무시.
		if(IP.getBlock())
			return true;
		// 1초내에 10번이상 접속하는 놈들 무시.
		if(time < IP.getTime()+1000) {
			if(IP.getCount()>10) {
				IP.setBlock( true );
				return true;
			} else {
				IP.setCount( IP.getCount() + 1 );
			}
		} else {
			IP.setCount( 0 );
		}
		IP.setTime( time );
		//
		return false;
	}
	
}
