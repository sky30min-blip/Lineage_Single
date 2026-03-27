package lineage.bean.lineage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

public class Clan {
	private int uid;					// 혈맹 고유 키
	private String name;				// 혈맹 이름
	private String lord;				// 군주 이름
	private byte[] icon;				// 혈맹 문장
	private List<String> member_list;	// 혈맹원들 이름 목록
	private List<PcInstance> list;		// 접속중인 혈맹원 목록
	private PcInstance temp_pc;			// 사용자 정보 임시 저장용으로 활용중. 가입요청시 기록함.
	private String war_clan;			// 전쟁중인 혈맹에 이름.
	private List<PcInstance> call_list;	// 콜클렌으로 요청한 사용자 목록. (버그방지용)
	private Long warehouseObjectId;
	private long clanWarehouseTime;		// 혈맹창고 찾기 이용시 한명만 이용 가능. 복사버그 방지 
	
	private int clan_point;				// 사용 가능한 혈맹 포인트
	private int sell_clan_point;		// 사용한 혈맹포인트
	private int clan_total_point;		// 누적 혈맹 포인트
	private int 경험치증가;
	private int 드랍확률증가;
	private int 아덴증가;
	private int 추타;
	private int 리덕;
	private int pvp_추타;
	private int pvp_리덕;
	private int 스턴내성;
	private int 치명타확률;
	private int sp;

	public Clan(){
		call_list = new ArrayList<PcInstance>();
		list = new ArrayList<PcInstance>();
		member_list = new ArrayList<String>();
		war_clan = null;
		warehouseObjectId = 0L;
		clanWarehouseTime = 0L;
		clan_point = sell_clan_point = clan_total_point = 경험치증가 = 드랍확률증가 = 아덴증가 = 추타 = 리덕 = pvp_추타 = pvp_리덕 = 스턴내성 = 치명타확률 = sp = 0;
	}
	
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLord() {
		return lord;
	}

	public void setLord(String lord) {
		this.lord = lord;
	}

	public byte[] getIcon() {
		return icon;
	}

	public void setIcon(byte[] icon) {
		this.icon = icon;
	}

	public PcInstance getTempPc() {
		return temp_pc;
	}

	public void setTempPc(PcInstance temp_pc) {
		this.temp_pc = temp_pc;
	}
	
	public String getWarClan() {
		return war_clan;
	}

	public void setWarClan(String war_clan) {
		this.war_clan = war_clan;
	}
	
	public Long getWarehouseObjectId() {
		return warehouseObjectId;
	}

	public void setWarehouseObjectId(Long warehouseObjectId) {
		this.warehouseObjectId = warehouseObjectId;
	}
	
	public long getClanWarehouseTime() {
		return clanWarehouseTime;
	}

	public void setClanWarehouseTime(long clanWarehouseTime) {
		this.clanWarehouseTime = clanWarehouseTime;
	}
	
	public int getClan_point() {
		return clan_point;
	}

	public void setClan_point(int clan_point) {
		this.clan_point = clan_point;
	}
	
	public int getSell_clan_point() {
		return sell_clan_point;
	}

	public void setSell_clan_point(int sell_clan_point) {
		this.sell_clan_point = sell_clan_point;
	}
	
	public int getClan_total_point() {
		return clan_total_point;
	}

	public void setClan_total_point(int clan_total_point) {
		this.clan_total_point = clan_total_point;
	}
	
	public int get경험치증가() {
		return 경험치증가;
	}

	public void set경험치증가(int 경험치증가) {
		this.경험치증가 = 경험치증가;
	}

	public int get드랍확률증가() {
		return 드랍확률증가;
	}

	public void set드랍확률증가(int 드랍확률증가) {
		this.드랍확률증가 = 드랍확률증가;
	}
	
	public int get아덴증가() {
		return 아덴증가;
	}

	public void set아덴증가(int 아덴증가) {
		this.아덴증가 = 아덴증가;
	}

	public int get추타() {
		return 추타;
	}

	public void set추타(int 추타) {
		this.추타 = 추타;
	}

	public int get리덕() {
		return 리덕;
	}

	public void set리덕(int 리덕) {
		this.리덕 = 리덕;
	}

	public int getPvp_추타() {
		return pvp_추타;
	}

	public void setPvp_추타(int pvp_추타) {
		this.pvp_추타 = pvp_추타;
	}

	public int getPvp_리덕() {
		return pvp_리덕;
	}

	public void setPvp_리덕(int pvp_리덕) {
		this.pvp_리덕 = pvp_리덕;
	}

	public int get스턴내성() {
		return 스턴내성;
	}

	public void set스턴내성(int 스턴내성) {
		this.스턴내성 = 스턴내성;
	}

	public int get치명타확률() {
		return 치명타확률;
	}

	public void set치명타확률(int 치명타확률) {
		this.치명타확률 = 치명타확률;
	}

	public int getSp() {
		return sp;
	}

	public void setSp(int sp) {
		this.sp = sp;
	}
	
	public void pointOptionReset() {
		경험치증가 = 드랍확률증가 = 아덴증가 = 추타 = 리덕 = pvp_추타 = pvp_리덕 = 스턴내성 = 치명타확률 = sp = 0;
	}
	
	/**
	 * 혈맹이 해체될때 호출됨.
	 *  : 풀에 넣기위한 준비 단계.
	 *    사용된 메모리 제거 등 처리함.
	 */
	public void close(){
		synchronized (list) {
			list.clear();
		}
		synchronized (call_list) {
			call_list.clear();
		}
		synchronized (member_list) {
			member_list.clear();
		}
		uid = 0;
		temp_pc = null;
		icon = null;
		war_clan = null;
		clanWarehouseTime = 0L;
		clan_point = sell_clan_point = clan_total_point = 경험치증가 = 드랍확률증가 = 아덴증가 = 추타 = 리덕 = pvp_추타 = pvp_리덕 = 스턴내성 = 치명타확률 = sp = 0;
	}
	
