package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;

public class C_SmithFinal extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_SmithFinal(data, length);
		else
			((C_SmithFinal)bp).clone(data, length);
		return bp;
	}
	
	public C_SmithFinal(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete() || !isRead(4))
			return this;
		
		ItemInstance ii = pc.getInventory().value( readD() );
		if(ii != null){
			if(ii instanceof ItemWeaponInstance){
				if(pc.getInventory().isAden(100*ii.getDurability(), true)){
					ii.setDurability(0);
					if(Lineage.server_version >= 160)
						pc.toSender( S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), ii) );
					// %0%s 이제 새 것처럼 되었습니다.
					pc.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 464, ii.toString()) );
				}else{
					// \f1아데나가 충분치 않습니다.
					pc.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
				}
			}
		}
		return this;
	}

}
