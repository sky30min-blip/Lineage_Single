package lineage.bean.event;

import lineage.gui.GuiMain;

public class GuiConsole implements Event {
	
	static public enum MODE {
		print,
		println,
	}

	private MODE mode;
	private String msg;
	private Object o;
	
	static synchronized public Event clone(Event e, MODE mode, String msg){
		if(e == null)
			e = new GuiConsole();
		((GuiConsole)e).setMode(mode);
		((GuiConsole)e).setMsg(msg);
		return e;
	}
	
	static synchronized public Event clone(Event e, MODE mode, Object o){
		if(e == null)
			e = new GuiConsole();
		((GuiConsole)e).setMode(mode);
		((GuiConsole)e).setObject(o);
		return e;
	}
	
	static synchronized public Event clone(Event e, MODE mode, String format, Object ...args){
		if(e == null)
			e = new GuiConsole();
		((GuiConsole)e).setMode(mode);
		((GuiConsole)e).setMsg( String.format(format, args) );
		return e;
	}
	
	public void setMode(MODE mode){
		this.mode = mode;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void setObject(Object o){
		this.o = o;
	}

	@Override
	public void init() {
		final String msg = this.msg;
		final Object o = this.o;
		final MODE mode = this.mode;
		GuiMain.display.asyncExec(new Runnable() {
			@Override
			public void run() {
				switch(mode){
					case print:
						if(msg != null)
							GuiMain.getConsoleComposite().print(msg);
						if(o != null)
							GuiMain.getConsoleComposite().print(o);
						break;
					case println:
						if(msg != null)
							GuiMain.getConsoleComposite().println(msg);
						if(o != null)
							GuiMain.getConsoleComposite().println(o);
						break;
				}
			}
		});
	}
	
	@Override
	public void close() {
		msg = null;
		o = null;
	}

}
