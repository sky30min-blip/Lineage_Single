package lineage.network.packet.server;

import lineage.bean.lineage.Clan;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_ClanMark extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Clan c){
		if(bp == null)
			bp = new S_ClanMark(c);
		else
			((S_ClanMark)bp).clone(c);
		return bp;
	}
	
	public S_ClanMark(Clan c){
		clone(c);
	}
	
	public void clone(Clan c){
		clear();
		writeC(Opcodes.S_OPCODE_DownLoadMark);
		writeD(c.getUid());
		writeB(c.getIcon());
	}
}
