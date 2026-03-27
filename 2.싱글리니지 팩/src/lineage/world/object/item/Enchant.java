package lineage.world.object.item;

import all_night.Lineage_Balance;
import lineage.database.EnchantLostItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.all_night.ScrollOfMetis;
import lineage.world.object.item.all_night.ScrollOfOrimArmor;
import lineage.world.object.item.all_night.ScrollOfOrimWeapon;
import lineage.world.object.item.all_night.ScrollOfWeapon;

public class Enchant extends ItemInstance {

	protected String EnMsg[];

	public Enchant() {
		EnMsg = new String[3];
	}

	/**
	 * 인첸트 확률계산할지 여부를 리턴함.
	 */
	protected boolean isChance(ItemInstance item) {	
		// 안전인첸보다 높거나, 저주일경우 인첸확률 체크.
		return bless == 2 ? item.getEnLevel() <= 0 : item.getEnLevel() >= item.getItem().getSafeEnchant();
	}

	/**
	 * 인첸트를 진행할지 여부.
	 * 
	 * @param item
	 * @return
	 */
	protected boolean isEnchant(ItemInstance item) {
		if (item == null)
			return false;
		if (!item.isAcc() && item instanceof ItemArmorInstance && Lineage.item_enchant_armor_max > 0 && item.getItem().getmaxEnchant() <= item.getEnLevel() && item.getItem().getmaxEnchant() != 0)
			return false;
		// 방어구 확인.
		if (!item.isAcc() && item instanceof ItemArmorInstance && Lineage.item_enchant_armor_max > 0 && Lineage.item_enchant_armor_max <= item.getEnLevel())
			return false;
		// 장신구 확인.	
		if (item.isAcc() && Lineage.item_enchant_accessory_max > 0 && Lineage.item_enchant_accessory_max <= item.getEnLevel())
			return false;
		// 무기 확인.
		
		if (item instanceof ItemWeaponInstance && item.getItem().getmaxEnchant() <= item.getEnLevel() && item.getItem().getmaxEnchant() != 0)
			return false;
		
		if (item instanceof ItemWeaponInstance && Lineage.item_enchant_weapon_max > 0 && Lineage.item_enchant_weapon_max <= item.getEnLevel() )
			return false;
		// 봉인 확인.
		if (item.getBless() < 0)
			return false;

		return true;
	}

	/**
	 * 인첸트를 처리할 메서드.
	 */

	protected int toEnchant(Character cha, ItemInstance item, ItemInstance accScroll) {


		// 기본 인첸 가능여부 판단.
		if (!isEnchant(item)) {
			if(item instanceof ItemWeaponInstance && item.getItem().getmaxEnchant() <= item.getEnLevel() && item.getItem().getmaxEnchant() != 0 )
				ChattingController.toChatting(cha, String.format("해당 아이템의 최고인챈은 +%d까지 입니다.",item.getItem().getmaxEnchant())  , Lineage.CHATTING_MODE_MESSAGE);
			
			if(item instanceof ItemArmorInstance && item.getItem().getmaxEnchant() <= item.getEnLevel() && item.getItem().getmaxEnchant() != 0 )
				ChattingController.toChatting(cha, String.format("해당 아이템의 최고인챈은 +%d까지 입니다.",item.getItem().getmaxEnchant())  , Lineage.CHATTING_MODE_MESSAGE);
			
			if (item instanceof ItemWeaponInstance && Lineage.item_enchant_weapon_max > 0 && Lineage.item_enchant_weapon_max <= item.getEnLevel() )
				ChattingController.toChatting(cha, String.format("무기는 최대 +%d까지 인챈트 가능합니다.", Lineage.item_enchant_weapon_max) , Lineage.CHATTING_MODE_MESSAGE);
			
			if (item instanceof ItemArmorInstance && Lineage.item_enchant_armor_max > 0 && Lineage.item_enchant_armor_max <= item.getEnLevel() && !item.isAcc())
				ChattingController.toChatting(cha, String.format("방어구는 +%d까지 인챈트 가능합니다.", Lineage.item_enchant_armor_max), Lineage.CHATTING_MODE_MESSAGE);

			if (item instanceof ItemArmorInstance && Lineage.item_enchant_accessory_max > 0 && Lineage.item_enchant_accessory_max <= item.getEnLevel() && item.isAcc())
				ChattingController.toChatting(cha, String.format("장신구는 +%d까지 인챈트 가능합니다.", Lineage.item_enchant_accessory_max), Lineage.CHATTING_MODE_MESSAGE);
			

			return -127;
		}

		return toEnchantNew(cha, item, accScroll);
	}

