package lineage.network.packet.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.database.AccountDatabase;
import lineage.database.CharacterMarbleDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ServerNoticeDatabase;
import lineage.network.LineageClient;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.server.S_AccountTime;
import lineage.network.packet.server.S_Character;
import lineage.network.packet.server.S_CharacterLength;
import lineage.network.packet.server.S_Login;
import lineage.network.packet.server.S_LoginFail;
import lineage.share.Lineage;
import lineage.util.Util;

public class C_NoticeOk extends ClientBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length) {
		if (bp == null)
			bp = new C_NoticeOk(data, length);
		else
			((C_NoticeOk) bp).clone(data, length);
		return bp;
	}

	public C_NoticeOk(byte[] data, int length) {
		clone(data, length);
	}

	@Override
	public BasePacket init(LineageClient c) {

		boolean skip = false;
		if (isRead(1))
			skip = readC() == 1;

		// 공지사항 확인.
		if (ServerNoticeDatabase.toNotice(c))
			return this;

		// 처리.
		if (c.getAccountUid() > 0) {
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();

				// 공지 uid 디비 갱신.
				AccountDatabase.updateNoticeUid(con, c.getAccountUid(), c.getAccountNoticeUid());

				if (Lineage.server_version < 163)
					c.toSender(S_Login.clone(BasePacketPooling.getPool(S_Login.class), S_LoginFail.REASON_ACCESS_OK));

				int length = AccountDatabase.getCharacterLength(con, c.getAccountUid());
				// 소유중인 케릭갯수 전송
				c.toSender(S_CharacterLength.clone(BasePacketPooling.getPool(S_CharacterLength.class), length, 6));
				// 소유중인 케릭터정보 전송
				if (length > 0) {
					PreparedStatement st = null;
					ResultSet rs = null;
					try {
						st = con.prepareStatement("SELECT name, clanNAME, class, sex, lawful, nowHP, nowMP, ac, level, str, dex, con, wis, cha, inter, register_date, objID FROM characters WHERE account_uid=?");
						st.setInt(1, c.getAccountUid());
						rs = st.executeQuery();
						while (rs.next()) {
							if (CharacterMarbleDatabase.checkData(rs.getLong("objID"))) {
								continue;
							}
							
							String name = rs.getString(1);
							String clan = rs.getString(2);
							int type = rs.getInt(3);
							int sex = rs.getInt(4);
							int lawful = rs.getInt(5);
							int hp = rs.getInt(6);
							int mp = rs.getInt(7);
							int ac = rs.getInt(8);
							int level = rs.getInt(9);
							int _str = rs.getInt(10);
							int _dex = rs.getInt(11);
							int _con = rs.getInt(12);
							int _wis = rs.getInt(13);
							int _cha = rs.getInt(14);
							int _int = rs.getInt(15);
							long register = rs.getLong("register_date");

							try {
								c.toSender(S_Character.clone(BasePacketPooling.getPool(S_Character.class), Opcodes.S_OPCODE_CHARLIST, name, clan, type, sex, lawful, hp, mp, ac, level, _str, _dex, _con, _wis, _cha, _int,
										Util.getLocaleString(register, false)));
							} catch (Exception e) {
								c.toSender(S_Character.clone(BasePacketPooling.getPool(S_Character.class), Opcodes.S_OPCODE_CHARLIST, name, clan, type, sex, lawful, hp, mp, ac, level, _str, _dex, _con, _wis, _cha, _int,
										null));
							}
						}
					} catch (Exception e) {
						lineage.share.System.printf("%s : init(Client c) 2\r\n", C_NoticeOk.class.toString());
						lineage.share.System.println(e);
					} finally {
						DatabaseConnection.close(st, rs);
					}
				}
				if (Lineage.server_version >= 300)
					// 계정 남은시간 전송.
					c.toSender(S_AccountTime.clone(BasePacketPooling.getPool(S_AccountTime.class), c.getAccountTime()));
			} catch (Exception e) {
				lineage.share.System.printf("%s : init(Client c) 1\r\n", C_NoticeOk.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con);
			}
		}
		return this;
	}
}
