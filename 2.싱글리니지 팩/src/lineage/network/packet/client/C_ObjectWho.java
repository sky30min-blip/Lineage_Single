package lineage.network.packet.client;

import java.text.DecimalFormat;

import lineage.database.CharactersDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.FishingController;
import lineage.world.controller.RobotController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;

public class C_ObjectWho extends ClientBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length) {
		if (bp == null)
			bp = new C_ObjectWho(data, length);
		else
			((C_ObjectWho) bp).clone(data, length);
		return bp;
	}

	public C_ObjectWho(byte[] data, int length) {
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc) {
		// 버그 방지.
		if (pc == null || pc.isWorldDelete())
			return this;

		// 접속된 전체유저 출력.
		if (pc.getGm() > 0) {
			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "+------------------------------"));
			for (PcInstance use : World.getPcList())
				pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("| Lv.%2d %s %s", use.getLevel(), use.getName(), Util.getMapName(use))));
			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "+------------------------------"));
		}
		// 사용자 이름 찾기.
		String name = readS();
		PcInstance use = World.findPc(name);

		if (use != null) {
			String msg = Lineage.object_who;

			// 이름
			msg = msg.replace("name", use.getName());
			// 호칭
			msg = msg.replace("title", use.getTitle() == null ? "" : use.getTitle());
			// 혈맹
			msg = msg.replace("clan", use.getClanId() == 0 ? "" : String.format("[%s]", use.getClanName()));
			// 피케이
			msg = msg.replace("pkcount", String.valueOf(use.getPkCount()));
			// 라우풀
			if (use.getLawful() < Lineage.NEUTRAL)
				msg = msg.replace("lawful", "(Chaotic)");
			else if (use.getLawful() >= Lineage.NEUTRAL && use.getLawful() < Lineage.NEUTRAL + 500)
				msg = msg.replace("lawful", "(Neutral)");
			else
				msg = msg.replace("lawful", "(Lawful)");
			// pvp
			double kill = CharactersDatabase.getPvpKill(use);
			double dead = CharactersDatabase.getPvpDead(use);
			double total = dead + kill;
			double rate = kill == 0 || total == 0 ? 0 : (kill / total) * 100;
			msg = msg.replace("pvpKill", String.valueOf((int) kill + "킬"));
			msg = msg.replace("pvpDead", String.valueOf((int) dead + "데스"));
			msg = msg.replace("pvpRaet", new DecimalFormat("0.#%").format(rate));
			msg = msg.replace("where", use.getMap() == 99 ? "[아덴필드]" : Util.getMapName(use));

			String classType = "";
			switch (use.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				classType = "[군주]";
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				classType = "[기사]";
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				classType = "[요정]";
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				classType = "[마법사]";
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				classType = "[다크엘프]";
				break;
			}
			msg = msg.replace("class", classType);

			if (pc.getGm() > 0) {
				if (!(use instanceof PcRobotInstance)) {
					msg += String.format(" [%s]", use.getClient().getAccountIp());
				} else if (use instanceof PcRobotInstance) {
					PcRobotInstance ri = (PcRobotInstance) use;
					msg += String.format(" [%s]", ri.action);
				}
			}

			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
		}
		// 접속자수 표현.
		int count = Lineage.world_player_count_init + (int) Math.round(World.getPcSize() * Lineage.world_player_count) + FishingController.getFishRobotListSize() + RobotController.getPcRobotListSize();
		pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 81, String.valueOf(count)));

		return this;
	}
}
