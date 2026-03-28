package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Poly;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Inventory;
import lineage.database.ItemMaplewandDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_ObjectPolyIcon;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class ShapeChange extends Magic {

	public ShapeChange(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new ShapeChange(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o.isWorldDelete())
			return;

		o.setGfx(o.getClassGfx());
		if (o.getInventory() != null && o.getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
			o.setGfxMode(o.getClassGfxMode() + o.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode());
		else
			o.setGfxMode(o.getClassGfxMode());
		o.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), o), true);
		ChattingController.toChatting(o, "\\fY변신 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY변신: "+getTime()+"초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, Skill skill, int object_id) {
		// 초기화
		object o = null;
		// 타겟 찾기
		if (object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList(object_id);
		// 처리
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if (SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)) {
				// 변신
				onBuff(cha, o, null, skill.getBuffDuration(), true, true);
				// 이팩트
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			}
		}
	}

	static public void init(Character cha, int time) {
		if (Lineage.server_version > 182)
			cha.toSender(S_ObjectPolyIcon.clone(BasePacketPooling.getPool(S_ObjectPolyIcon.class), time));
		BuffController.append(cha, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(208), time));
	}

	/**
	 * 몬스터가 사용하는 변신 처리 함수. : 몬스터 몇셀 주변에 있는 모든 사용자들을 강제 변신시킴. : 리치가 사용중.
	 * 
	 * @param mi
	 */
	static public void init(MonsterInstance mi, int loc, Poly poly, int time) {
		for (object o : mi.getInsideList()) {
			if (o instanceof PcInstance) {
				// 변신
				onBuff(mi, o, poly, time, true, true);
			}
		}
	}

	/**
	 * 변신주문서 에서 호출해서 사용중..
	 * 
	 * @param cha
	 * @param target
	 * @param p
	 * @param time
	 */
	static public boolean init2(Character cha, Character target, Poly p, int time, int bress) {
	    if (p != null) {
	        onBuff(cha, target, p, time, false, true);
	    } else {
	        BuffController.remove(cha, ShapeChange.class);
	    }

	    return true;
	}
	
	/**
	 * 변신주문서 에서 호출해서 사용중..
	 * 
	 * @param cha
	 * @param target
	 * @param p
	 * @param time
	 */
	public static boolean init(Character cha, Character target, Poly p, int time, int bress) {
	    Inventory inventory = cha.getInventory();
	    
	    if (inventory == null || p == null) {
	        BuffController.remove(cha, ShapeChange.class);
	        return true;
	    }
	    
	    boolean isRingPoly = false;
	    ItemInstance r1 = inventory.getSlot(Lineage.SLOT_RING_LEFT);
	    ItemInstance r2 = inventory.getSlot(Lineage.SLOT_RING_RIGHT);
	    
	    int transformationLevel = p.getMinLevel();
	    
	    if (cha instanceof PcInstance && !((PcInstance) cha).isTempPoly()) {
	        isRingPoly = true;
	    }
	    
	    boolean hasVIPRing1 = r1 != null && r1.getItem().getName().contains("변신 조종 반지");
	    boolean hasVIPRing2 = r2 != null && r2.getItem().getName().contains("변신 조종 반지");
	    boolean hasAncientRing1 = r1 != null && r1.getItem().getName().contains("고대의 반지");
	    boolean hasAncientRing2 = r2 != null && r2.getItem().getName().contains("고대의 반지");

	    // Check if both rings are "변신 조종 반지" or "고대의 반지"
	    boolean hasTwoVIPRings = (hasVIPRing1 && hasVIPRing2);
	    boolean hasTwoAncientRings = (hasAncientRing1 && hasAncientRing2);

	    boolean hasAnyRing = hasVIPRing1 || hasVIPRing2 || hasAncientRing1 || hasAncientRing2;
	    boolean hasMixedRings = (hasVIPRing1 && (hasAncientRing2 || hasAncientRing1)) || (hasVIPRing2 && (hasAncientRing1 || hasAncientRing2));

	    int ringPenalty = 0;

	    if (hasMixedRings || hasAnyRing) {
	        // Apply a penalty for wearing either "변신 조종 반지" or "고대의 반지"
	        ringPenalty = 1;
	    }

	    if (hasTwoVIPRings || hasTwoAncientRings || hasMixedRings) {
	        // Apply an additional penalty for wearing both types of rings
	        ringPenalty += 1;
	    }

	    transformationLevel -= ringPenalty;

	    
	    boolean canTransform = transformationLevel <= cha.getLevel() ||
	                           Lineage.event_poly ||
	                           cha.getGm() > 0 || cha.getMap() == Lineage.teamBattleMap;
	    
	    if (canTransform) {
	    	if (!Lineage.item_polymorph_bless && bress == 0)
	        time += 600;
	    	
	        onBuff(cha, target, p, time, false, true);
	    } else {
	        String message;
	        if (hasTwoVIPRings || hasVIPRing1 || hasVIPRing2 || hasAncientRing1 || hasAncientRing2) {
	            int levelDifference = transformationLevel - cha.getLevel();
	            String penaltyMessage = "";

	            if (hasTwoVIPRings) {
	                penaltyMessage = "변신 조종 반지를 두 개 모두 착용하여 변신 레벨이 2 감소합니다.";
	            } else if (hasTwoAncientRings) {
	                penaltyMessage = "고대의 반지를 두 개 모두 착용하여 변신 레벨이 2 감소합니다.";
	            } else if (hasMixedRings || hasAnyRing) {
	                penaltyMessage = "반지를 섞어서 착용하거나 반지를 하나 이상 착용하여 변신 레벨이 1 감소합니다.";
	            }

	            message = String.format("%s: %d레벨 이상 변신 가능. %d레벨이 부족합니다. %s", p.getPolyName(), p.getMinLevel(), levelDifference, penaltyMessage);
	        } else {
	            message = String.format("%s: %d레벨 이상 변신 가능", p.getPolyName(), p.getMinLevel());
	        }
	        ChattingController.toChatting(cha, message, Lineage.CHATTING_MODE_MESSAGE);
	        return false;
	    }
	    
	    return true;
	}

	/**
	 * 변신 최종 뒷처리 구간 : 마법을 통해서 이쪽으로 옴. : 변막을 통해서 이쪽으로 옴.
	 * 
	 * @param cha
	 * @param o
	 * @param time
	 */
	static public void onBuff(Character cha, object o, Poly p, int time, boolean ring, boolean packet) {
		if (cha == null || o == null || !(o instanceof PcInstance))
			return;
		
//		BuffInterface temp = BuffController.find(cha, SkillDatabase.find(208));
//		// 세트 아이템 착용 중 일 경우 변신 불가
//		if (cha.getMap() != Lineage.teamBattleMap && cha.getMap() != Lineage.BattleRoyalMap && temp != null && temp.getTime() == -1) {
//			ChattingController.toChatting(cha, "세트 아이템 착용 중 일 경우 변신이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
//			return;
//		}
		
		if (cha.getMap() != Lineage.teamBattleMap && cha.getMap() != Lineage.BattleRoyalMap && cha.isSetPoly) {
			ChattingController.toChatting(cha, "세트 아이템 착용 중 일 경우 변신이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (o.getInventory() != null && ring && o.getInventory().isRingOfPolymorphControl()) {
			// 변신할 괴물의 이름을 넣으십시오.
			if (packet) {
				List<String> quickPolymorph = new ArrayList<String>();
				quickPolymorph.add(cha.getQuickPolymorph() == null || cha.getQuickPolymorph().equalsIgnoreCase("") || cha.getQuickPolymorph().length() < 1 ? "빠른 변신 목록 없음" : cha.getQuickPolymorph());
				cha.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 180));
				object monlistAnchor = NpcSpawnlistDatabase.polyMonlistNpc != null ? NpcSpawnlistDatabase.polyMonlistNpc : cha;
				cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), monlistAnchor, "monlist", null, quickPolymorph));
			}
		} else {
			PcInstance pc = (PcInstance) o;
			if (p == null)
				p = ItemMaplewandDatabase.randomPoly();
			if (p != null && !o.isDead()) {
				if (o instanceof Character) {
					// 장비 해제.
					PolyDatabase.toEquipped((Character) o, p);
				}

				// 변신
				o.setGfx(p.getGfxId());

				if (Lineage.is_weapon_speed) {
					if (!o.checkSpear()) {
						ItemInstance weapon = o.getInventory().getSlot(Lineage.SLOT_WEAPON);

						if (weapon != null && weapon.getItem() != null && SpriteFrameDatabase.findGfxMode(o.getGfx(), weapon.getItem().getGfxMode() + Lineage.GFX_MODE_ATTACK)) {
							o.setGfxMode(weapon.getItem().getGfxMode());
						} else {
							o.setGfxMode(p.getGfxMode());
						}
					}
				} else {
					o.setGfxMode(p.getGfxMode());
				}

				if (packet) {
					o.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), o), true);
					if (Lineage.server_version > 182)
						o.toSender(S_ObjectPolyIcon.clone(BasePacketPooling.getPool(S_ObjectPolyIcon.class), time));
				}
				// 버프등록
				BuffController.append(o, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(208), time));

				// 일반 변신 이팩트 6082, 단풍 나무 변신 6130
				if (!p.getName().contains("세트") && !p.getName().contains("운영자") && !p.getName().contains("좀비 변신")) {
					// o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, 6082), true);
					// 아이템 수량 갱신
					if (pc.getTempPolyScroll() != null)
						pc.getInventory().count(pc.getTempPolyScroll(), pc.getTempPolyScroll().getCount() - 1, true);
					pc.setTempPoly(false);
					pc.setTempPolyScroll(null);
					if (!p.getName().contains("랭커"))
						pc.setQuickPolymorph(p.getName());
				}
			} else {
				if (packet)
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
			}
		}
	}

	/**
	 * 세트 아이템 변신이 있을경우 여기에서 처리
	 */
	static public void onBuff(Character cha, object o, Poly p, int time, boolean packet) {
		if (cha == null || o == null || !(o instanceof PcInstance))
			return;

		if (cha.getMap() == Lineage.teamBattleMap || cha.getMap() == Lineage.BattleRoyalMap)
			return;

		if (p == null)
			p = ItemMaplewandDatabase.randomPoly();
		if (p != null && !o.isDead()) {
			if (o instanceof Character)
				// 장비 해제.
				PolyDatabase.toEquipped((Character) o, p);
			// 변신
			o.setGfx(p.getGfxId());
			
			if (Lineage.is_weapon_speed) {
				if (!cha.checkSpear()) {
					ItemInstance weapon = o.getInventory().getSlot(Lineage.SLOT_WEAPON);
					
					if (weapon != null && weapon.getItem() != null && SpriteFrameDatabase.findGfxMode(o.getGfx(), weapon.getItem().getGfxMode() + Lineage.GFX_MODE_ATTACK)) {
						o.setGfxMode(weapon.getItem().getGfxMode());
					} else {
						if (o.getInventory().getSlot(Lineage.SLOT_WEAPON) != null && o instanceof Character) {
							Character c = (Character) o;
							c.getInventory().getSlot(Lineage.SLOT_WEAPON).toClick(c, null);
							ChattingController.toChatting(c, "현재 착용중인 무기는 해당 변신으로 착용 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						}		
						o.setGfxMode(p.getGfxMode());
					}
				}
			} else {
				o.setGfxMode(p.getGfxMode());
			}
			
			if (packet) {
				o.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), o), true);
				if (Lineage.server_version > 182)
					o.toSender(S_ObjectPolyIcon.clone(BasePacketPooling.getPool(S_ObjectPolyIcon.class), time));
			}
			// 버프등록
			BuffController.append(o, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(208), time));

		} else {
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
		}
	}

}
