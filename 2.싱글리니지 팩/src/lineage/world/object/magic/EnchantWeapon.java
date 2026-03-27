package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;

public class EnchantWeapon extends Magic {
	
	public EnchantWeapon(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new EnchantWeapon(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffEnchantWeapon(true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffEnchantWeapon(false);
		if(o instanceof ItemInstance){
			ItemInstance weapon = (ItemInstance)o;
			if(weapon.getCharacter() != null) {
				// 무기가 보통으로 돌아왔습니다.
				ChattingController.toChatting(weapon.getCharacter(), String.format("+%d %s 보통으로 돌아왔습니다.", weapon.getEnLevel(), weapon.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(weapon.getCharacter(), "\\fY인챈트 웨폰 종료", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	
	@Override
	public void toBuff(object o) {
		if(o instanceof ItemInstance && getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min){
			ItemInstance weapon = (ItemInstance)o;
			if(weapon.isEquipped() && weapon.getCharacter()!=null && !weapon.getCharacter().isWorldDelete()){
				ChattingController.toChatting(weapon.getCharacter(),"\\fY인챈트 웨폰: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.getInventory().value(object_id);
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true) && o instanceof ItemWeaponInstance)
				onBuff(cha, o, skill, skill.getBuffDuration());
		}
	}

	/**
	 * 중복코드 방지용
	 * @param cha
	 * @param o
	 * @param skill
	 * @param time
	 */
	static public void onBuff(Character cha, object o, Skill skill, int time){
		if(cha!=null && o!=null && skill!=null){
			ItemWeaponInstance weapon = (ItemWeaponInstance) o;
			if (weapon.getItem().getName().contains("화살"))
				return;
			// 홀리웨폰 삭제
			BuffController.remove(o, HolyWeapon.class);
			// 블레스 웨폰
			if (BuffController.find(o, SkillDatabase.find(48)) != null) {
				ChattingController.toChatting(cha, "\\fY무기에 블레스 웨폰 효과가 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			// 버프 등록
			BuffController.append(o, EnchantWeapon.clone(BuffController.getPool(EnchantWeapon.class), skill, time));		
			// 패킷 처리
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			ChattingController.toChatting(cha, String.format("+%d %s 한 순간 파랗게 빛납니다.", weapon.getEnLevel(), weapon.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(cha, "인챈트 웨폰: 근거리 대미지+2", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
}
