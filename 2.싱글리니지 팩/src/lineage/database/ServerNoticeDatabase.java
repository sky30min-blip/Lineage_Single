package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Notice;
import lineage.network.LineageClient;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Notice;
import lineage.share.TimeLine;

public class ServerNoticeDatabase {

	static private List<Notice> list;
	static private List<Notice> list_static;
	
	static public void init(Connection con){
		TimeLine.start("ServerNoticeDatabase..");
		
		list = new ArrayList<Notice>();
		list_static = new ArrayList<Notice>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// 무조건 표현할 공지 먼저 추출.
			st = con.prepareStatement("SELECT * FROM server_notice ORDER BY uid");
			rs = st.executeQuery();
			while(rs.next()){
				Notice n = new Notice();
				n.setUid( rs.getInt("uid") );
				n.setType( rs.getString("type") );
				n.setSubject( rs.getString("subject") );
				n.setContent( rs.getString("content") );
				
				if(n.getType().equalsIgnoreCase("static"))
					list_static.add( n );
				else
					list.add( n );
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ServerNoticeDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public void reload() {
		TimeLine.start("server_notice 테이블 리로드 완료 - ");
		
		list.clear();
		list_static.clear();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// 무조건 표현할 공지 먼저 추출.
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM server_notice ORDER BY uid");
			rs = st.executeQuery();
			while(rs.next()){
				Notice n = new Notice();
				n.setUid( rs.getInt("uid") );
				n.setType( rs.getString("type") );
				n.setSubject( rs.getString("subject") );
				n.setContent( rs.getString("content") );
				
				if(n.getType().equalsIgnoreCase("static"))
					list_static.add( n );
				else
					list.add( n );
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload()\r\n", ServerNoticeDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * 공지사항 확인하는 함수.
	 *  : 표현될 공지사항이 있을경우 true 리턴함.
	 *  : 공지사항 표현 패킷 처리함.
	 * @param c
	 * @return
	 */
	static public boolean toNotice(LineageClient c) {
		// 매번 표현될 공지 부분.
		for (Notice n : list_static) {
			if (c.getAccountNoticeStaticUid() < n.getUid()) {
				toNotice(c, n, true);
				return true;
			}
		}
		// 한번만 표현될 공지 부분.
		for (Notice n : list) {
			if (c.getAccountNoticeUid() < n.getUid()) {
				toNotice(c, n, false);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 중복코드 방지용.
	 * @param c
	 * @param n
	 * @param _static
	 */
	static private void toNotice(LineageClient c, Notice n, boolean _static){
		// uid 갱신.
		if(_static)
			c.setAccountNoticeStaticUid(n.getUid());
		else
			c.setAccountNoticeUid(n.getUid());
		// 공지사항 표현.
		c.toSender(S_Notice.clone(BasePacketPooling.getPool(S_Notice.class), String.format("\\f2%s\r\n%s", n.getSubject(), n.getContent())));
	}
	
}
