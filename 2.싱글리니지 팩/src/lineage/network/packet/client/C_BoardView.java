package lineage.network.packet.client;

import lineage.database.BackgroundDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BoardController;
import lineage.world.controller.RankController;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RankBoardInstance;

public class C_BoardView extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BoardView(data, length);
		else
			((C_BoardView)bp).clone(data, length);
		return bp;
	}
	
	public C_BoardView(byte[] data, int length){
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
			BoardController.toView(pc, tradeBoard, readD());
		} else {
			object o = pc.findInsideList(objId);
			if(o!=null && o instanceof BoardInstance){
				if(o instanceof RankBoardInstance)
					RankController.toView(pc, readD());
				else
					BoardController.toView(pc, (BoardInstance)o, readD());
			}
		}
		

		BoardInstance noticeBoard = BackgroundDatabase.getNoticeBoard();
		if (noticeBoard != null && objId == noticeBoard.getObjectId()) {
			BoardController.toView(pc, noticeBoard, readD());
		}
		BoardInstance guideBoard = BackgroundDatabase.getGuideBoard();
		if (guideBoard != null && objId == guideBoard.getObjectId()) {
			BoardController.toView(pc, guideBoard, readD());
		}
		BoardInstance updateBoard = BackgroundDatabase.getUpdateBoard();
		if (updateBoard != null && objId == updateBoard.getObjectId()) {
			BoardController.toView(pc, updateBoard, readD());
		}
		
		BoardInstance atBoard = BackgroundDatabase.getatBoard();
		if (atBoard != null && objId == atBoard.getObjectId()) {
			BoardController.toView(pc, atBoard, readD());
		}
		BoardInstance rankBoard = BackgroundDatabase.getRankBoard();
		if (rankBoard != null && objId == rankBoard.getObjectId()) {
			RankController.toView(pc, readD());
		}
		

		return this;
	}

}
