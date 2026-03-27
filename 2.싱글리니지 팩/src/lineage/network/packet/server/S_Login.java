package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Login extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int action){
		if(bp == null)
			bp = new S_Login(action);
		else
			((S_Login)bp).clone(action);
		return bp;
	}
	
	public S_Login(int action){
		clone(action);
	}
	
	public void clone(int action){
		clear();
		writeC(Opcodes.S_OPCODE_LOGINFAILS);
		writeC(action);
	}

	static synchronized public BasePacket clone(BasePacket bp, int action, int time){
		if(bp == null)
			bp = new S_Login(action, time);
		else
			((S_Login)bp).clone(action, time);
		return bp;
	}
	
	public S_Login(int action, int time){
		clone(action, time);
	}
	
	public void clone(int action, int time){
		clear();
		writeC(Opcodes.S_OPCODE_LOGINFAILS);
		writeC(action);
		writeD(time);	// 초단위로 값 넣어주면됨.
		writeH(32);		// ?
		writeH(22);		// ?
		writeD(0);		// 글세 모름
		writeC(0);		// 글세 모름
	}
	
}
