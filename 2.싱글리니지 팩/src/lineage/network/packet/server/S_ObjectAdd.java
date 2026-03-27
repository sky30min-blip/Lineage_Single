package lineage.network.packet.server;

import lineage.bean.lineage.Useshop;
import lineage.database.CharacterMarbleDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.UserShopController;
import lineage.world.controller.WantedController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.yadolan.HuntingZoneTeleportationBook;

public class S_ObjectAdd extends S_Inventory {

	static public BasePacket clone(BasePacket bp, object o, object oo) {
		if (bp == null)
			bp = new S_ObjectAdd(o, oo);
		else
			((S_ObjectAdd) bp).toClone(o, oo);
		return bp;
	}

	public S_ObjectAdd(object o, object oo) {
		toClone(o, oo);
	}

	public void toClone(object o, object oo) {
		clear();

		String name = o.isNameHidden() ? "" : o.getName();

		// 수배중 체크
		if (o instanceof PcInstance && WantedController.checkWantedPc(o))
			name = Lineage.wanted_name + name;

		if (o.getOwnObjectId() > 0 && o.getOwnName() != null) {
			boolean wanted = false;

			if (WantedController.checkWantedPc(o.getOwnObjectId()))
				wanted = true;

			if (wanted) {
				o.setOwnName(!o.getOwnName().contains(Lineage.wanted_name) ? Lineage.wanted_name + o.getOwnName() : o.getOwnName());
			} else {
				o.setOwnName(o.getOwnName().contains(Lineage.wanted_name) ? o.getOwnName().substring(5) : o.getOwnName());
			}
		}

		int hp = 0xff;
		int lev = o instanceof PcInstance ? 0 : o.getLevel();
		boolean isHpbar = (o instanceof SummonInstance && o.getOwnObjectId() == oo.getObjectId()) || (o.getPartyId() > 0 && o.getPartyId() == oo.getPartyId())
				|| (o.isHpbar() && o.getObjectId() == oo.getObjectId()) || (oo.getGm() != 0 && o instanceof PcInstance);
		// 요청사항: NPC/몬스터 머리 위 HP바는 전역 비표시.
		if (o instanceof MonsterInstance || o instanceof NpcInstance)
			isHpbar = false;
		byte[] msg = null;

		if (o instanceof ItemInstance) {
			ItemInstance item = (ItemInstance) o;
			StringBuffer sb = new StringBuffer();
			String element_name = null;
			Integer element_en = 0;
			// 축저주 구분
			if (item.isDefinite()) {
				// sb.append(item.getBless() == 0 ? "축복받은" : (item.getBless() ==
				// 1 ? "" : "저주받은"));
				// 인첸트 레벨 표현

				if (item.getEnWind() > 0) {
					element_name = "풍령";
					element_en = item.getEnWind();
				}
				if (item.getEnEarth() > 0) {
					element_name = "지령";
					element_en = item.getEnEarth();
				}
				if (item.getEnWater() > 0) {
					element_name = "수령";
					element_en = item.getEnWater();
				}
				if (item.getEnFire() > 0) {
					element_name = "화령";
					element_en = item.getEnFire();
				}
				if (element_name != null) {
					sb.append(element_name).append(":").append(element_en).append("단");
				}

				if (item instanceof ItemWeaponInstance || item instanceof ItemArmorInstance)
					sb.append(" ").append(item.getEnLevel() >= 0 ? "+" : "-").append(item.getEnLevel());
			}

			// 이름 표현
			sb.append(" ").append(item.getItem().getName());
			// 수량 표현
			if (item.getCount() > 1)
				sb.append(" (").append(Util.changePrice(item.getCount())).append(")");

			// 펫 목걸이
			if (item instanceof DogCollar) {
				DogCollar dc = (DogCollar) item;
				sb.append(" [Lv.");
				sb.append(dc.getPetLevel());
				sb.append(" ");
				sb.append(dc.getPetName());
				sb.append("]");
			}

			String itemName = CharacterMarbleDatabase.getItemName(o);
			if (itemName != null) {
				name = "" + itemName;
			} else {
				name = sb.toString();
			}
		}

		if (o instanceof PcInstance) {
			Useshop us = UserShopController.find((PcInstance) o);
			if (us != null)
				msg = us.getMsg();
		}

		// hp바 표현 부분
		if (isHpbar)
			hp = ((Character) o).getHpPercent();

		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(o.getX());
		writeH(o.getY());
		writeD(o.getObjectId());
		writeH(o.getGfx());
		writeC(o.getGfxMode());
		writeC(o.getHeading());
		writeC(o.getLight());
		writeC(o.getSpeed()); // 0:보통 1:빠름 2:느림
		writeD((int) o.getCount()); // 객체가 가지고있는 갯수
		writeH(o.getLawful());
		// if (o instanceof MonsterInstance) {
		// if (((MonsterInstance) o).getMonster().isBoss()) {
		// writeS(name);
		// }else{
		//
		// writeS("Lv: "+ o.getLevel() +" " +name );
		//
		// }
		// }else{
		writeS(name);
		// }

		writeS(o.isNameHidden() ? "" : o.getTitle());
		writeC(o.getStatus(oo)); // 세팅 - 0:mob,item(atk pointer), 1:poisoned(독),
									// 2:invisable(투명), 4:pc, 8:cursed(저주),
									// 16:brave(용기), 32:와퍼, 64:무빙악셀레이션(다엘마법),
									// 128:invisable but name
		writeD(o.isNameHidden() ? 0 : o.getClanId()); // 클렌 아이디
		writeS(o.isNameHidden() ? "" : o.getClanName()); // 클렌 이름
		writeS(o.isNameHidden() ? "" : o.getOwnName()); // 팻호칭 "allnight의" 도베르만
		writeC(0); // ??
		writeC(hp); // HP바 부분
		writeC(0); // 딸꾹 거리는 부분
		writeC(lev); // PC = 0, Mon = Lv
		writeB(msg); // 개인상점 광고부분
		writeC(0xFF);
		writeC(0xFF);

	}
}
