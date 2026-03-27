package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Party;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffElf;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.PartyController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class BlessOfFire extends Magic {
	
	public BlessOfFire(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new BlessOfFire(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffBlessOfFire(true);
		
		toBuffUpdate(o);
	}
	
	@Override
	public void toBuffUpdate(object o) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffBlessOfFire(false);
	}

	static public void init(Character cha, Skill skill){
		

		
		if(cha instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			Party p = PartyController.find(pc);
			if(p == null){
				onBuff(pc, skill);
				
			}else{
				for(PcInstance use : p.getList()){
					if(Util.isDistance(cha, use, Lineage.SEARCH_LOCATIONRANGE))
						onBuff(use, skill);
				}
			}
		}
	}
	
	static public void onBuff(PcInstance pc, Skill skill){
		
		// 중복되지않게 다른 버프 제거.
		// 블레스 웨폰
		BuffController.remove(pc, BlessWeapon.class);
		// 파이어 웨폰
		BuffController.remove(pc, FireWeapon.class);
		// 윈드 샷
		BuffController.remove(pc, WindShot.class);
		// 스톰샷
		BuffController.remove(pc, StormShot.class);
		// 버닝 웨폰
		BuffController.remove(pc, BurningWeapon.class);
		
		BuffController.append(pc, BlessOfFire.clone(BuffController.getPool(BlessOfFire.class), skill, skill.getBuffDuration()));
	}
}
