package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.database.DatabaseConnection;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.AStar;
import lineage.world.Node;
import lineage.world.World;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.npc.background.Racer;
import lineage.world.object.npc.background.door.Door;

public class DogRaceController implements Runnable {

	// 게임상태 변환용 변수.
	static public enum EVENT_STATUS {
		READY,	// 시작하기 1분전 준비상태
		PLAY,		// 게임중인 상태
		STOP,		// 레이서들 피니쉬되고 1분대기상태
		CLEAR,	// 9분정도 대기하면서 준비중인상태.
		SLEEP		// 밤에는 게임운영 안함.
	}
	public static boolean isDogGame = false;
	//
	static private DogRaceController drc;
	static private boolean running;
	// 레이스견 이름들
	static private String[] DOG_NAME;
	// 레이스견 gfx아이디
	static private int[][] DOG_GFX;
	// 레이스견 시작위치
	static private int[] DOG_START_LOCATION_X;
	static private int[] DOG_START_LOCATION_Y;
	static private int[] DOG_END_LOCATION_X;
	static private int[] DOG_END_LOCATION_Y;
	// 스탭별 이동해야할 좌표.
	static private int[] DOG_STEP_LOCATION_X;
	static private int[] DOG_STEP_LOCATION_Y;
	// 현재 게임상태
	static private EVENT_STATUS status;
	// html 레이서들 상태 표현용
	static private List<String> list_html;
	// 레이스와 연관된 npc 목록
	static private List<ShopInstance> list_npc;
	// 문 목록
	static private List<Door> list_door;
	// 현재 경기중인 레이서 목록
	static private List<Racer> list_race;
	// 골인한 레이서 목록
	static private List<String> list_Finish;
	// 진행중인 레이스 넘버
	static private int now_uid;
	// 진행중인 시간값 임시 저장.
	static private long temp_now_time;
	// 진행중인 위치값 임시 저장.
	static private int temp_now_idx;
	//
	static private AStar aStar;					// 길찾기 변수
	static private Node tail;					// 길찾기 변수
	static private int[] iPath;					// 길찾기 변수
	
	static public void init(Connection con) {
		TimeLine.start("DogRaceController..");
		
		//
		DOG_START_LOCATION_X = new int[]{33522, 33520, 33518, 33516, 33514};
		DOG_START_LOCATION_Y = new int[]{32861, 32863, 32865, 32867, 32869};
		DOG_END_LOCATION_X = new int[]{33526, 33526, 33526, 33526, 33526};
		DOG_END_LOCATION_Y = new int[]{32847, 32845, 32843, 32841, 32839};
		DOG_STEP_LOCATION_X = new int[]{33477, 33490};
		DOG_STEP_LOCATION_Y = new int[]{32861, 32847};
		DOG_GFX = new int[][]{
			{1353, 1461, 1462, 1463, 1464},
			{1355, 1465, 1466, 1467, 1468},
			{1357, 1469, 1470, 1471, 1472},
			{1359, 1473, 1474, 1475, 1476}
		};
		DOG_NAME = new String[]{
			"$1213",	// 베라티
			"$1214",	// 코피니
			"$1215",	// 베헤
			"$1216",	// 차셈
			"$1217",	// 티소
			"$1218",	// 구몰리
			"$1219",	// 제피
			"$1220",	// 렘바
			"$1221",	// 툴록
			"$1222",	// 토누토
			"$1223",	// 주코
			"$1224",	// 미코아
			"$1225",	// 나쎄
			"$1226",	// 페콜라
			"$1227",	// 소보
			"$1228",	// 시토
			"$1229",	// 핀핀
			"$1230",	// 제론
			"$1231",	// 코붐
			"$1232",	// 제비타
		};
		//
		drc = new DogRaceController();
		list_html = new ArrayList<String>();
		list_npc = new ArrayList<ShopInstance>();
		list_door = new ArrayList<Door>();
		list_Finish = new ArrayList<String>();
		// uid값 추출
		now_uid = getLastUid(con) + 1;
		// 상태 세팅
		status = EVENT_STATUS.SLEEP;
		// 경기할 레이서들 객체 미리 생성.
		list_race = new ArrayList<Racer>();
		for(int i=0 ; i<5 ; ++i){
			Racer dog = new Racer();
			dog.setObjectId( ServerDatabase.nextEtcObjId() );
			dog.setHomeX( DOG_START_LOCATION_X[i] );
			dog.setHomeY( DOG_START_LOCATION_Y[i] );
			dog.setHomeMap(4);
			dog.setHomeHeading(6);
			dog.num = i;
			list_race.add(dog);
		}
		//
		aStar = new AStar();
		iPath = new int[2];
		
		TimeLine.end();
	}
	
