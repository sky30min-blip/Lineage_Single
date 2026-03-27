package lineage.world.object.instance;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Exp;
import lineage.bean.database.Monster;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_HtmlSummon;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.network.packet.server.S_ObjectLawful;
import lineage.network.packet.server.S_SummonTargetSelect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.item.potion.PetPotion;

public class SummonInstance extends MonsterInstance {
	
	// 상태별 모드 목록.
	static public enum SUMMON_MODE {
		AggressiveMode,		// 공격 태세
		DefensiveMode,		// 방어 태세
		Deploy,				// 산개
		Alert,				// 경계
		ItemPickUp,			// 수집
		Rest,				// 휴식
		ItemPickUpFinal,	// 수집 마지막 뒷처리.
		Call,				// 호출
		TargetSelect,		// 소환술사로 인한 타켓 강제 변경 요청.
	}

	private SUMMON_MODE summon_mode;
	private long summon_time_start;					// 서먼 소환된 시간
	private long summon_time_end;					// 서먼 종료될 시간.
	private boolean elemental;			// 요정이 소환한 정령인지 확인하는 변수.
	
	static synchronized public SummonInstance clone(SummonInstance si, Monster m, int time){
		if(si == null)
			si = new SummonInstance();
		si.setMonster(m);
		// 휴식모드로 전환
		si.setSummonMode(SUMMON_MODE.Rest);
		// 자연회복을 위해 등록.
		CharacterController.toWorldJoin(si);
		// 소환된 시간과 종료될 시간 처리하기.
		si.setSummonTimeStart(System.currentTimeMillis());
		si.setSummonTimeEnd(si.getSummonTimeStart() + (1000*time));
		// 기본 정보 초기화.
		si.setElemental(false);
		return si;
	}
	
	@Override
	public void close(){
		// 관리목록에서 제거.
		if(summon != null)
			summon.remove(this);
		//
		super.close();
	}
	
	public SummonInstance(){
		summon_mode = SUMMON_MODE.Rest;
	}
	
	/**
	 * 호출에대한 응답멘트 처리하는 함수.
	 */
	protected void toWhistleMent(){}
	
	public SUMMON_MODE getSummonMode() {
		return summon_mode;
	}

	public long getSummonTimeStart() {
		return summon_time_start;
	}

	public void setSummonTimeStart(long summon_time_start) {
		this.summon_time_start = summon_time_start;
	}

	public long getSummonTimeEnd() {
		return summon_time_end;
	}

	public void setSummonTimeEnd(long summon_time_end) {
		this.summon_time_end = summon_time_end;
	}

	public boolean isElemental() {
		return elemental;
	}

	public void setElemental(boolean elemental) {
		this.elemental = elemental;
	}

	public void setSummonMode(SUMMON_MODE summon_mode) {
		this.summon_mode = summon_mode;
		switch(summon_mode){
			case Rest:
				clearAstarList();
				clearAttackList();
				for(Exp e : getExpList())
					ExpDatabase.setPool(e);
				clearExpList();
				break;
			case Alert:
				homeX = x;
				homeY = y;
				break;
			case Deploy:
				if(summon!=null && summon.getMaster()!=null){
					// 1[-] 2[+]
					int min_x_1 = summon.getMaster().getX()-4;
					int max_x_1 = summon.getMaster().getX()-8;
					int min_x_2 = summon.getMaster().getX()+4;
					int max_x_2 = summon.getMaster().getX()+8;
					int min_y_1 = summon.getMaster().getY()-4;
					int max_y_1 = summon.getMaster().getY()-8;
					int min_y_2 = summon.getMaster().getY()+4;
					int max_y_2 = summon.getMaster().getY()+8;
					switch(Util.random(1, 4)){
						case 1:
							homeX = Util.random(min_x_1, max_x_1);
							homeY = Util.random(min_y_1, max_y_1);
							break;
						case 2:
							homeX = Util.random(min_x_2, max_x_2);
							homeY = Util.random(min_y_1, max_y_1);
							break;
						case 3:
							homeX = Util.random(min_x_2, max_x_2);
							homeY = Util.random(min_y_2, max_y_2);
							break;
						case 4:
							homeX = Util.random(min_x_1, max_x_1);
							homeY = Util.random(min_y_2, max_y_2);
							break;
					}
				}else{
					setSummonMode(SUMMON_MODE.Rest);
				}
				break;
			case Call:
				clearAstarList();
				clearAttackList();
				// 레벨 및 종류별 채팅.
				toWhistleMent();
				break;
			case TargetSelect:
				clearAstarList();
				clearAttackList();
				break;
		}
	}

