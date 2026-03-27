package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

import lineage.world.object.object;

/**
 * GM 툴 채팅 모니터링: 채팅 발생 시 gm_chat_log 테이블에 INSERT.
 * 테이블이 없으면 무시.
 */
public final class GmChatLogDatabase {

	private static final int MAX_MSG_LEN = 500;

	/**
	 * 채팅 한 줄을 gm_chat_log에 기록.
	 * @param o 발신자 (null이면 GM/운영자)
	 * @param channel Lineage.CHATTING_MODE_* (0=일반, 2=외침, 3=전체, 4=혈맹, 9=귓말, 11=파티, 12=장사, 20=시스템)
	 * @param targetName 귓말 시 상대방 이름, 아니면 null 또는 ""
	 * @param msg 내용 (500자 초과 시 잘림)
	 */
	static public void append(object o, int channel, String targetName, String msg) {
		if (msg == null) msg = "";
		if (msg.length() > MAX_MSG_LEN) msg = msg.substring(0, MAX_MSG_LEN);
		String charName = (o == null) ? "******" : (o.getName() == null ? "" : o.getName());
		String target = (targetName == null) ? "" : targetName;
		try (Connection con = DatabaseConnection.getLineage();
				PreparedStatement st = con.prepareStatement(
						"INSERT INTO gm_chat_log (channel, char_name, target_name, msg) VALUES (?,?,?,?)")) {
			st.setInt(1, channel);
			st.setString(2, charName);
			st.setString(3, target);
			st.setString(4, msg);
			st.executeUpdate();
		} catch (Exception e) {
			// gm_chat_log 테이블이 없을 수 있음 - 무시
		}
	}
}
