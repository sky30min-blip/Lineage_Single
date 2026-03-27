package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.item.Letter;

public class S_LetterRead extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Letter l){
		if(bp == null)
			bp = new S_LetterRead(l);
		else
			((S_LetterRead)bp).toClone(l);
		return bp;
	}
	
	public S_LetterRead(Letter l){
		toClone(l);
	}
	
	public void toClone(Letter l){
		clear();
		
		writeC(Opcodes.S_OPCODE_LETTERREAD);
		writeD(l.getObjectId());
		writeH(l.getItem().getInvGfx());	// gfx
		writeH(949);						// 종류같음.. 펀지지는 949
		writeS(l.getFrom());				// 보낸이
		writeS(l.getTo());					// 받는이
		writeSS(l.getSubject());			// 제목
		writeSS(l.getMemo());				// 내용
		writeC(0x02);						//
	}

}
