package lineage.world.object.npc.kingdom;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.SummonController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class KingdomSoldierShop extends object {

	protected Npc npc;
	protected Kingdom kingdom;
	protected List<String> list_html;
	protected String html;
	protected String soldier_name;
	
	public KingdomSoldierShop(Npc npc){
		this.npc = npc;
		list_html = new ArrayList<String>();
		html = null;
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(kingdom.getClanId()==0 || kingdom.getClanId()!=pc.getClanId()){
			if(pc.getGm()==0){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html+"4"));
				return;
			}
		}
		
		list_html.clear();
		list_html.add(pc.getName());
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html+"1", null, list_html));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(kingdom==null || kingdom.getClanId()==0 || kingdom.getClanId()!=pc.getClanId()) {
			if(pc.getGm() == 0)
				return;
		}
		
		if(action.equalsIgnoreCase("demand")){
			// 용병 고용
			if(Lineage.server_version <= 230){
				// 구버전 패턴
				// 카리에 맞춰서 만들어서 등록.
				if(pc.getInventory().isAden(Lineage.kingdom_soldier_price, true))
					SummonController.toSoldier(pc, 60 * 60, soldier_name);
				else
					// \f1아데나가 충분치 않습니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 740));
			}else{
				// 상위버전 패턴.
			}
		}
		
	}
	
}
