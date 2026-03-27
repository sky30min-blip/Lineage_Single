package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.world.object.npc.Maid;
import lineage.world.object.npc.background.door.Door;


public class Agit {
	private int uid;
	private int chaObjectId;
	private String chaName;
	private int clanId;
	private String clanName;
	private String agitName;
	private int agitX;
	private int agitY;
	private int agitMap;
	private String agitDoor;
	private String agitSign;
	private String agitNpc;
	private Maid maid;
	private List<Door> list_door;
	
	public Agit(){
		list_door = new ArrayList<Door>();
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getChaObjectId() {
		return chaObjectId;
	}
	public void setChaObjectId(int chaObjectId) {
		this.chaObjectId = chaObjectId;
	}
	public String getChaName() {
		return chaName;
	}
	public void setChaName(String chaName) {
		this.chaName = chaName;
	}
	public int getClanId() {
		return clanId;
	}
	public void setClanId(int clanId) {
		this.clanId = clanId;
	}
	public String getClanName() {
		return clanName;
	}
	public void setClanName(String clanName) {
		this.clanName = clanName;
	}
	public String getAgitName() {
		return agitName;
	}
	public void setAgitName(String agitName) {
		this.agitName = agitName;
	}
	public int getAgitX() {
		return agitX;
	}
	public void setAgitX(int agitX) {
		this.agitX = agitX;
	}
	public int getAgitY() {
		return agitY;
	}
	public void setAgitY(int agitY) {
		this.agitY = agitY;
	}
	public int getAgitMap() {
		return agitMap;
	}
	public void setAgitMap(int agitMap) {
		this.agitMap = agitMap;
	}
	public String getAgitDoor() {
		return agitDoor;
	}
	public void setAgitDoor(String agitDoor) {
		this.agitDoor = agitDoor;
	}
	public String getAgitSign() {
		return agitSign;
	}
	public void setAgitSign(String agitSign) {
		this.agitSign = agitSign;
	}
	public String getAgitNpc() {
		return agitNpc;
	}
	public void setAgitNpc(String agitNpc) {
		this.agitNpc = agitNpc;
	}
	public Maid getMaid() {
		return maid;
	}
	public void setMaid(Maid maid) {
		this.maid = maid;
	}
	public void append(Door d){
		list_door.add(d);
	}
	public List<Door> getDoorList(){
		return list_door;
	}
	public boolean isLocation(String db, int x, int y){
		StringTokenizer st = new StringTokenizer(db, "|");
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			if(token.length()>1){
				String[] value = token.split(",");
				int db_x = Integer.valueOf(value[0]);
				int db_y = Integer.valueOf(value[1]);
				if(db_x==x && db_y==y)
					return true;
			}
		}
		return false;
	}
}
