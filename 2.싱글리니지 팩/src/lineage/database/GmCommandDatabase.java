package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.GmCommand;
import lineage.share.TimeLine;

public class GmCommandDatabase {

	static private List<GmCommand> list;
	
	static public void init(Connection con){
		TimeLine.start("GmListDatabase..");
		
		list = new ArrayList<GmCommand>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM gm_command");
			rs = st.executeQuery();
			while(rs.next()){
				GmCommand command = new GmCommand();
				command.setName(rs.getString("name"));
				command.setCommand(rs.getString("command"));
				command.setLevel(rs.getInt("level"));
				
				list.add(command);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", GmCommandDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}

	/**
	 * 운영자 명령어 시행가능한 레벨 찾아서 리턴하는 함수.
	 *  : CommandController에서 호출해서 사용함.
	 * @param command
	 * @return
	 */
	static public int find(String command){
		for(GmCommand gc : list){
			if(gc.getCommand().equalsIgnoreCase(command))
				return gc.getLevel();
		}
		return 0;
	}
}
