package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.bean.lineage.Clan;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Letter;

public final class LetterController {

	static public void init(){
		TimeLine.start("LetterController..");
		
		TimeLine.end();
	}
	
	/**
	 * 사용자가 월드에 접속할때 호출되는 함수.
	 * 아직 인벤토리에 추가안된 편지가 있는지 확인함
	 * 있을경우 그에따른 처리를 함께 함.
	 */
	static public void toWorldJoin(PcInstance pc){
		boolean find = false;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			// 검색 후 처리
			st = con.prepareStatement("SELECT * FROM characters_letter WHERE paperInventory='false' AND LOWER(paperTo)=?");
			st.setString(1, pc.getName().toLowerCase());
			rs = st.executeQuery();
			while(rs.next()){
				find = true;
				// 인벤 등록 처리.
				long time = 0;
				try { time = rs.getTimestamp("paperDate").getTime(); } catch (Exception e) { }
				toLetter(pc, rs.getInt("uid"), rs.getString("paperFrom"), rs.getString("paperTo"), rs.getString("paperSubject"), rs.getString("paperMemo"), time, rs.getInt("paperAden"));
			}
			rs.close();
			st.close();
			// 한개라도 편지가 있을경우 처리.
			if(find){
				// 비둘기 표현
				pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 1091), true);
				// 편지도착 알림 메세지
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 428));
				// 갱신
				st = con.prepareStatement("UPDATE characters_letter SET paperInventory='true' WHERE paperInventory='false' AND LOWER(paperTo)=?");
				st.setString(1, pc.getName().toLowerCase());
				st.executeUpdate();
				st.close();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : toWorldJoin(PcInstance pc)\r\n", LetterController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 편지지 작성한것을 처리하는 함수.
	 */
	static public void toLetter(String from, String to, String subject, String memo, int aden){
		if(to!=null && subject!=null && memo!=null){
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				toLetter(con, from, "Paper", to, subject, memo, aden);
			} catch (Exception e) {
				lineage.share.System.printf("%s : toLetter(object o, String to, String subject, String memo, int aden)\r\n", LetterController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con);
			}
		}
	}
	
	static public void toPledgeLetter(String from, String to, String subject, String memo){
		if(to!=null && subject!=null && memo!=null){
			// 혈맹 찾기.
			Clan clan = ClanController.find(to);
			if(clan != null){
				Connection con = null;
				try {
					con = DatabaseConnection.getLineage();
					// 혈맹원들 이름을 참고해서 편지 보내기.
					for(String cm : clan.getMemberList())
						toLetter(con, from, "clanPaper", cm, subject, memo, 0);
				} catch (Exception e) {
					lineage.share.System.printf("%s : toPledgeLetter(Character cha, String to, String subject, String memo)\r\n", LetterController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con);
				}
			}
		}
	}

	/**
	 * 디비로부터 정보를 읽는 함수.
	 *  : 사용자가 월드접속할때 아이템정보를 불러오고
	 *    거기서 아이템마다 toWorldJoin 를 호출함.
	 *    Letter클레스는 toWorldJoin 가 구현되어 있으며, 그곳에서 이곳을 호출하게됨.
	 */
	static public void read(Connection con, Letter l){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters_letter WHERE uid=?");
			st.setInt(1, l.getLetterUid());
			rs = st.executeQuery();
			if(rs.next()){
				l.setFrom( rs.getString("paperFrom") );
				l.setTo( rs.getString("paperTo") );
				l.setSubject( rs.getString("paperSubject") );
				l.setMemo( rs.getString("paperMemo") );
				long time = 0;
				try { time = rs.getTimestamp("paperDate").getTime(); } catch (Exception e) { }
				l.setDate( time );
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : read(Connection con, Letter l)\r\n", LetterController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}

	/**
	 * 등록된 편지들중 uid값이 젤 큰걸 찾아서 리턴.
	 */
	static private int selectMax(Connection con){
		int max = 0;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT MAX(uid) FROM characters_letter");
			rs = st.executeQuery();
			if(rs.next())
				max = rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : selectMax(Connection con)\r\n", LetterController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return max;
	}
	
	/**
	 * 편지 작성한 내용 디비에 기록처리하는 함수.
	 */
	static private void insert(Connection con, int new_uid, String type, String from, String to, String subject, String memo, String inventory, int aden){
		PreparedStatement st = null;
		try{
			st = con.prepareStatement("INSERT INTO characters_letter SET uid=?, type=?, paperFrom=?, paperTo=?, paperSubject=?, paperMemo=?, paperInventory=?, paperAden=?, paperDate=?");
			st.setInt(1, new_uid);
			st.setString(2, type);
			st.setString(3, from);
			st.setString(4, to);
			st.setString(5, subject);
			st.setString(6, memo);
			st.setString(7, inventory);
			st.setInt(8, aden);
			st.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));
			st.executeUpdate();
		} catch(Exception e) {
			lineage.share.System.printf("%s : insert(Connection con, int new_uid, String type, String from, String to, String subject, String memo, boolean inventory)\r\n", LetterController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * 중복되는 처리구문 제거를 위해 따로 뺌.
	 * 편지지를 생성해서 pc 인벤토리에 등록 처리 하는 함수.
	 */
	static private void toLetter(PcInstance pc, int new_uid, String from, String to, String subject, String memo, long date, int aden){
		// 편지지 생성.
		Letter l = (Letter)ItemDatabase.newInstance( ItemDatabase.find("편지지 - 안읽은 편지") );
		l.setObjectId(ServerDatabase.nextItemObjId());
		l.setLetterUid( new_uid );
		l.setFrom( from );
		l.setTo( to );
		l.setSubject( subject );
		l.setMemo( memo );
		l.setDate( date );
		// 편지지 등록.
		pc.getInventory().append(l, true);
		// 아덴 지급 처리.
		if(aden>0){
			ItemInstance ii = pc.getInventory().findAden();
			if(ii == null){
				ii = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
				ii.setObjectId(ServerDatabase.nextItemObjId());
				ii.setCount(0);
				pc.getInventory().append(ii, true);
			}
			//
			pc.getInventory().count(ii, ii.getCount()+aden, true);
		}
	}
	
	/**
	 * 혈맹 편지처리와 일반 편지처리 가 중복되서 이와같이 따로 함수로 뺌.
	 * @param con
	 * @param o
	 * @param type
	 * @param to
	 * @param subject
	 * @param memo
	 * @param aden
	 */
	static private void toLetter(Connection con, String from, String type, String to, String subject, String memo, int aden){
		// uid 생성
		int new_uid = selectMax(con) + 1;
		// to 사용자가 온라인 중일경우
		PcInstance user = World.findPc(to);
		// 사용자가 온라인일 경우 개인상점에서 보내는 편지는 제외
		if(user != null){
			// 인벤 등록 처리.
			toLetter(user, new_uid, from==null ? "메티스" : from, to, subject, memo, System.currentTimeMillis(), aden);
			// 비둘기 표현
			user.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), user, 1091), true);
			// 편지도착 알림 메세지
			user.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 428));
		}
		// 디비에 등록.
		insert(con, new_uid, type, from==null ? "메티스" : from, to, subject, memo, String.valueOf(user!=null), aden);
	}
}
