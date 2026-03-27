package lineage.network.packet.server;

import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_TaxPut extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, Kingdom k){
		if(bp == null)
			bp = new S_TaxPut(o, k);
		else
			((S_TaxPut)bp).toClone(o, k);
		return bp;
	}
	
	public S_TaxPut(object o, Kingdom k){
		toClone(o, k);
	}
	
	public void toClone(object o, Kingdom k){
		clear();
		
		writeC(Opcodes.S_OPCODE_CASTLETAXIN);
		writeD(o.getObjectId());
		//writeD((int)ko.getTaxTotal());
	}

}
