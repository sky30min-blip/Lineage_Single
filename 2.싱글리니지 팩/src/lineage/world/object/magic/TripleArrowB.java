package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class TripleArrowB {

	static public void init(Character cha, Skill skill, int object_id, int x, int y) {
		// 인벤토리에 화살이 있는지 체크하여 화살 객체를 가져옴
		ItemInstance arrow = cha.getInventory().findArrow();
		ItemInstance weapon = cha.getInventory().getSlot(Lineage.SLOT_WEAPON);
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		// 투망상태 해제
		Detection.onBuff(cha);
		
		int count = 3;
		
		if (o == null)
			return;

		if (weapon != null && weapon.getItem().getType2().equalsIgnoreCase("bow")) {
			if (!World.isAttack(cha, o) || !Util.isDistance(cha, o, 8)) 
				return;
			// 화살이 없을경우, 사이하 활을 착용하고 있으면 사이하 이펙트로 데미지
			if (arrow == null) {
				if (weapon.getItem().getNameIdNumber() == 1821) {
					if(o!=null && SkillController.isMagic(cha, skill, true)) {
						cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);	
						for ( int i = 0 ; i < 4 ; i++ )
							cha.toAttack(o, x, y, true, Lineage.ACTION_TRIPLE_ARROW_1, 0, true);
					}
				} else {
					if(o!=null && SkillController.isMagic(cha, skill, true)) {
						cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
						for (int i=0 ; i < 4 ; ++i)
							cha.toAttack(o, x, y, true, Lineage.ACTION_TRIPLE_ARROW_1, 0, true);		
					}
					ChattingController.toChatting(cha, "화살이 부족합니다. 화살을 무장 하십시오.", Lineage.CHATTING_MODE_MESSAGE);
				}
				return;
			} else {
				if(o!=null && SkillController.isMagic(cha, skill, true)) {
					cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
					for ( int i = 0 ; i < 4 ; i++ )
						cha.toAttack(o, x, y, true, Lineage.ACTION_TRIPLE_ARROW_1, 0, true);
				}
			}
		} else {
			ChattingController.toChatting(cha, "원거리 무기 착용시 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}	

