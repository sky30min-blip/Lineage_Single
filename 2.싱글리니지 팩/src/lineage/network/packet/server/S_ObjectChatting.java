package lineage.network.packet.server;

import lineage.database.ServerReloadDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.RankController;
import lineage.world.controller.WantedController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class S_ObjectChatting extends ServerBasePacket {

	private boolean suppressBroadcast;
	
	static public BasePacket clone(BasePacket bp, object o, int mode, String msg) {
		if (bp == null)
			bp = new S_ObjectChatting(o, mode, msg);
		else
			((S_ObjectChatting) bp).clone(o, mode, msg);
		return bp;
	}

	static public BasePacket clone(BasePacket bp, String msg) {
		if (bp == null)
			bp = new S_ObjectChatting(null, 0x14, msg);
		else
			((S_ObjectChatting) bp).clone(null, 0x14, msg);
		return bp;
	}
	
	public S_ObjectChatting(object o, int mode, String msg) {
		clone(o, mode, msg);
	}

	public void clone(object o, int mode, String msg) {
		clear();
		suppressBroadcast = false;
		if (isBrokenSystemMessage(mode, msg)) {
			suppressBroadcast = true;
			// 브로드캐스트는 상위(World.toSender)에서 차단하되,
			// 패킷 자체는 정상 구조로 만들어 클라이언트 튕김을 방지.
			msg = "";
		}
		
		boolean ranker = false;
		int rank = 0;
		StringBuffer text = new StringBuffer();
		
		try {
			// 랭킹 별 표시를 위해서 랭킹 추출
			if (o != null && o instanceof PcInstance && mode == Lineage.CHATTING_MODE_GLOBAL) {
				rank = RankController.getAllRank(o.getObjectId());
				
				if (rank < 1)
					rank = o.lastRank;

				if (rank > 0 && rank <= Lineage.rank_class_1 && o.getLevel() >= Lineage.rank_min_level)
					ranker = true;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : 랭커 채팅 오류\r\n", S_ObjectChatting.class.toString());
			lineage.share.System.println(e);
		}

		switch (mode) {
		case Lineage.CHATTING_MODE_NORMAL:
			String name = o.getName();

			// 수배중 체크
			if (o instanceof PcInstance && WantedController.checkWantedPc(o))
				name = Lineage.wanted_name + name;
			
			text.append(name);
			text.append(": ");
			text.append(msg);
			normal(o, mode, text.toString());
			break;
		case Lineage.CHATTING_MODE_SHOUT:
			if (o == null || o.getName().equals("$858")
			|| o.getName().equals("$854")
			|| o.getName().equals("$886")
			|| o.getName().equals("$755")
			|| o.getName().equals("$752")
			|| o.getName().equals("$754")
			|| o.getName().equals("$753")) {
				text.append(o.getTitle());
				text.append(": ");
			} else {
				text.append(o.getName());
				text.append(": ");
			}
			text.append(msg);
			shotsay(o, text.toString());
			break;
		case Lineage.CHATTING_MODE_GLOBAL:
			if (o == null || o.getGm() > 0) {
				text.append("[******] ");
			} else {
				String chaName = "[" + o.getName() + "] ";
				
				// 랭킹 별 추가
				if (ranker) {
					if (rank <= Lineage.rank_class_4)
						msg = "\\d7" + msg;
					else if (rank <= Lineage.rank_class_3)
						msg = "\\d6" + msg;
					else if (rank <= Lineage.rank_class_2)
						msg = "\\d5" + msg;
					else
						msg = "\\d4" + msg;
					
					if (msg.contains("\\d4"))
						chaName = "" + chaName;
					
					if (msg.contains("\\d5"))
						chaName = "  " + chaName;
					
					if (msg.contains("\\d6"))
						chaName = "    " + chaName;
					
					if (msg.contains("\\d7"))
						chaName = "      " + chaName;
				}
				
				text.append(chaName);
			}
			text.append(msg);
			global(o, mode, text.toString());
			break;
		case Lineage.CHATTING_MODE_CLAN: {
			if (o instanceof PcInstance) {
				PcInstance pc = (PcInstance) o;

				text.append("[");
				text.append(pc.isClanOrder() ? "지휘관" : o.getClanGrade() == 0 ? "혈맹원" : o.getClanGrade() == 1 ? "수호기사" : o.getClanGrade() == 2 ? "부군주" : "군주");
				text.append("]");
				text.append("{");
				text.append(o.getName());
				if (o.getAge() != 0) {
					text.append("(");
					text.append(o.getAge());
					text.append(")");
				}
				text.append("} ");
				text.append(o.getClanGrade() == 3 ? "\\fR" + msg : pc.isClanOrder() ? "\\fU" + msg : msg);
				clan(o, mode, text.toString());
			}

			break;
		}
		case 0x08: // 귓속말 - 받는사람
			whisperReceiver(o, mode, msg);
			break;
		case 0x09: // 귓속말 - 보낸사람
			text.append("-> (");
			text.append(o == null ? ServerReloadDatabase.manager_character_id : o.getName());
			text.append(") ");
			text.append(msg);
			whisperSender(o, mode, text.toString());
			break;
		case Lineage.CHATTING_MODE_PARTY:
			if (o != null) {
				text.append("(");
				text.append(o.getName());
				text.append(") ");
			}
			text.append(msg);
			party(o, mode, text.toString());
			break;
		case Lineage.CHATTING_MODE_TRADE:
			if (Lineage.server_version <= 200)
				text.append("\\fR");
			text.append("[");
			text.append(o.getName());
			text.append("] ");
			text.append(msg);
			trade(mode, text.toString());
			break;
		case 0x0D: // 혈맹 수호기사 채팅 %
			break;
		case 0x0E: // 채팅파티 채팅 *
			break;
		case 0x14:
			message(0x09, msg);
			break;
		}
	}

	public boolean isSuppressBroadcast() {
		return suppressBroadcast;
	}

	private boolean isBrokenSystemMessage(int mode, String msg) {
		if (msg == null)
			return false;
		if (!(mode == Lineage.CHATTING_MODE_GLOBAL || mode == Lineage.CHATTING_MODE_MESSAGE || mode == 0x14))
			return false;
		String t = msg.trim();
		if (t.length() < 3)
			return false;
		// "???? ????" 류 + 깨짐 문자(�) 포함 월드/시스템 메시지는 전송 차단.
		if (t.contains("??") || t.contains("？") || t.contains("�"))
			return true;
		return t.matches("[\\?\\s]+");
	}

	private void trade(int mode, String msg) {
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	/**
	 * 일반 채팅
	 */
	private void normal(object o, int mode, String msg) {
		if (o instanceof PcInstance)
			writeC(Opcodes.S_OPCODE_NORMALCHAT);
		else
			writeC(Opcodes.S_OPCODE_SHOTSAY);
		writeC(mode);
		writeD(o.getObjectId());
		writeS(msg);
		writeH(o.getX());
		writeH(o.getY());
	}

	/**
	 * 전체 채팅
	 */
	private void global(object o, int mode, String msg) {
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	/**
	 * 혈맹 채팅
	 */
	private void clan(object o, int mode, String msg) {
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	/**
	 * 귓속말
	 */
	private void whisperReceiver(object o, int mode, String msg) {
		writeC(Opcodes.S_OPCODE_WHISPERCHAT);
		writeS(o == null ? ServerReloadDatabase.manager_character_id : o.getName());
		writeS(msg);
	}

	/**
	 * 파티 채팅
	 */
	private void party(object o, int mode, String msg) {
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	/**
	 * 귓속말
	 */
	private void whisperSender(object o, int mode, String msg) {
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	/**
	 * 일반 텍스트 표현
	 */
	private void message(int mode, String msg) {
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(mode);
		writeS(msg);
	}

	private void shotsay(object o, String msg) {
		writeC(Opcodes.S_OPCODE_SHOTSAY);
		writeC(0x02); // 0x00:일반채팅색상, 0x02:외치기색상
		if (o != null)
			writeD(o.getObjectId());
		else
			writeD(0);
		writeS(msg);
	}
}
