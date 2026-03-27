package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Ip;
import lineage.share.TimeLine;

public final class BadIpDatabase {

	static private List<Ip> list;
	
	static public void init(Connection con){
		TimeLine.start("BadIpDatabase..");

		list = new ArrayList<Ip>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM bad_ip");
			rs = st.executeQuery();
			while(rs.next()){
				Ip i = new Ip();
				i.setIp( rs.getString("ip") );
				try{ i.setTime( rs.getTimestamp("register_date").getTime() ); }catch(Exception e){}
				
				list.add( i );
			}
		} catch (Exception e) {
			lineage.share.System.printf("lineage.database.BadIpDatabase.init(Connection con)\r\n : %s\r\n", e.toString());
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	/**
	 * 저장.
	 * @param con
	 */
	static public void close(Connection con) {
		PreparedStatement st = null;
		try{
			st = con.prepareStatement("DELETE FROM bad_ip");
			st.executeUpdate();
			st.close();
			for(Ip i : list){
				st = con.prepareStatement("INSERT INTO bad_ip SET ip=?, register_date=?");
				st.setString(1, i.getIp());
				if(i.getTime()==null || i.getTime()==0)
					st.setString(2, "0000-00-00 00:00:00");
				else
					st.setTimestamp(2, new Timestamp(i.getTime()));
				st.executeUpdate();
				st.close();
			}

			list.clear();
		} catch(Exception e) {
			lineage.share.System.printf("%s : void close(Connection con)\r\n", BadIpDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	static public void save(Connection con) {
		if (list != null) {
			synchronized (list) {
				PreparedStatement st = null;
				try{
					st = con.prepareStatement("DELETE FROM bad_ip");
					st.executeUpdate();
					st.close();
					for(Ip i : list){
						st = con.prepareStatement("INSERT INTO bad_ip SET ip=?, register_date=?");
						st.setString(1, i.getIp());
						if(i.getTime()==null || i.getTime()==0)
							st.setString(2, "0000-00-00 00:00:00");
						else
							st.setTimestamp(2, new Timestamp(i.getTime()));
						st.executeUpdate();
						st.close();
					}
				} catch(Exception e) {
					lineage.share.System.printf("%s : save()\r\n", BadIpDatabase.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(st);
				}
			}
		}
	}
	
	/**
	 * 배드 아이피 등록 처리 메서드.
	 * @param ip
	 */
	static public void append(String ip) {
		synchronized (list) {
			//
			if(find(ip) != null)
				return;
			//
			Ip i = new Ip();
			i.setIp( ip );
			i.setTime( System.currentTimeMillis() );
			
			list.add( i );
		}
	}
	
	/**
	 * 배드 아이피 삭제 처리 메소드.
	 * 2019-07-02
	 * by connector12@nate.com
	 */
	static public void remove(String ip) {
		synchronized (list) {
			Ip i = find(ip);
			//
			if(i != null)
				list.remove(i);
		}
	}
	
	/**
	 * 배드 아이피 전체 삭제 처리 메소드.
	 * 2019-07-02
	 * by connector12@nate.com
	 */
	static public void removeAll() {
		synchronized (list) {
			list.clear();
		}
	}
	
	static public Ip find(String ip){
		synchronized (list) {
			// 검색.
			for(Ip i : list){
				if(ip.equalsIgnoreCase(i.getIp()) || (i.getIp().contains("*") && ip.contains(i.getIp().substring(0, i.getIp().indexOf("*")))))
					return i;
			}
			return null;
		}
	}
	
	static public int getSize(){
		synchronized (list) {
			return list.size();
		}
	}	
}
