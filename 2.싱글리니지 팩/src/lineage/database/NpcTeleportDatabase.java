package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lineage.bean.database.NpcTeleport;
import lineage.share.TimeLine;

public class NpcTeleportDatabase {

	static private List<NpcTeleport> list;
	
	static public void init(Connection con){
		TimeLine.start("NpcTeleportDatabase..");
		
		list = new ArrayList<NpcTeleport>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM npc_teleport");
			rs = st.executeQuery();
			while(rs.next()){
				NpcTeleport nt = new NpcTeleport();
				nt.setName(rs.getString("name"));
				nt.setAction(rs.getString("action"));
				nt.setIdx(rs.getInt("tele_idx"));
				nt.setNum(rs.getInt("tele_num"));
				nt.setCheckMap(rs.getInt("check_map"));
				nt.setX(rs.getInt("x"));
				nt.setY(rs.getInt("y"));
				nt.setMap(rs.getInt("map"));
				nt.setPrice(rs.getInt("aden"));
				
				list.add( nt );
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", NpcTeleportDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public void find(String name, Map<Integer, List<NpcTeleport>> r_list){
		r_list.clear();
		for(NpcTeleport nt : list){
			if(nt.getName().equalsIgnoreCase(name)) {
				List<NpcTeleport> r = r_list.get(nt.getIdx());
				if(r == null) {
					r = new ArrayList<NpcTeleport>();
					r_list.put(nt.getIdx(), r);
				}
				r.add(nt);
			}
		}
	}
}
