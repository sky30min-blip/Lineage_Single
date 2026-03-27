package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_HyperText;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Kahlua extends CraftInstance {

	public Kahlua(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );

		Item i = ItemDatabase.find("고대 명궁의 가더");
		if(i != null){
			craft_list.put("kahluaA", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("잊혀진 가죽 갑옷"), 1) );
			l.add( new Craft(ItemDatabase.find("블랙 미스릴 판금"), 1) );
			l.add( new Craft(ItemDatabase.find("아라크네의 허물"), 20) );
			l.add( new Craft(ItemDatabase.find("페어리의 날개"), 20) );
			l.add( new Craft(ItemDatabase.find("엔트의 껍질"), 50) );
			l.add( new Craft(ItemDatabase.find("미스릴 실"), 50) );
			l.add( new Craft(ItemDatabase.find("아데나"), 1000000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("고대 투사의 가더");
		if(i != null){
			craft_list.put("kahluaB", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("잊혀진 판금 갑옷"), 1) );
			l.add( new Craft(ItemDatabase.find("블랙 미스릴 판금"), 5) );
			l.add( new Craft(ItemDatabase.find("아데나"), 1000000) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kahlua1"));
	}
	
	/**
	 * 제작처리 마지막 부분. : 중복코드 방지용
	 * 
	 * @param pc
	 * @param action
	 * @param count
	 */
	private void toLeather(PcInstance pc, String action, long count) {
		Item craft = craft_list.get(action);

		if (craft != null) {
			int max = CraftController.getMax(pc, list.get(craft));
			if (count > 0 && max > 0 && count <= max) {
				// 재료 제거
				for (int i = 0; i < count; ++i)
					CraftController.toCraft(pc, list.get(craft));
				// 제작 아이템 지급.
				int jegop = craft.getListCraft().get(action) == null ? 0 : craft.getListCraft().get(action);
				if (jegop == 0)
					CraftController.toCraft(this, pc, craft, count, true);
				else
					CraftController.toCraft(this, pc, craft, count * jegop, true);
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		if (pc.getInventory() != null) {
			Item craft = craft_list.get(action);
			// 재료가 부족 할 시 창 닫기
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kahlua5"));

			if (craft != null) {
				// 고대 명궁의 가더
				if (action.equalsIgnoreCase("kahluaA")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "kahlua6", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 고대 투사의 가더
				if (action.equalsIgnoreCase("kahluaB")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "kahlua6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}
