package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.share.Common;
import lineage.share.Mysql;
import lineage.share.TimeLine;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DatabaseConnection {
	
	/** HikariCP */
	private static HikariDataSource fairy;
	private static HikariDataSource donation_fairy;
	
	static public void init(){
		TimeLine.start("DatabaseConnection..");
		
		try {
			if (Mysql.driver == null || Mysql.driver.length() == 0
					|| Mysql.url == null || Mysql.url.length() == 0
					|| Mysql.id == null || Mysql.id.length() == 0
					|| Mysql.pw == null) {
				lineage.share.System.println("DatabaseConnection: mysql.conf 에 driver / url / id / pw 가 없습니다. 서버 실행 위치(작업 폴더)가 '2.싱글리니지 팩' 인지 확인하세요.");
				lineage.share.System.println("user.dir = " + java.lang.System.getProperty("user.dir"));
				TimeLine.end();
				return;
			}

			HikariConfig config = new HikariConfig();

			config.setDriverClassName(Mysql.driver);
			config.setJdbcUrl(Mysql.url);
			config.setUsername(Mysql.id);
			config.setPassword(Mysql.pw);
			/**서버 환경에 맞게끔 수정*/
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("useServerPrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			/** 구형 MySQL 커넥터 + JDBC4 isValid() 조합에서 NPE가 나는 경우가 있어 명시적 검증 쿼리 사용 */
			config.setConnectionTestQuery("SELECT 1");
			config.setMinimumIdle(4);
			config.setMaximumPoolSize(800);
			/** 60000(60초)은 Hikari 권장보다 지나치게 짧아 연결 주기 폐기 시 이상 동작을 유발할 수 있음 */
			config.setMaxLifetime(1800000);

			fairy = new HikariDataSource(config);
			
			if (Mysql.is_donation) {
				config.setDriverClassName(Mysql.driver);
				config.setJdbcUrl(Mysql.donation_url);
				config.setUsername(Mysql.id);
				config.setPassword(Mysql.pw);
				/**서버 환경에 맞게끔 수정*/
				config.addDataSourceProperty("cachePrepStmts", "true");
				config.addDataSourceProperty("useServerPrepStmts", "true");
				config.addDataSourceProperty("prepStmtCacheSize", "250");
				config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
				config.setMaximumPoolSize(10);
				
				donation_fairy = new HikariDataSource(config);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", DatabaseConnection.class.toString());
			lineage.share.System.println(e);
		}
		
		TimeLine.end();
	}
	
	static public void close(){
		if (fairy != null)
			fairy.close();
		
		if (donation_fairy != null) {
			donation_fairy.close();
		}
	}
	
	/**
	 * 풀에 등록된 컨넥션 한개 추출하기.
	 * @return
	 * @throws Exception
	 */
	static public Connection getLineage() throws Exception {		
		Connection con = null;
		do {
			con = fairy.getConnection();
			Thread.sleep(Common.THREAD_SLEEP);
		} while (con == null);
		return con;
	}
	
	static public Connection getDonation() throws Exception {
		Connection con = null;
		do {
			con = donation_fairy.getConnection();
			Thread.sleep(Common.THREAD_SLEEP);
		} while (con == null);
		return con;
	}
	
	static public void close(Connection con) {
		try { con.close(); } catch (Exception e) {}
	}
	
	static public void close(Connection con, PreparedStatement st) {
		close(st);
		close(con);
	}
	
	static public void close(Connection con, PreparedStatement st, ResultSet rs) {
		close(st, rs);
		close(con);
	}
	
	static public void close(PreparedStatement st) {
		try { st.close(); } catch (Exception e) {}
	}
	
	static public void close(PreparedStatement st, ResultSet rs) {
		try { rs.close(); } catch (Exception e) {}
		close(st);
	}
}
