package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Clan;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ImmuneToHarm extends Magic {

	public ImmuneToHarm(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new ImmuneToHarm(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffImmuneToHarm(true);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		o.setBuffImmuneToHarm(false);
		ChattingController.toChatting(o, "\\fY이뮨 투 함 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY이뮨 투 함: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, Skill skill, int object_id) {
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
			
			if ((SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, SkillController.isClan(cha, o)) || cha.getGm() > 0)) {
				if (!Util.isAreaAttack(cha, o) && !Util.isAreaAttack(o, cha))
					return;
				
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				
				BuffController.append(o, ImmuneToHarm.clone(BuffController.getPool(ImmuneToHarm.class), skill, skill.getBuffDuration()));
				ChattingController.toChatting(o, "이뮨 투 함: 대미지의 일정량 감소", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	static public void init(Character cha, int time) {
		BuffController.append(cha, ImmuneToHarm.clone(BuffController.getPool(ImmuneToHarm.class), SkillDatabase.find(9, 3), time));
	}

	static public void onBuff(object o, Skill skill) {
		onBuff(o, skill, skill.getBuffDuration());
	}

	static public void onBuff(object o, Skill skill, int time) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		BuffController.append(o, ImmuneToHarm.clone(BuffController.getPool(ImmuneToHarm.class), skill, time));
	}
}
