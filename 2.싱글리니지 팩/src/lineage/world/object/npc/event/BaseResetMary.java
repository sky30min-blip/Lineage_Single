package lineage.world.object.npc.event;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.EventInstance;
import lineage.world.object.instance.PcInstance;

public class BaseResetMary extends EventInstance {
	
	public BaseResetMary(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "candleg1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("0")){
			/*// 촛불 체크
			if(cha.getInventory().getItemIdType(808, 0)==null){
				ItemInstance item = Items.getInstance().newItem(808, 0, false);
				cha.getInventory().Controler(item, Config.ITEM_ADD, 1);
				cha.SendPacket(new MessagePacket(143, get_name(), item.get_name()), false);
				cha.SendPacket(new ShowHtmlPacket(get_objectId(), "candleg2"), false);
			}else{
				cha.SendPacket(new ShowHtmlPacket(get_objectId(), "candleg3"), false);
			}*/
		}
	}
	
}
