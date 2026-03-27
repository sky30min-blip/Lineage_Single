package lineage.world.object.item;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_ObjectAdd;
import lineage.network.packet.server.S_ObjectLight;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Candle extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Candle();
		item.setNowTime(60*10);
		item.setDynamicLight(Lineage.CANDLE_LIGHT);
		return item;
	}
	
	@Override
	public void toGiveItem(Character cha, Character target){
		toDrop(cha);
		toPickup(target);
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(equipped)
			// 버프 제거
			BuffController.remove(this, getClass());
		else
			// 버프 등록
			BuffController.append(this, this);
	}

	@Override
	public void setLight(int light) {
		super.setLight(light);
		if(!worldDelete)
			toSender(S_ObjectLight.clone(BasePacketPooling.getPool(S_ObjectLight.class), this), false);
	}
	
	@Override
	public void toPickup(Character cha){
		super.toPickup(cha);
		if(equipped)
			toOn();
	}
	
	@Override
	public void toDrop(Character cha){
		if(equipped){
			if(!isLighter())
				cha.setLight(0);
		}
		super.toDrop(cha);
	}
	
	@Override
	public void toWorldJoin(Connection con, PcInstance pc){
		super.toWorldJoin(con, pc);
		if(equipped)
			BuffController.append(this, this);
	}
	
	/**
	 * 켜기 처리 함수.
	 * @param light
	 */
	protected void toOn(){
		setLight( dynamicLight );
		// 라이트 확인.
		if(cha!=null && cha.getLight()<dynamicLight)
			cha.setLight( dynamicLight );
		if(cha != null)
			cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
	}
	
	/**
	 * 끄기 처리 함수.
	 * @param light
	 */
	protected void toOff(){
		setLight( 0 );
		// 현재 라이트보다 큰게 없다면 라이트값 조작.
		if(cha!=null && !isLighter() && cha.getLight()<=dynamicLight)
			cha.setLight( getLighter() );
		if(cha != null)
			cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
		if(!isWorldDelete())
			toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, this), false);
	}
	
	/**
	 * cha 인벤에 더 켜진 라이터가 존재하는지 여부 확인해주는 함수.
	 * @return
	 */
	private boolean isLighter(){
		List<ItemInstance> list_temp = new ArrayList<ItemInstance>();
		cha.getInventory().findLighter(list_temp);
		for(ItemInstance ii : list_temp){
			if(ii.getObjectId()!=getObjectId() && ii.getLight()>=dynamicLight)
				return true;
		}
		return false;
	}
	
	/**
	 * 현재 지정된 아이템에 밝기보다 작은 것을 찾아서 리턴함.
	 * @return
	 */
	private int getLighter(){
		List<ItemInstance> list_temp = new ArrayList<ItemInstance>();
		cha.getInventory().findLighter(list_temp);
		for(ItemInstance ii : list_temp){
			if(ii.getObjectId()!=getObjectId() && ii.getLight()<=dynamicLight)
				return ii.getLight();
		}
		return 0;
	}

	@Override
	public void toBuffStart(object o) {
		equipped = true;
		toOn();
	}

	@Override
	public void toBuffStop(object o) {
		equipped = false;
		toOff();
	}

	@Override
	public void toBuffEnd(object o) {
		toBuffStop(o);
		// 양초 제거.
		if(cha!=null){
			if(cha.getInventory()!=null){
				// 인벤에서 제거.
				cha.getInventory().count(this, 0, true);
				return;
			}
		}
		clearList(true);
		World.remove(this);
		ItemDatabase.setPool(this);
	}

}
