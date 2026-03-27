package lineage.world.object.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lineage.bean.database.Item;
import lineage.bean.database.PcShop;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase; 
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_PcShopBuy;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.world.World;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.LetterController;
import lineage.world.controller.PcMarketController;
import lineage.world.controller.PcTradeController;
import lineage.world.object.item.DogCollar;

public class PcShopInstance extends BackgroundInstance {

	public Map<Long, PcShop> list; // 개인상점 판매목록.
	public String shop_comment; // 개인상점 광고 문구.
	private int show_comment; // 
	public int classType;
	public int classSex;
	private long pc_objectId;
	public String pc_name;
	private static final String INSERT_SQL = "INSERT INTO characters_inventory SET objId=?, cha_objId=?, name=?, count=?, definite=1";
	private static final String UPDATE_SQL = "UPDATE characters_inventory SET count=count+? WHERE cha_objId=? AND name=? AND bress=1";

	public PcShopInstance(long pc_objId, String pc_name, int classType, int classSex) {
		list = new HashMap<Long, PcShop>();

		pc_objectId = pc_objId;
		this.pc_name = pc_name;
		this.classType = classType;
		this.classSex = classSex;
		
		switch (classType) {
		case 0:
			if (classSex == 0)
				setGfx(2611);
			else
				setGfx(2612);
			break;
		case 1:
			if (classSex == 0)
				setGfx(2613);
			else
				setGfx(2614);
			break;
		case 2:
			if (classSex == 0)
				setGfx(2615);
			else
				setGfx(2616);
			break;
		case 3:
			if (classSex == 0)
				setGfx(2617);
			else
				setGfx(2618);
			break;
		case 4:
			if (classSex == 0)
				setGfx(2786);
			else
				setGfx(2796);
			break;
		}
		
		setObjectId(ServerDatabase.nextEtcObjId());
		setName(pc_name + "의 상점");
		setClanId(0);
		setClanName("");
		setTitle("");
		setLawful(66536);
		setGfxMode(0);
		CharacterController.toWorldJoin(this);
	}

	@Override
	public void close() {
		clearShopList();

		clearList(true);
		World.remove(this);

		super.close();

		CharacterController.toWorldOut(this);
	}

	public long getPc_objectId() {
		return pc_objectId;
	}

	public String getShopComment() {
		return shop_comment;
	}
	
	public Map<Long, PcShop> getShopList() {
		synchronized (list) {
			return list;
		}
	}
	
	public PcShop getItem(long key) {
		synchronized (list) {
			return list.get(key);
		}
	}
	
	public void removeItem(long key) {
		synchronized (list) {
			list.remove(key);
		}
	}
	
	public void clearShopList() {
		if (list != null) {
			synchronized (list) {
				list.clear();
			}
		}
	}
	
	public void appendItem(long key, PcShop shop) {
		if (shop != null) {
			synchronized (list) {
				list.put(key, shop);
			}
		}
	}
	
	public int getListSize() {
		synchronized (list) {
			return list.size();
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {

	
		//야도란 상점 수정
		if (pc_objectId == pc.getObjectId()) {
			ChattingController.toChatting(pc, "자신의 상점은 클릭 할수없습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}else{
			pc.toSender(S_PcShopBuy.clone(BasePacketPooling.getPool(S_PcShopBuy.class), this));
		}

	}

	@Override
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp) {
		switch (cbp.readC()) {
		case 0: // 상점 구입
			toBuy(pc, cbp);
			break;
		}
	}

	@Override
	public void toTimer(long time) {
		//
		if (!isWorldDelete() && shop_comment != null && shop_comment.length() > 0) {
			if (show_comment++ % 20 == 0) {
				ChattingController.toChatting(this, shop_comment, Lineage.CHATTING_MODE_NORMAL);
				
				if (SpriteFrameDatabase.findGfxMode(getGfx(), 68))
					toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 68), true);
			}
		}
	}
	
