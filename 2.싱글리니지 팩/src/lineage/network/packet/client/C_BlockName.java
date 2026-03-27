package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

public class C_BlockName extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BlockName(data, length);
		else
			((C_BlockName)bp).clone(data, length);
		return bp;
	}
	
	public C_BlockName(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(pc==null || pc.isWorldDelete())
			return this;
		
		String name = readS();
		if (name != null && name.length() > 0){
			PcInstance use = World.findPc(name);
					
			if( pc.getListBlockName().contains(name) ){
				pc.getListBlockName().remove(name);
				// %0의 차단을 풀었습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 116, name));
				if(use != null)
					// %0%s 당신의 차단을 해제했습니다.
					use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 118, pc.getName()));
			}else{
				pc.getListBlockName().add(name);
				// %0%o 차단했습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 115, name));
				if(use != null)
					// %0%s 당신을 차단했습니다.
					use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 117, pc.getName()));
			}
			return this;
		} else {
			// 안내
			if(pc.getListBlockName().size() == 0) {
				// 차단된 사용자가 없습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 114));
			} else {
				for (String use : pc.getListBlockName()) {
					// 차단된 사용자: %0
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 113, use));
				}
			}

			return this;
		}
	}

}
