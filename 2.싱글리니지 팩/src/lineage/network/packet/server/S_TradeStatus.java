package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_TradeStatus extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, boolean status){
		if(bp == null)
			bp = new S_TradeStatus(status);
		else
			((S_TradeStatus)bp).toClone(status);
		return bp;
	}
	
	public S_TradeStatus(boolean status){
		toClone(status);
	}
	
	public void toClone(boolean status){
		clear();

		writeC(Opcodes.S_OPCODE_TRADESTATUS);
		writeC(status ? 0 : 1);	// 1: 취소, 0: 완료
	}
}
