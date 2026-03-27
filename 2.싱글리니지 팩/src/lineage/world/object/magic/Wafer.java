package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Wafer extends Magic {

	public Wafer(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time, boolean restart) {
		if (bi == null)
			bi = new Wafer(skill);
		bi.setSkill(skill);
		bi.setTime(time);

		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBrave(true);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, 1, getTime()), true);
	}

	@Override
	public void toBuffUpdate(object o) {
		o.setBrave(true);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, 1, getTime()), true);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o.isWorldDelete())
			return;
		o.setBrave(false);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, 0, 0), true);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == 1)
			o.speedCheck = System.currentTimeMillis() + 2000;
	}

	static public void init(Character cha, int time, boolean restart) {
		if (cha.getClassType() == Lineage.LINEAGE_CLASS_ELF) {
			// 버프 시간 중첩
			if (!restart)
				time = BuffController.addBuffTime(cha, SkillDatabase.find(200), time);
			
			BuffController.append(cha, Wafer.clone(BuffController.getPool(Wafer.class), SkillDatabase.find(200), time, restart));
		}
	}

}