	/**
	 * 쓰레드 활성화 함수.
	 */
	static public void start(){
		running = true;
		Thread t = new Thread(drc);
		t.setName( DogRaceController.class.toString() );
		t.start();
	}
	
	/**
	 * 종료 함수
	 */
	static public void close(){
		running = false;
	}
	
	@Override
	public void run() {
		// 경기상태가 시작 일경우.
		while(running){
			try {
				// 휴식
				Thread.sleep( Common.THREAD_SLEEP );
				//
				if(status != EVENT_STATUS.PLAY)
					continue;
				//
				long time = System.currentTimeMillis();
				// 레이서 한칸식 이동하기.
				for(Racer r : list_race){
					if( r.finish || isLucky(r) ){
					}else{
						if(r.isAi(time)){
							
							// 골인지점 도착 확인.
							if(DOG_END_LOCATION_X[r.num]==r.getX() && DOG_END_LOCATION_Y[r.num]==r.getY()){
								if( !list_Finish.contains(r.getName()) ){
									list_Finish.add(r.getName());
									r.finish = true;
									toMessage( String.format("%d등 %s", list_Finish.size(), r.getName()) );
								}
								continue;
							}
							
							// 출발 지점에 타일이 그지같아서 aStar가 안먹힘 그래서 20칸 앞으로 강제적으로 이동.
							if(r.step <= 20){
								r.toMoving(r.getX() - 1, r.getY(), r.getHeading());
								r.step += 1;
							// 이제부터 aStar먹이면서 처리.
							}else{
								int x = 0;
								int y = 0;
								// 임위로 잡은 위치부분까지만 이동시키게 하기 위함.
								// 그 이후에는 도착지점으로 좌표 변경 하여 처리.
								if(DOG_STEP_LOCATION_X.length <= (r.step - 21)){
									x = DOG_END_LOCATION_X[r.num];
									y = DOG_END_LOCATION_Y[r.num];
								}else{
									x = DOG_STEP_LOCATION_X[r.step - 21];
									y = DOG_STEP_LOCATION_Y[r.step - 21];
									if(x==r.getX() && y==r.getY()){
										r.step += 1;
										try {
											x = DOG_STEP_LOCATION_X[r.step - 21];
											y = DOG_STEP_LOCATION_Y[r.step - 21];
										} catch (Exception e) {
											x = DOG_END_LOCATION_X[r.num];
											y = DOG_END_LOCATION_Y[r.num];
										}
									}
								}
								//
								aStar.cleanTail();
								tail = aStar.searchTail(r, x, y, true);
								if(tail != null){
									int count = 100;
									while(tail != null){
										if (count-- < 0) {
											break;
										}
										// 현재위치 라면 종료
										if(tail.x==r.getX() && tail.y==r.getY())
											break;
										iPath[0] = tail.x;
										iPath[1] = tail.y;
										tail = tail.prev;
									}
									r.toMoving(iPath[0], iPath[1], Util.calcheading(r.getX(), r.getY(), iPath[0], iPath[1]));
								}
							}
						}
					}
				}
			} catch (Exception e) {
				lineage.share.System.println(DogRaceController.class+" : run()");
				lineage.share.System.println(e);
			}
		}
	}
	
	static public void appendNpc(ShopInstance di){
		list_npc.add( di );
	}
	
	static public void appendDoor(Door d){
		list_door.add( d );
	}
	
	static public EVENT_STATUS getStatus(){
		return status;
	}
	
	static public List<String> getRacerStatus(){
		list_html.clear();
		if(status==EVENT_STATUS.CLEAR){
			for(Racer s : list_race){
				list_html.add(DOG_NAME[s.idx]);	// 이름
				switch(s.Status){
					case 0:
						list_html.add("최저");
						break;
					case 1:
						list_html.add("보통");
						break;
					case 2:
						list_html.add("최상");
						break;
				}
				String theory = String.valueOf(s.Theory);
				list_html.add(theory.substring(0, theory.indexOf(".")+2)+"%");	// 승률
			}
		}
		return list_html;
	}
	
