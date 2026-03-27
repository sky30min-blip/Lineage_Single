package lineage.network.packet.server;

import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_ClanWar extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Kingdom k){
		if(bp == null)
			bp = new S_ClanWar(k);
		else
			((S_ClanWar)bp).clone(k);
		return bp;
	}

	static synchronized public BasePacket clone(BasePacket bp, int type, String a, String b){
		if(bp == null)
			bp = new S_ClanWar(type, a, b);
		else
			((S_ClanWar)bp).clone(type, a, b);
		return bp;
	}
	
	public S_ClanWar(Kingdom k){
		clone(k);
	}
	
	public S_ClanWar(int type, String a, String b){
		clone(type, a, b);
	}
	
	public void clone(Kingdom k){
		clear();
		
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(k.getWarStatus());	// 0. 시작 , 1 종료 , 2.진행중 3.주도권 4.차지
		writeC(k.getUid());			// 1. 켄트 2 . 오크요새 3. 윈다우드 4. 기란성 5. 하이네 6. 지저성 7. 아덴성 8. 디아드 요새
	}
	
	public void clone(int type, String a, String b){
		clear();
		
		writeC(Opcodes.S_OPCODE_CLANWAR);
		/*
			1: 전쟁 선포
			2: 전쟁 항복
			3: 전쟁 종료
			4: 전쟁 승리
			6: 동맹 맺음
			7: 동맹 깨어짐
			8: 교전중 - 리스하고 들어올때 보냄..
		*/
		writeC(type);
		writeS(a);	// 전자 혈맹이름
		writeS(b);	// 후자 혈맹이름
	}
	
}
