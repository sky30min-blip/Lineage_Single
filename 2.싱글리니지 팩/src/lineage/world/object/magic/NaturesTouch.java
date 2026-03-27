package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class NaturesTouch extends Magic {

	public NaturesTouch(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new NaturesTouch(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffNaturesTouch(true);	
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o.isWorldDelete())
			return;
		o.setBuffNaturesTouch(false);
		ChattingController.toChatting(o, "\\fY네이쳐스 터치 종료", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY네이쳐스 터치: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
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
			
			if (Util.isDistance(cha, o, 15) && SkillController.isMagic(cha, skill, true) && o instanceof Character) {
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				BuffController.append(o, NaturesTouch.clone(BuffController.getPool(NaturesTouch.class), skill, skill.getBuffDuration()));
				ChattingController.toChatting(o, String.format("네이쳐스 터치: HP회복+%d 증가", o.getLevel() - 9 > 15 ? 15 : o.getLevel() - 9 < 1 ? 1 : o.getLevel() - 9), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

}
