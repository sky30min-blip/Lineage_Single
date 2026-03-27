package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lineage.bean.database.DeadLostItem;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class DeadLostItemDatabase {
	static private List<DeadLostItem> list;
	static private long lastSaveTime;
	
	static public void init(Connection con) {
		TimeLine.start("EnchantLostItemDatabase..");

		list = new ArrayList<DeadLostItem>();
		lastSaveTime = 0L;

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM dead_lost_item WHERE 지급여부=0");
			rs = st.executeQuery();
			while (rs.next()) {
				DeadLostItem el = new DeadLostItem();
				el.setCha_objId(rs.getLong("캐릭터_objId"));
				el.setCha_name(rs.getString("캐릭터"));
				el.setItem_objId(rs.getLong("아이템_objId"));
				el.setItem_name(rs.getString("아이템"));
				el.setEn_level(rs.getInt("인첸트"));
				el.setBless(rs.getInt("축복"));
				el.setCount(rs.getLong("수량"));
				
				try {
					el.setLost_time(rs.getTimestamp("잃은시간").getTime());
				} catch (Exception e) {
					lineage.share.System.printf("%s : 잃은시간 세팅 에러.\r\n", DeadLostItemDatabase.class.toString());
					lineage.share.System.printf("캐릭터: %s / 아이템_objId: %d / 아이템: %s\r\n", rs.getString("캐릭터"), rs.getLong("아이템_objId"), getStringName(el));
				}
				
				el.set지급여부(rs.getInt("지급여부") == 1 ? true : false);
				
				list.add(el);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", DeadLostItemDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	static public List<DeadLostItem> getList() {
		synchronized (list) {
			return new ArrayList<DeadLostItem>(list);
		}
	}
	
	static public List<DeadLostItem> find(object o) {
		List<DeadLostItem> temp = new ArrayList<DeadLostItem>();

		for (DeadLostItem el : getList()) {
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
	
	static public String getStringName(DeadLostItem el) {
		return String.format("%s+%d %s(%,d)", el.getBless() == 1 ? "" : el.getBless() == 0 ? "[축]" : "[저주]", el.getEn_level(), el.getItem_name(), el.getCount());
	}
	
	static public void append(object o, ItemInstance i) {
		if (o != null && i != null && i.getItem() != null ) {
		
			

			synchronized (list) {
				for (DeadLostItem el : list) {
					if (el.getItem_objId() == i.getObjectId() && !el.is지급여부()) {
						return;
					}
				}

				DeadLostItem el = new DeadLostItem();
				el.setCha_objId(o.getObjectId());
				el.setCha_name(o.getName());
				el.setItem_objId(i.getObjectId());
				el.setItem_name(i.getItem().getName());
				el.setEn_level(i.getEnLevel());
				el.setBless(i.getBless());
				el.setCount(i.getCount());
				el.setLost_time(System.currentTimeMillis());
		
				el.set지급여부(false);
				list.add(el);
				insertDB(el);
			}
		}
	}
	
	static public void insertDB(DeadLostItem el) {
		PreparedStatement st = null;
		Connection con = null;

		try {
			con = DatabaseConnection.getLineage();

			try {
				st = con.prepareStatement("INSERT INTO dead_lost_item SET 캐릭터_objId=?, 캐릭터=?, 아이템_objId=?, 아이템=?, 인첸트=?, 축복=?, 수량=?, 잃은시간=?, 지급여부=?");
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
				st.setInt(9, el.is지급여부() ? 1 : 0);
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.printf("%s : insertDB()\r\n", DeadLostItemDatabase.class.toString());
				lineage.share.System.printf("캐릭터: %s / 아이템_objId: %d / 아이템: %s\r\n", el.getCha_name(), el.getItem_objId(), getStringName(el));
				lineage.share.System.println(e);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : save(deadLostItem el)\r\n", DeadLostItemDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void dead_lost_item_log(DeadLostItem el) {
		PreparedStatement st = null;
		Connection con = null;

		try {
			con = DatabaseConnection.getLineage();

			try {
				st = con.prepareStatement("INSERT INTO dead_lost_item_log SET 캐릭터_objId=?, 캐릭터=?, 아이템_objId=?, 아이템=?, 인첸트=?, 축복=?, 수량=?, 복구시간=?, 지급여부=?");
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
				st.setString(9, "지급완료");
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.printf("%s : insertDB()\r\n", DeadLostItemDatabase.class.toString());
				lineage.share.System.printf("캐릭터: %s / 아이템_objId: %d / 아이템: %s\r\n", el.getCha_name(), el.getItem_objId(), getStringName(el));
				lineage.share.System.println(e);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : save(deadLostItem el)\r\n", DeadLostItemDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public DeadLostItem 지급(object o, long objId) {
		if (o != null && o.getInventory() != null) {
			synchronized (list) {
				try {
					for (DeadLostItem el : list) {
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
	
	static public boolean deleteDB(DeadLostItem el) {
		PreparedStatement st = null;
		Connection con = null;

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("DELETE FROM dead_lost_item WHERE 캐릭터_objId=? AND 아이템_objId=?");
			st.setLong(1, el.getCha_objId());
			st.setLong(2, el.getItem_objId());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : deleteDB(deadLostItem el)\r\n", DeadLostItemDatabase.class.toString());
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
				st = con.prepareStatement("DELETE FROM dead_lost_item");
				st.executeUpdate();
				st.close();
				
				for (DeadLostItem el : getList()) {
					if (!el.is지급여부()) {
						try {
							st = con.prepareStatement("INSERT INTO dead_lost_item SET 캐릭터_objId=?, 캐릭터=?, 아이템_objId=?, 아이템=?, 인첸트=?, 축복=?, 수량=?, 잃은시간=?,지급여부=?");
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
							st.setInt(9, el.is지급여부() ? 1 : 0);
							st.executeUpdate();
							st.close();
						} catch (Exception e) {
							lineage.share.System.printf("%s : 저장 에러\r\n", DeadLostItemDatabase.class.toString());
							lineage.share.System.printf("캐릭터: %s / 아이템_objId: %d / 아이템: %s\r\n", el.getCha_name(), el.getItem_objId(), getStringName(el));
						}
					}
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : save()\r\n", DeadLostItemDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}
}
