package lineage;

import java.sql.Connection;

import org.apache.log4j.PropertyConfigurator;

import lineage.database.BackgroundDatabase;
import lineage.database.BadIpDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.DeadLostItemDatabase;
import lineage.database.DefiniteDatabase;
import lineage.database.DungeonDatabase;
import lineage.database.DungeontellbookDatabase;
import lineage.database.EnchantLostItemDatabase;
import lineage.database.ExpDatabase;
import lineage.database.FishItemListDatabase;
import lineage.database.GmCommandDatabase;
import lineage.database.GmTeleportDatabase;
import lineage.database.HackNoCheckDatabase;
import lineage.database.ItemBundleDatabase;
import lineage.database.ItemChanceBundleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.database.ItemMaplewandDatabase;
import lineage.database.ItemPinewandDatabase;
import lineage.database.ItemSetoptionDatabase;
import lineage.database.ItemSkillDatabase;
import lineage.database.ItemTeleportDatabase;
import lineage.database.LifeLostItemDatabase;
import lineage.database.MagicdollListDatabase;
import lineage.database.MonsterBossSpawnlistDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterDropDatabase;
import lineage.database.MonsterSkillDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.NpcDatabase;
import lineage.database.NpcShopDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.NpcTeleportDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.ServerDownBossListDatabase;
import lineage.database.ServerNoticeDatabase;
import lineage.database.ServerOpcodesDatabase;
import lineage.database.ServerReloadDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.database.SummonListDatabase;
import lineage.database.TeamBattleDatabase;
import lineage.database.TeleportHomeDatabase;
import lineage.database.TeleportResetDatabase;
import lineage.database.TimeDungeonDatabase;
import lineage.database.WarehouseDatabase;
import lineage.gui.GuiMain;
import lineage.network.LineageServer;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.plugin.PluginController;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.Mysql;
import lineage.share.Socket;
import lineage.thread.AiExecuteThread;
import lineage.thread.AiThread;
import lineage.thread.AutoAttackThread;
import lineage.thread.AutoHuntThread;
import lineage.thread.CharacterControlThread;
import lineage.thread.CharacterThread;
import lineage.thread.ControllerTherad;
import lineage.thread.DatabaseThread;
import lineage.thread.EventThread;
import lineage.thread.GuiThread;
import lineage.powerball.PowerBallScheduler;
import lineage.thread.PowerballSettlementThread;
import lineage.thread.ServerThread;
import lineage.thread.TimeThread;
import lineage.util.PakTools;
import lineage.util.Shutdown;
import lineage.world.AStar;
import lineage.world.World;
import lineage.world.controller.AgitController;
import lineage.world.controller.AuctionController;
import lineage.world.controller.AutoHuntController;
import lineage.world.controller.TeamBattleController;
import lineage.world.controller.TimeEventController;
import lineage.world.controller.BoardController;
import lineage.world.controller.BookController;
import lineage.world.controller.BossController;
import lineage.world.controller.BuffController;
import lineage.world.controller.BugScanningController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.ColosseumController;
import lineage.world.controller.CraftController;
import lineage.world.controller.DamageController;
import lineage.world.controller.DogRaceController;
import lineage.world.controller.DungeonController;
import lineage.world.controller.ElvenforestController;
import lineage.world.controller.EventController;
import lineage.world.controller.FightController;
import lineage.world.controller.FishingController;
import lineage.world.controller.FriendController;
import lineage.world.controller.GiranClanLordController;
import lineage.world.controller.InnController;
import lineage.world.controller.InventoryController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.LetterController;
import lineage.world.controller.MagicDollController;
import lineage.world.controller.NoticeController;
import lineage.world.controller.PartyController;
import lineage.world.controller.PcMarketController;
import lineage.world.controller.QuestController;
import lineage.world.controller.RankController;
import lineage.world.controller.RobotController;
import lineage.world.controller.ScriptController;
import lineage.world.controller.ShipController;
import lineage.world.controller.ShopController;
import lineage.world.controller.SkillController;
import lineage.world.controller.SlimeRaceController;
import lineage.world.controller.SpotController;
import lineage.world.controller.SummonController;
import lineage.world.controller.TradeController;
import lineage.world.controller.UserShopController;
import lineage.world.controller.WantedController;
import lineage.world.controller.WorldClearController;
import lineage.world.controller.WeddingController;
import lineage.world.controller.RobotClanController;
import lineage.world.controller.DollRaceController2;
import lineage.world.controller.TreasureHuntController;
import lineage.world.controller.Thebes;
import lineage.world.controller.DevilController;
import lineage.world.controller.DimensionController;
import lineage.world.controller.IceDungeonController;
import lineage.world.controller.WorldBossController;
import lineage.world.controller.HellController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;
import all_night.Lineage_Balance;
import all_night.Npc_promotion;
import all_night.Plugins;

