package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Warehouse;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;

public class WarehouseDatabase {
	
	private static List<Warehouse> pool;
	
	public static void init() {
		TimeLine.start("WarehouseDatabase..");
		
		pool = new ArrayList<Warehouse>();
		
		TimeLine.end();
	}
	

	
	static public Warehouse getPool(){
		Warehouse wh = null;
		synchronized (pool) {
			if(pool.size()>0){
				wh = pool.get(0);
				pool.remove(0);
			}else{
				wh = new Warehouse();
			}
		}
		return wh;
	}
	
	static public void setPool(Warehouse wh){
		if(wh == null)
			return;
		synchronized (pool) {
			if(!pool.contains(wh))
				pool.add(wh);
		}
	}
	
	static public void setPool(List<Warehouse> list){
		for(Warehouse wh : list)
			setPool(wh);
		list.clear();
	}
	
	public static Warehouse getAden(int uid, int dwarf_type) {
		Warehouse wh = null;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("SELECT * FROM warehouse_clan WHERE account_uid=? AND name='아데나' AND bress=1");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("SELECT * FROM warehouse_elf WHERE account_uid=? AND name='아데나' AND bress=1");
					break;
				default:
					st = con.prepareStatement("SELECT * FROM warehouse WHERE account_uid=? AND name='아데나' AND bress=1");
					break;
			}
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next())
				wh = get(rs, dwarf_type);
			
		} catch (Exception e) {
			lineage.share.System.println(WarehouseDatabase.class.toString()+" : getAden(int uid, int dwarf_type)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return wh;
	}
	
	public static void delete(int uid, int dwarf_type) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("DELETE FROM warehouse_clan WHERE uid=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("DELETE FROM warehouse_elf WHERE uid=?");
					break;
				default:
					st = con.prepareStatement("DELETE FROM warehouse WHERE uid=?");
					break;
			}
			st.setInt(1, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.println(WarehouseDatabase.class.toString()+" : delete(int uid, int dwarf_type)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	public static Warehouse find(int uid, int dwarf_type) {
		Warehouse wh = null;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("SELECT * FROM warehouse_clan WHERE uid=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("SELECT * FROM warehouse_elf WHERE uid=?");
					break;
				default:
					st = con.prepareStatement("SELECT * FROM warehouse WHERE uid=?");
					break;
			}
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next())
				wh = get(rs, dwarf_type);
			
		} catch (Exception e) {
			lineage.share.System.println(WarehouseDatabase.class.toString()+" : find(int uid, int dwarf_type)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return wh;
	}
	
	public static List<Warehouse> getList(int id, int dwarf_type) {
		List<Warehouse> list = new ArrayList<Warehouse>();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("SELECT * FROM warehouse_clan WHERE clan_id=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("SELECT * FROM warehouse_elf WHERE account_uid=?");
					break;
				default:
					st = con.prepareStatement("SELECT * FROM warehouse WHERE account_uid=?");
					break;
			}
			st.setInt(1, id);
			rs = st.executeQuery();
			while(rs.next()) {
				try {
					list.add( get(rs, dwarf_type) );
				} catch (Exception e) {}	
			}
		} catch (Exception e) {
			lineage.share.System.println(WarehouseDatabase.class.toString()+" : getList(int id, int dwarf_type)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return list;
	}
	
	/**
	 * 중복코드 방지용.
	 * @param rs
	 * @param dwarf_type
	 * @return
	 * @throws Exception
	 */
	private static Warehouse get(ResultSet rs, int dwarf_type) throws Exception {
		//
		Warehouse wh = getPool();
		wh.setUid( rs.getInt("uid") );
		if(dwarf_type == Lineage.DWARF_TYPE_CLAN)
			wh.setClanId( rs.getInt("clan_id") );
		else
			wh.setAccountUid( rs.getInt("account_uid") );
		wh.setInvId( rs.getInt("inv_id") );
		wh.setPetId( rs.getInt("pet_id") );
		wh.setLetterId( rs.getInt("letter_id") );
		wh.setName( rs.getString("name") );
		wh.setType( rs.getInt("type") );
		wh.setGfxid( rs.getInt("gfxid") );
		wh.setCount( rs.getLong("count") );
		wh.setQuantity( rs.getInt("quantity") );
		wh.setEn( rs.getInt("en") );
		wh.setDefinite( rs.getInt("definite")==1 );
		wh.setBress( rs.getInt("bress") );
		wh.setDurability( rs.getInt("durability") );
		wh.setTime( rs.getInt("time") );
		wh.setEnfire( rs.getInt("enfire") );  
		wh.setEnwater( rs.getInt("enwater") );  
		wh.setEnwind( rs.getInt("enwind") );  
		wh.setEnearth( rs.getInt("enearth") );  
		//
		Item item = ItemDatabase.find(wh.getName());
		if(item != null) {
			wh.set구분1( item.getType1() );
			wh.set구분2( item.getType2() );
		}
		//
		return wh;
	}

	/**
	 * 창고에 아이템이 몇개있는지 추출하는 메서드.
	 */
	public static int getCount(int uid, int dwarf_type) {
		int count = 0;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("SELECT COUNT(*) FROM warehouse_clan WHERE clan_id=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("SELECT COUNT(*) FROM warehouse_elf WHERE account_uid=?");
					break;
				default:
					st = con.prepareStatement("SELECT COUNT(*) FROM warehouse WHERE account_uid=?");
					break;
			}
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next())
				count = rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.println(WarehouseDatabase.class.toString()+" : getCount(int uid, int dwarf_type)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		return count;
	}

	/**
	 * 디비에 아이템 등록할때 처리하는 메서드.
	 */
	public static void insert(ItemInstance item, long inv_id, long count, int uid, int dwarf_type){
		int type = 0;
		if(item instanceof ItemWeaponInstance)
			type = 1;
		else if(item instanceof ItemArmorInstance)
			type = 2;
		else
			type = 3;

		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("INSERT INTO warehouse_clan SET clan_id=?, inv_id=?, name=?, type=?, gfxid=?, count=?, quantity=?, en=?, definite=?, bress=?, durability=?, time=?, pet_id=?, letter_id=?,enfire=?,enwater=?,enwind=?,enearth=?,dolloption_a=?,dolloption_b=?,dolloption_c=?,dolloption_d=?,dolloption_e=?,itemtime=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("INSERT INTO warehouse_elf SET account_uid=?, inv_id=?, name=?, type=?, gfxid=?, count=?, quantity=?, en=?, definite=?, bress=?, durability=?, time=?, pet_id=?, letter_id=?,enfire=?,enwater=?,enwind=?,enearth=?,dolloption_a=?,dolloption_b=?,dolloption_c=?,dolloption_d=?,dolloption_e=?,itemtime=?");
					break;
				default:
					st = con.prepareStatement("INSERT INTO warehouse SET account_uid=?, inv_id=?, name=?, type=?, gfxid=?, count=?, quantity=?, en=?, definite=?, bress=?, durability=?, time=?, pet_id=?, letter_id=?,enfire=?,enwater=?,enwind=?,enearth=?,dolloption_a=?,dolloption_b=?,dolloption_c=?,dolloption_d=?,dolloption_e=?,itemtime=?");
					break;
			}
			st.setInt(1, uid);
			st.setLong(2, inv_id);
			st.setString(3, item.getItem().getName());
			st.setInt(4, type);
			st.setInt(5, item.getItem().getInvGfx());
			st.setLong(6, count);
			st.setInt(7, item.getQuantity());
			st.setInt(8, item.getEnLevel());
			st.setInt(9, item.isDefinite()?1:0);
			st.setInt(10, item.getBless());
			st.setInt(11, item.getDurability());
			st.setInt(12, item.getTime());
			st.setLong(13, item.getPetObjectId());
			st.setInt(14, item.getLetterUid());
			st.setInt(15, item.getEnFire());
			st.setInt(16, item.getEnWater());
			st.setInt(17, item.getEnWind());
			st.setInt(18, item.getEnEarth());
			st.setInt(19, item.getInvDolloptionA());
			st.setInt(20, item.getInvDolloptionB());
			st.setInt(21, item.getInvDolloptionC());
			st.setInt(22, item.getInvDolloptionD());
			st.setInt(23, item.getInvDolloptionE());
			st.setString(24, item.getItemTimek());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.println(WarehouseDatabase.class.toString()+" : insert(ItemInstance item, int inv_id, int count, int uid, int dwarf_type)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
		
	}

	/**
	 * 창고에 같은 종류에 아이템이 존재한다는걸 미리 확인 했기때문에
	 * 그 정보를 토대로 count값만 갱신함.
	 */
	public static void update(String name, int bress, int uid, long count, int dwarf_type){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("UPDATE warehouse_clan SET count=count+? WHERE clan_id=? AND name=? AND bress=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("UPDATE warehouse_elf SET count=count+? WHERE account_uid=? AND name=? AND bress=?");
					break;
				default:
					st = con.prepareStatement("UPDATE warehouse SET count=count+? WHERE account_uid=? AND name=? AND bress=?");
					break;
			}
			st.setLong(1, count);
			st.setInt(2, uid);
			st.setString(3, name);
			st.setInt(4, bress);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.println(WarehouseDatabase.class.toString()+" : update(ItemInstance item, int uid, int count, boolean clan)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	/**
	 * 창고에 겹쳐질 수 있는 아이템이 존재하는지 확인하는 메서드.
	 */
		//
	public static long isPiles(boolean piles, int uid, String name, int bress, int dwarf_type){
		long inv_id = 0;
		
		if(piles) {
			Connection con = null;
			PreparedStatement st = null;
			ResultSet rs = null;
			try {
				con = DatabaseConnection.getLineage();
				switch(dwarf_type){
					case Lineage.DWARF_TYPE_CLAN:
						st = con.prepareStatement("SELECT * FROM warehouse_clan WHERE clan_id=? AND name=? AND bress=?");
						break;
					case Lineage.DWARF_TYPE_ELF:
						st = con.prepareStatement("SELECT * FROM warehouse_elf WHERE account_uid=? AND name=? AND bress=?");
						break;
					default:
						st = con.prepareStatement("SELECT * FROM warehouse WHERE account_uid=? AND name=? AND bress=?");
						break;
				}
				st.setInt(1, uid);
				st.setString(2, name);
				st.setInt(3, bress);
				rs = st.executeQuery();
				if(rs.next())
					inv_id = rs.getLong("inv_id");
			} catch (Exception e) {
				lineage.share.System.println(WarehouseDatabase.class.toString()+" : isPiles(ItemInstance item, int uid, int dwarf_type)");
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}

		return inv_id;
	}
	
	/**
	 * 창고 맡길시 수량 체크하여 20억이상이면 못맡기게 수정.
	 * 2019-06-27
	 * by connector12@nate.com
	 */
	public static boolean isCountCheck(PcInstance pc, int uid, String name, int bress, int dwarf_type, long count, long tempCount) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			con = DatabaseConnection.getLineage();
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("SELECT * FROM warehouse_clan WHERE clan_id=? AND name=? AND bress=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("SELECT * FROM warehouse_elf WHERE account_uid=? AND name=? AND bress=?");
					break;
				default:
					st = con.prepareStatement("SELECT * FROM warehouse WHERE account_uid=? AND name=? AND bress=?");
					break;
			}
			st.setInt(1, uid);
			st.setString(2, name);
			st.setInt(3, bress);
			rs = st.executeQuery();
			
			if(rs.next()) {
				if (rs.getLong("count") + count > Common.MAX_COUNT)
					return false;
			}
		} catch (Exception e) {
			lineage.share.System.println(WarehouseDatabase.class.toString()+" : isCountCheck(int uid, String name, int bress, int dwarf_type, long count)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		return true;
	}
	
}
