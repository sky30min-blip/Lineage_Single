package lineage.network.packet.server;

import java.util.List;

import lineage.bean.lineage.Board;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.instance.BoardInstance;

public class S_BoardList extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, BoardInstance bi, List<Board> list){
		if(bp == null)
			bp = new S_BoardList(bi, list);
		else
			((S_BoardList)bp).toClone(bi, list);
		return bp;
	}
	
	public S_BoardList(BoardInstance bi, List<Board> list){
		toClone(bi, list);
	}
	
	public void toClone(BoardInstance bi, List<Board> list){
		clear();

		writeC(Opcodes.S_OPCODE_BOARDLIST);
		writeD(bi.getObjectId());
		writeC(0xff);
		writeC(0xff);
		writeC(0xff);
		writeC(0x7f);
		writeH(list.size());							// 해당 페이지에 게시물 갯수.
		writeH(Lineage.board_write_price);		// 게시물 작성 가격
		for(Board b : list){
			writeD(b.getUid());						// 순번
			writeS(b.getName());				// 작성자
			writeS(b.toStringDays());				// 작성일
			writeS(b.getSubject());					// 제목
		}
	}
}
