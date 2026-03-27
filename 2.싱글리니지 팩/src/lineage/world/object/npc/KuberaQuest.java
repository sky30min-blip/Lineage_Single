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

public class KuberaQuest extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		showHtml(pc);
	}
	//쿠베라 몬스터 퀘스트
	public void showHtml(PcInstance pc) {
		List<String> kqlist = new ArrayList<String>();
		
		kqlist.clear();
		
		int lastq = Lineage.lastquest;
		switch (pc.getQuestChapter()) {

		case 1:
			// 진행번호
			kqlist.add(String.format("%d", 1));
			kqlist.add(String.format("%d", lastq));
			// 퀘스트명
			kqlist.add(String.format("%s", "용던3층 소탕"));
			// 퀘스트목표
			kqlist.add(String.format("%s", "용던3층에서 몬스터 30마리 처치"));
			if (pc.getQuestKill() > 0) {
				// 처치몬스터
				kqlist.add(String.format("%d", pc.getQuestKill()));
			} else {
				kqlist.add(String.format("0"));
			}

			// 보상
			kqlist.add(String.format("%s", Lineage.q1));
			break;
		case 2:
			// 진행번호
			kqlist.add(String.format("%d", 2));
			kqlist.add(String.format("%d", lastq));
			// 퀘스트명
			kqlist.add(String.format("%s", "용던4층 소탕"));
			// 퀘스트목표
			kqlist.add(String.format("%s", "용던4층에서 몬스터 30마리 처치"));
			// 처치몬스터
			kqlist.add(String.format("%d", pc.getQuestKill()));
			// 보상
			kqlist.add(String.format("%s",Lineage.q2));
			break;
		case 3:
			// 진행번호
			kqlist.add(String.format("%d", 3));
			kqlist.add(String.format("%d", lastq));
			// 퀘스트명
			kqlist.add(String.format("%s", "용던5층 소탕"));
			// 퀘스트목표
			kqlist.add(String.format("%s", "용던5층에서 몬스터 30마리 처치"));
			// 처치몬스터
			kqlist.add(String.format("%d", pc.getQuestKill()));
			// 보상
			kqlist.add(String.format("%s", Lineage.q3));
			break;

		default:
			break;
		}

		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kquest", null, kqlist));

	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		int monstkill = 0;
	
		switch (pc.getQuestChapter()) {
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
		if (action.equalsIgnoreCase("kquest-finish")) {
			
			if(pc.getQuestChapter() == 0) {
				ChattingController.toChatting(pc, String.format("\\fY [퀘스트] 완료 가능한 퀘스트가 없습니다."), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			if (pc.getQuestChapter() < Lineage.lastquest) {

				if (pc.getQuestKill() >= monstkill) {
					
					switch (pc.getQuestChapter()) {
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

					pc.setQuestChapter(pc.getQuestChapter() + 1);
					pc.setQuestKill(0);
					AccountDatabase.updateuQuestKill((int) pc.getObjectId());

					
					AccountDatabase.updateQuestChapter(pc.getQuestChapter(), (int) pc.getObjectId());
					ChattingController.toChatting(pc, String.format("\\fY 퀘스트 완료."), Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(pc, String.format("\\fY 퀘스트 보상이 지급되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
				} else {
					ChattingController.toChatting(pc, String.format("\\fY 남은 몬스터 %d 마리 ", monstkill - pc.getQuestKill()), Lineage.CHATTING_MODE_MESSAGE);
				}


			}
		}
		showHtml(pc);
	}
}