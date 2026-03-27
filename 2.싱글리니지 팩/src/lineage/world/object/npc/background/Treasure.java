package lineage.world.object.npc.background;

import lineage.bean.database.Item;
import lineage.bean.database.Skill;
import lineage.database.ItemDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.network.packet.server.S_ObjectAttackMagic;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CharacterController;
import lineage.world.controller.CraftController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.magic.EnergyBolt;

public class Treasure extends BackgroundInstance {

	private Skill skill;

	private int SLEEP_TIME = 60 * 10; // 10분
	private int current_time = 0;

	public Treasure() {
		CharacterController.toWorldJoin(this);
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
	    // 닫혀있을때만 처리.
	    if (gfxMode == 29) {
	        // 상자 열기.
	        toOn();
	        toSend();
	        // 아이템 지급
	        int count = 0;
	        Item item = null;
	        int rand = Util.random(0, 200);
	        if (rand == 0) {
	            // 젤
	            count = Util.random(1, 3);
	            item = ItemDatabase.find(249);
	        } else if (rand == 100) {
	            // 데이
	            count = Util.random(1, 3);
	            item = ItemDatabase.find(244);
	        } else if (rand > 100) {
	            // 체력회복제
	            count = Util.random(10, 20);
	            item = ItemDatabase.find(26);
	            if (item == null)
	                item = ItemDatabase.find(237);
	        } else {
	            int dmg = Util.random(50, 100);
	            if (dmg > 0) {
	                // 고정 이럽션 데미지
	                cha.setNowHp(cha.getNowHp() - 100);
	                toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), this, cha, Lineage.GFX_MODE_TYPE, dmg, SkillDatabase.find(6, 4).getCastGfx(), false, false, cha.getX(), cha.getY()), false);
	            }
	        }
	        if (item != null) {
	            CraftController.toCraft(cha, item, count, true);
	        }
	    }
	}

	@Override
	public void toTimer(long time) {
		if (current_time++ >= SLEEP_TIME) {
			current_time = 0;
			// 상자 닫기.
			toOff();
			toSend();
		}
	}

	public void toOn() {
		setGfxMode(28);
	}

	public void toOff() {
		setGfxMode(29);
	}

	public void toSend() {
		toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
	}

}
