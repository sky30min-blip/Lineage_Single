package lineage.world.object.monster.quest;

import lineage.bean.database.Monster;
import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CharacterController;
import lineage.world.controller.SkillController;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.magic.Tornado;

public class DarkElf extends MonsterInstance {
	
	// 토네이도
	static private Skill skill;
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new DarkElf();
		return MonsterInstance.clone(mi, m);
	}
	
	public DarkElf(){
		CharacterController.toWorldJoin(this);
		skill = SkillDatabase.find(7, 4);
		dynamic_attack_area = skill.getRange();
	}
	
	@Override
	public void close(){
		super.close();
		CharacterController.toWorldOut(this);
	}

	@Override
	public void readDrop(int map){
		// 퀘스트 맵에 잇는 녀석들은 드랍 무시.
//		if(getMap()<209 || getMap()>216){
//			super.readDrop(map);
//		}
	}
	
	static public void toAttack(MonsterInstance mi, object o, int x, int y, boolean bow, long attackTime){
		if( SkillController.isHpMpCheck(mi, skill.getHpConsume(), skill.getMpConsume()) && Util.random(0, 5)==0 ){
			// 액션 취하는 딜레이 주기.
			mi.setAiTime( SpriteFrameDatabase.getGfxFrameTime(mi, mi.getGfx(), mi.getGfxMode()+Lineage.GFX_MODE_SPELL_DIRECTION) );
			// 마법 시전.
			Tornado.init(mi, skill);
		}else{
			if(Util.isDistance(mi, o, mi.getMonster().getAtkRange())){
				mi.toAttack(o, x, y, bow, mi.getGfxMode()+Lineage.GFX_MODE_ATTACK, 0, false);
			}else{
				mi.setAiTime( SpriteFrameDatabase.getGfxFrameTime(mi, mi.getGfx(), mi.getGfxMode()+Lineage.GFX_MODE_WALK) );
				mi.toMoving(o, o.getX(), o.getY(), 0, true);
			}
		}
	}

}
