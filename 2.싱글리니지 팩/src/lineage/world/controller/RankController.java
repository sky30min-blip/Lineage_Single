package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.lineage.Board;
import lineage.bean.lineage.Rank;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BoardView;
import lineage.network.packet.server.S_ObjectName;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class RankController {
	static public object rankBronze;
	static public object pvpRankBronze;
	// 랭킹 리스트.
	static private List<Rank> list;
	static private List<Rank> temp_list;
	static private List<Rank> pool;
	// 랭킹 주기적으로 읽게 하기위해 사용될 변수.
	static private long time_last;
	// 랭킹을 출력할 순위 최대. 기본 100위까지만.
	static private final int print_limit = 100;
	// 랭킹 1위 레벨
	static public int rank_top_level;
	
	static public void init(Connection con){
		TimeLine.start("RankController..");
		
		list = new ArrayList<Rank>();
		temp_list = new ArrayList<Rank>();
		pool = new ArrayList<Rank>();
		time_last = System.currentTimeMillis() - Lineage.rank_update_delay;
		toRankRead(con, time_last);
		
		TimeLine.end();
	}
	
	static public Rank getPool(){
		Rank r = null;
		synchronized (pool) {
			if(pool.size()>0){
				r = pool.get(0);
				pool.remove(0);
			}else{
				r = new Rank();
			}
		}
		return r;
	}
	
	static public void setPool(Rank r){
		r.close();
		synchronized (pool) {
			if(!pool.contains(r))
				pool.add(r);
		}
	}
	
	/**
	 * Board에 대한 뒷처리 반드시 해야함. (pool 재사용 처리.)
	 * @return
	 */
	static public List<Board> getList(){
		List<Board> r_list = new ArrayList<Board>();
		synchronized (list) {
			for(Rank r : list)
				r_list.add( toView(r) );
		}
		return r_list;
	}

	static public void toTimer(long time) {
		if (time_last + Lineage.rank_update_delay < time) {
			time_last = time;
			// 메모리 반환.
			synchronized (list) {
				for (Rank r : list)
					setPool(r);
				list.clear();
			}
			
			// 정보 추출.
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();

				for (PcInstance pc : World.getPcList())
					CharactersDatabase.saveExp(con, pc);

				toRankRead(con, time);
				temp_list = new ArrayList<Rank>(list);

				
			} catch (Exception e) {
			} finally {
				DatabaseConnection.close(con);
			}

			Rank all = getView(0);
			if (all != null && all.getList().size() > 0 && rankBronze != null)
				updateBronze(all);
			
			Rank pvp = getView(6);
			if (pvp != null && pvp.getList().size() > 0 && pvpRankBronze != null)
				updatePvpRankBronze(pvp);
		}
	}
	
	static public void updateBronze(Rank all) {
		StringTokenizer st = new StringTokenizer(all.getList().get(0), "|");
		st.nextToken();
		String name = st.nextToken();
		int classType = Integer.valueOf(st.nextToken());
		st.nextToken();
		int sex = Integer.valueOf(st.nextToken());

		rankBronze.setName("[랭킹 1위] " + "\\f=" + name);

		switch (classType) {
		case 0:
			if (sex == 0)
				rankBronze.setGfx(5137);
			else
				rankBronze.setGfx(5135);
			break;
		case 1:
			if (sex == 0)
				rankBronze.setGfx(5143);
			else
				rankBronze.setGfx(5145);
			break;
		case 2:
			if (sex == 0)
				rankBronze.setGfx(5156);
			else
				rankBronze.setGfx(5158);
			break;
		case 3:
			if (sex == 0)
				rankBronze.setGfx(5139);
			else
				rankBronze.setGfx(5141);
			break;
		}

		rankBronze.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), rankBronze), true);
		rankBronze.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), rankBronze), true);
	}
	
	static public void updatePvpRankBronze(Rank pvp) {
		StringTokenizer st = new StringTokenizer(pvp.getList().get(0), "|");
		String name = st.nextToken();
		int classType = Integer.valueOf(st.nextToken());
		st.nextToken();
		int sex = Integer.valueOf(st.nextToken());

		pvpRankBronze.setName("[PvP 랭킹 1위] " + "\\f=" + name);

		switch (classType) {
		case 0:
			if (sex == 0)
				pvpRankBronze.setGfx(5137);
			else
				pvpRankBronze.setGfx(5135);
			break;
		case 1:
			if (sex == 0)
				pvpRankBronze.setGfx(5143);
			else
				pvpRankBronze.setGfx(5145);
			break;
		case 2:
			if (sex == 0)
				pvpRankBronze.setGfx(5156);
			else
				pvpRankBronze.setGfx(5158);
			break;
		case 3:
			if (sex == 0)
				pvpRankBronze.setGfx(5139);
			else
				pvpRankBronze.setGfx(5141);
			break;
		}

		pvpRankBronze.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), pvpRankBronze), true);
		pvpRankBronze.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), pvpRankBronze), true);
	}
	
	/**
	 * 게시판 읽기 처리 함수.
	 * @param pc
	 * @param bi
	 * @param uid
	 */
	static public void toView(PcInstance pc, int uid){
		Rank r = getView(uid);
		if(r != null) {
			//
			Board b = toView(r);
			pc.toSender(S_BoardView.clone(BasePacketPooling.getPool(S_BoardView.class), b));
			//
			BoardController.setPool( b );
		}
	}
	
	/**
	 * 랭킹 정보 추출 후 리턴.
	 * @param uid
	 */
	static public Rank getView(int uid) {
		synchronized (temp_list) {
			return new ArrayList<Rank>(temp_list).get(uid);
		}
	}
	
	/**
	 * 사용자에 전체 랭킹을 추출함.
	 * @param o
	 * @return
	 */
	public static int getRankAll(object o) {
		return getRank( o.getName() );
	}
	
	public static int getRankClass(object o) {
		int rank = 0;
		// 전체 랭킹 추출.
		Rank all = getView(0);
		// 전체 랭킹목록에서 해당 케릭에 전체랭킹 순위 추출.
		for(int i=0 ; i<all.getList().size() ; ++i) {
			StringTokenizer st = new StringTokenizer(all.getList().get(i), "|");
			int level = Integer.valueOf(st.nextToken());
			String name = st.nextToken();
			int classType = Integer.valueOf(st.nextToken());
			if(classType != o.getClassType())
				continue;
			
			rank += 1;
			if(name.equalsIgnoreCase(o.getName()))
				break;
		}
		return rank;
	}
	
	static private Board toView(Rank r) {
		//
		Board b = BoardController.getPool();
		StringBuffer sb = new StringBuffer();
		//
		b.setUid( r.getNum() );
		b.setName( "" );
		b.setSubject( r.getType() );
		b.setDays( r.getTime() );
		if (!r.getType().equals("PvP 랭킹")) {
			for(int i = 0 ; i < (r.getList().size() < 20 ? r.getList().size() : 20) ; ++i) {
				try {
					// 1~20위 까지만
					StringTokenizer st = new StringTokenizer(r.getList().get(i), "|");
					String level = st.nextToken();
					String name = st.nextToken();
					String classType = st.nextToken();
					classType = classType.equals("0") ? "군주" : classType.equals("1") ? "기사" : classType.equals("2") ? "요정" : classType.equals("3") ? "마법사" :"다크엘프";
					if (r.getType().equals("전체 랭킹"))
						sb.append( String.format("%d위 %s [%s]\r\n", i + 1, name, classType) );
					else
						sb.append( String.format("%d위 %s\r\n", i + 1, name) );
				} catch (Exception e) { }
			}
		} else {
			for(int i = 0 ; i < (r.getList().size() < 20 ? r.getList().size() : 20) ; ++i) {
				try {
					// 1~20위 까지만
					StringTokenizer st = new StringTokenizer(r.getList().get(i), "|");
					String name = st.nextToken();
					String classType = st.nextToken();
					String pkCount = st.nextToken();
					classType = classType.equals("0") ? "군주" : classType.equals("1") ? "기사" : classType.equals("2") ? "요정" : classType.equals("3") ? "마법사" :"다크엘프";
					sb.append( String.format("%d위 %s %s킬 [%s]\r\n", i + 1, name, pkCount, classType) );
				} catch (Exception e) { }
			}
		}
		
		b.setMemo( sb.toString() );
		//
		return b;
	}
	
	static private void toRankRead(Connection con, int idx, String type, long time, String where) {
		PreparedStatement st = null;
		ResultSet rs = null;
		int temp_idx = 0;
		try {
			Rank r = getPool();
			r.setNum(idx);
			r.setType(type);
			r.setTime(time);
			st = con.prepareStatement("SELECT * FROM characters " + where);
			rs = st.executeQuery();
			
			while(rs.next()) {
				if(idx == 0) {
					r.getList().add( String.format("%d|%s|%d|%d|%d|%d|", rs.getInt("level"), rs.getString("name"), rs.getInt("class"), ++temp_idx, rs.getInt("sex"), rs.getLong("objID")) );
					
					if (rank_top_level < rs.getInt("level"))
						rank_top_level = rs.getInt("level");
				} else if (idx == 6) {
					if (rs.getInt("pkcount") > 0) {
						r.getList().add( String.format("%s|%d|%d|%d", rs.getString("name"), rs.getInt("class"), rs.getInt("pkcount"), rs.getInt("sex")) );
					}
				} else {
					r.getList().add( String.format("%d|%s|%d|%d|%d|", rs.getInt("level"), rs.getString("name"), rs.getInt("class"), getRank(rs.getString("name")), rs.getLong("objID")) );
				}
			}

			synchronized (list) {
				list.add(r);
			}
			
		} catch (Exception e) {
			lineage.share.System.println(RankController.class+" : void toRankRead(Connection con, int idx, String type, long time, String where)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	
	/**
	 * 랭킹정보 추출.
	 * @param con
	 */
	static private void toRankRead(Connection con, long time) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			for (PcInstance pc : World.getPcList()) {
				st = con.prepareStatement("UPDATE characters SET exp=? WHERE objID=?");
				st.setDouble(1, pc.getExp());
				st.setLong(2, pc.getObjectId());
				st.executeUpdate();
			}		
			
			// 전체 랭킹.
			toRankRead(con, 0, "전체 랭킹", time, Lineage.rank_filter_names_query==null ? "ORDER BY exp DESC LIMIT "+print_limit : String.format("WHERE %s ORDER BY exp DESC LIMIT "+print_limit, Lineage.rank_filter_names_query));
			// 군주 랭킹.
			toRankRead(con, 1, "군주 랭킹", time, Lineage.rank_filter_names_query==null ? "WHERE class=0 ORDER BY exp DESC LIMIT "+print_limit : String.format("WHERE class=0 AND %s ORDER BY exp DESC LIMIT "+print_limit, Lineage.rank_filter_names_query));
			// 기사 랭킹.
			toRankRead(con, 2, "기사 랭킹", time, Lineage.rank_filter_names_query==null ? "WHERE class=1 ORDER BY exp DESC LIMIT "+print_limit : String.format("WHERE class=1 AND %s ORDER BY exp DESC LIMIT "+print_limit, Lineage.rank_filter_names_query));
			// 요정 랭킹.
			toRankRead(con, 3, "요정 랭킹", time, Lineage.rank_filter_names_query==null ? "WHERE class=2 ORDER BY exp DESC LIMIT "+print_limit : String.format("WHERE class=2 AND %s ORDER BY exp DESC LIMIT "+print_limit, Lineage.rank_filter_names_query));
			// 마법사 랭킹.
			toRankRead(con, 4, "마법사 랭킹", time, Lineage.rank_filter_names_query==null ? "WHERE class=3 ORDER BY exp DESC LIMIT "+print_limit : String.format("WHERE class=3 AND %s ORDER BY exp DESC LIMIT "+print_limit, Lineage.rank_filter_names_query));
			// 다크엘프 랭킹.
			toRankRead(con, 5, "다크엘프 랭킹", time, Lineage.rank_filter_names_query==null ? "WHERE class=4 ORDER BY exp DESC LIMIT "+print_limit : String.format("WHERE class=4 AND %s ORDER BY exp DESC LIMIT "+print_limit, Lineage.rank_filter_names_query));
			// PvP 랭킹.
			toRankRead(con, 6, "PvP 랭킹", time, Lineage.rank_filter_names_query==null ? "WHERE pkcount > 0 ORDER BY pkcount DESC LIMIT "+print_limit : String.format("WHERE %s ORDER BY pkcount DESC LIMIT "+print_limit, Lineage.rank_filter_names_query));
			
		} catch (Exception e) {
			lineage.share.System.println(RankController.class + " : toRankRead(Connection con, long time)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	
	/**
	 * 케릭터 이름으로 전체순위값 추출.
	 * @param name
	 * @return
	 */
	private static int getRank(String name) {
		return getRank(name, 0);
	}
	private static int getPvpRank(String name) {
		return getRank(name, 5);
	}
	
	public static int getPvPRankAll(object o) {
		return getPvpRank(o.getName());
	}


	public static int getRank(String name, int idx) {
	    try {
	        Rank r = getView(idx);
	
	        String format = (idx == 6) ? "%s|" : "|%s|";
	        String search = String.format(format, name);
	        int rank = 0;
	        for (int i = 0; i < r.getList().size(); ++i) {
	            String item = r.getList().get(i);
	            if (item.contains(search)) {
	                rank = i + 1;
	                break;
	            }
	        }
	        return rank;
	    } catch (Exception e) {
	 
	        return 0;
	    }
	}
	
	/**
	 * 오브젝트 아이디로 클래스랭킹 순위 리턴
	 * @param
	 * @return
	 * 2017-09-04
	 * by all_night.
	 */
	public static int getClassRank(long objId, int idx) {
		try {
			int rank = 0;
			// 전체 랭킹 추출.
			Rank r = getView(idx + 1);
			// 클래스 랭킹에서 해당 캐릭터 순위 추출
			for (int i = 0; i < r.getList().size(); ++i) {
				if (r.getList().get(i).length() > 0 && r.getList().get(i).indexOf(String.format("|%d|", objId)) > 0) {
					rank = i + 1;
					break;
				}
			}

			return rank;
		} catch (Exception e) {
			lineage.share.System.println(RankController.class + " : getClassRank(long objId, int idx)");
			lineage.share.System.println(e);
		}
		return 0;
	}
	
	/**
	 * 오브젝트 아이디로 전체랭킹 순위 리턴
	 * @param
	 * @return
	 * 2017-09-04
	 * by all_night.
	 */
	public static int getAllRank(long objId) {
		try {
			int rank = 0;
			// 전체 랭킹 추출.
			Rank r = getView(0);

			// 클래스 랭킹에서 해당 캐릭터 순위 추출
			for (int i = 0; i < r.getList().size(); ++i) {
				if (r.getList().get(i).length() > 0 && r.getList().get(i).indexOf(String.format("|%d|", objId)) > 0) {
					rank = i + 1;
					break;
				}
			}

			return rank;	
		} catch (Exception e) {
			lineage.share.System.println(RankController.class + " : getAllRank(long objId)");
			lineage.share.System.println(e);
		}
		return 0;
	}
}
