package lineage.world.object.item.bundle;

import java.util.Calendar;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Bundle;

public class Astrologist extends Bundle {

    static synchronized public ItemInstance clone(ItemInstance item) {
        if (item == null)
            item = new Astrologist();
        return item;
    }

    @Override
    public void toClick(Character cha, ClientBasePacket cbp) {
        // 현재 시간 얻기
        Calendar currentTime = Calendar.getInstance();
        int currentDayOfYear = currentTime.get(Calendar.DAY_OF_YEAR);
        
        // 이전 사용 시간 얻기
        long lastUseTime = ((PcInstance) cha).getSupplyTime();
        Calendar lastUseCalendar = Calendar.getInstance();
        lastUseCalendar.setTimeInMillis(lastUseTime);
        int lastUseDayOfYear = lastUseCalendar.get(Calendar.DAY_OF_YEAR);
        
        // 하루에 한 번만 사용 가능하도록 수정
        if (currentDayOfYear == lastUseDayOfYear) {
            ChattingController.toChatting(cha, "이 아이템은 하루에 한 번만 사용할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
            return;
        }

        int[][] db = {
                // 구슬 (nameid, min, max, chance)
                {5205, 1, 1, 1},
        };
        toBundle(cha, db, TYPE.LOOP_1);
        
        // 사용 시간 갱신
        ((PcInstance) cha).setSupplyTime(System.currentTimeMillis());
    }
}