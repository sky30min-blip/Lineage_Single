package lineage.world.object.item.all_night;

import system.MonDrop_System;
import lineage.bean.database.Monster;
import lineage.database.MonsterDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.instance.SummonInstance;

public class MonsterDropCheckWand extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new MonsterDropCheckWand();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		long objid = cbp.readD();
		object o = cha.findInsideList(objid);
		if (o instanceof MonsterInstance) {
		} else {
			ChattingController.toChatting(cha, "몬스터에게만 사용가능 합니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
		if (o != null && o instanceof MonsterInstance && !(o instanceof SummonInstance) && !(o instanceof PetInstance)) {
			String str = o.getMonster().getName();

			if (o.getMonster().getFaust() != null && o.getMonster().getFaust().length() > 0) {
				str = o.getMonster().getFaust();

			}
			Monster m = MonsterDatabase.find(str);
			if (m != null) {
				if (m.getDropList().size() > 0) {
					cha.toSender(MonDrop_System.clone(BasePacketPooling.getPool(MonDrop_System.class), m));
				} else {
					ChattingController.toChatting(cha, "해당 몬스터는 드랍목록이 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		}
	}
}
