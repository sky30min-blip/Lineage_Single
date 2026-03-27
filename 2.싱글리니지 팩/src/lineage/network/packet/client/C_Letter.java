package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Letter;
import lineage.world.object.instance.PcInstance;

public class C_Letter extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_Letter(data, length);
		else
			((C_Letter)bp).clone(data, length);
		return bp;
	}
	
	public C_Letter(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(pc==null || pc.isWorldDelete())
			return this;
		
		int type = readC();
		switch(type){
			case 0x10:	// 편지 읽기
			case 0x12:	// 보관된 편지 읽기
				break;
			case 0x20:	// 편지 보내기
				break;
			case 0x30:	// 편지 삭제
			case 0x32:	// 보관된 편지 삭제
				break;
			case 0x40:	// 편지 저장
				break;
			case 0x01:
				pc.toSender( S_Letter.clone(BasePacketPooling.getPool(S_Letter.class), 0x02) );
				break;
			default:	// 편지 목록 요청
				pc.toSender( S_Letter.clone(BasePacketPooling.getPool(S_Letter.class), type) );
				break;
		}
		return this;
	}
}
