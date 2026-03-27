package kuberaitem;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.DeadLostItem;
import lineage.bean.database.EnchantLostItem;
import lineage.bean.database.Item;
import lineage.database.DeadLostItemDatabase;
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

public class DeadRecovery extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new DeadRecovery();
		return item;
	}

	public void showHtml(PcInstance pc) {
		if (pc.getInventory() != null) {
			if (pc.DeadRecovery == null)
				pc.DeadRecovery = new long[100];

			List<String> msg = new ArrayList<String>();
			List<DeadLostItem> list = DeadLostItemDatabase.find(pc);

			int idx = 0;
			for (DeadLostItem el : list) {
				if (idx > pc.DeadRecovery.length - 1) {
					break;
				}
				
				if (el != null) {
					msg.add(Util.getLocaleString(el.getLost_time(), true));
					msg.add(String.format("%s", DeadLostItemDatabase.getStringName(el)));
					pc.DeadRecovery[idx] = el.getItem_objId();
					idx++;
				}
			}
			
			for (int i = 0; i < pc.DeadRecovery.length * 2; i++)
				msg.add(" ");
			
			if (list.size() < 1) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "deadRecovery0", null, msg));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "deadRecovery", null, msg));
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
					DeadLostItem el = DeadLostItemDatabase.지급(pc, pc.DeadRecovery[index]);
					
					if (el != null) {
						Item item = ItemDatabase.find(el.getItem_name());
						
						if (item != null) {
							String 재료 = getItem().getName();
							int 재료수량 = 1;
							ItemInstance 제거할아이템 = null;
							
							if (item.getSafeEnchant() <= el.getEn_level()) {
								if (item.getType1().equalsIgnoreCase("weapon")) {
									
									재료 = "드랍 복구 주문서";
									재료수량 = 1;
									
								} else if (item.getType1().equalsIgnoreCase("armor") && !item.isAcc()) {
									
									재료 = "드랍 복구 주문서";
									재료수량 = 1;
								} else if (item.isAcc()) {
									재료 = "드랍 복구 주문서";
									재료수량 = 1;
									
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
								if (DeadLostItemDatabase.deleteDB(el)) {
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
									
									String msg = DeadLostItemDatabase.getStringName(el);
									ChattingController.toChatting(pc, String.format("\\fR'%s' 복구 완료!", msg), Lineage.CHATTING_MODE_MESSAGE);
									DeadLostItemDatabase.dead_lost_item_log(el);
						
									if (!Common.system_config_console) {
										long time = System.currentTimeMillis();
										String timeString = Util.getLocaleString(time, true);
										String lostTime = Util.getLocaleString(el.getLost_time(), true);
										String log = String.format("[%s]\t [캐릭터: %s]\t [아이템: %s]\t [잃은시간: %s]", timeString, pc.getName(), msg, lostTime);
										
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
	

}
