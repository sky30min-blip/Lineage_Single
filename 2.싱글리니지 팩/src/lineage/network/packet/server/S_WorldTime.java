package lineage.network.packet.server;

import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_WorldTime extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new S_WorldTime();
		else
			((S_WorldTime)bp).toClone();
		return bp;
	}
	
	public S_WorldTime(){
		toClone();
	}
	
	public void toClone(){
		clear();
		
		writeC(Opcodes.S_OPCODE_GAMETIME);
		writeD(ServerDatabase.LineageWorldTime);
	}
}
