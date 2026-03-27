package lineage.gui.dialog;

import java.sql.Connection;

import lineage.bean.database.Npc;
import lineage.database.DatabaseConnection;
import lineage.database.NpcDatabase;
import lineage.database.NpcSpawnlistDatabase;
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

public class NpcSpawn {

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
		title = "엔피시 스폰";
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
		
		NpcSpawn.x = x;
		NpcSpawn.y = y;
		NpcSpawn.map = map;

		shell = new Shell(GuiMain.shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		shell.setSize(600,500);
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
		label_step1.setText("엔피시 선택");
		
		label_step2 = new Label(composite_status, SWT.NONE);
		label_step2.setText("스폰정보 입력");
		
		label_step3 = new Label(composite_status, SWT.NONE);
		label_step3.setText("최종 확인");
		
		label_step4 = new Label(composite_status, SWT.NONE);
		label_step4.setText("완료");
		
		composite_controller = new Composite(shell, SWT.NONE);
		
		step1();
//		step2("1234");
//		step3(title, title, map, map, map, map, map, title, true);
//		step4(title, title, map, map, map, map, map, title, true);
		
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
					toSearchNpc(text, list);
			}
		});
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 검색
				toSearchNpc(text, list);
			}
		});
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 선택된 몬스터 없다면 무시.
				if(list.getSelectionCount() <= 0){
					GuiMain.toMessageBox(title, "엔피시를 선택하여 주십시오.");
					return;
				}
				// 다음 스탭으로 이동.
				step2( list.getSelection()[0] );
			}
		});
		
		// 메모리에 로드된 전체 몬스터 이름 등록.
		toSearchNpc(text, list);
		
		composite_controller.layout();
	}

	/**
	 * 엔피시 검색.
	 * @param text
	 * @param list
	 */
	static private void toSearchNpc(Text text, List list){
		String name = text.getText().toLowerCase();
		
		// 이전 기록 제거
		list.removeAll();
		
		// 검색명이 없을경우 전체 표현.
		if(name==null || name.length()<=0){
			if(NpcDatabase.getList().size() > 0){
				// 월드 케릭이름 추출.
				for(Npc n : NpcDatabase.getList())
					list.add( n.getName() );
			}else{
				GuiMain.toMessageBox(title, "엔피시가 존재하지 않습니다.");
			}
			return;
		}
		
		// 검색.
		for(Npc n : NpcDatabase.getList()){
			int pos = n.getName().toLowerCase().indexOf(name);
			if(pos >= 0)
				list.add( n.getName() );
		}
		
		// 등록된게 없을경우 안내 멘트.
		if(list.getItemCount() <= 0)
			GuiMain.toMessageBox(title, "일치하는 엔피시가 없습니다.");
		
		// 포커스.
		text.setFocus();
	}
	
	static private void step2(final String npc){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(2);
		
		GridLayout gl_composite_controller = new GridLayout(4, false);
		gl_composite_controller.verticalSpacing = 2;
		gl_composite_controller.horizontalSpacing = 0;
		composite_controller.setLayout(gl_composite_controller);
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		final Button btnNpcspawnlist = new Button(composite_controller, SWT.CHECK);
		btnNpcspawnlist.setSelection(true);
		btnNpcspawnlist.setText("npc_spawnlist 테이블에 등록");
		GridData gd_btnNpcspawnlist = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gd_btnNpcspawnlist.horizontalIndent = 32;
		btnNpcspawnlist.setLayoutData(gd_btnNpcspawnlist);
		
		Label label = new Label(composite_controller, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("이름 : ");
		
		final Text text = new Text(composite_controller, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text.setText( String.format("[매니저 스폰툴] %s - %d", npc, NpcSpawnlistDatabase.selectCount(con)) );
		
		Label label_1 = new Label(composite_controller, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("엔피시 : ");
		
		Combo text_3 = new Combo(composite_controller, SWT.READ_ONLY);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		int idx = 0;
		int pos = 0;
		for(Npc n : NpcDatabase.getList()){
			if(npc.equalsIgnoreCase(n.getName()))
				pos = idx;
			text_3.add(n.getName(), idx++);
		}
		text_3.select(pos);
		
		Label label_8 = new Label(composite_controller, SWT.NONE);
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("호칭 : ");
		
		final Text text_2 = new Text(composite_controller, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label label_6 = new Label(composite_controller, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("재스폰 : ");
		
		final Text text_6 = new Text(composite_controller, SWT.BORDER);
		text_6.setText("60");
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblX = new Label(composite_controller, SWT.NONE);
		lblX.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblX.setText("x : ");
		
		final Text text_4 = new Text(composite_controller, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_4.setText( String.valueOf(x) );
		
		Label lblY = new Label(composite_controller, SWT.NONE);
		lblY.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblY.setText("y : ");
		
		final Text text_1 = new Text(composite_controller, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_1.setText( String.valueOf(y) );
		
		Label lblMap = new Label(composite_controller, SWT.NONE);
		lblMap.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMap.setText("map : ");
		
		final Text text_5 = new Text(composite_controller, SWT.BORDER);
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_5.setText( String.valueOf(map) );
		
		Label lblHeading = new Label(composite_controller, SWT.NONE);
		lblHeading.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHeading.setText("heading : ");
		
		final Text text_8 = new Text(composite_controller, SWT.BORDER);
		text_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_8.setText( "4" );
		
		Composite composite = new Composite(composite_controller, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		Button button = new Button(composite, SWT.NONE);
		GridData gd_button = new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1);
		gd_button.widthHint = 100;
		button.setLayoutData(gd_button);
		button.setText("이전");
		
		Button button_1 = new Button(composite, SWT.NONE);
		GridData gd_button_1 = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
		gd_button_1.widthHint = 100;
		button_1.setLayoutData(gd_button_1);
		button_1.setText("다음");

		// 이벤트 등록
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 이전
				step1();
			}
		});
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 다음
				step3(
						text.getText(), 
						npc,
						Integer.valueOf(text_4.getText()),
						Integer.valueOf(text_1.getText()),
						Integer.valueOf(text_5.getText()),
						Integer.valueOf(text_8.getText()),
						Integer.valueOf(text_6.getText()),
						text_2.getText(),
						btnNpcspawnlist.getSelection()
					);
			}
		});
		
		composite_controller.layout();
	}
	
	static private void step3(
				final String name, final String npcName, final int locX, final int locY, final int locMap, 
				final int heading, final int respawn, final String title, final boolean db
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
		
		Label label = new Label(composite_controller, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("이름 : ");
		
		Text text_7 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text_7.setText( name );
		
		Label label_2 = new Label(composite_controller, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("엔피시 : ");
		
		Text text_9 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text_9.setText( npcName );
		
		Label label_9 = new Label(composite_controller, SWT.NONE);
		label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_9.setText("호칭 : ");
		
		Text text_10 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text_10.setText( title );
		
		Label label_10 = new Label(composite_controller, SWT.NONE);
		label_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_10.setText("리스폰 : ");
		
		Text text_11 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text_11.setText( String.valueOf(respawn) );
		
		Label lblX_1 = new Label(composite_controller, SWT.NONE);
		lblX_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblX_1.setText("x : ");
		
		Text text_12 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_12.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_12.setText( String.valueOf(locX) );
		
		Label lblY_1 = new Label(composite_controller, SWT.NONE);
		lblY_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblY_1.setText("y : ");
		
		Text text_14 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_14.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_14.setText( String.valueOf(locY) );
		
		Label lblMap_1 = new Label(composite_controller, SWT.NONE);
		lblMap_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMap_1.setText("map : ");
		
		Text text_13 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_13.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_13.setText( String.valueOf(locMap) );
		
		Label lblHeading_1 = new Label(composite_controller, SWT.NONE);
		lblHeading_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHeading_1.setText("heading : ");
		
		Text text_15 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_15.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_15.setText( String.valueOf(heading) );
		
		Label label_1 = new Label(composite_controller, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("디비등록 : ");
		
		Text text_16 = new Text(composite_controller, SWT.BORDER | SWT.READ_ONLY);
		text_16.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text_16.setText( String.valueOf(db) );
		
		Composite composite_1 = new Composite(composite_controller, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		Button button_2 = new Button(composite_1, SWT.NONE);
		GridData gd_button_2 = new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1);
		gd_button_2.widthHint = 100;
		button_2.setLayoutData(gd_button_2);
		button_2.setText("이전");
		
		Button button_3 = new Button(composite_1, SWT.NONE);
		GridData gd_button_3 = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
		gd_button_3.widthHint = 100;
		button_3.setLayoutData(gd_button_3);
		button_3.setText("다음");
		
		// 이벤트 등록.
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 이전
				step2(npcName);
			}
		});
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 다음
				step4(name, npcName, locX, locY, locMap, heading, respawn, title, db);
			}
		});
		
		composite_controller.layout();
	}
	
	static private void step4(
				final String name, final String npcName, final int locX, final int locY, final int locMap, 
				final int heading, final int respawn, final String title, final boolean db
			){
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
		
		Button button_4 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_4 = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_button_4.widthHint = 100;
		button_4.setLayoutData(gd_button_4);
		button_4.setText("완료");
		
		// 이벤트 등록
		button_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 완료
				shell.dispose();
			}
		});
		
		// 스폰
		NpcSpawnlistDatabase.toSpawnNpc(name, npcName, title, locX, locY, locMap, heading, respawn);
		list_1.add("엔피시 스폰 완료.");
		// 디비 등록.
		if(db){
			NpcSpawnlistDatabase.insert(con, name, npcName, locX, locY, locMap, heading, respawn, title);
			list_1.add("npc_spawnlist 테이블에 등록 완료.");
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
