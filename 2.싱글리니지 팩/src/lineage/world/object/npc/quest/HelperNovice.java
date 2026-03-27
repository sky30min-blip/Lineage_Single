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
import lineage.world.object.magic.Heal;

public class HelperNovice extends QuestInstance {

	public HelperNovice(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		// 퀘스트 추출.
		Quest q = QuestController.find(pc, Lineage.QUEST_NOVICE);
		if(q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_NOVICE);

		// 버프
		Skill s = SkillDatabase.find(3, 2);
		Heal.onBuff(this, pc, s, s.getCastGfx(), pc.getTotalHp());
		Haste.onBuff(pc, SkillDatabase.find(7, 2));
		s = SkillDatabase.find(2, 3);
		EnchantWeapon.onBuff(pc, pc.getInventory().getSlot(Lineage.SLOT_WEAPON), s, s.getBuffDuration());

		// 구분 처리.
		if(pc.getLevel()==1 && q.getQuestStep()==0){
			// 경험치 처리.
			Exp e = ExpDatabase.find( pc.getLevel() );
			pc.setExp( e.getBonus() );
			// 아이템 처리.
			CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 5, true);
			// 퀘스트 변수 변경.
			q.setQuestStep(1);
			// 안내창
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutor"));
		}else{
			// 안내창
			switch(pc.getClassType()){
				case Lineage.LINEAGE_CLASS_ROYAL:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorp"));
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutork"));
					break;
				case Lineage.LINEAGE_CLASS_ELF:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutore"));
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorm"));
					break;
				case Lineage.LINEAGE_CLASS_DARKELF:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutord"));
					break;
				case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutordk"));
					break;
				case Lineage.LINEAGE_CLASS_BLACKWIZARD:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutori"));
					break;
			}
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		// 퀘스트 추출.
		Quest q = QuestController.find(pc, Lineage.QUEST_NOVICE);
		if(q == null)
			return;

		if(action.equalsIgnoreCase("l")){
			toTalk(pc, null);
		}else if(action.equalsIgnoreCase("A")){
			toRoyal(pc);
		}else if(action.equalsIgnoreCase("B")){
			toKnight(pc);
		}else if(action.equalsIgnoreCase("C")){
			toElf(pc);
		}else if(action.equalsIgnoreCase("D")){
			toWizard(pc);
		}else if(action.equalsIgnoreCase("E")){
			toDarkElf(pc);
		}else if(action.equalsIgnoreCase("F")){
			// 용기사인데 기사랑 패턴이 같음.
			toKnight(pc);
		}else if(action.equalsIgnoreCase("G")){
			toBlackWizard(pc);
		}else if(action.equalsIgnoreCase("O")){
			// 마을 서쪽 근교
			pc.toPotal(32605, 32837, 2005);
		}else if(action.equalsIgnoreCase("P")){
			// 마을 동쪽 근교
			pc.toPotal(32605, 32837, 2005);
		}else if(action.equalsIgnoreCase("Q")){
			// 마을 남서쪽 사냥터
			pc.toPotal(32605, 32837, 2005);
		}else if(action.equalsIgnoreCase("R")){
			// 마을 남동쪽 사냥터
			pc.toPotal(32733, 32902, 2005);
		}else if(action.equalsIgnoreCase("S")){
			// 마을 북동쪽 사냥터
			pc.toPotal(32802, 32803, 2005);
		}else if(action.equalsIgnoreCase("T")){
			// 마을 북서쪽 사냥터
			pc.toPotal(32642, 32763, 2005);
		}else if(action.equalsIgnoreCase("U")){
			// 마을 서쪽 사냥터
			pc.toPotal(32605, 32837, 2005);
		}else if(action.equalsIgnoreCase("V")){
			// 마을 남쪽 사냥터
			pc.toPotal(32605, 32837, 2005);
		}else if(action.equalsIgnoreCase("W")){
			// 마을 동쪽 사냥터
			pc.toPotal(32605, 32837, 2005);
		}else if(action.equalsIgnoreCase("X")){
			// 마을 북쪽 사냥터
			pc.toPotal(32605, 32837, 2005);
		}else if(action.equalsIgnoreCase("L")){
			// 상아탑
			pc.toPotal(34041, 32155, 4);
		}else if(action.equalsIgnoreCase("M")){
			// 다엘 세디아
			pc.toPotal(32878, 32905, 304);
		}else if(action.equalsIgnoreCase("N")){
			// 환술사 스비엘
			pc.toPotal(32760, 32885, 1000);
		}else if(action.equalsIgnoreCase("H")){
			// 말하는섬 창고
			pc.toPotal(32572, 32945, 0);
		}else if(action.equalsIgnoreCase("K")){
			// 게렝
			pc.toPotal(32562, 33082, 0);
		}else if(action.equalsIgnoreCase("J")){
			// 숨계던젼
			pc.toPotal(32872, 32871, 86);
		}
	}

