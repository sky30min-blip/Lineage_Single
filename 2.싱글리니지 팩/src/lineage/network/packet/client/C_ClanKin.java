package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.object.instance.PcInstance;

public class C_ClanKin extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ClanKin(data, length);
		else
			((C_ClanKin)bp).clone(data, length);
		return bp;
	}
	
	public C_ClanKin(byte[] data, int length){
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(pc==null || pc.isWorldDelete())
			return this;
		
//		ClanController.toKin(pc, readS());
		ChattingController.toChatting(pc, ".추방 명령어를 이용해주세요.", Lineage.CHATTING_MODE_MESSAGE);
		return this;
	}
}
