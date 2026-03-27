package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;


	public class Kouts extends MonsterInstance {
		
		
		static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
			if(mi == null)
				mi = new  Kouts();
			return MonsterInstance.clone(mi, m);
		}
		
		private boolean HP90 = false;
		private boolean HP80 = false;
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
		        ChattingController.toChatting(this, "이 건방진 것들이 어디서...!", Lineage.CHATTING_MODE_SHOUT);
		        HP90 = true;
		        return;
		    }

		    if (!HP80 && getNowHp() <= getTotalHp() * 0.8) {
		        ChattingController.toChatting(this, "...한번 보고 상대방의 실력을 파악하지 못하다니, 불쌍한 자들...", Lineage.CHATTING_MODE_SHOUT);
		        HP80 = true;
		        return;
		    }

		    if (!HP10 && getNowHp() <= getTotalHp() * 0.1) {
		        ChattingController.toChatting(this, "커헉!", Lineage.CHATTING_MODE_SHOUT);
		        HP10 = true;
		        return;
		    }

		    super.toAiAttack(time);
		}

		@Override
		protected void toAiWalk(long time){
			super.toAiWalk(time);
			
			if(toSearchHuman())
				ChattingController.toChatting(this, "수상한 자는 없는 것인가.", Lineage.CHATTING_MODE_SHOUT);
			return;
		}

		@Override
		public boolean isAttack(Character cha, boolean magic) {
			if(getGfxMode() != getClassGfxMode())	
				return false;
			return super.isAttack(cha, magic);
		}
	}
		
