package lineage.bean.database;

public class ServerOpcodes {
	
	static public enum TYPE {
		Client,
		Server
	};
	
	private int uid;
	private TYPE type;
	private String name;
	private String name_op;
	private int old_op;
	private int now_op;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public TYPE getType() {
		return type;
	}
	public void setType(TYPE type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameOp() {
		return name_op;
	}
	public void setNameOp(String name_op) {
		this.name_op = name_op;
	}
	public int getOldOp() {
		return old_op;
	}
	public void setOldOp(int old_op) {
		this.old_op = old_op;
	}
	public int getNowOp() {
		return now_op;
	}
	public void setNowOp(int now_op) {
		this.now_op = now_op;
	}
}
