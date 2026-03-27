package lineage.network.packet.server;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import kuberaitem.결혼반지;
import kuberaitem.기마투구;
import lineage.bean.database.Item;
import lineage.bean.database.ItemSetoption;
import lineage.database.CharacterMarbleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemSetoptionDatabase;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.PowerballController;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.item.Candle;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.Letter;
import lineage.world.object.item.MagicDoll;
import lineage.world.object.item.armor.Turban;
import lineage.world.object.item.weapon.Arrow;

public class S_Inventory extends ServerBasePacket {

	protected void toArmor(ItemInstance item) {
		if (item.getItem().getName().equalsIgnoreCase("절대 반지") || item.getItem().getNameIdNumber() == 431)
			toEtc(item.getItem(), item.getWeight());
		else
			toArmor(item.getItem(), (ItemArmorInstance) item, item.getDurability(), item.getEnLevel(), item.getWeight(), item.getDynamicMr(), item.getBless(), item.getDynamicStunDefence());

	}

	protected void toWeapon(ItemInstance item) {
		if (item.getItem().getType2().equalsIgnoreCase("fishing_rod"))
			toEtc(item.getItem(), item.getWeight());
		else
			toWeapon(item.getItem(), item, item.getDurability(), item.getEnLevel(), item.getWeight(), item.getBless());
	}

	protected void toEtc(ItemInstance item) {
		if (item.getItem().getNameIdNumber() == 1173) {
			DogCollar dc = (DogCollar) item;
			writeC(0x0f); // 15
			writeC(0x19);
			writeH(dc.getPetClassId());
			writeC(0x1a);
			writeH(dc.getPetLevel());
			writeC(0x1f);
			writeH(dc.getPetHp());
			writeC(0x17);
			writeC(item.getItem().getMaterial());
			writeD(item.getWeight());
		} else {
			if (!item.getItem().getType2().equalsIgnoreCase("sword_lack") && !item.getItem().getType2().equalsIgnoreCase("자동 칼질") && !item.getItem().getName().contains("기운을 잃은"))
				toEtc(item.getItem(), item.getWeight());
		}
	}

