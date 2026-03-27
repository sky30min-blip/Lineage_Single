package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.database.MonsterDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.magic.Cancellation;

public class Unicorn extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Unicorn();
		return MonsterInstance.clone(mi, m);
	}
	
	@Override
	public void toDamage(Character cha, int dmg, int type, Object...opt){
		super.toDamage(cha, dmg, type, opt);
		
		if(type==Lineage.ATTACK_TYPE_MAGIC && opt!=null && opt.length>0) {
			Class<?> c = (Class<?>)opt[0];
			if(c.isAssignableFrom(Cancellation.class)) {
				//
				int find_gfx = getMonster().getGfx()==2755 ? 2755 : 2755;
				Monster mon = MonsterDatabase.findGfx(find_gfx);
				if(mon != null) {
					// monster 객체 변경.
					setMonster(mon);
					// gfx 변경.
					setGfx(mon.getGfx());
					setGfxMode(mon.getGfxMode());
					// 드랍 목록 인벤토리에 갱신.
//					readDrop(-1);
					// 패킷 처리.
					toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), this), false);
				}
			}
		}
	}

}
