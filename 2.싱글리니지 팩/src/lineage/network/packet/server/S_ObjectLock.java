package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_ObjectLock extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int type){
		if(bp == null)
			bp = new S_ObjectLock(type);
		else
			((S_ObjectLock)bp).clone(type);
		return bp;
	}
	
	public S_ObjectLock(int type){
		clone(type);
	}
	
	public void clone(int type){
		clear();

		writeC(Opcodes.S_OPCODE_SetClientLock);
		writeC(type);						// 2:굳음, 3:굳음풀림, 4:굳음, 5:풀림 9:풀기 10:잠듬, 11:풀림 12:얼림, 13:풀림
		/*0 마비풀림[움직이지못함]
		2 마비걸림[움직이지못함] 커스패럴라이즈용
		4 마비걸림[움직이지못함] 어스바인드용 이런식으로 나눠서 보내서 중복되도록 가능
		5 마비풀림[움직이지못함]
		10 잠들어버림  포그
		11 잠풀림
		12 얼어버렸습니다
		13 얼어버린거 풀림
		22 쇼크스턴 마비걸림 [쇼크스턴아이콘포함] 스턴
		23 쇼크스턴 마비풀림
		24 발이묶임 [거미줄모양아이콘포함]  인탱글
		25 발이풀림*/
	}
}
