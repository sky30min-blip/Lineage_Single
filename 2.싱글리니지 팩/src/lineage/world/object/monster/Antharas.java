package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.magic.Detection;

public class Antharas extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Antharas();
		return MonsterInstance.clone(mi, m);
	}
	
	@Override
	public void toAiAttack(long time) {
		// hp가 30%미만이면 도망모드로 변환.
		if(getNowHp()<=getTotalHp()*0.3 && Util.random(0, 100)>80) {
			//
			setGfxMode(20);
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this), false);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
			//
			setAiStatus( 5 );
			return;
		}
		super.toAiAttack(time);
	}
	
	@Override
	public void toAiEscape(long time) {
		//
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+0);
		boolean isSafeHp = getNowHp() > getTotalHp()*0.35;
		// hp가 안전해지거나 일정확률적으로 돌아오기.
		if(isSafeHp || Util.random(0, 100)<=5) {
			//
			toTeleport(getX(), getY(), getMap(), false);
			//
			setGfxMode(11);
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this), false);
			setGfxMode(getClassGfxMode());
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
			//
			setAiStatus( 1 );
			return;
		}
	}
	
	@Override
	public boolean isAttack(Character cha, boolean magic) {
		if(getGfxMode() != getClassGfxMode())
			return false;
		return super.isAttack(cha, magic);
	}
	
	@Override
	public void toMagic(Character cha, Class<?> c){
		if(getGfxMode()==getClassGfxMode() || !c.toString().equals(Detection.class.toString()))
			return;

		//
		setGfxMode(11);
		toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this), false);
		setGfxMode(getClassGfxMode());
		toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
		//
		setAiStatus( 1 );
		//
		addAttackList(cha);
	}

}
