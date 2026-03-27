package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.database.BackgroundDatabase;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterDropDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.MonsterSkillDatabase;
import lineage.database.NpcDatabase;
import lineage.database.NpcShopDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.object;

/**
 * GM 툴 접속 중 반영: gm_item_delivery, gm_adena_delivery, gm_location_delivery 테이블을
 * 주기적으로 확인해 접속 중인 캐릭터에게 아이템/아데나/좌표를 즉시 반영한다.
 */
public class GmDeliveryController {

	static public void toTimer(long time) {
		// 1) 아이템 지급
		try (Connection con = DatabaseConnection.getLineage();
				PreparedStatement sel = con.prepareStatement("SELECT cha_objId, objId FROM gm_item_delivery WHERE delivered=0");
				ResultSet rs = sel.executeQuery()) {
			while (rs.next()) {
				long chaObjId = rs.getLong("cha_objId");
				long objId = rs.getLong("objId");
				PcInstance pc = World.findPc(chaObjId);
				if (pc != null) {
					CharactersDatabase.deliverOneGmItem(pc, objId);
				}
				try (PreparedStatement upd = con.prepareStatement("UPDATE gm_item_delivery SET delivered=1 WHERE objId=?")) {
					upd.setLong(1, objId);
					upd.executeUpdate();
				}
			}
		} catch (Exception e) {
			// gm_item_delivery 테이블이 없을 수 있음
		}

		// 2) 아데나 변경 즉시 반영
		try (Connection con = DatabaseConnection.getLineage();
				PreparedStatement sel = con.prepareStatement("SELECT id, cha_objId, new_count FROM gm_adena_delivery WHERE delivered=0");
				ResultSet rs = sel.executeQuery()) {
			while (rs.next()) {
				int id = rs.getInt("id");
				long chaObjId = rs.getLong("cha_objId");
				long newCount = rs.getLong("new_count");
				PcInstance pc = World.findPc(chaObjId);
				if (pc != null) {
					// findAden()은 Aden 서브클래스만 인식. DB 로드 시 일반 ItemInstance면 null → 이름으로 보조 검색
					ItemInstance aden = pc.getInventory().findAden();
					if (aden == null)
						aden = pc.getInventory().find("아데나", true);
					if (aden != null) {
						pc.getInventory().count(aden, newCount, true);
					} else if (newCount > 0) {
						ItemInstance ni = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
						if (ni != null) {
							ni.setObjectId(ServerDatabase.nextItemObjId());
							ni.setCount(newCount);
							pc.getInventory().append(ni, true);
						}
					}
				}
				try (PreparedStatement upd = con.prepareStatement("UPDATE gm_adena_delivery SET delivered=1 WHERE id=?")) {
					upd.setInt(1, id);
					upd.executeUpdate();
				}
			}
		} catch (Exception e) {
			// gm_adena_delivery 테이블이 없을 수 있음
		}

		// 3) 좌표 이동 즉시 반영
		try (Connection con = DatabaseConnection.getLineage();
				PreparedStatement sel = con.prepareStatement("SELECT id, cha_objId, locX, locY, locMAP FROM gm_location_delivery WHERE delivered=0");
				ResultSet rs = sel.executeQuery()) {
			while (rs.next()) {
				int id = rs.getInt("id");
				long chaObjId = rs.getLong("cha_objId");
				int locX = rs.getInt("locX");
				int locY = rs.getInt("locY");
				int locMAP = rs.getInt("locMAP");
				PcInstance pc = World.findPc(chaObjId);
				if (pc != null) {
					pc.toTeleport(locX, locY, locMAP, true);
				}
				try (PreparedStatement upd = con.prepareStatement("UPDATE gm_location_delivery SET delivered=1 WHERE id=?")) {
					upd.setInt(1, id);
					upd.executeUpdate();
				}
			}
		} catch (Exception e) {
			// gm_location_delivery 테이블이 없을 수 있음
		}

		// 4) GM 툴에서 보낸 전체 채팅 브로드캐스트
		try (Connection con = DatabaseConnection.getLineage();
				PreparedStatement sel = con.prepareStatement("SELECT id, msg FROM gm_chat_send WHERE sent=0");
				ResultSet rs = sel.executeQuery()) {
			while (rs.next()) {
				int id = rs.getInt("id");
				String msg = rs.getString("msg");
				if (msg != null && !msg.isEmpty()) {
					ChattingController.toChatting(null, msg, Lineage.CHATTING_MODE_GLOBAL);
				}
				try (PreparedStatement upd = con.prepareStatement("UPDATE gm_chat_send SET sent=1 WHERE id=?")) {
					upd.setInt(1, id);
					upd.executeUpdate();
				}
			}
		} catch (Exception e) {
			// gm_chat_send 테이블이 없을 수 있음
		}

		// 5) 웹 GM 툴 서버 명령 (gm_server_command 폴링)
		try (Connection con = DatabaseConnection.getLineage();
				PreparedStatement sel = con.prepareStatement("SELECT id, command, param FROM gm_server_command WHERE executed=0");
				ResultSet rs = sel.executeQuery()) {
			while (rs.next()) {
				int id = rs.getInt("id");
				String cmd = rs.getString("command");
				String param = rs.getString("param") != null ? rs.getString("param") : "";
				try {
					if ("server_open_wait".equalsIgnoreCase(cmd)) {
						CommandController.serverOpenWait();
					} else if ("server_open".equalsIgnoreCase(cmd)) {
						CommandController.serverOpen();
					} else if ("world_clear".equalsIgnoreCase(cmd)) {
						CommandController.toWorldItemClear(null);
					} else if ("character_save".equalsIgnoreCase(cmd)) {
						for (PcInstance pc : World.getPcList()) {
							pc.toCharacterSave();
						}
						lineage.share.System.println("캐릭터 정보 저장 완료");
					} else if ("kingdom_war".equalsIgnoreCase(cmd)) {
						CommandController.setKingdomWar();
					} else if ("all_buff".equalsIgnoreCase(cmd)) {
						CommandController.toBuffAll(null);
					} else if ("robot_on".equalsIgnoreCase(cmd)) {
						RobotController.reloadPcRobot(false);
					} else if ("robot_off".equalsIgnoreCase(cmd)) {
						RobotController.reloadPcRobot(true);
					} else if ("event_poly".equalsIgnoreCase(cmd)) {
						EventController.toPoly("1".equals(param.trim()));
					} else if ("event_rank_poly".equalsIgnoreCase(cmd)) {
						EventController.toRankPoly("1".equals(param.trim()));
					} else if ("npc_despawn".equalsIgnoreCase(cmd)) {
						String spawnName = param != null ? param.trim() : "";
						if (!spawnName.isEmpty()) {
							object o = World.findObjectByDatabaseKey(spawnName);
							if (o != null) {
								CharacterController.toWorldOut(o);
								World.remove(o);
								try (PreparedStatement ins = con.prepareStatement("INSERT IGNORE INTO gm_npc_despawned (spawn_name) VALUES (?)")) {
									ins.setString(1, spawnName);
									ins.executeUpdate();
								}
								lineage.share.System.println("[gm_server_command] npc_despawn 처리됨: " + spawnName);
							} else {
								lineage.share.System.println("[gm_server_command] npc_despawn 실패 - 월드에서 찾을 수 없음 (스폰 name 확인): " + spawnName);
							}
						}
					} else if ("npc_respawn".equalsIgnoreCase(cmd)) {
						String spawnName = param != null ? param.trim() : "";
						if (!spawnName.isEmpty() && NpcSpawnlistDatabase.spawnOne(spawnName)) {
							try (PreparedStatement del = con.prepareStatement("DELETE FROM gm_npc_despawned WHERE spawn_name=?")) {
								del.setString(1, spawnName);
								del.executeUpdate();
							}
						}
					} else if ("reload".equalsIgnoreCase(cmd)) {
						String p = param != null ? param.trim().toLowerCase() : "";
						if ("npc".equals(p)) {
							NpcDatabase.reload();
							lineage.share.System.println("[gm_server_command] reload: npc 테이블 리로드 완료");
						} else if ("item".equals(p)) {
							ItemDatabase.reload();
							lineage.share.System.println("[gm_server_command] reload: item 테이블 리로드 완료");
						} else if ("monster".equals(p)) {
							MonsterDatabase.reload();
							lineage.share.System.println("[gm_server_command] reload: monster 테이블 리로드 완료");
						} else if ("monster_drop".equals(p)) {
							MonsterDropDatabase.reload();
							lineage.share.System.println("[gm_server_command] reload: monster_drop 테이블 리로드 완료");
						} else if ("monster_skill".equals(p)) {
							MonsterSkillDatabase.reload();
							lineage.share.System.println("[gm_server_command] reload: monster_skill 테이블 리로드 완료");
						} else if ("npc_shop".equals(p)) {
							NpcShopDatabase.reload();
							lineage.share.System.println("[gm_server_command] reload: npc_shop 테이블 리로드 완료");
						} else if ("background_spawnlist".equals(p)) {
							BackgroundDatabase.reload();
							lineage.share.System.println("[gm_server_command] reload: background_spawnlist 테이블 리로드 완료");
						} else if ("monster_spawnlist".equals(p) || "all_spawn".equals(p) || "전체스폰".equals(p)) {
							MonsterSpawnlistDatabase.reload();
							lineage.share.System.println("[gm_server_command] reload: monster_spawnlist(전체스폰) 리로드 완료");
						} else {
							lineage.share.System.println("[gm_server_command] reload: 알 수 없는 param=" + param);
						}
					}
				} catch (Exception ex) {
					lineage.share.System.println("[gm_server_command] 실행 오류: " + cmd + " - " + ex);
				}
				try (PreparedStatement upd = con.prepareStatement("UPDATE gm_server_command SET executed=1 WHERE id=?")) {
					upd.setInt(1, id);
					upd.executeUpdate();
				}
			}
		} catch (Exception e) {
			// gm_server_command 테이블이 없을 수 있음
		}
	}

}
