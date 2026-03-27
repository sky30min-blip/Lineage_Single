package lineage.world.controller;

import lineage.database.ItemDatabase;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.npc.event.Keplisha;

public class LuckyController {
	static private int ADEN;				// 운세볼때 사용될 아덴
	static private enum TODAYLUCKY {			// 운세 종류
		Bad,
		Good,
		Perfect
	};
	
	static public void init() {
		TimeLine.start("LuckyController..");
		ADEN = 1000;
		
		TimeLine.end();
	}
	
	static public void close() {
		
	}
	
	/**
	 * 케플리샤 클릭시 호출됨.
	 * @param npc
	 * @param pc
	 */
	static public void toLucky(Keplisha npc, PcInstance pc) {
		// 항아리 체크.
		if(pc.getInventory().findDbNameId(5204) == null) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), npc, "keplisha1"));
			return;
		}
		// 구슬 체크.
		if(pc.getInventory().findDbNameId(5205) != null) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), npc, "keplisha2"));
			return;
		}
		// 부적 체크.
		if(pc.getInventory().findDbNameId(5206) != null) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), npc, "keplisha3"));
			return;
		}
		// 이도저도 아니면.
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), npc, "keplisha4"));
	}
	
	/**
	 * 케플리샤 클릭 후 액션 취할때 호출됨.
	 * @param npc
	 * @param pc
	 * @param action
	 */
	static public void toLuckyFinal(Keplisha npc, PcInstance pc, String action) {
		if(action.equalsIgnoreCase("0")) {
			// 항아리 생성
			CraftController.toCraft(npc, pc, ItemDatabase.find(5204), 1, true);
			// 케플리샤와 영혼의 계약을 맺는다.
			// keplisha4 또는 keplisha7
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), npc, "keplisha7"));
		}
		if(action.equalsIgnoreCase("1")) {
			// 후원금을 내고 운세를 본다
			// 아데나 체크
			if(pc.getInventory().isAden(ADEN, true)) {
				// 운세 보여주기 나중에 세밀하게 세팅
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), npc, "horosb38"));
				// 구슬 찾기.
				ItemInstance bead = pc.getInventory().findDbNameId(5205);
				if(bead != null) {
					// 구슬 삭제
					pc.getInventory().count(bead, bead.getCount()-1, true);
					// 부적 생성
					CraftController.toCraft(npc, pc, ItemDatabase.find(5206), 1, true);
				}
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), npc, "keplisha8"));
			}
		}
		if(action.equalsIgnoreCase("2")) {
			// 케플리샤의 축복을 받는다.
			if(pc.getGfx() != pc.getClassGfx()) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), npc, "keplisha9"));
			} else {
				ItemInstance charm = pc.getInventory().findDbNameId(5206);
				if(charm != null) {
					// 부적 삭제
					pc.getInventory().count(charm, charm.getCount()-1, true);
					// 변신
					toPoly(pc);
					// 변신후 마지막 npc창 이부분도 세밀하게 세팅해야됨.
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), npc, "horomon23"));
				}
			}
		}
		if(action.equalsIgnoreCase("3")) {
		    // 항아리 아이템 검색
		    ItemInstance jar = pc.getInventory().findDbNameId(5204);
		    ItemInstance bead = pc.getInventory().findDbNameId(5205);
		    
		    if(jar != null && bead != null) {
		        // 항아리와 구슬 아이템 제거
		    	pc.getInventory().count(jar, jar.getCount()-1, true);
		    	pc.getInventory().count(bead, bead.getCount()-1, true);
		        // 창 제거
		        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), npc, " "));
		    }
		}
	}
	
	static private void toPoly(PcInstance pc) {
	    String[] types = { "오늘의 운세(1)", "오늘의 운세(2)", "오늘의 운세(3)", "오늘의 운세(4)", "오늘의 운세(5)", "오늘의 운세(6)", "오늘의 운세(7)", "오늘의 운세(8)", "오늘의 운세(9)", "오늘의 운세(10)", "오늘의 운세(11)", "오늘의 운세(12)", "오늘의 운세(13)" };
	    int randomIndex = (int) (Math.random() * types.length);
	    String type = types[randomIndex];
	    if (type != null) {
	        if (type.equals("오늘의 운세(1)") || type.equals("오늘의 운세(2)") || type.equals("오늘의 운세(3)") || type.equals("오늘의 운세(4)") || type.equals("오늘의 운세(5)") || type.equals("오늘의 운세(6)") || type.equals("오늘의 운세(7)") || type.equals("오늘의 운세(8)") || type.equals("오늘의 운세(9)") || type.equals("오늘의 운세(10)")) {
	            // 이펙트 6130번 적용 후 변신
	            pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 6130), true);
	        } else if (type.equals("오늘의 운세(11)") || type.equals("오늘의 운세(12)") || type.equals("오늘의 운세(13)")) {
	            // 이펙트 6133번 적용 후 변신
	            pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 6132), true);
	        }
	        // 변신 적용
	        ShapeChange.onBuff(pc, pc, PolyDatabase.getPolyName(type), 1800, false, true);
	    }
	}
}