package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_SkillBuyList;
import lineage.world.object.instance.PcInstance;

public class C_SkillBuy extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_SkillBuy(data, length);
		else
			((C_SkillBuy)bp).clone(data, length);
		return bp;
	}
	
	public C_SkillBuy(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		if(pc.getGm()>0 || !pc.isTransparent())
			pc.toSender(S_SkillBuyList.clone(BasePacketPooling.getPool(S_SkillBuyList.class), pc));
		
		return this;
	}

}
