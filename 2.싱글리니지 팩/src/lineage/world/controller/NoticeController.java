package lineage.world.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lineage.bean.lineage.Kingdom;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;

public class NoticeController {

	/** notice.txt 한 줄: 분단위 간격 + 메시지 (gm_tool 과 동일: 분|메시지) */
	private static class NoticeEntry {
		long delayMs;
		String message;
		long lastSent;
	}

	static private long kingdom_war_notice_time;
	static private List<NoticeEntry> noticeEntries = new ArrayList<NoticeEntry>();

	static private void loadNoticeFile() {
		noticeEntries.clear();
		try {
			BufferedReader lnrr = new BufferedReader(new InputStreamReader(new FileInputStream("notice.txt"), StandardCharsets.UTF_8));
			String line;
			long now = System.currentTimeMillis();
			while ((line = lnrr.readLine()) != null) {
				if (!line.isEmpty() && line.charAt(0) == '\uFEFF')
					line = line.substring(1);
				String t = line.trim();
				if (t.isEmpty() || t.startsWith("#"))
					continue;

				long delayMs = Lineage.notice_delay > 0 ? Lineage.notice_delay : 60000L;
				String msg;
				int pipe = t.indexOf('|');
				if (pipe > 0) {
					try {
						int min = Integer.parseInt(t.substring(0, pipe).trim());
						if (min < 1)
							min = 1;
						delayMs = min * 60L * 1000L;
						msg = t.substring(pipe + 1).trim();
					} catch (NumberFormatException e) {
						msg = t;
					}
				} else {
					msg = t;
				}
				if (msg.isEmpty())
					continue;

				NoticeEntry e = new NoticeEntry();
				e.delayMs = delayMs;
				e.message = msg;
				e.lastSent = now;
				noticeEntries.add(e);
			}
			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : notice.txt 로드 오류\r\n", NoticeController.class.toString());
			lineage.share.System.println(e);
		}
	}

	static public void init() {
		TimeLine.start("NoticeController..");
		loadNoticeFile();
		TimeLine.end();
	}

	static public void reload() {
		TimeLine.start("notice.txt 파일 리로드 완료 - ");
		loadNoticeFile();
		TimeLine.end();
	}

