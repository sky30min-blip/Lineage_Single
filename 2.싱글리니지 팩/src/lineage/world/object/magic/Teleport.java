package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Book;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectLock;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BookController;
import lineage.world.controller.LocationController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Teleport {

	static public void init(Character cha, Skill skill, final ClientBasePacket cbp){
		//cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true)){
			if(onBuff(cha, cbp, 1, false, true))
				cha.toTeleport(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap(), true);
			return;
		}
		
		// 모두 실패했을때 처리.
		unLock(cha, true);
	}
	
	/**
	 * 몬스터 용.
	 * @param mi
	 * @param o
	 * @param ms
	 * @param action
	 * @param effect
	 * @param check
	 */
	static public void init(MonsterInstance mi, object o, MonsterSkill ms, int action, int effect, boolean check){
		//
		mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), false);
		//
		if(check && !SkillController.isMagic(mi, ms, true))
			return;
		//
		if(effect>0)
			mi.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mi, effect), false);
		//
		mi.toTeleport(o.getX(), o.getY(), o.getMap(), effect==0);
	}

	static public boolean onBuff(Character cha, final ClientBasePacket cbp, int bress, boolean item, boolean ment) {
		// 이반을 통한 좌표 텔레포트.
		if (cha instanceof PcInstance && (bress == 0 || cha.getInventory().isRingOfTeleportControl())) {
			// 패킷이 유효하고 시전이 가능한 맵인지 확인.
			if (cbp.isRead(6) && LocationController.isTeleportVerrYedHoraeZone(cha, true)) {
				int map = cbp.readH();
				int x = cbp.readH();
				int y = cbp.readH();
				Book b = BookController.find((PcInstance) cha, x, y, map);
				if (b != null) {
		
					if (!item && !cha.getInventory().isRingOfTeleportControl()  && Util.random(0, 3) == 0) {
						int loc = Util.random(10, 30);
						if (loc == 30) {
							// 랜덤
							Util.toRndLocation(cha);
						} else if (loc == 10) {
							// 지정된 위치로
							cha.setHomeX(x);
							cha.setHomeY(y);
						} else {
							// 일정 범위
							cha.setHomeX(Util.random(x - 10, x - 10));
							cha.setHomeY(Util.random(y - 10, y - 10));
						}
					} else {
						cha.setHomeX(x);
						cha.setHomeY(y);
					}
					cha.setHomeMap(map);
					return true;
				}
			}
		}

		// 랜덤 텔레포트
		if (LocationController.isTeleportZone(cha, true, ment)) {
			Util.toRndLocation(cha);
			return true;
		}

		return false;
	}
	
	static public void unLock(Character cha, boolean message){
		cha.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));

		// 주변의 에너지가 순간 이동을 방해하고 있습니다. 여기에서 순간 이동은 사용할 수 없습니다.
		if(message)
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 647));
	}
	
}
