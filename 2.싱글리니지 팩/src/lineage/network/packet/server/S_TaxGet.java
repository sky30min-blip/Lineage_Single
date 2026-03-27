package lineage.network.packet.server;

import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_TaxGet extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, Kingdom k){
		if(bp == null)
			bp = new S_TaxGet(o, k);
		else
			((S_TaxGet)bp).toClone(o, k);
		return bp;
	}
	
	public S_TaxGet(object o, Kingdom k){
		toClone(o, k);
	}
	
	public void toClone(object o, Kingdom k){
		clear();

		writeC(Opcodes.S_OPCODE_CASTLETAXOUT);
		writeD(o.getObjectId());
		writeD((int)k.getTaxTotal());
	}

}
