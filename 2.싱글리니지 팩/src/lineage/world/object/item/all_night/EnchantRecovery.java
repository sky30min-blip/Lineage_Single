package lineage.world.object.item.all_night;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.EnchantLostItem;
import lineage.bean.database.Item;
import lineage.database.EnchantLostItemDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class EnchantRecovery extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new EnchantRecovery();
		return item;
	}

	public void showHtml(PcInstance pc) {
		if (pc.getInventory() != null) {
			if (pc.enchantRecovery == null)
				pc.enchantRecovery = new long[100];

			List<String> msg = new ArrayList<String>();
			List<EnchantLostItem> list = EnchantLostItemDatabase.find(pc);

			int idx = 0;
			for (EnchantLostItem el : list) {
				if (idx > pc.enchantRecovery.length - 1) {
					break;
				}
				
				if (el != null) {
					msg.add(Util.getLocaleString(el.getLost_time(), true));
					msg.add(String.format("%s", EnchantLostItemDatabase.getStringName(el)));
					pc.enchantRecovery[idx] = el.getItem_objId();
					idx++;
				}
			}
			
			for (int i = 0; i < pc.enchantRecovery.length * 2; i++)
				msg.add(" ");
			
			if (list.size() < 1) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "enRecovery0", null, msg));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "enRecovery", null, msg));
			}
		}
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PcInstance) {
			showHtml((PcInstance) cha);
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (pc.getInventory() != null) {
			if (action.contains("recovery-")) {
				try {
					int index = Integer.valueOf(action.replace("recovery-", "").trim());
					EnchantLostItem el = EnchantLostItemDatabase.지급(pc, pc.enchantRecovery[index]);
					
					if (el != null) {
						Item item = ItemDatabase.find(el.getItem_name());
						
						if (item != null) {
							String 재료 = getItem().getName();
							int 재료수량 = 1;
							ItemInstance 제거할아이템 = null;
							
							if (item.getSafeEnchant() <= el.getEn_level()) {
								if (item.getType1().equalsIgnoreCase("weapon")) {
									switch (item.getSafeEnchant()) {
									case 0:
										switch (el.getEn_level()) {
										case 0:
											재료 = Lineage.weapon_safe_0_0_recovery_item;
											재료수량 = Lineage.weapon_safe_0_0_recovery_item_count;
											break;
										case 1:
											재료 = Lineage.weapon_safe_0_1_recovery_item;
											재료수량 = Lineage.weapon_safe_0_1_recovery_item_count;
											break;
										case 2:
											재료 = Lineage.weapon_safe_0_2_recovery_item;
											재료수량 = Lineage.weapon_safe_0_2_recovery_item_count;
											break;
										case 3:
											재료 = Lineage.weapon_safe_0_3_recovery_item;
											재료수량 = Lineage.weapon_safe_0_3_recovery_item_count;
											break;
										case 4:
											재료 = Lineage.weapon_safe_0_4_recovery_item;
											재료수량 = Lineage.weapon_safe_0_4_recovery_item_count;
											break;
										case 5:
											재료 = Lineage.weapon_safe_0_5_recovery_item;
											재료수량 = Lineage.weapon_safe_0_5_recovery_item_count;
											break;
										case 6:
											재료 = Lineage.weapon_safe_0_6_recovery_item;
											재료수량 = Lineage.weapon_safe_0_6_recovery_item_count;
											break;
										case 7:
											재료 = Lineage.weapon_safe_0_7_recovery_item;
											재료수량 = Lineage.weapon_safe_0_7_recovery_item_count;
											break;
										case 8:
											재료 = Lineage.weapon_safe_0_8_recovery_item;
											재료수량 = Lineage.weapon_safe_0_8_recovery_item_count;
											break;
										case 9:
											재료 = Lineage.weapon_safe_0_9_recovery_item;
											재료수량 = Lineage.weapon_safe_0_9_recovery_item_count;
											break;
										case 10:
											재료 = Lineage.weapon_safe_0_10_recovery_item;
											재료수량 = Lineage.weapon_safe_0_10_recovery_item_count;
											break;
										}
										break;
									case 6:
										switch (el.getEn_level()) {
										case 6:
											재료 = Lineage.weapon_safe_6_6_recovery_item;
											재료수량 = Lineage.weapon_safe_6_6_recovery_item_count;
											break;
										case 7:
											재료 = Lineage.weapon_safe_6_7_recovery_item;
											재료수량 = Lineage.weapon_safe_6_7_recovery_item_count;
											break;
										case 8:
											재료 = Lineage.weapon_safe_6_8_recovery_item;
											재료수량 = Lineage.weapon_safe_6_8_recovery_item_count;
											break;
										case 9:
											재료 = Lineage.weapon_safe_6_9_recovery_item;
											재료수량 = Lineage.weapon_safe_6_9_recovery_item_count;
											break;
										case 10:
											재료 = Lineage.weapon_safe_6_10_recovery_item;
											재료수량 = Lineage.weapon_safe_6_10_recovery_item_count;
											break;
										case 11:
											재료 = Lineage.weapon_safe_6_11_recovery_item;
											재료수량 = Lineage.weapon_safe_6_11_recovery_item_count;
											break;
										case 12:
											재료 = Lineage.weapon_safe_6_12_recovery_item;
											재료수량 = Lineage.weapon_safe_6_12_recovery_item_count;
											break;
										case 13:
											재료 = Lineage.weapon_safe_6_13_recovery_item;
											재료수량 = Lineage.weapon_safe_6_13_recovery_item_count;
											break;
										case 14:
											재료 = Lineage.weapon_safe_6_14_recovery_item;
											재료수량 = Lineage.weapon_safe_6_14_recovery_item_count;
											break;
										case 15:
											재료 = Lineage.weapon_safe_6_15_recovery_item;
											재료수량 = Lineage.weapon_safe_6_15_recovery_item_count;
											break;
										}
										break;
									}
								} else if (item.getType1().equalsIgnoreCase("armor") && !item.isAcc()) {
									switch (item.getSafeEnchant()) {
									case 0:
										switch (el.getEn_level()) {
										case 0:
											재료 = Lineage.armor_safe_0_0_recovery_item;
											재료수량 = Lineage.armor_safe_0_0_recovery_item_count;
											break;
										case 1:
											재료 = Lineage.armor_safe_0_1_recovery_item;
											재료수량 = Lineage.armor_safe_0_1_recovery_item_count;
											break;
										case 2:
											재료 = Lineage.armor_safe_0_2_recovery_item;
											재료수량 = Lineage.armor_safe_0_2_recovery_item_count;
											break;
										case 3:
											재료 = Lineage.armor_safe_0_3_recovery_item;
											재료수량 = Lineage.armor_safe_0_3_recovery_item_count;
											break;
										case 4:
											재료 = Lineage.armor_safe_0_4_recovery_item;
											재료수량 = Lineage.armor_safe_0_4_recovery_item_count;
											break;
										case 5:
											재료 = Lineage.armor_safe_0_5_recovery_item;
											재료수량 = Lineage.armor_safe_0_5_recovery_item_count;
											break;
										case 6:
											재료 = Lineage.armor_safe_0_6_recovery_item;
											재료수량 = Lineage.armor_safe_0_6_recovery_item_count;
											break;
										case 7:
											재료 = Lineage.armor_safe_0_7_recovery_item;
											재료수량 = Lineage.armor_safe_0_7_recovery_item_count;
											break;
										case 8:
											재료 = Lineage.armor_safe_0_8_recovery_item;
											재료수량 = Lineage.armor_safe_0_8_recovery_item_count;
											break;
										case 9:
											재료 = Lineage.armor_safe_0_9_recovery_item;
											재료수량 = Lineage.armor_safe_0_9_recovery_item_count;
											break;
										case 10:
											재료 = Lineage.armor_safe_0_10_recovery_item;
											재료수량 = Lineage.armor_safe_0_10_recovery_item_count;
											break;
										}
										break;
									case 4:
										switch (el.getEn_level()) {
										case 4:
											재료 = Lineage.armor_safe_4_4_recovery_item;
											재료수량 = Lineage.armor_safe_4_4_recovery_item_count;
											break;
										case 5:
											재료 = Lineage.armor_safe_4_5_recovery_item;
											재료수량 = Lineage.armor_safe_4_5_recovery_item_count;
											break;
										case 6:
											재료 = Lineage.armor_safe_4_6_recovery_item;
											재료수량 = Lineage.armor_safe_4_6_recovery_item_count;
											break;
										case 7:
											재료 = Lineage.armor_safe_4_7_recovery_item;
											재료수량 = Lineage.armor_safe_4_7_recovery_item_count;
											break;
										case 8:
											재료 = Lineage.armor_safe_4_8_recovery_item;
											재료수량 = Lineage.armor_safe_4_8_recovery_item_count;
											break;
										case 9:
											재료 = Lineage.armor_safe_4_9_recovery_item;
											재료수량 = Lineage.armor_safe_4_9_recovery_item_count;
											break;
										case 10:
											재료 = Lineage.armor_safe_4_10_recovery_item;
											재료수량 = Lineage.armor_safe_4_10_recovery_item_count;
											break;
										case 11:
											재료 = Lineage.armor_safe_4_11_recovery_item;
											재료수량 = Lineage.armor_safe_4_11_recovery_item_count;
											break;
										case 12:
											재료 = Lineage.armor_safe_4_12_recovery_item;
											재료수량 = Lineage.armor_safe_4_12_recovery_item_count;
											break;
										}
										break;
									case 6:
										switch (el.getEn_level()) {
										case 6:
											재료 = Lineage.armor_safe_6_6_recovery_item;
											재료수량 = Lineage.armor_safe_6_6_recovery_item_count;
											break;
										case 7:
											재료 = Lineage.armor_safe_6_7_recovery_item;
											재료수량 = Lineage.armor_safe_6_7_recovery_item_count;
											break;
										case 8:
											재료 = Lineage.armor_safe_6_8_recovery_item;
											재료수량 = Lineage.armor_safe_6_8_recovery_item_count;
											break;
										case 9:
											재료 = Lineage.armor_safe_6_9_recovery_item;
											재료수량 = Lineage.armor_safe_6_9_recovery_item_count;
											break;
										case 10:
											재료 = Lineage.armor_safe_6_10_recovery_item;
											재료수량 = Lineage.armor_safe_6_10_recovery_item_count;
											break;
										case 11:
											재료 = Lineage.armor_safe_6_11_recovery_item;
											재료수량 = Lineage.armor_safe_6_11_recovery_item_count;
											break;
										case 12:
											재료 = Lineage.armor_safe_6_12_recovery_item;
											재료수량 = Lineage.armor_safe_6_12_recovery_item_count;
											break;
										}
										break;
									}
								} else if (item.isAcc()) {
									switch (el.getEn_level()) {
									case 0:
										재료 = Lineage.acc_0_recovery_item;
										재료수량 = Lineage.acc_0_recovery_item_count;
										break;
									case 1:
										재료 = Lineage.acc_1_recovery_item;
										재료수량 = Lineage.acc_1_recovery_item_count;
										break;
									case 2:
										재료 = Lineage.acc_2_recovery_item;
										재료수량 = Lineage.acc_2_recovery_item_count;
										break;
									case 3:
										재료 = Lineage.acc_3_recovery_item;
										재료수량 = Lineage.acc_3_recovery_item_count;
										break;
									case 4:
										재료 = Lineage.acc_4_recovery_item;
										재료수량 = Lineage.acc_4_recovery_item_count;
										break;
									case 5:
										재료 = Lineage.acc_5_recovery_item;
										재료수량 = Lineage.acc_5_recovery_item_count;
										break;
									case 6:
										재료 = Lineage.acc_6_recovery_item;
										재료수량 = Lineage.acc_6_recovery_item_count;
										break;
									case 7:
										재료 = Lineage.acc_7_recovery_item;
										재료수량 = Lineage.acc_7_recovery_item_count;
										break;
									case 8:
										재료 = Lineage.acc_8_recovery_item;
										재료수량 = Lineage.acc_8_recovery_item_count;
										break;
									case 9:
										재료 = Lineage.acc_9_recovery_item;
										재료수량 = Lineage.acc_9_recovery_item_count;
										break;
									case 10:
										재료 = Lineage.acc_10_recovery_item;
										재료수량 = Lineage.acc_10_recovery_item_count;
										break;
									}
								}
							}
							
							if (재료 != null) {
								for (ItemInstance i : pc.getInventory().getList()) {
									if (i != null && i.getItem() != null && !i.isEquipped() && i.getItem().getName().equalsIgnoreCase(재료) && i.getCount() >= 재료수량) {
										제거할아이템 = i;
										break;
									}
								}
							}
							
							if (제거할아이템 != null) {
								if (EnchantLostItemDatabase.deleteDB(el)) {
									// 재료 제거
									pc.getInventory().count(제거할아이템, 제거할아이템.getCount() - 재료수량, true);
									
									el.set지급여부(true);
									
									ItemInstance temp = pc.getInventory().find(item.getName(), el.getBless(), item.isPiles());

									if (temp != null && (temp.getBless() != el.getBless() || temp.getEnLevel() != el.getEn_level()))
										temp = null;

									if (temp == null) {
										// 겹칠수 있는 아이템이 존재하지 않을경우.
										if (item.isPiles()) {
											temp = ItemDatabase.newInstance(item);
											temp.setObjectId(el.getItem_objId());
											temp.setBless(el.getBless());
											temp.setEnLevel(el.getEn_level());
											temp.setCount(el.getCount());
											temp.setDefinite(true);
											pc.getInventory().append(temp, true);
										} else {
											for (int idx = 0; idx < el.getCount(); idx++) {
												temp = ItemDatabase.newInstance(item);
												temp.setObjectId(el.getItem_objId());
												temp.setBless(el.getBless());
												temp.setEnLevel(el.getEn_level());
												temp.setDefinite(true);
												pc.getInventory().append(temp, true);
											}
										}
									} else {
										// 겹치는 아이템이 존재할 경우.
										pc.getInventory().count(temp, temp.getCount() + el.getCount(), true);
									}
									
									String msg = EnchantLostItemDatabase.getStringName(el);
									ChattingController.toChatting(pc, String.format("\\fR'%s' 복구 완료!", msg), Lineage.CHATTING_MODE_MESSAGE);
									
									if (Lineage.is_recovery_scroll && el.getScroll_name() != null) {
										Item scroll = ItemDatabase.find(el.getScroll_name());
										
										if (scroll != null) {
											ItemInstance temp_scroll = pc.getInventory().find(scroll.getName(), el.getScroll_bless(), scroll.isPiles());

											if (temp_scroll != null && temp_scroll.getBless() != el.getScroll_bless())
												temp_scroll = null;

											if (temp_scroll == null) {
												// 겹칠수 있는 아이템이 존재하지 않을경우.
												if (scroll.isPiles()) {
													temp_scroll = ItemDatabase.newInstance(scroll);
													temp_scroll.setObjectId(ServerDatabase.nextItemObjId());
													temp_scroll.setBless(el.getScroll_bless());
													temp_scroll.setEnLevel(0);
													temp_scroll.setCount(1);
													temp_scroll.setDefinite(true);
													pc.getInventory().append(temp_scroll, true);
												} else {
													temp_scroll = ItemDatabase.newInstance(scroll);
													temp_scroll.setObjectId(ServerDatabase.nextItemObjId());
													temp_scroll.setBless(el.getScroll_bless());
													temp_scroll.setEnLevel(0);
													temp_scroll.setDefinite(true);
													pc.getInventory().append(temp_scroll, true);
												}
											} else {
												// 겹치는 아이템이 존재할 경우.
												pc.getInventory().count(temp_scroll, temp_scroll.getCount() + 1, true);
											}
											
											ChattingController.toChatting(pc, String.format("\\fR'%s' 복구 완료!", getScrollName(el)), Lineage.CHATTING_MODE_MESSAGE);
										}
									}
									
									if (!Common.system_config_console) {
										long time = System.currentTimeMillis();
										String timeString = Util.getLocaleString(time, true);
										String lostTime = Util.getLocaleString(el.getLost_time(), true);
										String log = String.format("[%s]\t [캐릭터: %s]\t [아이템: %s]\t [주문서: %s]\t [잃은시간: %s]", timeString, pc.getName(), msg, getScrollName(el), lostTime);
										
										GuiMain.display.asyncExec(new Runnable() {
											public void run() {
												GuiMain.getViewComposite().getEnchantLostItemComposite().toLog(log);
											}
										});
									}
								}
							} else {
								ChattingController.toChatting(pc, String.format("\\fR[아이템 복구] \\fY%s(%d) \\fR이(가) 필요합니다.", 재료, 재료수량), Lineage.CHATTING_MODE_MESSAGE);
							}
						}
					}
				} catch (Exception e) {
					
				}
			}
			
			showHtml(pc);
		}
	}
	
	public String getScrollName(EnchantLostItem el) {
		if (el.getScroll_name() != null) {
			return String.format("%s%s",  el.getScroll_bless() == 1 ? "" : el.getScroll_bless() == 0 ? "(축)" : "(저주)", el.getScroll_name());
		} else {
			return "";
		}
	}
}