	/**
	 * 현재 진행중인 게임에 순번에 위치한 레이서에 정보 문자열로 변환해서 리턴.
	 *  : 상점에서 티켓 이름지정할때 사용중.
	 * @param idx
	 * @return
	 */
	static public String RacerTicketName(int idx){
		// uid-idx name
		String name = list_race.get(idx).getName();
		return String.format("%d-%d %s", now_uid, idx, name);
	}
	
	/**
	 * 레이표 구매시 호출됨.
	 * 	: 배당처리를위해 몇개 구매햇는지 확인하기위해 카운팅함.
	 * @param idx
	 */
	static public void setCountting(int idx, long count){
		list_race.get(idx).countting += count;
	}
	
	static public void toTimer(long time) {
		//
//		if(Lineage.server_version < 163)
//		if(true)
//			return;
		//
		if ((Lineage.server_version < 200) || (!isDogGame))
			return;
		switch(status){
			case READY:
				if(temp_now_time <= time){
					// 시작전 멘트.
					if(temp_now_idx > 0){
						toMessage( String.format("%d 초!", temp_now_idx--) );
					}else if(temp_now_idx <= 0){
						//
						toMessage("출발!");
						// 문 열기
						toDoor(true);
						// 게임상태 변경하기.
						status = EVENT_STATUS.PLAY;
					}
				}
				break;
			case PLAY:
				// 모두 도착햇다면 게임상태 변경하기.
				if(list_Finish.size() == 5){
					// 상태변경
					status = EVENT_STATUS.STOP;
					// 1분대기하기 위해.
					temp_now_time = time + (1000 * 60);
					// 디비 로그 갱신.
					insertDB();
				}
				break;
			case STOP:
				if(temp_now_time <= time){
					// 이전 게임에 사용된 메모리값들 제거.
					list_Finish.clear();
					for(Racer s : list_race){
						World.remove(s);
						s.clearList(true);
						s.clean();
					}
					// 시간에 따라 변경.
					int h = ServerDatabase.getLineageTimeHour();
			//		if(h>5 && h<22){
						// am6 ~ pm10 사이에만 운영하기.
						status = EVENT_STATUS.CLEAR;
						temp_now_idx = 0;
			//		}else{
						// 휴식모드로 전환.
						status = EVENT_STATUS.SLEEP;
						temp_now_idx = 0;
			//		}
				}
				break;
			case SLEEP:
			//	int h = ServerDatabase.getLineageTimeHour();
			//	if(h>5 && h<22)
					status = EVENT_STATUS.CLEAR;
				break;
			case CLEAR:
				if(temp_now_idx == 0){
					temp_now_idx = 3;
					// 문 닫기
					toDoor(false);
					// 레이서들 배치하기
					initRacer();
					// 레이스표 정보 갱신.
					initRacerTicket();
				}
				// 9분 대기.
				if(temp_now_time <= time){
					// 1분마다 멘트.
					temp_now_time = time + (1000 * 60);
//					temp_now_time = time + (1000 * 1);
					toMessage( String.format("경기 시작 %d 분전!", temp_now_idx--) );
					
					if(temp_now_idx == 0){
						temp_now_idx = 3;
						// 50초 대기
						temp_now_time = time + (1000 * 50);
//						temp_now_time = time + (1000 * 1);
						// 게임상태 변경하기.
						status = EVENT_STATUS.READY;
					}
				}
				break;
		}
	}
	
	static private void initRacer(){
		// 출전할 레이서 gfx 설정.
		setRacerGfx();
		// 출전할 레이서들 이름 설정.
		setRacerName();
		// 승률값 설정하기
		setRacerTheory();
		// 화면에 그리기.
		VisualRacer();
	}
	
	static private void initRacerTicket(){
		for(ShopInstance si : list_npc){
			for(int i=0 ; i<5 ; ++i){
				si.getNpc().getShop_list().get(i).setPrice(Lineage.dog_race_price);
				si.getNpc().getShop_list().get(i).setRaceUid(now_uid);
				si.getNpc().getShop_list().get(i).setRaceType("dog");
			}
		}
	}
	
	static private void VisualRacer(){
		for(Racer r : list_race){
			r.setHeading(r.getHomeHeading());
			r.toTeleport(r.getHomeX(), r.getHomeY(), r.getHomeMap(), false);
		}
	}
	
