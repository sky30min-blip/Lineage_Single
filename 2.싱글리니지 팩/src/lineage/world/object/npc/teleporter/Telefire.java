package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Telefire extends TeleportInstance {
	
	public Telefire(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		switch(getNpc().getNameIdNumber()){
		case 1072: // 은기사마을
			if(pc.getLawful() < Lineage.NEUTRAL) 
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bunch2"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bunch1"));
			break;
		case 1071: // 우즈벡
			if(pc.getLawful() < Lineage.NEUTRAL) 
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Cobb2"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Cobb1"));
			break;
		case 1070: // 글루딘
			if(pc.getLawful() < Lineage.NEUTRAL) 
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Hooper2"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Hooper1"));
			break;
		case 1068: // 말하는섬
			if(pc.getLawful() < Lineage.NEUTRAL) 
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "case2"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "case1"));
			break;
		case 1069: // 켄트
			if(pc.getLawful() < Lineage.NEUTRAL) 
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "harrison2"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "harrison1"));
			break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    String htmlPage = null;
	    switch (getNpc().getNameIdNumber()){
	        case 1072: // 은기사마을
	            htmlPage = "bunch3";
	            break;
	        case 1071: // 우즈벡
	            htmlPage = "Cobb3";
	            break;
	        case 1070: // 글루딘
	            htmlPage = "Hooper3";
	            break;
	        case 1068: // 말하는섬
	            htmlPage = "case3";
	            break;
	        case 1069: // 켄트
	            htmlPage = "harrison3";
	            break;
	        default:
	            super.toTalk(pc, action, type, cbp);
	            return;
	    }
	    
	    if (action.equalsIgnoreCase("teleportURL")) {
	        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, htmlPage, null, list.get(0)));
	    } else {
	        super.toTalk(pc, action, type, cbp);
	    }
	}
}