package lineage.network.packet.client;

import lineage.database.BackgroundDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;

public class C_BoardPaging extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BoardPaging(data, length);
		else
			((C_BoardPaging)bp).clone(data, length);
		return bp;
	}
	
	public C_BoardPaging(byte[] data, int length){
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(!isRead(8) || pc==null || pc.isWorldDelete())
			return this;
		
		long objId = readD();
		
		BoardInstance tradeBoard = BackgroundDatabase.getTradeBoard();
		if (tradeBoard != null && objId == tradeBoard.getObjectId()) {
			tradeBoard.toPage(pc, readD());
		} else {
			object o = pc.findInsideList(objId);
			if(o!=null && o instanceof BoardInstance)
				((BoardInstance)o).toPage(pc, readD());
		}
		
		BoardInstance noticeBoard = BackgroundDatabase.getNoticeBoard();
		if (noticeBoard != null && objId == noticeBoard.getObjectId()) {
			noticeBoard.toPage(pc, readD());
		}
		
		BoardInstance guideBoard = BackgroundDatabase.getGuideBoard();
		if (guideBoard != null && objId == guideBoard.getObjectId()) {
			guideBoard.toPage(pc, readD());
		}
		
		BoardInstance updateBoard = BackgroundDatabase.getUpdateBoard();
		if (updateBoard != null && objId == updateBoard.getObjectId()) {
			updateBoard.toPage(pc, readD());
		}
		
		BoardInstance atBoard = BackgroundDatabase.getatBoard();
		if (atBoard != null && objId == atBoard.getObjectId()) {
			atBoard.toPage(pc, readD());
		}
		BoardInstance rankBoard = BackgroundDatabase.getRankBoard();
		if (rankBoard != null && objId == rankBoard.getObjectId()) {
			rankBoard.toPage(pc, readD());
		}
		
		
		return this;
	}

}
