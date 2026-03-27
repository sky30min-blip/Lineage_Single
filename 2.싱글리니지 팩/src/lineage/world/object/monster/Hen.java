package lineage.world.object.monster;

import java.util.List;

import lineage.bean.database.Monster;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Hen extends MonsterInstance {

	static private long eggs_item_time;

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Hen();
		return MonsterInstance.clone(mi, m);
	}

	public void toAiAttack(long time) {
		// 도망모드로 전환.
		setAiStatus(Lineage.AI_STATUS_ESCAPE);
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		setDead(true);
		setAiStatus(Lineage.AI_STATUS_DEAD);

		// 라우풀 깍기
		cha.setLawful(cha.getLawful() - 10);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "hen1"));
	}

	@Override
	public void toAi(long time) {
		if (eggs_item_time <= time) {
			eggs_item_time = time + Lineage.eggs_spawn_time;

			int x = 0;
			int y = 0;

			ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("달걀"));
			if (ii.getObjectId() == 0)
				ii.setObjectId(ServerDatabase.nextItemObjId());
			ii.setCount(Util.random(Lineage.eggs_min_count, Lineage.eggs_max_count));
			if (ii.getCount() <= 0)
				ii.setCount(1);

			x = Util.random(getX() - 1, getX() + 1);
			y = Util.random(getY() - 1, getY() + 1);

			if (World.isThroughObject(x, y + 1, map, 0))
				ii.toTeleport(x, y, map, false);

			ii.toDrop(this);
		}
		super.toAi(time);
	}

}