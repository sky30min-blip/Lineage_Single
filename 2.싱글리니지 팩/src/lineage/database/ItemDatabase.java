package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import kuberaitem.Autosellitem;
import kuberaitem.BlessRemoves;
import kuberaitem.DeadRecovery;
import kuberaitem.ItemArmorBreak;
import kuberaitem.ItemChange;
import kuberaitem.ItemFinal;
import kuberaitem.RandomDollOption;
import kuberaitem.ScrollOfChangeBlessdoll;
import kuberaitem.ScrollOfEnchantElementalWeaponRe;
import kuberaitem.SelfSpell;
import kuberaitem.autohuntreset;
import kuberaitem.autohuntreset2;
import kuberaitem.darkelf_potion;
import kuberaitem.monsterClean;
import kuberaitem.petitem;
import kuberaitem.기억제거구슬;
import kuberaitem.희미한기억의구슬;
import kuberaitem.결혼반지;
import kuberaitem.기마투구;
import lineage.bean.database.Item;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemBookInstance;
import lineage.world.object.instance.ItemCrystalInstance;
import lineage.world.object.instance.ItemDarkSpiritCrystalInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.item.Aden;
import lineage.world.object.item.Berserker;
import lineage.world.object.item.BlessEva;
import lineage.world.object.item.Bundle;
import lineage.world.object.item.Candle;
import lineage.world.object.item.Card;
import lineage.world.object.item.ChanceBundle;
import lineage.world.object.item.CookBook;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.ElementalStone;
import lineage.world.object.item.ElvenWafer;
import lineage.world.object.item.Firework;
import lineage.world.object.item.InnRoomKey;
import lineage.world.object.item.Lamp;
import lineage.world.object.item.Lantern;
import lineage.world.object.item.LanternOil;
import lineage.world.object.item.Letter;
import lineage.world.object.item.MagicDoll;
import lineage.world.object.item.MagicFirewood;
import lineage.world.object.item.MagicFlute;
import lineage.world.object.item.Meat;
import lineage.world.object.item.MiniMap;
import lineage.world.object.item.MonsterEyeMeat;
import lineage.world.object.item.PetWhistle;
import lineage.world.object.item.PledgeLetter;
import lineage.world.object.item.RaceTicket;
import lineage.world.object.item.RedSock;
import lineage.world.object.item.Solvent;
import lineage.world.object.item.ThrowingKnife;
import lineage.world.object.item.Whetstone;
import lineage.world.object.item.all_night.Exp_marble;
import lineage.world.object.item.all_night.ScrollOfChangeBless;
import lineage.world.object.item.all_night.ScrollOfGiranDungeon;
import lineage.world.object.item.all_night.ScrollOfHpMpReset;
import lineage.world.object.item.all_night.ScrollOfMetis;
import lineage.world.object.item.all_night.ScrollOfNewClanJoin;
import lineage.world.object.item.all_night.ScrollOfOrimArmor;
import lineage.world.object.item.all_night.ScrollOfOrimWeapon;
import lineage.world.object.item.all_night.ScrollOfRankPoly;
import lineage.world.object.item.all_night.ScrollOfRankPoly2;
import lineage.world.object.item.all_night.ScrollOfWeapon;
import lineage.world.object.item.all_night.ScrollOfmythRankPoly;
import lineage.world.object.item.all_night.ScrollTOITeleport;
import lineage.world.object.item.all_night.ScrollOfAccessory;
import lineage.world.object.item.all_night.ScrollOfAccessory2;
import lineage.world.object.item.all_night.StatClear;
import lineage.world.object.item.all_night.Sword_lack;
import lineage.world.object.item.all_night.cpaty;
import lineage.world.object.item.all_night.huntgo;
import lineage.world.object.item.all_night.notice;
import lineage.world.object.item.all_night.monstersoul;
import lineage.world.object.item.all_night.ExpMarble;
import lineage.world.object.item.all_night.ExpSaveMarble;
import lineage.world.object.item.all_night.MonsterDropCheckWand;
import lineage.world.object.item.all_night.LifeLost;
import lineage.world.object.item.all_night.SelfMagic;
import lineage.world.object.item.all_night.ItemDropCheckWand;
import lineage.world.object.item.all_night.SelfImmuneToHarm;
import lineage.world.object.item.all_night.InventoryCheck;
import lineage.world.object.item.all_night.EnchantRecovery;
import lineage.world.object.item.all_night.EnchantRemove;
import lineage.world.object.item.all_night.DollAwaken;
import lineage.world.object.item.all_night.DollEvolutionOrderForm;
import lineage.world.object.item.all_night.AutoAttackItem;
import lineage.world.object.item.all_night.AutoHuntItem;
import lineage.world.object.item.all_night.ItemCheckWand;
import lineage.world.object.item.all_night.MaxHPIncreasePotion;
import lineage.world.object.item.all_night.MaxMPIncreasePotion;
import lineage.world.object.item.all_night.CharacterSaveMarble;
import lineage.world.object.item.all_night.ClassChangeTicket;
import lineage.world.object.item.all_night.ClanBuffPotion;
import lineage.world.object.item.all_night.AutoPotion;
import lineage.world.object.item.all_night.BuffMaan;
import lineage.world.object.item.all_night.Buff_potion;
import lineage.world.object.item.all_night.Caotic_potion;
import lineage.world.object.item.all_night.ChangeSexPotion;
import lineage.world.object.item.all_night.Exp_drop_potion;
import lineage.world.object.item.all_night.Exp_potion;
import lineage.world.object.item.all_night.Exp_support;
import lineage.world.object.item.all_night.FightPotion;
import lineage.world.object.item.all_night.Fishing_rice;
import lineage.world.object.item.all_night.GoldBar;
import lineage.world.object.item.all_night.ItemSwap;
import lineage.world.object.item.all_night.Item_Remove_Wand;
import lineage.world.object.item.all_night.Lawful_potion;
import lineage.world.object.item.all_night.LevelDownScroll;
import lineage.world.object.item.all_night.LevelUpScroll;
import lineage.world.object.item.all_night.PvP_clean;
import lineage.world.object.item.armor.ArmorOfIllusion;
import lineage.world.object.item.armor.Turban;
import lineage.world.object.item.bow.BowOfIllusion;
import lineage.world.object.item.bundle.Astrologist;
import lineage.world.object.item.cloak.CloakInvisibility;
import lineage.world.object.item.cloak.ElvenCloak;
import lineage.world.object.item.etc.EvolutionFruit;
import lineage.world.object.item.helm.HelmInfravision;
import lineage.world.object.item.helm.HelmMagicHealing;
import lineage.world.object.item.helm.HelmMagicPower;
import lineage.world.object.item.helm.HelmMagicSpeed;
import lineage.world.object.item.potion.BlindingPotion;
import lineage.world.object.item.potion.BluePotion;
import lineage.world.object.item.potion.BraveryPotion;
import lineage.world.object.item.potion.BraveryPotion2;
import lineage.world.object.item.potion.CurePoisonPotion;
import lineage.world.object.item.potion.ElixirPotion;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.item.potion.ManaPotion;
import lineage.world.object.item.potion.PetPotion;
import lineage.world.object.item.potion.WisdomPotion;
import lineage.world.object.item.quest.AriaReward;
import lineage.world.object.item.quest.BlackKey;
import lineage.world.object.item.quest.ElvenTreasure;
import lineage.world.object.item.quest.RedKey;
import lineage.world.object.item.quest.SecretRoomKey;
import lineage.world.object.item.ring.RingSummonControl;
import lineage.world.object.item.ring.RingTeleportControl;
import lineage.world.object.item.scroll.BlankScroll;
import lineage.world.object.item.scroll.ScrollChangeName;
import lineage.world.object.item.scroll.ScrollLabeledDaneFools;
import lineage.world.object.item.scroll.ScrollLabeledKernodwel;
import lineage.world.object.item.scroll.ScrollLabeledPratyavayah;
import lineage.world.object.item.scroll.ScrollLabeledVenzarBorgavve;
import lineage.world.object.item.scroll.ScrollLabeledVerrYedHorae;
import lineage.world.object.item.scroll.ScrollLabeledVerrYedHoraePledgeHouse;
import lineage.world.object.item.scroll.ScrollLabeledZelgoMer;
import lineage.world.object.item.scroll.ScrollOfEnchantArmorIllusion;
import lineage.world.object.item.scroll.ScrollOfEnchantElementalWeapon;
import lineage.world.object.item.scroll.ScrollPolymorph;
import lineage.world.object.item.scroll.ScrollResurrection;
import lineage.world.object.item.scroll.ScrollReturnAdenCity;
import lineage.world.object.item.scroll.ScrollReturnElvenForest;
import lineage.world.object.item.scroll.ScrollReturnGiranCity;
import lineage.world.object.item.scroll.ScrollReturnGludinTown;
import lineage.world.object.item.scroll.ScrollReturnHeineCity;
import lineage.world.object.item.scroll.ScrollReturnHiddenValley;
import lineage.world.object.item.scroll.ScrollReturnIvoryTowerTown;
import lineage.world.object.item.scroll.ScrollReturnKentVillage;
import lineage.world.object.item.scroll.ScrollReturnOrctown;
import lineage.world.object.item.scroll.ScrollReturnSilentCavern;
import lineage.world.object.item.scroll.ScrollReturnSilverKnightTown;
import lineage.world.object.item.scroll.ScrollReturnSingingIsland;
import lineage.world.object.item.scroll.ScrollReturnTalkingIslandVillage;
import lineage.world.object.item.scroll.ScrollReturnWerldernTown;
import lineage.world.object.item.scroll.ScrollReturnWoodbecVillage;
import lineage.world.object.item.scroll.ScrollTeleport;
import lineage.world.object.item.scroll.ScrollofEnchantWeaponIllusion;
import lineage.world.object.item.scroll.SealedCancelScroll;
import lineage.world.object.item.scroll.SealedScroll;
import lineage.world.object.item.scroll.SealedTOITeleportCharm;
import lineage.world.object.item.scroll.SpellScrollAbsoluteBarrier;
import lineage.world.object.item.scroll.SpellScrollAdvanceSpirit;
import lineage.world.object.item.scroll.SpellScrollBlessWeapon;
import lineage.world.object.item.scroll.SpellScrollBlessedArmor;
import lineage.world.object.item.scroll.SpellScrollCallLightning;
import lineage.world.object.item.scroll.SpellScrollChillTouch;
import lineage.world.object.item.scroll.SpellScrollConeOfCold;
import lineage.world.object.item.scroll.SpellScrollCounterMagic;
import lineage.world.object.item.scroll.SpellScrollCurePoison;
import lineage.world.object.item.scroll.SpellScrollCurseBlind;
import lineage.world.object.item.scroll.SpellScrollCurseParalyze;
import lineage.world.object.item.scroll.SpellScrollCursePoison;
import lineage.world.object.item.scroll.SpellScrollDarkness;
import lineage.world.object.item.scroll.SpellScrollDecreaseWeight;
import lineage.world.object.item.scroll.SpellScrollDestroy;
import lineage.world.object.item.scroll.SpellScrollDetection;
import lineage.world.object.item.scroll.SpellScrollEarthJail;
import lineage.world.object.item.scroll.SpellScrollEnchantWeapon;
import lineage.world.object.item.scroll.SpellScrollEnergyBolt;
import lineage.world.object.item.scroll.SpellScrollFireArrow;
import lineage.world.object.item.scroll.SpellScrollFireball;
import lineage.world.object.item.scroll.SpellScrollFrozenCloud;
import lineage.world.object.item.scroll.SpellScrollGreaterHeal;
import lineage.world.object.item.scroll.SpellScrollHeal;
import lineage.world.object.item.scroll.SpellScrollHolyWeapon;
import lineage.world.object.item.scroll.SpellScrollIceDagger;
import lineage.world.object.item.scroll.SpellScrollImmunetoHarm;
import lineage.world.object.item.scroll.SpellScrollLesserHeal;
import lineage.world.object.item.scroll.SpellScrollLight;
import lineage.world.object.item.scroll.SpellScrollLightning;
import lineage.world.object.item.scroll.SpellScrollManaDrain;
import lineage.world.object.item.scroll.SpellScrollMeditation;
import lineage.world.object.item.scroll.SpellScrollPhysicalEnchantDex;
import lineage.world.object.item.scroll.SpellScrollPhysicalEnchantStr;
import lineage.world.object.item.scroll.SpellScrollRemoveCurse;
import lineage.world.object.item.scroll.SpellScrollShield;
import lineage.world.object.item.scroll.SpellScrollSlow;
import lineage.world.object.item.scroll.SpellScrollStalac;
import lineage.world.object.item.scroll.SpellScrollTameMonster;
import lineage.world.object.item.scroll.SpellScrollTeleport;
import lineage.world.object.item.scroll.SpellScrollTurnUndead;
import lineage.world.object.item.scroll.SpellScrollVampiricTouch;
import lineage.world.object.item.scroll.SpellScrollWeaponBreak;
import lineage.world.object.item.scroll.SpellScrollWindShuriken;
import lineage.world.object.item.scroll.TOITeleportCharm;
import lineage.world.object.item.scroll.TOITeleportScroll;
import lineage.world.object.item.scroll.TalkingScroll;
import lineage.world.object.item.shield.ElvenShield;
import lineage.world.object.item.wand.EbonyWand;
import lineage.world.object.item.wand.ExpulsionWand;
import lineage.world.object.item.wand.MapleWand;
import lineage.world.object.item.wand.PineWand;
import lineage.world.object.item.wand.TeleportWand;
import lineage.world.object.item.weapon.Arrow;
import lineage.world.object.item.weapon.Claw;
import lineage.world.object.item.weapon.DiceDagger;
import lineage.world.object.item.weapon.Edoryu;
import lineage.world.object.item.weapon.SwordOfIllusion;
import lineage.world.object.item.yadolan.Seal_enchant;
import lineage.world.object.item.yadolan.aManaPotion;
import lineage.world.object.item.yadolan.at;
import lineage.world.object.item.yadolan.autohunttime1;
import lineage.world.object.item.yadolan.autohunttime2;
import lineage.world.object.item.yadolan.expRecovery;
import lineage.world.object.item.yadolan.guide;
import lineage.world.object.item.yadolan.update;
import lineage.world.object.item.yadolan.ShopControllerItem;
import lineage.world.object.item.yadolan.HuntingZoneTeleportationBook;
import lineage.world.object.item.yadolan.PenguinHuntingStick;
import lineage.world.object.item.yadolan.LordBuff;

