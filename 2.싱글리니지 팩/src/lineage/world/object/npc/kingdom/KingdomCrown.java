package lineage.world.object.npc.kingdom;

import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_KingdomAgent;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.KingdomController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class KingdomCrown extends object {

	private Kingdom kingdom;
	
	public KingdomCrown(Kingdom kingdom){
		this.kingdom = kingdom;
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		if (cha instanceof PcInstance && cha.getInventory() != null && Util.isDistance(this, cha, 1)) {
			if (cha.getInventory().getSlot(Lineage.SLOT_WEAPON) != null) {
				ChattingController.toChatting(cha, "무기 해제 후 시도하시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
				
			Clan c = ClanController.find(cha.getClanName());
			if (c == null)
				return;
			// 처리 가능여부 확인
			if (cha.isDead() || kingdom == null || cha.isInvis() || cha.getClanId() == 0 || cha.getClanId() == kingdom.getClanId() || cha.getClanGrade() != 3
				|| c.getList().size() < Lineage.crown_clan_min_people || cha.getGfx() != cha.getClassGfx() || KingdomController.find((PcInstance) cha) != null
				|| cha.getClanName().equalsIgnoreCase(Lineage.teamBattle_A_team) || cha.getClanName().equalsIgnoreCase(Lineage.teamBattle_B_team) 
				|| cha.getClanName().equalsIgnoreCase(Lineage.new_clan_name) || cha.getClassType() != Lineage.LINEAGE_CLASS_ROYAL)
				return;
			
			if (cha.getClanId() == kingdom.getClanId()) {
				ChattingController.toChatting(cha, String.format("%s의 면류관을 이미 차지하였습니다.", kingdom.getName()), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (c.getList().size() < Lineage.crown_clan_min_people) {
				ChattingController.toChatting(cha, String.format("%d명 이상 접속중인 혈맹만 면류관을 차지할 수 있습니다.", Lineage.crown_clan_min_people), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			if (cha.getGfx() != cha.getClassGfx()) {
				ChattingController.toChatting(cha, "변신 해제 후 면류관을 차지할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (cha.getClanGrade() != 3) {
				ChattingController.toChatting(cha, "군주 직위만 면류관을 차지할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (KingdomController.find((PcInstance) cha) != null) {
				ChattingController.toChatting(cha, "이미 다른 성을 소유중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			// 줍기 표현.
			cha.setHeading( Util.calcheading(cha, x, y) );
			
			if (SpriteFrameDatabase.findGfxMode(cha.getGfx(), Lineage.GFX_MODE_GET))
				cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_GET), true);
			
			World.toSender( S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "[알림] "+ c.getName() + "혈맹이 면류관을 차지하였습니다.") );

			// 성정보 변경.
			kingdom.setAgentId(cha.getObjectId());
			kingdom.setAgentName(cha.getName());
			kingdom.setClanId(cha.getClanId());
			kingdom.setClanName(cha.getClanName());
			
			if (Lineage.giran_kingdom_crown_min > 0) {
				int crownTime = Lineage.giran_kingdom_crown_min;
				kingdom.setCrownPickupEnd(System.currentTimeMillis() + (1000 * crownTime));
				
				if (crownTime / 3600 > 0) {
					World.toSender( S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("%d시간 %d분 %d초 동안 방어에 성공할 경우 공성전이 종료됩니다.", 
							crownTime / 3600, crownTime % 3600 / 60, crownTime % 3600 % 60)) );
				} else if (crownTime % 3600 / 60 > 0) {
					World.toSender( S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("%d분 %d초 동안 방어에 성공할 경우 공성전이 종료됩니다.", 
							crownTime % 3600 / 60, crownTime % 3600 % 60)) );
				} else {
					World.toSender( S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("%d초 동안 방어에 성공할 경우 공성전이 종료됩니다.", 
							crownTime % 3600 % 60)) );
				}
			}

			// 리니지 월드에 해당 성에 대한 성주정보 다시 잡아주기
			World.toSender(S_KingdomAgent.clone(BasePacketPooling.getPool(S_KingdomAgent.class), kingdom.getUid(), kingdom.getAgentId()));
			
			// 모두 마을로 귀환. 성혈은 내성으로 이동됨.
			kingdom.toTeleport(false, true);
			
			// 수호탑 복구.
			for(KingdomCastleTop kct : kingdom.getListCastleTop())
				kct.toRevival(null);
			
			// 면류관 표현 제거.
			kingdom.getCrown().clearList(true);
			kingdom.getCrownVisual().clearList(true);
			World.remove(kingdom.getCrown());
			World.remove(kingdom.getCrownVisual());
			
			
			for (KingdomDoor kd : kingdom.getListDoor())
				kd.toReset(false);			
		}
	}
}
