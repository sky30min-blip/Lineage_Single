package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lineage.bean.database.EnchantLostItem;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class EnchantLostItemDatabase {
	static private List<EnchantLostItem> list;
	static private long lastSaveTime;
	
	static public void init(Connection con) {
		TimeLine.start("EnchantLostItemDatabase..");

		list = new ArrayList<EnchantLostItem>();
		lastSaveTime = 0L;

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM enchant_lost_item WHERE 지급여부=0");
			rs = st.executeQuery();
			while (rs.next()) {
				EnchantLostItem el = new EnchantLostItem();
				el.setCha_objId(rs.getLong("캐릭터_objId"));
				el.setCha_name(rs.getString("캐릭터"));
				el.setItem_objId(rs.getLong("아이템_objId"));
				el.setItem_name(rs.getString("아이템"));
				el.setEn_level(rs.getInt("인첸트"));
				el.setBless(rs.getInt("축복"));
				el.setCount(rs.getLong("수량"));
				el.setScroll_name(rs.getString("주문서"));
				el.setScroll_bless(rs.getInt("주문서_축복"));
				
				try {
					el.setLost_time(rs.getTimestamp("잃은시간").getTime());
				} catch (Exception e) {
					lineage.share.System.printf("%s : 잃은시간 세팅 에러.\r\n", EnchantLostItemDatabase.class.toString());
					lineage.share.System.printf("캐릭터: %s / 아이템_objId: %d / 아이템: %s\r\n", rs.getString("캐릭터"), rs.getLong("아이템_objId"), getStringName(el));
				}
				
				el.set지급여부(rs.getInt("지급여부") == 1 ? true : false);
				
				list.add(el);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", EnchantLostItemDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	static public List<EnchantLostItem> getList() {
		synchronized (list) {
			return new ArrayList<EnchantLostItem>(list);
		}
	}
	
	static public List<EnchantLostItem> find(object o) {
		List<EnchantLostItem> temp = new ArrayList<EnchantLostItem>();

		for (EnchantLostItem el : getList()) {
			if (el != null && el.getCha_objId() == o.getObjectId() && !el.is지급여부() && isTime(el.getLost_time())) {
				temp.add(el);
			}
		}
		
		 Collections.sort(temp, Collections.reverseOrder());
		
		return temp;
	}
	
	static public boolean isTime(long time) {
		if (System.currentTimeMillis() < time + Lineage.recovery_time) {
			return true;
		}
		
		return false;
	}
	
	static public String getStringName(EnchantLostItem el) {
		return String.format("%s+%d %s(%,d)", el.getBless() == 1 ? "" : el.getBless() == 0 ? "[축]" : "[저주]", el.getEn_level(), el.getItem_name(), el.getCount());
	}
	
	static public void append(object o, ItemInstance i, ItemInstance scroll) {
		if (o != null && i != null && i.getItem() != null && scroll != null && scroll.getItem() != null) {
			if (i.getItem().getType1().equalsIgnoreCase("weapon")) {
				switch (i.getItem().getSafeEnchant()) {
				case 0:
					if (i.getEnLevel() < Lineage.recovery_weapon_safe_0_en_min) {
						return;
					}
					break;
				case 6:
					if (i.getEnLevel() < Lineage.recovery_weapon_safe_6_en_min) {
						return;
					}
					break;
				}
			} else if (i.isAcc()) {
				if (i.getEnLevel() < Lineage.recovery_acc_en_min) {
					return;
				}
			} else if (!i.isAcc() && i.getItem().getType1().equalsIgnoreCase("armor")) {
				switch (i.getItem().getSafeEnchant()) {
				case 0:
					if (i.getEnLevel() < Lineage.recovery_armor_safe_0_en_min) {
						return;
					}
					break;
				case 4:
					if (i.getEnLevel() < Lineage.recovery_armor_safe_4_en_min) {
						return;
					}
					break;
				case 6:
					if (i.getEnLevel() < Lineage.recovery_armor_safe_6_en_min) {
						return;
					}
					break;
				}
			}

			synchronized (list) {
				for (EnchantLostItem el : list) {
					if (el.getItem_objId() == i.getObjectId() && !el.is지급여부()) {
						return;
					}
				}

				EnchantLostItem el = new EnchantLostItem();
				el.setCha_objId(o.getObjectId());
				el.setCha_name(o.getName());
				el.setItem_objId(i.getObjectId());
				el.setItem_name(i.getItem().getName());
				el.setEn_level(i.getEnLevel());
				el.setBless(i.getBless());
				el.setCount(i.getCount());
				el.setLost_time(System.currentTimeMillis());
				el.setScroll_name(scroll.getItem().getName());
				el.setScroll_bless(scroll.getBless());
				el.set지급여부(false);
				list.add(el);
				insertDB(el);
			}
		}
	}
	
	static public void insertDB(EnchantLostItem el) {
		PreparedStatement st = null;
		Connection con = null;

		try {
			con = DatabaseConnection.getLineage();

			try {
				st = con.prepareStatement("INSERT INTO enchant_lost_item SET 캐릭터_objId=?, 캐릭터=?, 아이템_objId=?, 아이템=?, 인첸트=?, 축복=?, 수량=?, 잃은시간=?, 주문서=?, 주문서_축복=?, 지급여부=?");
				st.setLong(1, el.getCha_objId());
				st.setString(2, el.getCha_name());
				st.setLong(3, el.getItem_objId());
				st.setString(4, el.getItem_name());
				st.setInt(5, el.getEn_level());
				st.setInt(6, el.getBless());
				st.setLong(7, el.getCount());
				if (el.getLost_time() == 0)
					st.setString(8, "0000-00-00 00:00:00");
				else
					st.setTimestamp(8, new Timestamp(el.getLost_time()));
				st.setString(9, el.getScroll_name());
				st.setInt(10, el.getScroll_bless());
				st.setInt(11, el.is지급여부() ? 1 : 0);
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.printf("%s : insertDB()\r\n", EnchantLostItemDatabase.class.toString());
				lineage.share.System.printf("캐릭터: %s / 아이템_objId: %d / 아이템: %s\r\n", el.getCha_name(), el.getItem_objId(), getStringName(el));
				lineage.share.System.println(e);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : save(EnchantLostItem el)\r\n", EnchantLostItemDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	static public EnchantLostItem 지급(object o, long objId) {
		if (o != null && o.getInventory() != null) {
			synchronized (list) {
				try {
					for (EnchantLostItem el : list) {
						if (el.getCha_objId() == o.getObjectId() && el.getItem_objId() == objId && !el.is지급여부() && isTime(el.getLost_time())) {
							return el;
						}
					}
				} catch (Exception e) {
					
				}
			}
		}
		return null;
	}
	
	static public boolean deleteDB(EnchantLostItem el) {
		PreparedStatement st = null;
		Connection con = null;

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("DELETE FROM enchant_lost_item WHERE 캐릭터_objId=? AND 아이템_objId=?");
			st.setLong(1, el.getCha_objId());
			st.setLong(2, el.getItem_objId());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : deleteDB(EnchantLostItem el)\r\n", EnchantLostItemDatabase.class.toString());
			lineage.share.System.printf("캐릭터: %s / 아이템_objId: %d / 아이템: %s\r\n", el.getCha_name(), el.getItem_objId(), getStringName(el));
			lineage.share.System.println(e);
			return false;
		} finally {
			DatabaseConnection.close(con, st);
		}
		
		return true;
	}
	
	static public void save() {
		long time = System.currentTimeMillis();
		
		if (lastSaveTime < time) {
			lastSaveTime = time + 5000;
			
			PreparedStatement st = null;
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("DELETE FROM enchant_lost_item");
				st.executeUpdate();
				st.close();
				
				for (EnchantLostItem el : getList()) {
					if (!el.is지급여부()) {
						try {
							st = con.prepareStatement("INSERT INTO enchant_lost_item SET 캐릭터_objId=?, 캐릭터=?, 아이템_objId=?, 아이템=?, 인첸트=?, 축복=?, 수량=?, 잃은시간=?, 주문서=?, 주문서_축복=?, 지급여부=?");
							st.setLong(1, el.getCha_objId());
							st.setString(2, el.getCha_name());
							st.setLong(3, el.getItem_objId());
							st.setString(4, el.getItem_name());
							st.setInt(5, el.getEn_level());
							st.setInt(6, el.getBless());
							st.setLong(7, el.getCount());
							if (el.getLost_time() == 0)
								st.setString(8, "0000-00-00 00:00:00");
							else
								st.setTimestamp(8, new Timestamp(el.getLost_time()));
							st.setString(9, el.getScroll_name());
							st.setInt(10, el.getScroll_bless());
							st.setInt(11, el.is지급여부() ? 1 : 0);
							st.executeUpdate();
							st.close();
						} catch (Exception e) {
							lineage.share.System.printf("%s : 저장 에러\r\n", EnchantLostItemDatabase.class.toString());
							lineage.share.System.printf("캐릭터: %s / 아이템_objId: %d / 아이템: %s\r\n", el.getCha_name(), el.getItem_objId(), getStringName(el));
						}
					}
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : save()\r\n", EnchantLostItemDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}
}