	protected void toArmor(Item item, ItemArmorInstance armor, int durability, int enlevel, int weight, int dynamic_mr, int bless, double dynamic_stun_defence) {
		ItemSetoption setoption = Lineage.server_version >= 200 ? ItemSetoptionDatabase.find(item.getSetId()) : null;

		ItemInstance temp = null;
		temp = ItemDatabase.newInstance(item);
		if (temp != null) {
			temp.setEnLevel(enlevel);
			temp.setBless(bless);
			temp.checkOption();
		}

		writeC(getOptionSize(item, armor, durability, enlevel, setoption, bless, dynamic_mr, dynamic_stun_defence));
		writeC(19);
		writeC(item.getAc());
		writeC(item.getMaterial());
		if (Lineage.server_version > 300)
			writeC(-1); // Grade
		writeD(weight);

		// AC 0+enlevel
		if (enlevel != 0) {
			int ac = enlevel;

			// 목걸이1
			/*
			 * if ((item.getType2().equalsIgnoreCase("necklace") ||
			 * item.getType2().equalsIgnoreCase("earring")) && enlevel >= 5) {
			 * ac = enlevel - 4; if (ac > 6) ac = 6; }
			 * 
			 * if(item.getType2().equalsIgnoreCase("ring") ||
			 * item.getType2().equalsIgnoreCase("belt") ||
			 * ((item.getType2().equalsIgnoreCase("necklace") ||
			 * item.getType2().equalsIgnoreCase("earring")) && enlevel < 5)) ac
			 * = 0;
			 */

			writeC(0x02);
			writeC(ac);
		}

		int type = item.getRoyal() != 1 ? 0 : 1;
		type += item.getKnight() != 1 ? 0 : 2;
		type += item.getElf() != 1 ? 0 : 4;
		type += item.getWizard() != 1 ? 0 : 8;
		type += item.getDarkElf() != 1 ? 0 : 16;
		type += item.getDragonKnight() != 1 ? 0 : 32;
		type += item.getBlackWizard() != 1 ? 0 : 64;
		writeC(7);
		writeC(type);

		// 추가 데미지
		if (item.getAddDmg() != 0 || temp.getTollTipDmg() != 0) {
			int addDmg = item.getAddDmg() + temp.getTollTipDmg();
			if (addDmg > 0) {
				writeC(6);
				writeC(addDmg);
			}
		}

		// 공격 성공
		if (item.getAddHit() != 0 || temp.getTollTipHit() != 0) {
			int addHit = item.getAddHit() + temp.getTollTipHit();

			if (addHit > 0) {
				writeC(5);
				writeC(addHit);
			}
		}

		// STR
		if (item.getAddStr() != 0) {
			writeC(8);
			writeC(item.getAddStr());
		}

		// DEX
		if (item.getAddDex() != 0) {
			writeC(9);
			writeC(item.getAddDex());
		}

		// CON
		if (item.getAddCon() != 0) {
			writeC(10);
			writeC(item.getAddCon());
		}

		// INT
		if (item.getAddInt() != 0) {
			writeC(12);
			writeC(item.getAddInt());
		}

		// WIS
		if (item.getAddWis() != 0) {
			writeC(11);
			writeC(item.getAddWis());
		}

		// CHA
		if (item.getAddCha() != 0) {
			writeC(13);
			writeC(item.getAddCha());
		}

		// HP
		if (item.getAddHp() != 0 || temp.getTollTipHp() != 0) {
			int addHp = item.getAddHp() + temp.getTollTipHp();
			if (addHp > 0) {
				writeC(14);
				writeH(addHp);
			}
		}

		// 데미지 감소
		if (item.getAddReduction() != 0 || temp.getTollTipReduction() != 0) {
			int reduction = item.getAddReduction() + temp.getTollTipReduction();
			if (reduction > 0) {
				writeC(20);
				writeC(reduction);
			}
		}

		// 스턴 내성
		if (item.getStunDefense() != 0 || temp.getTollTipStunDefens() != 0) {
			int stunDefence = (int) ((item.getStunDefense() + dynamic_stun_defence) * 100) + temp.getTollTipStunDefens();
			writeC(28);
			writeC(stunDefence);
		}

		// SP
		if (item.getAddSp() != 0 || temp.getTollTipSp() != 0) {
			int addSp = item.getAddSp() + temp.getTollTipSp();
			if (addSp > 0) {
				writeC(17);
				writeC(addSp);
			}
		}

		// MR
		if (item.getAddMr() != 0 || dynamic_mr != 0 || temp.getTollTipMr() != 0) {
			writeC(15);
			writeH(item.getAddMr() + dynamic_mr + temp.getTollTipMr());
		}

		if (setoption != null && (setoption.isBrave() || setoption.isHaste()))
			writeC(18);

		// 최대 MP
		if (item.getAddMp() != 0 || temp.getTollTipMp() != 0) {
			writeC(24);
			writeC(item.getAddMp() + temp.getTollTipMp());
		}

		// 물약 회복량
		if (temp.getTollTipHealingPotion() != 0) {
			writeC(27);
			writeC(temp.getTollTipHealingPotion());
		}

		// PvP 데미지
		if (temp.getTollTipPvPDmg() != 0) {
			writeC(29);
			writeC(temp.getTollTipPvPDmg());
		}

		// PvP 데미지 감소
		if (temp.getTollTipPvPReduction() != 0) {
			writeC(30);
			writeC(temp.getTollTipPvPReduction());
		}

		if (setoption != null && (setoption.isBrave() || setoption.isHaste()))
			writeC(18);
		
		// 초기화
		if (temp != null)
			ItemDatabase.setPool(temp);

	}

