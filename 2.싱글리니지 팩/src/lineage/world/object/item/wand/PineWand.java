package lineage.world.object.item.wand;

import lineage.bean.database.Item;
import lineage.database.ItemPinewandDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_Message;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class PineWand extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new PineWand();
		return item;
	}

	@Override
	public ItemInstance clone(Item item){
		quantity = Util.random(5, 15);
		
		return super.clone(item);
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if (((PcInstance)cha).isFishing())
			return;
		if(quantity<=0){
			// \f1아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
			return;
		}
		
		setQuantity(quantity-1);
		cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));

		int x = cha.getX();
		int y = cha.getY();
		if(World.isThroughObject(x, y, cha.getMap(), cha.getHeading())){
			x += Util.getXY(cha.getHeading(), true);
			y += Util.getXY(cha.getHeading(), false);
		}

		MonsterInstance mon = ItemPinewandDatabase.newPineWandMonsterInstance();
		if(mon != null){
			mon.setHomeX(x);
			mon.setHomeY(y);
			mon.setHomeMap(cha.getMap());
			mon.setHeading(cha.getHeading());
			mon.toTeleport(x, y, cha.getMap(), false);
//			if(Lineage.monster_summon_item_drop)
//				mon.readDrop(cha.getMap());

			AiThread.append(mon);
		}
		
		// 수량이 0개일때 제거
		if (getQuantity() < 1) {
			cha.getInventory().count(this, getCount() - 1, true);
			
			if (getCount() > 0)
				setQuantiny(cha);
		}
	}
	
	public void setQuantiny(Character cha) {
		setQuantity(Util.random(5, 15));
		cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
	}
}
