package lineage.world.object.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.database.Warehouse;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.WarehouseClanLogDatabase;
import lineage.database.WarehouseDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_WareHouse;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.object.object;

public class DwarfInstance extends object {
	private Npc npc;
	protected Kingdom kingdom;

	public DwarfInstance(Npc npc) {
		this.npc = npc;
	}

	/**
	 * 창고를 이용할 수 있는 레벨인지 확인하는 메서드.
	 */
	static public boolean isLevel(int level) {
		return level >= Lineage.warehouse_level;
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {		
		//자동판매 초기화
		pc.isAutoSellAdding = false;
		pc.isAutoSellDeleting = false;
		
		synchronized (sync_dynamic) {
			int dwarf_type = Lineage.DWARF_TYPE_NONE; // 일반창고
			if (action.indexOf("pledge") > 0)
				dwarf_type = Lineage.DWARF_TYPE_CLAN; // 혈맹창고 
			else if (action.indexOf("elven") > 0)
				dwarf_type = Lineage.DWARF_TYPE_ELF; // 요정창고   미스릴 2 

			int id = dwarf_type == Lineage.DWARF_TYPE_CLAN ? pc.getClanId() : pc.getClient().getAccountUid();
			
			// 혈맹 창고 사용못하는 버그 확인
			Clan clan = ClanController.find(pc);
			PcInstance use = null;
			
			if (clan != null) {
				use = World.findPc(clan.getWarehouseObjectId());
				
				if (use == null || !Util.isDistance(use, this, Lineage.SEARCH_LOCATIONRANGE)) {
					clan.setWarehouseObjectId(0L);
				}
			}
		

			if (dwarf_type == Lineage.DWARF_TYPE_CLAN && pc.getClanId() == 0) {
				// \f1창고: 혈맹 창고 이용 불가(혈맹 미가입)
				ChattingController.toChatting(pc, "창고: 혈맹 창고 이용 불가(혈맹 미가입)", Lineage.CHATTING_MODE_MESSAGE);
				//pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 208));
			} else if (dwarf_type == Lineage.DWARF_TYPE_CLAN && pc.getClassType() != Lineage.LINEAGE_CLASS_ROYAL && (pc.getTitle() == null || pc.getTitle().length() == 0)) {
				// 호칭을 받지 못한 혈맹원이나 견습 혈맹원은 혈맹창고를 사용할 수 없습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 728));
			} else if (dwarf_type == Lineage.DWARF_TYPE_CLAN && clan.getWarehouseObjectId() > 0L && clan.getWarehouseObjectId() != pc.getObjectId()) {
			//	pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 209)); // 창고 목록 
				if (use != null) {
					ChattingController.toChatting(pc, String.format("'%s' 님이 혈맹 창고를 사용중입니다.", use.getName()), Lineage.CHATTING_MODE_MESSAGE);
				}	
			} else {
				int cnt = WarehouseDatabase.getCount(id, dwarf_type);
				if (cnt == 0) {
					
				   pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "noitemret")); 
				} else {
					if (dwarf_type == Lineage.DWARF_TYPE_CLAN)
						clan.setWarehouseObjectId(pc.getObjectId());

					// 창고 목록 열람.
					List<Warehouse> list = WarehouseDatabase.getList(id, dwarf_type);
					pc.toSender(S_WareHouse.clone(BasePacketPooling.getPool(S_WareHouse.class), this, dwarf_type, list));
					for (Warehouse wh : list)
						WarehouseDatabase.setPool(wh);
					list.clear();
				}
			}
		}
	}

	@Override
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp) {
		if (Lineage.open_wait && pc.getGm() == 0) {
			ChattingController.toChatting(pc, "[오픈 대기] 창고를 이용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		synchronized (sync_dynamic) {
			int type = cbp.readC();
			switch (type) {
			case 2: // 창고 맡기기
				insert(pc, Lineage.DWARF_TYPE_NONE, cbp);
				break;
			case 3: // 창고 찾기
				select(pc, Lineage.DWARF_TYPE_NONE, cbp);
				break;
			case 4: // 혈맹창고 맡기기
				insert(pc, Lineage.DWARF_TYPE_CLAN, cbp);
				break;
			case 5: // 혈맹창고 찾기
				Clan clan = ClanController.find(pc);

				if (clan != null) {
					if (System.currentTimeMillis() > clan.getClanWarehouseTime()) {
						clan.setClanWarehouseTime(System.currentTimeMillis() + (1000 * 5));
						select(pc, Lineage.DWARF_TYPE_CLAN, cbp);
					} else {
						ChattingController.toChatting(pc, "5초 후 다시 이용하시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
				}
				break;
			case 8: // 요정창고 맡기기
				insert(pc, Lineage.DWARF_TYPE_ELF, cbp);
				break;
			case 9: // 요정창고 찾기
				select(pc, Lineage.DWARF_TYPE_ELF, cbp);
				break;
			}
		}
	}

	/**
	 * 창고에서 아이템 꺼낼때 사용하는 메서드.
	 */
	private void select(PcInstance pc, int dwarf_type, ClientBasePacket cbp) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		PreparedStatement st2 = null;
		ResultSet rs2 = null;
		Clan clan = dwarf_type == Lineage.DWARF_TYPE_CLAN ? ClanController.find(pc) : null;
		
		if (dwarf_type == Lineage.DWARF_TYPE_CLAN && clan != null && clan.getWarehouseObjectId() != pc.getObjectId()) {
			ChattingController.toChatting(pc, "[혈맹창고] 잘못된 접근입니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		try {
			con = DatabaseConnection.getLineage();

			long count = cbp.readH();
			int id = dwarf_type == Lineage.DWARF_TYPE_CLAN ? pc.getClanId() : pc.getClient().getAccountUid();
			int w_Count = WarehouseDatabase.getCount(id, dwarf_type);
			
			if (count > 0 && count < 2100000000) {
				if (count > 0 && count <= w_Count) {


				long item_id = 0;
				long item_count = 0;

				for (int i = 0; i < count; ++i) {
					item_id = cbp.readD();
					item_count = cbp.readD();
					


			        String query = "";
	                switch (dwarf_type) {
	                    case Lineage.DWARF_TYPE_CLAN:
	                        query = "SELECT * FROM warehouse_clan WHERE uid=? AND clan_id=?";
	                        break;
	                    case Lineage.DWARF_TYPE_ELF:
	                        query = "SELECT * FROM warehouse_elf WHERE uid=? AND account_uid=?";
	                        break;
	                    default:
	                        query = "SELECT * FROM warehouse WHERE uid=? AND account_uid=?";
	                        break;
	                }
				    st = con.prepareStatement(query);
					st.setLong(1, item_id);
					st.setInt(2, id);
					rs = st.executeQuery();
					if (rs.next()) {
						int db_uid = rs.getInt(1);
						int db_inv_id = rs.getInt(3);
						int pet_objid = rs.getInt(4);
						int letter_id = rs.getInt(5);
						String db_name = rs.getString(6);
						long db_count = rs.getLong(9);
						int db_quantity = rs.getInt(10);
						int db_en = rs.getInt(11);
						boolean db_definite = rs.getInt(12) == 1;
						int db_bress = rs.getInt(13);
						int db_durability = rs.getInt(14);
						int db_time = rs.getInt(15);
						int db_enfire = rs.getInt(16);
						int db_enwater = rs.getInt(17);
						int db_enwind = rs.getInt(18);
						int db_enearth = rs.getInt(19);
						int DolloptionA = rs.getInt(20);
						int DolloptionB = rs.getInt(21);
						int DolloptionC = rs.getInt(22);
						int DolloptionD = rs.getInt(23);
						int DolloptionE = rs.getInt(24);
						String itemk = rs.getString(25);
						// log 용
						String item_name = null;
						long item_objid = db_inv_id;
						String target_name = null;
						long target_objid = 0;

						ItemInstance temp = ItemDatabase.newInstance(ItemDatabase.find(db_name));
						
						//쿠베라 창고 복사 보안
						if (temp == null) {
							long time = System.currentTimeMillis();
							String timeString = Util.getLocaleString(time, true);
							String log = String.format("[%s]\t[%s]\t %s(창고에 없는템 시도)", timeString, 
									dwarf_type == Lineage.DWARF_TYPE_CLAN ? "혈맹 창고 찾기" : "창고 찾기", pc.getName());

							GuiMain.display.asyncExec(new Runnable() {
								public void run() {
									GuiMain.getViewComposite().getWarehouseComposite().toLog(log);
								}
							});
							
							
							return;
						}
						if(item_count > db_count) {
							long time = System.currentTimeMillis();
							String timeString = Util.getLocaleString(time, true);
							String log = String.format("[%s]\t[%s]\t %s(창고 수량 조작 시도)", timeString, 
									dwarf_type == Lineage.DWARF_TYPE_CLAN ? "혈맹 창고 찾기" : "창고 찾기", pc.getName());

							GuiMain.display.asyncExec(new Runnable() {
								public void run() {
									GuiMain.getViewComposite().getWarehouseComposite().toLog(log);
								}
							});
							
							
							return;
						}
						
						if (temp != null && item_count > 0 && item_count <= db_count) {
							temp.setCount(item_count);
							temp.setBless(db_bress);
							if (pc.getInventory().isAppend(temp, temp.getCount(), false)) {
								boolean aden = dwarf_type == Lineage.DWARF_TYPE_ELF ? pc.getInventory().isMeterial(Lineage.warehouse_price_elf, true)
										: pc.getInventory().isAden(Lineage.warehouse_price, true);
								if (aden) {
									ItemInstance temp2 = pc.getInventory().find(temp);
									if (temp2 == null) {
										// insert
										temp.setObjectId(db_inv_id);
										temp.setQuantity(db_quantity);
										temp.setEnLevel(db_en);
										temp.setDefinite(db_definite);
										temp.setBless(db_bress);
										temp.setDurability(db_durability);
										temp.setTime(db_time);
										temp.setPetObjectId(pet_objid);
										temp.setLetterUid(letter_id);
										temp.setEnFire(db_enfire);
										temp.setEnWater(db_enwater);
										temp.setEnWind(db_enwind);
										temp.setEnEarth(db_enearth);
										temp.setInvDolloptionA(DolloptionA);
										temp.setInvDolloptionB(DolloptionB);
										temp.setInvDolloptionC(DolloptionC);
										temp.setInvDolloptionD(DolloptionD);
										temp.setInvDolloptionE(DolloptionE);
										temp.setItemTimek(itemk);
										pc.getInventory().append(temp, true);
										// 아이템 정보 갱신.
										temp.toWorldJoin(con, pc);
										//
										WarehouseClanLogDatabase.append(pc, temp, item_count, "remove");
										///snrn
										if (Lineage.clan_warehouse_message && dwarf_type == Lineage.DWARF_TYPE_CLAN) {
											//		Clan clan = ClanController.find(pc);
											if (clan != null) {
												String msg = String.format("[혈맹창고] %s 님이 %s 찾음", pc.getName(), temp.toStringDB());
												clan.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, msg));
											}
										}
										// log
										item_name = temp.toStringDB();
									} else {
										//
										WarehouseClanLogDatabase.append(pc, temp, item_count, "remove");
										//
										if (Lineage.clan_warehouse_message && dwarf_type == Lineage.DWARF_TYPE_CLAN) {
											//								Clan clan = ClanController.find(pc);
											if (clan != null) {
												String msg = String.format("[혈맹창고] %s 님이 %s 찾음", pc.getName(), temp.toStringDB());
												clan.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, msg));
											}
										}
										// log
										item_name = temp.toStringDB();
										target_name = temp2.toStringDB();
										target_objid = temp2.getObjectId();
										// update
										pc.getInventory().count(temp2, temp2.getCount() + temp.getCount(), true);
										//쿠베라 창고 복사 보안
										ItemDatabase.setPool(temp);
									}

									db_count -= item_count;
									if (db_count <= 0) {
										// delete
										switch (dwarf_type) {
										case Lineage.DWARF_TYPE_CLAN:
											st2 = con.prepareStatement("DELETE FROM warehouse_clan WHERE uid=?");
											break;
										case Lineage.DWARF_TYPE_ELF:
											st2 = con.prepareStatement("DELETE FROM warehouse_elf WHERE uid=?");
											break;
										default:
											st2 = con.prepareStatement("DELETE FROM warehouse WHERE uid=?");
											break;
										}
										st2.setInt(1, db_uid);
										st2.executeUpdate();
										st2.close();
									} else {
										// update
										switch (dwarf_type) {
										case Lineage.DWARF_TYPE_CLAN:
											st2 = con.prepareStatement("UPDATE warehouse_clan SET count=? WHERE uid=?");
											break;
										case Lineage.DWARF_TYPE_ELF:
											st2 = con.prepareStatement("UPDATE warehouse_elf SET count=? WHERE uid=?");
											break;
										default:
											st2 = con.prepareStatement("UPDATE warehouse SET count=? WHERE uid=?");
											break;
										}
										st2.setLong(1, db_count);
										st2.setInt(2, db_uid);
										st2.executeUpdate();
										st2.close();
						
									
									}

									//
									if (dwarf_type == Lineage.DWARF_TYPE_CLAN)
										Log.appendItem(pc, "type|혈맹창고찾기", String.format("item_name|%s", item_name), String.format("item_objid|%d", item_objid), String.format("count|%d", item_count),
												String.format("target_name|%s", target_name), String.format("target_objid|%d", target_objid));
									else
										Log.appendItem(pc, "type|창고찾기", String.format("item_name|%s", item_name), String.format("item_objid|%d", item_objid), String.format("count|%d", item_count),
												String.format("target_name|%s", target_name), String.format("target_objid|%d", target_objid));

									
									// gui 로그
									if (!Common.system_config_console && !(pc instanceof PcRobotInstance) && pc instanceof PcInstance) {
										long time = System.currentTimeMillis();
										String timeString = Util.getLocaleString(time, true);
										String log = String.format("[%s]\t[%s]\t캐릭터: %s\t캐릭터obj_id: %d\t  아이템: %s", timeString, 
												dwarf_type == Lineage.DWARF_TYPE_CLAN ? "혈맹 창고 찾기" : "창고 찾기", pc.getName(), pc.getObjectId(), Util.getItemNameToString(temp, item_count));

										GuiMain.display.asyncExec(new Runnable() {
											public void run() {
												GuiMain.getViewComposite().getWarehouseComposite().toLog(log);
											}
										});
									}
								} else {
									if (dwarf_type == Lineage.DWARF_TYPE_ELF)
										// \f1%0%s 부족합니다.
										pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 337, "미스릴"));
									else
										// \f1아데나가 충분치 않습니다.
										pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
									ItemDatabase.setPool(temp);
									break;
								}
							} else {
								ItemDatabase.setPool(temp);
								break;
							}
						}
					}
					rs.close();
					st.close();
				}

			}
		}	
		} catch (Exception e) {
			//lineage.share.System.println(DwarfInstance.class.toString() + " : select(PcInstance pc, boolean clan, ClientBasePacket cbp)");
			//lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st2, rs2);
			DatabaseConnection.close(con, st, rs);
		}
		//
		if (dwarf_type == Lineage.DWARF_TYPE_CLAN) {
			if (clan.getWarehouseObjectId() == pc.getObjectId())
				clan.setWarehouseObjectId(0L);
		}
	}

	/**
	 * 창고에 아이템 맡길때 처리하는 메서드.
	 */
	private void insert(PcInstance pc, int dwarf_type, ClientBasePacket cbp) {
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();

			int uid = dwarf_type == Lineage.DWARF_TYPE_CLAN ? pc.getClanId() : pc.getClient().getAccountUid();
			int Count = cbp.readH();
			int w_Count = WarehouseDatabase.getCount(uid, dwarf_type);
			boolean is = dwarf_type == Lineage.DWARF_TYPE_CLAN ? pc.getClanId() > 0 : true;

			if (Count > 0) {
		
				if (is && w_Count + Count <= Lineage.warehouse_max) {

					for (int i = 0; i < Count; ++i) {
						ItemInstance temp = pc.getInventory().value(cbp.readD());
						if (temp != null) {

							final long count = cbp.readD();
							
							if (count < 0 || count > Common.MAX_COUNT) {
								ChattingController.toChatting(pc, String.format("[20억 초과 창고 보관 불가] %s", temp.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
								continue;
							}
							if(count > temp.getCount()) {
								long time = System.currentTimeMillis();
								String timeString = Util.getLocaleString(time, true);
								String log = String.format("[%s]\t[%s]\t %s 창고 조작 시도", timeString, 
										dwarf_type == Lineage.DWARF_TYPE_CLAN ? "혈맹 창고 맡기기" : "창고 맡기기", pc.getName());

								GuiMain.display.asyncExec(new Runnable() {
									public void run() {
										GuiMain.getViewComposite().getWarehouseComposite().toLog(log);
									}
								});
								
								return;
							}
							if (temp != null && !temp.isEquipped() && pc.getInventory().isRemove(temp, count, true, true, true)) {
								if ((dwarf_type == Lineage.DWARF_TYPE_NONE && temp.getItem().isWarehouse()) 
									|| (dwarf_type == Lineage.DWARF_TYPE_CLAN && temp.getItem().isClanWarehouse())
									|| (dwarf_type == Lineage.DWARF_TYPE_ELF && temp.getItem().isElfWarehouse())) {
									if (count > 0 && count <= temp.getCount()) {
										String item_name = temp.toStringDB();
										// 등록하려는 아이템이 겹쳐지는 아이템이라면 디비에 겹칠 수 있는 것이 존재하는지 확인.
										long inv_id = temp.getItem().isPiles() ? WarehouseDatabase.isPiles(temp.getItem().isPiles(), uid, temp.getItem().getName(), temp.getBless(), dwarf_type) : 0;
										
										if (inv_id > 0) {
											if (!WarehouseDatabase.isCountCheck(pc, uid, temp.getItem().getName(), temp.getBless(), dwarf_type, count, temp.getCount())) {
												ChattingController.toChatting(pc, String.format("[20억 초과 창고 보관 불가] %s", temp.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
												continue;
											}
											// update
											WarehouseDatabase.update(temp.getItem().getName(), temp.getBless(), uid, count, dwarf_type);
									
										} else {
											if (count > Common.MAX_COUNT) {
												ChattingController.toChatting(pc, String.format("[20억 초과 창고 보관 불가] %s", temp.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
												continue;
											}
											
											// 맡기는 갯수와 아이템의 갯수가 같으면 삭제
											if (count == temp.getCount()) {
												WarehouseDatabase.insert(temp, temp.getObjectId(), count, uid, dwarf_type);
												
											} else {
												WarehouseDatabase.insert(temp, ServerDatabase.nextItemObjId(), count, uid, dwarf_type);
												
											
											}
										}

										if (dwarf_type == Lineage.DWARF_TYPE_CLAN)
											WarehouseClanLogDatabase.append(pc, temp, count, "append");

										if (dwarf_type == Lineage.DWARF_TYPE_CLAN)
											Log.appendItem(pc, "type|혈맹창고등록", String.format("item_name|%s", item_name), String.format("item_objid|%d", temp.getObjectId()), String.format("count|%d", count),
													String.format("target_objid|%d", inv_id));
										else
											Log.appendItem(pc, "type|창고등록", String.format("item_name|%s", item_name), String.format("item_objid|%d", temp.getObjectId()), String.format("count|%d", count),
													String.format("target_objid|%d", inv_id));
										
										// gui 로그
										if (!Common.system_config_console && !(pc instanceof PcRobotInstance) && pc instanceof PcInstance) {
											long time = System.currentTimeMillis();
											String timeString = Util.getLocaleString(time, true);
											String log = String.format("[%s]\t [%s]\t [캐릭터: %s]\t [캐릭터obj_id: %d]\t [아이템: %s]", timeString, 
													dwarf_type == Lineage.DWARF_TYPE_CLAN ? "혈맹 창고 맡기기" : "창고 맡기기", pc.getName(), pc.getObjectId(), Util.getItemNameToString(temp, count));

											GuiMain.display.asyncExec(new Runnable() {
												public void run() {
													GuiMain.getViewComposite().getWarehouseComposite().toLog(log);
												}
											});
										}

										pc.getInventory().count(temp, temp.getCount() - count, true);
									
									}
								}
							} else {
								long time = System.currentTimeMillis();
								String timeString = Util.getLocaleString(time, true);
								String log = String.format("[%s]\t[%s]\t %s 캐릭터:창고 조작 시도", timeString, 
										dwarf_type == Lineage.DWARF_TYPE_CLAN ? "혈맹 창고 맡기기" : "창고 맡기기", pc.getName());

								GuiMain.display.asyncExec(new Runnable() {
									public void run() {
										GuiMain.getViewComposite().getWarehouseComposite().toLog(log);
									}
								});
							}
						
						}
						
					}
				} else {
					// \f1더이상 물건을 넣을 자리가 없습니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 75));
				}
			}

		} catch (Exception e) {
			lineage.share.System.println(DwarfInstance.class.toString() + " : insert(PcInstance pc, int dwarf_type, ClientBasePacket cbp)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
	}

}