package lineage.world.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lineage.bean.database.TeamBattleTime;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.bean.lineage.Summon;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_KingdomAgent;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectLock;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.DollRaceController.EVENT_STATUS;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.GreaterHaste;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.HastePotionMagic;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.Wafer;
import lineage.world.object.magic.movingacceleratic;

public class DollRaceController {
	static private Calendar calendar;
	public static boolean isOpen;
	public static long bugEndTime;

	public static int counting;

	static private int[] MAGIC_DOLL_GFX;

	public static List<PcInstance> joinList;
	// 현재 게임상태
	public static EVENT_STATUS status;
	
	// 골인한 레이서 목록
	static private List<String> list_Finish;
	
	static public boolean askTeamBattle = false;
	static boolean joinEnd;
	
	static public boolean startTeamBattle = false;
	// 멘트치는 npc 
	static private List<BackgroundInstance> line;
	
	static int count = 30;
	static public boolean countgo = false;

	static private object 버경NPC;
	static private String 버경NPC이름 = "레이싱 안내원";
	
	// 게임상태 변환용 변수.
	static public enum EVENT_STATUS {
		WAIT,
		READY,	// 시작하기 1분전 준비상태
		PLAY,		// 게임중인 상태
		STOP,		// 
		CLEAR	// 

	}
	
	static public void init() {
		
		TimeLine.start("버그 경주 컨트롤러..");
		
		calendar = Calendar.getInstance();
		isOpen = false;
	
		
		MAGIC_DOLL_GFX = new int[]{
				5919,		// 서큐버스
				6096,		// 늑대인간
				6100,		// 버그베어
				6443,		// 장로
				6449,		// 크러스트시안
				6452,		// 돌 골렘
				6480,		// 에티
				8650,		// 인어
				7047,		// 코카트리스
				7053,		// 리치
				12539,		// 목각
				13516,		// 나이트발드
				14534,		// 아이리스
				13520,		// 데스나이트
				15975,		// 바포메트
				13464,		// 데몬
				15978,		// 커츠
			};
		
		MAGIC_DOLL_GFX = new int[]{
				5919,		// 서큐버스
				6096,		// 늑대인간
				6100,		// 버그베어
				6443,		// 장로
				6449,		// 크러스트시안
				6452,		// 돌 골렘
				6480,		// 에티
				8650,		// 인어
				7047,		// 코카트리스
				7053,		// 리치
				12539,		// 목각
				13516,		// 나이트발드
				14534,		// 아이리스
				13520,		// 데스나이트
				15975,		// 바포메트
				13464,		// 데몬
				15978,		// 커츠
			};
		버경NPC = new 버경NPC();
		버경NPC.setObjectId(ServerDatabase.nextEtcObjId());
		버경NPC.setName(버경NPC이름);
		버경NPC.setGfx(782);
		버경NPC.setHomeX(32763);
		버경NPC.setHomeY(32836);
		버경NPC.setHomeMap(5143);
		버경NPC.setHeading(4);
		버경NPC.setTitle("");
		버경NPC.toTeleport(버경NPC.getHomeX(), 버경NPC.getHomeY(), 버경NPC.getHomeMap(), false);
		
		joinList = new ArrayList<PcInstance>();
		list_Finish = new ArrayList<String>();
		line = new ArrayList<BackgroundInstance>();
		TimeLine.end();
	}
	
	
	
	static public void toTimer(long time) {
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int hour = date.getHours();
		int min = date.getMinutes();
		int sec = date.getSeconds();
		

		
		for (TeamBattleTime tebeTime : Lineage.bug_list) {
			// 1분 동안만 입장 받음
			if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min ) {
				// 1분전 혹시모를 한번더 초기화
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 00){
					startTeamBattle = false;
					joinEnd = false;
					askTeamBattle = false;
					line.clear();
					list_Finish.clear();
					joinList.clear();
					count = 30;
					countgo = false;	
				}
				//1분전
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 10){

					askTeamBattle = true;
					joinEnd = false;
					toAskTeamBattle("1분");
					spawnLine();
						
//					toMessage(버경NPC, String.format("인형레이스가 1분뒤  시작 합니다. " , sec));
				
					String msg = "\\fY  [알림] 인형레이스가 1분뒤 시작 합니다.";
	            	World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

				}