public final class Main implements Runnable {
	
	static public boolean running;
	
	static public final String SERVER_VERSION = "호두서버";
	
	/**
	 * 리니지 서버 시작
	 */
	static public void init() {
		if(running == true)
			return;
		
		if(Common.system_config_console == false)
			// gui모드 넣으면서 유연한 처리를위해 쓰레드를 따로빼서 처리함.
			// 이렇게 안하면 단일쓰레드에서 처리하다보니 이 과정이 끝나후 gui처리가 이뤄져서 순간 렉이 발생.
			new Thread(new Main()).start();
		else
			toLoading();
	}
	
	/**
	 * 리니지 서버 종료
	 */
	static public void close(){
		if(running == false)
			return;
		
		if(Common.system_config_console == false)
			// gui모드 넣으면서 유연한 처리를위해 쓰레드를 따로빼서 처리함.
			// 이렇게 안하면 단일쓰레드에서 처리하다보니 이 과정이 끝나여 gui처리가 이뤄져서 순간 렉이 발생.
			new Thread(new Main()).start();
		else
			toDelete();
	}
	
	@Override
	public void run(){
		if(running){
			toDelete();
		}else{
			toLoading();
		}
	}
	
	/**
	 * 서버 로딩 처리.
	 */
	static private void toLoading() {	
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir") + "/log4j.conf");
			
			if(Common.system_config_console)
				Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
			
			// 필요한 변수 및 클레스 초기화.
			Log.init();
			GuiThread.init();
			Mysql.init();
			Lineage.init(false);
			Lineage_Balance.init();
			Npc_promotion.init();
			Socket.init();
			Common.init();
			EventThread.init();
			AiExecuteThread.init();
			AiThread.init();
			World.init();
			AStar.init();
			BookController.init();
			InventoryController.init();
			BasePacketPooling.init();
			SkillController.init();
			DamageController.init();
			CharacterController.init();
			TradeController.init();
			DungeonController.init();
			ShipController.init();
			BuffController.init();
			SummonController.init();
			PartyController.init();
			InnController.init();
			BoardController.init();
			LetterController.init();
			UserShopController.init();
			ChattingController.init();
			ColosseumController.init();
			CraftController.init();
			BossController.init();
			NoticeController.init();
			FriendController.init();
			BugScanningController.init();
			MagicDollController.init();
			ShopController.init();
			WarehouseDatabase.init();
			PakTools.init();
			ScriptController.init();
			GiranClanLordController.init();
			TeamBattleController.init();
			WorldClearController.init();
			SpotController.init();
//			TebeController.init();
			HellController.init();
			TreasureHuntController.init();
			WorldBossController.init();
			IceDungeonController.init();
			TimeEventController.init();
			TreasureHuntController.init();
			DevilController.init();
			DimensionController.init();
			DollRaceController2.init();
		    AutoHuntController.init();
				
			// dbcp 활성화.
			DatabaseConnection.init();
			// 디비로부터 메모리 초기화.
			Connection con = DatabaseConnection.getLineage();
			ServerDatabase.init(con);
			SlimeRaceController.init(con);
			DogRaceController.init(con);
			ClanController.init(con);
			KingdomController.init(con);
			AgitController.init(con);
			AuctionController.init(con);
			ItemDatabase.init(con);
			ItemSetoptionDatabase.init(con);
			ItemSkillDatabase.init(con);
			ItemBundleDatabase.init(con);
			ItemChanceBundleDatabase.init(con);
			FishItemListDatabase.init(con);
			ItemTeleportDatabase.init(con);
			PolyDatabase.init(con);
			DungeonDatabase.init(con);
			DefiniteDatabase.init(con);
			ExpDatabase.init(con);
			BackgroundDatabase.init(con);
			SpriteFrameDatabase.init(con);
			SkillDatabase.init(con);
			NpcDatabase.init(con);
			NpcShopDatabase.init(con);
			NpcTeleportDatabase.init(con);
			NpcSpawnlistDatabase.init(con);
			EventController.init();
			MonsterDatabase.init(con);
			MonsterDropDatabase.init(con);
			MonsterSkillDatabase.init(con);
			MonsterSpawnlistDatabase.init(con);
			MonsterBossSpawnlistDatabase.init(con);
			ServerDownBossListDatabase.init(con);
			BadIpDatabase.init(con);
			QuestController.init(con);
			ServerOpcodesDatabase.init(con);
			ServerNoticeDatabase.init(con);
			RankController.init(con);
			WantedController.init(con);
			TeleportHomeDatabase.init(con);
			TeleportResetDatabase.init(con);
			ItemPinewandDatabase.init(con);
			ItemMaplewandDatabase.init(con);
			SummonListDatabase.init(con);
			GmCommandDatabase.init(con);
			MagicdollListDatabase.init(con);
			PcMarketController.init(con);
			ServerReloadDatabase.init(con);
			TimeDungeonDatabase.init(con);
			TeamBattleDatabase.init(con);
			LifeLostItemDatabase.init(con);
			GmTeleportDatabase.init(con);
			RobotClanController.init(con);
			HackNoCheckDatabase.init(con);
			EnchantLostItemDatabase.init(con);
			DeadLostItemDatabase.init(con);
			WeddingController.init(con);
			ItemDropMessageDatabase.init(con);
			DungeontellbookDatabase.init(con);
			DatabaseConnection.close(con);
			
			FishingController.init();
			
			// 성 스폰처리
			KingdomController.readKingdom();
			// 요정숲 관리 초기화. 디비값을 참고하기때문에 디비로딩후 처리해야함.
			ElvenforestController.init();
			// 로봇 처리. etc_objectid 때문에 여기에서 처리.
			RobotController.init();
			
			// 마지막으로 처리된 기타오브젝트값 기록.
			ServerDatabase.updateEtcObjId();
			
			// 소켓 활성화.
			LineageServer.init();

			// 필요한 쓰레드 활성화.
			AiThread.start();
			ServerThread.init();
			ControllerTherad.init();
			CharacterThread.init();
			DatabaseThread.init();
			CharacterControlThread.init();
			AutoAttackThread.init();
//			DogRaceController.start();
			FightController.init();
			TimeThread.init();
			PowerballSettlementThread.start();
			// 파워볼: 전광판 + 추첨 발표 NPC + 5분 주기 스케줄러 (스케줄러 run()에서 초기화)
			try {
				Thread powerBallThread = new Thread(new PowerBallScheduler());
				powerBallThread.setName("PowerBall-Scheduler");
				powerBallThread.setDaemon(true);
				powerBallThread.start();
			} catch (Exception e) {
				lineage.share.System.printf("[파워볼] 스케줄러 초기화 실패: %s\r\n", e.getMessage());
			}
			AutoHuntThread.init();
			Thebes.getInstance();

			// 서버 기본정보 표현.
			lineage.share.System.println("======================================================================");
			// Version 200
			lineage.share.System.printf("%s\r\n", SERVER_VERSION);
			// Version 200
			lineage.share.System.printf("Server Version  :  Lineage %.1f\r\n", Lineage.server_version*0.01);
			// Port 2000
			lineage.share.System.printf("Server Port      :  %d\r\n", Socket.PORT);
			// AutoAccount true
			lineage.share.System.printf("계정 자동생성  :  %s\r\n", Lineage.account_auto_create ? "on" : "off");
			// Rate enchant: drop: exp: aden: party:
			lineage.share.System.printf("인챈트 : %.1f배  ||  드랍 : %.1f배  ||  경험치 : %.1f배  ||  아데나 : %.1f배  ||  파티 : %.1f배\r\n", Lineage.rate_enchant, Lineage.rate_drop, Lineage.rate_exp, Lineage.rate_aden, Lineage.rate_party);

			lineage.share.System.println("======================================================================");
			
			lineage.share.System.printf( "'%s' 가동되었습니다.\r\n", ServerDatabase.getName() );

			running = true;
		} catch (Exception e) {
			lineage.share.System.printf("%s : toLoading()\r\n", Main.class.toString());
			lineage.share.System.println(e);
		}

