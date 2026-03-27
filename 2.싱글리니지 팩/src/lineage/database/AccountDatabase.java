package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import lineage.share.Lineage;

public final class AccountDatabase {
	
	static public void updateNoticeUid(Connection con, int uid, int notice_uid){
		PreparedStatement st = null;
		try{
			st = con.prepareStatement("UPDATE accounts SET notice_uid=? WHERE uid=?");
			st.setInt(1, notice_uid);
			st.setInt(2, uid);
			st.executeUpdate();
		}catch(Exception e){
			lineage.share.System.printf("%s : updateNoticeUid(Connection con, int uid, int notice_uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		}finally{
			DatabaseConnection.close(st);
		}
	}
	static public void updateDaycheck2() {
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET daytime=0");
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateDaycheck()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateRQuestCount(){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET RandomQuestCount=0 ");

			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : 3()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateRQuest(){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET radomquest=0 ");

			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : 3()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
//	static public void Ticket(int uid,long Ticketcount) {
//		Connection con = null;
//		PreparedStatement st = null;
//		
//		try {
//			con = DatabaseConnection.getLineage();
//			st = con.prepareStatement("UPDATE  characters  SET FighterTicket=FighterTicket+?  WHERE objID=? ");
//			st.setLong(1, Ticketcount);
//			st.setInt(2, uid);
//			st.executeUpdate();
//		} catch (Exception e) {
//			lineage.share.System.printf("%s : userpointbuy(String id,long point)\r\n", CharactersDatabase.class.toString());
//			lineage.share.System.println(e);
//		} finally {
//			DatabaseConnection.close(con,st);
//		}
//	}
	
	//싸울의뢰
	private static ConcurrentHashMap<Integer, Long> ticketCountCache = new ConcurrentHashMap<>();
	
	public static void buyTickets(int uid, long ticketCount) {
	    try (Connection con  = DatabaseConnection.getLineage()) { // 커넥션 풀에서 커넥션 얻기
	        // UPDATE 쿼리 작성 및 실행
	        try (PreparedStatement st = con.prepareStatement("UPDATE characters SET FighterTicket = FighterTicket + ? WHERE objID = ?")) {
	            con.setAutoCommit(false); // 트랜잭션 시작

	            st.setLong(1, ticketCount);
	            st.setInt(2, uid);
	            st.executeUpdate();

	            // 트랜잭션 커밋
	            con.commit();

	            // 캐시 업데이트 (필요에 따라 수행)
	            ticketCountCache.compute(uid, (key, value) -> (value == null) ? ticketCount : value + ticketCount);
	        } catch (SQLException e) {
	            // 트랜잭션 롤백
	            if (con != null) {
	                con.rollback();
	            }
	            e.printStackTrace();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
//	static public List<String> getCharacterRanks() {
//	    Connection con = null;
//	    PreparedStatement st = null;
//	    ResultSet rs = null;
//	    List<String> characterRanks = new ArrayList<>();
//
//	    try {
//	        con = DatabaseConnection.getLineage();
//	        st = con.prepareStatement("SELECT name, FighterTicket FROM characters ORDER BY FighterTicket DESC;");
//	        rs = st.executeQuery();
//
//	        while (rs.next()) {
//	            String name = rs.getString("name");
//	            int fighterTicket = rs.getInt("FighterTicket");
//	            characterRanks.add(name + " : " + fighterTicket + ")"); // 이름과 FighterTicket 정보 추가
//	        }
//	    } catch (Exception e) {
//	        lineage.share.System.printf("%s : getCharacterRanks()\r\n", AccountDatabase.class.toString());
//	        lineage.share.System.println(e);
//	    } finally {
//	        DatabaseConnection.close(con, st, rs);
//	    }
//
//	    return characterRanks;
//	}
//	static public int checkRank(int objID) {
//	    Connection con = null;
//	    PreparedStatement st = null;
//	    ResultSet rs = null;
//	    int rank = 0;
//
//	    try {
//	        con = DatabaseConnection.getLineage();
//	        st = con.prepareStatement("SELECT COUNT(*) as rank FROM characters WHERE FighterTicket > (SELECT FighterTicket FROM characters WHERE objID = ?);");
//	        st.setInt(1, objID);
//	        rs = st.executeQuery();
//	        if (rs.next()) {
//	            rank = rs.getInt("rank") + 1; // Adding 1 to get the actual rank
//	        }
//	    } catch (Exception e) {
//	        lineage.share.System.printf("%s : checkRank(int objID)\r\n", AccountDatabase.class.toString());
//	        lineage.share.System.println(e);
//	    } finally {
//	        DatabaseConnection.close(con, st, rs);
//	    }
//	    return rank;
//	}
	/**
	 * 공지사항 확인된 칼럼값 추출.
	 * @param con
	 * @param uid
	 * @return
	 */
	static public int getNoticeUid(int uid){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT notice_uid FROM accounts WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getNoticeUid(int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
	
	/**
	 * 아이피당 소유가능한 갯수 확인하여 생성 가능여부 리턴함.
	 * @param ip
	 * @return
	 */
	static public boolean isAccountCount(String ip){
		if(Lineage.account_ip_count<=0 || ip==null || ip.length()<=0)
			return true;
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT COUNT(*) FROM accounts WHERE last_ip=?");
			st.setString(1, ip);
			rs = st.executeQuery();
			if(rs.next())
				return rs.getInt(1) < Lineage.account_ip_count;
		} catch (Exception e) {
			lineage.share.System.printf("%s : isAccountCount(String ip)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		return true;
	}

	/**
	 * 계정 아이디를 통한 uid값 추출하는 함수.
	 * @param con
	 * @param id
	 * @return
	 */
	static public int getUid(String id) {
		int uid = 0;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM accounts WHERE LOWER(id)=?");
			st.setString(1, id.toLowerCase());
			rs = st.executeQuery();
			if(rs.next())
				uid = rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getUid(String id)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return uid;
	}
	
	static public void userpoint(String id,long point){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO cashback SET 계정=?, point=? ON DUPLICATE KEY UPDATE 계정=?, point=point+?  ");
			st.setString(1, id);
			st.setLong(2, point);
			st.setString(3, id);
			st.setLong(4, point);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : userpoint(int id, point )\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public long userpointcheck(String id) {
		Connection con = null;
		PreparedStatement st = null;
		long point = 0;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
	
			st = con.prepareStatement("select point from cashback where 계정=? ");
			st.setString(1, id);
			rs = st.executeQuery();
			if(rs.next())
				point = rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : userpointbuy(String id,long point)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con,st,rs);
		}
		return point;
	}
	
	static public String pwcheck(String id) {
		Connection con = null;
		PreparedStatement st = null;
		
		String pw = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
	
			st = con.prepareStatement("select pw from accounts where id=?");
			st.setString(1, id);
			rs = st.executeQuery();
			if(rs.next())
				pw = rs.getString(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : userpointbuy(String id,long point)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con,st,rs);
		}
		return pw;
	}
	static public void userpointbuy(String id,long point) {
		Connection con = null;
		PreparedStatement st = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE  cashback  SET point=point-?  WHERE 계정=? ");
			st.setLong(1, point);
			st.setString(2, id);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : userpointbuy(String id,long point)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con,st);
		}
	}
	static public String getid(String id){
		String id1 = null;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT account from characters where name =?;");
			st.setString(1, id);
			rs = st.executeQuery();
			if(rs.next())
				id1 = rs.getString(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getTime(int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return id1;
	}
	/**
	 * uid값에 필드에 id와 pw가 일치하는지 확인.
	 * @param con
	 * @param uid
	 * @param id
	 * @param pw
	 * @return
	 */
	static public boolean isAccount(int uid, String id, String pw){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM accounts WHERE uid=? AND LOWER(id)=? AND LOWER(pw)=?");
			st.setInt(1, uid);
			st.setString(2, id.toLowerCase());
			st.setString(3, pw.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isAccount(int uid, String id, String pw)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		return false;
	}
	
	/**
	 * 예전 슈롬에서 사용하던 디비값 패스워드 처리를 위해 사용할 함수.
	 * @param con
	 * @param uid
	 * @param id
	 * @param pw
	 * @return
	 */
	static public boolean isAccountOld(int uid, String id, String pw){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM accounts WHERE uid=? AND LOWER(id)=? AND old_pw=OLD_PASSWORD(?)");
			st.setInt(1, uid);
			st.setString(2, id.toLowerCase());
			st.setString(3, pw.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isAccountOld(int uid, String id, String pw)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return false;
	}
	
	/**
	 * 블럭된 계정인지 확인.
	 * @param con
	 * @param uid
	 * @param id
	 * @return
	 */
	static public boolean isBlock(int uid){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM accounts WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next()){
				try{ return rs.getTimestamp("block_date").getTime()>0; }catch(Exception e){}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : isBlock(int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return false;
	}
	
	/**
	 * uid와 연결된 계정에 소유중인 케릭터 갯수 리턴.
	 * @param con
	 * @param uid
	 * @return
	 */
	static public int getCharacterLength(Connection con, int uid) {
		int length = 0;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT objID FROM characters WHERE account_uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			while (rs.next()) {
				if (CharacterMarbleDatabase.checkData(rs.getInt("objID"))) {
					continue;
				}
				
				length++;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterLength(Connection con, int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return length;
	}
	
	/**
	 * uid와 연결된 계정의 패스워드를 변경하는 메서드.
	 * @param con
	 * @param uid
	 * @param nPw
	 */
	static public void changePw(int uid, String nPw){
		Connection con = null;
		PreparedStatement st = null;
		try{
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET pw=? WHERE uid=?");
			st.setString(1, nPw);
			st.setInt(2, uid);
			st.executeUpdate();
		}catch(Exception e){
			lineage.share.System.printf("%s : changePw(int uid, String nPw)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		}finally{
			DatabaseConnection.close(con, st);
		}
	}
	
	/**
	 * 계정의 남은시간 추출.
	 * @param con
	 * @param uid
	 * @return
	 */
	static public int getTime(int uid){
		int time = 0;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT time FROM accounts WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next())
				time = rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getTime(int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return time;
	}
	
	/**
	 * 계정 추가 처리 함수.
	 * @param con
	 * @param id
	 * @param pw
	 */
	static public void insert(String id, String pw){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO accounts SET id=?, pw=?, register_date=?, time=?, giran_dungeon_time=?, 자동사냥_이용시간=?,daycount=?");
			st.setString(1, id);
			st.setString(2, pw);
			st.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
			st.setInt(4, Lineage.flat_rate_price);
			st.setInt(5, Lineage.giran_dungeon_time);
			st.setInt(6, Lineage.auto_hunt_time);
			st.setInt(7, 1);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : insert(Connection con, String id, String pw)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	/**
	 * 로그인한 계정에 대한 아이피 업데이트 함수.
	 * @param con
	 * @param uid
	 * @param ip
	 */
	static public void updateIp(int uid, String ip){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET last_ip=? WHERE uid=?");
			st.setString(1, ip);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateIp(int uid, String ip)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	//기란감옥
	static public void updateGiran(int girancount, int uid){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET giran_dungeon_count=? WHERE uid=?");
			st.setInt(1, girancount);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateGiran(int uid, levelcheck )\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	//자동사냥
	static public void updateauto(int autocount, int uid){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET auto_count=? WHERE uid=?");
			st.setInt(1, autocount);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateauto(int uid, levelcheck )\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateDaycheck(int uid) {
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET daytime=0 WHERE uid=?");
			st.setInt(1, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateDaycheck()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateDaycp(int daycheck, int uid){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET daycheck=? WHERE uid=?");
			st.setInt(1, daycheck);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateDaycheck()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateDayc(){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET daycheck=0");

			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateDaycheck()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateptime(int dayptime,int uid){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET daytime=? WHERE uid=?");
			st.setInt(1, dayptime);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateDaycount()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateQuestChapter(int chapter,int uid){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET questchapter=? WHERE objID=?");
			st.setInt(1, chapter);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateDaycount()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	//00시에 초기화
	static public void updateQuestKill(){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET questkill=0");

			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateDaycheck()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateRQuestKill(){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET randomquestkill=0 ");

			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : 3()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	static public void updateuQuestKill(int uid){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET questkill=0 WHERE objID=?");
			st.setInt(1, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateDaycheck()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateuRQuestKill(int uid){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET randomquestkill=0 WHERE objID=?");
			st.setInt(1, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : 3()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	static public void updatequestcount(int count,int uid){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET RandomQuestCount=? WHERE objID=?");
			st.setInt(1, count);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : 1()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updaterquest(int rq,int uid){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET radomquest=? WHERE objID=?");
			st.setInt(1, rq);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : 2()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void updateDaycount(int daycount,int uid){
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET daycount=? WHERE uid=?");
			st.setInt(1, daycount);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateDaycount()\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	//레벨달성갱신
	static public void uplevelcheck(int levelcheck, int uid){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET 레벨달성체크=? WHERE uid=?");
			st.setInt(1, levelcheck);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : uplevelcheck(int uid, levelcheck )\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	/**
	 * 로그인한 시간 갱신 처리 함수.
	 * @param con
	 * @param uid
	 */
	static public void updateLoginsDate(int uid){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET logins_date=? WHERE uid=?");
			st.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateLoginsDate(int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	/**
	 * 계정 시간 갱신하는 함수.
	 * @param con
	 * @param uid
	 * @param time
	 */
	static public void updateTime(int uid, int time){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE accounts SET time=? WHERE uid=?");
			st.setInt(1, time);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateTime(int uid, int time)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
}
