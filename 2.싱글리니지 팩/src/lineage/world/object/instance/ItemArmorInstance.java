package lineage.world.object.instance;

import java.sql.Connection;

import lineage.bean.lineage.Inventory;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;

public class ItemArmorInstance extends ItemIllusionInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ItemArmorInstance();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PcInstance) {
			PcInstance pc = (PcInstance) cha;
			if (pc.isSound()) {
				if (!equipped)
					if (getItem().getType2().equalsIgnoreCase("helm")) {
						// 투구
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2807));
					} else if (getItem().getType2().equalsIgnoreCase("t")) {
						// 티셔츠
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2800));
					} else if (getItem().getType2().equalsIgnoreCase("armor")) {
						// 갑옷
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2802));
					} else if (getItem().getType2().equalsIgnoreCase("cloak")) {
						// 망토
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2814));
					} else if (getItem().getType2().equalsIgnoreCase("shield")) {
						// 방패
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2810));
					} else if (getItem().getType2().equalsIgnoreCase("glove")) {
						// 장갑
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2805));
					} else if (getItem().getType2().equalsIgnoreCase("boot")) {
						// 부츠
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2820));
					} else if (getItem().getType2().equalsIgnoreCase("belt")) {
						// 벨트
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2815));
					} else if (getItem().getType2().equalsIgnoreCase("necklace")) {
						// 목걸이
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2821));
					} else if (getItem().getType2().equalsIgnoreCase("ring")) {
						// 반지
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2822));

					}
			}
			if (isLvCheck(cha)) {
				if (isClassCheck(cha)) {
					Inventory inv = cha.getInventory();
					if (inv != null && isEquipped(cha, inv)) {
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

									if (weapon != null && weapon.getItem().isTohand()) {
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
					} else {
						// \f1이미 뭔가를 착용하고 있습니다.
						cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 124));
					}
				} else {
					if (equipped) {
						setEquipped(false);
						toSetoption(cha, true);
						toEquipped(cha, cha.getInventory());
						toOption(cha, true);
						toBuffCheck(cha);
					} else {
						// \f1당신의 클래스는 이 아이템을 사용할 수 없습니다.
						cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 264));
					}
				}
			}
		}
	}

	/**
	 * 방어구 착용 및 해제 처리 메서드.
	 */
	@Override
	public void toEquipped(Character cha, Inventory inv) {
		if (equipped) {
			cha.setAc(cha.getAc() + getTotalAc());
		} else {
			cha.setAc(cha.getAc() - getTotalAc());
		}

		switch (item.getSlot()) {
		case Lineage.SLOT_RING_LEFT:
		case Lineage.SLOT_RING_RIGHT:
			if (equipped) {
				if (inv.getSlot(Lineage.SLOT_RING_RIGHT) == null) {
					inv.setSlot(Lineage.SLOT_RING_RIGHT, this);
				} else {
					inv.setSlot(Lineage.SLOT_RING_LEFT, this);
				}
			} else {
				if (inv.getSlot(Lineage.SLOT_RING_RIGHT) != null && inv.getSlot(Lineage.SLOT_RING_RIGHT).getObjectId() == getObjectId()) {
					inv.setSlot(Lineage.SLOT_RING_RIGHT, null);
				} else if (inv.getSlot(Lineage.SLOT_RING_LEFT) != null && inv.getSlot(Lineage.SLOT_RING_LEFT).getObjectId() == getObjectId()) {
					inv.setSlot(Lineage.SLOT_RING_LEFT, null);
				} else {
					inv.setSlot(Lineage.SLOT_RING_RIGHT, null);
					inv.setSlot(Lineage.SLOT_RING_LEFT, null);
				}
			}
			break;
		default:
			inv.setSlot(item.getSlot(), equipped ? this : null);
			break;
		}

		if (!cha.isBlessArmor() && inv.getSlot(Lineage.SLOT_HELM) != null && inv.getSlot(Lineage.SLOT_SHIRT) != null && inv.getSlot(Lineage.SLOT_ARMOR) != null && inv.getSlot(Lineage.SLOT_CLOAK) != null
				&& inv.getSlot(Lineage.SLOT_GLOVE) != null && inv.getSlot(Lineage.SLOT_BOOTS) != null) {
			if ((inv.getSlot(Lineage.SLOT_HELM).getBless() == 0 || inv.getSlot(Lineage.SLOT_HELM).getBless() == -128)
					&& (inv.getSlot(Lineage.SLOT_HELM).getBless() == 0 || inv.getSlot(Lineage.SLOT_HELM).getBless() == -128)
					&& (inv.getSlot(Lineage.SLOT_SHIRT).getBless() == 0 || inv.getSlot(Lineage.SLOT_SHIRT).getBless() == -128)
					&& (inv.getSlot(Lineage.SLOT_ARMOR).getBless() == 0 || inv.getSlot(Lineage.SLOT_ARMOR).getBless() == -128)
					&& (inv.getSlot(Lineage.SLOT_CLOAK).getBless() == 0 || inv.getSlot(Lineage.SLOT_CLOAK).getBless() == -128)
					&& (inv.getSlot(Lineage.SLOT_GLOVE).getBless() == 0 || inv.getSlot(Lineage.SLOT_GLOVE).getBless() == -128)
					&& (inv.getSlot(Lineage.SLOT_BOOTS).getBless() == 0 || inv.getSlot(Lineage.SLOT_BOOTS).getBless() == -128)) {
				ChattingController.toChatting(cha, "축복받은 방어구 세트 효과 적용: AC-5", Lineage.CHATTING_MODE_MESSAGE);
				cha.setDynamicAc(cha.getDynamicAc() + 5);
				cha.setBlessArmor(true);
			}
		}

		if (!cha.isBlessAcc() && inv.getSlot(Lineage.SLOT_NECKLACE) != null && inv.getSlot(Lineage.SLOT_RING_LEFT) != null && inv.getSlot(Lineage.SLOT_RING_RIGHT) != null && inv.getSlot(Lineage.SLOT_BELT) != null) {
			if ((inv.getSlot(Lineage.SLOT_NECKLACE).getBless() == 0 || inv.getSlot(Lineage.SLOT_NECKLACE).getBless() == -128)
					&& (inv.getSlot(Lineage.SLOT_RING_LEFT).getBless() == 0 || inv.getSlot(Lineage.SLOT_RING_LEFT).getBless() == -128)
					&& (inv.getSlot(Lineage.SLOT_RING_RIGHT).getBless() == 0 || inv.getSlot(Lineage.SLOT_RING_RIGHT).getBless() == -128)
					&& (inv.getSlot(Lineage.SLOT_BELT).getBless() == 0 || inv.getSlot(Lineage.SLOT_BELT).getBless() == -128)) {
				ChattingController.toChatting(cha, "축복받은 장신구 세트 효과 적용: 대미지 감소+2, mr+5", Lineage.CHATTING_MODE_MESSAGE);
				cha.setDynamicReduction(cha.getDynamicReduction() + 2);
				cha.setDynamicMr(cha.getDynamicMr() + 5);

				cha.setBlessAcc(true);
			}
		}

		if (cha.isBlessArmor() && (inv.getSlot(Lineage.SLOT_HELM) == null || inv.getSlot(Lineage.SLOT_SHIRT) == null || inv.getSlot(Lineage.SLOT_ARMOR) == null || inv.getSlot(Lineage.SLOT_CLOAK) == null
				|| inv.getSlot(Lineage.SLOT_GLOVE) == null || inv.getSlot(Lineage.SLOT_BOOTS) == null)) {
			ChattingController.toChatting(cha, "축복받은 방어구 세트 효과 해제", Lineage.CHATTING_MODE_MESSAGE);
			cha.setDynamicAc(cha.getDynamicAc() - 5);
			cha.setBlessArmor(false);
		}

		if (cha.isBlessAcc() && (inv.getSlot(Lineage.SLOT_NECKLACE) == null || inv.getSlot(Lineage.SLOT_RING_LEFT) == null || inv.getSlot(Lineage.SLOT_RING_RIGHT) == null || inv.getSlot(Lineage.SLOT_BELT) == null)) {
			ChattingController.toChatting(cha, "축복받은 장신구 세트 효과 해제", Lineage.CHATTING_MODE_MESSAGE);
			cha.setDynamicReduction(cha.getDynamicReduction() - 2);
			cha.setDynamicMr(cha.getDynamicMr() - 5);
			cha.setBlessAcc(false);
		}
		if ((!cha.armorEnchant8) && (!cha.armorEnchant9) && (!cha.armorEnchant10) && (inv.getSlot(0) != null) && (inv.getSlot(3) != null) && (inv.getSlot(4) != null) && (inv.getSlot(5) != null)
				&& (inv.getSlot(9) != null) && (inv.getSlot(12) != null)) {

			if ((inv.getSlot(0).getEnLevel() >= 8) && (inv.getSlot(3).getEnLevel() >= 8) && (inv.getSlot(4).getEnLevel() >= 8) && (inv.getSlot(5).getEnLevel() >= 8) && (inv.getSlot(9).getEnLevel() >= 8)
					&& (inv.getSlot(12).getEnLevel() >= 8)) {
				ChattingController.toChatting(cha, "+8 방어구 세트 효과 적용: 대미지 감소+3", 20);
				cha.setDynamicReduction(cha.getDynamicReduction() + 3);
				cha.armorEnchant8 = true;
				cha.armorEnchant9 = false;
				cha.armorEnchant10 = false;
			}

			if ((inv.getSlot(0).getEnLevel() >= 9) && (inv.getSlot(3).getEnLevel() >= 9) && (inv.getSlot(4).getEnLevel() >= 9) && (inv.getSlot(5).getEnLevel() >= 9) && (inv.getSlot(9).getEnLevel() >= 9)
					&& (inv.getSlot(12).getEnLevel() == 9)) {
				ChattingController.toChatting(cha, "+9 방어구 세트 효과 적용: 대미지 감소+5", 20);
				cha.setDynamicReduction(cha.getDynamicReduction() + 5);
				cha.armorEnchant8 = false;
				cha.armorEnchant9 = true;
				cha.armorEnchant10 = false;
			}

			if ((inv.getSlot(0).getEnLevel() == 10) && (inv.getSlot(3).getEnLevel() == 10) && (inv.getSlot(4).getEnLevel() == 10) && (inv.getSlot(5).getEnLevel() == 10) && (inv.getSlot(9).getEnLevel() == 10)
					&& (inv.getSlot(12).getEnLevel() == 10)) {
				ChattingController.toChatting(cha, "+10 방어구 세트 효과 적용: 대미지 감소+8 , HP+300", 20);
				cha.setDynamicReduction(cha.getDynamicReduction() + 8);
				cha.setDynamicHp(cha.getDynamicHp() + 300);
				cha.armorEnchant8 = false;
				cha.armorEnchant9 = false;
				cha.armorEnchant10 = true;
			}

		}

		if ((cha.armorEnchant8 || cha.armorEnchant9 || cha.armorEnchant10)
				&& ((inv.getSlot(0) == null) || (inv.getSlot(3) == null) || (inv.getSlot(4) == null) || (inv.getSlot(5) == null) || (inv.getSlot(9) == null) || (inv.getSlot(12) == null))) {
			if (cha.armorEnchant8) {
				ChattingController.toChatting(cha, "+8 방어구 세트 효과 해제", 20);
				cha.setDynamicReduction(cha.getDynamicReduction() - 3);
				cha.armorEnchant8 = false;
			} else if (cha.armorEnchant9) {
				ChattingController.toChatting(cha, "+9 방어구 세트 효과 해제", 20);
				cha.setDynamicReduction(cha.getDynamicReduction() - 5);
				cha.armorEnchant9 = false;
			} else if (cha.armorEnchant10) {
				ChattingController.toChatting(cha, "+10 방어구 세트 효과 해제", 20);
				cha.setDynamicReduction(cha.getDynamicReduction() - 8);
				cha.setDynamicHp(cha.getDynamicHp() - 300);
				cha.armorEnchant10 = false;
			}
		}

		if (getBless() == 2 && equipped) {
			// \f1%0%s 손에 달라 붙었습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 149, getName()));
		}

		cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
	}

	/**
	 * 방어구 착용순서 체크하기.
	 */
	private boolean isEquipped(Character cha, Inventory inv) {
		// 착용해제 하려는가?
		if (equipped) {
			if (!Lineage.item_equipped_type) {
				// 갑옷해제시 망토 확인
				if (item.getSlot() == Lineage.SLOT_ARMOR && inv.getSlot(Lineage.SLOT_CLOAK) != null) {
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 127));
					return false;
				}
				// 티셔츠해제시 망토 확인
				if (item.getSlot() == Lineage.SLOT_SHIRT && inv.getSlot(Lineage.SLOT_CLOAK) != null) {
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 127));
					return false;
				}
				// 티셔츠해제시 아머 확인
				if (item.getSlot() == Lineage.SLOT_SHIRT && inv.getSlot(Lineage.SLOT_ARMOR) != null) {
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 127));
					return false;
				}
			}
			// 착용 하려는가?
		} else {
			if (!Lineage.item_equipped_type) {
				// 갑옷 착용시 망토 확인
				if (item.getSlot() == Lineage.SLOT_ARMOR && inv.getSlot(Lineage.SLOT_CLOAK) != null) {
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 126, "$226", "$225"));
					return false;
				}
				// 티셔츠 착용시 갑옷 확인
				if (item.getSlot() == Lineage.SLOT_SHIRT && inv.getSlot(Lineage.SLOT_ARMOR) != null) {
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 126, "$168", "$226"));
					return false;
				}
				// 티셔츠 착용시 망토 확인
				if (item.getSlot() == Lineage.SLOT_SHIRT && inv.getSlot(Lineage.SLOT_CLOAK) != null) {
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 126, "$168", "$225"));
					return false;
				}
			}
			// 방패 착용시 양손무기 확인
			if (item.getSlot() == Lineage.SLOT_SHIELD && inv.getSlot(Lineage.SLOT_WEAPON) != null && inv.getSlot(Lineage.SLOT_WEAPON).getItem().isTohand()) {
				if (!Lineage.item_equipped_type) {
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 129));
					return false;
				} else {
					inv.getSlot(Lineage.SLOT_WEAPON).toClick(cha, null);
				}
			}
			// 방패 착용시 가더 해제.
			if (item.getSlot() == Lineage.SLOT_SHIELD && inv.getSlot(Lineage.SLOT_GUARDER) != null)
				inv.getSlot(Lineage.SLOT_GUARDER).toClick(cha, null);
			// 가더 착용시 방패 해제.
			if (item.getSlot() == Lineage.SLOT_GUARDER && inv.getSlot(Lineage.SLOT_SHIELD) != null)
				inv.getSlot(Lineage.SLOT_SHIELD).toClick(cha, null);

			switch (item.getSlot()) {
			case Lineage.SLOT_RING_LEFT:
			case Lineage.SLOT_RING_RIGHT:
				if (inv.getSlot(Lineage.SLOT_RING_LEFT) != null && inv.getSlot(Lineage.SLOT_RING_RIGHT) != null) {
					if (Lineage.item_equipped_type && inv.getSlot(item.getSlot()).getBless() != 2) {
						do {
							inv.getSlot(item.getSlot()).toClick(cha, null);
						} while (inv.getSlot(item.getSlot()) != null);
					} else {
						return false;
					}
				}
				return true;
			default:
				if (inv.getSlot(item.getSlot()) != null) {
					if (Lineage.item_equipped_type && inv.getSlot(item.getSlot()).getBless() != 2) {
						do {
							inv.getSlot(item.getSlot()).toClick(cha, null);
						} while (inv.getSlot(item.getSlot()) != null);
					} else {
						return false;
					}
				}
				return true;
			}
		}
		return true;
	}

	/**
	 * 방어구 상태에따라 ac전체값 계산하여 리턴.
	 */
	public int getTotalAc() {
		int enLevel = getEnLevel();

		int ac = getItem().getAc() + enLevel + getDynamicAc();

		// 장신구는 따로 처리

		return ac < 0 ? 0 : ac;
	}

	/**
	 * 리니지 월드에 접속했을때 착용중인 아이템 처리를 위해 사용되는 메서드.
	 */
	@Override
	public void toWorldJoin(Connection con, PcInstance pc) {
		super.toWorldJoin(con, pc);
		if (equipped) {
			toSetoption(pc, false);
			toEquipped(pc, pc.getInventory());
			toOption(pc, false);
		}
	}

	/**
	 * 인첸트 활성화 됫을때 아이템의 뒷처리를 처리하도록 요청하는 메서드.
	 */
	@Override
	public void toEnchant(PcInstance pc, int en) {
		//
		if (en == -125 || en == -127)
			return;
		//
		if (en != 0) {
			if (equipped && getTotalAc() > 0) {
				pc.setAc(pc.getAc() + en);

				if ((getItem().getName().equalsIgnoreCase("수호성의 파워 글로브") || getItem().getName().equalsIgnoreCase("수호성의 활 골무")) && getEnLevel() > 4) {
					if (getItem().getName().equalsIgnoreCase("수호성의 파워 글로브"))
						pc.setDynamicAddHit(pc.getDynamicAddHit() + en);
					else
						pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + en);
				}

				pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
			}
		} else {
			Inventory inv = pc.getInventory();
			if (equipped) {
				setEquipped(false);
				toEquipped(pc, inv);
				toOption(pc, true);
				toSetoption(pc, true);
				toBuffCheck(pc);
			}
			inv.count(this, 0, true);
		}
		//
		super.toEnchant(pc, en);
	}

}
