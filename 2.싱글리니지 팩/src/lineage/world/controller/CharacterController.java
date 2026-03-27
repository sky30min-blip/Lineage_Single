package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import all_night.Lineage_Balance;
import lineage.bean.database.Exp;
import lineage.bean.lineage.Inventory;
import lineage.database.DeadLostItemDatabase;
import lineage.database.EnchantLostItemDatabase;
import lineage.database.ExpDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.item.cloak.ElvenCloak;
import lineage.world.object.magic.Meditation;

public final class CharacterController {

	static private List<object> list;

	static public void init() {
		TimeLine.start("CharacterController..");

		list = new ArrayList<object>();

		TimeLine.end();
	}

	static public void toWorldJoin(object o) {
		synchronized (list) {
			if (!list.contains(o))
				list.add(o);
		}
		//		lineage.share.System.println("CharacterController toWorldJoin : "+list.size()+" -> "+cha.toString());
	}

	static public void toWorldOut(object o) {
		synchronized (list) {
			list.remove(o);
		}

		//		lineage.share.System.println("CharacterController toWorldOut : "+list.size()+" -> "+cha.toString());
	}

	static public List<object> getList() {
		synchronized (list) {
			return new ArrayList<object>(list);
		}
	}

	/**
	 * 객체가 이동할대마다 호출됨.
	 * 
	 * @param cha
	 */
	static public void toMoving(Character cha) {
		

		// 관련 버프 제거.
		if (cha.isBuffMeditation())
			BuffController.remove(cha, Meditation.class);
	}

	/**
	 * 타이머에서 주기적으로 호출.
	 * 
	 * @param time
	 *            : 진행중인 현재 시간.
	 */
	static public void toTimer(long time) {
	    // 처리할 객체 순회.
	    for (object o : getList()) {
	        // 자연회복 처리.
	        try {
	            if (o != null && !o.isDead()) {
	                if (o instanceof Character) {
	                    Character cha = (Character) o;
	                    ItemInstance item = null;
	                    int tic_hp = cha.isHpTic() ? cha.hpTic() : 0;
	                    int tic_mp = cha.isMpTic() ? cha.mpTic() : 0;
	                    // 사용자일때 확인하기.
	                    if (cha instanceof PcInstance) {
	                        // 인벤토리 무게오바일때
	                        if (cha.getInventory() != null && !cha.getInventory().isWeightPercent(50)) {
	                            // 여관맵이라면 피 차게해야됨.
	                            // 엑조틱 바이탈라이즈 시전중일때 차게 해야됨.
	                            // 여관맵이 아닐때.
	                            if (!InnController.isInnMap(cha) && !cha.isBuffExoticVitalize() && !cha.isBuffAdditionalFire()) {
	                                tic_hp = tic_mp = 0;
	                                // 요정족 망토를 착용중이라면 피차게 해야됨.
	                                item = cha.getInventory().getSlot(Lineage.SLOT_CLOAK);
	                                if (item != null && item instanceof ElvenCloak)
	                                    tic_hp = 1;
	                            }
	                        }
	                    }

	                    // 틱 처리.
	                    if (tic_hp > 0 && cha.getTotalHp() != cha.getNowHp())
	                        cha.setNowHp(cha.getNowHp() + tic_hp);
	                    if (tic_mp > 0 && cha.getTotalMp() != cha.getNowMp())
	                        cha.setNowMp(cha.getNowMp() + tic_mp);
	                }
	            }
	        } catch (Exception e) {
	            lineage.share.System.println("자연회복 처리.");
	            lineage.share.System.println(" : " + o.toString());
	            lineage.share.System.println(e);
	        }

	        // 주기적으로 호출에 사용.
	        try {
	            if (o != null) {
	                o.toTimer(time);
	            }
	        } catch (Exception e) {
	            lineage.share.System.println("주기적으로 호출에 사용.");
	            lineage.share.System.println(" : " + o.toString() + "  " + o.getName());
	            lineage.share.System.println(e);
	        }
	    }
	}
		
		
		

	/**
	 * 객체에 경험치 하향 처리 함수.
	 * 
	 * @param cha
	 * @param o
	 */
	static public void toExpDown(Character cha) {
		// 공격자에게 경험치 주기. toAttackObject
		if (cha.getLevel() > 1) {
			Exp e = ExpDatabase.find(cha.getLevel());
			
			if (e != null) {
				double exp = 0;

				// 경험치 감소할 값 추출 부분
				Object o = PluginController.init(CharacterController.class, "toExpDown.exp", cha, e);
				if (o != null) {
					exp = (Double) o;
				} else {
					exp = e.getExp() * Lineage.player_dead_expdown_rate;
				}

				// 레벨다운됫는지 확인부분.
				if (e.getBonus() - e.getExp() > cha.getExp() - exp) {
					// 레벨 하향
					cha.setLevel(cha.getLevel() - 1);
					// hp & mp 하향.
					int hp = cha.getMaxHp() - toStatusUP(cha, true);
					int mp = cha.getMaxMp() - toStatusUP(cha, false);
					cha.setMaxHp(hp);
					cha.setMaxMp(mp);
				}
				// 경험치 하향.
				cha.setExp(cha.getExp() - exp);
				
				if (cha instanceof PcInstance) {
					PcInstance pc = (PcInstance) cha;
					// 경험치 기록.
					pc.setLostExp(exp);
					// 패킷 처리.
					cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
				}
			}
		}
	}

	/**
	 * 해당 객체에 아이템을 드랍처리할때 사용하는 함수. : 현재는 사용자가 호출해서 사용함. : 추후에는 팻도 이구간을 이용할 수 있도록 작업해두면 좋을듯.
	 * 
	 * @param cha
	 */
	static public void toItemDrop(Character cha) {
		if (PluginController.init(CharacterController.class, "toItemDrop", cha) != null)
			return;


		int dropCount = 0;
		int dropChance = 0;
		List<ItemInstance> list_itemdrop = new ArrayList<ItemInstance>();
		
		int lawful = cha.getLawful() - Lineage.NEUTRAL;
		
		if (lawful < -29999) {
			// -30000 ~
			dropCount = Util.random(1, 2);
			dropChance = 60;
		} else if (lawful < -19999 && lawful > -30000) {
			// -20000 ~ -29999
			dropCount = Util.random(1, 1);
			dropChance = 50;
		} else if (lawful < 0 && lawful > -20000) {
			// -1 ~ -19999
			dropCount = Util.random(1, 1);
			dropChance = 40;
		} else if (lawful > 0 && lawful < 20000) {
			// 1 ~ 19999
			dropCount = Util.random(0, 1);
			dropChance = 25;
		} else if (lawful > 19999 && lawful < 30000) {
			// 20000 ~ 29999
			dropCount = Util.random(0, 1);
			dropChance = 20;
		} else if (lawful > 29999) {
			// 30000~
			dropCount = Util.random(0, 1);
			dropChance = 5;
			// 풀라우풀보다 낮을경우 드랍적용
			if (lawful == 32767) {
				dropCount = Util.random(0, 1);
				dropChance = 1;
			}
		}

		for (ItemInstance item : cha.getInventory().getList()) {
			if (item.getItem().isDrop() && item.getItem().getNameIdNumber() != 4 && dropChance > Util.random(1, 100) && item.getBless() >= 0 && dropCount > 0) {
				// 로봇일경우 착용중인거 드랍 안함.
				if (cha instanceof RobotInstance && item.isEquipped())
					continue;
				// 그외엔
				list_itemdrop.add(item);
				if (--dropCount <= 0)
					break;
			}
		}
		for (ItemInstance item : list_itemdrop) {
		    ChattingController.toChatting(cha, String.format("\\fY[캐릭터 사망 드랍] %s", item.toStringSearch()), Lineage.CHATTING_MODE_MESSAGE);

		    if (item.isEquipped())
		        item.toClick(cha, null);
		    
		    cha.getInventory().toDrop(item, item.getCount(), cha.getX(), cha.getY(), false);

		    
		    if (!item.getItem().getType1().equalsIgnoreCase("weapon") && !item.getItem().getType1().equalsIgnoreCase("armor")) {
		       
		        continue;
		    }

		
		    DeadLostItemDatabase.append(cha, item);
		    
		 
		    
		    
		}
	}

