package lineage.world.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.database.BackgroundDatabase;
import lineage.database.ItemDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BlueMessage;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.background.ThebesGate;
//
public class Thebes implements Runnable {
	////
	
	//	 x : 0, y :1 ,map : 2
	private int Location[][] = { 
			{ 32851, 32706, 4 }, // 카오틱신전
			{ 32829, 32641, 4 }, // 카오틱신전
			{ 32731, 32696, 4 }, // 카오틱신전

			{ 32959, 33249, 4 }, // 오아시스
			{ 32904, 33170, 4 }, // 오아시스
			{ 32925, 33417, 4 }, // 오아시스

			{ 34271, 33362, 4 }, // 황혼의 산맥
			{ 34226, 33310, 4 }, // 황혼의 산맥
			{ 34256, 33193, 4 } // 황혼의 산맥
	};
	
	private int EndLocation[][] = { { 32639, 32874, 780 }, { 32639, 32877, 780 }, { 32635, 32877, 780 },
			{ 32643, 32873, 780 } };
	private ThebesGate 시작점;
	private int 도착점;

	private int 보스스폰위치[] = { 32781, 32832, 782 };

	private String 보상목록[][] = { { "균열의 핵", "1" }, { "균열의 핵", "1" }, { "균열의 핵", "1" }, { "균열의 핵", "1" },
			{ "균열의 핵", "1" }, { "균열의 핵", "1" }, { "완력의 목걸이", "1" }, { "민첩의 목걸이", "1" }, { "체력의 목걸이", "1" },
			{ "지혜의 목걸이", "1" }, { "매력의 목걸이", "1" }, { "지식의 목걸이", "1" }, { "잠긴 상급 오시리스의 보물상자", "1" },
			{ "잠긴 상급 오시리스의 보물상자", "1" }, { "잠긴 상급 오시리스의 보물상자", "1" }, { "잠긴 상급 오시리스의 보물상자", "1" },
			{ "잠긴 상급 오시리스의 보물상자", "1" }, { "잠긴 상급 오시리스의 보물상자", "1" }, { "열린 상급 오시리스의 보물상자", "1" },
			{ "열린 상급 오시리스의 보물상자", "1" }, { "열린 상급 오시리스의 보물상자", "1" }, { "테베 오시리스의 벨트", "1" }, { "테베 호루스의 반지", "1" },
			{ "테베 아누비스의 반지", "1" } };
	//
	private final int 시작대기 = 0;
	private final int 테베문교체 = 1;
	private final int 보스소환대기 = 2;
	private final int 테베진행 = 3;
	private final int 테베종료 = 4;

	private int 테베연장 = 0;
	private boolean 강제시작 = false;
	private boolean 진행중 = false;
	////
	private int 현재상태 = 0;
	private List<ThebesGate> 균열리스트;
	//
	private List<PcInstance> 보스방입장리스트;
	private static Thebes ins;
	//
	public static Thebes getInstance() {
		if (ins == null) {
			ins = new Thebes();
		}
		return ins;
	}

	public Thebes() {
		시작점 = (ThebesGate) BackgroundDatabase.toSpawnBackground("", "", 6919, 0, 0, 0, 0, 0, 0, 0, 0);
		boss = new ArrayList<MonsterInstance>();
		균열리스트 = new ArrayList<ThebesGate>();
		보스방입장리스트 = new ArrayList<PcInstance>();
		문스폰();
		Thread t = new Thread(this);
		t.start();
	}

	private boolean isLog = true;
	
	@Override
	public void run() {
		while (true) {
			try {
				동작();
			} catch (Exception e) {
				if (isLog) {
					isLog = false;
					Log.toError(e);
				}
//				e.printStackTrace();
			}
		}
	}


