package lineage.thread;

import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.controller.AgitController;
import lineage.world.controller.AuctionController;
import lineage.world.controller.ColosseumController;
import lineage.world.controller.DevilController;
import lineage.world.controller.DimensionController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.SpotController;
import lineage.world.controller.TeamBattleController;
import lineage.world.controller.DollRaceController;
import lineage.world.controller.DollRaceController2;
import lineage.world.controller.ElvenforestController;
import lineage.world.controller.FightController;
import lineage.world.controller.TreasureHuntController;
import lineage.world.controller.IceDungeonController;
import lineage.world.controller.WorldBossController;
import lineage.world.controller.HellController;
import lineage.world.controller.TimeEventController;
import lineage.world.controller.TebeController;
import lineage.world.controller.PenguinHuntingController;
public class ControllerTherad implements Runnable {
	static private ControllerTherad thread;
	// 쓰레드동작 여부
	static private boolean running;
	
	static public void init(){
		TimeLine.start("ControllerTherad..");
		
		thread = new ControllerTherad();
		start();
				
		TimeLine.end();
	}
	
	static private void start() {
		running = true;
		Thread t = new Thread( thread );
		t.setName(ControllerTherad.class.toString());
		t.start();
	}
	
	@Override
	public void run() {
		for(;running;){			
			try {
				long time = System.currentTimeMillis();

				// 팀대전 관리.
				try { TeamBattleController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("팀대전 관리.");
					lineage.share.System.println(e);
				}
				
				// 성 관리
				try { KingdomController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("성 관리");
					lineage.share.System.println(e);
				}
				
				// 스팟 쟁탈전 관리.
				try { SpotController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("스팟 쟁탈전 관리.");
					lineage.share.System.println(e);
				}
				
				// 테베 던전 관리.
//				try { TebeController.toTimer(time); } catch (Exception e) {
//					lineage.share.System.println("테베 던전 관리.");
//					lineage.share.System.println(e);
//				}
			
//				try { DollRaceController.toTimer(time); } catch (Exception e) {
//					lineage.share.System.println("버그경주관리.");
//					lineage.share.System.println(e);
//				}
				// 보물찾기 관리 쿠베라
				try { TreasureHuntController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("보물상자 찾기 관리.");
					lineage.share.System.println(e);
				}
				
				// 지옥 던전 관리 쿠베라
				try { HellController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("지옥 던전 관리.");
					lineage.share.System.println(e);
				}
	
	
				// 마족신전 쿠베라
				try { DimensionController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("마족신전 관리.");
					lineage.share.System.println(e);
				}
				//악영 쿠베라
				try { DevilController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("악마왕의영토 관리.");
					lineage.share.System.println(e);
				}
				
				try { DollRaceController2.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("자리뻇기 관리.");
					lineage.share.System.println(e);
				}
				//월드보스 쿠베라
				try { WorldBossController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("월드보스 던전 관리.");
					lineage.share.System.println(e);
				}
				//타임이벤트 쿠베라
				try { TimeEventController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("타임이벤트  관리.");
					lineage.share.System.println(e);
				}
				// 경매 관리
				try { if(Lineage.server_version >= 200){AuctionController.toTimer(time);} } catch (Exception e) {
					lineage.share.System.println("경매 관리");
					lineage.share.System.println(e);
				}
				
				// 아지트 관리
				try { if(Lineage.server_version >= 200){AgitController.toTimer(time);} } catch (Exception e) {
					lineage.share.System.println("아지트 관리");
					lineage.share.System.println(e);
				}
				
				// 콜로세움 관리
//				try { if(Lineage.server_version >= 200){ColosseumController.toTimer(time);} } catch (Exception e) {
//					lineage.share.System.println("콜로세움 관리");
//					lineage.share.System.println(e);
//				}
//				
				// 요정숲 관리
				try { ElvenforestController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("요정숲 관리");
					lineage.share.System.println(e);
				}
				
		
				Thread.sleep(Common.TIMER_SLEEP);
			} catch (Exception e) {
				lineage.share.System.printf("lineage.thread.ControllerTherad.run()\r\n : %s\r\n", e.toString());
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
