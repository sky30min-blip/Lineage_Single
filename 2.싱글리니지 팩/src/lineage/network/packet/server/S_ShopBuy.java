package lineage.network.packet.server;

import lineage.bean.database.Item;
import lineage.bean.database.Shop;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.DogRaceController;
import lineage.world.controller.SlimeRaceController;
import lineage.world.object.instance.DograceInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.instance.SlimeraceInstance;
import lineage.world.object.npc.PowerballNpc;

import java.util.ArrayList;
import java.util.List;

public class S_ShopBuy extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, ShopInstance shop) {
		if (bp == null)
			bp = new S_ShopBuy(shop);
		else
			((S_ShopBuy) bp).toClone(shop);
		return bp;
	}

	public S_ShopBuy(ShopInstance shop) {
		toClone(shop);
	}

	public void toClone(ShopInstance shop) {
		clear();

		writeC(Opcodes.S_OPCODE_SHOPBUY);
		writeD(shop.getObjectId());

		// 일반상점 구성구간.
		toShop(shop);
	}

	private void toShop(ShopInstance shop) {
		// getBuySize()는 item DB에 없는 행도 포함할 수 있음 → 패킷 개수와 실제 write 불일치 방지
		List<Shop> buyShops = new ArrayList<Shop>();
		for (Shop s : shop.getNpc().getShop_list()) {
			if (s.isItemBuy() && ItemDatabase.find(s.getItemName()) != null)
				buyShops.add(s);
		}
		writeH(buyShops.size());

		for (Shop s : buyShops) {
			Item i = ItemDatabase.find(s.getItemName());
			if (i == null)
				continue;
			writeD(s.getUid());
			writeH(i.getInvGfx());

			if (s.getPrice() != 0) {
				writeD(shop.getTaxPrice(s.getPrice(), false));
			} else {
				if ((i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor")) && !i.getType2().equalsIgnoreCase("necklace")
						&& !i.getType2().equalsIgnoreCase("ring") && !i.getType2().equalsIgnoreCase("belt")) {
					if (i.getType1().equalsIgnoreCase("weapon"))
						writeD(shop.getTaxPrice(i.getShopPrice() * s.getItemCount() + (s.getItemEnLevel() * ItemDatabase.find(244).getShopPrice()), false));
					else
						writeD(shop.getTaxPrice(i.getShopPrice() * s.getItemCount() + (s.getItemEnLevel() * ItemDatabase.find(249).getShopPrice()), false));
				} else {
					if ((i.getName().equalsIgnoreCase(Lineage.scroll_dane_fools) || i.getName().equalsIgnoreCase(Lineage.scroll_zel_go_mer) || i.getName().equalsIgnoreCase(Lineage.scroll_orim)|| i.getName().equalsIgnoreCase(Lineage.scroll_tell)|| i.getName().equalsIgnoreCase(Lineage.scroll_poly)) && (s.getItemBress() == 0 || s.getItemBress() == 2))
						writeD(shop.getTaxPrice(s.getItemBress() == 0 ? ItemDatabase.find(i.getNameIdNumber()).getShopPrice() * Lineage.sell_bless_item_rate : ItemDatabase.find(i.getNameIdNumber()).getShopPrice() * Lineage.sell_curse_item_rate, false));
					else
						writeD(shop.getTaxPrice(i.getShopPrice() * s.getItemCount(), false));
				}
			}

			if (shop instanceof DograceInstance) {
				writeS(DogRaceController.RacerTicketName(s.getUid()));
			} else if (shop instanceof SlimeraceInstance) {
				writeS(SlimeRaceController.SlimeRaceTicketName(s.getUid()));
			} else {
				StringBuffer sb = new StringBuffer();

				// 축저주 구분
				sb.append(s.getItemBress() == 0 ? "축복받은" : (s.getItemBress() == 1 ? "" : "저주받은"));
				// 인첸트 레벨 표현
				if (s.getItemEnLevel() != 0 && (i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor")))
					sb.append(" ").append(s.getItemEnLevel() >= 0 ? "+" : "-").append(s.getItemEnLevel());
				// 이름0 표현
				sb.append(" ").append(s.getItemName());
				// 수량 표현 (파워볼 쿠폰은 구매 수량 1로만 표시)
				int cnt = s.getItemCount();
				String nm = s.getItemName();
				if (shop instanceof PowerballNpc && nm != null && (nm.equals("홀 쿠폰") || nm.equals("짝 쿠폰")
						|| nm.equals("언더 쿠폰") || nm.equals("오버 쿠폰")))
					cnt = 1;
				if (cnt > 1)
					sb.append(" (").append(Util.changePrice(cnt)).append(")");
				writeS(sb.toString());
			}

			if (Lineage.server_version > 144) {
				if (i.getType1().equalsIgnoreCase("armor")) {
					if (i.getName().equalsIgnoreCase("신성한 엘름의 축복"))
						toArmor(i, null, 0, s.getItemEnLevel(), (int) i.getWeight(),
								s.getItemEnLevel() > 4 ? (s.getItemEnLevel() - 4) * i.getEnchantMr() : 0,
								s.getItemBress(), s.getItemEnLevel() * i.getEnchantStunDefense());
					else
						toArmor(i, null, 0, s.getItemEnLevel(), (int) i.getWeight(),
								s.getItemEnLevel() * i.getEnchantMr(), s.getItemBress(),
								s.getItemEnLevel() * i.getEnchantStunDefense());
				} else if (i.getType1().equalsIgnoreCase("weapon")) {
					toWeapon(i, null, 0, s.getItemEnLevel(), (int) i.getWeight(), s.getItemBress());
				} else {
					toEtc(i, (int) i.getWeight());
				}
			}
		}
	}
}
