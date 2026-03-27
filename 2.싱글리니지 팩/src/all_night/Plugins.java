package all_night;

import java.util.StringTokenizer;

import lineage.network.packet.client.C_ItemClick;
import lineage.plugin.Plugin;
import lineage.share.Lineage;
import lineage.world.controller.AutoHuntCheckController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CommandController;
import lineage.world.controller.FightController;
import lineage.world.controller.PcMarketController;
import lineage.world.controller.PcTradeController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Plugins implements Plugin {

	public Object init(Class<?> c, Object... opt) {

		if (c.isAssignableFrom(CommandController.class)) {
			if (opt[0].equals("toCommand")) {
				object o = (object) opt[1];
				String cmd = (String) opt[2];
				StringTokenizer st = (StringTokenizer) opt[3];

				if (FightController.isCommand(cmd))
					return FightController.toCommand(o, cmd, st);
				else
					return PcMarketController.toCommand(o, cmd, st);
			}
		}

		if (c.isAssignableFrom(ChattingController.class)) {
			if (opt[0].equals("toAutoHuntAnswer")) {
				PcInstance pc = (PcInstance) opt[1];
				String answer = (String) opt[2];

				if (Lineage.auto_hunt_monster_kill_count <= pc.getAutoHuntMonsterCount())
					return AutoHuntCheckController.checkMessage(pc, answer);
			} else if (opt[0].equals("swap")) {
				PcInstance pc = (PcInstance) opt[1];
				String key = (String) opt[2];

				if (pc.insertSwap(key))
					return true;
			}
		}

		if (c.isAssignableFrom(C_ItemClick.class)) {
			// 현금 거래 게시판.
			if (opt[0].equals("pcTrade")) {
				C_ItemClick cid = (C_ItemClick) opt[1];
				PcInstance pc = (PcInstance) opt[2];
				ItemInstance item = (ItemInstance) opt[3];

				if (PcTradeController.insertItemFinal(pc, item, item.getCount()))
					return true;
			} else if (opt[0].equals("pcShop")) {
				// 무인 상점.
				C_ItemClick cid = (C_ItemClick) opt[1];
				PcInstance pc = (PcInstance) opt[2];
				ItemInstance item = (ItemInstance) opt[3];

				if (PcMarketController.isShopToAppend(pc, item, item.getCount()))
					return true;
			}
		}

		return null;
	}

}
