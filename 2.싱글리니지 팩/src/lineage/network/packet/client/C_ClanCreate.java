package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.object.instance.PcInstance;

public class C_ClanCreate extends ClientBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length) {
		if (bp == null)
			bp = new C_ClanCreate(data, length);
		else
			((C_ClanCreate) bp).clone(data, length);
		return bp;
	}

	public C_ClanCreate(byte[] data, int length) {
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc) {
		// 버그방지
		if (pc == null || pc.isWorldDelete())
			return this;
		
//		ChattingController.toChatting(pc, "혈맹 창설은 [.창설 혈맹명] 명령어를 사용 하십시오.", Lineage.CHATTING_MODE_MESSAGE);
//		return this;
		
//		String clan_name = readS().replaceAll(" ", "").trim();
//
//		if (pc.getClanId() == 0 && clan_name != null) {
//			if (pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
//				if (pc.getLevel() >= Lineage.CLAN_MAKE_LEV) {
//					if (clan_name.length() >= Lineage.CLAN_NAME_MIN_SIZE && clan_name.length() <= Lineage.CLAN_NAME_MAX_SIZE) {
//						if (Lineage.server_version > 200) {
//							if (pc.getInventory().isAden(30000, true)) {
//								ClanController.toCreate(pc, clan_name);
//							} else {
//								// \f1아데나가 충분치 않습니다.
//								pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
//							}
//						} else {
//							ClanController.toCreate(pc, clan_name);
//						}
//					} else {
//						// 98 \f1혈맹이름이 너무 깁니다.
//						pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 98));
//					}
//				} else {
//					// 233 \f1레벨 5 이하의 군주는 혈맹을 만들 수 없습니다.
//					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 233));
//				}
//			} else {
//				// 85 \f1왕자와 공주만이 혈맹을 창설할 수 있습니다.
//				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 85));
//			}
//		} else {
//			// 86 \f1이미 혈맹을 창설하였습니다.
//			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 86));
//		}
		
		String clan_name = readS().replaceAll(" ", "").trim();

		if (pc.getClanId() == 0 && clan_name != null) {
			if (pc.getLevel() >= Lineage.CLAN_MAKE_LEV) {
				if (clan_name.length() >= Lineage.CLAN_NAME_MIN_SIZE && clan_name.length() <= Lineage.CLAN_NAME_MAX_SIZE) {
					if (Lineage.server_version > 200) {
						if (pc.getInventory().isAden(30000, true)) {
							ClanController.toCreate(pc, clan_name);
						} else {
							// \f1아데나가 충분치 않습니다.
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
						}
					} else {
						ClanController.toCreate(pc, clan_name);
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
			// 86 \f1이미 혈맹을 창설하였습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 86));
		}
		return this;
	}

}
