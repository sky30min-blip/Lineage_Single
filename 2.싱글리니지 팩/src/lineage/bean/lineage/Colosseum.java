package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.database.ServerDatabase;
import lineage.world.controller.ColosseumController;
import lineage.world.object.instance.MonsterInstance;

public class Colosseum {
	private String type;									// 말섬인지 기란인지 어디 콜롯세움인지 구분용.
	private String name;									// 이름.
	private int x;											// 
	private int y;											// 
	private int map;										// 콜로세움이 진행되는 맵아이디.
	private int maxStage;									// 최대 군 갯수.
	private int[] timeStart;								// 콜로세움 시작시간 목록.
	private int[] timeEnd;									// 각 군에따른 종료 시간 목록.
	private int[] timeCool;									// 각 군에따른 쿨타임 목록.
	private int[][] stageCount;								// 각 군에 나오는 몬스터 개체수 갯수 목록.
	private int[][] stageItemCount;							// 각 군에 나오는 아이템 갯수 목록.
	private int[] bossCount;								// 보스 개체수 갯수 목록.
	private Map<Integer, List<String>> list;				// 각 군에따라 스폰되는 몬스터 목록.
	private Map<Integer, List<String>> listItem;			// 각 군에 드랍할 아이템 목록.
	private List<String> list_boss;							// 각군에따른 보스 스폰 목록.
	private ColosseumController.COLOSSEUM_STATUS status;	// 현재 진행된 상태 확인용 변수.
	private List<MonsterInstance> list_spawn;				// 스폰된 몬스터 들.
	public int timer_ment_cnt;								// 멘트 처리에 사용되는 카운팅 변수. 어떤 멘트를 날릴지 파악용.
	public int nowStage;									// 현재 진행중인 군 값
	public long timer_time;									// 콜로세움 처리에 사용되는 시간값.
	public long timer_cool_time;							// 휴식 시간값.
	public int list_spawn_idx;								// 스폰된 몬스터 위치값.
	private List<String> info;								// 콜롯세움 html 에 정보 표현용.
	private int joinClass;									// 참가 가능 클래스
	private int joinSex;									// 참가 가능 성별
	private int joinMinLevel;								// 참가 가능 최저 레벨
	private int joinMaxLevel;								// 참가 가능 최고 레벨
	private boolean joinTeleport;							// 텔레포트 가능 유무
	private boolean joinResurrection;						// 부활 가능 유무
	private boolean joinPotion;								// 포션 사용 가능 유무
	private boolean joinHp;									// 자연회복 hp 가능 유무
	private boolean joinMp;									// 자연회복 mp 가능 유무
	private boolean joinSummon;								// 서먼/테이밍 몬스터 및 개 사용 가능 유무
	private boolean joinPvP;								// PvP 방식
	
