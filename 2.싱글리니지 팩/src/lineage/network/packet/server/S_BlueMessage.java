package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;

public class S_BlueMessage extends ServerBasePacket {
	static synchronized public BasePacket clone(BasePacket bp, int number){
		if(bp == null)
			bp = new S_BlueMessage(number, null);
		else
			((S_BlueMessage)bp).toClone(number, null);
		return bp;
	}

	static synchronized public BasePacket clone(BasePacket bp, int number, String msg){
		if (!Lineage.is_blue_message)
			return bp;
		
		if(bp == null)
			bp = new S_BlueMessage(number, msg);
		else
			((S_Message)bp).toClone(number, msg);
		return bp;
	}
	
	public S_BlueMessage(int number, String msg){
		toClone(number, msg);
	}
	
	public void toClone(int number, String msg){	
		clear();
		writeC(Opcodes.S_OPCODE_BLUEMESSAGE);
		writeH(number);
		if(msg!=null){
			writeC(0x01);
			writeS(msg);
		}
	}

}
