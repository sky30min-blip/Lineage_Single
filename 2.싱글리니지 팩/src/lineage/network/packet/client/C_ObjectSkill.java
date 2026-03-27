package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.SkillController;
import lineage.world.object.instance.PcInstance;

public class C_ObjectSkill extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectSkill(data, length);
		else
			((C_ObjectSkill)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectSkill(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isDead() || pc.isWorldDelete())
			return this;
		
		if(pc.getGm()>0 || !pc.isTransparent())
			SkillController.toSkill(pc, this);
		
		return this;
	}
}
