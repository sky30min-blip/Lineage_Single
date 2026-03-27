package lineage.world.object.instance;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ShopSell;
import lineage.share.Lineage;
import lineage.world.controller.DogRaceController;
import lineage.world.controller.SlimeRaceController;
import lineage.world.object.item.RaceTicket;

public class DograceInstance extends SlimeraceInstance {

	public DograceInstance(Npc n){
		super(n);
		
		// 레이스표 넣기.
		n.getShop_list().clear();
		for(int i=0 ; i<5 ; ++i){
			n.getShop_list().add( new Shop("레이스 표", 1, 1) );
			n.getShop_list().get(i).setUid(i);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		switch(DogRaceController.getStatus()){
			case STOP:
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maeno5") );
				break;
			case CLEAR:
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maeno1") );
				break;
			case READY:
			case PLAY:
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maeno3") );
				break;
			default:
				//pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maeno2") );
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maeno10") );
				break;
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("status")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maeno4", null, DogRaceController.getRacerStatus()));
		}else{
			super.toTalk(pc, action, type, cbp);
		}
		
		if (action.equalsIgnoreCase("sell")) {
			List<ItemInstance> sell_list = new ArrayList<ItemInstance>();
			for (Shop s : npc.getShop_list()) {
				// 판매할 수 있도록 설정된 목록만 처리.
				if (s.isItemSell()) {
					List<ItemInstance> search_list = new ArrayList<ItemInstance>();
					pc.getInventory().findDbName(s.getItemName(), search_list);
					for (ItemInstance item : search_list) {
						if (!item.isEquipped() && item.getItem().isSell() && (s.getItemEnLevel() == 0 || s.getItemEnLevel() == item.getEnLevel())) {
							//
							if (isSellAdd(item) && !sell_list.contains(item) && item.getEnLevel() == s.getItemEnLevel())
								sell_list.add(item);
						}
					}
				}
			}
			if (sell_list.size() > 0) {
				pc.toSender(S_ShopSell.clone(BasePacketPooling.getPool(S_ShopSell.class), this, sell_list));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maeno6"));
			}
		} else if (action.indexOf("3") > 0 || action.indexOf("6") > 0 || action.indexOf("7") > 0) {
			List<String> list_html = new ArrayList<String>();
			list_html.add(String.valueOf(getTax()));
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, action, null, list_html));
		}
	}

		
	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		super.toTeleport(x, y, map, effect);
		// 관리목록에 등록.
		DogRaceController.appendNpc(this);
	}
	
	@Override
	protected boolean isSellAdd(ItemInstance item){
		if(item instanceof RaceTicket){
			RaceTicket rt = (RaceTicket)item;
			return rt.getRacerType().equalsIgnoreCase("dog");
		}
		return false;
	}
	
}
