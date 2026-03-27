package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.eclipse.swt.widgets.Display;

import lineage.gui.GuiMain;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.controller.PcTradeController;

public final class ServerDatabase {

	static private String name;					// 서버 이름.
	public static Date serverOpneDate; 
	static private long pc_objid;				// 사용자가 사용할 obj값
	static private long item_objid;				// 아이템에 사용되는 obj값
	static private long npc_objid;
	static private long temp_etc_objid;			// 몬스터나 배경이미지등이 스폰을 다 마치고 마지막 값을 임시 저장함.
	static private long etc_objid;				// 몬스터나 배경이미지 등에 사용되는 obj값
	static private long inn_objid;				// 여관 고유 값.
	static public long LineageWorldTime;		// 리니지월드 시간
	static private Date LineageWorlddDate;
	static private int ServerOperatingTime;		// 서버 가동 시간
	static private Object sync = new Object();
	
	/**
	 * 초기화 함수.
	 * @param con
	 */
	static public void init(Connection con) {
		TimeLine.start("ServerDatabase..");

		etc_objid = 1;
		npc_objid = 100000;
		LineageWorlddDate = new Date(0);
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM server LIMIT 1");
			rs = st.executeQuery();
			if (rs.next()) {
				name = rs.getString("name");
				serverOpneDate = rs.getDate("sever_open_date");
				pc_objid = rs.getInt("pc_objid");
				item_objid = rs.getInt("item_objid");
				PcTradeController.uid = rs.getInt("pc_trade_uid");
				inn_objid = rs.getInt("inn_key");
				LineageWorldTime = rs.getInt("world_time");

				updateRunning(con, true);
			}

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (name != null)
						GuiMain.shell.setText(name);
				}
			});
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ServerDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	static public void reload() {
		TimeLine.start("server 테이블 리로드 - ");

		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM server LIMIT 1");
			rs = st.executeQuery();
			if (rs.next()) {
				name = rs.getString("name");
				serverOpneDate = rs.getDate("sever_open_date");
			}

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (name != null)
						GuiMain.shell.setText(name);
				}
			});
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload()\r\n", ServerDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		TimeLine.end();
	}
	
	/**
	 * 서버 닫힐때 호출되는 함수.
	 *  : 사용된 정보 디비에 저장처리.
	 * @param con
	 */
	static public void close(Connection con){
		toSave(con);
		updateRunning(con, false);
	}
	
	/**
	 * 사용자들이 사용할 objid 다음거 리턴.
	 * @return
	 */
	static public long nextPcObjId() {
		synchronized (sync) {
			return pc_objid++;
		}
	}
	
	/**
	 * 몬스터나 기타 백그라운드 객체들이 사용할 objid 리턴
	 * @return
	 */
	static public long nextEtcObjId(){
		synchronized (sync) {
			// 499999 이상이라면 이전에 기억했던 마지막값으로 다시 돌리기. 재사용 하는거임.
			if(etc_objid > 499999)
				etc_objid = temp_etc_objid;
			return etc_objid++;
		}
	}
	
	static public long nextNpcObjId() {
		synchronized (sync) {
			if(npc_objid > 999999)
				npc_objid = 100000;
			return npc_objid++;
		}
	}
	
	/**
	 * 아이템 전용 objid 리턴
	 * @return
	 */
	static public long nextItemObjId(){
		synchronized (sync) {
			return item_objid++;
		}
	}
	
	/**
	 * 여관 전용 objid 리턴
	 * @return
	 */
	static public long nextInnObjId(){
		synchronized (sync) {
			return inn_objid++;
		}
	}

	/**
	 * etc_objid 의 마지막값 저장.
	 *  : 몬스터나 백그라운드 로딩끝낸후 이것을 호출해서 기록함.
	 *  : etc_objid를 재사용하기위해 사이클형태 알고리즘 유도때문에 만듬.
	 */
	static public void updateEtcObjId(){
		synchronized (sync) {
			temp_etc_objid = etc_objid;
			if(temp_etc_objid <= 0)
				temp_etc_objid = 1;
		}
	}
	
	/**
	 * 리니지의 월드시간을 다음으로 연장시킬때 호출해서 사용하는 함수.
	 */
	static public void nextTime(){
		//
		LineageWorldTime += 6;
		LineageWorlddDate.setTime( (LineageWorldTime*1000) + (1000*60*60*15) );
		//
		ServerOperatingTime += 1;
	}
	
	static public int getLineageTimeHour(){
		return LineageWorlddDate.getHours();
	}
	
	static public int getLineageTimeMinute(){
		return LineageWorlddDate.getMinutes();
	}
	
	static public int getLineageTimeSeconds(){
		return LineageWorlddDate.getSeconds();
	}
	
	static public String toOperatingTime() {
		// 
		int day = ServerOperatingTime / (60*60*24);
		int hour = ((ServerOperatingTime-(day*(60*60*24))) / 3600);
		int min = ((ServerOperatingTime-(hour*(60*60))-(day*(60*60*24))) / 60);
		return String.format("%dday %02d:%02d:%02d", day, hour, min, ServerOperatingTime%60);
	}

	/**
	 * 현재 리니지 월드가 밤인지 확인해주는 함수.
	 *  : 6:00-17:59
	 * @return
	 */
	static public boolean isNight() {
		int hour = getLineageTimeHour();
		return !(hour>=6 && hour<=17);
	}
	
	/**
	 * 정보 저장.
	 * @param con
	 */
	static public void toSave(Connection con){
		synchronized (sync) {
			PreparedStatement st = null;
			try{
				st = con.prepareStatement("UPDATE server SET pc_objid=?, item_objid=?, inn_key=?, player_count=?, world_time=?, pc_trade_uid=?");
				st.setLong(1, pc_objid);
				st.setLong(2, item_objid);
				st.setLong(3, inn_objid);
				st.setLong(4, World.getPcSize());
				st.setLong(5, LineageWorldTime);
				st.setLong(6, PcTradeController.uid);
				st.executeUpdate();
			}catch(Exception e){
				lineage.share.System.printf("%s : toSave(Connection con)\r\n", ServerDatabase.class.toString());
				lineage.share.System.println(e);
			}finally{
				DatabaseConnection.close(st);
			}
		}
	}
	
	/**
	 * 정보 저장.
	 */
	static public void toSave(){
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			toSave(con);
		} catch (Exception e) {
			lineage.share.System.printf("%s : toSave()\r\n", ServerDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
	}
	
	static public void updateRunning(Connection con, boolean running){
		PreparedStatement st = null;
		try{
			st = con.prepareStatement("UPDATE server SET running=?");
			st.setString(1, String.valueOf(running));
			st.executeUpdate();
		}catch(Exception e){
			lineage.share.System.printf("%s : updateRunning(Connection con, boolean running)\r\n", ServerDatabase.class.toString());
			lineage.share.System.println(e);
		}finally{
			DatabaseConnection.close(st);
		}
	}

	public static long getPc_objid() {
		synchronized (sync) {
			return pc_objid;
		}
	}

	public static long getItem_objid() {
		synchronized (sync) {
			return item_objid;
		}
	}

	public static long getTemp_etc_objid() {
		synchronized (sync) {
			return temp_etc_objid;
		}
	}

	public static long getEtc_objid() {
		synchronized (sync) {
			return etc_objid;
		}
	}

	public static long getInn_objid() {
		synchronized (sync) {
			return inn_objid;
		}
	}

	public static long getNpc_objid() {
		synchronized (sync) {
			return npc_objid;
		}
	}
	
	public static long getLineageWorldTime() {
		return LineageWorldTime;
	}

	public static Date getLineageWorlddDate() {
		return LineageWorlddDate;
	}
	
	public static String getName(){
		return name;
	}
}