	// 20 대미지 감소
	// 24 최대 MP
	// 27 물약 회복량(%)
	// 28 스턴 내성
	// 29 PvP 대미지
	// 30 PvP 대미지 감소

	protected void toWeapon(Item item, ItemInstance weapon, int durability, int enlevel, int weight, int bless) {
		ItemSetoption setoption = Lineage.server_version >= 200 ? ItemSetoptionDatabase.find(item.getSetId()) : null;

		ItemInstance temp = null;
		temp = ItemDatabase.newInstance(item);
		if (temp != null) {
			temp.setEnLevel(enlevel);
			temp.setBless(bless);
			temp.checkOption();
		}

		writeC(getOptionSize(item, weapon instanceof Arrow ? weapon : (ItemWeaponInstance) weapon, durability, enlevel, setoption, bless, 0, 0));
		writeC(0x01);
		writeC(item.getSmallDmg());
		writeC(item.getBigDmg());
		writeC(item.getMaterial());
		writeD(weight);
		if (enlevel != 0) {
			writeC(0x02);
			writeC(enlevel);
		}
		if (durability != 0) {
			writeC(3);
			writeC(durability);
		}

		if (item.isTohand())
			writeC(4);
		
		if (item.getAddHit() != 0 || temp.getTollTipHit() != 0) {
			writeC(5);
			writeC(item.getAddHit() + temp.getTollTipHit());
		}
		
		if (item.getAddDmg() != 0 || temp.getTollTipDmg() != 0) {
			;
			writeC(6);
			writeC(item.getAddDmg() + temp.getTollTipDmg());
		}

		
		int type = item.getRoyal() != 1 ? 0 : 1;
		type += item.getKnight() != 1 ? 0 : 2;
		type += item.getElf() != 1 ? 0 : 4;
		type += item.getWizard() != 1 ? 0 : 8;
		type += item.getDarkElf() != 1 ? 0 : 16;
		type += item.getDragonKnight() != 1 ? 0 : 32;
		type += item.getBlackWizard() != 1 ? 0 : 64;
		writeC(7);
		writeC(type);

		if (item.getAddStr() != 0) {
			writeC(8);
			writeC(item.getAddStr());
		}

		if (item.getAddDex() != 0) {
			writeC(9);
			writeC(item.getAddDex());
		}

		if (item.getAddCon() != 0) {
			writeC(10);
			writeC(item.getAddCon());
		}

		if (item.getAddWis() != 0) {
			writeC(11);
			writeC(item.getAddWis());
		}

		if (item.getAddInt() != 0) {
			writeC(12);
			writeC(item.getAddInt());
		}

		if (item.getAddCha() != 0) {
			writeC(13);
			writeC(item.getAddCha());
		}

		if (item.getAddHp() != 0 || temp.getTollTipHp() != 0) {
			writeC(14);
			writeH(item.getAddHp() + temp.getTollTipHp());
		}

		if (item.getAddMr() != 0 || temp.getTollTipMr() != 0) {
			writeC(15);
			writeH(item.getAddMr() + temp.getTollTipMr());
		}

		if (item.getStealMp() != 0) {
			writeC(16);
		}

		if (item.getAddSp() != 0 || temp.getTollTipSp() != 0) {
			writeC(17);
			writeC(item.getAddSp() + temp.getTollTipSp());
		}
		
		if (setoption != null && (setoption.isBrave() || setoption.isHaste()))
			writeC(18);

		if (item.getAddReduction() != 0 || temp.getTollTipReduction() != 0) {
			writeC(20);
			writeC(item.getAddReduction() + temp.getTollTipReduction());
		}
		
		if (item.getAddMp() != 0 || temp.getTollTipMp() != 0) {
			writeC(24);
			writeC(item.getAddMp() + temp.getTollTipMp());
		}


		if (temp != null)
			ItemDatabase.setPool(temp);

	}

