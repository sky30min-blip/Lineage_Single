package lineage.share;

import java.util.ArrayList;
import java.util.List;

public final class Common {
	
	// 모든 쓰레드들의 휴식시간 ms
	static public final int THREAD_SLEEP		= 10;
	
	static public final int THREAD_SLEEP_30		= 30;
	static public final int THREAD_SLEEP_40		= 40;
	static public final int THREAD_SLEEP_50		= 50;
	static public final int THREAD_SLEEP_100	= 100;
	static public final int TIMER_SLEEP			= 1000;
	
	// 버퍼 사이즈
	static public final int BUFSIZE = 1024;
	// 문자열 사이즈
	static public final int STRSIZE = 20;
	
	// 문자열 인코딩 방식 지정
	// KSC5601, EUC-KR, UTF-8
	static public final String CHARSET = "EUC-KR";
	
	// 시스템 구동방식이 console 모드인지 gui 모드인지 확인하는 변수.
	static public boolean system_config_console = false;
	
	// 월드 접속시 표현될 메세지 정보.
	static public boolean SERVER_MESSAGE;				// 서버 메세지 표현할지 여부.
	static public List<String> SERVER_MESSAGE_LIST;		// 서버 메세지
	static public int SERVER_MESSAGE_TIME;				// 서버 메세지 주기적으로 표현할 시간값. 밀리세컨드 단위.
	// 서버다운할때 표현될 메세지.
	static public String SHUTDOWN_MESSAGE_FORMAT_HOUR;
	static public String SHUTDOWN_MESSAGE_FORMAT_MIN;
	static public String SHUTDOWN_MESSAGE_FORMAT_SEC;
	//
	static public List<String> BUFF_ROBOT_MENT;
	
	static public String OS_NAME;
	// 최대 가질수 있는 수량. 20억
	static public final long MAX_COUNT = 2000000000;
	// 1억
	static public final long ONE_HUNDRED_MILLION = 100000000;
	
	static public void init(){
		TimeLine.start("Common..");
		
		OS_NAME = java.lang.System.getProperty("os.name");
		SERVER_MESSAGE = true;
		SERVER_MESSAGE_TIME = 1000 * 600;
		SERVER_MESSAGE_LIST = new ArrayList<String>();
		SHUTDOWN_MESSAGE_FORMAT_HOUR = "%d시간 후 서버가 종료됩니다.";
		SHUTDOWN_MESSAGE_FORMAT_MIN = "%d분 후 서버가 종료됩니다.";
		SHUTDOWN_MESSAGE_FORMAT_SEC = "%d초 후 서버가 종료됩니다.";

		TimeLine.end();
	}
	
}
