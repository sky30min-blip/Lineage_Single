package lineage.gui.dialog;

import java.sql.Connection;

import lineage.bean.database.Monster;
import lineage.bean.database.MonsterGroup;
import lineage.bean.database.MonsterSpawnlist;
import lineage.database.DatabaseConnection;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.gui.GuiMain;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class MonsterSpawn {

	static private Shell shell;
	// 각 스탭마다 변경될 부분
	static private Composite composite_controller;
	// 왼쪽 박스에 표현될 라벨
	static private Label label_step1;
	static private Label label_step2;
	static private Label label_step3;
	static private Label label_step4;
	// 왼쪽 박스에 표현될 글자 폰트 정보
	static private Font normal;
	static private Font select;
	// 해당 창에 타이틀 명
	static private String title;
	// 이동하게될 좌표 정보
	static private int x;
	static private int y;
	static private int map;
	//
	static private Connection con;
	
	static {
		normal = SWTResourceManager.getFont("맑은 고딕", 9, SWT.NORMAL);
		select = SWTResourceManager.getFont("맑은 고딕", 9, SWT.BOLD);
		title = "몬스터 스폰";
	}

	/**
	 * Open the dialog.
	 * @return the result
	 * @wbp.parser.entryPoint
	 */
	static public void open(int x, int y, int map) {
		try {
			con = DatabaseConnection.getLineage();
		} catch (Exception e) { }
		
		MonsterSpawn.x = x;
		MonsterSpawn.y = y;
		MonsterSpawn.map = map;

		shell = new Shell(GuiMain.shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		shell.setSize(600, 550);
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
		label_step1.setText("몬스터 선택");
		
		label_step2 = new Label(composite_status, SWT.NONE);
		label_step2.setText("스폰정보 입력");
		
		label_step3 = new Label(composite_status, SWT.NONE);
		label_step3.setText("최종 확인");
		
		label_step4 = new Label(composite_status, SWT.NONE);
		label_step4.setText("완료");
		
		composite_controller = new Composite(shell, SWT.NONE);
		
		step1();
//		step2("1234");
//		step3(false, title, title, false, map, map, map, map, map, map, false, title, map, title, map, title, map, title, map, false);
//		step4(false, title, title, false, map, map, map, map, map, map, false, title, map, title, map, title, map, title, map, false);
		
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
	
	/**
	 * 몬스터 선택
	 */
	static private void step1(){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(1);
		
		GridLayout gl_composite_controller = new GridLayout(2, false);
		gl_composite_controller.verticalSpacing = 2;
		gl_composite_controller.horizontalSpacing = 0;
		composite_controller.setLayout(gl_composite_controller);
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		final Text text = new Text(composite_controller, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button button_1 = new Button(composite_controller, SWT.NONE);
		button_1.setText("검색");
		
		final List list = new List(composite_controller, SWT.BORDER | SWT.V_SCROLL);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Button button = new Button(composite_controller, SWT.NONE);
		GridData gd_button = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1);
		gd_button.widthHint = 100;
		button.setLayoutData(gd_button);
		button.setText("다음");
		
		// 이벤트 등록.
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode==13 || e.keyCode==16777296)
					// 검색
					toSearchMonster(text, list);
			}
		});
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 검색
				toSearchMonster(text, list);
			}
		});
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 선택된 몬스터 없다면 무시.
				if(list.getSelectionCount() <= 0){
					GuiMain.toMessageBox(title, "몬스터를 선택하여 주십시오.");
					return;
				}
				// 다음 스탭으로 이동.
				step2( list.getSelection()[0] );
			}
		});
		
		// 메모리에 로드된 전체 몬스터 이름 등록.
		toSearchMonster(text, list);
		
		composite_controller.layout();
	}

	/**
	 * 몬스터 검색.
	 * @param text
	 * @param list
	 */
	static private void toSearchMonster(Text text, List list){
		String name = text.getText().toLowerCase();
		
		// 이전 기록 제거
		list.removeAll();
		
		// 검색명이 없을경우 전체 표현.
		if(name==null || name.length()<=0){
			if(MonsterDatabase.getList().size() > 0){
				for(Monster m : MonsterDatabase.getList())
					list.add( m.getName() );
			}else{
				GuiMain.toMessageBox(title, "몬스터가 존재하지 않습니다.");
			}
			return;
		}
		
		// 검색.
		for(Monster mon : MonsterDatabase.getList()){
			int pos = mon.getName().toLowerCase().indexOf(name);
			if(pos >= 0)
				list.add( mon.getName() );
		}
		
		// 등록된게 없을경우 안내 멘트.
		if(list.getItemCount() <= 0)
			GuiMain.toMessageBox(title, "일치하는 몬스터가 없습니다.");
		
		// 포커스.
		text.setFocus();
	}
	
	/**
	 * 선택된 몬스터 스폰 정보 입력
	 */
	static private void step2(String name){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(2);
		
		GridLayout gl_composite_controller = new GridLayout(4, false);
		gl_composite_controller.verticalSpacing = 2;
		gl_composite_controller.horizontalSpacing = 0;
		composite_controller.setLayout(gl_composite_controller);
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		final Button btnMonsterspawnlist = new Button(composite_controller, SWT.CHECK);
		btnMonsterspawnlist.setText("monster_spawnlist 테이블에 등록");
		GridData gd_btnMonsterspawnlist = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gd_btnMonsterspawnlist.horizontalIndent = 40;
		btnMonsterspawnlist.setLayoutData(gd_btnMonsterspawnlist);
		
		final Button btnBoss = new Button(composite_controller, SWT.CHECK);
		GridData gd_btnBoss = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gd_btnBoss.horizontalIndent = 40;
		gd_btnBoss.widthHint = 108;
		btnBoss.setLayoutData(gd_btnBoss);
		btnBoss.setText("보스 몬스터");
		
		final Button button = new Button(composite_controller, SWT.CHECK);
		button.setSelection(true);
		GridData gd_button = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gd_button.horizontalIndent = 40;
		button.setLayoutData(gd_button);
		button.setText("소환 몬스터");
		
		Label label_1 = new Label(composite_controller, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("이름 : ");
		
		final Text text_2 = new Text(composite_controller, SWT.BORDER);
		text_2.setText("매니저 스폰툴");
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label label = new Label(composite_controller, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("몬스터 : ");
		
		final Combo text_1 = new Combo(composite_controller, SWT.READ_ONLY);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		int idx = 0;
		int pos = 0;
		for(Monster m : MonsterDatabase.getList()){
			if(name.equalsIgnoreCase(m.getName()))
				pos = idx;
			text_1.add(m.getName(), idx++);
		}
		text_1.select(pos);
		
		Label label_2 = new Label(composite_controller, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("랜덤 : ");
		
		final Combo combo = new Combo(composite_controller, SWT.READ_ONLY);
		combo.setItems(new String[] {"true", "false"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.select(1);
		
		Label label_3 = new Label(composite_controller, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("스폰갯수 : ");
		
		final Text text_3 = new Text(composite_controller, SWT.BORDER);
		text_3.setText("1");
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_4 = new Label(composite_controller, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("범위 : ");
		
		final Text text_4 = new Text(composite_controller, SWT.BORDER);
		text_4.setText("0");
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblX = new Label(composite_controller, SWT.NONE);
		lblX.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblX.setText("x : ");
		
		final Text text_5 = new Text(composite_controller, SWT.BORDER);
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_5.setText( String.valueOf(x) );
		
		Label lblY = new Label(composite_controller, SWT.NONE);
		lblY.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblY.setText("y : ");
		
		final Text text_6 = new Text(composite_controller, SWT.BORDER);
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_6.setText( String.valueOf(y) );
		
		Label lblMap = new Label(composite_controller, SWT.NONE);
		lblMap.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMap.setText("map : ");
		
		final Text text_7 = new Text(composite_controller, SWT.BORDER);
		text_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_7.setText( String.valueOf(map) );
		
		Label label_5 = new Label(composite_controller, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("리스폰 : ");
		
		final Text text_8 = new Text(composite_controller, SWT.BORDER);
		text_8.setText("60");
		text_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_6 = new Label(composite_controller, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("그룹 : ");
		
		final Combo combo_1 = new Combo(composite_controller, SWT.READ_ONLY);
		combo_1.setItems(new String[] {"true", "false"});
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo_1.select(1);
		
		Label label_7 = new Label(composite_controller, SWT.NONE);
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("몬스터1 : ");
		
		final Combo combo_group_monster1 = new Combo(composite_controller, SWT.READ_ONLY);
		combo_group_monster1.setEnabled(false);
		GridData gd_combo_group_monster1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo_group_monster1.widthHint = 10;
		combo_group_monster1.setLayoutData(gd_combo_group_monster1);
		combo_group_monster1.setItems( text_1.getItems() );
		
		Label label_8 = new Label(composite_controller, SWT.NONE);
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("갯수 : ");
		
		final Text text_group_monster1 = new Text(composite_controller, SWT.BORDER);
		text_group_monster1.setText("1");
		text_group_monster1.setEnabled(false);
		text_group_monster1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_9 = new Label(composite_controller, SWT.NONE);
		label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_9.setText("몬스터2 : ");
		
		final Combo combo_group_monster2 = new Combo(composite_controller, SWT.READ_ONLY);
		combo_group_monster2.setEnabled(false);
		GridData gd_combo_group_monster2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo_group_monster2.widthHint = 10;
		combo_group_monster2.setLayoutData(gd_combo_group_monster2);
		combo_group_monster2.setItems( text_1.getItems() );
		
		Label label_12 = new Label(composite_controller, SWT.NONE);
		label_12.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_12.setText("갯수 : ");
		
		final Text text_group_monster2 = new Text(composite_controller, SWT.BORDER);
		text_group_monster2.setText("1");
		text_group_monster2.setEnabled(false);
		text_group_monster2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_10 = new Label(composite_controller, SWT.NONE);
		label_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_10.setText("몬스터3 : ");
		
		final Combo combo_group_monster3 = new Combo(composite_controller, SWT.READ_ONLY);
		combo_group_monster3.setEnabled(false);
		GridData gd_combo_group_monster3 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo_group_monster3.widthHint = 10;
		combo_group_monster3.setLayoutData(gd_combo_group_monster3);
		combo_group_monster3.setItems( text_1.getItems() );
		
		Label label_13 = new Label(composite_controller, SWT.NONE);
		label_13.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_13.setText("갯수 : ");
		
		final Text text_group_monster3 = new Text(composite_controller, SWT.BORDER);
		text_group_monster3.setText("1");
		text_group_monster3.setEnabled(false);
		text_group_monster3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_11 = new Label(composite_controller, SWT.NONE);
		label_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_11.setText("몬스터4 : ");
		
		final Combo combo_group_monster4 = new Combo(composite_controller, SWT.READ_ONLY);
		combo_group_monster4.setEnabled(false);
		GridData gd_combo_group_monster4 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo_group_monster4.widthHint = 10;
		combo_group_monster4.setLayoutData(gd_combo_group_monster4);
		combo_group_monster4.setItems( text_1.getItems() );
		
		Label label_14 = new Label(composite_controller, SWT.NONE);
		label_14.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_14.setText("갯수 : ");
		
		final Text text_group_monster4 = new Text(composite_controller, SWT.BORDER);
		text_group_monster4.setText("1");
		text_group_monster4.setEnabled(false);
		text_group_monster4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite = new Composite(composite_controller, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		Button button_2 = new Button(composite, SWT.NONE);
		GridData gd_button_2 = new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1);
		gd_button_2.widthHint = 100;
		button_2.setLayoutData(gd_button_2);
		button_2.setText("이전");
		
		Button button_3 = new Button(composite, SWT.NONE);
		GridData gd_button_3 = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
		gd_button_3.widthHint = 100;
		button_3.setLayoutData(gd_button_3);
		button_3.setText("다음");
		
		// 이벤트 등록.
		combo_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 그룹 콤보박스 값에따라 연결된 위젯 활성화 여부 처리.
				boolean enabled = combo_1.getSelectionIndex() == 0;
				combo_group_monster1.setEnabled(enabled);
				combo_group_monster2.setEnabled(enabled);
				combo_group_monster3.setEnabled(enabled);
				combo_group_monster4.setEnabled(enabled);
				text_group_monster1.setEnabled(enabled);
				text_group_monster2.setEnabled(enabled);
				text_group_monster3.setEnabled(enabled);
				text_group_monster4.setEnabled(enabled);
				
			}
		});
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 이전
				step1();
			}
		});
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 다음
				step3(
						btnMonsterspawnlist.getSelection(),
						text_2.getText(),
						text_1.getText(),
						combo.getSelectionIndex()==0,
						Integer.valueOf(text_3.getText()),
						Integer.valueOf(text_4.getText()),
						Integer.valueOf(text_5.getText()),
						Integer.valueOf(text_6.getText()),
						Integer.valueOf(text_7.getText()),
						Integer.valueOf(text_8.getText()),
						combo_1.getSelectionIndex()==0,
						combo_group_monster1.getText(),
						Integer.valueOf(text_group_monster1.getText()),
						combo_group_monster2.getText(),
						Integer.valueOf(text_group_monster2.getText()),
						combo_group_monster3.getText(),
						Integer.valueOf(text_group_monster3.getText()),
						combo_group_monster4.getText(),
						Integer.valueOf(text_group_monster4.getText()),
						button.getSelection(),
						btnBoss.getSelection()
					);
			}
		});

		composite_controller.layout();
	}
	
	static private void step3(
				final boolean db, final String name, final String monster, final boolean random, final int count, final int loc_size, 
				final int x, final int y, final int map, final int re_spawn, final boolean groups, 
				final String monster_1, final int monster_1_count, final String monster_2, final int monster_2_count, 
				final String monster_3, final int monster_3_count, final String monster_4, final int monster_4_count, final boolean summon, final boolean boss
			){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(3);
		
		GridLayout gl_composite_controller = new GridLayout(4, false);
		gl_composite_controller.verticalSpacing = 2;
		gl_composite_controller.horizontalSpacing = 0;
		composite_controller.setLayout(gl_composite_controller);
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label label_15 = new Label(composite_controller, SWT.NONE);
		label_15.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_15.setText("이름 : ");
		
		Text text_9 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text_9.setText(name);
		
		Label label_16 = new Label(composite_controller, SWT.NONE);
		label_16.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_16.setText("몬스터 : ");
		
		Text text_10 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text_10.setText( monster );
		
		Label label_17 = new Label(composite_controller, SWT.NONE);
		label_17.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_17.setText("랜덤 : ");
		
		Text text_11 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_11.setText( String.valueOf(random) );
		
		Label label_18 = new Label(composite_controller, SWT.NONE);
		label_18.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_18.setText("갯수 : ");
		
		Text text_12 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_12.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_12.setText( String.valueOf(count) );
		
		Label label_19 = new Label(composite_controller, SWT.NONE);
		label_19.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_19.setText("범위 : ");
		
		Text text_13 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_13.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_13.setText( String.valueOf(loc_size) );
		
		Label lblX_1 = new Label(composite_controller, SWT.NONE);
		lblX_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblX_1.setText("x : ");
		
		Text text_15 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_15.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_15.setText( String.valueOf(x) );
		
		Label lblY_1 = new Label(composite_controller, SWT.NONE);
		lblY_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblY_1.setText("y : ");
		
		Text text_14 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_14.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_14.setText( String.valueOf(y) );
		
		Label lblMap_1 = new Label(composite_controller, SWT.NONE);
		lblMap_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMap_1.setText("map : ");
		
		Text text_16 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_16.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_16.setText( String.valueOf(map) );
		
		Label label_23 = new Label(composite_controller, SWT.NONE);
		label_23.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_23.setText("재스폰 : ");
		
		Text text_17 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_17.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_17.setText( String.valueOf(re_spawn) );
		
		Label label_24 = new Label(composite_controller, SWT.NONE);
		label_24.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_24.setText("그룹 : ");
		
		Text text_18 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_18.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_18.setText( String.valueOf(groups) );
		
		Label label_25 = new Label(composite_controller, SWT.NONE);
		label_25.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_25.setText("몬스터1 : ");
		
		Text text_19 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_19.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		if(monster_1!=null && monster_1.length()>0)
			text_19.setText( String.format("%s (%d)", monster_1, monster_1_count) );
		
		Label label_26 = new Label(composite_controller, SWT.NONE);
		label_26.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_26.setText("몬스터2 : ");
		
		Text text_20 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_20.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		if(monster_2!=null && monster_2.length()>0)
			text_20.setText( String.format("%s (%d)", monster_2, monster_2_count) );
		
		Label label_27 = new Label(composite_controller, SWT.NONE);
		label_27.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_27.setText("몬스터3 : ");
		
		Text text_21 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_21.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		if(monster_3!=null && monster_3.length()>0)
			text_21.setText( String.format("%s (%d)", monster_3, monster_3_count) );
		
		Label label_28 = new Label(composite_controller, SWT.NONE);
		label_28.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_28.setText("몬스터4 : ");
		
		Text text_22 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_22.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		if(monster_4!=null && monster_4.length()>0)
			text_22.setText( String.format("%s (%d)", monster_4, monster_4_count) );
		
		Label label_20 = new Label(composite_controller, SWT.NONE);
		label_20.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_20.setText("디비등록 : ");
		
		Text text_23 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_23.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text_23.setText( String.valueOf(db) );
		
		Label label = new Label(composite_controller, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("소환몬스터 : ");
		
		Text text_24 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_24.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text_24.setText( String.valueOf(summon) );
		
		Composite composite = new Composite(composite_controller, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		Button button = new Button(composite, SWT.NONE);
		GridData gd_button = new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1);
		gd_button.widthHint = 100;
		button.setLayoutData(gd_button);
		button.setText("이전");
		
		Button button_4 = new Button(composite, SWT.NONE);
		GridData gd_button_4 = new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1);
		gd_button_4.widthHint = 100;
		button_4.setLayoutData(gd_button_4);
		button_4.setText("다음");
		
		// 이벤트 등록
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 이전
				step2(monster);
			}
		});
		button_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 다음
				//step4(db, name, monster, random, count, loc_size, x, y, map, re_spawn, summon);
				step4(db, name, monster, random, count, loc_size, x, y, map, re_spawn, groups, monster_1, monster_1_count, monster_2, monster_2_count, monster_3, monster_3_count, monster_4, monster_4_count, summon, boss);
			}
		});
		
		composite_controller.layout();
	}
	
	static private void step4(
				final boolean db, final String name, final String monster, final boolean random, final int count, final int loc_size, 
				final int x, final int y, final int map, final int re_spawn, final boolean groups, 
				final String monster_1, final int monster_1_count, final String monster_2, final int monster_2_count, 
				final String monster_3, final int monster_3_count, final String monster_4, final int monster_4_count, final boolean summon, final boolean boss){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(4);
		
		GridLayout gl_composite_controller = new GridLayout(1, false);
		gl_composite_controller.verticalSpacing = 2;
		gl_composite_controller.horizontalSpacing = 0;
		composite_controller.setLayout(gl_composite_controller);
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		List list_1 = new List(composite_controller, SWT.BORDER | SWT.V_SCROLL);
		list_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Button button_5 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_5 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button_5.widthHint = 100;
		button_5.setLayoutData(gd_button_5);
		button_5.setText("완료");
		
		// 이벤트 등록
		button_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 완료
				shell.dispose();
			}
		});
		
		// 스폰객체 생성.
		MonsterSpawnlist ms = new MonsterSpawnlist();
		ms.setName(name);
		ms.setMonster( MonsterDatabase.find(monster) );
		ms.setRandom(random);
		ms.setCount(count);
		ms.setLocSize(loc_size);
		ms.setX(x);
		ms.setY(y);
		ms.getMap().add(map);
		// 소환객체가 아닐경우에만 리스폰 타임값 설정. 설정안할경우 재스폰 안하게 되며, 메모리 제거처리함.
		if(!summon) {
			ms.setReSpawn(re_spawn * 1000);
			ms.setReSpawnMax(re_spawn * 1000);
		}
		ms.setGroup(groups);
		if(ms.isGroup()){
			Monster g1 = MonsterDatabase.find(monster_1);
			Monster g2 = MonsterDatabase.find(monster_2);
			Monster g3 = MonsterDatabase.find(monster_3);
			Monster g4 = MonsterDatabase.find(monster_4);
			if(g1 != null)
				ms.getListGroup().add(new MonsterGroup(g1, monster_1_count));
			if(g2 != null)
				ms.getListGroup().add(new MonsterGroup(g2, monster_2_count));
			if(g3 != null)
				ms.getListGroup().add(new MonsterGroup(g3, monster_3_count));
			if(g4 != null)
				ms.getListGroup().add(new MonsterGroup(g4, monster_4_count));
		}
		list_1.add("MonsterSpawnlist 객체 생성.");
		// 월드에 몬스터 스폰 등록 처리.
		MonsterSpawnlistDatabase.toSpawnMonster(ms, null);
		list_1.add("월드 스폰 완료.");
		// 디비 등록 처리.
		if(db){
			MonsterSpawnlistDatabase.insert(con, name, monster, random, count, loc_size, x, y, map, re_spawn, groups, monster_1, monster_1_count, monster_2, monster_2_count, monster_3, monster_3_count, monster_4, monster_4_count);
			list_1.add("monster_spawnlist 테이블에 등록 완료.");
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
		label_step4.setForeground(step==4 ? SWTResourceManager.getColor(SWT.COLOR_DARK_RED) : SWTResourceManager.getColor(SWT.COLOR_BLACK));
		
		label_step1.setFont(step==1 ? select : normal);
		label_step2.setFont(step==2 ? select : normal);
		label_step3.setFont(step==3 ? select : normal);
		label_step4.setFont(step==4 ? select : normal);
	}
	
}
