package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.util.Util;
import lineage.world.object.object;

public class S_Door extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int x, int y, int h, boolean close){
		if(bp == null)
			bp = new S_Door(x, y, h, close);
		else
			((S_Door)bp).clone(x, y, h, close);
		return bp;
	}

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_Door(o);
		else
			((S_Door)bp).clone(o);
		return bp;
	}
	
	public S_Door(int x, int y, int h, boolean close){
		clone(x, y, h, close);
	}
	
	public S_Door(object o){
		clone(o);
	}
	
	public void clone(object o){
		clone(o.getX(), o.getY(), o.getHeading(), o.isDoorClose());
	}
	
	public void clone(int x, int y, int h, boolean close){
		clear();
		
		writeC(Opcodes.S_OPOCDE_ATTRIBUTE);
		switch(h){
			case 0:
				writeH(x);
				writeH(y);
				writeC(0);
				break;
			case 2:
				writeH(x);
				writeH(y);
				writeC(1);
				break;
			case 4:
				writeH(x+Util.getXY(h, true));
				writeH(y+Util.getXY(h, false));
				writeC(0);
				break;
			case 6:
				writeH(x+Util.getXY(h, true));
				writeH(y+Util.getXY(h, false));
				writeC(1);
				break;
		}
		writeC(close ? 65 : 0);
	}
}
