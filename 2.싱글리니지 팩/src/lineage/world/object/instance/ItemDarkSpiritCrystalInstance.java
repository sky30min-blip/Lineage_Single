package lineage.world.object.instance;

import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.object.Character;

public class ItemDarkSpiritCrystalInstance extends ItemBookInstance {

	static synchronized public ItemInstance clone(ItemInstance item, int skill_level, int skill_number){
		if(item == null)
			item = new ItemDarkSpiritCrystalInstance();
		item.setSkill(SkillDatabase.find(skill_level, skill_number));
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(isLevel(cha)){
			onMagic(cha);
			return;
		}
		
		// \f1아무일도 일어나지 않았습니다.
		if(cha instanceof PcInstance)
			cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79) );
	}

}
