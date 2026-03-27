package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lineage.bean.lineage.BeginnerTel;
import lineage.bean.lineage.Book;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Book;
import lineage.network.packet.server.S_Message;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.instance.PcInstance;

public final class BookController {

	// 사용자 별로 관리될 기억리스트 목록
	static private Map<PcInstance, List<Book>> list;
	// Book 클레스 재사용을 위한것
	static private List<Book> pool;

	// 기억 안되는 좌표 목록
	static private int NotLocation[][] = {
			// 화둥 사냥터
			{33544, 33800, 32188, 32412, 4},
			// 수련던전 던전 길
			{32544, 32629, 33475, 33515, 4},
			// 아덴 오만의탑 근처 사냥터
			{34209, 34304, 33100, 33535, 4},
	};
	
	static public void init(){
		TimeLine.start("BookController..");
		
		list = new HashMap<PcInstance, List<Book>>();
		pool = new ArrayList<Book>();
		
		TimeLine.end();
	}
	//기억의구슬

	public synchronized static void Bookmarkitem(PcInstance pc) {
	    Connection con = null;
	    PreparedStatement pstm = null;
	    ResultSet rs = null;
	    BeginnerTel bookmark = null;
	    List<Book> list = find(pc);
	    
	    if (list.size() != 0) {
			ChattingController.toChatting(pc, String.format("기억 제거 구슬을 이용하신후 다시 사용해주세요."), Lineage.CHATTING_MODE_MESSAGE);
			return;
	    }
	    try {
	        con = DatabaseConnection.getLineage();
	        pstm = con.prepareStatement("SELECT * FROM beginner_addbook");
	        rs = pstm.executeQuery();
	  
	        while (rs.next()) {
	            bookmark = new BeginnerTel();
	            bookmark.setLocation(rs.getString("location"));
	            bookmark.setLocX(rs.getInt("locX"));
	            bookmark.setLocY(rs.getInt("locY"));
	            bookmark.setLocMap(rs.getInt("locMAP"));
	            bookmark.setRandom(rs.getInt("random"));

	            if(isAppend(pc)){
					if(!isContains(list, bookmark.getLocation())){
						Book b = getPool();
						b.setLocation(bookmark.getLocation());
						b.setX(bookmark.getLocX());
						b.setY(bookmark.getLocY());
						b.setMap(bookmark.getLocMap());
						b.setRandom(bookmark.getRandom());
						
						
						
						list.add(b);

						CharactersDatabase.toBookAddItem(pc, b);
						pc.toSender(S_Book.clone(BasePacketPooling.getPool(S_Book.class), b));
					
						//ChattingController.toChatting(pc, ""+bookmark.getLocation()+" 기억이 등록 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
						ChattingController.toChatting(pc, String.format("%s 기억이 등록 되었습니다.", bookmark.getLocation()), Lineage.CHATTING_MODE_MESSAGE);
					}else {
						//ChattingController.toChatting(pc, ""+bookmark.getLocation()+" 이미 같은 이름이 존재합니다", Lineage.CHATTING_MODE_MESSAGE);
						ChattingController.toChatting(pc, String.format("%s 이미 같은 이름이 존재합니다.", bookmark.getLocation()), Lineage.CHATTING_MODE_MESSAGE);
					}
				}else{
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 214));
				}
	        }
	    } catch (Exception e) {
	    	 e.printStackTrace();  // Print the stack trace for more information
	        System.out.println("기억의 구슬 예외 발생");
	    } finally {
	        bookmark = null;
	        DatabaseConnection.close(con, pstm, rs);
	    }
	}
	public synchronized static void Bookmarkitemremove(PcInstance pc) {
		
		
	
	    List<Book> list = find(pc);
	    
	      
	    if (list.size() != 0) {
	        List<Book> bookmarksToRemove = new ArrayList<>(list); // Create a copy of the list

	        for (Book existingBookmark : bookmarksToRemove) {
	            list.remove(existingBookmark);  // Modify the original list
	            remove(pc, existingBookmark.getLocation());
	        }
	    }


	}
	/**
	 * 월드 접속시 호출.
	 * @param pc
	 */
	static public void toWorldJoin(PcInstance pc){
		// 생성된게 없을경우 생성해서 관리목록에 추가.
		synchronized (list) {
			if(!list.containsKey(pc))
				list.put(pc, new ArrayList<Book>());
		}
	}
	
	/**
	 * 월드에서 나가면 호출.
	 * @param pc
	 */
	static public void toWorldOut(PcInstance pc){
		// 관리되고있는 자료구조 찾기.
		List<Book> book_list = find(pc);
		if(book_list != null){
			// 순회하면서 풀에 다시 넣기.
			for(Book b : book_list)
				setPool(b);
			// 메모리에서 제거.
			book_list.clear();
			book_list = null;
		}
		synchronized (list) {
			list.remove(pc);
		}
	}
	
	/**
	 * CharactersDatabase.readBook 함수에서 호출해서 사용.
	 *  : 디비에 있는 정보를 토대로 pool에서 꺼낸 Book객체를 
	 *    해당 사용자 관리목록에 넣는 역활을 함.
	 *    패킷 처리도 함께 함.
	 * @param pc
	 * @param b
	 */
	static public void append(PcInstance pc, Book b){
		find(pc).add(b);
		pc.toSender(S_Book.clone(BasePacketPooling.getPool(S_Book.class), b));
	}

	/**
	 * 기억 명령어 요청 처리 함수
	 * @param pc
	 * @param location
	 */
	static public void append(PcInstance pc, String location){
		List<Book> list = find(pc);
		if(isAppend(pc)){
			if(!isContains(list, location)){
				if(location.length()>0 && location.length()<Common.STRSIZE){
					Book b = getPool();
					b.setLocation(location);
					b.setX(pc.getX());
					b.setY(pc.getY());
					b.setMap(pc.getMap());
					
					list.add(b);
					pc.toSender(S_Book.clone(BasePacketPooling.getPool(S_Book.class), b));
				}else{
					// \f1장소에 대한 이름이 너무 깁니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 204));
				}
			}else{
				// 같은 이름이 이미 존재합니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 327));
			}
		}else{
			// \f1이곳을 기억할 수 없습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 214));
		}
	}
	
	/**
	 * 기억 삭제 요청 처리 함수.
	 * @param pc
	 * @param location
	 */
	static public void remove(PcInstance pc, String location){
		List<Book> list = find(pc);
		Book b = find(list, location);
		// 찾았다면 풀에 다시 넣기.
		if(b!=null){
			list.remove(b);
			setPool(b);
		}
	}
	
	/**
	 * 풀에 있는 Book객체 하나 꺼내서 리턴.
	 * 더이상 없을경우 새로 할당해서 리턴.
	 * @return
	 */
	static public Book getPool(){
		Book b = null;
		synchronized (pool) {
			if(pool.size()>0){
				b = pool.get(0);
				pool.remove(0);
			}else{
				b = new Book();
			}
		}
		return b;
	}
	
	/**
	 * 풀에 사용한 Book클레스 넣는 함수.
	 * @param b
	 */
	static public void setPool(Book b){
		synchronized (pool) {
			if(!pool.contains(b))
				pool.add(b);
		}
	}
	
	/**
	 * 사용자와 연결된 객체 찾아서 리턴.
	 * @param pc
	 * @return
	 */
	static public List<Book> find(PcInstance pc){
		synchronized (list) {
			return list.get(pc);
		}
	}
	
	/**
	 * 사용자와 연결된 객체 찾아서 리턴.
	 * @param pc
	 * @return
	 */
	static public Book find(PcInstance pc, String location){
		return find(find(pc), location);
	}
	
	/**
	 * 해당 객체와 연결된 기억목록에서 좌표와 일치하는것을 찾아서 리턴.
	 * @param pc
	 * @param x
	 * @param y
	 * @param map
	 * @return
	 */
	static public Book find(PcInstance pc, int x, int y, int map){
		for(Book b : find(pc)){
			if(b.getX()==x && b.getY()==y && b.getMap()==map)
				return b;
		}
		return null;
	}
	
	/**
	 * 이름과 일치하는 Book 객체 찾아서 리턴.
	 * @param list
	 * @param location
	 * @return
	 */
	static private Book find(List<Book> list, String location){
		for(Book b : list){
			if(b.getLocation().equalsIgnoreCase(location))
				return b;
		}
		return null;
	}
	
	/**
	 * 기억 명령어 요청처리전에 호출해서 사용.
	 * 해당 맵이 기억가능한지 확인.
	 * 나아가 좌표까지 체크하여 기억 가능한지 확인.
	 * @param pc
	 * @return
	 */
	static private boolean isAppend(PcInstance pc) {
		// 영자는 무시.
		if (pc.getGm() > 0)
			return true;

		// 특정 맵 기억 안되게.
		if (pc.getMap() != 0 && pc.getMap() != 3 && pc.getMap() != 4 && pc.getMap() != 200)
			return false;
		// 아지트좌표 확인.
		if (AgitController.isAgitLocation(pc))
			return false;
		// 내성좌표 확인.
		if (KingdomController.isKingdomLocation(pc))
			return false;
		// 기억안되는 좌표내에 있는지 확인.
		for (int[] i : NotLocation) {
			if (i[0] <= pc.getX() && i[1] >= pc.getX() && i[2] <= pc.getY() && i[3] >= pc.getY() && i[4] == pc.getMap())
				return false;
		}
		// 이도저도 아니면 성공.
		return true;
	}
	
	/**
	 * 기억목록에서 동일한 것이 있는지 확인해주는 함수.
	 * @param list
	 * @param location
	 * @return
	 */
	static private boolean isContains(List<Book> list, String location){
		for(Book b : list){
			if(b.getLocation().equalsIgnoreCase(location))
				return true;
		}
		return false;
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
}
