package lineage.world.object.npc.buff;

import lineage.bean.database.Skill;
import lineage.database.ItemDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.BlessedArmor;
import lineage.world.object.magic.EnchantWeapon;

public class ArmorEnchanter extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "enchantera1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
	    if(action.equalsIgnoreCase("enca")){
	    	
	    	pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창 닫기

	        ItemInstance armorSlot = pc.getInventory().getSlot(Lineage.SLOT_ARMOR);
	        if (armorSlot != null) {
	            if (pc.getInventory().isAden(100, true)) {
	            	Skill s = SkillDatabase.find(3, 4);
	                if(s != null) {
	                	BlessedArmor.onBuff(pc, armorSlot, s, s.getBuffDuration());
	                }
	            } else {
	                pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189)); 
	            }
	        } else {
	        	ChattingController.toChatting(pc, "방어구를 착용 하십시요.", Lineage.CHATTING_MODE_MESSAGE);
	        }
	    }
	}
}
