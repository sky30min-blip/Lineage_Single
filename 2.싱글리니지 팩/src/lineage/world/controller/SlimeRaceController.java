package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.database.DatabaseConnection;
import lineage.database.ServerDatabase;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.npc.background.Racer;

public class SlimeRaceController {
	
	// 게임상태 변환용 변수.
	static public enum EVENT_STATUS {
		READY,	// 시작하기 1분전 준비상태
		PLAY,		// 게임중인 상태
		STOP,		// 레이서들 피니쉬되고 1분대기상태
		CLEAR,	// 9분정도 대기하면서 준비중인상태.
		SLEEP		// 밤에는 게임운영 안함.
	}
	
	// 레이서들의 이름목록.
	static private String[][] list_race_name = {
			// {html_name, name}
			{"$382", "$378"},				// 가버너
			{"$389", "$347"},				// 가디안
			{"$397", "$355"},				// 사이하
			{"$396", "$354"},				// 엘븐 애로우
			{"$390", "$348"},				// 이븐스타
			{"$398", "$356"},				// 호크윈드
			{"$387", "$345"},				// 레이디 호크
			{"$383", "$379"},				// 영이글
			{"$391", "$349"},				// 슈퍼블랙
			{"$401", "$359"},				// 젤리피쉬
			{"$388", "$346"},				// 라이트닝
			{"$394", "$352"},				// 슈팅스타
			{"$386", "$344"},				// 캐논 보이
			{"$393", "$351"},				// 마이베이비
			{"$384", "$380"},				// 세인트라이트
			{"$400", "$358"},				// 펌블
			{"$399", "$357"},				// 팰컨
			{"$392", "$350"},				// 마키아벨리
			{"$395", "$353"},				// 뷸렛
			{"$385", "$381"}					// 글루디아
	};
	
	// 현재 게임상태
	static private EVENT_STATUS status;
	// html 레이서들 상태 표현용
	static private List<String> list_html;
	// 슬라임 레이스와 연관된 npc 목록
	static private List<ShopInstance> list_npc;
	// 현재 경기중인 레이서 목록
	static private List<Racer> list_race;
	// 골인한 슬라임 목록
	static private List<String> list_SlimeFinish;
	// 진행중인 레이스 넘버
	static private int now_uid;
	// 진행중인 시간값 임시 저장.
	static private long temp_now_time;
	// 진행중인 위치값 임시 저장.
	static private int temp_now_idx;
	// 경기전 대기 카운트값
	static public int delay_cnt = 10;
	
	static public void init(Connection con){
		TimeLine.start("SlimeRaceController..");
		
		list_html = new ArrayList<String>();
		list_npc = new ArrayList<ShopInstance>();
		list_SlimeFinish = new ArrayList<String>();
		// uid값 추출
		now_uid = getLastUid(con) + 1;
		// 상태 세팅
		status = EVENT_STATUS.SLEEP;
		// 경기할 레이서들 객체 미리 생성.
		list_race = new ArrayList<Racer>();
		for(int i=0 ; i<5 ; ++i){
			Racer s = new Racer();
			s.setObjectId( ServerDatabase.nextEtcObjId() );
			s.setGfx(31);
			s.setHomeMap(4);
			s.setHomeX(32615+i);
			s.setHomeY(32655);
			s.setHeading(4);
			
			list_race.add(s);
		}
		
		
		TimeLine.end();
	}
	
	static public void appendNpc(ShopInstance si){
		list_npc.add( si );
	}
	
	static public EVENT_STATUS getStatus(){
		return status;
	}
	
