package lineage.world.object.item.all_night;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_LetterNotice;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class InventoryCheck extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item) {
		if(item == null)
			item = new InventoryCheck();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.isWorldDelete() || cha.isDead() || cha == null || cha.isLock() || cha.getMap() == Lineage.teamBattleMap || cha.getMap() == Lineage.BattleRoyalMap)
			return;
		//
		cha.getInventory().characterInventory = this;
		//
		ChattingController.toChatting(cha, "\\fR인벤토리를 확인할 캐릭명을 입력하십시오.", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	@Override
	public void toClickFinal(Character cha, Object... opt) {
		if (cha instanceof PcInstance) {
			String name = (String) opt[0];
			name = name.replaceAll(" ", "").trim();
			PcInstance pc = World.findPc(name);

			if (pc != null) {
				if (pc.getInventory() != null) {
					String[] arrayOfString = new String[3];
					arrayOfString[0] = "";
					arrayOfString[1] = String.format("[%s] 인벤정보", name);
					StringBuilder Str = new StringBuilder();

					if (pc.getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
						Str.append(String.format("무기: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_WEAPON))));

					if (pc.getInventory().getSlot(Lineage.SLOT_HELM) != null)
						Str.append(String.format("투구: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_HELM))));

					if (pc.getInventory().getSlot(Lineage.SLOT_SHIRT) != null)
						Str.append(String.format("티: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_SHIRT))));

					if (pc.getInventory().getSlot(Lineage.SLOT_ARMOR) != null)
						Str.append(String.format("갑옷: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_ARMOR))));

					if (pc.getInventory().getSlot(Lineage.SLOT_CLOAK) != null)
						Str.append(String.format("망토: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_CLOAK))));

					if (pc.getInventory().getSlot(Lineage.SLOT_GLOVE) != null)
						Str.append(String.format("장갑: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_GLOVE))));

					if (pc.getInventory().getSlot(Lineage.SLOT_BOOTS) != null)
						Str.append(String.format("부츠: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_BOOTS))));

					if (pc.getInventory().getSlot(Lineage.SLOT_SHIELD) != null)
						Str.append(String.format("방패: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_SHIELD))));

					if (pc.getInventory().getSlot(Lineage.SLOT_GUARDER) != null)
						Str.append(String.format("가더: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_GUARDER))));

					if (pc.getInventory().getSlot(Lineage.SLOT_NECKLACE) != null)
						Str.append(String.format("목걸이: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_NECKLACE))));
					
					if (pc.getInventory().getSlot(Lineage.SLOT_BELT) != null)
						Str.append(String.format("벨트: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_BELT))));

					if (pc.getInventory().getSlot(Lineage.SLOT_RING_RIGHT) != null)
						Str.append(String.format("반지: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_RING_RIGHT))));

					if (pc.getInventory().getSlot(Lineage.SLOT_RING_LEFT) != null)
						Str.append(String.format("반지: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_RING_LEFT))));

					if (pc.getInventory().getSlot(Lineage.SLOT_EARRING) != null)
						Str.append(String.format("귀걸이: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_EARRING))));

					if (pc.getInventory().getSlot(Lineage.SLOT_ARROW) != null)
						Str.append(String.format("화살: %s\n", Util.getItemNameToString(pc.getInventory().getSlot(Lineage.SLOT_ARROW))));

					arrayOfString[2] = Str.toString();
					cha.toSender(S_LetterNotice.clone(BasePacketPooling.getPool(S_LetterNotice.class), arrayOfString));

					cha.getInventory().count(this, getCount()-1, true);
				}
			} else {
				ChattingController.toChatting(cha, String.format("\\fR'%s' 캐릭터는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
