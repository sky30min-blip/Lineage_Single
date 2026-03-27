package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
import lineage.world.object.item.Card;
import lineage.world.object.item.MagicDoll;

public class PolyCardCompose extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {

		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "polycard", null, null));

	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (!pc.isWorldDelete() && !pc.isDead() && !pc.isLock() && pc.getInventory() != null) {
			// 인형 합성시 필요한 재료 갯수
			int count = action.equalsIgnoreCase("유일합성") ? 2 : 4;
			int specialCount = 1;
			String itemName = null;
			List<Card> list = new ArrayList<Card>();
			List<Card> CardList = new ArrayList<Card>();


		    for (ItemInstance item : pc.getInventory().getList()) {
		        if (item instanceof Card && !item.isEquipped()) {
		            // If the card is combined, split it into individual cards based on count
		            int cardCount = (int) item.getCount();
		            for (int j = 0; j < cardCount; j++) {
		                list.add((Card) item);
		            }
		        }
		    }
		    System.out.println("List of Individual Cards:");
		    for (Card card : list) {
		        System.out.println(card.getName() + " - Count: " + card.getCount());
		    }
			if (action.equalsIgnoreCase("일반합성")) {
				int level = 0;

				for (Card polycard : list) {
				    for (int i = 0; i < Lineage.polyCard[level].length; i++) {
				        if (polycard.getName().equalsIgnoreCase(Lineage.polyCard[level][i])) {
	
				            int cardCount = (int) polycard.getCount();
				            for (int j = 0; j < cardCount; j++) {
				                CardList.add(polycard);
				            }
				        }
				    }
				}

				if (CardList.size() >= count) {
					double probability = Math.random();

					if (probability < Lineage_Balance.polycard_1_perfect_probability) {
						itemName = Lineage.polyCard[level + 2][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level + 2].length)];
						ChattingController.toChatting(pc, "일반 변신 카드 합성 대성공!", Lineage.CHATTING_MODE_MESSAGE);
					} else if (probability < Lineage_Balance.polycard_1_probability) {
						itemName = Lineage.polyCard[level + 1][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level + 1].length)];
						ChattingController.toChatting(pc, "일반 변신 카드 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
					} else {
						itemName = Lineage.polyCard[level][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level].length)];
					}
				} else {
					ChattingController.toChatting(pc, String.format("일반 변신카드 %d개 부족합니다.", count - CardList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
			if (action.equalsIgnoreCase("고급합성")) {
				int level = 1;

			    for (Card polycard : list) {
			        for (int i = 0; i < Lineage.polyCard[level].length; i++) {
			            if (polycard.getName().equalsIgnoreCase(Lineage.polyCard[level][i])) {
			                int cardCount = (int) polycard.getCount();
			                for (int j = 0; j < cardCount; j++) {
			                    CardList.add(polycard);
			                }
			            }
			        }
			    }

				if (CardList.size() >= count) {
					double probability = Math.random();

					if (probability < Lineage_Balance.polycard_2_perfect_probability) {
						itemName = Lineage.polyCard[level + 2][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level + 2].length)];
						ChattingController.toChatting(pc, "고급 변신 카드 합성 대성공!", Lineage.CHATTING_MODE_MESSAGE);
						//대성공 이팩트
//						pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 13533), true);
					} else if (probability < Lineage_Balance.polycard_2_probability) {
						itemName = Lineage.polyCard[level + 1][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level + 1].length)];
						ChattingController.toChatting(pc, "고급 변신 카드 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
						//성공 이팩트
						//		pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 13533), true);
					} else {
						itemName = Lineage.polyCard[level][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level].length)];
					}
				} else {
					ChattingController.toChatting(pc, String.format("고급 변신카드 %d개 부족합니다.", count - CardList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
			if (action.equalsIgnoreCase("희귀합성")) {
				int level = 2;

				 for (Card polycard : list) {
				        for (int i = 0; i < Lineage.polyCard[level].length; i++) {
				            if (polycard.getName().equalsIgnoreCase(Lineage.polyCard[level][i])) {
				                int cardCount = (int) polycard.getCount();
				                for (int j = 0; j < cardCount; j++) {
				                    CardList.add(polycard);
				                }
				            }
				        }
				    }

				if (CardList.size() >= count) {
					double probability = Math.random();

					if (probability < Lineage_Balance.polycard_3_probability) {
						itemName = Lineage.polyCard[level + 1][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level + 1].length)];
						ChattingController.toChatting(pc, "희귀 변신 카드 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
						//성공 이팩트
						//		pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 13533), true);
					} else {
						itemName = Lineage.polyCard[level][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level].length)];
					}
				} else {
					ChattingController.toChatting(pc, String.format("희귀 변신카드 %d개 부족합니다.", count - CardList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
			if (action.equalsIgnoreCase("영웅합성")) {
				int level = 3;

				 for (Card polycard : list) {
				        for (int i = 0; i < Lineage.polyCard[level].length; i++) {
				            if (polycard.getName().equalsIgnoreCase(Lineage.polyCard[level][i])) {
				                int cardCount = (int) polycard.getCount();
				                for (int j = 0; j < cardCount; j++) {
				                    CardList.add(polycard);
				                }
				            }
				        }
				    }
				if (CardList.size() >= count) {
					double probability = Math.random();

					if (probability < Lineage_Balance.polycard_4_probability) {
						itemName = Lineage.polyCard[level + 1][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level + 1].length)];
						ChattingController.toChatting(pc, "영웅 변신 카드 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
						//성공 이팩트
						//		pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 13533), true);
					} else {
						itemName = Lineage.polyCard[level][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level].length)];
					}
				} else {
					ChattingController.toChatting(pc, String.format("영웅 변신카드 %d개 부족합니다.", count - CardList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
			if (action.equalsIgnoreCase("전설합성")) {
				int level = 4;

				 for (Card polycard : list) {
				        for (int i = 0; i < Lineage.polyCard[level].length; i++) {
				            if (polycard.getName().equalsIgnoreCase(Lineage.polyCard[level][i])) {
				                int cardCount = (int) polycard.getCount();
				                for (int j = 0; j < cardCount; j++) {
				                    CardList.add(polycard);
				                }
				            }
				        }
				    }

				if (CardList.size() >= count) {
					double probability = Math.random();

					if (probability < Lineage_Balance.polycard_5_probability) {
						itemName = Lineage.polyCard[level + 1][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level + 1].length)];
						ChattingController.toChatting(pc, "전설 변신 카드 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
						//성공 이팩트
						//		pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 13533), true);
					} else {
						itemName = Lineage.polyCard[level][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level].length)];
					}
				} else {
					ChattingController.toChatting(pc, String.format("전설 변신카드 %d개 부족합니다.", count - CardList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
			if (action.equalsIgnoreCase("신화합성")) {
				int level = 5;

				 for (Card polycard : list) {
				        for (int i = 0; i < Lineage.polyCard[level].length; i++) {
				            if (polycard.getName().equalsIgnoreCase(Lineage.polyCard[level][i])) {
				                int cardCount = (int) polycard.getCount();
				                for (int j = 0; j < cardCount; j++) {
				                    CardList.add(polycard);
				                }
				            }
				        }
				    }

				if (CardList.size() >= count) {
					double probability = Math.random();

					if (probability < Lineage_Balance.polycard_6_probability) {
						itemName = Lineage.polyCard[level + 1][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level + 1].length)];
						ChattingController.toChatting(pc, "신화 변신 카드 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
						//성공 이팩트
						//		pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 13533), true);
					} else {
						itemName = Lineage.polyCard[level][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level].length)];
					}
				} else {
					ChattingController.toChatting(pc, String.format("신화 변신카드 %d개 부족합니다.", count - CardList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
			if (action.equalsIgnoreCase("유일합성")) {
				int level = 6;

				 for (Card polycard : list) {
				        for (int i = 0; i < Lineage.polyCard[level].length; i++) {
				            if (polycard.getName().equalsIgnoreCase(Lineage.polyCard[level][i])) {
				                int cardCount = (int) polycard.getCount();
				                for (int j = 0; j < cardCount; j++) {
				                    CardList.add(polycard);
				                }
				            }
				        }
				    }

				if (CardList.size() >= count) {

			
						itemName = Lineage.polyCard[level + 1][ThreadLocalRandom.current().nextInt(Lineage.polyCard[level + 1].length)];
						ChattingController.toChatting(pc, "유일 변신 카드 합성 성공!", Lineage.CHATTING_MODE_MESSAGE);
						//성공 이팩트
						//		pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 13533), true);
				
				} else {
					ChattingController.toChatting(pc, String.format("유일 변신카드 %d개 부족합니다.", count - CardList.size()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
			 if (itemName != null) {
			        // Create and add the new synthesized card(s) to the player's inventory
			        Item item = ItemDatabase.find(itemName);
			        if (item != null) {
			            ItemInstance temp = ItemDatabase.newInstance(item);
			            temp.setObjectId(ServerDatabase.nextItemObjId());
			            temp.setBless(1);
			            temp.setEnLevel(0);
			            temp.setDefinite(true);
	
			             pc.toGiveItem(null, temp, temp.getCount());

			            // Adjust the card count based on the synthesis outcome
			            for (int i = 0; i < count; i++) {
			                Card card = CardList.get(i);
			                int cardCount = (int) card.getCount();
			                if (cardCount > 1) {
			                    pc.getInventory().count(card, cardCount - 1, true);
			                } else {
			                    pc.getInventory().remove(card, true);
			                }
			            }

			            // Display messages, effects, etc. for the synthesis outcome
			            ItemDropMessageDatabase.sendMessageMagicDoll(pc, itemName);
			            ChattingController.toChatting(pc, String.format("[변신카드 합성] %s 획득! ", temp.toStringDB()), Lineage.CHATTING_MODE_MESSAGE);
			        }
			}

		}
	}
}
