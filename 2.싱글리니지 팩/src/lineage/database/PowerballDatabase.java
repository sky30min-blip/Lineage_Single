package lineage.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 파워볼 미니게임 DB (powerball_results, powerball_bets)
 */
public final class PowerballDatabase {

	/** 총합 ≤ 이면 언더(0), 초과면 오버(1) — 컬럼 없을 때 total_sum 으로 계산 */
	private static final int UNDER_OVER_TOTAL_LINE = 72;

	/**
	 * powerball_results.under_over_type 존재 여부 (1회 검사 후 캐시).
	 * 마이그레이션 전 DB는 false → INSERT/SELECT 에서 컬럼 생략, 언더/오버는 total_sum 으로 유도.
	 */
	private static volatile int underOverColumnState = 0; // 0=미확인, 1=있음, -1=없음
	private static boolean loggedUnderOverMissing = false;

	private static boolean isUnderOverColumnPresent(Connection con) {
		int s = underOverColumnState;
		if (s != 0)
			return s > 0;
		synchronized (PowerballDatabase.class) {
			if (underOverColumnState != 0)
				return underOverColumnState > 0;
			boolean ok = false;
			try (java.sql.PreparedStatement st = con.prepareStatement(
				"SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() " +
				"AND TABLE_NAME = 'powerball_results' AND COLUMN_NAME = 'under_over_type' LIMIT 1");
				ResultSet r = st.executeQuery()) {
				ok = r.next();
			} catch (Exception ignore) {
				ok = false;
			}
			underOverColumnState = ok ? 1 : -1;
			if (!ok && !loggedUnderOverMissing) {
				loggedUnderOverMissing = true;
				lineage.share.System.println(
					"[파워볼] powerball_results 에 under_over_type 컬럼이 없습니다. total_sum(≤" + UNDER_OVER_TOTAL_LINE + ")으로 언더/오버를 계산합니다. " +
					"컬럼 추가 권장: db/powerball_under_over_migration.sql 또는 db/run_powerball_under_over.py");
			}
			return ok;
		}
	}

	private static int underOverTypeFromTotalSum(int totalSum) {
		return totalSum <= UNDER_OVER_TOTAL_LINE ? 0 : 1;
	}