		// 서버 시작 알리기.
		PluginController.init(Main.class, "toLoading");
	}
	
	/**
	 * 서버 종료처리 함수.
	 */
	static private void toDelete(){
		try {
			running = false;
			// 종료 알리기.
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "서버가 종료 됩니다."));
			Thread.sleep(1000);
			// 쓰레드 종료
			lineage.share.System.println("Thread close..");
			DatabaseThread.close();
			EventThread.close();
			AiExecuteThread.close();
			AutoHuntThread.close();
			ControllerTherad.close();
			ServerThread.close();
			AiThread.close();
			AutoAttackThread.close();
			CharacterControlThread.close();
			CharacterThread.close();
			TimeThread.close();
			PowerballSettlementThread.close();
			DogRaceController.close();
			Thread.sleep(1000);
			// 메모리 저장
			lineage.share.System.println("Save Database..");
			Connection con = DatabaseConnection.getLineage();
			ServerDatabase.close(con);
			ServerDownBossListDatabase.close(con);
			ClanController.close(con);
			KingdomController.close(con);
			AuctionController.close(con);
			AgitController.close(con);
			QuestController.close(con);
			BadIpDatabase.close(con);
			WeddingController.close(con);
			TradeController.close();
			save(con);
			FishingController.close(con);
			RobotClanController.save(con);
			// 메모리 제거.
			Thread.sleep(500);
			// 소켓 닫기
			lineage.share.System.println("Socket close..");
			LineageServer.close();
			lineage.share.System.println("ok..");
			// 로그 저장.
			Log.close();
			// 디비 컨넥션 닫기.
			DatabaseConnection.close(con);
			DatabaseConnection.close();
			Thread.sleep(500);
		} catch (Exception e) {
			lineage.share.System.println(Main.class+" : toDelete()");
			lineage.share.System.println(e);
		}
		
		// 서버 종료 알리기.
		PluginController.init(Main.class, "toDelete");
		
		// 콘솔모드에서 Runtime.getRuntime().addShutdownHook(Shutdown.getInstance()) 때문에 이 구간을 이행하면 재차 해당 함수가 호출됨.
		if(!Common.system_config_console)
			Runtime.getRuntime().exit(0);
	}
	
	/**
	 * 서버 종료시 저장.
	 * 2019-06-28
	 * by connector12@nate.com
	 */
	public static void save(Connection con) {
		for (PcInstance pc : World.getPcList()) {
			if (pc.isWorldDelete() || pc instanceof PcRobotInstance)
				continue;
			
			pc.setAutoSaveTime(false);
			// true 로 해놔야 나중에 클라 메모리 제거할때 사용자가 월드에 없다고 판단해서 저장 처리를 반복 하지 않음.
			pc.setWorldDelete(true);
			// 죽어있을경우에 처리를 위해.
			pc.toReset(true);
			// 저장
			pc.toSave(con);
		}
	}

	public static void main(String[] args) {	
		PluginController.setPlugin( new Plugins() );
		
		GuiMain.open();
		}
}
