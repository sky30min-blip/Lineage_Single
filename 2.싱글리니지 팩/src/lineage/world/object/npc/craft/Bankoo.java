package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.database.ItemDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SummonController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Bankoo extends CraftInstance {

	List<Item> list;

	public Bankoo(Npc npc) {
		super(npc);
		list = new ArrayList<Item>();
		list.add(ItemDatabase.find("녹색 해츨링 알"));
		list.add(ItemDatabase.find("황색 해츨링 알"));
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bankoo1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		Item ii = null;
		String pet_name = null;
		int pet_level = 0;
		int pet_hp = 0;
		int pet_mp = 0;
		String eggName = null;

		if (Lineage.server_version > 144) {
			switch (action) {
			case "buy71":
				pet_name = "해츨링(남)";
				pet_level = 6;
				pet_hp = 40 + Util.random(4, 7);
				pet_mp = 12 + Util.random(1, 2);
				eggName = "녹색 해츨링 알";
				break;
			case "buy81":
				pet_name = "해츨링(여)";
				pet_level = 6;
				pet_hp = 45 + Util.random(4, 7);
				pet_mp = 12 + Util.random(1, 2);
				eggName = "황색 해츨링 알";
				break;
			}
		}

		if (pet_name == null)
			return;

		if (pc.getInventory().find(eggName) == null) {
			ChattingController.toChatting(pc, eggName + "이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
			return;
		}
		switch (pet_name) {
		case "해츨링(남)":
			ii = list.get(0);
			break;
		case "해츨링(여)":
			ii = list.get(1);
			break;
		}

		ItemInstance iiInstance = pc.getInventory().find(ii);
		if (iiInstance != null && iiInstance.getCount() >= 1) {
			MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find(pet_name));
			mi.setLevel(pet_level);
			mi.setMaxHp(pet_hp);
			mi.setMaxMp(pet_mp);
			mi.setNowHp(pet_hp);
			mi.setNowMp(pet_mp);
			mi.setX(pc.getX());
			mi.setY(pc.getY());
			mi.setMap(pc.getMap());

			if (SummonController.toPet(pc, mi)) {
				pc.getInventory().count(iiInstance, iiInstance.getCount() - 1, true);
				MonsterSpawnlistDatabase.setPool(mi);
				// 창 닫기
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
			} else {
				ChattingController.toChatting(pc, "구매하실려는 펫이 너무 많습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}