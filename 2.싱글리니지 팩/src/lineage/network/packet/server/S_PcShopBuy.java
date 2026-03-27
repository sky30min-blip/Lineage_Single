package lineage.network.packet.server;

import lineage.bean.database.PcShop;
import lineage.database.CharacterMarbleDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.instance.PcShopInstance;

public class S_PcShopBuy extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, PcShopInstance psi) {
		if (bp == null)
			bp = new S_PcShopBuy(psi);
		else
			((S_PcShopBuy) bp).toClone(psi);
		return bp;
	}

	public S_PcShopBuy(PcShopInstance psi) {
		toClone(psi);
	}

	public void toClone(PcShopInstance psi) {
		clear();

		writeC(Opcodes.S_OPCODE_SHOPBUY);
		writeD(psi.getObjectId());

		// 일반상점 구성구간.
		writeH(psi.getListSize());

		for (PcShop s : psi.getShopList().values()) {
			writeD(s.getInvItemObjectId());
			writeH(s.getItem().getInvGfx());
			writeD(s.getPrice());

			StringBuffer sb = new StringBuffer();
			// 화폐타입
			if (!Lineage.is_market_only_aden) {
				sb.append("[").append(s.getAdenType().equalsIgnoreCase("아데나") ? "아덴" : "베릴").append("]");
				// 축저주 구분
				sb.append(s.getInvItemBress() == 0 ? " (축)" : (s.getInvItemBress() == 1 ? "" : " (저주)"));
				
				if(s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnFire() > 0){
					sb.append("화령 ");
					sb.append(s.getInvItemEnFire()).append("단계");
					sb.append(" ");
				}
				if(s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnWater() > 0){
					sb.append("수령 ");
					sb.append(s.getInvItemEnWater()).append("단계");
					sb.append(" ");
				}
				if(s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnWind() > 0){
					sb.append("풍령 ");
					sb.append(s.getInvItemEnWind()).append("단계");
					sb.append(" ");
				}
				if(s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnEarth() > 0){
					sb.append("지령 ");
					sb.append(s.getInvItemEnEarth()).append("단계");
					sb.append(" ");
				}
				// 인첸트 레벨 표현
				if ((s.getItem().getType1().equalsIgnoreCase("weapon") || s.getItem().getType1().equalsIgnoreCase("armor")))
					sb.append(" ").append(s.getInvItemEn() >= 0 ? "+" : "-").append(s.getInvItemEn()).append(" ");
			} else {
				// 축저주 구분
				sb.append(s.getInvItemBress() == 0 ? "(축) " : (s.getInvItemBress() == 1 ? "" : "(저주) "));
				if(s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnFire() > 0){
					sb.append("화령 ");
					sb.append(s.getInvItemEnFire()).append("단계");
					sb.append(" ");
				}
				if(s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnWater() > 0){
					sb.append("수령 ");
					sb.append(s.getInvItemEnWater()).append("단계");
					sb.append(" ");
				}
				if(s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnWind() > 0){
					sb.append("풍령 ");
					sb.append(s.getInvItemEnWind()).append("단계");
					sb.append(" ");
				}
				if(s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnEarth() > 0){
					sb.append("지령 ");
					sb.append(s.getInvItemEnEarth()).append("단계");
					sb.append(" ");
				}
				
				// 인첸트 레벨 표현
				if ((s.getItem().getType1().equalsIgnoreCase("weapon") || s.getItem().getType1().equalsIgnoreCase("armor")))
					sb.append(s.getInvItemEn() >= 0 ? "+" : "-").append(s.getInvItemEn()).append(" ");
			}

			// 이름 표현
			String itemName = CharacterMarbleDatabase.getItemName(s.getInvItemObjectId());
			if (itemName != null) {
				sb.append(itemName);
			} else {
				sb.append(" ").append(s.getItem().getName());
			}

			// 수량 표현
			if (s.getInvItemCount() > 1)
				sb.append(" (").append(Util.changePrice(s.getInvItemCount())).append(")");
			if (s.getItem().getNameIdNumber() == 1173) {
				sb.delete(0, sb.length());
				
				sb.append(" [Lv.");
				sb.append(s.getPetLevel());
				sb.append(" ");
				sb.append(s.getPetName());
				sb.append("]");
			}
			
			writeS(sb.toString());
	
			if (Lineage.server_version > 144) {
				if (s.getItem().getType1().equalsIgnoreCase("armor")) {
					if (s.getItem().getName().equalsIgnoreCase("신성한 엘름의 축복"))
						toArmor(s.getItem(), null, 0, s.getInvItemEn(), (int) s.getItem().getWeight(),
								s.getInvItemEn() > 4 ? (s.getInvItemEn() - 4) * s.getItem().getEnchantMr() : 0,
								s.getInvItemBress(), s.getInvItemEn() * s.getItem().getEnchantStunDefense());
					else
						toArmor(s.getItem(), null, 0, s.getInvItemEn(), (int) s.getItem().getWeight(),
								s.getInvItemEn() * s.getItem().getEnchantMr(), s.getInvItemBress(),
								s.getInvItemEn() * s.getItem().getEnchantStunDefense());
				} else if (s.getItem().getType1().equalsIgnoreCase("weapon")) {
					toWeapon(s.getItem(), null, 0, s.getInvItemEn(), (int) s.getItem().getWeight(), s.getInvItemBress());
				} else {
					toEtc(s.getItem(), (int) s.getItem().getWeight());
				}
			}
		}
	}

}
