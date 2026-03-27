package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lineage.database.AccountDatabase;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;

import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class KuberaQuest2 extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		showHtml(pc);
	}
	//쿠베라 몬스터 퀘스트
	public void showHtml(PcInstance pc) {
		List<String> kqrlist = new ArrayList<String>();
		kqrlist.clear();
		switch (pc.getRadomQuest()) {
		case 0:
			kqrlist.add(String.format("%d", pc.getRandomQuestCount()));
			kqrlist.add(String.format("%d", Lineage.dayquest));
			kqrlist.add(String.format("반복퀘스트 시작을 누르시면 퀘스트가 시작 됩니다"));
			break;
		case 1:
			kqrlist.add(String.format("%d", pc.getRandomQuestCount()));
			kqrlist.add(String.format("%d", Lineage.dayquest));
			// 퀘스트명
			kqrlist.add(String.format("%s", "용던3층 소탕"));
			// 퀘스트목표
			kqrlist.add(String.format("%s", "용던3층에서 몬스터 30마리 처치"));
			if (pc.getRandomQuestkill() > 0) {
				// 처치몬스터
				kqrlist.add(String.format("%d", pc.getRandomQuestkill()));
			} else {
				kqrlist.add(String.format("0"));
			}

			// 보상
			kqrlist.add(String.format("%s", "아데나 20000"));
			break;
		case 2:
			kqrlist.add(String.format("%d", pc.getRandomQuestCount()));
			kqrlist.add(String.format("%d", Lineage.dayquest));
			// 퀘스트명
			kqrlist.add(String.format("%s", "용던4층 소탕"));
			// 퀘스트목표
			kqrlist.add(String.format("%s", "용던4층에서 몬스터 30마리 처치"));
			// 처치몬스터
			if (pc.getRandomQuestkill() > 0) {
				// 처치몬스터
				kqrlist.add(String.format("%d", pc.getRandomQuestkill()));
			} else {
				kqrlist.add(String.format("0"));
			}
			// 보상
			kqrlist.add(String.format("%s", "아데나 50000"));
			break;
		case 3:
			kqrlist.add(String.format("%d", pc.getRandomQuestCount()));
			kqrlist.add(String.format("%d", Lineage.dayquest));
			// 퀘스트명
			kqrlist.add(String.format("%s", "용던5층 소탕"));
			// 퀘스트목표
			kqrlist.add(String.format("%s", "용던5층에서 몬스터 30마리 처치"));
			// 처치몬스터
			if (pc.getRandomQuestkill() > 0) {
				// 처치몬스터
				kqrlist.add(String.format("%d", pc.getRandomQuestkill()));
			} else {
				kqrlist.add(String.format("0"));
			}
			// 보상
			kqrlist.add(String.format("%s", "아데나 100000"));
			break;

		default:
			break;
		}

		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kquest2", null, kqrlist));

	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		int monstkill = 0;
		if (action.equalsIgnoreCase("kquest2-start")) {
			
			
			Random random = new Random();
			int i = random.nextInt(3) + 1;
			
			if (pc.getRandomQuestCount() > Lineage.dayquest) {
				ChattingController.toChatting(pc, String.format("\\fY 오늘 수행 가능한 퀘스트를 전부 완료 하셨습니다."), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			if(pc.getRandomQuestPlay()==1){
				ChattingController.toChatting(pc, String.format("\\fY [반복 퀘스트] 이미 진행중인 퀘스트가 있습니다."), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
		
			pc.setRadomQuest(i );
			pc.setRandomQuestPlay(1);
			AccountDatabase.updaterquest(pc.getRadomQuest(), (int) pc.getObjectId());
			
		}
		if (action.equalsIgnoreCase("kquest2-go")) {
			Random random = new Random();
			int i = random.nextInt(3) + 1;
			
			if(pc.getRadomQuest() == 0) {
				ChattingController.toChatting(pc, String.format("\\fY [반복 퀘스트] 수락된 퀘스트가 없어 이동이 불가능합니다"), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			
			switch (pc.getRadomQuest()) {
			case 1:
			
				if (i == 1) {
					pc.toTeleport(32704, 32830, 32, true);
				} else if (i == 2) {
					pc.toTeleport(32731, 32824, 32, true);
				} else if (i == 3) {
					pc.toTeleport(32673, 32868, 32, true);
				}
				break;
			case 2:
		
				if (i == 1) {
					pc.toTeleport(32713, 32833, 33, true);
				} else if (i == 2) {
					pc.toTeleport(32683, 32820, 33, true);
				} else if (i == 3) {
					pc.toTeleport(32714, 32872, 33, true);
				}
				break;
			case 3:
				
				if (i == 1) {
					pc.toTeleport(32690, 32863, 35, true);
				} else if (i == 2) {
					pc.toTeleport(32712, 32826, 35, true);
				} else if (i == 3) {
					pc.toTeleport(32743, 32800, 35, true);
				}
				break;
			}
			
		}
		switch (pc.getRadomQuest()) {
		case 1:
			monstkill = 30;
			break;
		case 2:
			monstkill = 30;
			break;
		case 3:
			monstkill = 30;
			break;

		default:
			break;
		}
		if (action.equalsIgnoreCase("kquest2-finish")) {

			if (pc.getRandomQuestCount() < Lineage.dayquest) {
				if(pc.getRadomQuest() == 0) {
					ChattingController.toChatting(pc, String.format("\\fY [반복 퀘스트] 완료 가능한 퀘스트가 없습니다."), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}

				if (pc.getRandomQuestkill() >= monstkill) {
					
					switch (pc.getRadomQuest()) {
					case 1:
						ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(Lineage.q1));
						ii.setCount(Lineage.qc1);
						pc.toGiveItem(null, ii, ii.getCount());
						break;
					case 2:
						ItemInstance ii2 = ItemDatabase.newInstance(ItemDatabase.find(Lineage.q2));
						ii2.setCount(Lineage.qc2);
						pc.toGiveItem(null, ii2, ii2.getCount());
						break;
					case 3:
						ItemInstance ii3 = ItemDatabase.newInstance(ItemDatabase.find(Lineage.q3));
						ii3.setCount(Lineage.qc3);
						pc.toGiveItem(null, ii3, ii3.getCount());
						break;

					default:
						break;
					}
					//완료후 처리 
					pc.setRandomQuestkill(0);
					pc.setRandomQuestCount(pc.getRandomQuestCount()+1);
					pc.setRadomQuest(0);
					pc.setRandomQuestPlay(0);
					AccountDatabase.updateuQuestKill((int) pc.getObjectId());			
					AccountDatabase.updatequestcount(pc.getRandomQuestCount(), (int) pc.getObjectId());
					ChattingController.toChatting(pc, String.format("\\fY [반복 퀘스트] 완료."), Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(pc, String.format("\\fY [반복 퀘스트] 보상이 지급되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
				} else {
					ChattingController.toChatting(pc, String.format("\\fY [반복 퀘스트] 남은 몬스터 %d 마리 ", monstkill - pc.getRandomQuestkill()), Lineage.CHATTING_MODE_MESSAGE);
				}


			}else{
				ChattingController.toChatting(pc, String.format("\\fY 오늘 수행 가능한 퀘스트를 전부 완료 하셨습니다."), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
		showHtml(pc);
	}
}