package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffElf;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class StormShot extends Magic {

	public StormShot(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new StormShot(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		Character cha = (Character) o;
		cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 6);
		cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + 3);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		Character cha = (Character) o;
		cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 6);
		cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - 3);
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min);
	}
	
	static public void init(Character cha, Skill skill, long object_id){
		
		if (cha.getInventory().getSlot(Lineage.SLOT_WEAPON) == null || !cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("bow")) {
			ChattingController.toChatting(cha, "활을 착용해야 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		// 초기화
		object o = null;
		// 타겟 찾기
		if (Lineage.is_storm_shot_target) {
			if (object_id == cha.getObjectId())
				o = cha;
			else
				o = cha.findInsideList(object_id);
		} else {
			o = cha;
		}
		
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)){
				// 중복되지않게 다른 버프 제거.
				// 블레스 웨폰
				BuffController.remove(o, BlessWeapon.class);
				// 파이어 웨폰
				BuffController.remove(o, FireWeapon.class);
				// 윈드 샷
				BuffController.remove(o, WindShot.class);
				// 브레스 오브 파이어
				BuffController.remove(o, BlessOfFire.class);
				// 버닝 웨폰
				BuffController.remove(o, BurningWeapon.class);
				// 소울 오브 프레임
				BuffController.remove(o, SoulOfFlame.class);
				
				// 버프 등록
				BuffController.append(o, StormShot.clone(BuffController.getPool(StormShot.class), skill, skill.getBuffDuration()));
				
				// 패킷 처리
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			}
		}
	}
	
	static public void onBuff(object o, Skill skill) {
		// 버닝 웨폰
		BuffController.remove(o, BurningWeapon.class);
		
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		// 버프 등록
		BuffController.append(o, StormShot.clone(BuffController.getPool(StormShot.class), skill, skill.getBuffDuration()));
	}
	
}