	private void toKnight(PcInstance pc){
		if(pc.getLevel() < 5)
			// 허수아비 안내
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutordk1"));
		else if(pc.getLevel() < 6)
			// 창고
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutordk2"));
		else if(pc.getLevel() < 8)
			// 그냥 칭찬
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutordk3"));
		else if(pc.getLevel() < 10)
			// 그냥 칭찬
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutordk4"));
		else
			// 
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutordk5"));
	}

	private void toBlackWizard(PcInstance pc){
		if(pc.getLevel() < 5)
			// 허수아비 안내
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutori1"));
		else if(pc.getLevel() < 6)
			// 창고
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutori2"));
		else if(pc.getLevel() < 10)
			// 그냥 칭찬
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutori3"));
		else if(pc.getLevel() < 12)
			// 환술사 1단계마법
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutori4"));
		else
			// 
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutori5"));
	}

	private void toDarkElf(PcInstance pc){
		if(pc.getLevel() < 5)
			// 허수아비 안내
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutord1"));
		else if(pc.getLevel() < 6)
			// 창고
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutord2"));
		else if(pc.getLevel() < 8)
			// 그냥 칭찬
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutord3"));
		else if(pc.getLevel() < 10)
			// 그냥 칭찬
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutord4"));
		else if(pc.getLevel() < 13)
			// 1단계 일반마법
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutord5"));
		else
			// 
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutord6"));
	}

	private void toElf(PcInstance pc){
		if(pc.getLevel() < 5)
			// 허수아비 안내
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutore1"));
		else if(pc.getLevel() < 8)
			// 창고
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutore2"));
		else if(pc.getLevel() < 9)
			// 1단계 일반마법
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutore3"));
		else if(pc.getLevel() < 10)
			// 그냥 칭찬
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutore4"));
		else if(pc.getLevel() < 11)
			// 1단계 요정마법
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutore5"));
		else
			// 
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutore6"));
	}

	private void toRoyal(PcInstance pc){
		if(pc.getLevel() < 5)
			// 허수아비 안내
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorp1"));
		else if(pc.getLevel() < 7)
			// 창고/ 혈맹창설 ㅋ
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorp2"));
		else if(pc.getLevel() < 10)
			// 칭찬
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorp3"));
		else if(pc.getLevel() < 13)
			// 1단계 일반마법
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorp4"));
		else
			// 
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorp5"));
	}

	private void toWizard(PcInstance pc){
		if(pc.getLevel() < 4)
			// 허수아비 안내
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorm1"));
		else if(pc.getLevel() < 5)
			// 1단계 마법
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorm2"));
		else if(pc.getLevel() < 8)
			// 창고
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorm3"));
		else if(pc.getLevel() < 12)
			// 2단계 마법
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorm4"));
		else if(pc.getLevel() < 13)
			// 3단계 마법
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorm5"));
		else
			// 
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tutorm6"));
	}


}
