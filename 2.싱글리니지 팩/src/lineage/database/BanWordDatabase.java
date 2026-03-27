package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;



public final class BanWordDatabase {
	private static BanWordDatabase _instance;
	public static BanWordDatabase getInstance() {
		if (_instance == null) _instance = new BanWordDatabase();
		return _instance;
	}
	
	public BanWordDatabase() {
		init();
	}
	
	private ArrayList<String> _list = new ArrayList<String>();
	public ArrayList<String> getList() {
		return _list;
	}
	
	private void init() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			pstm = con.prepareStatement("SELECT * FROM ban_word");
			rs = pstm.executeQuery();
			while (rs.next()) {
				_list.add(rs.getString("chat"));
			}
		} catch(Exception e) {
			lineage.share.System.printf("%s : void init\r\n", BanWordDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, pstm, rs);
		}
	}
	
	public void reload() {
		_list.clear();
		init();
	}
	
	public boolean checkMsg(String msg) {
		for (String s : _list) {
			if (msg.indexOf(s) >= 0) {
				return true;
			}
		}
		return false;
	}

}
