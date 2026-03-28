package lineage.world.controller;

import java.util.Calendar;
import java.util.Date;

import lineage.bean.database.TeamBattleTime;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BlueMessage;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.world.World;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class WorldBossController {
	static private Calendar calendar;
	public static boolean isOpen;
	public static boolean isWait;
	public static long worldEndTime;

	static public String bossMonsterName() {
		return GmEventSettings.getMonsterName(GmEventSettings.WORLDBOSS, "월드보스");
	}
	
	static public void init() {
		TimeLine.start("월드보스컨트롤러..");
		
		calendar = Calendar.getInstance();
		isOpen = false;
		isWait = false;
		worldEndTime = 0L;
		
		TimeLine.end();
	}
	
	static public void toTimer(long time) {
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int hour = date.getHours();
		int min = date.getMinutes();
		int sec = date.getSeconds();
		boolean en = GmEventSettings.isEnabled(GmEventSettings.WORLDBOSS);
		int playSec = GmEventSettings.getPlayTimeSeconds(GmEventSettings.WORLDBOSS, Lineage.world_play_time);
		String bossName = bossMonsterName();
		
		for (TeamBattleTime tebeTime : Lineage.world_dungeon_time_list) {
			
			int test = tebeTime.getMin() - 1;
			
			if (en && !isOpen && tebeTime.getHour() == hour && test == min && sec == 0) {
				for (MonsterInstance boss: BossController.getBossList()){
					if(boss.getMonster().getName().equalsIgnoreCase(bossName)){
						boss.toAiThreadDelete();
						World.removeMonster(boss);
						World.remove(boss);
						BossController.toWorldOut(boss);
					}
				}
				isWait = true;
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class),  String.format("\\fU월드보스 레이드가 1분뒤 시작 합니다 마을npc를 통하여 입장 해주세요")));
			}
			
			if (en && !isOpen && tebeTime.getHour() == hour && test == min && sec == 30) {
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("\\fU 월드보스 레이드가 30초뒤 시작 합니다  마을npc를 통하여 입장 해주세요")));
			}
			if (en && !isOpen && tebeTime.getHour() == hour && tebeTime.getMin() == min && sec == 0) {
				isOpen = true;
				worldEndTime = time + (1000L * playSec);
				MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find(bossName));
				if (mi != null) {
					mi.setHomeX( 32877);
					mi.setHomeY( 32817 );
					mi.setHomeMap(1400);
					mi.setBoss(true);
					AiThread.append(mi);
					BossController.appendBossList(mi);
					mi.toTeleport(mi.getHomeX(), mi.getHomeY(), mi.getHomeMap(), false);
				}
				sendMessage();
			}
		}
		if (isOpen && worldEndTime > 0 && worldEndTime < time) {
			isOpen = false;
			isWait = false;
			sendMessage();
			
			for (MonsterInstance boss: BossController.getBossList()){
				if(boss.getMonster().getName().equalsIgnoreCase(bossName)){
					boss.toAiThreadDelete();
					World.removeMonster(boss);
					World.remove(boss);
					BossController.toWorldOut(boss);
				}
			}
		}
	}
	
	static public void sendMessage() {
		if (!GmEventSettings.isEnabled(GmEventSettings.WORLDBOSS))
			return;
		if (isOpen) {
			String msg = "\\fY      ***** 월드보스 토벌이 시작 되었습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		} else {

			String msg = "\\fY      ***** 월드보스가 종료 되었습니다.*****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		}
	}
}
