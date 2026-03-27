package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Exp;
import lineage.share.TimeLine;

public final class ExpDatabase {

	static private List<Exp> pool;
	static private List<Exp> list;
	
	static public void init(Connection con){
		TimeLine.start("ExpDatabase..");
		
		pool = new ArrayList<Exp>();
		list = new ArrayList<Exp>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM exp");
			rs = st.executeQuery();
			while(rs.next()){
				Exp e = new Exp();
				e.setLevel( rs.getInt(1) );
				e.setExp( rs.getDouble(2) );
				e.setBonus( rs.getDouble(3) );
				
				list.add(e);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ExpDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}

	/**
	 * 레벨에 해당하는 경험치 정보 클레스 리턴.
	 */
	static public Exp find(final int level){
		for( Exp e : list ){
			if(e.getLevel() == level)
				return e;
		}
		return null;
	}
	
	static public Exp getPool(){
		Exp e = null;
		synchronized (pool) {
			if(pool.size()>0){
				e = pool.get(0);
				pool.remove(0);
			}else{
				e = new Exp();
			}
		}
		return e;
	}
	
	static public void setPool(Exp e){
		e.close();
		synchronized (pool) {
			if(!pool.contains(e))
				pool.add(e);
		}
	}
	
	static public int getSize(){
		return list.size();
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
}
