package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Party {
	private long key;
	private PcInstance temp;
	private PcInstance master;
	private List<PcInstance> list;
	private boolean clanParty;

	public Party() {
		list = new ArrayList<PcInstance>();
		master = temp = null;
		key = 0;
		clanParty = false;
	}

	public void close() {
		for (PcInstance pc : getList()) {
			pc.setPartyId(0);
			// 파티를 해산했습니다.
			if (Lineage.server_version > 144)
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 418));
			else
				ChattingController.toChatting(pc, "파티를 해산했습니다.", Lineage.CHATTING_MODE_MESSAGE);
			toUpdate(pc, false);
		}
		synchronized (list) {
			list.clear();
		}
		master = temp = null;
		key = 0;
		clanParty = false;
	}

	public boolean isClanParty() {
		return clanParty;
	}

	public void setClanParty(boolean clanParty) {
		this.clanParty = clanParty;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public PcInstance getMaster() {
		return master;
	}

	public void setMaster(PcInstance pc) {
		master = pc;
	}

	public PcInstance getTemp() {
		return temp;
	}

	public void setTemp(PcInstance temp) {
		this.temp = temp;
	}

	public void append(PcInstance pc) {
		synchronized (list) {
			if (!list.contains(pc))
				list.add(pc);
		}
	}

	public void remove(PcInstance pc) {
		if (temp != null && temp.getObjectId() == pc.getObjectId())
			temp = null;

		pc.setPartyId(0);
		// %0%s 파티를 떠났습니다
		toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 420, pc.getName()));

		toUpdate(pc, false);
		synchronized (list) {
			list.remove(pc);
		}
	}

	public int getSize() {
		return list.size();
	}

	/**
	 * 파티원들에게 패킷전송 처리 함수. : pc객체와 근접한 파티원에게만 전송.
	 * 
	 * @param packet
	 * @param pc
	 * @param me
	 */
	public void toSender(BasePacket packet, PcInstance pc, boolean me) {
		if (packet instanceof ServerBasePacket) {
			ServerBasePacket sbp = (ServerBasePacket) packet;
			for (PcInstance use : getList()) {
				if (Util.isDistance(pc, use, Lineage.SEARCH_LOCATIONRANGE)) {
					if (me) {
						use.toSender(ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()));
					} else {
						if (pc.getObjectId() != use.getObjectId())
							use.toSender(ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()));
					}
				}
			}
		}
		BasePacketPooling.setPool(packet);
	}

	public void toSender(BasePacket packet) {
		if (packet instanceof ServerBasePacket) {
			ServerBasePacket sbp = (ServerBasePacket) packet;
			for (PcInstance pc : getList())
				pc.toSender(ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()));
		}
		BasePacketPooling.setPool(packet);
	}

	public void toMessage(String msg) {
		for (PcInstance pc : getList())
			ChattingController.toChatting(pc, msg, Lineage.CHATTING_MODE_MESSAGE);
	}

	public void toUpdate(PcInstance pc) {
		if (Lineage.server_version <= 144)
			return;

		for (PcInstance use : getList()) {
			if (pc.getObjectId() != use.getObjectId() && Util.isDistance(pc, use, Lineage.SEARCH_LOCATIONRANGE))
				use.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), pc, true));
		}
	}

	public void toUpdate(PcInstance pc, boolean visual) {
		if (Lineage.server_version <= 144)
			return;

		// 주변객체 처리.
		for (PcInstance use : getList()) {
			if (pc.getObjectId() != use.getObjectId() && Util.isDistance(pc, use, Lineage.SEARCH_LOCATIONRANGE)) {
				pc.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), use, visual));
				use.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), pc, visual));
			}
		}
	}

	public void toExp(PcInstance pc, Character target, double exp) {
		// 전체 파티구성원을 루프돌면서 거리에 있는녀석들만 추려낸다
		// 추려낸 녀석들에게만 받게될 경험치를 추려낸갯수만큼 나누고 그값을 더한다
		// 파티보너스 경험치도 지급한다.

		List<PcInstance> temp_list = new ArrayList<PcInstance>();
		// 추려내기
		for (PcInstance use : getList()) {
			if (Util.isDistance(pc, use, Lineage.SEARCH_LOCATIONRANGE))
				temp_list.add(use);
		}
		// 갯수만큼 경험치 하향
		exp /= temp_list.size();
		// 1명 이상일때만.
		if (temp_list.size() > 1)
			// 파티 보너스 경험치
			exp *= Lineage.rate_party;
		// 라우풀 지급 값 추출.
		double lawful = Math.round(((target.getLevel() * 3) / 2) * Lineage.rate_lawful);

		if (target.getLawful() < 0) {
			lawful = ~(int) lawful + 1;
		}

		if (target instanceof MonsterInstance) {
			// if(((MonsterInstance) target).getMonster().getLawful() < 0) {
			// lawful = ~(int)lawful + 1;
			// }

			if (((MonsterInstance) target).getMonster().getLawful() - Lineage.NEUTRAL > 0) {
				int tempLawful = (((MonsterInstance) target).getMonster().getLawful() - Lineage.NEUTRAL) * -1;
				lawful = Util.random(tempLawful * 0.8, tempLawful);
			}
		}

		// 갯수만큼 라우풀 지급 하향.
		lawful /= temp_list.size();

		if (exp > 0) {
			// 지급
			for (PcInstance use : temp_list) {
				if (!use.isDead()) {
					if (Lineage.party_exp_level_range == 0 || Util.isRange(pc.getLevel(), use.getLevel(), Lineage.party_exp_level_range)) {
						// 경험치 지급.
						use.toExp(target, exp);
						// 라우풀 지급.
						use.setLawful(use.getLawful() + (int) lawful);
					}
				}
			}
		}

		temp_list.clear();
	}

	public List<PcInstance> getList() {
		synchronized (list) {
			return list;
		}
	}

	public List<PcInstance> getListTemp() {
		return new ArrayList<PcInstance>(list);
	}

	public boolean isParty(PcInstance pc, Party p) {
		if (p.getListTemp().contains(pc))
			return true;

		return false;
	}
}
