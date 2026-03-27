package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.Magic;

public class Wisdom extends Magic {

	public Wisdom(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new Wisdom(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setBuffWisdom(true);
			cha.setDynamicSp(cha.getDynamicSp()+2);
			cha.setDynamicTicMp(cha.getDynamicTicMp() + 2);
		}
		
		toBuffUpdate(o);
	}
	
	@Override
	public void toBuffUpdate(object o) {
		if(o instanceof Character){
			Character cha = (Character)o;
			if(Lineage.server_version > 200)
				cha.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), cha));
			else
				cha.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), cha, S_ObjectSpeed.BRAVE, 2, getTime()));
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setBuffWisdom(false);
			cha.setDynamicSp(cha.getDynamicSp()-2);
			cha.setDynamicTicMp(cha.getDynamicTicMp() - 2);
			if(Lineage.server_version > 200)
				cha.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), cha));
			cha.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), cha, S_ObjectSpeed.BRAVE, 0, 0));
		}
	}
	
	static public void init(Character cha, int time){

		// 홀리 제거
		BuffController.remove(cha, HolyWalk.class);
		// 지혜 적용
		BuffController.append(cha, Wisdom.clone(BuffController.getPool(Wisdom.class), SkillDatabase.find(203), time));
	}
}
