package lineage.world.object.item.scroll;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.magic.EnchantWeapon;

public final class SpellScrollEnchantWeapon extends ItemInstance {
	
	private Skill skill;
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SpellScrollEnchantWeapon();
		item.setSkill( SkillDatabase.find(2, 3) );
		return item;
	}
	
	@Override
	public Skill getSkill() {
		return skill;
	}

	@Override
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 처리불가능한 패킷상태는 무시.
		if(!cbp.isRead(4))
			return;
		// 초기화
		object o = cha.getInventory().value(cbp.readD());
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(o!=null && o instanceof ItemWeaponInstance){
			if(SkillController.isDelay(cha, skill) && SkillController.isMagic(cha, skill, false)){
				// 처리
				EnchantWeapon.onBuff(cha, o, skill, skill.getBuffDuration());
				// 수량 하향
				cha.getInventory().count(this, getCount()-1, true);
			}
		}else{
			// \f1아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
		}
	}

}
