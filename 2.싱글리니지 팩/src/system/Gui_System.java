package system;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class Gui_System {
	
	static public long getUsedMemoryMB() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
	}
	
	static public long getTotalMemoryMB() {
		return (Runtime.getRuntime().maxMemory()) / 1024L / 1024L;
	}
	
	static public double getUseCpu() {
		return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getSystemCpuLoad();
	}
	
	static public int getThread() {
		return Thread.activeCount();
	}
}
