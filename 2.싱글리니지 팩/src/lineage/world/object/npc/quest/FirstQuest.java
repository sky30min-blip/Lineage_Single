package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.database.ItemDatabase;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class FirstQuest extends QuestInstance {

	public FirstQuest(Npc npc){
		super(npc);
	}
	
	/**
	 * 말하는 두루마리 퀘스트 진행중에
	 * 어떤 마을에 어떤 엔피씨에게 가야할지
	 * 출력하는 html 태그뒤에 번호를 리턴함.
	 * @param current_step
	 * @param quest_step
	 * @return
	 */
	protected int toErrorHtml(int current_step, int quest_step) {
		return 14;
	}
	
	/**
	 * 첫번째 아이템 지급 처리.
	 * @param pc
	 */
	protected void toStep1(PcInstance pc){
		// 공통 추가 부분.
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("말하는 섬 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 확인 주문서"), 22, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 갑옷"), 1, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 투구"), 1, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 샌달"), 1, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽장갑"), 1, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 단검"), 1, true);
		
		// 클레스별 추가 부분.
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 한손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 양손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 도끼"), 1, true);
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 한손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 양손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 도끼"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 석궁"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 1000, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 창"), 1, true);
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 1000, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 한손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 석궁"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 활"), 1, true);
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 지팡이"), 1, true);
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 1000, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 한손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 석궁"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 크로우"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 이도류"), 1, true);
				break;
		}
	}
	
	/**
	 * @param pc
	 */
	protected void toStep2(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("글루딘 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep3(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("켄트 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep4(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("우드벡 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep5(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화전민 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep6(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("요정숲 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep7(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("은기사 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑 확인 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 반지"), 1, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep8(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("기란 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑 확인 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep9(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("하이네 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑 확인 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 저주 풀기 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep10(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("오렌 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑 확인 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 저주 풀기 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep11(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("웰던 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑 확인 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 저주 풀기 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 변신 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep12(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("아덴 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑 확인 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 저주 풀기 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 변신 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep13(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("침묵의 동굴 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑 확인 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 저주 풀기 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 변신 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 속도향상 물약"), 5, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
	/**
	 * @param pc
	 */
	protected void toStep14(PcInstance pc){
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 2, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑 확인 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 저주 풀기 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 변신 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 속도향상 물약"), 5, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 티셔츠"), 1, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 100, true);
	}
	
}
