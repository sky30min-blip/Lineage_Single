package lineage.world.object.item;

import java.sql.Connection;

import lineage.bean.database.Poly;
import lineage.bean.lineage.BuffInterface;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_ObjectPolyIcon;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.MagicDollController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public class Card extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Card();
		return item;
	}

	@Override
	public void close() {
		super.close();

	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!cha.isDead() && !cha.isWorldDelete() && cha.getInventory() != null && cha instanceof PcInstance) {
			PcInstance pc = (PcInstance) cha;
			if (cha.getMap() == 807) {
				ChattingController.toChatting(cha, "여기서는 사용 하실 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			// 팀대전, 낚시중에 변신 불가.
			if (!World.isTeamBattleMap(cha) && !cha.isFishing()) {
				BuffInterface temp = BuffController.find(cha, SkillDatabase.find(208));
				// 세트 아이템 착용 중 일 경우 변신 불가
				if (cha.getMap() != Lineage.teamBattleMap && cha.getMap() != Lineage.BattleRoyalMap && temp != null && temp.getTime() == -1) {
					if (!pc.isAutoHunt) {
						ChattingController.toChatting(cha, "세트 아이템 착용 중 일 경우 변신이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					}

					return;
				}

				String polyName = null;
				int time = getItem().getDuration();
				polyName = getItem().getPolyName();
				Poly p = PolyDatabase.getName(polyName);

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
						cha.toSender(S_ObjectPolyIcon.clone(BasePacketPooling.getPool(S_ObjectPolyIcon.class), time));
					// 버프등록
					BuffController.append(cha, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(208), time));
	
					cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 6082), true);

					// 아이템 수량 갱신
					if (!getItem().getName().contains("카드")) {
						cha.getInventory().count(this, getCount() - 1, true);
					}
				}
			}
		}

	}



}
