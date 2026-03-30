package lineage.world.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import lineage.bean.database.Item;
import lineage.database.DatabaseConnection;
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
	private static final String POWERBALL_BOARD_TYPE = "powerball_reward";
	private static final String POWERBALL_BOARD_ACCOUNT = "system";
	private static final String POWERBALL_BOARD_WRITER = "시스템";
	private static final DecimalFormat DF = new DecimalFormat("#,###");
	private static final int KST_OFFSET_HOURS = 9;
	// 기사/요정/마법사 풀(22%), 군주 풀(12%)
	private static final int POOL_FOUR_CLASS_PERCENT = 22;
	private static final int POOL_ROYAL_PERCENT = 12;
	// 순위별 분배 비율(1~3위)
	private static final int[] RANK_SPLIT_PERCENT = new int[] { 50, 30, 20 };

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
		else if (pickType == 2) pick = "언더 쿠폰(합72 이하)";
		else pick = "오버 쿠폰(합72 이상)";
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

	/**
	 * 파워볼 누적금 게시판 글을 1분마다 자동 갱신한다.
	 * - boards.type = powerball_reward 에 단일 글(uid=1)로 유지
	 */
	public static void refreshRewardBoardPost() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			long serverProfit = getTodayServerProfit(con);

			String subject = String.format("[자동갱신] %s 파워볼 누적금", getTodayLabel());
			String memo = buildBoardMemo(serverProfit);

			// 단일 게시글로 유지
			st = con.prepareStatement("DELETE FROM boards WHERE type=?");
			st.setString(1, POWERBALL_BOARD_TYPE);
			st.executeUpdate();
			st.close();

			st = con.prepareStatement(
				"INSERT INTO boards (uid, type, account_id, name, days, subject, memo) VALUES (?, ?, ?, ?, ?, ?, ?)");
			st.setInt(1, 1);
			st.setString(2, POWERBALL_BOARD_TYPE);
			st.setString(3, POWERBALL_BOARD_ACCOUNT);
			st.setString(4, POWERBALL_BOARD_WRITER);
			st.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			st.setString(6, subject);
			st.setString(7, memo);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("PowerballController.refreshRewardBoardPost()\r\n%s\r\n", e.toString());
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 매일 00:05(KST) 기준 자동 지급 실행.
	 * - 지급 대상일: 전날(reward_date = yesterday)
	 * - 이미 실행된 날짜는 중복 실행하지 않음
	 */
	public static void runAutoRewardSettlementIfDue() {
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
		// 00:05 이후(당일 남은 시간 전체 포함) 체크하여, 서버 재시작/지연 상황에서도 누락 없이 1회 실행
		int h = now.get(Calendar.HOUR_OF_DAY);
		int m = now.get(Calendar.MINUTE);
		boolean isDue = (h > 0) || (h == 0 && m >= 5);
		if (!isDue)
			return;

		Calendar target = (Calendar) now.clone();
		target.add(Calendar.DAY_OF_MONTH, -1);
		Date rewardDate = toSqlDate(target);

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			// 이미 실행했으면 종료
			st = con.prepareStatement("SELECT 1 FROM powerball_reward_run WHERE reward_date=? LIMIT 1");
			st.setDate(1, rewardDate);
			rs = st.executeQuery();
			if (rs.next()) {
				// 이미 정산됨 (GM 수동·이전 자동 등). 매분 스킵되므로 로그는 남기지 않음.
				return;
			}
			DatabaseConnection.close(null, st, rs);
			st = null;
			rs = null;

			long serverProfit = getServerProfitByKstDate(con, rewardDate);
			if (serverProfit <= 0) {
				// 원장을 넣지 않음: 예전에는 AUTO_0005_NEGATIVE_OR_ZERO 행이 남아 GM 수동 정산이 막히는 문제가 있었음.
				lineage.share.System.printf(
					"[파워볼 자동정산] 순이익 없음 — 스킵 reward_date=%s profit=%,d (원장 미기록, 익일 GM 수동 정산 가능)\r\n",
					rewardDate.toString(), serverProfit);
				return;
			}

			long poolFour = serverProfit * POOL_FOUR_CLASS_PERCENT / 100;
			long poolRoyal = serverProfit * POOL_ROYAL_PERCENT / 100;
			long[] fourAmounts = splitByRank(poolFour);
			long[] royalAmounts = splitByRank(poolRoyal);

			// 기사/요정/마법사/다크엘프 레벨 상위 3명 (class 1,2,3,4 병합 — GM 툴 네직업 풀과 동일 범위)
			List<RewardTarget> fourTargets = getTopTargetsByClass(con, "1,2,3,4", 3);
			// 군주 top3 (class 0)
			List<RewardTarget> royalTargets = getTopTargetsByClass(con, "0", 3);

			con.setAutoCommit(false);
			// 지급 + 라인 기록
			applyRewardGroup(con, rewardDate, "기사,요정,마법사", "four", fourTargets, fourAmounts);
			applyRewardGroup(con, rewardDate, "군주", "royal", royalTargets, royalAmounts);

			insertRewardRun(con, rewardDate, serverProfit, poolFour, poolRoyal, "AUTO_0005_DONE");
			con.commit();
			con.setAutoCommit(true);
			lineage.share.System.printf(
				"[파워볼 자동정산] 완료 reward_date=%s profit=%,d poolFour=%,d poolRoyal=%,d fourTargets=%d royalTargets=%d\r\n",
				rewardDate.toString(), serverProfit, poolFour, poolRoyal, fourTargets.size(), royalTargets.size());
		} catch (Exception e) {
			try {
				if (con != null)
					con.rollback();
			} catch (Exception ignore) {}
			lineage.share.System.printf("PowerballController.runAutoRewardSettlementIfDue()\r\n%s\r\n", e.toString());
		} finally {
			try {
				if (con != null)
					con.setAutoCommit(true);
			} catch (Exception ignore) {}
			DatabaseConnection.close(con, st, rs);
		}
	}

	private static Date toSqlDate(Calendar cal) {
		Calendar c = (Calendar) cal.clone();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return new Date(c.getTimeInMillis());
	}

	/**
	 * KST 날짜 기준 서버 순이익.
	 * GM 툴 powerball_economy.fetch_daily_summary 와 동일: (결과 행 KST일) OR (배팅 행 KST일) 로 포함하고,
	 * 당첨 지급은 결과 없으면 0 (LEFT JOIN + 가드). 배당은 lineage.conf powerball_payout_rate.
	 */
	private static long getServerProfitByKstDate(Connection con, Date kstDate) {
		double rate = Lineage.powerball_payout_rate;
		PreparedStatement st = null;
		ResultSet rs = null;
		long totalBet = 0L;
		long totalPaid = 0L;
		try {
			st = con.prepareStatement(
				"SELECT COALESCE(SUM(b.bet_amount),0) " +
				"FROM powerball_bets b " +
				"LEFT JOIN powerball_results r ON r.round_id = b.round_id " +
				"WHERE (DATE(DATE_ADD(r.created_at, INTERVAL ? HOUR)) = ?) " +
				"OR (DATE(DATE_ADD(b.created_at, INTERVAL ? HOUR)) = ?)");
			st.setInt(1, KST_OFFSET_HOURS);
			st.setDate(2, kstDate);
			st.setInt(3, KST_OFFSET_HOURS);
			st.setDate(4, kstDate);
			rs = st.executeQuery();
			if (rs.next())
				totalBet = rs.getLong(1);
		} catch (Exception ignore) {
		} finally {
			DatabaseConnection.close(null, st, rs);
		}

		try {
			st = con.prepareStatement(
				"SELECT COALESCE(SUM(CASE WHEN r.round_id IS NULL THEN 0 ELSE (" +
				"CASE " +
				"WHEN b.pick_type IN (0,1) AND b.pick_type = r.result_type THEN ROUND(b.bet_amount * ?) " +
				"WHEN b.pick_type IN (2,3) AND b.pick_type = " +
					"CASE WHEN COALESCE(r.under_over_type, CASE WHEN r.total_sum <= 72 THEN 0 ELSE 1 END) = 0 THEN 2 ELSE 3 END " +
					"THEN ROUND(b.bet_amount * ?) " +
				"ELSE 0 END) END), 0) " +
				"FROM powerball_bets b " +
				"LEFT JOIN powerball_results r ON r.round_id = b.round_id " +
				"WHERE (DATE(DATE_ADD(r.created_at, INTERVAL ? HOUR)) = ?) " +
				"OR (DATE(DATE_ADD(b.created_at, INTERVAL ? HOUR)) = ?)");
			st.setDouble(1, rate);
			st.setDouble(2, rate);
			st.setInt(3, KST_OFFSET_HOURS);
			st.setDate(4, kstDate);
			st.setInt(5, KST_OFFSET_HOURS);
			st.setDate(6, kstDate);
			rs = st.executeQuery();
			if (rs.next())
				totalPaid = rs.getLong(1);
		} catch (Exception ignore) {
		} finally {
			DatabaseConnection.close(null, st, rs);
		}
		return totalBet - totalPaid;
	}

	private static void insertRewardRun(Connection con, Date rewardDate, long serverProfit, long poolFour, long poolRoyal, String note) throws Exception {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement(
				"INSERT INTO powerball_reward_run (reward_date, server_profit, pool_four_class, pool_royal, rank_metric, note) " +
				"VALUES (?, ?, ?, ?, 'level', ?)");
			st.setDate(1, rewardDate);
			st.setLong(2, serverProfit);
			st.setLong(3, poolFour);
			st.setLong(4, poolRoyal);
			st.setString(5, note);
			st.executeUpdate();
		} finally {
			DatabaseConnection.close(null, st);
		}
	}

	private static List<RewardTarget> getTopTargetsByClass(Connection con, String classCsv, int limit) {
		List<RewardTarget> out = new ArrayList<RewardTarget>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = String.format(
				"SELECT objID, name, class FROM characters WHERE class IN (%s) AND block_date='0000-00-00 00:00:00' " +
				"AND COALESCE(gm,0)=0 " +
				"ORDER BY level DESC, exp DESC LIMIT ?", classCsv);
			st = con.prepareStatement(sql);
			st.setInt(1, limit);
			rs = st.executeQuery();
			while (rs.next()) {
				RewardTarget t = new RewardTarget();
				t.objId = rs.getInt("objID");
				t.name = rs.getString("name");
				t.classId = rs.getInt("class");
				out.add(t);
			}
		} catch (Exception e) {
		} finally {
			DatabaseConnection.close(null, st, rs);
		}
		return out;
	}

	private static void applyRewardGroup(Connection con, Date rewardDate, String classLabel, String slotPrefix, List<RewardTarget> targets, long[] amounts) throws Exception {
		for (int i = 0; i < 3; i++) {
			if (targets.size() <= i)
				break;
			long amount = amounts[i];
			if (amount <= 0)
				continue;

			RewardTarget t = targets.get(i);
			giveAdenaOffline(con, t.objId, t.name, amount);
			insertRewardLine(con, rewardDate, t, classLabel, i + 1, amount, slotPrefix + "_r" + (i + 1));
		}
	}

	/**
	 * 접속 중 캐릭터에게 DB 반영만으로는 인벤이 갱신되지 않으므로 GmDeliveryController 가 읽는 큐에 넣는다.
	 * (gm_adena_delivery: new_count = 첫 아데나 스택 수량, gm_item_delivery: 신규 행 objId 동기화)
	 */
	private static void tryEnqueueGmAdenaDelivery(Connection con, int chaObjId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		long total = 0L;
		try {
			st = con.prepareStatement(
				"SELECT count FROM characters_inventory WHERE cha_objId=? AND name=? ORDER BY objId ASC LIMIT 1");
			st.setInt(1, chaObjId);
			st.setString(2, "아데나");
			rs = st.executeQuery();
			if (rs.next())
				total = rs.getLong(1);
		} catch (Exception ignore) {
			return;
		} finally {
			DatabaseConnection.close(null, st, rs);
			st = null;
			rs = null;
		}
		try {
			st = con.prepareStatement("INSERT INTO gm_adena_delivery (cha_objId, new_count, delivered) VALUES (?, ?, 0)");
			st.setInt(1, chaObjId);
			st.setLong(2, total);
			st.executeUpdate();
		} catch (Exception ignore) {
			// 테이블 없음 등
		} finally {
			DatabaseConnection.close(null, st);
		}
	}

	private static void tryEnqueueGmItemDelivery(Connection con, int chaObjId, long objId) {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("INSERT INTO gm_item_delivery (cha_objId, objId, delivered) VALUES (?, ?, 0)");
			st.setLong(1, chaObjId);
			st.setLong(2, objId);
			st.executeUpdate();
		} catch (Exception ignore) {
		} finally {
			DatabaseConnection.close(null, st);
		}
	}

	private static void giveAdenaOffline(Connection con, int chaObjId, String chaName, long amount) throws Exception {
		if (amount <= 0)
			return;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// en/bress 조건은 실제 인벤(축복/인챈트 등)과 안 맞으면 UPDATE 0건 → 예외·전체 롤백이 잦음.
			// GM 툴과 동일하게 cha_objId+이름으로 첫 아데나 스택에 합산.
			st = con.prepareStatement(
				"UPDATE characters_inventory SET count=count+? WHERE cha_objId=? AND name=? ORDER BY objId ASC LIMIT 1");
			st.setLong(1, amount);
			st.setInt(2, chaObjId);
			st.setString(3, "아데나");
			int n = st.executeUpdate();
			DatabaseConnection.close(null, st, rs);
			st = null;
			if (n > 0) {
				tryEnqueueGmAdenaDelivery(con, chaObjId);
				return;
			}

			Item aden = ItemDatabase.find("아데나");
			long newObjId = ServerDatabase.nextItemObjId();
			st = con.prepareStatement(
				"INSERT INTO characters_inventory SET objId=?, cha_objId=?, cha_name=?, name=?, count=?, en=0, definite=1, bress=1, 구분1=?, 구분2=?");
			st.setLong(1, newObjId);
			st.setInt(2, chaObjId);
			st.setString(3, chaName);
			st.setString(4, "아데나");
			st.setLong(5, amount);
			st.setString(6, aden == null ? "etc" : aden.getType1());
			st.setString(7, aden == null ? "etc" : aden.getType2());
			st.executeUpdate();
			DatabaseConnection.close(null, st, rs);
			st = null;
			tryEnqueueGmItemDelivery(con, chaObjId, newObjId);
			tryEnqueueGmAdenaDelivery(con, chaObjId);
		} finally {
			DatabaseConnection.close(null, st, rs);
		}
	}

	private static void insertRewardLine(Connection con, Date rewardDate, RewardTarget t, String classLabel, int rank, long amount, String slotKey) throws Exception {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement(
				"INSERT INTO powerball_reward_line (reward_date, char_obj_id, char_name, class_id, class_label, rank_in_class, amount, slot_key) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			st.setDate(1, rewardDate);
			st.setInt(2, t.objId);
			st.setString(3, t.name);
			st.setInt(4, t.classId);
			st.setString(5, classLabel);
			st.setInt(6, rank);
			st.setLong(7, amount);
			st.setString(8, slotKey);
			st.executeUpdate();
		} finally {
			DatabaseConnection.close(null, st);
		}
	}

	private static class RewardTarget {
		int objId;
		String name;
		int classId;
	}

	private static String getTodayLabel() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return String.format("%d월 %d일", month, day);
	}

	private static long getTodayServerProfit(Connection con) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return getServerProfitByKstDate(con, new Date(cal.getTimeInMillis()));
	}

	private static String buildBoardMemo(long serverProfit) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s 현재 서버의 수익금 %,d아데나\r\n", getTodayLabel(), serverProfit));
		sb.append("(다음날 00:05초에 자동지급)\r\n");

		if (serverProfit <= 0) {
			sb.append("*기사, 요정, 마법사, 다크엘프 (통합 TOP3)\r\n");
			sb.append("현재 정산금은 없습니다.\r\n");
			sb.append("*군주\r\n");
			sb.append("현재 정산금은 없습니다.\r\n");
			return sb.toString();
		}

		long poolFour = serverProfit * POOL_FOUR_CLASS_PERCENT / 100;
		long poolRoyal = serverProfit * POOL_ROYAL_PERCENT / 100;
		long[] fourAmounts = splitByRank(poolFour);
		long[] royalAmounts = splitByRank(poolRoyal);

		sb.append(String.format("*기사·요정·마법사·다크엘프 통합 TOP3 (풀 %d%%)\r\n", POOL_FOUR_CLASS_PERCENT));
		appendRankAmountLines(sb, fourAmounts);
		sb.append(String.format("*군주 (풀 %d%%)\r\n", POOL_ROYAL_PERCENT));
		appendRankAmountLines(sb, royalAmounts);
		return sb.toString();
	}

	private static long[] splitByRank(long poolAmount) {
		long[] out = new long[3];
		long sum = 0L;
		for (int i = 0; i < 3; i++) {
			out[i] = poolAmount * RANK_SPLIT_PERCENT[i] / 100;
			sum += out[i];
		}
		// 나눗셈 오차 보정
		out[0] += (poolAmount - sum);
		return out;
	}

	/** 1~3위 지급 예정액만 표시 (캐릭터명 없음) */
	private static void appendRankAmountLines(StringBuilder sb, long[] amounts) {
		if (amounts == null || amounts.length < 3) {
			sb.append("현재 정산금은 없습니다.\r\n");
			return;
		}
		for (int i = 0; i < 3; i++) {
			sb.append(String.format("%d위 : %,d아데나\r\n", i + 1, amounts[i]));
		}
	}
}
