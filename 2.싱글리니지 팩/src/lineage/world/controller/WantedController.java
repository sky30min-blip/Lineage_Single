package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.bean.database.Wanted;
import lineage.bean.lineage.Kingdom;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

final public class WantedController {

	static private List<Wanted> list;

	/**
	 * 초기화 처리 함수.
	 */
	static public void init(Connection con) {
		TimeLine.start("WantedController..");

		list = new ArrayList<Wanted>();

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = con.prepareStatement("SELECT * FROM wanted");
			rs = st.executeQuery();

			while (rs.next()) {
				Wanted wanted = new Wanted();
				wanted.objId = rs.getLong("objId");
				wanted.target_name = rs.getString("name");
				wanted.target_price = rs.getLong("price");
				wanted.date = rs.getTimestamp("date").getTime();
				list.add(wanted);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", WantedController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public void clear() {
		synchronized (list) {
			list.clear();
		}
		World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "현상수배 목록이 초기화 되었습니다."));
	}

	static public List<Wanted> getList() {
		return new ArrayList<Wanted>(list);
	}

	/**
	 * 주기적으로 호출됨.
	 * 
	 * @param time
	 */
	static public void toTimer(long time) {
		//
	}

	/**
	 * 현상수배 요청 처리 함수.
	 * 
	 * @param o
	 * @param target_name
	 * @param target_price
	 */
	static public void append(object o, String target_name, long target_price) {
		if (target_name == null || target_name.length() == 0) {
			ChattingController.toChatting(o, "캐릭터명이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (target_price < 0) {
			ChattingController.toChatting(o, "현상금이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}


		int[] levelRanges = {48, 52, 55, 60, 70};
		int[] wantedPrices = {Lineage.wanted_price_max, Lineage.wanted_price_min52, Lineage.wanted_price_min55, Lineage.wanted_price_min60, Lineage.wanted_price_min65};
		String[] messages = {"현상금 최대금액은 %d아데나 입니다.", "현상금 최소금액은 %d아데나 입니다.", "현상금 최소금액은 %d아데나 입니다.", "현상금 최소금액은 %d아데나 입니다.", "현상금 최소금액은 %d아데나 입니다."};

		for (int i = 0; i < levelRanges.length; i++) {
		    if (wantedPrices[i] != 0 && ((o.getLevel() >= levelRanges[i] && (i == levelRanges.length - 1 || o.getLevel() < levelRanges[i + 1])) || (o.getLevel() >= levelRanges[i] && i == levelRanges.length - 1)) && wantedPrices[i] > target_price) {
		        ChattingController.toChatting(o, String.format(messages[i], wantedPrices[i]), Lineage.CHATTING_MODE_MESSAGE);
		        return;
		    }
		}
		
		// 케릭터 존재하는지 확인.
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		long objId = 0;
		boolean isFind = false;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters WHERE LOWER(name)=?");
			st.setString(1, target_name.toLowerCase());
			rs = st.executeQuery();

			if (rs.next()) {
				isFind = true;
				objId = rs.getLong("objID");
			}

			rs.close();
			st.close();

			st = con.prepareStatement("SELECT * FROM _robot WHERE LOWER(name)=?");
			st.setString(1, target_name.toLowerCase());
			rs = st.executeQuery();

			if (rs.next()) {
				isFind = true;
				objId = rs.getLong("objId");
			}
		} catch (Exception e) {
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		if (!isFind) {
			ChattingController.toChatting(o, "캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		// 이미 현상수배가 내려졌는지 확인.
		if (checkWantedPc(objId)) {
			ChattingController.toChatting(o, String.format("%s님은 이미 수배중 입니다.", target_name), Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		Map<Integer, Integer> wantedPrices1 = new HashMap<>();
		wantedPrices1.put(48, Lineage.wanted_price_min);
		wantedPrices1.put(52, Lineage.wanted_price_min52);
		wantedPrices1.put(55, Lineage.wanted_price_min55);
		wantedPrices1.put(60, Lineage.wanted_price_min60);
		wantedPrices1.put(70, Lineage.wanted_price_min65);

		if (!o.getInventory().isAden(target_price, true)) {
		    for (Map.Entry<Integer, Integer> entry : wantedPrices1.entrySet()) {
		        int minLevel = entry.getKey();
		        int wantedPrice = entry.getValue();

		        if (o.getLevel() <= minLevel) {
		            ChattingController.toChatting(o, String.format("수배: %d아데나", wantedPrice), Lineage.CHATTING_MODE_MESSAGE);
		            return;  // Early return when Adena is insufficient
		        }
		    }
		}
		Wanted wanted = new Wanted();
		wanted.objId = objId;
		wanted.target_name = target_name;
		wanted.target_price = target_price;
		wanted.date = System.currentTimeMillis();

		synchronized (list) {
			list.add(wanted);
		}

		con = null;
		st = null;
		rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO wanted SET objId=?, name=?, price=?, date=?");
			st.setLong(1, wanted.objId);
			st.setString(2, wanted.target_name);
			st.setLong(3, wanted.target_price);
			st.setTimestamp(4, new Timestamp(wanted.date));
			st.executeUpdate();
			
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("%s님을 수배 합니다.", target_name)));
		
			PcInstance pc = World.findPc(objId);
			pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 16221), pc instanceof PcInstance);
			if (pc != null) {
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 2);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 2);
				pc.setDynamicSp(pc.getDynamicSp() + 1);
				ChattingController.toChatting(pc, "[수배] 원/근 대미지 +2, sp+1", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 사용자가 죽었을때 호출됨.
	 * 
	 * @param cha
	 *            : 가해자
	 * @param o
	 *            : 피해자
	 */
	static public void toDead(Character cha, object o) {
		if (cha instanceof PcInstance && cha.getMap() != Lineage.teamBattleMap && !World.isCombatZone(cha.getX(), cha.getY(), cha.getMap()) && !World.isCombatZone(o.getX(), o.getY(), o.getMap())) {
			//
			Wanted wanted = null;
			for (Wanted w : list) {
				if (w.objId == o.getObjectId()) {
					wanted = w;
					break;
				}
			}
			//
			if (wanted == null)
				return;
			
			Kingdom kingdom = KingdomController.findKingdomLocation(o);
			if (kingdom != null && kingdom.isWar())
				return;

			if (o instanceof PcInstance) {
				PcInstance pc = (PcInstance) o;
				pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() - 2);
				pc.setDynamicAddDmg(pc.getDynamicAddDmg() - 2);
				pc.setDynamicSp(pc.getDynamicSp() - 1);


			}
			int regeditAdena = (int) (wanted.target_price * 0.8);
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("%s 현상금 지급  \\fS수배자:%s", cha.getName(), wanted.target_name)));

			synchronized (list) {
				list.remove(wanted);
			}

			Connection con = null;
			PreparedStatement st = null;

			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("DELETE FROM wanted WHERE objId=?");
				st.setLong(1, wanted.objId);
				st.executeUpdate();
			} catch (Exception e) {
			} finally {
				DatabaseConnection.close(con, st);
			}

			Item i = ItemDatabase.find("아데나");
	
			if (i != null) {
				ItemInstance temp = cha.getInventory().find(i.getName(), i.isPiles());
				
				if (temp == null) {
					temp = ItemDatabase.newInstance(i);
					temp.setObjectId(ServerDatabase.nextItemObjId());
					temp.setBless(1);
					temp.setCount((wanted.target_price - regeditAdena));
					
					temp.setDefinite(true);
					cha.getInventory().append(temp, true);
				} else {
					cha.getInventory().count(temp, temp.getCount() + (wanted.target_price - regeditAdena), true);
			
				}
			}
		
		}
	}

	/**
	 * 월드 접속시 호출.
	 * 
	 * @param pc
	 */
	static public void checkWanted(PcInstance pc) {
		// 현상수배자 명단 표현.
		synchronized (list) {
			if (list.size() == 0) {
				ChattingController.toChatting(pc, "수배자가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
			
			for (Wanted w : list)
				ChattingController.toChatting(pc, String.format("수배자: %s", w.target_name), Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	static public void toWorldJoin(PcInstance pc) {
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM wanted WHERE objId=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();

			if (rs.next()) {
				synchronized (list) {
					boolean result = false;
					
					for (Wanted w : list) {
						if (w.objId == pc.getObjectId()) {
							result = true;
							break;
						}
					}
					
					if (!result) {
						Wanted wanted = new Wanted();
						wanted.objId = rs.getLong("objId");
						wanted.target_name = rs.getString("name");
						wanted.target_price = rs.getLong("price");
						wanted.date = rs.getTimestamp("date").getTime();
						list.add(wanted);
					}
									
					pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + 2);
					pc.setDynamicAddDmg(pc.getDynamicAddDmg() + 2);
					pc.setDynamicSp(pc.getDynamicSp() + 1);
					ChattingController.toChatting(pc, "[수배] 원/근 대미지 +2, sp+1", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : toWorldJoin(PcInstance pc)\r\n", WantedController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 월드에서 나가면 호출.
	 * 
	 * @param pc
	 */
	static public void toWorldOut(PcInstance pc) {
		//
	}
	
	/**
	 * 캐릭터 이름 변경 주문서 사용시 호출.
	 * 2018-05-04
	 * by all-night.
	 */
	static public void changeName(String name, String newName) {
		for (Wanted w : list) {
			if (w.target_name.equalsIgnoreCase(name)) {
				w.target_name = newName;

				Connection con = null;
				PreparedStatement st = null;

				try {
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("UPDATE wanted SET name=? WHERE LOWER(name)=?");
					st.setString(1, newName);
					st.setString(2, name.toLowerCase());
					st.executeUpdate();
				} catch (Exception e) {
					lineage.share.System.printf("%s : changeName(String name, String newName)\r\n", WantedController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st);
				}
				break;
			}
		}
	}
	
	/**
	 * 수배 확인.
	 * 2019-08-04
	 * by connector12@nate.com
	 */
	static public boolean checkWantedPc(object o) {
		for (Wanted w : list) {
			if (w.objId == o.getObjectId())
				return true;
		}

		return false;
	}
	
	static public boolean checkWantedPc(long objId) {
		for (Wanted w : list) {
			if (w.objId == objId)
				return true;
		}

		return false;
	}
}
