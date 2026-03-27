package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Agit;
import lineage.database.DatabaseConnection;
import lineage.database.TeleportHomeDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.Maid;
import lineage.world.object.npc.background.door.Door;

public final class AgitController {

	static public List<Agit> list;
	
	static public void init(Connection con){
		TimeLine.start("AgitController..");
		
		list = new ArrayList<Agit>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM clan_agit");
			rs = st.executeQuery();
			while(rs.next()){
				Agit a = new Agit();
				a.setUid(rs.getInt("uid"));
				a.setChaObjectId(rs.getInt("cha_objId"));
				a.setChaName(rs.getString("cha_name"));
				a.setClanId(rs.getInt("clan_id"));
				a.setClanName(rs.getString("clan_name"));
				a.setAgitName(rs.getString("agit_name"));
				a.setAgitX(rs.getInt("agit_x"));
				a.setAgitY(rs.getInt("agit_y"));
				a.setAgitMap(rs.getInt("agit_map"));
				a.setAgitDoor(rs.getString("agit_door"));
				a.setAgitSign(rs.getString("agit_sign"));
				a.setAgitNpc(rs.getString("agit_npc"));
				
				// 등록
				list.add(a);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", AgitController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public void close(Connection con){
		for(Agit a : list)
			update(con, a);
		list.clear();
	}
	
	/**
	 * 타이머에서 주기적으로 호출.
	 * @param time
	 */
	static public void toTimer(long time){
		// 임대료 확인.
	}
	
	/**
	 * 해당 사용자와 연결된 아지트 찾아서 리턴.
	 * @param pc
	 * @return
	 */
	static public Agit find(PcInstance pc){
		for(Agit a : list){
			if(a.getClanId()!=0 && a.getClanId()==pc.getClanId())
				return a;
		}
		return null;
	}
	
	/**
	 * 아지트 고유값으로 객체 찾기.
	 * @param uid
	 * @return
	 */
	static public Agit find(int uid){
		for(Agit a : list){
			if(a.getUid() == uid)
				return a;
		}
		return null;
	}
	
	/**
	 * 아지트 목록에서 해당좌표와 일치하는 것과 있을경우 해당 아지트를 리턴.<br/>
	 *  : 아지트 문이나 푯말 시녀등을 잡아주기 위해.
	 * @param type	: 구분자
	 * @param x
	 * @param y
	 * @param map
	 * @return
	 */
	static public Agit find(String type, int x, int y){
		if(type.equalsIgnoreCase("door")){
			for(Agit a : list){
				if(a.isLocation(a.getAgitDoor(), x, y))
					return a;
			}
		}
		if(type.equalsIgnoreCase("npc")){
			for(Agit a : list){
				if(a.isLocation(a.getAgitNpc(), x, y))
					return a;
			}
		}
		if(type.equalsIgnoreCase("sign")){
			for(Agit a : list){
				if(a.isLocation(a.getAgitSign(), x, y))
					return a;
			}
		}
		return null;
	}
	
	/**
	 * 아지트 내부 좌표에 케릭터가 잇는지 체크
	 * @return
	 */
	static public boolean isAgitLocation(object o){
		for(int[] i : Lineage.AGITLOCATION){
			if(i[0]<=o.getX() && i[1]>=o.getX() && i[2]<=o.getY() && i[3]>=o.getY() && 4==o.getMap())
				return true;
		}
		return false;
	}
	
	static public boolean isAgitLocation(object o, int idx){
		return Lineage.AGITLOCATION[idx][0]<=o.getX() && Lineage.AGITLOCATION[idx][1]>=o.getX() && Lineage.AGITLOCATION[idx][2]<=o.getY() && Lineage.AGITLOCATION[idx][3]>=o.getY() && 4==o.getMap();
	}
	
	/**
	 * 아지트와 연결된 문을 시녀를 통해 열거나 닫을때 사용.
	 * @param maid
	 */
	static public void toDoor(Agit agit, boolean open){
		for(Door d : agit.getDoorList()){
			if(open)
				d.toOpen();
			else
				d.toClose();
			d.toSend();
		}
	}
	
	/**
	 * 시녀를통해 외부인을 내보내려하면 호출됨.
	 * @param maid
	 * @param agit
	 */
	static public void toExpel(Maid maid, Agit agit){
		List<object> temp = new ArrayList<object>();
		temp.addAll(maid.getInsideList());
		
		for(object o : temp){
			if(o instanceof PcInstance && agit.getClanId()!=o.getClanId() && isAgitLocation(o)){
				TeleportHomeDatabase.toLocation(o);
				o.toTeleport(o.getHomeX(), o.getHomeY(), o.getHomeMap(), true);
			}
		}
	}
	
	/**
	 * 아지트 이름 변경 요청 처리 함수.
	 * @param pc
	 * @param name
	 */
	static public void toNameChange(PcInstance pc, String name){
		Agit agit = find(pc);
		if(agit != null){
			if(isAgitName(name) || name.length()<=1){
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 514));
				return;
			}
			if(name.length() >= 10){
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 513));
				return;
			}
			if(pc.getClassType() != Lineage.LINEAGE_CLASS_ROYAL){
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 518));
				return;
			}
			
			// 아지트 이름 변경.
			agit.setAgitName(name);
			agit.getMaid().toTalk(pc, null);
		}
	}
	
	/**
	 * 아지트 목록에서 해당하는 이름과 같은게 있는지 확인해주는 함수.
	 * @param name
	 * @return
	 */
	static private boolean isAgitName(String name){
		for(Agit a : list){
			if(a.getAgitName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	/**
	 * 아지트정보 디비에 갱신하는 함수.
	 * @param con
	 * @param agit
	 */
	static public void update(Connection con, Agit agit){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE clan_agit SET cha_objId=?,cha_name=?,clan_id=?,clan_name=?,agit_name=? WHERE uid=?");
			st.setInt(1, agit.getChaObjectId());
			st.setString(2, agit.getChaName() == null ? "" : agit.getChaName());
			st.setInt(3, agit.getClanId());
			st.setString(4, agit.getClanName() == null ? "" : agit.getClanName());
			st.setString(5, agit.getAgitName() == null ? "" : agit.getAgitName());
			st.setInt(6, agit.getUid());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : update(Connection con, Agit agit)\r\n", AgitController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
}
