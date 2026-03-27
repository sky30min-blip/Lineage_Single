package lineage.world.object.npc.shop;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ShopBuy;
import lineage.network.packet.server.S_ShopSell;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Philip extends ShopInstance {
    
    private long actionTime;
    static public final int[] Philip = { 7, 7, 7, 3, 7, 7, 3 };
    private int currentGfxIndex = 0;
    private int lastAction = -1;
    
    public Philip(Npc npc){
        super(npc);
        kingdom = KingdomController.find(Lineage.KINGDOM_GIRAN);
    }
    
    
    @Override
    public void close() {
        super.close();
        actionTime = 0L;
    }
    
    @Override
    public void toAi(long time) {
        if (actionTime + (1 * (Util.random(10, 15))) < System.currentTimeMillis()) {
            int tempGfxMode = Philip[currentGfxIndex]; 
            if (SpriteFrameDatabase.findGfxMode(getGfx(), tempGfxMode) && lastAction != tempGfxMode) {
                actionTime = System.currentTimeMillis();
                toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, tempGfxMode), true);

                currentGfxIndex = (currentGfxIndex + 1) % Philip.length;
            }
        }
    } 
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLawful()<Lineage.NEUTRAL) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Philip2"));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Philip1"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("buy")) {
			pc.toSender(S_ShopBuy.clone(BasePacketPooling.getPool(S_ShopBuy.class), this));
		} else if (action.equalsIgnoreCase("sell")) {
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
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Philip4"));
			}
		} else if (action.indexOf("3") > 0 || action.indexOf("6") > 0 || action.indexOf("7") > 0) {
			List<String> list_html = new ArrayList<String>();
			list_html.add(String.valueOf(getTax()));
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, action, null, list_html));
		}
	}
}