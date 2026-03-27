package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Log;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class C_Dustbin extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_Dustbin(data, length);
		else
			((C_Dustbin)bp).clone(data, length);
		return bp;
	}
	
	public C_Dustbin(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete() || !isRead(4) || pc.getInventory()==null)
			return this;
		
		ItemInstance item = pc.getInventory().value( readD() );
		if(pc.getInventory().isRemove(item, item.getCount(), true, true, false)) {
			String item_name = item.toStringDB();
			long item_count = item.getCount();
			long item_objid = item.getObjectId();
			//
			pc.getInventory().count(item, 0, true);
			//
			Log.appendItem(pc, "type|휴지통", String.format("item_name|%s", item_name), String.format("name_objid|%d", item_objid), String.format("item_count|%d", item_count));
		}
		
		return this;
	}

}