	static private void setRacerTheory(){
		for(Racer s : list_race){
			s.Theory = Util.random(0.0, 0.9) + Util.random(0, 39);
			s.Status = Util.random(0, 2);
			// 럭키 확률 체크.
			switch(s.Status) {
				case 0:	// 최저
					s.Lucky = Util.random(0, 100) > 50;
					break;
				case 1:	// 보통
					s.Lucky = Util.random(0, 100) > 70;
					break;
				case 2:	// 최고
					s.Lucky = Util.random(0, 100) > 90;
					break;
			}
		}
	}
	
	static private void setRacerName(){
		String name = null;
		int idx = 0;
		for(Racer s : list_race){
			int count = 100;
			do{
				if (count-- < 0) {
					break;
				}
				idx = Util.random(0, DOG_NAME.length-1);
				name = DOG_NAME[idx];
			}while(isRacerName(name));
			s.setName(name);
			s.idx = idx;
		}
	}
	
	static private boolean isRacerName(String name){
		for(Racer s : list_race){
			if(s.getName()!=null && s.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	static private void setRacerGfx() {
		for(int i = 0 ; i<list_race.size() ; ++i){
			Racer r = list_race.get(i);
			int idx = Util.random(0, DOG_GFX.length-1);
			int gfx = DOG_GFX[idx][i];
			r.setGfx( gfx );
			r.setAiTime( SpriteFrameDatabase.find(r.getGfx(), r.getGfxMode()) );
		}
	}
	
	static private void insertDB(){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO race_log SET type='dog', race_idx=?, uid=?, price=?");
			st.setInt(1, getIdx(list_Finish.get(0)));
			st.setInt(2, now_uid++);
			st.setInt(3, getPrice( list_Finish.get(0) ));
			st.execute();
		}catch(Exception e){
			lineage.share.System.println(DogRaceController.class+" : insertDB()");
			lineage.share.System.println(e);
		}finally{
			DatabaseConnection.close(con, st);
		}
	}
	
	static private int getIdx(String name){
		for(int i=0 ; i<list_race.size() ; ++i){
			Racer s = list_race.get(i);
			if(s.getName().equalsIgnoreCase(name))
				return i;
		}
		return 0;
	}
	
	static private int getPrice(String name){
		int price = 0;
		try {
			Racer r = null;
			for(Racer s : list_race){
				if(s.getName().equalsIgnoreCase(name)){
					r = s;
					break;
				}
			}
			if(r!=null && r.getName().equalsIgnoreCase( list_Finish.get(0) )){
				int total = 0;																				// 판매된 전체 레이스표 갯수*price
				int slim_countting = r.countting * Lineage.dog_race_price;						// 우승한 레이서의 표 갯수*price
				for(Racer s : list_race)
					total += s.countting;
				total *= Lineage.dog_race_price;
				
				// 공식 적용
				double a = (total / slim_countting);
				double b = (slim_countting * a) * 1.0;	// 10%착감하기.
				
				// 최종가격에 장당가격 추출
				price = (int)(b / r.countting);
			}
		} catch (Exception e) { }
		return price;
	}
	
	static private boolean isLucky(Racer s){
		// 기본 승률 체크, lucky가 true면 무조건 고고싱!!
		if(s.Theory<Util.random(0.0, 100.0) && !s.Lucky){
			switch(s.Status){
				case 0:	// 최저
					return 50<Util.random(0, 100);
				case 1:	// 보통
					return 50<Util.random(0, 100);
				case 2:	// 최고
					return 50<Util.random(0, 100);
			}
		}
		return false;
	}
	
	/**
	 * 디비에 기록된 uid 최대값 추출.
	 * @param con
	 * @return
	 */
	static private int getLastUid(Connection con){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT MAX(uid) FROM race_log WHERE type='dog'");
			rs = st.executeQuery();
			if(rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.println(DogRaceController.class+" : getLastUid(Connection con)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 1;
	}
	
	/**
	 * 레이서 npc 들이 멘트쏠때 사용.
	 * @param msg
	 */
	static private void toMessage(String msg){
		for(ShopInstance si : list_npc)
			ChattingController.toChatting(si, msg, Lineage.CHATTING_MODE_SHOUT);
	}
	
	/**
	 * 문 열고 닫기 처리 함수.
	 * @param isOpen
	 */
	static private void toDoor(boolean isOpen) {
		for(Door d : list_door){
			if(isOpen)
				d.toOpen();
			else
				d.toClose();
			d.toSend();
		}
	}
	
}