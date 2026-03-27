package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.instance.PcInstance;

public class C_ObjectChattingOption extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectChattingOption(data, length);
		else
			((C_ObjectChattingOption)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectChattingOption(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(!isRead(2) || pc==null || pc.isWorldDelete())
			return this;
		
		int mode = readC();
		switch(mode){
			case 0x00:	// 전체 채팅
				pc.setChattingGlobal(readC()==1);
				break;
			case 0x02:	// 귓속 말
				pc.setChattingWhisper(readC()==1);
				break;
			case 0x06:	// 장사 채팅
				pc.setChattingTrade(readC()==1);
				break;
		}
		
		return this;
	}
}
