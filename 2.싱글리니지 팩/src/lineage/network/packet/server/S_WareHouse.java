package lineage.network.packet.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Warehouse;
import lineage.database.CharacterMarbleDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.instance.DwarfInstance;

public class S_WareHouse extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, DwarfInstance dwarf, int dwarf_type, List<Warehouse> list) {
		if (bp == null)
			bp = new S_WareHouse(dwarf, dwarf_type, list);
		else
			((S_WareHouse) bp).toClone(dwarf, dwarf_type, list);
		return bp;
	}

	public S_WareHouse(DwarfInstance dwarf, int dwarf_type, List<Warehouse> list) {
		toClone(dwarf, dwarf_type, list);
	}

	public void toClone(DwarfInstance dwarf, int dwarf_type, List<Warehouse> list) {
		clear();

		writeC(Opcodes.S_OPCODE_WAREHOUSE);
		writeD(dwarf.getObjectId());
		writeH(list.size());
		switch (dwarf_type) {
		case Lineage.DWARF_TYPE_CLAN: // 혈맹창고찾기
			writeC(5);
			break;
		case Lineage.DWARF_TYPE_ELF: // 요정창고찾기
			writeC(9);
			break;
		default: // 일반창고찾기
			writeC(3);
			break;
		}
		readDB(list); // 창고 목록
	}

	private void readDB(List<Warehouse> list) {
		for (Warehouse wh : list) {
			Item item = ItemDatabase.find(wh.getName());
			StringBuffer item_name = new StringBuffer();
			String itemName = null;
			String element_name = null;
			Integer element_en = 0;
			if (item == null) {
				item_name.append("none");
			} else {
				if (item.getNameIdNumber() == 1075 && item.getInvGfx() != 464) {
					item_name.append(readLetterDB(wh.getLetterId()));
				} else {
				
					if (wh.isDefinite() && (wh.getType() == 1 || wh.getType() == 2)) {
						if (wh.getEn() < 0)
							item_name.append("-");
						else
							item_name.append("+");
						item_name.append(wh.getEn());
					}
					if (wh.getEnwind() > 0) {
						element_name = "풍령";
						element_en = wh.getEnwind();
					}
					if (wh.getEnearth() > 0) {
						element_name = "지령";
						element_en = wh.getEnearth();
					}
					if (wh.getEnwater() > 0) {
						element_name = "수령";
						element_en = wh.getEnwater();
					}
					if (wh.getEnfire() > 0) {
						element_name = "화령";
						element_en = wh.getEnfire();
					}
					if (element_name != null) {
						item_name.append(element_name).append(":").append(element_en).append("단");
					}
					item_name.append(" ").append(item.getNameId());
					if (wh.getCount() > 1) {
						item_name.append(" (");
						item_name.append(Util.changePrice(wh.getCount()));
						item_name.append(")");
					}
					if (item.getNameIdNumber() == 1173)
						item_name.append(readPetDB(wh.getPetId()));
					
					itemName = CharacterMarbleDatabase.getItemName(wh.getInvId());
				}
			}

			writeD(wh.getUid()); // 번호
			writeC(wh.getType()); // 타입
			writeH(wh.getGfxid()); // GFX 아이디
			writeC(wh.getBress()); // 1: 보통 0: 축 2: 저주
			writeD(wh.getCount()); // 현재아템 총수량
			writeC(wh.isDefinite() ? 1 : 0); // 1: 확인 0: 미확인
			writeS(itemName == null ? item_name.toString() : itemName); // 이름
		}
	}

	/**
	 * 펫 아이디로 이름처리에 사용될 정보만 뽑아서 문자열로 리턴.
	 * 
	 * @param con
	 * @param pet_id
	 * @return
	 */
	private String readPetDB(int pet_id) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_pet WHERE objid=?");
			st.setInt(1, pet_id);
			rs = st.executeQuery();
			if (rs.next())
				return String.format(" [Lv.%d %s]", rs.getInt("level"), rs.getString("name"));

		} catch (Exception e) {
			lineage.share.System.printf("%s : readPetDB(int pet_id)\r\n", S_WareHouse.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return "";
	}

	/**
	 * 편지 정보 추출.
	 * 
	 * @param con
	 * @param letter_id
	 * @return
	 */
	private String readLetterDB(int letter_id) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_letter WHERE uid=?");
			st.setInt(1, letter_id);
			rs = st.executeQuery();
			if (rs.next())
				return String.format("%s : %s", rs.getString("paperFrom"), rs.getString("paperSubject"));

		} catch (Exception e) {
			lineage.share.System.printf("%s : readLetterDB(int letter_id)\r\n", S_WareHouse.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return "";
	}
}
