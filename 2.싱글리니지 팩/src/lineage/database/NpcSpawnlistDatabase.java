package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Clan;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.world.World;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ClanController;
import lineage.world.controller.RankController;
import lineage.world.object.object;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.RankBoardInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.npc.OmanShop;
import lineage.world.object.npc.BuffNpc;
import lineage.world.object.npc.ClanMaker;
import lineage.world.object.npc.Clan_lord;
import lineage.world.object.npc.Doett;
import lineage.world.object.npc.Ellyonne;
import lineage.world.object.npc.Giran_dungeon_Telepoter;
import lineage.world.object.npc.GoddessAgata;
import lineage.world.object.npc.GoddessYuris;
import lineage.world.object.npc.GoddessAriel;
import lineage.world.object.npc.Horun;
import lineage.world.object.npc.Market_telepoter;
import lineage.world.object.npc.Hurin;
import lineage.world.object.npc.MagicdollCompose;
import lineage.world.object.npc.Maid;
import lineage.world.object.npc.Morien;
import lineage.world.object.npc.Rank_bronze;
import lineage.world.object.npc.Sedia;
import lineage.world.object.npc.Siris;
import lineage.world.object.npc.TalkMovingNpc;
import lineage.world.object.npc.TalkNpc;
import lineage.world.object.npc.TebeTeleporter;
import lineage.world.object.npc.PenguinTeleporter;
import lineage.world.object.npc.PolyCardCompose;
import lineage.world.object.npc.Theodor;
import lineage.world.object.npc.Touma;
import lineage.world.object.npc.ShopManagement;
import lineage.world.object.npc.ManaStoneSynthesis;
import lineage.world.object.npc.TreasureHuntTeleporter;
import lineage.world.object.npc.WorldBossTeleporter;
import lineage.world.object.npc.createNpcH;
import lineage.world.object.npc.exarmorcreate;
import lineage.world.object.npc.jewelCreate;
import lineage.world.object.npc.testnpc;
import lineage.world.object.npc.자동판매;
import lineage.world.object.npc.OmanSealCreate;
import lineage.world.object.npc.BossTimer;
import lineage.world.object.npc.EvilTeleporter;
import lineage.world.object.npc.FrozenTeleporter;
import lineage.world.object.npc.AutoPotion;
import lineage.world.object.npc.ItemSwap;
import lineage.world.object.npc.KuberaQuest;
import lineage.world.object.npc.KuberaQuest2;
import lineage.world.object.npc.HellTeleporter;
import lineage.world.object.npc.Alfons;
import lineage.world.object.npc.AttendanceCheck;
import lineage.world.object.npc.Promot_npc;
import lineage.world.object.npc.PvP_Rank_bronze;
import lineage.world.object.npc.buff.ArmorEnchanter;
import lineage.world.object.npc.buff.Buff_Enhancement;
import lineage.world.object.npc.buff.Curer;
import lineage.world.object.npc.buff.Hadesty;
import lineage.world.object.npc.buff.Haste;
import lineage.world.object.npc.buff.PolymorphMagician;
import lineage.world.object.npc.buff.WeaponEnchanter;
import lineage.world.object.npc.craft.Alchemist;
import lineage.world.object.npc.craft.Anton;
import lineage.world.object.npc.craft.Arachne;
import lineage.world.object.npc.craft.Bamut;
import lineage.world.object.npc.craft.Bankoo;
import lineage.world.object.npc.craft.Detecter;
import lineage.world.object.npc.craft.Ent;
import lineage.world.object.npc.craft.Est;
import lineage.world.object.npc.craft.Eveurol;
import lineage.world.object.npc.craft.Estevan;
import lineage.world.object.npc.craft.Fairy;
import lineage.world.object.npc.craft.FairyQueen;
import lineage.world.object.npc.craft.Farin;
import lineage.world.object.npc.craft.Fillis;
import lineage.world.object.npc.craft.FishLady;
import lineage.world.object.npc.craft.Fivelviin;
import lineage.world.object.npc.craft.Hector;
import lineage.world.object.npc.craft.Herbert;
import lineage.world.object.npc.craft.Ivelviin;
import lineage.world.object.npc.craft.Joel;
import lineage.world.object.npc.craft.Julie;
import lineage.world.object.npc.craft.Kahlua;
import lineage.world.object.npc.craft.Karif;
import lineage.world.object.npc.craft.Ladar;
import lineage.world.object.npc.craft.Lien;
import lineage.world.object.npc.craft.Luudiel;
import lineage.world.object.npc.craft.Moria;
import lineage.world.object.npc.craft.Narhen;
import lineage.world.object.npc.craft.Nerupa;
import lineage.world.object.npc.craft.Paruit;
import lineage.world.object.npc.craft.Pan;
import lineage.world.object.npc.craft.Pierce;
import lineage.world.object.npc.craft.Pin;
import lineage.world.object.npc.craft.Rafons;
import lineage.world.object.npc.craft.Reona;
import lineage.world.object.npc.craft.Rodney;
import lineage.world.object.npc.craft.Ryumiel;
import lineage.world.object.npc.craft.Sarsha;
import lineage.world.object.npc.craft.Schuerme;
import lineage.world.object.npc.craft.Vincent;
import lineage.world.object.npc.dwarf.Axellon;
import lineage.world.object.npc.dwarf.Bahof;
import lineage.world.object.npc.dwarf.Borgin;
import lineage.world.object.npc.dwarf.Dorin;
import lineage.world.object.npc.dwarf.El;
import lineage.world.object.npc.dwarf.Gotham;
import lineage.world.object.npc.dwarf.Haidrim;
import lineage.world.object.npc.dwarf.Hakim;
import lineage.world.object.npc.dwarf.Hirim;
import lineage.world.object.npc.dwarf.Jianku;
import lineage.world.object.npc.dwarf.Juke;
import lineage.world.object.npc.dwarf.Kamu;
import lineage.world.object.npc.dwarf.Karim;
import lineage.world.object.npc.dwarf.Karudim;
import lineage.world.object.npc.dwarf.Kasham;
import lineage.world.object.npc.dwarf.Kriom;
import lineage.world.object.npc.dwarf.Kuhatin;
import lineage.world.object.npc.dwarf.Kuron;
import lineage.world.object.npc.dwarf.Kusian;
import lineage.world.object.npc.dwarf.Luku;
import lineage.world.object.npc.dwarf.Nodim;
import lineage.world.object.npc.dwarf.Rayearth;
import lineage.world.object.npc.dwarf.Sauram;
import lineage.world.object.npc.dwarf.Tarkin;
import lineage.world.object.npc.dwarf.Thram;
import lineage.world.object.npc.dwarf.Tigus;
import lineage.world.object.npc.dwarf.Timpukin;
import lineage.world.object.npc.dwarf.Tofen;
import lineage.world.object.npc.dwarf.Tulak;
import lineage.world.object.npc.event.BaseResetMary;
import lineage.world.object.npc.event.BaseResetRoro;
import lineage.world.object.npc.event.FishingBoy;
import lineage.world.object.npc.event.JewelCraftsman;
import lineage.world.object.npc.event.Keplisha;
import lineage.world.object.npc.event.Yuno;
import lineage.world.object.npc.MercenaryGroup;
import lineage.world.object.npc.guard.Dwarfcastle_Guard;
import lineage.world.object.npc.guard.Girancastle_Guard;
import lineage.world.object.npc.guard.Heine_Guard;
import lineage.world.object.npc.guard.Kentcastle_Guard;
import lineage.world.object.npc.guard.PatrolGuard;
import lineage.world.object.npc.guard.SentryGuard;
import lineage.world.object.npc.guard.Wyndowoodcastle_Guard;
import lineage.world.object.npc.inn.Elly;
import lineage.world.object.npc.inn.Enke;
import lineage.world.object.npc.inn.Lolia;
import lineage.world.object.npc.inn.Miranda;
import lineage.world.object.npc.inn.Molly;
import lineage.world.object.npc.inn.Sabin;
import lineage.world.object.npc.inn.Selena;
import lineage.world.object.npc.inn.Velisa;
import lineage.world.object.npc.kingdom.Biust;
import lineage.world.object.npc.kingdom.Colbert;
import lineage.world.object.npc.kingdom.Freckson;
import lineage.world.object.npc.kingdom.Halt;
import lineage.world.object.npc.kingdom.Hunt;
import lineage.world.object.npc.kingdom.Ishmael;
import lineage.world.object.npc.kingdom.Orville;
import lineage.world.object.npc.kingdom.Othmond;
import lineage.world.object.npc.kingdom.Potempin;
import lineage.world.object.npc.kingdom.SeghemAtuba;
import lineage.world.object.npc.kingdom.Vaiger;
import lineage.world.object.npc.pet.Almon;
import lineage.world.object.npc.pet.Alri;
import lineage.world.object.npc.pet.Cove;
import lineage.world.object.npc.pet.Dick;
import lineage.world.object.npc.pet.Hans;
import lineage.world.object.npc.pet.Johnson;
import lineage.world.object.npc.pet.Kevin;
import lineage.world.object.npc.pet.Marbin;
import lineage.world.object.npc.pet.Mild;
import lineage.world.object.npc.pet.Pau;
import lineage.world.object.npc.pet.Rostin;
import lineage.world.object.npc.quest.Aanon;
import lineage.world.object.npc.quest.AdminNovice;
import lineage.world.object.npc.quest.Aria;
import lineage.world.object.npc.quest.Chico;
import lineage.world.object.npc.quest.Dilong;
import lineage.world.object.npc.quest.Dunham;
import lineage.world.object.npc.quest.FairyPrincess;
import lineage.world.object.npc.quest.Galleon;
import lineage.world.object.npc.quest.Gatekeeper;
import lineage.world.object.npc.quest.GatekeeperAnt;
import lineage.world.object.npc.quest.Gerard;
import lineage.world.object.npc.quest.Gereng;
import lineage.world.object.npc.quest.Gilbert;
import lineage.world.object.npc.quest.Gion;
import lineage.world.object.npc.quest.Gunter;
import lineage.world.object.npc.quest.Heit;
import lineage.world.object.npc.quest.HelperNovice;
import lineage.world.object.npc.quest.Hob;
import lineage.world.object.npc.quest.Honin;
import lineage.world.object.npc.quest.Jaruman;
import lineage.world.object.npc.quest.Jem;
import lineage.world.object.npc.quest.Jerik;
import lineage.world.object.npc.quest.Jim;
import lineage.world.object.npc.quest.Kan;
import lineage.world.object.npc.quest.Lekman;
import lineage.world.object.npc.quest.Liri;
import lineage.world.object.npc.quest.Lyra;
import lineage.world.object.npc.quest.Mack;
import lineage.world.object.npc.quest.Mark;
import lineage.world.object.npc.quest.Marshall;
import lineage.world.object.npc.quest.Oshillia;
import lineage.world.object.npc.quest.Oth;
import lineage.world.object.npc.quest.Porikan;
import lineage.world.object.npc.quest.Richard;
import lineage.world.object.npc.quest.Ricky;
import lineage.world.object.npc.quest.Ronde;
import lineage.world.object.npc.quest.Ruba;
import lineage.world.object.npc.quest.SearchAnt;
import lineage.world.object.npc.quest.Serian;
import lineage.world.object.npc.quest.Syria;
import lineage.world.object.npc.quest.Talass;
import lineage.world.object.npc.quest.Tio;
import lineage.world.object.npc.quest.Tuck;
import lineage.world.object.npc.quest.Zero;
import lineage.world.object.npc.shop.*;
import lineage.world.object.npc.teleporter.Amisoo;
import lineage.world.object.npc.teleporter.Barnia;
import lineage.world.object.npc.teleporter.Brad;
import lineage.world.object.npc.teleporter.Coco;
import lineage.world.object.npc.teleporter.ColiseumManager;
import lineage.world.object.npc.teleporter.Cspace;
import lineage.world.object.npc.teleporter.Deanos;
import lineage.world.object.npc.teleporter.Drist;
import lineage.world.object.npc.teleporter.Duvall;
import lineage.world.object.npc.teleporter.Edlin;
import lineage.world.object.npc.teleporter.Elleris;
import lineage.world.object.npc.teleporter.Entgate;
import lineage.world.object.npc.teleporter.Enya;
import lineage.world.object.npc.teleporter.Escapefi;
import lineage.world.object.npc.teleporter.Esmereld;
import lineage.world.object.npc.teleporter.FieldOfHonor;
import lineage.world.object.npc.teleporter.FirstTeleporter;
import lineage.world.object.npc.teleporter.GateKeeper;
import lineage.world.object.npc.teleporter.Illdrath;
import lineage.world.object.npc.teleporter.Ishtar;
import lineage.world.object.npc.teleporter.Karen;
import lineage.world.object.npc.teleporter.Kirius;
import lineage.world.object.npc.teleporter.Kiyari;
import lineage.world.object.npc.teleporter.Kun;
import lineage.world.object.npc.teleporter.LegNpc;
import lineage.world.object.npc.teleporter.Leslie;
import lineage.world.object.npc.teleporter.Lucas;
import lineage.world.object.npc.teleporter.Luck;
import lineage.world.object.npc.teleporter.Mammon;
import lineage.world.object.npc.teleporter.MarketGuard;
import lineage.world.object.npc.teleporter.MarketTeleporter;
import lineage.world.object.npc.teleporter.Matt;
import lineage.world.object.npc.teleporter.Ober;
import lineage.world.object.npc.teleporter.OrcfbuWoo;
import lineage.world.object.npc.teleporter.Ribian;
import lineage.world.object.npc.teleporter.Riol;
import lineage.world.object.npc.teleporter.Sirius;
import lineage.world.object.npc.teleporter.Sky;
import lineage.world.object.npc.teleporter.Stanley;
import lineage.world.object.npc.teleporter.Stevie;
import lineage.world.object.npc.teleporter.Telefire;
import lineage.world.object.npc.teleporter.Trey;
import lineage.world.object.npc.teleporter.Wilma;
import lineage.world.object.npc.teleporter.Zeno;

