package lineage.world.object.item.scroll;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.ArmorBreak;
import lineage.world.object.magic.EnergyBolt;

public final class SpellScrollDestroy extends ItemInstance {
	
	private Skill skill;
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SpellScrollDestroy();
		item.setSkill( SkillDatabase.find(10, 4) );
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
		object o = cha.findInsideList(cbp.readD());
		if(o != null){
			if(SkillController.isDelay(cha, skill) && Util.isDistance(cha, o, 10) && SkillController.isMagic(cha, skill, false)){
				ArmorBreak.onBuff(o, skill, skill.getBuffDuration());
			}
		}else{
			// \f1아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
		}
	}

}
