package lineage.share;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.LogConnect;
import lineage.bean.LogExp;
import lineage.bean.LogInterface;
import lineage.bean.database.PcTrade;
import lineage.gui.GuiMain;
import lineage.network.LineageServer;
import lineage.network.codec.lineage.Decoder;
import lineage.network.codec.lineage.Encoder;
import lineage.util.Util;
import lineage.world.controller.PcTradeController;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;

import org.json.simple.JSONObject;

public class Log {
	
	// 로그 시스템 사용 여부
	static public boolean is;
	
	// 각 로그 파일에 기록할 딜레이 값들
	static public int chatting_delay;
	static private long temp_time_chatting;
	static public int connect_delay;
	static private long temp_time_connect;
	static public int exp_delay;
	static private long temp_time_exp;
	static public int network_delay;
	static private long temp_time_network;
	static public int sp_running_delay;
	static private long temp_time_sp_running;
	static public int item_delay;
	static private long temp_time_item;
	static public int warehouse_delay;
	static public int promote_delay;
	static private long temp_time_promote;
	static public int pc_trade_delay;
	static private long temp_time_pc_trade;
	static public int pc_shop_trade_delay;
	static private long temp_time_pc_shop_trade;
	static public int 매니저창로그딜레이 = 1000 * 60 * 5;
	static private long temp_time_매니저창로그;
	// 서버 가동 시간 저장 변수.
	static public long sp_running_time;
	
	// 로그 기록될 경로 변수
	static final private String LOG_DIR					= "log/";
	static final private String LOG_DIR_CHATTING		= LOG_DIR+"chatting/";
	static final private String LOG_DIR_CONNECT			= LOG_DIR+"connect/";
	static final private String LOG_DIR_EXP				= LOG_DIR+"exp/";
	static final private String LOG_DIR_NETWORK			= LOG_DIR+"network/";
	static final private String LOG_DIR_SPRUNNING		= LOG_DIR+"sp_running/";
	static final private String LOG_DIR_ITEM			= LOG_DIR+"item/";
	static final private String LOG_DIR_PROMOTE			= LOG_DIR+"promote/";
	static final private String LOG_DIR_PC_TRADE		= LOG_DIR+"pc_trade/";
	static final private String LOG_DIR_PC_SHOP_TRADE	= LOG_DIR+"pc_shop_trade/";
	
	static final private String LOG_DIR_매니저창		= LOG_DIR+"매니저창/";
	static final private String LOG_DIR_접속로그		= LOG_DIR_매니저창+"접속 로그/";
	static final private String LOG_DIR_창고로그		= LOG_DIR_매니저창+"창고 로그/";
	static final private String LOG_DIR_거래로그		= LOG_DIR_매니저창+"거래 로그/";
	static final private String LOG_DIR_인첸트로그		= LOG_DIR_매니저창+"인첸트 로그/";
	static final private String LOG_DIR_아이템드랍로그	= LOG_DIR_매니저창+"아이템 드랍 로그/";
	static final private String LOG_DIR_명령어로그		= LOG_DIR_매니저창+"명령어 로그/";
	static final private String LOG_DIR_스피드핵로그	= LOG_DIR_매니저창+"스피드핵 로그/";
	static final private String LOG_DIR_데미지체크로그	= LOG_DIR_매니저창+"데미지 체크 로그/";
	static final private String LOG_DIR_인첸트복구로그	= LOG_DIR_매니저창+"인첸트 복구 로그/";
	
	// 로그가 기록될 메모리
	static private List<String> list_chatting;
	static private List<LogConnect> list_connect;
	static private List<LogExp> list_exp;
	static private List<String> list_network;
	static private List<String> list_sp_running;
	static private List<String> list_item;
	static private List<String> list_promote;
	static private List<String> list_pc_trade;
	static private List<String> list_pc_shop_trade;
	
	static private List<String> list_접속로그;
	static private List<String> list_창고로그;
	static private List<String> list_거래로그;
	static private List<String> list_인첸트로그;
	static private List<String> list_아이템드랍로그;
	static private List<String> list_명령어로그;
	static private List<String> list_스피드핵로그;
	static private List<String> list_데미지체크로그;
	static private List<String> list_인첸트복구로그;
	
