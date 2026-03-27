package lineage.network.packet.server;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Exp;
import lineage.database.ExpDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Candle;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.Letter;
import lineage.world.object.item.RaceTicket;
import lineage.world.object.item.wand.EbonyWand;
import lineage.world.object.item.wand.MapleWand;
import lineage.world.object.item.wand.PineWand;


public class S_CharAutoShop extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, PcInstance pc, List<ItemInstance> list) {
		if (bp == null)
			bp = new S_CharAutoShop(pc, list);
		else
			((S_CharAutoShop) bp).toClone(pc, list);
		return bp;
	}

	public S_CharAutoShop(PcInstance pc, List<ItemInstance> list) {
		toClone(pc, list);
	}

	public void toClone(PcInstance pc, List<ItemInstance> list) {
		clear();

		writeC(Opcodes.S_OPCODE_WAREHOUSE);
		writeD(pc.getObjectId());

		List<ItemInstance> new_list = new ArrayList<ItemInstance>();
		
		for (ItemInstance wh : list) {
			if (wh.isEquipped())
				continue;

			if (wh instanceof RaceTicket || wh instanceof Letter || wh instanceof DogCollar)
				continue;

			if (wh.getItem().getName().equalsIgnoreCase("아데나") || wh.getInnRoomKey() > 0)
				continue;


			
			new_list.add(wh);
		}

		writeH(new_list.size());
		writeC(3);
		readDB(pc, new_list); // 창고 목록
	}

	private void readDB(PcInstance pc, List<ItemInstance> list) {
		for (ItemInstance wh : list) {
			int type = 0;
			if (wh instanceof ItemWeaponInstance)
				type = 1;
			else if (wh instanceof ItemArmorInstance)
				type = 2;
			else
				type = 3;

			writeD(wh.getObjectId()); // 번호
			writeC(0); // 타입
			writeH(wh.getItem().getInvGfx()); // GFX 아이디
			writeC(wh.getBless()); // 1: 보통 0: 축 2: 저주
			writeD(wh.getCount()); // 현재아템 총수량
			writeC(wh.isDefinite() ? 1 : 0); // 1: 확인 0: 미확인
			writeS(wh.getName()); // 이름

			if (Lineage.server_version >= 380)
				writeC(0x00);
		}
		writeD(0);
	}

	protected String getName(ItemInstance item) {
		StringBuffer sb = new StringBuffer();
		if (item.getItem().getNameIdNumber() == 1075 && item.getItem().getInvGfx() != 464) {
			Letter letter = (Letter) item;
			sb.append(letter.getFrom());
			sb.append(" : ");
			sb.append(letter.getSubject());
		} else {
			if (item.isDefinite() && (item instanceof ItemWeaponInstance || item instanceof ItemArmorInstance)) {
				// 속성 인첸 표현.
				String element_name = null;
				if (item.getEnWind() == 1) {
					element_name = "바람의";
					
				}
				if (item.getEnWind() == 2) {
					element_name = "태풍의";
				
				}
				if (item.getEnWind() == 3) {
					element_name = "실프의";
				
				}
				
			
				
				if (item.getEnEarth() == 1) {
					element_name = "대지의";
				
				}
				if (item.getEnEarth() == 2) {
					element_name = "파괴의";
				
				}
				if (item.getEnEarth() == 3) {
					element_name = "클레이의";
				
				}
			
				
				if (item.getEnWater() == 1) {
					element_name = "물의";
				
				}
				if (item.getEnWater() == 2) {
					element_name = "해일의";
					
				}
				if (item.getEnWater() == 3) {
					element_name = "운디네의";
					
				}
		
				if (item.getEnFire() == 1) {
					element_name = "불의";
				
				}
				if (item.getEnFire() == 2) {
					element_name = "폭발의";
			
				}
				if (item.getEnFire() == 3) {
					element_name = "이그니스의";
				
				}
		
				
				if (element_name != null) {
					sb.append(element_name).append("");
					sb.append(" ");
			
				}
				// 인첸 표현.
				if (item.getEnLevel() >= 0) {
					sb.append("+");
				}
				sb.append(item.getEnLevel());
				sb.append(" ");
			}
			// 앨리스
//			if (item instanceof Alice)

		
			// 착용중인 아이템 표현
			if (item.isEquipped()) {
				if (item instanceof ItemWeaponInstance) {
					sb.append(" ($9)");
				} else if (item instanceof ItemArmorInstance) {
					sb.append(" ($117)");
				} else if (item instanceof Candle) {
					// 양초, 등잔
					sb.append(" ($10)");
				}
			}

			if (item.getCount() > 1) {
				sb.append(" (");
				sb.append(item.getCount());
				sb.append(")");
			}
			if (item.isDefinite()
					&& (item instanceof MapleWand || item instanceof PineWand || item instanceof EbonyWand)) {
				sb.append(" (");
				sb.append(item.getQuantity());
				sb.append(")");
			}
			if (item.getItem().getNameIdNumber() == 1173) {
				DogCollar dc = (DogCollar) item;
				sb.append(" [Lv.");
				sb.append(dc.getPetLevel());
				sb.append(" ");
				sb.append(dc.getPetName());
				sb.append("]");
			}

			
			if (item.getInnRoomKey() > 0) {
				sb.append(" #");
				sb.append(item.getInnRoomKey());
			}
		}

		return sb.toString().trim();
	}

}
