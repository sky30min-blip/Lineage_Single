package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class GreaterHaste extends Magic {

	public GreaterHaste(Skill skill) {
		super(null, skill);	
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time, boolean restart) {
		if (bi == null)
			bi = new GreaterHaste(skill);
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
		// 초기화
		object o = null;
		
		// 타겟 찾기
		if (object_id == cha.getObjectId())
			o = cha;
		
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);

			if (SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)) {
				
				if (o.getSpeed() == 2) {
					// 슬로우 제거.
					BuffController.remove(o, Slow.class);
					return;
				}
				
				// 초록 물약과 중복 적용안됨 Haste
				BuffController.remove(o, HastePotionMagic.class);
				// 헤이스트와 중복 적용안됨 
				BuffController.remove(o, Haste.class);
				
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				
				BuffController.append(o, GreaterHaste.clone(BuffController.getPool(GreaterHaste.class), skill, skill.getBuffDuration(), false));
			}
		}
	}

}
