package lineage.world.object.item.all_night;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ItemCheckWand extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ItemCheckWand();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (Lineage.item_check_count < 1) {
			ChattingController.toChatting(cha, "현재 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else {
			PcInstance pc = null;
			int obj_id = cbp.readD();

			object o = cha.findInsideList(obj_id);

			if (o == null && cha.getGm() > 0) {
				pc = (PcInstance) cha;
			}
			
			if (pc != null || (o != null && o instanceof PcInstance)) {
				if (pc == null)
					pc = (PcInstance) o;
				
				if (pc != null) {
					if (pc.getGm() > cha.getGm()) {
						return;
					}
					
					cha.setItemCheckPc(pc);
					String msg = String.format("'%s'님의 장비 확인에 %s(%,d)가 소모됩니다.", pc.getName(), Lineage.item_check_name, Lineage.item_check_count);
					cha.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 782, msg));
				}
			}
		}
	}
}
