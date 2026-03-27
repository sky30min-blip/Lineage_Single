package lineage.thread;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.Character;
import lineage.world.object.instance.PcInstance;

public class CharacterControlThread implements Runnable {
	static private CharacterControlThread thread;
	// 쓰레드동작 여부
	static private boolean running;

	static public void init() {
		TimeLine.start("CharacterControlThread..");

		thread = new CharacterControlThread();
		start();

		TimeLine.end();
	}

	static private void start() {
		running = true;
		Thread t = new Thread(thread);
		t.setName(CharacterControlThread.class.toString());
		t.start();
	}

	@Override
	public void run() {
		for (; running;) {
			try {
				if (World.getPcList().size() < 1) {
					Thread.sleep(Common.TIMER_SLEEP);
					continue;
				}

				// 스탯 시스템
				for (PcInstance pc : World.getPcList()) {
					try {
						pc.setStat();
						pc.toLvStat(true);
					} catch (Exception e) {
						lineage.share.System.printf("스탯 시스템 에러\r\n : %s\r\n", e.toString());
						lineage.share.System.printf("캐릭터: %s\n", pc.getName());
					}

					// 피바를 직접 켠 사용자에게만 표시.
					if (pc.isHpbar()) {
						for (object o : pc.getInsideList()) {
							// 요청사항: NPC/몬스터 HP바는 비활성. (플레이어/소환만 허용)
							if (o != null && o instanceof Character && !(o instanceof lineage.world.object.instance.MonsterInstance)
									&& !(o instanceof lineage.world.object.instance.NpcInstance) && !o.isWorldDelete() && o.getMaxHp() > 1)
								pc.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), (Character) o, true));
							
							if (o != null && o.isInvis()) {
								
							}
						}
					}

					// 결투장 시스템
					if (Lineage.is_battle_zone) {
						try {
							pc.battleZone();
						} catch (Exception e) {
							lineage.share.System.printf("결투장 시스템 에러\r\n : %s\r\n", e.toString());
							lineage.share.System.printf("캐릭터: %s\n", pc.getName());
						}
					}
				}

				// 자동물약 시스템
				for (PcInstance pc : World.getPcList()) {
					try {
						if (pc == null || pc.isDead() || pc.isWorldDelete() || pc.getInventory() == null)
							continue;

						if (!pc.isAutoPotion || pc.autoPotionPercent < 1 || pc.autoPotionName == null || pc.autoPotionName.equalsIgnoreCase("") || pc.autoPotionName.length() < 2)
							continue;

						int hp = (int) (((double) pc.getNowHp() / (double) pc.getTotalHp()) * 100.0);
						if (hp > pc.autoPotionPercent)
							continue;

						pc.autoPotion();
					} catch (Exception e) {
						lineage.share.System.printf("자동물약 시스템 에러\r\n : %s\r\n", e.toString());
						lineage.share.System.printf("캐릭터: %s\n", pc.getName());
					}
				}

				Thread.sleep(Common.THREAD_SLEEP_100);
			} catch (Exception e) {
				lineage.share.System.printf("lineage.thread.CharacterControlThread.run()\r\n : %s\r\n", e.toString());
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
