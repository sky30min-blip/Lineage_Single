package lineage.share;

public final class TimeLine {

	static private long time;
	
	static public void startNano(){
		time = System.nanoTime();
	}
	
	static public long endNano(){
		return System.nanoTime()-time;
	}
	
	static public void start(String msg){
		time = System.currentTimeMillis();
		if(msg!=null)
			lineage.share.System.print(msg);
	}
	
	static public void end(){
		lineage.share.System.printf("%dms\r\n", System.currentTimeMillis()-time);
	}
	
}
