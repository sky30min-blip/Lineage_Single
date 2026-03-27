package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_SkillDelete extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int lv[]){
		if(bp == null)
			bp = new S_SkillDelete(lv);
		else
			((S_SkillDelete)bp).toClone(lv);
		return bp;
	}
	
	public S_SkillDelete(int lv[]){
		toClone(lv);
	}
	
	public void toClone(int lv[]){
		clear();
		
		writeC(Opcodes.S_OPCODE_SKILLDELETE);
		writeC(lv.length);	// 스킬 갯수
		writeC(lv[0]);	// 1단계
		writeC(lv[1]);	// 2단계
		writeC(lv[2]);	// 3단계
		writeC(lv[3]);	// 4단계
		writeC(lv[4]);	// 5단계
		writeC(lv[5]);	// 6단계
		writeC(lv[6]);	// 7단계
		writeC(lv[7]);	// 8단계
		writeC(lv[8]);	// 9단계
		writeC(lv[9]);	// 10단계
		// 기사 마법
		writeC(lv[10]);	// 64 - 쇼크스턴 128 - 리딕아머 192 - 쇼크스턴+리딕아머
		writeC(lv[11]);	// 1 - 바운스 어택
		// 다크엘프 마법
		writeC(lv[12]);	// 15마법과 30마법 함께 포함 1-블라인드하이딩 2-인첸트베놈 4-쉐도우아머 8-브링스톤 16-무빙악셀레이션 32-버닝스피릿츠 64-다크블라인드 128-베놈레지스트 
		writeC(lv[13]);	// 45마법과 50마법 함께 포함 1-더블브레이크 2-언케니닷지 4-쉐도우팽 8-파이널번 16-드레스마이티 32-드레스덱스터리티 64-드레스이베이젼 
		// 군주 마법
		writeC(lv[14]);	// 7단계	1-트루타겟 2-글로잉오라 4-샤이닝오라 8-콜클렌
		writeC(lv[15]);	// 아직 없는듯
		// 요정스킬
		writeC(lv[16]);	// 정령마법 1단계	1-레지스트매직 2-바디투마인드 4-텔레포트투마더
		writeC(lv[17]);	// 정령마법 2단계	1-클리어마인드 2-레지스트엘리멘트
		writeC(lv[18]);	// 정령마법 3단계	1-리턴투네이처 2-블러드투소울 4-프로텍션프롬엘리멘트 8-파이어웨폰 16-윈드샷 32-윈드워크 64-어스스킨 128-인탱글
		writeC(lv[19]);	// 정령마법 4단계	1-이레이즈매직 2-서먼레서엘리멘탈 4-블레스오브파이어 8-아이오브스톰 16-어스바인드 32-네이쳐스터치 64-블레스오브어스
		writeC(lv[20]);	// 정령마법 5단계	1-에어리어오브사일런스 2-서먼그레이터엘리멘탈 4-버닝웨폰 8-네이쳐스블레싱 16-콜오브네이쳐 32-스톰샷 64-윈드세클 128-아이언스킨
		writeC(lv[21]);	// 정령마법 6단계	1-엔조틱바이탈라이즈 2-워터라이프 4-엘리멘탈파이어 8-스톰워크
		// 용기사
		writeC(lv[22]);	// 16-드래곤스킨 32-버닝슬래쉬 64-가드브레이크 128-마그마브레스
		writeC(lv[23]);	// 1-각성안타라스 2-블러드러스트 4-포우슬레이어  8-피어 16-쇼크스킨  32-각성파푸리온  64-모탈바디 128-썬더그랩 
		writeC(lv[24]);	// 1-호러오브데스 2-프리징브레스 4-각성발라카스
		// 환술사
		writeC(lv[25]);	// 1-미러이미지 2-컨퓨전 4-스매쉬 8-일루션오우거  16-큐브이그니션 32-컨센트레이션 64-마인드브레이크 128-본브레이크
		writeC(lv[26]);	// 1-일루션리치 2-큐브퀘이크 4-페이션스 8-판타즘 16-암브레이커 32-일루션다이아골렘 64-큐브쇼크 128-인사이트
		writeC(lv[27]);	// 1-패닉 2-조이오브페인 4-일루션아바타 8-큐브밸런스
	}
}
