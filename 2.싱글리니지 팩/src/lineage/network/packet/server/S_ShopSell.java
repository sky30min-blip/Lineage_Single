package lineage.network.packet.server;

import java.sql.Connection;
import java.util.List;

import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ShopInstance;

public class S_ShopSell extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, ShopInstance shop, List<ItemInstance> list){
		if(bp == null)
			bp = new S_ShopSell(shop, list);
		else
			((S_ShopSell)bp).toClone(shop, list);
		return bp;
	}
	
	public S_ShopSell(ShopInstance shop, List<ItemInstance> list){
		toClone(shop, list);
	}
	
	public void toClone(ShopInstance shop, List<ItemInstance> list){
		clear();

		writeC(Opcodes.S_OPCODE_SHOPSELL);
		writeD(shop.getObjectId());
		writeH(list.size());	// 인벤토리에서 팔수잇는 아템 갯수
		
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			
			for( ItemInstance item : list ){
		
					writeD(item.getObjectId());
					writeD(shop.getPrice(con, item));
		

			}
			
			
		} catch (Exception e) {
		} finally {
			DatabaseConnection.close(con);
		}
	}
}
