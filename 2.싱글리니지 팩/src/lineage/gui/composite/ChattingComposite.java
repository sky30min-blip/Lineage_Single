package lineage.gui.composite;

import java.util.ArrayList;
import java.util.List;

import lineage.Main;
import lineage.database.ServerReloadDatabase;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class ChattingComposite extends Composite {
	
	private Text text;
	private StyledText chatting;
	private Button normal;
	private Button party;
	private Button clan;
	private Button global;
	private Button whisper;
	private Button trade;
	private org.eclipse.swt.graphics.Color color_global = SWTResourceManager.getColor(173, 235, 239);
	private org.eclipse.swt.graphics.Color color_whisper = SWTResourceManager.getColor(255, 203, 206);
	private org.eclipse.swt.graphics.Color color_whisper_manager = SWTResourceManager.getColor(246, 255, 0);
	private org.eclipse.swt.graphics.Color color_normal = SWTResourceManager.getColor(255, 251, 255);
	private org.eclipse.swt.graphics.Color color_trade = SWTResourceManager.getColor(189, 138, 198);
	private org.eclipse.swt.graphics.Color color_clan = SWTResourceManager.getColor(198, 239, 181);
	private org.eclipse.swt.graphics.Color color_party = SWTResourceManager.getColor(205, 214, 255);
	private org.eclipse.swt.graphics.Color color_message = SWTResourceManager.getColor(253, 248, 180);
	private int chatting_now_pos;
	private List<String> list_whisper = new ArrayList<String>();
	private int pos_whisper;
	private boolean bool_chatting_whisper;
	private Button scroll;
	private Button global_lock;
	private Button all_lock;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ChattingComposite(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(45, 45, 45));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		Group group = new Group(this, SWT.NONE);
		group.setText("전체 채팅");
		GridLayout gl_group = new GridLayout(10, false);
		gl_group.verticalSpacing = 0;
		gl_group.horizontalSpacing = 0;
		gl_group.marginHeight = 0;
		gl_group.marginWidth = 0;
		group.setLayout(gl_group);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		scroll = new Button(group, SWT.CHECK);
		GridData gd_scroll = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_scroll.horizontalIndent = 10;
		scroll.setLayoutData(gd_scroll);
		scroll.setText("스크롤락");
		
		all_lock = new Button(group, SWT.CHECK);
		all_lock.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Lineage.chatting_all_lock = all_lock.getSelection();
			}
		});
		all_lock.setText("모든채팅락");
		
		global_lock = new Button(group, SWT.CHECK);
		global_lock.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		global_lock.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Lineage.chatting_global_lock = global_lock.getSelection();
			}
		});
		global_lock.setText("전체채팅락");
		
		global = new Button(group, SWT.CHECK);
		global.setSelection(true);
		global.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		global.setText("전체");
		
		trade = new Button(group, SWT.CHECK);
		trade.setSelection(true);
		trade.setText("장사");
		
		clan = new Button(group, SWT.CHECK);
		clan.setSelection(true);
		clan.setText("혈맹");
		
		whisper = new Button(group, SWT.CHECK);
		whisper.setSelection(true);
		whisper.setText("귓말");
		
		party = new Button(group, SWT.CHECK);
		party.setSelection(true);
		party.setText("파티");
		
		normal = new Button(group, SWT.CHECK);
		normal.setSelection(true);
		normal.setText("일반");
		
		chatting = new StyledText(group, SWT.NONE | SWT.V_SCROLL);
		chatting.setBackground(SWTResourceManager.getColor(45, 45, 45));
		chatting.setDoubleClickEnabled(false);
		chatting.setEditable(false);
		chatting.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 10, 1));
		
		Menu menu = new Menu(chatting);
		chatting.setMenu(menu);
		
		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				chatting.setText("");
				chatting_now_pos = 0;
			}
		});
		menuItem.setText("화면 청소");
		
		text = new Text(this, SWT.NONE);
		text.setBackground(SWTResourceManager.getColor(45, 45, 45));
		text.setForeground(color_normal);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				switch(e.keyCode){
					case 13:	// 엔터
						// 전체 채팅.
						if(Main.running){
							String msg = text.getText();
							if(msg.startsWith("\"")){
								// 귓속말 처리.
								try {
									String name = msg.substring(1, msg.indexOf(" "));
									msg = msg.substring(name.length()+2);
									// 귓속말 목록에 추가.
									if(!list_whisper.contains(name))
										list_whisper.add(name);
									// 귓속말 전송
									ChattingController.toWhisper(null, name, msg);
								} catch (Exception e2) {

								}
							}else{
								ChattingController.toChatting(null, text.getText(), Lineage.CHATTING_MODE_GLOBAL);
							}
						}
						text.setText("");
						break;
					case 39:		// '
					case 131072:	// "
						if(bool_chatting_whisper){
							bool_chatting_whisper = false;
							// 마지막 귓말 상대로 위치 잡기.
							pos_whisper = list_whisper.size()-1;
							// 귓말 준비 문자로 변경
							text.setText( String.format("\"%s ", list_whisper.get(pos_whisper)) );
							// 커서위치를 문자열 뒤로 이동.
							text.setSelection(text.getText().length());
						}
						break;
					case 16777217:	// up
					case 16777218:	// down
						// 커서위치를 문자열 뒤로 이동.
						text.setSelection(text.getText().length());
						break;
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.keyCode){
					case 39:	// '
						bool_chatting_whisper = e.stateMask==SWT.SHIFT && text.getText().length()==0 && list_whisper.size()>0;
						break;
					case 16777217:	// up
					case 16777218:	// down
						if(text.getText().startsWith("\"") && list_whisper.size()>0){
							if(e.keyCode == 16777217){
								if(--pos_whisper<0){
									pos_whisper = -1;
									text.setText("\"");
									return;
								}
							}else{
								if(++pos_whisper>=list_whisper.size()){
									pos_whisper = list_whisper.size();
									text.setText("\"");
									return;
								}
							}
							text.setText( String.format("\"%s ", list_whisper.get(pos_whisper)) );
							// 커서위치를 문자열 뒤로 이동.
							text.setSelection(text.getText().length());
						}
						break;
				}
			}
		});
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.horizontalIndent = 10;
		text.setLayoutData(gd_text);
		
		Button button = new Button(this, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 전체 채팅.
				if(Main.running)
					ChattingController.toChatting(null, text.getText(), Lineage.CHATTING_MODE_GLOBAL);
				text.setText("");
			}
		});
		GridData gd_button = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_button.horizontalIndent = 10;
		gd_button.heightHint = 40;
		gd_button.widthHint = 100;
		button.setLayoutData(gd_button);
		button.setText("보내기");

	}

	/**
	 * 일반 채팅 처리.
	 * @param o
	 * @param msg
	 */
	public void toNormal(object o, String msg){
		if(normal.getSelection()){
			msg = String.format("\r\n%s : %s", o.getName(), msg);
			chatting.append( msg );
			if(!scroll.getSelection())
				chatting.setTopIndex(chatting.getVerticalBar().getMaximum());
			// 색상 입히기.
			chatting.setStyleRange( getHighlightStyle(chatting_now_pos, msg.length(), color_normal, SWT.BOLD) );
			chatting_now_pos += msg.length();
		}
	}
	/**
	 * 전체채팅 처리.
	 * @param o
	 * @param msg
	 */
	public void toGlobal(object o, String msg){
		if(global.getSelection()){
			msg = String.format("\r\n[%s] %s", o==null ? "******" : o.getName(), msg);
			chatting.append( msg );
			if(!scroll.getSelection())
				chatting.setTopIndex(chatting.getVerticalBar().getMaximum());
			// 색상 입히기.
			chatting.setStyleRange( getHighlightStyle(chatting_now_pos, msg.length(), color_global, SWT.BOLD) );
			chatting_now_pos += msg.length();
		}
	}
	
	/**
	 * 장사채팅 처리.
	 * @param o
	 * @param msg
	 */
	public void toTrade(object o, String msg){
		if(trade.getSelection()){
			msg = String.format("\r\n[%s] %s", o==null ? "메티스" : o.getName(), msg);
			chatting.append( msg );
			if(!scroll.getSelection())
				chatting.setTopIndex(chatting.getVerticalBar().getMaximum());
			// 색상 입히기.
			chatting.setStyleRange( getHighlightStyle(chatting_now_pos, msg.length(), color_trade, SWT.BOLD) );
			chatting_now_pos += msg.length();
		}
	}
	
	/**
	 * 혈맹채팅 처리.
	 * @param o
	 * @param msg
	 */
	public void toClan(object o, String msg){
		if(clan.getSelection()){
			msg = String.format("\r\n{%s}{%s} %s", o.getClanName(), o.getName(), msg);
			chatting.append( msg );
			if(!scroll.getSelection())
				chatting.setTopIndex(chatting.getVerticalBar().getMaximum());
			// 색상 입히기.
			chatting.setStyleRange( getHighlightStyle(chatting_now_pos, msg.length(), color_clan, SWT.BOLD) );
			chatting_now_pos += msg.length();
		}
	}
	
	/**
	 * 파티채팅 처리.
	 * @param o
	 * @param msg
	 */
	public void toParty(object o, String msg){
		if(party.getSelection()){
			msg = String.format("\r\n(%s) %s", o.getName(), msg);
			chatting.append( msg );
			if(!scroll.getSelection())
				chatting.setTopIndex(chatting.getVerticalBar().getMaximum());
			// 색상 입히기.
			chatting.setStyleRange( getHighlightStyle(chatting_now_pos, msg.length(), color_party, SWT.BOLD) );
			chatting_now_pos += msg.length();
		}
	}
	
	/**
	 * 귓속말채팅 처리.
	 * @param o
	 * @param name
	 * @param msg
	 */
	public void toWhisper(object o, String name, String msg){
		if(whisper.getSelection()){
			msg = String.format("\r\n(%s)->(%s) %s", o==null ? ServerReloadDatabase.manager_character_id : o.getName(), name, msg);
			chatting.append( msg );
			if(!scroll.getSelection())
				chatting.setTopIndex(chatting.getVerticalBar().getMaximum());
			
			// 색상 입히기.
			if (name.equals(ServerReloadDatabase.manager_character_id))
				chatting.setStyleRange(getHighlightStyle(chatting_now_pos, msg.length(), color_whisper_manager, SWT.BOLD));
			else
				chatting.setStyleRange(getHighlightStyle(chatting_now_pos, msg.length(), color_whisper, SWT.BOLD));
			
			chatting_now_pos += msg.length();
		}
	}
	
	/**
	 * 시스템 메세지
	 * @param msg
	 */
	public void toMessage(String msg){
		msg = String.format("\r\n%s", msg);
		chatting.append( msg );
		if(!scroll.getSelection())
			chatting.setTopIndex(chatting.getVerticalBar().getMaximum());
		// 색상 입히기.
		chatting.setStyleRange( getHighlightStyle(chatting_now_pos, msg.length(), color_message, SWT.BOLD) );
		chatting_now_pos += msg.length();
	}
	
	private StyleRange getHighlightStyle(int startOffset, int length, org.eclipse.swt.graphics.Color color, int font_style) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = startOffset;
		styleRange.length = length;
		styleRange.foreground = color;
		styleRange.fontStyle = font_style;
		return styleRange;
	}

}
