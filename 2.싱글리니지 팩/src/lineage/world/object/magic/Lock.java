package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectLock;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Lock extends Magic {

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time, object o) {
		if (bi == null)
			bi = new Lock(skill, o);
		bi.setSkill(skill);
		bi.setTime(time);

		if (!o.isLockLow()) {
			o.setLockLow(true);
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x02));
		}
		
		return bi;
	}

	public Lock(Skill skill, object o) {
		super(null, skill);
	}

	@Override
	public void toBuffStart(object o) {

	}

	@Override
	public void toBuff(object o) {
		// 굳게 만들기.
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x02));
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
		o.setLockLow(false);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x00));
	}

	/**
	 * 
	 * @param cha
	 * @param skill
	 * @param object_id
	 * @param x
	 * @param y
	 */
	static public void init(Character cha) {
		Skill skill = SkillDatabase.find(700);
		BuffController.append(cha, Lock.clone(BuffController.getPool(Lock.class), skill, skill.getBuffDuration(), cha));
	}
}
