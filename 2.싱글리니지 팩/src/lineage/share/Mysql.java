package lineage.share;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import lineage.bean.event.DatabaseBackup;
import lineage.thread.EventThread;

public final class Mysql {
	static public String driver;
	static public String url;
	static public boolean is_donation;
	static public String donation_url;	// 후원 자동지급용 url.
	static public String id;
	static public String pw;

	// 자동백업 활성화 할지 여부.
	static public boolean auto_backup;
	// 자동백업 할경우 그 주기값.
	static public int auto_backup_delay;
	// 자동저장 처리에 사용되는 임시 변수.
	static private long temp_time;
	// 백업 경로
	static public String auto_backup_path;
	
	/**
	 * mysql처리에 필요한 정보 초기화 처리하는 함수.
	 */
	static public void init(){
		TimeLine.start("Mysql..");
		
		try {
			File cfg = new File(java.lang.System.getProperty("user.dir"), "mysql.conf");
			BufferedReader lnrr = new BufferedReader(new InputStreamReader(new FileInputStream(cfg), StandardCharsets.UTF_8));
			String line;
			while ( (line = lnrr.readLine()) != null){
				if (!line.isEmpty() && line.charAt(0) == '\uFEFF')
					line = line.substring(1);
				if(line.startsWith("#"))
					continue;
				
				int pos = line.indexOf("=");
				if(pos>0){
					String key = line.substring(0, pos).trim();
					String value = line.substring(pos+1, line.length()).trim();
					
					if(key.equalsIgnoreCase("Driver"))
						driver = value;
					else if(key.equalsIgnoreCase("Url"))
						url = value;
					else if(key.equalsIgnoreCase("is_donation"))
						is_donation = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("donation_url"))
						donation_url = value;
					else if(key.equalsIgnoreCase("Id"))
						id = value;
					else if(key.equalsIgnoreCase("Pw"))
						pw = value;
					else if(key.equalsIgnoreCase("auto_backup"))
						auto_backup = value.equalsIgnoreCase("true");
					else if(key.equalsIgnoreCase("auto_backup_delay"))
						auto_backup_delay = Integer.valueOf(value) * 1000 * 60;
					else if(key.equalsIgnoreCase("auto_backup_path"))
						auto_backup_path = value;
				}
			}
			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Mysql.class.toString());
			lineage.share.System.println(e);
		}
		
		// 서버시작하자마자 바로 저장되는 문제 해결을 위해.
		temp_time = System.currentTimeMillis() + auto_backup_delay;
		
		TimeLine.end();
	}
	
	static public void toTimer(long time){
		if(!auto_backup)
			return;
		
		// 디비 자동저장 처리 구간.
		if(time >= temp_time){
			temp_time = time + auto_backup_delay;
			EventThread.append( DatabaseBackup.clone(EventThread.getPool(DatabaseBackup.class)) );
		}
	}
	
}
