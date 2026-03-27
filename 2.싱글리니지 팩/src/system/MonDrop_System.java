package system;

import lineage.bean.database.Drop;
import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.server.S_Inventory;
import lineage.share.Lineage;

public class MonDrop_System extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, Monster mon) {
		if (bp == null)
			bp = new MonDrop_System(mon);
		else
			((MonDrop_System) bp).toClone(mon);
		return bp;
	}

	public MonDrop_System(Monster mon) {
		toClone(mon);
	}

	public void toClone(Monster m) {

		clear();
		writeC(Opcodes.S_OPCODE_SHOPBUY);
		writeD(0);

		// 일반상점 구성구간.
		writeH(m.getDropList().size());

		for (Drop s : m.getDropList()) {
			Item i = ItemDatabase.find(s.getItemName());
			if (i != null) {
				writeD(0);
				writeH(i.getInvGfx());
				writeD(0);
				StringBuffer sb = new StringBuffer();
				// 축저주 구분
				sb.append(s.getItemBress() == 0 ? "[축복]" : (s.getItemBress() == 1 ? "" : "[저주]"));
				// 인첸트 레벨 표현
				if ((i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor")))
					sb.append(s.getItemEn() > 0 ? " +" : s.getItemEn() < 0 ? " -" : "")
							.append(s.getItemEn() > 0 ? s.getItemEn() : "");
				// 이름 표현
				sb.append(" ").append(i.getName());
				// 수량 표현
				if (s.getCountMax() > 1)
					sb.append(" (1 ~ ").append(s.getCountMax()).append(")");
				writeS(sb.toString());

				if (Lineage.server_version > 144) {
					if (i.getType1().equalsIgnoreCase("armor")) {
						toArmor(i, null, 0, s.getItemEn(), (int) i.getWeight(), 
								s.getItemEn() > 4 ? (s.getItemEn() - 4) * i.getEnchantMr() : 0,
								s.getItemBress(), s.getItemEn() * i.getEnchantStunDefense());
					} else if (i.getType1().equalsIgnoreCase("weapon")) {
						toWeapon(i, null, 0, s.getItemEn(), (int) i.getWeight(), s.getItemBress());
					} else {
						toEtc(i, (int) i.getWeight());
					}
				}
			}
		}
	}
}