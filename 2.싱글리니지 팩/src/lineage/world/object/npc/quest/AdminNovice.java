package lineage.world.object.npc.quest;

import lineage.bean.database.Exp;
import lineage.bean.database.Npc;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Quest;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;
import lineage.world.object.magic.EnchantWeapon;
import lineage.world.object.magic.Haste;

public class AdminNovice extends QuestInstance {
	
	public AdminNovice(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		// 퀘스트 추출.
		Quest q = QuestController.find(pc, Lineage.QUEST_NOVICE);
		if(q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_NOVICE);
		
		// 버프
		Haste.onBuff(pc, SkillDatabase.find(7, 5));
		Skill s = SkillDatabase.find(2, 3);
		EnchantWeapon.onBuff(pc, pc.getInventory().getSlot(Lineage.SLOT_WEAPON), s, s.getBuffDuration());
		
		// 구분 처리.
		if(pc.getLevel()<5){
			if(pc.getLevel() == 2){
				// 경험치 처리.
				Exp e = ExpDatabase.find( pc.getLevel() );
				pc.setExp( e.getBonus() );
			}
			// 퀘스트 변수 변경.
			q.setQuestStep(2);
			// 안내창
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "admin2"));
			return;
		}
		if(pc.getLevel()>=5 && q.getQuestStep()==2){
			// 퀘스트 변수 변경.
			q.setQuestStep(3);
			// 안내창
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "admin3"));
			return;
		}
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "admin1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		// 퀘스트 추출.
		Quest q = QuestController.find(pc, Lineage.QUEST_NOVICE);
		if(q == null)
			return;
		
		if(action.equalsIgnoreCase("A") && q.getQuestStep()==3){
			if(pc.getLevel() == 5){
				// 경험치 처리.
				Exp e = ExpDatabase.find( pc.getLevel() );
				pc.setExp( e.getBonus() );
			}
			// 퀘스트 변수 변경
			q.setQuestStep(4);
			// 장비 지급
			CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 투구"), 1, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 갑옷"), 1, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 장갑"), 1, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 샌달"), 1, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 방패"), 1, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 30, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 확인 주문서"), 20, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 5, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 50, true);
			CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 속도 향상 물약"), 5, true);
			// 안내창
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "admin1"));
		}
	}

}
