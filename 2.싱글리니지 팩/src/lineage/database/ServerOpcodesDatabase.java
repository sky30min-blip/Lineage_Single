package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ServerOpcodes;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.share.TimeLine;

public final class ServerOpcodesDatabase {

	static private List<ServerOpcodes> list;
	
	static public void init(Connection con){
		TimeLine.start("ServerOpcodesDatabase..");
		
		list = new ArrayList<ServerOpcodes>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM server_opcodes");
			rs = st.executeQuery();
			while(rs.next()){
				ServerOpcodes so = new ServerOpcodes();
				so.setUid( rs.getInt("uid") );
				so.setType( rs.getString("type").equalsIgnoreCase("client") ? ServerOpcodes.TYPE.Client : ServerOpcodes.TYPE.Server );
				so.setName( rs.getString("name") );
				so.setNameOp( rs.getString("name_op") );
				so.setOldOp( rs.getInt("old_op") );
				so.setNowOp( rs.getInt("now_op") );
				
				list.add( so );
			}
			
			// 옵코드 세팅.
			Opcodes.init();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ServerOpcodesDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public int find(int uid, ServerOpcodes.TYPE type){
		for(ServerOpcodes so : list){
			if(so.getUid()==uid && type==so.getType()){
				if(Lineage.server_version < 230)
					return so.getOldOp();
				else
					return so.getNowOp();
			}
		}
		return 255;
	}
	
	static public int getSize(){
		return list.size();
	}
	
}
