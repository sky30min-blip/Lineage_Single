package lineage.world.object.item.scroll;

import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class TalkingScroll extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new TalkingScroll();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!isClick(cha))
			return;
		//자동칼질
		CraftController.toCraft(this, cha, ItemDatabase.find("자동 칼질"), 1, false);
		//초기 지급 물약
		CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 체력 회복제"), (long) 100, false);
		CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 속도향상 물약"), (long) 10, false);
		//초기 주문서
		CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 확인 주문서"), (long) 10, false);
		CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 순간이동 주문서"), (long) 50, false);
		CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 귀환 주문서"), (long) 5, false);
		
		// 클레스별 장비 추가 부분.
		switch(cha.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				//무기류
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 단검"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 한손검"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 양손검"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 도끼"), 1, false);
				// 장비류
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 투구"), 1, false);
//				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 티셔츠"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 갑옷"), 1, false);
//				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 망토"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 방패"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 샌달"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 장갑"), 1, false);
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				//무기류
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 단검"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 한손검"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 양손검"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 도끼"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 창"), 1, false);
				// 장비류
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 투구"), 1, false);
//				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 티셔츠"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 갑옷"), 1, false);
//				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 망토"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 방패"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 샌달"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 장갑"), 1, false);
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				//무기류
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 단검"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 한손검"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 석궁"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 활"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("화살"), 2000, false);
				// 장비류
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 투구"), 1, false);
//				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 티셔츠"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 갑옷"), 1, false);
//				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 망토"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 방패"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 샌달"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 장갑"), 1, false);
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				//무기류
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 단검"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 지팡이"), 1, false);
				// 장비류
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 투구"), 1, false);
//				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 티셔츠"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 갑옷"), 1, false);
//				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 망토"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 방패"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 샌달"), 1, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 가죽 장갑"), 1, false);
				break;
	    }
		ChattingController.toChatting(cha, "보급품이 지급되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount() - 1, true);
	}

}
	

