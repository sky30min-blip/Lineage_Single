package lineage.world.object.item.scroll;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.CounterMagic;

public final class SpellScrollCounterMagic extends ItemInstance {
	
	private Skill skill;
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SpellScrollCounterMagic();
		item.setSkill( SkillDatabase.find(4, 6) );
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
		
		if(cha.getMap() == 807){
			ChattingController.toChatting(cha, "여기서는 사용 하실 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isDelay(cha, skill) && SkillController.isMagic(cha, skill, false)){
			// 처리
			CounterMagic.onBuff(cha, skill, skill.getBuffDuration());
			// 수량 하향
			cha.getInventory().count(this, getCount()-1, true);
		}
	}

}
