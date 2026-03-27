package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import all_night.Lineage_Balance;
import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.MagicDoll;

public class MagicdollCompose extends object {
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		
		List<String> ynlist2 = new ArrayList<String>();
		
		ynlist2.add(String.format("소모되는 아데나 : %s 원", Lineage.doll_drogon));	
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Magicdoll",null,ynlist2));
		
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if (!pc.isWorldDelete() && !pc.isDead() && !pc.isLock() && pc.getInventory() != null) {
			// 인형 합성시 필요한 재료 갯수
			int count = action.equalsIgnoreCase("특수 합성") ? 3 : 4;
			int specialCount = 1;
			String itemName = null;
			List<MagicDoll> list = new ArrayList<MagicDoll>();
			List<MagicDoll> magicDollList = new ArrayList<MagicDoll>();
			List<MagicDoll> magicDollSpecialList = new ArrayList<MagicDoll>();
			
			for (ItemInstance item : pc.getInventory().getList()) {
				if (item instanceof MagicDoll && !item.isEquipped())
					list.add((MagicDoll) item);
			}
			
			if (action.equalsIgnoreCase("1단계 합성")) {
				int level = 0;
				
				for (MagicDoll magicdoll : list) {
					for (int i = 0; i < Lineage.magicDoll[level].length; i++) {
						if (magicdoll.getItem().getName().equalsIgnoreCase(Lineage.magicDoll[level][i]))
							magicDollList.add(magicdoll);
					}
				}
				
				if (magicDollList.size() >= count) {
					double probability = Math.random();
					
					if (probability < Lineage_Balance.magicDoll_class_1_perfect_probability) {
						itemName = Lineage.magicDoll[level + 2][Util.random(0, Lineage.magicDoll[level + 2].length - 1)];
						ChattingController.toChatting(pc, "1단계 마법인형 합성 대성공!", Lineage.CHATTING_MODE_MESSAGE);
					} else if (probability < Lineage_Balance.magicDoll_class_1_probability) {
						itemName = Lineage.magicDoll[level + 1][Util.random(0, Lineage.magicDoll[level + 1].length - 1)];
						ChattingController.toChatting(pc, "1단계 마법인형 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
					} else {
						itemName = Lineage.magicDoll[level][Util.random(0, Lineage.magicDoll[level].length - 1)];
					}
				} else {
					ChattingController.toChatting(pc, String.format("1단계 인형 %d개 부족합니다.", count - magicDollList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}		
			} else if (action.equalsIgnoreCase("2단계 합성")) {
				int level = 1;
				
				for (MagicDoll magicdoll : list) {
					for (int i = 0; i < Lineage.magicDoll[level].length; i++) {
						if (magicdoll.getItem().getName().equalsIgnoreCase(Lineage.magicDoll[level][i]))
							magicDollList.add(magicdoll);
					}
				}
				
				if (magicDollList.size() >= count) {
					double probability = Math.random();
					
					if (probability < Lineage_Balance.magicDoll_class_2_perfect_probability) {
						itemName = Lineage.magicDoll[level + 2][Util.random(0, Lineage.magicDoll[level + 2].length - 1)];
						ChattingController.toChatting(pc, "2단계 마법인형 합성 대성공!", Lineage.CHATTING_MODE_MESSAGE);
					} else if (probability < Lineage_Balance.magicDoll_class_2_probability) {
						itemName = Lineage.magicDoll[level + 1][Util.random(0, Lineage.magicDoll[level + 1].length - 1)];
						ChattingController.toChatting(pc, "2단계 마법인형 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
					} else {
						itemName = Lineage.magicDoll[level][Util.random(0, Lineage.magicDoll[level].length - 1)];
					}
				} else {
					ChattingController.toChatting(pc, String.format("2단계 인형 %d개 부족합니다.", count - magicDollList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}	
			} else if (action.equalsIgnoreCase("3단계 합성")) {
				int level = 2;
				
				for (MagicDoll magicdoll : list) {
					for (int i = 0; i < Lineage.magicDoll[level].length; i++) {
						if (magicdoll.getItem().getName().equalsIgnoreCase(Lineage.magicDoll[level][i]))
							magicDollList.add(magicdoll);
					}
				}
				
				if (magicDollList.size() >= count) {
					double probability = Math.random();
					
					if (probability < Lineage_Balance.magicDoll_class_3_perfect_probability) {
						itemName = Lineage.magicDoll[level + 2][Util.random(0, Lineage.magicDoll[level + 2].length - 1)];
						ChattingController.toChatting(pc, "3단계 마법인형 합성 대성공!", Lineage.CHATTING_MODE_MESSAGE);
					} else if (probability < Lineage_Balance.magicDoll_class_3_probability) {
						itemName = Lineage.magicDoll[level + 1][Util.random(0, Lineage.magicDoll[level + 1].length - 1)];
						ChattingController.toChatting(pc, "3단계 마법인형 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
					} else {
						itemName = Lineage.magicDoll[level][Util.random(0, Lineage.magicDoll[level].length - 1)];
					}
				} else {
					ChattingController.toChatting(pc, String.format("3단계 인형 %d개 부족합니다.", count - magicDollList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			} else if (action.equalsIgnoreCase("4단계 합성")) {
				int level = 3;
				
				for (MagicDoll magicdoll : list) {
					for (int i = 0; i < Lineage.magicDoll[level].length; i++) {
						if (magicdoll.getItem().getName().equalsIgnoreCase(Lineage.magicDoll[level][i]))
							magicDollList.add(magicdoll);
					}
				}
				
				if (magicDollList.size() >= count) {
					double probability = Math.random();
					
					if (probability < Lineage_Balance.magicDoll_class_4_probability) {
						itemName = Lineage.magicDoll[level + 1][Util.random(0, Lineage.magicDoll[level + 1].length - 1)];
						ChattingController.toChatting(pc, "4단계 마법인형 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
					} else {
						itemName = Lineage.magicDoll[level][Util.random(0, Lineage.magicDoll[level].length - 1)];
					}
				} else {
					ChattingController.toChatting(pc, String.format("4단계 인형 %d개 부족합니다.", count - magicDollList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			} else if (action.equalsIgnoreCase("용 합성")) {
				int level = 4;
				count = 1;
				for (MagicDoll magicdoll : list) {
					for (int i = 0; i < Lineage.magicDoll[level].length; i++) {
						if (magicdoll.getItem().getName().equalsIgnoreCase(Lineage.magicDoll[level][i]))
							magicDollList.add(magicdoll);
					}
				}
				
		
	
				
				if (magicDollList.size() >= count ) {
					double probability = Math.random();
	
					if(pc.getInventory().isAden("아데나",Lineage.doll_drogon, true)){
						if (probability < Lineage_Balance.magicDoll_class_6_probability) {
							itemName = Lineage.magicDoll[level + 1][Util.random(0, Lineage.magicDoll[level + 1].length - 1)];
							pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 2048), true);
			
										
						} else {
							itemName = Lineage.magicDoll[level][Util.random(0, Lineage.magicDoll[level].length - 1)];
						}
							
					}else{
						ChattingController.toChatting(pc, "베릴이 부족합니다", Lineage.CHATTING_MODE_MESSAGE);
					}
						
								 
					 
					
					
				
				} else {
					ChattingController.toChatting(pc, String.format("5단계 인형이 부족합니다"), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
	
			}
//			} else if (action.equalsIgnoreCase("특수 합성")) {
//				int level = 3;
//				
//				for (MagicDoll magicdoll : list) {
//					for (int i = 0; i < Lineage.magicDoll[level].length; i++) {
//						if (magicdoll.getItem().getName().equalsIgnoreCase(Lineage.magicDoll[level][i]))
//							magicDollList.add(magicdoll);
//					}
//				}
//				
//				for (MagicDoll specialMagicdoll : list) {
//					for (int i = 0; i < Lineage.magicDoll[level + 1].length; i++) {
//						if (specialMagicdoll.getItem().getName().equalsIgnoreCase(Lineage.magicDoll[level + 1][i]))
//							magicDollSpecialList.add(specialMagicdoll);
//					}
//				}
//				
//				for (MagicDoll specialMagicdoll : list) {
//					for (int i = 0; i < Lineage.magicDoll[level + 2].length; i++) {
//						if (specialMagicdoll.getItem().getName().equalsIgnoreCase(Lineage.magicDoll[level + 2][i]))
//							magicDollSpecialList.add(specialMagicdoll);
//					}
//				}
//				
//				if (magicDollList.size() >= count && magicDollSpecialList.size() >= specialCount) {
//					double probability = Math.random();
//					// 일정 확률로 4대용 인형 성공.
//					// 실패시 5단계 인형 지급.
//					if (probability < Lineage_Balance.magicDoll_class_5_probability) {
//						itemName = Lineage.magicDoll[level + 2][Util.random(0, Lineage.magicDoll[level + 2].length - 1)];
//						ChattingController.toChatting(pc, "특수 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
//					} else {
//						itemName = Lineage.magicDoll[level + 1][Util.random(0, Lineage.magicDoll[level + 1].length - 1)];
//					}
//				} else {
//					if (magicDollList.size() < count && magicDollSpecialList.size() >= specialCount)
//						ChattingController.toChatting(pc, String.format("4단계 인형 %d개 부족합니다.", count - magicDollList.size()), Lineage.CHATTING_MODE_MESSAGE);
//					else if (magicDollList.size() >= count && magicDollSpecialList.size() < specialCount)
//						ChattingController.toChatting(pc, String.format("5단계 인형이 %d개 부족합니다.", specialCount - magicDollSpecialList.size()), Lineage.CHATTING_MODE_MESSAGE);
//					else
//						ChattingController.toChatting(pc, String.format("5단계 인형 %d개, 4단계 인형 %d개 부족합니다.", specialCount - magicDollSpecialList.size(), count - magicDollList.size()), Lineage.CHATTING_MODE_MESSAGE);
//					return;
//				}
//			}
			
			if (itemName != null) {
				Item item = ItemDatabase.find(itemName);
				
				if (item != null) {
					ItemInstance temp = ItemDatabase.newInstance(item);
					temp.setObjectId(ServerDatabase.nextItemObjId());
					temp.setBless(1);
					temp.setEnLevel(0);
					temp.setDefinite(true);
					pc.getInventory().append(temp, true);
					
//					if (action.equalsIgnoreCase("특수 합성")) {
//						for (int i = 0; i < count; i++)
//							pc.getInventory().count(magicDollList.get(i), magicDollList.get(i).getCount() - 1, true);
//						
//						for (int i = 0; i < specialCount; i++)
//							pc.getInventory().count(magicDollSpecialList.get(i), magicDollSpecialList.get(i).getCount() - 1, true);
//						
//					} else {
						for (int i = 0; i < count; i++)
							pc.getInventory().count(magicDollList.get(i), magicDollList.get(i).getCount() - 1, true);
//					}
			
					// 월드에 메세지 출력		
					ItemDropMessageDatabase.sendMessageMagicDoll(pc, itemName);
	
					ChattingController.toChatting(pc, String.format("[마법인형 합성] %s 획득! ", temp.toStringDB()), Lineage.CHATTING_MODE_MESSAGE);
				
				}
			}
			
		}
	}
}
