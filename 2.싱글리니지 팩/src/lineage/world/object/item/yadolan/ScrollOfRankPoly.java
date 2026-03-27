package lineage.world.object.item.yadolan;

import lineage.bean.database.Poly;
import lineage.bean.lineage.BuffInterface;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_ObjectPolyIcon;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.RankController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public class ScrollOfRankPoly extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollOfRankPoly();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!cha.isDead() && !cha.isWorldDelete() && cha.getInventory() != null && cha instanceof PcInstance) {
			PcInstance pc = (PcInstance) cha;
			int allRank = RankController.getAllRank(cha.getObjectId());
			int classRank = RankController.getClassRank(cha.getObjectId(), cha.getClassType());
			
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

				// 전체랭킹 20위 또는 클래스랭킹 3위는 랭커변신
				if ((((allRank > 0 && allRank <= Lineage.rank_poly_all) || 
					(classRank > 0 && classRank <= Lineage.rank_poly_class)) && cha.getLevel() >= Lineage.rank_min_level) ||   cha.getGm() > 0) {
					switch (cha.getClassType()) {
					case 0:
						if (cha.getClassSex() == 0)
							polyName = "왕자 랭커";
						else
							polyName = "공주 랭커";
						break;
					case 1:
						if (cha.getClassSex() == 0)
							polyName = "남자기사 랭커";
						else
							polyName = "여자기사 랭커";
						break;
					case 2:
						if (cha.getClassSex() == 0)
							polyName = "남자요정 랭커";
						else
							polyName = "여자요정 랭커";
						break;
					case 3:
						if (cha.getClassSex() == 0)
							polyName = "남자법사 랭커";
						else
							polyName = "여자법사 랭커";
						break;
					}			
				} else {
					ChattingController.toChatting(cha, String.format("전체 랭킹 %d위 이내 또는 클래스 랭킹 %d위 이내 변신 가능", Lineage.rank_poly_all, Lineage.rank_poly_class), Lineage.CHATTING_MODE_MESSAGE);
				}

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
					if (!getItem().getName().contains("무한")) {
						cha.getInventory().count(this, getCount() - 1, true);
					}
				}
			}
		}
	}
}
