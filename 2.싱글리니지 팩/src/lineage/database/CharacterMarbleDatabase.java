package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import lineage.bean.database.CharacterMarble;
import lineage.bean.database.Exp;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.PcMarketController;
import lineage.world.controller.PcTradeController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.all_night.CharacterSaveMarble;

public class CharacterMarbleDatabase {
	
	/**
	 * 저장된 캐릭터 확인
	 * 2020-12-01
	 * by connector12@nate.com
	 */
	static public boolean checkData(long objId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM character_marble WHERE cha_objId=?");
			st.setLong(1, objId);
			rs = st.executeQuery();
			
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : checkData(long objId)\r\n", CharacterMarbleDatabase.class.toString());
			lineage.share.System.printf("cha_objId: %d\r\n", objId);
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}	
		return false;
	}
	
	/**
	 * 계정의 캐릭터 수 확인
	 * 2020-12-01
	 * by connector12@nate.com
	 */
	static public boolean checkCharacterCount(PcInstance pc) {
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		int length = 0;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT objID FROM characters WHERE account_uid=?");
			st.setInt(1, pc.getClient().getAccountUid());
			rs = st.executeQuery();
			
			while (rs.next()) {
				if (checkData(rs.getInt("objID"))) {
					continue;
				}
				
				length++;
			}
			
			if (length < 3) {
				return true;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : checkCharacterCount(PcInstance pc)\r\n", CharacterMarbleDatabase.class.toString());
			lineage.share.System.printf("캐릭터: %s\r\n", pc.getName());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}	
		return false;
	}
	
	static public CharacterMarble getData(long objId) {	
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		CharacterMarble cm = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM character_marble WHERE item_objId=?");
			st.setLong(1, objId);
			rs = st.executeQuery();
			
			if (rs.next()) {
				cm = new CharacterMarble();
				cm.setItem_objId(rs.getLong("item_objId"));
				cm.setCha_objId(rs.getLong("cha_objId"));
				cm.setName(rs.getString("캐릭터"));
				cm.setLevel(rs.getInt("레벨"));
				cm.setExp(rs.getString("경험치"));
				cm.setClassType(rs.getInt("클래스"));
				cm.setSex(rs.getInt("성별"));
				return cm;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : getData(long objId)\r\n", CharacterMarbleDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}	
		return null;
	}
	
	static public boolean checkInventory(PcInstance pc, ItemInstance item) {
		if (Lineage.is_character_marble_inventory) {
			for (ItemInstance i : pc.getInventory().getList()) {
				if (i != null && i.getItem() != null && i.getObjectId() != item.getObjectId()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	static public boolean checkPcShop(PcInstance pc) {
		if (Lineage.is_character_marble_pc_shop) {
			if (PcMarketController.getShop(pc.getObjectId()) != null) {
				return false;
			}
			
			PreparedStatement st = null;
			ResultSet rs = null;
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("SELECT COUNT(*) AS cnt FROM pc_shop WHERE pc_objId=?");
				st.setLong(1, pc.getObjectId());
				rs = st.executeQuery();
				
				if (rs.next()) {
					if (rs.getInt(1) > 0) {
						return false;
					}
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : checkPcShop(PcInstance pc)\r\n", CharacterMarbleDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}
		
		return true;
	}
	
	static public boolean checkSellPcTrade(long objId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT COUNT(*) AS cnt FROM pc_trade WHERE sell_objId=? AND NOT state=?");
			st.setLong(1, objId);
			st.setString(2, PcTradeController.STATE_COMPLETE);
			rs = st.executeQuery();

			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					return false;
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : checkSellPcTrade(long objId)\r\n", CharacterMarbleDatabase.class.toString());
			lineage.share.System.printf("cha_objId: %d\r\n", objId);
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}	
		return true;
	}
	
	static public boolean checkBuyPcTrade(long objId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT COUNT(*) AS cnt FROM pc_trade WHERE buy_objId=? AND NOT state=?");
			st.setLong(1, objId);
			st.setString(2, PcTradeController.STATE_COMPLETE);
			rs = st.executeQuery();
			
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					return false;
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : checkBuyPcTrade(long objId)\r\n", CharacterMarbleDatabase.class.toString());
			lineage.share.System.printf("cha_objId: %d\r\n", objId);
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}	
		return true;
	}
	
	public static boolean checkWarehouse(int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		int count = 0;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT COUNT(*) AS cnt FROM warehouse WHERE account_uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next())
				count = rs.getInt(1);
			
			if (count < Lineage.warehouse_max) {
				return true;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : checkWarehouse(int uid)\r\n", CharacterMarbleDatabase.class.toString());
			lineage.share.System.printf("account_uid: %d\r\n", uid);
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		return false;
	}
	
	static public void insertDB(PcInstance pc, ItemInstance item) {
		if (pc != null) {
			PreparedStatement st = null;
			Connection con = null;

			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("INSERT INTO character_marble SET item_objId=?, cha_objId=?, 캐릭터=?, 레벨=?, 경험치=?, 클래스=?, 성별=?, 저장날짜=?");
				st.setLong(1, item.getObjectId());
				st.setLong(2, pc.getObjectId());
				st.setString(3, pc.getName());
				st.setInt(4, pc.getLevel());
				st.setString(5, expString(pc.getLevel(), pc.getExp()));
				st.setInt(6, pc.getClassType());
				st.setInt(7, pc.getClassSex());
				st.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
				st.executeUpdate();
				st.close();
				
				st = con.prepareStatement("INSERT INTO warehouse SET account_uid=?, inv_id=?, name=?, type=?, gfxid=?, count=?, quantity=?, en=?, definite=?, bress=?, "
						+ "durability=?, time=?, pet_id=?, letter_id=?");
				st.setInt(1, pc.getClient().getAccountUid());
				st.setLong(2, item.getObjectId());
				st.setString(3, item.getItem().getName());
				st.setInt(4, 3);
				st.setInt(5, item.getItem().getInvGfx());
				st.setLong(6, 1);
				st.setInt(7, item.getQuantity());
				st.setInt(8, item.getEnLevel());
				st.setInt(9, item.isDefinite() ? 1 : 0);
				st.setInt(10, item.getBless());
				st.setInt(11, item.getDurability());
				st.setInt(12, item.getTime());
				st.setLong(13, item.getPetObjectId());
				st.setInt(14, item.getLetterUid());
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.printf("%s : insertDB(PcInstance pc, long objId)\r\n", CharacterMarbleDatabase.class.toString());
				lineage.share.System.printf("캐릭터: %s\r\n", pc.getName());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}
	
	static public boolean changeDB(PcInstance pc, ItemInstance item) {
		if (pc != null) {
			PreparedStatement st = null;
			Connection con = null;
			ResultSet rs = null;
			long objId = 0;
			int result = 0;
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("SELECT * FROM character_marble WHERE item_objId=?");
				st.setLong(1, item.getObjectId());
				rs = st.executeQuery();
				
				if (rs.next()) {
					objId = rs.getLong("cha_objId");
				}
				
				st.close();				
				st = con.prepareStatement("DELETE FROM character_marble WHERE item_objId=? AND cha_objId=?");
				st.setLong(1, item.getObjectId());
				st.setLong(2, objId);
				result = st.executeUpdate();
				st.close();
				
				if (objId == 0 || result == 0) {
					ChattingController.toChatting(pc, "\\fY해당 캐릭터의 정보가 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return false;
				}
				
				st = con.prepareStatement("UPDATE characters SET account=?, account_uid=? WHERE objID=?");
				st.setString(1, pc.getClient().getAccountId());
				st.setInt(2, pc.getClient().getAccountUid());
				st.setLong(3, objId);
				st.executeUpdate();
				st.close();
			} catch (Exception e) {
				lineage.share.System.printf("%s : changeDB(PcInstance pc, ItemInstance item)\r\n", CharacterMarbleDatabase.class.toString());
				lineage.share.System.printf("캐릭터: %s item_objId: %d / cha_objId: %d\r\n", pc.getName(), item.getObjectId(), objId);
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
			return true;
		}
		
		return false;
	}
	
	static public void removeItem(long objId) {
		PreparedStatement st = null;
		Connection con = null;

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("DELETE FROM characters_inventory WHERE objId=?");
			st.setLong(1, objId);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : removeItem(long objId)\r\n", CharacterMarbleDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	/**
	 * 캐릭터 저장 구슬 아이템 이름 표기
	 * 2020-12-01
	 * by connector12@nate.com
	 */
	static public String getItemName(object o) {
		String name = null;
		
		if (o != null && o instanceof CharacterSaveMarble) {
			ItemInstance item = (ItemInstance) o;
			
			if (item != null && item.getItem() != null) {
				name = item.getItem().getName();
				CharacterMarble cm = getData(item.getObjectId());
				
				if (cm != null) {
					String sex = null;
					switch (cm.getClassType()) {
					case Lineage.LINEAGE_CLASS_ROYAL:
						if (cm.getSex() == 0) {
							sex = "남군주";
						} else {
							sex = "여군주";
						}
						break;
					case Lineage.LINEAGE_CLASS_KNIGHT:
						if (cm.getSex() == 0) {
							sex = "남기사";
						} else {
							sex = "여기사";
						}
						break;
					case Lineage.LINEAGE_CLASS_ELF:
						if (cm.getSex() == 0) {
							sex = "남요정";
						} else {
							sex = "여요정";
						}
						break;
					case Lineage.LINEAGE_CLASS_WIZARD:
						if (cm.getSex() == 0) {
							sex = "남법사";
						} else {
							sex = "여법사";
						}
						break;
					}
					
					if (Lineage.is_character_marble_name) {
						name = String.format("[ID: %s] [%s] Lv:%d Exp:%s%%", cm.getName(), sex, cm.getLevel(), cm.getExp());
					} else {
						name = String.format("[%s] Lv:%d Exp:%s%%", sex, cm.getLevel(), cm.getExp());
					}
				}
			}
		}
		
		return name;
	}
	
	/**
	 * 캐릭터 저장 구슬 아이템 이름 표기
	 * 2020-12-01
	 * by connector12@nate.com
	 */
	static public String getItemName(long objId) {
		String name = null;

		CharacterMarble cm = getData(objId);
		
		if (cm != null) {
			String sex = null;
			switch (cm.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				if (cm.getSex() == 0) {
					sex = "남군주";
				} else {
					sex = "여군주";
				}
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				if (cm.getSex() == 0) {
					sex = "남기사";
				} else {
					sex = "여기사";
				}
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				if (cm.getSex() == 0) {
					sex = "남요정";
				} else {
					sex = "여요정";
				}
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				if (cm.getSex() == 0) {
					sex = "남법사";
				} else {
					sex = "여법사";
				}
				break;
			}
			
			if (Lineage.is_character_marble_name) {
				name = String.format("[ID: %s] [%s] Lv:%d Exp:%s%%", cm.getName(), sex, cm.getLevel(), cm.getExp());
			} else {
				name = String.format("[%s] Lv:%d Exp:%s%%", sex, cm.getLevel(), cm.getExp());
			}
		}

		return name;
	}
	
	static public String expString(int level, double exp) {
		String msg = "";
		
		try {
			Exp e = ExpDatabase.find(level - 1);
			Exp e1 = ExpDatabase.find(level);
			if (e != null && e1 != null) {
				double temp = exp - e.getBonus();
				msg = String.format("%.1f", (temp / e1.getExp()) * 100.0);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : expString(int level, double exp)\r\n", CharacterMarbleDatabase.class.toString());
			lineage.share.System.println(e);
		}
		
		return msg;
	}
}
