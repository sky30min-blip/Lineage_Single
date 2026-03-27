package lineage.world.object.npc.background;

import lineage.world.object.object;

public class Racer extends object {

	public boolean finish;		// 
	public boolean Lucky;		// 
	public int Status;			// 
	public double Theory;		// 
	public int idx;				// 고유 순번
	public int countting;		// 표를 구매한 사람들의 갯수 배당처리를 위해 필요.
	public int num;				// 레이서 라인 번호
	// 코너링 부분에서 aStar를 발동해야함.
	// 처음부터 하기엔 비효율적이므로 스탭별로 aStar를 발동할지 여부를 사용하기위해.
	public int step;
	public long runDelayTime;
	
	public void clean(){
		finish = false;
		Lucky = false;
		Status = 0;
		Theory = 0;
		countting = 0;
		step = 0;
		runDelayTime = 0;
	}
	
}
