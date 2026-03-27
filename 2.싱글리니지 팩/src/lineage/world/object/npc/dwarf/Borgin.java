package lineage.world.object.npc.dwarf;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.database.Warehouse;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.database.WarehouseDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_WareHouse;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.DwarfInstance;
import lineage.world.object.instance.PcInstance;

public class Borgin extends DwarfInstance {
	
	private List<String> list_html;
	
	public Borgin(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_WINDAWOOD);
		list_html = new ArrayList<String>();
	}
	
	/**
	 * 창고를 이용할 수 있는 레벨인지 확인하는 메서드.
	 */
	static public boolean isLevel(int level) {
		return level >= Lineage.warehouse_level;
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		Kingdom k = KingdomController.find(pc);
		if(k==null || k.getUid()!=kingdom.getUid()){
			list_html.clear();
			list_html.add(kingdom.getClanName()==null ? "" : kingdom.getClanName());
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "borginop", null, list_html));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "borgin"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {		
		//자동판매 초기화
		pc.isAutoSellAdding = false;
		pc.isAutoSellDeleting = false;
		
		synchronized (sync_dynamic) {
			int dwarf_type = Lineage.DWARF_TYPE_NONE; // 일반창고
			if (action.indexOf("pledge") > 0)
				dwarf_type = Lineage.DWARF_TYPE_CLAN; // 혈맹창고 
			else if (action.indexOf("elven") > 0)
				dwarf_type = Lineage.DWARF_TYPE_ELF; // 요정창고   미스릴 2 

			int id = dwarf_type == Lineage.DWARF_TYPE_CLAN ? pc.getClanId() : pc.getClient().getAccountUid();
			
			// 혈맹 창고 사용못하는 버그 확인
			Clan clan = ClanController.find(pc);
			PcInstance use = null;
			
			if (clan != null) {
				use = World.findPc(clan.getWarehouseObjectId());
				
				if (use == null || !Util.isDistance(use, this, Lineage.SEARCH_LOCATIONRANGE)) {
					clan.setWarehouseObjectId(0L);
				}
			}
		

			if (dwarf_type == Lineage.DWARF_TYPE_CLAN && pc.getClanId() == 0) {
				// \f1창고: 혈맹 창고 이용 불가(혈맹 미가입)
				ChattingController.toChatting(pc, "창고: 혈맹 창고 이용 불가(혈맹 미가입)", Lineage.CHATTING_MODE_MESSAGE);
				//pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 208));
			} else if (dwarf_type == Lineage.DWARF_TYPE_CLAN && pc.getClassType() != Lineage.LINEAGE_CLASS_ROYAL && (pc.getTitle() == null || pc.getTitle().length() == 0)) {
				// 호칭을 받지 못한 혈맹원이나 견습 혈맹원은 혈맹창고를 사용할 수 없습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 728));
			} else if (dwarf_type == Lineage.DWARF_TYPE_CLAN && clan.getWarehouseObjectId() > 0L && clan.getWarehouseObjectId() != pc.getObjectId()) {
			//	pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 209)); // 창고 목록 
				if (use != null) {
					ChattingController.toChatting(pc, String.format("'%s' 님이 혈맹 창고를 사용중입니다.", use.getName()), Lineage.CHATTING_MODE_MESSAGE);
				}	
			} else {
				int cnt = WarehouseDatabase.getCount(id, dwarf_type);
				if (cnt == 0) {
					
				   pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "borgino")); 
				} else {
					if (dwarf_type == Lineage.DWARF_TYPE_CLAN)
						clan.setWarehouseObjectId(pc.getObjectId());

					// 창고 목록 열람.
					List<Warehouse> list = WarehouseDatabase.getList(id, dwarf_type);
					pc.toSender(S_WareHouse.clone(BasePacketPooling.getPool(S_WareHouse.class), this, dwarf_type, list));
					for (Warehouse wh : list)
						WarehouseDatabase.setPool(wh);
					list.clear();
				}
			}
		}
	}
}
