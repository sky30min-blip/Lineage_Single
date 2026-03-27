package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;

public class S_ServerVersion extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new S_ServerVersion();
		else
			((S_ServerVersion)bp).toClone();
		return bp;
	}
	
	public S_ServerVersion(){
		toClone();
	}
	
	public void toClone(){
		clear();
		writeC(Opcodes.S_OPCODE_SERVERVERSION);
		writeC(0x00); // must be
		// 일팩에서 0xc8 사용중.
		if(Lineage.nonpvp)
			writeC(0x32); // low version
		else
			writeC(0x02); 	// low version
		if(Lineage.server_version<=144){
			writeD(0x00009D7C); // serverver
			writeD(0x00009D11); // cache version
			writeD(0x0000791A); // auth ver
			writeD(0x00009D74); // npc ver
		}else if(Lineage.server_version<=240){
			writeD(0x000112a9); // serverver
			writeD(0x0000eb93); // cache version
			writeD(0x000a12a2); // auth ver
			writeD(0x000112b0); // npc ver
		}else{
			writeD(0x000189da); // serverver
			writeD(0x000189c5); // cache version
			writeD(0x77cef9f0); // auth ver
			writeD(0x000189d4); // npc ver
		}
		writeD(0x4600803f); // uptime
		writeC(0x00); 		// unk 1	[ 0x01:게임상에서 회원가입이 가능해짐. ]
		writeC(0x00); 		// unk 2
		writeC(0x00); 		// language korean
//		writeD();			// 일팩에서 time으로 사용됨.
	}
	
}
