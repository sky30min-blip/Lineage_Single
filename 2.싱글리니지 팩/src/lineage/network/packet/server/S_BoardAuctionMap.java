package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.instance.BoardInstance;

public class S_BoardAuctionMap extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, BoardInstance bi, int uid){
		if(bp == null)
			bp = new S_BoardAuctionMap(bi, uid);
		else
			((S_BoardAuctionMap)bp).toClone(bi, uid);
		return bp;
	}
	
	public S_BoardAuctionMap(BoardInstance bi, int uid){
		toClone(bi, uid);
	}
	
	public void toClone(BoardInstance bi, int uid){
		clear();
		
		writeC(Opcodes.S_OPCODE_Agit_Map);
		writeD(bi.getObjectId());			// npc오브젝트 아이디
		writeD(uid);						// 고유 아지트 번호
	}

}
