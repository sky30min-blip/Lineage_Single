package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.SummonController;
import lineage.world.object.instance.PcInstance;

public class C_SummonTargetSelect extends ClientBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_SummonTargetSelect(data, length);
		else
			((C_SummonTargetSelect)bp).clone(data, length);
		return bp;
	}
	
	public C_SummonTargetSelect(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		int pet_id = readD();
		int type = readC();
		int target_id = readD();
		SummonController.toTargetSelect(pc, pet_id, target_id, type);

		return this;
	}
}
