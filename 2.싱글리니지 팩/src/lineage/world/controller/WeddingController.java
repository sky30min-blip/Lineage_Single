package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Wedding;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.WeddingRing;

public class WeddingController {

	static private List<Wedding> list;

	/**
	 * 초기화 처리 메서드.
	 * 
	 * @param con
	 */
	static public void init(Connection con) {
		TimeLine.start("WeddingController..");

		list = new ArrayList<Wedding>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters_wedding");
			rs = st.executeQuery();
			while (rs.next()) {
				Wedding w = new Wedding();
				w.setManObjectId(rs.getLong("manObjectId"));
				w.setGirlObjectId(rs.getLong("girlObjectId"));
				w.setManName(rs.getString("manName"));
				w.setGirlName(rs.getString("girlName"));
				w.setDateTime(rs.getTimestamp("dateTime").getTime());
				//
				list.add(w);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", WeddingController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	/**
	 * 종료처리 메서드.
	 * 
	 * @param con
	 */
	static public void close(Connection con) {
		toSave(con);
		synchronized (list) {
			list.clear();
		}
	}

	/**
	 * 저장처리 메서드.
	 * 
	 * @param con
	 */
	static public void toSave(Connection con) {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("DELETE FROM characters_wedding");
			st.executeUpdate();
			st.close();
			synchronized (list) {
				int idx = 1;
				for (Wedding w : list) {
					if (w.getDateTime() == 0)
						continue;
					st = con.prepareStatement("INSERT INTO characters_wedding SET uid=?, manObjectId=?, girlObjectId=?, manName=?, girlName=?, dateTime=?");
					st.setInt(1, idx++);
					st.setLong(2, w.getManObjectId());
					st.setLong(3, w.getGirlObjectId());
					st.setString(4, w.getManName());
					st.setString(5, w.getGirlName());
					st.setTimestamp(6, new Timestamp(w.getDateTime()));
					st.executeUpdate();
					st.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.println(WeddingController.class.toString() + " : toSave(Connection con)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * 청혼 처리 메서드.
	 * 
	 * @param pc
	 */
	static public void toPropose(PcInstance pc) {
		//
		PcInstance use = getPlayer(pc);
		if (use == null)
			return;
		//
		Wedding w = find(pc);
		if (w != null) {
			// 657 결혼: 당신은 이미 결혼한 상태입니다!
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 657));
			return;
		}
		w = find(use);
		if (w != null) {
			// 658 결혼: 상대가 이미 결혼한 상태입니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 658));
			return;
		}
		if (pc.getInventory().find(WeddingRing.class) == null) {
			// 659 결혼: 당신은 결혼 반지가 없습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 659));
			return;
		}
		if (use.getInventory().find(WeddingRing.class) == null) {
			// 660 결혼: 상대가 결혼 반지가 없습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 660));
			return;
		}
		if (pc.getClassSex() == use.getClassSex()) {
			// 661 결혼: 동성끼리는 결혼 할 수 없습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 661));
			return;
		}
		// 654 %0%s 청혼을 하였습니다. %0의 청혼을 승낙하시겠습니까? (Y/N)
		use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 654, pc.getName()));
		synchronized (list) {
			w = new Wedding();
			w.setManObjectId(pc.getClassSex() == 1 ? pc.getObjectId() : use.getObjectId());
			w.setManName(pc.getClassSex() == 1 ? pc.getName() : use.getName());
			w.setGirlObjectId(pc.getClassSex() == 1 ? use.getObjectId() : pc.getObjectId());
			w.setGirlName(pc.getClassSex() == 1 ? use.getName() : pc.getName());
			list.add(w);
		}
	}

	/**
	 * 청혼처리 마지막 부분.
	 * 
	 * @param pc
	 * @param yes
	 */
	static public void toProposeFinal(PcInstance pc, boolean yes) {
		//
		Wedding w = find(pc);
		if (w == null)
			return;
		//
		PcInstance use = World.findPc(pc.getClassSex() == 1 ? w.getGirlObjectId() : w.getManObjectId());
		if (use == null) {
			remove(w);
			return;
		}
		//
		if (yes == false) {
			// 656 결혼: 아쉽지만.. %0%s 청혼을 거절하였습니다.
			use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 656, pc.getName()));
			remove(w);
		} else {
			// 655 결혼: 축하합니다! %0%s 청혼을 승낙하였습니다.
			use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 655, pc.getName()));
			w.setDateTime(System.currentTimeMillis());
		}

	}

	/**
	 * 이혼 처리 메서드.
	 * 
	 * @param pc
	 */
	static public void toDivorce(PcInstance pc) {
		//
		Wedding w = find(pc);
		if (w == null)
			return;
		// 653 이혼을 하면 당신의 결혼 반지가 사라집니다. 이혼하시겠습니까? (Y/N)
		pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 653, pc.getName()));
	}

	/**
	 * 이혼 처리 마지막 부분.
	 * 
	 * @param pc
	 */
	static public void toDivorceFinal(PcInstance pc, boolean yes) {
		//
		Wedding w = find(pc);
		if (w == null)
			return;
		remove(w);
		//
		List<ItemInstance> list = new ArrayList<ItemInstance>();
		pc.getInventory().findClass(WeddingRing.class, list);
		for(ItemInstance ii : list)
			pc.getInventory().count(ii, 0, true);
		list.clear();
		// 3038 이혼한 상태가 되었습니다.
		ChattingController.toChatting(pc, "이혼한 상태가 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public Wedding find(long objectId) {
		synchronized (list) {
			for (Wedding w : list) {
				if (w.getManObjectId() == objectId || w.getGirlObjectId() == objectId)
					return w;
			}
		}
		return null;
	}

	static private Wedding find(PcInstance pc) {
		return find(pc.getObjectId());
	}

	static public void append(Wedding w) {
		if (w == null)
			return;
		synchronized (list) {
			list.add(w);
		}
	}

	static public void remove(Wedding w) {
		if (w == null)
			return;
		synchronized (list) {
			list.remove(w);
		}
	}

	/**
	 * 맞은편에 있는 사용자 찾아서 리턴.
	 */
	static private PcInstance getPlayer(PcInstance pc) {
		int locx = pc.getX();
		int locy = pc.getY();
		switch (pc.getHeading()) {
			case 0:
				locy--;
				break;
			case 1:
				locx++;
				locy--;
				break;
			case 2:
				locx++;
				break;
			case 3:
				locx++;
				locy++;
				break;
			case 4:
				locy++;
				break;
			case 5:
				locx--;
				locy++;
				break;
			case 6:
				locx--;
				break;
			default:
				locx--;
				locy--;
				break;
		}
		final List<object> list = new ArrayList<object>();
		pc.findInsideList(locx, locy, list);
		for (object o : list) {
			if (o instanceof PcInstance) {
				// 3방향일때 +4하면 7방향이됨. 결과적으로 서로 맞우보게 됨.
				int h = o.getHeading() + 4;
				// 방향 7 이상일경우 -8을해서 0부터 시작되도록 함.
				if (h > 7)
					h -= 8;
				// 서로 마주보고있다면 리턴하기.
				if (h == pc.getHeading()) {
					return (PcInstance) o;
				} else {
					// \f1%0%s 당신을 보고 있지 않습니다.
					pc.toSender(new S_Message( 91, o.getName()));
					return null;
				}
			}
		}
		// 그 곳에는 아무도 없습니다.
		pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 93));
		return null;
	}

}
