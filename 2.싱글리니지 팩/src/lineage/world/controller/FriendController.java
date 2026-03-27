package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Friend;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_FriendList;
import lineage.share.TimeLine;
import lineage.world.object.instance.PcInstance;

public class FriendController {

	// 관리 목록.
	static private List<Friend> list;
	// 재사용 목록.
	static private List<Friend> pool;

	static public void init(){
		TimeLine.start("FriendController..");

		list = new ArrayList<Friend>();
		pool = new ArrayList<Friend>();

		TimeLine.end();
	}
	
	static public int getSize(){
		return list.size();
	}

	static public int getPoolSize(){
		synchronized (pool) {
			return pool.size();
		}
	}

	/**
	 * 사용자가 월드 진입할때마다 호출됨.
	 * @param pc
	 */
	static public void toWorldJoin(PcInstance pc){
		// 검색
		Friend f = find(pc);
		// 아직 없으면 메모리 생성 및 목록에 등록.
		if(f == null){
			f = getPool();
			f.setName(pc.getName());
			f.setObjectId(pc.getObjectId());
			synchronized (list) {
				if(!list.contains(f))
					list.add(f);
			}
		}

		// 디비에서 정보 추출.
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_friend WHERE object_id=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			while(rs.next())
				f.appendList( rs.getString("friend") );
		} catch (Exception e) {
			lineage.share.System.printf("%s : toWorldJoin(PcInstance pc)\r\n", FriendController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 사용자가 월드를 나갈때마다 호출됨.
	 * @param pc
	 */
	static public void toWorldOut(PcInstance pc){
		// 검색
		Friend f = find(pc.getObjectId());
		if(f != null){
			// 메모리 제거.
			synchronized (list) {
				list.remove(f);
			}
			// 재사용을 위해 등록.
			setPool(f);
		}
	}

	/**
	 * 저장 에서 호출됨.
	 *  : PcInstance.toSave
	 * @param con
	 * @param pc
	 */
	static public void toSave(Connection con, PcInstance pc){
		// 검색
		Friend f = find(pc.getObjectId());
		if(f != null){
			PreparedStatement st = null;
			ResultSet rs = null;
			try {
				// 디비 이전기록 제거.
				st = con.prepareStatement("DELETE FROM characters_friend WHERE object_id=?");
				st.setLong(1, pc.getObjectId());
				st.executeUpdate();
				st.close();
				// 디비 등록.
				for(String friend : f.getList()){
					st = con.prepareStatement( "INSERT INTO characters_friend SET name=?, object_id=?, friend=?" );
					st.setString(1, pc.getName());
					st.setLong(2, pc.getObjectId());
					st.setString(3, friend);
					st.executeUpdate();
					st.close();
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : toSave(Connection con, PcInstance pc)\r\n", FriendController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(st, rs);
			}
		}
	}

	/**
	 * 친구 추가 처리 함수.
	 * @param pc
	 * @param name
	 */
	static public void append(PcInstance pc, String name){
		// 메모리 검색
		Friend f = find(pc);
		// 메모리 세팅안됫을경우 무시. 버그가능성이 높음.
		if(f == null)
			return;
		// 친구목록 검색 후 등록.
		if(!f.containsList(name.toLowerCase()))
			f.appendList(name.toLowerCase());
		// 친구목록 창 표현.
		toList(pc);
	}

	/**
	 * 친구 삭제 처리 함수.
	 * @param pc
	 * @param name
	 */
	static public void remove(PcInstance pc, String name){
		// 메모리 검색
		Friend f = find(pc);
		// 메모리 세팅안됫을경우 무시.
		if(f == null)
			return;
		// 삭제
		f.removeList(name.toLowerCase());
		// 친구목록 창 표현.
		toList(pc);
	}

	/**
	 * 친구 목록 보기 요청 처리 함수.
	 * @param pc
	 */
	static public void toList(PcInstance pc){
		pc.toSender(S_FriendList.clone(BasePacketPooling.getPool(S_FriendList.class), find(pc)));
	}

	static private Friend find(PcInstance pc){
		synchronized (list) {
			for(Friend f : list){
				if(f.getObjectId() == pc.getObjectId())
					return f;
			}
			return null;
		}
	}

	static private Friend find(long object_id){
		synchronized (list) {
			for(Friend f : list){
				if(f.getObjectId() == object_id)
					return f;
			}
			return null;
		}
	}

	static private Friend getPool(){
		Friend f = null;
		synchronized (pool) {
			if(pool.size() > 0){
				f = pool.get(0);
				pool.remove(0);
			}else{
				f = new Friend();
			}
		}
		return f;
	}

	static private void setPool(Friend f){
		f.close();
		synchronized (pool) {
			if(!pool.contains(f))
				pool.add(f);
		}
	}

}
