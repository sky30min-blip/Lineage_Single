package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectTitle;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.RobotClan;


public class RobotClanController {
	public static List<RobotClan> list;
	
	static public void init(Connection con) {
		TimeLine.start("무인혈맹컨트롤러..");
		
		list = new ArrayList<RobotClan>();
		load(con);
		
		TimeLine.end();
	}
	
	public static void load(Connection con) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = con.prepareStatement("SELECT * FROM auto_clan_list");
			rs = st.executeQuery();
			
			while (rs.next()) {
				RobotClan ci = new RobotClan();
				ci.setObjectId(ServerDatabase.nextEtcObjId());
				ci.setClanId(rs.getInt("clan_id"));
				ci.setMasterClanId(rs.getInt("clan_id"));
				ci.setClanName(rs.getString("clan_name"));
				ci.setMasterClanName(rs.getString("clan_name"));
				ci.setGfx(rs.getInt("gfx"));
				ci.setJoinLevel(rs.getInt("가입레벨"));
				ci.set군주(rs.getInt("군주") == 1 ? true : false);
				ci.set기사(rs.getInt("기사") == 1 ? true : false);
				ci.set요정(rs.getInt("요정") == 1 ? true : false);
				ci.set마법사(rs.getInt("마법사") == 1 ? true : false);
				ci.set다크엘프(rs.getInt("다크엘프") == 1 ? true : false);
				ci.setX(rs.getInt("x"));
				ci.setY(rs.getInt("y"));
				ci.setMap(rs.getInt("map"));
				ci.setHeading(rs.getInt("heading"));
				ci.setPc_objectId(rs.getInt("pc_objId"));
				append(ci);
				
				ci.setName(setName(ci.getMasterClanName()));
				ci.toTeleport(ci.getX(), ci.getY(), ci.getMap(), false);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : load(Connection con)\r\n", RobotClanController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	public static void reload() {
		TimeLine.start("auto_clan_list 테이블 리로드 완료 - ");
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		for (RobotClan ci : getList()) {
			ci.clearList(true);
			World.remove(ci);
		}
		
		synchronized (list) {
			try {
				list.clear();
				
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("SELECT * FROM auto_clan_list");
				rs = st.executeQuery();
				
				while (rs.next()) {
					RobotClan ci = new RobotClan();
					ci.setObjectId(ServerDatabase.nextEtcObjId());
					ci.setClanId(rs.getInt("clan_id"));
					ci.setMasterClanId(rs.getInt("clan_id"));
					ci.setClanName(rs.getString("clan_name"));
					ci.setMasterClanName(rs.getString("clan_name"));
					ci.setGfx(rs.getInt("gfx"));
					ci.setJoinLevel(rs.getInt("가입레벨"));
					ci.set군주(rs.getInt("군주") == 1 ? true : false);
					ci.set기사(rs.getInt("기사") == 1 ? true : false);
					ci.set요정(rs.getInt("요정") == 1 ? true : false);
					ci.set마법사(rs.getInt("마법사") == 1 ? true : false);
					ci.set다크엘프(rs.getInt("다크엘프") == 1 ? true : false);
					ci.setX(rs.getInt("x"));
					ci.setY(rs.getInt("y"));
					ci.setMap(rs.getInt("map"));
					ci.setHeading(rs.getInt("heading"));
					ci.setPc_objectId(rs.getInt("pc_objId"));
					append(ci);
					
					ci.setName(setName(ci.getMasterClanName()));
					ci.toTeleport(ci.getX(), ci.getY(), ci.getMap(), false);
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : reload()\r\n", RobotClanController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}
		
		TimeLine.end();
	}
	static public long checkRobot(long objId) {
		Connection con = null;
		PreparedStatement st = null;
		long check = 0;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
	
			st = con.prepareStatement("select * from auto_clan_list where pc_objId =? ");
			st.setLong(1, objId);
			rs = st.executeQuery();
			if(rs.next())
				check = rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : checkRobot(String id,long point)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con,st,rs);
		}
		return check;
	}
	public static void save(Connection con) {
		PreparedStatement st = null;

		try {
			st = con.prepareStatement("DELETE FROM auto_clan_list");
			st.executeUpdate();
			st.close();
			
			for (RobotClan ci : getList()) {
				try {
					int i = 1;
					st = con.prepareStatement("INSERT INTO auto_clan_list SET clan_id=?, clan_name=?, gfx=?, 가입레벨=?, 군주=?, 기사=?, 요정=?, 마법사=?, 다크엘프=?, x=?, y=?, map=?, heading=?, pc_objId=?");
					st.setInt(i++, ci.getMasterClanId());
					st.setString(i++, ci.getMasterClanName() == null ? "" : ci.getMasterClanName());
					st.setInt(i++, ci.getGfx());
					st.setInt(i++, ci.getJoinLevel());
					st.setInt(i++, ci.is군주() == true ? 1 : 0);
					st.setInt(i++, ci.is기사() == true ? 1 : 0);
					st.setInt(i++, ci.is요정() == true ? 1 : 0);
					st.setInt(i++, ci.is마법사() == true ? 1 : 0);
					st.setInt(i++, ci.is다크엘프() == true ? 1 : 0);
					st.setInt(i++, ci.getX());
					st.setInt(i++, ci.getY());
					st.setInt(i++, ci.getMap());
					st.setInt(i++, ci.getHeading());
					st.setInt(i++, (int) ci.getPc_objectId());
					st.executeUpdate();
					st.close();
				} catch (Exception e) {
					lineage.share.System.printf("%s : save() -> 혈맹id: %d, 혈맹명: %s\r\n", RobotClanController.class.toString(), ci.getMasterClanId(), ci.getMasterClanName());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(st);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : save()\r\n", RobotClanController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	public static void append(RobotClan o) {
		synchronized (list) {
			if (!list.contains(o)) {
				list.add(o);
			}
		}
	}
	
	public static void remove(RobotClan o) {
		synchronized (list) {
			if (list.contains(o)) {
				list.remove(o);
			}		
		}
	}
	
	public static List<RobotClan> getList() {
		synchronized (list) {
			return new ArrayList<RobotClan>(list);
		}
	}
	
	public static RobotClan get무인혈맹(int clanId) {
		synchronized (list) {
			for (RobotClan temp : list) {
				if (temp.getMasterClanId() == clanId) {
					return temp;
				}
			}
		}
		
		return null;
	}
	
	public static RobotClan find무인혈맹(int objId) {
		synchronized (list) {
			for (RobotClan temp : list) {
				if (temp.getObjectId() == objId) {
					return temp;
				}
			}
		}
		
		return null;
	}
	
	public static void insert(PcInstance pc) {
		
		long check = checkRobot(pc.getObjectId());
		
		if (!checkZone(pc)) {
			return;
		}
		
		if (!checkMaster(pc, true)) {
			return;
		}
		if (check != 0) {
			ChattingController.toChatting(pc, "\\fR이미 개설중인 무인혈맹이 있습니다.", Lineage.CHATTING_MODE_MESSAGE);

			return;
		}
		
		RobotClan ci = get무인혈맹(pc.getClanId());

		



			
			if (ci == null ) {
				ci = new RobotClan();
				ci.setObjectId(ServerDatabase.nextEtcObjId());
				ci.setClanId(pc.getClanId());
				ci.setMasterClanId(pc.getClanId());
				ci.setClanName(pc.getClanName());
				ci.setMasterClanName(pc.getClanName());
				ci.setHeading(pc.getHeading());
				ci.setPc_objectId(pc.getObjectId());
				setGfx(pc, ci, true);
				append(ci);
				
				ci.setName(setName(ci.getMasterClanName()));
				ci.toTeleport(pc.getX(), pc.getY(), pc.getMap(), false);
				insertDB(ci);
				
				String msg = String.format("\\fY  *** [%s] 혈맹에서 혈맹원을 모집합니다. ***", ci.getClanName());
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
				msg = String.format("함께하실 혈맹원 모십니다.", ci.getClanName());
				ChattingController.toChatting(ci, msg, Lineage.CHATTING_MODE_SHOUT);
			}else{
				ci.toTalk(pc, null);
			}
		

		
	}
	
	public static String setName(String clanName) {
		//return String.format("(%s) 혈맹", clanName);
		return String.format("%s", clanName);
	}
	
	public static void setGfx(PcInstance pc, RobotClan ci, boolean setGfx) {
		if (setGfx) {
			switch (pc.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				if (pc.getClassSex() == 0)
					ci.setGfx(2611);
				else
					ci.setGfx(2612);
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				if (pc.getClassSex() == 0)
					ci.setGfx(2613);
				else
					ci.setGfx(2614);
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				if (pc.getClassSex() == 0)
					ci.setGfx(2615);
				else
					ci.setGfx(2616);
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				if (pc.getClassSex() == 0)
					ci.setGfx(2617);
				else
					ci.setGfx(2618);
				break;
			}
		} else {
			ci.setGfx(118);
		}
	}
	
	public static void insertDB(RobotClan ci) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		boolean result = false;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM auto_clan_list WHERE clan_id=?");
			st.setInt(1, ci.getMasterClanId());
			rs = st.executeQuery();
			
			result = rs.next() ? true : false;
		} catch (Exception e) {
			lineage.share.System.printf("%s : insertDB(무인혈맹 ci) -> [SELECT] 혈맹id: %d, 혈맹명: %s\r\n", RobotClanController.class.toString(), ci.getMasterClanId(), ci.getMasterClanName());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		try {
			if (result) {
				st = con.prepareStatement("DELETE FROM auto_clan_list WHERE clan_id=?");
				st.setInt(1, ci.getMasterClanId());
				st.executeUpdate();
				st.close();
			}
			
			int i = 1;
			st = con.prepareStatement("INSERT INTO auto_clan_list SET clan_id=?, clan_name=?, gfx=?, 가입레벨=?, 군주=?, 기사=?, 요정=?, 마법사=?, 다크엘프=?, x=?, y=?, map=?, heading=?, pc_objId=?");
			st.setInt(i++, ci.getMasterClanId());
			st.setString(i++, ci.getMasterClanName() == null ? "" : ci.getMasterClanName());
			st.setInt(i++, ci.getGfx());
			st.setInt(i++, ci.getJoinLevel());
			st.setInt(i++, ci.is군주() == true ? 1 : 0);
			st.setInt(i++, ci.is기사() == true ? 1 : 0);
			st.setInt(i++, ci.is요정() == true ? 1 : 0);
			st.setInt(i++, ci.is마법사() == true ? 1 : 0);
			st.setInt(i++, ci.is다크엘프() == true ? 1 : 0);
			st.setInt(i++, ci.getX());
			st.setInt(i++, ci.getY());
			st.setInt(i++, ci.getMap());
			st.setInt(i++, ci.getHeading());
			st.setInt(i++, (int) ci.getPc_objectId());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : insertDB(무인혈맹 ci) -> [INSERT] 혈맹id: %d, 혈맹명: %s\r\n", RobotClanController.class.toString(), ci.getMasterClanId(), ci.getMasterClanName());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	public static void remove(PcInstance pc, int clanId) {
		if (!checkZone(pc)) {
			return;
		}
		
		if (!checkMaster(pc, false)) {
			return;
		}
		
		RobotClan ci = get무인혈맹(pc.getGm() > 0 ? clanId : pc.getClanId());
		
		if (ci != null) {
			removeDB(ci.getMasterClanId());
			remove(ci);
			
			ci.clearList(true);
			World.remove(ci);
		} else {
			ChattingController.toChatting(pc, "[무인혈맹] 혈맹 관리자가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	static public void toJoin(PcInstance pc, RobotClan ci) {
		if (pc.getLevel() < ci.getJoinLevel()) {
			ChattingController.toChatting(pc, String.format("'%s' 혈맹은 %d레벨 이상 가입 가능합니다.", ci.getClanName(), ci.getJoinLevel()), Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		boolean isClass = true;
		switch (pc.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			if (!ci.is군주()) {
				isClass = false;
			}
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			if (!ci.is기사()) {
				isClass = false;
			}
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			if (!ci.is요정()) {
				isClass = false;
			}
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			if (!ci.is마법사()) {
				isClass = false;
			}
			break;
		}
		
		if (!isClass) {
			ChattingController.toChatting(pc, "해당 클래스는 가입할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		if (pc.getClanId() > 0) {
			// 89 \f1이미 혈맹에 가입했습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 89));
		} else {
			Clan c = ClanController.find(ci.getMasterClanId());
			Kingdom k = KingdomController.findClanId(ci.getMasterClanId());

			if (k != null && !Lineage.kingdom_clan_join) {
				ChattingController.toChatting(pc, "성혈맹은 가입이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				if (c != null) {
					if (!Lineage.is_two_clan_join && !CharactersDatabase.isClanJoin(pc, c.getName())) {
						ChattingController.toChatting(pc, "계정의 다른캐릭터에 혈맹이 존재합니다.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}

					if (c.sizeMemberList() < Lineage.clan_max && pc.getLevel() > 0) {
						// 가입자 정보 갱신.
						pc.setClanId(c.getUid());
						pc.setClanName(c.getName());
						pc.setTitle("");
						pc.setClanGrade(0);
						CharactersDatabase.updateClan(pc, c);
						// 패킷 처리
						pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), pc), true);
						// 94 \f1%0%o 혈맹의 일원으로 받아들였습니다.
						c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 94, pc.getName()));
						// 95 \f1%0 혈맹에 가입하였습니다.
						pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 95, c.getName()));
						// 혈맹 관리목록 갱신
						c.appendMemberList(pc.getName());
						c.appendList(pc);
						ClanController.updateClan(c);
					} else {
						ChattingController.toChatting(pc, String.format("혈맹의 최대 가입인원은 %d명 입니다.", Lineage.clan_max), Lineage.CHATTING_MODE_MESSAGE);
					}
				}
			}
		}
	}
	
	public static boolean checkMaster(PcInstance pc, boolean insert) {
		if (!insert && pc.getGm() > 0) {
			return true;
		}
		
		if (pc.getClanId() == 0) {
			ChattingController.toChatting(pc, "[무인혈맹] 가입된 혈맹이 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		
		if (pc.getClanId() <= 3) {
			ChattingController.toChatting(pc, "[무인혈맹] 신규혈맹은 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		
		if (pc.getClanGrade() < 2) {
			ChattingController.toChatting(pc, "[무인혈맹] 부군주 이상 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	public static boolean checkZone(PcInstance pc) {
		if (!World.isSafetyZone(pc.getX(), pc.getY(), pc.getMap())) {
			ChattingController.toChatting(pc, "세이프티존에서 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	public static void move(PcInstance pc, RobotClan ci) {
		if (checkZone(pc)) {
			ci.setHeading(pc.getHeading());
			ci.toTeleport(pc.getX(), pc.getY(), pc.getMap(), false);
		}
	}
	
	public static void update(int oldClanId, int newClanId) {
		Connection con = null;
		PreparedStatement st = null;
		
		RobotClan ci = get무인혈맹(oldClanId);
		
		synchronized (list) {
			if (ci != null) {
				ci.setClanId(newClanId);
				ci.setMasterClanId(newClanId);
				ci.toTeleport(ci.getX(), ci.getY(), ci.getMap(), false);
				
				try {
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("UPDATE auto_clan_list SET clan_id=? WHERE clan_id=?");
					st.setInt(1, newClanId);
					st.setInt(2, oldClanId);
					st.executeUpdate();
				} catch (Exception e) {
					lineage.share.System.printf("%s : update(int oldClanId, int newClanId) -> oldClanId: %d, newClanId: %d\r\n", RobotClanController.class.toString(), oldClanId, newClanId);
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st);
				}
			}
		}
	}
	
	public static void removeDB(int clanId) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		synchronized (list) {
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("DELETE FROM auto_clan_list WHERE clan_id=?");
				st.setInt(1, clanId);
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.printf("%s : removeDB(int clanId) -> clanId: %d\r\n", RobotClanController.class.toString(), clanId);
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}
	}
	
	public static void removeClan(int clanId) {
		RobotClan ci = get무인혈맹(clanId);	

		if (ci != null) {
			removeDB(ci.getMasterClanId());
			remove(ci);
			
			ci.clearList(true);
			World.remove(ci);
		}
	}
}
