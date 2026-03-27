package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class SolidCarriage extends Magic {

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new SolidCarriage(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	public SolidCarriage(Skill skill) {
		super(null, skill);
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicEr(cha.getDynamicEr() + 15);
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o.isWorldDelete())
			return;
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setDynamicEr(cha.getDynamicEr() - 15);
			ChattingController.toChatting(cha, "\\fY솔리드 캐리지 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY솔리드 캐리지: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 
	 * @param cha
	 * @param skill
	 * @param object_id
	 * @param x
	 * @param y
	 */
	static public void init(Character cha, Skill skill) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if ((cha.getInventory().getSlot(Lineage.SLOT_SHIELD) != null || cha.getInventory().getSlot(Lineage.SLOT_GUARDER) != null) && SkillController.isMagic(cha, skill, true)) {
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			BuffController.append(cha, SolidCarriage.clone(BuffController.getPool(SolidCarriage.class), skill, skill.getBuffDuration()));
			ChattingController.toChatting(cha, "솔리드 캐리지: ER(원거리 회피)+15, 방패 장착 해제시 종료", Lineage.CHATTING_MODE_MESSAGE);
		} else {
			if (cha.getInventory().getSlot(Lineage.SLOT_SHIELD) == null && cha.getInventory().getSlot(Lineage.SLOT_GUARDER) == null)
				ChattingController.toChatting(cha, "\\fY방패 또는 가더를 착용해야 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
		}

	}

}
