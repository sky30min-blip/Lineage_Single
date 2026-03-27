package lineage.network.packet.server;

import lineage.bean.lineage.Friend;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.World;

public class S_FriendList extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Friend f){
		if(bp == null)
			bp = new S_FriendList(f);
		else
			((S_FriendList)bp).clone(f);
		return bp;
	}
	
	public S_FriendList(Friend f){
		clone(f);
	}
	
	public void clone(Friend f){
		clear();
		
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(0);
		writeS("buddy");
		writeC(0);
		writeH(0x02);
		if(f!=null && f.sizeList()>0){
			// 전체 친구
			StringBuffer list_all = new StringBuffer();
			// 월드접속된 친구
			StringBuffer list_join = new StringBuffer();
			for(String name : f.getList()){
				list_all.append( String.format("%s ", name) );
				if(World.findPc(name) != null)
					list_join.append( String.format("%s ", name) );
			}
			writeS(list_all.toString());
			writeS(list_join.toString());
			writeC(0x27);
			writeC(0x32);
		}
		writeC(0);
	}
	
}
