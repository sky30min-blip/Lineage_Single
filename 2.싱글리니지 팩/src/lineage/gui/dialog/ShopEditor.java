package lineage.gui.dialog;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.NpcShopDatabase;
import lineage.gui.GuiMain;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class ShopEditor {

	static private Shell shell;
	// 각 스탭마다 변경될 부분
	static private Composite composite_controller;
	// 왼쪽 박스에 표현될 라벨
	static private Label label_step1;
	static private Label label_step2;
	static private Label label_step3;
	// 왼쪽 박스에 표현될 글자 폰트 정보
	static private Font normal;
	static private Font select;
	// 해당 창에 타이틀 명
	static private String title;
	// 이동하게될 좌표 정보
	static private Npc npc;
	//
	static private Connection con;

	static {
		normal = SWTResourceManager.getFont("맑은 고딕", 9, SWT.NORMAL);
		select = SWTResourceManager.getFont("맑은 고딕", 9, SWT.BOLD);
		title = "상점 물품 수정";
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	static public void open(Npc npc) {
		try {
			con = DatabaseConnection.getLineage();
		} catch (Exception e) { }
		
		ShopEditor.npc = npc;
		
		shell = new Shell(GuiMain.shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		shell.setSize(500, 400);
		shell.setText(title);
		
		GridLayout gl_shell = new GridLayout(2, false);
		gl_shell.horizontalSpacing = 2;
		gl_shell.verticalSpacing = 0;
		gl_shell.marginHeight = 0;
		gl_shell.marginWidth = 0;
		shell.setLayout(gl_shell);
		
		Composite composite_status = new Composite(shell, SWT.NONE);
		composite_status.setLayout(new GridLayout(1, false));
		composite_status.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		label_step1 = new Label(composite_status, SWT.NONE);
		label_step1.setText("물품 지정");
		
		label_step2 = new Label(composite_status, SWT.NONE);
		label_step2.setText("정보 수정");
		
		label_step3 = new Label(composite_status, SWT.NONE);
		label_step3.setText("완료");
		
		composite_controller = new Composite(shell, SWT.NONE);
		
		step1();
//		step2(null);
//		step3(null, true);
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!GuiMain.display.readAndDispatch()) 
				GuiMain.display.sleep();
		}
		
		composite_controller.dispose();
		label_step3.dispose();
		label_step2.dispose();
		label_step1.dispose();
		composite_status.dispose();
		
		DatabaseConnection.close(con);
	}
	
	static private void step1(){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(1);
		
		GridLayout gl_composite_controller = new GridLayout(3, false);
		gl_composite_controller.verticalSpacing = 0;
		gl_composite_controller.horizontalSpacing = 2;
		composite_controller.setLayout(gl_composite_controller);
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite_1 = new Composite(composite_controller, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.horizontalSpacing = 2;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.marginWidth = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		final Text text = new Text(composite_1, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button button_4 = new Button(composite_1, SWT.NONE);
		button_4.setText("검색");
		
		Group group_1 = new Group(composite_controller, SWT.NONE);
		group_1.setText("아이템");
		GridLayout gl_group_1 = new GridLayout(1, false);
		gl_group_1.verticalSpacing = 0;
		gl_group_1.horizontalSpacing = 0;
		gl_group_1.marginHeight = 0;
		gl_group_1.marginWidth = 0;
		group_1.setLayout(gl_group_1);
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		
		final List list = new List(group_1, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_list = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_list.widthHint = 100;
		list.setLayoutData(gd_list);
		
		DragSource dragSource = new DragSource(list, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		new Label(composite_controller, SWT.NONE);
		
		Group group = new Group(composite_controller, SWT.NONE);
		group.setText("상점");
		GridLayout gl_group = new GridLayout(1, false);
		gl_group.verticalSpacing = 0;
		gl_group.horizontalSpacing = 0;
		gl_group.marginHeight = 0;
		gl_group.marginWidth = 0;
		group.setLayout(gl_group);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		
		final List list_1 = new List(group, SWT.BORDER | SWT.V_SCROLL);
		list_1.setData("down", false);
		GridData gd_list_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_list_1.widthHint = 100;
		list_1.setLayoutData(gd_list_1);
		
		DropTarget dropTarget = new DropTarget(list_1, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		
		Button button_1 = new Button(composite_controller, SWT.NONE);
		button_1.setToolTipText("추가");
		button_1.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true, 1, 1));
		button_1.setText("->");
		
		Button button_2 = new Button(composite_controller, SWT.NONE);
		button_2.setToolTipText("제거");
		button_2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		button_2.setText("<-");
		new Label(composite_controller, SWT.NONE);
		new Label(composite_controller, SWT.NONE);
		
		Button button = new Button(composite_controller, SWT.NONE);
		GridData gd_button = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button.widthHint = 100;
		button.setLayoutData(gd_button);
		button.setText("다음");
		
		// 이벤트 등록.
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode==13 || e.keyCode==16777296)
					// 검색
					toSearchItem(text, list);
			}
		});
		button_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 검색
				toSearchItem(text, list);
			}
		});
		list_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				list_1.setData("down", true);
				list_1.setData("select", list_1.getSelectionIndex());
			}
			@Override
			public void mouseUp(MouseEvent e) {
				list_1.setData("down", false);
			}
		});
		list_1.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				Boolean drag = (Boolean)list_1.getData("down");
				if(drag){
					// 옮기기 전의 위치
					int select = (Integer)list_1.getData("select");
					// 옮긴 위치.
					int move_idx = list_1.getSelectionIndex();
					if(select != move_idx){
						// 위치 바꾸기.
						String temp = list_1.getItem(select);
						Object temp_object = list_1.getData(String.valueOf(select));
						list_1.setItem(select, list_1.getItem(move_idx));
						list_1.setData(String.valueOf(select), list_1.getData(String.valueOf(move_idx)));					
						
						list_1.setItem(move_idx, temp);
						list_1.setData(String.valueOf(move_idx), temp_object);
						// 정보 변경.
						list_1.setData("select", move_idx);
						list_1.select(move_idx);
					}
				}
			}
		});
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(list.getSelectionCount() <= 0)
					return;
				// 추가
				for(String name : list.getSelection())
					list_1.add( name );
				list_1.setTopIndex(list_1.getVerticalBar().getMaximum());
			}
		});
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(list_1.getSelectionCount() <= 0)
					return;

				for (int i = list_1.getSelectionIndex(); i < list_1.getItemCount(); i++)
					list_1.setData(String.valueOf(i), list_1.getData(String.valueOf(i + 1)));

				// 삭제
				list_1.remove(list_1.getSelectionIndex());
			}
		});
		list_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(list_1.getSelectionCount() <= 0)
					return;
				// 삭제
				if(e.keyCode == SWT.DEL) {
					for (int i = list_1.getSelectionIndex(); i < list_1.getItemCount(); i++)
						list_1.setData(String.valueOf(i), list_1.getData(String.valueOf(i + 1)));
					
					list_1.remove( list_1.getSelectionIndex() );
				}
			}
		});
		dragSource.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {
				event.data = list.getSelection()[0];
			}
		});
		dropTarget.addDropListener(new DropTargetAdapter() {
			@Override
			public void drop(DropTargetEvent event) {
				if(event.data instanceof String){
					list_1.add( (String)event.data );
					list_1.setTopIndex(list_1.getVerticalBar().getMaximum());
				}
			}
		});
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(list_1.getItemCount() == 0){
					GuiMain.toMessageBox("상점목록을 추가하여 주십시오.");
					return;
				}
				Map<Integer, Object> list = new HashMap<Integer, Object>();
				for(int i=list_1.getItemCount()-1 ; i>=0 ; --i)
					list.put(Integer.valueOf(i), list_1.getData(String.valueOf(i)));
	
				step2(list_1.getItems(), list);
			}
		});
		
		// 정보 갱신
		for(Item i : ItemDatabase.getList())
			list.add(i.getName());
		
		int idx = 0;
		for(Shop s : npc.getShop_list()) {
			list_1.add(s.getItemName());
			list_1.setData(String.valueOf(idx++), s);
		}
		
		composite_controller.layout();
	}
	
	/**
	 * 아이템 검색
	 * @param text
	 * @param list
	 */
	static private void toSearchItem(Text text, List list){
		String name = text.getText().toLowerCase();
		
		// 이전 기록 제거
		list.removeAll();
		
		// 검색명이 없을경우 전체 표현.
		if(name==null || name.length()<=0){
			if(ItemDatabase.getList().size() > 0){
				for(Item i : ItemDatabase.getList())
					list.add( i.getName() );
			}else{
				GuiMain.toMessageBox(title, "아이템이 존재하지 않습니다.");
			}
			return;
		}
		
		// 검색.
		for(Item i : ItemDatabase.getList()){
			int pos = i.getName().toLowerCase().indexOf(name);
			if(pos >= 0)
				list.add( i.getName() );
		}
		
		// 등록된게 없을경우 안내 멘트.
		if(list.getItemCount() <= 0)
			GuiMain.toMessageBox(title, "일치하는 아이템이 없습니다.");
		
		// 포커스.
		text.setFocus();
	}
	
	static private void step2(String[] shop_list, Map<Integer, Object> list){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(2);
		shell.setSize(900, 500);
		GridLayout gl_composite_controller = new GridLayout(2, false);
		gl_composite_controller.verticalSpacing = 0;
		gl_composite_controller.horizontalSpacing = 2;
		composite_controller.setLayout(gl_composite_controller);
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		final Button btnNpcshop = new Button(composite_controller, SWT.CHECK);
		btnNpcshop.setSelection(true);
		btnNpcshop.setText("npc_shop 정보 갱신");
		btnNpcshop.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		
		final Table table = new Table(composite_controller, SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;
		
		TableColumn tblclmnUid = new TableColumn(table, SWT.NONE);
		tblclmnUid.setWidth(100);
		tblclmnUid.setText("uid");
		
		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("name");
		
		TableColumn tblclmnItemname = new TableColumn(table, SWT.NONE);
		tblclmnItemname.setWidth(100);
		tblclmnItemname.setText("itemname");
		
		TableColumn tblclmnItemcount = new TableColumn(table, SWT.NONE);
		tblclmnItemcount.setWidth(100);
		tblclmnItemcount.setText("itemcount");
		
		TableColumn tblclmnItembress = new TableColumn(table, SWT.NONE);
		tblclmnItembress.setWidth(100);
		tblclmnItembress.setText("itembress");
		
		TableColumn tblclmnItemenlevel = new TableColumn(table, SWT.NONE);
		tblclmnItemenlevel.setWidth(100);
		tblclmnItemenlevel.setText("itemenlevel");
		
		TableColumn tblclmnItementime = new TableColumn(table, SWT.NONE);
		tblclmnItementime.setWidth(100);
		tblclmnItementime.setText("itemtime");
		
		TableColumn tblclmnSell = new TableColumn(table, SWT.NONE);
		tblclmnSell.setWidth(100);
		tblclmnSell.setText("sell");
		
		TableColumn tblclmnBuy = new TableColumn(table, SWT.NONE);
		tblclmnBuy.setWidth(100);
		tblclmnBuy.setText("buy");
		
		TableColumn tblclmnGamble = new TableColumn(table, SWT.NONE);
		tblclmnGamble.setWidth(100);
		tblclmnGamble.setText("gamble");
		
		TableColumn tblclmnPrice = new TableColumn(table, SWT.NONE);
		tblclmnPrice.setWidth(100);
		tblclmnPrice.setText("price");
		
		TableColumn tblclmnaden_type = new TableColumn(table, SWT.NONE);
		tblclmnaden_type.setWidth(100);
		tblclmnaden_type.setText("aden_type");
		
		
		Button button_3 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_3 = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_button_3.widthHint = 100;
		button_3.setLayoutData(gd_button_3);
		button_3.setText("이전");
		
		Button button_5 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_5 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_button_5.widthHint = 100;
		button_5.setLayoutData(gd_button_5);
		button_5.setText("다음");
		
		// 이벤트 등록.
		table.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(index);
					for (int i = 3; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener() {
								@Override
								public void handleEvent(final Event e) {
									switch (e.type) {
										case SWT.FocusOut:
											item.setText(column, text.getText());
											text.dispose();
											break;
										case SWT.Traverse:
											switch (e.detail) {
												case SWT.TRAVERSE_RETURN:
													item.setText(column, text.getText());
												case SWT.TRAVERSE_ESCAPE:
													text.dispose();
													e.doit = false;
											}
											break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, i);
							text.setText(item.getText(i));
							text.selectAll();
							text.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 이전
				step1();
			}
		});
		button_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 다음
				step3(table, btnNpcshop.getSelection());
			}
		});
		// 정보 갱신
		int idx = 0;
		for (String s : shop_list) {
			Object o = list.get(idx++);
			Shop shop = o == null ? null : (Shop) o;
			String[] field = new String[12];

			field[0] = String.valueOf(idx);
			field[1] = npc.getName();
			field[2] = s;
			field[3] = shop == null ? "1" : String.valueOf(shop.getItemCount());
			field[4] = shop == null ? "1" : String.valueOf(shop.getItemBress());
			field[5] = shop == null ? "0" : String.valueOf(shop.getItemEnLevel());
			field[6] = shop == null ? "0" : String.valueOf(shop.getItemTime());
			field[7] = shop == null ? "true" : String.valueOf(shop.isItemSell());
			field[8] = shop == null ? "true" : String.valueOf(shop.isItemBuy());
			field[9] = shop == null ? "false" : String.valueOf(shop.isGamble());
			field[10] = shop == null ? "0" : String.valueOf(shop.getPrice());
			field[11] = shop == null ? "아데나" : shop.getAdenType();
			new TableItem(table, SWT.NONE).setText(field);
		}
		
		composite_controller.layout();
	}
	
	static private void step3(Table table, boolean db){
		// 처리 1 (아래 이전내용들 다 제거하는것 때문에 에러생겨서 상단에서 먼저 초기화처리함.).
		npc.getShop_list().clear();
		for(TableItem ti : table.getItems()){
			Shop s = new Shop();
			s.setUid( Integer.valueOf(ti.getText(0)) );
			s.setNpcName( npc.getName() );
			s.setItemName( ti.getText(2) );
			s.setItemCount( Integer.valueOf(ti.getText(3)) );
			s.setItemBress( Integer.valueOf(ti.getText(4)) );
			s.setItemEnLevel( Integer.valueOf(ti.getText(5)) );
			s.setItemTime( Integer.valueOf(ti.getText(6)) );
			s.setItemSell( Boolean.valueOf(ti.getText(7)) );
			s.setItemBuy( Boolean.valueOf(ti.getText(8)) );
			s.setGamble( Boolean.valueOf(ti.getText(9)) );
			s.setPrice( Integer.valueOf(ti.getText(10)) );
			s.setAdenType( ti.getText(11) );
			npc.getShop_list().add(s);
		}
		
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(3);
		
		GridLayout gl_composite_controller = new GridLayout(1, false);
		gl_composite_controller.verticalSpacing = 0;
		gl_composite_controller.horizontalSpacing = 2;
		composite_controller.setLayout(gl_composite_controller);
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		List list_2 = new List(composite_controller, SWT.BORDER | SWT.V_SCROLL);
		list_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Button button_6 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_6 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button_6.widthHint = 100;
		button_6.setLayoutData(gd_button_6);
		button_6.setText("완료");

		// 이벤트 등록.
		button_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 다음
				shell.dispose();
			}
		});
		
		// 처리 2.
		list_2.add("메모리 갱신 완료.");
		if(db){
			NpcShopDatabase.delete(con, npc.getName());
			NpcShopDatabase.insert(con, npc.getShop_list());
			list_2.add("디비정보 갱신 완료.");
		}
		
		composite_controller.layout();
	}
	
	/**
	 * 스탭에 맞춰서 왼쪽 글씨 폰트 변경하기.
	 * @param step
	 */
	static private void selectStep(int step){
		label_step1.setForeground(step==1 ? SWTResourceManager.getColor(SWT.COLOR_DARK_RED) : SWTResourceManager.getColor(SWT.COLOR_BLACK));
		label_step2.setForeground(step==2 ? SWTResourceManager.getColor(SWT.COLOR_DARK_RED) : SWTResourceManager.getColor(SWT.COLOR_BLACK));
		label_step3.setForeground(step==3 ? SWTResourceManager.getColor(SWT.COLOR_DARK_RED) : SWTResourceManager.getColor(SWT.COLOR_BLACK));
		
		label_step1.setFont(step==1 ? select : normal);
		label_step2.setFont(step==2 ? select : normal);
		label_step3.setFont(step==3 ? select : normal);
	}
}
