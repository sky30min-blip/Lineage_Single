package lineage.network.packet.server;

import lineage.bean.lineage.StatDice;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_StatDice extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, StatDice sd){
		if(bp == null)
			bp = new S_StatDice(sd);
		else
			((S_StatDice)bp).clone(sd);
		return bp;
	}
	
	public S_StatDice(StatDice sd){
		clone(sd);
	}
	
	public void clone(StatDice sd){
		clear();
		writeC(Opcodes.S_OPCODE_STATDICE);
		writeC(sd.getStr());
		writeC(sd.getDex());
		writeC(sd.getCon());
		writeC(sd.getWis());
		writeC(sd.getCha());
		writeC(sd.getInt());
	}
	
}
