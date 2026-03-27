package lineage.gui;

import lineage.Main;
import lineage.database.BackgroundDatabase;
import lineage.database.FishItemListDatabase;
import lineage.database.GmTeleportDatabase;
import lineage.database.HackNoCheckDatabase;
import lineage.database.ItemBundleDatabase;
import lineage.database.ItemChanceBundleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.database.ItemSkillDatabase;
import lineage.database.ItemTeleportDatabase;
import lineage.database.LifeLostItemDatabase;
import lineage.database.MonsterBossSpawnlistDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterDropDatabase;
import lineage.database.MonsterSkillDatabase;
import lineage.database.NpcDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.ServerNoticeDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.database.SummonListDatabase;
import lineage.database.TeamBattleDatabase;
import lineage.database.TimeDungeonDatabase;
import lineage.gui.composite.ConsoleComposite;
import lineage.gui.composite.ViewComposite;
import lineage.share.Lineage;
import lineage.share.Socket;
import lineage.util.Shutdown;
import lineage.world.World;
import lineage.world.controller.AutoHuntController;
import lineage.world.controller.CommandController;
import lineage.world.controller.EventController;
import lineage.world.controller.ExpMarbleController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.NoticeController;
import lineage.world.controller.RobotController;
import lineage.world.controller.RobotClanController;
import lineage.world.object.instance.PcInstance;
import system.Gui_System;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import com.swtdesigner.SWTResourceManager;
import all_night.Lineage_Balance;
import all_night.Npc_promotion;
import all_night.util.Monster_Drop_sql;
import all_night.util.Monster_spawnlist_sql;
import all_night.util.Spr_Action_sql;

public final class GuiMain {

	// gui 컴포넌트들.
	static public Display display;
	static public Shell shell;
	static private ViewComposite viewComposite;
	static private ConsoleComposite consoleComposite;
	static private MenuItem menu_system_1_item_1;		// 서버가동
	static private MenuItem menu_system_1_item_2;		// 서버종료
	static private MenuItem event;						// 이벤트
	static private MenuItem event_menu_1;				// 변신 이벤트
	static private MenuItem command;					// 명령어
	static private MenuItem reload;						// 리로드
	static private MenuItem menuItem_5;					// 자동버프 이벤트
	static private MenuItem menuItem_7;					// 환상 이벤트
	static private MenuItem menuItem_8;					// 크리스마스 이벤트
	static private MenuItem menuItem_9;					// 할로윈 이벤트
	static private MenuItem menuItem_10;				// 토템 이벤트
	//컴포트
	static private Label cpu;
	static private Label memory;
	static private Label thread;
	static private Label usercount;
	
