package lineage.bean.database;

public class TeamBattleTime {
	int hour;
	int min;
	
	public TeamBattleTime(int hour, int min) {
		this.hour = hour;
		this.min = min;
	}
	
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
}
