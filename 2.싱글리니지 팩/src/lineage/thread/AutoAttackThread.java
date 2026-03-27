package lineage.thread;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lineage.share.Lineage;
import lineage.share.System;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;
import kubera.AttackController;

public class AutoAttackThread {

	private static final int ATTACK_INTERVAL = 5;
	private static final int MOVEMENT_INTERVAL = 200;

	private static volatile boolean running;
	private static final int BOW_ATTACK_RANGE = 11;
	private static final int SPEAR_ATTACK_RANGE = 2;
	private static final int MELEE_ATTACK_RANGE = 1;
	private static final int TRIPLE_ARROW_ATTACK_RANGE = 11;

	public static void init() {
		TimeLine.start("AutoAttackThread..");
		start();
		TimeLine.end();
	}

	private static void start() {
		running = true;

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		CompletableFuture.runAsync(() -> {
			while (running) {
				try {
					List<PcInstance> pcList = World.getPcList();

					if (pcList.isEmpty()) {
						continue;
					}

					long currentTime = System.currentTimeMillis();

					for (PcInstance pc : pcList) {
						if (pc.isAutoAttack) {
							if (shouldAutoAttack(currentTime, pc)) {
								autoAttack(pc);
							}
						}
					}

					Thread.sleep(ATTACK_INTERVAL);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				} catch (Exception e) {

				}
			}
		}, executorService);
	}

	private static boolean shouldAutoAttack(long currentTime, PcInstance pc) {
		if ((pc.autoAttackTime > currentTime && !pc.isUsingTripleArrow()) || pc instanceof PcRobotInstance) {
			return false;
		}
		if (pc.isUsingTripleArrow() && !pc.isTripleArrowFinished()) {
			return false;
		}
		if (pc.autoAttackTarget == null || pc.autoAttackTarget.isDead() || pc.autoAttackTarget.isInvis() || pc.autoAttackTarget.isTransparent()) {
			pc.resetAutoAttack();
			return false;
		}
		if (pc.isLock()) {
			pc.resetAutoAttack();
			return false;
		}
		return isInAttackRange(pc);
	}

	private static void autoAttack(PcInstance pc) {
		pc.setAttacking(true);

		int attackRange = getAttackRange(pc);
		boolean isBow = isEquipped(pc, "bow");

		AttackController.isAttack(pc, 0, 0, false);

		if (isEquipped(pc, "bow")) {
			pc.toAttack(pc.autoAttackTarget, pc.autoAttackTarget.getX(), pc.autoAttackTarget.getY(), true, 0, 0, false);
		} else {
			pc.toAttack(pc.autoAttackTarget, pc.autoAttackTarget.getX(), pc.autoAttackTarget.getY(), false, 0, 0, false);
		}
	}

	private static boolean isInAttackRange(PcInstance pc) {
		if (pc.getMap() != pc.autoAttackTarget.getMap()) {
			pc.resetAutoAttack();
			return false;
		}

		int attackRange = getAttackRange(pc);

		if (pc.isUsingTripleArrow()) {
			attackRange = TRIPLE_ARROW_ATTACK_RANGE;
		}

		if (!Util.isDistance(pc, pc.autoAttackTarget, attackRange)) {
			pc.resetAutoAttack();
			return false;
		}

		return Util.isDistance(pc, pc.autoAttackTarget, attackRange);
	}

	private static boolean isEquipped(PcInstance pc, String weaponType) {
		ItemInstance weapon = pc.getInventory().getSlot(Lineage.SLOT_WEAPON);

		if (weapon == null) {
			return false;
		}

		return weapon.getItem().getType2().equalsIgnoreCase(weaponType);
	}

	private static int getAttackRange(PcInstance pc) {
		if (isEquipped(pc, "bow")) {
			return BOW_ATTACK_RANGE;
		} else if (isEquipped(pc, "spear")) {
			return SPEAR_ATTACK_RANGE;
		} else {
			return MELEE_ATTACK_RANGE;
		}
	}

	public static void stop() {
		running = false;
	}

	public static void close() {
		running = false;
	}
}