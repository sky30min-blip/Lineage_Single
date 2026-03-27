package lineage.world.object.item.all_night;

import lineage.bean.lineage.Clan;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectTitle;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ScrollOfNewClanJoin extends ItemInstance {
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollOfNewClanJoin();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PcInstance && !cha.isWorldDelete() && !cha.isLock() && !cha.isDead()) {
			if (cha.getClanId() > 0) {
				//89 \f1이미 혈맹에 가입했습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 89));
			} else {
				if (cha.getLevel() < Lineage.new_clan_max_level) {
					Clan c = ClanController.find(Lineage.new_clan_name);
					
					if (c != null) {
						cha.setClanId(c.getUid());
						cha.setClanName(c.getName());
						cha.setTitle("신규");
						cha.setClanGrade(0);
						// 패킷 처리
						cha.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), cha), true);
						//94 \f1%0%o 혈맹의 일원으로 받아들였습니다.
						c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 94, cha.getName()));
						//95 \f1%0 혈맹에 가입하였습니다.
						cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 95, c.getName()));
						// 혈맹 관리목록 갱신
						c.appendMemberList(cha.getName());
						if (!cha.isWorldDelete())
							c.appendList((PcInstance) cha);
						
						cha.getInventory().count(this, getCount() - 1, true);
					}
				} else
					ChattingController.toChatting(cha, String.format("신규 혈맹은 %d레벨 이하 가입 가능합니다.", Lineage.new_clan_max_level - 1), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
