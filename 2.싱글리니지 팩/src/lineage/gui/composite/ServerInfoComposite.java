package lineage.gui.composite;

import lineage.database.BackgroundDatabase;
import lineage.database.BadIpDatabase;
import lineage.database.DefiniteDatabase;
import lineage.database.DungeonDatabase;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemSetoptionDatabase;
import lineage.database.MonsterBossSpawnlistDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.NpcDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.ServerOpcodesDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.LineageServer;
import lineage.network.packet.BasePacketPooling;
import lineage.share.Log;
import lineage.thread.AiExecuteThread;
import lineage.thread.AiThread;
import lineage.thread.EventThread;
import lineage.util.Util;
import lineage.world.AStar;
import lineage.world.World;
import lineage.world.controller.BoardController;
import lineage.world.controller.BookController;
import lineage.world.controller.BuffController;
import lineage.world.controller.ClanController;
import lineage.world.controller.EventController;
import lineage.world.controller.FriendController;
import lineage.world.controller.InnController;
import lineage.world.controller.InventoryController;
import lineage.world.controller.PartyController;
import lineage.world.controller.RobotController;
import lineage.world.controller.SummonController;
import lineage.world.controller.TradeController;
import lineage.world.controller.UserShopController;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.swtdesigner.SWTResourceManager;

public class ServerInfoComposite extends Composite {

	private Label pc_objid;
	private Label item_objid;
	private Label temp_etc_objid;
	private Label inn_key;
	private Label etc_objid;
	private Label world_time;
	private Label map_count;
	private Label player_count;
	private Label object_count;
	private Label event_thread;
	private Label timer_thread;
	private Label ai_thread;
	private Label heap_size;
	private Label badip;
	private Label monster;
	private Label itemsetoption;
	private Label exp;
	private Label item;
	private Label skill;
	private Label definite;
	private Label poly;
	private Label serveropcodes;
	private Label monsterbossspawnlist;
	private Label npc;
	private Label spriteframe;
	private Label dungeon;
	private Label pool_useshop;
	private Label pool_iteminstance;
	private Label pool_object;
	private Label pool_summon;
	private Label pool_clan;
	private Label pool_monsterinstance;
	private Label pool_innkey;
	private Label pool_basepacket;
	private Label pool_trade;
	private Label pool_buff;
	private Label pool_inventory;
	private Label pool_buffinterface;
	private Label pool_party;
	private Label pool_exp;
	private Label pool_summoninstance;
	private Label pool_event;
	private Label pool_petinstance;
	private Label pool_client;
	private Label pool_book;
	private Label pool_board;
	private Label pool_backgroundinstance;
	private Label pool_friend;
	private Label pool_log;
	private Label pool_event_illusion;
	private Label pool_astar_node;
	private Label server_operating_time;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ServerInfoComposite(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(24, 24, 24));
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		Group grpServer = new Group(this, SWT.NONE);
		GridData gd_grpServer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_grpServer.widthHint = 642;
		grpServer.setLayoutData(gd_grpServer);
		grpServer.setText("server");
		GridLayout gl_grpServer = new GridLayout(8, false);
		gl_grpServer.verticalSpacing = 0;
		gl_grpServer.horizontalSpacing = 0;
		gl_grpServer.marginHeight = 0;
		gl_grpServer.marginWidth = 0;
		grpServer.setLayout(gl_grpServer);
		
		Label lblObjectId = new Label(grpServer, SWT.NONE);
		lblObjectId.setToolTipText("동적으로 활성화되는 객체들의 고유값이 존재하는데 그것을 모니터링하기 위한것.");
		lblObjectId.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblObjectId.setText("                - Object Id -");
		
		Composite composite = new Composite(grpServer, SWT.NONE);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_composite.widthHint = 30;
		gd_composite.heightHint = 10;
		composite.setLayoutData(gd_composite);
		
		Label lblPoolStatus = new Label(grpServer, SWT.NONE);
		lblPoolStatus.setToolTipText("쓰레드들의 간단한 정보 표현.");
		lblPoolStatus.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblPoolStatus.setText("            - Thread -");
		
