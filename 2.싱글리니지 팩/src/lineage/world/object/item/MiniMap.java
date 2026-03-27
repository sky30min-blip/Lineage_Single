package lineage.world.object.item;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_MiniMap;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class MiniMap extends ItemInstance {

	private int id;
	
	static synchronized public ItemInstance clone(ItemInstance item, int id){
		if(item == null)
			item = new MiniMap(id);
		((MiniMap)item).setId(id);
		return item;
	}
	
	public MiniMap(int id){
		this.id = id;
	}
	
	public void setId(int id){
		this.id = id;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		cha.toSender(S_MiniMap.clone(BasePacketPooling.getPool(S_MiniMap.class), this, id));
	}
	
}
