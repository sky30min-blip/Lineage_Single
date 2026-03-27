package lineage.world.object.item.armor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_ObjectAdd;
import lineage.network.packet.server.S_ObjectLight;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_ObjectPolyIcon;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.bean.database.Poly;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.magic.ShapeChange;

public class Turban extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Turban();
		// 시간 설정 - 깃털 갯수
		// : 180개 3시간
		// : 240개 5시간
		// : 450개 10시간
		// 기본 시간을 3시간으로 설정.
		item.setNowTime(60 * 60 * 3);
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		super.toClick(cha, cbp);
		if (getTime() <= 1) {
			ChattingController.toChatting(cha, "마법효과가 사라졌습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		if (equipped)
			// 버프 등록
			BuffController.append(this, this);
		else
			// 버프 제거
			BuffController.remove(this, getClass());
	}

	@Override
	public void toWorldJoin(Connection con, PcInstance pc) {
		super.toWorldJoin(con, pc);
		if (equipped)
			BuffController.append(this, this);
	}

	@Override
	public void toBuffStart(object o) {
		// Character cha = (Character) o;
		String polyName = null;
		//
		switch (getItem().getNameIdNumber()) {
		case 5132: // 드레이크 변신터번 5645
			polyName = "turban Drake";
			break;
		case 5135: // 서큐버스 퀸 변신터번 4004
			polyName = "turban Succubus Queen";
			break;
		case 5136: // 나이트발드 변신터번 4000
			polyName = "turban Night Vald";
			break;
		case 5137: // 아이리스 변신터번 4001
			polyName = "turban Iris";
			break;
		case 5163: // 기마용 투구
			if (cha.getClassType() == 0x00) {
				if (cha.getClassSex() == 0)
					polyName = "horse prince";
				else
					polyName = "horse princess";
			} else if (cha.getClassType() == 0x02) {
				polyName = "horse elf";
			}
			break;
		case 5323: // 아덴근위대 변신터번 6406
			polyName = "turban Aden Guardian";
			break;
		case 5571: // 블랙 위자드 변신터번 6698
			polyName = "turban Darkelf Wizard";
			break;
		case 5570: // 하피 변신터번 6697
			polyName = "turban Happy";
			break;
		}
		// 변신 처리.
		Poly p = PolyDatabase.getPolyName(polyName);
		if (p != null) {
			// 장비 해제.
			PolyDatabase.toEquipped(cha, p);

			// 변신
			cha.setGfx(p.getGfxId());

			if (Lineage.is_weapon_speed) {
				if (!cha.checkSpear()) {
					ItemInstance weapon = cha.getInventory().getSlot(Lineage.SLOT_WEAPON);

					if (weapon != null && weapon.getItem() != null && SpriteFrameDatabase.findGfxMode(cha.getGfx(), weapon.getItem().getGfxMode() + Lineage.GFX_MODE_ATTACK)) {
						cha.setGfxMode(weapon.getItem().getGfxMode());
					} else {
						cha.setGfxMode(p.getGfxMode());
					}
				}
			} else {
				cha.setGfxMode(p.getGfxMode());
			}

			cha.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), cha), true);
			if (Lineage.server_version > 182)
				cha.toSender(S_ObjectPolyIcon.clone(BasePacketPooling.getPool(S_ObjectPolyIcon.class), getNowTime()));
			// 버프등록
			BuffController.append(cha, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(208), getNowTime()));
		}
	}

	@Override
	public void toBuffStop(object o) {
		equipped = false;
		BuffController.remove(cha, ShapeChange.class);
	}

	@Override
	public void toBuffEnd(object o) {
		if (cha == null || cha.isWorldDelete())
			return;

		// 종료 처리.
		toBuffStop(cha);
	}

	@Override
	public void toBuff(object o) {
		if (cha == null || cha.isWorldDelete())
			return;
		// 1분마다 한번씩 아이템정보 패킷 전송.
		// 아이템명에서 시간을 표현하기 때문에.
		if (getTime() % 1 == 0)
			cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), this));
		    
		if (getTime() <= 1) {
			if (isEquipped())
			toClick(cha, null);
			
			// 아이템 제거멘트
			if (this instanceof Turban)
				ChattingController.toChatting(cha, getItem().getName() + " 이(가) 증발하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
			// 버프 제거
			BuffController.remove(this, getClass());
		}
	}

	public void toWorldOut(object o) {
		if (cha == null || cha.isWorldDelete())
			return;

		toBuffEnd(o);
	}

}