		Composite composite_1 = new Composite(grpServer, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_1.heightHint = 10;
		gd_composite_1.widthHint = 30;
		composite_1.setLayoutData(gd_composite_1);
		
		Label lblWorld = new Label(grpServer, SWT.NONE);
		lblWorld.setToolTipText("현재 월드에 정보 표현.");
		lblWorld.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblWorld.setText("              - World -");
		
		Label lblPcobjid = new Label(grpServer, SWT.NONE);
		lblPcobjid.setToolTipText("현재까지 할당된 사용자객체의 고유 키값 입니다.\r\n");
		lblPcobjid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPcobjid.setText("pc_objid : ");
		
		pc_objid = new Label(grpServer, SWT.NONE);
		pc_objid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pc_objid.setText("1000000");
		new Label(grpServer, SWT.NONE);
		
		Label lblEventthread = new Label(grpServer, SWT.NONE);
		lblEventthread.setToolTipText("이벤트쓰레드 현황 입니다.\r\nlist run pool 방식으로 표현됩니다.\r\nlist : 처리해야할 목록 갯수.\r\nrun : 처리중인 목록 갯수.\r\npool : 재사용 갯수.");
		lblEventthread.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEventthread.setText("EventThread : ");
		
		event_thread = new Label(grpServer, SWT.NONE);
		event_thread.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		event_thread.setText("0-0-0");
		new Label(grpServer, SWT.NONE);
		
		Label lblWorldtime_1 = new Label(grpServer, SWT.NONE);
		lblWorldtime_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWorldtime_1.setToolTipText("현재 리니지월드에 시간입니다.");
		lblWorldtime_1.setText("world_time : ");
		
		world_time = new Label(grpServer, SWT.NONE);
		world_time.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		world_time.setText("00:00:00");
		
		Label lblItemobjid = new Label(grpServer, SWT.NONE);
		lblItemobjid.setToolTipText("현재까지 할당된 아이템 고유 키값 입니다.");
		lblItemobjid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblItemobjid.setText("item_objid : ");
		
		item_objid = new Label(grpServer, SWT.NONE);
		item_objid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		item_objid.setText("2000000");
		new Label(grpServer, SWT.NONE);
		
		Label lblTimerthread = new Label(grpServer, SWT.NONE);
		lblTimerthread.setToolTipText("타이머 쓰레드 구간 처리 시간이 표현됩니다.\r\n타이머에서는 서버내에서 동작하는 많은 시스템들을 일관처리 하는데.\r\n그 한 프레임처리 시간을 표현하고 있습니다.");
		lblTimerthread.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTimerthread.setText("TimerThread : ");
		
		timer_thread = new Label(grpServer, SWT.NONE);
		timer_thread.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		timer_thread.setText("0ms");
		new Label(grpServer, SWT.NONE);
		
		Label lblMapcount = new Label(grpServer, SWT.NONE);
		lblMapcount.setToolTipText("활성화된 맵에 갯수 입니다.");
		lblMapcount.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMapcount.setText("map_count : ");
		
		map_count = new Label(grpServer, SWT.NONE);
		map_count.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		map_count.setText("0");
		
		Label lblInnkey = new Label(grpServer, SWT.NONE);
		lblInnkey.setToolTipText("여관에 할당된 고유 키값 아이디 입니다.");
		lblInnkey.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInnkey.setText("inn_key : ");
		
		inn_key = new Label(grpServer, SWT.NONE);
		inn_key.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		inn_key.setText("1");
		new Label(grpServer, SWT.NONE);
		
		Label lblAithread = new Label(grpServer, SWT.NONE);
		lblAithread.setToolTipText("인공지능이 활성화된 객체들의 전체 갯수를 표현하고 있습니다.");
		lblAithread.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAithread.setText("AiThread : ");
		
		ai_thread = new Label(grpServer, SWT.NONE);
		ai_thread.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		ai_thread.setText("0");
		new Label(grpServer, SWT.NONE);
		
		Label lblPlayercount = new Label(grpServer, SWT.NONE);
		lblPlayercount.setToolTipText("플레이중인 사용자수 입니다.");
		lblPlayercount.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPlayercount.setText("player_count : ");
		
		player_count = new Label(grpServer, SWT.NONE);
		player_count.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		player_count.setText("0");
		
		Label lblEtcobjid = new Label(grpServer, SWT.NONE);
		lblEtcobjid.setToolTipText("배경이나 몬스터들이 사용하는 고유값.\r\n99999 가 최대치이며, 최대치를 오바할경우 이전에 기록한 최대값(temp_etc_objid)으로 되돌려서 순회하도록 되어있습니다.");
		lblEtcobjid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEtcobjid.setText("etc_objid : ");
		
		etc_objid = new Label(grpServer, SWT.NONE);
		etc_objid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		etc_objid.setText("1");
		new Label(grpServer, SWT.NONE);
		
		Label lblHeapSize = new Label(grpServer, SWT.NONE);
		lblHeapSize.setToolTipText("서버프로그램이 사용중인 힙사이즈 입니다.");
		lblHeapSize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHeapSize.setText("Memory Size : ");
		
		heap_size = new Label(grpServer, SWT.NONE);
		heap_size.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		heap_size.setText("0");
		new Label(grpServer, SWT.NONE);
		
		Label lblObjectcount = new Label(grpServer, SWT.NONE);
		lblObjectcount.setToolTipText("월드에 스폰된 객체들의 전체 갯수 입니다.");
		lblObjectcount.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblObjectcount.setText("object_count : ");
		
		object_count = new Label(grpServer, SWT.NONE);
		object_count.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		object_count.setText("0");
		
		Label lblTempetcobjid = new Label(grpServer, SWT.NONE);
		lblTempetcobjid.setToolTipText("서버 로딩중에 할당된 etc_objid의 최대값입니다.\r\netc_objid와 연동되여 동작합니다.");
		lblTempetcobjid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTempetcobjid.setText("temp_etc_objid : ");
		
		temp_etc_objid = new Label(grpServer, SWT.NONE);
		temp_etc_objid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		temp_etc_objid.setText("1");
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		new Label(grpServer, SWT.NONE);
		
		Composite composite_2 = new Composite(grpServer, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(11, false);
		gl_composite_2.verticalSpacing = 0;
		gl_composite_2.horizontalSpacing = 0;
		gl_composite_2.marginHeight = 0;
		gl_composite_2.marginWidth = 0;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));
		
