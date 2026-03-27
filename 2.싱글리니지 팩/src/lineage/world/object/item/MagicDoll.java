package lineage.world.object.item;

import java.sql.Connection;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.MagicDollController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class MagicDoll extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new MagicDoll();
		return item;
	}

	@Override
	public void close() {
		super.close();

	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PcInstance) {
			PcInstance pc = (PcInstance) cha;

			boolean isTeleport = true;

			for (int map : Lineage.MagicDollTeleportImpossibleMap) {
				if (pc.getMap() == map)
					isTeleport = false;
			}

			if (!isTeleport) {
				ChattingController.toChatting(pc, "이 근처에서는 마법인형을 소환할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			// 마법인형 소환이 처음일때
			if (pc.getMagicDollinstance() == null) {
				// 마법인형 소환.
				MagicDollController.toEnabled(pc, this);
			} else {
				if (pc.getMagicDoll().getObjectId() != getObjectId()) {
					// 마법인형 제거.
					MagicDollController.toDisable(pc, pc.getMagicDoll(), false);
					MagicDollController.toEnabled(pc, this);
				} else {
					MagicDollController.toDisable(pc, this, true);
				}
			}
		}
	}

	@Override
	public void toWorldJoin(Connection con, PcInstance pc) {
		super.toWorldJoin(con, pc);
		// 마법인형은 사용자가 접속시 착용상태로 둘 필요가 없음. (사용자가 월드 종료시 해당 아이템이 활성화된 상태로 등록이 되기 때문에 반드시 처리해줘야 함.)
		setEquipped(false);
		pc.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
	}

}
