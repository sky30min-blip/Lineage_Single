package lineage.world.object.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.bean.lineage.Summon;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.SummonController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PetInstance;

public class DogCollar extends ItemInstance {
	private long petObjectId;
	private String petName;
	private int petClassId;				// 네임아이디 번호. 패킷에 표현할때 펫의 클레스구분용으로 사용함.
	private int petLevel;				// 펫 레벨
	private int petHp;					// 펫의 순수 최대 hp값.
	private boolean petSpawn;			// 펫이 현재 소환된 상태인지 확인용.
	private boolean petDel;				// 펫이 삭제된 상태인지 확인용.

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new DogCollar();
		return item;
	}

	@Override
	public void close(){
		super.close();
		petName = null;
		petObjectId = petClassId = petLevel = petHp = 0;
		petDel = petSpawn = false;
	}

	@Override
	public long getPetObjectId() {
		return petObjectId;
	}

	@Override
	public void setPetObjectId(long petObjectId) {
		this.petObjectId = petObjectId;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public int getPetClassId() {
		return petClassId;
	}

	public void setPetClassId(int petClassId) {
		this.petClassId = petClassId;
	}

	public int getPetLevel() {
		return petLevel;
	}

	public void setPetLevel(int petLevel) {
		this.petLevel = petLevel;
	}

	public int getPetHp() {
		return petHp;
	}

	public void setPetHp(int petHp) {
		this.petHp = petHp;
	}

	public boolean isPetSpawn() {
		return petSpawn;
	}

	public void setPetSpawn(boolean petSpawn) {
		this.petSpawn = petSpawn;
	}
	
	public boolean isPetDel() {
		return petDel;
	}

	public void setPetDel(boolean petDel) {
		this.petDel = petDel;
	}

	/**
	 * 목걸이 정보를 해당 펫정보와 일치시키는 함수.
	 * @param pet
	 */
	public void toUpdate(PetInstance pet){
		petSpawn = true;
		petObjectId = pet.getObjectId();
		petClassId = pet.getMonster().getNameIdNumber();
		petLevel = pet.getLevel();
		petHp = pet.getMaxHp();
		petName = pet.getName();
		pet.setCollar(this);
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha instanceof PcInstance)
			// \f1아무일도 일어나지 않았습니다.
			cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79) );
	}

	@Override
	public void toWorldJoin(Connection con, PcInstance pc){
		super.toWorldJoin(con, pc);
		
		// 디비에서 정보 추출.
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters_pet WHERE objid=?");
			st.setLong(1, getPetObjectId());
			rs = st.executeQuery();
			if(rs.next()){
				petClassId = rs.getInt("classId");
				petLevel = rs.getInt("level");
				petHp = rs.getInt("maxHp");
				petName = rs.getString("name");
				petDel = rs.getString("del").equalsIgnoreCase("true");
			}
		} catch (Exception e) {
			lineage.share.System.println(DogCollar.class.toString()+" : toWorldJoin(Connection con, PcInstance pc)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		// 펫이 삭제된 상태라면 목걸이 제거.
		if(petDel){
			//
			pc.getInventory().count(this, 0, Lineage.server_version<=200);
			return;
		}
		
		// 목걸이 정보 다시 갱신.
		if(Lineage.server_version<=144) {
			cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
			cha.toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), this));
		} else if(Lineage.server_version <= 200) {
			cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), this));
		}
		
		// 메모리 연결.
		Summon s = SummonController.find(pc);
		if(s != null){
			object o = s.find(getPetObjectId());
			if(o != null)
				toUpdate((PetInstance)o);
		}
	}
	
}
