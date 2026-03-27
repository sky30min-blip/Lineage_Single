package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Exp;
import lineage.bean.database.Item;
import lineage.bean.database.Poly;
import lineage.bean.database.Shop;
import lineage.bean.database.Skill;
import lineage.bean.database.SkillRobot;
import lineage.bean.lineage.Book;
import lineage.bean.lineage.RobotPoly;
import lineage.database.DatabaseConnection;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcRobotInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.instance.ShopInstance;

public class RobotController {	
	// 무인케릭 관리 목록.
	static public List<PcRobotInstance> list_pc;
	//
	private static List<PcRobotInstance> pool_pc;
	// 변신 리스트.
	private static List<RobotPoly> list_poly;
	// 카운트 최대 횟수
	private static int maxCount = 100;
	
	static public void init(){
		TimeLine.start("RobotController..");	

		pool_pc = new ArrayList<PcRobotInstance>();
		// 무인케릭 객체 초기화.
		list_pc = new ArrayList<PcRobotInstance>();

		list_poly = new ArrayList<RobotPoly>();	

		if(Lineage.robot_auto_pc) {
			readPcRobot();
			readPoly();
		}

		TimeLine.end();
	}
	
	public static List<PcRobotInstance> getPcRobotList() {
		synchronized (list_pc) {
			return new ArrayList<PcRobotInstance>(list_pc);
		}
	}
	
	public static int getPcRobotListSize() {
		return list_pc.size();
	}
	
	private static PcRobotInstance getPoolPc() {
		PcRobotInstance pri = null;
		synchronized (pool_pc) {
			if(pool_pc.size()>0){
				pri = pool_pc.get(0);
				pool_pc.remove(0);
			}else{
				pri = new PcRobotInstance();
			}
		}
		return pri;
	}
	
	public static void setPool(PcRobotInstance pri){
		synchronized (pool_pc) {
			pool_pc.add(pri);
		}
	}
	
	static public void toTimer(long time){
		synchronized (list_pc) {
			for(RobotInstance bi : list_pc)
				bi.toTimer(time);
		}
	}
	
	/**
	 * 월드 아웃 처리 메서드.
	 * @param pri
	 */
	static public void toWorldOut(PcRobotInstance pri) {
		pri.toWorldOut();
		synchronized (list_pc) {
			list_pc.remove( pri );
		}
	}
	
	/**
	 * 구매하려는 아이템을 판매하는 상점 찾기.
	 * @param item_name
	 * @return
	 */
	public static ShopInstance findShop(PcRobotInstance pi, String item_name) {
		for (ShopInstance si : NpcSpawnlistDatabase.getShopList()) {
			for (Shop s : si.getNpc().getShop_list()) {
				// 본토의 상점만 검색.
				if (si.getMap() == 4 && Util.isDistance(pi.getX(), pi.getY(), pi.getMap(), si.getX(), si.getY(), si.getMap(), 60) 
					&& s.getItemName().equalsIgnoreCase(item_name) && s.getItemBress() == 1 && s.getAdenType().equalsIgnoreCase("아데나"))
					return si;
			}
		}
		return null;
	}
	