	// 서버팩 버전 2023 1107
	static public final String SERVER_VERSION = " Ver 0.1";
	// 클라이언트 접속 최대치값.
	static public int CLIENT_MAX = 500;
	
	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	static public void open() {
		display = Display.getDefault();
		shell = new Shell();
		shell.setSize(950, 648);
		shell.setText( String.format("카오스 %s", SERVER_VERSION) );
		shell.setImage( SWTResourceManager.getImage("images/icon.ico") );
		GridLayout gl_shell = new GridLayout(2, false);
		gl_shell.verticalSpacing = 0;
		gl_shell.horizontalSpacing = 0;
		gl_shell.marginHeight = 0;
		gl_shell.marginWidth = 0;
		shell.setLayout(gl_shell);
		
		Composite composite = new Composite(shell, SWT.NONE);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite.widthHint = 930;
		gd_composite.heightHint = 36;
		composite.setLayoutData(gd_composite);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		composite.setBounds(0, 524, 784, 37);

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		lblNewLabel.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.BOLD));
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblNewLabel.setBounds(233, 8, 68, 19);
		lblNewLabel.setText("Memory: ");

		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_1.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.BOLD));
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblNewLabel_1.setBounds(96, 8, 38, 19);
		lblNewLabel_1.setText("CPU: ");

		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_2.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.BOLD));
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblNewLabel_2.setBounds(469, 8, 68, 17);
		lblNewLabel_2.setText("Thread: ");

		memory = new Label(composite, SWT.NONE);
		memory.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.BOLD));
		memory.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		memory.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		memory.setBounds(307, 8, 128, 19);
		memory.setText(String.format("%d", Gui_System.getUsedMemoryMB()) + "MB/"
				+ String.format("%d", Gui_System.getTotalMemoryMB()) + "MB");

		cpu = new Label(composite, SWT.NONE);
		cpu.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		cpu.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		cpu.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.BOLD));
		cpu.setBounds(140, 8, 56, 19);
		cpu.setText(String.format("%.0f%%", Gui_System.getUseCpu()));

		thread = new Label(composite, SWT.NONE);
		thread.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.BOLD));
		thread.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		thread.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		thread.setBounds(543, 8, 46, 19);
		thread.setText(String.format("%d", Gui_System.getThread()));

		Label lblNewLabel_6 = new Label(composite, SWT.NONE);
		lblNewLabel_6.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_6.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.BOLD));
		lblNewLabel_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblNewLabel_6.setBounds(629, 8, 75, 29);
		lblNewLabel_6.setText("접속자 수 : ");

		usercount = new Label(composite, SWT.NONE);
		usercount.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.BOLD));
		usercount.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		usercount.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		usercount.setBounds(723, 8, 76, 17);
		usercount.setText("0");
		new Label(shell, SWT.NONE);
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem menu_system = new MenuItem(menu, SWT.CASCADE);
		menu_system.setText("서버ON/OFF");
		
		Menu menu_system_1 = new Menu(menu_system);
		menu_system.setMenu(menu_system_1);
		
		menu_system_1_item_1 = new MenuItem(menu_system_1, SWT.NONE);
		menu_system_1_item_1.setText("서버 ON");
		
		menu_system_1_item_2 = new MenuItem(menu_system_1, SWT.CASCADE);
		menu_system_1_item_2.setText("서버 OFF");
		menu_system_1_item_2.setEnabled(false);
		
		Menu serverOffMenu = new Menu(menu_system_1_item_2);
		menu_system_1_item_2.setMenu(serverOffMenu);
		
		MenuItem serverOffMenu_1 = new MenuItem(serverOffMenu, SWT.CHECK);
		MenuItem serverOffMenu_2 = new MenuItem(serverOffMenu, SWT.CHECK);
		MenuItem serverOffMenu_3 = new MenuItem(serverOffMenu, SWT.CHECK);
		MenuItem serverOffMenu_4 = new MenuItem(serverOffMenu, SWT.CHECK);
		MenuItem serverOffMenu_5 = new MenuItem(serverOffMenu, SWT.CHECK);
		MenuItem serverOffMenu_6 = new MenuItem(serverOffMenu, SWT.CHECK);
		serverOffMenu_6.setEnabled(false);
		
		new MenuItem(menu_system_1, SWT.SEPARATOR);
		
		serverOffMenu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lineage.share.System.println("서버가 즉시 종료됩니다...");
				Main.close();
				serverOffMenu_1.setSelection(true);
				serverOffMenu_2.setSelection(false);
				serverOffMenu_3.setSelection(false);
				serverOffMenu_4.setSelection(false);
				serverOffMenu_5.setSelection(false);
				serverOffMenu_6.setSelection(false);
				menu_system_1_item_2.setEnabled(false);
				
				serverOffMenu_1.setEnabled(false);
				serverOffMenu_2.setEnabled(false);
				serverOffMenu_3.setEnabled(false);
				serverOffMenu_4.setEnabled(false);
				serverOffMenu_5.setEnabled(false);
				serverOffMenu_6.setEnabled(false);
			}
		});
		serverOffMenu_1.setText("1.   즉시 서버 종료");
				
		serverOffMenu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Shutdown.getInstance() != null)
					Shutdown.getInstance().is_shutdown = false;
				
				new Thread(Shutdown.getInstance(10)).start();
				serverOffMenu_1.setSelection(false);
				serverOffMenu_2.setSelection(true);
				serverOffMenu_3.setSelection(false);
				serverOffMenu_4.setSelection(false);
				serverOffMenu_5.setSelection(false);
				serverOffMenu_6.setSelection(false);
				
				serverOffMenu_6.setEnabled(true);
				
				serverOffMenu_1.setEnabled(false);
				serverOffMenu_2.setEnabled(false);
				serverOffMenu_3.setEnabled(false);
				serverOffMenu_4.setEnabled(false);
				serverOffMenu_5.setEnabled(false);
			}
		});
		serverOffMenu_2.setText("2.   10초 후 서버 종료");
		
		serverOffMenu_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Shutdown.getInstance() != null)
					Shutdown.getInstance().is_shutdown = false;
				
				new Thread(Shutdown.getInstance(60)).start();
				serverOffMenu_1.setSelection(false);
				serverOffMenu_2.setSelection(false);
				serverOffMenu_3.setSelection(true);
				serverOffMenu_4.setSelection(false);
				serverOffMenu_5.setSelection(false);
				serverOffMenu_6.setSelection(false);
				
				serverOffMenu_6.setEnabled(true);
				
				serverOffMenu_1.setEnabled(false);
				serverOffMenu_2.setEnabled(false);
				serverOffMenu_3.setEnabled(false);
				serverOffMenu_4.setEnabled(false);
				serverOffMenu_5.setEnabled(false);
			}
		});
		serverOffMenu_3.setText("3.   1분 후 서버 종료");	
		
		serverOffMenu_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Shutdown.getInstance() != null)
					Shutdown.getInstance().is_shutdown = false;
				
				new Thread(Shutdown.getInstance(60 * 5)).start();
				serverOffMenu_1.setSelection(false);
				serverOffMenu_2.setSelection(false);
				serverOffMenu_3.setSelection(false);
				serverOffMenu_4.setSelection(true);
				serverOffMenu_5.setSelection(false);
				serverOffMenu_6.setSelection(false);
				
				serverOffMenu_6.setEnabled(true);
				
				serverOffMenu_1.setEnabled(false);
				serverOffMenu_2.setEnabled(false);
				serverOffMenu_3.setEnabled(false);
				serverOffMenu_4.setEnabled(false);
				serverOffMenu_5.setEnabled(false);
			}
		});
		serverOffMenu_4.setText("4.   5분 후 서버 종료");	
		
		serverOffMenu_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Shutdown.getInstance() != null)
					Shutdown.getInstance().is_shutdown = false;
				
				new Thread(Shutdown.getInstance(60 * 10)).start();
				serverOffMenu_1.setSelection(false);
				serverOffMenu_2.setSelection(false);
				serverOffMenu_3.setSelection(false);
				serverOffMenu_4.setSelection(false);
				serverOffMenu_5.setSelection(true);
				serverOffMenu_6.setSelection(false);
				
				serverOffMenu_6.setEnabled(true);
				
				serverOffMenu_1.setEnabled(false);
				serverOffMenu_2.setEnabled(false);
				serverOffMenu_3.setEnabled(false);
				serverOffMenu_4.setEnabled(false);
				serverOffMenu_5.setEnabled(false);
			}
		});
		serverOffMenu_5.setText("5.   10분 후 서버 종료");
				
		serverOffMenu_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Shutdown.getInstance() != null)
					Shutdown.getInstance().is_shutdown = false;
				
				serverOffMenu_1.setSelection(false);
				serverOffMenu_2.setSelection(false);
				serverOffMenu_3.setSelection(false);
				serverOffMenu_4.setSelection(false);
				serverOffMenu_5.setSelection(false);
				serverOffMenu_6.setSelection(false);
				
				serverOffMenu_1.setEnabled(true);
				serverOffMenu_2.setEnabled(true);
				serverOffMenu_3.setEnabled(true);
				serverOffMenu_4.setEnabled(true);
				serverOffMenu_5.setEnabled(true);
				serverOffMenu_6.setEnabled(false);
			}
		});
		serverOffMenu_6.setText("6.   서버 종료 취소");
		
		MenuItem menuItem_6 = new MenuItem(menu_system_1, SWT.NONE);
		menuItem_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Tray tray = display.getSystemTray();
				if(tray != null){
					// 현재 윈도우 감추기.
					shell.setVisible(false);
					// 트레이 활성화.
					final TrayItem item = new TrayItem(tray, SWT.NONE);
					item.setToolTipText( String.format("%s : %d", SERVER_VERSION, Lineage.server_version) );
					item.setImage( SWTResourceManager.getImage("images/icon.ico") );
					// 이벤트 등록.
					item.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							item.dispose();
							shell.setVisible(true);
							shell.setFocus();
						}
					});
				}
			}
		});
		menuItem_6.setText("최소 창모드");
		
		MenuItem menu_lineage = new MenuItem(menu, SWT.CASCADE);
		menu_lineage.setText("명령어 | 이벤트 | 리로드");
		
		Menu commandAndEvent = new Menu(menu_lineage);
		menu_lineage.setMenu(commandAndEvent);
		
		command = new MenuItem(commandAndEvent, SWT.CASCADE);
		command.setEnabled(false);
		command.setText("명령어");
		
		Menu command_menu = new Menu(command);
		command.setMenu(command_menu);
		
		MenuItem command_menu_1 = new MenuItem(command_menu, SWT.CHECK);
		command_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.serverOpenWait();
				command_menu_1.setSelection(true);
			}
		});
		command_menu_1.setText("서버 오픈대기");
		
		MenuItem command_menu_2 = new MenuItem(command_menu, SWT.NONE);
		command_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.serverOpen();
				command_menu_1.setSelection(false);
			}
		});
		command_menu_2.setText("서버 오픈");
		
		MenuItem command_menu_3 = new MenuItem(command_menu, SWT.NONE);
		command_menu_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.toBuffAll(null);
			}
		});
		command_menu_3.setText("올버프");
		
		MenuItem command_menu_4 = new MenuItem(command_menu, SWT.NONE);
		command_menu_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.toWorldItemClear(null);
			}
		});
		command_menu_4.setText("월드맵 청소");
		
		MenuItem command_menu_5 = new MenuItem(command_menu, SWT.NONE);
		command_menu_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (PcInstance pc : World.getPcList())
					pc.toCharacterSave();
				
				lineage.share.System.println("캐릭터 정보 저장 완료");
			}
		});
		command_menu_5.setText("캐릭터 저장");
		
		MenuItem command_menu_6 = new MenuItem(command_menu, SWT.NONE);
		command_menu_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Spr_Action_sql.writeSql();
			}
		});
		command_menu_6.setText("spr_action.sql 생성");
		
		MenuItem command_menu_7 = new MenuItem(command_menu, SWT.NONE);
		command_menu_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Monster_Drop_sql.writeSql();
			}
		});
		command_menu_7.setText("monster_drop.sql 생성");
		
		MenuItem command_menu_8 = new MenuItem(command_menu, SWT.NONE);
		command_menu_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Monster_spawnlist_sql.writeSql();
			}
		});
		command_menu_8.setText("monster_spawnlist.sql 생성");
		
		MenuItem command_menu_9 = new MenuItem(command_menu, SWT.NONE);
		command_menu_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.setKingdomWar();
			}
		});
		command_menu_9.setText("공성전");
		
		MenuItem command_menu_10 = new MenuItem(command_menu, SWT.NONE);
		command_menu_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.toBanAllRemove(null);
			}
		});
		command_menu_10.setText("전체 벤 해제");
		
		MenuItem command_menu_11 = new MenuItem(command_menu, SWT.NONE);
		command_menu_11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadPcRobot(false);
			}
		});
		command_menu_11.setText("로봇 전체 사용");
		
		MenuItem command_menu_12 = new MenuItem(command_menu, SWT.NONE);
		command_menu_12.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadPcRobot(true);
			}
		});
		command_menu_12.setText("로봇 전체 사용 안함");
		
		//-- 추가
		MenuItem command_menu_13 = new MenuItem(command_menu, SWT.CHECK);
		command_menu_13.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		
			}
		});
		command_menu_13.setText("테스트");
		
		event = new MenuItem(commandAndEvent, SWT.CASCADE);
		event.setEnabled(false);
		event.setText("이벤트");
		
		Menu event_menu = new Menu(event);
		event.setMenu(event_menu);
		
		event_menu_1 = new MenuItem(event_menu, SWT.CHECK);
		event_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toPoly( event_menu_1.getSelection() );
			}
		});
		event_menu_1.setText("변신 이벤트");
		
		MenuItem event_menu_2 = new MenuItem(event_menu, SWT.CHECK);
		event_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toRankPoly( event_menu_2.getSelection() );
			}
		});
		event_menu_2.setText("랭킹 변신 이벤트");
		
		MenuItem event_menu_3 = new MenuItem(event_menu, SWT.NONE);
		event_menu_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TimeDungeonDatabase.resetGiranDungeonTime();
			}
		});
		event_menu_3.setText("기란감옥 이용시간 초기화");
		
		MenuItem event_menu_4 = new MenuItem(event_menu, SWT.NONE);
		event_menu_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TimeDungeonDatabase.resetGiranDungeonScrollCount();
			}
		});
		event_menu_4.setText("기란감옥 초기화 주문서 사용횟수 초기화");
		
		MenuItem event_menu_5 = new MenuItem(event_menu, SWT.NONE);
		event_menu_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExpMarbleController.resetCount();
			}
		});
		event_menu_5.setText("경험치 저장 구슬 사용횟수 초기화");
		
		MenuItem event_menu_6 = new MenuItem(event_menu, SWT.NONE);
		event_menu_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AutoHuntController.resetAutoHuntTime();
			}
		});
		event_menu_6.setText("자동 사냥 이용시간 초기화");
		
		reload = new MenuItem(commandAndEvent, SWT.CASCADE);
		reload.setEnabled(false);
		reload.setText("리로드");
		
		Menu reload_menu = new Menu(reload);
		reload.setMenu(reload_menu);
		
		MenuItem reload_menu_1 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Lineage.init(true);
			}
		});
		reload_menu_1.setText("lineage.conf 파일 리로드");
		
		MenuItem reload_menu_2 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Lineage_Balance.init();
			}
		});
		reload_menu_2.setText("lineage_balance.conf 파일 리로드");
		
		MenuItem reload_menu_31 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_31.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Socket.reload();
			}
		});
		reload_menu_31.setText("socket.conf 파일 리로드");
		
		MenuItem reload_menu_3 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NoticeController.reload();
			}
		});
		reload_menu_3.setText("notice.txt 파일 리로드");
		
		MenuItem reload_temp_menu_3 = new MenuItem(reload_menu, SWT.NONE);
		reload_temp_menu_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Npc_promotion.reload();
			}
		});
		reload_temp_menu_3.setText("npc_promotion.conf 파일 리로드");
		
		MenuItem reload_menu_27 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_27.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BackgroundDatabase.reload();
			}
		});
		reload_menu_27.setText("background_spawnlist 테이블 리로드");
		
