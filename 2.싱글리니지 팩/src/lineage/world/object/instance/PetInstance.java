package lineage.world.object.instance;

import java.sql.Connection;

import lineage.bean.database.Exp;
import lineage.bean.database.Monster;
import lineage.database.DatabaseConnection;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectAdd;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SummonController;
import lineage.world.object.object;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.etc.EvolutionFruit;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;

public class PetInstance extends SummonInstance {

	// 배고픔상태 목록.
	static public enum PET_FOOD_MODE {
			Veryhungry,				// 아주 배고픔
			Littlehungry,			// 약간 배고픔
			NeitherHungryNorFull,	// 보통
			LittleFull,				// 배부름
			VeryFull,				// 아주 배부름
	};

	private PET_FOOD_MODE food_mode;
	private int food_counter;
	private DogCollar collar;
	
	static synchronized public PetInstance clone(PetInstance pi, Monster m){
		if(pi == null)
			pi = new PetInstance();
		pi.setMonster(m);
		// 휴식모드로 전환
		pi.setSummonMode(SUMMON_MODE.Rest);
		// 배고픔상태 변경
		pi.setFoodMode(PET_FOOD_MODE.NeitherHungryNorFull);
		// 자연회복을 위해 등록.
		CharacterController.toWorldJoin(pi);
		return pi;
	}
	
	@Override
	public void close(){
		super.close();
		food_counter = 0;
		collar = null;
	}
	
	public PetInstance(){
		food_mode = PET_FOOD_MODE.NeitherHungryNorFull;
		collar = null;
		food_counter = 0;
	}

	public PET_FOOD_MODE getFoodMode() {
		return food_mode;
	}

	public void setFoodMode(PET_FOOD_MODE food_mode) {
		this.food_mode = food_mode;
	}

	public DogCollar getCollar() {
		return collar;
	}

	public void setCollar(DogCollar collar) {
		this.collar = collar;
	}

	@Override
	public void toExp(object o, double exp){
		// 배율에따른 경험치 증가.
		exp *= Lineage.rate_exp_pet;
		// 레벨별 지급될 경험치 감소.
		if(getLevel()>64){
			if(getLevel()<10)
				exp = exp / 4;
			else if(getLevel()<20)
				exp = exp / 8;
			else if(getLevel()<30)
				exp = exp / 12;
			else if(getLevel()<40)
				exp = exp / 16;
			else if(getLevel()<50)
				exp = exp / 20;
			else
				exp = exp / 50;
		}
		setExp(getExp() + exp);
	}

