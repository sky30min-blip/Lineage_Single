package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Necromancer extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Necromancer();
		return MonsterInstance.clone(mi, m);
	}

	/**
	 * hp에 따른 멘트 출력
	 */
	private boolean HP90 = false;
	private boolean HP40 = false;
	private boolean HP10 = false;
	
	/**
	 * 클레스 찾아서 공격목록에 넣는 함수.
	 */
	private boolean toSearchHuman(){
		boolean find = false;
		for(object o : getInsideList(true)){
			if(o instanceof PcInstance){
				PcInstance pc = (PcInstance)o;
				if(isAttack(pc, true)){
					addAttackList(pc);
					find = true;
				}
			}
		}
		return find;
	}
	
	@Override
	public void toAiAttack(long time) {
	    if (!HP90 && getNowHp() <= getTotalHp() * 0.9) {
	        ChattingController.toChatting(this, "위대한 마력의 힘을 견뎌 보아라", Lineage.CHATTING_MODE_SHOUT);
	        HP90 = true;
	        return;
	    }

	    if (!HP40 && getNowHp() <= getTotalHp() * 0.8) {
	        ChattingController.toChatting(this, "윈대한 마력의 소유자님을 위하여...", Lineage.CHATTING_MODE_SHOUT);
	        HP40 = true;
	        return;
	    }

	    if (!HP10 && getNowHp() <= getTotalHp() * 0.1) {
	        ChattingController.toChatting(this, "이곳에서 너희의 영혼을 거두어주마", Lineage.CHATTING_MODE_SHOUT);
	        HP10 = true;
	        return;
	    }

	    super.toAiAttack(time);
	}

	@Override
	protected void toAiWalk(long time){
		super.toAiWalk(time);
		if(toSearchHuman())
		return;
	}

	@Override
	public boolean isAttack(Character cha, boolean magic) {
		if(getGfxMode() != getClassGfxMode())	
			return false;
		return super.isAttack(cha, magic);
	}
}
