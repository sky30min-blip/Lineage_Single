package lineage.world.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import lineage.database.ItemDatabase;
import lineage.database.PowerballDatabase;
import lineage.database.ServerDatabase;
import lineage.share.Lineage;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

/**
 * 파워볼 미니게임: 하루 288회차(5분×288), 매일 1로 초기화.
 * 결과는 서버 자체 생성. (홀/짝·언더/오바 등 확장 가능)
 */
public class PowerballController {

	/** 하루 회차 수 */
	public static final int ROUNDS_PER_DAY = 288;
	/** 당첨 시 배팅금 배율 */
	public static double PAYOUT_RATE = 1.9;
	private static final long MIN_BET = 50000L;
	private static final long MAX_BET = 5000000L;

	/**
	 * 현재 배팅 가능 회차. 한국 시간(Asia/Seoul) 기준, 하루 288회차(5분 단위).
	 * 스케줄러·쿠폰·DB 회차 ID와 동일해야 하므로 타임존 고정.
	 */
	public static int getCurrentRound() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int todayRound = 1 + (h * 60 + min) / 5; // 1~288
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long dayStartMs = cal.getTimeInMillis();
		long dayIndex = dayStartMs / (24 * 60 * 60 * 1000L);
		return (int) (dayIndex * ROUNDS_PER_DAY + todayRound);
	}

	/** 표시용 "오늘의 N회차" (1~288). NPC·공지·쿠폰 표시용 */
	public static int getTodayRoundDisplay(int roundId) {
		int r = (roundId - 1) % ROUNDS_PER_DAY + 1;
		return r <= 0 ? 1 : r;
	}

	/** 결과 발표 30초 전부터 구매 불가 (5분 단위 회차: XX:00, XX:05, XX:10... 직전 30초, 한국 시간 기준) */
	public static boolean isWithin30SecOfRoundClose() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
		int sec = cal.get(Calendar.SECOND);
		int min = cal.get(Calendar.MINUTE);
		return (min % 5 == 4 && sec >= 30);
	}

	/**
	 * 유저 배팅 처리 (명령어 또는 NPC 공통).
	 * 5만~500만 아데나, 1인 1회차 1회만, 결과 30초 전 구매 마감.
	 * @return true면 성공, false면 실패(이미 안내 메시지 전송됨)
	 */
	public static boolean doBet(PcInstance pc, int pickType, long amount) {
		PAYOUT_RATE = Lineage.powerball_payout_rate;
		if (amount < MIN_BET || amount > MAX_BET) {
			ChattingController.toChatting(pc, String.format("[파워볼] 배팅 금액은 %,d ~ %,d 아데나 사이로 입력하세요.", MIN_BET, MAX_BET), Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		if (!pc.getInventory().isAden(amount, false)) {
			ChattingController.toChatting(pc, "아데나가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		if (isWithin30SecOfRoundClose()) {
			ChattingController.toChatting(pc, "[파워볼] 결과 발표 30초 전에는 구매할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		if (pickType < 0 || pickType > 3) {
			ChattingController.toChatting(pc, "[파워볼] 선택이 올바르지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		int roundId = getCurrentRound();
		int charId = (int) pc.getObjectId();
		if (pickType <= 1) {
			if (PowerballDatabase.hasOddEvenBetThisRound(charId, roundId)) {
				ChattingController.toChatting(pc, "[파워볼] 이 회차 홀/짝은 이미 구매하셨습니다. (회차당 홀·짝 1회, 언더·오버 1회)", Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
		} else {
			if (PowerballDatabase.hasUnderOverBetThisRound(charId, roundId)) {
				ChattingController.toChatting(pc, "[파워볼] 이 회차 언더/오버는 이미 구매하셨습니다. (회차당 홀·짝 1회, 언더·오버 1회)", Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
		}
		if (!PowerballDatabase.insertBet(charId, roundId, amount, pickType)) {
			ChattingController.toChatting(pc, "[파워볼] 배팅 등록에 실패했습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		// 아데나 차감: count() 1회만 사용 (isAden(amount,true) 사용 시 플러그인/중복 호출로 2배 차감될 수 있음)
		ItemInstance aden = pc.getInventory().findAden();
		if (aden != null) {
			pc.getInventory().count(aden, aden.getCount() - amount, true);
		} else {
			// 인벤에 아데나가 없으면 생성 후 마이너스는 불가이므로 차감 생략(이미 isAden(amount,false)에서 검사함)
			ChattingController.toChatting(pc, "[파워볼] 아데나 지급 오류. 관리자에게 문의하세요.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		// 홀/짝/언더/오버 쿠폰 (회차+금액 → 인벤 표시용)
		String couponName;
		if (pickType == 1) couponName = "홀 쿠폰";
		else if (pickType == 0) couponName = "짝 쿠폰";
		else if (pickType == 2) couponName = "언더 쿠폰";
		else couponName = "오버 쿠폰";
		ItemInstance coupon = ItemDatabase.newInstance(ItemDatabase.find(couponName));
		if (coupon != null) {
			coupon.setObjectId(ServerDatabase.nextItemObjId());
			coupon.setCount(1);
			coupon.setBless(1);
			coupon.setDefinite(true);
			coupon.setItemTimek(roundId + ":" + amount);
			// clone 시 name=NameId 로 잡혀 인벤·330번 메시지가 깨지므로 DB 한글명(띄어쓰기 제거)으로 표시 통일
			coupon.setName(couponName.replace(" ", ""));
			pc.getInventory().append(coupon, true);
		}
		String pick;
		if (pickType == 1) pick = "홀";
		else if (pickType == 0) pick = "짝";
		else if (pickType == 2) pick = "언더(합≤72)";
		else pick = "오버(합>72)";
		ChattingController.toChatting(pc, String.format("[파워볼] 제%d회차 %s 쿠폰을 %,d 아데나에 구매했습니다. 당첨 시 NPC에게 팔아 %.2f배 수령.", getTodayRoundDisplay(roundId), pick, amount, PAYOUT_RATE), Lineage.CHATTING_MODE_MESSAGE);
		return true;
	}

	/** 한국 시간 기준 00:00~05:59 (공지 등에서 참고용) */
	private static boolean isNightMode() {
		Calendar cal = Calendar.getInstance(Locale.KOREA);
		return cal.get(Calendar.HOUR_OF_DAY) >= 0 && cal.get(Calendar.HOUR_OF_DAY) < 6;
	}

	/**
	 * 정산 스레드에서 1분마다 호출.
	 * 결과 생성은 PowerBallScheduler가 담당하므로 여기서는 자체 생성하지 않음.
	 */
	public static void runPeriodicLogic() {
		// 결과는 PowerBallScheduler 추첨으로만 생성 (중복 제거)
	}

	/**
	 * powerball_results에 결과만 있고 미정산(is_processed=0) 베팅이 있는 회차를 찾아 markBetsProcessed만 수행.
	 * 결과 발표는 스케줄러의 NPC 발표만 사용하므로 여기서는 전체 채팅 공지하지 않음.
	 */
	public static void doSettlement() {
		try {
			List<int[]> results = PowerballDatabase.getUnsettledResults();
			if (results == null || results.isEmpty())
				return;

			for (int[] r : results) {
				if (r == null || r.length < 3) continue;
				int roundId = r[0];
				PowerballDatabase.markBetsProcessed(roundId);
			}
		} catch (Exception e) {
			lineage.share.System.printf("PowerballController.doSettlement()\r\n%s\r\n", e.toString());
		}
	}
}
