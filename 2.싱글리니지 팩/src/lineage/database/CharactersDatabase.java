package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.FirstInventory;
import lineage.bean.database.FirstSpawn;
import lineage.bean.database.FirstSpell;
import lineage.bean.database.Item;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Agit;
import lineage.bean.lineage.Book;
import lineage.bean.lineage.Buff;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Inventory;
import lineage.network.LineageClient;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_InventoryList;
import lineage.network.packet.server.S_ObjectAction;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.MapPolicy;
import lineage.world.controller.BookController;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Letter;
import lineage.world.object.magic.EarthGuardian;
import lineage.world.object.magic.Blue;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.BuffFight;
import lineage.world.object.magic.ChattingClose;
import lineage.world.object.magic.ClearMind;
import lineage.world.object.magic.CurseBlind;
import lineage.world.object.magic.CurseParalyze;
import lineage.world.object.magic.CursePoison;
import lineage.world.object.magic.EarthSkin;
import lineage.world.object.magic.EnchantDexterity;
import lineage.world.object.magic.EnchantMighty;
import lineage.world.object.magic.Eva;
import lineage.world.object.magic.ExpDropBuff_10;
import lineage.world.object.magic.ExpDropBuff_20;
import lineage.world.object.magic.ExpDropBuff_50;
import lineage.world.object.magic.Exp_Potion;
import lineage.world.object.magic.FloatingEyeMeat;
import lineage.world.object.magic.FrameSpeedOverStun;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.HastePotionMagic;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.IceLance;
import lineage.world.object.magic.ImmuneToHarm;
import lineage.world.object.magic.IronSkin;
import lineage.world.object.magic.Light;
import lineage.world.object.magic.MaanBirth;
import lineage.world.object.magic.MaanBirthDelay;
import lineage.world.object.magic.MaanEarth;
import lineage.world.object.magic.MaanEarthDelay;
import lineage.world.object.magic.MaanFire;
import lineage.world.object.magic.MaanFireDelay;
import lineage.world.object.magic.MaanLife;
import lineage.world.object.magic.MaanLifeDelay;
import lineage.world.object.magic.MaanShape;
import lineage.world.object.magic.MaanShapeDelay;
import lineage.world.object.magic.MaanWatar;
import lineage.world.object.magic.MaanWatarDelay;
import lineage.world.object.magic.MaanWind;
import lineage.world.object.magic.MaanWindDelay;
import lineage.world.object.magic.ResistElemental;
import lineage.world.object.magic.ShadowArmor;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.Shield;
import lineage.world.object.magic.Slow;
import lineage.world.object.magic.Wafer;
import lineage.world.object.magic.Wisdom;
import lineage.world.object.magic.RevengeCooldown;
import lineage.world.object.magic.monster.CurseGhoul;

public final class CharactersDatabase {

