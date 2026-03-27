package lineage.world.object.item.all_night;

import java.sql.Connection;
import java.sql.PreparedStatement;

import lineage.database.DatabaseConnection;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class PvP_clean extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new PvP_clean();
		return item;
	}
	
	public void toClick(Character cha, ClientBasePacket cbp){
		Connection con = null;
		PreparedStatement stt = null;
		PcInstance pc = (PcInstance)cha;
		
		if (pc.isWorldDelete() || pc == null || pc.isDead())
			return;
		
		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("DELETE FROM characters_pvp WHERE objectId=?");
			stt.setLong(1, pc.getObjectId());
			stt.executeUpdate();
			stt.close();
			
			stt = con.prepareStatement("UPDATE characters SET pkcount=0 WHERE objID=?");
			stt.setLong(1, pc.getObjectId());
			stt.executeUpdate();
			stt.close();
			
			pc.setPkCount(0);
			ChattingController.toChatting(pc, "\\fY킬&데스가 초기화 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			cha.getInventory().count(this, getCount()-1, true);
		} catch (Exception e) {
			lineage.share.System.println("킬&데스 초기화 실패 : " + pc.getName());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, stt);
		}
	}
}