	public Colosseum(){
		info = new ArrayList<String>();
		list_spawn = new ArrayList<MonsterInstance>();
		listItem = new HashMap<Integer, List<String>>();
		list = new HashMap<Integer, List<String>>();
		list_boss = new ArrayList<String>();
		status = ColosseumController.COLOSSEUM_STATUS.휴식;
		timer_cool_time = timer_time = timer_ment_cnt = nowStage = maxStage = list_spawn_idx = map = x = y = 0;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getMap() {
		return map;
	}

	public void setMap(int map) {
		this.map = map;
	}

	public int getMaxStage() {
		return maxStage;
	}

	public void setMaxStage(int maxStage) {
		this.maxStage = maxStage;
	}

	public ColosseumController.COLOSSEUM_STATUS getStatus() {
		return status;
	}

	public void setStatus(ColosseumController.COLOSSEUM_STATUS status) {
		this.status = status;
	}

	public int[] getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(int[] timeStart) {
		this.timeStart = timeStart;
	}

	public int[] getTimeCool() {
		return timeCool;
	}

	public void setTimeCool(int[] timeCool) {
		this.timeCool = timeCool;
	}
	
	public int[] getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(int[] timeEnd) {
		this.timeEnd = timeEnd;
	}

	public int[][] getStageCount() {
		return stageCount;
	}

	public int[][] getStageItemCount() {
		return stageItemCount;
	}

	public void setStageItemCount(int[][] stageItemCount) {
		this.stageItemCount = stageItemCount;
	}

	public int[] getBossCount() {
		return bossCount;
	}

	public void setBossCount(int[] bossCount) {
		this.bossCount = bossCount;
	}

	public void setStageCount(int[][] stageCount) {
		this.stageCount = stageCount;
	}

	public Map<Integer, List<String>> getList(){
		return list;
	}

	public Map<Integer, List<String>> getListItem(){
		return listItem;
	}

	public List<String> getListBoss(){
		return list_boss;
	}
	
	public List<MonsterInstance> getListSpawn(){
		return list_spawn;
	}
	
	public String toString(int h){
		StringBuffer sb = new StringBuffer();
		sb.append(h);
		sb.append(name);
		return sb.toString();
	}
	
	public List<String> getHtmlInfo(){
		info.clear();
		// 다음 경기시간
		int h = 0;
		for(int c_h : timeStart){
			if(ServerDatabase.getLineageTimeHour() <= c_h){
				h = c_h;
				break;
			}
		}
		if(h == 0)
			h = timeStart[0];
		info.add(String.format("%d시", h));
		// 참가 가능 클래스
		switch(joinClass){
			case 1:
				info.add("군주");
				break;
			case 2:
				info.add("기사");
				break;
			case 3:
				info.add("군주, 기사");
				break;
			case 4:
				info.add("요정");
				break;
			case 5:
				info.add("군주, 요정");
				break;
			case 6:
				info.add("기사, 요정");
				break;
			case 7:
				info.add("군주, 기사, 요정");
				break;
			case 8:
				info.add("법사");
				break;
			case 9:
				info.add("군주, 법사");
				break;
			case 10:
				info.add("기사, 법사");
				break;
			case 11:
				info.add("군주, 기사, 법사");
				break;
			case 12:
				info.add("요정, 법사");
				break;
			case 13:
				info.add("군주, 요정, 법사");
				break;
			case 14:
				info.add("기사, 요정, 법사");
				break;
			case 15:
				info.add("군주, 기사, 요정, 법사");
				break;
		}
		// 참가 가능 성별
		info.add(joinSex==0 ? "모두" : joinSex==1 ? "남성" : "여성");
		// 참가 가능 최저 레벨
		info.add(String.valueOf(joinMinLevel));
		// 참가 가능 최고 레벨
		info.add(String.valueOf(joinMaxLevel));
		// 텔레포트
		info.add(joinTeleport ? "가능" : "불가능");
		// 부활
		info.add(joinResurrection ? "가능" : "불가능");
		// 포션 사용
		info.add(joinPotion ? "가능" : "불가능");
		// 자연 HP 변화
		info.add(joinHp ? "가능" : "불가능");
		// 자연 MP 변화
		info.add(joinMp ? "가능" : "불가능");
		// 서먼/테이밍 몬스터 및 개 사용
		info.add(joinSummon ? "가능" : "불가능");
		// PvP 방식
		info.add(joinPvP ? "PvP" : "NonPvP");
		return info;
	}
	
	public List<String> getHtmlTopRank(){
		info.clear();
		for(int i=0 ; i<10 ; ++i){
			info.add(String.valueOf(i));
		}
		return info;
	}

	public int getJoinClass() {
		return joinClass;
	}

	public void setJoinClass(int joinClass) {
		this.joinClass = joinClass;
	}

	public int getJoinSex() {
		return joinSex;
	}

	public void setJoinSex(int joinSex) {
		this.joinSex = joinSex;
	}

	public int getJoinMinLevel() {
		return joinMinLevel;
	}

	public void setJoinMinLevel(int joinMinLevel) {
		this.joinMinLevel = joinMinLevel;
	}

	public int getJoinMaxLevel() {
		return joinMaxLevel;
	}

	public void setJoinMaxLevel(int joinMaxLevel) {
		this.joinMaxLevel = joinMaxLevel;
	}

	public boolean isJoinTeleport() {
		return joinTeleport;
	}

	public void setJoinTeleport(boolean joinTeleport) {
		this.joinTeleport = joinTeleport;
	}

	public boolean isJoinResurrection() {
		return joinResurrection;
	}

	public void setJoinResurrection(boolean joinResurrection) {
		this.joinResurrection = joinResurrection;
	}

	public boolean isJoinPotion() {
		return joinPotion;
	}

	public void setJoinPotion(boolean joinPotion) {
		this.joinPotion = joinPotion;
	}

	public boolean isJoinHp() {
		return joinHp;
	}

	public void setJoinHp(boolean joinHp) {
		this.joinHp = joinHp;
	}

	public boolean isJoinMp() {
		return joinMp;
	}

	public void setJoinMp(boolean joinMp) {
		this.joinMp = joinMp;
	}

	public boolean isJoinSummon() {
		return joinSummon;
	}

	public void setJoinSummon(boolean joinSummon) {
		this.joinSummon = joinSummon;
	}

	public boolean isJoinPvP() {
		return joinPvP;
	}

	public void setJoinPvP(boolean joinPvP) {
		this.joinPvP = joinPvP;
	}
}
