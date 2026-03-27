package lineage.world.object.monster;

import all_night.Lineage_Balance;
import lineage.bean.database.Monster;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BossController;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Faust_Ghost extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Faust_Ghost();

		return MonsterInstance.clone(mi, m);
	}


	@Override
	public void setNowHp(int nowHp) {

	   super.setNowHp(nowHp);

		}
	}
