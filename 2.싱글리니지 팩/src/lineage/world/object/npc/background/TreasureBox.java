package lineage.world.object.npc.background;

import lineage.bean.database.Item;
import lineage.database.AccountDatabase;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CharacterController;
import lineage.world.controller.CraftController;
import lineage.world.object.Character;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.ItemInstance;

public class TreasureBox extends BackgroundInstance {
	
	private int SLEEP_TIME = Lineage.boxspawn;	// 15
	private int current_time = 0;
	
	public TreasureBox(){
		CharacterController.toWorldJoin(this);
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 닫혀있을때만 처리.
		if(gfxMode == 29){
			// 상자 열기.
			this.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class),this, 6082), true);
			toOn();
			toSend();
			// 아이템 지급
			int count = 0;
			Item item = null;
			

			count = Util.random(1, Lineage.tr_gift);
			item = ItemDatabase.find("베릴");
	
			ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("보물상자 획득 점수"));
			ii.setCount(1);
			cha.toGiveItem(null, ii, ii.getCount());

			setName("보물상자");
			CraftController.toCraft(this, cha, item, count, true);
			setName("");
		}
	}
	
	@Override
	public void toTimer(long time){
		if(gfxMode == 28){
			if(current_time++ >= SLEEP_TIME){
				current_time = 0;
				// 상자 닫기.
				toOff();
				toSend();
				Util.toRndLocation(this);
				toTeleport(getHomeX(), getHomeY(), getHomeMap(), true);
			}
		}

	}
	
	public void toOn(){
		setGfxMode( 28 );
		
		
	}
	
	public void toOff(){
		setGfxMode( 29 );

	}
	
	public void toSend(){
		toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
	}

}
