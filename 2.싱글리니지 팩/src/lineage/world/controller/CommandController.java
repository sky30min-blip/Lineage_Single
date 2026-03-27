package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import all_night.Lineage_Balance;
import all_night.Npc_promotion;
import lineage.bean.database.BossSpawn;
import lineage.bean.database.Drop;
import lineage.bean.database.Exp;
import lineage.bean.database.FirstInventory;
import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.database.Npc;
import lineage.bean.database.PcShop;
import lineage.bean.database.Shop;
import lineage.bean.database.Skill;
import lineage.bean.database.Warehouse;
import lineage.bean.database.marketPrice;
import lineage.bean.lineage.Buff;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.GmTeleport;
import lineage.bean.lineage.Kingdom;
import lineage.bean.lineage.Map;
import lineage.bean.lineage.Party;
import lineage.database.AccountDatabase;
import lineage.database.BackgroundDatabase;
import lineage.database.BadIpDatabase;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.DungeontellbookDatabase;
import lineage.database.ExpDatabase;
import lineage.database.FishItemListDatabase;
import lineage.database.GmTeleportDatabase;
import lineage.database.HackNoCheckDatabase;
import lineage.database.ItemBundleDatabase;
import lineage.database.ItemChanceBundleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.database.ItemSkillDatabase;
import lineage.database.MonsterBossSpawnlistDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterDropDatabase;
import lineage.database.MonsterSkillDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.NpcDatabase;
import lineage.database.NpcShopDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.ServerNoticeDatabase;
import lineage.database.ServerReloadDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SummonListDatabase;
import lineage.database.TeamBattleDatabase;
import lineage.database.TimeDungeonDatabase;
import lineage.database.WarehouseDatabase;
import lineage.gui.GuiMain;
import lineage.network.LineageServer;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Ability;
import lineage.network.packet.server.S_BlueMessage;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ClanWar;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_InventoryAdd;
import lineage.network.packet.server.S_InventoryDelete;
import lineage.network.packet.server.S_LetterNotice;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectGfx;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_SoundEffect;
import lineage.network.packet.server.S_Weather;
import lineage.network.packet.server.S_WorldTime;

import lineage.plugin.PluginController;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.Mysql;
import lineage.share.System;
import lineage.thread.AiThread;
import lineage.util.Shutdown;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;
import lineage.world.object.instance.PcShopInstance;
import lineage.world.object.item.ElvenWafer;
import lineage.world.object.item.MagicDoll;
import lineage.world.object.item.all_night.Exp_marble;
import lineage.world.object.item.all_night.Exp_support;
import lineage.world.object.item.all_night.Item_Remove_Wand;
import lineage.world.object.item.all_night.ScrollOfAccessory;
import lineage.world.object.item.potion.BluePotion;
import lineage.world.object.item.potion.BraveryPotion;
import lineage.world.object.item.potion.CurePoisonPotion;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.item.potion.WisdomPotion;
import lineage.world.object.item.scroll.ScrollLabeledDaneFools;
import lineage.world.object.item.scroll.ScrollLabeledVenzarBorgavve;
import lineage.world.object.item.scroll.ScrollLabeledVerrYedHorae;
import lineage.world.object.item.scroll.ScrollLabeledZelgoMer;
import lineage.world.object.item.scroll.ScrollPolymorph;
import lineage.world.object.item.scroll.ScrollTeleport;
import lineage.world.object.item.scroll.TOITeleportScroll;
import lineage.world.object.item.weapon.Arrow;
import lineage.world.object.magic.AdvanceSpirit;
import lineage.world.object.magic.BlessWeapon;
import lineage.world.object.magic.BlessedArmor;
import lineage.world.object.magic.BraveAvatar;
import lineage.world.object.magic.ChattingClose;
import lineage.world.object.magic.DecreaseWeight;
import lineage.world.object.magic.EnchantDexterity;
import lineage.world.object.magic.EnchantMighty;
import lineage.world.object.magic.FrameSpeedOverStun;
import lineage.world.object.magic.GlowingWeapon;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.IronSkin;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.ShiningShield;
import lineage.world.object.magic.ShockStun;
import system.MonDrop_System;
import lineage.world.object.magic.RevengeCooldown;
public class CommandController {
	static public String TOKEN = "-";
	static public boolean error = false;


