package lineage.world.object.item.all_night;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.AdvanceSpirit;

public class StatClear extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new StatClear();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		
		if( cha.getLevel() < 51){
			ChattingController.toChatting(cha, String.format("\\fY스텟초기화는 51렙부터 사용 가능합니다.", Lineage.exp_support_max_level ), Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
				

		
		cha.setTempItem(this);
		cha.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 770));
	}

	public void toAsk(PcInstance pc, boolean yes) {
		ItemInstance item = pc.getTempItem();

		if (item != null & pc != null && yes && !pc.isWorldDelete() && !pc.isDead() && !pc.isLock()) {
			BuffController.remove(pc, AdvanceSpirit.class);
			CharacterController.toResetStat(pc, pc.getClassType());
			pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));

			pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, item.getItem().getEffect()), true);
			// 알림
			ChattingController.toChatting(pc, "능력치가 초기화 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			// 아이템 수량 갱신
			pc.getInventory().count(item, item.getCount() - 1, true);
			pc.setTempItem(null);
		}
	}
}
