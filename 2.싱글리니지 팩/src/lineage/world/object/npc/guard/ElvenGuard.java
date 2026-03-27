package lineage.world.object.npc.guard;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class ElvenGuard extends PatrolGuard {
	
	public ElvenGuard(Npc npc){
		super(npc);
	}
	
	/**
	 * 요정외에 타 클레스 찾아서 공격목록에 넣는 함수.
	 */
	private boolean toSearchHuman(){
		boolean find = false;
		for(object o : getInsideList()){
			if(o instanceof PcInstance){
				PcInstance pc = (PcInstance)o;
				if(isAttack(pc, true) && pc.getClassType()!=Lineage.LINEAGE_CLASS_ELF){
					addAttackList(pc);
					find = true;
				}
			}
		}
		return find;
	}
	
	@Override
	public void toDamage(Character cha, int dmg, int type, Object...opt){
		// 데미지 처리.
		if(type==Lineage.ATTACK_TYPE_DIRECT || cha.getInventory()==null || cha.getInventory().getSlot(Lineage.SLOT_WEAPON)!=null || (cha.getClassType()!=Lineage.LINEAGE_CLASS_ELF && cha.getGm()==0)){
			super.toDamage(cha, dmg, type);
			return;
		}
		
		// 채집 처리
		if(cha instanceof PcInstance)
			toGatherUp((PcInstance)cha);
	}
	
	@Override
	protected void toAiWalk(long time){
		super.toAiWalk(time);
		
		// 요정외 타 클레스 찾기.
		if(toSearchHuman())
			ChattingController.toChatting(this, "$804", Lineage.CHATTING_MODE_SHOUT);
		
		// 요정숲 밖이라면 스폰된 위치로 텔레포트 하기.
		if( getMap()!=4 || ((getX()>=33216 || getX()<=32960) && (getY()>=32511 || getY()>32191)) )
			toTeleport(getHomeX(), getHomeY(), getHomeMap(), true);
	}
	
	@Override
	public void toAiAttack(long time) {
		super.toAiAttack(time);
		
		// 요정숲 밖이라면 스폰된 위치로 텔레포트 하기.
		if( getMap()!=4 || ((getX()>=33216 || getX()<=32960) && (getY()>=32511 || getY()>32191)) )
			toTeleport(getHomeX(), getHomeY(), getHomeMap(), true);
	}
	
	/**
	 * 채집 처리 담당 함수.
	 * @param cha
	 */
	protected void toGatherUp(PcInstance pc){ }
}
