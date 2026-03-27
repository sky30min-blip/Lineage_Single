package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemTeleport;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectLock;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.LocationController;
import lineage.world.controller.WantedController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class ItemTeleportDatabase {

	static private List<ItemTeleport> list;

	/**
	 * 초기화 처리 함수.
	 */
	static public void init(Connection con) {
		TimeLine.start("ItemTeleport..");

		list = new ArrayList<ItemTeleport>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item_teleport");
			rs = st.executeQuery();
			while (rs.next()) {
				ItemTeleport i = new ItemTeleport();
				i.setUid(rs.getInt("uid"));
				i.setName(rs.getString("name"));
				i.setX(rs.getInt("goto_x"));
				i.setY(rs.getInt("goto_y"));
				i.setMap(rs.getInt("goto_map"));
				i.setRange(rs.getInt("range"));
				i.setHeading(rs.getInt("goto_heading"));
				i.setLevel(rs.getInt("if_level"));
				i.setClassType(rs.getInt("if_class"));
				i.setRemove(rs.getInt("if_remove") == 1);

				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemTeleportDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	static public void reload() {
		TimeLine.start("ItemTeleport 테이블 리로드 - ");

		list.clear();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM item_teleport");
			rs = st.executeQuery();
			while (rs.next()) {
				ItemTeleport i = new ItemTeleport();
				i.setUid(rs.getInt("uid"));
				i.setName(rs.getString("name"));
				i.setX(rs.getInt("goto_x"));
				i.setY(rs.getInt("goto_y"));
				i.setMap(rs.getInt("goto_map"));
				i.setRange(rs.getInt("range"));
				i.setHeading(rs.getInt("goto_heading"));
				i.setLevel(rs.getInt("if_level"));
				i.setClassType(rs.getInt("if_class"));
				i.setRemove(rs.getInt("if_remove") == 1);

				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload()\r\n", ItemTeleportDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		TimeLine.end();
	}

	static public ItemTeleport find(int uid) {
		for (ItemTeleport it : getList()) {
			if (it.getUid() == uid)
				return it;
		}
		return null;
	}

	static public ItemTeleport find2(int map) {
		for (ItemTeleport it : getList()) {
			if (it.getMap() == map)
				return it;
		}
		return null;
	}
	
	static public List<ItemTeleport> getList() {
		return new ArrayList<ItemTeleport>(list);
	}

	static public boolean toTeleport(ItemTeleport it, object o, boolean message) {
		boolean flag = false;
		PcInstance pc = (PcInstance)o;
		
		if (pc.isAutoHunt) {
			pc.endAutoHunt(false, false);
		}
		
		if (it == null || o == null) {
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return false;
		}

		if (PluginController.init(ItemTeleportDatabase.class, "toTeleport", it, o, message) != null)
			return false;

		// 클레스 확인.
		if ((it.getClassType() & Lineage.getClassType(o.getClassType())) == 0) {
			if (message)
				ChattingController.toChatting(o, "해당 클레스로는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return false;
		}


	

		// 레벨 확인.
		if (it.getLevel() != 0 && it.getLevel() > o.getLevel()) {
			if (message)
				ChattingController.toChatting(o, "해당 레벨로는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return false;
		}
		if ((it.getUid() == 1 || it.getUid() == 73) && o.getMap() != Lineage.teamBattleMap && o.getMap() != Lineage.BattleRoyalMap && o.getMap() != 70) {
			if (it.getUid() == 1) {
				int[] loc = Lineage.getHomeXY();
				o.toPotal(loc[0], loc[1], loc[2]);
			} else if (it.getUid() == 73) {
				o.toPotal(Util.random(32700, 32704), Util.random(32834, 32839), Lineage.market_map);
			}	
		} else {
			// 이동가능한 부분만.
			if (it.getMap() == 101 || it.getMap() == 102 || it.getMap() == 103 || it.getMap() == 104 || it.getMap() == 105 || it.getMap() == 106 || 
				it.getMap() == 107 || it.getMap() == 108 || it.getMap() == 109 || it.getMap() == 110 || it.getMap() == 200) {		
				boolean oman = o.isOman();
				
				if(LocationController.isTeleportZone(o, true, !oman) || oman){
					flag = true;
					
					if (it.getRange() > 0) {
						if (it.getRange() >= 100) {
							o.setMap(it.getMap());
							Util.toRndLocation(o);
							o.toPotal(o.getHomeX(), o.getHomeY(), o.getHomeMap());
							return flag;
						}
						
						int roop_cnt = 0;
						int x = it.getX();
						int y = it.getY();
						int map = it.getMap();
						int lx = x;
						int ly = y;
						int loc = it.getRange();
						
						// 랜덤 좌표 스폰
						do {
							lx = Util.random(x - loc, x + loc);
							ly = Util.random(y - loc, y + loc);
							if (roop_cnt++ > 100) {
								lx = x;
								ly = y;
								break;
							}
						}while(
								!World.isThroughObject(lx, ly+1, map, 0) || 
								!World.isThroughObject(lx, ly-1, map, 4) || 
								!World.isThroughObject(lx-1, ly, map, 2) || 
								!World.isThroughObject(lx+1, ly, map, 6) ||
								!World.isThroughObject(lx-1, ly+1, map, 1) ||
								!World.isThroughObject(lx+1, ly-1, map, 5) || 
								!World.isThroughObject(lx+1, ly+1, map, 7) || 
								!World.isThroughObject(lx-1, ly-1, map, 3) ||
								World.isNotMovingTile(lx, ly, map)
							);
						
						o.toPotal(lx, ly, it.getMap());
					} else {
						o.toPotal(it.getX(), it.getY(), it.getMap());
					}
				}				
			} else {
				if (LocationController.isTeleportZone(o, true, true)) {
					flag = true;
					
					if (it.getRange() > 0) {
						if (it.getRange() >= 100) {
							o.setMap(it.getMap());
							Util.toRndLocation(o);
							o.toPotal(o.getHomeX(), o.getHomeY(), o.getHomeMap());
							return flag;
						}
						
						int roop_cnt = 0;
						int x = it.getX();
						int y = it.getY();
						int map = it.getMap();
						int lx = x;
						int ly = y;
						int loc = it.getRange();
						
						// 랜덤 좌표 스폰
						do {
							lx = Util.random(x - loc, x + loc);
							ly = Util.random(y - loc, y + loc);
							if (roop_cnt++ > 100) {
								lx = x;
								ly = y;
								break;
							}
						}while(
								!World.isThroughObject(lx, ly+1, map, 0) || 
								!World.isThroughObject(lx, ly-1, map, 4) || 
								!World.isThroughObject(lx-1, ly, map, 2) || 
								!World.isThroughObject(lx+1, ly, map, 6) ||
								!World.isThroughObject(lx-1, ly+1, map, 1) ||
								!World.isThroughObject(lx+1, ly-1, map, 5) || 
								!World.isThroughObject(lx+1, ly+1, map, 7) || 
								!World.isThroughObject(lx-1, ly-1, map, 3) ||
								World.isNotMovingTile(lx, ly, map)
							);
						
						o.toPotal(lx, ly, it.getMap());
					} else {
						o.toPotal(it.getX(), it.getY(), it.getMap());
					}		
				}
			}
		}
		return flag;
	}
	
	static public boolean toTeleport(ItemTeleport it, object o) {
		boolean flag = false;

		if (it == null || o == null) {
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return false;
		}

	

		// 레벨 확인.
		if (it.getLevel() != 0 && it.getLevel() > o.getLevel()) {
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return false;
		}

		// 이동가능한 부분만.
		if (LocationController.isTeleportZone(o, true, true))
			flag = true;

		return flag;
	}
}
