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

public class HolyWeapon extends Magic {

	public HolyWeapon(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill) {
		if (bi == null)
			bi = new HolyWeapon(skill);
		bi.setSkill(skill);
		bi.setTime(skill.getBuffDuration());
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffHolyWeapon(true);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		o.setBuffHolyWeapon(false);
		if (o instanceof ItemInstance) {
			ItemInstance weapon = (ItemInstance) o;
			if (weapon.getCharacter() != null) {
				ChattingController.toChatting(weapon.getCharacter(), String.format("+%d %s 보통으로 돌아왔습니다.", weapon.getEnLevel(), weapon.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(weapon.getCharacter(), "\\fY홀리 웨폰 종료", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	
	@Override
	public void toBuff(object o) {
		if(o instanceof ItemInstance && getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min){
			ItemInstance weapon = (ItemInstance)o;
			if(weapon.isEquipped() && weapon.getCharacter()!=null && !weapon.getCharacter().isWorldDelete()){
				ChattingController.toChatting(weapon.getCharacter(),"\\fY홀리 웨폰: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	static public void init(Character cha, Skill skill, int object_id) {
		// 초기화
		object o = null;
		// 타겟 찾기
		if (object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList(object_id);
		// 처리
		if (o != null && o instanceof Character) {
			if (o.getInventory() != null) {
				o = o.getInventory().getSlot(Lineage.SLOT_WEAPON);
				if (o == null) {
					ChattingController.toChatting(cha, "\\fY무기를 착용하고 있지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				if (o != null && o instanceof ItemWeaponInstance) {
					cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
					
					if (SkillController.isMagic(cha, skill, true))
						onBuff(o, skill);
				}
			}
		}
	}

	static public void onBuff(object o, Skill skill) {
		// 중복 버프 마법 우선순위 적용
		// 인챈트 웨폰
		if (BuffController.find(o, SkillDatabase.find(12)) != null) {
			ChattingController.toChatting(o, "\\fY무기에 인챈트 웨폰 효과가 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		// 블레스 웨폰
		if (BuffController.find(o, SkillDatabase.find(48)) != null) {
			ChattingController.toChatting(o, "\\fY무기에 블레스 웨폰 효과가 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		ItemWeaponInstance weapon = (ItemWeaponInstance) o;

		weapon.getCharacter().toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), weapon.getCharacter(), skill.getCastGfx()), true);
		BuffController.append(o, HolyWeapon.clone(BuffController.getPool(HolyWeapon.class), skill));

		ChattingController.toChatting(weapon.getCharacter(), String.format("+%d %s 한 순간 파랗게 빛납니다.", weapon.getEnLevel(), weapon.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
		ChattingController.toChatting(weapon.getCharacter(), "홀리 웨폰: 근거리 대미지+1, 근거리 명중+1, 언데드 몬스터 추가 대미지", Lineage.CHATTING_MODE_MESSAGE);
	}
}
