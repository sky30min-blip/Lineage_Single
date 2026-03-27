package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.ItemDropMessage;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ItemDropMessageDatabase {
	static private List<ItemDropMessage> list;
	
	static public void init(Connection con) {
		TimeLine.start("ItemDropMessageDatabase..");

		list = new ArrayList<ItemDropMessage>();
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = con.prepareStatement("SELECT * FROM item_drop_msg");
			rs = st.executeQuery();

			while (rs.next()) {
				ItemDropMessage idm = new ItemDropMessage();
				idm.setItem(rs.getString("아이템"));
				idm.set획득시알림여부(rs.getInt("아이템획득시_알림여부") == 1);
				idm.setEn(rs.getInt("인첸트"));
				list.add(idm);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemDropMessageDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();	
	}
	
	static public void reload() {
		TimeLine.start("ItemDropMessageDatabase..");

		list = new ArrayList<ItemDropMessage>();
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;

		try {
			list.clear();
			
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM item_drop_msg");
			rs = st.executeQuery();

			while (rs.next()) {
				ItemDropMessage idm = new ItemDropMessage();
				idm.setItem(rs.getString("아이템"));
				idm.set획득시알림여부(rs.getInt("아이템획득시_알림여부") == 1);
				idm.setEn(rs.getInt("인첸트"));
				list.add(idm);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload()\r\n", ItemDropMessageDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		TimeLine.end();	
	}
	
	static public List<ItemDropMessage> getList() {
		return new ArrayList<ItemDropMessage>(list);
	}
	
	static public boolean find(ItemInstance item, boolean is) {
		if (item != null && item.getItem() != null) {
			for (ItemDropMessage idm : getList()) {
				if (is) {
					if (idm.getItem().equalsIgnoreCase(item.getItem().getName()) && item.getEnLevel() >= idm.getEn()) {
						return true;
					}
				} else {
					if (idm.is획득시알림여부() && idm.getItem().equalsIgnoreCase(item.getItem().getName())) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	static public boolean find(Item item, int en, boolean is) {
		if (item != null) {
			for (ItemDropMessage idm : getList()) {
				if (is) {
					if (idm.getItem().equalsIgnoreCase(item.getName()) && en >= idm.getEn()) {
						return true;
					}
				} else {
					if (idm.is획득시알림여부() && idm.getItem().equalsIgnoreCase(item.getName())) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	static public boolean find(String item, int en, boolean is) {
		if (item != null) {
			for (ItemDropMessage idm : getList()) {
				if (is) {
					if (idm.getItem().equalsIgnoreCase(item) && en <= idm.getEn()) {
						return true;
					}
				} else {
					if (idm.is획득시알림여부() && idm.getItem().equalsIgnoreCase(item)) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	/**
	 * 특정 아이템 획득시 전체 메세지 여부.
	 * 몬스터 드랍, 상자 획득
	 * 2020-11-29
	 * by connector12@nate.com
	 */
	static public void sendMessage(object o, String item1, String item2) {
		String local = Util.getMapName((Character) o);
		
		if (o != null && o instanceof PcInstance && item1 != null && item2 != null) {
			if (find(item1, 0, false)) {			
				if (Lineage.is_item_drop_msg_name) {
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fR어느 아덴 용사가 \\fU%s \\fR에서", local)));
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fY%s \\fR을(를) 획득하였습니다.", item1)));
				} else {
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fT%s \\fR님이 \\fU%s \\fR에서",o.getName(), local)));
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fY%s \\fR을(를) 획득하였습니다.", item1)));
				}
			}
		}
	}
	static public void sendbuMessage(object o, String item1, String item2) {
		String local = Util.getMapName((Character) o);
		
		if (o != null && o instanceof PcInstance && item1 != null && item2 != null) {
			if (find(item1, 0, false)) {			
				if (Lineage.is_item_drop_msg_name) {
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fR어느 아덴 용사가 \\fU%s \\fR에서", item2)));
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fY%s \\fR을(를) 획득하였습니다.", item1)));
				} else {
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fT%s \\fR님이 \\fU%s \\fR에서",o.getName(), item2)));
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fY%s \\fR을(를) 획득하였습니다.", item1)));
				}
			}
		}
	}
	/**
	 * 특정 아이템 획득시 전체 메세지 여부.
	 * 2020-11-29
	 * by connector12@nate.com
	 */
	static public void sendMessageMagicDoll(object o, String item) {
		if (Lineage.is_item_drop_msg_doll && o != null && o instanceof PcInstance && item != null) {
			if (find(item, 0, false)) {
				if (Lineage.is_item_drop_msg_name) {
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "\\fR어느 아덴 용사가 인형 합성으로"));
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fY%s \\fR을(를) 획득하였습니다.", item)));
				} else {
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fT%s \\fR님이 인형 합성으로", o.getName())));
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fY%s \\fR을(를) 획득하였습니다.", item)));
				}
			}
		}
	}
	
	/**
	 * 특정 아이템 획득시 전체 메세지 여부.
	 * 2020-11-29
	 * by connector12@nate.com
	 */
	static public void sendMessageMagicDoll2(object o, String item) {
		if (Lineage.is_item_drop_msg_doll && o != null && o instanceof PcInstance && item != null) {
			if (Lineage.is_item_drop_msg_name) {
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "\\fR어느 아덴 용사가 인형 진화에 성공하여"));
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fY%s \\fR을(를) 획득하였습니다.", item)));
			} else {
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fT%s \\fR님이 인형 진화에 성공하여", o.getName())));
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fY%s \\fR을(를) 획득하였습니다.", item)));
			}
		}
	}
	
	/**
	 * 특정 아이템 획득시 전체 메세지 여부.
	 * 2020-11-29
	 * by connector12@nate.com
	 */
	static public void sendMessageLife(object o, String item) {
		if (Lineage.is_item_drop_msg_life && o != null && o instanceof PcInstance && item != null) {
			if (Lineage.is_item_drop_msg_name) {
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fR어느 아덴 용사가 \\fY%s \\fR에", item)));
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "\\fR생명을 부여했습니다."));
			} else {
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fT%s \\fR님이 \\fY%s \\fR에", o.getName(), item)));
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "\\fR생명을 부여했습니다."));
			}
		}
	}
	
	/**
	 * 특정 아이템 획득시 전체 메세지 여부.
	 * 2020-11-29
	 * by connector12@nate.com
	 */
	static public void sendMessageEn(object o, ItemInstance item, boolean is) {
		if (Lineage.is_item_drop_msg_en && o != null && o instanceof PcInstance && item != null && item.getItem() != null) {
			if (find(item, true)) {
				if (is) {
					if (Lineage.is_item_drop_msg_name) {
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fR어느 아덴 용사가 \\fY+%d %s", item.getEnLevel(), item.getItem().getName())));
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "\\fR인첸트에 성공했습니다."));
					} else {
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fT%s \\fR님이 \\fY+%d %s", o.getName(), item.getEnLevel(), item.getItem().getName())));
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "\\fR인첸트에 성공했습니다."));
					}
				} else {
					if (Lineage.is_item_drop_msg_name) {
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fR어느 아덴 용사가 \\fY+%d %s", item.getEnLevel(), item.getItem().getName())));
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "\\fR인첸트에 실패하였습니다."));
					} else {
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fT%s \\fR님이 \\fY+%d %s", o.getName(), item.getEnLevel(), item.getItem().getName())));
						World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "\\fR인첸트에 실패하였습니다."));
					}
				}
			}
		}
	}
	
	/**
	 * 특정 아이템 획득시 전체 메세지 여부.
	 * 2020-11-29
	 * by connector12@nate.com
	 */
	static public void sendMessageCreateItem(object o, Item item) {
		if (Lineage.is_item_drop_msg_create && o != null && o instanceof PcInstance && item != null) {
			if (find(item.getName(), 0, false)) {
				if (Lineage.is_item_drop_msg_name) {
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fR어느 아덴 용사가 \\fY%s \\fR을(를)", item.getName())));
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "\\fR제작하였습니다."));
				} else {
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fT%s \\fR님이 \\fY%s \\fR을(를)", o.getName(), item.getName())));
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "\\fR제작하였습니다."));
				}
			}
		}
	}
}
