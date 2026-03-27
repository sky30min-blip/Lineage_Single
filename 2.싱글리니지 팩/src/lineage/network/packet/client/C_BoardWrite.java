package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.BoardController;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;

public class C_BoardWrite extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BoardWrite(data, length);
		else
			((C_BoardWrite)bp).clone(data, length);
		return bp;
	}
	
	public C_BoardWrite(byte[] data, int length){
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(!isRead(4) || pc==null || pc.isWorldDelete())
			return this;
		
		object o = pc.findInsideList(readD());
	
		if(o!=null && o instanceof BoardInstance) {
			if (((BoardInstance)o).getType().equals("server") || ((BoardInstance)o).getType().equals("update") || ((BoardInstance)o).getType().equals("cash")) {
				if (pc.getGm() == 0) {
					ChattingController.toChatting(pc, "관리자만 작성 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				} else {
					BoardController.toWrite(pc, (BoardInstance)o, readS(), readS());
				}
			} else {
				if (pc.getLevel() < Lineage.board_write_min_level) {
					ChattingController.toChatting(pc, String.format("게시판은 %d레벨 이상 작성 가능합니다.", Lineage.board_write_min_level), Lineage.CHATTING_MODE_MESSAGE);
				} else {
					BoardController.toWrite(pc, (BoardInstance)o, readS(), readS());
				}	
			}
		}
		return this;
	}	
}
