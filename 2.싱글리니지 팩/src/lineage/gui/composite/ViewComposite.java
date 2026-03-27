package lineage.gui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.swtdesigner.SWTResourceManager;

public class ViewComposite extends Composite {

	private TabFolder tabFolder;
	
	private ServerInfoComposite serverInfoComposite;
	private ScreenRenderComposite screenRenderComposite;
	private ChattingComposite chattingComposite;
	private int tabSelectIdx;
	
	// 로그관련 탭
	private ConnectComposite connectComposite;
	private WarehouseComposite warehouseComposite;
	private TradeComposite tradeComposite;
	private EnchantComposite enchantComposite;
	private GiveAndDropComposite giveComposite;
	private CommandComposite commandComposite;
	private SpeedHackComposite speedHackComposite;
	private DamageCheckComposite damageCheckComposite;
	private ConnectorComposite connectorComposite;
	private EnchantLostItemComposite enchantLostItemComposite;
	private petComposite petComposite;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ViewComposite(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(24, 24, 24));
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("서버 정보");
		
		serverInfoComposite = new ServerInfoComposite(tabFolder, SWT.NONE);
		tabItem.setControl(serverInfoComposite);
		
		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("모니터링");
		
		screenRenderComposite = new ScreenRenderComposite(tabFolder, SWT.NONE);
		tabItem_1.setControl(screenRenderComposite);
		
		TabItem tabItem_2 = new TabItem(tabFolder, SWT.NONE);
		tabItem_2.setText("채팅");
		
		chattingComposite = new ChattingComposite(tabFolder, SWT.NONE);
		tabItem_2.setControl(chattingComposite);
		
		TabItem tabItem_3 = new TabItem(tabFolder, SWT.NONE);
		tabItem_3.setText("접속 로그");
		
		connectComposite = new ConnectComposite(tabFolder, SWT.NONE);
		tabItem_3.setControl(connectComposite);
		
		TabItem tabItem_4 = new TabItem(tabFolder, SWT.NONE);
		tabItem_4.setText("창고 로그");
		
		warehouseComposite = new WarehouseComposite(tabFolder, SWT.NONE);
		tabItem_4.setControl(warehouseComposite);
		
		TabItem tabItem_5 = new TabItem(tabFolder, SWT.NONE);
		tabItem_5.setText("거래 로그");
		
		tradeComposite = new TradeComposite(tabFolder, SWT.NONE);
		tabItem_5.setControl(tradeComposite);
		
		TabItem tabItem_6 = new TabItem(tabFolder, SWT.NONE);
		tabItem_6.setText("인첸트 로그");
		
		enchantComposite = new EnchantComposite(tabFolder, SWT.NONE);
		tabItem_6.setControl(enchantComposite);
		
		TabItem tabItem_7 = new TabItem(tabFolder, SWT.NONE);
		tabItem_7.setText("아이템 드랍 로그");
		
		giveComposite = new GiveAndDropComposite(tabFolder, SWT.NONE);
		tabItem_7.setControl(giveComposite);
		
		TabItem tabItem_8 = new TabItem(tabFolder, SWT.NONE);
		tabItem_8.setText("명령어 로그");
		
		commandComposite = new CommandComposite(tabFolder, SWT.NONE);
		tabItem_8.setControl(commandComposite);
		
		TabItem tabItem_9 = new TabItem(tabFolder, SWT.NONE);
		tabItem_9.setText("스피드핵 로그");
		
		speedHackComposite = new SpeedHackComposite(tabFolder, SWT.NONE);
		tabItem_9.setControl(speedHackComposite);
		
		TabItem tabItem_10 = new TabItem(tabFolder, SWT.NONE);
		tabItem_10.setText("데미지 로그");
		
		damageCheckComposite = new DamageCheckComposite(tabFolder, SWT.NONE);
		tabItem_10.setControl(damageCheckComposite);
		
		TabItem tabItem_11 = new TabItem(tabFolder, SWT.NONE);
		tabItem_11.setText("인첸트 복구 로그");
		
		enchantLostItemComposite = new EnchantLostItemComposite(tabFolder, SWT.NONE);
		tabItem_11.setControl(enchantLostItemComposite);
		
		TabItem tabItem_12 = new TabItem(tabFolder, SWT.NONE);
		tabItem_12.setText("펫 로그");
		
		petComposite = new petComposite(tabFolder, SWT.NONE);
		tabItem_12.setControl(petComposite);
		
//		TabItem tabItem_12 = new TabItem(tabFolder, SWT.NONE);
//		tabItem_12.setText("접속기 인증 로그");
//		
//		connectorComposite = new ConnectorComposite(tabFolder, SWT.NONE);
//		tabItem_12.setControl(connectorComposite);
	}
	
	public void toTimer(long time){
		tabSelectIdx = tabFolder.getSelectionIndex();
		
		// 서버정보 표현 갱신.
		if(tabSelectIdx == 0)
			serverInfoComposite.toUpdate();
		// 맵뷰어 랜더링 표현.
		if(tabSelectIdx == 1)
			screenRenderComposite.toUpdate();
	}
	
	public ScreenRenderComposite getScreenRenderComposite() {
		return screenRenderComposite;
	}
	
	public ChattingComposite getChattingComposite(){
		return chattingComposite;
	}
	
	public ConnectComposite getConnectComposite() {
		return connectComposite;
	}

	public WarehouseComposite getWarehouseComposite() {
		return warehouseComposite;
	}

	public TradeComposite getTradeComposite() {
		return tradeComposite;
	}

	public EnchantComposite getEnchantComposite() {
		return enchantComposite;
	}

	public GiveAndDropComposite getGiveComposite() {
		return giveComposite;
	}
	
	public CommandComposite getCommandComposite() {
		return commandComposite;
	}
	
	public SpeedHackComposite getSpeedHackComposite() {
		return speedHackComposite;
	}
	
	public DamageCheckComposite getDamageCheckComposite() {
		return damageCheckComposite;
	}
	
	public ConnectorComposite getConnectorComposite() {
		return connectorComposite;
	}
	
	public EnchantLostItemComposite getEnchantLostItemComposite() {
		return enchantLostItemComposite;
	}
	public petComposite getpetComposite() {
		return petComposite;
	}

	public int getTabSelectIdx(){
		return tabSelectIdx;
	}
}
