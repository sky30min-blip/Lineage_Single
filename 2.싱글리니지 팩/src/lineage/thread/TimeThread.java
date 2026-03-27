package lineage.thread;

import java.util.ArrayList;
import java.util.List;

import lineage.database.BackgroundDatabase;
import lineage.database.TimeDungeonDatabase;
import lineage.share.Common;
import lineage.share.TimeLine;
import lineage.world.controller.AutoHuntController;
import lineage.world.controller.BossController;
import lineage.world.controller.BuffController;
import lineage.world.controller.ExpMarbleController;
import lineage.world.controller.InnController;
import lineage.world.controller.GmDeliveryController;
import lineage.world.controller.MagicDollController;
import lineage.world.controller.PcMarketController;
import lineage.world.controller.PcTradeController;
import lineage.world.controller.ShipController;
import lineage.world.controller.SummonController;
import lineage.world.controller.WantedController;
import lineage.world.object.npc.PowerballNpc;

public class TimeThread implements Runnable {
	static public TimeThread thread;
	// 쓰레드동작 여부
	static private boolean running;
	
	static public void init(){
		TimeLine.start("TimeThread..");
		
		thread = new TimeThread();
		start();
				
		TimeLine.end();
	}
	
	static public void start() {
		if (thread == null)
			thread = new TimeThread();
		
		running = true;
		Thread t = new Thread( thread );
		t.setName(TimeThread.class.toString());
		t.start();
	}
	
	@Override
	public void run() {
		for(;running;){
			try {
				List<Object> temp = new ArrayList<Object>();
				long time = System.currentTimeMillis();
				
				// 던전 이용 시간 관리.
				try { TimeDungeonDatabase.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("던전 이용 시간 관리.");
					lineage.share.System.println(e);
				}
				
				// 자동 사냥 이용 시간 관리.
				try { AutoHuntController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("자동 사냥 이용 시간 관리.");
					lineage.share.System.println(e);
				}
				
				//경험치 저장 구슬 관리.
				try { ExpMarbleController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("경험치 저장 구슬 관리.");
					lineage.share.System.println(e);
				}
				
				// 버프 관리
				try { BuffController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("버프 관리");
					lineage.share.System.println(e);
				}

				// 서먼몬스터 관리
				try { SummonController.toTimer(); } catch (Exception e) {
					lineage.share.System.println("서먼몬스터 관리");
					lineage.share.System.println(e);
				}

				// 여관 관리
				try { InnController.toTimer(time, temp); } catch (Exception e) {
					lineage.share.System.println("여관 관리");
					lineage.share.System.println(e);
				}

				// 보스몬스터 관리.
				try { BossController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("보스몬스터 관리.");
					lineage.share.System.println(e);
				}
				
				// 마법인형 관리.
				try { MagicDollController.toTimer(time, temp); } catch(Exception e) {
					lineage.share.System.println("마법인형 관리.");
					lineage.share.System.println(e);
				}

				// 현상수배 관리.
				try { WantedController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("현상수배 관리.");
					lineage.share.System.println(e);
				}
				
				// 유저 현금 거래 관리.
				try { PcTradeController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("유저 현금 거래 관리.");
					lineage.share.System.println(e);
				}
				
				//무인 상점 관리.
				try { PcMarketController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("무인 상점 관리.");
					lineage.share.System.println(e);
				}

				// GM 툴 접속 중 지급 반영.
				try { GmDeliveryController.toTimer(time); } catch (Exception e) {}

				// 파워볼 NPC 이름 옆 회차/남은시간 갱신
				try { PowerballNpc.updateAllTitles(); } catch (Exception e) {}

				// 횟불 관리.
				try { BackgroundDatabase.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("횟불 관리.");
					lineage.share.System.println(e);
				}
				
//				// 강아지 레이스 관리.
//				try {
//					DogRaceController.toTimer(time);
//				} catch (Exception e) {
//					lineage.share.System.println("강아지 레이스 관리.");
//					lineage.share.System.println(e);
//				}
				
	/*			// 슬라임레이스 관리. 
				try { SlimeRaceController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("슬라임레이스 관리.");
					lineage.share.System.println(e);
				}*/
				
	/*			// 퀘스트 관리
				try { QuestController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("퀘스트 관리");
					lineage.share.System.println(e);
				}*/
				
			// 선착장
				try { ShipController.toTimer(); } catch (Exception e) {
					lineage.share.System.println("선착장");
					lineage.share.System.println(e);
				}
				
				Thread.sleep(Common.TIMER_SLEEP);
			} catch (Exception e) {
				lineage.share.System.printf("lineage.thread.TimeThread.run()\r\n : %s\r\n", e.toString());
			}
		}	
	}
	
	/**
	 * 쓰레드 종료처리 함수.
	 */
	static public void close() {
		running = false;
		thread = null;
	}
}