				// 30초전 
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 30){
					
					askTeamBattle = true;
					joinEnd = false;
					toAskTeamBattle("30초");
					
					String msg = "\\fY  [알림] 인형레이스가 30초뒤 시작 합니다.";
        			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
					
				}
				// 10초전 입장 종료
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec == 50 && !joinEnd) {
					joinEnd = true;
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "[알림] 인형레이스의 입장이 마감되었습니다."));
				}

				
				// 10초전 카운팅
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 50  ){
				
			        toMessage(버경NPC, String.format("경기 시작 10 초 전 입니다 " , sec));		
			     }
				
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 51  ){

			        toMessage(버경NPC, String.format("경기 시작 9 초 전 입니다 " , sec));		
			     }
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 52  ){

            		toMessage(버경NPC, String.format("경기 시작 8 초 전 입니다 " , sec));		
				}
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 53  ){

            		toMessage(버경NPC, String.format("경기 시작 7 초 전 입니다 " , sec));		
				}
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 54  ){

            		toMessage(버경NPC, String.format("경기 시작 6 초 전 입니다 " , sec));		
				}
				//5초전 부터는 입장마감으로 하기
				
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 55  ){

            		toMessage(버경NPC, String.format("경기 시작 5 초 전 입니다 " , sec));		
				}
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 56  ){

            		toMessage(버경NPC, String.format("경기 시작 4 초 전 입니다 " , sec));		
				}
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 57  ){

            		toMessage(버경NPC, String.format("경기 시작 3 초 전 입니다 " , sec));		
				}
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 58  ){

            		toMessage(버경NPC, String.format("경기 시작 2 초 전 입니다 " , sec));		
				}
				if (tebeTime.getHour() == hour && tebeTime.getMin() - 1 == min && sec  == 59  ){

            		toMessage(버경NPC, String.format("경기 시작 1 초 전 입니다 " , sec));	
            		
            		if(joinList.size() <= 0){
            			synchronized (joinList) {
	            			for (PcInstance pc : joinList) {
	            			
	            				pc.setGfx(pc.getClassGfx());
	            				int[] loc = Lineage.getHomeXY();
	            				pc.toTeleport(loc[0], loc[1], loc[2], true);
	            				
	            			
	            				ChattingController.toChatting(pc, String.format("[알림] 인원이 부족하여 인형레이스를 시작 할 수 없습니다."), Lineage.CHATTING_MODE_MESSAGE);
	            				ChattingController.toChatting(pc, String.format("[알림] 마을로 이동합니다."), Lineage.CHATTING_MODE_MESSAGE);
	            				toMessage(버경NPC, String.format(" 인원이 부족하여 인형레이스를 종료합니다. "));
	
	                		
	            			
	            			}
            			}
            			String msg = "\\fY  [알림] 인원이 부족하여 인형레이스가 시작되지않았습니다.";
            			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
            			
            			for (BackgroundInstance teamLine2 : line) {
            				teamLine2.clearList(true);
            				World.remove(teamLine2);
            			}
            			
            	
            			endrace();
        		
            		}else{
            			
            			askTeamBattle = false;
        				startTeamBattle = true;
            	  		toMessage(버경NPC, String.format("경기 시작! " , sec));	
            			bugEndTime = time + (1000 * Lineage.bug_play_time);
                		startbugrace();
            		}
            		
          
				}
			}
	
		}

		if(status ==EVENT_STATUS.PLAY ){
			
			endzone();
		}
		
		if(countgo){
			countDownAndEndGame();
		}
		

	
	}
	// 팀대전에 참여하시겠습니까? (y/n)
	static public void toAskTeamBattle(String time) {
		for (PcInstance pc : World.getPcList()) {
			if (!pc.isWorldDelete() && !checkList(pc))
				pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 779, time));
		}
	}
	static public void removeList(PcInstance pc) {
		synchronized (joinList) {
			if (joinList.contains(pc)) {
				joinList.remove(pc);
	
			}
		}
		int[] loc = Lineage.getHomeXY();
		pc.toTeleport(loc[0], loc[1], loc[2], true);
	}
	

	// YES / NO 대답을 했을시
		static public void toAsk(PcInstance pc, boolean yes) {
			if (yes && !pc.isWorldDelete() && !pc.isDead() && !pc.isLock() && !startTeamBattle && pc.getMap() != 5143 && !joinEnd && askTeamBattle && !pc.isFishing()) {
				if ( getJoinListSize() > 30) {
					ChattingController.toChatting(pc, String.format("인형레이스 입장 인원 30명을 초과하여 입장이 불가능합니다."), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}

					
					appendList(pc);
		
				
			} else {
				if (yes) {
					if (pc.isDead() || pc.isLock())
						ChattingController.toChatting(pc, "[알림] 현재 상태에서는 입장이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					if (pc.isFishing())
						ChattingController.toChatting(pc, "[알림] 낚시중엔 입장이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					if (startTeamBattle)
						ChattingController.toChatting(pc, "[알림] 인형레이스가 이미 시작되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					if (joinEnd)
						ChattingController.toChatting(pc, "[알림] 인형레이스 입장시간이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		}
	static private class 버경NPC extends object {

	}
	static public void spawnLine() {
		int y = 32845;
		
		for (int i = 0; i < 9; i++)
			line.add(new lineage.world.object.npc.background.BattleRoyalTeamLine());

		for (BackgroundInstance teamLine2 : line) {
			teamLine2.setGfx(10467);
			teamLine2.setObjectId(ServerDatabase.nextEtcObjId());
			teamLine2.toTeleport(32764, y, 5143, false);
			y++;
		}
	}
	static private void toMessage(object o, String msg) {
		ChattingController.toChatting(o, msg, Lineage.CHATTING_MODE_SHOUT);
	}
	
	static public void startbugrace() {

		for (BackgroundInstance teamLine2 : line) {
			teamLine2.clearList(true);
			World.remove(teamLine2);
		}
		

		
		status = EVENT_STATUS.PLAY;
	}
	static public void endrace() {
		for (BackgroundInstance teamLine2 : line) {
			teamLine2.clearList(true);
			World.remove(teamLine2);
		}
		
		
		startTeamBattle = false;
		askTeamBattle = false;
		joinEnd = false;
		line.clear();
		list_Finish.clear();
		joinList.clear();
		count = 30;
		countgo = false;
	}

	static public void endzone() {
		synchronized (joinList) {
		for (PcInstance pc : joinList) {
		    if (pc.getMap() == 5143 && pc.getX() >= 32767 && pc.getX() <= 32773 && pc.getY() >= 32811 && pc.getY() <= 32818) {
		        if (!list_Finish.contains(pc.getName())) {
		            list_Finish.add(pc.getName());
		            countgo = true;
		            
		            toMessage(버경NPC, String.format("[알림] 인형레이스 %d등 %s ", list_Finish.size(), list_Finish.get(list_Finish.size() - 1)));

		            ItemInstance rewardItem = null;
		            switch (list_Finish.size()) {
		                case 1:
		                    rewardItem = ItemDatabase.newInstance(ItemDatabase.find("인형레이스 1등 보상"));
		                    break;
		                case 2:
		                    rewardItem = ItemDatabase.newInstance(ItemDatabase.find("인형레이스 2등 보상"));
		                    break;
		                case 3:
		                    rewardItem = ItemDatabase.newInstance(ItemDatabase.find("인형레이스 3등 보상"));
		                    break;
		                case 4:
		                    rewardItem = ItemDatabase.newInstance(ItemDatabase.find("인형레이스 4등 보상"));
		                    break;
		                case 5:
		                    rewardItem = ItemDatabase.newInstance(ItemDatabase.find("인형레이스 5등 보상"));
		                    break;
		            }

		            if (rewardItem != null) {
		                rewardItem.setCount(1);
		                pc.toGiveItem(null, rewardItem, rewardItem.getCount());
		                pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x04));
		            }
		        }
		    }
		}
		}
	}
	//카운트다운
		public static void countDownAndEndGame() {
		    if (countgo) {
		        if (count >= 0) {
		            count--;
		            toMessage(버경NPC, String.format("경기 종료 %d 초 전 입니다 ",count )); 
		        }

		        if (count == 0) {
		            for (PcInstance pc : joinList) {
		                toMessage(버경NPC, String.format("고생하셧습니다. 인형레이스가 종료됫습니다. "));
		                ChattingController.toChatting(pc, String.format("[알림] 고생하셧습니다. 인형레이스가 종료됫습니다."), Lineage.CHATTING_MODE_MESSAGE);
		                ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("인형레이스 참여보상"));
		                ii.setCount(1);
		                pc.toGiveItem(null, ii, ii.getCount());
		                pc.setGfx(pc.getClassGfx());        
		                int[] loc = Lineage.getHomeXY();
		                pc.toTeleport(loc[0], loc[1], loc[2], true);
		                pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x05));
		            }
		          	endrace();
		        }
		  
		    }
		}
	// 팀대전 참여자 추가
	static public void appendList(PcInstance pc) {
		synchronized (joinList) {
			if (!joinList.contains(pc)) {
				joinList.add(pc);
				double random=Math.random();
				int num = (int)Math.round(random * (MAGIC_DOLL_GFX.length-1));
				
				//입장전 무기 해제
				if(pc.getInventory().getSlot(Lineage.SLOT_WEAPON) != null){
					pc.getInventory().getSlot(Lineage.SLOT_WEAPON).toClick(pc, null);
				}
			

				pc.setGfx(MAGIC_DOLL_GFX[num]);
				pc.setGfxMode(0);
				
				pc.setNowMp(0);
				
				//입장시 이속 버프 제거
				BuffController.remove(pc, Bravery.class);
				BuffController.remove(pc, GreaterHaste.class);
				BuffController.remove(pc, HolyWalk.class);
				BuffController.remove(pc, Wafer.class);
				BuffController.remove(pc, Haste.class);				
				BuffController.remove(pc, HastePotionMagic.class);
				BuffController.remove(pc, movingacceleratic.class);
				pc.toPotal(Util.random(33508, 33510), Util.random(32863, 32865), 5143);
				
				// 마법인형 소환이 처음일때
				if (pc.getMagicDollinstance() != null) {
	
					MagicDollController.toDisable(pc, pc.getMagicDoll(), false);
				}
				Summon s = SummonController.find(pc);
				if (s != null) {
					s.removeAllPet();

				}
				
				pc.toPotal(Util.random(32769, 32772), Util.random(32848, 32851), 5143);
				ChattingController.toChatting(pc, "인형레이스 참여가 완료 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}

	}
	
	static public boolean checkList(PcInstance pc) {
		synchronized (joinList) {
			if (joinList.contains(pc)) {
				return true;
			} else {
				return false;
			}
		}
	}

	static public List<PcInstance> getJoinList() {
		synchronized (joinList) {
			return joinList;
		}
	}
	
	static public int getJoinListSize() {
		synchronized (joinList) {
			return joinList.size();
		}
	}
	

}
