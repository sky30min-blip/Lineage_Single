package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.database.TimeDungeonDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.PcInstance;

public class CallClan {

	static public void init(Character cha, Skill skill, String name) {
		// 처리
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if (SkillController.isMagic(cha, skill, true)) {
			// 공성전중 콜클렌이 가능한지 확인.
			if (!Lineage.kingdom_war_callclan) {
				Kingdom k = KingdomController.findKingdomLocation(cha);
				if (k != null && k.isWar())
					return;
			}
			// 콜클렌 가능 지역 및 맵인지 확인.(랜덤텔레포트 가능한 맵 체크로 함.)
			// 시간제 던전은 콜클랜 못함.
			boolean isTPM = false;
			for (int mi : Lineage.TeleportPossibleMap) {
				if (cha.getMap() == mi && !TimeDungeonDatabase.isTimeDungeon(cha.getMap())) {
					isTPM = true;
					break;
				}
			}
			
			// 말섬이나 본토가 아니면 콜클랜 못함.
			if (cha.getMap() != 0 && cha.getMap() != 4)
				isTPM = false;
			
			if (!isTPM) {
				// \f1여기에서는 사용할 수 없습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 563));
				return;
			}

			Clan c = ClanController.find(cha.getClanId());
			if (c == null) {
				ChattingController.toChatting(cha, "\\fY혈맹을 가진 군주만 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (cha.getClanGrade() != 3) {
				ChattingController.toChatting(cha, "\\fY군주 직위를 가져야 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			PcInstance pc = c.find(name);
			if (pc != null && pc.getObjectId() != cha.getObjectId() && !pc.isFishing()) {
				c.appendCallList(pc);
				// 729 군주님께서 부르십니다. 소환에 응하시겠습니까? (y/N)
				pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 729));
			} else {
				if (pc == null)
					ChattingController.toChatting(cha, "\\fY혈맹원이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
				if (pc != null && pc.isFishing())
					ChattingController.toChatting(cha, "\\fY낚시 중인 혈맹원은 소환할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

}