	@Override
	public void setExp(double exp){
		if(isDead())
			return;
		
		Exp max = ExpDatabase.find(Lineage.pet_level_max);

		if(max!=null && exp>0 && level<max.getLevel()){
			Exp e = ExpDatabase.find(level);
			if(max.getBonus()-max.getExp()>exp)
				super.setExp(exp);
			else
				super.setExp(max.getBonus()-max.getExp());
			if(e != null){
				boolean lvUp = e.getBonus() <= exp;
				if(lvUp){
					int hp = CharacterController.toStatusUP(this, true);
					int mp = CharacterController.toStatusUP(this, false);
					for(int i=1 ; i <= Lineage.pet_level_max ; i++){
						e = ExpDatabase.find(i);
						if(getExp() < e.getBonus())
							break;
					}
					for(int i=e.getLevel()-level ; i > 1 ; i--){
						hp += CharacterController.toStatusUP(this, true);
						mp += CharacterController.toStatusUP(this, false);
					}
					hp = getMaxHp()+hp;
					mp = getMaxMp()+mp;

					if(hp >= 1000)
						hp = 1000;
					if(mp >= 500)
						mp = 500;

					maxHp = hp;
					maxMp = mp;
					setNowHp(getTotalHp());
					setNowMp(getTotalMp());
					level = e.getLevel();

					if(summon!=null && summon.getMaster()!=null){
						if(Lineage.server_version>144)
							summon.getMaster().toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), this, true));
						// \f1%0의 레벨이 올랐습니다.
						summon.getMaster().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 320, getName()));
						summon.getMaster().toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 2127));
					}
				}
			}
		}
	}
	
	@Override
	public void toGiveItem(object o, ItemInstance item, long count) {
		long time = System.currentTimeMillis(); 
		if(isDead())
			return;
		
//		if ( !getInventory().isAppendItem(item, count)) {
//			ChattingController.toChatting(o, "아이템을 더 이상 줄 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
//			return;
//		}
		// 서먼관리중일때만 처리.
		if (summon != null && count == 1) {
			switch (item.getItem().getNameIdNumber()) {
			case 23: // 고기
			case 623: // 괴물눈 고기
				o.getInventory().count(item, item.getCount() - count, true);
				food_mode = PET_FOOD_MODE.VeryFull;
				return;
			}
			
			//쿠베라 펫장비
			if (item.getItem().getName().contains("이빨")) {
				Connection con = null;
				try {
					con = DatabaseConnection.getLineage();
					
					if(!checkString(SummonController.selectPetWeapon(this))){
					
						ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(SummonController.selectPetWeapon(this)));
						ii.setDefinite(true);
						ii.setCount(1);
						o.toGiveItem(null, ii, ii.getCount());
						if(this.getName().contains("$")){
							ChattingController.toChatting(o, String.format("펫(%s)에 무기 %s가 장착 해제 되고", this.getMonster().getName(),SummonController.selectPetWeapon(this)), Lineage.CHATTING_MODE_MESSAGE);
							ChattingController.toChatting(o, String.format("펫(%s)에 무기 %s로 교체 되었습니다", this.getMonster().getName(),item.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
						}else{
							ChattingController.toChatting(o, String.format("펫(%s)에 무기 %s가 장착 해제 되고", this.getName(),SummonController.selectPetWeapon(this)), Lineage.CHATTING_MODE_MESSAGE);
							ChattingController.toChatting(o, String.format("펫(%s)에 무기 %s로 교체 되었습니다", this.getName(),item.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
						}
						
						this.setPetWeapon(item.getItem().getName());
						SummonController.updatePetWeapon(con, this);
					
						
						o.getInventory().count(item, item.getCount() - count, true);
							
						
					}else{

						this.setPetWeapon(item.getItem().getName());
						SummonController.updatePetWeapon(con, this);
						ChattingController.toChatting(o, String.format("펫 무기 %s가 장착되었습니다", item.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
						o.getInventory().count(item, item.getCount() - count, true);
					}
				
					
				} catch (Exception e) {
					lineage.share.System.println(PetInstance.class+" : toAi(long time)");
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con);
				}
			}
			if (item.getItem().getName().contains("펫아머")) {
				Connection con = null;
				try {
					con = DatabaseConnection.getLineage();
					
					if(!checkString(SummonController.selectPetArmor(this))){
					
						ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(SummonController.selectPetArmor(this)));
						ii.setDefinite(true);
						ii.setCount(1);
						o.toGiveItem(null, ii, ii.getCount());
		
						if(this.getName().contains("$")){
							ChattingController.toChatting(o, String.format("펫(%s)에 방어구 %s가 장착 해제 되고", this.getMonster().getName(),SummonController.selectPetArmor(this)), Lineage.CHATTING_MODE_MESSAGE);
							ChattingController.toChatting(o, String.format("펫(%s)에 방어구 %s로 교체 되었습니다", this.getMonster().getName(),item.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
						}else{
							ChattingController.toChatting(o, String.format("펫(%s)에 방어구 %s가 장착 해제 되고", this.getName(),SummonController.selectPetArmor(this)), Lineage.CHATTING_MODE_MESSAGE);
							ChattingController.toChatting(o, String.format("펫(%s)에 방어구 %s로 교체 되었습니다", this.getName(),item.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
						}
						this.setPetArmor(item.getItem().getName());
						SummonController.updateArmor(con, this);
					
						
						o.getInventory().count(item, item.getCount() - count, true);
							
						
					}else{

						this.setPetArmor(item.getItem().getName());
						SummonController.updateArmor(con, this);
						ChattingController.toChatting(o, String.format("펫 방어구 %s가 장착되었습니다", item.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
						o.getInventory().count(item, item.getCount() - count, true);
					}
				
					
				} catch (Exception e) {
					lineage.share.System.println(PetInstance.class+" : toAi(long time)");
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con);
				}
			}
			// 진화의 열매
			if (item instanceof EvolutionFruit) {
				if (o.getGm() == 0 && getLevel() < 30) {
					ChattingController.toChatting(o, "30레벨 이상 진화가 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				item.toClick(this, null);
				o.getInventory().count(item, item.getCount() - count, true);
				return;
			}
		}
		

        String msg = String.format("[펫템넣기] %s -> %s (아이템: %s 갯수: %d)", summon.getMaster().getName(), getName(),item.getItem().getName(),count);
			String timeString = Util.getLocaleString(time, true);
			String log = String.format("[%s]\t %s", timeString, msg);
			
			GuiMain.display.asyncExec(new Runnable() {
				public void run() {
					GuiMain.getViewComposite().getpetComposite().toLog(log);
				}
			});
		super.toGiveItem(o, item, count);
	}
	boolean checkString(String str) {
		return str == null || str.isEmpty();
		}
	@Override
	public void toAi(long time){
		if(summon!=null && summon.getMaster()==null){		
			// 인벤토리에 귀환주문서 있는지 확인한후 처리.
			ItemInstance ii = getInventory().findDbNameId(505);
			ItemInstance iii = getInventory().findDbNameId(39);
			
	
			
			if(ii!=null || iii!=null){
				// 귀환주문서 제거.
				if(ii != null)
					getInventory().count(ii, ii.getCount()-1, false);
				else if(iii != null)
					getInventory().count(iii, iii.getCount()-1, false);
				
				Connection con = null;
				try {
					con = DatabaseConnection.getLineage();
					// 펫 저장.
					SummonController.updatePet(con, this);
					// 펫 제거.
					summon.remove(this);
					toAiClean(true);
				} catch (Exception e) {
					lineage.share.System.println(PetInstance.class+" : toAi(long time)");
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con);
				}
				
				// 목걸이 정보 변경.
				if(collar != null)
					collar.setPetSpawn(false);
			}
		}
		super.toAi(time);
	}
	
	@Override
	public void toAiClean(boolean packet){
		// 인벤토리 아이템 땅에 드랍.
		for(ItemInstance item : getInventory().getList()){
			getInventory().toDrop(item, item.getCount(), x, y, false);
			//쿠베라펫
		   	getInventory().remove(item, true);
		}

		super.toAiClean(packet);
		
	
	}

	@Override
	public void toTimer() {
		
		long time= System.currentTimeMillis();
		
		// summon 객체가 null 이라면 관리목록에서 떠난것이므로 일반 몬스터 처럼 처리.
		if (summon == null)
			return;
		// 죽은상태라면 무시.
		if (isDead())
			return;

		// 10분마다 food상태 낮추기.
		if (Lineage.is_pet_hungry && (++food_counter % 600) == 0) {
			food_counter = 0;
			switch (food_mode) {
			case Veryhungry:
				ChattingController.toChatting(this, "펫이 도망가 버렸습니다.",0);
				// 디비에 del 칼럼 true 로 변경.
				SummonController.deletePet(this);
				//쿠베라펫 로그 기록
				  String msg = String.format("[펫도망] %s -> %s (사유: 많이 배고픔 )", summon.getMaster().getName(), getName());
						String timeString = Util.getLocaleString(time, true);
						String log = String.format("[%s]\t %s", timeString, msg);
						
						GuiMain.display.asyncExec(new Runnable() {
							public void run() {
								GuiMain.getViewComposite().getpetComposite().toLog(log);
							}
						});
				// 펫 도망가기 처리.
				summon.remove(this);
				homeX = x;
				homeY = y;
				homeMap = map;
				toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, this), false);
				
	
				break;

				 
			case Littlehungry:
				food_mode = PET_FOOD_MODE.Veryhungry;
				ChattingController.toChatting(this, "주인님 배가 많이 고파요!",0);
				break;
			case NeitherHungryNorFull:
				food_mode = PET_FOOD_MODE.Littlehungry;
				break;
			case LittleFull:
				ChattingController.toChatting(this, "주인님 약간 배가 고파요!",0);
				food_mode = PET_FOOD_MODE.NeitherHungryNorFull;
				
				break;
			case VeryFull:
				ChattingController.toChatting(this, "멍멍~! 배불러요!",0);
				food_mode = PET_FOOD_MODE.LittleFull;
				break;
			}
		}
	}
	
	@Override
	public void setAiStatus(int ai_status){
		// 시체유지중 재스폰 으로 전환되는 시점이라면 제거된걸로 간주하여 디비에 del 칼럼 true 로 변경.
		if(getAiStatus()==Lineage.AI_STATUS_CORPSE && ai_status==Lineage.AI_STATUS_SPAWN)
			SummonController.deletePet(this);
		
		super.setAiStatus(ai_status);
	}
	
	@Override
	protected void toWhistleMent(){
		if(level<12)
			return;
		
		String ment = "";
		switch(getMonster().getNameIdNumber()){
		case 268:	// 늑대
		case 904:	// 세인트 버나드
		case 905:	// 도베르만
		case 4072:       // 아기진돗개
		case 4079:  // 아기 캥거루
		case 4077:  // 아기 판다곰
		case 906:	// 콜리
		case 907:	// 세퍼드
		case 908:	// 비글
		case 1788:	// 허스키
		case 3041:	// 호랑이
			if(level>=12 && level<=23) {
				ment = "$1088";
			} else if(level>=24 && level<=35) {
				ment = "$1089";
			} else if(level>=36 && level<=47) {
				ment = "$1090";
			} else if(level>=48 && level<=49) {
				ment = "$1091";
			} else {
				ment = "$1092";
			}
			break;
		case 4078:	// 공포판다곰
			if(level>=12 && level<=23) {
				ment = "$1088";
			} else if(level>=24 && level<=35) {
				ment = "$1089";
			} else if(level>=36 && level<=47) {
				ment = "$1090";
			} else if(level>=48 && level<=49) {
				ment = "$1091";
			} else {
				ment = "$1092";
			}
			break;
		case 4080:	// 불꽃의캥거루
			if(level>=12 && level<=23) {
				ment = "$1088";
			} else if(level>=24 && level<=35) {
				ment = "$1089";
			} else if(level>=36 && level<=47) {
				ment = "$1090";
			} else if(level>=48 && level<=49) {
				ment = "$1091";
			} else {
				ment = "$1092";
			}
			break;
		case 4073: //진돗개	
			if(level>=12 && level<=23) {
				ment = "$1088";
			} else if(level>=24 && level<=35) {
				ment = "$1089";
			} else if(level>=36 && level<=47) {
				ment = "$1090";
			} else if(level>=48 && level<=49) {
				ment = "$1091";
			} else {
				ment = "$1092";
			}
			break;
		case 1397:	// 여우
			if(level>=12 && level<=23) {
				ment = "$2733";
			} else if(level>=24 && level<=35) {
				ment = "$2734";
			} else if(level>=36 && level<=47) {
				ment = "$2735";
			} else if(level>=48) {
				ment = "$2736";
			}
			break;
		case 1495:	// 곰
			if(level>=12 && level<=23) {
				ment = "$2738";
			} else if(level>=24 && level<=35) {
				ment = "$2739";
			} else if(level>=36 && level<=47) {
				ment = "$2740";
			} else if(level>=48) {
				ment = "$2741";
			}
			break;
		case 2563:	// 열혈토끼
			if(level>=12 && level<=23) {
				ment = "$2723";
			} else if(level>=24 && level<=35) {
				ment = "$2724";
			} else if(level>=36 && level<=47) {
				ment = "$2725";
			} else if(level>=48) {
				ment = "$2726";
			}
			break;
		case 2699:	// 하이 허스키
			if(level>=12 && level<=23) {
				ment = "$2815";
			} else if(level>=24 && level<=35) {
				ment = "$2816";
			} else if(level>=36 && level<=47) {
				ment = "$2817";
			} else if(level>=48) {
				ment = "$2818";
			}
			break;
		case 2692:	// 하이 도베르만
			if(level>=12 && level<=23) {
				ment = "$2811";
			} else if(level>=24 && level<=35) {
				ment = "$2812";
			} else if(level>=36 && level<=47) {
				ment = "$2813";
			} else if(level>=48) {
				ment = "$2814";
			}
			break;
		case 2701:	// 고양이
			if(level>=12 && level<=23) {
				ment = "$2728";
			} else if(level>=24 && level<=35) {
				ment = "$2729";
			} else if(level>=36 && level<=47) {
				ment = "$2730";
			} else if(level>=48) {
				ment = "$2731";
			}
			break;
		case 2698:	// 하이 비글
			if(level>=12 && level<=23) {
				ment = "$2847";
			} else if(level>=24 && level<=35) {
				ment = "$2848";
			} else if(level>=36 && level<=47) {
				ment = "$2849";
			} else if(level>=48) {
				ment = "$2850";
			}
			break;
		case 2697:	// 하이 래빗
			if(level>=12 && level<=23) {
				ment = "$2843";
			} else if(level>=24 && level<=35) {
				ment = "$2844";
			} else if(level>=36 && level<=47) {
				ment = "$2845";
			} else if(level>=48) {
				ment = "$2846";
			}
			break;
		case 2696:	// 하이 폭스
			if(level>=12 && level<=23) {
				ment = "$2835";
			} else if(level>=24 && level<=35) {
				ment = "$2836";
			} else if(level>=36 && level<=47) {
				ment = "$2837";
			} else if(level>=48) {
				ment = "$2838";
			}
			break;
		case 2702:	// 하이 캣
			if(level>=12 && level<=23) {
				ment = "$2839";
			} else if(level>=24 && level<=35) {
				ment = "$2840";
			} else if(level>=36 && level<=47) {
				ment = "$2841";
			} else if(level>=48) {
				ment = "$2842";
			}
			break;
		case 2695:	// 하이 세인트 버나드
			if(level>=12 && level<=23) {
				ment = "$2851";
			} else if(level>=24 && level<=35) {
				ment = "$2852";
			} else if(level>=36 && level<=47) {
				ment = "$2853";
			} else if(level>=48) {
				ment = "$2854";
			}
			break;
		case 2694:	// 하이 세퍼드
			if(level>=12 && level<=23) {
				ment = "$2819";
			} else if(level>=24 && level<=35) {
				ment = "$2820";
			} else if(level>=36 && level<=47) {
				ment = "$2821";
			} else if(level>=48) {
				ment = "$2822";
			}
			break;
		case 2693:	// 하이 베어
			if(level>=12 && level<=23) {
				ment = "$2831";
			} else if(level>=24 && level<=35) {
				ment = "$2832";
			} else if(level>=36 && level<=47) {
				ment = "$2833";
			} else if(level>=48) {
				ment = "$2834";
			}
			break;
		case 2703:	// 하이 울프
			if(level>=12 && level<=23) {
				ment = "$2823";
			} else if(level>=24 && level<=35) {
				ment = "$2824";
			} else if(level>=36 && level<=47) {
				ment = "$2825";
			} else if(level>=48) {
				ment = "$2826";
			}
			break;
		case 2704:	// 하이 콜리
			if(level>=12 && level<=23) {
				ment = "$2827";
			} else if(level>=24 && level<=35) {
				ment = "$2828";
			} else if(level>=36 && level<=47) {
				ment = "$2829";
			} else if(level>=48) {
				ment = "$2830";
			}
			break;
		case 4075:	// 배틀 타이거
			if(level>=12 && level<=23) {
				ment = "어흥! 부르셨습니까?";
			} else if(level>=24 && level<=35) {
				ment = "어흥! 무슨 일이십니까?";
			} else if(level>=36 && level<=47) {
				ment = "어흥! 명령만 내리시지요.";
			} else if(level>=48) {
				ment = "어흥!! 뭐든지 시켜주십시오.";
			}
				break;
		}
		ChattingController.toChatting(this, ment, Lineage.CHATTING_MODE_NORMAL);
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		
		 if(action.equalsIgnoreCase("weaponclear")){
			 
			 
				Connection con = null;
				try {
					con = DatabaseConnection.getLineage();
					

					if(checkString(SummonController.selectPetWeapon(this))){
						
						if(this.getName().contains("$")){
							ChattingController.toChatting(pc, String.format("펫(%s)에 장착된 무기가 없습니다", this.getMonster().getName(),SummonController.selectPetWeapon(this)), Lineage.CHATTING_MODE_MESSAGE);
							
						}else{
							ChattingController.toChatting(pc, String.format("펫(%s)에 장착된 무기가 없습니다", this.getName(),SummonController.selectPetWeapon(this)), Lineage.CHATTING_MODE_MESSAGE);
							
							
						}
				
					}else{
						ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(SummonController.selectPetWeapon(this)));
						ii.setDefinite(true);
						ii.setCount(1);
						pc.toGiveItem(null, ii, ii.getCount());
						if(this.getName().contains("$")){
							ChattingController.toChatting(pc, String.format("펫(%s)에 무기 %s가 장착 해제 되었습니다", this.getMonster().getName(),SummonController.selectPetWeapon(this)), Lineage.CHATTING_MODE_MESSAGE);
						
						}else{
							ChattingController.toChatting(pc, String.format("펫(%s)에 무기 %s가 장착 해제 되었습니다", this.getName(),SummonController.selectPetWeapon(this)), Lineage.CHATTING_MODE_MESSAGE);
							
						}
						this.setPetWeapon("");
						SummonController.updatePetWeapon(con, this);
						
					}
					
					
				} catch (Exception e) {
					lineage.share.System.println(PetInstance.class+" : toAi(long time)");
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con);
				}
			 
	
			
		}else if(action.equalsIgnoreCase("armorclear")){
			
			
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				

				if(checkString(SummonController.selectPetArmor(this))){
					
					if(this.getName().contains("$")){
						ChattingController.toChatting(pc, String.format("펫(%s)에 장착된 방어구가 없습니다", this.getMonster().getName(),SummonController.selectPetArmor(this)), Lineage.CHATTING_MODE_MESSAGE);
						
					
					}else{
						ChattingController.toChatting(pc, String.format("펫(%s)에 장착된 방어구가 없습니다", this.getName(),SummonController.selectPetArmor(this)), Lineage.CHATTING_MODE_MESSAGE);
						
						
					}
			
				}else{
					ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(SummonController.selectPetArmor(this)));
					ii.setDefinite(true);
					ii.setCount(1);
					pc.toGiveItem(null, ii, ii.getCount());
					if(this.getName().contains("$")){
						ChattingController.toChatting(pc, String.format("펫(%s)에 방어구 %s가 장착 해제 되었습니다", this.getMonster().getName(),SummonController.selectPetArmor(this)), Lineage.CHATTING_MODE_MESSAGE);
					
					}else{
						ChattingController.toChatting(pc, String.format("펫(%s)에 방어구 %s가 장착 해제 되었습니다", this.getName(),SummonController.selectPetArmor(this)), Lineage.CHATTING_MODE_MESSAGE);
						
					}
					this.setPetArmor("");
					SummonController.updateArmor(con, this);
					
				}
				
				
			} catch (Exception e) {
				lineage.share.System.println(PetInstance.class+" : toAi(long time)");
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con);
			}
		 
			
	
		
		}
		if(action.equalsIgnoreCase("dismiss")){
			//쿠베라 펫장비
			if (inv != null) {
			    for (ItemInstance ii : inv.getList()) {
			        if (ii != null) {
			            ItemInstance item = ItemDatabase.newInstance(ii);
			            if (item != null && item.getName() != null && !item.getName().contains("이빨")) {
			                item.toTeleport(this.x, this.y, map, false);
			                // 드랍됫다는거 알리기.
			                item.toDrop(this);
			                //쿠베라 펫버그
			        	    getInventory().remove(ii, true);
			            }
			        }
			    }
			}
			// 해산.
			// del칼럼 변경 (다시 호출 못 하도록.)
			SummonController.deletePet(this);
			// 객체 제거.
			toAiSpawn(0);
		}else{
			super.toTalk(pc, action, type, cbp);
		}
	}
	
}
