package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_ObjectPoisonLock;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class FrameSpeedOverStun extends Magic {

	public FrameSpeedOverStun(Skill skill) {
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new FrameSpeedOverStun(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
	
	@Override
	public void toBuffStart(object o) {
		o.setLockHigh(true);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 4));
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
		if (o.isWorldDelete())
			return;
		o.setLockHigh(false);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 5));
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
		
		if (o.isBuffCurseParalyze())
			BuffController.remove(o, CurseParalyze.class);
	}
	
	static public void init(Character cha, boolean isSpeedHack) {
		BuffController.append(cha, FrameSpeedOverStun.clone(BuffController.getPool(FrameSpeedOverStun.class), SkillDatabase.find(310), isSpeedHack ? Lineage.speed_hack_block_time : Lineage.gost_hack_block_time));
	}
	
	// 강제 종료시 다시 접속했을 경우를 위해 체크
	static public void init(Character cha, int time) {
		BuffController.append(cha, FrameSpeedOverStun.clone(BuffController.getPool(FrameSpeedOverStun.class), SkillDatabase.find(310), time));
	}
}