	/**
	 * 해당 객체의 혈맹원이 월드에 진입할때 호출됨.
	 * @param pc
	 */
	public void toWorldJoin(PcInstance pc) {
		// 혈맹원 %0님께서 방금 게임에 접속하셨습니다.
		toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("혈맹원 %s님께서 방금 게임에 접속하셨습니다.", pc.getName())));
		// 혈전중인지 체크.
		if (war_clan != null) {

		}
		//
		appendList(pc);
	}
	
	/**
	 * 해당 객체의 혈맹원이 월드에 빠져나갈때 호출됨.
	 * @param pc
	 */
	public void toWorldOut(PcInstance pc){
		synchronized (list) {
			if (list.contains(pc))
				list.remove(pc);
		}
	}
	
	public void appendList(PcInstance pc) {
		synchronized (list) {
			if (!list.contains(pc))
				list.add( pc );
		}
	}
	
	public void removeList(PcInstance pc) {
		synchronized (list) {
			if (list.contains(pc))
				list.remove( pc );
		}
	}
	
	public List<PcInstance> getList(){
		synchronized (list) {
			return new ArrayList<PcInstance>( list );
		}
	}
	
	public void appendMemberList(String name) {
		synchronized (member_list) {
			member_list.add(name.toLowerCase());
		}
	}
	
	public void removeMemberList(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		
		synchronized (member_list) {
			member_list.remove(name);
		}
	}
	
	public boolean containsMemberList(String name) {
		for (String string : member_list) {
			if (string.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
		
//		synchronized (member_list) {
//			if (member_list.contains(name.toLowerCase())) {
//				return true;
//			}
//		}
//
//		return false;
	}
	
	public int sizeMemberList() {
		return member_list.size();
	}

	public List<String> getMemberList(){
		synchronized (member_list) {
			return new ArrayList<String>( member_list );
		}
	}
	
	public void appendCallList(PcInstance pc) {
		synchronized (call_list) {
			call_list.add( pc );
		}
	}
	
	public void removeCallList(PcInstance pc) {
		synchronized (call_list) {
			call_list.remove( pc );
		}
	}
	
	public boolean containsCallList(PcInstance pc) {
		synchronized (call_list) {
			return call_list.contains(pc);
		}
	}
	
	/**
	 * 혈맹원 목록 초기화
	 * 2021-01-27
	 * by connector12@nate.com
	 */
	public void resetMember() {
		if (is일반혈맹()) {
			synchronized (member_list) {
				member_list.clear();
				
				Connection con = null;
				PreparedStatement st = null;
				ResultSet rs = null;
				
				try {
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("SELECT name FROM characters WHERE clanID=?");
					st.setInt(1, uid);
					rs = st.executeQuery();
					
					while (rs.next()) {
						String name = rs.getString(1);
						PcInstance pc = World.findPc(name);
						
						if (pc == null || (pc != null && pc.getClanId() == uid)) {
							member_list.add(name);
						}
					}
					
					st.close();
					rs.close();
					
					st = con.prepareStatement("UPDATE clan_list SET list=? WHERE uid=?");
					st.setString(1, getMemberNameList());
					st.setInt(2, uid);
					st.executeUpdate();
					st.close();
				} catch (Exception e) {
					lineage.share.System.printf("%s : resetMember()\r\n", Clan.class.toString());
					lineage.share.System.println(String.format("혈맹명: %s", name));
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st, rs);
				}
			}
		}
	}
	
	/**
	 * 접속되있는 전체 혈맹원들에게 패킷 전송 처리 함수.
	 * @param bp
	 */
	public void toSender(BasePacket bp){
		if(bp instanceof ServerBasePacket){
			ServerBasePacket sbp = (ServerBasePacket)bp;
			for(PcInstance pc : getList())
				pc.toSender( ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()) );
		}
		BasePacketPooling.setPool(bp);
	}
	
	/**
	 * 접속된 혈맹원 목록에서 동일한 이름을 가진 사용자 찾아서 리턴.
	 * @param name
	 * @return
	 */
	public PcInstance find(String name){
		for(PcInstance pc : getList()){
			if(pc.getName().equalsIgnoreCase(name))
				return pc;
		}
		return null;
	}
	
	/**
	 * 접속된 혈맹원 목록에서 군주를 찾아 리턴하는 함수.
	 * @return
	 */
	public PcInstance getRoyal(){
		for(PcInstance pc : getList()){
			if(/*pc.getName().equalsIgnoreCase(lord)*/pc.getClanGrade() == 3)
				return pc;
		}
		return null;
	}
	
	/**
	 * 접속중인 혈맹원들 이름 정리<br/>
	 *  : 접속된 혈맹원들 목록보기 패킷 처리에 사용.
	 * @return
	 */
	public String getMemberNameListConnect(){
		StringBuffer sb = new StringBuffer();
		for(PcInstance pc : getList()){
			sb.append(String.format("[%s]", pc.getClassType() == 0 ? "군" : 
				pc.getClassType() == 1 ? "기" : pc.getClassType() == 2 ? "요" : "법") + pc.getName());
			sb.append(" ");
		}
		return sb.toString();
	}
	
	/**
	 * 전체 혈맹원들 이름 추출
	 * @return
	 */
	public String getMemberNameList(){
		StringBuffer sb = new StringBuffer();
		for(String name : getMemberList()){
			sb.append(name);
			sb.append(" ");
		}
		return sb.toString();
	}
	
	public boolean is일반혈맹() {
		if (getLord() != null && getLord().length() > 0) {
			return true;
		}
		
		return false;
	}
}
