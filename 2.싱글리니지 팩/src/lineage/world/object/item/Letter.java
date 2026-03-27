package lineage.world.object.item;

import java.sql.Connection;

import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_LetterRead;
import lineage.world.controller.LetterController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Letter extends ItemInstance {

	private int uid;
	private String from;
	private String to;
	private String subject;
	private String memo;
	private long date;
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Letter();
		return item;
	}
	
	@Override
	public void close(){
		super.close();
		
		date = uid = 0;
		from = to = subject = memo = null;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	@Override
	public int getLetterUid() {
		return uid;
	}

	@Override
	public void setLetterUid(int uid) {
		this.uid = uid;
	}
	
	@Override
	public void toWorldJoin(Connection con, PcInstance pc){
		super.toWorldJoin(con, pc);
		LetterController.read(con, this);
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(getItem().getInvGfx()==464){
			// 새편지 작성
			cbp.readH();
			String to = cbp.readS();
			String subject = cbp.readSS();
			String memo = cbp.readSS();

			if (subject == null || subject.length() < 2)
				subject = "제목 없음";
			
			if (memo == null || memo.length() < 2)
				subject += "   ";

			// 수량 하향
			cha.getInventory().count(this, getCount()-1, true);
			// 편지작성한거 처리.
			LetterController.toLetter(cha.getName(), to, subject, memo, 0);
		}else{
			// 읽거나 않읽은 편지
			if(getItem().getInvGfx()==465)
				//읽은편지지로 변경.
				item = ItemDatabase.find("편지지 - 읽은 편지");
			// 편지창 뛰우기.
			cha.toSender(S_LetterRead.clone(BasePacketPooling.getPool(S_LetterRead.class), this));
		}
	}
}
