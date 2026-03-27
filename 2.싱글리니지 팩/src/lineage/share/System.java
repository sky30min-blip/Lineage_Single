package lineage.share;

import lineage.bean.event.GuiConsole;
import lineage.thread.GuiThread;

public final class System {

	static public void print(final String msg) {
		if (Common.system_config_console) {
			java.lang.System.out.print(msg);
		} else {
			GuiThread.append(GuiConsole.clone(GuiThread.getPool(GuiConsole.class), GuiConsole.MODE.print, msg));
		}
		// 로그 기록.
		if (Log.isLog(null))
			Log.appendSpRunning(msg);
	}

	static public void println(final String msg) {
		if (Common.system_config_console) {
			java.lang.System.out.println(msg);
		} else {
			GuiThread.append(GuiConsole.clone(GuiThread.getPool(GuiConsole.class), GuiConsole.MODE.println, msg));
		}
		// 로그 기록.
		if (Log.isLog(null))
			Log.appendSpRunning(msg);
	}

	static public void println(final Object o) {
		if (Common.system_config_console) {
			java.lang.System.out.println(o);
		} else {
			GuiThread.append(GuiConsole.clone(GuiThread.getPool(GuiConsole.class), GuiConsole.MODE.println, o));
		}
		// 로그 기록.
		if (Log.isLog(null))
			Log.appendSpRunning(o.toString());
	}

	static public void printf(String format, final Object... args) {
		if (Common.system_config_console) {
			java.lang.System.out.printf(format, args);
		} else {
			GuiThread.append(GuiConsole.clone(GuiThread.getPool(GuiConsole.class), GuiConsole.MODE.print, format, args));
		}
		// 로그 기록.
		if (Log.isLog(null))
			Log.appendSpRunning(String.format(format, args));
	}

	static public long nanoTime() {
		return java.lang.System.nanoTime();
	}

	static public long currentTimeMillis() {
		return java.lang.System.currentTimeMillis();
	}
}
