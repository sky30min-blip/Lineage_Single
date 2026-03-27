package lineage.world.object.npc.craft;

import lineage.bean.database.Npc;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SummonController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Rodney extends CraftInstance {

	public Rodney(Npc npc) {
		super(npc);

	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dogdealer"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		String pet_name = null;
		int pet_level = 0;
		int pet_hp = 0;
		int pet_mp = 0;
		int aden = 0;

		if (Lineage.server_version > 144) {
			switch (action) {
			case "buy 1": // 야생견 레벨 4 400
				pet_name = "늑대";
				pet_level = 4;
				pet_hp = 30;
				pet_mp = 5;
				aden = 400;
				break;
			case "buy 2": // 야생견 레벨 5 800
				pet_name = "늑대";
				pet_level = 5;
				pet_hp = 30 + Util.random(4, 7);
				pet_mp = 5 + Util.random(1, 2);
				aden = 800;
				break;
			case "buy 3": // 야생견 레벨 6 1600
				pet_name = "늑대";
				pet_level = 6;
				pet_hp = 30 + Util.random(8, 14);
				pet_mp = 5 + Util.random(2, 4);
				aden = 1600;
				break;
			case "buy 4": // 도베르만 레벨 6 2000
				pet_name = "도베르만";
				pet_level = 6;
				pet_hp = 20;
				pet_mp = 5;
				aden = 2000;
				break;
			case "buy 5": // 도베르만 레벨 7 4400
				pet_name = "도베르만";
				pet_level = 7;
				pet_hp = 20 + Util.random(3, 6);
				pet_mp = 5 + Util.random(1, 2);
				aden = 4400;
				break;
			case "buy 6": // 도베르만 레벨 8 9600
				pet_name = "도베르만";
				pet_level = 8;
				pet_hp = 20 + Util.random(6, 12);
				pet_mp = 5 + Util.random(2, 4);
				aden = 9600;
				break;
			case "buy 7": // 세퍼드 레벨 5 800
				pet_name = "세퍼드";
				pet_level = 5;
				pet_hp = 30;
				pet_mp = 5;
				aden = 800;
				break;
			case "buy 8": // 세퍼드 레벨 6 1700
				pet_name = "세퍼드";
				pet_level = 6;
				pet_hp = 30 + Util.random(5, 7);
				pet_mp = 5 + Util.random(1, 2);
				aden = 1700;
				break;
			case "buy 9": // 세퍼드 레벨 7 3800
				pet_name = "세퍼드";
				pet_level = 7;
				pet_hp = 30 + Util.random(10, 14);
				pet_mp = 5 + Util.random(2, 4);
				aden = 3800;
				break;
			case "buy 10": // 비글 레벨 3 160
				pet_name = "비글";
				pet_level = 3;
				pet_hp = 20;
				pet_mp = 30;
				aden = 160;
				break;
			case "buy 11": // 비글 레벨 4 360
				pet_name = "비글";
				pet_level = 4;
				pet_hp = 20 + Util.random(4, 6);
				pet_mp = 30 + Util.random(3, 4);
				aden = 360;
				break;
			case "buy 12": // 비글 레벨 5 760
				pet_name = "비글";
				pet_level = 5;
				pet_hp = 20 + Util.random(8, 12);
				pet_mp = 30 + Util.random(6, 12);
				aden = 760;
				break;
			}
		}
		// 구매할려는 펫 네임 체크
		if (pet_name == null)
			return;
		if (pc.getInventory().isAden(aden, false) == false) {
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189, "아데나"));
			return;
		}
		// 생성을위해 체크
		MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find(pet_name));
		mi.setLevel(pet_level);
		mi.setMaxHp(pet_hp);
		mi.setMaxMp(pet_mp);
		mi.setNowHp(pet_hp);
		mi.setNowMp(pet_mp);
		mi.setX(pc.getX());
		mi.setY(pc.getY());
		mi.setMap(pc.getMap());
		if (SummonController.toPet(pc, mi))
			pc.getInventory().isAden(aden, true);
		else
			// 멘트
			ChattingController.toChatting(pc, "구매하실려는 펫이 너무 많습니다.", Lineage.CHATTING_MODE_MESSAGE);
		
		MonsterSpawnlistDatabase.setPool(mi);
		// 창 닫기
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));

	}

}