	protected void toEtc(Item item, int weight) {
		writeC(0x06);
		writeC(0x17);
		writeC(item.getMaterial());
		writeD(weight);
	}

	protected String getName(ItemInstance item) {
		String name = CharacterMarbleDatabase.getItemName(item);
		if (name != null) {
			return name;
		}

		StringBuffer sb = new StringBuffer();
		if (item.getItem().getNameIdNumber() == 1075 && item.getItem().getInvGfx() != 464) {
			Letter letter = (Letter) item;
			sb.append(letter.getFrom());
			sb.append(" : ");
			sb.append(letter.getSubject());
		} else {
			// 봉인 표현
			if (item.isDefinite() && item.getBless() < 0) {
				sb.append("[봉인]");
				sb.append(" ");
			}

			if (item.isDefinite() && (item instanceof ItemWeaponInstance || item instanceof ItemArmorInstance) && !item.getItem().getType2().equalsIgnoreCase("fishing_rod")
					&& !item.getItem().getName().equalsIgnoreCase("절대 반지") && item.getItem().getNameIdNumber() != 431) {
				// 속성 인첸 표현.
				String element_name = null;
				Integer element_en = 0;

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
					sb.append(" ");
				}

				// 인첸 표현.
				if (item.getEnLevel() >= 0) {
					sb.append("+");
				}
				sb.append(item.getEnLevel());
				sb.append(" ");

			}
			sb.append(item.getName());
			// 파워볼 쿠폰: "000회차 홀 쿠폰" + "5,000,000" 형태로 표시.
			String n = item.getName();
			String tk = item.getItemTimek();
			if (tk != null && tk.contains(":") && n != null
					&& ("홀쿠폰".equals(n) || "짝쿠폰".equals(n) || "언더쿠폰".equals(n) || "오버쿠폰".equals(n))) {
				try {
					String[] sp = tk.split(":");
					if (sp.length >= 2) {
						int roundId = Integer.parseInt(sp[0]);
						int displayRound = PowerballController.getTodayRoundDisplay(roundId);
						long betAmount = Long.parseLong(sp[1]);
						String couponLabel = "홀 쿠폰";
						if ("짝쿠폰".equals(n))
							couponLabel = "짝 쿠폰";
						else if ("언더쿠폰".equals(n))
							couponLabel = "언더 쿠폰";
						else if ("오버쿠폰".equals(n))
							couponLabel = "오바 쿠폰";

						// 기존 이름 부분을 회차+쿠폰명으로 대체.
						sb.setLength(0);
						sb.append(String.format("%03d회차 %s", displayRound, couponLabel));
						sb.append(" ");
						sb.append(Util.changePrice(betAmount));
					}
				} catch (Exception ignore) {}
			}

			if (item.isDefinite()
					&& item.getQuantity() > 0/*
												 * (item instanceof MapleWand ||
												 * item instanceof PineWand ||
												 * item instanceof EbonyWand)
												 */) {
				sb.append(" (");
				sb.append(item.getQuantity());
				sb.append(")");
			}

			if (item.getCount() > 1) {
				sb.append(" (");
				sb.append(Util.changePrice(item.getCount()));
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

			if (item instanceof Turban) {
				sb.append(" [");
				sb.append(item.getNowTime());
				sb.append("]");
			}

			if (item.getInnRoomKey() > 0) {
				sb.append(" #");
				sb.append(item.getInnRoomKey());
			}
			long currentTimeSeconds = System.currentTimeMillis() / 1000;

			String itemTimek = item.getItemTimek();
			if (itemTimek != null && itemTimek.length() == 14) {
				try {
					LocalDateTime itemDateTime = LocalDateTime.parse(itemTimek, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
					ZonedDateTime itemZonedDateTime = ZonedDateTime.of(itemDateTime, ZoneId.of("Asia/Seoul"));

					ZonedDateTime currentZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(currentTimeSeconds), ZoneId.of("Asia/Seoul"));

					Duration duration = Duration.between(currentZonedDateTime, itemZonedDateTime);
					long remainingTimeInSeconds = duration.getSeconds();

					if (remainingTimeInSeconds > 0) {
						// 알림
						sb.append("[" + remainingTimeInSeconds + "]");

					}
				} catch (DateTimeParseException e) {
					// 날짜 문자열 파싱 오류 처리
					// 날짜 문자열이 유효하지 않을 때 이 부분이 실행됩니다.
					// 오류를 기록하거나 처리 방법을 결정합니다.
				}
			}
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

		}

		return sb.toString().trim();
	}

