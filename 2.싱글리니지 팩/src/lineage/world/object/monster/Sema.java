package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Sema extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Sema();
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
	
	/**
	 * 세마: 네가 아무리 그분과 친분이 있어도 나를 막을 수는 없을 것이다!
	 */
	@Override
	public void toAiAttack(long time) {
	    if (!HP90 && getNowHp() <= getTotalHp() * 0.9) {
	        ChattingController.toChatting(this, "네가 아무리 그분과 친분이 있어도 나를 막을 수는 없을 것이다!", Lineage.CHATTING_MODE_SHOUT);
	        HP90 = true;
	        return;
	    }

	    if (!HP40 && getNowHp() <= getTotalHp() * 0.8) {
	        ChattingController.toChatting(this, "네가 아무리 그분과 친분이 있어도 나를 막을 수는 없을 것이다!", Lineage.CHATTING_MODE_SHOUT);
	        HP40 = true;
	        return;
	    }

	    if (!HP10 && getNowHp() <= getTotalHp() * 0.1) {
	        ChattingController.toChatting(this, "네가 아무리 그분과 친분이 있어도 나를 막을 수는 없을 것이다!", Lineage.CHATTING_MODE_SHOUT);
	        HP10 = true;
	        return;
	    }

	    super.toAiAttack(time);
	}

	@Override
	protected void toAiWalk(long time){
		super.toAiWalk(time);
		if(toSearchHuman())
			 ChattingController.toChatting(this, "그는 관대하시니 너를 용서하실 것이라...빨리 나오지 못하겠는가!", Lineage.CHATTING_MODE_SHOUT);
		return;
	}

	@Override
	public boolean isAttack(Character cha, boolean magic) {
		if(getGfxMode() != getClassGfxMode())	
			return false;
		return super.isAttack(cha, magic);
	}
}
