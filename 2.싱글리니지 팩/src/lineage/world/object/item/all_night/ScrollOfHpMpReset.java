package lineage.world.object.item.all_night;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.share.Lineage;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ScrollOfHpMpReset extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollOfHpMpReset();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null) {
			if (cha.getNowHp() < cha.getTotalHp() || cha.getNowMp() < cha.getTotalMp()) {
				ChattingController.toChatting(cha, "HP 또는 MP가 최대치가 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			// HP/MP 초기화
			int hp = 0;
			int mp = 0;
			
			for (int i = 2; i <= cha.getLevel(); i++) {
				if (i <= 51) {
					hp += CharacterController.toStatusBaseUP(cha, true);
					mp += CharacterController.toStatusBaseUP(cha, false);
				} else {
					hp += CharacterController.toStatusUP(cha, true);
					mp += CharacterController.toStatusUP(cha, false);
				}
			}

			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				hp += Lineage.royal_hp;
				mp += Lineage.royal_mp;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				hp += Lineage.knight_hp;
				mp += Lineage.knight_mp;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				hp += Lineage.elf_hp;
				mp += Lineage.elf_mp;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				hp += Lineage.wizard_hp;
				mp += Lineage.wizard_mp;
				break;
			}
			
			cha.setMaxHp(hp);
			cha.setNowHp(hp);
			cha.setMaxMp(mp);
			cha.setNowMp(mp);
			
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			
			// 알림
			ChattingController.toChatting(cha, "HP/MP가 재조정 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			// 아이템 수량 갱신
			cha.getInventory().count(this, getCount() - 1, true);
		}
	}
}
