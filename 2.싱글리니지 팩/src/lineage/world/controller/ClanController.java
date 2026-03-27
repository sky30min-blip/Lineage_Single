package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.lineage.Agit;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ClanInfo;
import lineage.network.packet.server.S_ClanMark;
import lineage.network.packet.server.S_ClanWar;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectTitle;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

public final class ClanController {

	static private List<Clan> pool;				// 사용 다된 혈맹 목록
	static private Map<Integer, Clan> list;		// 혈맹 목록
	static private Integer next_uid;			// 혈맹 고유 키값에 사용되는 마지막 값
	
	/**
	 * 초기화 함수.
	 *  : 서버 가동될때 한번 호출됨.
	 *    디비 정보를 읽어서 메모리에 상주하는 역활.
	 * @param con
	 */
	static public void init(Connection con) {
		TimeLine.start("ClanController..");

		list = new HashMap<Integer, Clan>();
		pool = new ArrayList<Clan>();
		next_uid = 0;

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM clan_list ORDER BY uid");
			rs = st.executeQuery();
			while (rs.next()) {
				Clan c = new Clan();
				c.setUid(rs.getInt("uid"));
				c.setName(rs.getString("name"));
				c.setLord(rs.getString("lord"));
				c.setIcon(Util.StringToByte(rs.getString("icon")));
				c.setClan_point(rs.getInt("point"));
				c.setSell_clan_point(rs.getInt("sell_point"));
				c.setClan_total_point(rs.getInt("total_point"));
				c.set경험치증가(rs.getInt("경험치증가"));
				c.set드랍확률증가(rs.getInt("드랍확률증가"));
				c.set아덴증가(rs.getInt("아덴증가"));
				c.set추타(rs.getInt("추타"));
				c.setPvp_추타(rs.getInt("pvp_추타"));
				c.set리덕(rs.getInt("리덕"));
				c.setPvp_리덕(rs.getInt("pvp_리덕"));
				c.set스턴내성(rs.getInt("스턴내성"));
				c.set치명타확률(rs.getInt("치명타확률"));
				c.setSp(rs.getInt("sp"));
				
				for (String member : rs.getString("list").split(" "))
					c.appendMemberList(member);

				if (c.getUid() > next_uid)
					next_uid = c.getUid();
				list.put(c.getUid(), c);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ClanController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	/**
	 * 종료 함수.
	 *  : 서버가 종료될때 호출됨.
	 *    혈맹 정보를 디비에 기록하는 역활을 담당.
	 *  : 메모리 제거 처리도 덤으로
	 */
	static public void close(Connection con) {
		if (list != null) {
			PreparedStatement st = null;

			try {
				st = con.prepareStatement("DELETE FROM clan_list");
				st.executeUpdate();
				st.close();

				// close 함수에서도 요청하기 때문에 동기화 작업을 함.
				for (Clan c : list.values()) {
					StringBuffer icon = new StringBuffer();
					if (c.getIcon() != null) {
						for (int i = 0; i < c.getIcon().length; ++i)
							icon.append(String.format("%02x", c.getIcon()[i] & 0xff));
					}

					st = con.prepareStatement("INSERT INTO clan_list SET uid=?, name=?, lord=?, icon=?, list=?, point=?, sell_point=?, total_point=?, "
							+ "경험치증가=?, 드랍확률증가=?, 아덴증가=?, 추타=?, 리덕=?, pvp_추타=?, pvp_리덕=?, 스턴내성=?, 치명타확률=?, sp=?");
					st.setInt(1, c.getUid());
					st.setString(2, c.getName());
					st.setString(3, c.getLord());
					st.setString(4, icon.toString());
					st.setString(5, c.getMemberNameList());
					st.setInt(6, c.getClan_point());
					st.setInt(7, c.getSell_clan_point());
					st.setInt(8, c.getClan_total_point());
					st.setInt(9, c.get경험치증가());
					st.setInt(10, c.get드랍확률증가());
					st.setInt(11, c.get아덴증가());
					st.setInt(12, c.get추타());
					st.setInt(13, c.get리덕());
					st.setInt(14, c.getPvp_추타());
					st.setInt(15, c.getPvp_리덕());
					st.setInt(16, c.get스턴내성());
					st.setInt(17, c.get치명타확률());
					st.setInt(18, c.getSp());
					st.executeUpdate();
					st.close();
				}
				list.clear();
			} catch (Exception e) {
				lineage.share.System.printf("%s : close(Connection con)\r\n", ClanController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(st);
			}
		}
	}
	
	/**
	 * 신규 혈맹명 바꿀경우 처리 메소드.
	 * 2019-07-03
	 * by connector12@nate.com
	 */
	static public void reloadNewClan(String oldName, String newName){
		if (!oldName.equalsIgnoreCase(newName) && oldName.length() > 1 && newName.length() > 1) {
			Clan c = find(oldName);
			
			if (c != null) {
				for (PcInstance pc : c.getList())
					pc.setClanName(newName);
				
				c.setName(newName);
				
				PreparedStatement st = null;
				Connection con = null;
				try {
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("UPDATE clan_list SET name=? WHERE name=?");
					st.setString(1, newName);
					st.setString(2, oldName);
					st.executeUpdate();
					st.close();
					
					st = con.prepareStatement("UPDATE characters SET clanNAME=?, temp_clan_name=? WHERE clanNAME=?");
					st.setString(1, newName);
					st.setString(2, newName);
					st.setString(3, oldName);
					st.executeUpdate();
					st.close();

					st = con.prepareStatement("UPDATE warehouse_clan_log SET clan_name=? WHERE clan_name=?");
					st.setString(1, newName);
					st.setString(2, oldName);
					st.executeUpdate();
				} catch (Exception e) {
					lineage.share.System.printf("%s : reloadNewClan(String oldName, String newName)\r\n", ClanController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st);
				}
				Lineage.new_clan_name = newName;
			}
		}
	}

	/**
	 * 월드 접속시 호출됨.
	 * @param pc
	 */
	static public void toWorldJoin(PcInstance pc){
		Clan c = find(pc);
		if(c != null){
			//
			toClanWarCheck(pc, c);
			//
			c.toWorldJoin(pc);
		}
	}

	/**
	 * 월드 나갈때 호출됨.
	 * @param pc
	 */
	static public void toWorldOut(PcInstance pc){
		Clan c = find(pc);
		if(c != null){
			// 혈전 종료 처리 요청.
			toClanWarEnd(pc, c);
			//
			c.toWorldOut(pc);
		}
	}
	
	// .마크에 필요한 get메서드
	public static Map<Integer, Clan> getClanList(){
		return new HashMap<Integer, Clan>(list);
	}

	/**
	 * 혈맹 창설 뒤처리 함수.
	 * @param pc
	 * @param clan_name
	 */
	static public void toCreate(PcInstance pc, String clan_name) {
		if (pc.getClanId() == 0 && clan_name != null) {
			if (pc.getLevel() >= Lineage.CLAN_MAKE_LEV) {
				if (clan_name.length() <= Lineage.CLAN_NAME_MIN_SIZE) {
					ChattingController.toChatting(pc, "혈맹 이름이 너무 짧습니다.", Lineage.CHATTING_MODE_MESSAGE);
				} else {
					if (clan_name.length() >= Lineage.CLAN_NAME_MAX_SIZE) {
						// 98 \f1혈맹이름이 너무 깁니다.
						pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 98));
					} else {
						if (!isClanName(clan_name)) {
							// 혈맹 객체 풀에서 꺼내기. null이라면 새로 생성.
							Clan c = getPool();
							if (c == null)
								c = new Clan();
							// 정보 세팅.
							c.setUid(nextUid());
							c.setName(clan_name);
							c.setLord(pc.getName());
							c.appendMemberList(pc.getName());
							c.toWorldJoin(pc);
							// 사용자에게 혈맹이름와 uid 넣기.
							pc.setClanId(c.getUid());
							pc.setClanName(clan_name);
							// 호칭 초기화
							pc.setTitle("");
							pc.setClanGrade(3);
							pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), pc), true);
							// 84 \f1%0 혈맹이 창설되었습니다.
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 84, clan_name), true);
							// 메모리에 등록.
							synchronized (list) {
								if (!list.containsKey(c.getUid()))
									list.put(c.getUid(), c);
								toSaveClan(c);
								CharactersDatabase.updateClan(pc, c);
							}
						} else {
							// 99 \f1같은 이름의 혈맹이 존재합니다.
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 99));
						}
					}
				}
			} else {
				// 233 \f1레벨 5 이하는 혈맹을 만들 수 없습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 233));
			}
		} else {
			// 86 \f1이미 혈맹을 창설하였습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 86));
		}
	}
	
	/**
	 * 혈맹 탈퇴 처리 함수.
	 * @param pc
	 */
	static public void toOut(PcInstance pc) {
		if (pc.getMap() == Lineage.teamBattleMap || pc.getMap() == Lineage.BattleRoyalMap)
			return;
		Clan c = find(pc);
		if (c != null) {
			for (Kingdom k : KingdomController.getList()) {
				if (k != null && k.isWar()) {
					ChattingController.toChatting(pc, "공성전 진행 중에 혈맹을 탈퇴할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}

			Kingdom kingdom = KingdomController.find(pc);

			// 혈맹 전쟁 중에는 혈맹을 탈퇴할 수 없습니다.
			if ((kingdom != null && kingdom.isWar()) || c.getWarClan() != null) {
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 331));
				return;
			}

			if (c.getLord().equalsIgnoreCase(pc.getName())) {
				// 665 \f1성이나 아지트를 소유한 상태에서는 혈맹을 해산할 수 없습니다.
				if (AgitController.find(pc) != null || kingdom != null) {
					c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 665));
					return;
				}

				// 269 %1혈맹의 혈맹주 %0%s 혈맹을 해산시켰습니다.
				c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 269, c.getLord(), c.getName()));
				// 접속한 혈맹원들 정보 변경.
				for (PcInstance use : c.getList()) {
					use.setClanId(0);
					use.setClanName("");
					use.setTitle("");
					use.setClanGrade(0);
					use.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), use), true);
					CharactersDatabase.updateClan(use, c);
				}
				// 멤버들 이름을 참고해서 디비에 있는 회원정보 변경.
				Connection con = null;
				PreparedStatement st = null;
				try {
					con = DatabaseConnection.getLineage();
					for (String member : c.getMemberList()) {
						st = con.prepareStatement("UPDATE characters SET clanID=0, clanNAME='', title='' WHERE LOWER(name)=?");
						st.setString(1, member);
						st.executeUpdate();
						st.close();
					}
				} catch (Exception e) {
					lineage.share.System.printf("%s : toOut(PcInstance pc)\r\n", ClanController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st);
				}
				
				RobotClanController.removeClan(c.getUid());

				// 관리중이던 목록에서 제거.
				synchronized (list) {
					list.remove(c.getUid());
					toDeleteClan(c);
				}
				// 정보 초기화.
				c.close();
				// 재사용을 위해 풀에 넣기.
				setPool(c);
			} else {				
				// 178 \f1%0%s %1 혈맹을 탈퇴했습니다.
				c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 178, pc.getName(), c.getName()));
				// 관리목록에서 제거.
				c.toWorldOut(pc);
				c.removeMemberList(pc.getName());
				// 탈퇴 회원 정보 변경.
				pc.setClanId(0);
				pc.setClanName("");
				pc.setTitle("");
				pc.setClanGrade(0);
				pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), pc), true);
				CharactersDatabase.updateClan(pc, c);
				updateClan(c);
			}
		}
	}

	/**
	 * 혈맹원 추방처리 함수.
	 * @param pc
	 * @param member
	 */
	static public void toKin(PcInstance pc, String member) {
		Clan c = find(pc);
		if (c != null) {
			if (c.containsMemberList(member)) {
				// 혈맹원 정보 변경.
				PcInstance use = World.findPc(member);
				if (use != null) {
					use.setClanId(0);
					use.setClanName("");
					use.setTitle("");
					use.setClanGrade(0);
					updateClan(c);
					use.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), use), true);
					// 238 당신은 %0 혈맹으로부터 추방되었습니다.
					use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 238, c.getName()));
				}
				// 디비 정보 변경.
				Connection con = null;
				PreparedStatement st = null;
				try {
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("UPDATE characters SET clanID=0, clanNAME='', title='', clan_grade=0 WHERE LOWER(name)=?");
					st.setString(1, member);
					st.executeUpdate();
					st.close();
				} catch (Exception e) {
					lineage.share.System.printf("%s : toKin(PcInstance pc, String member)\r\n", ClanController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st);
				}
				// 혈맹 정보 변경.
				c.removeList(use);
				c.removeMemberList(member);
				// 240 %0%o 당신의 혈맹에서 추방하였습니다.
				c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 240, member));
			} else {
				ChattingController.toChatting(pc, "혈맹원이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	/**
	 * 가입 요청 처리 함수.
	 * @param pc	: 요청자
	 * @param use	: 찾은 사용자
	 */
	static public void toJoin(PcInstance pc, PcInstance use) {
		if (pc.getClanId() > 0) {
			// 89 \f1이미 혈맹에 가입했습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 89));
		} else {
			if (use.getClanGrade() > 0) {
				Clan c = find(use);
				Kingdom k = KingdomController.find(use);

				if (k != null && !Lineage.kingdom_clan_join) {
					ChattingController.toChatting(use, "성혈맹은 가입이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(pc, "성혈맹은 가입이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				} else {
					if (c != null) {
						if (!Lineage.is_two_clan_join && !CharactersDatabase.isClanJoin(pc, c.getName())) {
							ChattingController.toChatting(pc, "계정의 다른캐릭터에 혈맹이 존재합니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						// 97 %0%s 혈맹에 가입하기를 원합니다. 승낙하시겠습니까? (Y/N)
						use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 97, pc.getName()));
						c.setTempPc(pc);
						// }
					} else {
						// \f1%0%d 혈맹을 창설하지 않은 상태입니다.
						pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 90, use.getName()));
					}
				}
			} else {
				ChattingController.toChatting(use, "수호기사 이상 가입을 승낙할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, "상대방의 직위가 낮습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	/**
	 * 가입 요청에 대한 마지막 승낙여부 처리 함수.
	 * @param pc
	 * @param yes
	 */
	static public void toJoinFinal(PcInstance pc, boolean yes){
	
			Clan c = find(pc);
			if(c!=null){
				PcInstance use = c.getTempPc();
				if(use!=null && !use.isWorldDelete()){
					if(yes){
						
						if (pc.getClanId() > 0) {
							// 89 \f1이미 혈맹에 가입했습니다.
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 89));
						}
						if(c.sizeMemberList() < Lineage.clan_max && use.getLevel() > 0){
							// 가입자 정보 갱신.
							use.setClanId(c.getUid());
							use.setClanName(c.getName());
							use.setTitle("");
							use.setClanGrade(0);
							CharactersDatabase.updateClan(use, c);
							// 패킷 처리
							use.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), use), true);
							//94 \f1%0%o 혈맹의 일원으로 받아들였습니다.
							c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 94, use.getName()));
							//95 \f1%0 혈맹에 가입하였습니다.
							use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 95, c.getName()));
							// 혈맹 관리목록 갱신
							c.appendMemberList(use.getName());
							c.appendList(use);
							updateClan(c);
							// 플러그인에 알리기.
							PluginController.init(ClanController.class, "toJoinFinal", "ok", pc, use);
						}else{
							ChattingController.toChatting(pc, String.format("혈맹의 최대 가입인원은 %d명 입니다.", Lineage.clan_max), Lineage.CHATTING_MODE_MESSAGE);
							ChattingController.toChatting(use,  String.format("혈맹의 최대 가입인원은 %d명 입니다.", Lineage.clan_max), Lineage.CHATTING_MODE_MESSAGE);
						}
					}else{
						//237 %0 혈맹이 당신의 제안을 거절하였습니다.
						use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 237, c.getName()));
					}
				}
				c.setTempPc(null);
			}
		
		
	}

	/**
	 * 콜클랜에 대한 응답 처리 함수.
	 * @param pc
	 * @param yes
	 */
	static public void toCallClan(PcInstance pc, boolean yes){
		Clan c = find(pc);
		// 버그 확인.
		if(c!=null && c.containsCallList(pc)){
			PcInstance royal = c.getRoyal();
			if(royal != null){
				// 좌표 검색.
				if (royal.getMap() != 0 && royal.getMap() != 4) {
					ChattingController.toChatting(pc, "콜 클렌의 위치가 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				if (pc.isLock()) {
					ChattingController.toChatting(pc, "현재 상태에선 이동할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				// 처리.
				if(yes){
					pc.toTeleport(royal.getX(), royal.getY(), royal.getMap(), true);
				}else{
					// \f1%0%s 당신의 요청을 거절하였습니다.
					royal.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 96, pc.getName()));
				}
			}
			// 콜목록에서 제거.
			c.removeCallList(pc);
		}
	}

	/**
	 * 혈맹 문장 업로드 처리 함수.
	 * @param pc
	 * @param icon
	 */
	static public void toMarkUpload(PcInstance pc, byte[] icon){
		Clan c = find(pc);
		if(c!=null){
			// 전쟁중에는 업로드 안되도록 작업해야됨.
			if(pc.getClanGrade() >= 2){
				if (c.getWarClan() != null) {
					ChattingController.toChatting(pc, "전쟁중에 혈맹문장을 등록할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				int old_uid = c.getUid();
				int new_uid = nextUid();
				// 이전 전체 관리목록에서 제거.
				synchronized (list) {
					list.remove(old_uid);
				}
				
				// uid값 새로 갱신
				c.setUid(new_uid);
				c.setIcon(icon);
				// 아지트 갱신
				Agit agit = AgitController.find(pc);
				if(agit != null)
					agit.setClanId(c.getUid());
				// 성 갱신
				Kingdom kingdom = KingdomController.find(pc);
				if(kingdom != null)
					kingdom.setClanId(c.getUid());
				// 혈맹원들 uid갱신
				for(PcInstance use : c.getList())
					use.setClanId(c.getUid());
				
				RobotClanController.update(old_uid, new_uid);
				
				// 디비 정보 변경.
				Connection con = null;
				PreparedStatement st = null;
				try {
					con = DatabaseConnection.getLineage();
					// 케릭터 클랜아이디 갱신.
					st = con.prepareStatement("UPDATE characters SET clanID=? WHERE clanID=?");
					st.setInt(1, new_uid);
					st.setInt(2, old_uid);
					st.executeUpdate();
					st.close();
					// 창고 클랜아이디 갱신.
					st = con.prepareStatement("UPDATE warehouse_clan SET clan_id=? WHERE clan_id=?");
					st.setInt(1, new_uid);
					st.setInt(2, old_uid);
					st.executeUpdate();
					st.close();
					// 창고 로그 클랜아이디 갱신.
					st = con.prepareStatement("UPDATE warehouse_clan_log SET clan_uid=? WHERE clan_uid=?");
					st.setInt(1, new_uid);
					st.setInt(2, old_uid);
					st.executeUpdate();
					st.close();
				} catch (Exception e) {
					lineage.share.System.printf("%s : toMarkUpload(PcInstance pc, byte[] icon)\r\n", ClanController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st);
				}
				// 관리목록에 재 등록.
				synchronized (list) {
					if(!list.containsKey(c.getUid()))
						list.put(c.getUid(), c);
					toSaveClanMark(c, old_uid, agit);
				}
			}else{
				ChattingController.toChatting(pc, "군주 또는 부군주만 혈맹문장 등록이 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	/**
	 * uid에 맞는 혈맹 찾아서 해당 하는 혈맹의 문장 요청 처리하는 함수.
	 * @param pc
	 * @param uid
	 */
	static public void toMarkDownload(PcInstance pc, int uid){
		Clan c = find(uid);
		if(c!=null && c.getIcon()!=null && c.getIcon().length>0)
			pc.toSender(S_ClanMark.clone(BasePacketPooling.getPool(S_ClanMark.class), c));
	}

	/**
	 *  /혈맹 커맨드입력히 호출되는 함수.
	 */
	static public void toInfo(PcInstance pc){
		if (pc.getMap() == Lineage.teamBattleMap || pc.getMap() == Lineage.BattleRoyalMap)
			return;
		Clan c = find(pc);
		if(c != null){
			if(pc.getClanGrade() > 1 && pc.getClanGrade() < 4)
				pc.toSender(S_ClanInfo.clone(BasePacketPooling.getPool(S_ClanInfo.class), c, "pledgeM"));
			else
				pc.toSender(S_ClanInfo.clone(BasePacketPooling.getPool(S_ClanInfo.class), c, "pledge"));
		}
	}

	/**
	 * 전쟁 선포 처리 함수.
	 * @param pc
	 * @param name
	 */
	static public void toWar(PcInstance pc, String name){
		// \f1오직 왕자와 공주만이 전쟁을 선언할 수 있습니다.
		if(pc.getClassType() != Lineage.LINEAGE_CLASS_ROYAL){
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 478));
			return;
		}
		// \f1레벨 15 이하의 군주는 전쟁을 선포할 수 없습니다.
		if(pc.getLevel()<15){
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 232));
			return;
		}
		Clan clan = find(pc);
		// \f1전쟁을 하기 위해서는 먼저 혈맹을 창설하셔야 합니다.
		if(clan == null){
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 272));
			return;
		}
		// \f1당신의 혈맹은 이미 전쟁 중입니다.
		if(clan.getWarClan() != null){
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 234));
			return;
		}
		Kingdom kingdom = KingdomController.findClanName(name);
		// 공성전을 선언하기 위해서는 적어도 25 레벨이 되어야 합니다.
		if(kingdom!=null && pc.getLevel()<25){
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 475));
			return;
		}

		if(kingdom == null){
			// 혈전
			Clan target = find(name);
			if(target!=null && target.getWarClan()==null){
				clan.setWarClan(name);
				target.setWarClan(clan.getName());
				
				PcInstance use = target.getRoyal();
				if(use != null){
					if(Lineage.server_version > 144)
						// %0 혈맹이 당신 혈맹과 전쟁을 원합니다. 응전하시겠습니까? (y/N)
						use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 217, clan.getName()));
					else
						toWarFinal(use, true);
				}else{
					// \f1%0 혈맹 군주가 현재 사용중이 아닙니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 218, name));
				}
			}
		}else{
			// 공성전
			// 당신은 이미 성을 소유하고 있으므로 다른 성에 도전할 수 없습니다.
			if(AgitController.find(pc)!=null || KingdomController.find(pc)!=null){
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 474));
				return;
			}
			// 이미 공성전에 참여하셨습니다.
			if(kingdom.getListWar().contains(pc.getClanName())){
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 522));
				return;
			}
			// 혈맹원중 한명이라도 해당성에 내외성존에 있는지 확인.
			for(PcInstance use : ClanController.find(pc).getList()){
				if(kingdom.getMap()==use.getMap() || KingdomController.isKingdomLocation(use, kingdom.getUid()))
					return;
			}
			// 전쟁 처리 목록에 추가하기.
			kingdom.getListWar().add(pc.getClanName());
			// 전쟁중일경우 패킷 처리.
			if(kingdom.isWar())
				World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 1, pc.getClanName(), name));
		}
	}
	
	/**
	 * 혈전 선포 승낙여부.
	 * @param pc
	 */
	static public void toWarFinal(PcInstance pc, boolean isYes){
		// 초기화
		Clan clan = find(pc);
		Clan target = find(clan.getWarClan());
		// 버그확인.
		if(target==null || clan==null){
			if(clan != null)
				clan.setWarClan(null);
			if(target != null)
				target.setWarClan(null);
			return;
		}
		// 처리
		if(isYes){
			World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 1, target.getName(), clan.getName()));
		}else{
			// %0 혈맹이 당신의 혈맹과의 전쟁을 거절하였습니다.
			target.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 236, clan.getName()));
			
			clan.setWarClan(null);
			target.setWarClan(null);
		}
	}
	
	/**
	 * 항복처리 함수.
	 * @param pc
	 * @param isYes
	 */
	static public void toWarSubmission(PcInstance pc, String name) {
		// 초기화
		Clan clan = find(pc);
		Clan target = find(name);
		PcInstance target_royal = target!=null ? target.getRoyal() : null;
		//
		if(clan==null || target==null || target_royal==null)
			return;
		if(Lineage.server_version > 144)
			// %0 혈맹이 항복하기를 원합니다. 받아들이시겠습니까? (y/N)
			target_royal.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 221, clan.getName()));
		else
			toWarSubmissionFinal(target_royal, true);
	}
	
	/**
	 * 항복처리 함수.
	 * @param pc
	 * @param isYes
	 */
	static public void toWarSubmissionFinal(PcInstance pc, boolean isYes){
		// 초기화
		Clan clan = find(pc);
		Clan target = find(clan.getWarClan());
		PcInstance target_royal = target!=null ? target.getRoyal() : null;
		// 버그확인.
		if(target==null || clan==null || target_royal==null){
			if(clan != null)
				clan.setWarClan(null);
			if(target != null)
				target.setWarClan(null);
			return;
		}
		// 처리
		if(isYes){
			// 성혈맹이라면 성혈맹에 등록된 전쟁정보 제거.
			Kingdom kingdom = KingdomController.findClanName(clan.getName());
			if(kingdom != null)
				kingdom.getListWar().remove(target.getName());
			//
			World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 2, target.getName(), clan.getName()));
