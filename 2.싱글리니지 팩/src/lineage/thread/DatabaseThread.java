package lineage.thread;

import java.sql.Connection;

import lineage.database.BadIpDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ServerDatabase;
import lineage.database.ServerDownBossListDatabase;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.Mysql;
import lineage.share.System;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.controller.ClanController;
import lineage.world.controller.FishingController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.RankController;
import lineage.world.controller.RobotClanController;
import lineage.world.object.instance.PcInstance;

public class DatabaseThread implements Runnable {
	static private DatabaseThread thread;
	// 쓰레드동작 여부
	static private boolean running;
	// 서버 데이터 저장 시간
	static public long serverDataSaveTime;
	// 캐릭터 저장 시간.
	static long characterSaveTime;
	
	/**
	 * 초기화 함수.
	 */
	static public void init(){
		TimeLine.start("DatabaseThread..");
		
		thread = new DatabaseThread();
		serverDataSaveTime = characterSaveTime = System.currentTimeMillis() + (1000 * 5);
		start();
		
		TimeLine.end();
	}
	
	static private void start() {
		running = true;
		Thread t = new Thread( thread );
		t.setName(DatabaseThread.class.toString());
		t.start();
	}
	
	@Override
	public void run() {
		for(;running;){
			try {
				// 매초마다 오브젝트 ID 갱신.
				ServerDatabase.toSave();
			} catch (Exception e) {
				lineage.share.System.println("DatabaseThread : 오브젝트 ID 갱신 에러.");
				lineage.share.System.println(e);
			}
			
			long time = System.currentTimeMillis();
			
			try {
				if (Lineage.auto_save_time > 0 && serverDataSaveTime <= time) {		
					if (serverDataSaveTime > 0) {
						Connection con = null;
						try {
							con = DatabaseConnection.getLineage();
							try {
								// bad ip 저장
								BadIpDatabase.save(con);
							} catch (Exception e) {
								lineage.share.System.println("DatabaseThread : bad ip 저장 에러.");
								lineage.share.System.println(e);
							}
							
							try {
								// 혈맹 저장
								ClanController.saveClan(con);
							} catch (Exception e) {
								lineage.share.System.println("DatabaseThread : 혈맹 저장 에러.");
								lineage.share.System.println(e);
							}
							
							try {
								// 자동 낚시 저장
								FishingController.save(con);
							} catch (Exception e) {
								lineage.share.System.println("DatabaseThread : 자동 낚시 저장 에러.");
								lineage.share.System.println(e);
							}
							
							try {
								// kingdom 저장
								KingdomController.save(con);
							} catch (Exception e) {
								lineage.share.System.println("DatabaseThread : kingdom 저장 에러.");
								lineage.share.System.println(e);
							}
							
							try {
								// 보스 저장
								ServerDownBossListDatabase.save(con);
							} catch (Exception e) {
								lineage.share.System.println("DatabaseThread : 보스 저장 에러.");
								lineage.share.System.println(e);
							}
							
							try {
								// 무인혈맹 저장
								RobotClanController.save(con);
							} catch (Exception e) {
								lineage.share.System.println("DatabaseThread : 무인혈맹 저장 에러.");
								lineage.share.System.println(e);
							}
						} catch (Exception e) {
						} finally {
							DatabaseConnection.close(con);
						}
					}		
					serverDataSaveTime = time + Lineage.auto_save_time;
				}
				
				// 케릭터 저장 관리.
				if (World.getPcList().size() > 0 && Lineage.auto_save_time > 0 && characterSaveTime <= time) {
					if (characterSaveTime > 0) {
						Connection con = null;
						try {
							con = DatabaseConnection.getLineage();
							// 캐릭터 저장 관리.
							for (PcInstance pc : World.getPcList()) {
								try {
									// 자동저장 처리 구간.
									pc.autoSave(con, time);
							
								} catch (Exception e) {
									lineage.share.System.println("DatabaseThread : 캐릭터 저장 에러. 캐릭터: " + pc.getName());
									lineage.share.System.println(e);
								}
							}
						} catch (Exception e) {
						} finally {
							DatabaseConnection.close(con);
						}
					}
					characterSaveTime = time + Lineage.auto_save_time;
				}				
				
				// 랭킹 관리.
				try { RankController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("DatabaseThread : 랭킹 에러.");
					lineage.share.System.println(e);
				}
				
				// backup 관리.
				try { Mysql.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("DatabaseThread : 백업 에러.");
					lineage.share.System.println(e);
				}

				// log 관리.
				try { Log.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("DatabaseThread : 로그 에러.");
					lineage.share.System.println(e);
				}

				Thread.sleep(Common.TIMER_SLEEP);
			} catch (Exception e) {
				lineage.share.System.printf("lineage.thread.DatabaseThread.run()\r\n : %s\r\n", e.toString());
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