/*		MenuItem reload_menu_26 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_26.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EvolutionDatabase.reload();
			}
		});
		reload_menu_26.setText("evolution 테이블 리로드");*/
		
		MenuItem reload_menu_4 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FishItemListDatabase.reload();
			}
		});
		reload_menu_4.setText("fishing_item_list 테이블 리로드");
		
		MenuItem reload_menu_30 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_30.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GmTeleportDatabase.reload();
			}
		});
		reload_menu_30.setText("gm_teleport 테이블 리로드");
		
		MenuItem reload_menu_5 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ItemDatabase.reload();
			}
		});
		reload_menu_5.setText("item 테이블 리로드");
		
		MenuItem reload_menu_6 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ItemBundleDatabase.reload();
			}
		});
		reload_menu_6.setText("item_bundle 테이블 리로드");
		
		MenuItem reload_menu_7 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ItemChanceBundleDatabase.reload();
			}
		});
		reload_menu_7.setText("item_chance_bundle 테이블 리로드");
		
		MenuItem reload_menu_34 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_34.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ItemDropMessageDatabase.reload();
			}
		});
		reload_menu_34.setText("item_drop_msg 테이블 리로드");
		
		MenuItem reload_menu_8 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ItemSkillDatabase.reload();
			}
		});
		reload_menu_8.setText("item_skill 테이블 리로드");
		
		MenuItem reload_menu_36 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_36.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ItemTeleportDatabase.reload();
			}
		});
		reload_menu_36.setText("item_teleport 테이블 리로드");
		
		MenuItem reload_menu_29 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_29.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LifeLostItemDatabase.reload();
			}
		});
		reload_menu_29.setText("life_lost_item 테이블 리로드");
		
		MenuItem reload_menu_9 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				KingdomController.reload();
			}
		});
		reload_menu_9.setText("kingdom 테이블 리로드");
		
		MenuItem reload_menu_28 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_28.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonsterDatabase.reload();
			}
		});
		reload_menu_28.setText("monster 테이블 리로드");
		
		MenuItem reload_menu_10 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonsterDropDatabase.reload();
			}
		});
		reload_menu_10.setText("monster_drop 테이블 리로드");
		
		MenuItem reload_menu_11 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonsterBossSpawnlistDatabase.reload();
			}
		});
		reload_menu_11.setText("monster_spawnlist_boss 테이블 리로드");
		
		MenuItem reload_menu_12 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_12.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonsterSkillDatabase.reload();
			}
		});
		reload_menu_12.setText("monster_skill 테이블 리로드");
		
		MenuItem reload_menu_26 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_26.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NpcDatabase.reload();
			}
		});
		reload_menu_26.setText("npc 테이블 리로드");
		
		MenuItem reload_menu_14 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_14.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PolyDatabase.reload();
			}
		});
		reload_menu_14.setText("poly 테이블 리로드");
		
		MenuItem reload_menu_25 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_25.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadPcRobot();
			}
		});
		reload_menu_25.setText("_robot 테이블 리로드");
		
		MenuItem reload_menu_22 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_22.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadRobotBook();
			}
		});
		reload_menu_22.setText("_robot_book 테이블 리로드");
		
		MenuItem reload_menu_23 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_23.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadPoly();
			}
		});
		reload_menu_23.setText("_robot_poly 테이블 리로드");
		
		MenuItem reload_menu_21 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_21.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadRobotSkill();
			}
		});
		reload_menu_21.setText("_robot_skill 테이블 리로드");
		
		MenuItem reload_menu_15 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_15.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.serverMagicReload();
			}
		});
		reload_menu_15.setText("skill 테이블 리로드");
		
		MenuItem reload_menu_16 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_16.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpriteFrameDatabase.reload();
			}
		});
		reload_menu_16.setText("spr_frame 테이블 리로드");
		
		MenuItem reload_menu_17 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_17.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SummonListDatabase.reload();
			}
		});
		reload_menu_17.setText("summon_list 테이블 리로드");
		
		MenuItem reload_menu_35 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_35.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ServerDatabase.reload();
			}
		});
		reload_menu_35.setText("server 테이블 리로드");
		
		MenuItem reload_menu_18 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_18.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ServerNoticeDatabase.reload();
			}
		});
		reload_menu_18.setText("server_notice 테이블 리로드");
		
		MenuItem reload_menu_19 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_19.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.serverReload();
			}
		});
		reload_menu_19.setText("server_reload 테이블 리로드");
		
		MenuItem reload_menu_20 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_20.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TeamBattleDatabase.reload();
			}
		});
		reload_menu_20.setText("team_battle_item 테이블 리로드");
		
		MenuItem reload_menu_24 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_24.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TimeDungeonDatabase.reload();
			}
		});
		reload_menu_24.setText("time_dungeon 테이블 리로드");
		
		MenuItem reload_menu_32 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_32.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotClanController.reload();
			}
		});
		reload_menu_32.setText("auto_clan_list 테이블 리로드");
		
		MenuItem reload_menu_33 = new MenuItem(reload_menu, SWT.NONE);
		reload_menu_33.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HackNoCheckDatabase.reload();
			}
		});
		reload_menu_33.setText("hack_no_check_ip 테이블 리로드");
		
		MenuItem menu_execute = new MenuItem(menu, SWT.CASCADE);
		menu_execute.setText("실행");
		
		Menu menu_3 = new Menu(menu_execute);
		menu_execute.setMenu(menu_3);
		
		MenuItem lineageConf = new MenuItem(menu_3, SWT.NONE);
		lineageConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/lineage.conf");
				} catch (Exception e2) { }
			}
		});
		lineageConf.setText("lineage.conf 실행");
		
		MenuItem lineageBalanceConf = new MenuItem(menu_3, SWT.NONE);
		lineageBalanceConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/lineage_balance.conf");
				} catch (Exception e2) { }
			}
		});
		lineageBalanceConf.setText("lineage_balance.conf 실행");
		
		MenuItem socketConf = new MenuItem(menu_3, SWT.NONE);
		socketConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/socket.conf");
				} catch (Exception e2) { }
			}
		});
		socketConf.setText("socket.conf 실행");
		
		MenuItem mySqlConf = new MenuItem(menu_3, SWT.NONE);
		mySqlConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/mysql.conf");
				} catch (Exception e2) { }
			}
		});
		mySqlConf.setText("mysql.conf 실행");
		
		MenuItem noticeTxt = new MenuItem(menu_3, SWT.NONE);
		noticeTxt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/notice.txt");
				} catch (Exception e2) { }
			}
		});
		noticeTxt.setText("notice.txt 실행");
		
		MenuItem npcPromotionConf = new MenuItem(menu_3, SWT.NONE);
		npcPromotionConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/npc_promotion.conf");
				} catch (Exception e2) { }
			}
		});
		npcPromotionConf.setText("npc_promotion.conf 실행");
		
		MenuItem navicat = new MenuItem(menu_3, SWT.NONE);
		navicat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/Program Files (x86)/PremiumSoft/Navicat Premium 8.2/navicat.exe");
				} catch (Exception e2) { }
				
				try {
					Runtime.getRuntime().exec("C:/Program Files (x86)/PremiumSoft/Navicat Premium 8.0/navicat.exe");
				} catch (Exception e2) { }
			}
		});
		navicat.setText("나비켓 실행");
		
		MenuItem explorer = new MenuItem(menu_3, SWT.NONE);
		explorer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/Program Files/Internet Explorer/iexplore.exe");
				} catch (Exception e2) { }
			}
		});
		explorer.setText("익스플로러 실행");
		
		MenuItem chrome = new MenuItem(menu_3, SWT.NONE);
		chrome.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
				} catch (Exception e2) { }
			}
		});
		chrome.setText("구글 크롬 실행");
		
		MenuItem menu_help = new MenuItem(menu, SWT.CASCADE);
		menu_help.setText("연락처");
		
		Menu menu_4 = new Menu(menu_help);
		menu_help.setMenu(menu_4);
		
		MenuItem nateon = new MenuItem(menu_4, SWT.NONE);
		nateon.setText("이메일: ");
		
		MenuItem mntmNewItem_1 = new MenuItem(menu_4, SWT.NONE);
		mntmNewItem_1.setText("네이트온: ");
		
		viewComposite = new ViewComposite(shell, SWT.NONE);
		viewComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		consoleComposite = new ConsoleComposite(shell, SWT.NONE);
		GridData gd_consoleComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd_consoleComposite.heightHint = 140;
		consoleComposite.setLayoutData(gd_consoleComposite);
		
		// 이벤트 등록.
		menu_system_1_item_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 서버 정보 로드.
				Main.init();
				// 맵뷰어 랜더링 시작.
				viewComposite.getScreenRenderComposite().start();
				// 정보 변경.
				menu_system_1_item_1.setEnabled(false);
				menu_system_1_item_2.setEnabled(true);
			}
		});
		menu_system_1_item_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread(Shutdown.getInstance()).start();
			}
		});

		// 매니저를 윈도우화면 가운데 좌표로 변경.
		shell.setBounds((display.getBounds().width/2)-(shell.getBounds().width/2), (display.getBounds().height/2)-(shell.getBounds().height/2), shell.getBounds().width, shell.getBounds().height);
		//shell.setBounds(450, 220, shell.getBounds().width, shell.getBounds().height);
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			} catch (Exception e) { }
		}
		
		Main.close();
	}
	
	static public ViewComposite getViewComposite() {
		return viewComposite;
	}
	
	static public ConsoleComposite getConsoleComposite() {
		return consoleComposite;
	}
	
	static public void toTimer(long time){
		// 뷰어 처리.
		viewComposite.toTimer(time);
		// 실시간 상태
		usercount.setText(String.format("%d", World.getUserSize()));
		memory.setText(String.format("%d", Gui_System.getUsedMemoryMB()) + "MB/"
				+ String.format("%d", Gui_System.getTotalMemoryMB()) + "MB");
		thread.setText(String.format("%d", Gui_System.getThread()));
		cpu.setText(String.format("%.0f%%", Gui_System.getUseCpu()));
		// 초기화 안된 상태.
		if(!event.isEnabled()){
			// 메뉴 활성화.
			event.setEnabled(true);
			command.setEnabled(true);
			reload.setEnabled(true);
			// Lineage 설정 정보 갱신
			event_menu_1.setSelection( Lineage.event_poly );
			menuItem_5.setSelection( Lineage.event_buff );
			menuItem_7.setSelection( Lineage.event_illusion );
			menuItem_8.setSelection( Lineage.event_christmas );
			menuItem_9.setSelection( Lineage.event_halloween );
			menuItem_10.setSelection( Lineage.event_lyra );
		}
	}
	
	/**
	 * 경고창 띄울때 사용.
	 * @param msg
	 */
	static public void toMessageBox(final String msg){
		toMessageBox(SERVER_VERSION, msg);
	}
	static public void toMessageBox(final String title, final String msg){
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
		messageBox.setText( String.format("경고 :: %s", title) );
		messageBox.setMessage(msg);
		messageBox.open();
	}
}
