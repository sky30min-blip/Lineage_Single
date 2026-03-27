package lineage.world.object.instance;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.DamageController;
import lineage.world.controller.ElvenforestController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;

public class ItemBookInstance extends ItemInstance {

	private Skill skill;
	
	static synchronized public ItemInstance clone(ItemInstance item, int skill_level, int skill_number){
		if(item == null)
			item = new ItemBookInstance();
		item.setSkill(SkillDatabase.find(skill_level, skill_number));
		return item;
	}
	
	@Override
	public void setSkill(Skill skill){
		this.skill = skill;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory()!=null && isLevel(cha)) {
			if(isChaoticZone(cha) || isLawfulZone(cha) || isTreeZone(cha) || isTowerZone(cha)) {
				// 특정 존 안에 잇을때.
				if(item.isBookChaoticZone()) {
					if(isChaoticZone(cha)) {
						onMagic(cha);
						return;
					}
				} else if(item.isBookLawfulZone() || item.isBookNeutralZone()) {
					if(isLawfulZone(cha)) {
						onMagic(cha);
						return;
					}
				} else if(item.isBookMomtreeZone()) {
					if(isTreeZone(cha)) {
						onMagic(cha);
						return;
					}
				} else if(item.isBookTowerZone()) {
					if(isTowerZone(cha)) {
						onMagic(cha);
						return;
					}
				}
				// 콜라이트닝 데미지 적용.
				DamageController.toDamage(cha, cha, Util.random(30, 60), 2);
				cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 10), true);
				return;
				
			} else {
				// 아무필드에나 잇을때.
				if(item.isBookChaoticZone() || item.isBookLawfulZone() || item.isBookNeutralZone() || item.isBookMomtreeZone() || item.isBookTowerZone()) {
					// 존체크해야하는 마법책일 경우.
				} else {
					// 존체크 안하는 마법책일 경우.
					onMagic(cha);
					return;
				}
			}
		}

		// \f1아무일도 일어나지 않았습니다.
		if(cha instanceof PcInstance)
			cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79) );
	}

	/**
	 * 마법책을 습득할수 있는 레벨인지 확인하는 메서드.
	 */
	protected boolean isLevel(Character cha) {
		switch (cha.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL: // 군주
			if (item.getRoyal() > 0 && cha.getLevel() < item.getRoyal())
				ChattingController.toChatting(cha, String.format("%s레벨 이상 습득가능", item.getRoyal()), Lineage.CHATTING_MODE_MESSAGE);

			return item.getRoyal() > 0 && cha.getLevel() >= item.getRoyal();
		case Lineage.LINEAGE_CLASS_KNIGHT: // 기사
			if (item.getKnight() > 0 && cha.getLevel() < item.getKnight())
				ChattingController.toChatting(cha, String.format("%s레벨 이상 습득가능", item.getKnight()), Lineage.CHATTING_MODE_MESSAGE);

			return item.getKnight() > 0 && cha.getLevel() >= item.getKnight();
		case Lineage.LINEAGE_CLASS_ELF: // 요정
			if (item.getElf() > 0 && cha.getLevel() < item.getElf())
				ChattingController.toChatting(cha, String.format("%s레벨 이상 습득가능", item.getElf()), Lineage.CHATTING_MODE_MESSAGE);

			return item.getElf() > 0 && cha.getLevel() >= item.getElf();
		case Lineage.LINEAGE_CLASS_WIZARD:
			if (item.getWizard() > 0 && cha.getLevel() < item.getWizard())
				ChattingController.toChatting(cha, String.format("%s레벨 이상 습득가능", item.getWizard()), Lineage.CHATTING_MODE_MESSAGE);

			return item.getWizard() > 0 && cha.getLevel() >= item.getWizard();
		case Lineage.LINEAGE_CLASS_DARKELF:
			if (item.getDarkElf() > 0 && cha.getLevel() < item.getDarkElf())
				ChattingController.toChatting(cha, String.format("%s레벨 이상 습득가능", item.getDarkElf()), Lineage.CHATTING_MODE_MESSAGE);
			
			return item.getDarkElf() > 0 && cha.getLevel() >= item.getDarkElf();
		case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
			return item.getDragonKnight() > 0 && cha.getLevel() >= item.getDragonKnight();
		case Lineage.LINEAGE_CLASS_BLACKWIZARD:
			return item.getBlackWizard() > 0 && cha.getLevel() >= item.getBlackWizard();
		}
		return true;
	}

	/**
	 * 카우틱 신전
	 */
	protected boolean isChaoticZone(Character cha){
		return 
			(cha.getX()>=Lineage.CHAOTICZONE1_X1 && cha.getX()<=Lineage.CHAOTICZONE1_X2 && cha.getY()>=Lineage.CHAOTICZONE1_Y1 && cha.getY()<=Lineage.CHAOTICZONE1_Y2) || 
			(cha.getX()>=Lineage.CHAOTICZONE2_X1 && cha.getX()<=Lineage.CHAOTICZONE2_X2 && cha.getY()>=Lineage.CHAOTICZONE2_Y1 && cha.getY()<=Lineage.CHAOTICZONE2_Y2) ;
	}

	/**
	 * 라우풀 신전
	 */
	protected boolean isLawfulZone(Character cha){
		return 
			(cha.getX()>=Lineage.LAWFULLZONE1_X1 && cha.getX()<=Lineage.LAWFULLZONE1_X2 && cha.getY()>=Lineage.LAWFULLZONE1_Y1 && cha.getY()<=Lineage.LAWFULLZONE1_Y2) || 
			(cha.getX()>=Lineage.LAWFULLZONE2_X1 && cha.getX()<=Lineage.LAWFULLZONE2_X2 && cha.getY()>=Lineage.LAWFULLZONE2_Y1 && cha.getY()<=Lineage.LAWFULLZONE2_Y2) ;
	}

	/**
	 * 엄마나무
	 */
	protected boolean isTreeZone(Character cha){
		return ElvenforestController.isTreeZone(cha);
	}
	
	/**
	 * 상아탑 타워 인지
	 * @param cha
	 * @return
	 */
	protected boolean isTowerZone(Character cha){
		return true;
	}

	/**
	 * 마법 습득을 해도될경우 처리하는 메서드.
	 */
	protected void onMagic(Character cha){
		if(skill == null)
			return;
		
		if(SkillController.find(cha, skill.getUid(), false) == null){
			SkillController.find(cha).add( skill );
			SkillController.sendList(cha);
		} else {
			ChattingController.toChatting(cha, "해당 마법은 이미 습득하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		if (item.getEffect() > 0)
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, item.getEffect()), true);
		
		if(cha.getInventory() != null)
			cha.getInventory().count(this, getCount()-1, true);
	}
	
}
