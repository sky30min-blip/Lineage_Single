package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;

public class Haste extends Magic {

	public Haste(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time, boolean restart) {
		if (bi == null)
			bi = new Haste(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setSpeed(1);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 0, o.getSpeed(), getTime()), true);
		// \f1갑자기 빠르게 움직입니다.
		o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 184));
	}

	@Override
	public void toBuffUpdate(object o) {
		o.setSpeed(1);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 0, o.getSpeed(), getTime()), true);
		// \f1다리에 새 힘이 솟습니다.
		o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 183));
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o.isWorldDelete())
			return;
		o.setSpeed(0);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 0, o.getSpeed(), 0), true);
		// \f1느려지는 것을 느낍니다.
		o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 185));
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == 1)
			o.speedCheck = System.currentTimeMillis() + 2000;
	}

	static public void init(Character cha, Skill skill, long object_id) {
		if(cha.getMap() == 5143) {
			ChattingController.toChatting(cha, String.format("[알림] 인형레이스중엔 사용이 불가능합니다"), Lineage.CHATTING_MODE_MESSAGE);
		}
		// 초기화
		object o = null;
		// 타겟 찾기
		if (object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList(object_id);
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if (SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false))
				onBuff(o, skill);
		}
	}
	
	/**
	 * 몬스터용
	 * 데스나이트가 사용중
	 * 2017-10-26
	 * by all-night
	 */
	static public void init(MonsterInstance mi, object o, MonsterSkill ms) {
		// 처리
		if (o != null) {
			mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if (SkillController.isMagic(mi, ms, true)){
				onBuff(o, ms.getSkill());
	         
			}
		}
	}

	static public void init(Character cha, int time, boolean restart) {
		if (cha.getSpeed() == 2) {
			// 슬로우 제거.
			BuffController.remove(cha, Slow.class);
			return;
		}

		// 초록 물약과 중복 적용안됨
		BuffController.remove(cha, HastePotionMagic.class);
		// 그레이터헤이스트와 중복 적용안됨
		BuffController.remove(cha, GreaterHaste.class);

		BuffController.append(cha, Haste.clone(BuffController.getPool(Haste.class), SkillDatabase.find(6, 2), time, restart));
	}

	static public void onBuff(object o, Skill skill) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);

		if (o.getSpeed() == 2) {
			// 슬로우 제거.
			BuffController.remove(o, Slow.class);
			return;
		}
		// 초록 물약과 중복 적용안됨
		BuffController.remove(o, HastePotionMagic.class);
		// 그레이터헤이스트와 중복 적용안됨
		BuffController.remove(o, GreaterHaste.class);

		// 무기중 광전사의 도끼를 착용하고 있을경우 처리를 하지 않는다.
		// 방패중 에바의 방패역시 처리하지 않는다.
		ItemInstance item1 = o.getInventory() != null ? o.getInventory().getSlot(Lineage.SLOT_WEAPON) : null;
		ItemInstance item2 = o.getInventory() != null ? o.getInventory().getSlot(Lineage.SLOT_SHIELD) : null;
		if ((item1 != null && item1.getItem().getNameIdNumber() == 418) || (item2 != null && item2.getItem().getNameIdNumber() == 419))
			return;
		
		// 헤이스트 적용.
		BuffController.append(o, Haste.clone(BuffController.getPool(Haste.class), skill, skill.getBuffDuration(), false));
	}

	static public void onBuff(object o, Skill skill, int time) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);

		if (o.getSpeed() == 2) {
			// 슬로우 제거.
			BuffController.remove(o, Slow.class);
			return;
		}
		// 초록 물약과 중복 적용안됨
		BuffController.remove(o, HastePotionMagic.class);
		// 그레이터헤이스트와 중복 적용안됨
		BuffController.remove(o, GreaterHaste.class);

		// 무기중 광전사의 도끼를 착용하고 있을경우 처리를 하지 않는다.
		// 방패중 에바의 방패역시 처리하지 않는다.
		ItemInstance item1 = o.getInventory() != null ? o.getInventory().getSlot(Lineage.SLOT_WEAPON) : null;
		ItemInstance item2 = o.getInventory() != null ? o.getInventory().getSlot(Lineage.SLOT_SHIELD) : null;
		if ((item1 != null && item1.getItem().getNameIdNumber() == 418) || (item2 != null && item2.getItem().getNameIdNumber() == 419))
			return;
		
		// 헤이스트 적용.
		BuffController.append(o, Haste.clone(BuffController.getPool(Haste.class), skill, time, false));
	}

}
