package kuberaitem;

import lineage.bean.database.Poly;
import lineage.bean.lineage.Inventory;
import lineage.database.ItemDatabase;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.item.potion.BraveryPotion;
import lineage.world.object.magic.AbsoluteBarrier;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.ShapeChange;

public class 기마투구 extends ItemArmorInstance {

	private int useCount = 100;

	public int getUseCount() {
		return useCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public void addUseCount() {
		this.useCount++;
	}

	public void initUseCoun() {
		this.useCount = 100;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (useCount <= 0 && equipped) { // 벗으려는 시도할때
			BuffController.remove(cha, ShapeChange.class);
			super.toClick(cha, cbp);
			return;
		}
		if (useCount <= 0) {
			ChattingController.toChatting(cha, "착용할 수 없습니다", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (cha.isBuffAbsoluteBarrier())
			BuffController.remove(cha, AbsoluteBarrier.class);

		if (isLvCheck(cha)) {
			if (isClassCheck(cha)) {
				Inventory inv = cha.getInventory();
				if (inv != null) {
					if (PolyDatabase.toEquipped(cha, this) || equipped) {
						if (equipped) {
							if (bless == 2) {
								// \f1그렇게 할 수 없습니다. 저주 받은 것 같습니다.
								cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 150));
								return;
							}
							setEquipped(false);
						} else {
							if (item.getType2().equalsIgnoreCase("shield")) {
								ItemInstance weapon = inv.getSlot(Lineage.SLOT_WEAPON);
								if (weapon != null) {
									// \f1두손 무기를 무장하고 방패를 착용할 수 없습니다.
									cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 129));
									return;
								}
							}
							setEquipped(true);
						}

						toSetoption(cha, true);
						toEquipped(cha, inv);

						toOption(cha, true);
						toBuffCheck(cha);
					} else {
						ChattingController.toChatting(cha, "착용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
				}
			} else {
				// \f1당신의 클래스는 이 아이템을 사용할 수 없습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 264));
			}
		}
		if (equipped) {
			useCount--;
			Poly poly = PolyDatabase.getPolyName(cha.getClassSex() == 0 ? "horse prince" : "horse princess");
			if (poly != null) {
				BuffController.remove(cha, BraveryPotion.class);
				//BuffController.remove(cha, Bravery.class);
				ShapeChange.onBuff(cha, cha, poly, -1, false, true);
			}
		} else {
			BuffController.remove(cha, ShapeChange.class);
		}
	}
}
