package lineage.world.object.npc.background;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Agit;
import lineage.bean.lineage.Auction;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.AgitController;
import lineage.world.controller.AuctionController;
import lineage.world.object.Character;
import lineage.world.object.instance.BackgroundInstance;

public class Sign extends BackgroundInstance {
	
	private Agit agit;
	private List<String> list_html;
	
	public Sign(){
		list_html = new ArrayList<String>();
	}
	
	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		super.toTeleport(x, y, map, effect);
		
		// 아지트쪽에 영향을 주는 푯말인지 확인.
		agit = AgitController.find("sign", x, y);
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(agit != null){
			list_html.clear();
			Auction a = AuctionController.find(agit.getUid());
			if(a.isSell()){
				list_html.add(agit.getAgitName());
				cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "agnoname", null, list_html));
			}else{
				list_html.add(agit.getClanName());
				list_html.add(agit.getChaName());
				list_html.add(agit.getAgitName());
				cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "agname", null, list_html));
			}
		} else {
			list_html.clear();
			if (getGfx() == 1557) {
				list_html.add(Lineage.fish_rice);
				list_html.add(String.valueOf(Lineage.fish_delay));
				list_html.add(Lineage.fish_exp);
				list_html.add(String.valueOf(Lineage.auto_fish_level));
				list_html.add(Lineage.auto_fish_coin);
				list_html.add(String.valueOf(Lineage.auto_fish_expense));
				// 낚시 안내
				cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fishing", null, list_html));
			}
		}
	}
	
}
