package lineage.world.object.item.scroll;

import lineage.bean.database.Item;
import lineage.bean.database.Skill;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class BlankScroll extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new BlankScroll();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		int skill_uid = cbp.readC()+1;
		Skill skill = SkillDatabase.find(skill_uid);
		ItemInstance temp = null;
		
		// 마법사 클레스가 아니라면 무시.
		if(cha.getClassType() != Lineage.LINEAGE_CLASS_WIZARD)
			return;
		// 존재하지 않는 스킬이거나 스킬관리클레스가 초기화되지 않앗을때.
		if(skill == null)
			return;
		// 가지고 있지 않는 스킬이라면.
		if(SkillController.find(cha, skill.getSkillLevel(), skill.getSkillNumber()) == null)
			return;
		// 1단계 주문서 레벨 체크.
		if(item.getNameIdNumber()==1486 && skill.getSkillLevel()>1){
			// \f1스크롤이 그런 강한 마법을 기록하기에는 너무나 약합니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 591));
			return;
		}
		// 2단계 주문서 레벨 체크.
		if(item.getNameIdNumber()==1892 && skill.getSkillLevel()>2){
			// \f1스크롤이 그런 강한 마법을 기록하기에는 너무나 약합니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 591));
			return;
		}
		// 3단계 주문서 레벨 체크.
		if(item.getNameIdNumber()==1893 && skill.getSkillLevel()>3){
			// \f1스크롤이 그런 강한 마법을 기록하기에는 너무나 약합니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 591));
			return;
		}
		// 4단계 주문서 레벨 체크.
		if(item.getNameIdNumber()==1894 && skill.getSkillLevel()>4){
			// \f1스크롤이 그런 강한 마법을 기록하기에는 너무나 약합니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 591));
			return;
		}
		// 5단계 주문서 레벨 체크.
		if(item.getNameIdNumber()==1895 && skill.getSkillLevel()>5){
			// \f1스크롤이 그런 강한 마법을 기록하기에는 너무나 약합니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 591));
			return;
		}
		// hp 체크
		if(cha.getNowHp()<skill.getHpConsume()){
			// \f1HP가 부족하여 마법을 사용할 수 없습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 279));
			return;
		}
		// mp 체크
		if(cha.getNowMp()<skill.getMpConsume()){
			// \f1MP가 부족하여 마법을 사용할 수 없습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 278));
			return;
		}
		// 재료 체크
		if(skill.getItemConsume()>0){
			temp = cha.getInventory().findDbNameId(skill.getItemConsume());
			if(temp==null || temp.getCount()<skill.getItemConsumeCount()){
				// \f1마법 사용에 필요한 재료가 부족합니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 299));
				return;
			}
		}

		// 처리.
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		cha.setNowHp( cha.getNowHp()-skill.getHpConsume() );
		cha.setNowMp( cha.getNowMp()-skill.getMpConsume() );
		cha.setLawful( cha.getLawful()-skill.getLawfulConsume() );
		cha.getInventory().count(this, getCount()-1, true);
		if(temp != null)
			cha.getInventory().count(temp, temp.getCount()-skill.getItemConsumeCount(), true);
		// 추가.
		StringBuffer sb = new StringBuffer();
		sb.append("spellscroll_");
		sb.append(skill.getUid());
		Item i = ItemDatabase.find("item", sb.toString());
		if(i != null){
			temp = cha.getInventory().find(i.getName(), 1, i.isPiles());
			if(temp == null){
				temp = ItemDatabase.newInstance(i);
				temp.setObjectId( ServerDatabase.nextItemObjId() );
				cha.getInventory().append( temp, true );
			}else{
				cha.getInventory().count(temp, temp.getCount()+1, true);
			}
		}
	}
}
