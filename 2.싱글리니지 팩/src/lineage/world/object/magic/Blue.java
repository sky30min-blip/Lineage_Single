package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Blue extends Magic {

	public Blue(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new Blue(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffUpdate(object o) {

	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setBuffBluePotion(true);
		}
		//
		toBuffUpdate(o);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			cha.setBuffBluePotion(false);
			// 메세지 표현 (몸에 깃들어 있던 마력이 흩어지는 것을 느낍니다.)
			if (Lineage.server_version > 163)
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 712));
			else
				ChattingController.toChatting(cha, "몸에 깃들어 있던 마력이 흩어지는 것을 느낍니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min);
	}

	static public void init(Character cha, int time) {
		BuffInterface b = BuffController.find(cha, SkillDatabase.find(204));

		if (b != null)
			BuffController.append(cha, Blue.clone(BuffController.getPool(Blue.class), SkillDatabase.find(204), b.getTime() + time));
		else
			BuffController.append(cha, Blue.clone(BuffController.getPool(Blue.class), SkillDatabase.find(204), time));

		// 메세지 표현 (머리 속이 맑아지며 마력이 충만해집니다.)
		if (Lineage.server_version > 163)
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 703));
		else
			ChattingController.toChatting(cha, "머리 속이 맑아지며 마력이 충만해집니다.", Lineage.CHATTING_MODE_MESSAGE);
	}
}
