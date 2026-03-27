package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.bean.database.Skill;
import lineage.share.Lineage;
import lineage.world.controller.SkillController;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class FloatingEye extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new FloatingEye();
		return MonsterInstance.clone(mi, m);
	}

	static public void toAiAttack(MonsterInstance mi, long time) {
		Skill skill = mi.getMonster().getSkillList().get(0);
		if (skill != null) {
			// hp & mp 확인하여 마법시전이 불가능하면 도망모드로 전환.
			if (!SkillController.isHpMpCheck(mi, skill.getHpConsume(), skill.getMpConsume())) {
				mi.setAiStatus(Lineage.AI_STATUS_ESCAPE);
				return;
			}
			// 전투목록자들이 전부 얼려잇는 상태라면 도망모드 전환.
			boolean all_cp = true;
			for (object o : mi.getAttackList()) {
				if (!o.isBuffCurseParalyze())
					all_cp = false;
			}
			if (all_cp) {
				mi.setAiStatus(Lineage.AI_STATUS_ESCAPE);
				return;
			}
		}
		// 이도저도 아닐경우 공격패턴으로 전환
		mi.toAiAttack(time);
	}

	static public void toAiEscape(MonsterInstance mi, long time) {
		Skill skill = mi.getMonster().getSkillList().get(0);
		if (skill != null) {
			// 전투목록자들중 얼리지 않은 놈이 잇다면 mp확인해서 전투모드 전환할지 확인.
			boolean all_cp = true;
			for (object o : mi.getAttackList()) {
				if (!o.isBuffCurseParalyze())
					all_cp = false;
			}
			if (!all_cp) {
				// hp & mp 확인하여 마법시전 가능할경우 공격모드로 전환.
				if (!SkillController.isHpMpCheck(mi, skill.getHpConsume(), skill.getMpConsume())) {
					mi.setAiStatus(Lineage.AI_STATUS_ATTACK);
					return;
				}
			}
		}
		// 도망가기 이행.
		mi.toAiEscape(time);
	}

}