	protected int getOptionSize(ItemInstance item) {
		return getOptionSize(item.getItem(), item, item.getDurability(), item.getEnLevel(), null, item.getBless(), item.getDynamicMr(), item.getDynamicStunDefence());
	}

	protected int getOptionSize(Item item, ItemInstance ii, int durability, int enlevel, ItemSetoption setoption, int bless, int dynamic_mr, double dynamic_stun_defence) {
		int size = 0;

		// System.out.println(item.getName() + " (기존 방식) : " + size);
		size = checkSize(item, ii, enlevel, bless);
		// System.out.println(item.getName() + " (개선 방식) : " + size);
		return size;
	}

	private int checkSize(Item i, ItemInstance ii, int en, int bress) {
		int size = 0;

		if (ii == null) {
			Item item = ItemDatabase.find(i.getName());
			if (item != null) {
				ii = ItemDatabase.newInstance(item);
				if (ii != null) {
					ii.setEnLevel(en);
					ii.setBless(bress);
					ii.checkOption();
					size = ii.getStatusBytes().length;
					ItemDatabase.setPool(ii);
				}
			}
		} else {
			ii.checkOption();
			size = ii.getStatusBytes().length;
		}

		// System.out.println(i.getName() + " (개선 방식) : " + size);

		return size;
	}

