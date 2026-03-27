package lineage.network.packet.server;

import lineage.bean.lineage.Party;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.instance.PcInstance;

public class S_PartyInfo extends ServerBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, Party p, String action){
		if(bp == null)
			bp = new S_PartyInfo(p, action);
		else
			((S_PartyInfo)bp).clone(p, action);
		return bp;
	}
	
	public S_PartyInfo(Party p, String action){
		clone(p, action);
	}
	
	public void clone(Party p, String action){
		clear();
		
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(0);
		writeS(action);
		writeC(0);
		
		StringBuffer sb = new StringBuffer();
		for(PcInstance pc : p.getList()){
			sb.append(pc.getName());
			sb.append(" ");
		}
		if (p.isClanParty()) {
			writeH(0x02);						// 표현할 문자 갯수
			writeS(p.getMaster().getClanName());	// 파티장 이름
			writeS(sb.toString());				// 파티원 리스트
		} else {
			writeH(0x02);						// 표현할 문자 갯수
			writeS(p.getMaster().getName());	// 파티장 이름
			writeS(sb.toString());				// 파티원 리스트
		}
	}

}