	/**
	 * 명령어 처리 함수
	 * 
	 * @param pc
	 *            : 명령어 요청자
	 * @param cmd
	 *            : 명령어
	 * @return : 명령어 수행 성공 여부
	 */
	static public boolean toCommand(object o, String cmd) {
		if (o == null)
			return false;

		if (cmd.startsWith(Lineage.command) == false || cmd.length() < 2)
			return false;

		if(!Lineage.is_chatting_close_command && o!=null && o.getGm() == 0 && o.isBuffChattingClose()){
			// 현재 채팅 금지중입니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
			return true;
		}
		
		try {
			if (!Common.system_config_console) {
				long time = System.currentTimeMillis();
				String timeString = Util.getLocaleString(time, true);
				
				String log = String.format("[%s]\t [캐릭터: %s]\t [%s]", timeString, o.getName(), cmd);
				
				GuiMain.display.asyncExec(new Runnable() {
					public void run() {
						GuiMain.getViewComposite().getCommandComposite().toLog(log);
					}
				});
			}
			
			StringTokenizer st = new StringTokenizer(cmd);
			String key = st.nextToken();

			Object is_check = PluginController.init(CommandController.class, "toCommand", o, key, st);
			if (is_check != null)
				return (Boolean) is_check;

			if (Lineage.user_command) {
				// 공통 명령어
				if (key.equalsIgnoreCase(Lineage.command + "명령어")) {		
					ChattingController.toChatting(o, "\\fY------------------- 서버 명령어 안내 --------------------", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, ".틱|.정보|.피바|.버프|.버프시간|.랭킹|.마방|.나이|.서버정보", Lineage.CHATTING_MODE_MESSAGE);	
					ChattingController.toChatting(o, ".보스|.던전|.파티|%주변파티|%혈맹파티|.드랍|.몹드랍|.오토루팅|.메세지", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, ".전투|.전투멘트|.대미지|.이펙트|.수배|.스왑", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, ".후원|.자동낚시|.인벤|.무인혈맹", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o,".교환|.상점멘트|.매크로 ", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, "\\fY----------------------------------------------------------", Lineage.CHATTING_MODE_MESSAGE);				
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "도움말")) {
					o.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), o, "help"));
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "라이트") || key.equalsIgnoreCase(Lineage.command + "맵핵")) {
					o.toSender(S_Ability.clone(BasePacketPooling.getPool(S_Ability.class), 3, true));
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "보스")) {
					bossList(o);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "리니지")) {
					String now_date = String.format("%02d:%02d:%02d", ServerDatabase.getLineageTimeHour(), ServerDatabase.getLineageTimeMinute(), ServerDatabase.getLineageTimeSeconds());
					ChattingController.toChatting(o, String.format("리니지 시간 %s", now_date), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(".몹드랍")) {
					monsterDrop(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "드랍")) {
					try {
						String itemName = st.nextToken();
						int bless = 1;
											
						if (itemName != null) {
							List<String> list = new ArrayList<String>();
							list.add(itemName);
							
							if (st.hasMoreTokens()) {
								switch (st.nextToken()) {
								case "축":
									bless = 0;
									break;
								case "일반":
									bless = 1;
									break;
								case "저주":
									bless = 2;
									break;
								}
								
								for (Drop d : MonsterDropDatabase.getDropList()) {
									if (list.size() >= 250)
										break;
									
									if (d.getItemName().replace(" ", "").contains(itemName)) {
										if (d.getItemBress() == bless) {
											if (d.getItemBress() == 1) 
												list.add(String.format("%s: %s", d.getMonName(), d.getItemName()));
											else
												list.add(String.format("%s: (%s)%s", d.getMonName(), d.getItemBress() == 0 ? "축" : "저주", d.getItemName()));
										}
									}
								}
							} else {
								for (Drop d : MonsterDropDatabase.getDropList()) {
									if (list.size() >= 250)
										break;
									
									if (d.getItemName().replace(" ", "").contains(itemName)) {
										if (d.getItemBress() == 1) 
											list.add(String.format("%s: %s", d.getMonName(), d.getItemName()));
										else
											list.add(String.format("%s: (%s)%s", d.getMonName(), d.getItemBress() == 0 ? "축" : "저주", d.getItemName()));
									}
								}
							}
							
							if (list.size() < 2)
								o.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), o, "itemdrop1", null, list));
							else
								o.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), o, "itemdrop", null, list));
						}
					} catch (Exception e) {
						if (o != null) {
							ChattingController.toChatting(o, Lineage.command + "드랍 아이템이름 (이름은 띄어쓰기없이 입력해주세요.) (축/일반/저주)", Lineage.CHATTING_MODE_MESSAGE);
							ChattingController.toChatting(o, "예) " + Lineage.command + "드랍 갑옷마법주문서 축", Lineage.CHATTING_MODE_MESSAGE);
							ChattingController.toChatting(o, Lineage.command + "드랍 아이템이름 축/일반/저주 입력안할경우 모두 검색", Lineage.CHATTING_MODE_MESSAGE);
						}
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "물약멘트") || key.equalsIgnoreCase(Lineage.command + "물약메세지") || key.equalsIgnoreCase(Lineage.command + "물약")) {
					o.setAutoPotionMent(!o.isAutoPotionMent());
					ChattingController.toChatting(o, String.format("물약멘트기능이 %s활성화 되었습니다.", o.isAutoPotionMent() ? "" : "비"), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "오토루팅")) {
					o.setAutoPickup(!o.isAutoPickup());
					ChattingController.toChatting(o, String.format("오토루팅이 %s활성화 되었습니다.", o.isAutoPickup() ? "" : "비"), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "메세지") || key.equalsIgnoreCase(Lineage.command + "멘트")) {
					PcInstance pc = (PcInstance) o;
					if (pc.isAutoPickMessage())
						pc.setAutoPickMessage(false);
					else
						pc.setAutoPickMessage(true);
					ChattingController.toChatting(o, String.format("오토루팅 메세지가 %s활성화 되었습니다.", pc.isAutoPickMessage() ? "" : "비"), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "틱")) {
					if (o instanceof Character) {
						Character cha = (Character) o;
						ChattingController.toChatting(cha, String.format("\\fY[HP:%d초당/%d회복] [MP:%d초당/%d회복]", cha.getHpTime(), cha.getHpTic(), cha.getMpTime(), cha.getMpTic()),
								Lineage.CHATTING_MODE_MESSAGE);
						return true;
					}
				} else if (key.equalsIgnoreCase(Lineage.command + "피바")) {
					// 설정.
					o.setHpbar(!o.isHpbar());
					ChattingController.toChatting(o, String.format("hp바 표현이 %s활성화 되었습니다.", o.isHpbar() ? "" : "비"), Lineage.CHATTING_MODE_MESSAGE);
					// 표현.
					o.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), (Character) o, o.isHpbar()));
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "혈맹가입") || key.equalsIgnoreCase(Lineage.command + "원격가입") || key.equalsIgnoreCase(Lineage.command + "원격혈맹")) {
					try {
						PcInstance user = World.findPc(st.nextToken());
						if (user == null) {
							ChattingController.toChatting(o, "캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
						} else {
							if (o.getName().equals(user.getName())) {
								ChattingController.toChatting(o, "자신에게 가입신청을 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							} else {
								ClanController.toJoin((PcInstance) o, user);
							}
						}
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "혈맹가입 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "후원")) {
					String[] arrayOfString = new String[4];
					arrayOfString[0] = new StringBuilder().append(ServerDatabase.getName()).toString();
					arrayOfString[1] = "* 후 원 안내 *";
	
					StringBuilder Str = new StringBuilder();
					Str.append("\n\n");
					Str.append(Lineage.done_ment1);
					Str.append("\n\n");
					Str.append(Lineage.done_ment2);
					Str.append("\n\n");
					Str.append(Lineage.done_ment3);
					Str.append("\n\n");
					Str.append(Lineage.done_ment4);
					Str.append("\n\n");
					Str.append(Lineage.done_ment5);
					Str.append("\n\n");
					Str.append(Lineage.done_ment6);
					Str.append("\n\n");
					Str.append(Lineage.done_ment7);
					
					arrayOfString[2] = Str.toString();
					o.toSender(S_LetterNotice.clone(BasePacketPooling.getPool(S_LetterNotice.class), arrayOfString));
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "전투") || key.equalsIgnoreCase(Lineage.command + "마크")) {
					try {
						Clan clan = ClanController.find((PcInstance) o);
						
						if (clan == null) {
							ChattingController.toChatting(o, "혈맹에 가입해야 사용할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return true;
						}
						if (clan.getName().equalsIgnoreCase(Lineage.new_clan_name)) {
							ChattingController.toChatting(o, "신규혈맹은 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return true;
						}
						
						if (o.isMark) {
							o.isMark = false;
							
							for (Clan c : ClanController.getClanList().values()) {
								if (c != null && !c.getName().equalsIgnoreCase(clan.getName()) && !c.getName().equalsIgnoreCase(Lineage.new_clan_name) && 
										!c.getName().equalsIgnoreCase(Lineage.teamBattle_A_team) && !c.getName().equalsIgnoreCase(Lineage.teamBattle_B_team)) {
									o.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, clan.getName(), c.getName()));
								}
							}
						} else {
							o.isMark = true;
							
							for (Clan c : ClanController.getClanList().values()) {
								if (c != null && !c.getName().equalsIgnoreCase(clan.getName()) && !c.getName().equalsIgnoreCase(Lineage.new_clan_name) && 
										!c.getName().equalsIgnoreCase(Lineage.teamBattle_A_team) && !c.getName().equalsIgnoreCase(Lineage.teamBattle_B_team)) {
									o.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 1, clan.getName(), c.getName()));
								}
							}
						}
					} catch (Exception e) {
						ChattingController.toChatting(o, "혈맹에 가입해야 사용할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "직위")) {
					try {
						String memberName = st.nextToken();
						int grade = Integer.valueOf(st.nextToken());
						PcInstance clanMember = World.findPc(memberName);
						Clan c = ClanController.find((PcInstance) o);

						for (Kingdom k : KingdomController.getKingdomList()) {
							if (k.isWar()) {
								ChattingController.toChatting(o, "공성 진행 중엔 직위 변경이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
								return true;
							}
						}
						
						if (grade > 3) {
							ChattingController.toChatting(o, "직위는 3보다 클 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return true;
						}

						if (o.getClanGrade() > 1 && o.getClanId() != 0 && grade < 4) {
							if (o.getClanGrade() > 2 && o.getClanGrade() < 4 && grade == 3) {
								if (!Lineage.only_clan_boss_class_royal) {
									ChattingController.toChatting(o, "군주 직위는 다른사람에게 넘길 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
									return true;
								}
								
								if (checkClanGrade(o)) {
									ChattingController.toChatting(o, "군주 직위는 한명만 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
									return true;
								}
							}
							if (o.getName().equalsIgnoreCase(memberName)) {
								ChattingController.toChatting(o, "자신에게 직위를 부여할 수 없습니다. ", Lineage.CHATTING_MODE_MESSAGE);
							} else {
								if ((o.getClanGrade() > grade && grade >= 0) || (o.getClanGrade() == 3 && grade >= 0 && grade < 4)) {
									if (clanMember != null) {
										if (clanMember.getClanId() != o.getClanId()) {
											ChattingController.toChatting(o, "혈맹원이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
										} else {
											if (o.getClanGrade() > clanMember.getClanGrade() || o.getClanGrade() > 2) {
												clanMember.setClanGrade(grade);
												c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 768, o.getName(), clanMember.getName(),
														(grade == 0 ? "혈맹원" : grade == 1 ? "수호기사" : grade == 2 ? "부군주" : "군주")));
												if (o.getClanGrade() == 3 && grade == 3) {
													o.setClanGrade(2);
													c.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE,
															String.format("%s님이 부군주로 임명되었습니다.", o.getName())));
												}
											} else {
												ChattingController.toChatting(o, "자신과 같거나 높은 직위에게 직위를 부여할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
											}
										}
									} else {
										// 클랜멤버가 존재하지않을때
										checkClanGrade(o, memberName, grade, c);
									}

									if (grade == 3)
										ClanController.setClanLord(memberName, c);
								} else {
									ChattingController.toChatting(o, "자신의 직위보다 높은 직위를 부여할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
									return true;
								}
							}
						} else {
							ChattingController.toChatting(o, "부군주 이상 직위를 부여할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "직위 아이디 등급  ex).직위 홍길동 1", Lineage.CHATTING_MODE_MESSAGE);
						ChattingController.toChatting(o, "[0:혈맹원] [1:수호기사] [2:부군주] [3:군주]", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "추방")) {
					try {
						String memberName = st.nextToken();
						PcInstance clanMember = World.findPc(memberName);
						Clan c = ClanController.find((PcInstance) o);

						if (o.getClanGrade() > 1 && o.getClanGrade() < 4 && o.getClanId() != 0) {
							if (o.getName().equalsIgnoreCase(memberName)) {
								ChattingController.toChatting(o, "자신을 추방할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							} else {
								if (clanMember != null) {
									if (clanMember.getClanId() != o.getClanId()) {
										ChattingController.toChatting(o, "혈맹원이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
									} else {
										if (o.getClanGrade() > clanMember.getClanGrade() || o.getName().equalsIgnoreCase(c.getLord())) {
											ClanController.toKin((PcInstance) o, memberName);
										} else {
											ChattingController.toChatting(o, "자신과 같거나 높은 직위를 추방할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
										}
									}
								} else {
									// 클랜멤버가 존재하지않을때
									checkClanMember(o, memberName, c, clanMember);
								}
							}
						} else {
							ChattingController.toChatting(o, "부군주 이상 직위만 추방가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "추방 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "수배")) {
					// .수배 아이디 100000
					try {
					    long wantedPrice = 0;
					    if (o.getLevel() <= 48) {
					        wantedPrice = Lineage.wanted_price_min;
					    } else if (o.getLevel() <= 52) {
					        wantedPrice = Lineage.wanted_price_min52;
					    } else if (o.getLevel() <= 55) {
					        wantedPrice = Lineage.wanted_price_min55;
					    } else if (o.getLevel() <= 60) {
					        wantedPrice = Lineage.wanted_price_min60;
					    } else {
					        wantedPrice = Lineage.wanted_price_min65;
					    }

					    WantedController.append(o, o.getName(), wantedPrice);
					} catch (Exception e) {
					    ChattingController.toChatting(o, Lineage.command + "수배", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
			
		
				} else if (key.equalsIgnoreCase(Lineage.command + "좌표복구")) {
					유저좌표복구(o, st);
					return true;
					
				} else if (key.equalsIgnoreCase(Lineage.command + "파티") || key.equalsIgnoreCase(Lineage.command + "초대")) {
					try {
						PcInstance user = World.findPc(st.nextToken());
						if (user == null) {
							ChattingController.toChatting(o, "캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
						} else {
							if (o.getName().equals(user.getName())) {
								ChattingController.toChatting(o, "자신은 초대할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							} else {
								PartyController.toParty((PcInstance) o, user);
							}
						}
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "파티 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "주변파티")) {
					try {
						for (PcInstance pc : World.getPcList()) {
							if (!pc.getName().equals(o.getName()) && Util.isDistance(o, pc, 5) ){
							PartyController.toParty((PcInstance) o, pc);
							}
						}
						
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "주변파티", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "혈맹파티")) {
					혈맹파티(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "정산")) {
					베릴분배(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "배팅")) {
					powerballBet(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "파워볼홀")) {
					powerballBetAmount(o, st, 1);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "파워볼짝")) {
					powerballBetAmount(o, st, 0);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "파워볼언더")) {
					powerballBetAmount(o, st, 2);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "파워볼오버")) {
					powerballBetAmount(o, st, 3);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "지휘")) {
					if (o.getClanGrade() < 2) {
						ChattingController.toChatting(o, "부군주 이상 임명 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						return true;
					}
					try {
						PcInstance user = World.findPc(st.nextToken());
						Clan c = ClanController.find((PcInstance) o);
						if (user == null) {
							ChattingController.toChatting(o, "캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return true;
						} else {
							if (user.getClanId() != o.getClanId()) {
								ChattingController.toChatting(o, "혈맹원이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
								return true;
							} else {
								for (PcInstance member : c.getList()) {
									if (member.isClanOrder() && !member.getName().equals(user.getName())) {
										member.setClanOrder(false);
										break;
									}
								}
								user.setClanOrder(true);
								c.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE,
										String.format("%s님이 지휘관으로 임명되었습니다.", user.getName())));
							}
						}
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "지휘 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "사운드")) {
					PcInstance pc = (PcInstance) o;
					if (pc.isSound())
						pc.setSound(false);
					else
						pc.setSound(true);
					ChattingController.toChatting(o, String.format("사운드가 %s활성화 되었습니다.", pc.isSound() ? "" : "비"), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "파티멘트") || key.equalsIgnoreCase(Lineage.command + "파티메세지")) {
					PartyMent(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "버프시간")) {
					int cnt = 0;
					List<String> list = new ArrayList<String>();
					Buff buff = BuffController.find(o);
					if (buff != null) {
						for (BuffInterface b : buff.getList()) {
							if (b.getSkill() != null)
								list.add(String.format("%s : %d초", b.getSkill().getName(), b.getTime()));
							o.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), o, "Buff", null, list));
							cnt += 1;
						}
						ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
						ChattingController.toChatting(o, "현제 " + cnt + "개의 버프가 적용되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
						ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
						return true;
					}
				} else if (key.equalsIgnoreCase(Lineage.command + "나이")) {
					try {
						int age = Integer.valueOf(st.nextToken());
						if (age < 15 || age > 60) { // 에러 체크
							ChattingController.toChatting(o, String.format("나이 범위가 잘못되었습니다. 15세 부터 60세 까지 가능합니다."), Lineage.CHATTING_MODE_MESSAGE);
						} else {
							o.setAge(age);
							ChattingController.toChatting(o, String.format("%s님의 나이가 %d로 설정되었습니다", o.getName(), o.getAge()), Lineage.CHATTING_MODE_MESSAGE);
						}
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "나이 숫자", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "버프")) {
					if( o.getMap() != 5143){
						if (o.getLevel() < Lineage.buff_max_level ) {
							toBuff(o);
						} else {
							if (o.getInventory().isAden(Lineage.buff_aden, true)) {
								toBuff(o);
								ChattingController.toChatting(o, String.format("버프: %d아데나 소모.", Lineage.buff_aden), Lineage.CHATTING_MODE_MESSAGE);
							} else {
								ChattingController.toChatting(o, String.format("버프는 %d아데나가 필요합니다.", Lineage.buff_aden), Lineage.CHATTING_MODE_MESSAGE);
							}
						}	
					}else{
							ChattingController.toChatting(o, String.format("인형경주중엔 사용 불가능합니다."), Lineage.CHATTING_MODE_MESSAGE);
					}
						
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "시간")) {
					Calendar cal = Calendar.getInstance(Locale.KOREA); // 주어진 시간대에 맞게 현재 시각으로 초기화된 GregorianCalender객체를 반환.// 또는 Calendar now = Calendar.getInstance(Locale.KOREA);
					String date = cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH) + 1 + "월 " + cal.get(Calendar.DATE) + "일 " + cal.get(Calendar.HOUR_OF_DAY) + "시 " + cal.get(Calendar.MINUTE)
							+ "분 " + cal.get(Calendar.SECOND) + "초 ");
					ChattingController.toChatting(o, String.format("[한국 표준시간] %s입니다.", date), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "랭킹")) {
					ChattingController.toChatting(o, String.format("[전체 랭킹] %d위 [%s 랭킹] %d위 입니다.", RankController.getRankAll(o),
							o.getClassType() == 0 ? "군주" : o.getClassType() == 1 ? "기사" : o.getClassType() == 2 ? "요정" : "마법사", RankController.getRankClass(o)), Lineage.CHATTING_MODE_MESSAGE);
					
				
					ChattingController.toChatting(o, String.format("[pvp 랭킹] %d위 입니다.", RankController.getPvPRankAll(o)), Lineage.CHATTING_MODE_MESSAGE);
					
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "정보") || key.equalsIgnoreCase(Lineage.command + "캐릭") || key.equalsIgnoreCase(Lineage.command + "케릭") || key.equalsIgnoreCase(Lineage.command + "마방")) {
					characterInfo(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "서버정보")) {
					serverInfo(o);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "자동낚시") && Lineage.is_auto_fishing) {
					if (o instanceof PcInstance) {
						PcInstance pc = (PcInstance) o;
						
						if (pc.getLevel() < Lineage.auto_fish_level) {
							ChattingController.toChatting(pc, String.format("자동낚시는 %d레벨 이상 가능합니다.", Lineage.auto_fish_level), Lineage.CHATTING_MODE_MESSAGE);
							return true;
						}
						
						if (pc.isFishing())
							FishingController.isAutoFishing(pc);
						else
							ChattingController.toChatting(pc, "낚시가 진행중일 경우 자동낚시가 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "창설")) {
					if (o != null && !o.isDead() && !o.isLock() && !o.isWorldDelete()) {
						PcInstance pc = (PcInstance) o;
						
						try {
							String clan_name = st.nextToken();
							String check = "ㅂㅈㄷㄱㅅㅁㄴㅇㄹㅎㅋㅌㅊㅍㅛㅕㅑㅐㅔㅗㅓㅏㅣㅠㅜㅡㅄㄳㄻㄿㄼㄺㄽㅀ!@#$%^&*()~_-+=|\\<>,.?/[]{};:'\"`";
			
							// 혈맹이름 체크						
							for (int i = 0; i < check.length(); ++i) {
								char comVal = check.charAt(i);
								for (int j = 0; j < clan_name.length(); ++j) {
									if (comVal == clan_name.charAt(j)) {
										// 생성불가능한 이름이 존재하므로 true
										ChattingController.toChatting(pc, "사용할 수 없는 혈맹명 입니다.", Lineage.CHATTING_MODE_MESSAGE);
										return true;
									}
								}
							}
							
							if (pc.getClanId() == 0 && clan_name != null) {
								if (pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
									if (pc.getLevel() >= Lineage.CLAN_MAKE_LEV) {
										if (clan_name.length() >= Lineage.CLAN_NAME_MIN_SIZE && clan_name.length() <= Lineage.CLAN_NAME_MAX_SIZE) {
											if (Lineage.server_version > 200) {
												if (pc.getInventory().isAden("아데나",30000, true)) {
													ClanController.toCreate(pc, clan_name);
												} else {
													// \f1아데나가 충분치 않습니다.
													pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
												}
											} else {
												if (pc.getInventory().isAden("아데나",30000, true)) {
													ClanController.toCreate(pc, clan_name);
												} else {
													// \f1아데나가 충분치 않습니다.
													pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
												}
											}
										} else {
											// 98 \f1혈맹이름이 너무 깁니다.
											pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 98));
										}
									} else {
										// 233 \f1레벨 5 이하의 군주는 혈맹을 만들 수 없습니다.
										pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 233));
									}
								} else {
									// 85 \f1왕자와 공주만이 혈맹을 창설할 수 있습니다.
									pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 85));
								}
							} else {
								// 86 \f1이미 혈맹을 창설하였습니다.
								pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 86));
							}
						} catch (Exception e) {
							ChattingController.toChatting(pc, Lineage.command + "창설 혈맹명", Lineage.CHATTING_MODE_MESSAGE);
						}					
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "대미지") || key.equalsIgnoreCase(Lineage.command + "데미지")) {
					o.setDamageMassage(o.isDamageMassage() ? false : true);
					ChattingController.toChatting(o, String.format("허수아비 대미지 멘트: %s활성화", o.isDamageMassage() ? "비" : ""), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "던전")) {
					PcInstance pc = (PcInstance) o;
					String dungeon = null;
					
					if (pc.getGiran_dungeon_time() > 0)
						dungeon = "기란감옥";
					if (pc.getGiran_dungeon_time() > 0) {
					    int dungeonTimeInSeconds = pc.getGiran_dungeon_time();
					    final int SECONDS_PER_HOUR = 3600;
					    final int SECONDS_PER_MINUTE = 60;

					    int hours = dungeonTimeInSeconds / SECONDS_PER_HOUR;
					    int remainingSecondsAfterHours = dungeonTimeInSeconds % SECONDS_PER_HOUR;
					    int minutes = remainingSecondsAfterHours / SECONDS_PER_MINUTE;
					    int seconds = remainingSecondsAfterHours % SECONDS_PER_MINUTE;

					    String formattedTime;
					    if (hours > 0) {
					        formattedTime = String.format("%s: %d시간 %d분 %d초", dungeon, hours, minutes, seconds);
					    } else if (minutes > 0) {
					        formattedTime = String.format("%s: %d분 %d초", dungeon, minutes, seconds);
					    } else {
					        formattedTime = String.format("%s: %d초", dungeon, seconds);
					    }

					    ChattingController.toChatting(o, formattedTime, Lineage.CHATTING_MODE_MESSAGE);
					} else {
					    ChattingController.toChatting(o, "던전 이용시간을 모두 사용하셨습니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
						
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "주사위")) {
					int effect = 0;
					
					switch((int)(Math.random() * 5 +1)){
					
					case 1:
						effect = 3204;
						break;
					case 2:
						effect = 3205;
						break;
					case 3:
						effect = 3206;
						break;
					case 4:
						effect = 3207;
						break;
					case 5:
						effect = 3208;
						break;
					}
					
					o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect), true);
			
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "전투멘트")) {
					o.setWarMessage(o.isWarMessage() ? false : true);
					ChattingController.toChatting(o, String.format("전투멘트: %s활성화", o.isWarMessage() ? "비" : ""), Lineage.CHATTING_MODE_MESSAGE);
					return true;

//				} else if (key.contains(Lineage.command + "후원")) {
//					List<String> list = new ArrayList<String>();
//					
//					list.add(ServerReloadDatabase.manager_kakao_talk);
//					list.add(ServerReloadDatabase.manager_nate_on);
//					// 후원 안내
//					o.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), o, "donation", null, list));
//					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "인벤") || key.equalsIgnoreCase(Lineage.command + "인벤정리")) {
					try {
						if (o.getInventory() != null) {
							inventorySetting(o);
							ChattingController.toChatting(o, "인벤토리 정리 완료.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} catch (Exception e) {
						ChattingController.toChatting(o, "인벤토리 정리 실패.", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + Lineage.command)) {
					try {
						PcInstance pc = (PcInstance) o;
						pc.칼렉풀기();
					} catch (Exception e) {
						
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "시세")) {
					((PcInstance) o).marketPrice.clear();
					String itemName =  st.nextToken();
					int index = 1;
					List<String> list = new ArrayList<String>();
					List<String> tempMsg = new ArrayList<String>();
					int en = 0;
					int bless = 1;

					tempMsg.add(itemName);
					list.add((bless == 0 ? "(축) " : bless == 2 ? "(저주) " : "") + (en > 0 ? "+" + en + " " : en < 0 ? en + " " : "") + itemName);

					for(PcShopInstance psi : PcMarketController.shop_list.values()){
						if (psi.getX() > 0 && psi.getY() > 0 && psi.getPc_objectId() != psi.getObjectId()) {
							for(PcShop s : psi.list.values()){
								if (s.getItem() == null)
									continue;
								 if (index > 60) { // 보낼 아이템 개수가 10개를 초과하면 더 이상 추가하지 않음
							            break;
							        }
							        
								if (s.getItem().getName().contains(itemName)) {
									marketPrice mp = new marketPrice();
									StringBuffer sb = new StringBuffer();

									sb.append(String.format("%d. %s", index++, s.getInvItemBress() == 0 ? "(축) " : s.getInvItemBress() == 1 ? "" : "(저주) "));

									if (s.getInvItemEn() > 0)
										sb.append(String.format("+%d ", s.getInvItemEn()));

									sb.append(String.format("%s", s.getItem().getName()));

									if (s.getInvItemCount() > 1)
										sb.append(String.format("(%d)", s.getInvItemCount()));
									
					
									sb.append(String.format(" [판매 금액]: %s 아데나", Util.changePrice(s.getPrice())));
							
							
									list.add(sb.toString());

									mp.setShopNpc(psi);
									mp.setX(psi.getX());
									mp.setY(psi.getY());
									mp.setMap(psi.getMap());
									mp.setObjId(psi.getObjectId());
									((PcInstance) o).marketPrice.add(mp);
								}
							}
						} else {
							continue;
						}
					}
					
			
					((PcInstance) o).PcMarket_Step = 0;
					if (list.size() < 2 || ((PcInstance) o).marketPrice.size() < 1) {
		
						((PcInstance) o).toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), PcMarketController.marketPriceNPC, "marketprice1", null, list));
						((PcInstance) o).PcMarket_Step = 0;
						return true;
					} else {
						int count = 60 - (list.size() - 1);
						for (int i = 0; i < count; i++)
							list.add(" ");				
						// 시세 검색 결과 html 패킷 보냄.
						((PcInstance) o).PcMarket_Step = 0;
						((PcInstance) o).toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), PcMarketController.marketPriceNPC, "marketprice", null, list));
					}
			
					return true;
		
		
				} else if (key.equalsIgnoreCase(Lineage.command + "자동물약")) {
					if (o instanceof PcInstance)
						NpcSpawnlistDatabase.autoPotion.toTalk((PcInstance) o, null);
					
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "계좌")) {
					PcTradeController.insertInfo(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "계좌확인")) {
					PcTradeController.enterInfo(o);
					return true;

				} else if (key.equalsIgnoreCase(Lineage.command + "거래게시판")) {
					PcTradeController.viewBoard(o);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "판매등록")) {
					PcTradeController.insertItem(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "판매취소")) {
					PcTradeController.toDelete(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "판매목록")) {
					PcTradeController.saleList(o);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "구매신청")) {
					PcTradeController.buyItem(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "구매정보")) {
					PcTradeController.buyTradeInfo(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "구매취소")) {
					PcTradeController.buyCancel(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "구매목록")) {
					PcTradeController.buyList(o);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "입금완료")) {
					PcTradeController.depositComplete(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "입금확인")) {
					PcTradeController.tradeComplete(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "칼질")) {
					if (o.isAutoAttack)
						o.isAutoAttack = false;
					else
						o.isAutoAttack = true;
					
					ChattingController.toChatting(o, String.format("[자동공격: %s]", o.isAutoAttack ? "활성화" : "비활성화"), Lineage.CHATTING_MODE_MESSAGE);
					return true;

				} else if (key.equalsIgnoreCase(Lineage.command + "스왑") || key.equalsIgnoreCase(Lineage.command + "장비스왑")) {
					swap(o);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "교환")) {
					trade(o, st);
	
				return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "매크로")) {
					macro(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "포인트")) {
						
					포인트(o);
			
					return true;

				} else if (key.equalsIgnoreCase(Lineage.command + "이펙") || key.equalsIgnoreCase(Lineage.command + "이펙트")) {
					이펙트(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "무인혈맹")) {
					if (o instanceof PcInstance) {
						PcInstance pc = (PcInstance) o;
						RobotClanController.insert(pc);
					}				
					return true;
			
//				} else if (key.equalsIgnoreCase(Lineage.command + "고정신청")) {
//					고정신청(o, st);
//					return true;

				}
			}


			// 운영자 명령어 확인하기.
			if (o.getGm() > 0 ) {
//				if(!o.getName().equalsIgnoreCase("메티스")){
//					return true;
//				}
				if (key.equalsIgnoreCase(Lineage.command + "영자명령어")) {
					ChattingController.toChatting(o, "\\fY----------------- 운영자 명령어 안내 ------------------", Lineage.CHATTING_MODE_MESSAGE);
					String c = Lineage.command;
					ChattingController.toChatting(o, c + "확인|" + c + "고정|" + c + "선물|" + c + "전체선물|" + c + "아이템|" + c + "귀환|" + c + "이동", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, c + "영자마크|" + c + "몬스터|" + c + "소환|" + c + "출두|" + c + "올버프|" + c + "스킬올마|" + c + "라우풀", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, c + "채금|" + c + "채금해제|" + c + "전체채금해제|" + c + "청소|" + c + "만피|" + c + "만엠|" + c + "올피", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, c + "올엠|" + c + "부활|" + c + "스텔스|" + c + "투망|" + c + "레벨|" + c + "귓말|" + c + "인벤검색|" + c + "몹정리", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, c + "차단|" + c + "차단해제|" + c + "리로드|" + c + "검색|" + c + "정보|" + c + "상점삭제|" + c + "데미지확인", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, c + "날씨|" + c + "투견|" + c + "스팟|" + c + "테베|" + c + "악영|" + c + "지옥|" + c + "얼던|" + c + "피뻥|" + c + "엠뻥|" + c + "채창", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, c + "변신|" + c + "유저변신|" + c + "변신해제|" + c + "스핵해제|" + c + "스핵제외|" + c + "스핵체크", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, c + "창고검색|" + c + "인벤검색|" + c + "템검색", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, c + "좌표복구," + c + "몬스터회피," + c + "경험치획등량", Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "맵") || key.equalsIgnoreCase(Lineage.command + "확인")) {
					ChattingController.toChatting(o, String.format("X좌표: [%d] Y좌표: [%d] 맵번호: [%d]", o.getX(), o.getY(), o.getMap()), Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, String.format("gfx: [%d] gfx모드: [%d] 헤딩: [%d]", o.getGfx(), o.getGfxMode(), o.getHeading()), Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, String.format("타일값: [%d] 객체 수: [%d] 타일객체여부: [%s]", 
									World.get_map(o.getX(), o.getY(), o.getMap()), World.getMapdynamic(o.getX(), o.getY(), o.getMap()), 
									World.isMapdynamic(o.getX(), o.getY(), o.getMap()) == false ? "false" : "true"), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "위치") || key.equalsIgnoreCase(Lineage.command + "좌표")) {
					ChattingController.toChatting(o, String.format("X좌표: [%d] Y좌표: [%d] 맵번호: [%d]", o.getX(), o.getY(), o.getMap()), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "웰던개방해") || key.equalsIgnoreCase(Lineage.command + "웰던개방")) {
					Lineage.is_welldone_teleport = true;
					ChattingController.toChatting(o, "웰던 지역 텔레포트가 개방되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "웰던봉인해") || key.equalsIgnoreCase(Lineage.command + "웰던봉인")) {
					Lineage.is_welldone_teleport = false;
					ChattingController.toChatting(o, "웰던 지역 텔레포트가 봉인되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "하이네개방해") || key.equalsIgnoreCase(Lineage.command + "하이네개방")) {
					Lineage.is_heine_teleport = true;
					ChattingController.toChatting(o, "하이네 지역 텔레포트가 개방되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "하이네봉인해") || key.equalsIgnoreCase(Lineage.command + "하이네봉인")) {
					Lineage.is_heine_teleport = false;
					ChattingController.toChatting(o, "하이네 지역 텔레포트가 봉인되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "아덴개방해") || key.equalsIgnoreCase(Lineage.command + "아덴개방")) {
					Lineage.is_aden_teleport = true;
					ChattingController.toChatting(o, "아덴 지역 텔레포트가 개방되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "아덴봉인해") || key.equalsIgnoreCase(Lineage.command + "아덴봉인")) {
					Lineage.is_aden_teleport = false;
					ChattingController.toChatting(o, "아덴 지역 텔레포트가 봉인되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "영토개방상태")) {
					ChattingController.toChatting(o, String.format("웰던:%s 하이네:%s 아덴:%s",
							Lineage.is_welldone_teleport ? "개방" : "봉인",
							Lineage.is_heine_teleport ? "개방" : "봉인",
							Lineage.is_aden_teleport ? "개방" : "봉인"), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "봇")) {
					String name = st.nextToken();
					PcInstance pc = World.findPc(name);
					
					if (pc instanceof PcRobotInstance) {
						PcRobotInstance pi = (PcRobotInstance) pc;
						ChattingController.toChatting(o, String.format("ID:%s 상태:%s[%d] 공격리스트:%d  주위공격리스트:%d", 
								pi.getName(), pi.pcrobot_mode, pi.getAiStatus(), pi.getAttackListSize(), pi.getInsideListAttackCount()), Lineage.CHATTING_MODE_MESSAGE);
					}
					
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "콜롯세움")) {
					try {
						String msg = st.nextToken();

						if (msg.equalsIgnoreCase("사용")) {
							Lineage.colosseum_giran = true;
						} else if (msg.equalsIgnoreCase("중지")) {
							Lineage.colosseum_giran = false;
						}

						ChattingController.toChatting(o, String.format("기란 콜롯세움  : %s", Lineage.colosseum_giran ? "사용" : "중지"), Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "콜롯세움 사용 /중지", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "테베시작")) {
					Thebes.getInstance().테베강제시작(true);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "테베종료")) {
					Thebes.getInstance().테베강제종료(true);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "전체삭제")) {
					try {
						인벤전체삭제(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, "[서버알림] " + Lineage.command + "전체삭제 [캐릭명] [아이템이름] [갯수] [인챈트] [축/저주]", 20);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "인벤삭제")) {
					try {
						인벤삭제(o, st);
					} catch (Exception localException61) {
						ChattingController.toChatting(o, Lineage.command + "인벤삭제 chaname", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "고정")) {
					try {
						String name = st.nextToken();
						PcInstance pc = World.findPc(name);
						
						if (pc != null) {
							if (pc.isMember()) {
								ChattingController.toChatting(o, "이미 고정등록 완료된 계정", Lineage.CHATTING_MODE_MESSAGE);
								return true;
							}
							pc.setMember(true);
							ChattingController.toChatting(o, String.format("%s 계정 고정멤버 등록 완료", pc.getAccountId()), Lineage.CHATTING_MODE_MESSAGE);
							ChattingController.toChatting(pc, String.format("%s 계정 고정멤버 등록 완료", pc.getAccountId()), Lineage.CHATTING_MODE_MESSAGE);
							pc.고정멤버버프(true);
						} else {
							CharactersDatabase.isCharacterMember(o, name);
						}
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "고정 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "선물")) {
					try {
						PcInstance pc = World.findPc(st.nextToken());
						if (pc != null) {
							if (pc.getInventory() != null)
								toGiveItem(o, pc, st);
						} else
							ChattingController.toChatting(o, "해당 캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "선물 아이템 갯수 인챈 bless", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "포인트지급")) {
					
					try {
						PcInstance pc = World.findPc(st.nextToken());
						if (pc != null) {
							      포인트지급(o, pc, st);
						} else
							ChattingController.toChatting(o, "해당 캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "포인트지급 캐릭터닉네임 포인트", Lineage.CHATTING_MODE_MESSAGE);
					}
			
					return true;
					
					
				} else if (key.equalsIgnoreCase(Lineage.command + "포인트회수")) {
					try {
						PcInstance pc = World.findPc(st.nextToken());
						if (pc != null) {
					
							      포인트회수(o, pc, st);
						} else
							ChattingController.toChatting(o, "해당 캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "포인트회수 캐릭터닉네임 포인트", Lineage.CHATTING_MODE_MESSAGE);
					}
			
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "메모리정리")) {
					
					try {
						 Runtime.getRuntime().gc();
						 ChattingController.toChatting(o, "메모리가 정리 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
					
					}
					return true;
					
				} else if (key.equalsIgnoreCase(Lineage.command + "전체선물")) {
					try {
						toAllGiveItem(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "전체선물 아이템 갯수 인첸 bless", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "오픈대기")) {
					serverOpenWait();
					ChattingController.toChatting(o, "오픈대기 완료", Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "오픈")) {
					serverOpen();
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "귀환")) {
					귀환(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "타임")) {
					ServerDatabase.LineageWorldTime = Integer.valueOf(cmd);

					o.toSender(S_WorldTime.clone(BasePacketPooling.getPool(S_WorldTime.class)));
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "라우풀")) {
					try {
						PcInstance pc = null;
						int lawful = Integer.valueOf(st.nextToken()) + 65536;
						if (st.hasMoreTokens())
							pc = World.findPc(st.nextToken());
						if (pc == null) {
							pc = (PcInstance) o;
						}
						pc.setLawful(lawful);
						return true;
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "라우풀 수치 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "이동")) {
					try {
						int x = Integer.valueOf(st.nextToken());
						int y = Integer.valueOf(st.nextToken());
						int map = Integer.valueOf(st.nextToken());
						o.toTeleport(x, y, map, true);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "이동 [X좌표 Y좌표 맵번호]", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "변신")) {
					try {
						int gfx = 1080;
						
						if (st.hasMoreTokens())
							gfx = Integer.valueOf(st.nextToken());
						
						o.setGfx(gfx);
						o.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), o), true);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "변신 [변신번호] 메티스: 1080", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "개경")) {
					try {
						toDogGameControl(o, st);
					} catch (Exception localException60) {
						ChattingController.toChatting(o, new StringBuilder().append(TOKEN).append("개경 [시작,종료]").toString(), 20);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "모드")) {
					try {
						int gfxmode = Integer.valueOf(st.nextToken());
						o.setGfxMode(gfxmode);
						o.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), o), true);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "모드 모드번호", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "액션")) {
					try {
						int action = Integer.valueOf(st.nextToken());
						o.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), o, action), true);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "액션 액션번호", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "이팩트")) {
					try {
						int effect = Integer.valueOf(st.nextToken());
						o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect), true);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "이팩트 이팩트번호", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "유저변신")) {
					try {
						PcInstance pc = World.findPc(st.nextToken());
						int gfx = Integer.valueOf(st.nextToken());
						pc.setGfx(gfx);
						pc.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), pc), true);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "유저변신 유저이름 변신번호", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "몬스터")) {
					try {
						toMonster(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "몬스터 한글네임 count", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "아이템")) {
					try {
						toItem(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "아이템 한글네임 count enlevel bress 속성종류[1,2,3,4] 1~5단  인형옵션[1,2,3,4,5] 수치",
								Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "소환")) {
					try {
						toCall(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "소환 name", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;

				} else if (key.equalsIgnoreCase(Lineage.command + "출두")) {
					try {
						toGo(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "출두 name", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "버프")) {
					try {
						toBuff(o, st);
						return true;
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "버프 name", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "올버프")) {
					try {
						toBuffAll(o);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "올버프", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "테스트")) {
					try {
						
			
						    BackgroundInstance shockStun = new lineage.world.object.npc.background.ShockStun();
						    
						    shockStun.clearList(true);
						    World.remove(shockStun);
				
									
					} catch (Exception e) {
					
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "차단")) {
					try {
						toBan(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "차단 캐릭터", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "차단해제")) {
					try {
						toBanRemove(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "차단해제 캐릭터", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "전체차단해제")) {
					toBanAllRemove(o);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "스킬올마")) {
					try {
						toSkillAllMaster(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "스킬올마 name", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "채금")) {
					try {
						toChattingClose(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "채금 name time", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				}else if (key.equalsIgnoreCase(Lineage.command + "서버통계")) {
					try {
					String itemname = st.nextToken();
					int en = Integer.parseInt(st.nextToken());

					Item item = ItemDatabase.find2(itemname);
					if (item == null) {
					    ChattingController.toChatting(o, "존재하지 않는 아이템입니다.", Lineage.CHATTING_MODE_MESSAGE);
					    return true;
					}

					Connection con = null;
					PreparedStatement stt = null;
					ResultSet rs = null;

					try {
					    con = DatabaseConnection.getLineage();
					    stt = con.prepareStatement("SELECT name, en, COUNT(*) AS item_count FROM characters_inventory WHERE name = ? AND en = ? AND cha_name != '메티스' GROUP BY name, en");
					    stt.setString(1, item.getName());
					    stt.setInt(2, en);
					    rs = stt.executeQuery();
					    
					    StringBuilder Str = new StringBuilder();
					    Str.append("\n\n");
					    Str.append("");
					    Str.append("\n\n");

					    if (rs.next()) {
					        do {
					            String name = rs.getString("name");
					            int enchantment = rs.getInt("en");
					            int count = rs.getInt("item_count");
					            String message = name + " (+" + enchantment + "): " + count + "개";
					            Str.append(message);
					            Str.append("\n");
					        } while (rs.next());
					    } else {
					        Str.append("아이템 통계 반영하는데 다소 시간이 걸립니다.");
					    }

					    // 메시지 출력
					    String[] arrayOfString = new String[4];
					    arrayOfString[0] = ServerDatabase.getName();
					    arrayOfString[1] = "* 인첸트 통계 *";
					    arrayOfString[2] = Str.toString();
					    o.toSender(S_LetterNotice.clone(BasePacketPooling.getPool(S_LetterNotice.class), arrayOfString));
					
					} catch (SQLException e) {
					    lineage.share.System.println("아이템 찾기 오류: " + e.getMessage());
					
					}finally {
					    DatabaseConnection.close(con, stt, rs);
					}
					} catch (Exception e) {
					    ChattingController.toChatting(o, Lineage.command + "서버통계 name 인챈트", Lineage.CHATTING_MODE_MESSAGE);
				
					
					}
				    return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "채금해제")) {
					try {
						toChattingCloseRemove(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "채금해제 name", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "전체채금해제")) {
					toChattingCloseAllRemove(o);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "청소")) {
					try {
						toWorldItemClear(o);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "청소", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "셧다운")) {
					try {
						toShutdown(o, st);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "셧다운 초", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;

				} else if (key.equalsIgnoreCase(Lineage.command + "몹정리")) {
					try {
						toClearMonster(o);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "몹정리", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "부활")) {
					try {
						String name = o.getName();
						
						while (st.hasMoreTokens())
							name = st.nextToken();

						PcInstance pc = World.findPc(name);
						
						if (pc != null) {
							pc.toRevival(o);
							
							if (o != null)
								ChattingController.toChatting(o, name + " 부활 완료.", Lineage.CHATTING_MODE_MESSAGE);
						} else {
							if (o != null)
								ChattingController.toChatting(o, name + " 캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "부활", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "올부활")) {
					try {
						for (PcInstance pc : World.getPcList())
							pc.toRevival(o);
						
						if (o != null)
							ChattingController.toChatting(o, "올부활 완료.", Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "올부활", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "죽기")) {
					try {
						o.setNowHp(0);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "죽기", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "전체소환")) {
					try {
						for (PcInstance pc : World.getPcList())
							pc.toTeleport(o.getX(), o.getY(), o.getMap(), true);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "전체소환", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "현상수배초기화")) {
					WantedController.clear();
					ChattingController.toChatting(o, "초기화 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "창고검색")) {
					try {
						String name = st.nextToken();

						PcInstance use = World.findPc(name);
						PcInstance pc = (PcInstance) o;
						List<String> list = new ArrayList<String>();	
						Npc n = NpcDatabase.find("보여주기상점");
						n.getShop_list().clear();
						list.add(name);
						if (use != null) {
							int idx = 0;
								List<Warehouse> list1 = WarehouseDatabase.getList(use.getClient().getAccountUid(), Lineage.DWARF_TYPE_NONE);
								for (Warehouse wh : list1){
									if (list1.size() >= 250)
										
									break;
									
									n.getShop_list().add(new Shop(wh.getName(), wh.getEn(),1,(int) wh.getCount(), wh.getBress()));										
									n.getShop_list().get(idx).setUid(idx);
							}
						}
						
						NpcSpawnlistDatabase.보여주기상점.toTalk(pc, null);	
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "창고검색 캐릭터", Lineage.CHATTING_MODE_MESSAGE);
						
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "인벤검색")) {
					try {
						String name = st.nextToken();
						int i = Integer.parseInt(st.nextToken());
						PcInstance use = World.findPc(name);
						PcInstance pc = (PcInstance) o;
						List<String> list = new ArrayList<String>();	
						Npc n = NpcDatabase.find("보여주기상점");
						n.getShop_list().clear();
						list.add(name);
					
						
						if (use != null) {
							int idx = 0;
							
							
							switch (i) {
							case 0:
									for (ItemInstance ii : use.getInventory().getList()){
										if (list.size() >= 250)
										break;

										n.getShop_list().add(new Shop(ii.getItem().getName(), ii.getEnLevel(),1,(int) ii.getCount(), ii.getBless()));										
										n.getShop_list().get(idx).setUid(idx);
			
									}
								break;
							case 1:
							
								for (ItemInstance ii : use.getInventory().getList()){
									if (list.size() >= 250)
									break;
									
									if(!ii.getItem().getType1().equalsIgnoreCase("weapon") && !ii.getItem().getType1().equalsIgnoreCase("armor")){
										n.getShop_list().add(new Shop(ii.getItem().getName(), ii.getEnLevel(),1,(int) ii.getCount(), ii.getBless()));										
										n.getShop_list().get(idx).setUid(idx);
									}
								}
								
								break;
							case 2:
								for (ItemInstance ii : use.getInventory().getList()){
									if (list.size() >= 250)
									break;
									
									if(ii.getItem().getType1().equalsIgnoreCase("weapon")){
										n.getShop_list().add(new Shop(ii.getItem().getName(), ii.getEnLevel(),1,(int) ii.getCount(), ii.getBless()));										
										n.getShop_list().get(idx).setUid(idx);
									}
								}
								break;
							case 3:
								for (ItemInstance ii : use.getInventory().getList()){
									if (list.size() >= 250)
									break;
									
									if(ii.getItem().getType1().equalsIgnoreCase("armor")){
										n.getShop_list().add(new Shop(ii.getItem().getName(), ii.getEnLevel(),1,(int) ii.getCount(), ii.getBless()));										
										n.getShop_list().get(idx).setUid(idx);
									}
								}
								break;
							}
							NpcSpawnlistDatabase.보여주기상점.toTalk(pc, null);	
			
						} else {
							ChattingController.toChatting(o, "'" + name + "' 사용자가 접속하고 있지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
						
		
						
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "인벤검색 캐릭터 [0~2]", Lineage.CHATTING_MODE_MESSAGE);
						
						ChattingController.toChatting(o, "0=전부, 1=잡템만, 2=무기 , 3= 갑옷",  Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "템검색")) {
					try {
						String name = st.nextToken();
						PcInstance use = World.findPc(name);
						PcInstance pc = (PcInstance) o;
						List<String> list = new ArrayList<String>();	
						Npc n = NpcDatabase.find("보여주기상점");
						n.getShop_list().clear();
						list.add(name);
					
						
						if (use != null) {
							int idx = 0;
							for (ItemInstance ii : use.getInventory().getList()){
								if (list.size() >= 250)
									break;
									if(ii.isEquipped()){
										n.getShop_list().add(new Shop(ii.getItem().getName(), ii.getEnLevel(),1,(int) ii.getCount(), ii.getBless()));										
										n.getShop_list().get(idx).setUid(idx);
									}
					
			
				
							
							}
							NpcSpawnlistDatabase.보여주기상점.toTalk(pc, null);	
			
						} else {
							ChattingController.toChatting(o, "'" + name + "' 사용자가 접속하고 있지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
						
		
						
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "템검색 캐릭터", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "검색")) {
					try {
						toSearchItem(o, st);
					} catch (Exception localException78) {
						ChattingController.toChatting(o, Lineage.command + "검색 [0~2] [이름]을 입력 해 주세요.", Lineage.CHATTING_MODE_MESSAGE);
						ChattingController.toChatting(o, "0=잡템, 1=무기, 2=갑옷",  Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "버프제거")) {
					
					toBuffremove(o);
							
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "만피")) {
					try {
						String name = o.getName();
						while (st.hasMoreTokens()) {
							name = st.nextToken();
						}
						PcInstance use = World.findPc(name);
						if (use != null)
							use.setNowHp(use.getTotalHp());
						else
							ChattingController.toChatting(o, "캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "만피 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
					
				} else if (key.equalsIgnoreCase(Lineage.command + "만엠")) {
					try {
						String name = o.getName();
						while (st.hasMoreTokens()) {
							name = st.nextToken();
						}
						PcInstance use = World.findPc(name);
						if (use != null)
							use.setNowMp(use.getTotalMp());
						else
							ChattingController.toChatting(o, "캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "만엠 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "올피")) {
					try {
						for (PcInstance pc : World.getPcList())
							pc.setNowHp(pc.getTotalHp());
						
						ChattingController.toChatting(o, "올피 완료.", Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "올피", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "올엠")) {
					try {
						for (PcInstance pc : World.getPcList())
							pc.setNowMp(pc.getTotalMp());
						
						ChattingController.toChatting(o, "올엠 완료.", Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "올엠", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "영자모드")) {
					if (0 < o.getGm())
						o.setGm(0);
					else
						o.setGm(99);
					
					ChattingController.toChatting(o, String.format("운영자 모드 %s활성화", o.getGm() > 0 ? "" : "비"), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "스텔스")) {
					if (o.isTransparent()) {
						o.setGfx(1080);
						o.setTransparent(false);
					} else {
						o.setGfx(1080);
						o.setTransparent(true);
					}
					
					o.toTeleport(o.getX(), o.getY(), o.getMap(), false);
					ChattingController.toChatting(o, String.format("스텔스 모드 %s활성화", o.isTransparent() ? "" : "비"), Lineage.CHATTING_MODE_MESSAGE);
					return true;
//				} else if (key.equalsIgnoreCase(Lineage.command + "공성")) {
//					setKingdomWar();
//					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "공성체크")) {
					try {
						toKingdomWarCheck(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "공성체크", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;

				} else if (key.equalsIgnoreCase(Lineage.command + "공성시작")) {
					try {
						toKingdomWarStart(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "공성종료 [성이름] ", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;

					//
				} else if (key.equalsIgnoreCase(Lineage.command + "공성종료")) {
					try {
						toKingdomWarStop(o, st);
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "공성종료 [성이름]", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "전체메세지")) {
					try {
						StringBuffer msg = new StringBuffer();
						
						while (st.hasMoreTokens())
							msg.append(st.nextToken() + " ");
						
						for (PcInstance pc : World.getPcList())
							pc.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, "\\fR [******] "+ msg.toString()));
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "전체메세지 메세지", Lineage.CHATTING_MODE_MESSAGE);
					}	
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "투망")) {
					if (o.isInvis()) {
						o.setInvis(false);
						o.setBuffInvisiBility(false);	
					} else {
						o.setInvis(true);
						o.setBuffInvisiBility(true);
					}
					
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "레벨")) {
					 try {
						 String pcName = st.nextToken();
						 PcInstance pc = null;
						 String temp = st.nextToken();
						 int level = Integer.valueOf(temp.substring(0, temp.indexOf(".")));
						 double tempExp = Integer.valueOf(temp.substring(temp.indexOf(".") + 1));
						 double exp = 0;
						 
						 if (World.findPc(pcName) == null) {
							 ChattingController.toChatting(o, "해당 캐릭터는 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
							 return true;
						 } else {
							 pc = World.findPc(pcName);
						 }
						 
						 if (tempExp >= 99)
							 tempExp = 99;
						 
						 tempExp *= 0.01;
						 
						Exp e = ExpDatabase.find(level - 1);
						exp = e.getBonus();
						tempExp *= ExpDatabase.find(level).getExp();

						if (pc.getExp() > exp + tempExp) {
							if (e.getLevel() < pc.getLevel()) {							
								for (int i = pc.getLevel(); i > e.getLevel(); i--) {
									// hp & mp 하향.
									pc.setMaxHp( pc.getMaxHp()-CharacterController.toStatusUP(pc, true) );
									pc.setMaxMp( pc.getMaxMp()-CharacterController.toStatusUP(pc, false) );
								}
				
								pc.setLevel(e.getLevel());
								pc.setExp(exp + tempExp);
							} else {
								pc.setExp(exp + tempExp);
							}
						} else {
							pc.setExp(exp + tempExp);
						}

						 ChattingController.toChatting(o, String.format("%s 캐릭터 %s 설정 완료.", pcName, temp), Lineage.CHATTING_MODE_MESSAGE);
						// 패킷 처리.
						pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
	                    } catch (Exception e) {
							if(o!=null)
	                            ChattingController.toChatting(o, Lineage.command + "레벨 캐릭명 52.80", Lineage.CHATTING_MODE_MESSAGE);
	                    }
						return true;			
				} else if (key.equalsIgnoreCase(Lineage.command + "스턴")) {
					 try {
						 String pcName = st.nextToken();
						 int count = Integer.valueOf(st.nextToken());
						 PcInstance pc = null;
						 
						 if (World.findPc(pcName) == null) {
							 ChattingController.toChatting(o, "해당 캐릭터는 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
							 return true;
						 } else {
							 pc = World.findPc(pcName);
							 
							 FrameSpeedOverStun.init(pc, count);
							 ChattingController.toChatting(o, String.format("[%s] %d초 스턴 완료.", pcName, count), Lineage.CHATTING_MODE_MESSAGE);
							 ChattingController.toChatting(pc, "불법적인 행위로 운영자로 부터 스턴을 당하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
						 }								
	                    } catch (Exception e) {
							if(o!=null)
	                            ChattingController.toChatting(o, Lineage.command + "스턴 캐릭명 시간(초)", Lineage.CHATTING_MODE_MESSAGE);
	                    }
						return true;	
				} else if (key.equalsIgnoreCase(Lineage.command + "턴")) {
					try {
						String pcName = st.nextToken();
						int count = Integer.valueOf(st.nextToken());
						PcInstance pc = null;
						Skill skill = SkillDatabase.find(16);
						if (World.findPc(pcName) == null) {
							ChattingController.toChatting(o, "해당 캐릭터는 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return true;
						} else {
							pc = World.findPc(pcName);

							ShockStun.init(pc, skill,(int) pc.getObjectId());
							ChattingController.toChatting(o, String.format("[%s] %d초 스턴 완료.", pcName, count), Lineage.CHATTING_MODE_MESSAGE);
							ChattingController.toChatting(pc, "불법적인 행위로 운영자로 부터 스턴을 당하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "스턴 캐릭명 시간(초)", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "귓말")) {
					if (o.isGmWhisper)
						o.isGmWhisper = false;
					else
						o.isGmWhisper = true;

					ChattingController.toChatting(o, String.format("운영자 귓말 '%s' 완료", o.isGmWhisper ? "끔" : "켬"), Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "스핵해제")) {
					try {
						String pcName = st.nextToken();
						PcInstance pc = null;

						if (World.findPc(pcName) == null) {
							ChattingController.toChatting(o, String.format("[%s] 캐릭터는 존재하지 않습니다.", pcName), Lineage.CHATTING_MODE_MESSAGE);
							return true;
						} else {
							pc = World.findPc(pcName);
						}

						BuffController.remove(pc, FrameSpeedOverStun.class);

						ChattingController.toChatting(o, String.format("[%s] 캐릭터 스핵해제 완료.", pcName), Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "스핵해제 캐릭터", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "팀대전종료")) {
					if (TeamBattleController.startTeamBattle) {
						ChattingController.toChatting(o, String.format("[팀대전 종료] A팀 점수: %d / B팀 점수: %d", TeamBattleController.A_TeamScore, TeamBattleController.B_TeamScore), Lineage.CHATTING_MODE_MESSAGE);
						TeamBattleController.endTeamBattle(0);
					} else
						ChattingController.toChatting(o, "팀대전 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "리로드")) {
					try {
						reload(o, st);
					} catch (Exception e) {
						if (o != null) {
							if (o != null) {
								ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ  리로드  ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
								ChattingController.toChatting(o, "[ 엔피씨, 변신, 스킬, 상점, 보스]", Lineage.CHATTING_MODE_MESSAGE);
								ChattingController.toChatting(o, "[몬스터, 몹드랍, 몹스킬, 백그라운드, 번들, 찬스번들, 서먼]", Lineage.CHATTING_MODE_MESSAGE);
								ChattingController.toChatting(o, "[공지, 서버메세지, 템스킬, 낚시, 팀대전, 타임던전, 로봇]", Lineage.CHATTING_MODE_MESSAGE);
								ChattingController.toChatting(o, "[무인혈맹, 스핵, 몹스폰, 전체스폰, 아이템메세지]", Lineage.CHATTING_MODE_MESSAGE);
								ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
							}
						}
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "상점삭제")) {
					removePcShop(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "데미지확인")) {
					try {
						String pcName = st.nextToken();
						PcInstance pc = null;

						if (World.findPc(pcName) == null) {
							ChattingController.toChatting(o, String.format("[%s] 캐릭터는 존재하지 않습니다.", pcName), Lineage.CHATTING_MODE_MESSAGE);
							return true;
						} else {			
							pc = World.findPc(pcName);
							pc.isDmgCheck = pc.isDmgCheck ? false : true;
						}

						ChattingController.toChatting(o, String.format("[%s] 캐릭터 데미지 확인 %s활성화.", pcName, pc.isDmgCheck ? "" : "비"), Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "데미지확인 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
					
				} else if (key.equalsIgnoreCase(Lineage.command + "가이드전송")) {
					BoardInstance b = BackgroundDatabase.getGuideBoard();

					for (PcInstance pc : World.getPcList()) {
						if (b != null)
							b.toClick(pc, null);
					}

					return true;
				
					
				} else if (key.equalsIgnoreCase(Lineage.command + "날씨")) {
					setWeather(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "스팟")) {
					spot(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "타일뷰어")) {
					tileViewer(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "사운드")) {
					try {
						int sound = Integer.valueOf(st.nextToken()); 
						
						o.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), sound));
					} catch (Exception e) {
						ChattingController.toChatting(o, Lineage.command + "사운드 사운드번호", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				//} else if (key.equalsIgnoreCase(Lineage.command + "테베")) {
				//	테베(o, st);
				//	return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "테베시작")) {
					Thebes.getInstance().테베강제시작(true);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "테베종료")) {
					Thebes.getInstance().테베강제종료(true);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "얼던")) {
					얼던(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "지옥")) {
					지옥(o, st);
					return true;

				} else if (key.equalsIgnoreCase(Lineage.command + "보물찾기")) {
					보물찾기(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "펭귄")) {
					펭귄(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "타임이벤")) {
					타임이벤(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "타임이벤설정")) {
					타임이벤2(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "월드보스")) {
					월드보스(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "악영")) {
					악영(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "마족")) {
					마족(o, st);
					return true;

				} else if (key.equalsIgnoreCase(Lineage.command + "피뻥")) {
					plusHp(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "엠뻥")) {
					plusMp(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "채창")) {
					chattingLock(o, st);
					return true;
				
				} else if (key.equalsIgnoreCase(Lineage.command + "변신해제")) {
					변신해제(o, st);
					return true;
				
				} else if (key.equalsIgnoreCase(Lineage.command + "스핵제외")) {
					try {
						String pcName = st.nextToken();
						PcInstance pc = World.findPc(pcName);

						if (pc == null) {
							ChattingController.toChatting(o, String.format("[%s] 캐릭터는 존재하지 않습니다.", pcName), Lineage.CHATTING_MODE_MESSAGE);
							return true;
						} else {			
							HackNoCheckDatabase.append(o, pc);
						}
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "스핵제외 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "스핵체크")) {
					try {
						String pcName = st.nextToken();
						PcInstance pc = World.findPc(pcName);

						if (pc == null) {
							ChattingController.toChatting(o, String.format("[%s] 캐릭터는 존재하지 않습니다.", pcName), Lineage.CHATTING_MODE_MESSAGE);
							return true;
						} else {			
							HackNoCheckDatabase.remove(o, pc);
						}
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "스핵체크 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "영자마크")) {
					영자마크(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "경험치구슬전체초기화")) {
					ExpMarbleController.resetCount();
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "경험치구슬초기화")) {
					try {
						String pcName = st.nextToken();
						ExpMarbleController.resetCount(o, pcName);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "경험치구슬초기화 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "몬스터회피")) {
					if (o.isMonhitdmg()){
						o.setMonhitdmg(false);
						ChattingController.toChatting(o, String.format("몬스터 회피 보기 비활성화"), Lineage.CHATTING_MODE_MESSAGE);
					}else{
						o.setMonhitdmg(true);
						ChattingController.toChatting(o, String.format("몬스터 회피 보기 활성화"), Lineage.CHATTING_MODE_MESSAGE);
					}

					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "경험치바")) {
					if (o.ismonExp()){
						o.setmonExp(false);
						ChattingController.toChatting(o, String.format("몬스터 경험치 보기 비활성화"), Lineage.CHATTING_MODE_MESSAGE);
					}else{
						o.setmonExp(true);
						ChattingController.toChatting(o, String.format("몬스터 경험치 보기 활성화"), Lineage.CHATTING_MODE_MESSAGE);
					}

					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "매크로끔")) {
					macroOff(o, st);
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "오토확인")) {
					try {
						String pcName = st.nextToken();
						AutoHuntCheckController.오토확인(o, pcName);
					} catch (Exception e) {
						if (o != null)
							ChattingController.toChatting(o, Lineage.command + "오토확인 아이디", Lineage.CHATTING_MODE_MESSAGE);
					}
					return true;
				} else if (key.equalsIgnoreCase(Lineage.command + "좌표복구")) {
					좌표복구(o, st);
					return true;
				}
			}
		} catch (Exception e) {

		}
		return false;
	}
	
	/**
	 * 화면안에있는 몬스터 정리처리하는 함수.
	 * 
	 * @param o
	 */
	public static void toClearMonster(object o) {
		o.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), o, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, 245), true);
		for (object inside_o : o.getInsideList()) {
			if (!inside_o.isDead() && inside_o instanceof MonsterInstance && inside_o.getSummon() == null && !FightController.isFightMonster(inside_o)) {
				MonsterInstance mon = (MonsterInstance) inside_o;
				DamageController.toDamage((Character) o, mon, mon.getTotalHp(), Lineage.ATTACK_TYPE_MAGIC);
			}
		}

		ChattingController.toChatting(o, "몬스터 정리 완료.", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 셧다운 처리 함수.
	 * 
	 * @param o
	 * @param st
	 */
	static private void toShutdown(object o, StringTokenizer st) {
		int delay = Integer.valueOf(st.nextToken());
		
		if (Shutdown.getInstance() != null)
			Shutdown.getInstance().is_shutdown = false;
		
		new Thread(Shutdown.getInstance(delay)).start();
	}

	/**
	 * 채금처리. : ScreenRenderComposite 에서 사용중
	 * 
	 * @param o
	 * @param st
	 */
	static public void toChattingClose(object o, StringTokenizer st) {
		PcInstance pc = World.findPc(st.nextToken());
		if (pc != null) {
			int time = Integer.valueOf(st.nextToken());
			ChattingClose.init(pc, time);

			// %0의 채팅을 금지시켰습니다.
			if (o != null)
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 287, pc.getName()));
		}
	}
	
	/**
	 * 채금 해제
	 * 2019-07-07
	 * by connector12@nate.com
	 */
	static public void toChattingCloseRemove(object o, StringTokenizer st) {
		String name = st.nextToken();
		PcInstance pc = World.findPc(name);
		
		if (pc != null)
			BuffController.remove(pc, ChattingClose.class);
		
		CharactersDatabase.chattingCloseRemove(name);
		
		if (o != null)
			ChattingController.toChatting(o, String.format("[채금 해제] 캐릭터: %s", name), Lineage.CHATTING_MODE_MESSAGE);
	}
	
	/**
	 * 전체 채금 해제
	 * 2019-07-07
	 * by connector12@nate.com
	 */
	static public void toChattingCloseAllRemove(object o) {
		for (PcInstance pc : World.getPcList()) {
			if (pc != null)
				BuffController.remove(pc, ChattingClose.class);
		}
		
		CharactersDatabase.chattingCloseAllRemove();
		
		if (o != null)
			ChattingController.toChatting(o, "전체 채금 해제 완료.", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 스킬 올마
	 * 
	 * @param o
	 * @param st
	 */
	static private void toSkillAllMaster(object o, StringTokenizer st) {
		PcInstance pc = (PcInstance) o;
		if (st.hasMoreTokens()) {
			pc = World.findPc(st.nextToken());
		}
		if (pc != null) {
			switch (pc.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				// 힐
				SkillController.append(pc, 1, 0, false);
				// 라이트
				SkillController.append(pc, 1, 1, false);
				// 실드
				SkillController.append(pc, 1, 2, false);
				// 에너지 볼트
				SkillController.append(pc, 1, 3, false);
				// 텔레포트
				SkillController.append(pc, 1, 4, false);
				// 아이스 대거
				SkillController.append(pc, 1, 5, false);
				// 윈드 커터
				SkillController.append(pc, 1, 6, false);
				// 홀리 웨폰
				SkillController.append(pc, 1, 7, false);
				// 큐어 포이즌
				SkillController.append(pc, 2, 0, false);
				// 칠 터치
				SkillController.append(pc, 2, 1, false);
				// 커스: 포이즌
				SkillController.append(pc, 2, 2, false);
				// 인챈트 웨폰
				SkillController.append(pc, 2, 3, false);
				// 디텍션
				SkillController.append(pc, 2, 4, false);
				// 디크리즈 웨이트
				SkillController.append(pc, 2, 5, false);
				// 파이어 애로우
				SkillController.append(pc, 2, 6, false);
				
				// 군주전용 스킬
				// 트루 타겟
				SkillController.append(pc, 15, 0, false);
				// 글로잉 웨폰
				SkillController.append(pc, 15, 1, false);
				// 샤이닝 실드
				SkillController.append(pc, 15, 2, false);
				// 콜클렌
				SkillController.append(pc, 15, 3, false);
				// 브레이브 멘탈
				SkillController.append(pc, 15, 4, false);
				// 브레이브 아바타
				SkillController.append(pc, 15, 5, false);
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				// 힐
				SkillController.append(pc, 1, 0, false);
				// 라이트
				SkillController.append(pc, 1, 1, false);
				// 실드
				SkillController.append(pc, 1, 2, false);
				// 에너지 볼트
				SkillController.append(pc, 1, 3, false);
				// 텔레포트
				SkillController.append(pc, 1, 4, false);
				// 아이스 대거
				SkillController.append(pc, 1, 5, false);
				// 윈드 커터
				SkillController.append(pc, 1, 6, false);
				// 홀리 웨폰
				SkillController.append(pc, 1, 7, false);
				
				// 기사 전용 마법
				// 쇼크 스턴
				SkillController.append(pc, 2, 7, false);
				// 리덕션 아머
				SkillController.append(pc, 7, 6, false);
				// 바운스 어택
				SkillController.append(pc, 8, 6, false);
				// 솔리드 캐리지
				SkillController.append(pc, 9, 7, false);
				// 카운터 배리어
				SkillController.append(pc, 10, 7, false);
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				// 힐
				SkillController.append(pc, 1, 0, false);
				// 라이트
				SkillController.append(pc, 1, 1, false);
				// 실드
				SkillController.append(pc, 1, 2, false);
				// 에너지 볼트
				SkillController.append(pc, 1, 3, false);
				// 텔레포트
				SkillController.append(pc, 1, 4, false);
				// 아이스 대거
				SkillController.append(pc, 1, 5, false);
				// 윈드 커터
				SkillController.append(pc, 1, 6, false);
				// 홀리 웨폰
				SkillController.append(pc, 1, 7, false);
				// 큐어 포이즌
				SkillController.append(pc, 2, 0, false);
				// 칠 터치
				SkillController.append(pc, 2, 1, false);
				// 커스: 포이즌
				SkillController.append(pc, 2, 2, false);
				// 인챈트 웨폰
				SkillController.append(pc, 2, 3, false);
				// 디텍션
				SkillController.append(pc, 2, 4, false);
				// 디크리즈 웨이트
				SkillController.append(pc, 2, 5, false);
				// 파이어 애로우
				SkillController.append(pc, 2, 6, false);			
				// 라이트닝
				SkillController.append(pc, 3, 0, false);
				// 턴 언데드
				SkillController.append(pc, 3, 1, false);
				// 익스트라 힐
				SkillController.append(pc, 3, 2, false);
				// 커스: 블라인드
				SkillController.append(pc, 3, 3, false);
				// 블레스드 아머
				SkillController.append(pc, 3, 4, false);
				// 프로즌 클라우드
				SkillController.append(pc, 3, 5, false);
				// 버서커스
				SkillController.append(pc, 3, 6, false);
				// 파이어 볼
				SkillController.append(pc, 4, 0, false);
				// 피지컬 인챈트: DEX
				SkillController.append(pc, 4, 1, false);
				// 웨폰 브레이크
				SkillController.append(pc, 4, 2, false);
				// 뱀파이어릭 터치
				SkillController.append(pc, 4, 3, false);
				// 슬로우
				SkillController.append(pc, 4, 4, false);
				// 어스 재일
				SkillController.append(pc, 4, 5, false);
				// 카운터 매직
				SkillController.append(pc, 4, 6, false);
				// 메디테이션
				SkillController.append(pc, 4, 7, false);		
				// 커스: 패럴라이즈
				SkillController.append(pc, 5, 0, false);
				// 콜 라이트닝
				SkillController.append(pc, 5, 1, false);
				// 그레이터 힐
				SkillController.append(pc, 5, 2, false);
				// 테이밍 몬스터
				SkillController.append(pc, 5, 3, false);
				// 리무브 커스
				SkillController.append(pc, 5, 4, false);
				// 콘 오브 콜드
				SkillController.append(pc, 5, 5, false);
				// 마나 드레인
				SkillController.append(pc, 5, 6, false);
				// 다크니스
				SkillController.append(pc, 5, 7, false);			
				// 크리에이트 좀비
				SkillController.append(pc, 6, 0, false);
				// 피지컬 인챈트: STR
				SkillController.append(pc, 6, 1, false);
				// 헤이스트
				SkillController.append(pc, 6, 2, false);
				// 캔슬레이션
				SkillController.append(pc, 6, 3, false);
				// 이럽션
				SkillController.append(pc, 6, 4, false);
				// 선 버스트
				SkillController.append(pc, 6, 5, false);
				// 위크니스
				SkillController.append(pc, 6, 6, false);
				// 블레스 웨폰
				SkillController.append(pc, 6, 7, false);
				
				// 요정 전용 마법
				// 레지스트 매직
				SkillController.append(pc, 17, 0, false);
				// 바디 투 마인드
				SkillController.append(pc, 17, 1, false);
				// 텔레포트 투 마더
				SkillController.append(pc, 17, 2, false);
				// 클리어 마인드
				SkillController.append(pc, 18, 0, false);
				// 레지스트 엘리멘트
				SkillController.append(pc, 18, 1, false);
				// 트리플 애로우
				SkillController.append(pc, 19, 0, false);
				// 블러드 투 소울
				SkillController.append(pc, 19, 1, false);
				// 이글 아이
				SkillController.append(pc, 19, 2, false);
				// 아쿠아 프로텍트
				SkillController.append(pc, 19, 3, false);
				// 폴루트 워터
				SkillController.append(pc, 19, 4, false);
				// 어스 가디언
				SkillController.append(pc, 19, 5, false);
				// 스트라이커 게일
				SkillController.append(pc, 19, 6, false);
				// 인탱글
				SkillController.append(pc, 19, 7, false);			
				// 이레이즈 매직
				SkillController.append(pc, 20, 0, false);
				// 버닝 웨폰
				SkillController.append(pc, 20, 1, false);
				// 엘리멘탈 파이어
				SkillController.append(pc, 20, 2, false);
				// 아이 오브 스톰
				SkillController.append(pc, 20, 3, false);
				// 네이쳐스 터치
				SkillController.append(pc, 20, 5, false);
				// 엑조틱 바이탈라이즈
				SkillController.append(pc, 20, 6, false);				
				// 에어리어 오브 사일런스
				SkillController.append(pc, 21, 0, false);
				// 어디셔널 파이어
				SkillController.append(pc, 21, 1, false);
				// 워터 라이프
				SkillController.append(pc, 21, 2, false);
				// 네이쳐스 블레싱
				SkillController.append(pc, 21, 3, false);
				// 어스 바인드
				SkillController.append(pc, 21, 4, false);
				// 스톰 샷
				SkillController.append(pc, 21, 5, false);
				// 소울 오브 프레임
				SkillController.append(pc, 21, 6, false);
				// 아이언 스킨
				SkillController.append(pc, 21, 7, false);
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				// 힐
				SkillController.append(pc, 1, 0, false);
				// 라이트
				SkillController.append(pc, 1, 1, false);
				// 실드
				SkillController.append(pc, 1, 2, false);
				// 에너지 볼트
				SkillController.append(pc, 1, 3, false);
				// 텔레포트
				SkillController.append(pc, 1, 4, false);
				// 아이스 대거
				SkillController.append(pc, 1, 5, false);
				// 윈드 커터
				SkillController.append(pc, 1, 6, false);
				// 홀리 웨폰
				SkillController.append(pc, 1, 7, false);
				// 큐어 포이즌
				SkillController.append(pc, 2, 0, false);
				// 칠 터치
				SkillController.append(pc, 2, 1, false);
				// 커스: 포이즌
				SkillController.append(pc, 2, 2, false);
				// 인챈트 웨폰
				SkillController.append(pc, 2, 3, false);
				// 디텍션
				SkillController.append(pc, 2, 4, false);
				// 디크리즈 웨이트
				SkillController.append(pc, 2, 5, false);
				// 파이어 애로우
				SkillController.append(pc, 2, 6, false);			
				// 라이트닝
				SkillController.append(pc, 3, 0, false);
				// 턴 언데드
				SkillController.append(pc, 3, 1, false);
				// 익스트라 힐
				SkillController.append(pc, 3, 2, false);
				// 커스: 블라인드
				SkillController.append(pc, 3, 3, false);
				// 블레스드 아머
				SkillController.append(pc, 3, 4, false);
				// 프로즌 클라우드
				SkillController.append(pc, 3, 5, false);
				// 버서커스
				SkillController.append(pc, 3, 6, false);
				// 파이어 볼
				SkillController.append(pc, 4, 0, false);
				// 피지컬 인챈트: DEX
				SkillController.append(pc, 4, 1, false);
				// 웨폰 브레이크
				SkillController.append(pc, 4, 2, false);
				// 뱀파이어릭 터치
				SkillController.append(pc, 4, 3, false);
				// 슬로우
				SkillController.append(pc, 4, 4, false);
				// 어스 재일
				SkillController.append(pc, 4, 5, false);
				// 카운터 매직
				SkillController.append(pc, 4, 6, false);
				// 메디테이션
				SkillController.append(pc, 4, 7, false);		
				// 커스: 패럴라이즈
				SkillController.append(pc, 5, 0, false);
				// 콜 라이트닝
				SkillController.append(pc, 5, 1, false);
				// 그레이터 힐
				SkillController.append(pc, 5, 2, false);
				// 테이밍 몬스터
				SkillController.append(pc, 5, 3, false);
				// 리무브 커스
				SkillController.append(pc, 5, 4, false);
				// 콘 오브 콜드
				SkillController.append(pc, 5, 5, false);
				// 마나 드레인
				SkillController.append(pc, 5, 6, false);
				// 다크니스
				SkillController.append(pc, 5, 7, false);			
				// 크리에이트 좀비
				SkillController.append(pc, 6, 0, false);
				// 피지컬 인챈트: STR
				SkillController.append(pc, 6, 1, false);
				// 헤이스트
				SkillController.append(pc, 6, 2, false);
				// 캔슬레이션
				SkillController.append(pc, 6, 3, false);
				// 이럽션
				SkillController.append(pc, 6, 4, false);
				// 선 버스트
				SkillController.append(pc, 6, 5, false);
				// 위크니스
				SkillController.append(pc, 6, 6, false);
				// 블레스 웨폰
				SkillController.append(pc, 6, 7, false);			
				// 힐 올
				SkillController.append(pc, 7, 0, false);
				// 아이스 랜스
				SkillController.append(pc, 7, 1, false);
				// 서먼 몬스터
				SkillController.append(pc, 7, 2, false);
				// 홀리 워크
				SkillController.append(pc, 7, 3, false);
				// 토네이도
				SkillController.append(pc, 7, 4, false);
				// 그레이터 헤이스트
				SkillController.append(pc, 7, 5, false);
				// 디지즈
				SkillController.append(pc, 7, 7, false);
				// 풀 힐
				SkillController.append(pc, 8, 0, false);
				// 파이어월
				SkillController.append(pc, 8, 1, false);
				// 블리자드
				SkillController.append(pc, 8, 2, false);
				// 인비지블리티
				SkillController.append(pc, 8, 3, false);
				// 리절렉션
				SkillController.append(pc, 8, 4, false);
				// 어스 퀘이크
				SkillController.append(pc, 8, 5, false);
				// 라이프 스트림
				//SkillController.append(pc, 8, 6, false);
				// 사일런스
				SkillController.append(pc, 8, 7, false);		
				// 포그 오브 슬리핑
				SkillController.append(pc, 9, 1, false);
				// 어드밴스 스피릿
				SkillController.append(pc, 9, 2, false);
				// 이뮨 투 함
				SkillController.append(pc, 9, 3, false);
				// 매스 텔레포트
				SkillController.append(pc, 9, 4, false);
				// 파이어 스톰
				SkillController.append(pc, 9, 5, false);
				// 디케이 포션
				SkillController.append(pc, 9, 6, false);		
				// 크리에이트 매지컬 웨폰
				SkillController.append(pc, 10, 0, false);
				// 미티어 스트라이크
				SkillController.append(pc, 10, 1, false);
				// 디스인티그레이트
				SkillController.append(pc, 10, 4, false);
				// 앱솔루트 배리어
				SkillController.append(pc, 10, 5, false);
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				break;
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
				break;
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:
				break;
			}
			SkillController.sendList(pc);

			ChattingController.toChatting(o, "스킬올마 완료.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 계정, 캐릭터, IP 차단
	 * @param
	 * @return
	 * 2017-09-04
	 * by all_night.
	 */
	static public void toBan(object o, StringTokenizer st) {
		boolean find = false;
		String ip = null;							// IP
		String account = null;						// 계정
		String name = st.nextToken().toLowerCase(); // 캐릭명

		// 케릭 찾기.
		Connection con = null;
		PreparedStatement stt = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("SELECT * FROM characters WHERE LOWER(name)=?");
			stt.setString(1, name);
			rs = stt.executeQuery();
			
			while (rs.next()) {
				find = true;
				account = rs.getString("account");
			}

		} catch (Exception e) {
			lineage.share.System.println("[벤] 캐릭터 계정 찾기 오류: " + e);
		} finally {
			DatabaseConnection.close(con, stt, rs);
		}
		
		// 계정 찾기.
		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("SELECT * FROM accounts WHERE LOWER(id)=?");
			stt.setString(1, account);
			rs = stt.executeQuery();

			while (rs.next()) {
				find = true;
				ip = rs.getString("last_ip");
				BadIpDatabase.append(ip);
			}
		} catch (Exception e) {
			lineage.share.System.println("[벤] 계정으로 마지막 접속 IP찾기 오류: " + e);
		} finally {
			DatabaseConnection.close(con, stt, rs);
		}

		// 찾았다면
		if (find) {
			try {
				con = DatabaseConnection.getLineage();
				// 계정 차단.
				stt = con.prepareStatement("UPDATE accounts SET block_date=? WHERE LOWER(id)=?");
				stt.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
				stt.setString(2, account);
				stt.executeUpdate();
				stt.close();
			} catch (Exception e) {
				lineage.share.System.println("[벤] 계정 차단 오류: " + e);
			} finally {
				DatabaseConnection.close(con, stt, rs);
			}
			
			try {
				con = DatabaseConnection.getLineage();
				// 케릭 차단.
				stt = con.prepareStatement("UPDATE characters SET block_date=? WHERE LOWER(name)=?");
				stt.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
				stt.setString(2, name);
				stt.executeUpdate();

				// 접속된 사용자 차단.
				PcInstance find_use = null;
				if (account != null) {
					for (PcInstance pc : World.getPcList()) {
						if (pc.getAccountId().equalsIgnoreCase(account)) {
							find_use = pc;
							BadIpDatabase.append(pc.getClient().getAccountIp());
							break;
						}
					}
				}

				// 찾은 사용자 종료.
				if (find_use != null) {
					find_use.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
					LineageServer.close(find_use.getClient());
				}
			} catch (Exception e) {
				lineage.share.System.println("[벤] 캐릭터 차단 오류: " + e);
			} finally {
				DatabaseConnection.close(con, stt, rs);
			}

			if (o != null)
				ChattingController.toChatting(o, String.format("[계정: %s] [캐릭터: %s] [IP: %s] 벤 완료.", account, name, ip), Lineage.CHATTING_MODE_MESSAGE);
			else
				lineage.share.System.println(String.format("[계정: %s] [캐릭터: %s] [IP: %s] 벤 완료.", account, name, ip));
		}
	}
	
	/**
	 * 차단 해제
	 * 2019-07-02
	 * by connector12@nate.com
	 */
	static public void toBanRemove(object o, StringTokenizer st) {
		boolean find = false;
		String ip = null;							// IP
		String account = null;						// 계정
		String name = st.nextToken().toLowerCase(); // 캐릭명

		// 케릭 찾기.
		Connection con = null;
		PreparedStatement stt = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("SELECT * FROM characters WHERE LOWER(name)=?");
			stt.setString(1, name);
			rs = stt.executeQuery();
			
			if (rs.next()) {
				find = true;
				account = rs.getString("account");
			}
		} catch (Exception e) {
			lineage.share.System.println("[벤 해제] 캐릭터 계정 찾기 오류: " + e);
		} finally {
			DatabaseConnection.close(con, stt, rs);
		}
		
		// 계정 찾기.
		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("SELECT * FROM accounts WHERE LOWER(id)=?");
			stt.setString(1, account);
			rs = stt.executeQuery();

			if (rs.next()) {
				find = true;
				ip = rs.getString("last_ip");
				BadIpDatabase.remove(ip);
			}
		} catch (Exception e) {
			lineage.share.System.println("[벤] 계정으로 마지막 접속 IP찾기 오류: " + e);
		} finally {
			DatabaseConnection.close(con, stt, rs);
		}

		// 찾았다면
		if (find) {
			try {
				con = DatabaseConnection.getLineage();
				// 계정 차단 해제.
				stt = con.prepareStatement("UPDATE accounts SET block_date='0000-00-00 00:00:00' WHERE LOWER(id)=?");
				stt.setString(1, account);
				stt.executeUpdate();
				stt.close();
			} catch (Exception e) {
				lineage.share.System.println("[벤 해제] 계정 차단 해제 오류: " + e);
			} finally {
				DatabaseConnection.close(con, stt, rs);
			}
			
			try {
				con = DatabaseConnection.getLineage();
				// 케릭 차단 해제.
				stt = con.prepareStatement("UPDATE characters SET block_date='0000-00-00 00:00:00' WHERE LOWER(name)=?");
				stt.setString(1, name);
				stt.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.println("[벤 해제] 캐릭터 차단 해제 오류: " + e);
			} finally {
				DatabaseConnection.close(con, stt, rs);
			}

			if (o != null)
				ChattingController.toChatting(o, String.format("[계정: %s] [캐릭터: %s] [IP: %s] 벤 해제 완료.", account, name, ip), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	static public void toBanAllRemove(object o) {
		Connection con = null;
		PreparedStatement stt = null;
		
		try {
			BadIpDatabase.removeAll();
			
			con = DatabaseConnection.getLineage();
			// 계정 차단 해제.
			stt = con.prepareStatement("UPDATE accounts SET block_date='0000-00-00 00:00:00'");
			stt.executeUpdate();
			stt.close();
			
			// 케릭 차단 해제.
			stt = con.prepareStatement("UPDATE characters SET block_date='0000-00-00 00:00:00'");
			stt.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.println("[전체 벤 해제] 계정, 캐릭터 차단 해제 오류: " + e);
		} finally {
			DatabaseConnection.close(con, stt);
		}

		if (o != null)
			ChattingController.toChatting(o, "전체 벤 해제 완료.", Lineage.CHATTING_MODE_MESSAGE);
		else
			lineage.share.System.println("전체 벤 해제 완료.");
	}

	/**
	 * 청소 : GuiMain 에서 사용중
	 * 
	 * @param o
	 */
	static public void toWorldItemClear(object o) {
		World.clearWorldItem();
		if (o != null)
			ChattingController.toChatting(o, "청소 완료.", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 인벤삭제.
	 * 
	 * @param o
	 * @param st
	 **/
	static private void 인벤전체삭제(object o, StringTokenizer st) {
		PcInstance pc = World.findPc(st.nextToken());

		String itemname = null;

		if (st.hasMoreTokens()) {
			itemname = String.valueOf(st.nextToken());
		}

		int count = 1;
		if (st.hasMoreTokens()) {
			count = Integer.parseInt(st.nextToken());
		}

		int en = 0;
		if (st.hasMoreTokens()) {
			en = Integer.parseInt(st.nextToken());
		}

		int bress = 1;
		if (st.hasMoreTokens()) {
			bress = Integer.parseInt(st.nextToken());
		}

		ItemInstance targetItem = null;

		if (itemname != null) {
			Item i = ItemDatabase.findItemIdByNameWithoutSpace(itemname);
			targetItem = ItemDatabase.newInstance(i);
		}

		if (pc == null) {
			ChattingController.toChatting(o, "해당캐릭터가 존재하지 않습니다.", 20);
		}

		try {
			for (ItemInstance item : pc.getInventory().getList()) {
				if (itemname == null) {
					if (!item.isEquipped()) {
						pc.getInventory().remove(item, true);
						ChattingController.toChatting(o, pc.getName() + "님의 +" + item.getEnLevel() + " " + item.getItem().getName() + "(" + count + ") 삭제완료.", 20);
						ChattingController.toChatting(pc, "운영자님이 당신의 +" + item.getEnLevel() + " " + item.getItem().getName() + "(" + count + ") 삭제하였습니다.", 20);
					}
				} else {
					if (item.getName() == targetItem.getName() && item.getEnLevel() == en && item.getBless() == bress) {
						pc.getInventory().remove(item, true);
						ChattingController.toChatting(o, pc.getName() + "님의 +" + item.getEnLevel() + " " + item.getItem().getName() + "(" + count + ") 삭제완료.", 20);
						ChattingController.toChatting(pc, "운영자님이 당신의 +" + item.getEnLevel() + " " + item.getItem().getName() + "(" + count + ") 삭제하였습니다.", 20);
					}
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, "[서버알림] 아이템이름이 없을시 착용템 제외 모두 삭제.", 20);
		}
	}

	public static void 인벤삭제(object o, StringTokenizer st) {
		String str = st.nextToken();
		PcInstance pc = World.findPc(str);
		if (pc != null) {
			for (ItemInstance i : pc.getInventory().getList()) {
				try {
					if (i.getItem().getNameIdNumber() != 417)
						if (!i.getItem().getName().equalsIgnoreCase("절대 반지"))
							if (i.isEquipped()) {
								i.setEquipped(false);
								i.toSetoption(pc, true);
								i.toEquipped(pc, pc.getInventory());
								i.toOption(pc, true);
								pc.getInventory().count(i, 0L, true);
							} else {
								pc.getInventory().remove(i, true);
							}
				} catch (Exception localException) {
					lineage.share.System.printf("%s : toInventoryDelete(object o, StringTokenizer st)\r\n", new Object[] { CommandController.class.toString() });
					lineage.share.System.println(localException);
				}
			}
			ChattingController.toChatting(pc, "운영자 전용 아이템 제외한 인벤리스트 삭제완료", Lineage.CHATTING_MODE_MESSAGE);
		} else if (o != null) {
			ChattingController.toChatting(o, new StringBuilder().append(str).append("은(는) 월드상 접속중이지 않습니다.").toString(), 20);
		}
	}
	
	/**
	 * 올버프 ; GuiMain 에서 사용중
	 * 
	 * @param o
	 */
	static public void toBuffAll(object o) {
		for (PcInstance pc : World.getPcList())
			toAllBuff(pc);

		if (o != null)
			ChattingController.toChatting(o, "올버프 완료.", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 버프 : ScreenRenderComposite 에서 사용중
	 * 
	 * @param o
	 * @param st
	 */
	static public void toBuff(object o, StringTokenizer st) {
		PcInstance pc = World.findPc(st.nextToken());
		if (pc != null) {
			toAllBuff(pc);
			if (o != null)
				ChattingController.toChatting(o, "버프 완료.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 중복 코드 방지용. : 여기서 사용중. : PcInstnace.toWorldJoin 사용중.
	 * 
	 * @param o
	 */
	static public void toBuff(object o) {
		Haste.onBuff(o, SkillDatabase.find(6, 2), 1800);
		DecreaseWeight.onBuff(o, SkillDatabase.find(2, 5));
		
		if (o.getInventory() != null && o.getInventory().getSlot(Lineage.SLOT_ARMOR) != null)
			BlessedArmor.onBuff((Character) o, o.getInventory().getSlot(Lineage.SLOT_ARMOR), SkillDatabase.find(3, 4), SkillDatabase.find(3, 4).getBuffDuration());
		
		EnchantDexterity.onBuff(o, SkillDatabase.find(4, 1));
		EnchantMighty.onBuff(o, SkillDatabase.find(6, 1));
		
		if (o.getInventory() != null && o.getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
			BlessWeapon.onBuff(o.getInventory().getSlot(Lineage.SLOT_WEAPON), SkillDatabase.find(6, 7));
		
	
	}

	/**
	 * 운영자 올버프 명령어 에서 사용
	 */
	static public void toAllBuff(object o) {
		DecreaseWeight.onBuff(o, SkillDatabase.find(2, 5));
		
		if (o.getInventory() != null && o.getInventory().getSlot(Lineage.SLOT_ARMOR) != null)
			BlessedArmor.onBuff((Character) o, o.getInventory().getSlot(Lineage.SLOT_ARMOR), SkillDatabase.find(3, 4), SkillDatabase.find(3, 4).getBuffDuration());
		
		EnchantDexterity.onBuff(o, SkillDatabase.find(4, 1));
		EnchantMighty.onBuff(o, SkillDatabase.find(6, 1));
		
		if (o.getInventory() != null && o.getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
			BlessWeapon.onBuff(o.getInventory().getSlot(Lineage.SLOT_WEAPON), SkillDatabase.find(6, 7));
		
		AdvanceSpirit.onBuff(o, SkillDatabase.find(9, 2));
		
		if (Lineage.server_version >= 182)
			IronSkin.onBuff(o, SkillDatabase.find(21, 7));
		
		GlowingWeapon.onBuff(o, SkillDatabase.find(100));
		ShiningShield.onBuff(o, SkillDatabase.find(101));
		BraveAvatar.onBuff(o, SkillDatabase.find(307));
	}

	/**
	 * 아이템 생성
	 * 
	 * @param o
	 * @param st
	 */
	private static void toItem(object o, StringTokenizer st) {
		String itemName = st.nextToken();
		int itemCount = 1;
		int enchantLevel = 0;
		int blessType = 1;
		int ElementsFire = 0;
		int ElementsWater = 0;
		int ElementsWind = 0;
		int ElementsEarth = 0;
		int dollOptionsA = 0;
		int dollOptionsB = 0;
		int dollOptionsC = 0;
		int dollOptionsD = 0;
		int dollOptionsE = 0;

		if (st.hasMoreTokens()) {
			itemCount = Integer.valueOf(st.nextToken());
		}
		if (st.hasMoreTokens()) {
			enchantLevel = Integer.valueOf(st.nextToken());
		}
		if (st.hasMoreTokens()) {
			blessType = Integer.valueOf(st.nextToken());
		}

		if (st.hasMoreTokens()) {
			switch (st.nextToken()) {
			case "1":
				ElementsFire = Integer.valueOf(st.nextToken());
				break;
			case "2":
				ElementsWater = Integer.valueOf(st.nextToken());
				break;
			case "3":
				ElementsWind = Integer.valueOf(st.nextToken());
				break;

			case "4":
				ElementsEarth = Integer.valueOf(st.nextToken());
				break;
			}

		}
		if (st.hasMoreTokens()) {
			switch (st.nextToken()) {
			case "1":
				dollOptionsA = Integer.valueOf(st.nextToken());
				break;
			case "2":
				dollOptionsB = Integer.valueOf(st.nextToken());
				break;
			case "3":
				dollOptionsC = Integer.valueOf(st.nextToken());
				break;

			case "4":
				dollOptionsD = Integer.valueOf(st.nextToken());
				break;
				
			case "5":
				dollOptionsE = Integer.valueOf(st.nextToken());
				break;
			}

		}

		Item item = ItemDatabase.find2(itemName);

		if (item != null) {
			ItemInstance temp = o.getInventory().find(item.getName(), blessType, item.isPiles());

			if (temp != null && (temp.getBless() != blessType || temp.getEnLevel() != enchantLevel)) {
				temp = null;
			}

			if (temp == null) {
				if (item.isPiles()) {
					temp = ItemDatabase.newInstance(item);
					temp.setObjectId(ServerDatabase.nextItemObjId());
					temp.setBless(blessType);
					temp.setEnLevel(enchantLevel);
					temp.setCount(itemCount);
					temp.setEnFire(ElementsFire);
					temp.setEnWater(ElementsWater);
					temp.setEnWind(ElementsWind);
					temp.setEnEarth(ElementsEarth);
					temp.setInvDolloptionA(dollOptionsA);
					temp.setInvDolloptionB(dollOptionsB);
					temp.setInvDolloptionC(dollOptionsC);
					temp.setInvDolloptionD(dollOptionsD);
					temp.setInvDolloptionE(dollOptionsE);
					temp.setDefinite(true);
					o.getInventory().append(temp, true);
				} else {
					for (int i = 0; i < itemCount; i++) {
						temp = ItemDatabase.newInstance(item);
						temp.setObjectId(ServerDatabase.nextItemObjId());
						temp.setBless(blessType);
						temp.setEnLevel(enchantLevel);
						temp.setEnFire(ElementsFire);
						temp.setEnWater(ElementsWater);
						temp.setEnWind(ElementsWind);
						temp.setEnEarth(ElementsEarth);
						temp.setInvDolloptionA(dollOptionsA);
						temp.setInvDolloptionB(dollOptionsB);
						temp.setInvDolloptionC(dollOptionsC);
						temp.setInvDolloptionD(dollOptionsD);
						temp.setInvDolloptionE(dollOptionsE);
						temp.setDefinite(true);
						o.getInventory().append(temp, true);
					}
				}
			} else {
				o.getInventory().count(temp, temp.getCount() + itemCount, true);
			}

			String message;
			if (item.getType1().equalsIgnoreCase("weapon") || item.getType1().equalsIgnoreCase("armor")) {
				message = String.format("아이템 생성: +%d %s(%d)", enchantLevel, item.getName(), itemCount);
			} else {
				message = String.format("아이템 생성: %s(%d)", item.getName(), itemCount);
			}
			ChattingController.toChatting(o, message, Lineage.CHATTING_MODE_MESSAGE);
		} else {
			ChattingController.toChatting(o, String.format("아이템 (%s) 생성 실패", itemName), Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 아이템 선물
	 * 
	 * @param o
	 * @param st
	 */
	static private void toGiveItem(object o, object pc, StringTokenizer st) {
		String name = st.nextToken();
		long count = 1;
		int en = 0;
		int bless = 1;

		if (st.hasMoreTokens())
			count = Long.valueOf(st.nextToken());
		if (st.hasMoreTokens())
			en = Integer.valueOf(st.nextToken());
		if (st.hasMoreTokens())
			bless = Integer.valueOf(st.nextToken());

		Item i = ItemDatabase.find2(name);

		if (i != null) {
			ItemInstance temp = pc.getInventory().find(i.getName(), bless, i.isPiles());

			if (temp != null && (temp.getBless() != bless || temp.getEnLevel() != en))
				temp = null;

			if (temp == null) {
				// 겹칠수 있는 아이템이 존재하지 않을경우.
				if (i.isPiles()) {
					temp = ItemDatabase.newInstance(i);
					temp.setObjectId(ServerDatabase.nextItemObjId());
					temp.setBless(bless);
					temp.setEnLevel(en);
					temp.setCount(count);
					temp.setDefinite(true);
					pc.getInventory().append(temp, true);
				} else {
					for (int idx = 0; idx < count; idx++) {
						temp = ItemDatabase.newInstance(i);
						temp.setObjectId(ServerDatabase.nextItemObjId());
						temp.setBless(bless);
						temp.setEnLevel(en);
						temp.setDefinite(true);
						pc.getInventory().append(temp, true);
					}
				}
			} else {
				// 겹치는 아이템이 존재할 경우.
				pc.getInventory().count(temp, temp.getCount() + count, true);
			}

			// 알림.
			if (pc != null) {
				if (temp.getItem().getType1().equalsIgnoreCase("weapon") || temp.getItem().getType1().equalsIgnoreCase("armor")) {
					ChattingController.toChatting(pc, String.format("운영자 선물: +%d %s(%d)", en, i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, String.format("+%d %s(%d) 선물: %s", en, i.getName(), count, pc.getName()), Lineage.CHATTING_MODE_MESSAGE);
					pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 16139), true);
					
				} else {
					ChattingController.toChatting(pc, String.format("운영자 선물: %s(%d)", i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, String.format("%s(%d) 선물: %s", i.getName(), count, pc.getName()), Lineage.CHATTING_MODE_MESSAGE);
					pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 16139), true);
				}
			}
		} else
			ChattingController.toChatting(o, String.format("아이템 (%s) 생성 실패", name), Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 아이템 전체 선물
	 * 
	 * @param o
	 * @param st
	 */
	static private void toAllGiveItem(object o, StringTokenizer st) {
		String name = st.nextToken();
		long count = 1;
		int en = 0;
		int bless = 1;

		if (st.hasMoreTokens())
			count = Long.valueOf(st.nextToken());
		if (st.hasMoreTokens())
			en = Integer.valueOf(st.nextToken());
		if (st.hasMoreTokens())
			bless = Integer.valueOf(st.nextToken());

		Item i = ItemDatabase.find2(name);

		if (i != null) {
			for (PcInstance pc : World.getPcList()) {
				if (pc != null && pc.getInventory() != null) {
					ItemInstance temp = pc.getInventory().find(i.getName(), bless, i.isPiles());

					if (temp != null && (temp.getBless() != bless || temp.getEnLevel() != en))
						temp = null;

					if (temp == null) {
						// 겹칠수 있는 아이템이 존재하지 않을경우.
						if (i.isPiles()) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setBless(bless);
							temp.setEnLevel(en);
							temp.setCount(count);
							temp.setDefinite(true);
							pc.getInventory().append(temp, true);
						} else {
							for (int idx = 0; idx < count; idx++) {
								temp = ItemDatabase.newInstance(i);
								temp.setObjectId(ServerDatabase.nextItemObjId());
								temp.setBless(bless);
								temp.setEnLevel(en);
								temp.setDefinite(true);
								pc.getInventory().append(temp, true);
							}
						}
					} else {
						// 겹치는 아이템이 존재할 경우.
						pc.getInventory().count(temp, temp.getCount() + count, true);
					}

					// 알림.
					if (pc != null) {
						if (temp.getItem().getType1().equalsIgnoreCase("weapon") || temp.getItem().getType1().equalsIgnoreCase("armor")){
							ChattingController.toChatting(pc, String.format("전체 선물: +%d %s(%d)", en, i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
							pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 16139), true);
						}else{
							ChattingController.toChatting(pc, String.format("전체 선물: %s(%d)", i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
							pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 16139), true);
					}
				  }
				}
			}
			if (i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor"))
				ChattingController.toChatting(o, String.format("+%d %s(%d) 전체 선물 완료", en, i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
			else
				ChattingController.toChatting(o, String.format("%s(%d) 전체 선물 완료", i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
		} else
			ChattingController.toChatting(o, String.format("아이템 (%s) 생성 실패", name), Lineage.CHATTING_MODE_MESSAGE);

	}

	/**
	 * 몬스터 생성.
	 * 
	 * @param o
	 * @param st
	 */
	static private void toMonster(object o, StringTokenizer st) {
		String name = st.nextToken();
		int count = 1;
		int range = 1;

		if (st.hasMoreTokens()) {
			count = Integer.valueOf(st.nextToken());
		}
		
		if (st.hasMoreTokens()) {
			range = Integer.valueOf(st.nextToken());
		}

		Map m = World.get_map(o.getMap());
		
		if (m != null) {
			int x1 = m.locX1;
			int x2 = m.locX2;
			int y1 = m.locY1;
			int y2 = m.locY2;
		
			for (int i = 0; i < count; ++i) {
				MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find2(name));

				if (mi != null) {
					mi.setHomeX(o.getX());
					mi.setHomeY(o.getY());
					mi.setHomeMap(o.getMap());
					mi.setHeading(Util.random(0, 7));
					
					if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
						if (mi.getMonster().isHaste())
							mi.setSpeed(1);
						if (mi.getMonster().isBravery())
							mi.setBrave(true);
					}

					if (range > 1) {				
						int roop_cnt = 0;
						int x = o.getX();
						int y = o.getY();
						int map = o.getMap();
						int lx = x;
						int ly = y;
						int loc = range;
						// 랜덤 좌표 스폰
						do {
							lx = Util.random(x - loc < x1 ? x1 : x - loc, x + loc > x2 ? x2 : x + loc);
							ly = Util.random(y - loc < y1 ? y1 : y - loc, y + loc > y2 ? y2 : y + loc);
							if (roop_cnt++ > 100) {
								lx = x;
								ly = y;
								break;
							}
						}while(
								!World.isThroughObject(lx, ly+1, map, 0) || 
								!World.isThroughObject(lx, ly-1, map, 4) || 
								!World.isThroughObject(lx-1, ly, map, 2) || 
								!World.isThroughObject(lx+1, ly, map, 6) ||
								!World.isThroughObject(lx-1, ly+1, map, 1) ||
								!World.isThroughObject(lx+1, ly-1, map, 5) || 
								!World.isThroughObject(lx+1, ly+1, map, 7) || 
								!World.isThroughObject(lx-1, ly-1, map, 3) ||
								World.isNotMovingTile(lx, ly, map)
							);
						
						mi.toTeleport(lx, ly, o.getMap(), false);
					} else {
						mi.toTeleport(o.getX(), o.getY(), o.getMap(), false);
					}			

					AiThread.append(mi);
					World.appendMonster(mi);
				} else {
					ChattingController.toChatting(o, String.format("[몬스터] %s(%d) 생성 실패", name, count), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
			ChattingController.toChatting(o, String.format("[몬스터] %s(%d) 생성", name, count), Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 소환.
	 * 
	 * @param o
	 * @param st
	 */
	static private void toCall(object o, StringTokenizer st) {
		String name = st.nextToken();
		PcInstance pc = World.findPc(name);
		if (pc != null) {
			if (pc instanceof PcRobotInstance)
				pc.toTeleport(o.getX(), o.getY(), o.getMap(), false);
			else
				pc.toPotal(o.getX(), o.getY(), o.getMap());

			ChattingController.toChatting(o, "소환 완료.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 출두.
	 * 
	 * @param o
	 * @param st
	 */
	static private void toGo(object o, StringTokenizer st) {
		PcInstance pc = World.findPc(st.nextToken());
		if (pc != null) {
			o.toTeleport(pc.getX(), pc.getY(), pc.getMap(), false);

			ChattingController.toChatting(o, "출두 완료.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 맵핵. by feel
	 * 
	 * @param o
	 * @param st
	 */
	static private void maphack(object o, StringTokenizer st) {
		String chk = st.nextToken().toLowerCase();
		if (o.isDead()) {
			ChattingController.toChatting(o, "죽은 상태에선 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		if (chk.equalsIgnoreCase("켬")) {
			o.setMapHack(true);
			o.toSender(new S_Ability(3, true));
			ChattingController.toChatting(o, String.format("맵핵이 활성화 되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (chk.equalsIgnoreCase("끔")) {
			o.setMapHack(false);
			o.toSender(new S_Ability(3, false));
			ChattingController.toChatting(o, String.format("맵핵이 비활성화 되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	static private void Monhitdmg(object o, StringTokenizer st) {
		String chk = st.nextToken().toLowerCase();
		if (o.isDead()) {
			ChattingController.toChatting(o, "죽은 상태에선 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		if (chk.equalsIgnoreCase("켬")) {
			o.setMonhitdmg(true);
			ChattingController.toChatting(o, String.format("몬스터 회피율이 활성화 되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (chk.equalsIgnoreCase("끔")) {
			o.setMonhitdmg(false);
			ChattingController.toChatting(o, String.format("몬스터 회피율이 비활성화 되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	/**
	 * by all_night
	 */
	static private void checkClanGrade(object o, String name, int grade, Clan c) {
		Connection con = null;
		PreparedStatement stt = null;
		ResultSet rs = null;
		int clanId = 0;
		int clanGrade = 0;
		String charId = null;

		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("SELECT name, clanId, clan_grade FROM characters WHERE name = ?");
			stt.setString(1, name);
			rs = stt.executeQuery();
			while (rs.next()) {
				charId = rs.getString("name");
				clanId = rs.getInt("clanId");
				clanGrade = rs.getInt("clan_grade");
			}
		} catch (Exception e) {
			lineage.share.System.println("직위 부여 디비 SELECT 실패 : " + e);
		} finally {
			DatabaseConnection.close(con, stt);
		}

		if (charId == null) {
			ChattingController.toChatting(o, "캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else {
			if (clanId == o.getClanId()) {
				if (o.getClanGrade() > clanGrade || o.getClanGrade() > 2) {
					setClanGrade(o, charId, grade);
					c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 768, o.getName(), charId, (grade == 0 ? "혈맹원" : grade == 1 ? "수호기사" : grade == 2 ? "부군주" : "군주")));
					if (o.getClanGrade() == 3 && grade == 3) {
						o.setClanGrade(2);
						c.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("%s님이 '부군주' 로 임명되셨습니다.", o.getName())));
					}
				} else {
					//상대방이 등급이 더 높을때
					ChattingController.toChatting(o, "자신과 같거나 높은 직위에게 직위를 부여할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				//혈맹이 다를때
				ChattingController.toChatting(o, "혈맹원이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	/**
	 * by all_night
	 */
	static private void setClanGrade(object o, String name, int grade) {
		Connection con = null;
		Connection conn = null;
		PreparedStatement st = null;
		PreparedStatement stt = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET clan_grade=? WHERE name=?");
			st.setInt(1, grade);
			st.setString(2, name);
			st.executeUpdate();
			st.close();

			if (o.getClanGrade() == 3 && grade == 3) {
				conn = DatabaseConnection.getLineage();
				stt = conn.prepareStatement("UPDATE characters SET clan_grade=? WHERE name=?");
				stt.setInt(1, 2);
				stt.setString(2, o.getName());
				stt.executeUpdate();
				stt.close();
			}
		} catch (Exception e) {
			lineage.share.System.println("직위 부여 UPDATE 실패 : " + e);
		} finally {
			DatabaseConnection.close(con, stt);
		}
	}

	/**
	 * by all_night
	 */
	static private void checkClanMember(object o, String name, Clan c, PcInstance member) {
		Connection con = null;
		PreparedStatement stt = null;
		ResultSet rs = null;
		int clanId = 0;
		int clanGrade = 0;
		String charId = null;

		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("SELECT name, clanId, clan_grade FROM characters WHERE name = ?");
			stt.setString(1, name);
			rs = stt.executeQuery();
			while (rs.next()) {
				charId = rs.getString("name");
				clanId = rs.getInt("clanId");
				clanGrade = rs.getInt("clan_grade");
			}
		} catch (Exception e) {
			lineage.share.System.println("추방 디비 SELECT 실패 : " + e);
		} finally {
			DatabaseConnection.close(con, stt);
		}

		if (charId == null) {
			ChattingController.toChatting(o, "캐릭터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else {
			if (clanId == o.getClanId()) {
				if (o.getClanGrade() > clanGrade || o.getName().equals(c.getLord())) {
					setClanKick(charId);
					c.removeList(member);
					c.removeMemberList(name);
					//240 %0%o 당신의 혈맹에서 추방하였습니다.
					c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 240, name));
				} else {
					//상대방이 등급이 더 높을때
					ChattingController.toChatting(o, "자신과 같거나 높은 직위는 추방할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				//혈맹이 다를때
				ChattingController.toChatting(o, "혈맹원이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	/**
	 * by all_night
	 */
	static private void setClanKick(String name) {
		Connection con = null;
		PreparedStatement stt = null;
		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("UPDATE characters SET clanID=0, clanNAME='', title='', clan_grade=0 WHERE LOWER(name)=?");
			stt.setString(1, name);
			stt.executeUpdate();
			stt.close();
		} catch (Exception e) {
			lineage.share.System.println("추방 UPDATE 실패 : " + e);
		} finally {
			DatabaseConnection.close(con, stt);
		}
	}

	/**
	 * by all_night
	 */
	static private boolean checkClanGrade(object o) {
		// npc 이름은 npc_spawnlist에 있는 name 값으로 입력. 
		// 예를 들어 '버질 1888' 이면 띄어쓰기까지 정확하게 입력.	
		boolean result = false;
		String name = null;
		int clanGrade = 0;
		Connection con = null;
		PreparedStatement stt = null;
		ResultSet rs = null;

		for (PcInstance clanMember : World.getPcList()) {
			if (clanMember.getClanId() == o.getClanId() && !o.getName().equals(clanMember.getName()) && clanMember.getClanGrade() == 3) {
				result = true;
				break;
			}
		}

		if (!result) {
			try {
				con = DatabaseConnection.getLineage();
				stt = con.prepareStatement("SELECT name, clan_grade FROM characters WHERE clanId = ?");
				stt.setInt(1, o.getClanId());
				rs = stt.executeQuery();
				while (rs.next()) {
					name = rs.getString("name");
					clanGrade = rs.getInt("clan_grade");
					if (!name.equals(o.getName()) && clanGrade == 3) {
						result = true;
						break;
					}
				}
			} catch (Exception e) {
				lineage.share.System.println("혈맹 직위 체크 디비 SELECT 실패 : " + e);
			} finally {
				DatabaseConnection.close(con, stt);
			}
		}
		return result;
	}

	/**
	 * by all_night
	 */
	static private void checkPromote(object o) {
		Connection con = null;
		PreparedStatement stt = null;
		ResultSet rs = null;
		String name = null;
		String itemName = null;
		int count = 0;

		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("SELECT * FROM promote WHERE name=?");
			stt.setString(1, o.getName());
			rs = stt.executeQuery();
			while (rs.next()) {
				name = rs.getString("name");
				itemName = rs.getString("item_name");
				count = rs.getInt("count");
			}
			stt.close();

			if (name != null && count > 0) {
				stt = con.prepareStatement("DELETE FROM promote WHERE name=?");
				stt.setString(1, o.getName());
				stt.executeUpdate();
				stt.close();

				if (o.getInventory() != null) {
					if (ItemDatabase.find(itemName) != null) {
						ItemInstance coin = o.getInventory().find(itemName, ItemDatabase.find(itemName).isPiles());
						if (coin == null) {
							coin = ItemDatabase.newInstance(ItemDatabase.find(itemName));
							coin.setObjectId(ServerDatabase.nextItemObjId());
							coin.setCount(0);
							coin.setDefinite(true);
							o.getInventory().append(coin, true);
						}
						o.getInventory().count(coin, coin.getCount() + count, true);
					}
				}
				
				ChattingController.toChatting(o, String.format("\\fR%s(%d) 획득", itemName, count), Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "\\fR서버를 위해 홍보를 해주셔서 감사합니다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "\\fR서버 발전을 위하여 많은 홍보 부탁드립니다.", Lineage.CHATTING_MODE_MESSAGE);
				
				if (Log.isLog(o))
					Log.appendPromote((PcInstance) o, count);
			} else {
				ChattingController.toChatting(o, "\\fR홍보확인 불가", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			lineage.share.System.println("홍보 보상 DB체크 실패 : " + e);
		} finally {
			DatabaseConnection.close(con, stt, rs);
		}
	}

	/**
	 * by all_night
	 */
	/**
	 * by kubera
	 */
	static private void characterInfo(object o, StringTokenizer st) {
		List<String> info = new ArrayList<String>();
		info.clear();
		PcInstance pc = (PcInstance) o;
		String name = null;
		int dger = 0;
		if (o.getGm() > 0) {
			try {
				name = st.nextToken();
				pc = World.findPc(name);
				
				if (pc == null) {
					ChattingController.toChatting(o, String.format("%s 캐릭터는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			} catch(Exception e) {
				pc = (PcInstance) o;
			}		
		}
		
		// 캐릭명
		info.add(String.format("[%s]", pc.getName()));
		//레벨
		info.add(String.format("[%s]", pc.getLevel()));
		//랭킹
		info.add(String.format("[%s]", RankController.getRankAll(pc)));
		

		// Str
		info.add(String.format("%s", String.valueOf(pc.getTotalStr())));
		info.add(String.format("%s", String.valueOf(pc.getStr())));

		// Dex
		info.add(String.format("%s", String.valueOf(pc.getTotalDex())));
		info.add(String.format("%s", String.valueOf(pc.getDex())));
		// Con
		info.add(String.format("%s", String.valueOf(pc.getTotalCon())));
		info.add(String.format("%s", String.valueOf(pc.getCon())));
		// Int
		info.add(String.format("%s", String.valueOf(pc.getTotalInt())));
		info.add(String.format("%s", String.valueOf(pc.getInt())));
		// Wis
		info.add(String.format("%s", String.valueOf(pc.getTotalWis())));
		info.add(String.format("%s", String.valueOf(pc.getWis())));
		// Cha
		info.add(String.format("%s", String.valueOf(pc.getTotalCha())));
		info.add(String.format("%s", String.valueOf(pc.getCha())));
		
		info.add(String.format("%s", String.valueOf(pc.getElixir())));

		// 물리 방어력(AC)
		info.add(String.format("%s", pc.getTotalAc() - 10 <= 0 ? String.valueOf(10 - pc.getTotalAc()) : "-" + String.valueOf(pc.getTotalAc() - 10)));
		// 마법 방어력(MR)
		info.add(String.format("%s", String.valueOf(SkillController.getMr(pc, false))));
		//sp
		info.add(String.format("%s", String.valueOf(SkillController.getSp(pc))));
		if (pc.getTotalAc() >= 210) {
			dger += pc.getTotalAc() / 8;
		} else if (pc.getTotalAc() >= 200) {
			dger += pc.getTotalAc() / 9;
		} else if (pc.getTotalAc() >= 190) {
			dger += pc.getTotalAc() / 10;
		} else if (pc.getTotalAc() >= 180) {
			dger += pc.getTotalAc() / 11;
		} else if (pc.getTotalAc() >= 170) {
			dger += pc.getTotalAc() / 12;
		} else if (pc.getTotalAc() >= 160) {
			dger += pc.getTotalAc() / 13;
		} else if (pc.getTotalAc() >= 150) {
			dger += pc.getTotalAc() / 14;
		} else if (pc.getTotalAc() >= 140) {
			dger += pc.getTotalAc() / 15;
		} else if (pc.getTotalAc() >= 130) {
			dger += pc.getTotalAc() / 16;
		} else {
			dger += pc.getTotalAc() / 17; //
		}

//		info.add(String.format("%s", String.valueOf(pc.isBuffStrikerGale() ? DamageController.getEr(pc) / 3 : DamageController.getEr(pc))));
		info.add(String.valueOf(pc.getTotalEr() + dger));	
		


		info.add(String.format("%s", String.valueOf(CharacterController.toStatStr(pc, "DmgFigure"))));
		info.add(String.format("%s", String.valueOf(CharacterController.toStatStr(pc, "isHitFigure"))));
		info.add(String.format("%s", String.valueOf(pc.getTotalCritical(false))));
		
		
		// 원거리 데미지
		info.add(String.format("%s", String.valueOf(CharacterController.toStatDex(pc, "DmgFigure"))));
		// 원거리 명중
		info.add(String.format("%s", String.valueOf(CharacterController.toStatDex(pc, "isHitFigure"))));
		// 원거리 치명타
		info.add(String.format("%s", String.valueOf(pc.getTotalCritical(true))));
		
	
		// 마법 데미지
		info.add(String.format("%s", String.valueOf(CharacterController.toStatInt(pc, "magicDamage"))));
		// 마법 명중
		info.add(String.format("%s", String.valueOf(Math.round(pc.getDynamicMagicHit()))));
		// 마법 치명타
		info.add(String.format("%s", String.valueOf(CharacterController.toStatInt(pc, "magicCritical"))));
		// 마법 보너스
		info.add(String.format(" %s", String.valueOf(CharacterController.toStatInt(pc, "magicBonus"))));
		// MP 소모 감소
		info.add(String.format("%s", String.valueOf(CharacterController.toStatInt(pc, "mpDecrease"))));

		// 데미지 감소
		info.add(String.format("%s", String.valueOf(pc.getTotalReduction())));
		// 데미지 감소 무시
		info.add(String.format("%s", String.valueOf(pc.getDynamicIgnoreReduction())));
		// PvP 데미지
		info.add(String.format("%s", String.valueOf(pc.getDynamicAddPvpDmg())));
		// PvP 데미지 감소
		info.add(String.format("%s", String.valueOf(pc.getDynamicAddPvpReduction())));
		
		
		// 스턴 명중
		info.add(String.format("%s", String.valueOf(Math.round((pc.getDynamicStunHit() * 100) + pc.getKnightSkillHit() ))));
		
		// 스턴 내성
		info.add(String.format("%s", String.valueOf(Math.round(pc.getTotalStunResist() * 100))));
		
		// 정령 적중
		info.add(String.format("%s", String.valueOf(Math.round(pc.getElfSkillHit()))));
		
//		//마법명중
//		info.add(String.format("%s", String.valueOf(Math.round(pc.getDynamicExp()* 100))));

		// HP 회복(틱)
		info.add(String.format("%s", String.valueOf(pc.getHpTic())));
		// HP 물약 회복 증가
		info.add(String.format("%s", String.valueOf(pc.getTotalHpPotion())));
		// MP 회복(틱)
		info.add(String.format("%s", String.valueOf(pc.getMpTic())));

		
		o.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), o, "character", null, info));
	}
	
	/**
	 * 몬스터 드랍 방식 교체
	 * 
	 * @param o
	 * @param st
	 */

	static private void monsterDrop(object o, StringTokenizer st) {
		try {
			long l1 = java.lang.System.currentTimeMillis() / 1000L;

			String str1 = st.nextToken();

			Monster m = MonsterDatabase.find3(str1);

			if (m != null) {
				if (m.getDropList().size() > 0) {
					o.toSender(MonDrop_System.clone(BasePacketPooling.getPool(MonDrop_System.class), m));
				} else {
					ChattingController.toChatting(o, "해당 몬스터는 드랍목록이 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
			o.setDelaytime(l1);
		} catch (Exception localException1) {
			if (o != null)
				ChattingController.toChatting(o, ".몹드랍 몬스터명", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 서버 정보
	 * 2017-10-12
	 * by all-night
	 */
	static private void serverInfo(object o) {
		List<String> info = new ArrayList<String>();

		info.clear();
		// 서버명
		info.add(ServerDatabase.getName() != null ? ServerDatabase.getName() : "호두서버");
		// 운영자 ID
		info.add("메티스");

		// 레벨 제한
		info.add(String.valueOf(Lineage.level_max));
		// 서버 최고 레벨
		info.add(String.valueOf(RankController.rank_top_level));
		// 무기 인챈트 제한
		info.add(String.valueOf(Lineage.item_enchant_weapon_max));
		// 방어구 인챈트 제한
		info.add(String.valueOf(Lineage.item_enchant_armor_max));
		// 장신구 인챈트 제한
		info.add(String.valueOf(Lineage.item_enchant_accessory_max));
		// 혈맹 최대 가입 인원
		info.add(String.valueOf(Lineage.clan_max));
		// 파티 추가 경험치
		info.add(String.format("%.1f", Lineage.rate_party));
		// 기란 감옥 일일 이용 시간(분)
		info.add(String.valueOf(Lineage.giran_dungeon_time / 60));
		// 기란 감옥 초기화 시간
		String time = null;
		
		if (Lineage.giran_dungeon_inti_time < 12)
			time = "오전 " + String.valueOf(Lineage.giran_dungeon_inti_time) + "시";
		else
			time = "오후 "+ String.valueOf(Lineage.giran_dungeon_inti_time - 12) + "시";
		
		info.add(time);

		o.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), o, "serverInfo", null, info));
	}

	/**
	 * 
	 * @param
	 * @return
	 * 2017-08-29
	 * by all_night.
	 */
	static public void serverOpenWait() {
		Lineage.open_wait = true;
		Lineage.level_max = 10;
		Lineage.rate_drop = 0;
		Lineage.rate_aden = 0;
	}

	/**
	 * 
	 * @param
	 * @return
	 * 2017-08-29
	 * by all_night.
	 */
	static public void serverOpen() {
		if (Lineage.open_wait) {
			Lineage.open_wait = false;
			Lineage.init(true);
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "서버 오픈대기 종료. 지금부터 정상 배율이 적용됩니다."));
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("서버에 오신것을 진심으로 환영합니다.")));
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "F1 도움말과 인베토리에 서버가이드 서버공지는 꼭 한번씩 확인 부탁드립니다."));
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "사냥터 이동은 f1 또는 인벤토리에 사냥터이동을 이용해주세요."));
		}
	}

	/**
	 * 
	 * @param
	 * @return
	 * 2017-08-29
	 * by all_night.
	 */
	static public void serverReload() {
		ServerReloadDatabase.reLoad();
		for (PcInstance pc : World.getPcList()) {
			pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
		}
	}

	/**
	 * 
	 * @param
	 * @return
	 * 2017-08-29
	 * by all_night.
	 */
	static public void serverMagicReload() {
		for (PcInstance pc : World.getPcList()) {
			CharactersDatabase.reLoadSaveSkill(pc);
			SkillController.toWorldOut(pc);
		}
		SkillDatabase.reLoadSkill();
		for (PcInstance pc : World.getPcList()) {
			SkillController.toWorldJoin(pc);
			CharactersDatabase.readSkill(pc);
		}
	}
	
	static public void setKingdomWar() {
		for (Kingdom kingdom : KingdomController.getList()) {
			if (!kingdom.isWar())
				kingdom.toStartWar(System.currentTimeMillis());
			else
				kingdom.toStopWar(System.currentTimeMillis());
		}
	}
	
	static public void inventorySetting(object o) {
		try {
			List<ItemInstance> list = o.getInventory().getList();
			List<ItemInstance> tempList = new ArrayList<ItemInstance>();

			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("아데나")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("베릴")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("도플갱어 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("가드리아 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("드루가 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("플래바포 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("발키리 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("필리아 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("플래티넘데스 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("황금도플 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("데스나이트 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("다크엘프 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("달의질리언 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("철의아툰 변신 카드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("칼렉풀기")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("자동 칼질")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("자칼트리")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("자칼스턴")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("자칼선버스트")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("자칼뱀파")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("자동 물약")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("사냥터 이동")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("서버 가이드")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("서버 업데이트 내역")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[필독]서버 공지")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("혈맹 파티")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}

			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[자힐] 힐")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[자힐] 익스트라 힐")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[자힐] 그레이터 힐")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[자힐] 풀 힐")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[자뮨] 이뮨 투 함")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[편리성] 버서커")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[편리성] 군업")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[편리성] 스톰샷")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[편리성] 네이쳐스 터치")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[편리성] 워터 라이프")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("[편리성] 워터 라이프")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("기란 마을 이동 부적")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("시장 이동 부적")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			// 아이템 제거 막대.
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof Item_Remove_Wand) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			// 레벨업 지원.
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof Exp_support) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("장비 스왑")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			// 착용중인 무기
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null &&  i instanceof ItemWeaponInstance && i.isEquipped()) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			// 착용중인 방어구
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof ItemArmorInstance && i.isEquipped()) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("아인하사드의 룬")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}
			
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof ItemArmorInstance && i.isEquipped()) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}

			// 화살
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof Arrow) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			
			// 마안류
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().contains("마안")) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}

			// 미착용 무기
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof ItemWeaponInstance) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}

			// 미착용 방어구
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof ItemArmorInstance) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 물약
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof HealingPotion) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 초록 물약
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof HastePotion) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 용기 물약
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof BraveryPotion) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 와퍼
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof ElvenWafer) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase("샴페인")) {
					synchronized (list) {
						if (!tempList.contains(i)) {
							tempList.add(i);
							list.remove(i);
							break;
						}
					}
				}
			}

			// 지혜 물약
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof WisdomPotion) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 파란 물약
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i instanceof BluePotion) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 해독제
			for (ItemInstance i : list) {
				if (i instanceof CurePoisonPotion) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			
			// 물약류
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().contains("물약")) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			
			// 경험치 지급단
			for (ItemInstance i : list) {
				if (i instanceof Exp_marble) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			
			// 변신 주문서
			for (ItemInstance i : list) {
				if (i instanceof ScrollPolymorph) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 순간이동 주문서
			for (ItemInstance i : list) {
				if (i instanceof ScrollLabeledVenzarBorgavve) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 귀환 주문서
			for (ItemInstance i : list) {
				if (i instanceof ScrollLabeledVerrYedHorae) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 데이
			for (ItemInstance i : list) {
				if (i instanceof ScrollLabeledDaneFools) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 젤
			for (ItemInstance i : list) {
				if (i instanceof ScrollLabeledZelgoMer) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 장신구 마법 주문서
			for (ItemInstance i : list) {
				if (i instanceof ScrollOfAccessory) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 이동 주문서 및 부적
			for (ItemInstance i : list) {
				if (i instanceof ScrollTeleport) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			// 오만의 탑 이동 주문서
			for (ItemInstance i : list) {
				if (i instanceof TOITeleportScroll) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			
			// 주문서류
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && i.getItem().getName().contains("주문서")) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}	
			
			// 마법인형
			for (ItemInstance i : list) {
				if (i instanceof MagicDoll) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			
			// 마법서류
			for (ItemInstance i : list) {
				if (i != null && i.getItem() != null && (i.getItem().getName().contains("주문서") || i.getItem().getName().contains("기술서"))) {
					if (!tempList.contains(i))
						tempList.add(i);
				}
			}
			
			// 기타 아이템
			for (ItemInstance i : list) {
				if (!tempList.contains(i))
					tempList.add(i);
			}

			for (ItemInstance i : tempList)
				o.toSender(S_InventoryDelete.clone(BasePacketPooling.getPool(S_InventoryDelete.class), i));

			for (ItemInstance i : tempList)
				o.toSender(S_InventoryAdd.clone(BasePacketPooling.getPool(S_InventoryAdd.class), i));
		} catch (Exception e) {
		}
	}
	
	public static void toSearchItem(object o, StringTokenizer st) {
		int i = Integer.parseInt(st.nextToken());
		String str = st.nextToken();

		PreparedStatement stt = null;
		ResultSet rs = null;
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			switch (i) {
			case 0:
				stt = con.prepareStatement(new StringBuilder().append("SELECT * FROM item WHERE 구분1 NOT IN ('armor', 'weapon') AND REPLACE(아이템이름, ' ', '') LIKE '%").append(str).append("%'").toString());
				break;
			case 1:
				stt = con.prepareStatement(new StringBuilder().append("SELECT * FROM item WHERE 구분1 IN ('weapon') AND REPLACE(아이템이름, ' ', '') LIKE '%").append(str).append("%'").toString());
				break;
			case 2:
				stt = con.prepareStatement(new StringBuilder().append("SELECT * FROM item WHERE 구분1 IN ('armor') AND REPLACE(아이템이름, ' ', '') LIKE '%").append(str).append("%'").toString());
			}
			rs = stt.executeQuery();
			int m = 0;
			for (int j = 0; rs.next(); j++) {
				m = j;
				ChattingController.toChatting(o,
						new StringBuilder().append("\\fR명칭: ").append(rs.getString("아이템이름")).append(" \\fT--> ").append(rs.getString("구분1")).append("(").append(rs.getString("구분2")).append(")").toString(), 20);
			}
			ChattingController.toChatting(o, new StringBuilder().append("총 [").append(m).append("]개의 데이터가 검색되었습니다.").toString(), Lineage.CHATTING_MODE_MESSAGE);
		} catch (Exception e) {
			lineage.share.System.printf("%s : toSearchItem(object o, StringTokenizer st)\r\n", new Object[] { CommandController.class.toString() });
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, stt, rs);
		}
	}
	static private void 포인트(object o) {

				try {
					String account = o.getName();
				
		
					
					String pccheck=AccountDatabase.getid(account);
					int point = (int) AccountDatabase.userpointcheck(pccheck);
					
					ChattingController.toChatting(o,  "보유하신 포인트는 " +point+" 포인트 입니다" , Lineage.CHATTING_MODE_MESSAGE);
			
				} catch (Exception e) {			
					return;
				}
				
			
		
	
	}
	static private void 포인트지급(object o, object pc, StringTokenizer st) {
		if (o != null) {

				try {
					String account = pc.getName();
					int point = Integer.valueOf(st.nextToken());
		
					
					String pccheck=AccountDatabase.getid(account);
					AccountDatabase.userpoint(pccheck,point);
					
					ChattingController.toChatting(o, pc.getName()+"님에게 "+ point + "마일리지를 지급하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(pc, "운영자님이 "+ point + "마일리지를 지급하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
			
				} catch (Exception e) {
					ChattingController.toChatting(o, Lineage.command + "마일리지지급 캐릭터닉네임 포인트", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
			
		}
	
	}
	public static void 포인트회수(object o, object pc, StringTokenizer st) {
		if (o != null) {

				try {
					String account = pc.getName();
					int point = Integer.valueOf(st.nextToken());
		
					
				
					String pccheck=AccountDatabase.getid(account);
					
			
					AccountDatabase.userpointbuy(pccheck,point);
					ChattingController.toChatting(o, pc.getName()+"님에게 "+ point + "마일리지를 회수하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(pc, "운영자님이 "+ point + "마일리지를 회수하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
				
		
				} catch (Exception e) {
					ChattingController.toChatting(o, Lineage.command + "마일리지회수 캐릭터닉네임 회수할포인트", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
			
		}
	
	}
	public static void reload(object o, StringTokenizer st) {
		boolean is = true;
		String command = st.nextToken();
		
		switch (command) {
		case "욕설필터":
			ChattingController.reload();
			break;
		case "텔북":
			DungeontellbookDatabase.reload();
			break;
		case "콘프":
			Lineage.init(true);
			break;
		case "밸런스콘프":
			Lineage_Balance.init();
			break;
		case "서버메세지":
			NoticeController.reload();
			break;
		case "백그라운드":
			BackgroundDatabase.reload();
			break;
		case "낚시":
			FishItemListDatabase.reload();
			break;
		case "아이템":
			ItemDatabase.reload();
			break;
		case "번들":
			ItemBundleDatabase.reload();
			break;
		case "찬스번들":
			ItemChanceBundleDatabase.reload();
			break;
		case "템스킬":
		case "아이템스킬":
			ItemSkillDatabase.reload();
			break;
		case "몹드랍":
			MonsterDropDatabase.reload();
			break;
		case "보스":
			MonsterBossSpawnlistDatabase.reload();
			break;
		case "몹스킬":
			MonsterSkillDatabase.reload();
			break;
		case "npc":	
		case "엔피씨":
			NpcDatabase.reload();
			Npc_promotion.reload();
			break;
		case "변신":
			PolyDatabase.reload();
			break;
		case "로봇":
			RobotController.reloadPcRobot();
			RobotController.reloadRobotBook();
			RobotController.reloadPoly();
			RobotController.reloadRobotSkill();
			break;
		case "스킬":
			serverMagicReload();
			break;
		case "서먼":
			SummonListDatabase.reload();
			break;
		case "공지":
			ServerNoticeDatabase.reload();
			break;
		case "팀대전":
			TeamBattleDatabase.reload();
			break;
		case "타임던전":
			TimeDungeonDatabase.reload();
			break;
		case "상점":
			NpcShopDatabase.reload();
			break;
		case "몹":
		case "몬스터":
			MonsterDatabase.reload();
			break;
		case "무인혈맹":
			RobotClanController.reload();
			break;
		case "스핵":
			HackNoCheckDatabase.reload();
			break;
		case "몹스폰":
			try {
				int map = Integer.valueOf(st.nextToken());
				MonsterSpawnlistDatabase.reload(map);
			} catch (Exception e) {
				ChattingController.toChatting(o, Lineage.command + "리로드 몹스폰 맵번호", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}		
			break;
		case "전체스폰":
			MonsterSpawnlistDatabase.reload();
			break;
		case "아이템메세지":
		case "아이템메시지":
			ItemDropMessageDatabase.reload();
			break;
		default:
			if (o != null) {
				is = false;
				ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ  리로드  ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "[콘프, 밸런스콘프, 아이템, 엔피씨, 변신, 스킬, 상점, 보스]", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "[몬스터, 몹드랍, 몹스킬, 백그라운드, 번들, 찬스번들, 서먼]", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "[공지, 서버메세지, 템스킬, 낚시, 팀대전, 타임던전, 로봇]", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "[무인혈맹, 스핵, 몹스폰, 전체스폰, 아이템메세지]", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
			}
			break;
		}		
		if (o != null && is)
			ChattingController.toChatting(o, String.format("%s 리로드 완료.", command), Lineage.CHATTING_MODE_MESSAGE);
	}
	
	/**
	 * 공성 체크
	 * 
	 * @param paramobject
	 * @param paramStringTokenizer
	 */
	public static void toKingdomWarCheck(object paramobject, StringTokenizer paramStringTokenizer) {
		for (Kingdom localKingdom : KingdomController.getList()) {
			if (localKingdom.isWar())
				ChattingController.toChatting(paramobject, String.format("%s : 공성이 진행중입니다.", new Object[] { localKingdom.getName() }), 20);
			else
				ChattingController.toChatting(paramobject, String.format("%s : 공성시간이 아닙니다.", new Object[] { localKingdom.getName() }), 20);

		}
	}

	/**
	 * 공성 시작
	 * 
	 * @param o
	 * @param st
	 */
	public static void toKingdomWarStart(object o, StringTokenizer st) {
		String str = st.nextToken();
		int i = 0;
		for (Kingdom k : KingdomController.getList()) {

			if (k.getName().replaceAll(" ", "").equalsIgnoreCase(str)) {
				if (k.isWar()) {
					i = 1;
					ChattingController.toChatting(o, String.format("%s : 공성이 이미 진행중입니다.", str), Lineage.CHATTING_MODE_MESSAGE);
					return;
				} else {
					k.toStartWar(System.currentTimeMillis());
					k.setWarDay(Calendar.getInstance().getTime().getTime());

					i = 1;
					if (o != null)
						ChattingController.toChatting(o, String.format("%s : 공성이 시작되었습니다.", str), Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		}
		if ((o != null) && (i == 0))
			ChattingController.toChatting(o, String.format("%s : 존재하지 않는 성이름입니다.", str), Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 공성 종료
	 * 
	 * @param o
	 * @param st
	 */
	public static void toKingdomWarStop(object o, StringTokenizer st) {
		String str = st.nextToken();
		int i = 0;
		for (Kingdom k : KingdomController.getList()) {
			if ((str.equals("전체")) || (k.getName().replaceAll(" ", "").equalsIgnoreCase(str))) {
				if (!k.isWar()) {
					i = 1;
					ChattingController.toChatting(o, String.format("%s : 공성이 진행중이지 않습니다.", k.getName()), Lineage.CHATTING_MODE_MESSAGE);
				} else {
					k.toStopWar(java.lang.System.currentTimeMillis());
					i = 1;
					if (o != null)
						ChattingController.toChatting(o, String.format("%s : 공성이 종료되었습니다.", k.getName()), Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		}
		if ((o != null) && (!str.equals("전체")) && (i == 0))
			ChattingController.toChatting(o, String.format("%s : 존재하지 않는 성이름입니다.", str), Lineage.CHATTING_MODE_MESSAGE);
	}

	
	/**
	 * 개인상점 강제 철거
	 * 2019-07-25
	 * by connector12@nate.com
	 */
	public static void removePcShop(object o, StringTokenizer st) {
		String pcName = null;
		
		try {
			pcName = st.nextToken();
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "상점삭제 캐릭명", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		PcInstance pc = World.findPc(pcName);
		
		if (pc != null) {
			// pc가 존재할 경우.
			PcShopInstance ps = PcMarketController.getShop(pc.getObjectId());
			
			if (ps != null) {
				if (ps.getListSize() > 0) {
					for (PcShop s : ps.getShopList().values()) {
						if (s.getItem() == null)
							continue;
						Item item = ItemDatabase.find(s.getItem().getName());
						if (item != null) {
							ItemInstance temp = ItemDatabase.newInstance(item);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setCount(s.getInvItemCount());
							temp.setEnLevel(s.getInvItemEn());
							temp.setBless(s.getInvItemBress());
							temp.setDefinite(true);
							// 인벤에 등록처리.
							pc.getInventory().append(temp, temp.getCount());
						}
					}
					ps.clearShopList();
				}
				ps.close();
				PcMarketController.removeShop(pc.getObjectId());
				ChattingController.toChatting(pc, "\\fR운영자에 의해 상점이 종료되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		} else {
			// pc가 존재하지 않을 경우.
			String shopName = pcName + "의 상점";
			PcShopInstance ps = null;
			long objId = 0;
			
			for (PcShopInstance tempPs : PcMarketController.getShopList().values()) {
				if (tempPs.getName().equalsIgnoreCase(shopName)) {
					ps = tempPs;
					break;
				}	
			}
			
			if (ps != null) {
				PreparedStatement stt = null;
				ResultSet rs = null;
				Connection con = null;
				objId = ps.getPc_objectId();
				
				try {
					con = DatabaseConnection.getLineage();
					
					for (PcShop pcs : ps.getShopList().values()) {
						if (pcs.getItem() != null && pcs.getItem().isPiles()) {
							stt = con.prepareStatement("SELECT * FROM characters_inventory WHERE cha_objId=? AND name=? AND en=? AND bress=?");	
							stt.setLong(1, objId);
							stt.setString(2, pcs.getItem().getName());
							stt.setInt(3, pcs.getInvItemEn());
							stt.setInt(4, pcs.getInvItemBress());
							rs = stt.executeQuery();
							
							if (rs.next()) {		
								long count = rs.getLong("count");
								
								stt.close();
								stt = con.prepareStatement("UPDATE characters_inventory SET count=? WHERE cha_objId=? AND name=? AND en=? AND bress=?");
								stt.setLong(1, count + pcs.getInvItemCount());
								stt.setLong(2, objId);
								stt.setString(3, pcs.getItem().getName());
								stt.setInt(4, pcs.getInvItemEn());
								stt.setInt(5, pcs.getInvItemBress());
								stt.executeUpdate();
							}
						} else if (pcs.getItem() != null) {
							stt = con.prepareStatement("INSERT INTO characters_inventory SET objId=?, cha_objId=?, cha_name=?, name=?, count=?, en=?, definite=1, bress=?, 구분1=?, 구분2=?");	
							stt.setLong(1, pcs.getInvItemObjectId());
							stt.setLong(2, objId);
							stt.setString(3, pcName);
							stt.setString(4, pcs.getItem().getName());
							stt.setLong(5, pcs.getInvItemCount());
							stt.setInt(6, pcs.getInvItemEn());
							stt.setInt(7, pcs.getInvItemBress());
							stt.setString(8, pcs.getItem().getType1());
							stt.setString(9, pcs.getItem().getType2());
							stt.executeUpdate();
						}
					}

					ps.clearShopList();
					ps.close();
					PcMarketController.removeShop(objId);
					
					if (o != null)
						ChattingController.toChatting(o, String.format("[%s] 개인상점 종료", pcName), Lineage.CHATTING_MODE_MESSAGE);
				} catch (Exception e) {
					lineage.share.System.printf("%s : removePcShop(object o, StringTokenizer st)\r\n", CommandController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, stt, rs);
				}
			} else {
				if (o != null)
					ChattingController.toChatting(o, String.format("[%s]의 개인상점이 존재하지 않습니다. ", pcName), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	
	/**
	 * 장비 스왑 명령어
	 * 2019-08-01
	 * by connector12@nate.com
	 */
	public static void swap(object o) {
		PcInstance pc = (PcInstance) o;	
		NpcSpawnlistDatabase.itemSwap.toTalk(pc, null);
	}
	
	/**
	 * 보스 리스트
	 * 2019-08-20
	 * by connector12@nate.com
	 */
	public static void bossList(object o) {
		List<String> bossList = new ArrayList<String>();
		bossList.clear();
		
		for (BossSpawn bossSpawn : MonsterBossSpawnlistDatabase.getSpawnList()) {
			bossList.add(String.format("[%s]", bossSpawn.getMonster()));
			bossList.add(String.format("%s", bossSpawn.getSpawnTime()));
			bossList.add(String.format("요일: %s", bossSpawn.getSpawnDay().trim()));
			bossList.add(" ");
		}
		
		for (int i = 0; i < 150; i++)
			bossList.add(" ");
		
		o.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), o, "bossList", null, bossList));
		
		if (BossController.getBossList().size() < 1) {
			ChattingController.toChatting(o, "\\fR생존한 보스가 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		ChattingController.toChatting(o, "\\fRㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
		
		for (MonsterInstance boss: BossController.getBossList())
			ChattingController.toChatting(o, String.format("\\fY%s %s", Util.getMapName(boss), boss.getMonster().getName()), Lineage.CHATTING_MODE_MESSAGE);
		
		ChattingController.toChatting(o, "\\fRㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
	}

	public static void pList(object o) {

		int count = 0;
		
		for (MonsterInstance boss: BossController.getBossList()){
			if(boss.getMonster().getName().equalsIgnoreCase("잭 오랜 턴")){
				count = count +1;
			
				ChattingController.toChatting(o, String.format("남은 잭오랜턴 %d 마리" , count), Lineage.CHATTING_MODE_MESSAGE);
			}

		}
			
		
		ChattingController.toChatting(o, "\\fRㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
		ChattingController.toChatting(o, String.format("남은 잭오랜턴 %d 마리" , count), Lineage.CHATTING_MODE_MESSAGE);
	
		ChattingController.toChatting(o, "\\fRㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
	}
	/**
	 * 원격 교환.
	 * 2019-08-30
	 * by connector12@nate.com
	 * 수정 야도란
	 */
	public static void trade(object o, StringTokenizer st) {
		if (o instanceof PcInstance) {
			PcInstance pc = (PcInstance) o;
			String name = null;
			
			try {
				name = st.nextToken();
				PcInstance use = World.findPc(name);
				//야도란 자신한테는 교환 불가능
				if (name.equals(pc.getName())) {
					ChattingController.toChatting(o, "자신에게 거래 신청은 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				if (use != null && !(use instanceof PcRobotInstance)) {
					if (!use.isWorldDelete() && !use.isDead() && !use.isLock() && use.getInventory() != null) {
						if (!pc.isBuffCriminal() && !use.isBuffCriminal()) {
							if (!pc.isTransparent() && !use.isTransparent())
								TradeController.toTrade(pc, use);
						} else {
							ChattingController.toChatting(o, "전투중일 경우 교환이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
					}
				} else {
				
					if (use == null) {
						if (name != null)
							ChattingController.toChatting(o, String.format("[%s] 캐릭터는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
				}
			} catch (Exception e) {
				ChattingController.toChatting(o, Lineage.command + "교환 캐릭명", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
		}
	}
	
	public static void macro(object o, StringTokenizer st) {
		if (o.getLevel() < Lineage.chatting_level_global) {
			ChattingController.toChatting(o, String.format("채팅 매크로는 %d레벨 이상 가능합니다.", Lineage.chatting_level_global), Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		try {
			StringBuffer sb = new StringBuffer();
			String msg = null;
			
			while (st.hasMoreTokens())
				sb.append(st.nextToken() + " ");
			
			msg = sb.toString().trim();
			
			if (msg.equalsIgnoreCase("켬") || msg.equalsIgnoreCase("끔")) {
				if (msg.equalsIgnoreCase("켬")) {
					if (!o.isMacro)
						o.isMacro = true;
				}
				
				if (msg.equalsIgnoreCase("끔")) {
					if (o.isMacro)
						o.isMacro = false;
				}

				ChattingController.toChatting(o, String.format("[채팅 매크로] %s활성화", o.isMacro ? "": "비"), Lineage.CHATTING_MODE_MESSAGE);
				
				if (o.isMacro && o.macroMsg == null)
					ChattingController.toChatting(o, "현재 설정된 매크로 메세지가 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;	
			}

			if (msg.length() > 0) {
				o.isMacro = true;
				o.macroMsg = msg;
				ChattingController.toChatting(o, "채팅 매크로가 설정완료되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, Lineage.command + "매크로 켬/끔", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, Lineage.command + "매크로 멘트", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "매크로 켬/끔", Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(o, Lineage.command + "매크로 멘트", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	public static void macroOff(object o, StringTokenizer st) {
		try {
			String name = st.nextToken();
			PcInstance pc = World.findPc(name);
			
			if (pc == null) {
				ChattingController.toChatting(o, String.format("'%s' 캐릭터는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
				return;
			} else {
				pc.isMacro = false;
				ChattingController.toChatting(o, String.format("[채팅 매크로] '%s' 캐릭터 %s활성화", name, pc.isMacro ? "" : "비"), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "매크로끔 아이디", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	public static void setWeather(object o, StringTokenizer st) {
		try {
			int weather = Integer.valueOf(st.nextToken());
			LineageServer.weather = weather;
			World.toSender(S_Weather.clone(BasePacketPooling.getPool(S_Weather.class), LineageServer.weather));
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "날씨 0:맑음 / 1:눈조금 / 2:눈많이 / 3:눈펑펑", Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(o, Lineage.command + "날씨 17:비조금 / 18:비많이 / 19:폭우", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	public static void spot(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("시작")) {
				if (!SpotController.spot.isStart)
					SpotController.spot.start(System.currentTimeMillis());
				else
					ChattingController.toChatting(o, "스팟 쟁탈전은 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
			} else if (msg.equalsIgnoreCase("종료")) {
				if (SpotController.spot.isStart)
					SpotController.spot.end(true);
				else
					ChattingController.toChatting(o, "스팟 쟁탈전은 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "스팟 시작/종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	static public void toBuffremove(object o) {
		
		BuffController.removeAll(o);
	}
	/**
	 * 후원 자동지급.
	 * 2019-10-31
	 * by connector12@nate.com
	 */
	public static void donation(object o, StringTokenizer st) {
		if (!Mysql.is_donation) {
			return;
		}
		
		try {
			String name = st.nextToken();
			int price = Integer.valueOf(st.nextToken());
			boolean result = false;

			PreparedStatement stt = null;
			ResultSet rs = null;
			Connection con = null;
			try {
				con = DatabaseConnection.getDonation();
				stt = con.prepareStatement("SELECT * FROM donation_history WHERE name=? AND price=? AND complete=0");
				stt.setString(1, name);
				stt.setInt(2, price);
				rs = stt.executeQuery();

				if (rs.next())
					result = true;

				if (result && price > 0) {
					PcInstance pc = (PcInstance) o;
					Item i = ItemDatabase.find("아데나");
					long count = (long) (price * 1);

					if (count > 0) {
						if (i != null && pc != null) {
							boolean is = false;
							stt.close();
							stt = con.prepareStatement("UPDATE donation_history SET complete=1, cha_name=?, 지급아이템=?, 지급수량=? WHERE name=? AND price=? AND complete=0");
							stt.setString(1, pc.getName());
							stt.setString(2, i.getName());
							stt.setLong(3, count);
							stt.setString(4, name);
							stt.setInt(5, price);
							stt.executeUpdate();
							is = true;

							if (is) {
								ItemInstance temp = pc.getInventory().find(i.getName(), 1, i.isPiles());

								if (temp != null && (temp.getBless() != 1 || temp.getEnLevel() != 0))
									temp = null;

								if (temp == null) {
									// 겹칠수 있는 아이템이 존재하지 않을경우.
									if (i.isPiles()) {
										temp = ItemDatabase.newInstance(i);
										temp.setObjectId(ServerDatabase.nextItemObjId());
										temp.setBless(1);
										temp.setEnLevel(0);
										temp.setCount(count);
										temp.setDefinite(true);
										pc.getInventory().append(temp, true);
									} else {
										for (int idx = 0; idx < count; idx++) {
											temp = ItemDatabase.newInstance(i);
											temp.setObjectId(ServerDatabase.nextItemObjId());
											temp.setBless(1);
											temp.setEnLevel(0);
											temp.setDefinite(true);
											pc.getInventory().append(temp, true);
										}
									}
								} else {
									// 겹치는 아이템이 존재할 경우.
									pc.getInventory().count(temp, temp.getCount() + count, true);
								}

								// 알림.
								ChattingController.toChatting(o, String.format("지급완료: %s(%d)", i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
							}
						} else {
							ChattingController.toChatting(o, "지급받을 아이템이 존재하지않습니다. 운영자에게 문의하시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else {
						ChattingController.toChatting(o, "입금한 금액 잘못되었습니다. 운영자에게 문의하시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
				} else {
					ChattingController.toChatting(o, "입금 내역이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : donation(object o, StringTokenizer st) [캐릭터: %s]\r\n", CommandController.class.toString(), o.getName());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, stt, rs);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "지급 입금자명 입금액", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 타일 뷰어.
	 * 2019-11-15
	 * by connector12@nate.com
	 */
	static private void tileViewer(object o, StringTokenizer st) {
		try {
			int value = Integer.valueOf(st.nextToken());
			World.tileValue = value;
			
			if (World.tileValue > -1) {
				ChattingController.toChatting(o, String.format("\\fR[타일뷰어] 타일값:%d 설정.", World.tileValue), Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "\\fR타일값이 -1일 경우 타일뷰어 종료.", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, "\\fR타일뷰어 종료.", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			if (o != null)
				ChattingController.toChatting(o, Lineage.command + "타일뷰어 타일값", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 테베 시작/종료
	 * 2019-11-28
	 * by connector12@nate.com
	 */
	public static void 테베(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("시작")) {
				if (!TebeController.isOpen) {
					TebeController.isOpen = true;
					TebeController.tebeEndTime = System.currentTimeMillis() + (1000 * Lineage.tebe_play_time);
					TebeController.sendMessage();
				} else {
					ChattingController.toChatting(o, "테베라스는 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else if (msg.equalsIgnoreCase("종료")) {
				if (TebeController.isOpen) {
					TebeController.isOpen = false;
					TebeController.tebeEndTime = System.currentTimeMillis();
					TebeController.sendMessage();
				} else {
					ChattingController.toChatting(o, "테베라스는 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "테베 시작/종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	public static void 얼던(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("시작")) {
				if (!IceDungeonController.isOpen) {
					IceDungeonController.isOpen = true;
					IceDungeonController.iceEndTime = System.currentTimeMillis() + (1000 * Lineage.ice_play_time);
					IceDungeonController.sendMessage();
				} else {
					ChattingController.toChatting(o, "얼음여왕 던전은 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else if (msg.equalsIgnoreCase("종료")) {
				if (IceDungeonController.isOpen) {
					IceDungeonController.isOpen = false;
					IceDungeonController.iceEndTime = System.currentTimeMillis();
					IceDungeonController.sendMessage();
				} else {
					ChattingController.toChatting(o, "얼음여왕 던전은 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "얼던 시작/종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	
	/**
	 * 지옥 시작/종료
	 * 2019-11-28
	 * by connector12@nate.com
	 */
	public static void 지옥(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("시작")) {
				if (!HellController.isOpen) {
					HellController.isOpen = true;
					HellController.hellEndTime = System.currentTimeMillis() + (1000 * Lineage.hell_play_time);
					HellController.sendMessage();
				} else {
					ChattingController.toChatting(o, "지옥은 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else if (msg.equalsIgnoreCase("종료")) {
				if (HellController.isOpen) {
					HellController.isOpen = false;
					HellController.hellEndTime = System.currentTimeMillis();
					HellController.sendMessage();
				} else {
					ChattingController.toChatting(o, "지옥은 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "지옥 시작/종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	public static void 보물찾기(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("시작")) {
				if (!TreasureHuntController.isOpen) {
					TreasureHuntController.isOpen = true;
					TreasureHuntController.TreasuressEndTime = System.currentTimeMillis() + (1000 * Lineage.Treasuress_play_time);
					TreasureHuntController.sendMessage();
				} else {
					ChattingController.toChatting(o, "보물찾기는 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else if (msg.equalsIgnoreCase("종료")) {
				if (TreasureHuntController.isOpen) {
					TreasureHuntController.isOpen = false;
					TreasureHuntController.TreasuressEndTime = System.currentTimeMillis();
					TreasureHuntController.sendMessage();
				} else {
					ChattingController.toChatting(o, "보물찾기는 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "보물찾기 시작/종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	public static void 펭귄(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("시작")) {
				if (!PenguinHuntingController.isOpen) {
					PenguinHuntingController.isOpen = true;
					PenguinHuntingController.phuntEndTime = System.currentTimeMillis() + (1000 * Lineage.phunt_play_time);
					PenguinHuntingController.sendMessage();
				} else {
					ChattingController.toChatting(o, "펭귄서식지는 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else if (msg.equalsIgnoreCase("종료")) {
				if (PenguinHuntingController.isOpen) {
					PenguinHuntingController.isOpen = false;
					PenguinHuntingController.phuntEndTime = System.currentTimeMillis();
					PenguinHuntingController.sendMessage();
				} else {
					ChattingController.toChatting(o, "펭귄서식지는 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "펭귄 시작/종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	public static void 타임이벤(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			int num = Util.random(1, 5);
			if (msg.equalsIgnoreCase("시작")) {
				if (!TimeEventController.isOpen) {
					TimeEventController.isOpen = true;
					TimeEventController.num = num;
					
					TimeEventController.event_timeEndTime = System.currentTimeMillis() + (1000 * Lineage.time_event_play_time);
					TimeEventController.sendMessage();
				} else {
					ChattingController.toChatting(o, "타임이벤트는 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else if (msg.equalsIgnoreCase("종료")) {
				if (TimeEventController.isOpen) {
					TimeEventController.isOpen = false;
					TimeEventController.num = 0;
					TimeEventController.event_timeEndTime = System.currentTimeMillis();
					TimeEventController.sendMessage();
				} else {
					ChattingController.toChatting(o, "타임이벤트는 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "타임이벤 시작/종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	public static void 타임이벤2(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			int num = Integer.valueOf(st.nextToken());
			if (msg.equalsIgnoreCase("시작")) {
				if (!TimeEventController.isOpen) {
					TimeEventController.isOpen = true;
					TimeEventController.num = num;
					
					TimeEventController.event_timeEndTime = System.currentTimeMillis() + (1000 * Lineage.time_event_play_time);
					TimeEventController.sendMessage();
				} else {
					ChattingController.toChatting(o, "타임이벤트는 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} 
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "타임이벤설정 시작 번호[1~5]", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	public static void 월드보스(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("시작")) {
				if (!WorldBossController.isOpen) {
					WorldBossController.isOpen = true;
					
				
					WorldBossController.worldEndTime = System.currentTimeMillis() + (1000 * Lineage.world_play_time);
					WorldBossController.sendMessage();
				} else {
					ChattingController.toChatting(o, "월드보스는 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else if (msg.equalsIgnoreCase("종료")) {
				if (WorldBossController.isOpen) {
					WorldBossController.isOpen = false;
					WorldBossController.isWait = false;
					for (MonsterInstance boss: BossController.getBossList()){
						
						if(boss.getMonster().getName().equalsIgnoreCase("월드보스")){
							
							boss.toAiThreadDelete();
							World.removeMonster(boss);
							World.remove(boss);
							BossController.toWorldOut(boss);

						}
						
					}
					WorldBossController.worldEndTime = System.currentTimeMillis();
					WorldBossController.sendMessage();
				} else {
					ChattingController.toChatting(o, "월드보스는 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "월드보스 시작/종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	/**
	 * 악영 시작/종료
	 * 2019-11-28
	 * by connector12@nate.com
	 */
	public static void 악영(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("시작")) {
				if (!DevilController.isOpen) {
					DevilController.isOpen = true;
					DevilController.devilEndTime = System.currentTimeMillis() + (1000 * Lineage.devil_play_time);
					DevilController.sendMessage();
				} else {
					ChattingController.toChatting(o, "악마왕의 영토는 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else if (msg.equalsIgnoreCase("종료")) {
				if (DevilController.isOpen) {
					DevilController.isOpen = false;
					DevilController.devilEndTime = System.currentTimeMillis();
					DevilController.sendMessage();
				} else {
					ChattingController.toChatting(o, "악마왕의 영토는 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "악영 시작/종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	public static void 마족(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("시작")) {
				if (!DimensionController.isOpen) {
					DimensionController.isOpen = true;
					DimensionController.deteEndTime = System.currentTimeMillis() + (1000 * Lineage.devil_play_time);
					DimensionController.sendMessage();
				} else {
					ChattingController.toChatting(o, "마족신전은 이미 진행중입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else if (msg.equalsIgnoreCase("종료")) {
				if (DimensionController.isOpen) {
					DimensionController.isOpen = false;
					DimensionController.deteEndTime = System.currentTimeMillis();
					DimensionController.sendMessage();
				} else {
					ChattingController.toChatting(o, "마족신전은 진행중이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "악영 시작/종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 피뻥튀기.
	 * 2019-12-05
	 * by connector12@nate.com
	 */
	public static void plusHp(object o, StringTokenizer st) {
		try {
			String name = st.nextToken();
			PcInstance pc = World.findPc(name);
			
			if (pc != null) {
				pc.setDynamicHp(pc.getDynamicHp() + Integer.valueOf(st.nextToken()));
				pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
			} else {
				ChattingController.toChatting(o, String.format("'%s' 캐릭터는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "피뻥 캐릭터 수치", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 피뻥튀기.
	 * 2019-12-05
	 * by connector12@nate.com
	 */
	public static void plusMp(object o, StringTokenizer st) {
		try {
			String name = st.nextToken();
			PcInstance pc = World.findPc(name);
			
			if (pc != null) {
				pc.setDynamicMp(pc.getDynamicMp() + Integer.valueOf(st.nextToken()));
				pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
			} else {
				ChattingController.toChatting(o, String.format("'%s' 캐릭터는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "엠뻥 캐릭터 수치", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 개레이스
	 */
	public static void toDogGameControl(object paramobject, StringTokenizer paramStringTokenizer) {
		String str = paramStringTokenizer.nextToken();
		if (str.equals("시작")) {
			DogRaceController.isDogGame = true;
			DogRaceController.start();
		} else if (str.equals("종료")) {
			DogRaceController.isDogGame = false;
			DogRaceController.close();
		}
		if (paramobject != null)
			ChattingController.toChatting(paramobject, new StringBuilder().append("도그 레이스가 ").append(str).append("되었습니다.").toString(), 20);
	}
	
	/**
	 * 파티멘트 : ScreenRenderComposite 에서 사용중
	 * 
	 * @param o
	 * @param st
	 * @throws SQLException
	 */
	static private void PartyMent(object o, StringTokenizer st) {
		try {
			PcInstance pc = (PcInstance) o;
			String str = "";
			if (pc.isPartyMent()) {
				pc.setPartyMent(false);
				str = "비활성화";
			} else {
				pc.setPartyMent(true);
				str = "활성화";
			}
			ChattingController.toChatting(o, new StringBuilder().append("파티원 아이템 획득 메시지를 ").append(str).append(" 하였습니다.").toString(), Lineage.CHATTING_MODE_MESSAGE);
		} catch (Exception localException) {
			if (o != null)
				ChattingController.toChatting(o, ".파티메세지", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 채창락.
	 * 2019-12-11
	 * by connector12@nate.com
	 */
	public static void chattingLock(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("켬")) {
				Lineage.chatting_all_lock = false;
				ChattingController.toChatting(o, "[채팅: 모든 채팅 가능]", Lineage.CHATTING_MODE_MESSAGE);
			} else if (msg.equalsIgnoreCase("끔")) {
				Lineage.chatting_all_lock = true;
				ChattingController.toChatting(o, "[채팅: 모든 채팅 불가]", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, Lineage.command + "채창락 켬/끔", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "채창 켬/끔", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 무기 이펙트 켬/끔
	 * 2020-07-16
	 * by connector12@nate.com
	 */
	public static void 이펙트(object o, StringTokenizer st) {
		try {
			String msg = st.nextToken();
			
			if (msg.equalsIgnoreCase("켬")) {
				o.무기이펙트 = true;
				ChattingController.toChatting(o, "[무기 이펙트: 켬]", Lineage.CHATTING_MODE_MESSAGE);
			} else if (msg.equalsIgnoreCase("끔")) {
				o.무기이펙트 = false;
				ChattingController.toChatting(o, "[무기 이펙트: 끔]", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, Lineage.command + "이펙트 켬/끔", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "이펙트 켬/끔", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 복수
	 * 2020-08-03
	 * by connector12@nate.com
	 */
	public static void 복수(object o, StringTokenizer st) {
		if (!Lineage.is_revenge) {
			ChattingController.toChatting(o, "복수 시스템을 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		if (o instanceof PcInstance) {
			try {
				String name = st.nextToken();			
				PcInstance target = World.findPc(name);
				PcInstance pc = (PcInstance) o;
				
				if (pc != null && pc.getInventory() != null) {
					if (target != null && !target.isDead()) {
						BuffInterface b = BuffController.find(o, SkillDatabase.find(Lineage.revenge_uid));
						if (b != null) {
							ChattingController.toChatting(o, String.format("복수는 %d초 후 사용할 수 있습니다.", b.getTime()), Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						if (name.equalsIgnoreCase(o.getName())) {
							ChattingController.toChatting(o, "복수는 자신에게 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						// 운영자에게 복수 불가.
						if (target.getGm() > 0) {
							ChattingController.toChatting(o, "대상에게 복수할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						// 투명상태인 대상에 복수 불가.
						if (target.isInvis()) {
							ChattingController.toChatting(o, "대상에게 복수할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						if (o.getLevel() < Lineage.revenge_level) {
							ChattingController.toChatting(o, String.format("복수는 %d레벨 이상 사용가능합니다.", Lineage.revenge_level), Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						if (target.getLevel() < Lineage.revenge_level) {
							ChattingController.toChatting(o, String.format("복수할 대상이 %d레벨 미만입니다.", Lineage.revenge_level), Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						if (!Lineage.is_clan_revenge && target.getClanId() > 0 && o.getClanId() > 0 && target.getClanId() == o.getClanId()) {
							ChattingController.toChatting(o, "복수는 같은 혈맹원에게 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						if (World.isSafetyZone(target.getX(), target.getY(), target.getMap())) {
							ChattingController.toChatting(o, "복수할 대상이 세이프티존에 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						if (target.getMap() != 70 && World.isCombatZone(target.getX(), target.getY(), target.getMap())) {
							ChattingController.toChatting(o, "복수할 대상이 컴뱃존에 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						Kingdom kingdom = KingdomController.findKingdomLocation(target);
						if (kingdom != null && kingdom.isWar()) {
							ChattingController.toChatting(o, "복수할 대상이 공성중인 성주변에 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						
						for (int map : Lineage.revenge_not_map_list) {
							if (map == target.getMap()) {
								ChattingController.toChatting(o, "복수할 대상이 이동 불가한 지역에 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
								return;
							}
						}
						
						boolean itemCheck = Lineage.revenge_need_item_list.size() == 0 ? true : false;
						
						if (!itemCheck) {
							for (FirstInventory fi : Lineage.revenge_need_item_list) {
								for (ItemInstance it : pc.getInventory().getList()) {
									if (it != null && it.getItem() != null && !it.isEquipped() && it.getItem().getName().equalsIgnoreCase(fi.getName()) && fi.getCount() <= it.getCount()) {
										itemCheck = true;
										pc.getInventory().count(it, it.getCount() - fi.getCount(), true);
										break;
									}
								}						
								if (itemCheck)
									break;
							}
						}
						
						if (itemCheck) {
							RevengeCooldown.init(pc);
							int count = 100;
							int range = Util.random(2, 3);

							Map m = World.get_map(target.getMap());
							if (m != null) {
								int x1 = m.locX1;
								int x2 = m.locX2;
								int y1 = m.locY1;
								int y2 = m.locY2;

								if (range > 1) {
									int roop_cnt = 0;
									int x = target.getX();
									int y = target.getY();
									int map = target.getMap();
									int lx = x;
									int ly = y;
									int loc = range;
									// 랜덤 좌표 스폰
									do {
										lx = Util.random(x - loc < x1 ? x1 : x - loc, x + loc > x2 ? x2 : x + loc);
										ly = Util.random(y - loc < y1 ? y1 : y - loc, y + loc > y2 ? y2 : y + loc);
										if (roop_cnt++ > count) {
											o.toTeleport(target.getX(), target.getY(), target.getMap(), false);
											break;
										}
									} while (!World.isThroughObject(lx, ly + 1, map, 0) ||
											!World.isThroughObject(lx, ly - 1, map, 4) ||
											!World.isThroughObject(lx - 1, ly, map, 2) ||
											!World.isThroughObject(lx + 1, ly, map, 6) ||
											!World.isThroughObject(lx - 1, ly + 1, map, 1) ||
											!World.isThroughObject(lx + 1, ly - 1, map, 5) ||
											!World.isThroughObject(lx + 1, ly + 1, map, 7) ||
											!World.isThroughObject(lx - 1, ly - 1, map, 3) ||
											World.isNotMovingTile(lx, ly, map));

									o.toTeleport(lx, ly, map, false);
								} else {
									o.toTeleport(target.getX(), target.getY(), target.getMap(), false);
								}
							}
						} else {
						
							ChattingController.toChatting(o, String.format("복수는 %s 중 1개가 필요합니다.", Lineage.revenge_need_item), Lineage.CHATTING_MODE_MESSAGE);
						}
					} else {
						ChattingController.toChatting(o, String.format("'%s'는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
					}
				}
			} catch (Exception e) {
				ChattingController.toChatting(o, Lineage.command + "복수 케릭명", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	
	public static void 변신해제(object o, StringTokenizer st) {
		try {
			String name = st.nextToken();			
			PcInstance pc = World.findPc(name);
			
			if (pc != null) {
				BuffController.remove(pc, ShapeChange.class);
				
				pc.setGfx(pc.getClassGfx());
				if (pc.getInventory() != null && pc.getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
					pc.setGfxMode(pc.getClassGfxMode() + pc.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode());
				else
					pc.setGfxMode(pc.getClassGfxMode());
				
				pc.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), pc), true);
				
				ChattingController.toChatting(o, String.format("'%s' 변신 해제 완료", name), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, String.format("'%s'는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "변신해제 케릭명", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	public static void 영자마크(object o, StringTokenizer st) {
		try {
			String tempClan = "임시영자마크";
			
			if (o.getClanName().equalsIgnoreCase("")) {
				o.setClanName(tempClan);
				o.toTeleport(o.getX(), o.getY(), o.getMap(), false);
			}
			
			if (o.isMark) {
				o.isMark = false;
				
				for (Clan c : ClanController.getClanList().values()) {
					if (c != null && !c.getName().equalsIgnoreCase(Lineage.new_clan_name) && 
							!c.getName().equalsIgnoreCase(Lineage.teamBattle_A_team) && !c.getName().equalsIgnoreCase(Lineage.teamBattle_B_team)) {
						o.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, o.getClanName(), c.getName()));
					}
				}
				
				if (o.getClanName().equalsIgnoreCase(tempClan)) {
					o.setClanName("");
					o.toTeleport(o.getX(), o.getY(), o.getMap(), false);
				}
			} else {
				o.isMark = true;
				
				for (Clan c : ClanController.getClanList().values()) {
					if (c != null && !c.getName().equalsIgnoreCase(Lineage.new_clan_name) && 
							!c.getName().equalsIgnoreCase(Lineage.teamBattle_A_team) && !c.getName().equalsIgnoreCase(Lineage.teamBattle_B_team)) {
						o.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 1, o.getClanName(), c.getName()));
					}
				}
			}
		} catch (Exception e) {
			
		}
	}
	
	public static void 혈맹파티(object o, StringTokenizer st) {
		Party clanParty = null;
		boolean result = true;
		
		if (o.getClanId() == 0) {
			ChattingController.toChatting(o, "혈맹에 가입되어 있지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		PcInstance cha = (PcInstance) o;
		PartyController.checkParty(cha);
		
		Party temp = PartyController.find(cha);
		if (temp != null && !temp.isClanParty() && temp.getMaster().getObjectId() != cha.getObjectId()) {
			PartyController.close(cha);
		}	
		
		try {
			Clan c = ClanController.find(cha);
			if (c != null) {
				for (PcInstance pc : c.getList()) {
					Party p = PartyController.find(pc);
					
					if (p != null && pc != null && pc.getClanId() == cha.getClanId() && p.isClanParty()) {
						clanParty = p;
						break;
					}
				}
				
				// 혈맹파티를 찾아서 혈맹파티가 있을경우 혈맹파티 명령어를 입력한 대상이 혈맹파티로 가입
				if (clanParty != null) {		
					for (PcInstance clan : clanParty.getList()) {
						if (clan.getObjectId() == cha.getObjectId()) {
							result = false;
							break;
						}
					}
					
					if (result) {
						cha.setPartyId(clanParty.getKey());
						PartyController.toClanParty(cha, true);
					}
				} else {
					for (PcInstance pc : ClanController.find(cha).getList()) {
							PartyController.toClanParty(cha, pc);
					}
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "혈맹파티", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	public static void 혈맹파티(object o) {
		Party clanParty = null;
		boolean result = true;
		
		if (o.getClanId() == 0) {
			ChattingController.toChatting(o, "혈맹에 가입되어 있지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		PcInstance cha = (PcInstance) o;
		PartyController.checkParty(cha);
		
		Party temp = PartyController.find(cha);
		if (temp != null && !temp.isClanParty() && temp.getMaster().getObjectId() != cha.getObjectId()) {
			PartyController.close(cha);
		}	
		
		try {
			Clan c = ClanController.find(cha);
			if (c != null) {
				for (PcInstance pc : c.getList()) {
					Party p = PartyController.find(pc);
					
					if (p != null && pc != null && pc.getClanId() == cha.getClanId() && p.isClanParty()) {
						clanParty = p;
						break;
					}
				}
				
				// 혈맹파티를 찾아서 혈맹파티가 있을경우 혈맹파티 명령어를 입력한 대상이 혈맹파티로 가입
				if (clanParty != null) {		
					for (PcInstance clan : clanParty.getList()) {
						if (clan.getObjectId() == cha.getObjectId()) {
							result = false;
							break;
						}
					}
					
					if (result) {
						cha.setPartyId(clanParty.getKey());
						PartyController.toClanParty(cha, true);
					}
				} else {
					for (PcInstance pc : ClanController.find(cha).getList()) {
							PartyController.toClanParty(cha, pc);
					}
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "혈맹파티", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/** 파워볼 배팅: .배팅 [홀/짝] [금액] (기존 호환) */
	private static void powerballBet(object o, StringTokenizer st) {
		if (!(o instanceof PcInstance)) {
			ChattingController.toChatting(o, "캐릭터만 배팅할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		PcInstance pc = (PcInstance) o;
		try {
			if (!st.hasMoreTokens()) {
				ChattingController.toChatting(o, Lineage.command + "파워볼홀·짝·언더·오버 100000 (5만~500만)", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			String pick = st.nextToken();
			if (!st.hasMoreTokens()) {
				ChattingController.toChatting(o, Lineage.command + "파워볼홀·짝·언더·오버 100000", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			long amount = Long.parseLong(st.nextToken());
			int pickType = -1;
			if (pick.equalsIgnoreCase("홀")) pickType = 1;
			else if (pick.equalsIgnoreCase("짝")) pickType = 0;
			else if (pick.equalsIgnoreCase("언더")) pickType = 2;
			else if (pick.equalsIgnoreCase("오버")) pickType = 3;
			else {
				ChattingController.toChatting(o, "홀·짝·언더·오버 중 하나를 입력하세요. 예: .배팅 언더 100000", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			PowerballController.doBet(pc, pickType, amount);
			return;
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "파워볼홀·짝·언더·오버 100000", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/** 파워볼 쿠폰 구매: .파워볼홀·짝·언더·오버 [금액] (5만~500만, 1회차당 1회) */
	private static void powerballBetAmount(object o, StringTokenizer st, int pickType) {
		if (!(o instanceof PcInstance)) {
			ChattingController.toChatting(o, "캐릭터만 구매할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		PcInstance pc = (PcInstance) o;
		String cmdHint = powerballCouponCommand(pickType);
		try {
			if (!st.hasMoreTokens()) {
				ChattingController.toChatting(o, cmdHint + " 100000  (5만~500만 아데나)", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			long amount = Long.parseLong(st.nextToken());
			PowerballController.doBet(pc, pickType, amount);
		} catch (NumberFormatException e) {
			ChattingController.toChatting(o, "금액을 숫자로 입력하세요. 예: " + cmdHint + " 100000", Lineage.CHATTING_MODE_MESSAGE);
		} catch (Exception e) {
			ChattingController.toChatting(o, cmdHint + " 100000  (5만~500만 아데나)", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	private static String powerballCouponCommand(int pickType) {
		switch (pickType) {
			case 1: return Lineage.command + "파워볼홀";
			case 0: return Lineage.command + "파워볼짝";
			case 2: return Lineage.command + "파워볼언더";
			case 3: return Lineage.command + "파워볼오버";
			default: return Lineage.command + "파워볼홀";
		}
	}

	public static void 베릴분배(object o, StringTokenizer st) {
		

		if((o.getClassType()!=Lineage.LINEAGE_CLASS_ROYAL && o.getClanGrade() != 3) || o.getClanId()==0){
			// 이 명령은 혈맹 군주만이 이용할 수 있습니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 518));
			return;
		}
	
		long count = 0;
	
		List<object> list_temp = new ArrayList<object>();
		List<object> list_temp2 = new ArrayList<object>();
		
		if (st.hasMoreTokens())
			count = Long.valueOf(st.nextToken());
		
			PcInstance cha = (PcInstance) o;
			ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
			
			if(count >= 0 ){
				
			}
		
			if(ii != null){

	
				
				// 혈맹원 추출.
				Clan c = ClanController.find(cha);
				if (c != null) {
					for (PcInstance pc : c.getList()) {
						if(pc.getName().equals(o.getName()))
							continue;
						
						if (!list_temp.contains(pc) && Util.isDistance(o, pc, 8) ){
							list_temp.add(pc);
									
						}else{
							ChattingController.toChatting(o,"8칸 이내에 분배할 혈원이 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					}
				}
				for (object o1 : list_temp){
					if (o.getInventory().isAden("아데나",count, true)) {
						ii.setCount(count);
						o1.toGiveItem(null, ii, ii.getCount());
						ChattingController.toChatting(o, String.format(o1.getName()+"님에게 %d개 만큼 분배 되었습니다.", count), Lineage.CHATTING_MODE_MESSAGE);
						o1.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o1, 1765), true);
						ChattingController.toChatting(o1,  String.format(o1.getName()+"님에게 %d개 만큼 분배 되었습니다.", count), Lineage.CHATTING_MODE_MESSAGE);
					}else{
						list_temp2.add(o1);
					}
				}
				
				for(int i=0 ; i< list_temp2.size();i++ ){
					ChattingController.toChatting(o, String.format(list_temp2.get(i).getName() +"님에게는 아데나가 부족하여 %d원 만큼 분배하지 못하였습니다",count) , Lineage.CHATTING_MODE_MESSAGE);
				
				}
				
				
				
			}
			
			list_temp2.clear();
			list_temp.clear();

	}
	public static void 고정신청(object o, StringTokenizer st) {
		if (o != null && !o.isDead() && o instanceof PcInstance) {
			PcInstance pc = (PcInstance) o;
			
			if (!pc.isMember()) {
				String phone = null;
				
				try {
					phone = st.nextToken();
					
					if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", phone)) {
						ChattingController.toChatting(o, "핸드폰 번호 형식이 잘못되었습니다. ex) 010-1234-5678", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
				} catch (Exception e) {
					ChattingController.toChatting(o, Lineage.command + "고정신청 010-1234-5678", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				if (pc.getAccountId() != null && pc.getName() != null && phone != null) {
					Connection con = null;
					PreparedStatement stt = null;
					
					try {
						con = DatabaseConnection.getLineage();
						stt = con.prepareStatement("INSERT INTO member SET 계정=?, 캐릭터=?, 연락처=?, 고정신청날짜=?");
						stt.setString(1, pc.getAccountId());
						stt.setString(2, pc.getName());
						stt.setString(3, phone);
						stt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
						stt.executeUpdate();
						
						pc.setMember(true);
						ChattingController.toChatting(pc, String.format("\\fY[고정 신청] '%s' 계정 고정 신청 완료!", pc.getAccountId()), Lineage.CHATTING_MODE_MESSAGE);
						pc.고정멤버버프(true);
					} catch (Exception e) {
						
					} finally {
						DatabaseConnection.close(con, stt);
					}
				}
			} else {
				ChattingController.toChatting(o, "이미 고정 신청이 완료되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	public static void 캐쉬백(object o, StringTokenizer st ) {
	
			PcInstance pc = (PcInstance) o;
			
			if (!pc.isMember()) {
				String account = null;
				String cash = null;
				try {
					account = st.nextToken();
					cash = st.nextToken();
				} catch (Exception e) {
					ChattingController.toChatting(o, Lineage.command + "계정id 갯수", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				if (pc.getAccountId() != null && cash != null) {
					Connection con = null;
					PreparedStatement stt = null;
					
					try {
						con = DatabaseConnection.getLineage();
						stt = con.prepareStatement("INSERT INTO cashback SET 계정=?, count=?, 날짜=?");
						stt.setString(1, account);
						stt.setString(2, cash);
						stt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
						stt.executeUpdate();
						
						ChattingController.toChatting(o, "계정"+ account+" " + cash +" 개를 다음차에 지급하실 수량으로 입력하셧습니다", Lineage.CHATTING_MODE_MESSAGE);
					} catch (Exception e) {
						
					} finally {
						DatabaseConnection.close(con, stt);
					}
				}
			} else {
				ChattingController.toChatting(o, "이미 추가하셧습니다", Lineage.CHATTING_MODE_MESSAGE);
			}
	
	}
	public static void 귀환(object o, StringTokenizer st) {
		if (o != null) {
			try {
				int uid = Integer.valueOf(st.nextToken());
				GmTeleport gt = GmTeleportDatabase.find(uid);
				
				if (gt != null)
					o.toTeleport(gt.getX(), gt.getY(), gt.getMap(), true);
				else {
					ChattingController.toChatting(o, String.format("%d번은 귀환 목록에 존재하지 않습니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
					
					ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 귀환 목록 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
					
					StringBuffer msg = new StringBuffer();
					for (GmTeleport gtt : GmTeleportDatabase.getList()) {
						msg.append(String.format("[%d]:%s ", gtt.getUid(), gtt.getName()));
					}
					
					ChattingController.toChatting(o, msg.toString(), Lineage.CHATTING_MODE_MESSAGE);
					
					ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
				}
			} catch (Exception e) {
				o.toTeleport(32740, 32865, 99, false);
										
				ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 귀환 목록 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
				
				StringBuffer msg = new StringBuffer();
				for (GmTeleport gt : GmTeleportDatabase.getList()) {
					msg.append(String.format("[%d]:%s ", gt.getUid(), gt.getName()));
				}
				
				ChattingController.toChatting(o, msg.toString(), Lineage.CHATTING_MODE_MESSAGE);
				
				ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
				
				ChattingController.toChatting(o, Lineage.command + "귀환 번호", Lineage.CHATTING_MODE_MESSAGE);
			}								
		}
	}
	
	public static void 좌표복구(object o, StringTokenizer st) {
		int objId = 0;
		Connection con = null;
		
		try {
			String name = st.nextToken();
			con = DatabaseConnection.getLineage();
			objId = CharactersDatabase.getCharacterObjectId(con, name);
			
			if (objId > 0) {
				int[] loc = Lineage.getHomeXY();
				CharactersDatabase.updateLocation(con, objId, loc[0], loc[1], loc[2]);
				ChattingController.toChatting(o, String.format("'%s' 캐릭터 좌표복구 완료.", name), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, String.format("'%s' 캐릭터는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "좌표복구 캐릭터", Lineage.CHATTING_MODE_MESSAGE);
		} finally {
			DatabaseConnection.close(con);
		}		
	}
	public static void 유저좌표복구(object o, StringTokenizer st) {
		int objId = 0;
		Connection con = null;
		
		try {
		
			con = DatabaseConnection.getLineage();
			objId = CharactersDatabase.getAccountUid(con, o.getObjectId());
			
		
				int[] loc = Lineage.getHomeXY();
				CharactersDatabase.updateLocation(con, objId, loc[0], loc[1], loc[2]);
				ChattingController.toChatting(o, String.format(" 좌표복구 완료."), Lineage.CHATTING_MODE_MESSAGE);
			
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "좌표복구 캐릭터", Lineage.CHATTING_MODE_MESSAGE);
		} finally {
			DatabaseConnection.close(con);
		}		
	}
}