	/**
	 * 로봇 이름 존재여부리턴.
	 * @param name
	 * @return
	 */
	public static boolean isName(String name) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM _robot WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isName(String name)\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return false;
	}
	
	/**
	 * 스킬정보 추출.
	 * @param pr
	 */
	static public void readSkill(Connection con, PcRobotInstance pr) {
		List<Skill> list = SkillController.find(pr);
		
		if(list==null)
			return;

		list.clear();

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM _robot_skill WHERE class=?");
			st.setString(1, getClassNameHangl(pr.getClassType()));
			rs = st.executeQuery();
			while(rs.next()) {
				Skill s = SkillDatabase.find(rs.getInt("skill_uid"));
				if(s != null) {
					SkillRobot sr = new SkillRobot(s);
					sr.setType(rs.getString("skill_type"));
					sr.setProbability(rs.getDouble("시전확률") * 0.01);
					sr.setWeaponType(rs.getString("시전무기"));
					sr.setTarget(rs.getString("공격대상"));
					sr.setLevel(rs.getInt("사용레벨"));
					
					switch (rs.getString("정령속성")) {
					case "일반":
						sr.setAttribute(Lineage.ELEMENT_NONE);
						break;
					case "물":
						sr.setAttribute(Lineage.ELEMENT_WATER);
						break;
					case "바람":
						sr.setAttribute(Lineage.ELEMENT_WIND);
						break;
					case "땅":
						sr.setAttribute(Lineage.ELEMENT_EARTH);
						break;
					case "불":
						sr.setAttribute(Lineage.ELEMENT_FIRE);
						break;
					}
					
					list.add( sr );
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readSkill(PcRobotInstance pr)\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	static public void reloadRobotSkill() {	
		TimeLine.start("_robot_skill 테이블 리로드 완료 - ");

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();

			synchronized (list_pc) {
				if (list_pc.size() > 0) {
					for (RobotInstance pr : list_pc) {
						List<Skill> list = SkillController.find(pr);

						if (list == null)
							return;

						list.clear();

						st = con.prepareStatement("SELECT * FROM _robot_skill WHERE class=?");
						st.setString(1, getClassNameHangl(pr.getClassType()));
						rs = st.executeQuery();
						while (rs.next()) {
							Skill s = SkillDatabase.find(rs.getInt("skill_uid"));
							if (s != null) {
								SkillRobot sr = new SkillRobot(s);
								sr.setType(rs.getString("skill_type"));
								sr.setProbability(rs.getDouble("시전확률") * 0.01);
								sr.setWeaponType(rs.getString("시전무기"));
								sr.setTarget(rs.getString("공격대상"));
								sr.setLevel(rs.getInt("사용레벨"));
								
								switch (rs.getString("정령속성")) {
								case "일반":
									sr.setAttribute(Lineage.ELEMENT_NONE);
									break;
								case "물":
									sr.setAttribute(Lineage.ELEMENT_WATER);
									break;
								case "바람":
									sr.setAttribute(Lineage.ELEMENT_WIND);
									break;
								case "땅":
									sr.setAttribute(Lineage.ELEMENT_EARTH);
									break;
								case "불":
									sr.setAttribute(Lineage.ELEMENT_FIRE);
									break;
								}
								
								list.add(sr);
							}
						}
					}
					st.close();
					rs.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reRoadRobotSkill()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		TimeLine.end();
	}
	
	/**
	 * 기억정보 추출.
	 * @param pr
	 */
	static public void readBook(Connection con, PcRobotInstance pr) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM _robot_book");
			rs = st.executeQuery();
			while(rs.next()) {
				Book b = BookController.getPool();
				b.setLocation(rs.getString("location"));
				b.setX(rs.getInt("locX"));
				b.setY(rs.getInt("locY"));
				b.setMap(rs.getInt("locMAP"));
				b.setMinLevel(rs.getInt("입장레벨"));
				BookController.append(pr, b);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readBook(PcRobotInstance pr)\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	/**
	 * 리로드 북
	 * 2018-08-11
	 * by connector12@nate.com
	 */
	static public void reloadRobotBook() {
		TimeLine.start("_robot_book 테이블 리로드 완료 - ");

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();

			synchronized (list_pc) {
				for (PcRobotInstance pr : list_pc) {
					BookController.find(pr).clear();

					st = con.prepareStatement("SELECT * FROM _robot_book");
					rs = st.executeQuery();
					while (rs.next()) {
						Book b = BookController.getPool();
						b.setLocation(rs.getString("location"));
						b.setX(rs.getInt("locX"));
						b.setY(rs.getInt("locY"));
						b.setMap(rs.getInt("locMAP"));
						b.setMinLevel(rs.getInt("입장레벨"));
						BookController.append(pr, b);
					}
					st.close();
					rs.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reRoadRobotBook()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		TimeLine.end();
	}
	
	/**
	 * 무인PC 정보 추출.
	 */
	private static void readPcRobot() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {		
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM _robot");
			rs = st.executeQuery();
			while (rs.next()) {
				if (rs.getString("스폰_여부").equalsIgnoreCase("false") || checkName(rs.getString("name")) != null || rs.getInt("objId") < 1900000)
					continue;
					
				PcRobotInstance pr = getPoolPc();
				
				if (pr == null) {
					pr = new PcRobotInstance();
				}
				
				pr.setObjectId(rs.getInt("objId"));
				pr.setName(rs.getString("name"));
				pr.action = rs.getString("행동");
				pr.setStr(rs.getInt("str"));
				pr.setDex(rs.getInt("dex"));
				pr.setCon(rs.getInt("con"));
				pr.setWis(rs.getInt("wis"));
				pr.setInt(rs.getInt("inter"));
				pr.setCha(rs.getInt("cha"));

				pr.setX(rs.getInt("locX"));
				pr.setY(rs.getInt("locY"));
				pr.setMap(rs.getInt("locMAP"));
				
				pr.setTitle(rs.getString("title"));
				pr.setLawful(rs.getInt("lawful"));
				pr.setClanId(rs.getInt("clanID"));
				pr.setClanName(rs.getString("clan_name"));
				if (rs.getString("class").equalsIgnoreCase("군주")) {
					pr.setClassType(Lineage.LINEAGE_CLASS_ROYAL);
					pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.royal_male_gfx : Lineage.royal_female_gfx);
					pr.setGfx(pr.getClassGfx());
					pr.setMaxHp(Lineage.royal_hp);
					pr.setMaxMp(Lineage.royal_mp);
				} else if (rs.getString("class").equalsIgnoreCase("기사")) {
					pr.setClassType(Lineage.LINEAGE_CLASS_KNIGHT);
					pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.knight_male_gfx : Lineage.knight_female_gfx);
					pr.setGfx(pr.getClassGfx());
					pr.setMaxHp(Lineage.knight_hp);
					pr.setMaxMp(Lineage.knight_mp);
				} else if (rs.getString("class").equalsIgnoreCase("요정")) {
					pr.setClassType(Lineage.LINEAGE_CLASS_ELF);
					pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.elf_male_gfx : Lineage.elf_female_gfx);
					pr.setGfx(pr.getClassGfx());
					pr.setMaxHp(Lineage.elf_hp);
					pr.setMaxMp(Lineage.elf_mp);
				} else if (rs.getString("class").equalsIgnoreCase("마법사")) {
					pr.setClassType(Lineage.LINEAGE_CLASS_WIZARD);
					pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.wizard_male_gfx : Lineage.wizard_female_gfx);
					pr.setGfx(pr.getClassGfx());
					pr.setMaxHp(Lineage.wizard_hp);
					pr.setMaxMp(Lineage.wizard_mp);
				} else if (rs.getString("class").equalsIgnoreCase("다크엘프")) {
					pr.setClassType(Lineage.LINEAGE_CLASS_DARKELF);
					pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.darkelf_male_gfx : Lineage.darkelf_female_gfx);
					pr.setGfx(pr.getClassGfx());
					pr.setMaxHp(Lineage.darkelf_hp);
					pr.setMaxMp(Lineage.darkelf_mp);
				}
				//
				Exp e = ExpDatabase.find(rs.getInt("level"));
				int hp = 0;
				int mp = 0;
				for (int i = 0; i < e.getLevel(); ++i) {
					hp += CharacterController.toStatusUP(pr, true);
					mp += CharacterController.toStatusUP(pr, false);
				}

				pr.setMaxHp(pr.getMaxHp() + hp);
				pr.setMaxMp(pr.getMaxMp() + mp);
				pr.setNowHp(pr.getMaxHp());
				pr.setNowMp(pr.getMaxMp());
				pr.setLevel(e.getLevel());
				pr.setDynamicMr(rs.getInt("mr"));
				pr.setDynamicSp(rs.getInt("sp"));
				pr.setFood(Lineage.MAX_FOOD);
				pr.setAttribute(pr.getClassType() == Lineage.LINEAGE_CLASS_ELF ? Util.random(1, 4) : 0);
				pr.setWeaponEn(rs.getInt("무기인챈트"));
				pr.setAc(rs.getInt("ac"));
				pr.setHeading(rs.getInt("heading"));
				pr.setWeapon_name(rs.getString("무기 이름").length() > 0 ? rs.getString("무기 이름") : null);
				pr.setDoll_name(rs.getString("마법 인형").length() > 0 ? rs.getString("마법 인형") : null);

				pr.toWorldJoin(con);
				//
				synchronized (list_pc) {
					list_pc.add(pr);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readPcRobot()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * 로봇 이름으로 검색.
	 * 2019-04-28
	 * by connector12@nate.com
	 */
	public static PcRobotInstance checkName(String name) {
		synchronized (list_pc) {
			for (PcRobotInstance pi : list_pc) {
				if (pi.getName().equalsIgnoreCase(name))
					return pi;
			}
		}
		
		return null;
	}
	
	/**
	 * 로봇 uid로 검색.
	 * 2019-04-28
	 * by connector12@nate.com
	 */
	public static PcRobotInstance checkObjId(long objId) {
		synchronized (list_pc) {
			for (PcRobotInstance pi : list_pc) {
				if (pi.getObjectId() == objId)
					return pi;
			}
		}
		
		return null;
	}
	
	public static void reloadPcRobot() {
		TimeLine.start("_robot 테이블 리로드 완료 - ");
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		int[] home = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM _robot");
			rs = st.executeQuery();
			while (rs.next()) {
				if (rs.getInt("objId") >= 1900000) {
					// 스폰되어 있는 상태에서 삭제.
					if (rs.getString("스폰_여부").equalsIgnoreCase("false")) {				
						if (checkObjId(rs.getInt("objId")) != null) {
							PcRobotInstance pi = checkObjId(rs.getInt("objId"));
							toWorldOut(pi);
						}
					} else {
						// 스폰 안되어있을 경우 스폰.
						if (checkObjId(rs.getInt("objId")) == null) {
							PcRobotInstance pr = getPoolPc();
							
							if (pr == null) {
								pr = new PcRobotInstance();
							}
							
							pr.setObjectId(rs.getInt("objId"));
							pr.setName(rs.getString("name"));
							pr.action = rs.getString("행동");
							pr.setStr(rs.getInt("str"));
							pr.setDex(rs.getInt("dex"));
							pr.setCon(rs.getInt("con"));
							pr.setWis(rs.getInt("wis"));
							pr.setInt(rs.getInt("inter"));
							pr.setCha(rs.getInt("cha"));

							pr.setX(rs.getInt("locX"));
							pr.setY(rs.getInt("locY"));
							pr.setMap(rs.getInt("locMAP"));

							pr.setTitle(rs.getString("title"));
							pr.setLawful(rs.getInt("lawful"));
							pr.setClanId(rs.getInt("clanID"));
							pr.setClanName(rs.getString("clan_name"));
							if (rs.getString("class").equalsIgnoreCase("군주")) {
								pr.setClassType(Lineage.LINEAGE_CLASS_ROYAL);
								pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.royal_male_gfx : Lineage.royal_female_gfx);
								pr.setGfx(pr.getClassGfx());
								pr.setMaxHp(Lineage.royal_hp);
								pr.setMaxMp(Lineage.royal_mp);
							} else if (rs.getString("class").equalsIgnoreCase("기사")) {
								pr.setClassType(Lineage.LINEAGE_CLASS_KNIGHT);
								pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.knight_male_gfx : Lineage.knight_female_gfx);
								pr.setGfx(pr.getClassGfx());
								pr.setMaxHp(Lineage.knight_hp);
								pr.setMaxMp(Lineage.knight_mp);
							} else if (rs.getString("class").equalsIgnoreCase("요정")) {
								pr.setClassType(Lineage.LINEAGE_CLASS_ELF);
								pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.elf_male_gfx : Lineage.elf_female_gfx);
								pr.setGfx(pr.getClassGfx());
								pr.setMaxHp(Lineage.elf_hp);
								pr.setMaxMp(Lineage.elf_mp);
							} else if (rs.getString("class").equalsIgnoreCase("마법사")) {
								pr.setClassType(Lineage.LINEAGE_CLASS_WIZARD);
								pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.wizard_male_gfx : Lineage.wizard_female_gfx);
								pr.setGfx(pr.getClassGfx());
								pr.setMaxHp(Lineage.wizard_hp);
								pr.setMaxMp(Lineage.wizard_mp);
							} else if (rs.getString("class").equalsIgnoreCase("다크엘프")) {
								pr.setClassType(Lineage.LINEAGE_CLASS_DARKELF);
								pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.darkelf_male_gfx : Lineage.darkelf_female_gfx);
								pr.setGfx(pr.getClassGfx());
								pr.setMaxHp(Lineage.darkelf_hp);
								pr.setMaxMp(Lineage.darkelf_mp);
							}
							//
							Exp e = ExpDatabase.find(rs.getInt("level"));
							int hp = 0;
							int mp = 0;
							for (int i = 0; i < e.getLevel(); ++i) {
								hp += CharacterController.toStatusUP(pr, true);
								mp += CharacterController.toStatusUP(pr, false);
							}

							pr.setMaxHp(pr.getMaxHp() + hp);
							pr.setMaxMp(pr.getMaxMp() + mp);
							pr.setNowHp(pr.getMaxHp());
							pr.setNowMp(pr.getMaxMp());
							pr.setLevel(e.getLevel());
							pr.setDynamicMr(rs.getInt("mr"));
							pr.setDynamicSp(rs.getInt("sp"));
							pr.setFood(Lineage.MAX_FOOD);
							pr.setAttribute(pr.getClassType() == Lineage.LINEAGE_CLASS_ELF ? Util.random(1, 4) : 0);
							pr.setWeaponEn(rs.getInt("무기인챈트"));
							pr.setAc(rs.getInt("ac"));
							pr.setHeading(rs.getInt("heading"));
							pr.setWeapon_name(rs.getString("무기 이름").length() > 0 ? rs.getString("무기 이름") : null);
							pr.setDoll_name(rs.getString("마법 인형").length() > 0 ? rs.getString("마법 인형") : null);
							pr.isReload = true;

							pr.toWorldJoin(con);
							//
							synchronized (list_pc) {
								if (!list_pc.contains(checkObjId(rs.getInt("objId"))))
									list_pc.add(pr);
							}
						} else {
							// 스폰 되어있는데 정보를 수정하였을 경우 처리.
							PcRobotInstance pr = checkObjId(rs.getInt("objId"));
							pr.setName(rs.getString("name"));
							pr.setPcBobot_mode(rs.getString("행동"));
							pr.action = rs.getString("행동");
							pr.setStr(rs.getInt("str"));
							pr.setDex(rs.getInt("dex"));
							pr.setCon(rs.getInt("con"));
							pr.setWis(rs.getInt("wis"));
							pr.setInt(rs.getInt("inter"));
							pr.setCha(rs.getInt("cha"));
							pr.setTitle(rs.getString("title"));
							pr.setLawful(rs.getInt("lawful"));
							pr.setClanId(rs.getInt("clanID"));
							pr.setClanName(rs.getString("clan_name"));
							if (rs.getString("class").equalsIgnoreCase("군주")) {
								pr.setClassType(Lineage.LINEAGE_CLASS_ROYAL);
								pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.royal_male_gfx : Lineage.royal_female_gfx);
								pr.setGfx(pr.getClassGfx());
								pr.setMaxHp(Lineage.royal_hp);
								pr.setMaxMp(Lineage.royal_mp);
							} else if (rs.getString("class").equalsIgnoreCase("기사")) {
								pr.setClassType(Lineage.LINEAGE_CLASS_KNIGHT);
								pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.knight_male_gfx : Lineage.knight_female_gfx);
								pr.setGfx(pr.getClassGfx());
								pr.setMaxHp(Lineage.knight_hp);
								pr.setMaxMp(Lineage.knight_mp);
							} else if (rs.getString("class").equalsIgnoreCase("요정")) {
								pr.setClassType(Lineage.LINEAGE_CLASS_ELF);
								pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.elf_male_gfx : Lineage.elf_female_gfx);
								pr.setGfx(pr.getClassGfx());
								pr.setMaxHp(Lineage.elf_hp);
								pr.setMaxMp(Lineage.elf_mp);
							} else if (rs.getString("class").equalsIgnoreCase("마법사")) {
								pr.setClassType(Lineage.LINEAGE_CLASS_WIZARD);
								pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.wizard_male_gfx : Lineage.wizard_female_gfx);
								pr.setGfx(pr.getClassGfx());
								pr.setMaxHp(Lineage.wizard_hp);
								pr.setMaxMp(Lineage.wizard_mp);
							} else if (rs.getString("class").equalsIgnoreCase("다크엘프")) {
								pr.setClassType(Lineage.LINEAGE_CLASS_DARKELF);
								pr.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.darkelf_male_gfx : Lineage.darkelf_female_gfx);
								pr.setGfx(pr.getClassGfx());
								pr.setMaxHp(Lineage.darkelf_hp);
								pr.setMaxMp(Lineage.darkelf_mp);
							}
							//
							Exp e = ExpDatabase.find(rs.getInt("level"));
							int hp = 0;
							int mp = 0;
							for (int i = 0; i < e.getLevel(); ++i) {
								hp += CharacterController.toStatusUP(pr, true);
								mp += CharacterController.toStatusUP(pr, false);
							}

							pr.setMaxHp(pr.getMaxHp() + hp);
							pr.setMaxMp(pr.getMaxMp() + mp);
							pr.setNowHp(pr.getMaxHp());
							pr.setNowMp(pr.getMaxMp());
							pr.setLevel(e.getLevel());
							pr.setDynamicMr(rs.getInt("mr"));
							pr.setDynamicSp(rs.getInt("sp"));
							pr.setFood(Lineage.MAX_FOOD);
							pr.setAttribute(pr.getClassType() == Lineage.LINEAGE_CLASS_ELF ? Util.random(1, 4) : 0);
							pr.setWeaponEn(rs.getInt("무기인챈트"));
							pr.setAc(rs.getInt("ac"));
							pr.setHeading(rs.getInt("heading"));
							pr.setWeapon_name(rs.getString("무기 이름").length() > 0 ? rs.getString("무기 이름") : null);
							pr.setDoll_name(rs.getString("마법 인형").length() > 0 ? rs.getString("마법 인형") : null);
							pr.isReload = true;
							
							if (pr.getInventory() != null) {
								for (ItemInstance weapon : pr.getInventory().getList()) {
									if (weapon instanceof ItemWeaponInstance && weapon.isEquipped()) {
										weapon.setEnLevel(pr.getWeaponEn());
										break;
									}
								}
							}
							
							pr.toTeleport(pr.getX(), pr.getY(), pr.getMap(), false);
						}
					}
				}
			}
			
			// 나비켓에서 로봇 삭제 후 리로드 할경우 해당 로봇이 스폰되어있을 경우 삭제.
			synchronized (list_pc) {
				for (PcRobotInstance pi : getPcRobotList()) {
					if (!pi.isReload) {
						list_pc.remove(pi);
						pi.toWorldOut();
					} else {
						pi.isReload = false;
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readPcRobot()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		TimeLine.end();
	}
	
	/**
	 * 로봇 사용 여부 처리 메소드.
	 * 2019-07-03
	 * by connector12@nate.com
	 */
	public static void reloadPcRobot(boolean isDelete) {
		Connection con = null;
		PreparedStatement st = null;

		try {
			con = DatabaseConnection.getLineage();
			
			if (isDelete) {
				st = con.prepareStatement("UPDATE _robot SET 스폰_여부='false'");
				st.executeUpdate();
			} else {
				st = con.prepareStatement("UPDATE _robot SET 스폰_여부='true'");
				st.executeUpdate();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reloadPcRobot(boolean isDelete)\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
		
		reloadPcRobot();
	}
	
	private static String getClassNameHangl(int classType) {
		String className = "군주";
		if(classType == Lineage.LINEAGE_CLASS_KNIGHT)
			className = "기사";
		if(classType == Lineage.LINEAGE_CLASS_ELF)
			className = "요정";
		if(classType == Lineage.LINEAGE_CLASS_WIZARD)
			className = "마법사";
		if(classType == Lineage.LINEAGE_CLASS_DARKELF)
			className = "다크엘프";
		if(classType == Lineage.LINEAGE_CLASS_DRAGONKNIGHT)
			className = "용기사";
		if(classType == Lineage.LINEAGE_CLASS_BLACKWIZARD)
			className = "환술사";
		return className;
	}
	
	/**
	 * 체력 포션.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public String getHealingPotion(PcRobotInstance pi) {
		String[] item = {"빨간 물약", "주홍 물약", "맑은 물약", "농축 체력 회복제", "농축 고급 체력 회복제", "농축 강력 체력 회복제"};
		String temp = null;
		int count = 0;
		
		do {
			if (count++ > maxCount)
				break;
			
			temp = item[Util.random(0, item.length - 1)];
		} while (RobotController.findShop(pi, temp) == null);
				
		return temp;
	}
	
	/**
	 * 체력 포션 갯수.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public int getHealingPotionCnt() {
		return Util.random(100, 200);
	}
	
	/**
	 * 초록 물약.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public String getHastePotion(PcRobotInstance pi) {
		String[] item = {"초록 물약", "강화 초록 물약"};
		String temp = null;
		int count = 0;
		
		do {
			if (count++ > maxCount)
				break;
			
			temp = item[Util.random(0, item.length - 1)];
		} while (RobotController.findShop(pi, temp) == null);
		
		return temp;
	}
	
	/**
	 * 초록 물약 갯수.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public int getHastePotionCnt() {
		return Util.random(5, 15);
	}
	
	/**
	 * 용기 물약.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public String getBraveryPotion(PcRobotInstance pi) {
		String[] item = {"용기의 물약", "강화 용기의 물약"};
		String temp = null;
		int count = 0;
		
		do {
			if (count++ > maxCount)
				break;
			
			temp = item[Util.random(0, item.length - 1)];
		} while (RobotController.findShop(pi, temp) == null);
		
		return temp;
	}
	
	/**
	 * 용기 물약 갯수.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public int getBraveryPotionCnt() {
		return Util.random(5, 15);
	}
	
	/**
	 * 와퍼.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public String getElvenWafer(PcRobotInstance pi) {
		String[] item = {"엘븐 와퍼", "강화 엘븐 와퍼"};
		String temp = null;
		int count = 0;
		
		do {
			if (count++ > maxCount)
				break;
			
			temp = item[Util.random(0, item.length - 1)];
		} while (RobotController.findShop(pi, temp) == null);
		
		return temp;
	}
	
	/**
	 * 와퍼 갯수.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public int getElvenWaferCnt() {
		return Util.random(5, 15);
	}
	
	/**
	 * 변신 주문서.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public String getScrollPolymorph(PcRobotInstance pi) {
		String[] item = {"변신 주문서"};
		String temp = null;
		int count = 0;
		
		do {
			if (count++ > maxCount)
				break;
			
			temp = item[Util.random(0, item.length - 1)];
		} while (RobotController.findShop(pi, temp) == null);
		
		return temp;
	}
	
	/**
	 * 변신 주문서 갯수.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public int getScrollPolymorphCnt() {
		return Util.random(5, 10);
	}
	
	/**
	 * 화살.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public String getArrow(PcRobotInstance pi) {
		String[] item = {"은 화살"};
		String temp = null;
		int count = 0;
		
		do {
			if (count++ > maxCount)
				break;
			
			temp = item[Util.random(0, item.length - 1)];
		} while (RobotController.findShop(pi, temp) == null);
		
		return temp;
	}
	
	/**
	 * 화살 갯수.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public int getArrowCnt() {
		return Util.random(300, 500);
	}
	
	/**
	 * 무기.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	static public Item getWeapon(int classType) {
		String weapon = null;
		String[] royal = {"일본도", "레이피어"};
		String[] knight = {"양손검"};
		String[] elf = {"크로스 보우", "장궁"};
		String[] wizard = {"힘의 지팡이", "마나의 지팡이"};
		
		switch (classType) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			weapon = royal[Util.random(0, royal.length - 1)];
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			weapon = knight[Util.random(0, knight.length - 1)];
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			weapon = elf[Util.random(0, elf.length - 1)];
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			weapon = wizard[Util.random(0, wizard.length - 1)];
			break;
		}
		
		return ItemDatabase.find(weapon);
	}
	
	static public void readPoly() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		list_poly.clear();
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM _robot_poly");
			rs = st.executeQuery();
			while (rs.next()) {
				Poly p = PolyDatabase.getName(rs.getString("poly_name"));
				
				if (p == null)
					continue;
				
				RobotPoly rp = new RobotPoly();
				
				rp.setPoly(p);
				rp.setPolyClass(rs.getString("변신클래스"));

				synchronized (list_poly) {
					if (rs.getString("사용여부").equalsIgnoreCase("true"))
						list_poly.add(rp);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readPoly()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	static public void reloadPoly() {
		TimeLine.start("_robot_poly 테이블 리로드 완료 - ");
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		list_poly.clear();
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM _robot_poly");
			rs = st.executeQuery();
			while (rs.next()) {
				Poly p = PolyDatabase.getName(rs.getString("poly_name"));
				
				if (p == null)
					continue;
				
				RobotPoly rp = new RobotPoly();
				
				rp.setPoly(p);
				rp.setPolyClass(rs.getString("변신클래스"));

				synchronized (list_poly) {
					if (rs.getString("사용여부").equalsIgnoreCase("true"))
						list_poly.add(rp);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readPoly()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		TimeLine.end();
	}
	
	static public List<RobotPoly> getPolyList() {
		return new ArrayList<RobotPoly>(list_poly);
	}
	
	/**
	 * 변신 가능한 목록이 있는지 확인.
	 * 2018-08-13
	 * by connector12@nate.com
	 */
	static public boolean isPoly(PcRobotInstance pr) {
		for (RobotPoly rp : getPolyList()) {
			if (rp.getPoly().getMinLevel() <= pr.getLevel() && SpriteFrameDatabase.findGfxMode(rp.getPoly().getGfxId(), pr.getGfxMode() + Lineage.GFX_MODE_ATTACK))
				return true;
		}
		return false;	
	}
}
