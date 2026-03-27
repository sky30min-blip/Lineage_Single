package lineage.world.object.item;

import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.LetterController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class PledgeLetter extends Letter {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new PledgeLetter();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(getItem().getInvGfx()==464){
			// 새편지 작성
			cbp.readH();
			String to = cbp.readS();		// 혈맹 이름
			String subject = cbp.readSS();	// 제목
			String memo = cbp.readSS();		// 내용
			
			if (subject.length() < 2)
				subject = "제목 없음";
			
			if (memo.length() < 2)
				subject += "   ";

			// 수량 하향
			cha.getInventory().count(this, getCount()-1, true);
			// 편지작성한거 처리.
			LetterController.toPledgeLetter(cha.getName(), to, subject, memo);
		}else{
			super.toClick(cha, cbp);
		}
	}

}