	protected int getOptionSize(Item item, int durability, int enlevel, ItemSetoption setoption, int bless, int dynamic_mr, double dynamic_stun_defence, int dynamic_sp, int dynamic_reduction) {
		int size = 0;

		if (item.getType1().equalsIgnoreCase("armor")) {
			if (Lineage.server_version > 300)
				size += 10;
			else
				size += 9;

			if (enlevel != 0)
				size += 2;
			if (item.getAddStr() != 0)
				size += 2;
			if (item.getAddDex() != 0)
				size += 2;
			if (item.getAddCon() != 0)
				size += 2;
			if (item.getAddInt() != 0)
				size += 2;
			if (item.getAddCha() != 0)
				size += 2;
			if (item.getAddWis() != 0)
				size += 2;

			if (item.getName().equalsIgnoreCase("완력의 부츠") || item.getName().equalsIgnoreCase("민첩의 부츠") || item.getName().equalsIgnoreCase("지식의 부츠")) {
				if ((bless == 0 || bless == -128) || enlevel > 6)
					size += 3;
				if (enlevel == 9)
					size += 2;

				return size;
			}

			if (item.getType2().equalsIgnoreCase("necklace")) {
				if (item.getAddDmg() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHit() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHp() != 0 || enlevel > 0)
					size += 3;
				if (item.getAddMp() != 0)
					size += 2;
				if (item.getAddMr() != 0 || dynamic_mr != 0)
					size += 3;
				if (item.getAddSp() != 0 || dynamic_sp != 0)
					size += 2;
				if (item.getStunDefense() != 0 || dynamic_stun_defence != 0 || enlevel > 6)
					size += 2;
				if (item.getAddReduction() > 0 || dynamic_reduction != 0)
					size += 2;

				// 물약 회복량
				if (enlevel > 4)
					size += 2;

				return size;
			}

			if (item.getType2().equalsIgnoreCase("ring")) {
				if (item.getAddDmg() != 0 || (bless == 0 || bless == -128) || enlevel > 4)
					size += 2;
				if (item.getAddHit() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHp() != 0 || enlevel > 0)
					size += 3;
				if (item.getAddMp() != 0)
					size += 2;
				if ((item.getAddMr() != 0 || dynamic_mr != 0) || enlevel > 5)
					size += 3;
				if (item.getAddSp() != 0 || dynamic_sp != 0 || enlevel > 6)
					size += 2;
				if (item.getStunDefense() != 0 || dynamic_stun_defence != 0)
					size += 2;
				if (item.getAddReduction() > 0 || dynamic_reduction != 0)
					size += 2;

				// PvP 데미지
				if (enlevel > 6)
					size += 2;

				return size;
			}

			if (item.getType2().equalsIgnoreCase("belt")) {
				if (item.getAddDmg() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHit() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHp() != 0 || enlevel > 5)
					size += 3;
				if (item.getAddMp() != 0 || enlevel > 0)
					size += 2;
				if (item.getAddMr() != 0 || dynamic_mr != 0)
					size += 3;
				if (item.getAddSp() != 0 || dynamic_sp != 0)
					size += 2;
				if (item.getStunDefense() != 0 || dynamic_stun_defence != 0)
					size += 2;
				if (item.getAddReduction() > 0 || dynamic_reduction != 0 || enlevel > 4)
					size += 2;

				// PvP 리덕션
				if (enlevel > 6)
					size += 2;

				return size;
			}

			if (item.getType2().equalsIgnoreCase("earring")) {
				if (item.getAddDmg() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHit() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHp() != 0)
					size += 3;
				if (item.getAddMp() != 0)
					size += 2;
				if (item.getAddMr() != 0)
					size += 3;
				if (item.getAddSp() != 0)
					size += 2;
				if (item.getStunDefense() != 0)
					size += 2;
				if (item.getAddReduction() > 0)
					size += 2;

				return size;
			}
		} else if (item.getType1().equalsIgnoreCase("weapon")) {
			size += 10;
			if (enlevel != 0)
				size += 2;
			if (item.getAddStr() != 0)
				size += 2;
			if (item.getAddDex() != 0)
				size += 2;
			if (item.getAddCon() != 0)
				size += 2;
			if (item.getAddInt() != 0)
				size += 2;
			if (item.getAddCha() != 0)
				size += 2;
			if (item.getAddWis() != 0)
				size += 2;
			if (durability != 0)
				size += 2;
			if (item.isTohand())
				size += 1;
			if (item.getStealMp() != 0)
				size += 1;
		} else {
			return 0;
		}

		if (item.getAddReduction() != 0 || dynamic_reduction != 0)
			size += 2;
		if (item.getStunDefense() != 0 || dynamic_stun_defence != 0)
			size += 2;
		if (item.getAddMp() != 0)
			size += 2;
		if (item.getAddMr() != 0 || dynamic_mr != 0)
			size += 3;
		if (item.getAddSp() != 0 || ((bless == 0 || bless == -128) && item.getType2().equalsIgnoreCase("wand")) || dynamic_sp != 0)
			size += 2;
		if (item.getAddHp() != 0 || (bless == 0 || bless == -128) && item.getType1().equalsIgnoreCase("armor"))
			size += 3;
		if (item.getAddDmg() != 0 || ((bless == 0 || bless == -128) && item.getType1().equalsIgnoreCase("weapon") && !item.getType2().equalsIgnoreCase("wand")))
			size += 2;
		if (item.getAddHit() != 0 || ((item.getName().equalsIgnoreCase("수호성의 파워 글로브") || item.getName().equalsIgnoreCase("수호성의 활 골무")) && enlevel > 4))
			size += 2;
		if (setoption != null && (setoption.isBrave() || setoption.isHaste()))
			size += 1;

		return size;
	}
}