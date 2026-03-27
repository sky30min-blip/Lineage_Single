package lineage.world.object.item.yadolan;

import lineage.database.BackgroundDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class expRecovery extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new expRecovery();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null){
			if(((PcInstance) cha).getLostExp() > 0){
				
				cha.setExp(cha.getExp() + (((PcInstance) cha).getLostExp() * 100));
				((PcInstance) cha).setLostExp(0);
				// 아이템 수량 갱신
				cha.getInventory().count(this, getCount() - 1, true);
				cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
				ChattingController.toChatting(cha, String.format("잃은 경험치의 %d%가 회복 되었습니다.", Lineage.player_lost_exp_rate * 100), Lineage.CHATTING_MODE_MESSAGE);
	
				
			}else{
				ChattingController.toChatting(cha, "회복할 경험치가 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}

	}
}
