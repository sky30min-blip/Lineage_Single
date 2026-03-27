package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class WarehouseClanLogDatabase {

	/**
	 * 로그 등록.
	 * @param pc
	 * @param item
	 */
	public static void append(PcInstance pc, ItemInstance item, long count, String type) {
		if(pc.getClanId() == 0)
			return;
		//
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO warehouse_clan_log SET type=?, character_uid=?, character_name=?, clan_uid=?, clan_name=?, item_uid=?, item_name=?, item_count=?, item_en=?, item_bress=?");
			st.setString(1, type);
			st.setLong(2, pc.getObjectId());
			st.setString(3, pc.getName());
			st.setInt(4, pc.getClanId());
			st.setString(5, pc.getClanName());
			st.setLong(6, item.getObjectId());
			st.setString(7, item.getItem().getName());
			st.setLong(8, item.getCount());
			st.setInt(9, item.getEnLevel());
			st.setInt(10, item.getBless());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : append(PcInstance pc, ItemInstance item, long count)\r\n", WarehouseClanLogDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
}