public final class ItemDatabase {

	static private List<Item> list;
	static private List<ItemInstance> pool;

	/**
	 * 초기화 함수
	 * 
	 * @param con
	 */
	static public void init(Connection con) {
		TimeLine.start("ItemDatabase..");

		pool = new ArrayList<ItemInstance>();
		list = new ArrayList<Item>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item");
			rs = st.executeQuery();
			while (rs.next()) {
				Item i = new Item();
				i.setName(rs.getString("아이템이름"));
				i.setType1(rs.getString("구분1"));
				i.setType2(rs.getString("구분2"));
				i.setNameId(rs.getString("NAMEID")); 
				i.setMaterial(getMaterial(rs.getString("재질")));
				i.setMaterialName(rs.getString("재질"));
				i.setPcTrade(rs.getString("현금거래").equalsIgnoreCase("true"));
				i.setSmallDmg(rs.getInt("작은 몬스터"));
				i.setBigDmg(rs.getInt("큰 몬스터"));
				i.setWeight(Double.valueOf(rs.getString("무게")));
				i.setInvGfx(rs.getInt("인벤ID"));
				i.setGroundGfx(rs.getInt("GFXID"));
				i.setAction1(rs.getInt("ACTION1"));
				i.setAction2(rs.getInt("ACTION2"));
				i.setSell(rs.getString("판매").equalsIgnoreCase("true"));
				i.setPiles(rs.getString("겹침").equalsIgnoreCase("true"));
				i.setTrade(rs.getString("거래").equalsIgnoreCase("true"));
				i.setDrop(rs.getString("드랍").equalsIgnoreCase("true"));
				i.setWarehouse(rs.getString("창고").equalsIgnoreCase("true"));
				i.setClanWarehouse(rs.getString("창고_혈맹").equalsIgnoreCase("true"));
				i.setElfWarehouse(rs.getString("창고_요숲").equalsIgnoreCase("true"));
				i.setEnchant(rs.getString("인첸트").equalsIgnoreCase("true"));
				i.setSafeEnchant(rs.getInt("안전인첸트"));
				i.setMaxEnchant(rs.getInt("최고인챈"));
				i.setRoyal(rs.getInt("군주"));
				i.setKnight(rs.getInt("기사"));
				i.setElf(rs.getInt("요정"));
				i.setWizard(rs.getInt("마법사"));
				i.setDarkElf(rs.getInt("다크엘프"));
				i.setDragonKnight(rs.getInt("용기사"));
				i.setBlackWizard(rs.getInt("환술사"));
				i.setAddHit(rs.getInt("공격성공율"));
				i.setAddDmg(rs.getInt("추가타격치"));
				i.setAddMagicHit(rs.getInt("마법 명중"));
				i.setAc(rs.getInt("ac"));
				i.setAddStr(rs.getInt("add_str"));
				i.setAddDex(rs.getInt("add_dex"));
				i.setAddCon(rs.getInt("add_con"));
				i.setAddInt(rs.getInt("add_int"));
				i.setAddWis(rs.getInt("add_wis"));
				i.setAddCha(rs.getInt("add_cha"));
				i.setAddHp(rs.getInt("HP증가"));
				i.setAddMp(rs.getInt("MP증가"));
				i.setAddSp(rs.getInt("SP증가"));
				i.setAddMr(rs.getInt("MR증가"));
				i.setCanbedmg(rs.getString("손상").equalsIgnoreCase("true"));
				i.setLevelMin(rs.getInt("level_min"));
				i.setLevelMax(rs.getInt("level_max"));
				i.setEffect(rs.getInt("이펙트ID"));
				i.setSetId(rs.getInt("셋트아이템ID"));
				i.setDelay(rs.getInt("delay"));
				i.setWaterress(rs.getInt("waterress"));
				i.setWindress(rs.getInt("windress"));
				i.setEarthress(rs.getInt("earthress"));
				i.setFireress(rs.getInt("fireress"));
				i.setStunDefense(rs.getInt("stun_defense") * 0.01);
				i.setEnchantStunDefense(rs.getInt("enchant_stun_defense") * 0.01);
				i.setAddWeight(rs.getDouble("add_weight"));
				i.setTicHp(rs.getInt("tic_hp"));
				i.setTicMp(rs.getInt("tic_mp"));
				i.setShopPrice(rs.getInt("shop_price"));
				i.setDropChance(rs.getInt("drop_chance") * 0.01);
				i.setGfxMode(getWeaponGfx(i.getType2()));
				i.setSlot(getSlot(i.getType2()));
				i.setEquippedSlot(getEquippedSlot(i.getType1(), i.getType2()));
				i.setSolvent(rs.getInt("solvent"));
				i.setBookChaoticZone(rs.getString("book_chaotic_zone").equalsIgnoreCase("true"));
				i.setBookLawfulZone(rs.getString("book_lawful_zone").equalsIgnoreCase("true"));
				i.setBookMomtreeZone(rs.getString("book_momtree_zone").equalsIgnoreCase("true"));
				i.setBookNeutralZone(rs.getString("book_neutral_zone").equalsIgnoreCase("true"));
				i.setBookTowerZone(rs.getString("book_tower_zone").equalsIgnoreCase("true"));
				if (rs.getString("attribute_crystal").equalsIgnoreCase("earth"))
					i.setAttributeCrystal(Lineage.ELEMENT_EARTH);
				else if (rs.getString("attribute_crystal").equalsIgnoreCase("fire"))
					i.setAttributeCrystal(Lineage.ELEMENT_FIRE);
				else if (rs.getString("attribute_crystal").equalsIgnoreCase("wind"))
					i.setAttributeCrystal(Lineage.ELEMENT_WIND);
				else if (rs.getString("attribute_crystal").equalsIgnoreCase("water"))
					i.setAttributeCrystal(Lineage.ELEMENT_WATER);
				i.setPolyName(rs.getString("poly_name"));
				i.setInventorySave(rs.getString("is_inventory_save").equalsIgnoreCase("true"));
				i.setAqua(rs.getString("is_aqua").equalsIgnoreCase("true"));
				i.setStealHp(rs.getInt("steal_hp"));
				i.setStealMp(rs.getInt("steal_mp"));
				i.setTohand(rs.getString("is_tohand").equalsIgnoreCase("true"));
				i.setAddReduction(rs.getInt("reduction"));
				i.setIgnoreReduction(rs.getInt("ignore_reduction"));
				i.setEnchantMr(rs.getInt("enchant_mr"));
				i.setCriticalEffect(rs.getInt("critical_effect"));
				i.setAddCriticalSword(rs.getInt("sword_add_critical"));
				i.setAddCriticalBow(rs.getInt("bow_add_critical"));
				i.setAddCriticalMagic(rs.getInt("magic_add_critical"));
				i.setAddCaoticDamage(rs.getString("caotic_add_damage").equalsIgnoreCase("true"));
				i.setDuration(rs.getInt("duration"));
				i.setStunHit(Double.valueOf(rs.getString("스턴 적중").trim()) * 0.01);
				i.setEnchantStunHit(Double.valueOf(rs.getString("인챈당 스턴 적중").trim()) * 0.01);
				i.setEnchantSp(rs.getInt("인챈당 SP"));
				i.setEnchantReduction(rs.getInt("인챈당 리덕션"));
				i.setEnchantIgnoreReduction(rs.getInt("인챈당 리덕션 무시"));
				i.setEnchantSwordCritical(rs.getInt("인챈당 근거리 치명타"));
				i.setEnchantBowCritical(rs.getInt("인챈당 원거리 치명타"));
				i.setEnchantMagicCritical(rs.getInt("인챈당 마법 치명타"));
				i.setPvpDamage(rs.getInt("PvP 데미지"));
				i.setEnchantPvpDamage(rs.getInt("인챈당 PvP 데미지"));
				i.setPvpReduction(rs.getInt("PvP 리덕션"));
				i.setEnchantPvpReduction(rs.getInt("인챈당 PvP 리덕션"));

				try {
					StringBuffer sb = new StringBuffer();
					StringTokenizer stt = new StringTokenizer(i.getNameId(), " $ ");
					while (stt.hasMoreTokens())
						sb.append(stt.nextToken());
					i.setNameIdNumber(Integer.valueOf(sb.toString().trim()));
				} catch (Exception e) {
				}

				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	static public void reload() {
		TimeLine.start("item 테이블 리로드 완료 - ");
		
		synchronized (list) {
			list.clear();
			
			PreparedStatement st = null;
			ResultSet rs = null;
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("SELECT * FROM item");
				rs = st.executeQuery();
				while (rs.next()) {
					Item i = new Item();
					i.setName(rs.getString("아이템이름"));
					i.setType1(rs.getString("구분1"));
					i.setType2(rs.getString("구분2"));
					i.setNameId(rs.getString("NAMEID"));
					i.setMaterial(getMaterial(rs.getString("재질")));
					i.setMaterialName(rs.getString("재질"));
					i.setPcTrade(rs.getString("현금거래").equalsIgnoreCase("true"));
					i.setSmallDmg(rs.getInt("작은 몬스터"));
					i.setBigDmg(rs.getInt("큰 몬스터"));
					i.setWeight(Double.valueOf(rs.getString("무게")));
					i.setInvGfx(rs.getInt("인벤ID"));
					i.setGroundGfx(rs.getInt("GFXID"));
					i.setAction1(rs.getInt("ACTION1"));
					i.setAction2(rs.getInt("ACTION2"));
					i.setSell(rs.getString("판매").equalsIgnoreCase("true"));
					i.setPiles(rs.getString("겹침").equalsIgnoreCase("true"));
					i.setTrade(rs.getString("거래").equalsIgnoreCase("true"));
					i.setDrop(rs.getString("드랍").equalsIgnoreCase("true"));
					i.setWarehouse(rs.getString("창고").equalsIgnoreCase("true"));
					i.setClanWarehouse(rs.getString("창고_혈맹").equalsIgnoreCase("true"));
					i.setElfWarehouse(rs.getString("창고_요숲").equalsIgnoreCase("true"));
					i.setEnchant(rs.getString("인첸트").equalsIgnoreCase("true"));
					i.setSafeEnchant(rs.getInt("안전인첸트"));
					i.setMaxEnchant(rs.getInt("최고인챈"));
					i.setRoyal(rs.getInt("군주"));
					i.setKnight(rs.getInt("기사"));
					i.setElf(rs.getInt("요정"));
					i.setWizard(rs.getInt("마법사"));
					i.setDarkElf(rs.getInt("다크엘프"));
					i.setDragonKnight(rs.getInt("용기사"));
					i.setBlackWizard(rs.getInt("환술사"));
					i.setAddHit(rs.getInt("공격성공율"));
					i.setAddDmg(rs.getInt("추가타격치"));
					i.setAddMagicHit(rs.getInt("마법 명중"));
					i.setAc(rs.getInt("ac"));
					i.setAddStr(rs.getInt("add_str"));
					i.setAddDex(rs.getInt("add_dex"));
					i.setAddCon(rs.getInt("add_con"));
					i.setAddInt(rs.getInt("add_int"));
					i.setAddWis(rs.getInt("add_wis"));
					i.setAddCha(rs.getInt("add_cha"));
					i.setAddHp(rs.getInt("HP증가"));
					i.setAddMp(rs.getInt("MP증가"));
					i.setAddSp(rs.getInt("SP증가"));
					i.setAddMr(rs.getInt("MR증가"));
					i.setCanbedmg(rs.getString("손상").equalsIgnoreCase("true"));
					i.setLevelMin(rs.getInt("level_min"));
					i.setLevelMax(rs.getInt("level_max"));
					i.setEffect(rs.getInt("이펙트ID"));
					i.setSetId(rs.getInt("셋트아이템ID"));
					i.setDelay(rs.getInt("delay"));
					i.setWaterress(rs.getInt("waterress"));
					i.setWindress(rs.getInt("windress"));
					i.setEarthress(rs.getInt("earthress"));
					i.setFireress(rs.getInt("fireress"));
					i.setStunDefense(rs.getInt("stun_defense") * 0.01);
					i.setEnchantStunDefense(rs.getInt("enchant_stun_defense") * 0.01);
					i.setAddWeight(rs.getDouble("add_weight"));
					i.setTicHp(rs.getInt("tic_hp"));
					i.setTicMp(rs.getInt("tic_mp"));
					i.setShopPrice(rs.getInt("shop_price"));
					i.setDropChance(rs.getInt("drop_chance") * 0.01);
					i.setGfxMode(getWeaponGfx(i.getType2()));
					i.setSlot(getSlot(i.getType2()));
					i.setEquippedSlot(getEquippedSlot(i.getType1(), i.getType2()));
					i.setSolvent(rs.getInt("solvent"));
					i.setBookChaoticZone(rs.getString("book_chaotic_zone").equalsIgnoreCase("true"));
					i.setBookLawfulZone(rs.getString("book_lawful_zone").equalsIgnoreCase("true"));
					i.setBookMomtreeZone(rs.getString("book_momtree_zone").equalsIgnoreCase("true"));
					i.setBookNeutralZone(rs.getString("book_neutral_zone").equalsIgnoreCase("true"));
					i.setBookTowerZone(rs.getString("book_tower_zone").equalsIgnoreCase("true"));
					if (rs.getString("attribute_crystal").equalsIgnoreCase("earth"))
						i.setAttributeCrystal(Lineage.ELEMENT_EARTH);
					else if (rs.getString("attribute_crystal").equalsIgnoreCase("fire"))
						i.setAttributeCrystal(Lineage.ELEMENT_FIRE);
					else if (rs.getString("attribute_crystal").equalsIgnoreCase("wind"))
						i.setAttributeCrystal(Lineage.ELEMENT_WIND);
					else if (rs.getString("attribute_crystal").equalsIgnoreCase("water"))
						i.setAttributeCrystal(Lineage.ELEMENT_WATER);
					i.setPolyName(rs.getString("poly_name"));
					i.setInventorySave(rs.getString("is_inventory_save").equalsIgnoreCase("true"));
					i.setAqua(rs.getString("is_aqua").equalsIgnoreCase("true"));
					i.setStealHp(rs.getInt("steal_hp"));
					i.setStealMp(rs.getInt("steal_mp"));
					i.setTohand(rs.getString("is_tohand").equalsIgnoreCase("true"));
					i.setAddReduction(rs.getInt("reduction"));
					i.setIgnoreReduction(rs.getInt("ignore_reduction"));
					i.setEnchantMr(rs.getInt("enchant_mr"));
					i.setCriticalEffect(rs.getInt("critical_effect"));
					i.setAddCriticalSword(rs.getInt("sword_add_critical"));
					i.setAddCriticalBow(rs.getInt("bow_add_critical"));
					i.setAddCriticalMagic(rs.getInt("magic_add_critical"));
					i.setAddCaoticDamage(rs.getString("caotic_add_damage").equalsIgnoreCase("true"));
					i.setDuration(rs.getInt("duration"));
					i.setStunHit(Double.valueOf(rs.getString("스턴 적중").trim()) * 0.01);
					i.setEnchantStunHit(Double.valueOf(rs.getString("인챈당 스턴 적중").trim()) * 0.01);
					i.setEnchantSp(rs.getInt("인챈당 SP"));
					i.setEnchantReduction(rs.getInt("인챈당 리덕션"));
					i.setEnchantIgnoreReduction(rs.getInt("인챈당 리덕션 무시"));
					i.setEnchantSwordCritical(rs.getInt("인챈당 근거리 치명타"));
					i.setEnchantBowCritical(rs.getInt("인챈당 원거리 치명타"));
					i.setEnchantMagicCritical(rs.getInt("인챈당 마법 치명타"));
					i.setPvpDamage(rs.getInt("PvP 데미지"));
					i.setEnchantPvpDamage(rs.getInt("인챈당 PvP 데미지"));
					i.setPvpReduction(rs.getInt("PvP 리덕션"));
					i.setEnchantPvpReduction(rs.getInt("인챈당 PvP 리덕션"));
					
					try {
						StringBuffer sb = new StringBuffer();
						StringTokenizer stt = new StringTokenizer(i.getNameId(), " $ ");
						while (stt.hasMoreTokens())
							sb.append(stt.nextToken());
						i.setNameIdNumber(Integer.valueOf(sb.toString().trim()));
					} catch (Exception e) {
					}

					list.add(i);
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : reload()\r\n", ItemDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}
		
		TimeLine.end();
	}

	static public Item find(String type1, String type2) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getType1().equalsIgnoreCase(type1) && i.getType2().equalsIgnoreCase(type2))
					return i;
			}
			return null;
		}
	}
	