	// 재사용 목록
	static private List<LogInterface> pool;
	
	static {
		sp_running_time = System.currentTimeMillis();
		pool = new ArrayList<LogInterface>();
		list_chatting = new ArrayList<String>();
		list_connect = new ArrayList<LogConnect>();
		list_exp = new ArrayList<LogExp>();
		list_network = new ArrayList<String>();
		list_sp_running = new ArrayList<String>();
		list_item = new ArrayList<String>();
		list_promote = new ArrayList<String>();
		list_pc_trade = new ArrayList<String>();
		list_pc_shop_trade = new ArrayList<String>();
		
		list_접속로그 = new ArrayList<String>();
		list_창고로그 = new ArrayList<String>();
		list_거래로그 = new ArrayList<String>();
		list_인첸트로그 = new ArrayList<String>();
		list_아이템드랍로그 = new ArrayList<String>();
		list_명령어로그 = new ArrayList<String>();
		list_스피드핵로그 = new ArrayList<String>();
		list_데미지체크로그 = new ArrayList<String>();
		list_인첸트복구로그 = new ArrayList<String>();
	}
	
	/**
	 * 필요한 정보 초기화 처리하는 함수.
	 */
	static public void init(){
		try {
			BufferedReader lnrr = new BufferedReader( new FileReader("log.conf"));
			String line;
			while ( (line = lnrr.readLine()) != null){
				if(line.startsWith("#"))
					continue;
				
				int pos = line.indexOf("=");
				if(pos>0){
					String key = line.substring(0, pos).trim();
					String value = line.substring(pos+1, line.length()).trim();
					
					if(key.equalsIgnoreCase("is"))
						is = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("chatting_delay"))
						chatting_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("connect_delay"))
						connect_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("exp_delay"))
						exp_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("network_delay"))
						network_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("sp_running_delay"))
						sp_running_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("promote_delay"))
						promote_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("pc_trade_delay"))
						pc_trade_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("pc_shop_trade_delay"))
						pc_shop_trade_delay = Integer.valueOf(value) * 1000 * 60;
				}
			}
			lnrr.close();		
			// 로그 디렉토리 확인.
			mkdirs();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Log.class.toString());
			lineage.share.System.println(e);
		}
	}
	
	static public void close(){
		if(!is)
			return;	
		// 로그 저장.
		toTimer(0);
	}

	static public void toTimer(long time){
		if(!is)
			return;
		
		// 로그 저장.
		toSaveChatting(time);
		toSaveConnect(time);
		toSaveExp(time);
		toSaveNetwork(time);
		toSaveSpRunning(time);
		toSaveItem(time);
		toSavePromote(time);
		toSavePcTrade(time);
		toSavePcShopTrade(time);
		
		toSave매니저창(time);
	}
	
	/**
	 * 로그 처리해도 되는 객체인지 확인.
	 * @param o
	 * @return
	 */
	static public boolean isLog(object o) {
		//
		if(!is)
			return false;
		//
		if(o != null) {
			// 로봇 무시.
			if(o instanceof RobotInstance)
				return false;
			// 몬스터 무시.
			if(o instanceof MonsterInstance)
				return false;
		}
		// 그외엔 성공
		return true;
	}
	
	/**
	 * 로그 기록 함수.
	 * @param uid
	 * @param msg
	 */
	static public void appendItem(object o, String...opt) {
		if(Log.isLog(o) == false)
			return;
		//
		long time = System.currentTimeMillis();
		String timeString = Util.getLocaleString(time, true);
		//
		JSONObject json = new JSONObject();
		json.put("time", time);
		json.put("time_string", timeString);
		for(String str : opt) {
			StringTokenizer st = new StringTokenizer(str, "|");
			json.put(st.nextToken(), st.nextToken());
		}
		//
		json.put("cha_name", o.getName());
		json.put("cha_objid", o.getObjectId());
		if(o instanceof PcInstance) {
			PcInstance pc = (PcInstance)o;
			json.put("account_id", pc.getClient().getAccountId());
			json.put("account_ip", pc.getClient().getAccountIp());
			json.put("account_uid", pc.getClient().getAccountUid());
		}
		//
		String script = String.format("%s", json.toJSONString());
		synchronized (list_item) {
			list_item.add( script );
		}
		
		//
		json.clear();
		json = null;
	}
	
	/**
	 * 로그 기록 함수.
	 * @param msg
	 */
	static public void appendSpRunning(String msg){
		long time = System.currentTimeMillis();
		String timeString = Util.getLocaleString(time, true);
		// time	내용
		synchronized (list_sp_running) {
			list_sp_running.add( String.format("[%s\t%d]\t%s", timeString, time, msg) );
		}
	}
	
	private static int errorCount;
	/**
	 * 에러 로그
	 */
	static public void toError(Exception e) {
		if (errorCount++ >= 50) {
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] ste = e.getStackTrace();
		sb.append("\t");
		sb.append(e.getClass().getName()).append(": ").append(e.getMessage());
		sb.append("\r\n");
		int size = ste.length;
		for (int i = 0; i < size; i++) {
			sb.append(ste[i]).append("\r\n");
		} 
		
		appendSpRunning(sb.toString());
	}
	
	/**
	 * 경험치 로그 기록 함수.
	 * @param key
	 * @param lv
	 * @param add_exp
	 * @param total_exp
	 * @param monster_lv
	 * @param monster_name
	 * @param monster_exp
	 */
	static public void appendExp(long key, int lv, int add_exp, int total_exp, int monster_lv, String monster_name, int monster_exp){
		// 찾기
		LogExp le = null;
		synchronized (list_exp) {
			for(LogExp l : list_exp){
				if(l.getDate() == key){
					le = l;
					break;
				}
			}
			// 없을경우 생성 및 관리목록에 등록.
			if(le == null){
				LogInterface li = getPool(LogExp.class);
				if(li == null)
					li = new LogExp();
				le = (LogExp)li;
				// 정보 초기화.
				le.setDate( key );
				// 등록
				list_exp.add( le );
			}
		}
		
		String timeString = Util.getLocaleString(System.currentTimeMillis(), true);
		// time	경험치배율	레벨	지급된경험치	누적경험치	몬스터레벨	몬스터이름	몬스터경험치
		le.append( String.format("[%s]\t%f\t%d\t%d\t%d\t%d\t%s\t%d", timeString, Lineage.rate_exp, lv, add_exp, total_exp, monster_lv, monster_name, monster_exp) );
	}
	
	/**
	 * 케릭터 생성하게되면 이놈도 호출됨.
	 * 관리목록에 등록할 객체 생성 및 초기화 처리 함수.
	 * @param key
	 * @return
	 */
	static private LogConnect toConnect(long key){
		LogInterface li = getPool(LogConnect.class);
		if(li == null)
			li = new LogConnect();
		LogConnect lc = (LogConnect)li;
		
		// 정보 초기화.
		lc.setDate( key );
		return lc;
	}
	
	/**
	 * 케릭터 생성시 호출됨.
	 * 	필요한 객체생성 하고 관리목록에 등록.
	 * 	로그 갱신처리.
	 * @param ip
	 * @param id
	 * @param name
	 * @param time
	 */
	static public void toConnect(String ip, String id, String name, long time){
		LogConnect lc = toConnect(time);
		// 정보 초기화.
		lc.setDate( time );
		// 관리목록에 등록.
		synchronized (list_connect) {
			list_connect.add( lc );
		}
		// 로그 갱신.
		appendConnect(time, ip, id, name, "생성");
	}
	
	/**
	 * 로그 기록 함수.
	 * @param key
	 * @param ip
	 * @param id
	 * @param msg
	 */
	static public void appendConnect(long key, String ip, String id, String name, String msg){
		LogConnect lc = null;
		synchronized (list_connect) {
			// 찾기
			for(LogConnect l : list_connect){
				if(l.getDate() == key){
					lc = l;
					break;
				}
			}
			// 없을경우 생성 및 관리목록에 등록.
			if(lc == null){
				lc = toConnect(key);
				list_connect.add( lc );
			}
		}
		
		String timeString = Util.getLocaleString(System.currentTimeMillis(), true);
		String str = String.format("[%s]\t [IP: %s]\t [계정: %s]\t [캐릭명: %s]\t [%s]", timeString, ip, id, name, msg);
		// time	아이피	계정명	내용(접속, 종료, 클라종료, 케릭생성, 케릭삭제)
		lc.append( str );
		
		if (!Common.system_config_console) {
			GuiMain.display.asyncExec(new Runnable() {
				public void run() {
					GuiMain.getViewComposite().getConnectComposite().toLog(str);
				}
			});
		}
	}
	
	/**
	 * 채팅 로그 기록용
	 * @param pc
	 * @param msg
	 * @param mode
	 */
	static public void appendChatting(PcInstance pc, String msg, int mode){
		String timeString = Util.getLocaleString(System.currentTimeMillis(), true);
		String ip = pc==null ? "null" : pc.getClient().getAccountIp();
		String id = pc==null ? "null" : pc.getClient().getAccountId();
		String name = pc==null ? "null" : pc.getName();
		
		
		// time	아이피	계정명	케릭이름	채팅종류	채팅내용
		synchronized (list_chatting) {
			list_chatting.add( String.format("[%s]\t%s\t%s\t%s\t%d\t%s", timeString, ip, id, name, mode, msg) );
		}
	}
	/**
	 * 홍보보상 로그 기록
	 */
	static public void appendPromote(PcInstance pc, int count) {
		String timeString = Util.getLocaleString(System.currentTimeMillis(), true);
		String id = pc == null ? "null" : pc.getClient().getAccountId();
		String name = pc == null ? "null" : pc.getName();

		// 시간 계정 케릭터 갯수
		synchronized (list_promote) {
			list_promote.add(String.format("[%s]\t계정: %s\t캐릭터: %s\t홍보 보상 상자: %d개", timeString, id, name, count));
		}
	}
	
	static public void appendPcTrade(PcTrade pt) {
		String timeString = Util.getLocaleString(System.currentTimeMillis(), true);
		String temp_timeString = Util.getLocaleString(pt.getComplete_day(), true);		

		synchronized (list_pc_trade) {
			list_pc_trade.add(String.format("[%s]\t판매자: %s\t판매자obj_id: %d\t구매자: %s\t구매자obj_id: %d\t가격: %s원\t아이템obj_id: %d\t아이템(수량): %s+%d%s(%d)"
					+ "\t거래시간: %s\t판매자 이름: %s\t연락처: %s\t은행명: %s\t계좌번호: %s", timeString, pt.getSell_name(), pt.getSell_objId(), pt.getBuy_name(), pt.getBuy_objId(),
					PcTradeController.changePrice(pt.getPrice()), pt.getItem_objId(), PcTradeController.changeBless(pt.getBless()), pt.getEnchant(), pt.getItem(), pt.getCount(), temp_timeString,
					pt.getName(), pt.getPhone_num(), pt.getBank_name(), pt.getBank_num()));
		}
	}
	
	static public void appendPcShopTrade(String mode, String pcName, long pcObjId, String buyerName, long buyerObjId, String price, String totalPrice, 
										String bless, int en, String itemName, long totalCount, long count, long nowCount) {
		String timeString = Util.getLocaleString(System.currentTimeMillis(), true);	

		synchronized (list_pc_shop_trade) {
			list_pc_shop_trade.add(String.format("[%s] %s \t판매자: %s\t판매자obj_id: %d\t구매자: %s\t구매자obj_id: %d \t개당가격: %s아데나 \t구매가격: %s아데나 \t아이템(총수량/구매수량/현재수량): %s+%d%s(%d/%d/%d)", 
					timeString, mode, pcName, pcObjId, buyerName, buyerObjId, price, totalPrice, bless, en, itemName, totalCount, count, nowCount));
		}
	}
	
	static public void append매니저창(String type, String msg) {
		switch (type) {
		case "접속":
			synchronized (list_접속로그) {
				list_접속로그.add(msg);
			}
			break;
		case "창고":
			synchronized (list_창고로그) {
				list_창고로그.add(msg);
			}
			break;
		case "거래":
			synchronized (list_거래로그) {
				list_거래로그.add(msg);
			}
			break;
		case "인첸트":
			synchronized (list_인첸트로그) {
				list_인첸트로그.add(msg);
			}
			break;
		case "아이템드랍":
			synchronized (list_아이템드랍로그) {
				list_아이템드랍로그.add(msg);
			}
			break;
		case "명령어":
			synchronized (list_명령어로그) {
				list_명령어로그.add(msg);
			}
			break;
		case "스피드핵":
			synchronized (list_스피드핵로그) {
				list_스피드핵로그.add(msg);
			}
			break;
		case "데미지체크":
			synchronized (list_데미지체크로그) {
				list_데미지체크로그.add(msg);
			}
			break;
		case "인첸트 복구":
			synchronized (list_인첸트복구로그) {
				list_인첸트복구로그.add(msg);
			}
			break;
		}
	}
	
	/**
	 * 로그정보 파일에기록처리하는 함수.
	 * @param file_name
	 * @param list
	 */
	static private void write(String file_name, List<String> list){
		try {
			// 파일 이어쓰기 모드로 객체 생성, 없을경우 자동생성.
			FileWriter fw = new FileWriter(file_name, true);
			// 로그 기록.
			for(String s : list)
				fw.append( String.format("%s\r\n", s) );
			// 파일 출력.
			fw.flush();
			fw.close();
		} catch (Exception e) { }
	}
	
	/**
	 * 로그정보 파일에기록처리하는 함수.
	 * @param file_name
	 * @param list
	 */
	static public void write(String file_name, String data){
		try {
			// 파일 이어쓰기 모드로 객체 생성, 없을경우 자동생성.
			FileWriter fw = new FileWriter(file_name, true);
			// 로그 기록.
			fw.append( data );
			// 파일 출력.
			fw.flush();
			fw.close();
		} catch (Exception e) { }
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
	
	/**
	 * 로그에 사용되는 디렉토리 생성 함수.
	 */
	static private void mkdirs(){
		File dir = new File(LOG_DIR_CHATTING);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_CONNECT);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_EXP);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_NETWORK);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_SPRUNNING);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_ITEM);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_PROMOTE);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_PC_TRADE);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_PC_SHOP_TRADE);
		if(!dir.isDirectory())
			dir.mkdirs();
		
		dir = new File(LOG_DIR_접속로그);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_창고로그);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_거래로그);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_인첸트로그);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_아이템드랍로그);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_명령어로그);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_스피드핵로그);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_데미지체크로그);
		if(!dir.isDirectory())
			dir.mkdirs();
		dir = new File(LOG_DIR_인첸트복구로그);
		if(!dir.isDirectory())
			dir.mkdirs();
	}
	
	/**
	 * 채팅 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveChatting(long time){
		if(time==0 || temp_time_chatting<=time){
			temp_time_chatting = time + chatting_delay;
			
			String file_name = String.format("%s%s.log", LOG_DIR_CHATTING, Util.getLocaleString(time==0 ? System.currentTimeMillis() : time, false));
			synchronized (list_chatting) {
				write(file_name, list_chatting);
				list_chatting.clear();
			}
		}
	}
	
	/**
	 * 접속 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveConnect(long time){
		if(time==0 || temp_time_connect<=time){
			temp_time_connect = time + connect_delay;
			
			synchronized (list_connect) {
				for(LogConnect lc : list_connect){
					String file_name = String.format("%s%s.log", LOG_DIR_CONNECT, Util.getLocaleString(System.currentTimeMillis(), false));
					write(file_name, lc.getList());
					setPool(lc);
				}
				list_connect.clear();
			}
		}
	}
	
	/**
	 * 경험치 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveExp(long time){
		if(time==0 || temp_time_exp<=time){
			temp_time_exp = time + exp_delay;
			
			synchronized (list_exp) {
				for(LogExp le : list_exp){
					String file_name = String.format("%s%s.log", LOG_DIR_EXP, Util.getLocaleString(System.currentTimeMillis(), false));
					write(file_name, le.getList());
					setPool(le);
				}
				list_exp.clear();
			}
		}
	}
	
	/**
	 * 패킷량 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveNetwork(long time){
		if(time==0 || temp_time_network<=time){
			temp_time_network = time + network_delay;
			
			String file_name = String.format("%s%s.log", LOG_DIR_NETWORK, Util.getLocaleString(time==0 ? System.currentTimeMillis() : time, false));
			synchronized (list_network) {
				write(file_name, list_network);
				list_network.clear();
			}
		}
		
		appendNetwork(time);
	}
	
	/**
	 * 패킷량 로그 기록 처리 함수.
	 * @param time
	 */
	static private void appendNetwork(long time) {
		// 종료되는 시점에서는 할 필요 없음.
		if(time == 0)
			return;
		
		// time	recv	send	클라갯수	아이피|recv|send
		synchronized (list_network) {
			list_network.add( String.format("[%s]\t%d\t%d\t%d\t%s", Util.getLocaleString(time, true), Decoder.recv_length, Encoder.send_length, LineageServer.getClientSize(), LineageServer.getLogNetwork()) );
		}
		Encoder.send_length = Decoder.recv_length = 0;
	}
	
	/**
	 * 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveSpRunning(long time) {
		if(time==0 || temp_time_sp_running<=time){
			temp_time_sp_running = time + sp_running_delay;
			
			String file_name = String.format("%s%s.log", LOG_DIR_SPRUNNING, Util.getLocaleString(sp_running_time, true));
			synchronized (list_sp_running) {
				write(file_name, list_sp_running);
				list_sp_running.clear();
			}
		}
	}
	
	/**
	 * 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSaveItem(long time) {
		if(time==0 || temp_time_item<=time){
			temp_time_item = time + item_delay;
			
			String file_name = String.format("%s%s.log", LOG_DIR_ITEM, Util.getLocaleString(time==0 ? System.currentTimeMillis() : time, false));
			synchronized (list_item) {
				write(file_name, list_item);
				list_item.clear();
			}
		}
	}
	
	/**
	 * 홍보보상 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSavePromote(long time) {
		if (time == 0 || temp_time_promote <= time) {
			temp_time_promote = time + promote_delay;

			String file_name = String.format("%s%s.log", LOG_DIR_PROMOTE, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_promote) {
				write(file_name, list_promote);
				list_promote.clear();
			}
		}
	}
	
	/**
	 * 무인 상점 거래 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSavePcShopTrade(long time) {
		if (time == 0 || temp_time_pc_shop_trade <= time) {
			temp_time_pc_shop_trade = time + pc_shop_trade_delay;

			String file_name = String.format("%s%s.log", LOG_DIR_PC_SHOP_TRADE, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_pc_shop_trade) {
				write(file_name, list_pc_shop_trade);
				list_pc_shop_trade.clear();
			}
		}
	}
	
	/**
	 * 유저 현금 거래 로그 저장 처리 함수.
	 * @param time
	 */
	static private void toSavePcTrade(long time) {
		if (time == 0 || temp_time_pc_trade <= time) {
			temp_time_pc_trade = time + pc_trade_delay;

			String file_name = String.format("%s%s.log", LOG_DIR_PC_TRADE, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_pc_trade) {
				write(file_name, list_pc_trade);
				list_pc_trade.clear();
			}
		}
	}
	
	static private void toSave매니저창(long time) {
		toSave접속로그(time);
		toSave창고로그(time);
		toSave거래로그(time);
		toSave인첸트로그(time);
		toSave아이템드랍로그(time);
		toSave명령어로그(time);
		toSave스피드핵로그(time);
		toSave데미지체크로그(time);
		toSave인첸트복구로그(time);
	}
	
	static private void toSave접속로그(long time) {
		if (time == 0 || temp_time_매니저창로그 <= time) {
			temp_time_매니저창로그 = time + 매니저창로그딜레이;

			String file_name = String.format("%s%s.log", LOG_DIR_접속로그, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_접속로그) {
				write(file_name, list_접속로그);
				list_접속로그.clear();
			}
		}
	}
	
	static private void toSave창고로그(long time) {
		if (time == 0 || temp_time_매니저창로그 <= time) {
			temp_time_매니저창로그 = time + 매니저창로그딜레이;

			String file_name = String.format("%s%s.log", LOG_DIR_창고로그, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_창고로그) {
				write(file_name, list_창고로그);
				list_창고로그.clear();
			}
		}
	}
	
	static private void toSave거래로그(long time) {
		if (time == 0 || temp_time_매니저창로그 <= time) {
			temp_time_매니저창로그 = time + 매니저창로그딜레이;

			String file_name = String.format("%s%s.log", LOG_DIR_거래로그, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_거래로그) {
				write(file_name, list_거래로그);
				list_거래로그.clear();
			}
		}
	}
	
	static private void toSave인첸트로그(long time) {
		if (time == 0 || temp_time_매니저창로그 <= time) {
			temp_time_매니저창로그 = time + 매니저창로그딜레이;

			String file_name = String.format("%s%s.log", LOG_DIR_인첸트로그, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_인첸트로그) {
				write(file_name, list_인첸트로그);
				list_인첸트로그.clear();
			}
		}
	}
	
	static private void toSave아이템드랍로그(long time) {
		if (time == 0 || temp_time_매니저창로그 <= time) {
			temp_time_매니저창로그 = time + 매니저창로그딜레이;

			String file_name = String.format("%s%s.log", LOG_DIR_아이템드랍로그, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_아이템드랍로그) {
				write(file_name, list_아이템드랍로그);
				list_아이템드랍로그.clear();
			}
		}
	}
	
	static private void toSave명령어로그(long time) {
		if (time == 0 || temp_time_매니저창로그 <= time) {
			temp_time_매니저창로그 = time + 매니저창로그딜레이;

			String file_name = String.format("%s%s.log", LOG_DIR_명령어로그, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_명령어로그) {
				write(file_name, list_명령어로그);
				list_명령어로그.clear();
			}
		}
	}
	
	static private void toSave스피드핵로그(long time) {
		if (time == 0 || temp_time_매니저창로그 <= time) {
			temp_time_매니저창로그 = time + 매니저창로그딜레이;

			String file_name = String.format("%s%s.log", LOG_DIR_스피드핵로그, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_스피드핵로그) {
				write(file_name, list_스피드핵로그);
				list_스피드핵로그.clear();
			}
		}
	}
	
	static private void toSave데미지체크로그(long time) {
		if (time == 0 || temp_time_매니저창로그 <= time) {
			temp_time_매니저창로그 = time + 매니저창로그딜레이;

			String file_name = String.format("%s%s.log", LOG_DIR_데미지체크로그, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_데미지체크로그) {
				write(file_name, list_데미지체크로그);
				list_데미지체크로그.clear();
			}
		}
	}
	
	static private void toSave인첸트복구로그(long time) {
		if (time == 0 || temp_time_매니저창로그 <= time) {
			temp_time_매니저창로그 = time + 매니저창로그딜레이;

			String file_name = String.format("%s%s.log", LOG_DIR_인첸트복구로그, Util.getLocaleString(time == 0 ? System.currentTimeMillis() : time, false));
			synchronized (list_인첸트복구로그) {
				write(file_name, list_인첸트복구로그);
				list_인첸트복구로그.clear();
			}
		}
	}
	
	static private LogInterface getPool(Class<?> c){
		LogInterface li = null;
		synchronized (pool) {
			for(LogInterface o : pool){
				if(o.getClass().equals(c)){
					li = o;
					break;
				}
			}
			if(li != null)
				pool.remove(li);
		}
		return li;
	}
	
	static private void setPool(LogInterface li){
		li.close();
		synchronized (pool) {
			if(!pool.contains(li))
				pool.add(li);
		}
	}
}
