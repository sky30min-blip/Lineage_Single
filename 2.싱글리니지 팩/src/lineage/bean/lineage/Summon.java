package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcRobotInstance;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.item.DogCollar;

public class Summon {
	private long master_object_id;
	private object master;			// 객체 관리자.
	private long summon_master_leave_time;		// 마스터가 나간 시간
	private List<object> list;		// 관리중인 목록.
	private SummonInstance temp_si;	// 임시 기록용 (대화 요청되면 이녀석 갱신됨)

	public Summon(){
		temp_si = null;
		master = null;
		summon_master_leave_time = 0L;
		list = new ArrayList<object>();
	}

	/**
	 * 풀에 다시 등록될때 메모리 초기화처리 하는 뒷처리 함수.
	 */
	public void close(){
		master_object_id = 0;
		master = null;
		temp_si = null;
		summon_master_leave_time = 0L;
		synchronized (list) {
			list.clear();
		}
	}
	
	public long getSummon_master_leave_time() {
		return summon_master_leave_time;
	}

	public void setSummon_master_leave_time(long summon_master_leave_time) {
		this.summon_master_leave_time = summon_master_leave_time;
	}

	public void setMasterObjectId(long master_object_id){
		this.master_object_id = master_object_id;
	}

	public long getMasterObjectId(){
		return master_object_id;
	}

	public object getMaster() {
		return master;
	}

	public SummonInstance getTempSi() {
		return temp_si;
	}

	public void setTempSi(SummonInstance tempSi) {
		temp_si = tempSi;
	}

	/**
	 * SummonController.toSave 에서 호출해서 사용중.
	 * SummonController.toTeleport 에서 호출.
	 * PcRobotInstance.toBuffSummon() 에서 호출.
	 * HealAll.init 에서 호출.
	 *  : 펫만 필터링하여 디비 저장하기 위함.
	 *  : 텔레포트 처리하기 위해.
	 * @return
	 */
	public List<object> getList() {
		synchronized (list) {
			return new ArrayList<object>(list);
		}
	}

	/**
	 * 갯수 리턴
	 * @return
	 */
	public int getSize(){
		return list.size();
	}

	public object find(long object_id){
		for(object o : getList()){
			if(o.getObjectId() == object_id)
				return o;
		}
		return null;
	}

	/**
	 * 서먼 객체 관리자 등록 처리 함수.
	 * @param o
	 */
	public void toWorldJoin(object o){
		master = o;
		master.setSummon(this);
		// 펫 목걸이 연결은 PcInstance에 inventory 세팅하면서 목걸이가 걸렷을때 DogCollar.toWorldJoin 에서 처리.
	}

	/**
	 * 마스터가 월드를 빠져나갈때 호출됨.
	 */
	public void toWorldOut(){
		// 펫 목걸이 연결 해제.
		for(ItemInstance ii : master.getInventory().getList()){
			if(ii instanceof DogCollar){
				DogCollar dc = (DogCollar)ii;
				object o = find(dc.getPetObjectId());
				if(o != null)
					((PetInstance)o).setCollar(null);
			}
		}

		master.setSummon(null);
		master = null;
		setSummon_master_leave_time(System.currentTimeMillis() + (1000 * 60));
	}

	/**
	 * 관리목록에 등록.
	 * @param o
	 */
	public void append(object o){
		synchronized (list) {
			list.add(o);
		}

		o.setSummon(this);
		if(master != null){
			o.setOwnName(master.getName());
			o.setOwnObjectId(master.getObjectId());
		}
	}

	/**
	 * 관리목록에서 제거.
	 * @param o
	 */
	public void remove(object o){
		synchronized (list) {
			list.remove(o);
		}

		o.setSummon(null);
		o.setOwnName(null);
		o.setOwnObjectId(0);
	}

	/**
	 * 모든 펫만 제거.<br/>
	 *  : 목걸이 정보 갱신.<br/>
	 *  : 사용자 인벤토리 갱신을위해 패킷 처리.<br/>
	 */
	public boolean removeAllPet(){
		boolean check = false;
		for(object o : getList()){
			if(o instanceof PetInstance){
				PetInstance pet = (PetInstance)o;
				// 펫과 연결된 목걸이에 정보 갱신.
				if(pet.getCollar() != null){
					pet.getCollar().toUpdate(pet);
					pet.getCollar().setPetSpawn(false);
					// 패킷 처리.
					if(master!=null && Lineage.server_version>=160)
						master.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), pet.getCollar()));
				}
				remove(pet);
				pet.toAiClean(true);
				check = true;
			}
		}
		return check;
	}

	/**
	 * 전체 모드 경 함수.
	 */
	public void setMode(SummonInstance.SUMMON_MODE mode){
		for(object o : getList()){
			if(o instanceof SummonInstance)
				((SummonInstance)o).setSummonMode(mode);
		}
	}

	/**
	 * 펫 모드 변경 함수.
	 * @param mode
	 */
	public void setModePet(SummonInstance.SUMMON_MODE mode){
		for(object o : getList()){
			if(o instanceof PetInstance)
				((PetInstance)o).setSummonMode(mode);
		}
	}

	/**
	 * target 이 공격을 해서 데미지가 가해졌을때 호출됨.<br/>
	 *  : SummonController.toDamage에서 호출함.<br/>
	 *  : SummonInstance.toDamage 에서 호출함.
	 * @param target
	 * @param dmg
	 */
	public void toDamage(object target, int dmg){
		// 로봇이 소환한 객체라면 로봇 공격목록에 추가.
		if(master!=null && master instanceof PcRobotInstance)
			((PcRobotInstance)master).addAttackList(target);
		//
		for(object o : getList()){
			if(o instanceof SummonInstance){
				SummonInstance si = (SummonInstance)o;
				// 공격태세, 경계, 방어태세만 등록.
				if(si.getSummonMode()==SummonInstance.SUMMON_MODE.AggressiveMode || si.getSummonMode()==SummonInstance.SUMMON_MODE.Alert || si.getSummonMode()==SummonInstance.SUMMON_MODE.DefensiveMode)
					si.addAttackList(target);
			}
		}
	}
	public void removePet(long petObjId) {
		for (object o : getList()) {
			if (o instanceof PetInstance && o.getObjectId() == petObjId) {
				PetInstance pet = (PetInstance) o;
				// 펫과 연결된 목걸이에 정보 갱신.
				if (pet.getCollar() != null) {
					pet.getCollar().toUpdate(pet);
					pet.getCollar().setPetSpawn(false);
					// 패킷 처리.
					if (master != null && Lineage.server_version >= 160)
						master.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), pet.getCollar()));
				}
				remove(pet);
				pet.toAiClean(true);
				break;
			}
		}
	}
	/**
	 * 타이머에서 주기적으로 호출함.
	 */
	public void toTimer(){
		for(Object o : getList()){
			if(o instanceof SummonInstance)
				((SummonInstance)o).toTimer();
		}
	}

	/**
	 * 요정 정령객체가 몇개인지 확인해주는 함수.
	 * @return
	 */
	public int getElementalSize(){
		int size = 0;
		for(object o : getList()){
			if(o instanceof SummonInstance){
				SummonInstance si = (SummonInstance)o;
				if(si.isElemental())
					size += 1;
			}
		}
		return size;
	}
}
