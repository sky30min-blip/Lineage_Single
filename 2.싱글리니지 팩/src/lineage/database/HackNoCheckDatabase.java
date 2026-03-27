package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class HackNoCheckDatabase {
	static private List<String> list;
	
	static public void init(Connection con) {
		TimeLine.start("HackNoCheckDatabase..");

		list = new ArrayList<String>();

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM hack_no_check_ip");
			rs = st.executeQuery();
			while (rs.next()) {
				list.add(rs.getString("ip"));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", HackNoCheckDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public void reload() {
		TimeLine.start("hack_no_check_ip 테이블 리로드 완료 - ");
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		synchronized (list) {
			try {
				list.clear();
				
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("SELECT * FROM hack_no_check_ip");
				rs = st.executeQuery();
				
				while (rs.next()) {
					list.add(rs.getString("ip"));
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : reload()\r\n", HackNoCheckDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}
		
		TimeLine.end();
	}
	
	static public void append(object o, PcInstance pc) {
		String ip = pc.getClient().getAccountIp();
		
		if (ip != null) {
			Connection con = null;
			PreparedStatement st = null;
			
			synchronized (list) {
				if (!list.contains(ip)) {
					list.add(ip);
					
					try {
						con = DatabaseConnection.getLineage();
						st = con.prepareStatement("INSERT INTO hack_no_check_ip SET ip=?");
						st.setString(1, ip);
						st.executeUpdate();
						
						ChattingController.toChatting(o, String.format("[스핵제외 추가] 캐릭터: %s ip: %s", pc.getName(), ip), Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						lineage.share.System.printf("%s : append(object o, PcInstance pc) -> 캐릭터: %s\r\n", HackNoCheckDatabase.class.toString(), pc.getName());
						lineage.share.System.println(e);
					} finally {
						DatabaseConnection.close(con, st);
					}
				}
			}
		}
	}
	
	static public void remove(object o, PcInstance pc) {
		String ip = pc.getClient().getAccountIp();
		
		if (ip != null) {
			Connection con = null;
			PreparedStatement st = null;
			
			synchronized (list) {
				if (list.contains(ip)) {
					list.remove(ip);
					
					try {
						con = DatabaseConnection.getLineage();
						st = con.prepareStatement("DELETE FROM hack_no_check_ip WHERE ip=?");
						st.setString(1, ip);
						st.executeUpdate();
						
						ChattingController.toChatting(o, String.format("[스핵체크 추가] 캐릭터: %s ip: %s", pc.getName(), ip), Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						lineage.share.System.printf("%s : remove(object o, PcInstance pc) -> 캐릭터: %s\r\n", HackNoCheckDatabase.class.toString(), pc.getName());
						lineage.share.System.println(e);
					} finally {
						DatabaseConnection.close(con, st);
					}
				}
			}
		}
	}
	
	static public boolean isHackCheck(PcInstance pc) {
		String ip = pc.getClient().getAccountIp();
		
		if (ip != null) {
			synchronized (list) {
				for (String tempIp : list) {
					if (tempIp.equalsIgnoreCase(ip)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
