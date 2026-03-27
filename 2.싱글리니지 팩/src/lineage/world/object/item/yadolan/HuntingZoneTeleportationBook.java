package lineage.world.object.item.yadolan;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.DungeonBook;
import lineage.bean.database.FirstSpawn;
import lineage.database.DungeontellbookDatabase;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.WantedController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class HuntingZoneTeleportationBook extends ItemInstance {

    public static synchronized ItemInstance clone(ItemInstance item) {
        if (item == null) {
            item = new HuntingZoneTeleportationBook();
        }
        return item;
    }

    @Override
    public void toClick(Character cha, ClientBasePacket cbp) {
        List<String> msg = createDungeonMessages();
      //자동판매 초기화
		PcInstance pc = (PcInstance)cha;
		
		pc.isAutoSellAdding = false;
		pc.isAutoSellDeleting = false;
		
        cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dunbook", null, msg));
    }

    @Override
    public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
        if (!isPlayerValidForTeleport(pc)) {
            return;
        }

        if (Lineage.open_wait) {
            ChattingController.toChatting(pc, "오픈 대기엔 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
            return;
        }
        try {
		DungeonBook dungeonBook = DungeontellbookDatabase.find(Integer.valueOf(action));
        if (dungeonBook != null) {
            if (shouldPreventEntry(pc, dungeonBook)) {
                return;
            }

            teleportToRandomLocation(pc, dungeonBook);
        }
        } catch (NumberFormatException e) {
            // Handle the case where the action is not a valid integer
            // Log an error or display a message to the user
        }
    }

    // Helper methods

    private List<String> createDungeonMessages() {
        List<String> msg = new ArrayList<>(100);
        
        msg.clear();
        for (DungeonBook db : DungeontellbookDatabase.getList()) {
        	if (db.getAden() != null && ItemDatabase.find(db.getAden()) != null && db.getCount() > 0) {
				msg.add(String.format("%s (%,d)", db.getName(),  db.getCount()));
			} else {
				msg.add(String.format("%s", db.getName()));
			}
        }
        for (int i = 0; i < 100; i++) {
			msg.add(" ");
		}
        return msg;
    }


    private boolean isPlayerValidForTeleport(PcInstance pc) {
        return pc != null && !pc.isWorldDelete() && !pc.isDead() && !pc.isLock() && pc.getInventory() != null && !pc.isFishing();
    }

    private boolean shouldPreventEntry(PcInstance pc, DungeonBook dungeonBook) {
        if (pc.getLevel() < dungeonBook.getLevel()) {
            ChattingController.toChatting(pc, String.format("해당 던전은 %d레벨 이상 입장 가능합니다.", dungeonBook.getLevel()), Lineage.CHATTING_MODE_MESSAGE);
            return true;
        }

        if (dungeonBook.isClan() && pc.getClanId() < 1) {
            ChattingController.toChatting(pc, "해당 던전은 혈맹 가입자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
            return true;
        }

        if (dungeonBook.isWanted() && !WantedController.checkWantedPc(pc)) {
            ChattingController.toChatting(pc, "해당 던전은 수배자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
            return true;
        }

        return false;
    }

    private void teleportToRandomLocation(PcInstance pc, DungeonBook dungeonBook) {
        List<FirstSpawn> locList = dungeonBook.getLoc_list();
        if (locList != null && !locList.isEmpty()) {
            FirstSpawn fs = locList.get(Util.random(0, locList.size() - 1));

            if (dungeonBook.getAden() != null && dungeonBook.getCount() > 0) {
                if (ItemDatabase.find(dungeonBook.getAden()) != null && pc.getInventory().isAden(dungeonBook.getAden(), dungeonBook.getCount(), true)) {
                    if (pc.isAutoHunt) {
                        pc.endAutoHunt(false, false);
                    }
                    pc.toPotal(fs.getX(), fs.getY(), fs.getMap());
                } else {
                    ChattingController.toChatting(pc, String.format("%s(%,d) 이(가) 부족합니다.", dungeonBook.getAden(), dungeonBook.getCount()), Lineage.CHATTING_MODE_MESSAGE);
                }
            } else {
                pc.toPotal(fs.getX(), fs.getY(), fs.getMap());
            }
        }
    }
}