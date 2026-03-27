package lineage.network.packet.server;

import lineage.bean.lineage.Clan;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_ClanInfo extends ServerBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, Clan c, String action){
		if(bp == null)
			bp = new S_ClanInfo(c, action);
		else
			((S_ClanInfo)bp).clone(c, action);
		return bp;
	}
	
	public S_ClanInfo(Clan c, String action){
		clone(c, action);
	}
	
	public void clone(Clan c, String action){
		clear();
		
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(0);
		writeS(action);
		writeC(0);
		
		if(action.equalsIgnoreCase("pledgeM")){
			writeH(0x03);							// 표현할 문자 갯수
			writeS(c.getName());					// 혈맹이름
			writeS(c.getMemberNameListConnect());	// 접속한 혈맹원
			writeS(c.getMemberNameList());			// 전체 혈맹원
		}else{
			writeH(0x02);							// 표현할 문자 갯수
			writeS(c.getName());					// 혈맹이름
			writeS(c.getMemberNameListConnect());	// 접속한 혈맹원
		}
	}

}
