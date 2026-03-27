package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.PcMarketController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class C_ItemClick extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ItemClick(data, length);
		else
			((C_ItemClick)bp).clone(data, length);
		return bp;
	}
	
	public C_ItemClick(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.getInventory()==null || !isRead(4) || pc.isDead() || pc.isWorldDelete())
			return this;
		
		ItemInstance item = pc.getInventory().value( readD() );
		
		if (item.getItem().getName().equalsIgnoreCase("전장의 가호")){
			int Seal_Level = item.getEnLevel();
			ChattingController.toChatting(pc, "-------------적용중인 효과---------------------------", Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(pc, String.format("[알림] +%d %s : 근/원 대미지 +%d, 근/원 명중 +%d,", item.getEnLevel(), item.getItem().getName(), Seal_Level, Seal_Level),
					Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(pc, String.format("SP +%d, hp+ %d , 경험치획득량 + %.0f퍼 증가", Seal_Level, Seal_Level * 20, (Seal_Level * 0.01) * 100), Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(pc, "--------------------------------------------------", Lineage.CHATTING_MODE_MESSAGE);
			return this;
		}

		if (item != null && item.isClick(pc) && (pc.getGm() > 0 || !pc.isTransparent())) {
			// 상점 등록 확인
			if(pc.PcMarket_Step == 2){
				PcMarketController.isShopToAppend(pc, item, pc.PcMarket_Count);
				
				return this;
			}
			
			// 플러그인 확인.
			if (PluginController.init(C_ItemClick.class, "pcItemTrade", this, pc, item) == null && PluginController.init(C_ItemClick.class, "init", this, pc, item) == null && PluginController.init(C_ItemClick.class, "pcTrade", this, pc, item) == null )
				item.toClick(pc, this);
		}
		return this;
	}
}
