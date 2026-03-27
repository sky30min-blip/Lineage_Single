package kubera;

import lineage.bean.database.Skill;
import lineage.bean.database.SpriteFrame;
import lineage.database.SpriteFrameDatabase;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;

import java.util.HashMap;
import java.util.Map;

public class AttackController {
	// 이전 공격 시간
	private static Map<Integer, Integer> checksumStorage = new HashMap<>();

	public static boolean isAttackTime(PcInstance pc, int action, boolean triple) {
		if (triple || pc instanceof RobotInstance) {
			return false;
		}

		pc.setCurrentAttackMotion(action);

		long frame = (long) (getGfxFrameTime(pc, pc.getGfx(), action) * (pc.isAutoAttack ? 0 : Lineage.speed_check_attack_frame_rate));
		long skillframe = (long) (getGfxFrameTime(pc, pc.getGfx(), pc.getCurrentSkillMotion()) * (pc.getCurrentSkillMotion() == Lineage.GFX_MODE_SPELL_DIRECTION ? 2 : 1.7));

		return isAttack(pc, frame, skillframe, triple);
	}

	public static boolean isMagicTime(PcInstance pc, int action) {

		pc.setCurrentSkillMotion(action);
		long frame = (long) (getGfxFrameTime(pc, pc.getGfx(), action) * (action == Lineage.GFX_MODE_SPELL_DIRECTION ? Lineage.speed_check_dir_magic_frame_rate : Lineage.speed_check_no_dir_magic_frame_rate));
		return isSkill(pc, frame);
	}

	public static boolean isAttack(PcInstance pc, long frame, long skillframe, boolean triple) {
		long Time = System.currentTimeMillis();

		if (pc.isLock() || (pc.getAttackTime() >= Time && !triple)) {
			return false;
		}

		long attackWaitTime = 0;
		long lastSkillTime = pc.getSkillTime();
		long diffTime = Time - lastSkillTime;

		if (lastSkillTime > 0 && diffTime < skillframe) {
			if (diffTime < 0) {
				return false;
			}
		}

		if (lastSkillTime > 0 && pc.getAttackTime() > Time) {
			if (pc.getCurrentSkillMotion() == Lineage.GFX_MODE_SPELL_DIRECTION) {
				attackWaitTime = lastSkillTime + 1000;
			} else if (pc.getCurrentSkillMotion() == Lineage.GFX_MODE_SPELL_NO_DIRECTION) {
				attackWaitTime = lastSkillTime + 500;
			}
		}

		// 공격 대기 시간 조정
		if (pc.getAttackTime() <= Time) {
			attackWaitTime += frame;
			pc.setAttackTime(Time + attackWaitTime);
		} else {
			attackWaitTime += pc.getAttackTime() - Time;
			pc.setAttackTime(pc.getAttackTime() + attackWaitTime);
			pc.setAiTime(pc.getAttackTime());
		}

		return true;
	}

	private static boolean isSkill(PcInstance pc, long frame) {
		long time = System.currentTimeMillis();

		if (pc.isLock()) {
			return false;
		}

		int lastSkillMotion = pc.getLastSkillMotion();
		if (lastSkillMotion != pc.getCurrentSkillMotion()) {
			pc.setCurrentSkillMotion(lastSkillMotion);
			pc.setSkillTime(time + frame);
		}

		return true;
	}

	public static int getGfxFrameTime(object o, int gfx, int action) {
		SpriteFrame spriteFrame = SpriteFrameDatabase.getList().get(gfx);

		if (spriteFrame != null) {
			double frame = 0;
			Integer gfxFrame = spriteFrame.getList().get(action);

			if (gfxFrame != null)
				frame = gfxFrame.intValue();
			else
				return 1000;

			frame = calculateFrameTime(o, frame);

			int checksum = calculateChecksum(gfx, action);
			if (checksum != getPredefinedChecksum(gfx, action)) {

				return 1000;
			}

			return (int) frame;
		}
		return 1000;
	}

	private static int calculateChecksum(int gfx, int action) {
		return (gfx + action) % 1000;
	}

	private static int getPredefinedChecksum(int gfx, int action) {
		int checksum = checksumStorage.getOrDefault(gfx, 0);
		if (checksum == 0) {
			checksum = calculateChecksum(gfx, action);
			checksumStorage.put(gfx, checksum);
		}
		return checksum;
	}

	private static double calculateFrameTime(object o, double frame) {
		if (o.getSpeed() == 0 && !o.isBrave())
			frame *= 42;
		else if ((o.getSpeed() == 1 && !o.isBrave()) || (o.getSpeed() == 0 && o.isBrave()))
			frame *= 31;
		else if (o.getSpeed() == 1 && o.isBrave())
			frame *= 23.5;
		else if (o.getSpeed() == 2 && !o.isBrave())
			frame *= 81;
		else if (o.getSpeed() == 2 && o.isBrave())
			frame *= 61;

		return frame;
	}

	/**
	 * 스킬 사용시 모션을 몇번을 개산해야할지 계산해주는 함수.
	 */
	static public int getSkillMotion(Skill skill) {
		switch ((int) skill.getUid()) {
		case 4: // 에너지 볼트
		case 6: // 아이스 대거
		case 7: // 윈드 커터
		case 10: // 칠 터치
		case 15: // 파이어 애로우
		case 16: // 쇼크스턴
		case 17: // 라이트닝
		case 22: // 프로즌 클라우드
		case 25: // 파이어볼
		case 28: // 뱀파이어릭 터치
		case 30: // 어스 재일
		case 34: // 콜 라이트닝
		case 38: // 콘 오브 콜드
		case 45: // 이럽션
		case 46: // 선 버스트
		case 50: // 아이스 랜스
		case 53: // 토네이도
		case 59: // 블리자드
		case 62: // 어스 퀘이크
		case 70: // 파이어 스톰
		case 74: // 미티어 스트라이크
		case 77: // 디스인티그레이트
		case 115: // 트리플 애로우
			return Lineage.GFX_MODE_SPELL_DIRECTION;
		}
		return Lineage.GFX_MODE_SPELL_NO_DIRECTION;
	}
}