	/** 현재 배팅 가능 회차 = 결과 테이블 최대 회차 + 1 */
	public static int getCurrentRound() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT COALESCE(MAX(round_id), 0) + 1 FROM powerball_results");
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCurrentRound()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 1;
	}

	/** 해당 캐릭터가 이 회차에 이미 배팅했는지 (홀/짝·언더/오버 합쳐 1건 이상) */
	public static boolean hasBetThisRound(int charId, int roundId) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT 1 FROM powerball_bets WHERE char_id = ? AND round_id = ? LIMIT 1");
			st.setInt(1, charId);
			st.setInt(2, roundId);
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : hasBetThisRound()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
			return true; // 오류 시 중복 허용 방지
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/** 이 회차에 홀/짝( pick 0·1 ) 이미 구매했는지 */
	public static boolean hasOddEvenBetThisRound(int charId, int roundId) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement(
				"SELECT 1 FROM powerball_bets WHERE char_id = ? AND round_id = ? AND pick_type IN (0,1) LIMIT 1");
			st.setInt(1, charId);
			st.setInt(2, roundId);
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : hasOddEvenBetThisRound()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
			return true;
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/** 이 회차에 언더/오버( pick 2·3 ) 이미 구매했는지 */
	public static boolean hasUnderOverBetThisRound(int charId, int roundId) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement(
				"SELECT 1 FROM powerball_bets WHERE char_id = ? AND round_id = ? AND pick_type IN (2,3) LIMIT 1");
			st.setInt(1, charId);
			st.setInt(2, roundId);
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : hasUnderOverBetThisRound()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
			return true;
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/** 배팅 등록 — 홀/짝·언더/오버는 회차당 각각 1회 */
	public static boolean insertBet(int charId, int roundId, long betAmount, int pickType) {
		if (pickType >= 0 && pickType <= 1) {
			if (hasOddEvenBetThisRound(charId, roundId))
				return false;
		} else if (pickType >= 2 && pickType <= 3) {
			if (hasUnderOverBetThisRound(charId, roundId))
				return false;
		} else
			return false;
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement(
				"INSERT INTO powerball_bets (char_id, round_id, bet_amount, pick_type, is_processed) VALUES (?, ?, ?, ?, 0)");
			st.setInt(1, charId);
			st.setInt(2, roundId);
			st.setLong(3, betAmount);
			st.setInt(4, pickType);
			st.executeUpdate();
			return true;
		} catch (Exception e) {
			lineage.share.System.printf("%s : insertBet()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
			return false;
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	/** 아직 정산하지 않은 결과 회차 목록 (정산용) */
	public static List<int[]> getUnsettledResults() {
		List<int[]> list = new ArrayList<int[]>();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			boolean uoCol = isUnderOverColumnPresent(con);
			String sql = uoCol
				? "SELECT round_id, total_sum, result_type, under_over_type FROM powerball_results r "
					+ "WHERE EXISTS (SELECT 1 FROM powerball_bets b WHERE b.round_id = r.round_id AND b.is_processed = 0) "
					+ "ORDER BY round_id"
				: "SELECT round_id, total_sum, result_type FROM powerball_results r "
					+ "WHERE EXISTS (SELECT 1 FROM powerball_bets b WHERE b.round_id = r.round_id AND b.is_processed = 0) "
					+ "ORDER BY round_id";
			st = con.prepareStatement(sql);
			rs = st.executeQuery();
			while (rs.next()) {
				int rid = rs.getInt(1);
				int tsum = rs.getInt(2);
				int rt = rs.getInt(3);
				int uot = uoCol ? rs.getInt(4) : underOverTypeFromTotalSum(tsum);
				list.add(new int[] { rid, tsum, rt, uot });
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : getUnsettledResults()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return list;
	}

	/** 해당 회차 당첨 배팅 목록 (char_id, bet_amount) - pick_type 이 결과와 일치 */
	public static List<long[]> getWinningBets(int roundId, int resultType) {
		List<long[]> list = new ArrayList<long[]>();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement(
				"SELECT id, char_id, bet_amount FROM powerball_bets WHERE round_id = ? AND pick_type = ? AND is_processed = 0");
			st.setInt(1, roundId);
			st.setInt(2, resultType);
			rs = st.executeQuery();
			while (rs.next()) {
				list.add(new long[] { rs.getLong("id"), rs.getInt("char_id"), rs.getLong("bet_amount") });
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : getWinningBets()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return list;
	}

	/**
	 * 서버 자체 생성 결과 INSERT.
	 * - 동일 회차가 이미 있으면 UPDATE (멱등).
	 * - id AUTO_INCREMENT 꼬임으로 Duplicate PRIMARY 시 MAX(id)+1 로 보정 후 1회 재시도.
	 */
	public static boolean insertResult(int roundId, int totalSum, int resultType, int underOverType) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			boolean uoCol = isUnderOverColumnPresent(con);
			st = con.prepareStatement("SELECT 1 FROM powerball_results WHERE round_id = ? LIMIT 1");
			st.setInt(1, roundId);
			rs = st.executeQuery();
			if (rs.next()) {
				DatabaseConnection.close(null, st, rs);
				rs = null;
				if (uoCol) {
					st = con.prepareStatement(
						"UPDATE powerball_results SET total_sum = ?, result_type = ?, under_over_type = ? WHERE round_id = ?");
					st.setInt(1, totalSum);
					st.setInt(2, resultType);
					st.setInt(3, underOverType);
					st.setInt(4, roundId);
				} else {
					st = con.prepareStatement(
						"UPDATE powerball_results SET total_sum = ?, result_type = ? WHERE round_id = ?");
					st.setInt(1, totalSum);
					st.setInt(2, resultType);
					st.setInt(3, roundId);
				}
				st.executeUpdate();
				return true;
			}
			DatabaseConnection.close(null, st, rs);
			rs = null;
			st = null;
			if (uoCol) {
				st = con.prepareStatement(
					"INSERT INTO powerball_results (round_id, total_sum, result_type, under_over_type) VALUES (?, ?, ?, ?)");
				st.setInt(1, roundId);
				st.setInt(2, totalSum);
				st.setInt(3, resultType);
				st.setInt(4, underOverType);
			} else {
				st = con.prepareStatement(
					"INSERT INTO powerball_results (round_id, total_sum, result_type) VALUES (?, ?, ?)");
				st.setInt(1, roundId);
				st.setInt(2, totalSum);
				st.setInt(3, resultType);
			}
			st.executeUpdate();
			return true;
		} catch (Exception e) {
			String msg = e.getMessage() != null ? e.getMessage() : "";
			if (msg.contains("Duplicate") && msg.contains("PRIMARY")) {
				try {
					Connection c2 = DatabaseConnection.getLineage();
					java.sql.Statement stmt = c2.createStatement();
					try {
						ResultSet r2 = stmt.executeQuery(
							"SELECT COALESCE(MAX(id), 0) + 1 AS n FROM powerball_results");
						if (r2.next()) {
							int nextAi = Math.max(1, r2.getInt("n"));
							r2.close();
							stmt.executeUpdate(
								"ALTER TABLE powerball_results AUTO_INCREMENT = " + nextAi);
							boolean uo2 = isUnderOverColumnPresent(c2);
							if (uo2) {
								try (PreparedStatement ins = c2.prepareStatement(
									"INSERT INTO powerball_results (round_id, total_sum, result_type, under_over_type) VALUES (?, ?, ?, ?)")) {
									ins.setInt(1, roundId);
									ins.setInt(2, totalSum);
									ins.setInt(3, resultType);
									ins.setInt(4, underOverType);
									ins.executeUpdate();
								} catch (Exception insEx) {
									try (PreparedStatement up = c2.prepareStatement(
										"UPDATE powerball_results SET total_sum = ?, result_type = ?, under_over_type = ? WHERE round_id = ?")) {
										up.setInt(1, totalSum);
										up.setInt(2, resultType);
										up.setInt(3, underOverType);
										up.setInt(4, roundId);
										if (up.executeUpdate() < 1)
											throw insEx;
									}
								}
							} else {
								try (PreparedStatement ins = c2.prepareStatement(
									"INSERT INTO powerball_results (round_id, total_sum, result_type) VALUES (?, ?, ?)")) {
									ins.setInt(1, roundId);
									ins.setInt(2, totalSum);
									ins.setInt(3, resultType);
									ins.executeUpdate();
								} catch (Exception insEx) {
									try (PreparedStatement up = c2.prepareStatement(
										"UPDATE powerball_results SET total_sum = ?, result_type = ? WHERE round_id = ?")) {
										up.setInt(1, totalSum);
										up.setInt(2, resultType);
										up.setInt(3, roundId);
										if (up.executeUpdate() < 1)
											throw insEx;
									}
								}
							}
							stmt.close();
							DatabaseConnection.close(c2);
							return true;
						}
						r2.close();
					} catch (Exception ignore) {
						/* id 컬럼 없는 스키마(round_id만 PK)면 여기서 실패 */
					}
					stmt.close();
					DatabaseConnection.close(c2);
				} catch (Exception e2) {
					lineage.share.System.printf("%s : insertResult() retry\r\n",
						PowerballDatabase.class.toString());
					lineage.share.System.println(e2);
				}
			}
			lineage.share.System.printf("%s : insertResult()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
			return false;
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	/** 해당 캐릭터·회차·pick_type 배팅 1건 (id, bet_amount, pick_type) — 매입 시 쿠폰 종류와 일치하는 행만 */
	public static long[] getBetByCharRound(int charId, int roundId, int pickType) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement(
				"SELECT id, bet_amount, pick_type FROM powerball_bets WHERE char_id = ? AND round_id = ? AND pick_type = ? AND is_processed = 1 AND COALESCE(claimed, 0) = 0 LIMIT 1");
			st.setInt(1, charId);
			st.setInt(2, roundId);
			st.setInt(3, pickType);
			rs = st.executeQuery();
			if (rs.next())
				return new long[] { rs.getLong("id"), rs.getLong("bet_amount"), rs.getInt("pick_type") };
		} catch (Exception e) {
			lineage.share.System.printf("%s : getBetByCharRound()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return null;
	}

	/** 환불용: 해당 pick_type, claimed=0 (is_processed 무관) */
	public static long[] getBetByCharRoundForRefund(int charId, int roundId, int pickType) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement(
				"SELECT id, bet_amount, pick_type FROM powerball_bets WHERE char_id = ? AND round_id = ? AND pick_type = ? AND COALESCE(claimed, 0) = 0 LIMIT 1");
			st.setInt(1, charId);
			st.setInt(2, roundId);
			st.setInt(3, pickType);
			rs = st.executeQuery();
			if (rs.next())
				return new long[] { rs.getLong("id"), rs.getLong("bet_amount"), rs.getInt("pick_type") };
		} catch (Exception e) {
			lineage.share.System.printf("%s : getBetByCharRoundForRefund()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return null;
	}

	/** 해당 회차 결과 값 (0: 짝, 1: 홀) - 없으면 -1 */
	public static int getResultForRound(int roundId) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT result_type FROM powerball_results WHERE round_id = ?");
			st.setInt(1, roundId);
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt("result_type");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getResultForRound()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return -1;
	}

	/** 해당 회차 언더/오버 (0: 언더, 1: 오버) — 없으면 -1 */
	public static int getUnderOverForRound(int roundId) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			if (isUnderOverColumnPresent(con)) {
				st = con.prepareStatement("SELECT under_over_type FROM powerball_results WHERE round_id = ?");
				st.setInt(1, roundId);
				rs = st.executeQuery();
				if (rs.next())
					return rs.getInt("under_over_type");
			} else {
				st = con.prepareStatement("SELECT total_sum FROM powerball_results WHERE round_id = ?");
				st.setInt(1, roundId);
				rs = st.executeQuery();
				if (rs.next())
					return underOverTypeFromTotalSum(rs.getInt(1));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : getUnderOverForRound()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return -1;
	}

	/** 매입 완료 처리 (중복 수령 방지) */
	public static void setClaimed(long betId) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE powerball_bets SET claimed = 1 WHERE id = ?");
			st.setLong(1, betId);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : setClaimed()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	/** 해당 회차 미처리 배팅 전부 처리 완료로 표시 */
	public static void markBetsProcessed(int roundId) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE powerball_bets SET is_processed = 1 WHERE round_id = ?");
			st.setInt(1, roundId);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : markBetsProcessed()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	// ---------- 당일(KST) 홀/짝·언더/오버 누적 — 서버 재시작 후에도 이어짐 (powerball_daily_counts) ----------

	private static volatile boolean dailyCountsTableEnsured = false;

	private static void ensureDailyCountsTable(Connection con) {
		if (dailyCountsTableEnsured)
			return;
		synchronized (PowerballDatabase.class) {
			if (dailyCountsTableEnsured)
				return;
			try (PreparedStatement st = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS powerball_daily_counts ("
							+ "stat_date DATE NOT NULL PRIMARY KEY COMMENT 'KST 달력일',"
							+ "odd_count INT NOT NULL DEFAULT 0,"
							+ "even_count INT NOT NULL DEFAULT 0,"
							+ "under_count INT NOT NULL DEFAULT 0,"
							+ "over_count INT NOT NULL DEFAULT 0,"
							+ "updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)"
							+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파워볼 당일 결과 누적(진행자 멘트용)'")) {
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.println("[파워볼] powerball_daily_counts 테이블 생성 실패: " + e);
			}
			dailyCountsTableEnsured = true;
		}
	}

	/** 한국시간 기준 오늘 날짜(자정 기준) */
	public static Date todayKstSqlDate() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}

	/**
	 * 당일 집계 로드. 행 없으면 0.
	 * @return { odd, even, under, over }
	 */
	public static int[] loadDailyCounts(Date statDate) {
		int[] out = new int[] { 0, 0, 0, 0 };
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			ensureDailyCountsTable(con);
			st = con.prepareStatement(
					"SELECT odd_count, even_count, under_count, over_count FROM powerball_daily_counts WHERE stat_date = ? LIMIT 1");
			st.setDate(1, statDate);
			rs = st.executeQuery();
			if (rs.next()) {
				out[0] = rs.getInt(1);
				out[1] = rs.getInt(2);
				out[2] = rs.getInt(3);
				out[3] = rs.getInt(4);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : loadDailyCounts()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return out;
	}

	/** 당일 집계 저장(UPSERT). 추첨 직후 호출. */
	public static void saveDailyCounts(Date statDate, int odd, int even, int under, int over) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			ensureDailyCountsTable(con);
			st = con.prepareStatement(
					"INSERT INTO powerball_daily_counts (stat_date, odd_count, even_count, under_count, over_count) VALUES (?,?,?,?,?) "
							+ "ON DUPLICATE KEY UPDATE odd_count=VALUES(odd_count), even_count=VALUES(even_count), "
							+ "under_count=VALUES(under_count), over_count=VALUES(over_count)");
			st.setDate(1, statDate);
			st.setInt(2, odd);
			st.setInt(3, even);
			st.setInt(4, under);
			st.setInt(5, over);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : saveDailyCounts()\r\n", PowerballDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
}
