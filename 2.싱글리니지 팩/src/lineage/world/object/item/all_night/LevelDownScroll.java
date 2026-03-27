package lineage.world.object.item.all_night;

import lineage.bean.database.Exp;
import lineage.database.ExpDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.world.controller.CharacterController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class LevelDownScroll extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new LevelDownScroll();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		cha.getInventory().count(this, getCount()-1, true);
		
		if(cha.getLevel() > 1) {
			Exp e = ExpDatabase.find(cha.getLevel() - 1);
			// 레벨 하향
			cha.setLevel(e.getLevel());
			// 경험치 하향.
			if(cha.getLevel() == 1)
				cha.setExp( 0 );
			else
				cha.setExp( e.getBonus()-e.getExp() );
			// hp & mp 하향.
			cha.setMaxHp( cha.getMaxHp()-CharacterController.toStatusUP(cha, true) );
			cha.setMaxMp( cha.getMaxMp()-CharacterController.toStatusUP(cha, false) );
			// 패킷 처리.
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}

}
