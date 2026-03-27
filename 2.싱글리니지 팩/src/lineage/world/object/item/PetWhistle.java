package lineage.world.object.item;

import lineage.bean.lineage.Summon;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_SoundEffect;
import lineage.world.controller.SummonController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.SummonInstance.SUMMON_MODE;

public class PetWhistle extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new PetWhistle();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 사운드 재생.
		cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), getItem().getEffect()), true);
		// 펫 모드 변경.
		Summon s = SummonController.find(cha);
		if(s != null)
			s.setModePet(SUMMON_MODE.Call);
	}

}
