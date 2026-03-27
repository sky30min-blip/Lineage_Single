package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_TradeStart extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_TradeStart(o);
		else
			((S_TradeStart)bp).toClone(o);
		return bp;
	}
	
	public S_TradeStart(object o){
		toClone(o);
	}
	
	public void toClone(object o){
		clear();
		
		writeC(Opcodes.S_OPCODE_TRADE);
		writeS(o.getName());
	}
}