	static public List<String> getRacerStatus(){
		list_html.clear();
		if(status==EVENT_STATUS.CLEAR){
			for(Racer s : list_race){
				list_html.add(list_race_name[s.idx][0]);	// 이름
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
	static public String SlimeRaceTicketName(int idx){
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
	
	static public void toTimer(long time){
		if(Lineage.server_version > 200)
			return;
		
		switch(status){
			case READY:
				if(temp_now_time <= time){
					// 시작전 멘트.
					if(temp_now_idx > 0){
						toMessage( String.format("%d 초!", temp_now_idx--) );
					}else if(temp_now_idx <= 0){
						toMessage("출발!");
						// 게임상태 변경하기.
						status = EVENT_STATUS.PLAY;
					}
				}
				break;
			case PLAY:
				// 슬라임 한칸식 이동하기.
				SlimeMove();
				// 피니쉬라인 체크하기.
				SlimeFinish();
				// 모두 도착햇다면 게임상태 변경하기.
				if(list_SlimeFinish.size() == 5){
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
					list_SlimeFinish.clear();
					for(Racer s : list_race){
						World.remove(s);
						s.clearList(true);
						s.clean();
					}
					// 시간에 따라 변경.
					int h = ServerDatabase.getLineageTimeHour();
					if(h>6 && h<24){
						// am6 ~ pm10 사이에만 운영하기.
						status = EVENT_STATUS.CLEAR;
						temp_now_idx = 0;
					}else{
						// 휴식모드로 전환.
						status = EVENT_STATUS.SLEEP;
						temp_now_idx = 0;
					}
				}
				break;
			case SLEEP:
				int h = ServerDatabase.getLineageTimeHour();
				if(h>6 && h<24)
					status = EVENT_STATUS.CLEAR;
				break;
			case CLEAR:
				if(temp_now_idx == 0){
					temp_now_idx = delay_cnt;
					// 레이서들 배치하기
					initSlime();
					// 레이스표 정보 갱신.
					initSlimeRaceTicket();
				}
				// 9분 대기.
				if(temp_now_time <= time){
					// 1분마다 멘트.
					temp_now_time = time + (1000 * 60);
					toMessage( String.format("경기 시작 %d 분전!", temp_now_idx--) );
					
					if(temp_now_idx == 0){
						temp_now_idx = 10;
						// 50초 대기
						temp_now_time = time + (1000 * 50);
						// 게임상태 변경하기.
						status = EVENT_STATUS.READY;
					}
				}
				break;
		}
	}
	
	static private void SlimeFinish(){
		for(Racer s : list_race){
			if(s.getY()==32684){
				if(!s.finish){
					s.finish = true;
					if(!list_SlimeFinish.contains(s.getName())){
						list_SlimeFinish.add(s.getName());
						toMessage( String.format("%d등 %s", list_SlimeFinish.size(), s.getName()) );
					}
				}
			}
		}
	}
	
	static private void SlimeMove(){
		for(Racer s : list_race){
			if( s.finish || SlimeLucky(s) ){
			}else{
				s.toMoving(s.getX(), s.getY()+1, s.getHeading());
			}
		}
	}
	
	static private boolean SlimeLucky(Racer s){
		// 기본 승률 체크, lucky가 true면 무조건 고고싱!!
		if(s.Theory<Util.random(0.0, 100.0) && !s.Lucky){
			switch(s.Status){
				case 0:	// 최저
					return 40<Util.random(0, 100);
				case 1:	// 보통
					return 60<Util.random(0, 100);
				case 2:	// 최고
					return 80<Util.random(0, 100);
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
			st = con.prepareStatement("SELECT MAX(uid) FROM race_log WHERE type='slime'");
			rs = st.executeQuery();
			if(rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.println(SlimeRaceController.class+" : getLastUid(Connection con)");
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
	
	static private void initSlime(){
		// 출전할 슬라임들 이름 설정.
		setSlimeName();
		// 승률값 설정하기
		setSlimeTheory();
		// 화면에 그리기.
		VisualSlime();
	}
	
	static private void initSlimeRaceTicket(){
		for(ShopInstance si : list_npc){
			for(int i=0 ; i<5 ; ++i){
				si.getNpc().getShop_list().get(i).setPrice(Lineage.slime_race_price);
				si.getNpc().getShop_list().get(i).setRaceUid(now_uid);
				si.getNpc().getShop_list().get(i).setRaceType("slime");
			}
		}
	}
	
	static private void VisualSlime(){
		for(Racer s : list_race)
			s.toTeleport(s.getHomeX(), s.getHomeY(), s.getHomeMap(), false);
	}
	
	static private void setSlimeTheory(){
		for(Racer s : list_race){
			s.Theory = Util.random(0.0, 20.0);
			s.Status = Util.random(0, 2);
			// 상태가 최저와 보통일대 럭키 확률 넣기.
			if(s.Status <= 1){
				if(s.Status == 0){
					// 최저
					if(Util.random(0, 100)>60)
						s.Lucky = true;
				}else{
					// 보통
					if(Util.random(0, 100)>80)
						s.Lucky = true;
				}
			}
		}
	}
	
	static private void setSlimeName(){
		String name = null;
		int idx = 0;
		for(Racer s : list_race){
			do{
				idx = Util.random(0, list_race_name.length-1);
				name = list_race_name[idx][1];
			}while(isSlimeName(name));
			s.setName(name);
			s.idx = idx;
		}
	}
	
	static private boolean isSlimeName(String name){
		for(Racer s : list_race){
			if(s.getName()!=null && s.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	static private void insertDB(){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO race_log SET type='slime', race_idx=?, uid=?, price=?");
			st.setInt(1, getIdx(list_SlimeFinish.get(0)));
			st.setInt(2, now_uid++);
			st.setInt(3, getPrice( list_SlimeFinish.get(0) ));
			st.execute();
		}catch(Exception e){
			lineage.share.System.println(SlimeRaceController.class+" : insertDB()");
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
			Racer slim = null;
			for(Racer s : list_race){
				if(s.getName().equalsIgnoreCase(name)){
					slim = s;
					break;
				}
			}
			if(slim!=null && slim.getName().equalsIgnoreCase( list_SlimeFinish.get(0) )){
				int total = 0;																	// 판매된 전체 슬라임표 갯수*price
				int slim_countting = slim.countting * Lineage.slime_race_price;					// 우승한 슬라임의 표 갯수*price
				for(Racer s : list_race)
					total += s.countting;
				total *= Lineage.slime_race_price;
				
				// 공식 적용
				double a = (total / slim_countting);
				double b = (slim_countting * a) * 0.9;	// 10%착감하기.
				
				// 최종가격에 장당가격 추출
				price = (int)(b / slim.countting);
			}
		} catch (Exception e) { }
		return price;
	}
	
}