		Label lblDatabase = new Label(composite_2, SWT.NONE);
		lblDatabase.setToolTipText("디비에 있는 데이터를 불러온 정보의 갯수 표현.");
		lblDatabase.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 5, 1));
		lblDatabase.setSize(67, 15);
		lblDatabase.setText("                               - Database -");
		
		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		GridData gd_composite_3 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_3.widthHint = 40;
		gd_composite_3.heightHint = 10;
		composite_3.setLayoutData(gd_composite_3);
		
		Label lblPool = new Label(composite_2, SWT.NONE);
		lblPool.setToolTipText("동적으로 할당된 객체들 재사용 가능 갯수.");
		lblPool.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 5, 1));
		lblPool.setText("                          - Pool -");
		
		Label lblBadip = new Label(composite_2, SWT.NONE);
		lblBadip.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBadip.setToolTipText("접근 차단된 아이피들의 로딩된 갯수.");
		lblBadip.setText("BadIp : ");
		
		badip = new Label(composite_2, SWT.NONE);
		GridData gd_badip = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_badip.widthHint = 50;
		badip.setLayoutData(gd_badip);
		badip.setText("0");
		
		Label lblMonster = new Label(composite_2, SWT.NONE);
		lblMonster.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMonster.setToolTipText("로딩된 몬스터들의 갯수.");
		lblMonster.setText("Monster : ");
		
		monster = new Label(composite_2, SWT.NONE);
		monster.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		monster.setText("0");
		new Label(composite_2, SWT.NONE);
		
		Label lblExp_1 = new Label(composite_2, SWT.NONE);
		lblExp_1.setToolTipText("경험치 처리에 사용되는 객체 갯수.");
		lblExp_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExp_1.setText("Exp : ");
		
		pool_exp = new Label(composite_2, SWT.NONE);
		GridData gd_pool_exp = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_pool_exp.widthHint = 50;
		pool_exp.setLayoutData(gd_pool_exp);
		pool_exp.setText("0");
		
		Label lblBoard = new Label(composite_2, SWT.NONE);
		lblBoard.setToolTipText("게시판 갯수.");
		lblBoard.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBoard.setText("Board : ");
		
		pool_board = new Label(composite_2, SWT.NONE);
		pool_board.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_board.setText("0");
		
		Label lblDefinite = new Label(composite_2, SWT.NONE);
		lblDefinite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDefinite.setToolTipText("확인주문서 사용시 표현되는 로딩된 메세지들의 갯수.");
		lblDefinite.setText("Definite : ");
		
		definite = new Label(composite_2, SWT.NONE);
		definite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		definite.setText("0");
		
		Label lblNpc = new Label(composite_2, SWT.NONE);
		lblNpc.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNpc.setToolTipText("로딩된 엔피시들의 갯수.");
		lblNpc.setText("Npc : ");
		
		npc = new Label(composite_2, SWT.NONE);
		npc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		npc.setText("0");
		new Label(composite_2, SWT.NONE);
		
		Label lblIteminstance = new Label(composite_2, SWT.NONE);
		lblIteminstance.setToolTipText("아이템 객체 갯수.");
		lblIteminstance.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIteminstance.setText("ItemInstance : ");
		
		pool_iteminstance = new Label(composite_2, SWT.NONE);
		pool_iteminstance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		pool_iteminstance.setText("0");
		
		Label lblBook = new Label(composite_2, SWT.NONE);
		lblBook.setToolTipText("기억 갯수.");
		lblBook.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBook.setText("Book : ");
		
		pool_book = new Label(composite_2, SWT.NONE);
		pool_book.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_book.setText("0");
		
		Label lblDungeon = new Label(composite_2, SWT.NONE);
		lblDungeon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDungeon.setToolTipText("로딩된 던전 좌표의 갯수.");
		lblDungeon.setText("Dungeon : ");
		
		dungeon = new Label(composite_2, SWT.NONE);
		dungeon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		dungeon.setText("0");
		
		Label lblPoly = new Label(composite_2, SWT.NONE);
		lblPoly.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPoly.setToolTipText("로딩된 변신목록 갯수.");
		lblPoly.setText("Poly : ");
		
		poly = new Label(composite_2, SWT.NONE);
		poly.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		poly.setText("0");
		new Label(composite_2, SWT.NONE);
		
		Label lblMonsterinstance = new Label(composite_2, SWT.NONE);
		lblMonsterinstance.setToolTipText("몬스터 객체 갯수.");
		lblMonsterinstance.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMonsterinstance.setText("MonsterInstance : ");
		
		pool_monsterinstance = new Label(composite_2, SWT.NONE);
		pool_monsterinstance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		pool_monsterinstance.setText("0");
		
		Label lblBuff = new Label(composite_2, SWT.NONE);
		lblBuff.setToolTipText("버프 갯수.");
		lblBuff.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBuff.setText("Buff : ");
		
		pool_buff = new Label(composite_2, SWT.NONE);
		pool_buff.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_buff.setText("0");
		
		Label lblExp = new Label(composite_2, SWT.NONE);
		lblExp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExp.setToolTipText("로딩된 경험치 갯수.");
		lblExp.setText("Exp : ");
		
		exp = new Label(composite_2, SWT.NONE);
		exp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		exp.setText("0");
		
		Label lblServeropcodes = new Label(composite_2, SWT.NONE);
		lblServeropcodes.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServeropcodes.setToolTipText("로딩된 옵코드목록 갯수");
		lblServeropcodes.setText("ServerOpcodes : ");
		
		serveropcodes = new Label(composite_2, SWT.NONE);
		serveropcodes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		serveropcodes.setText("0");
		new Label(composite_2, SWT.NONE);
		
		Label lblObject = new Label(composite_2, SWT.NONE);
		lblObject.setToolTipText("엔피씨나 배경에 사용되는 객체, 문, 횟불 등등.. 갯수.");
		lblObject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblObject.setText("Object : ");
		
		pool_object = new Label(composite_2, SWT.NONE);
		pool_object.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		pool_object.setText("0");
		
		Label lblBuffinterface = new Label(composite_2, SWT.NONE);
		lblBuffinterface.setToolTipText("버프 갯수.");
		lblBuffinterface.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBuffinterface.setText("BuffInterface : ");
		
		pool_buffinterface = new Label(composite_2, SWT.NONE);
		pool_buffinterface.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_buffinterface.setText("0");
		
		Label lblItem = new Label(composite_2, SWT.NONE);
		lblItem.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblItem.setToolTipText("로딩된 아이템 갯수.");
		lblItem.setText("Item : ");
		
		item = new Label(composite_2, SWT.NONE);
		item.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		item.setText("0");
		
		Label lblSkill = new Label(composite_2, SWT.NONE);
		lblSkill.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSkill.setToolTipText("로딩된 스킬들의 갯수.");
		lblSkill.setText("Skill : ");
		
		skill = new Label(composite_2, SWT.NONE);
		skill.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		skill.setText("0");
		new Label(composite_2, SWT.NONE);
		
		Label lblBasepacket = new Label(composite_2, SWT.NONE);
		lblBasepacket.setToolTipText("패킷 구성에 사용되는 객체 갯수.");
		lblBasepacket.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBasepacket.setText("BasePacket : ");
		
		pool_basepacket = new Label(composite_2, SWT.NONE);
		pool_basepacket.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		pool_basepacket.setText("0");
		
		Label lblClan = new Label(composite_2, SWT.NONE);
		lblClan.setToolTipText("혈맹 갯수.");
		lblClan.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClan.setText("Clan : ");
		
		pool_clan = new Label(composite_2, SWT.NONE);
		pool_clan.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_clan.setText("0");
		
		Label lblItemsetopion = new Label(composite_2, SWT.NONE);
		lblItemsetopion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblItemsetopion.setToolTipText("로딩된 셋트아이템에 갯수");
		lblItemsetopion.setText("ItemSetoption : ");
		
		itemsetoption = new Label(composite_2, SWT.NONE);
		itemsetoption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		itemsetoption.setText("0");
		
		Label lblSpriteframe = new Label(composite_2, SWT.NONE);
		lblSpriteframe.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSpriteframe.setToolTipText("로딩된 프레임들의 갯수.");
		lblSpriteframe.setText("SpriteFrame : ");
		
		spriteframe = new Label(composite_2, SWT.NONE);
		spriteframe.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spriteframe.setText("0");
		new Label(composite_2, SWT.NONE);
		
		Label lblClient = new Label(composite_2, SWT.NONE);
		lblClient.setToolTipText("클라이언트 갯수.");
		lblClient.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClient.setText("Client : ");
		
		pool_client = new Label(composite_2, SWT.NONE);
		pool_client.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		pool_client.setText("0");
		
		Label lblInnkey_1 = new Label(composite_2, SWT.NONE);
		lblInnkey_1.setToolTipText("여관키 갯수.");
		lblInnkey_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInnkey_1.setText("InnKey : ");
		
		pool_innkey = new Label(composite_2, SWT.NONE);
		pool_innkey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_innkey.setText("0");
		
		Label lblMonsterbossspawnlist = new Label(composite_2, SWT.NONE);
		lblMonsterbossspawnlist.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMonsterbossspawnlist.setToolTipText("로딩된 보스몬스터 스폰 갯수.");
		lblMonsterbossspawnlist.setText("MonsterBossSpawnlist : ");
		
		monsterbossspawnlist = new Label(composite_2, SWT.NONE);
		monsterbossspawnlist.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		monsterbossspawnlist.setText("0");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblEvent = new Label(composite_2, SWT.NONE);
		lblEvent.setToolTipText("이벤트 갯수.");
		lblEvent.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEvent.setText("Event : ");
		
		pool_event = new Label(composite_2, SWT.NONE);
		pool_event.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		pool_event.setText("0");
		
		Label lblInventory = new Label(composite_2, SWT.NONE);
		lblInventory.setToolTipText("인벤토리 갯수.");
		lblInventory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInventory.setText("Inventory : ");
		
		pool_inventory = new Label(composite_2, SWT.NONE);
		pool_inventory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_inventory.setText("0");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblLog = new Label(composite_2, SWT.NONE);
		lblLog.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLog.setText("Log : ");
		
		pool_log = new Label(composite_2, SWT.NONE);
		pool_log.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		pool_log.setText("0");
		
		Label lblParty = new Label(composite_2, SWT.NONE);
		lblParty.setToolTipText("파티 갯수.");
		lblParty.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblParty.setText("Party : ");
		
		pool_party = new Label(composite_2, SWT.NONE);
		pool_party.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_party.setText("0");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblSummon = new Label(composite_2, SWT.NONE);
		lblSummon.setToolTipText("소환객체 갯수.");
		lblSummon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSummon.setText("Summon : ");
		
		pool_summon = new Label(composite_2, SWT.NONE);
		pool_summon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_summon.setText("0");
		new Label(composite_2, SWT.NONE);
		
		Label lblSummoninstance = new Label(composite_2, SWT.NONE);
		lblSummoninstance.setToolTipText("길들인 객체 갯수.");
		lblSummoninstance.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSummoninstance.setText("SummonInstance : ");
		
		pool_summoninstance = new Label(composite_2, SWT.NONE);
		pool_summoninstance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_summoninstance.setText("0");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblPetinstance = new Label(composite_2, SWT.NONE);
		lblPetinstance.setToolTipText("길들인 객체 갯수.");
		lblPetinstance.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPetinstance.setText("PetInstance : ");
		
		pool_petinstance = new Label(composite_2, SWT.NONE);
		pool_petinstance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_petinstance.setText("0");
		new Label(composite_2, SWT.NONE);
		
		Label lblTrade = new Label(composite_2, SWT.NONE);
		lblTrade.setToolTipText("거래 갯수.");
		lblTrade.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTrade.setText("Trade : ");
		
		pool_trade = new Label(composite_2, SWT.NONE);
		pool_trade.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_trade.setText("0");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblUseshop = new Label(composite_2, SWT.NONE);
		lblUseshop.setToolTipText("개인상점 갯수.");
		lblUseshop.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUseshop.setText("Useshop : ");
		
		pool_useshop = new Label(composite_2, SWT.NONE);
		pool_useshop.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_useshop.setText("0");
		new Label(composite_2, SWT.NONE);
		
		Label lblBackgroundinstance = new Label(composite_2, SWT.NONE);
		lblBackgroundinstance.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBackgroundinstance.setText("BackgroundInstance : ");
		
		pool_backgroundinstance = new Label(composite_2, SWT.NONE);
		pool_backgroundinstance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_backgroundinstance.setText("0");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblFriend = new Label(composite_2, SWT.NONE);
		lblFriend.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFriend.setText("Friend : ");
		
		pool_friend = new Label(composite_2, SWT.NONE);
		pool_friend.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_friend.setText("0");
		new Label(composite_2, SWT.NONE);
		
		Label lblAstarNode = new Label(composite_2, SWT.NONE);
		lblAstarNode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAstarNode.setText("AstarNode : ");
		
		pool_astar_node = new Label(composite_2, SWT.NONE);
		pool_astar_node.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		pool_astar_node.setText("0");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblEventIllusion = new Label(composite_2, SWT.NONE);
		lblEventIllusion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEventIllusion.setText("event_illusion : ");
		
		pool_event_illusion = new Label(composite_2, SWT.NONE);
		pool_event_illusion.setText("0");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblNewLabel = new Label(composite_2, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 5, 1));
		lblNewLabel.setText("- Etc -");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		Label lblNewLabel_1 = new Label(composite_2, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("서버동작 시간 : ");
		
		server_operating_time = new Label(composite_2, SWT.NONE);
		server_operating_time.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		server_operating_time.setText("0day 00:00:00");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	/**
	 * 정보 갱신 처리 함수.
	 */
	public void toUpdate(){
		// Object Id
		pc_objid.setText( String.valueOf(ServerDatabase.getPc_objid()) );
		item_objid.setText( String.valueOf(ServerDatabase.getItem_objid()) );
		temp_etc_objid.setText( String.valueOf(ServerDatabase.getTemp_etc_objid()) );
		inn_key.setText( String.valueOf(ServerDatabase.getInn_objid()) );
		etc_objid.setText( String.valueOf(ServerDatabase.getEtc_objid()) );
		
		// Thread
		event_thread.setText( String.format("%d-%d-%d", EventThread.getListSize() + AiExecuteThread.getListSize(), EventThread.getRunSize() + AiExecuteThread.getRunSize(), EventThread.getPoolSize()) );
		//timer_thread.setText( String.format("%dms", TimerThread.getTimeLine()) );
		ai_thread.setText( String.valueOf(AiThread.getSize()) );
		heap_size.setText( String.format("%dMB", Util.getMemoryMB()) );
		
		// World
		world_time.setText( String.format("%02d:%02d:%02d", ServerDatabase.getLineageTimeHour(), ServerDatabase.getLineageTimeMinute(), ServerDatabase.getLineageTimeSeconds()) );
		map_count.setText( String.valueOf(World.getMapSize()) );
		player_count.setText( String.format("유저(%d) 로봇(%d)", World.getPcSize(), RobotController.getPcRobotListSize()) );
		object_count.setText( String.valueOf(World.getSize()) );
		
		// Database
		badip.setText( String.valueOf(BadIpDatabase.getSize()) );
		monster.setText( String.valueOf(MonsterDatabase.getSize()) );
		itemsetoption.setText( String.valueOf(ItemSetoptionDatabase.getSize()) );
		exp.setText( String.valueOf(ExpDatabase.getSize()) );
		item.setText( String.valueOf(ItemDatabase.getSize()) );
		skill.setText( String.valueOf(SkillDatabase.getSize()) );
		definite.setText( String.valueOf(DefiniteDatabase.getSize()) );
		poly.setText( String.valueOf(PolyDatabase.getSize()) );
		serveropcodes.setText( String.valueOf(ServerOpcodesDatabase.getSize()) );
		monsterbossspawnlist.setText( String.valueOf(MonsterBossSpawnlistDatabase.getSize()) );
		npc.setText( String.valueOf(NpcDatabase.getSize()) );
		spriteframe.setText( String.valueOf(SpriteFrameDatabase.getSize()) );
		dungeon.setText( String.valueOf(DungeonDatabase.getSize()) );
		
		// pool
		pool_useshop.setText( String.valueOf(UserShopController.getPoolSize()) );
		pool_iteminstance.setText( String.valueOf(ItemDatabase.getPoolSize()) );
		pool_object.setText( String.valueOf(NpcSpawnlistDatabase.getPoolSize()) );
		pool_summon.setText( String.valueOf(SummonController.getPoolSize()) );
		pool_clan.setText( String.valueOf(ClanController.getPoolSize()) );
		pool_monsterinstance.setText( String.valueOf(MonsterSpawnlistDatabase.getPoolSize()) );
		pool_innkey.setText( String.valueOf(InnController.getPoolSize()) );
		pool_basepacket.setText( String.valueOf(BasePacketPooling.getPoolSize()) );
		pool_trade.setText( String.valueOf(TradeController.getPoolSize()) );
		pool_buff.setText( String.valueOf(BuffController.getPoolBuffSize()) );
		pool_inventory.setText( String.valueOf(InventoryController.getPoolSize()) );
		pool_buffinterface.setText( String.valueOf(BuffController.getPoolSize()) );
		pool_party.setText( String.valueOf(PartyController.getPoolSize()) );
		pool_exp.setText( String.valueOf(ExpDatabase.getPoolSize()) );
		pool_summoninstance.setText( String.valueOf(SummonController.getSummonPoolSize()) );
		pool_event.setText( String.valueOf(EventThread.getPoolSize()) );
		pool_petinstance.setText( String.valueOf(SummonController.getPetPoolSize()) );
		pool_client.setText( String.valueOf(LineageServer.getPoolSize()) );
		pool_book.setText( String.valueOf(BookController.getPoolSize()) );
		pool_board.setText( String.valueOf(BoardController.getPoolSize()) );
		pool_backgroundinstance.setText( String.valueOf(BackgroundDatabase.getPoolSize()) );
		pool_friend.setText( String.valueOf(FriendController.getPoolSize()) );
		pool_log.setText( String.valueOf(Log.getPoolSize()) );
		pool_event_illusion.setText( String.valueOf(EventController.getIllusionItemSize()) );
		pool_astar_node.setText( String.valueOf(AStar.getPoolSize()) );
		server_operating_time.setText( ServerDatabase.toOperatingTime() );
	}
	
}
