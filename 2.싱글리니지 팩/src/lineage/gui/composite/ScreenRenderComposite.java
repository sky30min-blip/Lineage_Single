package lineage.gui.composite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import lineage.bean.event.GuiToWorldAllObject;
import lineage.gui.GuiMain;
import lineage.gui.dialog.MonsterSpawn;
import lineage.gui.dialog.NpcSpawn;
import lineage.gui.dialog.PlayerInventory;
import lineage.gui.dialog.PlayerTeleport;
import lineage.gui.dialog.ShopEditor;
import lineage.share.Lineage;
import lineage.thread.EventThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.CommandController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.instance.SummonInstance;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class ScreenRenderComposite extends Composite implements Runnable {

	private Combo list_map;
	private int[] list_index;
	private Combo list_player;
	private Text hitObjectName;
	private Text hitObjectStr;
	private Text hitObjectDex;
	private Text hitObjectCon;
	private Text hitObjectWis;
	private Text hitObjectInt;
	private Text hitObjectCha;
	private Text hitObjectLevel;
	private Text hitObjectExp;
	private Text hitObjectHp;
	private Text hitObjectMp;
	private Text hitObjectAc;
	private Text hitObjectWeight;
	private Text hitObjectEarth;
	private Text hitObjectFire;
	private Text hitObjectWater;
	private Text hitObjectWind;
	private Text hitObjectLawfulValue;
	private Text hitObjectSp;
	private Text hitObjectMr;
	private Label hitObjectLawful;
	private Menu screen_menu;

	// 랜더링될 스크린.
	private Composite screen;
	private Frame frame;
	private BufferStrategy strategy;
	// 사용될 폰트
	private Font strFont;			// 왼쪽상단 좌표 표현 폰트
	private Font npcFont;			// 엔피시 표현 폰트
	private Font monsterFont;		// 몬스터 표현 폰트
	private Font boss_monsterFont;	// 보스 몬스터 표현 폰트
	private Font playerFont;		// 사용자 표현 폰트
	private Font petFont;
	// 색상들
	private Color background_color;
	private Color player_color;
	private Color monster_color;
	private Color boss_monster_color;
	private Color npc_color;
	private Color pet_color;
	// 객체 클릭시 표현될 이미지
	private Image hitObjectImage;
	// 클릭된 객체 임시 저장 변수.
	private object hitObject;
	// 기본 프레임값
	private static final int FRAME = 1000 / 30;
	// 스크린에 오버된 마우스 커서 위치.
	private int cur_x;
	private int cur_y;
	// 스크린에 마우스 클릭된 위치.
	private int cur_point_x;
	private int cur_point_y;
	// 맵 이미지
	private Image mapImage;
	// 맵 이미지 표현될 위치값
	private int map_x;
	private int map_y;
	private int map_dynamic_x;
	private int map_dynamic_y;
	// 리니지 좌표값
	private int lineage_x;
	private int lineage_y;
	// 현재 정의된 맵
	private lineage.bean.lineage.Map nowMap;
	// 현재 맵의 크기.
	private int mapWidth;
	private int mapHeight;
	// 현재 줌상태
	private int zoom;
	// 미니맵 표현될 좌표
	private int miniMap_x = 0;
	private int miniMap_y = 80;
	// 미니맵 크기
	private int miniMap_width = 100;
	private int miniMap_height = 100;
	// 미니맵에 포커스라인 색상
	private Color miniMap_focus_color;

	// 맵에 표현하게될 객체 목록.
	private List<object> list_all;		// 실제 그리기위해 몰빵할 리스트.
	// 화면에 표현할 객체들 확인용
	private Button screen_print_player;
	private Button screen_print_monster;
	private Button screen_print_background;
	private Button screen_print_npc;
	private Button screen_print_item;
	private Button screen_print_shop;


	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ScreenRenderComposite(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(24, 24, 24));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);

		Group group_screen = new Group(this, SWT.NONE);
		group_screen.setText("스크린");
		group_screen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_group_screen = new GridLayout(6, false);
		gl_group_screen.verticalSpacing = 0;
		gl_group_screen.horizontalSpacing = 0;
		gl_group_screen.marginHeight = 0;
		gl_group_screen.marginWidth = 0;
		group_screen.setLayout(gl_group_screen);

		screen_print_item = new Button(group_screen, SWT.CHECK);
		screen_print_item.setText("아이템");

		screen_print_monster = new Button(group_screen, SWT.CHECK);
		screen_print_monster.setText("몬스터");

		screen_print_npc = new Button(group_screen, SWT.CHECK);
		screen_print_npc.setText("엔피시");

		screen_print_shop = new Button(group_screen, SWT.CHECK);
		screen_print_shop.setText("상점");

		screen_print_background = new Button(group_screen, SWT.CHECK);
		screen_print_background.setText("배경");

		screen_print_player = new Button(group_screen, SWT.CHECK);
		screen_print_player.setSelection(true);
		screen_print_player.setText("사용자");

		screen = new Composite(group_screen, SWT.NONE | SWT.EMBEDDED);
		screen.addMouseWheelListener(event_screen_wheel);
		screen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));

		screen_menu = new Menu(screen);
		screen.setMenu(screen_menu);

		frame = SWT_AWT.new_Frame(screen);


		Group group_1 = new Group(this, SWT.NONE);
		group_1.setText("컨트롤");
		GridLayout gl_group_1 = new GridLayout(3, false);
		gl_group_1.verticalSpacing = 1;
		gl_group_1.marginHeight = 0;
		gl_group_1.marginWidth = 0;
		group_1.setLayout(gl_group_1);
		GridData gd_group_1 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_group_1.widthHint = 200;
		group_1.setLayoutData(gd_group_1);

		Label lblMap = new Label(group_1, SWT.NONE);
		lblMap.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMap.setText("map");

		list_map = new Combo(group_1, SWT.READ_ONLY);
		list_map.addSelectionListener(event_list_map_select);
		list_map.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblPlayer = new Label(group_1, SWT.NONE);
		lblPlayer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPlayer.setText("player");

		list_player = new Combo(group_1, SWT.READ_ONLY);
		list_player.addMouseListener(event_list_player);
		list_player.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnFocus = new Button(group_1, SWT.NONE);
		btnFocus.addSelectionListener(event_list_player_focus);
		btnFocus.setText("focus");

		Group group = new Group(group_1, SWT.NONE);
		group.setText("객체 정보");
		GridLayout gl_group = new GridLayout(5, false);
		gl_group.verticalSpacing = 1;
		gl_group.marginHeight = 0;
		gl_group.marginWidth = 0;
		group.setLayout(gl_group);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		Label lblName = new Label(group, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("name");

		hitObjectName = new Text(group, SWT.BORDER);
		hitObjectName.setEnabled(false);
		hitObjectName.setEditable(false);
		hitObjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		Label lblLevel = new Label(group, SWT.NONE);
		lblLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLevel.setText("level");

		hitObjectLevel = new Text(group, SWT.BORDER);
		hitObjectLevel.setEditable(false);
		hitObjectLevel.setEnabled(false);
		GridData gd_hitObjectLevel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_hitObjectLevel.widthHint = 22;
		hitObjectLevel.setLayoutData(gd_hitObjectLevel);

		Composite composite_1 = new Composite(group, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.marginWidth = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label lblExp = new Label(composite_1, SWT.NONE);
		lblExp.setText("exp");

		hitObjectExp = new Text(composite_1, SWT.BORDER);
		hitObjectExp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		hitObjectExp.setEditable(false);
		hitObjectExp.setEnabled(false);

		Label lblHp = new Label(group, SWT.NONE);
		lblHp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHp.setText("hp");

		hitObjectHp = new Text(group, SWT.BORDER);
		hitObjectHp.setEditable(false);
		hitObjectHp.setEnabled(false);
		hitObjectHp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		Label lblMp = new Label(group, SWT.NONE);
		lblMp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMp.setText("mp");

		hitObjectMp = new Text(group, SWT.BORDER);
		hitObjectMp.setEditable(false);
		hitObjectMp.setEnabled(false);
		hitObjectMp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		hitObjectLawful = new Label(group, SWT.NONE);
		hitObjectLawful.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		hitObjectLawful.setText("Neutral");

		hitObjectLawfulValue = new Text(group, SWT.BORDER);
		hitObjectLawfulValue.setEnabled(false);
		hitObjectLawfulValue.setEditable(false);
		hitObjectLawfulValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		Composite composite_2 = new Composite(group, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(4, false);
		gl_composite_2.marginHeight = 0;
		gl_composite_2.marginWidth = 0;
		gl_composite_2.verticalSpacing = 1;
		composite_2.setLayout(gl_composite_2);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1);
		gd_composite_2.horizontalIndent = 12;
		composite_2.setLayoutData(gd_composite_2);

		Label lblStr = new Label(composite_2, SWT.NONE);
		lblStr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStr.setText("str");

		hitObjectStr = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectStr = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectStr.widthHint = 39;
		hitObjectStr.setLayoutData(gd_hitObjectStr);
		hitObjectStr.setEditable(false);
		hitObjectStr.setEnabled(false);

		Label lblWis = new Label(composite_2, SWT.NONE);
		lblWis.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWis.setText("wis");

		hitObjectWis = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectWis = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectWis.widthHint = 28;
		hitObjectWis.setLayoutData(gd_hitObjectWis);
		hitObjectWis.setEditable(false);
		hitObjectWis.setEnabled(false);

		Label lblDex = new Label(composite_2, SWT.NONE);
		lblDex.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDex.setText("dex");

		hitObjectDex = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectDex = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectDex.widthHint = 26;
		hitObjectDex.setLayoutData(gd_hitObjectDex);
		hitObjectDex.setEditable(false);
		hitObjectDex.setEnabled(false);

		Label lblInt = new Label(composite_2, SWT.NONE);
		lblInt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInt.setText("int");

		hitObjectInt = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectInt = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectInt.widthHint = 33;
		hitObjectInt.setLayoutData(gd_hitObjectInt);
		hitObjectInt.setEditable(false);
		hitObjectInt.setEnabled(false);

		Label lblCon = new Label(composite_2, SWT.NONE);
		lblCon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCon.setText("con");

		hitObjectCon = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectCon = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectCon.widthHint = 35;
		hitObjectCon.setLayoutData(gd_hitObjectCon);
		hitObjectCon.setEditable(false);
		hitObjectCon.setEnabled(false);

		Label lblCha = new Label(composite_2, SWT.NONE);
		lblCha.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCha.setText("cha");

		hitObjectCha = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectCha = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectCha.widthHint = 32;
		hitObjectCha.setLayoutData(gd_hitObjectCha);
		hitObjectCha.setEditable(false);
		hitObjectCha.setEnabled(false);

		Label lblAc = new Label(composite_2, SWT.NONE);
		lblAc.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAc.setText("ac");

		hitObjectAc = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectAc = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectAc.widthHint = 36;
		hitObjectAc.setLayoutData(gd_hitObjectAc);
		hitObjectAc.setEnabled(false);
		hitObjectAc.setEditable(false);

		Label lblWeight = new Label(composite_2, SWT.NONE);
		lblWeight.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWeight.setText("weight");

		hitObjectWeight = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectWeight = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectWeight.widthHint = 29;
		hitObjectWeight.setLayoutData(gd_hitObjectWeight);
		hitObjectWeight.setEnabled(false);
		hitObjectWeight.setEditable(false);

		Label lblEarth = new Label(composite_2, SWT.NONE);
		lblEarth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEarth.setText("earth");

		hitObjectEarth = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectEarth = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectEarth.widthHint = 33;
		hitObjectEarth.setLayoutData(gd_hitObjectEarth);
		hitObjectEarth.setEnabled(false);
		hitObjectEarth.setEditable(false);

		Label lblWater = new Label(composite_2, SWT.NONE);
		lblWater.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWater.setText("water");

		hitObjectWater = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectWater = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectWater.widthHint = 34;
		hitObjectWater.setLayoutData(gd_hitObjectWater);
		hitObjectWater.setEnabled(false);
		hitObjectWater.setEditable(false);

		Label lblFire = new Label(composite_2, SWT.NONE);
		lblFire.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFire.setText("fire");

		hitObjectFire = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectFire = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectFire.widthHint = 28;
		hitObjectFire.setLayoutData(gd_hitObjectFire);
		hitObjectFire.setEnabled(false);
		hitObjectFire.setEditable(false);

		Label lblWind = new Label(composite_2, SWT.NONE);
		lblWind.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWind.setText("wind");

		hitObjectWind = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectWind = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectWind.widthHint = 32;
		hitObjectWind.setLayoutData(gd_hitObjectWind);
		hitObjectWind.setEnabled(false);
		hitObjectWind.setEditable(false);

		Label lblSp = new Label(composite_2, SWT.NONE);
		lblSp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSp.setText("sp");

		hitObjectSp = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectSp = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectSp.widthHint = 26;
		hitObjectSp.setLayoutData(gd_hitObjectSp);
		hitObjectSp.setEnabled(false);
		hitObjectSp.setEditable(false);

		Label lblMr = new Label(composite_2, SWT.NONE);
		lblMr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMr.setText("mr");

		hitObjectMr = new Text(composite_2, SWT.BORDER);
		GridData gd_hitObjectMr = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hitObjectMr.widthHint = 33;
		hitObjectMr.setLayoutData(gd_hitObjectMr);
		hitObjectMr.setEnabled(false);
		hitObjectMr.setEditable(false);
		// 더블버퍼 처리.
		frame.createBufferStrategy( 2 );
		strategy = frame.getBufferStrategy();
		// 색상 지정.
		background_color = new Color(60, 60, 60);
		player_color = new Color(0, 162, 232);
		monster_color = new Color(237, 28, 36);
		boss_monster_color = new Color(000, 102, 255);
		npc_color = new Color(34, 177, 76);
		pet_color = new Color(239, 228, 176);
		// 미니맵 포커스 색상 지정.
		miniMap_focus_color = Color.PINK;
		// 글씨 폰트지정.
		strFont = new Font( "Dialog", Font.PLAIN, 25 );
		playerFont = new Font( "Player", Font.BOLD, 12 );
		monsterFont = new Font( "Monster", Font.PLAIN, 12 );
		boss_monsterFont = new Font( "Boss_Monster", Font.BOLD, 12 );
		npcFont = new Font( "Npc", Font.PLAIN, 12 );
		petFont = new Font( "Pet", Font.BOLD, 12 );

		// 이벤트 지정 부분.
		frame.addMouseListener(event_screen_click);
		frame.addMouseMotionListener(event_screen_move);

		// 
		list_all = new ArrayList<object>();

		//
		hitObjectImage = new ImageIcon( "images/Emotion.png" ).getImage();
	}

	/**
	 * 랜더링 활성화.
	 *  : GuiMain에서 서버 시작누르면 이걸 ViewComposite를 거쳐 이걸 호출함.
	 */
	public void start(){
		// 쓰레드 활성화.
		new Thread(this).start();
	}

	/**
	 * 랜더링할 객체들의 모음 처리 함수.
	 * @param list_all
	 */
	public void toUpdate(List<object> list_all){
		synchronized (this.list_all) {
			this.list_all.clear();
			this.list_all.addAll(list_all);
		}
	}

	/**
	 * 정보 갱신 처리 함수.
	 */
	public void toUpdate(){
		try {
			// 정보 갱신.
			if(list_map.getItemCount() != World.getMapSize()){
				// 아직 맵정보가 갱신 안됫을때.
				list_map.setItems(World.toStringMap());
				list_index = World.toIndexMap();
			}
			// 맵이 지정된 상태일 경우만.
			if (mapImage != null) {
				// 정보 추출 요청
				EventThread.append(GuiToWorldAllObject.clone(EventThread.getPool(GuiToWorldAllObject.class), nowMap, screen_print_item.getSelection(), screen_print_npc.getSelection(), screen_print_monster.getSelection(),
						screen_print_background.getSelection(), screen_print_player.getSelection(), screen_print_shop.getSelection()));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : toUpdate()\r\n", ScreenRenderComposite.class.toString());
			lineage.share.System.println(e);
		}
	}

	@Override
	public void run(){
		try {
			Graphics g = null;
			while(true){
				if(GuiMain.getViewComposite().getTabSelectIdx() == 1){
					g = strategy.getDrawGraphics();
	
					// 스크린 단색으로 입히기.
					g.setColor(background_color);
					g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
	
					if(mapImage != null){
						// 맵 그리기
						synchronized (mapImage) {
							g.drawImage(mapImage, map_x, map_y, mapWidth*zoom, mapHeight*zoom, null);
						}
						try {
							// 리니지 좌표값 정의.
							toLineageLocation();
							// 커서 좌표 그리기.
							toStringLocation(g);
							// 객체 그리기.
							toObject(g);
							// 선택된 객체위에 이미지 표현.
							toHitObjectImage(g);
							// 선택된 객체를 중심으로 맵 자동 이동처리.
							toHitObjectFocus(g);
							// 미니맵 표현.
							toMiniMap(g);
						} catch (Exception e) { }
					}
	
					// 메모리 제거.
					g.dispose();
	
					// 화면에 뿌리기.
					if ( !strategy.contentsLost() )
						strategy.show();
					Toolkit.getDefaultToolkit().sync();
				}
				
				Thread.sleep(FRAME);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : run()\r\n", ScreenRenderComposite.class.toString());
			lineage.share.System.println(e);
		}
	}

	/**
	 * 맵 그리기.
	 * @param map
	 */
	public void printMap(int map, int zoom, int map_x, int map_y){
		try {

			nowMap = World.get_map(map);
			int x = nowMap.locX1;
			int y = nowMap.locY1;
			mapWidth = nowMap.locX2 - x;
			mapHeight = nowMap.locY2 - y;
			mapImage = frame.createImage(mapWidth, mapHeight);
			this.map_x = map_x;
			this.map_y = map_y;
			this.zoom = zoom;

			synchronized (mapImage) {
				Graphics g = mapImage.getGraphics();
				g.setColor(background_color);
				int c = 0;
				for(int i=0 ; i<nowMap.data_size ; ++i){
					if(++c>nowMap.size){
						y+=1;
						x = nowMap.locX1;
						c = 0;
					}
	
					if (World.tileValue > -1) {
						int tile = World.get_map(x, y, map);
						
						if (tile != World.tileValue) {
							g.fillRect(x - nowMap.locX1, y - nowMap.locY1, 1, 1);
						}
					} else {
						if (World.isNotMovingTile(x, y, nowMap.mapid) || !World.isThroughAttack(x, y + 1, nowMap.mapid, 0)) {
							g.fillRect(x - nowMap.locX1, y - nowMap.locY1, 1, 1);
						}
					}

					x += 1;
				}
				g.dispose();
			}
		} catch (Exception e) { }
	}

	/**
	 * 리니지 좌표 표현.
	 * @param g
	 */
	private void toStringLocation(Graphics g){	
		if (nowMap != null) {
			if (hitObject != null) {
				int x = hitObject.getX();
				int y = hitObject.getY();
				int map = hitObject.getMap();
				g.setFont(strFont);
				g.setColor(Color.GREEN);
				g.drawString(String.format("x:%d y:%d   %s   타일값:%d", x, y, Util.getMapName(null, nowMap.mapid), World.get_map(x, y, map)), 10, 25);
			} else {
				int x = lineage_x;
				int y = lineage_y;
				int map = nowMap.mapid;
				g.setFont(strFont);
				g.setColor(Color.GREEN);
				g.drawString(String.format("x:%d y:%d   %s   타일값:%d", lineage_x, lineage_y, Util.getMapName(null, nowMap.mapid), World.get_map(x, y, map)), 10, 25);
			}
		}
	}

	/**
	 * 처리된값을 참고로 리니지 좌표 추출.
	 */
	private void toLineageLocation(){
		if(nowMap != null){
			lineage_x = nowMap.locX1 + ((cur_x-map_x)/zoom);
			lineage_y = nowMap.locY1 + ((cur_y-map_y)/zoom);
		}
	}

	/**
	 * 미니맵 그리기.
	 * @param g
	 */
	private void toMiniMap(Graphics g){
		g.drawImage(mapImage, miniMap_x, miniMap_y, miniMap_width, miniMap_height, null);
		// 보고있는 화면 위치 포커스 맞추기.
		int focus_x = miniMap_x;
		int focus_y = miniMap_y;
		int focus_width = miniMap_width/zoom;
		int focus_height = miniMap_height/zoom;
		// 연산
		if(zoom > 1){
			// x값 연산
			focus_x += (map_x/zoom)/(mapWidth/miniMap_width);
			if(focus_x<0){
				// 범위 내에 잇을경우 abs로 음수를 양수로 전환.
				focus_x = Math.abs(focus_x);
				// 우측으로 너무 벗어낫을경우 우측끝에 지정하기.
				if(focus_x+focus_width > miniMap_x+miniMap_width)
					focus_x = miniMap_x + miniMap_width-focus_width;
			}else{
				// 왼쪽으로 벗어낫을경우 왼쪽끝에 지정.
				focus_x = miniMap_x;
			}
			// y값 연산.
			focus_y -= (map_y/zoom)/(mapHeight/miniMap_height);
			if(focus_y<miniMap_y){
				// 위로 벗어낫을경우 위쪽 끝지점 지정.
				focus_y = miniMap_y;
			}else{
				// 아래로 벗어낫을경우 아래 끝지점 지정.
				if(focus_y+focus_height > miniMap_y+miniMap_height)
					focus_y = miniMap_y + miniMap_height-focus_height;
			}
		}
		// 표현.
		g.setColor(miniMap_focus_color);
		g.drawRect(focus_x, focus_y, focus_width, focus_height);
		g.drawRect(focus_x+1, focus_y+1, focus_width-2, focus_height-2);
	}

	/**
	 * 리니지 월드 객체 그리기.
	 * @param g
	 */
	private void toObject(Graphics g){
		synchronized (list_all) {
			for(object o : list_all){
				if(o.isWorldDelete()==false){
					String name = o.getName();
					int draw_x = ((o.getX()-nowMap.locX1)*zoom) + map_x;
					int draw_y = ((o.getY()-nowMap.locY1)*zoom) + map_y;
					int draw_x1 = draw_x;
					int draw_y1 = draw_y;
					
					if(o instanceof PcInstance){
						g.setFont(playerFont);
						g.setColor(player_color);
						draw_x -= (name.length()*playerFont.getSize())/2;
						draw_y += playerFont.getSize()/2;
					}else if(o instanceof SummonInstance){
						SummonInstance s = (SummonInstance)o;
						g.setFont(petFont);
						g.setColor(pet_color);
						if(s.getMonster()!=null)
							name = s.getMonster().getName();
						draw_x -= (name.length()*monsterFont.getSize())/2;
						draw_y += petFont.getSize()/2;
					} else if (o instanceof MonsterInstance) {
						MonsterInstance mon = (MonsterInstance) o;
						
						if (mon.getMonster().isBoss() || mon.isBoss()) {
							g.setFont(boss_monsterFont);
							g.setColor(boss_monster_color);
							
							if (mon.getMonster() != null)
								name = "[보스] " + mon.getMonster().getName();
							
							draw_x -= (name.length() * boss_monsterFont.getSize()) / 2;
							draw_y += boss_monsterFont.getSize() / 2;
						} else {
							g.setFont(monsterFont);
							g.setColor(monster_color);

							if (mon.getMonster() != null)
								name = mon.getMonster().getName();
							
							draw_x -= (name.length() * monsterFont.getSize()) / 2;
							draw_y += monsterFont.getSize() / 2;
						}
					}else if(o instanceof NpcInstance){
						NpcInstance npc = (NpcInstance)o;
						g.setFont(npcFont);
						g.setColor(npc_color);
						if(npc.getNpc() != null)
							name = npc.getNpc().getName();
						draw_x -= (name.length()*npcFont.getSize())/2;
						draw_y += npcFont.getSize()/2;
					}else if(o instanceof ShopInstance){
						ShopInstance shop = (ShopInstance)o;
						g.setFont(npcFont);
						g.setColor(npc_color);
						if(shop.getNpc() != null)
							name = shop.getNpc().getName();
						draw_x -= (name.length()*npcFont.getSize())/2;
						draw_y += npcFont.getSize()/2;
					}
/*					g.drawString(name, draw_x + 5, draw_y + 10);
					g.fillRect(draw_x1, draw_y1, 4, 4);*/
					
					if (zoom < 10) {
						g.drawString(name, draw_x + 5, draw_y + 10);
						g.fillRect(draw_x1, draw_y1, 4, 4);
					} else {
						g.drawString(name, draw_x + 5 + (zoom - 5), draw_y + 10 + (zoom - 5));
						g.fillRect(draw_x1, draw_y1, zoom - 5, zoom - 5);
					}
				}
			}
		}
	}

	/**
	 * 선택된 객체머리위에 이미지 표현.
	 * @param g
	 */
	private void toHitObjectImage(Graphics g){
		if(hitObject!=null){
			// 이미지 정보
			int width = 29;
			int height = 35;
			int sx = 462;
			int sy = 255;
			// 객체 정보
			int draw_x = zoom < 10 ? ((hitObject.getX() - nowMap.locX1) * zoom) + map_x - (width / 2) : ((hitObject.getX() - nowMap.locX1) * zoom) + map_x - (width / 2) + (zoom / 3);
			int draw_y = ((hitObject.getY()-nowMap.locY1)*zoom) + map_y - height;
			g.drawImage(hitObjectImage, draw_x, draw_y, draw_x+width, draw_y+height, sx, sy, sx+width, sy+height, null);

			// 선택된 객체와 현재지정된 맵이 다를경우 맵 전환하기.
			if(hitObject.getMap()!=nowMap.mapid){			
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						if(hitObject != null){
							try {
								int idx = 0;
								for (int i = 0; i < list_index.length; i++) {
									if (list_index[i] == hitObject.getMap()) {
										idx = i;
										break;
									}
								}
								//list_map.select( list_map.indexOf(String.valueOf(hitObject.getMap())) );
								list_map.select( idx );
								printMap(hitObject.getMap(), zoom, map_x, map_y);
							} catch (Exception e) { }
						}
					}
				});
			}
			// 월드에서 제거된상태일경우 체크 해제하기.
			if(hitObject.isWorldDelete())
				hitObject = null;
		}
	}

	/**
	 * 선택된 객체를 중심으로 맵 자동으로 맞추기.
	 * @param g
	 */
	private void toHitObjectFocus(Graphics g){
		if(hitObject != null){
			// 객체 좌표
			int x = hitObject.getX() - nowMap.locX1;
			int y = hitObject.getY() - nowMap.locY1;
			// 현재 화면 가운데 좌표
			int screen_x = ((frame.getWidth()/2)-map_x)/zoom;
			int screen_y = ((frame.getHeight()/2)-map_y)/zoom;
			// 두좌표 차이를 구하기.
			x = (screen_x-x) * zoom;
			y = (screen_y-y) * zoom;
			// 차이난 만큼 맵 이동.
			map_x = x+map_x;
			map_y = y+map_y;
		}
	}

	/**
	 * 현재 커서위치에 객체가 오버되었는지 확인해주는 함수.
	 * @return
	 */
	private object searchOverObject(){
		synchronized (list_all) {
			for(object o : list_all){
				if(o.isWorldDelete() || o.getName()==null)
					continue;
				
				// 원본
/*				int dynamicX = o.getX() - nowMap.locX1;
				int dynamicY = o.getY() - nowMap.locY1;
				int draw_x = (dynamicX * zoom) - ((cha.getName().length() * 12) / 2) + map_x;
				int draw_y = (dynamicY * zoom) + (12 / 2) + map_y;
				int x1 = draw_x - zoom;
				int x2 = draw_x + cha.getName().length() * 12 + zoom;
				int y1 = draw_y - 12 - zoom;
				int y2 = draw_y + zoom;*/

				int size = zoom;					
				int draw_x = ((o.getX() - nowMap.locX1) * zoom) + map_x;
				int draw_y = ((o.getY() - nowMap.locY1) * zoom) + map_y;
				
				int x1 = draw_x;
				int x2 = draw_x + size;
				int y1 = draw_y;
				int y2 = draw_y + size;

				if(cur_x>=x1 && cur_x<=x2 && cur_y>=y1 && cur_y<=y2)
					return o;
			}
			return null;
		}
	}
	
	// 마우스 커서가 스크린 아무곳에서 클릭했을때 호출되서 처리하는 변수.
	private MouseAdapter event_screen_click = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if(mapImage == null)
				return;

			// 마우스 우측버튼 클릭 처리
			if(e.getButton() == MouseEvent.BUTTON3){
				GuiMain.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						// 이전 메뉴 다 제거.
						for(MenuItem mi : screen_menu.getItems())
							mi.dispose();
						// 메뉴 구성.
						if(hitObject != null){
							if(hitObject instanceof PcInstance){
								new MenuItem(screen_menu, SWT.NONE).setText("벤");
								new MenuItem(screen_menu, SWT.NONE).setText("채팅 금지");
								new MenuItem(screen_menu, SWT.NONE).setText("버프");
								new MenuItem(screen_menu, SWT.SEPARATOR);
								// 인벤토리
								new MenuItem(screen_menu, SWT.NONE).setText("인벤토리");

								screen_menu.getItem(0).addSelectionListener( screen_menu_pc_1 );
								screen_menu.getItem(1).addSelectionListener( screen_menu_pc_2 );
								screen_menu.getItem(2).addSelectionListener( screen_menu_pc_3 );
								screen_menu.getItem(4).addSelectionListener( screen_menu_pc_4 );
							}
							if(hitObject instanceof MonsterInstance){
								// 인벤토리 수정
								// 드랍정보 수정
								new MenuItem(screen_menu, SWT.NONE).setText("인벤토리");
							}
							if(hitObject instanceof ShopInstance){
								new MenuItem(screen_menu, SWT.NONE).setText("물품 수정");

								screen_menu.getItem(0).addSelectionListener( screen_menu_shop_1 );
							}
						}else{
							new MenuItem(screen_menu, SWT.NONE).setText("사용자 텔레포트");
							new MenuItem(screen_menu, SWT.SEPARATOR);
							new MenuItem(screen_menu, SWT.NONE).setText("몬스터 스폰");
							new MenuItem(screen_menu, SWT.NONE).setText("엔피시 스폰");

							screen_menu.getItem(0).addSelectionListener( screen_menu_1 );
							screen_menu.getItem(2).addSelectionListener( screen_menu_2 );
							screen_menu.getItem(3).addSelectionListener( screen_menu_3 );
						}
						// 메뉴 표현.
						screen_menu.setVisible(true);
					}
				});
				return;
			}

			//
			cur_point_x = e.getX();
			cur_point_y = e.getY();
			map_dynamic_x = map_x;
			map_dynamic_y = map_y;

			// 현재 커서 위치에 객체 찾기.
			hitObject = searchOverObject();
			if(hitObject != null){
				// 표현.
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						String name = hitObject.getName();
						double exp = 0;
						int nowHp=0;
						int nowMp=0;
						int totalHp=0;
						int totalMp=0;
						int _str=0;
						int _dex=0;
						int _con=0;
						int _wis=0;
						int _int=0;
						int _cha = 0;
						int ac = 0;
						int weight = 0;
						int earth = 0;
						int water = 0;
						int wind = 0;
						int fire = 0;
						int sp = 0;
						int mr = 0;
						String lawful = "Lawful";
						// 추출.
						if(hitObject instanceof NpcInstance){
							NpcInstance npc = (NpcInstance)hitObject;
							if(npc.getNpc() != null)
								name = npc.getNpc().getName();
							else
								name = npc.getName();
						}else if(hitObject instanceof MonsterInstance){
							MonsterInstance mon = (MonsterInstance)hitObject;
							if(mon.getMonster() != null)
								name = mon.getMonster().getName();
							else
								name = mon.getName();
						}else if(hitObject instanceof PcInstance){
//							PcInstance pc = (PcInstance)hitObject;
						}
						if(hitObject instanceof Character){
							Character cha = (Character)hitObject;
							exp = cha.getExp();
							nowHp = cha.getNowHp();
							nowMp = cha.getNowMp();
							totalHp = cha.getTotalHp();
							totalMp = cha.getTotalMp();
							_str  = cha.getLvStr() ;
							_dex = cha.getLvDex() ;
							_con = cha.getLvCon() ;
							_wis = cha.getLvWis() ;
							_int = cha.getLvInt() ;
							_cha = cha.getLvCha() ;
							ac = 10-cha.getTotalAc();
							weight = (int)Math.floor( (cha.getInventory().getWeightPercent()/(Lineage.server_version>=270?240D:30D))*100D );
							earth = cha.getEarthress();
							water = cha.getWaterress();
							fire = cha.getFireress();
							wind = cha.getWindress();
							sp = SkillController.getSp(cha);
							mr = SkillController.getMr(cha, false);
						}
						if(hitObject.getLawful() < Lineage.NEUTRAL)
							lawful = "Chaotic";
						else if(hitObject.getLawful()>=Lineage.NEUTRAL && hitObject.getLawful()<Lineage.NEUTRAL+500)
							lawful = "Neutral";
						// 표현.
						hitObjectName.setText(name);
						hitObjectLevel.setText(String.valueOf(hitObject.getLevel()));
						hitObjectExp.setText(String.valueOf(exp));
						hitObjectHp.setText(String.format("%d/%d", nowHp, totalHp));
						hitObjectMp.setText(String.format("%d/%d", nowMp, totalMp));
						hitObjectStr.setText(String.valueOf(_str));
						hitObjectDex.setText(String.valueOf(_dex));
						hitObjectCon.setText(String.valueOf(_con));
						hitObjectWis.setText(String.valueOf(_wis));
						hitObjectInt.setText(String.valueOf(_int));
						hitObjectCha.setText(String.valueOf(_cha));
						hitObjectAc.setText(String.valueOf(ac));
						hitObjectWeight.setText(String.format("%d%%", weight));
						hitObjectEarth.setText(String.valueOf(earth));
						hitObjectWater.setText(String.valueOf(water));
						hitObjectFire.setText(String.valueOf(fire));
						hitObjectWind.setText(String.valueOf(wind));
						hitObjectSp.setText(String.valueOf(sp));
						hitObjectMr.setText(String.valueOf(mr));
						hitObjectLawful.setText(lawful);
						hitObjectLawfulValue.setText(String.valueOf(hitObject.getLawful()-Lineage.NEUTRAL));
					}
				});
			}
		}
	};

	// 마우스 커서가 스크린에서 움직일때 처리되는 변수.
	private MouseMotionAdapter event_screen_move = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent e) {
			if(mapImage == null)
				return;
			// 마우스 이동 좌표 갱신.
			cur_x = e.getX();
			cur_y = e.getY();
			// 포커스를 스크린객체로 자동으로 잡게하기 위해. 키이벤트 발생 유도.
			GuiMain.display.asyncExec(new Runnable() {
				@Override
				public void run() {
					screen.forceFocus();
				}
			});
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			// 맵이 읽혀지지 않았거나 좌측버튼누른상태에 드래그가 아니라면 무시.
			if(mapImage==null || e.getModifiers()!=16)
				return;

			mouseMoved(e);

			map_x = (cur_x-cur_point_x) + map_dynamic_x;
			map_y = (cur_y-cur_point_y) + map_dynamic_y;
		}
	};

	// 컨트롤러에 있는 맵 하나 선택할때 처리하는 변수.
	private SelectionAdapter event_list_map_select = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				printMap(list_index[list_map.getSelectionIndex()], 1, 0, 0);
			} catch (Exception e2) { }
		}
	};

	// 콤보박스 클릭시 호출.
	private org.eclipse.swt.events.MouseAdapter event_list_player = new org.eclipse.swt.events.MouseAdapter() {
		@Override
		public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
			if(!lineage.Main.running)
				return;

			list_player.removeAll();
			try {
				// 접속된 사용자 목록 갱신.
				List<PcInstance> pc_list = World.getPcList();
				if(pc_list.size() > 0){
					int pc_size = pc_list.size();
					String list_player_item[] = new String[pc_size];
					for(PcInstance pc : pc_list)
						list_player_item[--pc_size] = pc.getName().length()>10 ? pc.getName().substring(0, 9) : pc.getName();
					list_player.setItems(list_player_item);
				}
			} catch (Exception e2) {
				list_player.removeAll();
			}
		}
	};

	// 사용자 focus버튼 클릭시 처리하는 변수.
	private SelectionAdapter event_list_player_focus = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			int idx = list_player.getSelectionIndex();
			if(idx >= 0){
				String name = list_player.getItem( idx );
				if(name!=null){
					PcInstance pc = World.findPc(name);
					if(pc != null){
						hitObject = pc;
						if(nowMap == null)
							printMap( hitObject.getMap(), 10, 0, 0 );
					}
				}
			}
		}
	};

	// 스크린에서 마우스휠 할경우 호출되서 처리하는 변수.
	private MouseWheelListener event_screen_wheel = new MouseWheelListener() {
		@Override
		public void mouseScrolled(org.eclipse.swt.events.MouseEvent e) {
			// 확대 및 추소인지 확인.
			final boolean zoom_in = e.count > 0;
			int new_zoom = zoom_in ? zoom+1 : zoom-1;
			if(new_zoom<=0) {
				new_zoom = 0;
				return;
			}
			if(new_zoom>30)
				new_zoom = 30;
			if(new_zoom != zoom){
				// 줌처리 전에 화면 정가운데에 리니지 좌표 추출.
				int x = ((frame.getWidth()/2)-map_x)/zoom;
				int y = ((frame.getHeight()/2)-map_y)/zoom;
				// 줌 처리.
				zoom = new_zoom;
				// 줌처리후 가운데 좌표값 추출.
				int new_x = ((frame.getWidth()/2)-map_x)/zoom;
				int new_y = ((frame.getHeight()/2)-map_y)/zoom;
				// 전과 이전의 차이 연산
				x = (new_x-x) * zoom;
				y = (new_y-y) * zoom;
				// 이전에 가운데 좌표로 맵이동.
				map_x = x+map_x;
				map_y = y+map_y;
				lineage.share.System.println(zoom_in ? String.format("확대 : zoom(%d)", zoom) : String.format("축소 : zoom(%d)", zoom));
			}
		}
	};

	// 스크린 마우스 우측버튼 메뉴 : 사용자 텔레포트
	private SelectionAdapter screen_menu_1 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			PlayerTeleport.open(lineage_x, lineage_y, nowMap.mapid);
		}
	};

	// 스크린 마우스 우측버튼 메뉴 : 몬스터 스폰
	private SelectionAdapter screen_menu_2 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			MonsterSpawn.open(lineage_x, lineage_y, nowMap.mapid);
		}
	};

	// 스크린 마우스 우측버튼 메뉴 : 엔피시 스폰
	private SelectionAdapter screen_menu_3 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			NpcSpawn.open(lineage_x, lineage_y, nowMap.mapid);
		}
	};

	// 상점 엔피시 우측버튼 메뉴 : 상점 물품
	private SelectionAdapter screen_menu_shop_1 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(hitObject instanceof ShopInstance){
				ShopInstance shop = (ShopInstance)hitObject;
				if(shop.getNpc() != null)
					ShopEditor.open(shop.getNpc());
			}
		}
	};

	private SelectionAdapter screen_menu_pc_1 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(hitObject instanceof PcInstance){
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText( "경고" );
				messageBox.setMessage("정말 벤 하시겠습니까?");
				if(messageBox.open() == SWT.YES)
					CommandController.toBan(null, new StringTokenizer(hitObject.getName()));
			}
		}
	};

	private SelectionAdapter screen_menu_pc_2 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(hitObject instanceof PcInstance){
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText( "경고" );
				messageBox.setMessage("채금 5분을 주시겠습니까?");
				if(messageBox.open() == SWT.YES)
					CommandController.toChattingClose(null, new StringTokenizer(String.format("%s %d", hitObject.getName(), 5)));
			}
		}
	};

	private SelectionAdapter screen_menu_pc_3 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(hitObject instanceof PcInstance){
				CommandController.toBuff(null, new StringTokenizer(hitObject.getName()));
			}
		}
	};

	private SelectionAdapter screen_menu_pc_4 = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(hitObject instanceof PcInstance)
				PlayerInventory.open( (PcInstance)hitObject );
		}
	};

}
