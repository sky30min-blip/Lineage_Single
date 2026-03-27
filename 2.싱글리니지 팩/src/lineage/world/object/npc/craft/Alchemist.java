package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Alchemist extends CraftInstance {

	public Alchemist(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "alchemy1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		switch(action.charAt(0)) {
			case 'a':	// 마프르의 유산 1개		마력의 돌 20개
				toCraft(pc, ItemDatabase.find("마프르의 유산"), ItemDatabase.find("마력의 돌"), 20, 1);
				break;
			case 'b':	// 마프르의 유산 10개
				toCraft(pc, ItemDatabase.find("마프르의 유산"), ItemDatabase.find("마력의 돌"), 20, 10);
				break;
			case 'c':	// 마프르의 유산 50개
				toCraft(pc, ItemDatabase.find("마프르의 유산"), ItemDatabase.find("마력의 돌"), 20, 50);
				break;
			case 'd':	// 마프르의 유산 1개		정령옥 40개
				toCraft(pc, ItemDatabase.find("마프르의 유산"), ItemDatabase.find("정령옥"), 40, 1);
				break;
			case 'e':	// 마프르의 유산 10개
				toCraft(pc, ItemDatabase.find("마프르의 유산"), ItemDatabase.find("정령옥"), 40, 10);
				break;
			case 'f':	// 마프르의 유산 50개
				toCraft(pc, ItemDatabase.find("마프르의 유산"), ItemDatabase.find("정령옥"), 40, 50);
				break;
			case 'g':	// 마프르의 유산 1개		흑마석 10개
				toCraft(pc, ItemDatabase.find("마프르의 유산"), ItemDatabase.find("흑마석"), 10, 1);
				break;
			case 'h':	// 마프르의 유산 10개
				toCraft(pc, ItemDatabase.find("마프르의 유산"), ItemDatabase.find("흑마석"), 10, 10);
				break;
			case 'i':	// 마프르의 유산 50개
				toCraft(pc, ItemDatabase.find("마프르의 유산"), ItemDatabase.find("흑마석"), 10, 50);
				break;
			case 'j':	// 사이하의 유산 1개		마력의 돌 25개
				toCraft(pc, ItemDatabase.find("사이하의 유산"), ItemDatabase.find("마력의 돌"), 25, 1);
				break;
			case 'k':	// 사이하의 유산 10개
				toCraft(pc, ItemDatabase.find("사이하의 유산"), ItemDatabase.find("마력의 돌"), 25, 10);
				break;
			case 'l':	// 사이하의 유산 50개
				toCraft(pc, ItemDatabase.find("사이하의 유산"), ItemDatabase.find("마력의 돌"), 25, 50);
				break;
			case 'm':	// 사이하의 유산 1개		정령옥 60개
				toCraft(pc, ItemDatabase.find("사이하의 유산"), ItemDatabase.find("정령옥"), 60, 1);
				break;
			case 'n':	// 사이하의 유산 10개
				toCraft(pc, ItemDatabase.find("사이하의 유산"), ItemDatabase.find("정령옥"), 60, 10);
				break;
			case 'o':	// 사이하의 유산 50개
				toCraft(pc, ItemDatabase.find("사이하의 유산"), ItemDatabase.find("정령옥"), 60, 50);
				break;
			case 'p':	// 사이하의 유산 1개		흑마석 20개
				toCraft(pc, ItemDatabase.find("사이하의 유산"), ItemDatabase.find("흑마석"), 20, 1);
				break;
			case 'q':	// 사이하의 유산 10개
				toCraft(pc, ItemDatabase.find("사이하의 유산"), ItemDatabase.find("흑마석"), 20, 10);
				break;
			case 'r':	// 사이하의 유산 50개
				toCraft(pc, ItemDatabase.find("사이하의 유산"), ItemDatabase.find("흑마석"), 20, 50);
				break;
			case 's':	// 에바의 유산 1개		마력의 돌 30개
				toCraft(pc, ItemDatabase.find("에바의 유산"), ItemDatabase.find("마력의 돌"), 30, 1);
				break;
			case 't':	// 에바의 유산 10개
				toCraft(pc, ItemDatabase.find("에바의 유산"), ItemDatabase.find("마력의 돌"), 30, 10);
				break;
			case 'u':	// 에바의 유산 50개
				toCraft(pc, ItemDatabase.find("에바의 유산"), ItemDatabase.find("마력의 돌"), 30, 50);
				break;
			case 'v':	// 에바의 유산 1개		정령옥 50개
				toCraft(pc, ItemDatabase.find("에바의 유산"), ItemDatabase.find("정령옥"), 50, 1);
				break;
			case 'w':	// 에바의 유산 10개
				toCraft(pc, ItemDatabase.find("에바의 유산"), ItemDatabase.find("정령옥"), 50, 10);
				break;
			case 'x':	// 에바의 유산 50개
				toCraft(pc, ItemDatabase.find("에바의 유산"), ItemDatabase.find("정령옥"), 50, 50);
				break;
			case 'y':	// 에바의 유산 1개		흑마석 20개
				toCraft(pc, ItemDatabase.find("에바의 유산"), ItemDatabase.find("흑마석"), 20, 1);
				break;
			case 'z':	// 에바의 유산 10개
				toCraft(pc, ItemDatabase.find("에바의 유산"), ItemDatabase.find("흑마석"), 20, 10);
				break;
			case 'A':	// 에바의 유산 50개
				toCraft(pc, ItemDatabase.find("에바의 유산"), ItemDatabase.find("흑마석"), 20, 50);
				break;
			case 'B':	// 파아그리오의 유산 1개		마력의 돌 25개
				toCraft(pc, ItemDatabase.find("파아그리오의 유산"), ItemDatabase.find("마력의 돌"), 25, 1);
				break;
			case 'C':	// 파아그리오의 유산 10개
				toCraft(pc, ItemDatabase.find("파아그리오의 유산"), ItemDatabase.find("마력의 돌"), 25, 10);
				break;
			case 'D':	// 파아그리오의 유산 50개
				toCraft(pc, ItemDatabase.find("파아그리오의 유산"), ItemDatabase.find("마력의 돌"), 25, 50);
				break;
			case 'E':	// 파아그리오의 유산 1개		정령옥 30개
				toCraft(pc, ItemDatabase.find("파아그리오의 유산"), ItemDatabase.find("정령옥"), 30, 1);
				break;
			case 'F':	// 파아그리오의 유산 10개
				toCraft(pc, ItemDatabase.find("파아그리오의 유산"), ItemDatabase.find("정령옥"), 30, 10);
				break;
			case 'G':	// 파아그리오의 유산 50개
				toCraft(pc, ItemDatabase.find("파아그리오의 유산"), ItemDatabase.find("정령옥"), 30, 50);
				break;
			case 'H':	// 파아그리오의 유산 1개		흑마석 10개
				toCraft(pc, ItemDatabase.find("파아그리오의 유산"), ItemDatabase.find("흑마석"), 10, 1);
				break;
			case 'I':	// 파아그리오의 유산 10개
				toCraft(pc, ItemDatabase.find("파아그리오의 유산"), ItemDatabase.find("흑마석"), 10, 10);
				break;
			case 'J':	// 파아그리오의 유산 50개
				toCraft(pc, ItemDatabase.find("파아그리오의 유산"), ItemDatabase.find("흑마석"), 10, 50);
				break;
			case 'K':	// 검은 혈흔에서 4종류의 속성석을 분리 1개
				toCraft(pc, 1);
				break;
			case 'L':	// 검은 혈흔에서 4종류의 속성석을 분리 10개
				toCraft(pc, 10);
				break;
			case 'M':	// 검은 혈흔에서 4종류의 속성석을 분리 50개
				toCraft(pc, 50);
				break;
		}
	}
	
	private void toCraft(PcInstance pc, Item jeryo, Item item, int give_count, int count) {
		List<Craft> l = new ArrayList<Craft>();
		l.add( new Craft(jeryo, count) );
		l.add( new Craft(ItemDatabase.find("아데나"), 1000*count) );
		if(CraftController.isCraft(pc, l, false)) {
			// 재료 제거
			CraftController.toCraft(pc, l);
			// 제작 아이템 지급.
			CraftController.toCraft(this, pc, item, give_count*count, true);
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "alchemy11"));
		}
		l.clear();
		l = null;
	}
	
	private void toCraft(PcInstance pc, int count) {
		List<Craft> l = new ArrayList<Craft>();
		l.add( new Craft(ItemDatabase.find("검은 혈흔"), count) );
		if(CraftController.isCraft(pc, l, false)) {
			// 재료 제거
			CraftController.toCraft(pc, l);
			// 제작 아이템 지급.
			CraftController.toCraft(this, pc, ItemDatabase.find("마프르의 유산"), count, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("사이하의 유산"), count, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("에바의 유산"), count, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("파아그리오의 유산"), count, true);

		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "alchemy12"));
		}
		l.clear();
		l = null;
	}
	
}

