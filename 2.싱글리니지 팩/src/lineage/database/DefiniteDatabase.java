package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Definite;
import lineage.share.TimeLine;

public final class DefiniteDatabase {

	static private List<Definite> list;

	static public void init(Connection con){
		TimeLine.start("DefiniteDatabase..");

		list = new ArrayList<Definite>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM definite_scroll");
			rs = st.executeQuery();
			while(rs.next()){
				Definite d = new Definite();
				d.setNameIdNumber( rs.getInt("item_namenumber") );
				d.setName( rs.getString("name") );
				d.setMessage( rs.getInt("msg_number") );
				d.setType( rs.getString("type") );
				// 등록
				list.add(d);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", DefiniteDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public Definite find(int id){
		for( Definite d : list ) {
			if(d.getNameIdNumber() == id)
				return d;
		}
		return null;
	}
	
	static public int getSize(){
		return list.size();
	}
	
}
