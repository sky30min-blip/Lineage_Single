package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.instance.SummonInstance;

public class S_SummonTargetSelect extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, SummonInstance si){
		if(bp == null)
			bp = new S_SummonTargetSelect(si);
		else
			((S_SummonTargetSelect)bp).clone(si);
		return bp;
	}
	
	public S_SummonTargetSelect(SummonInstance si){
		clone(si);
	}
	
	public void clone(SummonInstance si){
		clear();
		writeC(Opcodes.S_OPCODE_PromptTargetSelect);
		writeD(si.getObjectId());
		writeC(0x00);
	}

}