	public static Item findItemIdByNameWithoutSpace(String name) {
		synchronized (list) {
			for (Item item : list) {
				if (item != null && item.getName().replace(" ", "").equals(name)) {
					return item;
				}
			}
			return null;
		}
	}

	/**
	 * 이름으로 해당 객체 찾기 함수.
	 * 
	 * @param name
	 * @return
	 */
	static public Item find(String name) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getName().equalsIgnoreCase(name))
					return i;
			}
			return null;
		}
	}

	static public Item find2(String name) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getName().replace(" ", "").equalsIgnoreCase(name))
					return i;
			}
			return null;
		}
	}
	static public Item find_ItemId(int nameid){
		synchronized (list) {
			for(Item i : list){
				if(i.getNameIdNumber() == nameid)
					return i;
			}
			return null;
		}
	}
	static public Item find(int name_id) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getNameIdNumber() == name_id)
					return i;
			}
			return null;
		}
	}

	/**
	 * 아이템 객체 생성해주는 함수 : 아이템 고유 이름번호를 이용해 구분해서 클레스 생성 풀에 등록되어있는 객체 가져와서 재사용.
	 * 
	 * @param item
	 * @return
	 */
	static public ItemInstance newInstance(Item item) {
		// 버그 방지.
		if (item == null)
			return null;

		Object o = PluginController.init(ItemDatabase.class, "newInstance", item);
		if (o instanceof ItemInstance)
			return (ItemInstance) o;

		// 생성 처리.
		switch (item.getNameIdNumber()) {
		case 2: // 등잔
			return Lamp.clone(getPool(Lamp.class)).clone(item);
		case 4: // 아데나
			return Aden.clone(getPool(Aden.class)).clone(item);
		case 23: // 고기
		case 68: // 당근
		case 72: // 달걀
		case 82: // 레몬
		case 85: // 오렌지
		case 106: // 사과
		case 107: // 바나나
		case 1179: // 사탕
			return Meat.clone(getPool(Meat.class)).clone(item);
		case 27: // 단풍나무 막대
		case 260: // 변신 막대
			return MapleWand.clone(getPool(MapleWand.class)).clone(item);
		case 28: // 소나무 막대
		case 258: // 괴물 생성 막대
			return PineWand.clone(getPool(PineWand.class)).clone(item);
		case 30: // 갑옷 마법 주문서 `젤고 머'
		case 249:
			return ScrollLabeledZelgoMer.clone(getPool(ScrollLabeledZelgoMer.class)).clone(item);
		case 34: // 저주 풀기 주문서 `프라탸바야'
		case 243:
		case 3299: // 상아탑의 저주풀기 주문서
			return ScrollLabeledPratyavayah.clone(getPool(ScrollLabeledPratyavayah.class)).clone(item);
		case 35: // 무기 마법 주문서 `데이엔 푸엘스'
		case 244:
			return ScrollLabeledDaneFools.clone(getPool(ScrollLabeledDaneFools.class)).clone(item);
		case 39: // 귀환 주문서 `베르 예드 호레'
		case 505:
		case 3297: // 상아탑의 귀환 주문서
			return ScrollLabeledVerrYedHorae.clone(getPool(ScrollLabeledVerrYedHorae.class)).clone(item);
		case 40: // 순간이동 주문서 `벤자르 보르가브'
		case 230:
		case 3296: // 상아탑의 순간이동 주문서
			return ScrollLabeledVenzarBorgavve.clone(getPool(ScrollLabeledVenzarBorgavve.class)).clone(item);
		case 43: // 확인 주문서 `케르노드 웰'
		case 55:
		case 3298: // 상아탑의 확인 주문서
			return ScrollLabeledKernodwel.clone(getPool(ScrollLabeledKernodwel.class)).clone(item);
		case 2463: // 스팅
		case 2516: // 실버 스팅
		case 2517: // 헤비 스팅
			return ThrowingKnife.clone(getPool(ThrowingKnife.class)).clone(item);
		case 67: // 양초
			return Candle.clone(getPool(Candle.class)).clone(item);
		case 180: // 투명망토
			return CloakInvisibility.clone(getPool(CloakInvisibility.class)).clone(item);
		case 26: // 체력 회복제
		case 235: // 주홍 물약
		case 237: // 빨간 물약
		case 238: // 맑은 물약
		case 255: // 고급 체력 회복제
		case 328: // 강력 체력 회복제
		case 794: // 엔트의 열매
		case 1251: // 농축 체력 회복제
		case 1252: // 농축 고급 체력 회복제
		case 1253: // 농축 강력 체력 회복제
		case 1943: // 토끼의 간
		case 2575: // 신속 체력 회복제
		case 2576: // 신속 고급 체력 회복제
		case 2577: // 신속 강력 체력 회복제
		case 3301: // 상아탑의 체력 회복제
		case 852372: // 오렌지 주스
		case 1062372: // 사과 주스
		case 1072372: // 바나나 주스
		case 3403: // 그을린 빵조각
		case 3404: // 타다남은 빵조각
		case 3405: // 와인
			return HealingPotion.clone(getPool(HealingPotion.class)).clone(item);
		case 110: // 엘븐 와퍼
		case 4373: // 강화 엘븐 와퍼
			return ElvenWafer.clone(getPool(ElvenWafer.class)).clone(item);
		case 170: // 요정족 망토
			return ElvenCloak.clone(getPool(ElvenCloak.class)).clone(item);
		case 187: // 요정족 방패
		case 419: // 에바의 방패
			return ElvenShield.clone(getPool(ElvenShield.class)).clone(item);
		case 232: // 파란물약
		case 507: // 마력 회복 물약
		case 7737: // 농축 마력의 물약
			return BluePotion.clone(getPool(BluePotion.class)).clone(item);
		case 233: // 비취물약
		case 763: // 엔트의 줄기.
			return CurePoisonPotion.clone(getPool(CurePoisonPotion.class)).clone(item);
		case 234: // 초록물약
		case 264: // 속도향상물약 
		case 3302: // 상아탑의 속도향상 물약
		case 3406: // 위스키
			return HastePotion.clone(getPool(HastePotion.class)).clone(item);
		case 239: // 불투명 물약
			return BlindingPotion.clone(getPool(BlindingPotion.class)).clone(item);
		case 241: // 순간이동 조종 반지
			return RingTeleportControl.clone(getPool(RingTeleportControl.class)).clone(item);
		case 49: // 벨록스 넵
		case 257: // 부활 주문서
			return ScrollResurrection.clone(getPool(ScrollResurrection.class)).clone(item);
		case 263: // 흑단 막대
			return EbonyWand.clone(getPool(EbonyWand.class)).clone(item);
		case 326: // 랜턴
			return Lantern.clone(getPool(Lantern.class)).clone(item);
		case 327: // 랜턴용 기름
			return LanternOil.clone(getPool(LanternOil.class)).clone(item);
		case 343: // 슬라임 레이스표
		case 1247: // 개 레이스 표
			return RaceTicket.clone(getPool(RaceTicket.class)).clone(item);
		case 416: // 악운의 단검
			return DiceDagger.clone(getPool(DiceDagger.class)).clone(item);
//		case 418: // 광전사의도끼
//			return Berserker.clone(getPool(Berserker.class)).clone(item);
		case 617: // 빨간 양말
			return RedSock.clone(getPool(RedSock.class)).clone(item);
		case 623: // 괴물 눈 고기
			return MonsterEyeMeat.clone(getPool(MonsterEyeMeat.class)).clone(item);
		case 762: // 정령의 돌
			return ElementalStone.clone(getPool(ElementalStone.class)).clone(item);
		case 777: // 마법의 플룻
			return MagicFlute.clone(getPool(MagicFlute.class)).clone(item);
		case 954: // 여관 열쇠
			return InnRoomKey.clone(getPool(InnRoomKey.class)).clone(item);
		case 938: // 마법의 투구: 치유
			return HelmMagicHealing.clone(getPool(HelmMagicHealing.class)).clone(item);
		case 939: // 마법의 투구: 신속
			return HelmMagicSpeed.clone(getPool(HelmMagicSpeed.class)).clone(item);
		case 940: // 마법의 투구: 힘
			return HelmMagicPower.clone(getPool(HelmMagicPower.class)).clone(item);
		case 1008: // 인프라비젼 투구
			return HelmInfravision.clone(getPool(HelmInfravision.class)).clone(item);
		case 943: // 용기의 물약
		case 3372: // 악마의 피
			return BraveryPotion.clone(getPool(BraveryPotion.class)).clone(item);
		case 971: // 변신 주문서
		case 3300: // 상아탑의 변신 주문서
			return ScrollPolymorph.clone(getPool(ScrollPolymorph.class)).clone(item);
		case 975: // 추방 막대
			return ExpulsionWand.clone(getPool(ExpulsionWand.class)).clone(item);
		case 1086: // 펫 호루라기
			return PetWhistle.clone(getPool(PetWhistle.class)).clone(item);
		case 1100: // 숫돌
			return Whetstone.clone(getPool(Whetstone.class)).clone(item);
		case 1486: // 빈 주문서 (레벨 1)
		case 1892: // 2
		case 1893: // 3
		case 1894: // 4
		case 1895: // 5
			return BlankScroll.clone(getPool(BlankScroll.class)).clone(item);
		case 1507: // 에바의 물약
		case 1508: // 인어의 비늘
			return BlessEva.clone(getPool(BlessEva.class)).clone(item);
		case 1652234: // 강화 초록 물약
		case 1652264: // 강화 속도향상 물약
			return HastePotion.clone(getPool(HastePotion.class)).clone(item);
		case 517: // 마법서 (힐)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 1, 0).clone(item);
		case 518: // 마법서 (라이트)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 1, 1).clone(item);
		case 519: // 마법서 (실드)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 1, 2).clone(item);
		case 520: // 마법서 (에너지 볼트)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 1, 3).clone(item);
		case 521: // 마법서 (텔리포트)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 1, 4).clone(item);
		case 522: // 마법서 (큐어 포이즌)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 2, 0).clone(item);
		case 523: // 마법서 (칠 터치)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 2, 1).clone(item);
		case 524: // 마법서 (커스: 포이즌)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 2, 2).clone(item);
		case 525: // 마법서 (인챈트 웨폰)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 2, 3).clone(item);
		case 526: // 마법서 (디텍션)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 2, 4).clone(item);
		case 527: // 마법서 (라이트닝)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 3, 0).clone(item);
		case 528: // 마법서 (턴 언데드)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 3, 1).clone(item);
		case 529: // 마법서 (익스트라 힐)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 3, 2).clone(item);
		case 530: // 마법서 (커스: 블라인드)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 3, 3).clone(item);
		case 531: // 마법서 (블레스드 아머)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 3, 4).clone(item);
		case 532: // 마법서 (파이어볼)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 4, 0).clone(item);
		case 533: // 마법서 (피지컬 인챈트: DEX)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 4, 1).clone(item);
		case 534: // 마법서 (웨폰 브레이크)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 4, 2).clone(item);
		case 535: // 마법서 (뱀파이어릭 터치)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 4, 3).clone(item);
		case 536: // 마법서 (슬로우)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 4, 4).clone(item);
		case 537: // 마법서 (커스: 패럴라이즈)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 5, 0).clone(item);
		case 538: // 마법서 (콜 라이트닝)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 5, 1).clone(item);
		case 539: // 마법서 (그레이터 힐)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 5, 2).clone(item);
		case 540: // 마법서 (테이밍 몬스터)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 5, 3).clone(item);
		case 541: // 마법서 (리무브 커스)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 5, 4).clone(item);
		case 542: // 마법서 (크리에이트 좀비)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 6, 0).clone(item);
		case 543: // 마법서 (피지컬 인챈트: STR)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 6, 1).clone(item);
		case 544: // 마법서 (헤이스트)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 6, 2).clone(item);
		case 545: // 마법서 (캔슬레이션)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 6, 3).clone(item);
		case 546: // 마법서 (이럽션)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 6, 4).clone(item);
		case 547: // 마법서 (힐 올)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 7, 0).clone(item);
		case 548: // 마법서 (아이스 랜스)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 7, 1).clone(item);
		case 549: // 마법서 (서먼 몬스터)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 7, 2).clone(item);
		case 550: // 마법서 (홀리 워크)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 7, 3).clone(item);
		case 551: // 마법서 (토네이도)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 7, 4).clone(item);
		case 552: // 마법서 (풀 힐)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 8, 0).clone(item);
		case 553: // 마법서 (파이어월)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 8, 1).clone(item);
		case 554: // 마법서 (블리자드)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 8, 2).clone(item);
		case 555: // 마법서 (인비지블리티)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 8, 3).clone(item);
		case 556: // 마법서 (리절렉션)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 8, 4).clone(item);
		case 557: // 마법서 (라이트닝 스톰)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 9, 0).clone(item);
		case 558: // 마법서 (포그 오브 슬리핑)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 9, 1).clone(item);
		case 559: // 마법서 (셰이프 체인지)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 9, 2).clone(item);
		case 560: // 마법서 (이뮨 투 함)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 9, 3).clone(item);
		case 561: // 마법서 (매스 텔리포트)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 9, 4).clone(item);
		case 562: // 마법서 (크리에이트 매지컬 웨폰)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 10, 0).clone(item);
		case 563: // 마법서 (미티어 스트라이크)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 10, 1).clone(item);
		case 564: // 마법서 (리플렉팅 풀)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 10, 2).clone(item);
		case 565: // 마법서 (스톱)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 10, 3).clone(item);
		case 566: // 마법서 (디스인티그레이트)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 10, 4).clone(item);
		case 1581: // 마법서 (아이스 대거)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 1, 5).clone(item);
		case 1582: // 마법서 (윈드 커터)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 1, 6).clone(item);
		case 1583: // 마법서 (파이어 애로우)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 2, 6).clone(item);
		case 3259: // 기술서 (쇼크 스턴)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 2, 7).clone(item);
		case 1585: // 마법서 (프로즌 클라우드)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 3, 5).clone(item);
		case 1866: // 마법서 (버서커스)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 3, 6).clone(item);
		case 1586: // 마법서 (어스 재일)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 4, 5).clone(item);
		case 1587: // 마법서 (콘 오브 콜드)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 5, 5).clone(item);
		case 1588: // 마법서 (선 버스트)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 6, 5).clone(item);
		case 1589: // 마법서 (어스 퀘이크)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 8, 5).clone(item);
		case 1590: // 마법서 (파이어 스톰)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 9, 5).clone(item);
		case 1651: // 마법서 (그레이터 헤이스트)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 7, 5).clone(item);
		case 1857: // 마법서 (홀리 웨폰)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 1, 7).clone(item);
		case 1858: // 마법서 (디크리즈 웨이트)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 2, 5).clone(item);
		case 1859: // 마법서 (위크 엘리멘트)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 3, 6).clone(item);
		case 1860: // 마법서 (카운터 매직)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 4, 6).clone(item);
		case 1861: // 마법서 (메디테이션)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 4, 7).clone(item);
		case 1862: // 마법서 (마나 드레인)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 5, 6).clone(item);
		case 1863: // 마법서 (다크니스)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 5, 7).clone(item);
		case 1864: // 마법서 (위크니스)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 6, 6).clone(item);
		case 1865: // 마법서 (블레스 웨폰)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 6, 7).clone(item);
		case 4007: // 기술서 (리덕션 아머)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 7, 6).clone(item);
		case 1867: // 마법서 (디지즈)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 7, 7).clone(item);
		case 4008: // 기술서 (바운스 어택)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 8, 6).clone(item);
		case 1869: // 마법서 (사일런스)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 8, 7).clone(item);
		case 1870: // 마법서 (디케이 포션)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 9, 6).clone(item);
		case 4712: // 기술서 (솔리드 캐리지)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 9, 7).clone(item);
		case 1872: // 마법서 (맵솔루트 배리어)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 10, 5).clone(item);
		case 4713: // 기술서 (카운터 배리어)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 10, 7).clone(item);
		case 1873: // 마법서 (어드밴스 스피릿)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 9, 2).clone(item);
		case 1959: // 마법서 (트루 타겟)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 15, 0).clone(item);
		case 1960: // 마법서 (글로잉 오라)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 15, 1).clone(item);
		case 3175: // 마법서 (샤이닝 오라)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 15, 2).clone(item);
		case 2089: // 마법서 (콜 클렌)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 15, 3).clone(item);
		case 3176: // 마법서 (브레이브 멘탈)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 15, 4).clone(item);
		case 15721: // 마법서 (브레이브 아바타)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 15, 5).clone(item);
		case 1829: // 정령의 수정 (레지스트 매직)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 17, 0).clone(item);
		case 1830: // 정령의 수정 (바디 투 마인드)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 17, 1).clone(item);
		case 1831: // 정령의 수정 (텔레포트 투 마더)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 17, 2).clone(item);
		case 1832: // 정령의 수정 (클리어 마인드)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 18, 0).clone(item);
		case 1833: // 정령의 수정 (레지스트 엘리멘트)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 18, 1).clone(item);
		case 3261: // 정령의 수정 (트리플 애로우)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 19, 0).clone(item);
		case 1835: // 정령의 수정 (블러드 투 소울)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 19, 1).clone(item);
		case 1839: // 정령의 수정 (이글 아이)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 19, 2).clone(item);
		case 4716: // 정령의 수정 (아쿠아 프로텍트)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 19, 3).clone(item);
		case 4717: // 정령의 수정 (폴루트 워터)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 19, 4).clone(item);
		case 1848: // 정령의 수정 (어스 가디언)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 19, 5).clone(item);
		case 4718: // 정령의 수정 (스트라이커 게일)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 19, 6).clone(item);
		case 1841: // 정령의 수정 (인탱글)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 19, 7).clone(item);
		case 1842: // 정령의 수정 (이레이즈 매직)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 20, 0).clone(item);
		case 1851: // 정령의 수정 (버닝 웨폰)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 20, 1).clone(item);		
		case 1836: // 정령의 수정 (프로텍션 프롬 엘리멘트)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 19, 2).clone(item);
		case 1840: // 정령의 수정 (어스 스킨)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 19, 6).clone(item);
		case 1843: // 정령의 수정 (서먼 레서 엘리멘탈)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 20, 1).clone(item);
		case 3267: // 정령의 수정 (엘리멘탈 파이어)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 20, 2).clone(item);
		case 1845: // 정령의 수정 (아이 오브 스톰)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 20, 3).clone(item);
		case 3265: // 정령의 수정 (엑조틱 바이탈라이즈)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 20, 6).clone(item);
		case 1847: // 정령의 수정 (네이쳐스 터치)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 20, 5).clone(item);
		case 1849: // 정령의 수정 (에어리어 오브 사일런스)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 21, 0).clone(item);
		case 4715: // 정령의 수정 (어디셔널 파이어)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 21, 1).clone(item);
		case 3266: // 정령의 수정 (워터 라이프)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 21, 2).clone(item);
		case 1852: // 정령의 수정 (네이쳐스 블레싱)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 21, 3).clone(item);
		case 1846: // 정령의 수정 (어스 바인드)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 21, 4).clone(item);
		case 1854: // 정령의 수정 (스톰 샷)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 21, 5).clone(item);
		case 4714: // 정령의 수정 (소울 오브 프레임)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 21, 6).clone(item);
		case 1856: // 정령의 수정 (아이언 스킨)
			return ItemCrystalInstance.clone(getPool(ItemCrystalInstance.class), 21, 7).clone(item);
		case 1173: // 펫 목걸이
			return DogCollar.clone(getPool(DogCollar.class)).clone(item);
		case 1101: // 지도 본토
			return MiniMap.clone(getPool(MiniMap.class), 16).clone(item);
		case 1102: // 지도 말섬
			return MiniMap.clone(getPool(MiniMap.class), 1).clone(item);
		case 1103: // 지도 글루딘
			return MiniMap.clone(getPool(MiniMap.class), 2).clone(item);
		case 1104: // 지도 켄트
			return MiniMap.clone(getPool(MiniMap.class), 3).clone(item);
		case 1105: // 지도 화민촌
			return MiniMap.clone(getPool(MiniMap.class), 4).clone(item);
		case 1106: // 지도 요숲
			return MiniMap.clone(getPool(MiniMap.class), 5).clone(item);
		case 1107: // 지도 우드벡
			return MiniMap.clone(getPool(MiniMap.class), 6).clone(item);
		case 1108: // 지도 은기사
			return MiniMap.clone(getPool(MiniMap.class), 7).clone(item);
		case 1109: // 지도 용계
			return MiniMap.clone(getPool(MiniMap.class), 8).clone(item);
		case 1188: // 지도 기란
			return MiniMap.clone(getPool(MiniMap.class), 9).clone(item);
		case 1533: // 지도 노섬
			return MiniMap.clone(getPool(MiniMap.class), 10).clone(item);
		case 1534: // 지도 숨계
			return MiniMap.clone(getPool(MiniMap.class), 11).clone(item);
		case 1535: // 지도 하이네
			return MiniMap.clone(getPool(MiniMap.class), 12).clone(item);
		case 1607: // 지도 웰던
			return MiniMap.clone(getPool(MiniMap.class), 13).clone(item);
		case 1889: // 지도 오렌
			return MiniMap.clone(getPool(MiniMap.class), 14).clone(item);
		case 1411: // 노래하는 섬 귀환 주문서
			return ScrollReturnSingingIsland.clone(getPool(ScrollReturnSingingIsland.class)).clone(item);
		case 1424: // 숨겨진 계곡 귀환 주문서
			return ScrollReturnHiddenValley.clone(getPool(ScrollReturnHiddenValley.class)).clone(item);
		case 2203: // 지도 아덴
			return MiniMap.clone(getPool(MiniMap.class), 15).clone(item);
		case 2543: // 지도 침동
			return MiniMap.clone(getPool(MiniMap.class), 17).clone(item);
		case 2582: // 봉인된 오만의 탑 11층 이동 부적
			return SealedTOITeleportCharm.clone(getPool(SealedTOITeleportCharm.class), 11).clone(item);
		case 2583: // 봉인된 오만의 탑 21층 이동 부적
			return SealedTOITeleportCharm.clone(getPool(SealedTOITeleportCharm.class), 21).clone(item);
		case 2584: // 봉인된 오만의 탑 31층 이동 부적
			return SealedTOITeleportCharm.clone(getPool(SealedTOITeleportCharm.class), 31).clone(item);
		case 2585: // 봉인된 오만의 탑 41층 이동 부적
			return SealedTOITeleportCharm.clone(getPool(SealedTOITeleportCharm.class), 41).clone(item);
		case 2668: // 봉인된 오만의 탑 51층 이동 부적
			return SealedTOITeleportCharm.clone(getPool(SealedTOITeleportCharm.class), 51).clone(item);
		case 2669: // 봉인된 오만의 탑 61층 이동 부적
			return SealedTOITeleportCharm.clone(getPool(SealedTOITeleportCharm.class), 61).clone(item);
		case 2670: // 봉인된 오만의 탑 71층 이동 부적
			return SealedTOITeleportCharm.clone(getPool(SealedTOITeleportCharm.class), 71).clone(item);
		case 2671: // 봉인된 오만의 탑 81층 이동 부적
			return SealedTOITeleportCharm.clone(getPool(SealedTOITeleportCharm.class), 81).clone(item);
		case 2672: // 봉인된 오만의 탑 91층 이동 부적
			return SealedTOITeleportCharm.clone(getPool(SealedTOITeleportCharm.class), 91).clone(item);
		case 2400: // 오만의 탑 11층 이동 부적
			return TOITeleportCharm.clone(getPool(TOITeleportCharm.class), 11).clone(item);
		case 2401: // 오만의 탑 21층 이동 부적
			return TOITeleportCharm.clone(getPool(TOITeleportCharm.class), 21).clone(item);
		case 2402: // 오만의 탑 31층 이동 부적
			return TOITeleportCharm.clone(getPool(TOITeleportCharm.class), 31).clone(item);
		case 2403: // 오만의 탑 41층 이동 부적
			return TOITeleportCharm.clone(getPool(TOITeleportCharm.class), 41).clone(item);
		case 2678: // 오만의 탑 51층 이동 부적
			return TOITeleportCharm.clone(getPool(TOITeleportCharm.class), 51).clone(item);
		case 2679: // 오만의 탑 61층 이동 부적
			return TOITeleportCharm.clone(getPool(TOITeleportCharm.class), 61).clone(item);
		case 2680: // 오만의 탑 71층 이동 부적
			return TOITeleportCharm.clone(getPool(TOITeleportCharm.class), 71).clone(item);
		case 2681: // 오만의 탑 81층 이동 부적
			return TOITeleportCharm.clone(getPool(TOITeleportCharm.class), 81).clone(item);
		case 2682: // 오만의 탑 91층 이동 부적
			return TOITeleportCharm.clone(getPool(TOITeleportCharm.class), 91).clone(item);
		case 3616: // 지도 해적섬
			return MiniMap.clone(getPool(MiniMap.class), 18).clone(item);
		case 2268: // 결혼 반지(사파이어)
		case 2269: // 결혼 반지(다이아몬드)
		case 2373: // 결혼 반지(에메랄드)
		case 2374: // 결혼 반지(루비)
		case 2375: // 결혼 반지(금)
		case 2376: // 결혼 반지(은)
		case 2750: // 오림의 결혼 반지
		case 2751: // 세마의 결혼 반지
		case 13322: // 고결한 결혼 반지
			return 결혼반지.clone(getPool(결혼반지.class)).clone(item);
		case 1075: // 편지지
		case 1606: // 크리스마스 카드
			return Letter.clone(getPool(Letter.class)).clone(item);
		case 1146: // 혈맹 편지지
			return PledgeLetter.clone(getPool(PledgeLetter.class)).clone(item);
		case 1755: // 붉은 열쇠
			return RedKey.clone(getPool(RedKey.class)).clone(item);
		case 1756: // 검은 열쇠
			return BlackKey.clone(getPool(BlackKey.class)).clone(item);
		case 2022: // 비밀방 열쇠
			return SecretRoomKey.clone(getPool(SecretRoomKey.class)).clone(item);
		case 2090: // 아리아의 보답
			return AriaReward.clone(getPool(AriaReward.class)).clone(item);
		case 2091: // 요정족 보물
			return ElvenTreasure.clone(getPool(ElvenTreasure.class)).clone(item);
		case 2380: // 혈맹 귀환 주문서
			return ScrollLabeledVerrYedHoraePledgeHouse.clone(getPool(ScrollLabeledVerrYedHoraePledgeHouse.class)).clone(item);
		case 1997: // 환상의 검
			return SwordOfIllusion.clone(getPool(SwordOfIllusion.class)).clone(item);
		case 1998: // 환상의 갑옷
			return ArmorOfIllusion.clone(getPool(ArmorOfIllusion.class)).clone(item);
		case 1999: // 환상의 활
			return BowOfIllusion.clone(getPool(BowOfIllusion.class)).clone(item);
		case 2000: // 환상의 무기 마법 주문서
			return ScrollofEnchantWeaponIllusion.clone(getPool(ScrollofEnchantWeaponIllusion.class)).clone(item);
		case 2001: // 환상의 갑옷 마법 주문서
			return ScrollOfEnchantArmorIllusion.clone(getPool(ScrollOfEnchantArmorIllusion.class)).clone(item);
		case 3258: // 봉인 주문서
			return SealedScroll.clone(ItemDatabase.getPool(SealedScroll.class)).clone(item);
		case 3268: // 봉인 해제 주문서
			return SealedCancelScroll.clone(ItemDatabase.getPool(SealedCancelScroll.class)).clone(item);
		case 5239: // 용해제
			return Solvent.clone(ItemDatabase.getPool(Solvent.class)).clone(item);
		case 14871436: // 마법주문서 (힐)
			return SpellScrollLesserHeal.clone(getPool(SpellScrollLesserHeal.class)).clone(item);
		case 14871437: // 마법주문서 (라이트)
			return SpellScrollLight.clone(getPool(SpellScrollLight.class)).clone(item);
		case 14871438: // 마법주문서 (실드)
			return SpellScrollShield.clone(getPool(SpellScrollShield.class)).clone(item);
		case 14871439: // 마법주문서 (에너지 볼트)
			return SpellScrollEnergyBolt.clone(getPool(SpellScrollEnergyBolt.class)).clone(item);
		case 14871966: // 마법주문서 (아이스 대거)
			return SpellScrollIceDagger.clone(getPool(SpellScrollIceDagger.class)).clone(item);
		case 14871967: // 마법주문서 (윈드커터)
			return SpellScrollWindShuriken.clone(getPool(SpellScrollWindShuriken.class)).clone(item);
		case 14871440: // 마법주문서 (텔레포트)
			return SpellScrollTeleport.clone(getPool(SpellScrollTeleport.class)).clone(item);
		case 14871977: // 마법주문서 (홀리웨폰)
			return SpellScrollHolyWeapon.clone(getPool(SpellScrollHolyWeapon.class)).clone(item);
		case 14871441: // 마법주문서 (큐어포이즌)
			return SpellScrollCurePoison.clone(getPool(SpellScrollCurePoison.class)).clone(item);
		case 14871442: // 마법주문서 (칠터치)
			return SpellScrollChillTouch.clone(getPool(SpellScrollChillTouch.class)).clone(item);
		case 14871443: // 마법주문서 (커스: 포이즌)
			return SpellScrollCursePoison.clone(getPool(SpellScrollCursePoison.class)).clone(item);
		case 14871444: // 마법주문서 (인첸트 웨폰)
			return SpellScrollEnchantWeapon.clone(getPool(SpellScrollEnchantWeapon.class)).clone(item);
		case 14871445: // 마법주문서 (디텍션)
			return SpellScrollDetection.clone(getPool(SpellScrollDetection.class)).clone(item);
		case 14871978: // 마법주문서 (디크리즈 웨이트)
			return SpellScrollDecreaseWeight.clone(getPool(SpellScrollDecreaseWeight.class)).clone(item);
		case 14871968: // 마법주문서 (파이어 애로우)
			return SpellScrollFireArrow.clone(getPool(SpellScrollFireArrow.class)).clone(item);
		case 14871969: // 마법주문서 (스탈락)
			return SpellScrollStalac.clone(getPool(SpellScrollStalac.class)).clone(item);
		case 14871446: // 마법주문서 (라이트닝)
			return SpellScrollLightning.clone(getPool(SpellScrollLightning.class)).clone(item);
		case 14871447: // 마법주문서 (턴 언데드)
			return SpellScrollTurnUndead.clone(getPool(SpellScrollTurnUndead.class)).clone(item);
		case 14871448: // 마법주문서 (익스트라 힐)
			return SpellScrollHeal.clone(getPool(SpellScrollHeal.class)).clone(item);
		case 14871449: // 마법주문서 (커스: 블라인드)
			return SpellScrollCurseBlind.clone(getPool(SpellScrollCurseBlind.class)).clone(item);
		case 14871450: // 마법주문서 (블레스드 아머)
			return SpellScrollBlessedArmor.clone(getPool(SpellScrollBlessedArmor.class)).clone(item);
		case 14871451: // 마법주문서 (파이어볼)
			return SpellScrollFireball.clone(getPool(SpellScrollFireball.class)).clone(item);
		case 14871452: // 마법주문서 (피지컬 인챈트: DEX)
			return SpellScrollPhysicalEnchantDex.clone(getPool(SpellScrollPhysicalEnchantDex.class)).clone(item);
		case 14871453: // 마법주문서 (웨폰 브레이크)
			return SpellScrollWeaponBreak.clone(getPool(SpellScrollWeaponBreak.class)).clone(item);
		case 14871454: // 마법주문서 (뱀파이어릭 터치)
			return SpellScrollVampiricTouch.clone(getPool(SpellScrollVampiricTouch.class)).clone(item);
		case 14871455: // 마법주문서 (슬로우)
			return SpellScrollSlow.clone(getPool(SpellScrollSlow.class)).clone(item);
		case 14871462: // 마법주문서 (피지컬 인챈트: STR)
			return SpellScrollPhysicalEnchantStr.clone(getPool(SpellScrollPhysicalEnchantStr.class)).clone(item);
		case 14871971: // 마법주문서 (어스 재일)
			return SpellScrollEarthJail.clone(getPool(SpellScrollEarthJail.class)).clone(item);
		case 14871980: // 마법주문서 (카운터 매직)
			return SpellScrollCounterMagic.clone(getPool(SpellScrollCounterMagic.class)).clone(item);
		case 14871981: // 마법주문서 (메디테이션)
			return SpellScrollMeditation.clone(getPool(SpellScrollMeditation.class)).clone(item);
		case 14871485: // 마법주문서 (디스인그레이트)
			return SpellScrollDestroy.clone(getPool(SpellScrollDestroy.class)).clone(item);
		case 14871479: // 마법주문서 (이뮨 투 함)
			return SpellScrollImmunetoHarm.clone(getPool(SpellScrollImmunetoHarm.class)).clone(item);
		case 14871992: // 마법주문서 (앱솔루트 베리어)
			return SpellScrollAbsoluteBarrier.clone(getPool(SpellScrollAbsoluteBarrier.class)).clone(item);
		case 14871970: // 마법주문서 (프로즌클라우드)
			return SpellScrollFrozenCloud.clone(getPool(SpellScrollFrozenCloud.class)).clone(item);
		case 14871456: // 마법주문서 (커스: 패럴라이즈)
			return SpellScrollCurseParalyze.clone(getPool(SpellScrollCurseParalyze.class)).clone(item);
		case 14871457: // 마법주문서 (콜 라이트닝)
			return SpellScrollCallLightning.clone(getPool(SpellScrollCallLightning.class)).clone(item);
		case 14871458: // 마법주문서 (그레이터 힐)
			return SpellScrollGreaterHeal.clone(getPool(SpellScrollGreaterHeal.class)).clone(item);
		case 14871459: // 마법주문서 (테이밍 몬스터)
			return SpellScrollTameMonster.clone(getPool(SpellScrollTameMonster.class)).clone(item);
		case 14871460: // 마법주문서 (리무브 커스)
			return SpellScrollRemoveCurse.clone(getPool(SpellScrollRemoveCurse.class)).clone(item);
		case 14871972: // 마법주문서 (콘 오브 콜드)
			return SpellScrollConeOfCold.clone(getPool(SpellScrollConeOfCold.class)).clone(item);
		case 14871982: // 마법주문서 (마나 드레인)
			return SpellScrollManaDrain.clone(getPool(SpellScrollManaDrain.class)).clone(item);
		case 14871983: // 마법주문서 (다크니스)
			return SpellScrollDarkness.clone(getPool(SpellScrollDarkness.class)).clone(item);
		case 14871985: // 마법주문서 (블레스 웨폰)
			return SpellScrollBlessWeapon.clone(getPool(SpellScrollBlessWeapon.class)).clone(item);
		case 14871993: // 마법주문서 (어드밴스 스피릿)
			return SpellScrollAdvanceSpirit.clone(getPool(SpellScrollAdvanceSpirit.class)).clone(item);
		case 3278: // 말하는 두루마리
			return TalkingScroll.clone(getPool(TalkingScroll.class)).clone(item);
		case 2530: // 엘릭서-STR
		case 2532: // dex
		case 2531: // con
		case 2534: // wis
		case 2533: // int
		case 2535: // cha
			return ElixirPotion.clone(getPool(ElixirPotion.class)).clone(item);
		case 2578: // 송편
		case 2579: // 쑥송편
		case 3370: // 정신력의 물약
			return ManaPotion.clone(getPool(ManaPotion.class)).clone(item);
		case 2518: // 흑정령의 수정 (블라인드 하이딩)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 13, 0).clone(item);
		case 2519: // 흑정령의 수정 (인챈트 베놈)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 13, 1).clone(item);
		case 2520: // 흑정령의 수정 (쉐도우 아머)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 13, 2).clone(item);
		case 2521: // 흑정령의 수정 (브링 스톤)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 13, 3).clone(item);
		case 2522: // 흑정령의 수정 (무빙 악셀레이션)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 13, 4).clone(item);
		case 2523: // 흑정령의 수정 (버닝 스피릿츠)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 13, 5).clone(item);
		case 2524: // 흑정령의 수정 (다크 블라인드)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 13, 6).clone(item);
		case 2525: // 흑정령의 수정 (베놈 레지스트)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 13, 7).clone(item);
		case 2526: // 흑정령의 수정 (더블 브레이크)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 14, 0).clone(item);
		case 2527: // 흑정령의 수정 (언케니 닷지)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 14, 1).clone(item);
		case 2528: // 흑정령의 수정 (쉐도우 팽)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 14, 2).clone(item);
		case 2529: // 흑정령의 수정 (파이널 번)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 14, 3).clone(item);
		case 2810: // 진화의 열매
			return EvolutionFruit.clone(getPool(EvolutionFruit.class)).clone(item);
		case 3172: // 흑정령의 수정 (드레스 마이티)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 14, 4).clone(item);
		case 3173: // 흑정령의 수정 (드레스 덱스터리티)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 14, 5).clone(item);
		case 3174: // 흑정령의 수정 (드레스 이베이젼)
			return ItemDarkSpiritCrystalInstance.clone(getPool(ItemDarkSpiritCrystalInstance.class), 14, 6).clone(item);
		case 3303: // 말하는 섬 마을 귀환 주문서
			return ScrollReturnTalkingIslandVillage.clone(getPool(ScrollReturnTalkingIslandVillage.class)).clone(item);
		case 3304: // 글루딘 마을 귀환 주문서
			return ScrollReturnGludinTown.clone(getPool(ScrollReturnGludinTown.class)).clone(item);
		case 3305: // 켄트 마을 귀환 주문서
			return ScrollReturnKentVillage.clone(getPool(ScrollReturnKentVillage.class)).clone(item);
		case 3306: // 우드벡 마을 귀환 주문서
			return ScrollReturnWoodbecVillage.clone(getPool(ScrollReturnWoodbecVillage.class)).clone(item);
		case 3307: // 화전민 마을 귀환 주문서
			return ScrollReturnOrctown.clone(getPool(ScrollReturnOrctown.class)).clone(item);
		case 3308: // 요정숲 귀환 주문서
			return ScrollReturnElvenForest.clone(getPool(ScrollReturnElvenForest.class)).clone(item);
		case 3309: // 은기사 마을 귀환 주문서
			return ScrollReturnSilverKnightTown.clone(getPool(ScrollReturnSilverKnightTown.class)).clone(item);
		case 3310: // 기란 마을 귀환 주문서
			return ScrollReturnGiranCity.clone(getPool(ScrollReturnGiranCity.class)).clone(item);
		case 3311: // 하이네 마을 귀환 주문서
			return ScrollReturnHeineCity.clone(getPool(ScrollReturnHeineCity.class)).clone(item);
		case 3312: // 오렌 마을 귀환 주문서
			return ScrollReturnIvoryTowerTown.clone(getPool(ScrollReturnIvoryTowerTown.class)).clone(item);
		case 3313: // 웰던 마을 귀환 주문서
			return ScrollReturnWerldernTown.clone(getPool(ScrollReturnWerldernTown.class)).clone(item);
		case 3314: // 아덴 마을 귀환 주문서
			return ScrollReturnAdenCity.clone(getPool(ScrollReturnAdenCity.class)).clone(item);
		case 3315: // 침묵의 동굴 귀환 주문서
			return ScrollReturnSilentCavern.clone(getPool(ScrollReturnSilentCavern.class)).clone(item);
		case 5419:
		case 5226:
		case 5418:
		case 7448:
		case 21343:
		case 5227:
		case 5417:
		case 7511:
		case 20870:
		case 20880:
		case 20881:
		case 20878:
		case 20879:
		case 5225:
		case 20872:
		case 21313:
		case 20466:
		case 20477:
		case 20478:
		case 12855:
		case 20873:
		case 21314:
		case 15574:
		case 20461:
		case 20875:
		case 20877:
		case 11144:
		case 11145:
		case 24846:
		case 20874:
		case 20876:
		case 11146:
		case 24847:
		case 25694:
		case 25695:
		case 25696:
			return MagicDoll.clone(getPool(MagicDoll.class)).clone(item);
		case 5725: // 바람의 무기 강화 주문서
		case 5726: // 대지의 무기 강화 주문서
		case 5727: // 물의 무기 강화 주문서
		case 5728: // 불의 무기 강화 주문서
			return ScrollOfEnchantElementalWeapon.clone(getPool(ScrollOfEnchantElementalWeapon.class)).clone(item);
		case 5132: // 드레이크 선장 변신터번
		case 5163: // 기마투구
			return Turban.clone(getPool(Turban.class)).clone(item);
		//case 5163: // 기마투구
		//	return 기마투구.clone(getPool(기마투구.class)).clone(item);
		case 1937: // 소환 조종 반지
			return RingSummonControl.clone(getPool(RingSummonControl.class)).clone(item);
		case 4922: // 장작
			return MagicFirewood.clone(getPool(MagicFirewood.class)).clone(item);
		case 4923: // 요리책 : 1단계
		case 4924:
		case 4925:
		case 4926:
		case 4927: // 요리책 : 5단계
			return CookBook.clone(getPool(CookBook.class)).clone(item);
		case 944:  // 지혜의 물약
		case 7738: // 농축 지혜의 물약
			return WisdomPotion.clone(getPool(WisdomPotion.class)).clone(item);
		case 5204: // 점술사 항아리
			return Astrologist.clone(getPool(Astrologist.class)).clone(item);
		case 5861: // 레벨업 지원
			return Exp_support.clone(getPool(Exp_support.class)).clone(item);
		case 17535: // 장인의 무기 마법 주문서
			return ScrollOfWeapon.clone(getPool(ScrollOfWeapon.class)).clone(item);
		default:
			if (item.getType1().equalsIgnoreCase("weapon")) {
				if (item.getType2().equalsIgnoreCase("arrow")) {
					return Arrow.clone(getPool(Arrow.class)).clone(item);
				}  else {
					return ItemWeaponInstance.clone(getPool(ItemWeaponInstance.class)).clone(item);
				}
			} else if (item.getType1().equalsIgnoreCase("armor")) {
				return ItemArmorInstance.clone(getPool(ItemArmorInstance.class)).clone(item);
			} else if (item.getType1().equalsIgnoreCase("item")) {
				return newDefaultItem(item);
			} else {
				return ItemInstance.clone(getPool(ItemInstance.class)).clone(item);
			}
		}
	}

	/**
	 * 아이템 생성처리 함수. : 관리를 위해 함수 따로 뺌. : item타입에 type2값에 의한 생성처리.
	 * 
	 * @param item
	 * @return
	 */
	static private ItemInstance newDefaultItem(Item item) {
		if (item.getType2().equalsIgnoreCase("bravery potion")) {
			// 용기 물약
			return BraveryPotion.clone(getPool(BraveryPotion.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("scroll_levelup")) {
			// 레벨업 주문서
			return LevelUpScroll.clone(getPool(LevelUpScroll.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("scroll_leveldown")) {
			// 레벨다운 주문서
			return LevelDownScroll.clone(getPool(LevelDownScroll.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("$2380")) {
			// 혈맹 귀환 주문서
			return ScrollLabeledVerrYedHoraePledgeHouse.clone(getPool(ScrollLabeledVerrYedHoraePledgeHouse.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("firework")) {
			// 폭죽
			return Firework.clone(getPool(Firework.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("change_sex")) {
			// 캐릭터 성별 변경 주문서
			return ChangeSexPotion.clone(getPool(ChangeSexPotion.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("bundle")) {
			// 번들 아이템
			return Bundle.clone(getPool(Bundle.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("chance_bundle")) {
			// 확률 번들 아이템
			return ChanceBundle.clone(getPool(ChanceBundle.class)).clone(item);

		} else if (item.getType2().startsWith("teleport_")) {
			// 이동 주문서
			return ScrollTeleport.clone(getPool(ScrollTeleport.class)).clone(item);

	

		} else if (item.getType2().equalsIgnoreCase("dog collar")) {
			// 펫 목걸이
			return DogCollar.clone(getPool(DogCollar.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("dog whistle")) {
			// 펫 호루라기
			return PetWhistle.clone(getPool(PetWhistle.class)).clone(item);

		} else if (item.getType2().startsWith("healing potion")) {
			// 체력 회복 물약
			return HealingPotion.clone(getPool(HealingPotion.class)).clone(item);
		} else if (item.getType2().startsWith("pet potion")) {
			// 체력 회복 물약
			return PetPotion.clone(getPool(PetPotion.class)).clone(item);
			
		} else if (item.getType2().startsWith("exp_marble")) {
			return Exp_marble.clone(getPool(Exp_marble.class)).clone(item);

		} else if (item.getType2().startsWith("haste potion")) {
			// 초록 물약
			return HastePotion.clone(getPool(HastePotion.class)).clone(item);
		} else if (item.getType2().startsWith("mana_potion")) {
			return aManaPotion.clone(getPool(aManaPotion.class)).clone(item);
		} else if (item.getType2().startsWith("blue potion")) {
			// 파란 물약
			return BluePotion.clone(getPool(BluePotion.class)).clone(item);

		} else if (item.getType2().startsWith("elven wafer")) {
			// 엘븐와퍼
			return ElvenWafer.clone(getPool(ElvenWafer.class)).clone(item);

		} else if (item.getType2().startsWith("verr yed horae")) {
			// 귀환 주문서
			return ScrollLabeledVerrYedHorae.clone(getPool(ScrollLabeledVerrYedHorae.class)).clone(item);

		} else if (item.getType2().startsWith("kernodwel")) {
			// 확인 주문서
			return ScrollLabeledKernodwel.clone(getPool(ScrollLabeledKernodwel.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("Seal_enchant")) {

			return Seal_enchant.clone(getPool(Seal_enchant.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("자동사냥 계정 시간 초기화")) {
			return autohuntreset.clone(getPool(autohuntreset.class)).clone(item);	
		} else if (item.getType2().equalsIgnoreCase("자동사냥 계정 시간 초기화2")) {
			return autohuntreset2.clone(getPool(autohuntreset2.class)).clone(item);	
	
		} else if (item.getType2().startsWith("polymorph")) {
			// 변신 주문서
			return ScrollPolymorph.clone(getPool(ScrollPolymorph.class)).clone(item);

		} else if (item.getType2().startsWith("venzar borgavve")) {
			// 순간이동 주문서
			return ScrollLabeledVenzarBorgavve.clone(getPool(ScrollLabeledVenzarBorgavve.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("change_name")) {
			// 케릭명 변경 주문서.
			return ScrollChangeName.clone(getPool(ScrollChangeName.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("stat_clear")) {
			// 스탯 초기화
			return StatClear.clone(getPool(StatClear.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("exp_potion")) {
			// 경험치 물약
			return Exp_potion.clone(getPool(Exp_potion.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("buff_potion")) {
			// 버프 물약
			return Buff_potion.clone(getPool(Buff_potion.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("change_bless")) {
			// 축복 부여 주문서
			return ScrollOfChangeBless.clone(getPool(ScrollOfChangeBless.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("change_bless2")) {
			// 축복 부여 주문서
			return ScrollOfChangeBlessdoll.clone(getPool(ScrollOfChangeBlessdoll.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("fishing_rice")) {
			// 영양 미끼
			return Fishing_rice.clone(getPool(Fishing_rice.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("인형 각성 주문서")) {
			// 인형 진화 주문서
			return DollAwaken.clone(getPool(DollAwaken.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("accessory_scroll")) {
			// 장신구 주문서	
			return ScrollOfAccessory.clone(getPool(ScrollOfAccessory.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("scroll_orim_armor")) {
			// 오림의 갑옷 마법 주문서	
			return ScrollOfOrimArmor.clone(getPool(ScrollOfOrimArmor.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("scroll_orim_weapon")) {
			// 오림의 무기 마법 주문서	
			return ScrollOfOrimWeapon.clone(getPool(ScrollOfOrimWeapon.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("money")) {
			// 은괴, 금괴	
			return GoldBar.clone(getPool(GoldBar.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("sword_lack")) {
			// 칼렉풀기
			return Sword_lack.clone(getPool(Sword_lack.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("lawful_potion")) {
			// 라우풀 물약
			return Lawful_potion.clone(getPool(Lawful_potion.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("caotic_potion")) {
			// 카오틱 물약
			return Caotic_potion.clone(getPool(Caotic_potion.class)).clone(item);

		} else if (item.getType2().startsWith("oman_")) {
			// 오만의탑 이동 주문서
			return TOITeleportScroll.clone(getPool(TOITeleportScroll.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("pvp_clean")) {
			// 킬&데스 초기화권
			return PvP_clean.clone(getPool(PvP_clean.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("new_clan_join")) {
			// 신규혈맹 가입 주문서
			return ScrollOfNewClanJoin.clone(getPool(ScrollOfNewClanJoin.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("giran_time_scroll")) {
			// 기란감옥 시간 초기화 주문서
			return ScrollOfGiranDungeon.clone(getPool(ScrollOfGiranDungeon.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("scroll_of_metis")) {
			// 메티스의 축복
			return ScrollOfMetis.clone(getPool(ScrollOfMetis.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("hp_mp_set_scroll")) {
			// HP/MP 재조정 주문서
			return ScrollOfHpMpReset.clone(getPool(ScrollOfHpMpReset.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("몹정리")) {
			// 전투 강화 물약
			return monsterClean.clone(getPool(monsterClean.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("fight_potion")) {
			// 전투 강화 물약
			return FightPotion.clone(getPool(FightPotion.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("영혼석")) {
			// 몬스터 영혼석
			return monstersoul.clone(getPool(monstersoul.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("자동 칼질")) {
			// 자동 칼질
			return AutoAttackItem.clone(getPool(AutoAttackItem.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("텔레포트 막대")) {
			// 텔레포트 막대
			return TeleportWand.clone(getPool(TeleportWand.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("자동 판매")) {
			// 자동 물약
			return Autosellitem.clone(getPool(Autosellitem.class)).clone(item);	
			
		} else if (item.getType2().equalsIgnoreCase("초보자용 빨간 물약") ||
				   item.getType2().equalsIgnoreCase("초보자용 주홍 물약") ||
				   item.getType2().equalsIgnoreCase("초보자용 맑은 물약")) {
			// 초보자용 빨간 물약
			return HealingPotion.clone(getPool(HealingPotion.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("rank_poly")) {
			// 랭킹 변신 주문서
			return ScrollOfRankPoly.clone(getPool(ScrollOfRankPoly.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("rank_poly2")) {
			// 랭킹 변신 주문서
			return ScrollOfRankPoly2.clone(getPool(ScrollOfRankPoly2.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("myth_rank_poly")) {
			// 랭킹 변신 주문서
			return ScrollOfmythRankPoly.clone(getPool(ScrollOfmythRankPoly.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("item_swap")) {
			// 장비 스왑
			return ItemSwap.clone(getPool(ItemSwap.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("market_icon")) {
			return ShopControllerItem.clone(getPool(ShopControllerItem.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("자동 물약")) {
			// 자동 물약
			return AutoPotion.clone(getPool(AutoPotion.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("한방군업")) {
			return LordBuff.clone(getPool(LordBuff.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("서버 정보")) {
			return notice.clone(getPool(notice.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("서버 가이드")) {
			return guide.clone(getPool(guide.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("서버 공지")) {
			return at.clone(getPool(at.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("서버 업데이트")) {
			return update.clone(getPool(update.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("사냥터 이동")) {
			return huntgo.clone(getPool(notice.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("혈맹 파티")) {
			// 자동 물약
			return cpaty.clone(getPool(cpaty.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("dungeon_tellbook")) {
			return HuntingZoneTeleportationBook.clone(getPool(HuntingZoneTeleportationBook.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("생명의 나뭇잎")) {
			// 생명의 나뭇잎
			return LifeLost.clone(getPool(LifeLost.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("샴페인")) {
			return BraveryPotion2.clone(getPool(BraveryPotion2.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("maan")) {
			// 수/풍/지/화/탄생/형상/생명의 마안
			return BuffMaan.clone(getPool(BuffMaan.class)).clone(item);


		} else if (item.getType2().equalsIgnoreCase("무한 물약")) {
			// 무한 물약
			return HealingPotion.clone(getPool(HealingPotion.class)).clone(item);

		} else if (item.getType2().contains("드래곤의")) {
			// 경험치 물약
			return Exp_drop_potion.clone(getPool(Exp_drop_potion.class)).clone(item);

		} else if (item.getType2().contains("무한 경험치 물약")) {
			// 무한 경험치 물약
			return Exp_drop_potion.clone(getPool(Exp_drop_potion.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("royal_shockstun")) {
			// 군주 기술서 (쇼크 스턴)
			return ItemBookInstance.clone(getPool(ItemBookInstance.class), 2, 7).clone(item);
			

		} else if (item.getType2().equalsIgnoreCase("최대 HP 증가 물약")) {
			// 최대 HP 증가 물약
			return MaxHPIncreasePotion.clone(getPool(MaxHPIncreasePotion.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("최대 MP 증가 물약")) {
			// 최대 MP 증가 물약
			return MaxMPIncreasePotion.clone(getPool(MaxMPIncreasePotion.class)).clone(item);
			

		} else if (item.getType2().equalsIgnoreCase("이뮨 투 함")) {
			// 이뮨 투 함
			return SelfImmuneToHarm.clone(getPool(SelfImmuneToHarm.class)).clone(item);
	
	
		} else if (item.getType2().equalsIgnoreCase("인벤 확인 주문서")) {
			// 인벤 확인 주문서
			return InventoryCheck.clone(getPool(InventoryCheck.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("아이템 제거 막대")) {
			// 아이템 제거 막대
			return Item_Remove_Wand.clone(getPool(Item_Remove_Wand.class)).clone(item);

		} else if (item.getType2().equalsIgnoreCase("몬스터 드랍 확인 막대")) {
			// 몬스터 드랍 확인 막대
			return MonsterDropCheckWand.clone(getPool(MonsterDropCheckWand.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("아이템 드랍 확인 막대")) {
			// 아이템 드랍 확인 막대
			return ItemDropCheckWand.clone(getPool(ItemDropCheckWand.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("장비 확인 막대")) {
			// 장비 확인 막대
			return ItemCheckWand.clone(getPool(ItemCheckWand.class)).clone(item);
			
//		} else if (item.getType2().equalsIgnoreCase("doll_option_scroll")) {
//			// 장신구 주문서
//			return RandomDollOptionGrantingOrderForm.clone(getPool(RandomDollOptionGrantingOrderForm.class)).clone(item);
//			
		} else if (item.getType2().equalsIgnoreCase("doll_option_scroll")) {
			// 장신구 주문서
			return RandomDollOption.clone(getPool(RandomDollOption.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("item_change_scroll")) {
			// 장신구 주문서
			return ItemChange.clone(getPool(ItemChange.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("마법인형")) {
			// 마법인형
			return MagicDoll.clone(getPool(MagicDoll.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("인첸트 복구 주문서")) {
			// 인첸트 복구 주문서
			return EnchantRecovery.clone(getPool(EnchantRecovery.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("드랍 복구 주문서")) {
			// 인첸트 복구 주문서
			return DeadRecovery.clone(getPool(DeadRecovery.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("속죄의 성서")) {
			// 인첸트 복구 주문서
			return expRecovery.clone(getPool(expRecovery.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("클래스 변경권")) {
			// 클래스 변경권
			return ClassChangeTicket.clone(getPool(ClassChangeTicket.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("인형 진화 주문서")) {
			// 인형 진화 주문서
			return DollEvolutionOrderForm.clone(getPool(DollEvolutionOrderForm.class)).clone(item);
			

		} else if (item.getType2().equalsIgnoreCase("캐릭터 저장 구슬")) {
			// 캐릭터 저장 구슬
			return CharacterSaveMarble.clone(getPool(CharacterSaveMarble.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("경험치 저장 구슬")) {
			// 경험치 저장 구슬
			return ExpSaveMarble.clone(getPool(ExpSaveMarble.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("경험치 구슬")) {
			// 경험치 구슬
			return ExpMarble.clone(getPool(ExpMarble.class)).clone(item);
			
		} else if (item.getType2().equalsIgnoreCase("인첸트 제거 주문서")) {
			// 인첸트 제거 주문서
			return EnchantRemove.clone(getPool(EnchantRemove.class)).clone(item);
			
		// 셀프 시전 마법
		} else if (item.getType2().equalsIgnoreCase("셀프 시전 마법")) {
			return SelfMagic.clone(getPool(SelfMagic.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("petitem")) {
			return petitem.clone(getPool(petitem.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("미티어막대기")) {
			return PenguinHuntingStick.clone(getPool(PenguinHuntingStick.class)).clone(item);
		// 혈맹 버프 물약
		} else if (item.getType2().equalsIgnoreCase("혈맹 버프 물약")) {
			return ClanBuffPotion.clone(getPool(ClanBuffPotion.class)).clone(item);
		// 다크엘프
		} else if (item.getType2().equalsIgnoreCase("dark_elfp")) {
			return darkelf_potion.clone(getPool(darkelf_potion.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("다크엘프 마법")) {
			return SelfSpell.clone(getPool(SelfSpell.class)).clone(item);
		} else if (item.getNameId().equalsIgnoreCase("아머브레이크")) {
			return ItemArmorBreak.clone(getPool(ItemArmorBreak.class)).clone(item);
		} else if (item.getNameId().equalsIgnoreCase("파이널번")) {
			return ItemFinal.clone(getPool(ItemFinal.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("card")) {
			return Card.clone(getPool(Card.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("accessory_scroll2")) {
			return ScrollOfAccessory2.clone(getPool(ScrollOfAccessory2.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("기억의구슬")) {
			return 희미한기억의구슬.clone(getPool(희미한기억의구슬.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("기억제거구슬")) {
			return 기억제거구슬.clone(getPool(기억제거구슬.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("autoitem")) {
			return AutoHuntItem.clone(getPool(AutoHuntItem.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("속성제거")) {
			return ScrollOfEnchantElementalWeaponRe.clone(getPool(ScrollOfEnchantElementalWeaponRe.class)).clone(item);
		} else if (item.getType2().equalsIgnoreCase("축복제거")) {
			return BlessRemoves.clone(getPool(BlessRemoves.class)).clone(item);
		} else {

			return ItemInstance.clone(getPool(ItemInstance.class)).clone(item);
		}
	}

	/**
	 * 아이템 객체 정보를 그대로 복사해서 객체 생성. object_id 새로 할당 해당 객체 리턴.
	 * 
	 * @param item
	 * @return
	 */
	static public ItemInstance newInstance(ItemInstance item) {
		if (item != null) {
			ItemInstance temp = newInstance(item.getItem());
			if (temp != null) {
				temp.setObjectId(ServerDatabase.nextItemObjId());
				temp.setDefinite(item.isDefinite());
				temp.setCount(item.getCount());
				temp.setBless(item.getBless());
				temp.setQuantity(item.getQuantity());
				temp.setEnLevel(item.getEnLevel());
				temp.setDurability(item.getDurability());
				temp.setDynamicMr(item.getDynamicMr());
				temp.setTime(item.getTime());
				temp.setNowTime(item.getNowTime());
				temp.setEquipped(item.isEquipped());
				temp.setTimeDrop(item.getTimeDrop());
				temp.setDynamicLight(item.getDynamicLight());
				temp.setDynamicAc(item.getDynamicAc());
				temp.setUsershopBuyPrice(item.getUsershopBuyPrice());
				temp.setUsershopSellPrice(item.getUsershopSellPrice());
				temp.setUsershopBuyCount(item.getUsershopBuyCount());
				temp.setUsershopSellCount(item.getUsershopSellCount());
				temp.setUsershopIdx(item.getUsershopIdx());
				temp.setSkill(item.getSkill());
				temp.setCharacter(item.getCharacter());
				temp.setDynamicStunHit(item.getDynamicStunHit());
				temp.setDynamicSp(item.getDynamicSp());
				temp.setDynamicReduction(item.getDynamicReduction());
				temp.setDynamicIgnoreReduction(item.getDynamicIgnoreReduction());
				temp.setDynamicSwordCritical(item.getDynamicSwordCritical());
				temp.setDynamicBowCritical(item.getDynamicBowCritical());
				temp.setDynamicMagicCritical(item.getDynamicMagicCritical());
				temp.setItemTimek(item.getItemTimek());
				// InnRoomKey
				temp.setInnRoomKey(item.getInnRoomKey());
				// RaceTicket
				temp.setRaceTicket(item.getRaceTicket());
				// DogCollar
				if (item instanceof DogCollar) {
					DogCollar dc = (DogCollar) item;
					DogCollar temp_dc = (DogCollar) temp;
					temp_dc.setPetObjectId(dc.getPetObjectId());
					temp_dc.setPetName(dc.getPetName());
					temp_dc.setPetClassId(dc.getPetClassId());
					temp_dc.setPetLevel(dc.getPetLevel());
					temp_dc.setPetHp(dc.getPetHp());
					temp_dc.setPetSpawn(dc.isPetSpawn());
					temp_dc.setPetDel(dc.isPetDel());
				}
				// Letter
				if (item instanceof Letter) {
					Letter l = (Letter) item;
					Letter temp_l = (Letter) temp;
					temp_l.setFrom(l.getFrom());
					temp_l.setTo(l.getTo());
					temp_l.setSubject(l.getSubject());
					temp_l.setMemo(l.getMemo());
					temp_l.setDate(l.getDate());
					temp_l.setLetterUid(l.getLetterUid());
				}

				return temp;
			}
		}
		return null;
	}

	static private void clearPool() {
		TimeLine.start("ItemDatabase 에서 Pool 초과로 메모리 정리 중..");

		// 풀 전체 제거.
		pool.clear();
		// gc 한번 호출.
		System.gc();

		TimeLine.end();
	}

	/**
	 * 사용다된 아이템 풀에 다시 넣는 함수.
	 * 
	 * @param item
	 */
	static public void setPool(ItemInstance item) {
		item.close();
		
		if (Lineage.pool_itemInstance) {			
			synchronized (pool) {
				if (Util.isPoolAppend(pool) && pool.contains(item) == false) {
					pool.add(item);
				} else {
					item = null;
					clearPool();
				}
			}
			//		lineage.share.System.println("append : "+pool.size());
		}
	}

	/**
	 * 아이템 재사용을위해 풀에서 필요한 객체 찾아서 리턴.
	 * 
	 * @param c
	 * @return
	 */
	static public ItemInstance getPool(Class<?> c) {
		if (Lineage.pool_itemInstance) {
			synchronized (pool) {
				ItemInstance item = findPool(c);
				if (item != null)
					pool.remove(item);

				//			lineage.share.System.println("remove : "+pool.size());
				return item;
			}
		} else {
			return null;
		}
	}

	static private ItemInstance findPool(Class<?> c) {
		for (ItemInstance item : pool) {
			if (item.getClass().equals(c))
				return item;
		}
		return null;
	}

	static private int getMaterial(final String meterial) {
		if (meterial.equalsIgnoreCase("액체"))
			return 1;
		else if (meterial.equalsIgnoreCase("밀랍"))
			return 2;
		else if (meterial.equalsIgnoreCase("식물성"))
			return 3;
		else if (meterial.equalsIgnoreCase("동물성"))
			return 4;
		else if (meterial.equalsIgnoreCase("종이"))
			return 5;
		else if (meterial.equalsIgnoreCase("천"))
			return 6;
		else if (meterial.equalsIgnoreCase("가죽"))
			return 7;
		else if (meterial.equalsIgnoreCase("나무"))
			return 8;
		else if (meterial.equalsIgnoreCase("뼈"))
			return 9;
		else if (meterial.equalsIgnoreCase("용 비늘"))
			return 10;
		else if (meterial.equalsIgnoreCase("철"))
			return 11;
		else if (meterial.equalsIgnoreCase("금속"))
			return 12;
		else if (meterial.equalsIgnoreCase("구리"))
			return 13;
		else if (meterial.equalsIgnoreCase("은"))
			return 14;
		else if (meterial.equalsIgnoreCase("금"))
			return 15;
		else if (meterial.equalsIgnoreCase("백금"))
			return 16;
		else if (meterial.equalsIgnoreCase("미스릴"))
			return 17;
		else if (meterial.equalsIgnoreCase("블랙미스릴"))
			return 18;
		else if (meterial.equalsIgnoreCase("유리"))
			return 19;
		else if (meterial.equalsIgnoreCase("보석"))
			return 20;
		else if (meterial.equalsIgnoreCase("광석"))
			return 21;
		else if (meterial.equalsIgnoreCase("오리하루콘"))
			return 22;
		return 0;
	}

	static private int getWeaponGfx(final String type2) {
		if (type2.equalsIgnoreCase("sword"))
			return Lineage.WEAPON_SWORD;
		// 양손검도 클라 모션은 한손검과 동일하게 (문제 시 WEAPON_TOHANDSWORD 로 복구)
		else if (type2.equalsIgnoreCase("tohandsword"))
			return Lineage.WEAPON_SWORD;
		else if (type2.equalsIgnoreCase("axe"))
			return Lineage.WEAPON_AXE;
		else if (type2.equalsIgnoreCase("bow"))
			return Lineage.WEAPON_BOW;
		else if (type2.equalsIgnoreCase("spear"))
			return Lineage.WEAPON_SPEAR;
		else if (type2.equalsIgnoreCase("wand"))
			return Lineage.WEAPON_WAND;
		else if (type2.equalsIgnoreCase("staff"))
			return Lineage.WEAPON_WAND;
		else if (type2.equalsIgnoreCase("dagger"))
			return Lineage.WEAPON_DAGGER;
		else if (type2.equalsIgnoreCase("blunt"))
			return Lineage.WEAPON_BLUNT;
		else if (type2.equalsIgnoreCase("edoryu"))
			return Lineage.WEAPON_EDORYU;
		else if (type2.equalsIgnoreCase("claw"))
			return Lineage.WEAPON_CLAW;
		else if (type2.equalsIgnoreCase("throwingknife"))
			return Lineage.WEAPON_THROWINGKNIFE;
		else if (type2.equalsIgnoreCase("arrow"))
			return Lineage.WEAPON_ARROW;
		else if (type2.equalsIgnoreCase("gauntlet"))
			return Lineage.WEAPON_GAUNTLET;
		else if (type2.equalsIgnoreCase("chainsword"))
			return Lineage.WEAPON_CHAINSWORD;
		else if (type2.equalsIgnoreCase("keyrink"))
			return Lineage.WEAPON_KEYRINK;
		else if (type2.equalsIgnoreCase("tohandblunt"))
			return Lineage.WEAPON_BLUNT;
		else if (type2.equalsIgnoreCase("tohandstaff"))
			return Lineage.WEAPON_WAND;
		else if (type2.equalsIgnoreCase("tohandspear"))
			return Lineage.WEAPON_SPEAR;
		else if (type2.equalsIgnoreCase("fishing_rod"))
			return Lineage.FISHING_ROD;

		return Lineage.WEAPON_NONE;
	}

	static private int getSlot(final String type2) {
		if (type2.equalsIgnoreCase("helm"))
			return Lineage.SLOT_HELM;
		else if (type2.equalsIgnoreCase("earring"))
			return Lineage.SLOT_EARRING;
		else if (type2.equalsIgnoreCase("necklace"))
			return Lineage.SLOT_NECKLACE;
		else if (type2.equalsIgnoreCase("t"))
			return Lineage.SLOT_SHIRT;
		else if (type2.equalsIgnoreCase("armor"))
			return Lineage.SLOT_ARMOR;
		else if (type2.equalsIgnoreCase("cloak"))
			return Lineage.SLOT_CLOAK;
		else if (type2.equalsIgnoreCase("belt"))
			return Lineage.SLOT_BELT;
		else if (type2.equalsIgnoreCase("glove"))
			return Lineage.SLOT_GLOVE;
		else if (type2.equalsIgnoreCase("shield"))
			return Lineage.SLOT_SHIELD;
		else if (type2.equalsIgnoreCase("boot"))
			return Lineage.SLOT_BOOTS;
		else if (type2.equalsIgnoreCase("ring"))
			return Lineage.SLOT_RING_LEFT;
		else if (type2.equalsIgnoreCase("sword"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("axe"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("bow"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("wand"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("tohandsword"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("spear"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("dagger"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("blunt"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("claw"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("edoryu"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("gauntlet"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("speer"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("staff"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("chainsword"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("keyrink"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("tohandblunt"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("tohandstaff"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("tohandspear"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("guarder"))
			return Lineage.SLOT_GUARDER;
		else if (type2.equalsIgnoreCase("fishing_rod"))
			return Lineage.SLOT_WEAPON;
		else if (type2.equalsIgnoreCase("arrow"))
			return Lineage.SLOT_ARROW;

		return Lineage.SLOT_NONE;
	}

	static private int getEquippedSlot(final String type1, final String type2) {
		if (type2.equalsIgnoreCase("armor"))
			return 2;
		else if (type2.equalsIgnoreCase("cloak"))
			return 10;
		else if (type2.equalsIgnoreCase("t"))
			return 18;
		else if (type2.equalsIgnoreCase("glove"))
			return 20;
		else if (type2.equalsIgnoreCase("boot"))
			return 21;
		else if (type2.equalsIgnoreCase("helm"))
			return 22;
		else if (type2.equalsIgnoreCase("ring"))
			return 23;
		else if (type2.equalsIgnoreCase("necklace"))
			return 24;
		else if (type2.equalsIgnoreCase("shield") || type2.equalsIgnoreCase("guarder"))
			return 25;
		else if (type2.equalsIgnoreCase("belt"))
			return 37;
		else if (type2.equalsIgnoreCase("earring"))
			return 40;
		else if (type1.equalsIgnoreCase("weapon") && !type2.equalsIgnoreCase("arrow"))
			return 1;
		
		return 40;
	}

	static public int getSize() {
		return list.size();
	}

	static public int getPoolSize() {
		return pool.size();
	}

	static public List<Item> getList() {
		return new ArrayList<Item>(list);
	}
}