	/**
	 * 디비에서 원하는 인첸트아이템에 전체 갯수를 리턴함.
	 * 
	 * @param en
	 * @param isWeapon
	 * @return
	 */
	static public int getInventoryEnchantCount(int en, boolean isWeapon) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT COUNT(*) as cnt FROM characters_inventory WHERE 구분1=? AND en=?");
			st.setString(1, isWeapon ? "weapon" : "armor");
			st.setInt(2, en);
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt("cnt");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getInventoryEnchantCount(int en, boolean isWeapon)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}

	/**
	 * 홈페이지 연동 부분에서 사용함. : 글 작성시 계정과 연결된 케릭을 선택하기위해 : 케릭 목록을 리턴함.
	 * 
	 * @param account
	 * @return
	 */
	static public List<String> getCharacters(String account) {
		List<String> list = new ArrayList<String>();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT name FROM characters WHERE account=?");
			st.setString(1, account);
			rs = st.executeQuery();
			while (rs.next())
				list.add(rs.getString("name"));
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterClass(String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return list;
	}

	/**
	 * 홈페이지 연동 부분에서 사용함. : BoardController.java 쪽에서 글작성한 사용자에 클레스를 확인하기 위해. : 클레스가 군주인지, 기사인지 확인하며 성별도 함께 확인해야함. : 리턴형 값에 의해서 적절한 이미지를 처리함.
	 * 
	 * @param name
	 * @return [클레스][성별]
	 */
	static public String getCharacterClass(String name) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT class, sex FROM characters WHERE name=?");
			st.setString(1, name);
			rs = st.executeQuery();
			if (rs.next())
				return String.format("%d%d", rs.getInt("class"), rs.getInt("sex"));
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterClass(String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return "00";
	}

	/**
	 * 디비에있는 케릭터를 찾아서 좌표 변경하는 함수.
	 * 
	 * @param con
	 * @param name
	 * @param x
	 * @param y
	 * @param map
	 */
	static public void updateLocation(Connection con, String name, int x, int y, int map) {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE characters SET locX=?, locY=?, locMAP=? WHERE LOWER(name)=?");
			st.setInt(1, x);
			st.setInt(2, y);
			st.setInt(3, map);
			st.setString(4, name.toLowerCase());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateLocation(Connection con, String name, int x, int y, int map)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 디비에있는 케릭터를 찾아서 좌표 변경하는 함수.
	 * 
	 * @param con
	 * @param objId
	 * @param x
	 * @param y
	 * @param map
	 */
	static public void updateLocation(Connection con, int objId, int x, int y, int map) {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE characters SET locX=?, locY=?, locMAP=? WHERE objID=?");
			st.setInt(1, x);
			st.setInt(2, y);
			st.setInt(3, map);
			st.setInt(4, objId);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateLocation(Connection con, int objId, int x, int y, int map)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * 디비에있는 케릭터 이름 목록 만들어서 리턴함.
	 * 
	 * @param con
	 * @param r_list
	 */
	static public void getNameAllList(Connection con, List<String> r_list) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters ORDER BY name");
			rs = st.executeQuery();
			while (rs.next())
				r_list.add(rs.getString("name"));
		} catch (Exception e) {
			lineage.share.System.printf("%s : getNameAllList(Connection con, List<String> r_list)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}

	/**
	 * 경매처리구간에서 Agit객체에 사용자 정보를 등록해야하는 일이 있음.<br/>
	 * 그 구간처리를 여기서 맡음.
	 * 
	 * @param con
	 * @param name
	 * @param agit
	 */
	static public void updateAgit(Connection con, String name, Agit agit) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if (rs.next()) {
				agit.setClanId(rs.getInt("clanID"));
				agit.setClanName(rs.getString("clanNAME"));
				agit.setChaName(rs.getString("name"));
				agit.setChaObjectId(rs.getInt("objID"));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateAgit(Connection con, String name, Agit agit)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	static public void updateGiranDungeonTime() {
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET giran_dungeon_time=?");
			st.setInt(1, Lineage.giran_dungeon_time);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateGiranDungeonTime()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	/**
	 * 자정 기란감옥 초기화 전: 오프라인 캐릭터가 던전 맵에 저장돼 있으면 기란 마을로 옮김. nowHP가 0이면 최소 체력으로 복구.
	 */
	static public void evacuateOfflineFromGiranDungeonMapsToGiranTown() {
		PreparedStatement st = null;
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement(
					"UPDATE characters SET locX=33430, locY=32817, locMAP=4, "
							+ "nowHP=IF(nowHP<1, LEAST(maxHP, GREATEST(`level`,1)), nowHP) "
							+ "WHERE locMAP IN (53,54,55,56,653,654,655,656)");
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : evacuateOfflineFromGiranDungeonMapsToGiranTown()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	static public void updateGiranDungeonScrollCount() {
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET giran_dungeon_count=0");
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateGiranDungeonScrollCount()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateAutoCount() {
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET auto_count=0");
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateAuto_count()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	/**
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	static public int getCharacterObjectId(Connection con, String name) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT objID FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt("objID");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterObjectId(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}
	
	/**
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	static public int getAccountUid(Connection con, long objID) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT account_uid FROM characters WHERE objID=?");
			st.setLong(1, objID);
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt("account_uid");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterObjectId(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}

	/**
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	static public long getCharacterRegisterDate(Connection con, String name) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT register_date FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if (rs.next())
				return rs.getLong("register_date");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterRegisterDate(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}

	/**
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	static public int getCharacterLevel(Connection con, String name) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT level FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterLevel(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}

	/**
	 * 디비에서 이름에 해당하는 케릭터의 혈맹아이디 찾아서 리턴.
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	static public int getCharacterClanId(Connection con, String name) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT clanID FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterClanId(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}
	
	/**
	 * 계정의 캐릭터들이 혈맹에 가입되어 있는지 확인하는 함수
	 * 2017-10-17
	 * by all-night
	 */
	static public boolean isClanJoin(PcInstance pc, String clanName) {
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		boolean result = true;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT clanNAME FROM characters WHERE account_uid=?");
			st.setLong(1, pc.getAccountUid());
			rs = st.executeQuery();
			
			while (rs.next()) {
				if (!rs.getString("clanNAME").equalsIgnoreCase(Lineage.new_clan_name) && !rs.getString("clanNAME").equalsIgnoreCase(Lineage.teamBattle_A_team) 
						&& !rs.getString("clanNAME").equalsIgnoreCase(Lineage.teamBattle_B_team) && !rs.getString("clanNAME").equalsIgnoreCase(clanName) 
						&& rs.getString("clanNAME").length() > 0) {
					result = false;
				}
			}	
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterClanId(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return result;
	}

	/**
	 * 디비에서 이름에 해당하는 케릭터의 클레스 종류 리턴.
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	static public int getCharacterClass(Connection con, String name) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT class FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterClass(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}

	/**
	 * 아이피당 캐릭터수 확인.
	 * 2019-11-05
	 * by connector12@nate.com
	 */
	static public boolean isCharacterCount(String ip) {
		if (Lineage.ip_character_count <= 0 || ip == null || ip.length() <= 0)
			return true;

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT COUNT(*) FROM characters WHERE last_ip=?");
			st.setString(1, ip);
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt(1) < Lineage.ip_character_count;
		} catch (Exception e) {
			lineage.share.System.printf("%s : isCharacterCount(String ip)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		return true;
	}
	
	/**
	 * 케릭터 테이블에서 해당하는 이름이 존재하는지 확인하는 함수.
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	static public boolean isCharacterName(Connection con, String name) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT name FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isCharacterName(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return false;
	}
	
	static public void isCharacterMember(object o, String name) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		int accountId = 0;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT account_uid FROM characters WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			
			if (rs.next())
				accountId = rs.getInt(1);
			
			st.close();
			rs.close();
			
			if (accountId > 0) {
				st = con.prepareStatement("UPDATE accounts SET member=true WHERE uid=?");
				st.setInt(1, accountId);
				st.executeUpdate();
				
				ChattingController.toChatting(o, String.format("[%s] 캐릭터 고정멤버 신청 완료.", name), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, String.format("[%s] 캐릭터가 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : isCharacterMember(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}



	/**
	 * 쿠베라 한글아이디만 (쿠베라)
	 * 2023-08-01
	 * by 오픈카톡 https://open.kakao.com/o/sbONOzMd
	 */
	static public boolean isInvalidName(Connection con, String name) {
		// 이름 검수.
		for (int i = 0; i < name.length(); ++i) {
		    char nameChar = name.charAt(i);
		    if (!Character.UnicodeBlock.of(nameChar).equals(Character.UnicodeBlock.HANGUL_SYLLABLES)) {
		        // 한글이 아닌 문자가 포함된 경우
		        return true;
		    }
		}
		// 디비에서 확인.
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT name FROM bad_name WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isInvalidName(Connection con, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return false;
	}

	/**
	 * 케릭터 디비에 해당 오브젝트가 존재하는지 확인해주는 함수.
	 * 
	 * @param con
	 * @param obj_id
	 * @return
	 */
	static public boolean isCharacterObjectId(Connection con, long obj_id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters WHERE objID=?");
			st.setLong(1, obj_id);
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isCharacterObjectId(Connection con, long obj_id)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return false;
	}

	/**
	 * 케릭터 정보 등록 처리 함수.
	 * 
	 * @param con
	 * @param obj_id
	 * @param name
	 * @param type
	 * @param sex
	 * @param hp
	 * @param mp
	 * @param Str
	 * @param Dex
	 * @param Con
	 * @param Wis
	 * @param Cha
	 * @param Int
	 * @param gfx
	 * @param x
	 * @param y
	 * @param map
	 * @param account_id
	 * @param account_uid
	 */
	static public void insertCharacter(Connection con, long obj_id, String name, int type, int sex, int hp, int mp, int Str, int Dex, int Con, int Wis, int Cha, int Int, int gfx, FirstSpawn fs,
			String account_id, int account_uid, long time) {
		PreparedStatement st = null;
		try {
			// characters
			st = con.prepareStatement(
					"INSERT INTO characters SET name=?, account=?, account_uid=?, objID=?, nowHP=?, maxHP=?, nowMP=?, maxMP=?, str=?, dex=?, con=?, wis=?, inter=?, cha=?, sex=?, class=?, locX=?, locY=?, locMAP=?, gfx=?, register_date=?, food=?, gfxMode=?, save_interface=''");
			st.setString(1, name);
			st.setString(2, account_id);
			st.setInt(3, account_uid);
			st.setLong(4, obj_id);
			st.setInt(5, hp);
			st.setInt(6, hp);
			st.setInt(7, mp);
			st.setInt(8, mp);
			st.setInt(9, Str);
			st.setInt(10, Dex);
			st.setInt(11, Con);
			st.setInt(12, Wis);
			st.setInt(13, Int);
			st.setInt(14, Cha);
			st.setInt(15, sex);
			st.setInt(16, type);
			// 캐릭터 생성시 스폰지점 등록		
			if (fs == null) {
				int[] loc = Lineage.getHomeXY();
				st.setInt(17, loc[0]);
				st.setInt(18, loc[1]);
				st.setInt(19, loc[2]);
			} else {
				st.setInt(17, fs.getX());
				st.setInt(18, fs.getY());
				st.setInt(19, fs.getMap());
			}
			
			st.setInt(20, gfx);
			st.setLong(21, time);
			st.setInt(22, Lineage.server_version > 230 ? 5 : 65);
			st.setInt(23, 0);
			st.executeUpdate();
			st.close();
			// characters_buff
			st = con.prepareStatement("INSERT INTO characters_buff SET name=?, objID=?");
			st.setString(1, name);
			st.setLong(2, obj_id);
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			lineage.share.System.printf(
					"%s : insertCharacter(Connection con, long obj_id, String name, int type, int sex, int hp, int mp, int Str, int Dex, int Con, int Wis, int Cha, int Int, int gfx, FirstSpawn fs, String account_id, int account_uid, long time)\r\n",
					CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * 초기 지급 아이템 처리 함수.
	 * 
	 * @param con
	 * @param obj_id
	 * @param name
	 * @param type
	 */
	static public void insertInventory(Connection con, long obj_id, String name, int type) {
		PreparedStatement st = null;
		try {
			List<FirstInventory> list = null;
			switch (type) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				list = Lineage.royal_first_inventory;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				list = Lineage.knight_first_inventory;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				list = Lineage.elf_first_inventory;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				list = Lineage.wizard_first_inventory;
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				list = Lineage.darkelf_first_inventory;
				break;
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
				list = Lineage.dragonknight_first_inventory;
				break;
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:
				list = Lineage.blackwizard_first_inventory;
				break;
			}
			if (list != null) {
				for (FirstInventory fi : list) {
					Item i = ItemDatabase.find(fi.getName());
					if (i != null) {
						for (int j = i.isPiles() ? 1 : fi.getCount(); j > 0; --j) {
							Long uid = ServerDatabase.nextItemObjId();
							st = con.prepareStatement("INSERT INTO characters_inventory SET objId=?, cha_objId=?, cha_name=?, name=?, count=?, nowtime=?, definite=?, equipped=?");
							st.setLong(1, uid);
							st.setLong(2, obj_id);
							st.setString(3, name);
							st.setString(4, fi.getName());
							st.setInt(5, i.isPiles() ? fi.getCount() : 1);
							st.setInt(6, i.getNameIdNumber() == 67 || i.getNameIdNumber() == 2 || i.getNameIdNumber() == 326 ? 600 : 0);
							st.setInt(7, 1);
							st.setInt(8, 0);
							st.executeUpdate();
							st.close();
						}
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : insertInventory(Connection con, long obj_id, String name, int type)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	static public void insertSkill(Connection con, long obj_id, String name, int type) {
		PreparedStatement st = null;
		try {
			List<FirstSpell> spell = null;
			switch (type) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				spell = Lineage.royal_first_spell;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				spell = Lineage.knight_first_spell;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				spell = Lineage.elf_first_spell;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				spell = Lineage.wizard_first_spell;
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				spell = Lineage.darkelf_first_spell;
				break;
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
				spell = Lineage.dragonknight_first_spell;
				break;
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:
				spell = Lineage.blackwizard_first_spell;
				break;
			}
			if (spell != null) {
				for (FirstSpell fs : spell) {
					st = con.prepareStatement("INSERT INTO characters_skill SET cha_objId=?, cha_name=?, skill=?");
					st.setLong(1, obj_id);
					st.setString(2, name);
					st.setInt(3, fs.getSpellUid());
					st.executeUpdate();
					st.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : insertSkill(Connection con, long obj_id, String name, int type)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * uid와 연결된 케릭터테이블에 해당하는 이름이 존재하는지 확인.
	 * 
	 * @param con
	 * @param account_uid
	 * @param name
	 * @return
	 */
	static public boolean isCharacter(int account_uid, String name) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters WHERE account_uid=? AND LOWER(name)=? AND block_date='0000-00-00 00:00:00'");
			st.setInt(1, account_uid);
			st.setString(2, name.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isCharacter(int account_uid, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return false;
	}

	/**
	 * 해당하는 케릭터가 월드에 접속한 시간 갱신해주는 함수
	 * 
	 * @param con
	 * @param name
	 */
	static public void updateCharacterJoinTimeStamp(String name) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET join_date=? WHERE LOWER(name)=?");
			st.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			st.setString(2, name.toLowerCase());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateCharacterJoinTimeStamp(String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	/**
	 * 해당 케릭터 차단날자 갱신하는 함수.
	 * 
	 * @param name
	 */
	static public void updateCharacterBlockTimeStamp(String name) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET block_date=? WHERE LOWER(name)=?");
			st.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			st.setString(2, name.toLowerCase());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateCharacterJoinTimeStamp(String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	static public void updateCharacterName(String name, String newName) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET name=?, temp_name=? WHERE LOWER(name)=?");
			st.setString(1, newName);
			st.setString(2, newName);
			st.setString(3, name.toLowerCase());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateCharacterName(String name, String newName)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	/**
	 * name과 연결된 케릭터테이블에 정보 추출 pcinstance 객체 생성 리턴.
	 * 
	 * @param con
	 * @param c
	 * @param name
	 * @return
	 */
	static public PcInstance readCharacter(LineageClient c, String name) {
		PcInstance pc = null;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters WHERE account_uid=? AND LOWER(name)=?");
			st.setInt(1, c.getAccountUid());
			st.setString(2, name.toLowerCase());
			rs = st.executeQuery();
			if (rs.next()) {
				pc = c.getPc();
				pc.setAccountId(rs.getString("account"));
				pc.setAccountUid(rs.getInt("account_uid"));
				pc.setName(rs.getString("name"));
				pc.setObjectId(rs.getInt("objID"));
				pc.setClassType(rs.getInt("class"));
				pc.setLevel(rs.getInt("level"));
				pc.setMaxHp(rs.getInt("maxHP"));
				pc.setNowHp(rs.getInt("nowHP"));
				pc.setMaxMp(rs.getInt("maxMP"));
				pc.setNowMp(rs.getInt("nowMP"));
				pc.setStr(rs.getInt("str"));
				pc.setDex(rs.getInt("dex"));
				pc.setCon(rs.getInt("con"));
				pc.setWis(rs.getInt("wis"));
				pc.setInt(rs.getInt("inter"));
				pc.setCha(rs.getInt("cha"));
				pc.setClassSex(rs.getInt("sex"));
				pc.setExp(rs.getDouble("exp"));
				pc.setLostExp(rs.getDouble("lost_exp"));
				pc.setX(rs.getInt("locX"));
				pc.setY(rs.getInt("locY"));
				pc.setMap(rs.getInt("locMAP"));
				// 에피소드 8 기준: 저장 위치가 허용 맵이 아니면 본토로 보정
				if (!MapPolicy.isAllowed(pc.getMap())) {
					pc.setX(MapPolicy.getSafeX());
					pc.setY(MapPolicy.getSafeY());
					pc.setMap(MapPolicy.getSafeMapId());
					pc.setHomeX(MapPolicy.getSafeX());
					pc.setHomeY(MapPolicy.getSafeY());
					pc.setHomeMap(MapPolicy.getSafeMapId());
				}
				pc.setHeading(rs.getInt("locHeading"));
				pc.setTitle(rs.getString("title"));
				pc.setFood(rs.getInt("food"));
				pc.setGfx(rs.getInt("gfx"));
				pc.setGfxMode(rs.getInt("gfxMode"));
				pc.setLawful(rs.getInt("lawful"));
				pc.setClanId(rs.getInt("clanID"));
				pc.setClanGrade(rs.getInt("clan_grade"));
				pc.setClanName(rs.getString("clanNAME"));
				pc.setPkCount(rs.getInt("pkcount"));
				try {
					pc.setPkTime(rs.getTimestamp("pkTime").getTime());
				} catch (Exception e) {
				}
				pc.setChattingGlobal(rs.getInt("global_chating") == 1);
				pc.setChattingTrade(rs.getInt("trade_chating") == 1);
				pc.setChattingWhisper(rs.getInt("whisper_chating") == 1);
				pc.setAttribute(rs.getInt("attribute"));
				pc.setLvStr(rs.getInt("lvStr"));
				pc.setLvCon(rs.getInt("lvCon"));
				pc.setLvDex(rs.getInt("lvDex"));
				pc.setLvWis(rs.getInt("lvWis"));
				pc.setLvInt(rs.getInt("lvInt"));
				pc.setLvCha(rs.getInt("lvCha"));
				pc.setElixir(rs.getInt("elixir"));
				pc.setSpeedHackWarningCounting(rs.getInt("speedhack_warning_count"));
				pc.setRegisterDate(rs.getLong("register_date"));

				try {
					pc.setJoinDate(rs.getLong("join_date"));
				} catch (Exception e) {
				}
				if (rs.getString("save_interface").length() > 0)
					pc.setDbInterface(Util.StringToByte(rs.getString("save_interface")));
				
				switch (pc.getClassType()) {
				case Lineage.LINEAGE_CLASS_ROYAL:
					pc.setClassGfx(pc.getClassSex() == 0 ? Lineage.royal_male_gfx : Lineage.royal_female_gfx);
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:
					pc.setClassGfx(pc.getClassSex() == 0 ? Lineage.knight_male_gfx : Lineage.knight_female_gfx);
					break;
				case Lineage.LINEAGE_CLASS_ELF:
					pc.setClassGfx(pc.getClassSex() == 0 ? Lineage.elf_male_gfx : Lineage.elf_female_gfx);
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					pc.setClassGfx(pc.getClassSex() == 0 ? Lineage.wizard_male_gfx : Lineage.wizard_female_gfx);
					break;
				case Lineage.LINEAGE_CLASS_DARKELF:
					pc.setClassGfx(pc.getClassSex() == 0 ? Lineage.darkelf_male_gfx : Lineage.darkelf_female_gfx);
					break;
				case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
					pc.setClassGfx(pc.getClassSex() == 0 ? Lineage.dragonknight_male_gfx : Lineage.dragonknight_female_gfx);
					break;
				case Lineage.LINEAGE_CLASS_BLACKWIZARD:
					pc.setClassGfx(pc.getClassSex() == 0 ? Lineage.blackwizard_male_gfx : Lineage.blackwizard_female_gfx);
					break;
				}
				pc.setAge(rs.getInt("age"));
				pc.setLevelUpStat(rs.getInt("level_up_stat"));
				pc.setResetBaseStat(rs.getInt("reset_base_stat"));
				pc.setResetLevelStat(rs.getInt("reset_level_stat"));
				pc.setQuickPolymorph(rs.getString("quick_polymorph"));
				pc.autoPotionPercent = rs.getInt("auto_potion_percent");
				pc.autoPotionName = rs.getString("auto_potion") == null ? "" : rs.getString("auto_potion");
				pc.isAutoPotion = rs.getInt("is_auto_potion") == 1 ? true : false;
				pc.setAutoHuntMonsterCount(rs.getInt("auto_hunt_monster_count"));
				pc.setEvolutionCount(rs.getInt("evolution_count"));
				pc.setTempName(rs.getString("temp_name"));
				pc.setTempClanName(rs.getString("temp_clan_name"));
				pc.setTempClanId(rs.getInt("temp_clan_id"));
				pc.setTempClanGrade(rs.getInt("temp_clan_grade"));
				pc.setTempTitle(rs.getString("temp_title"));
				pc.setBattleTeam(rs.getInt("battle_team"));
				pc.scrollWeaponCount = rs.getInt("장인주문서_사용횟수");
				pc.setExp_marble_save_count(rs.getInt("경험치저장구슬_사용횟수"));
				pc.setExp_marble_use_count(rs.getInt("경험치구슬_사용횟수"));
				pc.auto_hunt_time = rs.getInt("자동사냥_남은시간");
				pc.auto_return_home_hp = rs.getInt("자동사냥_귀환체력");
				pc.is_auto_buff = rs.getInt("자동사냥_자동버프") == 1 ? true : false;
				pc.is_auto_potion_buy = rs.getInt("자동사냥_물약구매") == 1 ? true : false;
				pc.is_auto_poly_select = rs.getInt("자동사냥_우선변줌사용") == 1 ? true : false;
				pc.is_auto_rank_poly = rs.getInt("자동사냥_자동랭변") == 1 ? true : false;
				pc.is_auto_rank_poly_buy = rs.getInt("자동사냥_랭변구매") == 1 ? true : false;
				pc.is_auto_poly = rs.getInt("자동사냥_자동변신") == 1 ? true : false;
				pc.is_auto_poly_buy = rs.getInt("자동사냥_변줌구매") == 1 ? true : false;
				pc.is_auto_teleport = rs.getInt("자동사냥_자동텔포") == 1 ? true : false;
				pc.is_auto_bravery = rs.getInt("자동사냥_자동용기") == 1 ? true : false;
				pc.is_auto_bravery_buy = rs.getInt("자동사냥_용기구매") == 1 ? true : false;
				pc.is_auto_haste = rs.getInt("자동사냥_자동촐기") == 1 ? true : false;
				pc.is_auto_haste_buy = rs.getInt("자동사냥_촐기구매") == 1 ? true : false;
				pc.is_auto_arrow_buy = rs.getInt("자동사냥_화살구매") == 1 ? true : false;
				pc.is_auto_trunundead = rs.getInt("자동사냥_턴언데드") == 1 ? true : false;
				pc.setQuestChapter(rs.getInt("questchapter"));
				pc.setQuestKill(rs.getInt("questkill"));
				pc.setRadomQuest(rs.getInt("radomquest"));
				pc.setRandomQuestkill(rs.getInt("randomquestkill"));
				pc.setRandomQuestCount(rs.getInt("RandomQuestCount"));
				pc.setRandomQuestPlay(rs.getInt("RandomQuestPlay"));
				try {
				    String sellColumn = rs.getString("sell");
				    if (sellColumn != null) {
				        for (String member : sellColumn.split(",")) {
				            String trimmedMember = member.trim();
				            if (!trimmedMember.isEmpty()) {
				                pc.isAutoSellList.add(trimmedMember);
				            }
				        }
				    } else {
	
				    }
				    
				pc.isAutoSell = rs.getInt("sellon") == 1 ? true : false;
				pc.isAutoSelluser = rs.getInt("selluser") == 1 ? true : false;

				// GM 권한: 캐릭터 단위 (characters.gm 컬럼)
				try {
					int charGm = rs.getInt("gm");
					pc.setGm(charGm >= 200 ? charGm : 0);
				} catch (SQLException e) {
					// gm 컬럼 없음: 기본 0 유지
				}

				} catch (SQLException e) {

				    e.printStackTrace(); // 적절한 예외 처리를 추가하세요.
				}
				st.close();
				rs.close();
				
				st = con.prepareStatement("SELECT * FROM accounts WHERE uid=?");
				st.setInt(1, c.getAccountUid());
				rs = st.executeQuery();
				
				if (rs.next()) {
					pc.setGiran_dungeon_time(rs.getInt("giran_dungeon_time"));
					pc.setInfoName(rs.getString("info_name").length() < 1 ? null : rs.getString("info_name"));
					pc.setInfoPhoneNum(rs.getString("info_phone_num").length() < 1 ? null : rs.getString("info_phone_num"));
					pc.setInfoBankName(rs.getString("info_bank_name").length() < 1 ? null : rs.getString("info_bank_name"));
					pc.setInfoBankNum(rs.getString("info_bank_num").length() < 1 ? null : rs.getString("info_bank_num"));
					pc.setGiran_dungeon_count(rs.getInt("giran_dungeon_count"));
					pc.auto_hunt_account_time = rs.getInt("자동사냥_이용시간");
					//야도란 레벨 달성체크 보상
					pc.setPclevel_gift_check(rs.getInt("레벨달성체크"));
					pc.setAuto_count(rs.getInt("auto_count"));
					pc.setDaycount(rs.getInt("daycount"));
					pc.setDaycheck(rs.getInt("daycheck"));
					pc.setDayptime(rs.getInt("daytime"));
					// GM 권한은 캐릭터 단위(characters.gm)로만 적용. 계정 access_level은 사용하지 않음.
				}			
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readCharacter(Client c, String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return pc;
	}

	/**
	 * 고정 멤버 유무 읽어옴
	 * 
	 * @param con
	 * @param c
	 * @param name
	 * @return
	 */
	static public void readMember(LineageClient c, PcInstance pc) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM accounts WHERE uid=?");
			st.setInt(1, c.getAccountUid());
			rs = st.executeQuery();
			if (rs.next()) {
				pc.setMember(rs.getString("member").equals("true"));
				// GM 권한은 캐릭터 단위(characters.gm)로만 적용
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readMember(LineageClient c, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 캐릭터의 gm 값을 DB(characters.gm)에서 읽어 적용. 접속 시 한 번 더 호출해 확실히 반영.
	 */
	static public void applyGmFromAccount(LineageClient c, PcInstance pc) {
		if (c == null || pc == null)
			return;
		if (pc.getObjectId() <= 0)
			return;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT gm FROM characters WHERE objID=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			if (rs.next()) {
				try {
					int charGm = rs.getInt("gm");
					pc.setGm(charGm >= 200 ? charGm : 0);
				} catch (SQLException e) {
					// gm 컬럼 없음: 변경하지 않음
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : applyGmFromAccount\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 사용자 정보 저장 함수.
	 * 
	 * @param con
	 * @param pc
	 */
	static public void saveMember(Connection con, PcInstance pc) {
		PreparedStatement st = null;
		try {
			// 처리.
			st = con.prepareStatement("UPDATE accounts SET member=? WHERE uid=?");
			st.setString(1, pc.isMember() ? "true" : "false");
			st.setLong(2, pc.getAccountUid());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveMember(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * 인벤토리 정보 추출.
	 * 
	 * @param con
	 * @param pc
	 */
	static public void readInventory(PcInstance pc) {
		Inventory inv = pc.getInventory();
		if (inv == null)
			return;

//		Connection con = null;
//		PreparedStatement st = null;
//		ResultSet rs = null;
//		try {
//			con = DatabaseConnection.getLineage();
//			st = con.prepareStatement("SELECT * FROM characters_inventory WHERE cha_objId=?");
//			st.setLong(1, pc.getObjectId());
//			rs = st.executeQuery();
//			while (rs.next()) {
//				try {					
//					ItemInstance item = ItemDatabase.newInstance(ItemDatabase.find(rs.getString("name")));
//					if (item != null) {
//						item.setObjectId(rs.getInt(1));
//						item.setCount(rs.getLong(5));
//						item.setQuantity(rs.getInt(6));
//						item.setEnLevel(rs.getInt(7));
//						item.setEquipped(rs.getInt(8) == 1);
//						item.setDefinite(rs.getInt(9) == 1);
//						item.setBless(rs.getInt(10));
//						item.setDurability(rs.getInt(11));
//						item.setNowTime(rs.getInt(12));
//						item.setPetObjectId(rs.getInt(13));
//						item.setInnRoomKey(rs.getInt(14));
//						item.setLetterUid(rs.getInt(15));
//						item.setRaceTicket(rs.getString(16));
//
//						if (item.isEquipped() && item.getItem().getType2().equalsIgnoreCase("fishing_rod")) {
//							item.setEquipped(false);
//							pc.setGfxMode(0);
//							pc.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), pc, pc.getGfxMode()), true);
//						}
//
//						// 편지지일경우 월드 업데이트를 우선함.
//						if (item instanceof Letter) {
//							// 착용중인 아이템 정보 갱신.
//							item.toWorldJoin(con, pc);
//							// 인벤에 등록하면서 패킷 전송.
//							inv.append(item, Lineage.server_version <= 200);
//						} else {
//							// 인벤에 등록하면서 패킷 전송.
//							inv.append(item, Lineage.server_version <= 200);
//							// 착용중인 아이템 정보 갱신.
//							item.toWorldJoin(con, pc);
//						}
//					}
//				} catch (Exception e) {
//					lineage.share.System.printf("%s : 인벤 로드 에러.\r\n", CharactersDatabase.class.toString());
//					lineage.share.System.println(e + "   캐릭터: " + pc.getName());
//				}
//			}
//			if (Lineage.server_version > 200)
//				pc.toSender(S_InventoryList.clone(BasePacketPooling.getPool(S_InventoryList.class), inv));
//
//		} catch (Exception e) {
//			lineage.share.System.printf("%s : readInventory(PcInstance pc)\r\n", CharactersDatabase.class.toString());
//			lineage.share.System.println(e + "   캐릭터: " + pc.getName());
//		} finally {
//			DatabaseConnection.close(con, st, rs);
//		}
		try (Connection con = DatabaseConnection.getLineage();
			     PreparedStatement st = con.prepareStatement("SELECT * FROM characters_inventory WHERE cha_objId=?")) {
			    st.setLong(1, pc.getObjectId());
			    try (ResultSet rs = st.executeQuery()) {
			        while (rs.next()) {
			            try {
			                ItemInstance item = ItemDatabase.newInstance(ItemDatabase.find(rs.getString("name")));
			                if (item != null) {
								item.setObjectId(rs.getInt(1));
								item.setCount(rs.getLong(5));
								item.setQuantity(rs.getInt(6));
								item.setEnLevel(rs.getInt(7));
								item.setEquipped(rs.getInt(8) == 1);
								item.setDefinite(rs.getInt(9) == 1);
								item.setBless(rs.getInt(10));
								item.setDurability(rs.getInt(11));
								item.setNowTime(rs.getInt(12));
								item.setPetObjectId(rs.getInt(13));
								item.setInnRoomKey(rs.getInt(14));
								item.setLetterUid(rs.getInt(15));
								item.setRaceTicket(rs.getString(16));
								item.setEnFire(rs.getInt(20));
								item.setEnWater(rs.getInt(21));
								item.setEnWind(rs.getInt(22));
								item.setEnEarth(rs.getInt(23));
								item.setInvDolloptionA(rs.getInt(24));
								item.setInvDolloptionB(rs.getInt(25));
								item.setInvDolloptionC(rs.getInt(26));
								item.setInvDolloptionD(rs.getInt(27));
								item.setInvDolloptionE(rs.getInt(28));
								item.setItemTimek(rs.getString(29));
			                   
			                   if (item.isEquipped() && "fishing_rod".equalsIgnoreCase(item.getItem().getType2())) {
			                        item.setEquipped(false);
			                        pc.setGfxMode(0);
			                        pc.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), pc, pc.getGfxMode()), true);
			                    }

			                    // 편지지일경우 월드 업데이트를 우선함.
			                    if (item instanceof Letter) {
			                        // 착용중인 아이템 정보 갱신.
			                        item.toWorldJoin(con, pc);
			                        // 인벤에 등록하면서 패킷 전송.
			                        inv.append(item, Lineage.server_version <= 200);
			                    } else {
			                        // 인벤에 등록하면서 패킷 전송.
			                        inv.append(item, Lineage.server_version <= 200);
			                        // 착용중인 아이템 정보 갱신.
			                        item.toWorldJoin(con, pc);
			                    }
			                }
			            } catch (Exception e) {
			                lineage.share.System.printf("%s : 인벤 로드 에러.\r\n", CharactersDatabase.class.toString());
			                lineage.share.System.println(e + "   캐릭터: " + pc.getName());
			            }
			        }
			        if (Lineage.server_version > 200) {
			            pc.toSender(S_InventoryList.clone(BasePacketPooling.getPool(S_InventoryList.class), inv));
			        }
			    }
			} catch (Exception e) {
			    lineage.share.System.printf("%s : readInventory(PcInstance pc)\r\n", CharactersDatabase.class.toString());
			    lineage.share.System.println(e + "   캐릭터: " + pc.getName());
			}
	}

	/**
	 * GM 툴 접속 중 지급: objId에 해당하는 인벤 1건을 DB에서 읽어 해당 캐릭터 인벤에 추가 후 패킷 전송.
	 */
	static public void deliverOneGmItem(PcInstance pc, long objId) {
		if (pc == null || pc.getInventory() == null)
			return;
		try (Connection con = DatabaseConnection.getLineage();
				PreparedStatement st = con.prepareStatement("SELECT * FROM characters_inventory WHERE objId=?")) {
			st.setLong(1, objId);
			try (ResultSet rs = st.executeQuery()) {
				if (!rs.next())
					return;
				ItemInstance item = ItemDatabase.newInstance(ItemDatabase.find(rs.getString("name")));
				if (item == null)
					return;
				item.setObjectId(rs.getInt("objId"));
				item.setCount(rs.getLong("count"));
				item.setQuantity(rs.getInt("quantity"));
				item.setEnLevel(rs.getInt("en"));
				item.setEquipped(rs.getInt("equipped") == 1);
				try { item.setDefinite(rs.getInt(9) == 1); } catch (Exception e) {}
				try { item.setBless(rs.getInt(10)); } catch (Exception e) {}
				item.setDurability(rs.getInt(11));
				item.setNowTime(rs.getInt(12));
				item.setPetObjectId(rs.getInt(13));
				item.setInnRoomKey(rs.getInt(14));
				item.setLetterUid(rs.getInt(15));
				item.setRaceTicket(rs.getString(16));
				try {
					item.setEnFire(rs.getInt(20));
					item.setEnWater(rs.getInt(21));
					item.setEnWind(rs.getInt(22));
					item.setEnEarth(rs.getInt(23));
					item.setInvDolloptionA(rs.getInt(24));
					item.setInvDolloptionB(rs.getInt(25));
					item.setInvDolloptionC(rs.getInt(26));
					item.setInvDolloptionD(rs.getInt(27));
					item.setInvDolloptionE(rs.getInt(28));
					item.setItemTimek(rs.getString(29));
				} catch (Exception e) { /* 컬럼 없으면 무시 */ }
				pc.getInventory().append(item, true);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : deliverOneGmItem PcInstance=%s objId=%d\r\n", CharactersDatabase.class.toString(), pc.getName(), objId);
			lineage.share.System.println(e);
		}
	}

	/**
	 * 스킬 정보 추출.
	 * 
	 * @param con
	 * @param pc
	 */
	static public void readSkill(PcInstance pc) {
		List<Skill> list = SkillController.find(pc);
		if (list == null)
			return;

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_skill WHERE cha_objId=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			while (rs.next()) {
				Skill s = SkillDatabase.find(rs.getInt("skill"));
				if (s != null && !list.contains(s))
					list.add(s);
			}

			SkillController.sendList(pc);
		} catch (Exception e) {
			lineage.share.System.printf("%s : readSkill(PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 버프 정보 추출.
	 * 
	 * @param con
	 * @param pc
	 */
	static public void readBuff(PcInstance pc) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_buff WHERE objId=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			if (rs.next()) {
				int light = rs.getInt("light");
				int shield = rs.getInt("shield");
				int curse_poison = rs.getInt("curse_poison");
				int curse_poison_level = rs.getInt("curse_poison_level");
				int curse_blind = rs.getInt("curse_blind");
				int slow = rs.getInt("slow");
				int curse_paralyze = rs.getInt("curse_paralyze");
				int enchant_dexterity = rs.getInt("enchant_dexterity");
				int enchant_mighty = rs.getInt("enchant_mighty");
				int haste = rs.getInt("haste");
				int hastePotion = rs.getInt("haset_potion");
				int shape_change = rs.getInt("shape_change");
				int immune_to_harm = rs.getInt("immune_to_harm");
				int bravery_potion = rs.getInt("bravery_potion");
				int elvenwafer = rs.getInt("elvenwafer");
				int eva = rs.getInt("eva");
				int wisdom = rs.getInt("wisdom");
				int blue_potion = rs.getInt("blue_potion");
				int floating_eye_meat = rs.getInt("floating_eye_meat");
				int clearmind = rs.getInt("clearmind");
				int resistelemental = rs.getInt("resistelemental");
				int icelance = rs.getInt("icelance");
				int earthskin = rs.getInt("earthskin");
				int ironskin = rs.getInt("ironskin");
				int blessearth = rs.getInt("blessearth");
				int curse_ghoul = rs.getInt("curse_ghoul");
				int chatting_close = rs.getInt("chatting_close");
				int holywalk = rs.getInt("holywalk");
				int shadowarmor = rs.getInt("shadowarmor");
				int frameSpeedOverStun = rs.getInt("frame_speed_stun");
				int expPotion = (rs.getInt("exp_potion"));
				int buffFight = (rs.getInt("buff_fight"));
				int 수룡의마안 = (rs.getInt("수룡의_마안"));
				int 풍룡의마안 = (rs.getInt("풍룡의_마안"));
				int 지룡의마안 = (rs.getInt("지룡의_마안"));
				int 화룡의마안 = (rs.getInt("화룡의_마안"));
				int 생명의마안 = (rs.getInt("생명의_마안"));
				int 탄생의마안 = (rs.getInt("탄생의_마안"));
				int 형상의마안 = (rs.getInt("형상의_마안"));
				int 수룡의마안_딜레이 = (rs.getInt("수룡의마안_딜레이"));
				int 풍룡의마안_딜레이 = (rs.getInt("풍룡의마안_딜레이"));
				int 지룡의마안_딜레이 = (rs.getInt("지룡의마안_딜레이"));
				int 화룡의마안_딜레이 = (rs.getInt("화룡의마안_딜레이"));
				int 생명의마안_딜레이 = (rs.getInt("생명의마안_딜레이"));
				int 탄생의마안_딜레이 = (rs.getInt("탄생의마안_딜레이"));
				int 형상의마안_딜레이 = (rs.getInt("형상의마안_딜레이"));
				int 경험치_드랍_10 = (rs.getInt("경험치_드랍_10"));
				int 경험치_드랍_20 = (rs.getInt("경험치_드랍_20"));
				int 경험치_드랍_50 = (rs.getInt("경험치_드랍_50"));
				int 복수_쿨타임 = (rs.getInt("복수_쿨타임"));

				
				if (light > 0)
					Light.init(pc, light);
				if (shield > 0)
					Shield.init(pc, shield);
				if (curse_poison > 0)
					CursePoison.init(pc, curse_poison, curse_poison_level);
				if (curse_blind > 0)
					CurseBlind.init(pc, curse_blind);
				if (slow > 0)
					Slow.init(pc, slow);
				if (wisdom > 0)
					Wisdom.init(pc, wisdom);
				if (curse_paralyze > 0)
					CurseParalyze.init(pc, curse_paralyze);
				if (enchant_dexterity > 0)
					EnchantDexterity.init(pc, enchant_dexterity);
				if (enchant_mighty > 0)
					EnchantMighty.init(pc, enchant_mighty);
				if (haste > 0 || haste == -1)
					Haste.init(pc, haste, true);
				if (hastePotion > 0)
					HastePotionMagic.init(pc, hastePotion, true);
				if (shape_change > 0)
					ShapeChange.init(pc, shape_change);
				if (immune_to_harm > 0)
					ImmuneToHarm.init(pc, immune_to_harm);
				if (bravery_potion > 0 || bravery_potion == -1)
					Bravery.init(pc, bravery_potion, true);
				if (elvenwafer > 0 || elvenwafer == -1)
					Wafer.init(pc, elvenwafer, true);
				if (eva > 0)
					Eva.init(pc, eva);
				if (blue_potion > 0)
					Blue.init(pc, blue_potion);
				if (floating_eye_meat > 0)
					FloatingEyeMeat.init(pc, floating_eye_meat);
				if (clearmind > 0)
					ClearMind.init(pc, clearmind);
				if (resistelemental > 0)
					ResistElemental.init(pc, resistelemental);
				if (icelance > 0)
					IceLance.init(pc, icelance);
				if (earthskin > 0)
					EarthSkin.init(pc, earthskin);
				if (ironskin > 0)
					IronSkin.init(pc, ironskin);
				if (blessearth > 0)
					EarthGuardian.init(pc, blessearth);
				if (curse_ghoul > 0)
					CurseGhoul.init(pc, curse_ghoul);
				if (chatting_close > 0)
					ChattingClose.init(pc, chatting_close, true);
				if (holywalk > 0 || holywalk == -1)
					HolyWalk.init(pc, holywalk);
				if (shadowarmor > 0)
					ShadowArmor.init(pc, shadowarmor);
				if (expPotion > 0)
					Exp_Potion.init(pc, expPotion);
				if (frameSpeedOverStun > 0)
					FrameSpeedOverStun.init(pc, frameSpeedOverStun);
				if (buffFight > 0)
					BuffFight.init(pc, buffFight);
				if (수룡의마안 > 0)
					MaanWatar.init(pc, 수룡의마안);
				if (풍룡의마안 > 0)
					MaanWind.init(pc, 풍룡의마안);
				if (지룡의마안 > 0)
					MaanEarth.init(pc, 지룡의마안);
				if (화룡의마안 > 0)
					MaanFire.init(pc, 화룡의마안);
				if (생명의마안 > 0)
					MaanLife.init(pc, 생명의마안);
				if (탄생의마안 > 0)
					MaanBirth.init(pc, 탄생의마안);
				if (형상의마안 > 0)
					MaanShape.init(pc, 형상의마안);
				if (수룡의마안_딜레이 > 0)
					MaanWatarDelay.init(pc, 수룡의마안_딜레이);
				if (풍룡의마안_딜레이 > 0)
					MaanWindDelay.init(pc, 풍룡의마안_딜레이);
				if (지룡의마안_딜레이 > 0)
					MaanEarthDelay.init(pc, 지룡의마안_딜레이);
				if (화룡의마안_딜레이 > 0)
					MaanFireDelay.init(pc, 화룡의마안_딜레이);
				if (생명의마안_딜레이 > 0)
					MaanLifeDelay.init(pc, 생명의마안_딜레이);
				if (탄생의마안_딜레이 > 0)
					MaanBirthDelay.init(pc, 탄생의마안_딜레이);
				if (형상의마안_딜레이 > 0)
					MaanShapeDelay.init(pc, 형상의마안_딜레이);
				if (경험치_드랍_10 > 0)
					ExpDropBuff_10.init(pc, 경험치_드랍_10);
				if (경험치_드랍_20 > 0)
					ExpDropBuff_20.init(pc, 경험치_드랍_20);
				if (경험치_드랍_50 > 0)
					ExpDropBuff_50.init(pc, 경험치_드랍_50);
				if (복수_쿨타임 > 0)
					RevengeCooldown.init(pc, 복수_쿨타임);

				PluginController.init(CharactersDatabase.class, "readBuff", pc, rs);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readBuff(PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e + "   캐릭터: " + pc.getName());
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 기억 정보 추출.
	 * 
	 * @param con
	 * @param pc
	 */
	static public void readBook(PcInstance pc) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_book WHERE objId=? ORDER BY location");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			while (rs.next()) {
				Book b = BookController.getPool();
				b.setLocation(rs.getString("location"));
				b.setX(rs.getInt("locX"));
				b.setY(rs.getInt("locY"));
				b.setMap(rs.getInt("locMAP"));

				BookController.append(pc, b);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readBook(PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	static public void deleteBook(PcInstance pc) {
	    Connection con = null;
	    PreparedStatement st = null;

	    try {
	        con = DatabaseConnection.getLineage();
	        st = con.prepareStatement("DELETE FROM characters_book WHERE objId=?");
	        st.setLong(1, pc.getObjectId());
	        st.executeUpdate();
	    } catch (Exception e) {
	        lineage.share.System.printf("%s : deleteBook(PcInstance pc)\r\n", CharactersDatabase.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st, null);
	    }
	}
	//기억의구슬
	public static void toBookAddItem(PcInstance pc, Book temp) {		
		Connection con = null;
		PreparedStatement pst = null;
		int i = 0;
		try {
			con = DatabaseConnection.getLineage();
			pst = con.prepareStatement("INSERT INTO characters_book SET objId = ?, name = ?, location = ?, locX = ?, locY = ?, locMap = ?, random = ?");
			pst.setLong(++i, pc.getObjectId());
			pst.setString(++i, pc.getName());
			pst.setString(++i, temp.getLocation());
			pst.setInt(++i, temp.getX());
			pst.setInt(++i, temp.getY());
			pst.setInt(++i, temp.getMap());
			pst.setInt(++i, temp.getRandom());
			pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.close(con, pst);
		}
	}
	/**
	 * 사용자 정보 저장 함수.
	 * 
	 * @param con
	 * @param pc
	 */
	static public void saveCharacter(Connection con, PcInstance pc) {
		PreparedStatement st = null;
		try {
			// 초기화.
			StringBuffer db_interface = new StringBuffer();
			// 추출.
			if (pc.getDbInterface() != null) {
				for (byte d : pc.getDbInterface())
					db_interface.append(String.format("%02x", d & 0xff));
			}
			// 처리.
			st = con.prepareStatement("UPDATE characters SET " + "level=?, nowHP=?, maxHP=?, nowMP=?, maxMP=?, ac=?, exp=?, locx=?, locy=?, locmap=?, title=?, "
					+ "food=?, lawful=?, clanid=?, clanname=?, gfx=?, gfxmode=?, attribute=?, str=?, dex=?, con=?, wis=?, inter=?, cha=?, lvStr=?, lvCon=?, lvDex=?, "
					+ "lvWis=?, lvInt=?, lvCha=?, global_chating=?, trade_chating=?, whisper_chating=?, pkcount=?, pkTime=?, "
					+ "lost_exp=?, save_interface=?, elixir=?,speedhack_warning_count=?, age=?, clan_grade=?, end_date=?, locHeading=?, level_up_stat=?, reset_base_stat=?, reset_level_stat=?, quick_polymorph=?,"
					+ "is_auto_potion=?, auto_potion_percent=?, auto_potion=?, auto_hunt_monster_count=?, evolution_count=?, temp_name=?, temp_clan_name=?, temp_clan_id=?, temp_clan_grade=?, temp_title=?, battle_team=?, last_ip=?, "
					+ "장인주문서_사용횟수=?, 경험치저장구슬_사용횟수=?, 경험치구슬_사용횟수=?, "
					+ "자동사냥_남은시간=?, 자동사냥_귀환체력=?, 자동사냥_자동버프=?, 자동사냥_물약구매=?, 자동사냥_우선변줌사용=?, 자동사냥_자동랭변=?, 자동사냥_랭변구매=?, 자동사냥_자동변신=?, 자동사냥_변줌구매=?, "
					+ "자동사냥_자동텔포=?, 자동사냥_자동용기=?, 자동사냥_용기구매=?, 자동사냥_자동촐기=?, 자동사냥_촐기구매=?, 자동사냥_화살구매=?, 자동사냥_턴언데드=?,questchapter=?,questkill=?,radomquest=?,randomquestkill=?,RandomQuestCount=?,RandomQuestPlay=?, sell=?,sellon=?,selluser=? WHERE objID=?");
			st.setInt(1, pc.getLevel());
			st.setInt(2, pc.getNowHp());
			st.setInt(3, pc.getMaxHp());
			st.setInt(4, pc.getNowMp());
			st.setInt(5, pc.getMaxMp());
			st.setInt(6, pc.getAc());
			st.setDouble(7, pc.getExp());
			st.setInt(8, pc.getX());
			st.setInt(9, pc.getY());
			st.setInt(10, pc.getMap());
			st.setString(11, pc.getTitle() == null ? "" : pc.getTitle());
			st.setInt(12, pc.getFood());
			st.setInt(13, pc.getLawful());
			st.setInt(14, pc.getClanId());
			st.setString(15, pc.getClanName() == null ? "" : pc.getClanName());
			st.setInt(16, pc.getGfx());
			st.setInt(17, pc.getGfxMode() < 0 ? 0 : pc.getGfxMode());
			st.setInt(18, pc.getAttribute());
			st.setInt(19, pc.getStr());
			st.setInt(20, pc.getDex());
			st.setInt(21, pc.getCon());
			st.setInt(22, pc.getWis());
			st.setInt(23, pc.getInt());
			st.setInt(24, pc.getCha());
			st.setInt(25, pc.getLvStr());
			st.setInt(26, pc.getLvCon());
			st.setInt(27, pc.getLvDex());
			st.setInt(28, pc.getLvWis());
			st.setInt(29, pc.getLvInt());
			st.setInt(30, pc.getLvCha());
			st.setInt(31, pc.isChattingGlobal() ? 1 : 0);
			st.setInt(32, pc.isChattingTrade() ? 1 : 0);
			st.setInt(33, pc.isChattingWhisper() ? 1 : 0);
			st.setInt(34, pc.getPkCount());
			if (pc.getPkTime() == 0)
				st.setString(35, "0000-00-00 00:00:00");
			else
				st.setTimestamp(35, new Timestamp(pc.getPkTime()));
			st.setDouble(36, pc.getLostExp());
			st.setString(37, db_interface.toString());
			st.setInt(38, pc.getElixir());
			st.setInt(39, pc.getSpeedHackWarningCounting());
			st.setInt(40, pc.getAge());
			st.setInt(41, pc.getClanGrade());
			st.setTimestamp(42, new Timestamp(System.currentTimeMillis()));
			st.setInt(43, pc.getHeading());
			st.setInt(44, pc.getLevelUpStat());
			st.setInt(45, pc.getResetBaseStat());
			st.setInt(46, pc.getResetLevelStat());
			st.setString(47, pc.getQuickPolymorph() == null ? "" : pc.getQuickPolymorph());
			st.setInt(48, pc.isAutoPotion == true ? 1 : 0);
			st.setInt(49, pc.autoPotionPercent);
			st.setString(50, pc.autoPotionName == null ? "" : pc.autoPotionName);
			st.setInt(51, pc.getAutoHuntMonsterCount());
			st.setInt(52, pc.getEvolutionCount());
			st.setString(53, pc.getTempName() == null ? "" : pc.getTempName());
			st.setString(54, pc.getTempClanName() == null ? "" : pc.getTempClanName());
			st.setInt(55, pc.getTempClanId());
			st.setInt(56, pc.getTempClanGrade());
			st.setString(57, pc.getTempTitle() == null ? "" : pc.getTempTitle());
			st.setInt(58, pc.getBattleTeam());
			st.setString(59, pc.getClient().getAccountIp());
			st.setInt(60, pc.scrollWeaponCount);
			st.setInt(61, pc.getExp_marble_save_count());
			st.setInt(62, pc.getExp_marble_use_count());
			st.setInt(63, pc.auto_hunt_time);
			st.setInt(64, pc.auto_return_home_hp);
			st.setInt(65, pc.is_auto_buff ? 1 : 0);
			st.setInt(66, pc.is_auto_potion_buy ? 1 : 0);
			st.setInt(67, pc.is_auto_poly_select ? 1 : 0);
			st.setInt(68, pc.is_auto_rank_poly ? 1 : 0);
			st.setInt(69, pc.is_auto_rank_poly_buy ? 1 : 0);
			st.setInt(70, pc.is_auto_poly ? 1 : 0);
			st.setInt(71, pc.is_auto_poly_buy ? 1 : 0);
			st.setInt(72, pc.is_auto_teleport ? 1 : 0);
			st.setInt(73, pc.is_auto_bravery ? 1 : 0);
			st.setInt(74, pc.is_auto_bravery_buy ? 1 : 0);
			st.setInt(75, pc.is_auto_haste ? 1 : 0);
			st.setInt(76, pc.is_auto_haste_buy ? 1 : 0);
			st.setInt(77, pc.is_auto_arrow_buy ? 1 : 0);
			st.setInt(78, pc.is_auto_trunundead ? 1 : 0);
			st.setInt(79, pc.getQuestChapter());
			st.setInt(80, pc.getQuestKill());
			st.setInt(81, pc.getRadomQuest());
			st.setInt(82,pc.getRandomQuestkill());
			st.setInt(83, pc.getRandomQuestCount());
			st.setInt(84, pc.getRandomQuestPlay());
			st.setString(85, pc.isAutoSellList.toString().replace("[", "").replace("]", ""));
			st.setInt(86, pc.isAutoSell ? 1 : 0);
			st.setInt(87, pc.isAutoSelluser ? 1 : 0);
			st.setLong(88, pc.getObjectId());
			
			st.executeUpdate();
			st.close();

			// GM 툴 등으로 accounts.giran_dungeon_time 만 올라간 상태에서, 메모리는 0(또는 미로드)인 채 로그아웃 저장하면
			// DB를 0으로 덮어써 초기화가 무효가 됨 → DB가 더 크면 메모리에 맞춤 후 저장.
			int giranMem = pc.getGiran_dungeon_time();
			PreparedStatement stG = con.prepareStatement("SELECT giran_dungeon_time FROM accounts WHERE uid=?");
			stG.setLong(1, pc.getAccountUid());
			ResultSet rsG = stG.executeQuery();
			if (rsG.next()) {
				int giranDb = rsG.getInt("giran_dungeon_time");
				if (giranMem < 1 && giranDb > 0)
					pc.setGiran_dungeon_time(giranDb);
			}
			rsG.close();
			stG.close();

			st = con.prepareStatement("UPDATE accounts SET giran_dungeon_time=?, info_name=?, info_phone_num=?, info_bank_name=?, info_bank_num=?, giran_dungeon_count=?, 자동사냥_이용시간=?, 레벨달성체크=?, auto_count=?, daycount=?, daycheck=? , daytime=? WHERE uid=?");
			st.setInt(1, pc.getGiran_dungeon_time());
			st.setString(2, pc.getInfoName() == null ? "" : pc.getInfoName());
			st.setString(3, pc.getInfoPhoneNum() == null ? "" : pc.getInfoPhoneNum());
			st.setString(4, pc.getInfoBankName() == null ? "" : pc.getInfoBankName());
			st.setString(5, pc.getInfoBankNum() == null ? "" : pc.getInfoBankNum());
			st.setInt(6, pc.getGiran_dungeon_count());
			st.setInt(7, pc.auto_hunt_account_time);
			//야도란 레벨달성 보상 추가
			st.setInt(8, pc.getPclevel_gift_check());
			st.setInt(9, pc.getAuto_count());
			st.setInt(10, pc.getDaycount());
			st.setInt(11, pc.getDaycheck());
			st.setInt(12, pc.getDayptime());
			st.setLong(13, pc.getAccountUid());
			st.executeUpdate();

		} catch (Exception e) {
			lineage.share.System.printf("%s : saveCharacter(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e + "   캐릭: " + pc.getName());
			lineage.share.System.println("now hp: " + pc.getNowHp() + "   max hp: " + pc.getMaxHp());
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	static public void saveExp(Connection con, PcInstance pc) {
		PreparedStatement st = null;
		try {
			// 처리.
			st = con.prepareStatement("UPDATE characters SET level=?, exp=? WHERE objID=?");
			st.setInt(1, pc.getLevel());
			st.setDouble(2, pc.getExp());
			st.setLong(3, pc.getObjectId());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveExp(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * 인벤토리 정보 저장 함수 개선 (쿠베라)
	 * 2023-08-09
	 * 개선 사항추가
	 * by 오픈카톡 https://open.kakao.com/o/sbONOzMd
	 * @param con
	 * @param pc
	 */
	static public void saveInventory(Connection con, PcInstance pc) {
	    if (pc == null || pc.getInventory() == null) {
	        return;
	    }

	    String insertQuery = "INSERT INTO characters_inventory (objId, cha_objId, cha_name, name, count, quantity, en, equipped, definite, bress, durability, nowtime, pet_objid, inn_key, letter_uid, slimerace, 구분1, 구분2, enfire, enwater, enwind, enearth, dolloption_a, dolloption_b, dolloption_c, dolloption_d, dolloption_e, itemtime) " +
	            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    PreparedStatement deleteStatement = null;
	    PreparedStatement insertStatement = null;

	    try {
	        Inventory inv = pc.getInventory();
	        if (inv == null) {
	            return;
	        }

	        deleteStatement = con.prepareStatement("DELETE FROM characters_inventory WHERE cha_objId=?");
	        deleteStatement.setLong(1, pc.getObjectId());
	        deleteStatement.executeUpdate();

	        insertStatement = con.prepareStatement(insertQuery);

	        con.setAutoCommit(false);

	        for (ItemInstance item : inv.getList()) {
	            if (item.getItem() == null || !item.getItem().isInventorySave()) {
	                continue;
	            }

	            setInsertParameters(insertStatement, item, pc);
	            insertStatement.addBatch();
	        }

	        insertStatement.executeBatch();
	        con.commit();
	    } catch (SQLException e) {
	        handleSQLException(e, pc);
	        try {
	            if (con != null) {
	                con.rollback();
	            }
	        } catch (SQLException e1) {
	            e1.printStackTrace();
	        }
	    } finally {
	        DatabaseConnection.close(deleteStatement);
	        DatabaseConnection.close(insertStatement);
	        try {
	            con.setAutoCommit(true);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
	static public void tosaveInventory( PcInstance pc) {
		
		Connection con = null;
		
	    if (pc == null || pc.getInventory() == null) {
	        return;
	    }

	    String insertQuery = "INSERT INTO characters_inventory (objId, cha_objId, cha_name, name, count, quantity, en, equipped, definite, bress, durability, nowtime, pet_objid, inn_key, letter_uid, slimerace, 구분1, 구분2, enfire, enwater, enwind, enearth, dolloption_a, dolloption_b, dolloption_c, dolloption_d, dolloption_e, itemtime) " +
	            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    PreparedStatement deleteStatement = null;
	    PreparedStatement insertStatement = null;

	    try {
	    	
	    	con = DatabaseConnection.getLineage();
	    	
	        Inventory inv = pc.getInventory();
	        if (inv == null) {
	            return;
	        }

	        deleteStatement = con.prepareStatement("DELETE FROM characters_inventory WHERE cha_objId=?");
	        deleteStatement.setLong(1, pc.getObjectId());
	        deleteStatement.executeUpdate();

	        insertStatement = con.prepareStatement(insertQuery);

	        con.setAutoCommit(false);

	        for (ItemInstance item : inv.getList()) {
	            if (item.getItem() == null || !item.getItem().isInventorySave()) {
	                continue;
	            }

	            setInsertParameters(insertStatement, item, pc);
	            insertStatement.addBatch();
	        }

	        insertStatement.executeBatch();
	        con.commit();
	    } catch (SQLException e) {
	        handleSQLException(e, pc);
	        try {
	            if (con != null) {
	                con.rollback();
	            }
	        } catch (SQLException e1) {
	            e1.printStackTrace();
	        }
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
	        DatabaseConnection.close(deleteStatement);
	        DatabaseConnection.close(insertStatement);
	        try {
	            con.setAutoCommit(true);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
	private static void setInsertParameters(PreparedStatement stmt, ItemInstance item, PcInstance pc) throws SQLException {
	    stmt.setLong(1, item.getObjectId());
	    stmt.setLong(2, pc.getObjectId());
	    stmt.setString(3, pc.getName());
	    stmt.setString(4, item.getItem().getName());
	    stmt.setLong(5, item.getCount());
	    stmt.setInt(6, item.getQuantity());
	    stmt.setInt(7, item.getEnLevel());
	    stmt.setInt(8, item.isEquipped() ? 1 : 0);
	    stmt.setInt(9, item.isDefinite() ? 1 : 0);
	    stmt.setInt(10, item.getBless());
	    stmt.setInt(11, item.getDurability());
	    stmt.setInt(12, item.getNowTime());
	    stmt.setLong(13, item.getPetObjectId());
	    stmt.setLong(14, item.getInnRoomKey());
	    stmt.setInt(15, item.getLetterUid());
	    stmt.setString(16, item.getRaceTicket());
        stmt.setString(17, item.getItem().getType1());
        stmt.setString(18, item.getItem().getType2());
        stmt.setInt(19, item.getEnFire());
        stmt.setInt(20, item.getEnWater());
        stmt.setInt(21, item.getEnWind());
        stmt.setInt(22, item.getEnEarth());
        stmt.setInt(23, item.getInvDolloptionA());
        stmt.setInt(24, item.getInvDolloptionB());
        stmt.setInt(25, item.getInvDolloptionC());
        stmt.setInt(26, item.getInvDolloptionD());
        stmt.setInt(27, item.getInvDolloptionE()); // 수정된 인덱스
        stmt.setString(28, item.getItemTimek()); // 수정된 인덱스

	}

	private static void handleSQLException(SQLException e, PcInstance pc) {
	    lineage.share.System.printf("%s : 캐릭터 인벤 저장 에러 두기. 캐릭명: %s\r\n", CharactersDatabase.class.toString(), pc.getName());
	    lineage.share.System.println(e);
	}
	/**
	 * 스킬 정보 저장 함수.
	 * 
	 * @param con
	 * @param pc
	 */
	static public void saveSkill(Connection con, PcInstance pc) {
		PreparedStatement st = null;
		try {
			List<Skill> list = SkillController.find(pc);
			if (list != null) {
				//
				st = con.prepareStatement("DELETE FROM characters_skill WHERE cha_objId=?");
				st.setLong(1, pc.getObjectId());
				st.executeUpdate();
				st.close();
				//
				for (Skill s : list) {
					st = con.prepareStatement("INSERT INTO characters_skill SET cha_objId=?, cha_name=?, skill=?, skill_name=?");
					st.setLong(1, pc.getObjectId());
					st.setString(2, pc.getName());
					st.setInt(3, s.getUid());
					st.setString(4, s.getName());
					st.executeUpdate();
					st.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveSkill(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	static public void reLoadSaveSkill(PcInstance pc) {
		PreparedStatement st = null;
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			List<Skill> list = SkillController.find(pc);
			if (list != null) {
				//
				st = con.prepareStatement("DELETE FROM characters_skill WHERE cha_objId=?");
				st.setLong(1, pc.getObjectId());
				st.executeUpdate();
				st.close();
				//
				for (Skill s : list) {
					st = con.prepareStatement("INSERT INTO characters_skill SET cha_objId=?, cha_name=?, skill=?, skill_name=?");
					st.setLong(1, pc.getObjectId());
					st.setString(2, pc.getName());
					st.setInt(3, s.getUid());
					st.setString(4, s.getName());
					st.executeUpdate();
					st.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveSkill(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	/**
	 * 버프 저장 함수.
	 * 
	 * @param con
	 * @param pc
	 */
	static public void saveBuff(Connection con, PcInstance pc) {
		PreparedStatement st = null;
		try {
			Buff b = BuffController.find(pc);
			BuffInterface light = null;
			BuffInterface shield = null;
			BuffInterface curse_poison = null;
			BuffInterface curse_blind = null;
			BuffInterface slow = null;
			BuffInterface curse_paralyze = null;
			BuffInterface enchant_dexterity = null;
			BuffInterface enchant_mighty = null;
			BuffInterface haste = null;
			BuffInterface hastePotion = null;
			BuffInterface shape_change = null;
			BuffInterface immune_to_harm = null;
			BuffInterface bravery_potion = null;
			BuffInterface elvenwafer = null;
			BuffInterface eva = null;
			BuffInterface wisdom = null;
			BuffInterface blue_potion = null;
			BuffInterface floating_eye_meat = null;
			BuffInterface clearmind = null;
			BuffInterface resistelemental = null;
			BuffInterface icelance = null;
			BuffInterface earthskin = null;
			BuffInterface ironskin = null;
			BuffInterface blessearth = null;
			BuffInterface curse_ghoul = null;
			BuffInterface chatting_close = null;
			BuffInterface holywalk = null;
			BuffInterface shadowarmor = null;
			BuffInterface expPotion = null;
			BuffInterface frameSpeedOverSutn = null;
			BuffInterface buff_fight = null;
			BuffInterface 수룡의마안 = null;
			BuffInterface 풍룡의마안 = null;
			BuffInterface 지룡의마안 = null;
			BuffInterface 화룡의마안 = null;
			BuffInterface 생명의마안 = null;
			BuffInterface 탄생의마안 = null;
			BuffInterface 형상의마안 = null;
			BuffInterface 수룡의마안_딜레이 = null;
			BuffInterface 풍룡의마안_딜레이 = null;
			BuffInterface 지룡의마안_딜레이 = null;
			BuffInterface 화룡의마안_딜레이 = null;
			BuffInterface 생명의마안_딜레이 = null;
			BuffInterface 탄생의마안_딜레이 = null;
			BuffInterface 형상의마안_딜레이 = null;
			BuffInterface 경험치_드랍_10 = null;
			BuffInterface 경험치_드랍_20 = null;
			BuffInterface 경험치_드랍_50 = null;
			BuffInterface 복수_쿨타임 = null;

			if (b != null) {
				light = b.find(Light.class);
				shield = b.find(Shield.class);
				curse_poison = b.find(CursePoison.class);
				curse_blind = b.find(CurseBlind.class);
				slow = b.find(Slow.class);
				curse_paralyze = b.find(CurseParalyze.class);
				enchant_dexterity = b.find(EnchantDexterity.class);
				enchant_mighty = b.find(EnchantMighty.class);
				haste = b.find(Haste.class);
				hastePotion = b.find(HastePotionMagic.class);
				shape_change = b.find(ShapeChange.class);
				immune_to_harm = b.find(ImmuneToHarm.class);
				bravery_potion = b.find(Bravery.class);
				elvenwafer = b.find(Wafer.class);
				eva = b.find(Eva.class);
				wisdom = b.find(Wisdom.class);
				blue_potion = b.find(Blue.class);
				floating_eye_meat = b.find(FloatingEyeMeat.class);
				clearmind = b.find(ClearMind.class);
				resistelemental = b.find(ResistElemental.class);
				icelance = b.find(IceLance.class);
				blessearth = b.find(EarthGuardian.class);
				earthskin = b.find(EarthSkin.class);
				ironskin = b.find(IronSkin.class);
				curse_ghoul = b.find(CurseGhoul.class);
				chatting_close = b.find(ChattingClose.class);
				holywalk = b.find(HolyWalk.class);
				shadowarmor = b.find(ShadowArmor.class);
				expPotion = b.find(Exp_Potion.class);
				frameSpeedOverSutn = b.find(FrameSpeedOverStun.class);
				buff_fight = b.find(BuffFight.class);			
				수룡의마안 = b.find(MaanWatar.class);
				풍룡의마안 = b.find(MaanWind.class);
				지룡의마안 = b.find(MaanEarth.class);
				화룡의마안 = b.find(MaanFire.class);
				생명의마안 = b.find(MaanLife.class);
				탄생의마안 = b.find(MaanBirth.class);
				형상의마안 = b.find(MaanShape.class);
				수룡의마안_딜레이 = b.find(MaanWatarDelay.class);
				풍룡의마안_딜레이 = b.find(MaanWindDelay.class);
				지룡의마안_딜레이 = b.find(MaanEarthDelay.class);
				화룡의마안_딜레이 = b.find(MaanFireDelay.class);
				생명의마안_딜레이 = b.find(MaanLifeDelay.class);
				탄생의마안_딜레이 = b.find(MaanBirthDelay.class);
				형상의마안_딜레이 = b.find(MaanShapeDelay.class);
				경험치_드랍_10 = b.find(ExpDropBuff_10.class);
				경험치_드랍_20 = b.find(ExpDropBuff_20.class);
				경험치_드랍_50 = b.find(ExpDropBuff_50.class);
				복수_쿨타임 = b.find(RevengeCooldown.class);
	
			}

			st = con.prepareStatement("UPDATE characters_buff SET " + "name=?, light=?, shield=?, curse_poison=?, curse_poison_level=?, curse_blind=?, slow=?, curse_paralyze=?, enchant_dexterity=?, enchant_mighty=?, "
					+ "haste=?, haset_potion=?, shape_change=?, immune_to_harm=?, bravery_potion=?, elvenwafer=?, eva=?, wisdom=?, blue_potion=?, "
					+ "floating_eye_meat=?, clearmind=?, resistelemental=?, icelance=?, earthskin=?, ironskin=?, blessearth=?, curse_ghoul=?, "
					+ "chatting_close=?, holywalk=?, shadowarmor=?, exp_potion=?, frame_speed_stun=?, buff_fight=?, 수룡의_마안=?, 풍룡의_마안=?, 지룡의_마안=?, 화룡의_마안=?, 생명의_마안=?, "
					+ "탄생의_마안=?, 형상의_마안=?, 수룡의마안_딜레이=?, 풍룡의마안_딜레이=?, 지룡의마안_딜레이=?, 화룡의마안_딜레이=?, 생명의마안_딜레이=?, "
					+ "탄생의마안_딜레이=?, 형상의마안_딜레이=?, 경험치_드랍_10=?, 경험치_드랍_20=?, 경험치_드랍_50=?, 복수_쿨타임=? WHERE objId=?");
			st.setString(1, pc.getName());
			st.setInt(2, light == null ? 0 : light.getTime());
			st.setInt(3, shield == null ? 0 : shield.getTime());
			st.setInt(4, curse_poison == null ? 0 : curse_poison.getTime());
			st.setInt(5, curse_poison == null ? 0 : curse_poison.getDamage());
			st.setInt(6, curse_blind == null ? 0 : curse_blind.getTime());
			st.setInt(7, slow == null ? 0 : slow.getTime());
			st.setInt(8, curse_paralyze == null ? 0 : curse_paralyze.getTime());
			st.setInt(9, enchant_dexterity == null ? 0 : enchant_dexterity.getTime());
			st.setInt(10, enchant_mighty == null ? 0 : enchant_mighty.getTime());
			st.setInt(11, haste == null ? 0 : haste.getTime());
			st.setInt(12, hastePotion == null ? 0 : hastePotion.getTime());
			st.setInt(13, shape_change == null ? 0 : shape_change.getTime());
			st.setInt(14, immune_to_harm == null ? 0 : immune_to_harm.getTime());
			st.setInt(15, bravery_potion == null ? 0 : bravery_potion.getTime());
			st.setInt(16, elvenwafer == null ? 0 : elvenwafer.getTime());
			st.setInt(17, eva == null ? 0 : eva.getTime());
			st.setInt(18, wisdom == null ? 0 : wisdom.getTime());
			st.setInt(19, blue_potion == null ? 0 : blue_potion.getTime());
			st.setInt(20, floating_eye_meat == null ? 0 : floating_eye_meat.getTime());
			st.setInt(21, clearmind == null ? 0 : clearmind.getTime());
			st.setInt(22, resistelemental == null ? 0 : resistelemental.getTime());
			st.setInt(23, icelance == null ? 0 : icelance.getTime());
			st.setInt(24, earthskin == null ? 0 : earthskin.getTime());
			st.setInt(25, ironskin == null ? 0 : ironskin.getTime());
			st.setInt(26, blessearth == null ? 0 : blessearth.getTime());
			st.setInt(27, curse_ghoul == null ? 0 : curse_ghoul.getTime());
			st.setInt(28, chatting_close == null ? 0 : chatting_close.getTime());
			st.setInt(29, holywalk == null ? 0 : holywalk.getTime());
			st.setInt(30, shadowarmor == null ? 0 : shadowarmor.getTime());
			st.setInt(31, expPotion == null ? 0 : expPotion.getTime());
			st.setInt(32, frameSpeedOverSutn == null ? 0 : frameSpeedOverSutn.getTime());
			st.setInt(33, buff_fight == null ? 0 : buff_fight.getTime());
			st.setInt(34, 수룡의마안 == null ? 0 : 수룡의마안.getTime());
			st.setInt(35, 풍룡의마안 == null ? 0 : 풍룡의마안.getTime());
			st.setInt(36, 지룡의마안 == null ? 0 : 지룡의마안.getTime());
			st.setInt(37, 화룡의마안 == null ? 0 : 화룡의마안.getTime());
			st.setInt(38, 생명의마안 == null ? 0 : 생명의마안.getTime());
			st.setInt(39, 탄생의마안 == null ? 0 : 탄생의마안.getTime());
			st.setInt(40, 형상의마안 == null ? 0 : 형상의마안.getTime());
			st.setInt(41, 수룡의마안_딜레이 == null ? 0 : 수룡의마안_딜레이.getTime());
			st.setInt(42, 풍룡의마안_딜레이 == null ? 0 : 풍룡의마안_딜레이.getTime());
			st.setInt(43, 지룡의마안_딜레이 == null ? 0 : 지룡의마안_딜레이.getTime());
			st.setInt(44, 화룡의마안_딜레이 == null ? 0 : 화룡의마안_딜레이.getTime());
			st.setInt(45, 생명의마안_딜레이 == null ? 0 : 생명의마안_딜레이.getTime());
			st.setInt(46, 탄생의마안_딜레이 == null ? 0 : 탄생의마안_딜레이.getTime());
			st.setInt(47, 형상의마안_딜레이 == null ? 0 : 형상의마안_딜레이.getTime());
			st.setInt(48, 경험치_드랍_10 == null ? 0 : 경험치_드랍_10.getTime());
			st.setInt(49, 경험치_드랍_20 == null ? 0 : 경험치_드랍_20.getTime());
			st.setInt(50, 경험치_드랍_50 == null ? 0 : 경험치_드랍_50.getTime());
			st.setInt(51, 복수_쿨타임 == null ? 0 : 복수_쿨타임.getTime());
			st.setLong(52, pc.getObjectId());
			st.executeUpdate();
			//
			PluginController.init(CharactersDatabase.class, "saveBuff", pc, con);
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveBuff(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * 기억 정보 저장 함수.
	 * 
	 * @param con
	 * @param pc
	 */
	static public void saveBook(Connection con, PcInstance pc) {
		PreparedStatement st = null;
		try {
			List<Book> list = BookController.find(pc);
			if (list == null)
				return;
			//
			st = con.prepareStatement("DELETE FROM characters_book WHERE objId=?");
			st.setLong(1, pc.getObjectId());
			st.executeUpdate();
			st.close();
			//
			for (Book b : list) {
				st = con.prepareStatement("INSERT INTO characters_book SET objId=?, name=?, location=?, locX=?, locY=?, locMAP=?");
				st.setLong(1, pc.getObjectId());
				st.setString(2, pc.getName());
				st.setString(3, b.getLocation());
				st.setInt(4, b.getX());
				st.setInt(5, b.getY());
				st.setInt(6, b.getMap());
				st.executeUpdate();
				st.close();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveBook(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * pvp시 승리햇을경우 호출됨
	 * 
	 * @param pc
	 *            : 승리자
	 * @param target
	 *            : 패배자
	 */
	static public void updatePvpKill(PcInstance pc, PcInstance target) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO characters_pvp SET objectId=?, name=?, is_kill=?, target_objectId=?, target_name=?, pvp_date=?");
			st.setLong(1, pc.getObjectId());
			st.setString(2, pc.getName());
			st.setInt(3, 1);
			st.setLong(4, target.getObjectId());
			st.setString(5, target.getName());
			st.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updatePvpKill(PcInstance pc, PcInstance target)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}


	/**
	 * pvp시 패배했을때 호출됨.
	 * 
	 * @param pc
	 *            : 패배자
	 * @param target
	 *            : 승리자
	 */
	static public void updatePvpDead(PcInstance pc, PcInstance target) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO characters_pvp SET objectId=?, name=?, is_dead=?, target_objectId=?, target_name=?, pvp_date=?");
			st.setLong(1, pc.getObjectId());
			st.setString(2, pc.getName());
			st.setInt(3, 1);
			st.setLong(4, target.getObjectId());
			st.setString(5, target.getName());
			st.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updatePvpDead(PcInstance pc, PcInstance target)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	/**
	 * pvp시 죽인 횟수 리턴함.
	 * 
	 * @param pc
	 * @return
	 */
	static public int getPvpKill(PcInstance pc) {
		int cnt = 0;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT COUNT(uid) as cnt FROM characters_pvp WHERE objectId=? AND is_kill=1");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			if (rs.next())
				cnt = rs.getInt("cnt");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getPvpKill(PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return cnt;
	}

	/**
	 * pvp시 죽은 횟수 리턴함.
	 * 
	 * @param pc
	 * @return
	 */
	static public int getPvpDead(PcInstance pc) {
		int cnt = 0;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT COUNT(uid) as cnt FROM characters_pvp WHERE objectId=? AND is_dead=1");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			if (rs.next())
				cnt = rs.getInt("cnt");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getPvpDead(PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return cnt;
	}
	
	/**
	 * 차단 리스트 저장
	 * 2017-12-18
	 * by all-night
	 */
	static public void saveBlockList(Connection con, PcInstance pc) {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("DELETE FROM characters_block_list WHERE cha_objId=?");
			st.setLong(1, pc.getObjectId());
			st.executeUpdate();
			st.close();
			// 처리.
			for (String name : pc.getListBlockName()) {
				st = con.prepareStatement("INSERT INTO characters_block_list SET cha_objId=?, cha_name=?, block_name=?");
				st.setLong(1, pc.getObjectId());
				st.setString(2, pc.getName());
				st.setString(3, name);
				st.executeUpdate();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveBlockList(Connection con, PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * 차단 리스트 추출
	 * 2017-12-18
	 * by all-night
	 */
	static public void readBlockList(PcInstance pc) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_block_list WHERE cha_objId=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			
			while (rs.next()) {
				if (!pc.getListBlockName().contains(rs.getString("block_name")))
					pc.getListBlockName().add(rs.getString("block_name"));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readBlockList(PcInstance pc)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	
	/**
	 * 채금 해제
	 * 2019-07-07
	 * by connector12@nate.com
	 */
	static public void chattingCloseRemove(String name) {
		Connection con = null;
		PreparedStatement st = null;

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters_buff SET chatting_close=0 WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : chattingCloseRemove(String name)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	/**
	 * 전체 채금 해제
	 * 2019-07-07
	 * by connector12@nate.com
	 */
	static public void chattingCloseAllRemove() {
		Connection con = null;
		PreparedStatement st = null;

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters_buff SET chatting_close=0");
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : chattingCloseAllRemove()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	/**
	 * 혈맹 가입시 정보 수정.
	 * 2019-10-29
	 * by connector12@nate.com
	 */
	static public void updateClan(PcInstance pc, Clan c) {
		if (c != null) {
			Connection con = null;
			PreparedStatement st = null;

			pc.setTempClanName(pc.getClanName());
			pc.setTempClanId(pc.getClanId());
			pc.setTempTitle(pc.getTitle());
			pc.setTempClanGrade(pc.getClanGrade());			
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("UPDATE characters SET clanID=?, clanNAME=?, title=?, clan_grade=?, temp_clan_id=?, temp_clan_name=?, temp_title=?, temp_clan_grade=? WHERE objID=?");
				st.setInt(1, pc.getClanId());
				st.setString(2, pc.getClanName() == null ? "" : pc.getClanName());
				st.setString(3, pc.getTitle() == null ? "" : pc.getTitle());
				st.setInt(4, pc.getClanGrade());
				st.setInt(5, pc.getTempClanId());
				st.setString(6, pc.getTempClanName() == null ? "" : pc.getTempClanName());
				st.setString(7, pc.getTempTitle() == null ? "" : pc.getTempTitle());
				st.setInt(8, pc.getTempClanGrade());
				st.setLong(9, pc.getObjectId());
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.printf("%s : updateClan(PcInstance pc, Clan c)\r\n", CharactersDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}
	
	static public void classChange(long objId, int sex, int classType, int classGfx) {
		Connection con = null;
		PreparedStatement st = null;

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET sex=?, class=?, gfx=?, attribute=0 WHERE objID=?");
			st.setInt(1, sex);
			st.setInt(2, classType);
			st.setInt(3, classGfx);
			st.setLong(4, objId);
			st.executeUpdate();
			st.close();
			
			st = con.prepareStatement("DELETE FROM characters_skill WHERE cha_objId=?");
			st.setLong(1, objId);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : classChange(long objId, int sex, int classType, int classGfx)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	static public void updateAutoHuneTime() {
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET 자동사냥_남은시간=?");
			st.setInt(1, Lineage.auto_hunt_time);
			st.executeUpdate();
			
			st.close();
			st = con.prepareStatement("UPDATE accounts SET 자동사냥_이용시간=?");
			st.setInt(1, Lineage.auto_hunt_time);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateAutoHuneTime()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
}
