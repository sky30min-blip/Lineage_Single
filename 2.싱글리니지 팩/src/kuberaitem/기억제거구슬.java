package kuberaitem;

import lineage.network.LineageServer;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Disconnect;
import lineage.share.Lineage;
import lineage.world.controller.BookController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class 기억제거구슬 extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new 기억제거구슬();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){

		PcInstance pc = (PcInstance) cha;
		BookController.Bookmarkitemremove(pc);
		cha.getInventory().count(this, getCount()-1, true);
		try {
			
			ChattingController.toChatting(pc, String.format("기억 내역 초기화를 위해 종료합니다."), Lineage.CHATTING_MODE_MESSAGE);
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		} 
		
		// 사용자 강제종료 시키기.
		pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
		LineageServer.close(pc.getClient());
	}
}
