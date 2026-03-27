package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_HyperText;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.object;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Hector extends CraftInstance {
	
	private long actionTime;
	private int lastAction;
	
	public Hector(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("강철 장갑");
		if(i != null){
			craft_list.put("request iron gloves", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("장갑"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 150) );
			l.add( new Craft(ItemDatabase.find("아데나"), 25000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("강철 면갑");
		if(i != null){
			craft_list.put("request iron visor", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("기사의 면갑"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 120) );
			l.add( new Craft(ItemDatabase.find("아데나"), 16500) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("강철 방패");
		if(i != null){
			craft_list.put("request iron shield", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("사각 방패"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 200) );
			l.add( new Craft(ItemDatabase.find("아데나"), 16000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("강철 장화");
		if(i != null){
			craft_list.put("request iron boots", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("부츠"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 160) );
			l.add( new Craft(ItemDatabase.find("아데나"), 8000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("강철 판금 갑옷");
		if(i != null){
			craft_list.put("request iron plate mail", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("판금 갑옷"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 450) );
			l.add( new Craft(ItemDatabase.find("아데나"), 30000) );
			list.put(i, l);
		}
	}

	@Override
	public void toAi(long time) {
		if (actionTime + (215 * (Util.random(10, 30))) < System.currentTimeMillis()) {
			while (true) {
				int tempGfxMode = Lineage.Hector[Util.random(0, Lineage.Hector.length - 1)];
				if (SpriteFrameDatabase.findGfxMode(getGfx(), tempGfxMode) && lastAction != tempGfxMode) {
					lastAction = 0;
					actionTime = System.currentTimeMillis();
					toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, tempGfxMode), true);
					break;
				}
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLawful()<Lineage.NEUTRAL)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "hector2"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "hector1"));
		
	}
	
	/**
	 * 제작처리 마지막 부분.
	 *  : 중복코드 방지용
	 * @param pc
	 * @param action
	 * @param count
	 */
	private void toLeather(PcInstance pc, String action, long count){
		Item craft = craft_list.get(action);
		
		if(craft != null){
			int max = CraftController.getMax(pc, list.get(craft));
			if(count>0 && max>0 && count<=max){
				// 재료 제거
				for(int i=0 ; i<count ; ++i)
					CraftController.toCraft(pc, list.get(craft));
				// 제작 아이템 지급.
				int jegop = craft.getListCraft().get(action)==null ? 0 : craft.getListCraft().get(action);
				if(jegop == 0)
					CraftController.toCraft(this, pc, craft, count, true);
				else
					CraftController.toCraft(this, pc, craft, count*jegop, true);
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		
		if (pc.getInventory() != null) {
			Item craft = craft_list.get(action);
			// 재료가 부족 할 시 창 닫기
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
			 
			if (craft != null) {
				// 장갑
				if (action.equalsIgnoreCase("request iron gloves")) {
					// 재료 확인.
					if(CraftController.isCraft(pc, list.get(craft), true)){
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if(Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "hectorC1", action, 0, 1, 1, max, temp_request_list));
					
					}
				}
			}
			 if (craft != null) {
				// 면갑
				if (action.equalsIgnoreCase("request iron visor")) {
					// 재료 확인.
					if(CraftController.isCraft(pc, list.get(craft), true)){
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if(Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "hectorC2", action, 0, 1, 1, max, temp_request_list));
					}
				}
			 }
			 if (craft != null) {
				// 방패
				if (action.equalsIgnoreCase("request iron shield")) {
					// 재료 확인.
					if(CraftController.isCraft(pc, list.get(craft), true)){
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if(Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "hectorC3", action, 0, 1, 1, max, temp_request_list));
					}
				}
			 }
			 if (craft != null) {
				// 장화
				if (action.equalsIgnoreCase("request iron boots")) {
					// 재료 확인.
					if(CraftController.isCraft(pc, list.get(craft), true)){
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if(Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "hectorC4", action, 0, 1, 1, max, temp_request_list));
					}
				}
			 }
			 if (craft != null) {
				// 갑옷
				if (action.equalsIgnoreCase("request iron plate mail")) {
					// 재료 확인.
					if(CraftController.isCraft(pc, list.get(craft), true)){
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if(Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "hectorC5", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}
