package lineage.util;

import lineage.Main;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Common;
import lineage.world.World;

public final class Shutdown extends Thread {
	static private Shutdown _instance;
	// 셧다운 처리전 딜레이할 시간값. 초단위
	static public int shutdown_delay;
	// 셧다운 여부.
	public boolean is_shutdown = true;
	
	public Shutdown(int time) {
		shutdown_delay = time;
		is_shutdown = true;
	}
	
	static public Shutdown getInstance() {
		if (_instance == null)
			return null;
		
		return _instance;		
	}

	static public Shutdown getInstance(int time) {
		if (_instance == null) {
			_instance = new Shutdown(time);
		} else {
			shutdown_delay = time;
			_instance.is_shutdown = true;
		}

		return _instance;
	}

	@Override
	public void run() {		
		try {
			String msg = null;

			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "잠시 후 서버가 종료됩니다."));
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "안전한 곳에서 종료하시기 바랍니다."));

			for (int i = shutdown_delay; i > 0; --i) {
				if (!is_shutdown) {
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "서버 종료가 취소되었습니다."));
					lineage.share.System.println("서버 종료가 취소되었습니다.");
					break;
				}

				// 1시간 단위로 메세지.
				if (i >= 3600 && i % 3600 == 0) {
					msg = String.format(Common.SHUTDOWN_MESSAGE_FORMAT_HOUR, i / 3600);
					lineage.share.System.println(msg);
					// 월드 유저에게 서버 종료 알림.
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
				}

				// 1분 단위로 메세지.
				if (i < 3600 && i >= 60 && i % 60 == 0) {
					msg = String.format(Common.SHUTDOWN_MESSAGE_FORMAT_MIN, i / 60);
					lineage.share.System.println(msg);
					// 월드 유저에게 서버 종료 알림.
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
				}

				// 1초 단위로 메세지.
				if (i < 60) {
					msg = String.format(Common.SHUTDOWN_MESSAGE_FORMAT_SEC, i);
					lineage.share.System.println(msg);
					// 월드 유저에게 서버 종료 알림.
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
				}
				sleep(Common.TIMER_SLEEP);
			}
		} catch (Exception e) {

		}

		if (is_shutdown)
			Main.close();
	}

}