public final class NpcSpawnlistDatabase {

	static private List<object> pool;
	static private List<object> list;
	// 상점 npc 목록. (로봇이 아이템구입시 상인을 찾을때 사용됨.)
	static private List<ShopInstance> list_shop;
	// 장비 스왑
	static public object itemSwap;
	// 자동 물약
	static public object autoPotion;
	// 매입 상인
	static public ShopInstance sellShop;
	//by 야도란 사냥터이동 & 보스이동ses950317@nate.com

	static public object yadolantelboss;
	static public object rankcheck;
	static public object weaponchange;
	static public object armornchange;
	static public object bosstime;
	static public object playcheck;
	static public object quest;
	static public object quest2;
	static public object marketNpc;
	static public object 보여주기상점;
	static public object 뽑기엔피시;
	
	static public object 무기상인;
	static public object 방어구상인;
	static public object 마법서상인;
	static public object 악세상인;
	static public object 룬상인;
	static public object 주문서상인;
	
	static public object 동물가죽;
	static public object 고급피혁;
	static public object 뼈조각;
	static public object 철괴;
	//f1상점
	static public object 코인상점;
	static public object AutoSellItem;
	static public object 무기방어구상인;
	
	static public void init(Connection con) {
		TimeLine.start("NpcSpawnlistDatabase..");

		pool = new ArrayList<object>();
		list = new ArrayList<object>();
		list_shop = new ArrayList<ShopInstance>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM npc_spawnlist");
			rs = st.executeQuery();
			while (rs.next()) {
				toSpawnNpc(rs.getString("name"), rs.getString("npcName"), rs.getString("title"), rs.getInt("locX"), rs.getInt("locY"), rs.getInt("locMap"), rs.getInt("heading"), rs.getInt("respawn"));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", NpcSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		playcheck= new AttendanceCheck();	
		quest= new KuberaQuest();
		quest2= new KuberaQuest2();
		itemSwap = new ItemSwap();
		autoPotion = new AutoPotion();
		AutoSellItem = new 자동판매();
		//	야도란 ses950317@nate.com
		marketNpc = new ShopManagement();
		bosstime =  new BossTimer();
		rankcheck =  new RankBoardInstance();
		뽑기엔피시 = new testnpc();
		
		autoPotion.setObjectId(ServerDatabase.nextEtcObjId());
		뽑기엔피시.setObjectId(ServerDatabase.nextEtcObjId());
		playcheck.setObjectId(ServerDatabase.nextEtcObjId());
		quest.setObjectId(ServerDatabase.nextEtcObjId());
		quest2.setObjectId(ServerDatabase.nextEtcObjId());
		AutoSellItem.setObjectId(ServerDatabase.nextEtcObjId());
		itemSwap.setObjectId(ServerDatabase.nextEtcObjId());
		rankcheck.setObjectId(ServerDatabase.nextEtcObjId());
		bosstime.setObjectId(ServerDatabase.nextEtcObjId());

		TimeLine.end();
	}

	/**
	 * 중복코드 방지용.
	 * 
	 * @param npc
	 * @param title
	 * @param x
	 * @param y
	 * @param map
	 * @param heading
	 * @param respawn
	 */
	static public void toSpawnNpc(String key, String npc, String title, int x, int y, int map, int heading, int respawn) {
		Npc n = NpcDatabase.find(npc);
		if (n != null) {
			object o = newObject(n, newObject(n));
			o.setDatabaseKey(key);
			
			// 신규혈맹
			if (o instanceof Clan_lord) {
				Clan c = ClanController.find(Lineage.new_clan_name);
				
				if (c != null) {
					o.setClanId(c.getUid());
					o.setClanName(Lineage.new_clan_name);
				}
			}
			
			// 로봇 전용 상점 목록
			if (o instanceof BuyShop)
				list_shop.add((ShopInstance) o);
			
			o.setTitle(title);
			o.setHomeX(x);
			o.setHomeY(y);
			o.setHomeMap(map);
			o.setHomeHeading(heading);
			o.setHeading(heading);
			o.setReSpawnTime(respawn);
			o.toTeleport(o.getHomeX(), o.getHomeY(), o.getHomeMap(), false);
			n.getSpawnList().add(new int[] { x, y, map });
			
			if (n.isAi())
				AiThread.append(o);
			
			if (o.getName().equalsIgnoreCase("보여주기상점"))
				보여주기상점 = o;
			//f1상점
			if (o.getName().equalsIgnoreCase("코인 상점"))
				코인상점 = o;
			
			if (o.getName().equalsIgnoreCase("무기 상인"))
				무기상인 = o;
			if (o.getName().equalsIgnoreCase("방어구 상인"))
				방어구상인 = o;
			if (o.getName().equalsIgnoreCase("마법서 상인"))
				마법서상인 = o;
			if (o.getName().equalsIgnoreCase("악세 상인"))
				악세상인 = o;
			if (o.getName().equalsIgnoreCase("룬 상인"))
				룬상인 = o;
			if (o.getName().equalsIgnoreCase("마법 주문서 상인"))
				주문서상인 = o;
			
			if (o.getName().equalsIgnoreCase("가죽 세공사"))
				동물가죽 = o;
			if (o.getName().equalsIgnoreCase("피혁 세공사"))
				고급피혁 = o;
			if (o.getName().equalsIgnoreCase("뼈 세공사"))
				뼈조각 = o;
			if (o.getName().equalsIgnoreCase("철 세공사"))
				철괴 = o;
			
			if (o.getName().equalsIgnoreCase("테스트 엔피시"))
				뽑기엔피시 = o;
			if (o.getName().equalsIgnoreCase("무기 방어구 상인"))
				무기방어구상인 = o;

			if (o instanceof Rank_bronze)
				RankController.rankBronze = o;
			else if (o instanceof PvP_Rank_bronze)
				RankController.pvpRankBronze = o;
			else if (o instanceof SellShop)
				sellShop = (ShopInstance) o;
//			//싸울의뢰
//			if (o instanceof Trank_bronze1)
//				PcInstance.trankBronze1 = o;
//			if (o instanceof Trank_bronze2)
//				PcInstance.trankBronze2 = o;
//			if (o instanceof Trank_bronze3)
//				PcInstance.trankBronze3 = o;
			
			appendList(o);
		}
	}

	/** npc_spawnlist에서 name으로 한 건 조회 후 해당 NPC만 스폰 (GM 툴 npc_respawn용) */
	static public boolean spawnOne(String spawnName) {
		if (spawnName == null || spawnName.isEmpty()) return false;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT name, npcName, title, locX, locY, locMap, heading, respawn FROM npc_spawnlist WHERE name=?");
			st.setString(1, spawnName);
			rs = st.executeQuery();
			if (rs.next()) {
				String title = rs.getString("title");
				toSpawnNpc(rs.getString("name"), rs.getString("npcName"), title != null ? title : "", rs.getInt("locX"), rs.getInt("locY"), rs.getInt("locMap"), rs.getInt("heading"), rs.getInt("respawn"));
				return true;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : spawnOne(%s)\r\n", NpcSpawnlistDatabase.class.toString(), spawnName);
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return false;
	}
	
	static public void appendList(object o) {
		synchronized (list) {
			if (!list.contains(o)) {
				list.add(o);
			}
		}	
	}
	
	static public void reload() {
		synchronized (list) {
			for (object o : list) {
				o.setAiStatus(Lineage.AI_STATUS_DELETE);
				o.clearList(true);
//				testnpc.exitgame((PcInstance) o);
				World.remove(o);
				CharacterController.toWorldOut(o);
		
			}
			
			list_shop.clear();
			list.clear();
			
	
			
			PreparedStatement st = null;
			ResultSet rs = null;
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("SELECT * FROM npc_spawnlist");
				rs = st.executeQuery();
				while (rs.next()) {
					toSpawnNpc(rs.getString("name"), rs.getString("npcName"), rs.getString("title"), rs.getInt("locX"), rs.getInt("locY"), rs.getInt("locMap"), rs.getInt("heading"), rs.getInt("respawn"));
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : reload()\r\n", NpcSpawnlistDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}
	}

	static public object newObject(Npc n, object o) {
		if (n == null || o == null)
			return null;

		o.setObjectId(ServerDatabase.nextEtcObjId());
		o.setName(n.getNameId());
		o.setGfx(n.getGfx());
		o.setGfxMode(n.getGfxMode());
		o.setMaxHp(n.getHp() == 0 ? 1 : n.getHp());
		o.setNowHp(n.getHp() == 0 ? 1 : n.getHp());
		o.setLawful(n.getLawful());
		o.setClassGfx(o.getGfx());
		o.setClassGfxMode(o.getGfxMode());
		o.setLight(n.getLight());

		return o;
	}

	static public object newObject(Npc n) {
		// 버그 방지.
		if (n == null)
			return null;

		Object o = PluginController.init(NpcSpawnlistDatabase.class, "newObject", n);
		if (o instanceof object)
			return (object) o;

		switch (n.getNameIdNumber()) {
		case 240: // 마을 보초 경비병
		case 58240: // 난쟁이 경비병
		case 360240: // 켄트 경비병
		case 951240: // 윈다우드 경비병
		case 1513240: // 하이네 경비병
		case 1242240: // 기란 경비병
			return new SentryGuard(n);
		case 24400: // 하이네 경비병
			return new Heine_Guard(n);
		case 24401: // 켄트성 경비병
			return new Kentcastle_Guard(n);
		case 24402: // 윈다우드성 경비병
			return new Wyndowoodcastle_Guard(n);
		case 24403: // 기란 경비병
			return new Girancastle_Guard(n);
		case 24404: // 지져성 경비병
			return new Dwarfcastle_Guard(n);
		case 269: // 판도라
			return new Pandora(n);
		case 270: // 군터
			return new Gunter(n);
		case 304: // 발심
			return new Balshim(n);
		case 309: // 도린
			return new Dorin(n);
		case 320: // 선착장 관리인
			return new HarborMaster(n);
		case 332: // 케티
			return new Catty(n);
		case 333: // 룻
			return new Luth(n);
		case 334: // 카림
			return new Karim(n);
		case 365: // 아만
			return new Aaman(n);
		case 373: // 고라
			return new Gora(n);
		case 374: // 게렝
			return new Gereng(n);
		case 406: // 오림
			return new Orim(n);
		case 432: // 이스마엘
			return new Ishmael(n);
		case 445: // 훈트
			return new Hunt(n);
		case 446: // 애쉬톤
			return new TalkMovingNpc(n, "ashton1");
		case 447: // 던컨
			return new TalkMovingNpc(n, "dunkan1");
		case 448: // 무어
			return new TalkMovingNpc(n, "moor1");
		case 449: // 카나
			return new TalkMovingNpc(n, "cana1");
		case 450: // 로리아
			return new Lolia(n);
		case 451: // 파르보
			return new TalkMovingNpc(n, "farbo1");
		case 452: // 제인트
			return new Jeint(n);
		case 453: // 지타
			return new TalkMovingNpc(n, "rjyta1");
		case 454: // 렝고
			return new TalkMovingNpc(n, "lengo1");
		case 455: // 휜
			return new TalkMovingNpc(n, "fiin1");
		case 456: // 쥬디스
			return new TalkMovingNpc(n, "judice1");
		case 458: // 안딘
			return new Andyn(n);
		case 459: // 이소리야
			return new Ysorya(n);
		case 468: // 바호프
			return new Bahof(n);
		case 474: // 문지기
			return new Gatekeeper(n);
		case 478: // 토마
			return new Touma(n);
		case 1054: // 파린
			return new Farin();
		case 479: // 우드포드
			return new TalkMovingNpc(n, "woodford1");
		case 480: // 오포
			return new TalkMovingNpc(n, "ofo1");
		case 481: // 로한
			return new TalkMovingNpc(n, "rohan1");
		case 483: // 토미
			return new TalkMovingNpc(n, "tommy1");
		case 484: // 라이라
			return new Lyra(n);
		case 485: // 사니타
			return new TalkMovingNpc(n, "sanita1");
		case 486: // 엘른
			return new TalkMovingNpc(n, "ellne1");
		case 487: // 한나
			return new TalkMovingNpc(n, "hanna1");
		case 488: // 세겜 아투바
			return new SeghemAtuba(n);
		case 568: // 스람
			return new Thram(n);
		case 614: // 존슨
			return new Johnson(n);
		case 615: // 딕
			return new Dick(n);
		case 737: // 밥
			return new TalkMovingNpc(n, "bob1");
		case 738: // 루카
			return new TalkMovingNpc(n, "ruka1");
		case 749: // 네루파
			return new Nerupa(n);
		case 750: // 엘
			return new El(n);
		case 751: // 호런
			return new Horun();
		case 752: // 아라크네
			return new Arachne(n);
		case 753: // 판
			return new Pan(n);
		case 754: // 페어리
			return new Fairy(n);
		case 755: // 엔트
			return new Ent(n);
		case 805: // 페어리 퀸
			return new FairyQueen(n);
		case 811: // 나르엔
			return new Narhen(n);
		case 812: // 도에트
			return new Doett(n);
		case 813: // 후린달렌
			return new Hurin(n);
		case 820: // 모리엔
			return new Morien(n);
		case 821: // 테오도르
			return new Theodor(n);
		case 826: // 라반
			return new TalkMovingNpc(n, "laban1");
		case 829: // 루카스
			return new Lucas(n);
		case 830: // 스티브
			return new Stevie(n);
		case 831: // 스텐리
			return new Stanley(n);
		case 833: // 파울
		case 834: // 다니엘
			return new LegNpc(n);
		case 838: // 필립
			return new Philip(n);
		case 840: // 로드니
			return new Rodney(n);
		case 846: // 헥터
			return new Hector(n);
		case 848: // 빈센트
			return new Vincent(n);
		case 849: // 에버트
			return new Evert(n);
		case 850: // 콜버트
			return new Colbert(n);
		case 851: // 알폰스
			return new Alfons();
		case 854: // 퍼킨
			return new Perkin(n);
		case 855: // 제이슨
			return new Jason(n);
		case 857: // 란달
			return new Randal(n);
		case 858: // 세실
			return new Cecil(n);
		case 859: // 디오
			return new Dio(n);
		case 860: // 안톤
			return new Anton(n);
		case 861: // 데렉
			return new Derek(n);
		case 862: // 모나
			return new TalkMovingNpc(n, "mona1");
		case 841:  //[기란] 네일
			return new TalkMovingNpc(n, "neil1");
		case 864:  //[기란] 엘리자
			return new TalkMovingNpc(n, "eliza1");
		case 852:  //[기란] 빅터
			return new TalkMovingNpc(n, "victor1");
		case 837:  //[기란] 시드니
			return new TalkMovingNpc(n, "sidney1");
		case 845:  //[기란] 머윈
			return new TalkMovingNpc(n, "merwyn1");
		case 868:  //[기란] 제시
			return new TalkMovingNpc(n, "jessy1");
		case 879:  //[기란] 샐리
			return new TalkMovingNpc(n, "sally1");
		case 836:  //[기란] 브루노
			return new TalkMovingNpc(n, "bruno1");
		case 856:  //[기란] 라반
			return new TalkMovingNpc(n, "laban1");
		case 887:  //[기란] 이올라
			return new TalkMovingNpc(n, "iola1");
		case 877:  //[기란] 아만다
			return new TalkMovingNpc(n, "amanda1");
		case 871:  //[기란] 벨마
			return new TalkMovingNpc(n, "velma1");
		case 847:  //[기란] 테리
			return new TalkMovingNpc(n, "terry1");
		case 839:  //[기란] 에반
			return new TalkMovingNpc(n, "eban1");
		case 835:  //[기란] 앤드류
			return new TalkMovingNpc(n, "andrew1");
		case 832:  //[기란] 잭
			return new TalkMovingNpc(n, "jack1");
		case 863:  //[기란] 헬렌
			return new TalkMovingNpc(n, "helen1");
		case 865: // 모리아
			return new Moria(n);
		case 866: // 마가렛
			return new Margaret(n);
		case 870: // 제니
			return new TalkMovingNpc(n, "jenny1");
		case 872: // 앨리스
			return new TalkMovingNpc(n, "alice1");
		case 873: // 에블린
			return new TalkMovingNpc(n, "evelyn1");
		case 874: // 토비아
			return new TalkMovingNpc(n, "tovia1");
		case 875: // 리엘
			return new TalkMovingNpc(n, "leal1");
		case 876: // 알다
			return new TalkMovingNpc(n, "alda1");
		case 878: // 리나
			return new TalkMovingNpc(n, "lina1");
		case 880: // 데이지
			return new TalkMovingNpc(n, "daisy1");
		case 881: // 브리젯
			return new TalkMovingNpc(n, "bridget1");
		case 882: // 타냐
			return new TalkMovingNpc(n, "tanya1");
		case 883: // 다리아
			return new TalkMovingNpc(n, "daria1");
		case 884: // 도리스
			return new TalkMovingNpc(n, "doris1");
		case 885: // 트레이시
			return new TalkMovingNpc(n, "tracy1");
		case 886: // 폴리
			return new Polly(n);
		case 891: // 베리타
			return new Verita(n);
		case 909: // 브루너
			return new TalkMovingNpc(n, "brunner1");
		case 910: // 엘미나
			return new Elmina(n);
		case 911: // 마빈
			return new Marbin(n);
		case 912: // 벨리사
			return new Velisa(n);
		case 914: // 게라드
			return new Gerard(n);
		case 915: // 글렌
			return new Glen(n);
		case 916: // 멜린
			return new Mellin(n);
		case 917: // 아논
			return new Aanon(n);
		case 918: // 미란다
			return new Miranda(n);
		case 921: // 오스몬드
			return new Othmond(n);
		case 927: // 돼지
			return new TalkMovingNpc(n, "pig1");
		case 928: // 암닭
			return new TalkMovingNpc(n, "hen1");
		case 929: // 젖소
			return new TalkMovingNpc(n, "milkcow1");
		case 934: // 아놋테
			return new TalkMovingNpc(n, "anotte1");
		case 935: // 할트
			return new Halt(n);
		case 946: // 트레이
			return new Trey(n);
		case 947: // 메트
			return new Matt(n);
		case 948: // 타르킨
			return new Tarkin(n);
		case 949: // 고담
			return new Gotham(n);
		case 950: // 보긴
			return new Borgin(n);
		case 955: // 셀레나
			return new Selena(n);
		case 963: // 한스
			return new Hans(n);
		case 964: // 잭슨
			return new Jackson(n);
		case 965: // 아슈르
			return new Ashur(n);
		case 1053: // 라다르
			return new Ladar(n);
		case 1055: // 라이엔
			return new Lien();
		case 1056: // 줄리
			return new Julie(n);
		case 1057: // 핀
			return new Pin();
		case 1058: // 죠엘
			return new Joel();
		case 1068: // 케이스
		case 1069: // 해리슨
		case 1070: // 후퍼
		case 1071: // 콥
		case 1072: // 번치
			return new Telefire(n);
		case 1073: // 쿠하틴
			return new Kuhatin(n);
		case 1145: // 에스트
			return new Est(n);
		case 1177: // 레인져
			return new PatrolGuard(n);
		case 1238: // 오빌
			return new Orville(n);
		case 1246: // 허버트
			return new Herbert(n);
		case 1248: // 사우람
			return new Sauram(n);
		case 1249: // 노딤
			return new Nodim(n);
		case 1250: // 몰리
			return new Molly(n);
		case 1259: // 말콤
			return new TalkMovingNpc(n, "malcom1");
		case 1260: // 데이먼
			return new TalkMovingNpc(n, "damon1");
		case 1261: // 타이러스
			return new TalkMovingNpc(n, "tyrus1");
		case 1262: // 셔원
			return new TalkMovingNpc(n, "sherwin1");
		case 1263: // 모란
			return new TalkMovingNpc(n, "moran1");
		case 1264: // 페르디난드
			return new TalkMovingNpc(n, "ferdinand1");
		case 1265: // 질레스
			return new TalkMovingNpc(n, "giles1");
		case 1266: // 알드레드
			return new TalkMovingNpc(n, "aldred1");
		case 1267: // 길리언
			return new TalkMovingNpc(n, "gulian1");
		case 1268: // 마누스
			return new TalkMovingNpc(n, "manus1");
		case 1269: // 피에르
			return new TalkMovingNpc(n, "pierre1");
		case 1271: // 올리버
			return new TalkMovingNpc(n, "oliver1");
		case 1272: // 어니스트
			return new TalkMovingNpc(n, "ernest1");
		case 1286: // 워너
			return new Werner(n);
		case 1295: // 버질
			return new Vergil(n);
		case 1298: // 케빈
			return new Kevin(n);
		case 1299: // 알몬
			return new Almon(n);
		case 1301: // 메이어
			return new Mayer(n);
		case 1354: // 윌마
			return new Wilma(n);
		case 1380: // 토펜
			return new Tofen(n);
		case 1382: // 경매관리인
			return new TalkNpc("auction1", true);
		case 1396: // 페이퍼 맨
			return new TalkMovingNpc(n, "paperman");
		case 1413: // 루더
			return new TalkNpc("luder", false);
		case 1414:	// 요한
			return new Johan(n);
		case 1415: // 치료사
			return new Curer();
		case 1416: // 시리스
			return new Siris();
		case 1417: // 이쉬타
			return new Ishtar(n);
		case 1418: // 제노
			return new Zeno(n);
		case 1419:	// 벨게터
			return new TalkNpc("belgeter", false);
		case 1420: // 프라운
			return new Fraoun(n);
		case 1422: // 일드라스
			return new Illdrath(n);
		case 1423: // 드리스트
			return new Drist(n);
		case 1434: // 부아크
			return new Buakheu(n);
		case 1488: // 에브롤
			return new Eveurol(n);
		case 1500: // 결투장 안내원
			return new FieldOfHonor(n);
		case 1501: // 굿맨
			return new TalkNpc("goodman", false);
		case 1502: // 뉴트럴맨
			return new TalkNpc("neutralman", false);
		case 1503: // 이블맨
			return new TalkNpc("evilman", false);
		case 1510: // 루디엘
			return new Luudiel(n);
		case 1512: // 바이거
			return new Vaiger(n);
		case 1515: // 시반
			return new Shivan(n);
		case 1516: // 브리트
			return new Britt(n);
		case 1517: // 리올
			return new Riol(n);
		case 1518: // 데릭
			return new TalkMovingNpc(n, "derick1");
		case 1524: // 아리나
			return new TalkMovingNpc(n, "arina1");
		case 1525: // 안나벨
			return new TalkMovingNpc(n, "annabel1");
		case 1526: // 펠릭스
			return new TalkMovingNpc(n, "felix1");
		case 1527: // 오리엘
			return new TalkMovingNpc(n, "oriel1");
		case 1528: // 버랜트
			return new TalkMovingNpc(n, "barent1");
		case 1529: // 폴츠
			return new TalkMovingNpc(n, "paults1");
		case 1530: // 스펜서
			return new TalkMovingNpc(n, "spencer1");
		case 1531: // 게일
			return new TalkMovingNpc(n, "gale1");
		case 1538: // 하킴
			return new Hakim(n);
		case 1539: // 앨리
			return new Elly(n);
		case 1551: // 하이드림
			return new Haidrim(n);
		case 1557: // 샤샤
			return new TalkMovingNpc(n, "sasha");
		case 1591: // 포템핀
			return new Potempin(n);
		case 1592: // 이벨빈
			return new Ivelviin(n);
		case 1594: // 베리
			return new Berry(n);
		case 1595: // 랄프
			return new Ralf(n);
		case 1596: // 레슬리
			return new Leslie(n);
		case 1597: // 코브
			return new Cove(n);
		case 1598: // 가빈
			return new TalkMovingNpc(n, "gavin1");
		case 1599: // 데일리
			return new TalkMovingNpc(n, "daley1");
		case 1600: // 아타라
			return new TalkMovingNpc(n, "atara1");
		case 1602: // 프렉슨
			return new Freckson(n);
		case 1604: // 엑셀론
			return new Axellon(n);
		case 1609: // 콜롯세움 관리인
		case 1902: // 콜롯세움 부관리인
			if (Lineage.colosseum_talkingisland || Lineage.colosseum_silverknighttown || Lineage.colosseum_gludin || Lineage.colosseum_windawood || Lineage.colosseum_kent)
				return new ColiseumManager(n);
		case 1611: // 크리옴
			return new Kriom(n);
		case 1643: // 로즈
			return new Rose(n);
		case 1644: // 티나
			return new Tina(n);
		case 1653: // 에스메랄다
			return new Esmereld(n);
		case 1684: // 변신술사
			return new PolymorphMagician();
		case 1685: // 방어구 강화사
			return new ArmorEnchanter();
		case 1686: // 무기 강화사
			return new WeaponEnchanter();
		case 1724: // 리온
			return new TalkNpc("rion1", false);
		case 1725: // 커스
			return new TalkNpc("cuse1", false);
		case 1726: // 키리스
			return new Kiris(n);
		case 1728: // 루바
			return new Ruba(n);
		case 1729: // 티오
			return new Tio(n);
		case 1730: // 쿤
			return new Kun(n);
		case 1731: // 키요리
			return new Kiyari(n);
		case 1732: // 파고
			return new Pago(n);
		case 1737: // 코코
			return new Coco(n);
		case 1738: // 스카이
			return new Sky(n);
		case 1772: // 마일드
			return new Mild(n);
		case 1773: // 키리우스
			return new Kirius(n);
		case 1775: // 비우스
			return new Bius(n);
		case 1776: // 만드라
			return new Mandra(n);
		case 1777: // 데리안
			return new TalkMovingNpc(n, "derian1");
		case 1778: // 타라스
			return new Talass(n);
		case 1779: // 바뤼에스
			return new Varyeth(n);
		case 1780: // 엘뤼온
			return new Ellyonne();
		case 1781: // 크리스터
			return new Kreister(n);
		case 1792: // 비온
			return new TalkMovingNpc(n, "bion1");
		case 1793: // 디마
			return new TalkMovingNpc(n, "dima1");
		case 1794: // 루루
			return new TalkMovingNpc(n, "ruru1");
		case 1795: // 데칸
			return new TalkMovingNpc(n, "dekan1");
		case 1796: // 로터스
			return new TalkMovingNpc(n, "rotus1");
		case 1797: // 가루가
			return new TalkMovingNpc(n, "garuga1");
		case 1823: // 데푸리
			return new TalkNpc("defuri", false);
		case 1824: // 티파니
			return new TalkNpc("tifany", false);
		case 1825: // 로쿠
			return new TalkNpc("roku", false);
		case 1826: // 타우스
			return new TalkNpc("taus", true);
		case 1827: // 비얀
			return new TalkNpc("biyan", true);
		case 1828: // 엔케
			return new Enke(n);
		case 1875: // 린다
			return new Rinda(n);
		case 1876: // 파고르
			return new Pagoru(n);
		case 1877: // 디코
			return new Dico(n);
		case 1878: // 히림
			return new Hirim(n);
		case 1897: // 바르니아
			return new Barnia(n);
		case 1898: // 리비안
			return new Ribian(n);
		case 1925: // 리키
			return new Ricky(n);
		case 1926: // 오스
			return new Oth(n);
		case 1927: // 제로
			return new Zero(n);
		case 1928: // 젬
			return new Jem(n);
		case 1931: // 이스발 - 하이네 선착장 관리인
			return new Isvall(n);
		case 1932: // 아시리스
			return new Escapefi(n);
		case 1944: // 어블리젼 - 잊혀진섬 선착장 관리인
			return new Oblivion(n);
		case 1953: // 파울로
			return new Detecter(n);
		case 1954: // 치키
			return new Chiky(n);
		case 1955: // 럭키
			return new Luck(n);
		case 1956: // 티론
			return new Tilon(n);
		case 2012: // 마크
			return new Mark(n);
		case 2013: // 짐
			return new Jim(n);
		case 2098: // 초보 텔레포터
			return new FirstTeleporter(n);
		case 2014: // 수색개미
			return new SearchAnt();
		case 2015: // 문지기개미
			return new GatekeeperAnt();
		case 2016: // 아리아
			return new Aria(n);
		case 2018: // 페어리 프린세스
			return new FairyPrincess(n);
		case 2019: // 디롱
			return new Dilong(n);
		case 2021: // 마샤
			return new Marshall(n);
		case 2036: // 제인
			return new Jane(n);
		case 2082: // 잭-오-랜턴
			return new JackLantern(n);
		case 2121: // 데프만
			return new Defman(n);
		case 2122: // 라온
			return new Raon(n);
		case 2123: // 파우
			return new Pau(n);
		case 2124: // 카무
			return new Kamu(n);
		case 2126: // 시리우스
			return new Sirius(n);
		case 2128: // 비우스트
			return new Biust(n);
		case 2135: // 마이키
			return new TalkMovingNpc(n, "mikey1");
		case 2138: // 엘레아노
			return new TalkMovingNpc(n, "elleano1");
		case 2141: // 마르엔
			return new TalkMovingNpc(n, "maren1");
		case 2149: // 제이미
			return new TalkMovingNpc(n, "jaimy1");
		case 2143: // 쉐리안
			return new TalkMovingNpc(n, "sheryan1");
		case 2144: // 버클리
			return new TalkMovingNpc(n, "buckley1");
		case 2145: // 존스
			return new TalkMovingNpc(n, "jones1");
		case 2146: // 빔
			return new TalkMovingNpc(n, "bim1");
		case 2147: // 라프가
			return new TalkMovingNpc(n, "rafga1");
		case 2148: // 로젠
			return new Rozen(n);
		case 2150: // 막스
			return new TalkMovingNpc(n, "marx1");
		case 2151: // 라파엘
			return new TalkMovingNpc(n, "rapael1");
		case 2152: // 바바라
			return new TalkNpc("babara1", true);
		case 2153: // 키드만
			return new TalkNpc("kidman1", true);
		case 2154: // 아퀸
			return new TalkNpc("aquin1", true);
		case 2157: // 켈빈
			return new TalkMovingNpc(n, "calvin1");
		case 2160: // 리차드
			return new Richard();
		case 2161: // 맥
			return new Mack(n);
		case 2163: // 스빈
			return new Sabin(n);
		case 2166: // 브레드
			return new Brad(n);
		case 2228: // 카루딤
			return new Karudim(n);
		case 2229: // 쥬케
			return new Juke(n);
		case 2230: // 팀프킨
			return new Timpukin(n);
		case 2231: // 캐서린
			return new Catherine(n);
		case 2232: // 샤루
			return new Sharu(n);
		case 2234: // 엘레리스
			return new Elleris(n);
		case 2235: // 마구스
			return new Magus(n);
		case 2236: // 페가
			return new Fega(n);
		case 2237: // 멜리사
			return new Melissa(n);
		case 2238: // 혈맹집행인
			return new ClanMaker();
		case 2257: // 조사원
			return new TalkMovingNpc(n, "searcherk4");
		case 2258: // 헤이트
			return new Heit(n);
		case 2343: // 헤이스트사
			return new Haste();
		case 2345: // 던햄
			return new Dunham(n);
		case 2371: // 파르츠
			return new Paruit(n);
		case 2430: // 경비 대장
			return new Kan(n);
		case 2432: // 론드
			return new Ronde(n);
		case 2448: // 하로
			return new TalkMovingNpc(n, "haro1");
		case 2491: // 하데스티
			return new Hadesty();
		case 2492: // 레이아스
			return new Rayearth(n);
		case 2493: // 공간이동사 에냐
			return new Enya(n);
		case 2494: // 스크와티
			return new Squalid(n);
		case 2496: // 카렌
			return new Karen(n);
		case 2497: // 에들렌
			return new Edlin(n);
		case 2498: // 칸의 경비병
			return new TalkNpc("kandum", false);
		case 2501: // 론드의 암살대
			return new TalkNpc("rondedum", false);
		case 2550: // 데린
			return new TalkMovingNpc(n, "derin1");
		case 2552: // 세디아
			return new Sedia();
		case 2554: // 피에로
			return new TalkMovingNpc(n, "pierot1");
		case 2556: // 비숍
			return new TalkMovingNpc(n, "bishop1");
		case 2557: // 피어스
			return new Pierce(n);
		case 2558: // 그랑디크
			return new TalkMovingNpc(n, "grandik1");
		case 2560: // 엘비엔느
			return new TalkMovingNpc(n, "ellvienue1");
		case 2561: // 라뮤네
			return new TalkMovingNpc(n, "lamune1");
		case 2586: // 디아노스
			return new Deanos(n);
		case 2608: // 바무트
			return new Bamut(n);
		case 2688:	// 카리프
			return new Karif(n);
		case 2689: // 류미엘
			return new Ryumiel(n);
		case 2860: // 조드
			return new Jode(n);
		case 2861: // 롤코
			return new Rollko(n);
		case 2899: // 파심
			return new Pasim(n);
		case 2902: // 라폰스
			return new Rafons(n);
		case 2929: // 프랑코
			return new Franko(n);
		case 2934: // 루즈
			return new TalkMovingNpc(n, "citizen1");
		case 2935: // 벡터
			return new TalkMovingNpc(n, "citizen2");
		case 2936: // 러스터
			return new TalkMovingNpc(n, "citizen3");
		case 2938: // 세라티
			return new TalkMovingNpc(n, "citizen5");
		case 2941: // 그레이
			return new TalkMovingNpc(n, "citizen8");
		case 2942: // 케니히
			return new TalkMovingNpc(n, "citizen9");
		case 2943: // 뮤라스
			return new TalkMovingNpc(n, "citizen10");
		case 2985: // 속박된 영혼
			return new FetteredSoul(n);
		case 2986: // 사르샤
			return new Sarsha(n);
		case 3055: // 쟌쿠
			return new Jianku(n);
		case 3070: // 쿠론
			return new Kuron(n);
		case 3071: // 투락
			return new Tulak(n);
		case 3072: // 쿠샨
			return new Kusian(n);
		case 3131: // 에마르트
			return new TraderEmart(n);
		case 3135: // 레오나
			return new Reona(n);
		case 3161: // 레딘
			return new TalkMovingNpc(n, "redin1");
		case 3164: // 번스
			return new TalkMovingNpc(n, "burns1");
		case 3170: // 얀스틴
			return new TalkMovingNpc(n, "yastin1");
		case 3222: // 루쿠
			return new Luku(n);
		case 3224: // 카샴
			return new Kasham(n);
		case 3225: // 유노
			return new Yuno(n);
		case 3237: // 아덴 상단
			return new AdenChamber_of_Commerce(n);
		case 3316: // 레크만
			return new Lekman(n);
		case 3317: // 세리안
			return new Serian(n);
		case 3318: // 리리
			return new Liri(n);
		case 3319: // 기온
			return new Gion(n);
		case 3320: // 시리아
			return new Syria(n);
		case 3321: // 오실리아
			return new Oshillia(n);
		case 3322: // 호닌
			return new Honin(n);
		case 3323: // 치코
			return new Chico(n);
		case 3324: // 홉
			return new Hob(n);
		case 3325: // 터크
			return new Tuck(n);
		case 3326: // 갈리온
			return new Galleon(n);
		case 3327: // 길버트
			return new Gilbert(n);
		case 3328: // 포리칸
			return new Porikan(n);
		case 3329: // 제릭
			return new Jerik(n);
		case 3330: // 자루만
			return new Jaruman(n);
		case 3427: // 오베르
			return new Ober(n);
		case 3428: // 니키
			return new Niki(n);
		case 3436: // 듀발
			return new Duvall(n);
		case 3437: // 듀란
			return new Duran(n);
		case 3438: // 록산느
			return new Roxanne(n);
		case 3449: // 아르카
			return new TalkMovingNpc(n, "arka1");
		case 3480: // 연금술사
			return new Alchemist(n);
		case 3526: // 쿠드
			return new Tigus(n);
		case 3527: // 베릭
			return new Berik(n);
		case 3529: // 듀론
			return new Duron(n);
		case 3530: // 포니
			return new Foni(n);
		case 3531: // 카미트
			return new TalkMovingNpc(n, "kamit1");
		case 3561: // 루디안
			return new TalkMovingNpc(n, "rudian1a");
		case 3563: // 티구스
			return new Tigus(n);
		case 3565: // 알리
			return new Alri(n);
		case 3594: // 루니
			return new TalkMovingNpc(n, "rooney");
		case 3595: // 로베르토
			return new TalkMovingNpc(n, "roberto");
		case 3596: // 루푸스
			return new TalkMovingNpc(n, "lupus");
		case 3597: // 카루
			return new TalkMovingNpc(n, "karu");
		case 3947: // 공간의 일그러짐
			return new Entgate(n);
		case 4043: // 지휘관 에스테반
			return new Estevan(n);
		case 4109: // 보석 세공사
			return new JewelCraftsman(n);
		case 4641: // 차원의 문
			return new Cspace(n);
		case 4656: // 두다마라 부우
			return new OrcfbuWoo(n);
		case 4818: // 시종장 맘몬
			return new Mammon(n);
		case 4850: // 치안대장 아미수
			return new Amisoo(n);
		case 5041: // 전쟁물자 상인
			return new Mellisa(n);
		case 5108: // 라쿠키
			return new Rakuki(n);
		case 5164: // 아덴 기마 단원
			return new HorseSeller(n);
		case 5190: // 오스틴
			return new Rostin(n);
			//return new Ostin(n);
		case 5199:	// 케플리샤
			return new Keplisha(n);
		case 5200: // 샤론
			return new Sharon(n);
		case 5201: // 쿠엔
			return new TalkNpc("kuen1", false);
		case 5113: // 라르손
			return new TalkNpc("rarson1", false);
		case 5276: // 낚시 꼬마
			return new FishingBoy(n);
		case 5277: // 낚시 할아버지
			return new FishElder(n);
		case 5295: // 낚시 아주머니
			return new FishLady(n);
		case 5278: // 알프레드
			return new Alfred(n);
		case 5290: // 오로라
			return new TalkMovingNpc(n, "aurora1");
		case 5291: // 베키
			return new TalkMovingNpc(n, "becky1");
		case 5293: // 체이스
			return new TalkMovingNpc(n, "chase1");
		case 5294: // 제리코
			return new TalkMovingNpc(n, "jericho1");
		case 5394: // 마이진
			return new Maijin(n);
		case 5138: // 수상한 잡화 상인
		case 5324: // 수상한 조련사
		case 5742: // 수상한 변신술사
		case 5743: // 수상한 무기 상인
		case 5744: // 수상한 갑옷 상인
			return new Premium(n);
		case 5590: // 회상의 촛불지기 로로
			return new BaseResetRoro(n);
		case 5783: // 회상의 촛불소녀 마리
			return new BaseResetMary(n);
		case 8446: // 초보자 도우미
			return new HelperNovice(n);
		case 8447: // 수련장 관리인
			return new AdminNovice(n);
		case 16464: // 게라드 용병단
		case 16465: // 윈다우드 용병단
		case 16466: // 켄트 용병단
		case 16467: // 기란 용병단
		case 16469: // 하이네 용병단
		case 16470: // 아덴 용병단
			return new MercenaryGroup();
		case 29332930: // 윈다우드 용병단장
		case 12422930: // 기란 용병단장
			return new TalkNpc("ddummyc1", false);
		case 29312930: // 웰던 용병단장
			return new TalkNpc("gdummyc1", false);
			//마을 관리인
		case 2171: // 관리인
			return new TalkNpc("othertown", false);
		case 2170: // 촌장
			return new TalkNpc("secretary1", false);
		case 1639: // 모모
			return new Momo(n);
		case 1640: // 오킴
			return new Orcm(n);
		case 1642: // 진
			return new Jin(n);
		case 1637: // 콜드
			return new Cold(n);
		case 1641: // 올드
			return new Old(n);
		case 2167: // 루키
			return new Rookie(n);
		case 1638: // 아리에
			return new Arieh(n);
		case 1674: // 신녀 아가타
			return new GoddessAgata();
		case 5191: // 필리스
			return new Fillis(n);
		case 5902: // 신녀 유리스
			return new GoddessYuris();
		case 2342: // 신녀 에리엘
			return new GoddessAriel();
		case 7441: // 기란감옥 텔레포터 멀린
			return new Giran_dungeon_Telepoter();
		case 7740: // 강인한 하이오스
		case 7741: // 세심한 슈누
		case 7742: // 끈질긴 도오호
		case 7743: // 찬란한 바에미
			return new Fivelviin(n);
		case 7773: // 한쿠
			return new Hankoo(n);
		case 7774: // 반쿠
			return new Bankoo(n);
		case 7775: // 만쿠
			return new Mankoo(n);
		case 7892: // 슈에르메
			return new Schuerme(n);
		case 8572: // 기란 시장 텔레포터
		case 8573: // 글말 시장 텔레포터
		case 8574: // 은말 시장 텔레포터
		case 8575: // 오렌 시장 텔레포터
			return new MarketTeleporter(n);
		case 18390: // 기란 시장 경비병
		case 18391: // 글말 시장 경비병
		case 18392: // 은말 시장 경비병
		case 18393: // 오렌 시장 경비병
			return new MarketGuard(n);
		case 3221: // 시장 공간 이동사
			return new Market_telepoter(n);
		case 15710: // 칼루아
			return new Kahlua(n);
		case 17593: // 켄트성 게이트키퍼
		case 17594: // 윈다우드성 게이트키퍼
		case 17595: // 기란성 게이트키퍼
		case 17596: // 지저성 게이트키퍼
		case 17597: // 아덴성 게이트키퍼
		case 17598: // 하이네 게이트키퍼
		case 17599: // 오크요새 게이트키퍼
		case 17600: // 디아드요새 게이트키퍼
			return new GateKeeper(n);
		case 25905: // 메리드
			return new Marry(n);
			

		default:
			if (n.isAi()) {
				if (n.getType().equalsIgnoreCase("promot_npc")) 
					return new Promot_npc(n);
				else
					return new NpcInstance(n);
			} else {
				if (n.getType().equalsIgnoreCase("buy shop")) {
					return new BuyShop(n);
				} else if (n.getType().equalsIgnoreCase("buy shop2")) {
					return new BuyShop2(n);
				} else if (n.getType().equalsIgnoreCase("buy shop3")) {
					return new BuyShop3(n);
				} else if (n.getType().equalsIgnoreCase("cash shop")) {
					return new CashShop(n);
				} else if (n.getType().equalsIgnoreCase("sell shop")) {
					return new SellShop(n);
				} else if (n.getType().equalsIgnoreCase("sell shop2")) {
					return new SellShop2(n);
				} else if (n.getType().equalsIgnoreCase("Dwarf")) {
					return new Gotham(n);
				} else if (n.getType().equalsIgnoreCase("rank bronze")) {
					return new Rank_bronze();
//					//싸울의뢰
//				} else if (n.getType().equalsIgnoreCase("trankBronze1")) {
//					return new Trank_bronze1();
//				} else if (n.getType().equalsIgnoreCase("trankBronze2")) {
//					return new Trank_bronze2();
//				} else if (n.getType().equalsIgnoreCase("trankBronze3")) {
//					return new Trank_bronze3();
					
				} else if (n.getType().equalsIgnoreCase("pvp rank bronze")) {
					return new PvP_Rank_bronze();
				} else if (n.getType().equalsIgnoreCase("buff_npc")) {
					return new BuffNpc();
				} else if (n.getType().equalsIgnoreCase("buy_sell_shop")) {
					return new BuySellShop(n);
			
				} else if (n.getType().equalsIgnoreCase("tebe_teleporter")) {
					return new TebeTeleporter();
				} else if (n.getType().equalsIgnoreCase("hell_teleporter")) {
					return new HellTeleporter();
				} else if (n.getType().equalsIgnoreCase("phunt_tell")) {
					return new PenguinTeleporter();
				} else if (n.getType().equalsIgnoreCase("tbox_tell")) {
					return new TreasureHuntTeleporter();
				} else if (n.getType().equalsIgnoreCase("world_teleporter")) {
					return new WorldBossTeleporter();
				} else if (n.getType().equalsIgnoreCase("오만 부적 제작사")) {
					return new OmanSealCreate();
				} else if (n.getType().equalsIgnoreCase("devil_teleporter")) {
					return new EvilTeleporter();
				} else if (n.getType().equalsIgnoreCase("ice_teleporter")) {
					return new FrozenTeleporter();
				} else if (n.getType().equalsIgnoreCase("변신카드합성")) {
					return new PolyCardCompose();
				} else if (n.getType().equalsIgnoreCase("마안 합성사")) {
					return new ManaStoneSynthesis();
				} else if (n.getType().equalsIgnoreCase("dollgo")) {// 인형 합성사
					return new MagicdollCompose();
				} else if (n.getType().equalsIgnoreCase("투구제작")) {// 인형 합성사
					return new createNpcH();
				} else if (n.getType().equalsIgnoreCase("뽑기 엔피시")) {// 인형 합성사
					return new testnpc();
				} else if (n.getType().equalsIgnoreCase("보석제작")) {// 인형 합성사
					return new jewelCreate();
				} else if (n.getType().equalsIgnoreCase("투제1")) {
					return new exarmorcreate();
				} else if (n.getType().equalsIgnoreCase("강화버프")) {
					return new Buff_Enhancement();
				} else if (n.getType().equalsIgnoreCase("파워볼진행자")) {
					return new lineage.world.object.npc.PowerballNpc(n);
				} else if (n.getType().equalsIgnoreCase("파워볼") || n.getType().equalsIgnoreCase("일반볼")) {
					return new NpcInstance(n); // 추첨 발표용 NPC
				} else {
					switch (n.getGfx()) {
					case 1256: // 아지트 시녀
						return new Maid();
					case 1766: // 기란마을 은행원
						return new Bank(n);
					case 5913: // 오크 산타
						return new OrcSanta(n);
					case 6246: // 낚시터 상인
						return new Illusina(n);
					case 11512: // 기란마을 군주
						return new Clan_lord();
					case 12039: // 오만의 탑 이동 부적 상인
						return new OmanShop(n);
					default:
						return new object();
					}
				}				
			}
		}
	}

	static public object getPool(Class<?> c) {
		synchronized (pool) {
			object r_o = null;
			for (object o : pool) {
				if (o.getClass().equals(c)) {
					r_o = o;
					break;
				}
			}
			if (r_o != null)
				pool.remove(r_o);

			return r_o;
		}
	}

	static public void setPool(NpcInstance ni) {
		ni.close();
		synchronized (pool) {
			if (!pool.contains(ni))
				pool.add(ni);
		}
	}

	/** ShopInstance, TeleportInstance 등 NpcInstance가 아닌 NPC 객체 풀 반환용 */
	static public void setPoolObject(object o) {
		try {
			o.close();
		} catch (Exception e) {
			lineage.share.System.println(e);
		}
		synchronized (pool) {
			if (!pool.contains(o))
				pool.add(o);
		}
	}

	static public int getPoolSize() {
		return pool.size();
	}
	
	static public List<ShopInstance> getShopList() {
		synchronized (list_shop) {
			return list_shop;
		}
	}

	static public int selectCount(Connection con) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT COUNT(*) FROM npc_spawnlist");
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : selectCount(Connection con)\r\n", NpcSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return 0;
	}
	static public void delete(String name) {
		PreparedStatement st = null;
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("DELETE FROM npc_spawnlist WHERE name=?");
			st.setString(1, name);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : delete(Npc n)\r\n", NpcSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	static public void insert(Connection con, final String name, final String npcName, final int locX, final int locY, final int locMap, final int heading, final int respawn, final String title) {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("INSERT INTO npc_spawnlist SET name=?, npcName=?, locX=?, locY=?, locMap=?, heading=?, respawn=?, title=?");
			st.setString(1, name);
			st.setString(2, npcName);
			st.setInt(3, locX);
			st.setInt(4, locY);
			st.setInt(5, locMap);
			st.setInt(6, heading);
			st.setInt(7, respawn);
			st.setString(8, title);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : insert()\r\n", NpcSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	static public void delete(Npc n) {
		PreparedStatement st = null;
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("DELETE FROM npc_spawnlist WHERE name=?");
			st.setString(1, n.getName());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : delete(Npc n)\r\n", NpcSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
}