	protected int toEnchantNew(Character cha, ItemInstance item, ItemInstance accScroll) {
		boolean chance = isChance(item);
		boolean isEnchant = true;
		//boolean isEnchantTop = false;
		int rnd = 0;
		int safeEnLevel = item.getItem().getSafeEnchant();
		String item_name = item.toStringDB();
		long item_objid = item.getObjectId();

		// 메세지 설정.
		EnMsg[0] = item.toString();
		// 검게, 파랗게, 은색으로
		EnMsg[1] = bless == 2 ? "$246" : item instanceof ItemWeaponInstance ? "$245" : "$252";
		EnMsg[2] = "$247"; // 한 순간

		if (!item.isAcc()) {
			if (this instanceof ScrollOfOrimWeapon || this instanceof ScrollOfOrimArmor) {
				double orimChance = 0;
				rnd = 1;
				// 오림의 갑옷 마법 주문서
				if (this instanceof ScrollOfOrimWeapon) {
					// 안전 인챈까진 100%확률
					if (item.getEnLevel() >= item.getItem().getSafeEnchant()) {
						if (bless == 1) {
							if (item.getItem().getSafeEnchant() == 0) {
								switch (item.getEnLevel()) {
								case 0:
									orimChance = Lineage_Balance.orim_weapon_0_0_probability;
									break;
								case 1:
									orimChance = Lineage_Balance.orim_weapon_0_1_probability;
									break;
								case 2:
									orimChance = Lineage_Balance.orim_weapon_0_2_probability;
									break;
								case 3:
									orimChance = Lineage_Balance.orim_weapon_0_3_probability;
									break;
								case 4:
									orimChance = Lineage_Balance.orim_weapon_0_4_probability;
									break;
								case 5:
									orimChance = Lineage_Balance.orim_weapon_0_5_probability;
									break;
								case 6:
									orimChance = Lineage_Balance.orim_weapon_0_6_probability;
									break;
								case 7:
									orimChance = Lineage_Balance.orim_weapon_0_7_probability;
									break;
								case 8:
									orimChance = Lineage_Balance.orim_weapon_0_8_probability;
									break;
								default:
									orimChance = Lineage_Balance.orim_weapon_0_9_probability;
									break;
								}
							} else {
								switch (item.getEnLevel()) {
								case 6:
									orimChance = Lineage_Balance.orim_weapon_6_probability;
									break;
								case 7:
									orimChance = Lineage_Balance.orim_weapon_7_probability;
									break;
								case 8:
									orimChance = Lineage_Balance.orim_weapon_8_probability;
									break;
								case 9:
									orimChance = Lineage_Balance.orim_weapon_9_probability;
									break;
								case 10:
									orimChance = Lineage_Balance.orim_weapon_10_probability;
									break;
								case 11:
									orimChance = Lineage_Balance.orim_weapon_11_probability;
									break;
								case 12:
									orimChance = Lineage_Balance.orim_weapon_12_probability;
									break;
								case 13:
									orimChance = Lineage_Balance.orim_weapon_13_probability;
									break;
								case 14:
									orimChance = Lineage_Balance.orim_weapon_14_probability;
									break;
								default:
									orimChance = Lineage_Balance.orim_weapon_15_probability;
									break;
								}
							}

							isEnchant = Math.random() < orimChance;

							if (!isEnchant) {
								isEnchant = true;

								if (Math.random() < Lineage_Balance.orim_scroll_weapon_nothing_probability)
									rnd = 0;
								else
									rnd = -1;

								if (item.getEnLevel() < 1 && rnd == -1)
									rnd = 0;

								if (rnd == -1)
									EnMsg[1] = "$246";
							}
						} else if (bless == 0 || bless == -128) {
							if (item.getItem().getSafeEnchant() == 0) {
								switch (item.getEnLevel()) {
								case 0:
									orimChance = Lineage_Balance.orim_bless_weapon_0_0_probability;
									break;
								case 1:
									orimChance = Lineage_Balance.orim_bless_weapon_0_1_probability;
									break;
								case 2:
									orimChance = Lineage_Balance.orim_bless_weapon_0_2_probability;
									break;
								case 3:
									orimChance = Lineage_Balance.orim_bless_weapon_0_3_probability;
									break;
								case 4:
									orimChance = Lineage_Balance.orim_bless_weapon_0_4_probability;
									break;
								case 5:
									orimChance = Lineage_Balance.orim_bless_weapon_0_5_probability;
									break;
								case 6:
									orimChance = Lineage_Balance.orim_bless_weapon_0_6_probability;
									break;
								case 7:
									orimChance = Lineage_Balance.orim_bless_weapon_0_7_probability;
									break;
								case 8:
									orimChance = Lineage_Balance.orim_bless_weapon_0_8_probability;
									break;
								default:
									orimChance = Lineage_Balance.orim_bless_weapon_0_9_probability;
									break;
								}
							} else {
								switch (item.getEnLevel()) {
								case 6:
									orimChance = Lineage_Balance.orim_bless_weapon_6_probability;
									break;
								case 7:
									orimChance = Lineage_Balance.orim_bless_weapon_7_probability;
									break;
								case 8:
									orimChance = Lineage_Balance.orim_bless_weapon_8_probability;
									break;
								case 9:
									orimChance = Lineage_Balance.orim_bless_weapon_9_probability;
									break;
								case 10:
									orimChance = Lineage_Balance.orim_bless_weapon_10_probability;
									break;
								case 11:
									orimChance = Lineage_Balance.orim_bless_weapon_11_probability;
									break;
								case 12:
									orimChance = Lineage_Balance.orim_bless_weapon_12_probability;
									break;
								case 13:
									orimChance = Lineage_Balance.orim_bless_weapon_13_probability;
									break;
								case 14:
									orimChance = Lineage_Balance.orim_bless_weapon_14_probability;
									break;
								default:
									orimChance = Lineage_Balance.orim_bless_weapon_15_probability;
									break;
								}
							}

							isEnchant = Math.random() < orimChance;

							if (!isEnchant) {
								isEnchant = true;
								rnd = 0;
							}
						}
					}
				} else if (this instanceof ScrollOfOrimArmor) {
					// 안전 인챈까진 100%확률
					if (item.getEnLevel() >= item.getItem().getSafeEnchant()) {
						if (bless == 1) {
							if (item.getItem().getSafeEnchant() == 0) {
								switch (item.getEnLevel()) {
								case 0:
									orimChance = Lineage_Balance.orim_armor_0_0_probability;
									break;
								case 1:
									orimChance = Lineage_Balance.orim_armor_0_1_probability;
									break;
								case 2:
									orimChance = Lineage_Balance.orim_armor_0_2_probability;
									break;
								case 3:
									orimChance = Lineage_Balance.orim_armor_0_3_probability;
									break;
								case 4:
									orimChance = Lineage_Balance.orim_armor_0_4_probability;
									break;
								case 5:
									orimChance = Lineage_Balance.orim_armor_0_5_probability;
									break;
								case 6:
									orimChance = Lineage_Balance.orim_armor_0_6_probability;
									break;
								case 7:
									orimChance = Lineage_Balance.orim_armor_0_7_probability;
									break;
								case 8:
									orimChance = Lineage_Balance.orim_armor_0_8_probability;
									break;
								default:
									orimChance = Lineage_Balance.orim_armor_0_9_probability;
									break;
								}
							} else if (item.getItem().getSafeEnchant() == 4) { 
								switch (item.getEnLevel()) {
								case 4:
									orimChance = Lineage_Balance.orim_armor_4_4_probability;
									break;
								case 5:
									orimChance = Lineage_Balance.orim_armor_4_5_probability;
									break;
								case 6:
									orimChance = Lineage_Balance.orim_armor_4_6_probability;
									break;
								case 7:
									orimChance = Lineage_Balance.orim_armor_4_7_probability;
									break;
								case 8:
									orimChance = Lineage_Balance.orim_armor_4_8_probability;
									break;
								case 9:
									orimChance = Lineage_Balance.orim_armor_4_9_probability;
									break;
								case 10:
									orimChance = Lineage_Balance.orim_armor_4_10_probability;
									break;
								case 11:
									orimChance = Lineage_Balance.orim_armor_4_11_probability;
									break;
								case 12:
									orimChance = Lineage_Balance.orim_armor_4_12_probability;
									break;
								default:
									orimChance = Lineage_Balance.orim_armor_4_13_probability;
									break;
								}
							} else {
								switch (item.getEnLevel()) {
								case 6:
									orimChance = Lineage_Balance.orim_armor_6_probability;
									break;
								case 7:
									orimChance = Lineage_Balance.orim_armor_7_probability;
									break;
								case 8:
									orimChance = Lineage_Balance.orim_armor_8_probability;
									break;
								case 9:
									orimChance = Lineage_Balance.orim_armor_9_probability;
									break;
								case 10:
									orimChance = Lineage_Balance.orim_armor_10_probability;
									break;
								case 11:
									orimChance = Lineage_Balance.orim_armor_11_probability;
									break;
								case 12:
									orimChance = Lineage_Balance.orim_armor_12_probability;
									break;
								case 13:
									orimChance = Lineage_Balance.orim_armor_13_probability;
									break;
								case 14:
									orimChance = Lineage_Balance.orim_armor_14_probability;
									break;
								default:
									orimChance = Lineage_Balance.orim_armor_15_probability;
									break;
								}
							}

							isEnchant = Math.random() < orimChance;

							if (!isEnchant) {
								isEnchant = true;

								if (Math.random() < Lineage_Balance.orim_scroll_armor_nothing_probability)
									rnd = 0;
								else
									rnd = -1;

								if (item.getEnLevel() < 1 && rnd == -1)
									rnd = 0;

								if (rnd == -1)
									EnMsg[1] = "$246";
							}
						} else if (bless == 0 || bless == -128) {
							if (item.getItem().getSafeEnchant() == 0) {
								switch (item.getEnLevel()) {
								case 0:
									orimChance = Lineage_Balance.orim_bless_armor_0_0_probability;
									break;
								case 1:
									orimChance = Lineage_Balance.orim_bless_armor_0_1_probability;
									break;
								case 2:
									orimChance = Lineage_Balance.orim_bless_armor_0_2_probability;
									break;
								case 3:
									orimChance = Lineage_Balance.orim_bless_armor_0_3_probability;
									break;
								case 4:
									orimChance = Lineage_Balance.orim_bless_armor_0_4_probability;
									break;
								case 5:
									orimChance = Lineage_Balance.orim_bless_armor_0_5_probability;
									break;
								case 6:
									orimChance = Lineage_Balance.orim_bless_armor_0_6_probability;
									break;
								case 7:
									orimChance = Lineage_Balance.orim_bless_armor_0_7_probability;
									break;
								case 8:
									orimChance = Lineage_Balance.orim_bless_armor_0_8_probability;
									break;
								default:
									orimChance = Lineage_Balance.orim_bless_armor_0_9_probability;
									break;
								}
							} else if (item.getItem().getSafeEnchant() == 4) {
								switch (item.getEnLevel()) {
								case 4:
									orimChance = Lineage_Balance.orim_bless_armor_4_4_probability;
									break;
								case 5:
									orimChance = Lineage_Balance.orim_bless_armor_4_5_probability;
									break;
								case 6:
									orimChance = Lineage_Balance.orim_bless_armor_4_6_probability;
									break;
								case 7:
									orimChance = Lineage_Balance.orim_bless_armor_4_7_probability;
									break;
								case 8:
									orimChance = Lineage_Balance.orim_bless_armor_4_8_probability;
									break;
								case 9:
									orimChance = Lineage_Balance.orim_bless_armor_4_9_probability;
									break;
								case 10:
									orimChance = Lineage_Balance.orim_bless_armor_4_10_probability;
									break;
								case 11:
									orimChance = Lineage_Balance.orim_bless_armor_4_11_probability;
									break;
								case 12:
									orimChance = Lineage_Balance.orim_bless_armor_4_12_probability;
									break;
								default:
									orimChance = Lineage_Balance.orim_bless_armor_4_13_probability;
									break;
								}
							}else {
								switch (item.getEnLevel()) {
								case 6:
									orimChance = Lineage_Balance.orim_bless_armor_6_probability;
									break;
								case 7:
									orimChance = Lineage_Balance.orim_bless_armor_7_probability;
									break;
								case 8:
									orimChance = Lineage_Balance.orim_bless_armor_8_probability;
									break;
								case 9:
									orimChance = Lineage_Balance.orim_bless_armor_9_probability;
									break;
								case 10:
									orimChance = Lineage_Balance.orim_bless_armor_10_probability;
									break;
								case 11:
									orimChance = Lineage_Balance.orim_bless_armor_11_probability;
									break;
								case 12:
									orimChance = Lineage_Balance.orim_bless_armor_12_probability;
									break;
								case 13:
									orimChance = Lineage_Balance.orim_bless_armor_13_probability;
									break;
								case 14:
									orimChance = Lineage_Balance.orim_bless_armor_14_probability;
									break;
								default:
									orimChance = Lineage_Balance.orim_bless_armor_15_probability;
									break;
								}
							}

							isEnchant = Math.random() < orimChance;

							if (!isEnchant) {
								isEnchant = true;
								rnd = 0;
							}
						}
					}
				}
			} else {
				switch (bless) {
				// 일반 주문서
				// 축 주문서
				case 0:
				case 1:
					// 인첸트값 설정
					rnd = 1;
					
					// 축복받은 주문서
					if (bless == 0) {
						if (item instanceof ItemWeaponInstance) {
							switch (safeEnLevel) {
							// 안전인첸트 0
							case 0:
								switch (item.getEnLevel()) {
								case 0:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant0_0_3_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant0_0_2_probability)
										rnd = 2;
									break;
								case 1:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant0_1_4_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant0_1_3_probability)
										rnd = 2;
									break;
								case 2:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant0_2_5_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant0_2_4_probability)
										rnd = 2;
									break;
								case 3:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant0_3_6_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant0_3_5_probability)
										rnd = 2;
									break;
								case 4:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant0_4_7_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant0_4_6_probability)
										rnd = 2;
									break;
								case 5:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant0_5_8_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant0_5_7_probability)
										rnd = 2;
									break;
								default:
									if (item.getEnLevel() >= 6) {
										if (Math.random() < Lineage_Balance.weapon_safe_enchant0_6_enchant3_probability)
											rnd = 3;
										else if (Math.random() < Lineage_Balance.weapon_safe_enchant0_6_enchant2_probability)
											rnd = 2;
									} else {
										rnd = 1;
									}
									break;
								}
								break;
							// 안전인첸트 6
							case 6:
								switch (item.getEnLevel()) {
								case 0:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant6_0_3_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant6_0_2_probability)
										rnd = 2;
									break;
								case 1:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant6_1_4_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant6_1_3_probability)
										rnd = 2;
									break;
								case 2:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant6_2_5_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant6_2_4_probability)
										rnd = 2;
									break;
								case 3:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant6_3_6_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant6_3_5_probability)
										rnd = 2;
									break;
								case 4:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant6_4_7_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant6_4_6_probability)
										rnd = 2;
									break;
								case 5:
									if (Math.random() < Lineage_Balance.weapon_safe_enchant6_5_8_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.weapon_safe_enchant6_5_7_probability)
										rnd = 2;
									break;
								default:
									if (item.getEnLevel() >= 6) {
										if (Math.random() < Lineage_Balance.weapon_safe_enchant6_6_enchant3_probability)
											rnd = 3;
										else if (Math.random() < Lineage_Balance.weapon_safe_enchant6_6_enchant2_probability)
											rnd = 2;
									} else {
										rnd = 1;
									}
									break;
								}
								break;
							}
						} else if (item instanceof ItemArmorInstance) {
							switch (safeEnLevel) {
							// 안전인첸트 0
							case 0:
								switch (item.getEnLevel()) {
								case 0:
									if (Math.random() < Lineage_Balance.armor_safe_enchant0_0_3_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant0_0_2_probability)
										rnd = 2;
									break;
								case 1:
									if (Math.random() < Lineage_Balance.armor_safe_enchant0_1_4_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant0_1_3_probability)
										rnd = 2;
									break;
								case 2:
									if (Math.random() < Lineage_Balance.armor_safe_enchant0_2_5_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant0_2_4_probability)
										rnd = 2;
									break;
								case 3:
									if (Math.random() < Lineage_Balance.armor_safe_enchant0_3_6_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant0_3_5_probability)
										rnd = 2;
									break;
								case 4:
									if (Math.random() < Lineage_Balance.armor_safe_enchant0_4_7_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant0_4_6_probability)
										rnd = 2;
									break;
								case 5:
									if (Math.random() < Lineage_Balance.armor_safe_enchant0_5_8_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant0_5_7_probability)
										rnd = 2;
									break;
								default:
									if (item.getEnLevel() >= 6) {
										if (Math.random() < Lineage_Balance.armor_safe_enchant0_6_enchant3_probability)
											rnd = 3;
										else if (Math.random() < Lineage_Balance.armor_safe_enchant0_6_enchant2_probability)
											rnd = 2;
									} else {
										rnd = 1;
									}
									break;
								}
								break;
							// 안전인첸트 4
							case 4:
								switch (item.getEnLevel()) {
								case 0:
									if (Math.random() < Lineage_Balance.armor_safe_enchant4_0_3_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant4_0_2_probability)
										rnd = 2;
									break;
								case 1:
									if (Math.random() < Lineage_Balance.armor_safe_enchant4_1_4_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant4_1_3_probability)
										rnd = 2;
									break;
								case 2:
									if (Math.random() < Lineage_Balance.armor_safe_enchant4_2_5_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant4_2_4_probability)
										rnd = 2;
									break;
								case 3:
									if (Math.random() < Lineage_Balance.armor_safe_enchant4_3_6_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant4_3_5_probability)
										rnd = 2;
									break;
								case 4:
									if (Math.random() < Lineage_Balance.armor_safe_enchant4_4_7_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant4_4_6_probability)
										rnd = 2;
									break;
								case 5:
									if (Math.random() < Lineage_Balance.armor_safe_enchant4_5_8_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant4_5_7_probability)
										rnd = 2;
									break;
								default:
									if (item.getEnLevel() >= 6) {
										if (Math.random() < Lineage_Balance.armor_safe_enchant4_6_enchant3_probability)
											rnd = 3;
										else if (Math.random() < Lineage_Balance.armor_safe_enchant4_6_enchant2_probability)
											rnd = 2;
									} else {
										rnd = 1;
									}
									break;
								}
								break;
							// 안전인첸트 6
							case 6:
								switch (item.getEnLevel()) {
								case 0:
									if (Math.random() < Lineage_Balance.armor_safe_enchant6_0_3_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant6_0_2_probability)
										rnd = 2;
									break;
								case 1:
									if (Math.random() < Lineage_Balance.armor_safe_enchant6_1_4_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant6_1_3_probability)
										rnd = 2;
									break;
								case 2:
									if (Math.random() < Lineage_Balance.armor_safe_enchant6_2_5_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant6_2_4_probability)
										rnd = 2;
									break;
								case 3:
									if (Math.random() < Lineage_Balance.armor_safe_enchant6_3_6_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant6_3_5_probability)
										rnd = 2;
									break;
								case 4:
									if (Math.random() < Lineage_Balance.armor_safe_enchant6_4_7_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant6_4_6_probability)
										rnd = 2;
									break;
								case 5:
									if (Math.random() < Lineage_Balance.armor_safe_enchant6_5_8_probability)
										rnd = 3;
									else if (Math.random() < Lineage_Balance.armor_safe_enchant6_5_7_probability)
										rnd = 2;
									break;
								default:
									if (item.getEnLevel() >= 6) {
										if (Math.random() < Lineage_Balance.armor_safe_enchant6_6_enchant3_probability)
											rnd = 3;
										else if (Math.random() < Lineage_Balance.armor_safe_enchant6_6_enchant2_probability)
											rnd = 2;
									} else {
										rnd = 1;
									}
									break;
								}
								break;
							}
						}
						
						// "잠시" 메세지 설정
						if (rnd > 1)
							EnMsg[2] = "$248";
					}

					// 인첸트 확률
					if (chance) {
						if (item instanceof ItemWeaponInstance) {
							switch (safeEnLevel) {
							// 안전인첸트 0
							case 0:
								switch (item.getEnLevel()) {
								case 0:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant0_0_probability;
									break;
								case 1:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant0_1_probability;
									break;
								case 2:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant0_2_probability;
									break;
								case 3:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant0_3_probability;
									break;
								case 4:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant0_4_probability;
									break;
								case 5:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant0_5_probability;
									break;
								case 6:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant0_6_probability;
									break;
								case 7:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant0_7_probability;
									break;
								case 8:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant0_8_probability;
									break;
								default :
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant0_9_probability;
									break;
								}
								break;
							// 안전인첸트 6
							case 6:
								switch (item.getEnLevel()) {
								case 6:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant6_6_probability;
									break;
								case 7:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant6_7_probability;
									break;
								case 8:
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant6_8_probability;
									break;
								default :
									isEnchant = Math.random() < Lineage_Balance.weapon_safe_enchant6_9_probability;
									break;
								}
								break;
							}
							
							if (item.getEnLevel() >= 9 && !isEnchant) {
								if (Math.random() < Lineage_Balance.weapon_enchant_9_success_probability) {
									isEnchant = true;
									rnd = 1;
								} else if (Math.random() < Lineage_Balance.weapon_enchant_9_nothing_probability) {
									isEnchant = true;
									rnd = 0;
								}
							}
							
							// 장인의 무기 마법 주문서
							if (this instanceof ScrollOfWeapon ) {
								double orimChance = 0;
								
								if (bless == 1) {
									if (item.getItem().getSafeEnchant() == 0) {
										switch (item.getEnLevel()) {
										case 0:
											orimChance = Lineage_Balance.orim_weapon_0_0_probability;
											break;
										case 1:
											orimChance = Lineage_Balance.orim_weapon_0_1_probability;
											break;
										case 2:
											orimChance = Lineage_Balance.orim_weapon_0_2_probability;
											break;
										case 3:
											orimChance = Lineage_Balance.orim_weapon_0_3_probability;
											break;
										case 4:
											orimChance = Lineage_Balance.orim_weapon_0_4_probability;
											break;
										case 5:
											orimChance = Lineage_Balance.orim_weapon_0_5_probability;
											break;
										case 6:
											orimChance = Lineage_Balance.orim_weapon_0_6_probability;
											break;
										case 7:
											orimChance = Lineage_Balance.orim_weapon_0_7_probability;
											break;
										case 8:
											orimChance = Lineage_Balance.orim_weapon_0_8_probability;
											break;
										default:
											orimChance = Lineage_Balance.orim_weapon_0_9_probability;
											break;
										}
									} else {
										switch (item.getEnLevel()) {
										case 6:
											orimChance = Lineage_Balance.orim_weapon_6_probability;
											break;
										case 7:
											orimChance = Lineage_Balance.orim_weapon_7_probability;
											break;
										case 8:
											orimChance = Lineage_Balance.orim_weapon_8_probability;
											break;
										case 9:
											orimChance = Lineage_Balance.orim_weapon_9_probability;
											break;
										case 10:
											orimChance = Lineage_Balance.orim_weapon_10_probability;
											break;
										case 11:
											orimChance = Lineage_Balance.orim_weapon_11_probability;
											break;
										case 12:
											orimChance = Lineage_Balance.orim_weapon_12_probability;
											break;
										case 13:
											orimChance = Lineage_Balance.orim_weapon_13_probability;
											break;
										case 14:
											orimChance = Lineage_Balance.orim_weapon_14_probability;
											break;
										default:
											orimChance = Lineage_Balance.orim_weapon_15_probability;
											break;
										}
									}

									isEnchant = Math.random() < orimChance;

									if (!isEnchant) {
										isEnchant = true;

										if (Math.random() < Lineage_Balance.orim_scroll_weapon_nothing_probability)
											rnd = 0;
										else
											rnd = -1;

										if (item.getEnLevel() < 1 && rnd == -1)
											rnd = 0;

										if (rnd == -1)
											EnMsg[1] = "$246";
									}
								} else if (bless == 0 || bless == -128) {
									if (item.getItem().getSafeEnchant() == 0) {
										switch (item.getEnLevel()) {
										case 0:
											orimChance = Lineage_Balance.orim_bless_weapon_0_0_probability;
											break;
										case 1:
											orimChance = Lineage_Balance.orim_bless_weapon_0_1_probability;
											break;
										case 2:
											orimChance = Lineage_Balance.orim_bless_weapon_0_2_probability;
											break;
										case 3:
											orimChance = Lineage_Balance.orim_bless_weapon_0_3_probability;
											break;
										case 4:
											orimChance = Lineage_Balance.orim_bless_weapon_0_4_probability;
											break;
										case 5:
											orimChance = Lineage_Balance.orim_bless_weapon_0_5_probability;
											break;
										case 6:
											orimChance = Lineage_Balance.orim_bless_weapon_0_6_probability;
											break;
										case 7:
											orimChance = Lineage_Balance.orim_bless_weapon_0_7_probability;
											break;
										case 8:
											orimChance = Lineage_Balance.orim_bless_weapon_0_8_probability;
											break;
										default:
											orimChance = Lineage_Balance.orim_bless_weapon_0_9_probability;
											break;
										}
									} else {
										switch (item.getEnLevel()) {
										case 6:
											orimChance = Lineage_Balance.orim_bless_weapon_6_probability;
											break;
										case 7:
											orimChance = Lineage_Balance.orim_bless_weapon_7_probability;
											break;
										case 8:
											orimChance = Lineage_Balance.orim_bless_weapon_8_probability;
											break;
										case 9:
											orimChance = Lineage_Balance.orim_bless_weapon_9_probability;
											break;
										case 10:
											orimChance = Lineage_Balance.orim_bless_weapon_10_probability;
											break;
										case 11:
											orimChance = Lineage_Balance.orim_bless_weapon_11_probability;
											break;
										case 12:
											orimChance = Lineage_Balance.orim_bless_weapon_12_probability;
											break;
										case 13:
											orimChance = Lineage_Balance.orim_bless_weapon_13_probability;
											break;
										case 14:
											orimChance = Lineage_Balance.orim_bless_weapon_14_probability;
											break;
										default:
											orimChance = Lineage_Balance.orim_bless_weapon_15_probability;
											break;
										}
									}

									isEnchant = Math.random() < orimChance;

									if (!isEnchant) {
										isEnchant = true;
										rnd = 0;
									}
								}

							}
						} else if (item instanceof ItemArmorInstance) {
							switch (safeEnLevel) {
							// 안전인첸트 0
							case 0:
								switch (item.getEnLevel()) {
								case 0:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant0_0_probability;
									break;
								case 1:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant0_1_probability;
									break;
								case 2:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant0_2_probability;
									break;
								case 3:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant0_3_probability;
									break;
								case 4:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant0_4_probability;
									break;
								case 5:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant0_5_probability;
									break;
								case 6:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant0_6_probability;
									break;
								case 7:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant0_7_probability;
									break;
								case 8:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant0_8_probability;
									break;
								default :
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant0_9_probability;
									break;
								}
								break;
							// 안전인첸트 4
							case 4:
								switch (item.getEnLevel()) {
								case 4:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant4_4_probability;
									break;
								case 5:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant4_5_probability;
									break;
								case 6:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant4_6_probability;
									break;
								case 7:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant4_7_probability;
									break;
								case 8:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant4_8_probability;
									break;
								default :
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant4_9_probability;
									break;
								}
								break;
							// 안전인첸트 6
							case 6:
								switch (item.getEnLevel()) {
								case 6:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant6_6_probability;
									break;
								case 7:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant6_7_probability;
									break;
								case 8:
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant6_8_probability;
									break;
								default :
									isEnchant = Math.random() < Lineage_Balance.armor_safe_enchant6_9_probability;
									break;
								}
								break;
							}
						}
					}
					break;
				// 저주 주문서
				case 2:
					if(item.getEnLevel() > 0){
						rnd = -1;
						isEnchant = true;
						
						if (chance && Math.random() < 0.5)
							isEnchant = false;
					
					}
				

					break;
				}
			}
		} else {
			if (getItem().getName().contains("장신구 마법 주문서[") && getItem().getSmallDmg() > 1) {
				
				rnd = 1;
		
				double test = getItem().getSmallDmg()*0.01;
				isEnchant = Math.random() < test;
				
				if (!isEnchant) {
					isEnchant = true;
					rnd = 0;
				}
				
					
			}
			// 장신구 인첸트
			if (getItem().getName().equalsIgnoreCase("장신구 마법 주문서")) {
				rnd = 1;
				switch (safeEnLevel) {
					case 0:
						switch (item.getEnLevel()) {
						case 0:
							isEnchant = Math.random() < Lineage_Balance.accessories_0_probability;
							break;
						case 1:
							isEnchant = Math.random() < Lineage_Balance.accessories_1_probability;
							break;
						case 2:
							isEnchant = Math.random() < Lineage_Balance.accessories_2_probability;
							break;
						case 3:
							isEnchant = Math.random() < Lineage_Balance.accessories_3_probability;
							break;
						case 4:
							isEnchant = Math.random() < Lineage_Balance.accessories_4_probability;
							break;
						case 5:
							isEnchant = Math.random() < Lineage_Balance.accessories_5_probability;
							break;
						case 6:
							isEnchant = Math.random() < Lineage_Balance.accessories_6_probability;
							break;
						case 7:
							isEnchant = Math.random() < Lineage_Balance.accessories_7_probability;
							break;
						case 8:
							isEnchant = Math.random() < Lineage_Balance.accessories_8_probability;
							break;
						default:
							isEnchant = Math.random() < Lineage_Balance.accessories_9_probability;
							break;
					}
					break;
					case 2:
						switch (item.getEnLevel()) {	
						case 0:
						case 1:
							rnd = 1;
							isEnchant = true;
							break;
						case 2:
							isEnchant = Math.random() < Lineage_Balance.accessories_2_probability;
						case 3:
							isEnchant = Math.random() < Lineage_Balance.accessories_3_probability;
							break;
						case 4:
							isEnchant = Math.random() < Lineage_Balance.accessories_4_probability;
							break;
						case 5:
							isEnchant = Math.random() < Lineage_Balance.accessories_5_probability;
							break;
						case 6:
							isEnchant = Math.random() < Lineage_Balance.accessories_6_probability;
							break;
						case 7:
							isEnchant = Math.random() < Lineage_Balance.accessories_7_probability;
							break;
						case 8:
							isEnchant = Math.random() < Lineage_Balance.accessories_8_probability;
							break;
						default:
							isEnchant = Math.random() < Lineage_Balance.accessories_9_probability;
							break;
					}
					break;
				}
			} else if (getItem().getName().equalsIgnoreCase("오림의 장신구 마법 주문서") && bless == 1) {
				rnd = 1;
				
				switch (item.getEnLevel()) {
				case 0:
					isEnchant = Math.random() < Lineage_Balance.accessories_0_probability;
					break;
				case 1:
					isEnchant = Math.random() < Lineage_Balance.accessories_1_probability;
					break;
				case 2:
					isEnchant = Math.random() < Lineage_Balance.accessories_2_probability;
					break;
				case 3:
					isEnchant = Math.random() < Lineage_Balance.accessories_3_probability;
					break;
				case 4:
					isEnchant = Math.random() < Lineage_Balance.accessories_4_probability;
					break;
				case 5:
					isEnchant = Math.random() < Lineage_Balance.accessories_5_probability;
					break;
				case 6:
					isEnchant = Math.random() < Lineage_Balance.accessories_6_probability;
					break;
				case 7:
					isEnchant = Math.random() < Lineage_Balance.accessories_7_probability;
					break;
				case 8:
					isEnchant = Math.random() < Lineage_Balance.accessories_8_probability;
					break;
				default:
					isEnchant = Math.random() < Lineage_Balance.accessories_9_probability;
					break;
				}
				
				if (!isEnchant) {
					isEnchant = true;
					
					if (Math.random() < Lineage_Balance.accessories_nothing_probability)
						rnd = 0;
					else
						rnd = -1;
					
					if (item.getEnLevel() < 1 && rnd == -1)
						rnd = 0;
					
					if (rnd == -1)
						EnMsg[1] = "$246";	
				}
			} else if (getItem().getName().equalsIgnoreCase("오림의 장신구 마법 주문서") && bless == 0) {
				rnd = 1;
				
				switch (item.getEnLevel()) {
				case 0:
					isEnchant = Math.random() < Lineage_Balance.accessories_bless_0_probability;
					break;
				case 1:
					isEnchant = Math.random() < Lineage_Balance.accessories_bless_1_probability;
					break;
				case 2:
					isEnchant = Math.random() < Lineage_Balance.accessories_bless_2_probability;
					break;
				case 3:
					isEnchant = Math.random() < Lineage_Balance.accessories_bless_3_probability;
					break;
				case 4:
					isEnchant = Math.random() < Lineage_Balance.accessories_bless_4_probability;
					break;
				case 5:
					isEnchant = Math.random() < Lineage_Balance.accessories_bless_5_probability;
					break;
				case 6:
					isEnchant = Math.random() < Lineage_Balance.accessories_bless_6_probability;
					break;
				case 7:
					isEnchant = Math.random() < Lineage_Balance.accessories_bless_7_probability;
					break;
				case 8:
					isEnchant = Math.random() < Lineage_Balance.accessories_bless_8_probability;
					break;
				default:
					isEnchant = Math.random() < Lineage_Balance.accessories_bless_9_probability;
					break;
				}
				
				if (!isEnchant) {
					isEnchant = true;
					rnd = 0;
				}
			}
		}

		// 메티스의 축복
		if (this instanceof ScrollOfMetis) {
			rnd = 1;
			isEnchant = true;
		}
		
		StringBuffer itemName = new StringBuffer();
		// 인첸 성공시 아이템에 실제 변수 설정하는 부분.
		if (isEnchant || cha.getGm() > 0) {
			if (cha.getGm() > 0)
				isEnchant = true;
	
			if (item.getItem().getSafeEnchant() <= item.getEnLevel() &&  rnd > 0)
				itemName.append(String.format("%s ->", Util.getItemNameToString(item, item.getCount())));
			// 아이템 인첸트값 set
			if (item instanceof ItemWeaponInstance && (item.getEnLevel() + rnd) > item.getItem().getmaxEnchant() && item.getItem().getmaxEnchant() > 0 )  {
			
				if (item.isEquipped()) {
					item.setEquipped(false);
					item.toOption(cha, false);
						
					item.setEnLevel(item.getItem().getmaxEnchant() );
					
					item.setEquipped(true);
					item.toOption(cha, true);
					return item.getItem().getmaxEnchant();
					
				} else {
					item.setEnLevel(item.getItem().getmaxEnchant() );
					return item.getItem().getmaxEnchant();
				}	
					
			}
			if (item instanceof ItemWeaponInstance && (item.getEnLevel() + rnd) > Lineage.item_enchant_weapon_max   )  {
			
				if (item.isEquipped()) {
					item.setEquipped(false);
					item.toOption(cha, false);
						
					item.setEnLevel(Lineage.item_enchant_weapon_max);
					
					item.setEquipped(true);
					item.toOption(cha, true);
				} else {
					item.setEnLevel(Lineage.item_enchant_weapon_max);
				}			
			} else if (!item.isAcc() && item instanceof ItemArmorInstance && (item.getEnLevel() + rnd) > Lineage.item_enchant_armor_max) {
				if (item.isEquipped()) {
					item.setEquipped(false);
					item.toOption(cha, false);
					
					item.setEnLevel(Lineage.item_enchant_armor_max);
					
					item.setEquipped(true);
					item.toOption(cha, true);
				} else {
					item.setEnLevel(Lineage.item_enchant_armor_max);
				}		
			} else if (item.isAcc() && (item.getEnLevel() + rnd) > Lineage.item_enchant_accessory_max) {
				if (item.isEquipped()) {
					item.setEquipped(false);
					item.toOption(cha, false);
					
					item.setEnLevel(Lineage.item_enchant_armor_max);
					
					item.setEquipped(true);
					item.toOption(cha, true);
				} else {
					item.setEnLevel(Lineage.item_enchant_armor_max);
				}		
			} else {
				if (item.isEquipped()) {
					item.setEquipped(false);
					item.toOption(cha, false);
					
					item.setEnLevel(item.getEnLevel() + rnd);
					
					item.setEquipped(true);
					item.toOption(cha, true);
				} else {
					item.setEnLevel(item.getEnLevel() + rnd);
				}
			}

			if (item.getItem().getSafeEnchant() < item.getEnLevel() && rnd > 0)
				itemName.append(Util.getItemNameToString(item, item.getCount()));
			
			if (Lineage.server_version <= 144) {
				cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), item));
				cha.toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), item));
			} else {
				cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
			}
			
			// \f1%0%s %2 강렬하게 %1 빛났지만 다행히 아무 일도 없었습니다.
			if (rnd == 0) {
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 160, EnMsg[0], EnMsg[1], EnMsg[2]));
				return -125;
			}
			
			// \f1%0%s %2 %1 빛납니다.
			if (rnd != 0)
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 161, EnMsg[0], EnMsg[1], EnMsg[2]));
			
			if ((item.getItem().getType2().equalsIgnoreCase("ring") || item.getItem().getType2().equalsIgnoreCase("necklace")
					|| item.getItem().getType2().equalsIgnoreCase("belt")) && rnd == -1)
				return -126;
			
			if (isEnchant && rnd > 0) {
				ItemDropMessageDatabase.sendMessageEn(cha, item, true);
			}
		} else {
			rnd = 0;
			// \f1%0%s %2 강렬하게 %1 빛나더니 증발되어 사라집니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 164, EnMsg[0], EnMsg[1], EnMsg[2]));
			
			ItemDropMessageDatabase.sendMessageEn(cha, item, false);
		}

		long time = System.currentTimeMillis();
		String timeString = Util.getLocaleString(time, true);
		
		// log
		if (isEnchant) {
	
			
			Log.appendItem(cha, "type|인첸트성공", String.format("item_name|%s", item_name), String.format("item_objid|%d", item_objid), String.format("scroll_name|%s", toStringDB()),
					String.format("scroll_objid|%d", getObjectId()), String.format("scroll_bress|%d", getBless()), String.format("enchant_value|%d", rnd));
			
			if (!Common.system_config_console && item.getItem().getSafeEnchant() < item.getEnLevel() && rnd > 0) {
				String log = String.format("[%s] [인첸트 성공]\t [캐릭터: %s]\t [아이템: %s]\t [주문서: %s]\t [인첸증가: %d]", timeString, cha.getName(), Util.getItemNameToString(item, item.getCount()), Util.getItemNameToString(this, getCount()), rnd);
				
				GuiMain.display.asyncExec(new Runnable() {
					public void run() {
						GuiMain.getViewComposite().getEnchantComposite().toLog(log);
					}
				});
			}
		} else {
			if (rnd == 0) {
				EnchantLostItemDatabase.append(cha, item, this);
			}
		
			Log.appendItem(cha, "type|인첸트실패", String.format("item_name|%s", item_name), String.format("item_objid|%d", item_objid), String.format("scroll_name|%s", toStringDB()),
					String.format("scroll_objid|%d", getObjectId()), String.format("scroll_bress|%d", getBless()));
			
			if (!Common.system_config_console) {
				String log = String.format("[%s] [인첸트 실패]\t [캐릭터: %s]\t [아이템: %s]\t [주문서: %s]", timeString, cha.getName(), Util.getItemNameToString(item, item.getCount()), Util.getItemNameToString(this, getCount()));
				
				GuiMain.display.asyncExec(new Runnable() {
					public void run() {
						GuiMain.getViewComposite().getEnchantComposite().toLog(log);
					}
				});
			}
		}

		return rnd;
	}

}