	private void 문스폰() {
		for (int i = 0; i < Location.length; i++) {
			try {
				ThebesGate m = (ThebesGate) BackgroundDatabase.toSpawnBackground("", "", 6920, 0, 0, 0, Location[i][0],
						Location[i][1], Location[i][2], 0, 0);
				균열리스트.add(m);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//
	public boolean 현재진행중() {
		return 진행중;
	}
	//
	private void 시작문스폰(int x, int y, int map) {
		시작점.setX(x);
		시작점.setY(y);
		시작점.setMap(map);
		시작점.setHomeX(x);
		시작점.setHomeY(y);
		시작점.setMap(map);
		시작점.toTeleport(x, y, map, false);

	}
	//
	public int[] 도착점정보() {
		if (도착점 != -1)
			return EndLocation[Util.random(0, EndLocation.length - 1)];
		return null;
	}
	//
	public boolean 테베문체크(PcInstance pc) {
		if (시작점 == null)
			return false;
		if (현재상태 == 시작대기 && !진행중)
			return false;
		if (도착점 == -1)
			return false;
		if (Math.abs(pc.getX() - 시작점.getX()) > 1)
			return false;
		if (Math.abs(pc.getY() - 시작점.getY()) > 1)
			return false;
		if (pc.getMap() != 시작점.getMap())
			return false;

		return true;
	}

	private void 문제거() {
		for (ThebesGate 문 : 균열리스트) {
			문.toAiThreadDelete();
		}
		균열리스트.clear();
	}

	public void 테베강제시작(boolean t) {
		강제시작 = t;
		//	
	}

	public void 테베강제종료(boolean t) {
		현재상태 = 4;
		테베연장 = 0;
	}

	private Monster mon = MonsterDatabase.find("테베 호루스");
	private Monster mon2 = MonsterDatabase.find("테베 아누비스");
	List<MonsterInstance> boss;
	
	private void 보스소환() {
		for (PcInstance pc : World.getPcList()) {
			if (pc == null) {
				continue;
			} else if (pc.getMap() >= 780 && pc.getMap() <= 782) {
				
				ChattingController.toChatting(pc, "오시리스: 어리석은 것들..이곳이 어디라고!! 아누비스! 호루스! 저것들을 쓸어버려라!!", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, "아누비스: 너희에게 죽음을....", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, "호루스: 자비는 없다....", Lineage.CHATTING_MODE_MESSAGE);
				
			}
		}

		MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(mon);
		if (mi != null) {
			mi.setHomeX(보스스폰위치[0]);
			mi.setHomeY(보스스폰위치[1]);
			mi.setHomeMap(보스스폰위치[2]);
			mi.setHeading(3);
			mi.setPineWand(true);
			mi.toTeleport(보스스폰위치[0], 보스스폰위치[1], 보스스폰위치[2], false);
			////
			AiThread.append(mi);
			boss.add(mi);
		}
		
		MonsterInstance mi2 = MonsterSpawnlistDatabase.newInstance(mon2);
		if (mi2 != null) {
			mi2.setHomeX(보스스폰위치[0] + 4);
			mi2.setHomeY(보스스폰위치[1] + 4);
			mi2.setHomeMap(보스스폰위치[2]);
			mi2.setHeading(3);
			mi2.setPineWand(true);
			mi2.toTeleport(보스스폰위치[0] + 4, 보스스폰위치[1] + 4, 보스스폰위치[2], false);
			///
			AiThread.append(mi2);
			boss.add(mi2);
		}
		/*
		boss.add(mi);
		boss.add(mi2);
		*/
	}
	//
	private void 보스제거() {
		if (boss.size() == 0)
			return;
		for (MonsterInstance m : boss) {
			m.toAiThreadDelete();
		}
		boss.clear();
	}

	public synchronized void 보스처치() {
		테베연장++;
	}

	public boolean 보스소환여부() {
		return 현재상태 == 테베진행;
	}

	public boolean 보스소환대기() {
		return 현재상태 == 보스소환대기;
	}

	public List<PcInstance> 보스방입장인원() {
		return 보스방입장리스트;
	}

	
	private void 유저추방(boolean 승리) {
		for (PcInstance pc : World.getPcList()) {
			if (pc == null) {
				continue;
			}
			if (승리 && pc.getMap() == 782) {
				pc.toTeleport(33440, 32800, 4, true);
			} else if (!승리 && pc.getMap() >= 780 && pc.getMap() <= 782) {
				pc.toTeleport(33440, 32800, 4, true);
			}
			
			if (!승리) {
				ChattingController.toChatting(pc, "시간의 균열이 사라집니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	private void 아이템보상() {
		for (int index = 0; index < 보상목록.length; index++) {
			String 보상 = 보상목록[index][0];
			int max = Integer.parseInt(보상목록[index][1]);

			List<PcInstance> rewardList = new ArrayList<>();
			World.getPcList().forEach(mem -> {
				if (mem.getMap() == 782)
					rewardList.add(mem);
			});
			int random = Util.random(0, rewardList.size() - 1);

			if (random >= 0) {
				PcInstance pc = rewardList.get(Util.random(0, rewardList.size() - 1));
				아이템주기(pc, 보상, max, rewardList);
			}
		}
	}

	private void 아이템주기(PcInstance pc, String name, int max, List<PcInstance> rewardList) {
		try {
			int count = Util.random(1, max);
			int en = 0;
			int bress = 1;
			Item i = ItemDatabase.find(name);
			if (i != null) {
				// 메모리 생성 및 초기화.
				ItemInstance temp = ItemDatabase.newInstance(i);
				temp.setCount(count);
				temp.setEnLevel(en);
				temp.setBless(bress);
				temp.setDefinite(false);
				// 인벤에 등록처리.
				pc.getInventory().append(temp, count);
				// 알림.
				for (PcInstance p : rewardList) {
					if (p == null)
						continue;
					ChattingController.toChatting(p, String.format("%s님께서 %s를 얻었습니다.", pc.getName(), name), Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void 성공멘트() {
		for (PcInstance pc : World.getPcList()) {
			if (pc == null) {
				continue;
			}

			ChattingController.toChatting(pc, "시간의 균열의 힘이 약해졌습니다. 하루 동안 이계의 공간이 유지 됩니다.",
					Lineage.CHATTING_MODE_MESSAGE);

		}
	}

	private void 승리대사() {
		for (PcInstance pc : World.getPcList()) {
			if (pc == null) {
				continue;
			}
			if (pc.getMap() == 782) {
				ChattingController.toChatting(pc, "테베 오시리스: 이럴수가..!!! 우리가 졌다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, "테베 오시리스: 지금 이 시간부터 하루 동안 테베라스를 개방하도록 하겠다.",
						Lineage.CHATTING_MODE_MESSAGE);

			}
		}
	}

	private void 패배대사() {
		for (PcInstance pc : World.getPcList()) {
			if (pc == null) {
				continue;
			}
			if (pc.getMap() >= 780 && pc.getMap() <= 782) {

				ChattingController.toChatting(pc, "테베 오시리스: 너희들은 실패했다!!!", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	private void 시스템메세지(int start, int period, boolean 승리) throws Exception {
		for (int t = start; t > 0; t -= period) {
			for (PcInstance pc : World.getPcList()) {
				if (pc == null) {
					continue;
				}
				if (승리) {
					if (pc.getMap() == 782)
						ChattingController.toChatting(pc, "시스템 메세지 : " + t + "초 후에 텔레포트 합니다.",
								Lineage.CHATTING_MODE_MESSAGE);
				} else if (pc.getMap() >= 780 && pc.getMap() <= 782) {
					ChattingController.toChatting(pc, "시스템 메세지 : " + t + "초 후에 텔레포트 합니다.",
							Lineage.CHATTING_MODE_MESSAGE);
				}
			}
			Thread.sleep(period * 1000);
		}
	}

	Calendar cal = Calendar.getInstance();

	private void 동작() throws Exception {
		cal = Calendar.getInstance();
		// System.out.println("현재상태 : "+현재상태+" 잡은 몹 : "+테베연장+" / 도착점 : "+도착점+" 강제시작여부 :
		// "+강제시작);
		switch (현재상태) {
		case 시작대기: //HOUR_OF_DAY : 20시간  MINUTE : 3분
			if (cal.get(Calendar.HOUR_OF_DAY) == 20 && cal.get(Calendar.MINUTE) <= 3 || 강제시작) {
				현재상태++;
				강제시작 = false;
			}
			break;
		case 테베문교체:
			진행중 = true;
			// 문제거하고 특별 해당하는 문만 새로 오픈
			int 랜덤문번호 = Util.random(0, 8);
			도착점 = Util.random(0, 3);
			문제거();
			시작문스폰(Location[랜덤문번호][0], Location[랜덤문번호][1], Location[랜덤문번호][2]);
			보스제거();

			for (PcInstance pc : World.getPcList()) {
				if (pc == null) {
					continue;
				}
				String msg = "시간의 균열이 열렸습니다. 이계의 침공이 시작 됩니다.";
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

				// 화면 중앙에 메세지 알리기.
				if (Lineage.is_blue_message)
					World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
				//ChattingController.toChatting(pc, "시간의 균열이 열렸습니다. 이계의 침공이 시작 됩니다.", Lineage.CHATTING_MODE_MESSAGE);
			}

			현재상태++;
			break;
		case 보스소환대기:
			if (cal.get(Calendar.HOUR_OF_DAY) == 22 && cal.get(Calendar.MINUTE) == 30) {
				보스소환();
				현재상태++;
			}
			break;
		case 테베진행:
			if (cal.get(Calendar.HOUR_OF_DAY) == 23 || 테베연장 >= 2) {
				현재상태++;
			}
			break;
		case 테베종료:

			if (테베연장 < 2) {
				// 연장되지않았다면 시작지점 0,0,0으로 변경
				도착점 = -1;
				시작문스폰(0, 0, 0);
				문스폰();
				진행중 = false;
				패배대사();
				Thread.sleep(3000);
				// 유저팅구기
				시스템메세지(5, 1, false);
				유저추방(false);

			} else {
				성공멘트();
				아이템보상();
				Thread.sleep(3000);
				승리대사();
				Thread.sleep(3000);
				시스템메세지(30, 10, false);
				유저추방(true);
			}
			보스방입장리스트.clear();
			테베연장 = 0;
			현재상태 = 0;
			break;
		}
		Thread.sleep(60000);
	}
	
}  //-- end class