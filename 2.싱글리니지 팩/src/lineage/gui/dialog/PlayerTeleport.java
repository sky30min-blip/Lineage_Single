package lineage.gui.dialog;

import java.sql.Connection;
import java.util.ArrayList;

import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.gui.GuiMain;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class PlayerTeleport {

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
	// 디비에 등록된 사용자 이름 정보 추출후 담을 변수.
	static private java.util.List<String> list_search_name;
	// 이동하게될 좌표 정보
	static private int x;
	static private int y;
	static private int map;
	//
	static private Connection con;
	
	static {
		normal = SWTResourceManager.getFont("맑은 고딕", 9, SWT.NORMAL);
		select = SWTResourceManager.getFont("맑은 고딕", 9, SWT.BOLD);
		title = "사용자 텔레포트";
		list_search_name = new ArrayList<String>();
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	static public void open(int x, int y, int map) {
		try {
			con = DatabaseConnection.getLineage();
		} catch (Exception e) { }
		
		PlayerTeleport.x = x;
		PlayerTeleport.y = y;
		PlayerTeleport.map = map;
		
		shell = new Shell(GuiMain.shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		shell.setSize(400, 300);
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
		label_step1.setText("사용자 선택");
		
		label_step2 = new Label(composite_status, SWT.NONE);
		label_step2.setText("최종 확인");
		
		label_step3 = new Label(composite_status, SWT.NONE);
		label_step3.setText("완료");
		
		composite_controller = new Composite(shell, SWT.NONE);
		
		step1();
		
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
	 * 사용자 선택 스탭.
	 */
	static private void step1(){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();

		selectStep(1);
		
		composite_controller.setLayout(new GridLayout(1, false));
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite = new Composite(composite_controller, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(2, false));
		
		final Button button = new Button(composite, SWT.RADIO);
		button.setText("접속한 사용자");
		
		final Button button_1 = new Button(composite, SWT.RADIO);
		button_1.setText("전체 사용자");
		
		Composite composite_1 = new Composite(composite_controller, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		final Text text = new Text(composite_1, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button button_2 = new Button(composite_1, SWT.NONE);
		button_2.setText("검색");
		
		final List list = new List(composite_1, SWT.BORDER | SWT.V_SCROLL);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Button button_3 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_3 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button_3.widthHint = 100;
		button_3.setLayoutData(gd_button_3);
		button_3.setText("다음");
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				step2( list.getSelection() );
			}
		});
		
		// 이벤트 등록.
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!button.getSelection())
					return;
				// 검색
				text.setText("");
				toSearchPlayer(text, list, button_1.getSelection());
				
			}
		});
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!button_1.getSelection())
					return;
				// 검색
				text.setText("");
				toSearchPlayer(text, list, button_1.getSelection());
			}
		});
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode==13 || e.keyCode==16777296)
					// 검색
					toSearchPlayer(text, list, button_1.getSelection());
			}
		});
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 검색
				toSearchPlayer(text, list, button_1.getSelection());
			}
		});
		
		composite_controller.layout();
	}
	
	static private void step2(final String[] select_name){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();

		selectStep(2);
		
		composite_controller.setLayout(new GridLayout(2, false));
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label label_1 = new Label(composite_controller, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		label_1.setText("선택된 사용자 목록");
		
		List list_1 = new List(composite_controller, SWT.BORDER | SWT.V_SCROLL);
		list_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		list_1.setItems( select_name );
		
		Composite composite_2 = new Composite(composite_controller, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.verticalSpacing = 0;
		gl_composite_2.horizontalSpacing = 0;
		gl_composite_2.marginHeight = 0;
		gl_composite_2.marginWidth = 0;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		Label label_2 = new Label(composite_2, SWT.NONE);
		label_2.setText("이동될 좌표 정보 : ");
		
		Label label_3 = new Label(composite_2, SWT.NONE);
		label_3.setText( String.format("%d %d %d", x, y, map) );
		
		Button button_4 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_4 = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_button_4.widthHint = 100;
		button_4.setLayoutData(gd_button_4);
		button_4.setText("이전");
		
		// 이벤트 등록.
		button_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				step1();
			}
		});
		
		Button button_5 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_5 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_button_5.widthHint = 100;
		button_5.setLayoutData(gd_button_5);
		button_5.setText("다음");
		button_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(select_name==null || select_name.length<=0)
					GuiMain.toMessageBox(title, "선택된 사용자가 없습니다.");
				else
					step3(select_name);
			}
		});
		
		composite_controller.layout();
	}
	
	static private void step3(final String[] select_name){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();

		selectStep(3);
		
		composite_controller.setLayout(new GridLayout(1, false));
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		List list_2 = new List(composite_controller, SWT.BORDER | SWT.V_SCROLL);
		list_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Button button_6 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_6 = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1);
		gd_button_6.widthHint = 100;
		button_6.setLayoutData(gd_button_6);
		button_6.setText("완료");
		
		// 좌표 변경 처리.
		for(String name : select_name){
			list_2.add( String.format("%s 좌표변경 완료.", name) );
			PcInstance pc = World.findPc(name);
			if(pc == null){
				CharactersDatabase.updateLocation(con, name, x, y, map);
			}else{
				pc.toPotal(x, y, map);
			}
		}
		
		// 이벤트 등록
		button_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		composite_controller.layout();
	}
	
	/**
	 * 사용자 검색
	 * @param name
	 */
	static private void toSearchPlayer(Text text, List list, boolean all){
		String name = text.getText().toLowerCase();
		
		// 이전 기록 제거
		list.removeAll();
		
		// 검색명이 없을경우 전체 표현.
		if(name==null || name.length()<=0){
			if(all){
				// 디비 케릭이름 추출.
				list_search_name.clear();
				CharactersDatabase.getNameAllList(con, list_search_name);
				// 처리
				if(list_search_name.size() > 0){
					// 월드 케릭이름 추출.
					for(String s : list_search_name)
						list.add( s );
				}else{
					GuiMain.toMessageBox(title, "등록된 사용자가 없습니다.");
				}
			}else{
				// 처리
				if(World.getPcSize() > 0){
					// 월드 케릭이름 추출.
					for(PcInstance pc : World.getPcList())
						list.add( pc.getName() );
				}else{
					GuiMain.toMessageBox(title, "접속중인 사용자가 없습니다.");
				}
			}
			
			return;
		}
		
		// 검색.
		if(all){
			// 디비 케릭이름 추출.
			list_search_name.clear();
			CharactersDatabase.getNameAllList(con, list_search_name);
			// 월드 케릭이름 추출.
			for(String s : list_search_name){
				int pos = s.toLowerCase().indexOf(name);
				if(pos >= 0)
					list.add( s );
			}
		}else{
			for(PcInstance pc : World.getPcList()){
				int pos = pc.getName().toLowerCase().indexOf(name);
				if(pos >= 0)
					list.add( pc.getName() );
			}
		}
		
		// 등록된게 없을경우 안내 멘트.
		if(list.getItemCount() <= 0)
			GuiMain.toMessageBox(title, "일치하는 사용자가 없습니다.");
		
		// 포커스.
		text.setFocus();
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
