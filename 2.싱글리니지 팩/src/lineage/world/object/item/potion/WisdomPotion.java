package lineage.world.object.item.potion;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.Wisdom;

public class WisdomPotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new WisdomPotion();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!isClick(cha))
			return;

		if (cha.getClassType() == Lineage.LINEAGE_CLASS_WIZARD) {
			// 이팩트 표현
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, getItem().getEffect()), true);
			// 버프 처리
			Wisdom.init(cha, getItem().getDuration());
		} else {
			// 아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
		}
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount() - 1, true);
	}

}
