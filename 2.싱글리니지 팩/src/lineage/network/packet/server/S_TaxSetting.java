package lineage.network.packet.server;

import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.object;

public class S_TaxSetting extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, Kingdom k){
		if(bp == null)
			bp = new S_TaxSetting(o, k);
		else
			((S_TaxSetting)bp).toClone(o, k);
		return bp;
	}
	
	public S_TaxSetting(object o, Kingdom k){
		toClone(o, k);
	}
	
	public void toClone(object o, Kingdom k){
		clear();
		
		writeC(Opcodes.S_OPCODE_CASTLETAXRATIO);
		writeD(o.getObjectId());
		writeC(Lineage.min_tax);	// 최소
		writeC(Lineage.max_tax);	// 최대
		// 현재세팅된 세율값
		writeC(k.getTaxRate());
	}

}
