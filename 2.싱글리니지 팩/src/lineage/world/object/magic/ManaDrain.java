package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class ManaDrain {

	static public void init(Character cha, Skill skill, int object_id) {
		// 타겟 찾기
		object o = cha.findInsideList(object_id);
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if (SkillController.isMagic(cha, skill, true))
				onBuff(cha, o, skill);
		}
	}

	/**
	 * 중복코드 방지용.
	 * 
	 * @param cha
	 * @param o
	 * @param skill
	 */
	static public void onBuff(Character cha, object o, Skill skill) {
		// 투망상태 해제
		Detection.onBuff(cha);
		// 처리
		if (SkillController.isFigure(cha, o, skill, true, false) && Util.isDistance(cha, o, 4)) {
			int steal_mp = CharacterController.toStatInt(cha, "magicBonus") * 4;
			steal_mp -= SkillController.getMr((Character) o, false) * 0.1;
			
			if (steal_mp < 1)
				steal_mp = 1;
			
			if (o.getNowMp() < (int) steal_mp)
				steal_mp = o.getNowMp();

			o.setNowMp(o.getNowMp() - steal_mp);
			cha.setNowMp(cha.getNowMp() + (int) Math.round(steal_mp / 2));

			DamageController.toDamage(cha, o, 0, Lineage.ATTACK_TYPE_MAGIC);

			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx() + 1), true);
			// 공격당한거 알리기.
			o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
			ChattingController.toChatting(cha, "마나 드레인: 대상의 MP를 소실시키고 일부 MP를 흡수", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		// \f1마법이 실패했습니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
	}

}
