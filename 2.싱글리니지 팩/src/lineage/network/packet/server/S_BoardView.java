package lineage.network.packet.server;

import lineage.bean.lineage.Board;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_BoardView extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Board b){
		if(bp == null)
			bp = new S_BoardView(b);
		else
			((S_BoardView)bp).toClone(b);
		return bp;
	}
	
	public S_BoardView(Board b){
		toClone(b);
	}
	
	public void toClone(Board b){
		clear();

		writeC(Opcodes.S_OPCODE_BOARDREAD);
		writeD(b.getUid());			// 순번
		writeS(b.getName());		// 작성자
		writeS(b.getSubject());		// 제목
		writeS(b.toStringDays());	// 날짜
		writeS(b.getMemo());		// 내용
	}

}
