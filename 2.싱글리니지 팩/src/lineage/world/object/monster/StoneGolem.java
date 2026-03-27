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
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.Detection;

public class StoneGolem extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new StoneGolem();
		return MonsterInstance.clone(mi, m);
	}
	
	// 휴식도중 반응하는 아이템 목록들
	private static final List<Integer> weapon_list;
	
	static {
		weapon_list = new ArrayList<Integer>();
		weapon_list.add(1);
		weapon_list.add(14);
		weapon_list.add(69);
		weapon_list.add(74);
		weapon_list.add(20);
	}
	
	@Override
	public void toTeleport(int x, int y, int map, boolean effect) {
		super.toTeleport(x, y, map, effect);
		

		toStay(true);
	}
	
	@Override
	protected void toAiWalk(long time) {
		if(getGfxMode() == 11){
			return;
		}
		if(getGfxMode() == getClassGfxMode()) {
			super.toAiWalk(time);
			return;
		}
		//
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+0);
		//
		if(isPlayerInside())
			toStay(false);
	}
	

	
	@Override
	public void toMagic(Character cha, Class<?> c){
		if(getGfxMode()==getClassGfxMode() || !c.toString().equals(Detection.class.toString()))
			return;
		
		toStay(false);
		addAttackList(cha);
	}
	
	@Override
	public void toAiAttack(long time) {
		if(getGfxMode() == getClassGfxMode()) {
			super.toAiAttack(time);
			return;	
		}

		//
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+0);
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
	

	
	private void toStay(boolean recess) {
		if(!recess){
			ai_time = SpriteFrameDatabase.find(gfx, gfxMode+11);
			setGfxMode(0);
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 11), false);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
		}else{
			ai_time = SpriteFrameDatabase.find(gfx, gfxMode+4);
			setGfxMode(4);
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this), false);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
		}
	}
}
