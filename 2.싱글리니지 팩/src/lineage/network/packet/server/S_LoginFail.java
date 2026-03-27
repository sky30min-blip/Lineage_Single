package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_LoginFail extends ServerBasePacket {

	static public final int REASON_OK = 0x02;						// 성공적으로 케릭 생성함.
	static public final int RETURN = 0x04;							// 뒤로 돌리기
	static public final int REASON_ALREADY_EXSISTS = 0x06;			// 케릭 이름이 이미 존재할때
	static public final int REASON_INVALID_NAME = 0x09;				// 건전한 이름이 아닐때
	static public final int REASON_WRONG_AMOUNT = 0x15;				// 스탯이 잘못됨
	static public final int REASON_WRONG_CLASS = 0x17;				// 클레스가 잘못됨.
	
	static public final int LOGIN_USER_OR_PASS_WRONG = 0x08;		// 패스워드가 잘못되었을때
	static public final int LOGIN_USER_ON = 0x16;					// 접속에 실패했습니다 3분후 재시도 해 주십시오.
	static public final int LOGIN_USER_OR_ID_AND_PASS_WRONG = 0x1a;	// 계정과 비번 통합계정등 정보가 다 잘못되었을때.
	
	static public final int REASON_ACCESS_END = 0x1c;				// 시간 만료 되었을때
	static public final int REASON_ACCESS_OK = 0x33;				// 계정 시간이 남은 로그인 성공시
	static public final int REASON_IP_FREETIME_OUT = 0x24;			// 해당아아피 계정시간이 다된경우.

	static synchronized public BasePacket clone(BasePacket bp, int type){
		if(bp == null)
			bp = new S_LoginFail(type);
		else
			((S_LoginFail)bp).clone(type);
		return bp;
	}
	
	public S_LoginFail(int type){
		clone(type);
	}
	
	public void clone(int type){
		clear();
		writeC(Opcodes.S_OPCODE_LOGINFAILS);
		writeC(type);
	}

}
