package lineage.world.object.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import lineage.network.packet.server.S_Disconnect;
import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.bean.lineage.Kingdom;
import lineage.database.AccountDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ShopBuy;
import lineage.network.packet.server.S_ShopSell;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.item.RaceTicket;

public class ShopInstance extends object {

	protected Npc npc;
	// 성 정보
	protected Kingdom kingdom;

	public ShopInstance(Npc npc) {
		this.npc = npc;
		kingdom = null;
	}

	public Npc getNpc() {
		return npc;
	}

	public void setNpc(Npc npc) {
		this.npc = npc;
	}

	/**
	 * 현재 물가 추출.
	 * 
	 * @return
	 */
	public int getTax() {
		if (Lineage.shop_no_tax_npc != null && getNpc() != null && Lineage.shop_no_tax_npc.contains(getNpc().getName())) {
			return 0;
		}
		
		return kingdom == null ? 0 : kingdom.getTaxRate();
	}

	/**
	 * 세금으로인한 차액을 공금에 추가하기.
	 * 
	 * @param price
	 */
	public void addTax(int price) {
		if (kingdom != null && Lineage.add_tax)
			kingdom.toTax(price, true, "shop");
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		//자동판매 초기화
		pc.isAutoSellAdding = false;
		pc.isAutoSellDeleting = false;
		
		if (action.equalsIgnoreCase("buy")) {
			pc.toSender(S_ShopBuy.clone(BasePacketPooling.getPool(S_ShopBuy.class), this));
		} else if (action.equalsIgnoreCase("sell")) {
			List<ItemInstance> sell_list = new ArrayList<ItemInstance>();
			//쿠베라 상점 매입 부분 개편
			for (Shop s : npc.getShop_list()) {
				// 판매할 수 있도록 설정된 목록만 처리.
				if (s.isItemSell()) {
					List<ItemInstance> search_list = new ArrayList<ItemInstance>();
					pc.getInventory().findDbName(s.getItemName(), search_list);
					for (ItemInstance item : search_list) {
						if (!item.isEquipped() && item.getItem().isSell() && (s.getItemEnLevel() == 0 || s.getItemEnLevel() == item.getEnLevel())) {
							//
							if (isSellAdd(item) && !sell_list.contains(item) && item.getEnLevel() == s.getItemEnLevel())
								sell_list.add(item);
						}
					}
				}
			}
			if (sell_list.size() > 0){
//			    System.out.println("Items in sellList:");
//	        for (ItemInstance item : sell_list) {
//	            System.out.println("Item: " + item.getName() + " (EnLevel: " + item.getEnLevel() + ")");
//	        }
				pc.toSender(S_ShopSell.clone(BasePacketPooling.getPool(S_ShopSell.class), this, sell_list));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "nosell"));
			}
		} else if (action.indexOf("3") > 0 || action.indexOf("6") > 0 || action.indexOf("7") > 0) {
			List<String> list_html = new ArrayList<String>();
			list_html.add(String.valueOf(getTax()));
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, action, null, list_html));
		}
	}

	@Override
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp) {
		switch (cbp.readC()) {
		case 0: // 상점 구입
			toBuy(pc, cbp);
			break;
		case 1: // 상점 판매
			toSell(pc, cbp);
			break;
		}
	}

	/**
	 * 상점 구매
	 */
	protected void toBuy(PcInstance pc, ClientBasePacket cbp) {


		long count = cbp.readH();
		if (count > 0 && count <= 100) {
		
			
			for (int j = 0; j < count; ++j) {
				
				long item_idx = cbp.readD();
				long item_count = cbp.readD();
			
				if (item_count > 0 && item_count <= 1000) {
					Shop s = npc.findShop(item_idx);
					if (Lineage.open_wait && pc.getGm() == 0 && !s.getNpcName().equalsIgnoreCase("")) {
						ChattingController.toChatting(pc, "[오픈 대기] 상점을 이용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					} 
					
					if (s != null) {
						if (!s.isItemBuy()) {
							return;
						}
						
						
						Item i = ItemDatabase.find(s.getItemName());
						int shop_price = 0;
						
						if (s.getPrice() != 0) {
							shop_price = getTaxPrice(s.getPrice(), false);
						} else {
							if ((i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor"))
									&& !i.getType2().equalsIgnoreCase("necklace") && !i.getType2().equalsIgnoreCase("ring") && !i.getType2().equalsIgnoreCase("belt")) {
								if (i.getType1().equalsIgnoreCase("weapon"))
									shop_price = getTaxPrice(i.getShopPrice() * s.getItemCount() + (s.getItemEnLevel() * ItemDatabase.find(244).getShopPrice()), false);
								else
									shop_price = getTaxPrice(i.getShopPrice() * s.getItemCount() + (s.getItemEnLevel() * ItemDatabase.find(249).getShopPrice()), false);
							} else {
								if ((i.getName().equalsIgnoreCase(Lineage.scroll_dane_fools) || i.getName().equalsIgnoreCase(Lineage.scroll_zel_go_mer) || i.getName().equalsIgnoreCase(Lineage.scroll_orim)) && (s.getItemBress() == 0 || s.getItemBress() == 2))
									shop_price = getTaxPrice(s.getItemBress() == 0 ? ItemDatabase.find(i.getNameIdNumber()).getShopPrice() * Lineage.sell_bless_item_rate : ItemDatabase.find(i.getNameIdNumber()).getShopPrice() * Lineage.sell_curse_item_rate, false);
								else
									shop_price = getTaxPrice(i.getShopPrice() * s.getItemCount(), false);
							}
						}
						
						// 아이템 갯수에 맞게 갯수 재 설정.
						long new_item_count = item_count * s.getItemCount();
						//
						if (pc.getInventory().isAppend(i, count, i.isPiles() ? 1 : new_item_count)) {

							
							
							long check2 = AccountDatabase.userpointcheck(pc.getAccountId());
							long check1 = shop_price * item_count;
							if (s.getAdenType().equalsIgnoreCase("포인트")) {
								if (check1 > check2) {
									pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 776, s.getAdenType()));
									break;
								}else{
									AccountDatabase.userpointbuy(pc.getAccountId(), shop_price * item_count);
									ItemInstance temp = pc.getInventory().find(s.getItemName(), s.getItemBress(), i.isPiles());
									if (temp == null) {
										// 겹칠수 있는 아이템이 존재하지 않을경우.
										if (i.isPiles()) {
											temp = ItemDatabase.newInstance(i);
											temp.setObjectId(ServerDatabase.nextItemObjId());
											temp.setCount(new_item_count);
											temp.setEnLevel(s.getItemEnLevel());
											temp.setEnFire(s.getInvItemEnFire());
											temp.setEnWater(s.getInvItemEnWater());
											temp.setEnWind(s.getInvItemEnWind());
											temp.setEnEarth(s.getInvItemEnEarth());
											temp.setBless(s.getItemBress());

											int daysToAdd = 0;

											if (s.getItemName().contains("1일")) {
											    daysToAdd = 1;
											} else if (s.getItemName().contains("3일")) {
											    daysToAdd = 3;
											} else if (s.getItemName().contains("7일")) {
											    daysToAdd = 7;
											} else if (s.getItemName().contains("30일")) {
											    daysToAdd = 30;
											} else {
											    temp.setItemTimek(s.getItemTimeK());
											}

											if (daysToAdd > 0) {
											    LocalDateTime now = LocalDateTime.now();
											    LocalDateTime futureDate = now.plusDays(daysToAdd);

											    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
											    String formattedDate = futureDate.format(formatter);

											    // 날짜를 long으로 변환하기 전에 날짜 유효성을 확인합니다.
											    try {
											        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
											        LocalDateTime parsedDate = LocalDateTime.parse(formattedDate, parser);
											        
											        // 파싱된 날짜를 long으로 변환합니다.
											        long to7 = parsedDate.toInstant(ZoneOffset.UTC).toEpochMilli();
											        String stringValue = Long.toString(to7);

											        temp.setItemTimek(stringValue);

											        // 사용 가능한 기간 메시지
											        DateTimeFormatter messageFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
											        String message = temp.getItem().getName() + " 아이템은 ";
											        String message2 = futureDate.format(messageFormatter) + "까지 사용 가능합니다.";
											        ChattingController.toChatting(pc, message, Lineage.CHATTING_MODE_MESSAGE);
											        ChattingController.toChatting(pc, message2, Lineage.CHATTING_MODE_MESSAGE);
											    } catch (DateTimeParseException e) {
											        // 날짜 문자열 파싱 오류 처리
											        // 날짜 문자열이 유효하지 않을 때 이 부분이 실행됩니다.
											        // 오류를 기록하거나 처리 방법을 결정합니다.
											    }
											}
											if (i.getName().equalsIgnoreCase("신성한 엘름의 축복"))
												temp.setDynamicMr(s.getItemEnLevel() > 4 ? (s.getItemEnLevel() - 4) * i.getEnchantMr() : 0);
											else
												temp.setDynamicMr(s.getItemEnLevel() * i.getEnchantMr());
											temp.setDynamicMr(s.getItemEnLevel() * i.getEnchantMr());
											temp.setDynamicStunDefence(s.getItemEnLevel() * i.getEnchantStunDefense());
											temp.setDynamicStunHit(s.getItemEnLevel() * i.getEnchantStunHit());
											temp.setDynamicSp(s.getItemEnLevel() * i.getEnchantSp());
											temp.setDynamicReduction(s.getItemEnLevel() * i.getEnchantReduction());
											temp.setDynamicIgnoreReduction(s.getItemEnLevel() * i.getEnchantIgnoreReduction());
											temp.setDynamicSwordCritical(s.getItemEnLevel() * i.getEnchantSwordCritical());
											temp.setDynamicBowCritical(s.getItemEnLevel() * i.getEnchantBowCritical());
											temp.setDynamicMagicCritical(s.getItemEnLevel() * i.getEnchantMagicCritical());
											temp.setDynamicPvpDmg(s.getItemEnLevel() * i.getEnchantPvpDamage());
											temp.setDynamicPvpReduction(s.getItemEnLevel() * i.getEnchantPvpReduction());
											temp.setDefinite(true);
											pc.getInventory().append(temp, true);
											//
											Log.appendItem(pc, "type|상점구입", String.format("npc_name|%s", getNpc().getName()), String.format("item_name|%s", temp.toStringDB()),
													String.format("item_objid|%d", temp.getObjectId()), String.format("count|%d", item_count), String.format("shop_uid|%d", s.getUid()),
													String.format("shop_count|%d", s.getItemCount()));
										} else {
											for (int k = 0; k < new_item_count; ++k) {
												temp = ItemDatabase.newInstance(i);
												temp.setObjectId(ServerDatabase.nextItemObjId());
												// 겜블 아이템은 겹칠일이 없어서 여기다가 넣음.
												if (s.isGamble()) {
													temp.setEnLevel(getGambleEnLevel());
												} else {
													temp.setEnLevel(s.getItemEnLevel());
													temp.setEnFire(s.getInvItemEnFire()); 
													temp.setEnWater(s.getInvItemEnWater()); 
													temp.setEnWind(s.getInvItemEnWind()); 
													temp.setEnEarth(s.getInvItemEnEarth());      
													temp.setDefinite(true);
												}
												
												int daysToAdd = 0;

												if (s.getItemName().contains("1일")) {
												    daysToAdd = 1;
												} else if (s.getItemName().contains("3일")) {
												    daysToAdd = 3;
												} else if (s.getItemName().contains("7일")) {
												    daysToAdd = 7;
												} else if (s.getItemName().contains("30일")) {
												    daysToAdd = 30;
												} else {
												    temp.setItemTimek(s.getItemTimeK());
												}

												if (daysToAdd > 0) {
												    LocalDateTime now = LocalDateTime.now();
												    LocalDateTime futureDate = now.plusDays(daysToAdd);

												    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
												    String formattedDate = futureDate.format(formatter);

												    // 날짜를 long으로 변환하기 전에 날짜 유효성을 확인합니다.
												    try {
												        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
												        LocalDateTime parsedDate = LocalDateTime.parse(formattedDate, parser);
												        
												        // 파싱된 날짜를 long으로 변환합니다.
												        long to7 = parsedDate.toInstant(ZoneOffset.UTC).toEpochMilli();
												        String stringValue = Long.toString(to7);

												        temp.setItemTimek(stringValue);

												        // 사용 가능한 기간 메시지
												        DateTimeFormatter messageFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
												        String message = temp.getItem().getName() + " 아이템은 ";
												        String message2 = futureDate.format(messageFormatter) + "까지 사용 가능합니다.";
												        ChattingController.toChatting(pc, message, Lineage.CHATTING_MODE_MESSAGE);
												        ChattingController.toChatting(pc, message2, Lineage.CHATTING_MODE_MESSAGE);
												    } catch (DateTimeParseException e) {
												        // 날짜 문자열 파싱 오류 처리
												        // 날짜 문자열이 유효하지 않을 때 이 부분이 실행됩니다.
												        // 오류를 기록하거나 처리 방법을 결정합니다.
												    }
												}
												temp.setBless(s.getItemBress());
												temp.setEnFire(s.getInvItemEnFire()); 
												temp.setEnWater(s.getInvItemEnWater()); 
												temp.setEnWind(s.getInvItemEnWind()); 
												temp.setEnEarth(s.getInvItemEnEarth()); 
												if (i.getName().equalsIgnoreCase("신성한 엘름의 축복"))
													temp.setDynamicMr(s.getItemEnLevel() > 4 ? (s.getItemEnLevel() - 4) * i.getEnchantMr() : 0);
												else
													temp.setDynamicMr(s.getItemEnLevel() * i.getEnchantMr());
												temp.setDynamicStunDefence(s.getItemEnLevel() * i.getEnchantStunDefense());
												temp.setDynamicStunHit(s.getItemEnLevel() * i.getEnchantStunHit());
												temp.setDynamicSp(s.getItemEnLevel() * i.getEnchantSp());
												temp.setDynamicReduction(s.getItemEnLevel() * i.getEnchantReduction());
												temp.setDynamicIgnoreReduction(s.getItemEnLevel() * i.getEnchantIgnoreReduction());
												temp.setDynamicSwordCritical(s.getItemEnLevel() * i.getEnchantSwordCritical());
												temp.setDynamicBowCritical(s.getItemEnLevel() * i.getEnchantBowCritical());
												temp.setDynamicMagicCritical(s.getItemEnLevel() * i.getEnchantMagicCritical());
												temp.setDynamicPvpDmg(s.getItemEnLevel() * i.getEnchantPvpDamage());
												temp.setDynamicPvpReduction(s.getItemEnLevel() * i.getEnchantPvpReduction());
												pc.getInventory().append(temp, true);
												Log.appendItem(pc, "type|상점구입", String.format("npc_name|%s", getNpc().getName()), String.format("item_name|%s", temp.toStringDB()),
														String.format("item_objid|%d", temp.getObjectId()), String.format("count|%d", item_count), String.format("shop_uid|%d", s.getUid()),
														String.format("shop_count|%d", s.getItemCount()));
											}
										}

									} else {
										// 겹치는 아이템이 존재할 경우.
										pc.getInventory().count(temp, temp.getCount() + new_item_count, true);
										//
										Log.appendItem(pc, "type|상점구입", String.format("npc_name|%s", getNpc().getName()), String.format("item_name|%s", s.getItemName()),
												String.format("target_name|%s", temp.toStringDB()), String.format("target_objid|%d", temp.getObjectId()), String.format("count|%d", item_count),
												String.format("shop_uid|%d", s.getUid()), String.format("shop_count|%d", s.getItemCount()));

									}
								}
							}else{
								if (pc.getInventory().isAden(s.getAdenType(), shop_price * item_count, true)) {
									//
									ItemInstance temp = pc.getInventory().find(s.getItemName(), s.getItemBress(), i.isPiles());
									if (temp == null) {
										// 겹칠수 있는 아이템이 존재하지 않을경우.
										if (i.isPiles()) {
											temp = ItemDatabase.newInstance(i);
											temp.setObjectId(ServerDatabase.nextItemObjId());
											temp.setCount(new_item_count);
											temp.setEnLevel(s.getItemEnLevel());
											temp.setEnFire(s.getInvItemEnFire());
											temp.setEnWater(s.getInvItemEnWater());
											temp.setEnWind(s.getInvItemEnWind());
											temp.setEnEarth(s.getInvItemEnEarth());
											temp.setBless(s.getItemBress());

											int daysToAdd = 0;

											if (s.getItemName().contains("1일")) {
											    daysToAdd = 1;
											} else if (s.getItemName().contains("3일")) {
											    daysToAdd = 3;
											} else if (s.getItemName().contains("7일")) {
											    daysToAdd = 7;
											} else if (s.getItemName().contains("30일")) {
											    daysToAdd = 30;
											} else {
											    temp.setItemTimek(s.getItemTimeK());
											}

											if (daysToAdd > 0) {
											    LocalDateTime now = LocalDateTime.now();
											    LocalDateTime futureDate = now.plusDays(daysToAdd);

											    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
											    String formattedDate = futureDate.format(formatter);

											    try {
											        long to7 = Long.parseLong(formattedDate.replace("/", "").replace(" ", "").replace(":", ""));
											        String stringValue = Long.toString(to7);
											        temp.setItemTimek(stringValue);

											        // 사용 가능한 기간 메시지
											        DateTimeFormatter messageFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
											        String message = temp.getItem().getName() + "아이템은";
											        String message2 = futureDate.format(messageFormatter) + "까지 사용 가능합니다.";
											        ChattingController.toChatting(pc, message, Lineage.CHATTING_MODE_MESSAGE);
											        ChattingController.toChatting(pc, message2, Lineage.CHATTING_MODE_MESSAGE);
											    } catch (NumberFormatException e) {
											        // Handle the parsing error, e.g., print an error message or log it
											        System.err.println("Error parsing formattedDate: " + e.getMessage());
											    }
											}
											if (i.getName().equalsIgnoreCase("신성한 엘름의 축복"))
												temp.setDynamicMr(s.getItemEnLevel() > 4 ? (s.getItemEnLevel() - 4) * i.getEnchantMr() : 0);
											else
												temp.setDynamicMr(s.getItemEnLevel() * i.getEnchantMr());
											temp.setDynamicMr(s.getItemEnLevel() * i.getEnchantMr());
											temp.setDynamicStunDefence(s.getItemEnLevel() * i.getEnchantStunDefense());
											temp.setDynamicStunHit(s.getItemEnLevel() * i.getEnchantStunHit());
											temp.setDynamicSp(s.getItemEnLevel() * i.getEnchantSp());
											temp.setDynamicReduction(s.getItemEnLevel() * i.getEnchantReduction());
											temp.setDynamicIgnoreReduction(s.getItemEnLevel() * i.getEnchantIgnoreReduction());
											temp.setDynamicSwordCritical(s.getItemEnLevel() * i.getEnchantSwordCritical());
											temp.setDynamicBowCritical(s.getItemEnLevel() * i.getEnchantBowCritical());
											temp.setDynamicMagicCritical(s.getItemEnLevel() * i.getEnchantMagicCritical());
											temp.setDynamicPvpDmg(s.getItemEnLevel() * i.getEnchantPvpDamage());
											temp.setDynamicPvpReduction(s.getItemEnLevel() * i.getEnchantPvpReduction());
											temp.setDefinite(true);
											pc.getInventory().append(temp, true);
											//
											Log.appendItem(pc, "type|상점구입", String.format("npc_name|%s", getNpc().getName()), String.format("item_name|%s", temp.toStringDB()),
													String.format("item_objid|%d", temp.getObjectId()), String.format("count|%d", item_count), String.format("shop_uid|%d", s.getUid()),
													String.format("shop_count|%d", s.getItemCount()));
										} else {
											for (int k = 0; k < new_item_count; ++k) {
												temp = ItemDatabase.newInstance(i);
												temp.setObjectId(ServerDatabase.nextItemObjId());
												// 겜블 아이템은 겹칠일이 없어서 여기다가 넣음.
												if (s.isGamble()) {
													temp.setEnLevel(getGambleEnLevel());
												} else {
													temp.setEnLevel(s.getItemEnLevel());
													temp.setEnFire(s.getInvItemEnFire()); 
													temp.setEnWater(s.getInvItemEnWater()); 
													temp.setEnWind(s.getInvItemEnWind()); 
													temp.setEnEarth(s.getInvItemEnEarth());      
													temp.setDefinite(true);
												}
												

												int daysToAdd = 0;

												if (s.getItemName().contains("1일")) {
												    daysToAdd = 1;
												} else if (s.getItemName().contains("3일")) {
												    daysToAdd = 3;
												} else if (s.getItemName().contains("7일")) {
												    daysToAdd = 7;
												} else if (s.getItemName().contains("30일")) {
												    daysToAdd = 30;
												} else {
													  temp.setItemTimek(s.getItemTimeK());
												}
											     
												if (daysToAdd > 0) {
												    LocalDateTime now = LocalDateTime.now();
												    LocalDateTime futureDate = now.plusDays(daysToAdd);

												    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
												    String formattedDate = futureDate.format(formatter);
												    long to7 = Long.parseLong(formattedDate.replace("/", "").replace(" ", "").replace(":", ""));
												    String stringValue = Long.toString(to7);

												    temp.setItemTimek(stringValue);

												    // 사용 가능한 기간 메시지
												    DateTimeFormatter messageFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
												    String message = temp.getItem().getName() + "아이템은";
												    String message2 = futureDate.format(messageFormatter) + "까지 사용 가능합니다.";
												    ChattingController.toChatting(pc, message, Lineage.CHATTING_MODE_MESSAGE);
												    ChattingController.toChatting(pc, message2, Lineage.CHATTING_MODE_MESSAGE);
												}
												temp.setBless(s.getItemBress());
												temp.setEnFire(s.getInvItemEnFire()); 
												temp.setEnWater(s.getInvItemEnWater()); 
												temp.setEnWind(s.getInvItemEnWind()); 
												temp.setEnEarth(s.getInvItemEnEarth()); 
												if (i.getName().equalsIgnoreCase("신성한 엘름의 축복"))
													temp.setDynamicMr(s.getItemEnLevel() > 4 ? (s.getItemEnLevel() - 4) * i.getEnchantMr() : 0);
												else
													temp.setDynamicMr(s.getItemEnLevel() * i.getEnchantMr());
												temp.setDynamicStunDefence(s.getItemEnLevel() * i.getEnchantStunDefense());
												temp.setDynamicStunHit(s.getItemEnLevel() * i.getEnchantStunHit());
												temp.setDynamicSp(s.getItemEnLevel() * i.getEnchantSp());
												temp.setDynamicReduction(s.getItemEnLevel() * i.getEnchantReduction());
												temp.setDynamicIgnoreReduction(s.getItemEnLevel() * i.getEnchantIgnoreReduction());
												temp.setDynamicSwordCritical(s.getItemEnLevel() * i.getEnchantSwordCritical());
												temp.setDynamicBowCritical(s.getItemEnLevel() * i.getEnchantBowCritical());
												temp.setDynamicMagicCritical(s.getItemEnLevel() * i.getEnchantMagicCritical());
												temp.setDynamicPvpDmg(s.getItemEnLevel() * i.getEnchantPvpDamage());
												temp.setDynamicPvpReduction(s.getItemEnLevel() * i.getEnchantPvpReduction());
												pc.getInventory().append(temp, true);
												//
												Log.appendItem(pc, "type|상점구입", String.format("npc_name|%s", getNpc().getName()), String.format("item_name|%s", temp.toStringDB()),
														String.format("item_objid|%d", temp.getObjectId()), String.format("count|%d", item_count), String.format("shop_uid|%d", s.getUid()),
														String.format("shop_count|%d", s.getItemCount()));
											}
										}

									} else {
										// 겹치는 아이템이 존재할 경우.
										pc.getInventory().count(temp, temp.getCount() + new_item_count, true);
										//
										Log.appendItem(pc, "type|상점구입", String.format("npc_name|%s", getNpc().getName()), String.format("item_name|%s", s.getItemName()),
												String.format("target_name|%s", temp.toStringDB()), String.format("target_objid|%d", temp.getObjectId()), String.format("count|%d", item_count),
												String.format("shop_uid|%d", s.getUid()), String.format("shop_count|%d", s.getItemCount()));

									}
									// 아데나일때만 처리.
									if (s.getAdenType().equalsIgnoreCase("아데나")) {
										// 세금으로인한 차액을 공금에 추가.
										if (s.getPrice() != 0)
											addTax((int) ((shop_price - s.getPrice()) * item_count));
										else
											addTax((int) ((shop_price - i.getShopPrice()) * item_count));
									}

								} else {
									// 0%%s 충분치 않습니다.
									pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 776, s.getAdenType()));
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 상점 판매
	 */
	protected void toSell(PcInstance pc, ClientBasePacket cbp) {
		if (Lineage.open_wait && pc.getGm() == 0) {
			ChattingController.toChatting(pc, "[오픈 대기] 상점을 이용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		Connection con = null;
		int count = cbp.readH();
		if (count > 0) {
			try {
				con = DatabaseConnection.getLineage();
	
				
				for (int i = 0; i < count; ++i) {
					int inv_id = cbp.readD();
					long item_count = cbp.readD();
					ItemInstance temp = pc.getInventory().value(inv_id);
					
					
//					//자동상점 버그 수정 야도란
//					if (inv_id != temp.getObjectId()) {
//						pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
//						return;
//					}
//					if ( item_count != 1) {
//						pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
//						return;
//					}
//					if (item_count <= 0 || temp.getCount() <= 0) {
//						pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
//						return;
//					}
//					if (item_count > temp.getCount()) {
//						item_count = temp.getCount();
//					}
					if (temp != null && !temp.isEquipped() && item_count > 0 && temp.getCount() >= item_count) {
						//
						String target_name = temp.toStringDB();
						long target_objid = temp.getObjectId();
						long aden_objid = 0;
						//
						Shop s = npc.findShopItemId(temp.getItem().getName(), temp.getBless());
						// 판매될수 있는 아이템만 처리.
						if (s != null && s.isItemSell()) {
							// 가격 체크
							long target_price = getPrice(con, temp);
							// 아덴 지급
							if (target_price > 0) {
								ItemInstance aden = pc.getInventory().find(s.getAdenType(), true);

								if (aden == null) {

									aden = ItemDatabase.newInstance(ItemDatabase.find(s.getAdenType()));
									aden.setObjectId(ServerDatabase.nextItemObjId());
									aden.setCount(0);

									if (!s.getAdenType().equalsIgnoreCase("포인트")) {
										pc.getInventory().append(aden, true);
									}

								}

								aden_objid = aden.getObjectId();

								long total = aden.getCount() + (target_price * item_count);

								if (s.getAdenType().equalsIgnoreCase("포인트")) {
									AccountDatabase.userpoint(pc.getAccountId(), total);

								} else {
									pc.getInventory().count(aden, aden.getCount() + (target_price * item_count), true);
								}
								//

								//
								Log.appendItem(pc, "type|상점판매금", "npc_name|" + getNpc().getName(), "aden_name|" + s.getAdenType(), "aden_objid|" + aden_objid, "target_name|" + target_name, "target_objid|" + target_objid,
										"target_price|" + target_price, "item_count|" + item_count);
								// 세금계산은 아데나일때만 처리.
								if (s.getAdenType().equalsIgnoreCase("아데나")) {
									// 세금으로인한 차액을 공금에 추가.
									if (s.getPrice() != 0)
										addTax((int) ((s.getPrice() * 0.5) - target_price));
									else
										addTax((int) ((temp.getItem().getShopPrice() * 0.5) - target_price));
								}

							}
							//
							Log.appendItem(pc, "type|상점판매", String.format("npc_name|%s", getNpc().getName()), String.format("target_name|%s", target_name), String.format("target_objid|%d", target_objid),
									String.format("target_price|%d", target_price), String.format("item_count|%d", item_count));

							// 판매되는 아이템 제거.
							pc.getInventory().count(temp, temp.getCount() - item_count, true);
						}
					}
				}
			} catch (Exception e) {
			} finally {
				DatabaseConnection.close(con);
			}
		}
	}

	/**
	 * 설정된 세율에 따라 가격을 연산하여 리턴함.
	 * 
	 * @param price
	 * @return
	 */
	public int getTaxPrice(double price, boolean sell) {
		// sell 일경우 기본가격의 35%
		double a = sell ? price * Lineage.sell_item_rate : price;
		// 세율값 +@ 또는 -@ [원가에 지정된 세율만큼만]
		if (Lineage.add_tax) {
			if (sell)
				a -= a * (getTax() * 0.01);
			else
				a += a * (getTax() * 0.01);
		}
		// 반올림 처리.
		return (int) Math.round(a);
	}

	/**
	 * 겜블 상점에서 아이템 구매시 인첸트 값 추출해주는 함수.
	 * 
	 * @return
	 */
	private int getGambleEnLevel() {
		int en = 0;
		double percent = Math.random() * 100;
				
		if (percent < 0.1)
			en = 7;
		else if (percent < 5)
			en = 6;
		else if (percent < 10)
			en = 5;
		else if (percent < 15)
			en = 4;
		else if (percent < 20)
			en = 3;
		else if (percent < 25)
			en = 2;
		else if (percent < 30)
			en = 1;
		
		return en;
	}

	/**
	 * 레이스 상점에 표현될 아이템에 가격을 추출.
	 * 
	 * @param item
	 * @param PcShop
	 * @return
	 */
	public int getPrice(Connection con, ItemInstance item) {
		// 슬라임 레이스표 가격 추출.
		if (item instanceof RaceTicket) {
			RaceTicket ticket = (RaceTicket) item;
			PreparedStatement st = null;
			ResultSet rs = null;
			try {
				// 로그 참고로 목록 만들기.
				st = con.prepareStatement("SELECT * FROM race_log WHERE uid=? AND race_idx=? AND type=?");
				st.setInt(1, ticket.getRaceUid());
				st.setInt(2, ticket.getRacerIdx());
				st.setString(3, ticket.getRacerType());
				rs = st.executeQuery();
				if (rs.next())
					return rs.getInt("price");
			} catch (Exception e) {
				lineage.share.System.println(ShopInstance.class + " : getPrice(Connection con, ItemInstance item)");
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(st, rs);
			}
			// 당첨 안된거 0원
			return 0;
		}
		Shop shop = npc.findShopItemId2(item.getItem().getName(), item.getBless(),item.getEnLevel());
		// 그외 일반 아이템 가격 추출.
		if (item == null || shop == null/* || item.getItem().getType2().equalsIgnoreCase("arrow")*/) {
			// 버그도 무시.
			return 0;
		} else {
			if (shop.getPrice() != 0) {
				return shop.getPrice();
			} else {
				if ((item.getItem().getName().equalsIgnoreCase(Lineage.scroll_dane_fools) || item.getItem().getName().equalsIgnoreCase(Lineage.scroll_zel_go_mer)
						|| item.getItem().getName().equalsIgnoreCase(Lineage.scroll_orim)) && (item.getBless() == 0 || item.getBless() == 2))
					return getTaxPrice(item.getItem().getShopPrice() * (item.getBless() == 0 ? Lineage.sell_bless_item_rate : Lineage.sell_curse_item_rate), true);
				else
					return getTaxPrice(item.getItem().getShopPrice(), true);
			}

		}
	}

	/**
	 * 상점 판매목록에 추가해도 되는지 확인해주는 함수.
	 * 
	 * @return
	 */
	protected boolean isSellAdd(ItemInstance item) {
		return true;
	}

}