	/**
	 * 상점 구매
	 */
	protected void toBuy(PcInstance pc, ClientBasePacket cbp) {
		int count = cbp.readH();
		if (count > 0 && count <= 100) {
			for (int j = 0; j < count; ++j) {
				long item_idx = cbp.readD();
				long item_count = cbp.readD();
				
				if(item_count >2000000000 ||item_count < 0)
					return;
				
				if (item_count > 0) {
					PcShop s = getItem(item_idx);
	                
					//쿠베라 상점 버그
					if(s!=null)
						if(s.getInvItemCount()>0 && item_count>s.getInvItemCount())
						// 수량이 등록한것보다 많게 요청했을때.
							item_count = s.getInvItemCount();
					
					if(item_count > s.getInvItemCount()) {
						return;
					}
						
					if (pc.getInventory().isAppend(s.getItem(), item_count, s.getItem().isPiles() ? 1 : item_count)) {

						if ((pc.getInventory().isAden(s.getAdenType(), s.getPrice() * item_count, false) && s.getPc().getPc_objectId() != pc.getObjectId()) ||
							s.getPc().getPc_objectId() == pc.getObjectId()) {
							
							if (s.getPc().getPc_objectId() != pc.getObjectId())
								pc.getInventory().isAden(s.getAdenType(), s.getPrice() * item_count, true);

							s.setInvItemCount(s.getInvItemCount() - item_count);

							// 사용자 아이템 추가.
							ItemInstance temp = pc.getInventory().find(s.getItem().getName(), s.getInvItemBress(), s.getItem().isPiles());
							if (temp == null) {
								// 겹칠수 있는 아이템이 존재하지 않을경우.
								if (s.getItem().isPiles()) {
									temp = ItemDatabase.newInstance(s.getItem());
									temp.setObjectId(s.getInvItemObjectId() == 0 ? ServerDatabase.nextItemObjId() : s.getInvItemObjectId());
									temp.setCount(item_count);
									temp.setEnLevel(s.getInvItemEn());
									temp.setDefinite(s.isInvItemDefinite());
									temp.setBless(s.getInvItemBress());
									temp.setEnFire(s.getInvItemEnFire()); 
									temp.setEnWater(s.getInvItemEnWater()); 
									temp.setEnWind(s.getInvItemEnWind()); 
									temp.setEnEarth(s.getInvItemEnEarth());
									temp.setInvDolloptionA(s.getInvDolloptionA());
									temp.setInvDolloptionB(s.getInvDolloptionB());
									temp.setInvDolloptionC(s.getInvDolloptionC());
									temp.setInvDolloptionD(s.getInvDolloptionD());
									temp.setInvDolloptionE(s.getInvDolloptionE());
									if (s.getItem().getName().equalsIgnoreCase("신성한 엘름의 축복"))
										temp.setDynamicMr(s.getInvItemEn() > 4 ? (s.getInvItemEn() - 4) * s.getItem().getEnchantMr() : 0);
									else
										temp.setDynamicMr(s.getInvItemEn() * s.getItem().getEnchantMr());
									temp.setDynamicStunDefence(s.getInvItemEn() * s.getItem().getEnchantStunDefense());
									temp.setDynamicStunHit(s.getInvItemEn() * s.getItem().getEnchantStunHit());
									temp.setDynamicSp(s.getInvItemEn() * s.getItem().getEnchantSp());
									temp.setDynamicReduction(s.getInvItemEn() * s.getItem().getEnchantReduction());
									temp.setDynamicIgnoreReduction(s.getInvItemEn() * s.getItem().getEnchantIgnoreReduction());
									temp.setDynamicSwordCritical(s.getInvItemEn() * s.getItem().getEnchantSwordCritical());
									temp.setDynamicBowCritical(s.getInvItemEn() * s.getItem().getEnchantBowCritical());
									temp.setDynamicMagicCritical(s.getInvItemEn() * s.getItem().getEnchantMagicCritical());
									temp.setDynamicPvpDmg(s.getInvItemEn() * s.getItem().getEnchantPvpDamage());
									temp.setDynamicPvpReduction(s.getInvItemEn() * s.getItem().getEnchantPvpReduction());
								   
									temp.setPetObjectId(s.getPetObjId());
									temp.setItemTimek(s.getInvItemK());
									 insertShopHistory((int)pc.getObjectId(), pc.getName(), s.getPc().getName(), s.getItem().getName(), s.getPrice(), item_count, s.getInvItemEn(), s.getInvItemBress(), true);
									pc.getInventory().append(temp, true);
									
								} else {
									for (int k = 0; k < item_count; ++k) {
										temp = ItemDatabase.newInstance(s.getItem());
										temp.setObjectId(s.getInvItemObjectId() == 0 ? ServerDatabase.nextItemObjId() : s.getInvItemObjectId());
										temp.setEnLevel(s.getInvItemEn());
										temp.setDefinite(s.isInvItemDefinite());
										temp.setBless(s.getInvItemBress());
										temp.setEnFire(s.getInvItemEnFire()); 
										temp.setEnWater(s.getInvItemEnWater()); 
										temp.setEnWind(s.getInvItemEnWind()); 
										temp.setEnEarth(s.getInvItemEnEarth()); 
										temp.setInvDolloptionA(s.getInvDolloptionA());
										temp.setInvDolloptionB(s.getInvDolloptionB());
										temp.setInvDolloptionC(s.getInvDolloptionC());
										temp.setInvDolloptionD(s.getInvDolloptionD());
										temp.setInvDolloptionE(s.getInvDolloptionE());
										if (s.getItem().getName().equalsIgnoreCase("신성한 엘름의 축복"))
											temp.setDynamicMr(s.getInvItemEn() > 4 ? (s.getInvItemEn() - 4) * s.getItem().getEnchantMr() : 0);
										else
											temp.setDynamicMr(s.getInvItemEn() * s.getItem().getEnchantMr());
										temp.setDynamicStunDefence(s.getInvItemEn() * s.getItem().getEnchantStunDefense());
										temp.setDynamicStunHit(s.getInvItemEn() * s.getItem().getEnchantStunHit());
										temp.setDynamicSp(s.getInvItemEn() * s.getItem().getEnchantSp());
										temp.setDynamicReduction(s.getInvItemEn() * s.getItem().getEnchantReduction());
										temp.setDynamicIgnoreReduction(s.getInvItemEn() * s.getItem().getEnchantIgnoreReduction());
										temp.setDynamicSwordCritical(s.getInvItemEn() * s.getItem().getEnchantSwordCritical());
										temp.setDynamicBowCritical(s.getInvItemEn() * s.getItem().getEnchantBowCritical());
										temp.setDynamicMagicCritical(s.getInvItemEn() * s.getItem().getEnchantMagicCritical());
										temp.setDynamicPvpDmg(s.getInvItemEn() * s.getItem().getEnchantPvpDamage());
										temp.setDynamicPvpReduction(s.getInvItemEn() * s.getItem().getEnchantPvpReduction());
										  insertShopHistory((int)pc.getObjectId(), pc.getName(), s.getPc().getName(), s.getItem().getName(), s.getPrice(), item_count, s.getInvItemEn(), s.getInvItemBress(), true);
										temp.setPetObjectId(s.getPetObjId());
										temp.setItemTimek(s.getInvItemK());
										pc.getInventory().append(temp, true);
									}
								}
							} else {
								// 겹치는 아이템이 존재할 경우.
								pc.getInventory().count(temp, temp.getCount() + item_count, true);
							}
							
							// 221208 펫 목걸이 수정
							if (temp instanceof DogCollar) {
								DogCollar dogCollar = (DogCollar) temp;
								Connection con = null;
								try {
									con = DatabaseConnection.getLineage();
									dogCollar.toWorldJoin(con, pc);
								} catch (Exception e) {
									
								} finally {
									DatabaseConnection.close(con);
								}
							}
							
							if (s.getPc().getPc_objectId() != pc.getObjectId()) {
								// 아데나 지급.
								PcInstance pcShop = World.findPc(pc_objectId);
								
								// 판매자가 접속해있을 경우.
								if (pcShop != null) {
									Item item = ItemDatabase.find(s.getAdenType());
									if (item != null) {
										ItemInstance tempItem = ItemDatabase.newInstance(item);
										tempItem.setCount(s.getPrice() * item_count);
										tempItem.setEnLevel(0);
										tempItem.setBless(1);
										tempItem.setDefinite(true);
										// 인벤에 등록처리.
										pcShop.getInventory().append(tempItem, tempItem.getCount());

										if (item_count > 1)
											ChattingController.toChatting(pcShop,
													String.format("개인상점: '%s(%,d)' 판매대금 %s(%,d) 입금", s.getItem().getName(), item_count, s.getAdenType(), s.getPrice() * item_count),
													Lineage.CHATTING_MODE_MESSAGE);
										else {
											if (s.getItem().getType1().equalsIgnoreCase("weapon") || s.getItem().getType1().equalsIgnoreCase("armor"))
												ChattingController.toChatting(pcShop,
														String.format("개인상점: '+%d%s' 판매대금 %s(%,d) 입금", s.getInvItemEn(), s.getItem().getName(), s.getAdenType(), s.getPrice() * item_count),
														Lineage.CHATTING_MODE_MESSAGE);
											else
												ChattingController.toChatting(pcShop, String.format("개인상점: '%s' 판매대금 %s(%,d) 입금", s.getItem().getName(), s.getAdenType(), s.getPrice() * item_count),
														Lineage.CHATTING_MODE_MESSAGE);
										}
										
										if (item_count > 1)
											LetterController.toLetter("개인상점", pc_name, "판매완료", String.format("'%s(%,d)' 아이템이 판매완료되어 %s(%,d) 입금 되었습니다.", s.getItem().getName(), item_count, s.getAdenType(), s.getPrice() * item_count), 0);
										else {
											if (s.getItem().getType1().equalsIgnoreCase("weapon") || s.getItem().getType1().equalsIgnoreCase("armor"))
												LetterController.toLetter("개인상점", pc_name, "판매완료", String.format("'+%d%s' 아이템이 판매완료되어 %s(%,d) 입금 되었습니다.", s.getInvItemEn(), s.getItem().getName(), s.getAdenType(), s.getPrice() * item_count), 0);
											else
												LetterController.toLetter("개인상점", pc_name, "판매완료", String.format("'%s' 아이템이 판매완료되어 %s(%,d) 입금 되었습니다.", s.getItem().getName(), s.getAdenType(), s.getPrice() * item_count), 0);
										}
									}				
						
								} else {
									// 판매자가 오프라인일 경우.
									long aden_objectid = getAdenObjectId(s.getAdenType());
									if (aden_objectid == 0) {
										// 등록
										toInsertAden(s.getPrice() * item_count, s.getAdenType());
									} else {
										// 갱신
										toUpdateAden(s, s.getPrice() * item_count);
									}
									if (item_count > 1)
										LetterController.toLetter("개인상점", pc_name, "판매완료", String.format("'%s(%,d)' 아이템이 판매완료되어 %s(%,d) 입금 되었습니다.", s.getItem().getName(), item_count, s.getAdenType(), s.getPrice() * item_count), 0);
									else {
										if (s.getItem().getType1().equalsIgnoreCase("weapon") || s.getItem().getType1().equalsIgnoreCase("armor"))
											LetterController.toLetter("개인상점", pc_name, "판매완료", String.format("'+%d%s' 아이템이 판매완료되어 %s(%,d) 입금 되었습니다.", s.getInvItemEn(), s.getItem().getName(), s.getAdenType(), s.getPrice() * item_count), 0);
										else
											LetterController.toLetter("개인상점", pc_name, "판매완료", String.format("'%s' 아이템이 판매완료되어 %s(%,d) 입금 되었습니다.", s.getItem().getName(), s.getAdenType(), s.getPrice() * item_count), 0);
									}
								}								
								Log.appendPcShopTrade("[아이템 구매]", pc_name, pc_objectId, pc.getName(), pc.getObjectId(), PcTradeController.changePrice((int) s.getPrice()),
										PcTradeController.changePrice((int) (s.getPrice() * item_count)), PcTradeController.changeBless(s.getInvItemBress()), s.getInvItemEn(), s.getItem().getName(), 
										s.getInvItemCount() + item_count, item_count, s.getInvItemCount());
							} else {
								if (item_count > 1)
									ChattingController.toChatting(pc, String.format("개인상점: '%s(%,d)' 상점에서 삭제", s.getItem().getName(), item_count), Lineage.CHATTING_MODE_MESSAGE);
								else {
									if (s.getItem().getType1().equalsIgnoreCase("weapon") || s.getItem().getType1().equalsIgnoreCase("armor"))
										ChattingController.toChatting(pc, String.format("개인상점: '+%d%s' 상점에서 삭제", s.getInvItemEn(), s.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
									else
										ChattingController.toChatting(pc, String.format("개인상점: '%s' 상점에서 삭제", s.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
								}
								Log.appendPcShopTrade("[아이템 삭제]", pc_name, pc_objectId, pc.getName(), pc.getObjectId(), PcTradeController.changePrice((int) s.getPrice()),
										PcTradeController.changePrice((int) (s.getPrice() * item_count)), PcTradeController.changeBless(s.getInvItemBress()), s.getInvItemEn(), s.getItem().getName(), 
										s.getInvItemCount() + item_count, item_count, s.getInvItemCount());
							}


							// 디비 갱신.
							if (s.getInvItemCount() == 0) {
								// 삭제.
								PcMarketController.deleteItem(s);
								removeItem(s.getInvItemObjectId());
								list.remove(s.getInvItemObjectId());
								s = null;
							} else {
//								// 업데이트.
								PcMarketController.updateItem(pc, s);
							}
							if (getListSize() < 1) {
								close();		
								PcMarketController.shop_list.remove(pc.getObjectId());
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
	private long getAdenObjectId(String adenType) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_inventory WHERE cha_objId=? AND name=? AND bress=1");
			st.setLong(1, pc_objectId);
			st.setString(2, adenType);
			rs = st.executeQuery();
			if (rs.next())
				return rs.getLong("objId");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getAdenObjectId(String adenType)\r\n", PcShopInstance.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}


	public void insertShopHistory(int sellerId, String sellerName, String buyerName, String itemName, long price, long count, int enchant, int bless, boolean confirmState) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO pc_shop_history (판매자_아이디, 판매자_이름, 구매자_이름, 아이템_이름, 가격, 갯수, 인챈트, 블레스, 확인상태, 판매_시간) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())");
			st.setLong(1, sellerId);
			st.setString(2, sellerName);
			st.setString(3, buyerName);
			st.setString(4, itemName);
			st.setLong(5, price);
			st.setLong(6, count);
			st.setInt(7, enchant);
			st.setInt(8, bless);
			st.setString(9, confirmState ? "true" : "false");
			st.executeUpdate();
		} catch (Exception e) {
			System.out.println("Error in insertShopHistory: " + e.getMessage());
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	private void toInsertAden(long count, String adenType) {
	    PreparedStatement st = null;
	    try {
	        st = DatabaseConnection.getLineage().prepareStatement(INSERT_SQL);
	        st.setLong(1, ServerDatabase.nextItemObjId());
	        st.setLong(2, pc_objectId);
	        st.setString(3, adenType);
	        st.setLong(4, count);
	        st.executeUpdate();
	    } catch (Exception e) {
	        lineage.share.System.printf("%s : toInsertAden(long count, String adenType)\r\n", PcShopInstance.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        if (st != null) {
	            try {
	                st.close();
	            } catch (SQLException e) {
	                lineage.share.System.println(e);
	            }
	        }
	    }
	}

	private void toUpdateAden(PcShop s, long count) {
	    PreparedStatement st = null;
	    try {
	        st = DatabaseConnection.getLineage().prepareStatement(UPDATE_SQL);
	        st.setLong(1, count);
	        st.setLong(2, pc_objectId);
	        st.setString(3, s.getAdenType());
	        st.executeUpdate();
	    } catch (Exception e) {
	        lineage.share.System.printf("%s : toUpdateAden(shop s, long count)\r\n", PcShopInstance.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        if (st != null) {
	            try {
	                st.close();
	            } catch (SQLException e) {
	                lineage.share.System.println(e);
	            }
	        }
	    }
	}
}