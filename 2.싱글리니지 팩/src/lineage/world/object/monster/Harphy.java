package lineage.world.object.monster;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Monster;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.potion.BraveryPotion;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.item.potion.WisdomPotion;

public class Harphy extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Harphy();
		return MonsterInstance.clone(mi, m);
	}
	
	// 휴식도중 반응하는 아이템 목록들
	private static final List<Integer> item_list;
	
	static {
		item_list = new ArrayList<Integer>();
		item_list.add(232);
		item_list.add(233);
		item_list.add(234);
		item_list.add(235);
		item_list.add(237);
		item_list.add(238);
		item_list.add(239);
		item_list.add(764);
		item_list.add(1507);
	}
	
	
	@Override
	public void toTeleport(int x, int y, int map, boolean effect) {
		super.toTeleport(x, y, map, effect);
		
		if(Util.random(0, 1) == 0)
			toStay(true);
	}

	@Override
	public void toAi(long time) {
		//
		if(getGfxMode() == getClassGfxMode()) {
			if(getNowHp()<=(getTotalHp()*0.4) && Util.random(0, 99)<=10)
				toStay(true);
		}
		
		super.toAi(time);
	}
	
	@Override
	protected void toAiWalk(long time) {
		if(getGfxMode() == getClassGfxMode()) {
	
			super.toAiWalk(time);
			return;
		}
		//
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+0);
		//
		
		
		if (isItemInside() || isPlayerInside())
			toStay(false);
	}
	
	@Override
	public void toAiAttack(long time) {
		if(getGfxMode() == getClassGfxMode()) {
			if (isItemInside()){
			super.toAiPickup(time);
		
			}else{
				super.toAiAttack(time);
			}
	

			return;	
		}

		//
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+0);
		
		if (isItemInside()){
			toStay(false);
		
		}
	}
	
	@Override
	protected boolean isPickupItem(object o) {
		if (o instanceof HealingPotion || o instanceof HastePotion || o instanceof BraveryPotion)
			return true;
		if (o instanceof ItemInstance && !containsAstarList(o))
			return item_list.contains(((ItemInstance) o).getItem().getNameIdNumber());
		else
			return super.isPickupItem(o);
	}
	
	@Override
	public void setNowHp(int nowHp) {
		if(getGfxMode() == getClassGfxMode())
			super.setNowHp(nowHp);
	}
	
	/**
	 * 주변에사용자가존재하는지 확인.
	 * @return
	 */
	private boolean isPlayerInside() {
	
		for(object o : getInsideList(true)) {
			if(o instanceof PcInstance && Util.isDistance(this, o, 2)) {
		
				toDamage((PcInstance)o, 0, 3);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 주변에 반응하는 아이템이 드랍되잇는지 확인.
	 * @return
	 */
	private boolean isItemInside() {
		for(object o : getInsideList(true)) {
			if(isPickupItem(o))
				return true;
		}
		return false;
	}
	
	private void toStay(boolean recess) {
		if(!recess){
			
			ai_time = SpriteFrameDatabase.find(gfx, 45);
			setGfxMode(getClassGfxMode());
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 45), false);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
		}else{
			
			ai_time = SpriteFrameDatabase.find(gfx, 44);
			setGfxMode(4);
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 44), false);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
		}
	}

}
