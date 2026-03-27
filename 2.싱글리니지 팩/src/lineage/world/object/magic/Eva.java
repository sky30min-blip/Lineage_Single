package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Eva extends Magic {

	public Eva(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new Eva(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffEva(true);
		//o.toSender(S_BuffEva.clone(BasePacketPooling.getPool(S_BuffEva.class), o, getTime()));
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setBuffEva(false);
		//o.toSender(S_BuffEva.clone(BasePacketPooling.getPool(S_BuffEva.class), o, 0));
	}
	
	static public void init(Character cha, int time){
		BuffController.append(cha, Eva.clone(BuffController.getPool(Eva.class), SkillDatabase.find(202), time));
	}

}
