package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Colosseum;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.controller.ColosseumController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class ColiseumManager extends TeleportInstance {

/*
	경기진행중 - colos1
	경기진행중이지 않고 대기상태일때 - colos2
	고득점자 명단 - colos3
	무한대전 참가 마감 - colos4
	경기참가원 받을때 - colosg
	무한대전 준비중 - colos
*/
	
	private Colosseum c;
	
	public ColiseumManager(Npc npc){
		super(npc);
	}
	
	@Override
	public void setTitle(String title) {
		c = ColosseumController.find(title);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(c == null)
			return;
		
		switch(c.getStatus()){
			case 휴식:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "colos2", null, c.getHtmlInfo()));
				break;
			case 대기:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "colos"));
				break;
			default:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "colos1"));
				break;
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		// 버그 방지.
		if(c.getStatus() != ColosseumController.COLOSSEUM_STATUS.대기)
			return;
		
		if(action.equalsIgnoreCase("ent")){
			// 무한대전에 참가한다.
			ColosseumController.toJoin(pc, c);
			
		}else if(action.equalsIgnoreCase("info")){
			// 무한대전 경기 정보를 확인한다.
			
		}else if(action.equalsIgnoreCase("sco")){
			// 고득점자 명단을 열람한다.
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "colos3", null, c.getHtmlTopRank()));
			
		}
	}
	
}
