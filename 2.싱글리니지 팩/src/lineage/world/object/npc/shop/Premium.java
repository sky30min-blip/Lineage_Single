package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Premium extends ShopInstance {
	
	public Premium(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		switch(getNpc().getNameIdNumber()){
			case 5138:	// 수상한 잡화 상인
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "suspicious1"));
				break;
			case 5324:	// 수상한 조련사
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "suspicious1d"));
				break;
			case 5742:	// 수상한 변신술사
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "suspicious1c"));
				break;
			case 5743:	// 수상한 무기 상인
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "suspicious1a"));
				break;
			case 5744:	// 수상한 갑옷 상인
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "suspicious1b"));
				break;
		}
	}

}