	static public void toTimer(long time) {
		boolean sentAny = false;
		for (NoticeEntry e : noticeEntries) {
			if (time - e.lastSent >= e.delayMs) {
				e.lastSent = time;
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), e.message));
				sentAny = true;
			}
		}
		if (sentAny && Lineage.open_wait) {
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("테스트 서버는 현재 오픈 대기중입니다.")));
		}

		if ((time - kingdom_war_notice_time >= Lineage.kingdom_war_notice_delay) && Lineage.is_kingdom_war && Lineage.is_kingdom_war_notice) {
			kingdom_war_notice_time = time;

			// 공성전 시간 알림
			kingdomWarNoticeNew(time);
		}
	}

	public static void kingdomWarNoticeOld(long time) {
		try {
			Kingdom k = KingdomController.find(4);

			if (k == null)
				return;

			Calendar nowCalendar = Calendar.getInstance();
			Calendar kingdomWarCalendar = Calendar.getInstance();

			nowCalendar.setTimeInMillis(time);
			kingdomWarCalendar.setTimeInMillis(k.getWarDay());

			Date nowDate = nowCalendar.getTime();
			Date kingdomWarDate = kingdomWarCalendar.getTime();

			nowDate.setTime(time);
			kingdomWarDate.setTime(k.getWarDay());

			if (nowDate.getTime() < kingdomWarDate.getTime()) {

				if (nowDate.getMonth() >= kingdomWarDate.getMonth() && nowDate.getDate() > kingdomWarDate.getDate())
					return;

				if (nowDate.getMonth() == kingdomWarDate.getMonth() && nowDate.getDate() == kingdomWarDate.getDate()
						&& (nowDate.getHours() > kingdomWarDate.getHours() || kingdomWarDate.getTime() - nowDate.getTime() < Lineage.kingdom_war_notice_delay))
					return;

				String day = "";

				switch (kingdomWarDate.getDay()) {
				case 0:
					day = "일";
					break;
				case 1:
					day = "월";
					break;
				case 2:
					day = "화";
					break;
				case 3:
					day = "수";
					break;
				case 4:
					day = "목";
					break;
				case 5:
					day = "금";
					break;
				case 6:
					day = "토";
					break;
				}

				if (nowDate.getMonth() == kingdomWarDate.getMonth() && nowDate.getDate() == kingdomWarDate.getDate()) {
					if (kingdomWarDate.getHours() < 12)
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("기란 공성전이 오늘 오전 %d시 %d분에 진행됩니다.",
								kingdomWarDate.getHours() == 0 ? 12 : kingdomWarDate.getHours(), kingdomWarDate.getMinutes())));
					else
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("기란 공성전이 오늘 오후 %d시 %d분에 진행됩니다.",
								kingdomWarDate.getHours() - 12 == 0 ? 12 : kingdomWarDate.getHours() - 12, kingdomWarDate.getMinutes())));
				} else {
					if (kingdomWarDate.getHours() < 12)
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("기란 공성전이 %d월 %d일(%s) 오전 %d시 %d분에 진행됩니다.",
								kingdomWarDate.getMonth() + 1, kingdomWarDate.getDate(), day,
								kingdomWarDate.getHours() == 0 ? 12 : kingdomWarDate.getHours(), kingdomWarDate.getMinutes())));
					else
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("기란 공성전이 %d월 %d일(%s) 오후 %d시 %d분에 진행됩니다.",
								kingdomWarDate.getMonth() + 1, kingdomWarDate.getDate(), day,
								kingdomWarDate.getHours() == 0 ? 12 : kingdomWarDate.getHours(), kingdomWarDate.getMinutes())));
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : 기란 공성 시간 공지 오류\r\n", NoticeController.class.toString());
			lineage.share.System.println(e);
		}
	}

	public static void kingdomWarNoticeNew(long time) {
		try {
			Kingdom k = KingdomController.find(4);

			if (k == null)
				return;

			Calendar nowCalendar = Calendar.getInstance();
			nowCalendar.setTimeInMillis(time);
			Date nowDate = nowCalendar.getTime();
			nowDate.setTime(time);

			StringBuffer sb = new StringBuffer();
			int idx = 0;
			boolean isWar = false;
			boolean toDay = false;
			for (Integer d : Lineage.getGiranKingdomWarDayList()) {
				int day = (int) d;

				if (nowDate.getDay() == day)
					toDay = true;

				if (idx > 0)
					sb.append(", ");

				switch (day) {
				case 0:
					sb.append("일");
					break;
				case 1:
					sb.append("월");
					break;
				case 2:
					sb.append("화");
					break;
				case 3:
					sb.append("수");
					break;
				case 4:
					sb.append("목");
					break;
				case 5:
					sb.append("금");
					break;
				case 6:
					sb.append("토");
					break;
				}
				idx++;
				isWar = true;
			}

			if (isWar) {
				if (toDay) {
					if (Lineage.giran_kingdom_war_hour < 12)
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("기란 공성전이 오늘 오전 %d시 %s에 진행됩니다.",
								Lineage.giran_kingdom_war_hour == 0 ? 12 : Lineage.giran_kingdom_war_hour, Lineage.giran_kingdom_war_min == 0 ? "정각" : String.format("%d분", Lineage.giran_kingdom_war_min))));
					else
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("기란 공성전이 오늘 오후 %d시 %s에 진행됩니다.",
								Lineage.giran_kingdom_war_hour - 12 == 0 ? 12 : Lineage.giran_kingdom_war_hour - 12, Lineage.giran_kingdom_war_min == 0 ? "정각" : String.format("%d분", Lineage.giran_kingdom_war_min))));
				} else {
					if (Lineage.giran_kingdom_war_hour < 12)
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("기란 공성전은 (%s요일) 오전 %d시 %s에 진행됩니다.", sb.toString(),
								Lineage.giran_kingdom_war_hour == 0 ? 12 : Lineage.giran_kingdom_war_hour, Lineage.giran_kingdom_war_min == 0 ? "정각" : String.format("%d분", Lineage.giran_kingdom_war_min))));
					else
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("기란 공성전은 (%s요일) 오후 %d시 %s에 진행됩니다.", sb.toString(),
								Lineage.giran_kingdom_war_hour - 12 == 0 ? 12 : Lineage.giran_kingdom_war_hour - 12, Lineage.giran_kingdom_war_min == 0 ? "정각" : String.format("%d분", Lineage.giran_kingdom_war_min))));
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : 기란 공성 시간 공지 오류\r\n", NoticeController.class.toString());
			lineage.share.System.println(e);
		}
	}
}
