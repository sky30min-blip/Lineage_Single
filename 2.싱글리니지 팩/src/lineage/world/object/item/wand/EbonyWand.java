package lineage.world.object.item.wand;

import lineage.bean.database.Item;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.EnergyBolt;

public class EbonyWand extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new EbonyWand();
		return item;
	}

	@Override
	public ItemInstance clone(Item item) {
		quantity = Util.random(5, 15);

		return super.clone(item);
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (((PcInstance) cha).isFishing())
			return;
			
		object o = null;
		int obj_id = cbp.readD();
		int x = cbp.readH();
		int y = cbp.readH();

		// 방향 전환.
		cha.setHeading(Util.calcheading(cha, x, y));
		// 수량 하향
		setQuantity(quantity - 1);

		cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
		// 객체 찾기.
		if (obj_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList(obj_id);
		
		if (o == null)
			return;
		
		if (!SpriteFrameDatabase.findGfxMode(cha.getGfx(), Lineage.GFX_MODE_WAND))
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_WAND), true);
		
		// 처리.
		if (obj_id == cha.getObjectId()) {	
			cha.toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), cha, Lineage.GFX_MODE_WAND, getItem().getEffect(), x, y), cha instanceof PcInstance);
		} else {		
			EnergyBolt.toBuff(cha, o, null, Lineage.GFX_MODE_WAND, getItem().getEffect(), Util.random(getItem().getSmallDmg(), getItem().getBigDmg()));
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
