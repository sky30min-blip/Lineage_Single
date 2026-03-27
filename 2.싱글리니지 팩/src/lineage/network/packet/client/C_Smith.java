package lineage.network.packet.client;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_SmithList;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;

public class C_Smith extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_Smith(data, length);
		else
			((C_Smith)bp).clone(data, length);
		return bp;
	}
	
	public C_Smith(byte[] data, int length){
		clone(data, length);
		
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		final List<ItemInstance> list = new ArrayList<ItemInstance>();
		// 손상된 무기 추출.
		for(ItemInstance ii : pc.getInventory().getList()){
			if(ii instanceof ItemWeaponInstance && /*!ii.isEquipped() && */ii.getDurability() > 0)
				list.add(ii);
		}
		// 손상된 무기 표현.
		pc.toSender(S_SmithList.clone(BasePacketPooling.getPool(S_SmithList.class), list));
		
		return this;
	}

}