	/**
	 * 레벨업시 호출하며, hp&mp상승값 리턴함.
	 * 
	 * @param HpMp
	 *            : hp-true mp-false
	 * @return : 상승값.
	 */
	static public int toStatusUP(Character cha, boolean HpMp) {
		Object o = PluginController.init(CharacterController.class, "toStatusUP", cha, HpMp);
		if (o != null)
			return (Integer) o;

		int con = cha.getCon() + cha.getLvCon();
		int wis = cha.getWis() + cha.getLvWis();
		int start_hp = 0;
		int start_mp = 0;
		int temp = 0;
		int HPMP = 0;

		// 2017년 8월 3일 현재 리니지 파워북에 있는 스탯세팅 자료를 기반으로 완전 동일하게 수정. by all_night
		if (HpMp) { // hp
			if (cha instanceof PcInstance) {
				switch (cha.getClassType()) {
				case Lineage.LINEAGE_CLASS_ROYAL: // 군주
					if (con < 13)
						HPMP = Util.random(16, 17);
					else if (con < 14)
						HPMP = Util.random(17, 18);
					else if (con < 15)
						HPMP = Util.random(18, 19);
					else if (con < 16)
						HPMP = Util.random(19, 20);
					else if (con < 17)
						HPMP = Util.random(20, 21);
					else if (con < 18)
						HPMP = Util.random(21, 22);
					else if (con < 19)
						HPMP = Util.random(22, 23);
					else if (con < 20)
						HPMP = Util.random(23, 24);
					else if (con < 21)
						HPMP = Util.random(24, 25);
					else if (con < 22)
						HPMP = Util.random(25, 26);
					else if (con < 23)
						HPMP = Util.random(26, 27);
					else if (con < 24)
						HPMP = Util.random(27, 28);
					else if (con < 25)
						HPMP = Util.random(28, 29);
					else if (con < 27)
						HPMP = Util.random(29, 30);
					else if (con < 29)
						HPMP = Util.random(30, 31);
					else if (con < 31)
						HPMP = Util.random(31, 32);
					else if (con < 33)
						HPMP = Util.random(32, 33);
					else if (con < 35)
						HPMP = Util.random(33, 34);
					else if (con < 37)
						HPMP = Util.random(34, 35);
					else if (con < 39)
						HPMP = Util.random(35, 36);
					else if (con < 41)
						HPMP = Util.random(36, 37);
					else if (con < 43)
						HPMP = Util.random(37, 38);
					else if (con < 45)
						HPMP = Util.random(38, 39);
					else
						HPMP = Util.random(39, 40);
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT: // 기사
					if (con < 17)
						HPMP = Util.random(21, 22);
					else if (con < 18)
						HPMP = Util.random(22, 23);
					else if (con < 19)
						HPMP = Util.random(23, 24);
					else if (con < 20)
						HPMP = Util.random(24, 25);
					else if (con < 21)
						HPMP = Util.random(25, 26);
					else if (con < 22)
						HPMP = Util.random(26, 27);
					else if (con < 23)
						HPMP = Util.random(27, 28);
					else if (con < 24)
						HPMP = Util.random(28, 29);
					else if (con < 25)
						HPMP = Util.random(29, 30);
					else if (con < 27)
						HPMP = Util.random(30, 31);
					else if (con < 29)
						HPMP = Util.random(31, 32);
					else if (con < 31)
						HPMP = Util.random(32, 33);
					else if (con < 33)
						HPMP = Util.random(33, 34);
					else if (con < 35)
						HPMP = Util.random(34, 35);
					else if (con < 37)
						HPMP = Util.random(35, 36);
					else if (con < 39)
						HPMP = Util.random(36, 37);
					else if (con < 41)
						HPMP = Util.random(37, 38);
					else if (con < 43)
						HPMP = Util.random(38, 39);
					else if (con < 45)
						HPMP = Util.random(39, 40);
					else
						HPMP = Util.random(40, 41);
					break;
				case Lineage.LINEAGE_CLASS_ELF: // 요정
					if (con < 13)
						HPMP = Util.random(10, 11);
					else if (con < 14)
						HPMP = Util.random(11, 12);
					else if (con < 15)
						HPMP = Util.random(12, 13);
					else if (con < 16)
						HPMP = Util.random(13, 14);
					else if (con < 17)
						HPMP = Util.random(14, 15);
					else if (con < 18)
						HPMP = Util.random(15, 16);
					else if (con < 19)
						HPMP = Util.random(16, 17);
					else if (con < 20)
						HPMP = Util.random(17, 18);
					else if (con < 21)
						HPMP = Util.random(18, 19);
					else if (con < 22)
						HPMP = Util.random(19, 20);
					else if (con < 23)
						HPMP = Util.random(20, 21);
					else if (con < 24)
						HPMP = Util.random(21, 22);
					else if (con < 25)
						HPMP = Util.random(22, 23);
					else if (con < 27)
						HPMP = Util.random(23, 24);
					else if (con < 29)
						HPMP = Util.random(24, 25);
					else if (con < 31)
						HPMP = Util.random(25, 26);
					else if (con < 33)
						HPMP = Util.random(26, 27);
					else if (con < 35)
						HPMP = Util.random(27, 28);
					else if (con < 37)
						HPMP = Util.random(28, 29);
					else if (con < 39)
						HPMP = Util.random(29, 30);
					else if (con < 41)
						HPMP = Util.random(30, 31);
					else if (con < 43)
						HPMP = Util.random(31, 32);
					else if (con < 45)
						HPMP = Util.random(32, 33);
					else
						HPMP = Util.random(33, 34);
					break;
				case Lineage.LINEAGE_CLASS_DARKELF: // 다크엘프
					
					if (con < 13)
						HPMP = Util.random(10, 11);
					else if (con < 14)
						HPMP = Util.random(11, 12);
					else if (con < 15)
						HPMP = Util.random(12, 13);
					else if (con < 16)
						HPMP = Util.random(13, 14);
					else if (con < 17)
						HPMP = Util.random(14, 15);
					else if (con < 18)
						HPMP = Util.random(15, 16);
					else if (con < 19)
						HPMP = Util.random(16, 17);
					else if (con < 20)
						HPMP = Util.random(17, 18);
					else if (con < 21)
						HPMP = Util.random(18, 19);
					else if (con < 22)
						HPMP = Util.random(19, 20);
					else if (con < 23)
						HPMP = Util.random(20, 21);
					else if (con < 24)
						HPMP = Util.random(21, 22);
					else if (con < 25)
						HPMP = Util.random(22, 23);
					else if (con < 27)
						HPMP = Util.random(23, 24);
					else if (con < 29)
						HPMP = Util.random(24, 25);
					else if (con < 31)
						HPMP = Util.random(25, 26);
					else if (con < 33)
						HPMP = Util.random(26, 27);
					else if (con < 35)
						HPMP = Util.random(27, 28);
					else if (con < 37)
						HPMP = Util.random(28, 29);
					else if (con < 39)
						HPMP = Util.random(29, 30);
					else if (con < 41)
						HPMP = Util.random(30, 31);
					else if (con < 43)
						HPMP = Util.random(31, 32);
					else if (con < 45)
						HPMP = Util.random(32, 33);
					else
						HPMP = Util.random(33, 34);
					break;
				case Lineage.LINEAGE_CLASS_WIZARD: // 법사
					if (con < 13)
						HPMP = Util.random(7, 8);
					else if (con < 14)
						HPMP = Util.random(8, 9);
					else if (con < 15)
						HPMP = Util.random(9, 10);
					else if (con < 16)
						HPMP = Util.random(10, 11);
					else if (con < 17)
						HPMP = Util.random(11, 12);
					else if (con < 18)
						HPMP = Util.random(12, 13);
					else if (con < 19)
						HPMP = Util.random(13, 14);
					else if (con < 20)
						HPMP = Util.random(14, 15);
					else if (con < 21)
						HPMP = Util.random(15, 16);
					else if (con < 22)
						HPMP = Util.random(16, 17);
					else if (con < 23)
						HPMP = Util.random(17, 18);
					else if (con < 24)
						HPMP = Util.random(18, 19);
					else if (con < 25)
						HPMP = Util.random(19, 20);
					else if (con < 27)
						HPMP = Util.random(20, 21);
					else if (con < 29)
						HPMP = Util.random(21, 22);
					else if (con < 31)
						HPMP = Util.random(22, 23);
					else if (con < 33)
						HPMP = Util.random(23, 24);
					else if (con < 35)
						HPMP = Util.random(24, 25);
					else if (con < 37)
						HPMP = Util.random(25, 26);
					else if (con < 39)
						HPMP = Util.random(26, 27);
					else if (con < 41)
						HPMP = Util.random(27, 28);
					else if (con < 43)
						HPMP = Util.random(28, 29);
					else if (con < 45)
						HPMP = Util.random(29, 30);
					else
						HPMP = Util.random(30, 31);
					break;
				}
			} else if (cha instanceof SummonInstance) {
				temp = Util.random(1, 32);

				if (con <= 15)
					start_hp = 5;
				else
					start_hp = con - 10;

				if (temp <= 6) {
					start_hp += 1;
				} else if (temp <= 16) {
					start_hp += 2;
				} else if (temp <= 26) {
					start_hp += 3;
				} else if (temp <= 31) {
					start_hp += 4;
				} else {
					start_hp += 5;
				}
				HPMP = start_hp;
			}

		} else { // mp
			if (cha instanceof PcInstance) {
				switch (cha.getClassType()) {
				case Lineage.LINEAGE_CLASS_ROYAL: // 군주
					if (wis < 12)
						HPMP = Util.random(3, 4);
					else if (wis < 15)
						HPMP = Util.random(3, 5);
					else if (wis < 18)
						HPMP = Util.random(4, 6);
					else if (wis < 20)
						HPMP = Util.random(4, 7);
					else if (wis < 21)
						HPMP = Util.random(5, 7);
					else if (wis < 24)
						HPMP = Util.random(5, 8);
					else if (wis < 25)
						HPMP = Util.random(5, 9);
					else if (wis < 27)
						HPMP = Util.random(6, 9);
					else if (wis < 30)
						HPMP = Util.random(6, 10);
					else if (wis < 33)
						HPMP = Util.random(7, 11);
					else if (wis < 35)
						HPMP = Util.random(7, 12);
					else if (wis < 36)
						HPMP = Util.random(8, 12);
					else if (wis < 39)
						HPMP = Util.random(8, 13);
					else if (wis < 40)
						HPMP = Util.random(8, 14);
					else if (wis < 42)
						HPMP = Util.random(9, 14);
					else if (wis < 45)
						HPMP = Util.random(9, 15);
					else if (wis < 45)
						HPMP = Util.random(9, 15);
					else
						HPMP = Util.random(10, 16);
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT: // 기사
					if (wis < 10)
						HPMP = Util.random(0, 2);
					else if (wis < 15)
						HPMP = Util.random(1, 2);
					else if (wis < 18)
						HPMP = Util.random(2, 3);
					else if (wis < 24)
						HPMP = Util.random(2, 4);
					else if (wis < 27)
						HPMP = Util.random(3, 5);
					else if (wis < 30)
						HPMP = Util.random(3, 6);
					else if (wis < 33)
						HPMP = Util.random(4, 6);
					else if (wis < 36)
						HPMP = Util.random(4, 7);
					else if (wis < 40)
						HPMP = Util.random(4, 8);
					else if (wis < 42)
						HPMP = Util.random(5, 8);
					else if (wis < 45)
						HPMP = Util.random(5, 9);
					else
						HPMP = Util.random(6, 10);
					break;
				case Lineage.LINEAGE_CLASS_ELF: // 요정
					if (wis < 15)
						HPMP = Util.random(4, 7);
					else if (wis < 18)
						HPMP = Util.random(5, 8);
					else if (wis < 20)
						HPMP = Util.random(5, 10);
					else if (wis < 21)
						HPMP = Util.random(7, 10);
					else if (wis < 24)
						HPMP = Util.random(7, 11);
					else if (wis < 25)
						HPMP = Util.random(7, 13);
					else if (wis < 27)
						HPMP = Util.random(8, 13);
					else if (wis < 30)
						HPMP = Util.random(8, 14);
					else if (wis < 33)
						HPMP = Util.random(10, 16);
					else if (wis < 35)
						HPMP = Util.random(10, 17);
					else if (wis < 36)
						HPMP = Util.random(11, 17);
					else if (wis < 39)
						HPMP = Util.random(11, 19);
					else if (wis < 40)
						HPMP = Util.random(11, 20);
					else if (wis < 42)
						HPMP = Util.random(13, 20);
					else if (wis < 45)
						HPMP = Util.random(13, 22);
					else
						HPMP = Util.random(14, 23);
					break;
				case Lineage.LINEAGE_CLASS_DARKELF: // 다크엘프
					
					if (wis < 15)
						HPMP = Util.random(4, 7);
					else if (wis < 18)
						HPMP = Util.random(5, 8);
					else if (wis < 20)
						HPMP = Util.random(5, 10);
					else if (wis < 21)
						HPMP = Util.random(7, 10);
					else if (wis < 24)
						HPMP = Util.random(7, 11);
					else if (wis < 25)
						HPMP = Util.random(7, 13);
					else if (wis < 27)
						HPMP = Util.random(8, 13);
					else if (wis < 30)
						HPMP = Util.random(8, 14);
					else if (wis < 33)
						HPMP = Util.random(10, 16);
					else if (wis < 35)
						HPMP = Util.random(10, 17);
					else if (wis < 36)
						HPMP = Util.random(11, 17);
					else if (wis < 39)
						HPMP = Util.random(11, 19);
					else if (wis < 40)
						HPMP = Util.random(11, 20);
					else if (wis < 42)
						HPMP = Util.random(13, 20);
					else if (wis < 45)
						HPMP = Util.random(13, 22);
					else
						HPMP = Util.random(14, 23);
					break;
				case Lineage.LINEAGE_CLASS_WIZARD: // 법사
					if (wis < 15)
						HPMP = Util.random(6, 10);
					else if (wis < 18)
						HPMP = Util.random(8, 12);
					else if (wis < 20)
						HPMP = Util.random(8, 14);
					else if (wis < 21)
						HPMP = Util.random(10, 14);
					else if (wis < 24)
						HPMP = Util.random(10, 16);
					else if (wis < 25)
						HPMP = Util.random(10, 18);
					else if (wis < 27)
						HPMP = Util.random(12, 18);
					else if (wis < 30)
						HPMP = Util.random(12, 20);
					else if (wis < 33)
						HPMP = Util.random(14, 22);
					else if (wis < 35)
						HPMP = Util.random(14, 24);
					else if (wis < 36)
						HPMP = Util.random(16, 24);
					else if (wis < 39)
						HPMP = Util.random(16, 26);
					else if (wis < 40)
						HPMP = Util.random(16, 28);
					else if (wis < 42)
						HPMP = Util.random(18, 28);
					else if (wis < 45)
						HPMP = Util.random(18, 30);
					else
						HPMP = Util.random(20, 32);
					break;
				}
			} else if (cha instanceof SummonInstance) {
				if (wis <= 9) {
					temp = Util.random(1, 4);
					if (temp == 1) {
						start_mp = 0;
					} else if (temp <= 3) {
						start_mp = 1;
					} else {
						start_mp = 2;
					}
				} else {
					temp = Util.random(1, 4);
					if (temp == 1) {
						start_mp = 1;
					} else if (temp <= 3) {
						start_mp = 2;
					} else {
						start_mp = 3;
					}
				}

				HPMP = start_mp;
			}
		}
		
		if (cha instanceof PcInstance) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				if (HpMp)
					HPMP = (int) (HPMP * Lineage_Balance.level_up_hp_royal);
				else
					HPMP = (int) (HPMP * Lineage_Balance.level_up_mp_royal);	
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				if (HpMp)
					HPMP = (int) (HPMP * Lineage_Balance.level_up_hp_knight);
				else
					HPMP = (int) (HPMP * Lineage_Balance.level_up_mp_knight);	
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				if (HpMp)
					HPMP = (int) (HPMP * Lineage_Balance.level_up_hp_elf);
				else
					HPMP = (int) (HPMP * Lineage_Balance.level_up_mp_elf);	
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				if (HpMp)
					HPMP = (int) (HPMP * Lineage_Balance.level_up_hp_darkelf);
				else
					HPMP = (int) (HPMP * Lineage_Balance.level_up_mp_darkelf);	
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				if (HpMp)
					HPMP = (int) (HPMP * Lineage_Balance.level_up_hp_wizard);
				else
					HPMP = (int) (HPMP * Lineage_Balance.level_up_mp_wizard);	
				break;
			}
		}
		
		return HPMP;
	}
	
	static public int toStatusBaseUP(Character cha, boolean HpMp) {
		Object o = PluginController.init(CharacterController.class, "toStatusUP", cha, HpMp);
		if (o != null)
			return (Integer) o;

		int con = cha.getCon();
		int wis = cha.getWis();
		int start_hp = 0;
		int start_mp = 0;
		int temp = 0;
		int HPMP = 0;

		// 2017년 8월 3일 현재 리니지 파워북에 있는 스탯세팅 자료를 기반으로 완전 동일하게 수정. by all_night
		if (HpMp) { // hp
			if (cha instanceof PcInstance) {
				switch (cha.getClassType()) {
				case Lineage.LINEAGE_CLASS_ROYAL: // 군주
					if (con < 13)
						HPMP = Util.random(16, 17);
					else if (con < 14)
						HPMP = Util.random(17, 18);
					else if (con < 15)
						HPMP = Util.random(18, 19);
					else if (con < 16)
						HPMP = Util.random(19, 20);
					else if (con < 17)
						HPMP = Util.random(20, 21);
					else if (con < 18)
						HPMP = Util.random(21, 22);
					else if (con < 19)
						HPMP = Util.random(22, 23);
					else if (con < 20)
						HPMP = Util.random(23, 24);
					else if (con < 21)
						HPMP = Util.random(24, 25);
					else if (con < 22)
						HPMP = Util.random(25, 26);
					else if (con < 23)
						HPMP = Util.random(26, 27);
					else if (con < 24)
						HPMP = Util.random(27, 28);
					else if (con < 25)
						HPMP = Util.random(28, 29);
					else if (con < 27)
						HPMP = Util.random(29, 30);
					else if (con < 29)
						HPMP = Util.random(30, 31);
					else if (con < 31)
						HPMP = Util.random(31, 32);
					else if (con < 33)
						HPMP = Util.random(32, 33);
					else if (con < 35)
						HPMP = Util.random(33, 34);
					else if (con < 37)
						HPMP = Util.random(34, 35);
					else if (con < 39)
						HPMP = Util.random(35, 36);
					else if (con < 41)
						HPMP = Util.random(36, 37);
					else if (con < 43)
						HPMP = Util.random(37, 38);
					else if (con < 45)
						HPMP = Util.random(38, 39);
					else
						HPMP = Util.random(39, 40);
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT: // 기사
					if (con < 17)
						HPMP = Util.random(21, 22);
					else if (con < 18)
						HPMP = Util.random(22, 23);
					else if (con < 19)
						HPMP = Util.random(23, 24);
					else if (con < 20)
						HPMP = Util.random(24, 25);
					else if (con < 21)
						HPMP = Util.random(25, 26);
					else if (con < 22)
						HPMP = Util.random(26, 27);
					else if (con < 23)
						HPMP = Util.random(27, 28);
					else if (con < 24)
						HPMP = Util.random(28, 29);
					else if (con < 25)
						HPMP = Util.random(29, 30);
					else if (con < 27)
						HPMP = Util.random(30, 31);
					else if (con < 29)
						HPMP = Util.random(31, 32);
					else if (con < 31)
						HPMP = Util.random(32, 33);
					else if (con < 33)
						HPMP = Util.random(33, 34);
					else if (con < 35)
						HPMP = Util.random(34, 35);
					else if (con < 37)
						HPMP = Util.random(35, 36);
					else if (con < 39)
						HPMP = Util.random(36, 37);
					else if (con < 41)
						HPMP = Util.random(37, 38);
					else if (con < 43)
						HPMP = Util.random(38, 39);
					else if (con < 45)
						HPMP = Util.random(39, 40);
					else
						HPMP = Util.random(40, 41);
					break;
				case Lineage.LINEAGE_CLASS_ELF: // 요정
					if (con < 13)
						HPMP = Util.random(10, 11);
					else if (con < 14)
						HPMP = Util.random(11, 12);
					else if (con < 15)
						HPMP = Util.random(12, 13);
					else if (con < 16)
						HPMP = Util.random(13, 14);
					else if (con < 17)
						HPMP = Util.random(14, 15);
					else if (con < 18)
						HPMP = Util.random(15, 16);
					else if (con < 19)
						HPMP = Util.random(16, 17);
					else if (con < 20)
						HPMP = Util.random(17, 18);
					else if (con < 21)
						HPMP = Util.random(18, 19);
					else if (con < 22)
						HPMP = Util.random(19, 20);
					else if (con < 23)
						HPMP = Util.random(20, 21);
					else if (con < 24)
						HPMP = Util.random(21, 22);
					else if (con < 25)
						HPMP = Util.random(22, 23);
					else if (con < 27)
						HPMP = Util.random(23, 24);
					else if (con < 29)
						HPMP = Util.random(24, 25);
					else if (con < 31)
						HPMP = Util.random(25, 26);
					else if (con < 33)
						HPMP = Util.random(26, 27);
					else if (con < 35)
						HPMP = Util.random(27, 28);
					else if (con < 37)
						HPMP = Util.random(28, 29);
					else if (con < 39)
						HPMP = Util.random(29, 30);
					else if (con < 41)
						HPMP = Util.random(30, 31);
					else if (con < 43)
						HPMP = Util.random(31, 32);
					else if (con < 45)
						HPMP = Util.random(32, 33);
					else
						HPMP = Util.random(33, 34);
					break;
				case Lineage.LINEAGE_CLASS_DARKELF: // 다크엘프

					if (con < 13)
						HPMP = Util.random(12, 13);
					else if (con < 14)
						HPMP = Util.random(13, 14);
					else if (con < 15)
						HPMP = Util.random(14, 15);
					else if (con < 16)
						HPMP = Util.random(15, 16);
					else if (con < 17)
						HPMP = Util.random(16, 17);
					else if (con < 18)
						HPMP = Util.random(17, 18);
					else if (con < 19)
						HPMP = Util.random(18, 19);
					else if (con < 20)
						HPMP = Util.random(19, 20);
					else if (con < 21)
						HPMP = Util.random(20, 21);
					else if (con < 22)
						HPMP = Util.random(21, 22);
					else if (con < 23)
						HPMP = Util.random(22, 23);
					else if (con < 24)
						HPMP = Util.random(23, 24);
					else if (con < 25)
						HPMP = Util.random(24, 25);
					else if (con < 27)
						HPMP = Util.random(25, 26);
					else if (con < 29)
						HPMP = Util.random(26, 27);
					else if (con < 31)
						HPMP = Util.random(27, 28);
					else if (con < 33)
						HPMP = Util.random(28, 29);
					else if (con < 35)
						HPMP = Util.random(29, 30);
					else if (con < 37)
						HPMP = Util.random(30, 31);
					else if (con < 39)
						HPMP = Util.random(31, 32);
					else if (con < 41)
						HPMP = Util.random(32, 33);
					else if (con < 43)
						HPMP = Util.random(33, 34);
					else if (con < 45)
						HPMP = Util.random(34, 35);
					else
						HPMP = Util.random(34, 35);
					break;
				case Lineage.LINEAGE_CLASS_WIZARD: // 법사
					if (con < 13)
						HPMP = Util.random(7, 8);
					else if (con < 14)
						HPMP = Util.random(8, 9);
					else if (con < 15)
						HPMP = Util.random(9, 10);
					else if (con < 16)
						HPMP = Util.random(10, 11);
					else if (con < 17)
						HPMP = Util.random(11, 12);
					else if (con < 18)
						HPMP = Util.random(12, 13);
					else if (con < 19)
						HPMP = Util.random(13, 14);
					else if (con < 20)
						HPMP = Util.random(14, 15);
					else if (con < 21)
						HPMP = Util.random(15, 16);
					else if (con < 22)
						HPMP = Util.random(16, 17);
					else if (con < 23)
						HPMP = Util.random(17, 18);
					else if (con < 24)
						HPMP = Util.random(18, 19);
					else if (con < 25)
						HPMP = Util.random(19, 20);
					else if (con < 27)
						HPMP = Util.random(20, 21);
					else if (con < 29)
						HPMP = Util.random(21, 22);
					else if (con < 31)
						HPMP = Util.random(22, 23);
					else if (con < 33)
						HPMP = Util.random(23, 24);
					else if (con < 35)
						HPMP = Util.random(24, 25);
					else if (con < 37)
						HPMP = Util.random(25, 26);
					else if (con < 39)
						HPMP = Util.random(26, 27);
					else if (con < 41)
						HPMP = Util.random(27, 28);
					else if (con < 43)
						HPMP = Util.random(28, 29);
					else if (con < 45)
						HPMP = Util.random(29, 30);
					else
						HPMP = Util.random(30, 31);
					break;
				}
			} else if (cha instanceof SummonInstance) {
				temp = Util.random(1, 32);

				if (con <= 15)
					start_hp = 5;
				else
					start_hp = con - 10;

				if (temp <= 6) {
					start_hp += 1;
				} else if (temp <= 16) {
					start_hp += 2;
				} else if (temp <= 26) {
					start_hp += 3;
				} else if (temp <= 31) {
					start_hp += 4;
				} else {
					start_hp += 5;
				}
				HPMP = start_hp;
			}

		} else { // mp
			if (cha instanceof PcInstance) {
				switch (cha.getClassType()) {
				case Lineage.LINEAGE_CLASS_ROYAL: // 군주
					if (wis < 12)
						HPMP = Util.random(3, 4);
					else if (wis < 15)
						HPMP = Util.random(3, 5);
					else if (wis < 18)
						HPMP = Util.random(4, 6);
					else if (wis < 20)
						HPMP = Util.random(4, 7);
					else if (wis < 21)
						HPMP = Util.random(5, 7);
					else if (wis < 24)
						HPMP = Util.random(5, 8);
					else if (wis < 25)
						HPMP = Util.random(5, 9);
					else if (wis < 27)
						HPMP = Util.random(6, 9);
					else if (wis < 30)
						HPMP = Util.random(6, 10);
					else if (wis < 33)
						HPMP = Util.random(7, 11);
					else if (wis < 35)
						HPMP = Util.random(7, 12);
					else if (wis < 36)
						HPMP = Util.random(8, 12);
					else if (wis < 39)
						HPMP = Util.random(8, 13);
					else if (wis < 40)
						HPMP = Util.random(8, 14);
					else if (wis < 42)
						HPMP = Util.random(9, 14);
					else if (wis < 45)
						HPMP = Util.random(9, 15);
					else if (wis < 45)
						HPMP = Util.random(9, 15);
					else
						HPMP = Util.random(10, 16);
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT: // 기사
					if (wis < 10)
						HPMP = Util.random(0, 2);
					else if (wis < 15)
						HPMP = Util.random(1, 2);
					else if (wis < 18)
						HPMP = Util.random(2, 3);
					else if (wis < 24)
						HPMP = Util.random(2, 4);
					else if (wis < 27)
						HPMP = Util.random(3, 5);
					else if (wis < 30)
						HPMP = Util.random(3, 6);
					else if (wis < 33)
						HPMP = Util.random(4, 6);
					else if (wis < 36)
						HPMP = Util.random(4, 7);
					else if (wis < 40)
						HPMP = Util.random(4, 8);
					else if (wis < 42)
						HPMP = Util.random(5, 8);
					else if (wis < 45)
						HPMP = Util.random(5, 9);
					else
						HPMP = Util.random(6, 10);
					break;
				case Lineage.LINEAGE_CLASS_ELF: // 요정
					if (wis < 15)
						HPMP = Util.random(4, 7);
					else if (wis < 18)
						HPMP = Util.random(5, 8);
					else if (wis < 20)
						HPMP = Util.random(5, 10);
					else if (wis < 21)
						HPMP = Util.random(7, 10);
					else if (wis < 24)
						HPMP = Util.random(7, 11);
					else if (wis < 25)
						HPMP = Util.random(7, 13);
					else if (wis < 27)
						HPMP = Util.random(8, 13);
					else if (wis < 30)
						HPMP = Util.random(8, 14);
					else if (wis < 33)
						HPMP = Util.random(10, 16);
					else if (wis < 35)
						HPMP = Util.random(10, 17);
					else if (wis < 36)
						HPMP = Util.random(11, 17);
					else if (wis < 39)
						HPMP = Util.random(11, 19);
					else if (wis < 40)
						HPMP = Util.random(11, 20);
					else if (wis < 42)
						HPMP = Util.random(13, 20);
					else if (wis < 45)
						HPMP = Util.random(13, 22);
					else
						HPMP = Util.random(14, 23);
					break;
				case Lineage.LINEAGE_CLASS_DARKELF: // 다크엘프
					if (wis < 15)
						HPMP = Util.random(4, 7);
					else if (wis < 18)
						HPMP = Util.random(5, 8);
					else if (wis < 20)
						HPMP = Util.random(5, 10);
					else if (wis < 21)
						HPMP = Util.random(7, 10);
					else if (wis < 24)
						HPMP = Util.random(7, 11);
					else if (wis < 25)
						HPMP = Util.random(7, 13);
					else if (wis < 27)
						HPMP = Util.random(8, 13);
					else if (wis < 30)
						HPMP = Util.random(8, 14);
					else if (wis < 33)
						HPMP = Util.random(10, 16);
					else if (wis < 35)
						HPMP = Util.random(10, 17);
					else if (wis < 36)
						HPMP = Util.random(11, 17);
					else if (wis < 39)
						HPMP = Util.random(11, 19);
					else if (wis < 40)
						HPMP = Util.random(11, 20);
					else if (wis < 42)
						HPMP = Util.random(13, 20);
					else if (wis < 45)
						HPMP = Util.random(13, 22);
					else
						HPMP = Util.random(14, 23);
					break;
				case Lineage.LINEAGE_CLASS_WIZARD: // 법사
					if (wis < 15)
						HPMP = Util.random(6, 10);
					else if (wis < 18)
						HPMP = Util.random(8, 12);
					else if (wis < 20)
						HPMP = Util.random(8, 14);
					else if (wis < 21)
						HPMP = Util.random(10, 14);
					else if (wis < 24)
						HPMP = Util.random(10, 16);
					else if (wis < 25)
						HPMP = Util.random(10, 18);
					else if (wis < 27)
						HPMP = Util.random(12, 18);
					else if (wis < 30)
						HPMP = Util.random(12, 20);
					else if (wis < 33)
						HPMP = Util.random(14, 22);
					else if (wis < 35)
						HPMP = Util.random(14, 24);
					else if (wis < 36)
						HPMP = Util.random(16, 24);
					else if (wis < 39)
						HPMP = Util.random(16, 26);
					else if (wis < 40)
						HPMP = Util.random(16, 28);
					else if (wis < 42)
						HPMP = Util.random(18, 28);
					else if (wis < 45)
						HPMP = Util.random(18, 30);
					else
						HPMP = Util.random(20, 32);
					break;
				}
			} else if (cha instanceof SummonInstance) {
				if (wis <= 9) {
					temp = Util.random(1, 4);
					if (temp == 1) {
						start_mp = 0;
					} else if (temp <= 3) {
						start_mp = 1;
					} else {
						start_mp = 2;
					}
				} else {
					temp = Util.random(1, 4);
					if (temp == 1) {
						start_mp = 1;
					} else if (temp <= 3) {
						start_mp = 2;
					} else {
						start_mp = 3;
					}
				}

				HPMP = start_mp;
			}
		}
		
		if (cha instanceof PcInstance) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				if (HpMp)
					HPMP = (int) (HPMP * Lineage_Balance.level_up_hp_royal);
				else
					HPMP = (int) (HPMP * Lineage_Balance.level_up_mp_royal);	
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				if (HpMp)
					HPMP = (int) (HPMP * Lineage_Balance.level_up_hp_knight);
				else
					HPMP = (int) (HPMP * Lineage_Balance.level_up_mp_knight);	
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				if (HpMp)
					HPMP = (int) (HPMP * Lineage_Balance.level_up_hp_elf);
				else
					HPMP = (int) (HPMP * Lineage_Balance.level_up_mp_elf);	
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				if (HpMp)
					HPMP = (int) (HPMP * Lineage_Balance.level_up_hp_darkelf);
				else
					HPMP = (int) (HPMP * Lineage_Balance.level_up_mp_darkelf);	
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				if (HpMp)
					HPMP = (int) (HPMP * Lineage_Balance.level_up_hp_wizard);
				else
					HPMP = (int) (HPMP * Lineage_Balance.level_up_mp_wizard);	
				break;
			}
		}
		
		return HPMP;
	}
	
	static public void toResetStat(PcInstance pc, int classType) {
		// 기초 스탯 초기화.
		switch (classType) {
		case Lineage.LINEAGE_CLASS_ROYAL: // 군주
			pc.setStr(Lineage.royal_stat_str);
			pc.setDex(Lineage.royal_stat_dex);
			pc.setCon(Lineage.royal_stat_con);
			pc.setInt(Lineage.royal_stat_int);
			pc.setWis(Lineage.royal_stat_wis);
			pc.setCha(Lineage.royal_stat_cha);
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT: // 기사
			pc.setStr(Lineage.knight_stat_str);
			pc.setDex(Lineage.knight_stat_dex);
			pc.setCon(Lineage.knight_stat_con);
			pc.setInt(Lineage.knight_stat_int);
			pc.setWis(Lineage.knight_stat_wis);
			pc.setCha(Lineage.knight_stat_cha);
			break;
		case Lineage.LINEAGE_CLASS_ELF: // 요정
			pc.setStr(Lineage.elf_stat_str);
			pc.setDex(Lineage.elf_stat_dex);
			pc.setCon(Lineage.elf_stat_con);
			pc.setInt(Lineage.elf_stat_int);
			pc.setWis(Lineage.elf_stat_wis);
			pc.setCha(Lineage.elf_stat_cha);
			break;
		case Lineage.LINEAGE_CLASS_DARKELF: // 요정
			pc.setStr(Lineage.elf_stat_str);
			pc.setDex(Lineage.elf_stat_dex);
			pc.setCon(Lineage.elf_stat_con);
			pc.setInt(Lineage.elf_stat_int);
			pc.setWis(Lineage.elf_stat_wis);
			pc.setCha(Lineage.elf_stat_cha);
			break;
		case Lineage.LINEAGE_CLASS_WIZARD: // 마법사
			pc.setStr(Lineage.wizard_stat_str);
			pc.setDex(Lineage.wizard_stat_dex);
			pc.setCon(Lineage.wizard_stat_con);
			pc.setInt(Lineage.wizard_stat_int);
			pc.setWis(Lineage.wizard_stat_wis);
			pc.setCha(Lineage.wizard_stat_cha);
			break;
		}
		
		pc.setResetBaseStat(75 - (pc.getStr() + pc.getDex() + pc.getCon() + pc.getInt() + pc.getWis() + pc.getCha()));
		
		if (pc.getLevel() > 50)
			pc.setResetLevelStat((pc.getLevel() - 50) + pc.getElixir());

		
		

		// 레벨업 스탯 초기화
		pc.setLvStr(0);
		pc.setLvDex(0);
		pc.setLvCon(0);
		pc.setLvWis(0);
		pc.setLvCha(0);
		pc.setLvInt(0);
		pc.setLevelUpStat(0);
		// HP/MP 초기화
		int hp = 0;
		int mp = 0;
		
		switch (classType) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			hp += Lineage.royal_hp;
			mp += Lineage.royal_mp;
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			hp += Lineage.knight_hp;
			mp += Lineage.knight_mp;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			hp += Lineage.elf_hp;
			mp += Lineage.elf_mp;
			break;
		case Lineage.LINEAGE_CLASS_DARKELF:
			hp += Lineage.darkelf_hp;
			mp += Lineage.darkelf_mp;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			hp += Lineage.wizard_hp;
			mp += Lineage.wizard_mp;
			break;
		}

		pc.setMaxHp(hp);
		pc.setNowHp(hp);
		pc.setMaxMp(mp);
		pc.setNowMp(mp);
	}

	static public int toStatStr(Character cha, String type) {
		int str = cha.getTotalStr();
		// 근거리 대미지
		int dmg = cha.getDynamicAddDmg() + cha.getLevel() / 10;
		// 근거리 명중
		int hit = cha.getDynamicAddHit();
		// 근거리 치명타
		int critical = cha.getDynamicCritical();
		// 최대 소지 무게
		int maxWeight = 0;

		if (str < 9) {
			dmg += 2;
			hit += 5;
		} else if (str < 10) {
			dmg += 2;
			hit += 6;
		} else if (str < 11) {
			dmg += 3;
			hit += 6;
			maxWeight += 100;
		} else if (str < 12) {
			dmg += 3;
			hit += 7;
			maxWeight += 100;
		} else if (str < 14) {
			dmg += 4;
			hit += 8;
			maxWeight += 200;
		} else if (str < 15) {
			dmg += 5;
			hit += 9;
			maxWeight += 300;
		} else if (str < 16) {
			dmg += 5;
			hit += 10;
			maxWeight += 300;
		} else if (str < 17) {
			dmg += 6;
			hit += 10;
			maxWeight += 400;
		} else if (str < 18) {
			dmg += 6;
			hit += 11;
			maxWeight += 400;
		} else if (str < 20) {
			dmg += 7;
			hit += 12;
			maxWeight += 500;
		} else if (str < 21) {
			dmg += 8;
			hit += 13;
			maxWeight += 600;
		} else if (str < 22) {
			dmg += 8;
			hit += 14;
			maxWeight += 600;
		} else if (str < 23) {
			dmg += 9;
			hit += 14;
			maxWeight += 700;
		} else if (str < 24) {
			dmg += 9;
			hit += 15;
			maxWeight += 700;
		} else if (str < 25) {
			dmg += 10;
			hit += 16;
			maxWeight += 800;
		} else if (str < 26) {
			dmg += 11;
			hit += 17;
			maxWeight += 800;
		} else if (str < 27) {
			dmg += 12;
			hit += 18;
			maxWeight += 900;
		} else if (str < 28) {
			dmg += 12;
			hit += 19;
			maxWeight += 900;
		} else if (str < 29) {
			dmg += 13;
			hit += 19;
			maxWeight += 1000;
		} else if (str < 30) {
			dmg += 13;
			hit += 20;
			maxWeight += 1000;
		} else if (str < 32) {
			dmg += 14;
			hit += 21;
			maxWeight += 1100;
		} else if (str < 33) {
			dmg += 15;
			hit += 22;
			maxWeight += 1200;
		} else if (str < 34) {
			dmg += 15;
			hit += 23;
			maxWeight += 1200;
		} else if (str < 35) {
			dmg += 16;
			hit += 23;
			maxWeight += 1300;
		} else if (str < 36) {
			dmg += 17;
			hit += 25;
			maxWeight += 1300;
		} else if (str < 38) {
			dmg += 18;
			hit += 26;
			maxWeight += 1400;
		} else if (str < 39) {
			dmg += 19;
			hit += 27;
			maxWeight += 1500;
		} else if (str < 40) {
			dmg += 19;
			hit += 28;
			maxWeight += 1500;
		} else if (str < 41) {
			dmg += 20;
			hit += 28;
			critical += 1;
			maxWeight += 1600;
		} else if (str < 42) {
			dmg += 20;
			hit += 29;
			critical += 1;
			maxWeight += 1600;
		} else if (str < 44) {
			dmg += 21;
			hit += 30;
			critical += 1;
			maxWeight += 1700;
		} else if (str < 45) {
			dmg += 22;
			hit += 31;
			critical += 1;
			maxWeight += 1800;
		} else if (str < 46) {
			dmg += 25;
			hit += 35;
			critical += 2;
			maxWeight += 1800;
		} else if (str < 47) {
			dmg += 26;
			hit += 36;
			critical += 2;
			maxWeight += 1900;
		} else if (str < 48) {
			dmg += 26;
			hit += 37;
			critical += 2;
			maxWeight += 1900;
		} else if (str < 49) {
			dmg += 27;
			hit += 37;
			critical += 2;
			maxWeight += 2000;
		} else if (str < 50) {
			dmg += 27;
			hit += 38;
			critical += 2;
			maxWeight += 2000;
		} else if (str < 51) {
			dmg += 28;
			hit += 38;
			critical += 3;
			maxWeight += 2100;
		} else if (str < 52) {
			dmg += 28;
			hit += 39;
			critical += 3;
			maxWeight += 2100;
		} else if (str < 53) {
			dmg += 29;
			hit += 39;
			critical += 3;
			maxWeight += 2200;
		} else if (str < 54) {
			dmg += 29;
			hit += 40;
			critical += 3;
			maxWeight += 2200;
		} else if (str < 55) {
			dmg += 30;
			hit += 40;
			critical += 3;
			maxWeight += 2300;
		} else if (str < 56) {
			dmg += 32;
			hit += 42;
			critical += 4;
			maxWeight += 2400;
		} else if (str < 57) {
			dmg += 33;
			hit += 43;
			critical += 4;
			maxWeight += 2500;
		} else if (str < 58) {
			dmg += 33;
			hit += 43;
			critical += 4;
			maxWeight += 2500;
		} else if (str < 59) {
			dmg += 34;
			hit += 44;
			critical += 4;
			maxWeight += 2600;
		} else if (str < 60) {
			dmg += 34;
			hit += 45;
			critical += 4;
			maxWeight += 2600;
		} else if (str < 61) {
			dmg += 35;
			hit += 45;
			critical += 5;
			maxWeight += 2700;
		} else if (str < 62) {
			dmg += 35;
			hit += 46;
			critical += 5;
			maxWeight += 2700;
		} else if (str < 63) {
			dmg += 36;
			hit += 45;
			critical += 5;
			maxWeight += 2800;
		} else if (str < 64) {
			dmg += 36;
			hit += 46;
			critical += 5;
			maxWeight += 2800;
		} else if (str < 65) {
			dmg += 37;
			hit += 46;
			critical += 6;
			maxWeight += 2900;
		} else {
			dmg += 38;
			hit += 47;
			critical += 7;
			maxWeight += 3000;
		}
		
		if (type.equalsIgnoreCase("DmgFigure")) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				
				return (int) Math.round(dmg * Lineage_Balance.royal_damage_figure);
			case Lineage.LINEAGE_CLASS_KNIGHT:
				return (int) Math.round(dmg * Lineage_Balance.knight_damage_figure);
			case Lineage.LINEAGE_CLASS_ELF:
				return (int) Math.round(dmg * Lineage_Balance.elf_damage_figure);
			case Lineage.LINEAGE_CLASS_DARKELF:
				return (int) Math.round(dmg * Lineage_Balance.darkelf_damage_figure);
			case Lineage.LINEAGE_CLASS_WIZARD:
				return (int) Math.round(dmg * Lineage_Balance.wizard_damage_figure);
			default:
				return dmg;
			}
		}

		if (type.equalsIgnoreCase("isHitFigure")) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				return (int) Math.round(hit * Lineage_Balance.royal_hit_figure);
			case Lineage.LINEAGE_CLASS_KNIGHT:
				return (int) Math.round(hit * Lineage_Balance.knight_hit_figure);
			case Lineage.LINEAGE_CLASS_ELF:
				return (int) Math.round(hit * Lineage_Balance.elf_hit_figure);
			case Lineage.LINEAGE_CLASS_DARKELF:
				return (int) Math.round(hit * Lineage_Balance.darkelf_hit_figure);
			case Lineage.LINEAGE_CLASS_WIZARD:
				return (int) Math.round(hit * Lineage_Balance.wizard_hit_figure);
			default:
				return hit;
			}
		}
		
		if (type.equalsIgnoreCase("Critical")) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				return (int) Math.round(critical * Lineage_Balance.royal_critical_figure);
			case Lineage.LINEAGE_CLASS_KNIGHT:
				return (int) Math.round(critical * Lineage_Balance.knight_critical_figure);
			case Lineage.LINEAGE_CLASS_ELF:
				return (int) Math.round(critical * Lineage_Balance.elf_critical_figure);
			case Lineage.LINEAGE_CLASS_DARKELF:
				return (int) Math.round(critical * Lineage_Balance.darkelf_critical_figure);
			case Lineage.LINEAGE_CLASS_WIZARD:
				return (int) Math.round(critical * Lineage_Balance.wizard_critical_figure);
			default:
				return critical;
			}
		}
		
		if (type.equalsIgnoreCase("getMaxWeight")) {
			return cha.getClassType() == Lineage.LINEAGE_CLASS_KNIGHT ? maxWeight + 200: maxWeight;
		}

		return 0;

	}

	static public int toStatDex(Character cha, String type) {
		int dex = cha.getTotalDex();
		// 원거리 대미지
		int dmg = cha.getDynamicAddDmgBow() + cha.getLevel() / 10;
		// 원거리 명중
		int hit = cha.getDynamicAddHitBow();
		// 원거리 치명타
		int critical = cha.getDynamicBowCritical();

		if (dex < 8) {
			dmg += 2;
			hit += -3;
		} else if (dex < 9) {
			dmg += 2;
			hit += -2;
		} else if (dex < 10) {
			dmg += 3;
			hit += -1;
		} else if (dex < 11) {
			dmg += 3;
			hit += 0;
		} else if (dex < 12) {
			dmg += 3;
			hit += 1;
		} else if (dex < 13) {
			dmg += 4;
			hit += 2;
		} else if (dex < 14) {
			dmg += 4;
			hit += 3;
		} else if (dex < 15) {
			dmg += 4;
			hit += 4;
		} else if (dex < 16) {
			dmg += 5;
			hit += 5;
		} else if (dex < 17) {
			dmg += 5;
			hit += 6;
		} else if (dex < 18) {
			dmg += 5;
			hit += 7;
		} else if (dex < 19) {
			dmg += 6;
			hit += 8;
		} else if (dex < 20) {
			dmg += 6;
			hit += 9;
		} else if (dex < 21) {
			dmg += 6;
			hit += 10;
		} else if (dex < 22) {
			dmg += 7;
			hit += 11;
		} else if (dex < 23) {
			dmg += 7;
			hit += 12;
		} else if (dex < 24) {
			dmg += 7;
			hit += 13;
		} else if (dex < 25) {
			dmg += 8;
			hit += 14;
		} else if (dex < 26) {
			dmg += 9;
			hit += 16;
		} else if (dex < 27) {
			dmg += 9;
			hit += 17;
		} else if (dex < 28) {
			dmg += 10;
			hit += 18;
		} else if (dex < 29) {
			dmg += 10;
			hit += 19;
		} else if (dex < 30) {
			dmg += 10;
			hit += 20;
		} else if (dex < 31) {
			dmg += 11;
			hit += 21;
		} else if (dex < 32) {
			dmg += 11;
			hit += 22;
		} else if (dex < 33) {
			dmg += 11;
			hit += 23;
		} else if (dex < 34) {
			dmg += 12;
			hit += 24;
		} else if (dex < 35) {
			dmg += 12;
			hit += 25;
		} else if (dex < 36) {
			dmg += 13;
			hit += 27;
		} else if (dex < 37) {
			dmg += 14;
			hit += 28;
		} else if (dex < 38) {
			dmg += 14;
			hit += 29;
		} else if (dex < 39) {
			dmg += 14;
			hit += 30;
		} else if (dex < 40) {
			dmg += 15;
			hit += 31;
		} else if (dex < 41) {
			dmg += 15;
			hit += 32;
			critical += 1;
		} else if (dex < 42) {
			dmg += 15;
			hit += 33;
			critical += 1;
		} else if (dex < 43) {
			dmg += 16;
			hit += 34;
			critical += 1;
		} else if (dex < 44) {
			dmg += 16;
			hit += 35;
			critical += 1;
		} else if (dex < 45) {
			dmg += 16;
			hit += 36;
			critical += 1;
		} else if (dex < 46) {
			dmg += 20;
			hit += 40;
			critical += 2;
		} else if (dex < 47) {
			dmg += 21;
			hit += 41;
			critical += 2;
		} else if (dex < 48) {
			dmg += 21;
			hit += 42;
			critical += 2;
		} else if (dex < 49) {
			dmg += 22;
			hit += 43;
			critical += 2;
		} else if (dex < 50) {
			dmg += 22;
			hit += 43;
			critical += 2;
		} else if (dex < 51) {
			dmg += 23;
			hit += 44;
			critical += 3;
		} else if (dex < 52) {
			dmg += 23;
			hit += 44;
			critical += 3;
		} else if (dex < 53) {
			dmg += 24;
			hit += 45;
			critical += 3;
		} else if (dex < 54) {
			dmg += 24;
			hit += 45;
			critical += 3;
		} else if (dex < 55) {
			dmg += 25;
			hit += 46;
			critical += 3;
		} else if (dex < 56) {
			dmg += 27;
			hit += 48;
			critical += 4;
		} else if (dex < 57) {
			dmg += 28;
			hit += 49;
			critical += 4;
		} else if (dex < 58) {
			dmg += 28;
			hit += 50;
			critical += 4;
		} else if (dex < 59) {
			dmg += 29;
			hit += 50;
			critical += 4;
		} else if (dex < 60) {
			dmg += 29;
			hit += 51;
			critical += 4;
		} else if (dex < 61) {
			dmg += 30;
			hit += 51;
			critical += 4;
		} else if (dex < 62) {
			dmg += 30;
			hit += 52;
			critical += 5;
		} else if (dex < 63) {
			dmg += 31;
			hit += 52;
			critical += 5;
		} else if (dex < 64) {
			dmg += 31;
			hit += 53;
			critical += 5;
		} else if (dex < 65) {
			dmg += 32;
			hit += 53;
			critical += 5;
		} else {
			dmg += 33;
			hit += 54;
			critical += 6;
		}

		if (type.equalsIgnoreCase("DmgFigure")) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				return (int) Math.round(dmg * Lineage_Balance.royal_bow_damage_figure);
			case Lineage.LINEAGE_CLASS_KNIGHT:
				return (int) Math.round(dmg * Lineage_Balance.knight_bow_damage_figure);
			case Lineage.LINEAGE_CLASS_ELF:
				return (int) Math.round(dmg * Lineage_Balance.elf_bow_damage_figure);
			case Lineage.LINEAGE_CLASS_DARKELF:
				return (int) Math.round(dmg * Lineage_Balance.darkelf_bow_damage_figure);
			case Lineage.LINEAGE_CLASS_WIZARD:
				return (int) Math.round(dmg * Lineage_Balance.wizard_bow_damage_figure);
			default:
				return dmg;
			}
		}

		if (type.equalsIgnoreCase("isHitFigure")) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				return (int) Math.round(hit * Lineage_Balance.royal_bow_hit_figure);
			case Lineage.LINEAGE_CLASS_KNIGHT:
				return (int) Math.round(hit * Lineage_Balance.knight_bow_hit_figure);
			case Lineage.LINEAGE_CLASS_ELF:
				return (int) Math.round(hit * Lineage_Balance.elf_bow_hit_figure);
			case Lineage.LINEAGE_CLASS_DARKELF:
				return (int) Math.round(hit * Lineage_Balance.darkelf_bow_damage_figure);
			case Lineage.LINEAGE_CLASS_WIZARD:
				return (int) Math.round(hit * Lineage_Balance.wizard_bow_hit_figure);
			default:
				return hit;
			}
		}

		if (type.equalsIgnoreCase("Critical")) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				return (int) Math.round(critical * Lineage_Balance.royal_bow_critical_figure);
			case Lineage.LINEAGE_CLASS_KNIGHT:
				return (int) Math.round(critical * Lineage_Balance.knight_bow_critical_figure);
			case Lineage.LINEAGE_CLASS_ELF:
				return (int) Math.round(critical * Lineage_Balance.elf_bow_critical_figure);
			case Lineage.LINEAGE_CLASS_DARKELF:
					return (int) Math.round(critical * Lineage_Balance.darkelf_bow_critical_figure);
			case Lineage.LINEAGE_CLASS_WIZARD:
				return (int) Math.round(critical * Lineage_Balance.wizard_bow_critical_figure);
			default:
				return critical;
			}
		}

		return 0;
	}

	static public int toStatCon(Character cha, String type) {
		int con = cha.getTotalCon();
		// HP 회복(틱)
		int hpTic = 0;
		// HP 물약 회복 증가
		int hpPotion = 0;	
		// 최대 소지 무게
		int maxWeight = 0;
		
		if (con < 12) {
			hpTic += 5;
		} else if (con < 13) {
			hpTic += 6;
		} else if (con < 14) {
			hpTic += 6;
			maxWeight += 100;
		} else if (con < 15) {
			hpTic += 7;
			maxWeight += 100;
		} else if (con < 16) {
			hpTic += 7;
			maxWeight += 200;
		} else if (con < 17) {
			hpTic += 8;
			maxWeight += 200;
		} else if (con < 18) {
			hpTic += 8;
			maxWeight += 300;
		} else if (con < 19) {
			hpTic += 9;
			maxWeight += 300;
		} else if (con < 20) {
			hpTic += 9;
			maxWeight += 400;
		} else if (con < 21) {
			hpTic += 10;
			hpPotion += 1;
			maxWeight += 400;
		} else if (con < 22) {
			hpTic += 10;
			hpPotion += 1;
			maxWeight += 500;
		} else if (con < 23) {
			hpTic += 11;
			hpPotion += 1;
			maxWeight += 500;
		} else if (con < 24) {
			hpTic += 11;
			hpPotion += 1;
			maxWeight += 600;
		} else if (con < 25) {
			hpTic += 12;
			hpPotion += 1;
			maxWeight += 600;
		} else if (con < 26) {
			hpTic += 13;
			hpPotion += 1;
			maxWeight += 700;
		} else if (con < 27) {
			hpTic += 14;
			hpPotion += 1;
			maxWeight += 700;
		} else if (con < 28) {
			hpTic += 14;
			hpPotion += 1;
			maxWeight += 800;
		} else if (con < 29) {
			hpTic += 15;
			hpPotion += 1;
			maxWeight += 800;
		} else if (con < 30) {
			hpTic += 15;
			hpPotion += 1;
			maxWeight += 900;
		} else if (con < 31) {
			hpTic += 16;
			hpPotion += 2;
			maxWeight += 900;
		} else if (con < 32) {
			hpTic += 16;
			hpPotion += 2;
			maxWeight += 1000;
		} else if (con < 33) {
			hpTic += 17;
			hpPotion += 2;
			maxWeight += 1000;
		} else if (con < 34) {
			hpTic += 17;
			hpPotion += 2;
			maxWeight += 1100;
		} else if (con < 35) {
			hpTic += 18;
			hpPotion += 2;
			maxWeight += 1100;
		} else if (con < 36) {
			hpTic += 19;
			hpPotion += 3;
			maxWeight += 1200;
		} else if (con < 37) {
			hpTic += 20;
			hpPotion += 3;
			maxWeight += 1200;
		} else if (con < 38) {
			hpTic += 20;
			hpPotion += 3;
			maxWeight += 1300;
		} else if (con < 39) {
			hpTic += 21;
			hpPotion += 3;
			maxWeight += 1300;
		} else if (con < 40) {
			hpTic += 21;
			hpPotion += 3;
			maxWeight += 1400;
		} else if (con < 41) {
			hpTic += 22;
			hpPotion += 4;
			maxWeight += 1400;
		} else if (con < 42) {
			hpTic += 22;
			hpPotion += 4;
			maxWeight += 1500;
		} else if (con < 43) {
			hpTic += 23;
			hpPotion += 4;
			maxWeight += 1500;
		} else if (con < 44) {
			hpTic += 23;
			hpPotion += 4;
			maxWeight += 1600;
		} else if (con < 45) {
			hpTic += 24;
			hpPotion += 4;
			maxWeight += 1600;
		} else if (con < 46) {
			hpTic += 27;
			hpPotion += 6;
			maxWeight += 1700;
		} else if (con < 47) {
			hpTic += 28;
			hpPotion += 7;
			maxWeight += 1800;
		} else if (con < 48) {
			hpTic += 29;
			hpPotion += 7;
			maxWeight += 1900;
		} else if (con < 49) {
			hpTic += 30;
			hpPotion += 7;
			maxWeight += 2000;
		} else if (con < 50) {
			hpTic += 31;
			hpPotion += 7;
			maxWeight += 2100;
		} else if (con < 51) {
			hpTic += 32;
			hpPotion += 7;
			maxWeight += 2200;
		} else if (con < 52) {
			hpTic += 33;
			hpPotion += 8;
			maxWeight += 2300;
		} else if (con < 53) {
			hpTic += 34;
			hpPotion += 8;
			maxWeight += 2400;
		} else if (con < 54) {
			hpTic += 35;
			hpPotion += 8;
			maxWeight += 2500;
		} else if (con < 55) {
			hpTic += 36;
			hpPotion += 8;
			maxWeight += 2600;
		} else if (con < 56) {
			hpTic += 37;
			hpPotion += 10;
			maxWeight += 2700;
		} else if (con < 57) {
			hpTic += 38;
			hpPotion += 11;
			maxWeight += 2700;
		} else if (con < 58) {
			hpTic += 38;
			hpPotion += 11;
			maxWeight += 2800;
		} else if (con < 59) {
			hpTic += 39;
			hpPotion += 11;
			maxWeight += 2800;
		} else if (con < 60) {
			hpTic += 40;
			hpPotion += 11;
			maxWeight += 2900;
		} else {
			hpTic += 41;
			hpPotion += 12;
			maxWeight += 3000;
		}
		
		switch (cha.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			hpTic *= Lineage_Balance.royal_hp_tic_figure;
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			hpTic *= Lineage_Balance.knight_hp_tic_figure;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			hpTic *= Lineage_Balance.elf_hp_tic_figure;
			break;
		case Lineage.LINEAGE_CLASS_DARKELF:
			hpTic *= Lineage_Balance.darkelf_hp_tic_figure;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			hpTic *= Lineage_Balance.wizard_hp_tic_figure;
			break;
		}
		
		if (type.equalsIgnoreCase("hpTic"))
			return hpTic;
		
		if (type.equalsIgnoreCase("HealingPotion"))
			return hpPotion;
		
		if (type.equalsIgnoreCase("getMaxWeight"))
			return maxWeight;

		return 0;
	}

	static public int toStatInt(Character cha, String type) {
		int statInt = cha.getTotalInt();
		// 마법 대미지
		int magicDamage = cha.getDynamicMagicDmg();
		// 마법 명중
		int magicHit = cha.getDynamicMagicHit();
		// 마법 치명타
		int magicCritical = cha.getDynamicMagicCritical();
		// 마법 보너스
		int magicBonus = 0;
		// MP 소모 감소
		int mpDecrease = 0;

		if (statInt < 9) {
			magicHit += -4;
			magicBonus += 2;
			mpDecrease += 5;
		} else if (statInt < 11) {
			magicHit += -3;
			magicBonus += 2;
			mpDecrease += 6;
		} else if (statInt < 12) {
			magicHit += -3;
			magicBonus += 2;
			mpDecrease += 7;
		} else if (statInt < 14) {
			magicHit += -2;
			magicBonus += 3;
			mpDecrease += 8;
		} else if (statInt < 15) {
			magicHit += -2;
			magicBonus += 3;
			mpDecrease += 9;
		} else if (statInt < 16) {
			magicDamage += 1;
			magicHit += -1;
			magicBonus += 3;
			mpDecrease += 10;
		} else if (statInt < 17) {
			magicDamage += 1;
			magicHit += -1;
			magicBonus += 4;
			mpDecrease += 10;
		} else if (statInt < 18) {
			magicDamage += 1;
			magicHit += -1;
			magicBonus += 4;
			mpDecrease += 11;
		} else if (statInt < 20) {
			magicDamage += 1;
			magicBonus += 4;
			mpDecrease += 12;
		} else if (statInt < 21) {
			magicDamage += 2;
			magicBonus += 5;
			mpDecrease += 13;
		} else if (statInt < 23) {
			magicDamage += 2;
			magicBonus += 5;
			mpDecrease += 14;
		} else if (statInt < 24) {
			magicDamage += 2;
			magicHit += 1;
			magicBonus += 5;
			mpDecrease += 15;
		} else if (statInt < 25) {
			magicDamage += 2;
			magicHit += 1;
			magicBonus += 6;
			mpDecrease += 16;
		} else if (statInt < 26) {
			magicDamage += 4;
			magicHit += 2;
			magicBonus += 6;
			mpDecrease += 16;
		} else if (statInt < 27) {
			magicDamage += 4;
			magicHit += 3;
			magicBonus += 6;
			mpDecrease += 17;
		} else if (statInt < 28) {
			magicDamage += 4;
			magicHit += 3;
			magicBonus += 6;
			mpDecrease += 18;
		} else if (statInt < 29) {
			magicDamage += 4;
			magicHit += 3;
			magicBonus += 7;
			mpDecrease += 18;
		} else if (statInt < 30) {
			magicDamage += 4;
			magicHit += 4;
			magicBonus += 7;
			mpDecrease += 19;
		} else if (statInt < 32) {
			magicDamage += 5;
			magicHit += 4;
			magicBonus += 7;
			mpDecrease += 20;
		} else if (statInt < 33) {
			magicDamage += 5;
			magicHit += 5;
			magicBonus += 8;
			mpDecrease += 21;
		} else if (statInt < 35) {
			magicDamage += 5;
			magicHit += 5;
			magicBonus += 8;
			mpDecrease += 22;
		} else if (statInt < 36) {
			magicDamage += 7;
			magicHit += 7;
			magicCritical += 1;
			magicBonus += 8;
			mpDecrease += 23;
		} else if (statInt < 38) {
			magicDamage += 7;
			magicHit += 7;
			magicCritical += 1;
			magicBonus += 9;
			mpDecrease += 24;
		} else if (statInt < 39) {
			magicDamage += 7;
			magicHit += 8;
			magicCritical += 1;
			magicBonus += 9;
			mpDecrease += 25;
		} else if (statInt < 40) {
			magicDamage += 7;
			magicHit += 8;
			magicCritical += 1;
			magicBonus += 9;
			mpDecrease += 26;
		} else if (statInt < 41) {
			magicDamage += 8;
			magicHit += 8;
			magicCritical += 2;
			magicBonus += 10;
			mpDecrease += 26;
		} else if (statInt < 42) {
			magicDamage += 8;
			magicHit += 9;
			magicCritical += 2;
			magicBonus += 10;
			mpDecrease += 27;
		} else if (statInt < 44) {
			magicDamage += 8;
			magicHit += 9;
			magicCritical += 2;
			magicBonus += 10;
			mpDecrease += 28;
		} else if (statInt < 45) {
			magicDamage += 8;
			magicHit += 10;
			magicCritical += 2;
			magicBonus += 11;
			mpDecrease += 29;
		} else if (statInt < 46) {
			magicDamage += 12;
			magicHit += 13;
			magicCritical += 4;
			magicBonus += 12;
			mpDecrease += 30;
		} else if (statInt < 47) {
			magicDamage += 12;
			magicHit += 13;
			magicCritical += 4;
			magicBonus += 12;
			mpDecrease += 30;
		} else if (statInt < 48) {
			magicDamage += 12;
			magicHit += 14;
			magicCritical += 4;
			magicBonus += 12;
			mpDecrease += 31;
		} else if (statInt < 49) {
			magicDamage += 12;
			magicHit += 14;
			magicCritical += 4;
			magicBonus += 13;
			mpDecrease += 31;
		} else if (statInt < 50) {
			magicDamage += 12;
			magicHit += 14;
			magicCritical += 4;
			magicBonus += 13;
			mpDecrease += 32;
		} else if (statInt < 51) {
			magicDamage += 13;
			magicHit += 15;
			magicCritical += 5;
			magicBonus += 13;
			mpDecrease += 33;
		} else if (statInt < 52) {
			magicDamage += 13;
			magicHit += 15;
			magicCritical += 5;
			magicBonus += 13;
			mpDecrease += 33;
		} else if (statInt < 53) {
			magicDamage += 13;
			magicHit += 15;
			magicCritical += 5;
			magicBonus += 14;
			mpDecrease += 34;
		} else if (statInt < 54) {
			magicDamage += 13;
			magicHit += 16;
			magicCritical += 5;
			magicBonus += 14;
			mpDecrease += 34;
		} else if (statInt < 55) {
			magicDamage += 13;
			magicHit += 16;
			magicCritical += 5;
			magicBonus += 14;
			mpDecrease += 35;
		} else if (statInt < 56) {
			magicDamage += 14;
			magicHit += 16;
			magicCritical += 6;
			magicBonus += 14;
			mpDecrease += 36;
		} else if (statInt < 57) {
			magicDamage += 14;
			magicHit += 17;
			magicCritical += 6;
			magicBonus += 15;
			mpDecrease += 36;
		} else if (statInt < 58) {
			magicDamage += 14;
			magicHit += 17;
			magicCritical += 6;
			magicBonus += 15;
			mpDecrease += 37;
		} else if (statInt < 59) {
			magicDamage += 14;
			magicHit += 17;
			magicCritical += 6;
			magicBonus += 15;
			mpDecrease += 38;
		} else if (statInt < 60) {
			magicDamage += 14;
			magicHit += 18;
			magicCritical += 6;
			magicBonus += 15;
			mpDecrease += 38;
		} else if (statInt < 61) {
			magicDamage += 15;
			magicHit += 18;
			magicCritical += 6;
			magicBonus += 16;
			mpDecrease += 39;
		} else if (statInt < 62) {
			magicDamage += 16;
			magicHit += 18;
			magicCritical += 6;
			magicBonus += 16;
			mpDecrease += 39;
		} else if (statInt < 63) {
			magicDamage += 16;
			magicHit += 19;
			magicCritical += 6;
			magicBonus += 16;
			mpDecrease += 39;
		} else if (statInt < 64) {
			magicDamage += 17;
			magicHit += 19;
			magicCritical += 6;
			magicBonus += 17;
			mpDecrease += 40;
		} else if (statInt < 64) {
			magicDamage += 17;
			magicHit += 20;
			magicCritical += 6;
			magicBonus += 17;
			mpDecrease += 40;
		} else {
			magicDamage += 18;
			magicHit += 21;
			magicCritical += 7;
			magicBonus += 18;
			mpDecrease += 45;
		}	
		
		if (type.equalsIgnoreCase("magicDamage")) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				return (int) Math.round(magicDamage * Lineage_Balance.royal_magic_damage_figure);
			case Lineage.LINEAGE_CLASS_KNIGHT:
				return (int) Math.round(magicDamage * Lineage_Balance.knight_magic_damage_figure);
			case Lineage.LINEAGE_CLASS_ELF:
				return (int) Math.round(magicDamage * Lineage_Balance.elf_magic_damage_figure);
			case Lineage.LINEAGE_CLASS_DARKELF:
				return (int) Math.round(magicDamage * Lineage_Balance.darkelf_magic_damage_figure);
			case Lineage.LINEAGE_CLASS_WIZARD:
				return (int) Math.round(magicDamage * Lineage_Balance.wizard_magic_damage_figure);
			default:
				return magicDamage;
			}
		}
		
		if (type.equalsIgnoreCase("magicHit")) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				return (int) Math.round(magicHit * Lineage_Balance.royal_magic_hit_figure);
			case Lineage.LINEAGE_CLASS_KNIGHT:
				return (int) Math.round(magicHit * Lineage_Balance.knight_magic_hit_figure);
			case Lineage.LINEAGE_CLASS_ELF:
				return (int) Math.round(magicHit * Lineage_Balance.elf_magic_hit_figure);
			case Lineage.LINEAGE_CLASS_DARKELF:
				return (int) Math.round(magicHit * Lineage_Balance.darkelf_magic_hit_figure);
			case Lineage.LINEAGE_CLASS_WIZARD:
				return (int) Math.round(magicHit * Lineage_Balance.wizard_magic_hit_figure);
			default:
				return magicHit;
			}
		}
		
		if (type.equalsIgnoreCase("magicCritical")) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				return (int) Math.round(magicCritical * Lineage_Balance.royal_magic_critical_figure);
			case Lineage.LINEAGE_CLASS_KNIGHT:
				return (int) Math.round(magicCritical * Lineage_Balance.knight_magic_critical_figure);
			case Lineage.LINEAGE_CLASS_ELF:
				return (int) Math.round(magicCritical * Lineage_Balance.elf_magic_critical_figure);
			case Lineage.LINEAGE_CLASS_DARKELF:
				return (int) Math.round(magicCritical * Lineage_Balance.darkelf_magic_critical_figure);
			case Lineage.LINEAGE_CLASS_WIZARD:
				return (int) Math.round(magicCritical * Lineage_Balance.wizard_magic_critical_figure);
			default:
				return magicCritical;
			}
		}
		
		if (type.equalsIgnoreCase("magicBonus")) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				return (int) Math.round(magicBonus * Lineage_Balance.royal_magic_bonus_figure);
			case Lineage.LINEAGE_CLASS_KNIGHT:
				return (int) Math.round(magicBonus * Lineage_Balance.knight_magic_bonus_figure);
			case Lineage.LINEAGE_CLASS_ELF:
				return (int) Math.round(magicBonus * Lineage_Balance.elf_magic_bonus_figure);
			case Lineage.LINEAGE_CLASS_DARKELF:
				return (int) Math.round(magicBonus * Lineage_Balance.darkelf_magic_bonus_figure);
			case Lineage.LINEAGE_CLASS_WIZARD:
				return (int) Math.round(magicBonus * Lineage_Balance.wizard_magic_bonus_figure);
			default:
				return magicBonus;
			}
		}
		
		if (type.equalsIgnoreCase("mpDecrease"))
			return mpDecrease;
		
		return 0;
	}

	static public int toStatWis(Character cha, String type) {
		int wis = cha.getTotalWis();
		// MP 회복(틱)
		int mpTic = 0;
		
		if (wis < 10) {
			mpTic += 1;
		} else if (wis < 11) {
			mpTic += 2;
		} else if (wis < 14) {
			mpTic += 2;
		} else if (wis < 15) {
			mpTic += 2;
		} else if (wis < 16) {
			mpTic += 3;
		} else if (wis < 18) {
			mpTic += 3;
		} else if (wis < 20) {
			mpTic += 3;
		} else if (wis < 22) {
			mpTic += 4;
		} else if (wis < 24) {
			mpTic += 4;
		} else if (wis < 25) {
			mpTic += 4;
		} else if (wis < 26) {
			mpTic += 6;
		} else if (wis < 28) {
			mpTic += 6;
		} else if (wis < 30) {
			mpTic += 6;
		} else if (wis < 32) {
			mpTic += 7;
		} else if (wis < 34) {
			mpTic += 7;
		} else if (wis < 35) {
			mpTic += 7;
		} else if (wis < 36) {
			mpTic += 9;
		} else if (wis < 38) {
			mpTic += 9;
		} else if (wis < 40) {
			mpTic += 9;
		} else if (wis < 42) {
			mpTic += 10;
		} else if (wis < 44) {
			mpTic += 10;
		} else if (wis < 45) {
			mpTic += 10;
		} else {
			mpTic += 14;
		}
		
		switch (cha.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			mpTic *= Lineage_Balance.royal_mp_tic_figure;
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			mpTic *= Lineage_Balance.knight_mp_tic_figure;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			mpTic *= Lineage_Balance.elf_mp_tic_figure;
			break;
		case Lineage.LINEAGE_CLASS_DARKELF:
			mpTic *= Lineage_Balance.darkelf_mp_tic_figure;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			mpTic *= Lineage_Balance.wizard_mp_tic_figure;
			break;
		}
		
		if (type.equalsIgnoreCase("mpTic"))
			return mpTic;

		return 0;
	}

	static public int toStatCha(Character cha, String type) {
		int charisma = cha.getTotalCha();

		return 0;
	}
	
	static public void toResetBaseStat(PcInstance pc) {
		int hp = 0;
		int mp = 0;
		int maxLev = 51;
		
		if (pc.getLevel() < maxLev) {
			maxLev = pc.getLevel();
		}
		
		for (int i = 2; i <= maxLev; i++) {
			hp += CharacterController.toStatusUP(pc, true);
			mp += CharacterController.toStatusUP(pc, false);
		}

		switch (pc.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			hp += Lineage.royal_hp;
			mp += Lineage.royal_mp;
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			hp += Lineage.knight_hp;
			mp += Lineage.knight_mp;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			hp += Lineage.elf_hp;
			mp += Lineage.elf_mp;
			break;
		case Lineage.LINEAGE_CLASS_DARKELF:
			hp += Lineage.darkelf_hp;
			mp += Lineage.darkelf_mp;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			hp += Lineage.wizard_hp;
			mp += Lineage.wizard_mp;
			break;
		}

		pc.setMaxHp(hp);
		pc.setNowHp(hp);
		pc.setMaxMp(mp);
		pc.setNowMp(mp);
		
//		pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
//		pc.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), pc));
	}
	
	static public void toResetLevelStat(PcInstance pc) {
		int hp = 0;
		int mp = 0;
		
		hp += CharacterController.toStatusUP(pc, true);
		mp += CharacterController.toStatusUP(pc, false);

		pc.setMaxHp(pc.getMaxHp() + hp);
		pc.setNowHp(pc.getNowHp() + hp);
		pc.setMaxMp(pc.getMaxMp() + mp);
		pc.setNowMp(pc.getNowHp() + mp);
		
//		pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
//		pc.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), pc));
	}
}