	@Override
	public void setNowHp(int nowHp){
		super.setNowHp(nowHp);
		if(summon!=null && summon.getMaster()!=null && Lineage.server_version>144)
			summon.getMaster().toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), this, true));
	}
	
	@Override
	public void setLawful(int lawful) {
		super.setLawful(lawful);
		if(summon!=null && summon.getMaster()!=null)
			toSender(S_ObjectLawful.clone(BasePacketPooling.getPool(S_ObjectLawful.class), this), false);
	}

	@Override
	public void toRevival(object o){
		super.toRevival(o);
		
		// 서먼관리중일때만 처리.
		if(summon != null)
			// 모드 휴식으로 변경.
			setSummonMode(SUMMON_MODE.Rest);
	}
	
	@Override
	public void toDamage(Character cha, int dmg, int type, Object...opt){
		if(summon != null){
			// 경험치 지급될 목록에 추가.
			appendExp(cha, dmg);
			
			// 서먼 객체들에게 알리기.
			summon.toDamage(cha, dmg);
			return;
		}
		super.toDamage(cha, dmg, type);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(summon != null)
			pc.toSender(S_HtmlSummon.clone(BasePacketPooling.getPool(S_HtmlSummon.class), this));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(summon == null)
			return;
		if(summon.getMasterObjectId() != pc.getObjectId())
			return;
		
		// 대화 요청된 객체 임시 저장.
		summon.setTempSi(this);
		
		if(action.equalsIgnoreCase("attackchr")){
			// 공격목표 지정
			pc.toSender(S_SummonTargetSelect.clone(BasePacketPooling.getPool(S_SummonTargetSelect.class), this));
		}else if(action.equalsIgnoreCase("aggressive")){
			// 공격 태세
			summon.setMode(SUMMON_MODE.AggressiveMode);
			toTalk(pc, null);
			
		}else if(action.equalsIgnoreCase("defensive")){
			// 방어 태세
			summon.setMode(SUMMON_MODE.DefensiveMode);
			toTalk(pc, null);
			
		}else if(action.equalsIgnoreCase("stay")){
			// 휴식
			summon.setMode(SUMMON_MODE.Rest);
			toTalk(pc, null);
			
		}else if(action.equalsIgnoreCase("extend")){
			// 산개
			summon.setMode(SUMMON_MODE.Deploy);
			toTalk(pc, null);
			
		}else if(action.equalsIgnoreCase("alert")){
			// 경계
			summon.setMode(SUMMON_MODE.Alert);
			toTalk(pc, null);
			
		}else if(action.equalsIgnoreCase("getitem")){
			// 수집
		summon.setMode(SUMMON_MODE.ItemPickUp);
			toTalk(pc, null);
	
		}else if(action.equalsIgnoreCase("dismiss")){
			for (ItemInstance ii : inv.getList()) {
				ItemInstance item = ItemDatabase.newInstance(ii);
				item.toTeleport(this.x, this.y, map, false);
				// 드랍됫다는거 알리기.
				item.toDrop(this);
			}
			// 해산
			toAiSpawn(0);
			
		}else if(action.equalsIgnoreCase("changename")){
			// 이름 정하기
			pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 325));
		}
	}
	
	@Override
	public void toAi(long time){
		// 관리자가 없을경우 휴식모드로 전환.
		// 휴식모드가 아닐경우에만 해당.
		if (summon.getMaster()==null && summon.getSummon_master_leave_time() < System.currentTimeMillis()) {
			this.toAiThreadDelete();
		}
		
		if(summon!=null && summon_mode!=SUMMON_MODE.Rest && summon.getMaster()==null){
			setSummonMode(SUMMON_MODE.Rest);
			return;
		}
		
		// 촐기 물약 복용
		if (getSpeed() == 0 || getSpeed() == 2) {
			for (ItemInstance item : getInventory().getList()) {
				if (item instanceof HastePotion)
					item.toClick(this, null);
			}
		}
		
		// 체력 물약 복용
		int hp = (int) (((double) getNowHp() / (double) getTotalHp()) * 100.0);	
		if (hp < 80) {
			for (ItemInstance item : getInventory().getList()) {
				  if (item instanceof HealingPotion || item instanceof PetPotion) {
			            item.toClick(this, null);
			        }
			}
		}
		
		// 그외엔 일반 몬스터 처럼 처리.
		super.toAi(time);
	}

	@Override
	protected void toAiWalk(long time){
		// summon 객체가 null 이라면 관리목록에서 떠난것이므로 일반 몬스터 처럼 처리.
		if(summon == null){
			super.toAiWalk(time);
			return;
		}
		
		// Astar 발동처리하다가 길이막혀서 이동못하던 객체를 모아놓은 변수를 일정주기마다 클린하기.
		if(Util.random(0, 10) == 0)
			clearAstarList();
		
		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode+Lineage.GFX_MODE_WALK);
		
		switch(summon_mode){
			case AggressiveMode:
			case DefensiveMode:
				if(summon.getMaster()!=null){
					// 주인 따라다니기.
					if(!Util.isDistance(this, summon.getMaster(), 2))
						toMoving(null, summon.getMaster().getX(), summon.getMaster().getY(), 0, true);
				}
				break;
			case Deploy:
				// 주인을 중심으로 8방향 무작위로 선택. 7거리만큼 이동.
				// 지점에 장애물이 잇을수 잇기때문에 유효범위 2으로 설정.
				if( Util.isDistance(x, y, map, homeX, homeY, map, 2) ){
					setSummonMode(SUMMON_MODE.Rest);
				}else{
					toMoving(null, homeX, homeY, 0, true);
				}
				break;
			case Alert:
				// 경계지점에 장애물이 잇을수 잇기때문에 유효범위 2으로 설정.
				if( Util.isDistance(x, y, map, homeX, homeY, map, 2) ){
					// 공격 대기상태.
				}else{
					// 경계지점으로 이동
					toMoving(null, homeX, homeY, 0, true);
				}
				break;
			case ItemPickUp:
				// 아이템 픽업.
				toAiPickup(time);
				break;
	         case ItemPickUpFinal:
	             if (summon.getMaster() != null) {
	                if (!Util.isDistance(this, summon.getMaster(), 1)) {
	                   // 주인에게 접근.
	                   toMoving(null, summon.getMaster().getX(), summon.getMaster().getY(), 0, true);
	                } else {
	                   // 아이템 주기.
	                
	                   ArrayList<ItemInstance> items =(ArrayList<ItemInstance>)getInventory().getList();
	                   int size = items.size();
	                   for(int i = 0 ; i < size; i++) {
	                      ItemInstance ii = items.get(i);
	                      if (ii.isEquipped())
	                         continue;
	                      if (summon.getMaster().getInventory().isAppend(ii, ii.getCount(), false)) {
	                         ItemInstance temp = summon.getMaster().getInventory().find(ii);
	                         if (temp != null) {
	                            summon.getMaster().getInventory().count(temp, ii, temp.getCount() + ii.getCount(), true);
	                            getInventory().remove(ii, true);
	  	                      
	                         } else {
	                            // 전체 이동
	                            temp = ii;
	                            // 처리할 아이템 새로 등록.
	                            summon.getMaster().getInventory().append(temp, true);
	                            getInventory().remove(ii, true);
	  	                      
	                         }
	                         // \f1%0%s 당신에게 %1%o 주었습니다.
	                         summon.getMaster().toSender(new S_Message( 143, getName(), ii.toString()));
	                         //
	                      	items.clear();
	   
	                         String msg = String.format("[펫수집] %s -> %s (아이템: %s 갯수: %d)", summon.getMaster().getName(), getName(),ii.getItem().getName(),ii.getCount());
								String timeString = Util.getLocaleString(time, true);
								String log = String.format("[%s]\t %s", timeString, msg);
								
								GuiMain.display.asyncExec(new Runnable() {
									public void run() {
										GuiMain.getViewComposite().getpetComposite().toLog(log);
									}
								});
								
	                         
	                      }
	                   }
	                  
	                   // 휴식모드로 전환.
	                   setSummonMode(SUMMON_MODE.Rest);
	                }
	                
	             } else {
	                // 휴식모드로 전환.
	                setSummonMode(SUMMON_MODE.Rest);
	             }
	             break;
			case Call:
				// 호루라기로 호출됨.
				if(summon.getMaster()!=null){
					if(!Util.isDistance(this, summon.getMaster(), 1)){
						// 주인에게 접근.
						toMoving(null, summon.getMaster().getX(), summon.getMaster().getY(), 0, true);
					}else{
						// 휴식모드로 전환.
						setSummonMode(SUMMON_MODE.Rest);
					}
				}else{
					// 휴식모드로 전환.
					setSummonMode(SUMMON_MODE.Rest);
				}
				break;
		}
	}
	
	/**
	 * 타이머에서 주기적으로 호출함.
	 *  : Timer -> SummonController -> Summon -> this
	 */
	public void toTimer(){
		// summon 객체가 null 이라면 관리목록에서 떠난것이므로 일반 몬스터 처럼 처리.
		if(summon == null)
			return;
		
		summon_time_start += 1000;
		// 종료할 시간이 되엇다면..
		if(!isDead() && summon_time_start >= summon_time_end)
			toAiSpawn(0);
	}
}
