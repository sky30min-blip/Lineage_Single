package lineage.world.object.npc.craft;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Schuerme extends ShopInstance {

	private String 지룡의마안[][] = { { "봉인된 지룡의 마안", "1" }, { "아데나", "100000000" } };
	private String 수룡의마안[][] = { { "봉인된 수룡의 마안", "1" }, { "아데나", "100000000" } };
	private String 풍룡의마안[][] = { { "봉인된 풍룡의 마안", "1" }, { "아데나", "100000000" } };
	private String 화룡의마안[][] = { { "봉인된 화룡의 마안", "1" }, { "아데나", "100000000" } };

	private String 탄생의마안[][] = { { "지룡의 마안", "1" }, { "수룡의 마안", "1" }};
	private String 형상의마안[][] = { { "탄생의 마안", "1" }, { "풍룡의 마안", "1" }};
	private String 생명의마안[][] = { { "형상의 마안", "1" }, { "화룡의 마안", "1" }};
	private String 생명의마안1[][] = { { "형상의 마안", "1" }, { "화룡의 마안", "1" }};

	public Schuerme(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		setHeading(Util.calcheading(this, pc.getX(), pc.getY()));
		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sherme2"));
	}
	
	private boolean isCraft(PcInstance pc,String list[][]) {
		for(int i = 0 ; i < list.length; i++) {
			if(pc.getInventory().find(list[i][0])==null) {
				ChattingController.toChatting(pc, String.format("%s %d개가 부족합니다", list[i][0], Integer.parseInt(list[i][1])), Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
			if(pc.getInventory().find(list[i][0]).getCount()<Integer.parseInt(list[i][1])) {
				ChattingController.toChatting(pc, String.format("%s %d개가 부족합니다", list[i][0], Integer.parseInt(list[i][1])), Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
		}
		return true;
	}
	private void deleteItem(PcInstance pc,String list[][]) {
		for(int i = 0 ; i < list.length; i++) {
			ItemInstance del = pc.getInventory().find(list[i][0]);
			pc.getInventory().count(del, del.getCount()-Integer.parseInt(list[i][1]),true);
		}
	}
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		int success_probability = 50;
		int rnd = Util.random(0, 100);
		
		if(action.equalsIgnoreCase("a")) {
			Item i = ItemDatabase.find("지룡의 마안");
			if(i!=null && isCraft(pc,지룡의마안)) { 
				    deleteItem(pc,지룡의마안);
					CraftController.toCraft(this, pc,i, 1, true);
			}
		}else if(action.equalsIgnoreCase("b")) {
			Item i = ItemDatabase.find("수룡의 마안");
			if(i!=null && isCraft(pc,수룡의마안)) { 
				    deleteItem(pc,수룡의마안);
				    CraftController.toCraft(this, pc,i, 1, true);
			}
		}else if(action.equalsIgnoreCase("c")) {
			Item i = ItemDatabase.find("화룡의 마안");
			if(i!=null && isCraft(pc,화룡의마안)) { 
				 deleteItem(pc,화룡의마안);
				 CraftController.toCraft(this, pc,i, 1, true);
			}
		}else if(action.equalsIgnoreCase("d")) {
			Item i = ItemDatabase.find("풍룡의 마안");
			if(i!=null && isCraft(pc,풍룡의마안)) { 
				 deleteItem(pc,풍룡의마안);
				 CraftController.toCraft(this, pc,i, 1, true);
			}
		}else if(action.equalsIgnoreCase("e")) {
			Item i = ItemDatabase.find("탄생의 마안");
			if(i!=null && isCraft(pc,탄생의마안)) { 
				if(rnd < success_probability)
					CraftController.toCraft(this, pc,i, -1, true);
				else
					ChattingController.toChatting(pc, "제작에 실패 하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
				deleteItem(pc,탄생의마안);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sherme5"));
			}
		}else if(action.equalsIgnoreCase("f")) {
			Item i = ItemDatabase.find("형상의 마안");
			if(i!=null && isCraft(pc,형상의마안)) {
				if(rnd < success_probability)
					CraftController.toCraft(this, pc,i, -1, true);
				else
					ChattingController.toChatting(pc, "제작에 실패 하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
				deleteItem(pc,형상의마안);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sherme5"));
			}
		}else if(action.equalsIgnoreCase("g")) {
			Item i = ItemDatabase.find("생명의 마안");
			if(i!=null && isCraft(pc,생명의마안1)) {
				if(rnd < success_probability)
					CraftController.toCraft(this, pc,i, -1, true);
				else
				ChattingController.toChatting(pc, "제작에 실패 하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
				deleteItem(pc,생명의마안);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sherme5"));
			}
		}
	}
}