//			World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, clan.getName(), target.getName()));
			World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 4, clan.getName(), target.getName()));
			//
			clan.setWarClan(null);
			target.setWarClan(null);
		}else{
			// %0 혈맹이 당신의 제안을 거절하였습니다.
			target_royal.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 237, clan.getName()));
		}
	}
	
	/**
	 * 사용자가 죽엇을때 호출됨.
	 * @param pc
	 */
	static public void toDead(PcInstance pc){
		// 혈전 종료 처리 요청.
		toClanWarEnd(pc, null);
	}
	
	/**
	 * 혈맹간에 전쟁 종료처리하는 함수.
	 */
	static private void toClanWarEnd(PcInstance pc, Clan clan){
		// 군주가 아니면 무시.
		if(pc.getClassType() != Lineage.LINEAGE_CLASS_ROYAL)
			return;
		// 혈맹이 존재하지 않는다면 무시.
		if(pc.getClanId() == 0)
			return;
		// 초기화.
		if(clan == null)
			clan = find(pc);
		if(clan == null)
			return;
		// 혈맹간에 전쟁중일때.
		if(clan.getWarClan() != null){
			// 성혈맹이라면 성혈맹에 등록된 전쟁정보 제거.
			Kingdom kingdom = KingdomController.findClanName(clan.getWarClan());
			if(kingdom != null) 
				kingdom.getListWar().remove(clan.getName());
			//
			World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, clan.getName(), clan.getWarClan()));
			World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 4, clan.getWarClan(), clan.getName()));
			//
			clan.setWarClan(null);
		}
	}
	
	/**
	 * 리스하고 들어왓을때 호출됨.
	 *  : 혈전중일 경우 교전중이라는 것을 클라에게 알리기 위해.
	 * @param pc
	 * @param clan
	 */
	static private void toClanWarCheck(PcInstance pc, Clan clan) {
		// 혈맹이 존재하지 않는다면 무시.
		if(pc.getClanId() == 0)
			return;
		// 초기화.
		if(clan == null)
			clan = find(pc);
		if(clan == null)
			return;
		// 혈맹간에 전쟁중일때.
		if(clan.getWarClan() != null)
			pc.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 8, clan.getName(), clan.getWarClan()));
	}

	/**
	 * 혈맹이름으로 혈맹객체 찾기.
	 * @param clan_name
	 * @return
	 */
	static public Clan find(String clan_name){
		synchronized (list) {
			for(Clan c : list.values()){
				if(c.getName().equalsIgnoreCase(clan_name))
					return c;
			}
			return null;
		}
	}

	/**
	 * 사용자와 연결된 혈맹객체 찾아서 리턴.
	 * @param pc
	 * @return
	 */
	static public Clan find(PcInstance pc){
		synchronized (list) {
			return list.get(pc.getClanId());
		}
	}

	/**
	 * 혈맹 고유 아이디로 혈맹 객체 찾기.
	 * @param uid
	 * @return
	 */
	static public Clan find(int uid){
		synchronized (list) {
			return list.get(uid);
		}
	}

	/**
	 * 창설된 혈맹 목록에서 동일한 혈맹 이름이 존재하는지 확인해주는 함수.
	 * @param con
	 * @param name
	 * @return
	 */
	static private boolean isClanName(String name){
		synchronized (list) {
			for(Clan c : list.values()){
				if(c.getName().equalsIgnoreCase(name))
					return true;
			}
			return false;
		}
	}

	/**
	 * 혈맹 고유uid값의 다음값을 추출.
	 * @return
	 */
	static private int nextUid() {
		synchronized (next_uid) {
			return ++next_uid;
		}
	}

	/**
	 * 풀에 있는 혈맹객체 재사용하기 위해 꺼내기.
	 * @return
	 */
	static private Clan getPool(){
		Clan c = null;
		synchronized (pool) {
			if(pool.size()>0){
				c = pool.get(0);
				pool.remove(0);
			}
		}

		//		lineage.share.System.println("remove : "+pool.size());
		return c;
	}

	/**
	 * 사용 완료된 객체 풀에 넣기.
	 * @param c
	 */
	static private void setPool(Clan c){
		synchronized (pool) {
			if(!pool.contains(c))
				pool.add(c);
		}

		//		lineage.share.System.println("append : "+pool.size());
	}

	/**
	 * 해당 사용자에 카리 및 퀘스트수행 정보를 참고해서
	 * 받아 들일수 있는 최대 혈맹원수 연산후 리턴.
	 */
	static private int getClanMemberMaxSize(PcInstance pc){
		if(pc.getClanGrade() >= 1)
			return 50;
		return 0;
	}

	static public int getPoolSize(){
		return pool.size();
	}
	
	/**
	 * 혈맹 자동 저장
	 * 2017-12-14
	 * by all-night
	 */
	static public void saveClan(Connection con) {
		if (list != null) {
			synchronized (list) {
				PreparedStatement st = null;
				try {
					st = con.prepareStatement("DELETE FROM clan_list");
					st.executeUpdate();
					st.close();

					// close 함수에서도 요청하기 때문에 동기화 작업을 함.
					for (Clan c : list.values()) {
						if (c != null) {
							StringBuffer icon = new StringBuffer();
							if (c.getIcon() != null) {
								for (int i = 0; i < c.getIcon().length; ++i)
									icon.append(String.format("%02x", c.getIcon()[i] & 0xff));
							}

							st = con.prepareStatement("INSERT INTO clan_list SET uid=?, name=?, lord=?, icon=?, list=?, point=?, sell_point=?, total_point=?, "
									+ "경험치증가=?, 드랍확률증가=?, 아덴증가=?, 추타=?, 리덕=?, pvp_추타=?, pvp_리덕=?, 스턴내성=?, 치명타확률=?, sp=?");
							st.setInt(1, c.getUid());
							st.setString(2, c.getName() == null ? "" : c.getName());
							st.setString(3, c.getLord() == null ? "" : c.getLord());
							st.setString(4, icon.toString() == null ? "" : icon.toString());
							st.setString(5, c.getMemberNameList() == null ? "" : c.getMemberNameList());
							st.setInt(6, c.getClan_point());
							st.setInt(7, c.getSell_clan_point());
							st.setInt(8, c.getClan_total_point());
							st.setInt(9, c.get경험치증가());
							st.setInt(10, c.get드랍확률증가());
							st.setInt(11, c.get아덴증가());
							st.setInt(12, c.get추타());
							st.setInt(13, c.get리덕());
							st.setInt(14, c.getPvp_추타());
							st.setInt(15, c.getPvp_리덕());
							st.setInt(16, c.get스턴내성());
							st.setInt(17, c.get치명타확률());
							st.setInt(18, c.getSp());
							st.executeUpdate();
							st.close();
						}
					}
				} catch (Exception e) {
					lineage.share.System.printf("%s : saveClan()\r\n", ClanController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(st);
				}
			}
		}
	}
	
	/**
	 * 혈맹 군주 수정.
	 * 2019-07-11
	 * by connector12@nate.com
	 */
	static public void setClanLord(String name, Clan c) {
		synchronized (list) {
			if (c != null) {
				c.setLord(name);
				
				PreparedStatement st = null;
				Connection con = null;
				
				try {
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("UPDATE clan_list SET lord=? WHERE uid=?");
					st.setString(1, name);
					st.setInt(2, c.getUid());
					st.executeUpdate();
				} catch (Exception e) {
					lineage.share.System.printf("%s : setClanLord(String name, Clan c)\r\n", ClanController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st);
				}
			}
		}
	}
	
	/**
	 * 혈맹 창설 또는 정보변경시 DB에 저장.
	 * 2019-10-29
	 * by connector12@nate.com
	 */
	static public void toSaveClan(Clan c) {
		PreparedStatement st = null;
		Connection con = null;
		
		if (c != null) {
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("DELETE FROM clan_list WHERE uid=?");
				st.setInt(1, c.getUid());
				st.executeUpdate();			
				st.close();
				
				StringBuffer icon = new StringBuffer();
				if (c.getIcon() != null) {
					for (int i = 0; i < c.getIcon().length; ++i)
						icon.append(String.format("%02x", c.getIcon()[i] & 0xff));
				}
				
				st = con.prepareStatement("INSERT INTO clan_list SET uid=?, name=?, lord=?, icon=?, list=?, point=?, sell_point=?, total_point=?, "
						+ "경험치증가=?, 드랍확률증가=?, 아덴증가=?, 추타=?, 리덕=?, pvp_추타=?, pvp_리덕=?, 스턴내성=?, 치명타확률=?, sp=?");
				st.setInt(1, c.getUid());
				st.setString(2, c.getName() == null ? "" : c.getName());
				st.setString(3, c.getLord() == null ? "" : c.getLord());
				st.setString(4, icon.toString() == null ? "" : icon.toString());
				st.setString(5, c.getMemberNameList() == null ? "" : c.getMemberNameList());
				st.setInt(6, c.getClan_point());
				st.setInt(7, c.getSell_clan_point());
				st.setInt(8, c.getClan_total_point());
				st.setInt(9, c.get경험치증가());
				st.setInt(10, c.get드랍확률증가());
				st.setInt(11, c.get아덴증가());
				st.setInt(12, c.get추타());
				st.setInt(13, c.get리덕());
				st.setInt(14, c.getPvp_추타());
				st.setInt(15, c.getPvp_리덕());
				st.setInt(16, c.get스턴내성());
				st.setInt(17, c.get치명타확률());
				st.setInt(18, c.getSp());
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.printf("%s : tosave(Clan c)\r\n", ClanController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}
	
	/**
	 * 혈맹 문장 업로드시 DB저장.
	 * 2019-10-29
	 * by connector12@nate.com
	 */
	static public void toSaveClanMark(Clan c, int oldUid, Agit agit) {
		PreparedStatement st = null;
		Connection con = null;
		
		if (c != null) {
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("DELETE FROM clan_list WHERE uid=?");
				st.setInt(1, oldUid);
				st.executeUpdate();			
				st.close();
				
				StringBuffer icon = new StringBuffer();
				if (c.getIcon() != null) {
					for (int i = 0; i < c.getIcon().length; ++i)
						icon.append(String.format("%02x", c.getIcon()[i] & 0xff));
				}
				
				st = con.prepareStatement("INSERT INTO clan_list SET uid=?, name=?, lord=?, icon=?, list=?, point=?, sell_point=?, total_point=?, "
						+ "경험치증가=?, 드랍확률증가=?, 아덴증가=?, 추타=?, 리덕=?, pvp_추타=?, pvp_리덕=?, 스턴내성=?, 치명타확률=?, sp=?");
				st.setInt(1, c.getUid());
				st.setString(2, c.getName() == null ? "" : c.getName());
				st.setString(3, c.getLord() == null ? "" : c.getLord());
				st.setString(4, icon.toString() == null ? "" : icon.toString());
				st.setString(5, c.getMemberNameList() == null ? "" : c.getMemberNameList());
				st.setInt(6, c.getClan_point());
				st.setInt(7, c.getSell_clan_point());
				st.setInt(8, c.getClan_total_point());
				st.setInt(9, c.get경험치증가());
				st.setInt(10, c.get드랍확률증가());
				st.setInt(11, c.get아덴증가());
				st.setInt(12, c.get추타());
				st.setInt(13, c.get리덕());
				st.setInt(14, c.getPvp_추타());
				st.setInt(15, c.getPvp_리덕());
				st.setInt(16, c.get스턴내성());
				st.setInt(17, c.get치명타확률());
				st.setInt(18, c.getSp());
				st.executeUpdate();
				st.close();
				
				// 아지트 정보 변경
				if (agit != null) {
					st = con.prepareStatement("UPDATE clan_agit SET clan_id=? WHERE clan_id=?");
					st.setInt(1, oldUid);
					st.setInt(2, c.getUid());
					st.executeUpdate();
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : tosave(Clan c)\r\n", ClanController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}
	
	/**
	 * 혈맹 해산시 DB에 저장.
	 * 2019-10-29
	 * by connector12@nate.com
	 */
	static public void toDeleteClan(Clan c) {
		PreparedStatement st = null;
		Connection con = null;
		
		if (c != null) {
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("DELETE FROM clan_list WHERE uid=?");
				st.setInt(1, c.getUid());
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.printf("%s : toDeleteClan(Clan c)\r\n", ClanController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}

	/**
	 * 혊맹 가입, 탈퇴시 혈맹정보 변경.
	 * 2019-11-24
	 * by connector12@nate.com
	 */
	static public void updateClan(Clan c) {
		if (c != null) {
			PreparedStatement st = null;
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("UPDATE clan_list SET list=? WHERE uid=?");
				st.setString(1, c.getMemberNameList() == null ? "" : c.getMemberNameList());
				st.setInt(2, c.getUid());
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.printf("%s : updateClan(Clan c)\r\n", ClanController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}
	
	/**
	 * 캐릭터 이름 변경 주문서 사용시 호출.
	 * 2019-11-24
	 * by connector12@nate.com
	 */
	static public void changeName(PcInstance pc, String name, String newName) {
		Clan c = find(pc);
		if (c != null) {
			PreparedStatement st = null;
			Connection con = null;
			ResultSet rs = null;
			
			try {
				con = DatabaseConnection.getLineage();
				
				if (c.getLord().equalsIgnoreCase(pc.getName())) {
					c.setLord(newName);
					
					st = con.prepareStatement("UPDATE clan_list SET lord=? WHERE uid=?");
					st.setString(1, newName == null ? "" : newName);
					st.setInt(2, c.getUid());
					st.executeUpdate();
					st.close();
				}
				
				String memberList = null;
				st = con.prepareStatement("SELECT list FROM clan_list WHERE uid=?");
				st.setInt(1, c.getUid());
				rs = st.executeQuery();
				
				if (rs.next())
					memberList = rs.getString("list");
				
				st.close();

				if (memberList != null) {
					memberList = memberList.replace(pc.getName(), newName);
					
					st = con.prepareStatement("UPDATE clan_list SET list=? WHERE uid=?");
					st.setString(1, memberList == null ? "" : memberList);
					st.setInt(2, c.getUid());
					st.executeUpdate();
					st.close();
				}
				
				c.removeMemberList(pc.getName());
				c.appendMemberList(newName);
			} catch (Exception e) {
				lineage.share.System.printf("%s : changeName(PcInstance pc, String name, String newName)\r\n", ClanController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}
	}
}
