package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class HarborMaster extends ShopInstance {
	
	public HarborMaster(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(getMap() == 0){
			// 말하는섬 선착장
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "shipEvI3"));
		}else{
			// 글루디오 선착장
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "shipEvM1"));
		}
	}
	
	@Override
	public int getTax(){
		// 선착장은 세율 적용 안하는듯.
		return 0;
	}
	
}
