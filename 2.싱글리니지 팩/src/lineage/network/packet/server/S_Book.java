package lineage.network.packet.server;

import lineage.bean.lineage.Book;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Book extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Book b){
		if(bp == null)
			bp = new S_Book(b);
		else
			((S_Book)bp).clone(b);
		return bp;
	}
	
	public S_Book(Book b){
		clone(b);
	}
	
	public void clone(Book b) {
		clear();
		writeC(Opcodes.S_OPCODE_BOOKMARKS);
		writeS(b.getLocation());
		writeH(b.getMap());
		writeH(b.getX());
		writeH(b.getY());
	}
}
