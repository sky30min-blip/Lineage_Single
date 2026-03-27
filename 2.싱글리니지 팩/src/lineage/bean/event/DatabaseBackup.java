package lineage.bean.event;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.database.DatabaseConnection;
import lineage.database.ServerDatabase;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.Mysql;
import lineage.share.System;
import lineage.util.Util;


public class DatabaseBackup implements Event {
	
	static synchronized public Event clone(Event e){
		if(e == null)
			e = new DatabaseBackup();
		return e;
	}
	
	@Override
	public void init() {
		long time = System.currentTimeMillis();
		System.println("DB 백업을 시작합니다.");

		// 폴더 확인.
		File dir = new File(Mysql.auto_backup_path);
		if(dir.isDirectory() == false){
			System.println("backup 폴더가 존재하지않아 생성하였습니다.");
			dir.mkdir();
		}
		
		// 정보 추출.
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			
			// 추출하기전 필요한 정보 저장.
			ServerDatabase.toSave(con);
			
			List<String> li_type1 = new ArrayList<String>();	// 일반 칼럼
			List<String> li_type2 = new ArrayList<String>();	// 키쪽
			List<String> pri = new ArrayList<String>();			// 프리미엄 키
			String file_name = String.format("%s/[%s] %d.sql", Mysql.auto_backup_path, Util.getLocaleString(time, true), Lineage.server_version);
			
			// 1차 기록.
			Log.write(file_name, "/*\r\nsp 자동 백업 시스템\r\n*/\r\nSET FOREIGN_KEY_CHECKS=0;");

			st = con.prepareStatement("SHOW TABLES");
			rs = st.executeQuery();
			while(rs.next()){
				String table = rs.getString(1);
				int size1 = 0;
				int size2 = 0;
				
				StringBuffer row_db = new StringBuffer();
				row_db.append( String.format("\r\n-- %s 테이블 정보\r\n", table) );
				row_db.append( "CREATE TABLE `" );
				row_db.append( table );
				row_db.append( "` (\r\n" );
				
				// 해당 테이블에 정보 추출.
				PreparedStatement st1 = con.prepareStatement( String.format("DESC %s", table) );
				ResultSet rs1 = st1.executeQuery();
				while(rs1.next()){
					StringBuffer db = new StringBuffer();
					StringBuffer db2 = new StringBuffer();
					if(rs1.getString(1) != null){
						// field
						db.append(" `");
						db.append(rs1.getString(1));
						db.append("` ");
					}
					if(rs1.getString(2) != null){
						// type
						db.append(rs1.getString(2));
						db.append(" ");
					}
					if(rs1.getString(3) != null){
						// null
						if(rs1.getString(3).equalsIgnoreCase("NO"))
							db.append("NOT NULL ");
					}
					if(rs1.getString(6) != null){
						// extra
						db.append(rs1.getString(6));
						db.append(" ");
					}
					if(rs1.getString(5) != null){
						// default
						db.append("default '");
						db.append(rs1.getString(5).trim());
						db.append("'");
						if(rs1.getString(4).equalsIgnoreCase("PRI")){
							db2.append("KEY `");
							db2.append(rs1.getString(1));
							db2.append("` (`");
							db2.append(rs1.getString(1));
							db2.append("`)");
							li_type2.add( db2.toString() );
						}
					}
					if(rs1.getString(4) != null){
						// key
						if(rs1.getString(4).equalsIgnoreCase("PRI")){
							pri.add( rs1.getString(1) );
						}else if(rs1.getString(4).equalsIgnoreCase("MUL")){
							db2.append("KEY `");
							db2.append(rs1.getString(1));
							db2.append("` (`");
							db2.append(rs1.getString(1));
							db2.append("`)");
							li_type2.add( db2.toString() );
						}
					}

					li_type1.add( db.toString() );
				}
				rs1.close();
				st1.close();

				int s = pri.size();
				if(s > 0){
					StringBuffer db2 = new StringBuffer();
					db2.append("PRIMARY KEY  (");
					for(String p : pri){
						db2.append("`");
						db2.append(p);
						db2.append("`");
						if(--s > 0){
							db2.append(", ");
						}
					}
					db2.append(")");
					li_type2.add( db2.toString() );
				}

				size1 = li_type1.size();
				size2 = li_type2.size();
				for(String p : li_type1){
					row_db.append( p );
					if(size2==0){
						if(--size1>0)
							row_db.append( "," );
					}else{
						row_db.append( "," );
					}
					row_db.append( "\r\n" );
				}
				for(String p : li_type2){
					row_db.append( p );
					if(--size2>0)
						row_db.append( "," );
					row_db.append( "\r\n" );
				}
				row_db.append( ") ENGINE=MyISAM DEFAULT CHARSET=utf8;" );

				size1 = li_type1.size();
				size2 = li_type2.size();

				li_type1.clear();
				li_type2.clear();
				pri.clear();

				row_db.append( "\r\n\r\n" );
				
				row_db.append( "-- 필드정보들\r\n" );
				st1 = con.prepareStatement(String.format("SELECT * FROM %s", table));
				rs1 = st1.executeQuery();
				while(rs1.next()){
					StringBuffer db = new StringBuffer();
					db.append("INSERT INTO `");
					db.append(table);
					db.append("` VALUES (");
					for(int i=1 ; i<=size1 ; ++i){
						db.append("'");
						try {
							if(rs1.getString(i) != null)
								db.append( rs1.getString(i).replaceAll("'", "\\\\'").trim() );
						} catch (Exception e) {
							if(e.toString().indexOf("0000-00-00 00:00:00")>0)
								db.append( "0000-00-00 00:00:00" );
						}
						db.append("'");
						if(i<size1){
							db.append(", ");
						}
					}
					db.append(");");

					li_type1.add( db.toString() );
				}
				rs1.close();
				st1.close();

				for(String p : li_type1) {
					row_db.append( p );
					row_db.append("\r\n");
				}
				li_type1.clear();
				
				// 2차 기록.
				Log.write(file_name, row_db.toString());
			}
			
		} catch (Exception e) {
			System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		System.println("백업이 완료되었습니다.");
	}

	@Override
	public void close() {
		//
	}

}
