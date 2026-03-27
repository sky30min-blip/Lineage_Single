package lineage.world.object.magic;

import all_night.Lineage_Balance;
import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_ObjectPoisonLock;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class ShockStun extends Magic {

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time, object o, int effect) {
		if (bi == null)
			bi = new ShockStun(skill, o);
		bi.setSkill(skill);
		bi.setTime(time);
		
		bi.setEffect(new lineage.world.object.npc.background.ShockStun());
		bi.getEffect().setGfx(effect);	
		bi.getEffect().setObjectId(ServerDatabase.nextEtcObjId());
		bi.getEffect().toTeleport(o.getX(), o.getY(), o.getMap(), false);
		if (!o.isLockLow()) {
			o.setLockLow(true);
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x02));
			o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
		}
		
		return bi;
	}

	public ShockStun(Skill skill, object o) {
		super(null, skill);
	}

	@Override
	public void toBuffStart(object o) {

	}

	@Override
	public void toBuff(object o) {
		// 굳게 만들기.
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x02));
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
	}

	@Override
	public void toBuffUpdate(object o) {

	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		//
		getEffect().clearList(true);
		World.remove(getEffect());
		//
		if (o.isWorldDelete())
			return;
		o.setLockLow(false);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x00));
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
	}

	/**
	 * 
	 * @param cha
	 * @param skill
	 * @param object_id
	 * @param x
	 * @param y
	 */
	static public void init(Character cha, Skill skill, int object_id) {
		if (cha.getGm() == 0 && cha.getClassType() != Lineage.LINEAGE_CLASS_ROYAL && cha.getClassType() != Lineage.LINEAGE_CLASS_KNIGHT) {
			ChattingController.toChatting(cha, "\\fY당신의 클래스는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		int dmg = 0;
		int range =  1;
		
		if (cha.getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
			ItemInstance item2 = cha.getInventory().find("엑스칼리버", 0, 1);
			
			if(item2 != null){
				range=4;
			}else{
				range=1;
			}
		}

		// 타겟 찾기
		object o = cha.findInsideList(object_id);
		PcInstance pc = (PcInstance) cha;
		
		if (!World.isAttack(cha, o))
			return;

		if (cha.getInventory().getSlot(Lineage.SLOT_WEAPON) == null) {
			ChattingController.toChatting(cha, "\\fY무기를 착용해야 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}


		if (Lineage_Balance.is_stun_twohandsword) {
			if (cha.getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
				if (!cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("sword") &&
					!cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("tohandsword")) {
					ChattingController.toChatting(cha, "\\fY검을 착용해야 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			} else {
				if (!cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("tohandsword")) {
					ChattingController.toChatting(cha, "\\fY양손검을 착용해야 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
		} else {
			if (!cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("sword") && 
				!cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("tohandsword") &&
				!cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("spear")) {
				ChattingController.toChatting(cha, "\\fY검을 착용해야 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
		}
		if (!Util.isDistance(cha, o, range)) {
			cha.delay_magic = 0;
			ChattingController.toChatting(cha, "\\fY상대방이 너무 멀리있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		// 처리
		if (o != null && Util.isAreaAttack(cha, o) && Util.isAreaAttack(o, cha)) {
			if (SkillController.isMagic(cha, skill, true)) {

				if (!o.isLock()) {
					//dmg = DamageController.getDamage(cha, o, false, cha.getInventory().getSlot(Lineage.SLOT_WEAPON), null, 0);
					// 대미지의 15%만 적용.
					//dmg *= 0.15;
					//DamageController.toDamage(cha, o, dmg, Lineage.ATTACK_TYPE_WEAPON);

					o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
					pc.useShockStunSkill();
					if (SpriteFrameDatabase.findGfxMode(o.getGfx(), o.getGfxMode() + Lineage.GFX_MODE_DAMAGE))
						o.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), o, Lineage.GFX_MODE_DAMAGE), true);
					// 투망상태 해제
					Detection.onBuff(cha);
					// 처리
					if (SkillController.isFigure(cha, o, skill, true, false)) {
						int time = 0;
						int level = cha.getLevel() - o.getLevel();

						// 쇼크 스턴 시간 설정.
						// 시전자와 대상의 레벨차이로 시간을 설정함.
						if (level <= 0 && level >= -3)
							// 동렙부터 상대방이 3레벨 높을 경우
							// 2 ~ 3초
							time = Util.random(2, 3);
						else if (level <= 0 && level >= -5)
							// 상대방이 4 ~ 5레벨이 클 경우.
							// 1 ~ 2초
							time = Util.random(1, 2);
						else if (level <= -6)
							// 상대방이 6레벨이상 클 경우.
							// 0 ~ 1초
							time = Util.random(0, 1);
						else
							// 시전자가 상대방 보다 레벨이 높을 경우.
							time = Util.random(1, skill.getBuffDuration());
						
						ItemInstance item = cha.getInventory().find("포스스턴", 0, 1);
						
						if(item != null){
							time = time+1;
							

							BuffController.append(o, ShockStun.clone(BuffController.getPool(ShockStun.class), skill, time, o, 13545));
						}else{
							if (cha.getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
								ItemInstance item2 = cha.getInventory().find("엑스칼리버", 0, 1);
								
								if(item2 != null){
									o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, 13544), true);
								}
							}
							BuffController.append(o, ShockStun.clone(BuffController.getPool(ShockStun.class), skill, time, o, 11727));
						}

	
					}
				}
			}
		}
	}
	
	static public void init(Character cha, Skill skill, object o) {
		if (cha.getInventory().getSlot(Lineage.SLOT_WEAPON) == null) {
			return;
		}
		
		int range = 1;
		
		if (cha.getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
			ItemInstance item2 = cha.getInventory().find("포스스턴", 0, 1);
			
			if(item2 != null){
				range=4;
			}else{
				range=1;
			}
		}

		if (!World.isAttack(cha, o))
			return;

		if (!Util.isDistance(cha, o, range)) {
			return;
		}
		PcInstance pc = (PcInstance) cha;
		// 처리
		if (o != null && Util.isAreaAttack(cha, o) && Util.isAreaAttack(o, cha)) {
			if (!o.isLock()) {
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				pc.useShockStunSkill();
				// 처리
				if (SkillController.isFigure(cha, o, skill, true, false)) {
					int time = 0;
					int level = cha.getLevel() - o.getLevel();

					// 쇼크 스턴 시간 설정.
					// 시전자와 대상의 레벨차이로 시간을 설정함.
					if (level <= 0 && level >= -3)
						// 동렙부터 상대방이 3레벨 높을 경우
						// 2 ~ 3초
						time = Util.random(2, 3);
					else if (level <= 0 && level >= -5)
						// 상대방이 4 ~ 5레벨이 클 경우.
						// 1 ~ 2초
						time = Util.random(1, 2);
					else if (level <= -6)
						// 상대방이 6레벨이상 클 경우.
						// 0 ~ 1초
						time = Util.random(0, 1);
					else
						// 시전자가 상대방 보다 레벨이 높을 경우.
						time = Util.random(1, skill.getBuffDuration());

					ItemInstance item = cha.getInventory().find("포스스턴", 0, 1);
					
					if(item != null){
						time = time+1;
			
						BuffController.append(o, ShockStun.clone(BuffController.getPool(ShockStun.class), skill, time, o, 13545));
					}else{
						if (cha.getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
							ItemInstance item2 = cha.getInventory().find("엑스칼리버", 0, 1);
							
							if(item2 != null){
								o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, 13544), true);
							}
						}
						BuffController.append(o, ShockStun.clone(BuffController.getPool(ShockStun.class), skill, time, o, 11727));
					}
				}
			}
		}
	}

	/*
	 * 몬스터용
	 * 나이트발드 사용중
	 */
	static public void init(MonsterInstance mi, object o, MonsterSkill ms, int action) {
		// 대상이 스턴일시 연속 스턴 안걸리도록 하기위해
		if (o.isLock()) {
			return;
		}
		// 몬스터가 마법을 써도 되는 상태인지 확인
		if (!SkillController.isMagic(mi, ms, true))
			return;
		
		mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), true);
		
		if (ms.getCastGfx() > 0)
			mi.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mi, ms.getCastGfx()), true);
		
		// 스턴확률 계산하여 성공처리되면 스턴 
		if (SkillController.isFigure(mi, o, ms.getSkill(), true, false))
			BuffController.append(o, ShockStun.clone(BuffController.getPool(ShockStun.class), ms.getSkill(), Util.random(1, ms.getSkill().getBuffDuration()), o, 9717));
	}
}
