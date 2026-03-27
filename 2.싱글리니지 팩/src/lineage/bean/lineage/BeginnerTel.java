package lineage.bean.lineage;

public class BeginnerTel{	
	
	private String _getlocation;
	private int _locx;
	private int _locy;
	private int _locmap;

	public String getLocation(){
		return _getlocation;
	}
	
	public void setLocation(String a){
		this._getlocation = a;
	}
	
	public int getLocX(){
		return _locx;
	}
	
	public void setLocX(int i){
		this._locx = i;
	}
	
	public int getLocY(){
		return _locy;
	}
	
	public void setLocY(int i){
		this._locy = i;
	}
	public int getLocMap(){
		return _locmap;
	}
	
	public void setLocMap(int i){
		this._locmap = i;
	}
	
	private int random;
	public int getRandom() {
		return random;
	}
	public void setRandom(int i) {
		this.random = i;
	}


}  //-- end class