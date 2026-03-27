package lineage.world.object.npc.kingdom;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Kingdom;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.AgitController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.KingdomController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.guard.SentryGuard;

public class DwarfcastleGuard extends SentryGuard {

	private Npc npc;
	protected Kingdom kingdom;
	protected String html;
	private List<String> list_html;
	private List<object> list_temp;
	
	public DwarfcastleGuard(Npc npc, Kingdom kingdom) {
		super(npc);
		this.kingdom = kingdom;
		list_html = new ArrayList<String>();
		list_temp = new ArrayList<object>();
		html = null;
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		
		list_html.clear();
		list_html.add(kingdom.getClanName());
		list_html.add(kingdom.getAgentName());
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Dwarfcastle", null, list_html));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (kingdom == null)
			return;

		if (action.equalsIgnoreCase("askwartime1")) {
			if (pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
				if (AgitController.find(pc) != null || KingdomController.find(pc) != null) {
					ChattingController.toChatting(pc, "당신은 이미 성을 소유하고 있으므로 다른 성에 도전할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
					// 창닫기
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
				} else {
					//제거
					//자동선포로 변경
					ChattingController.toChatting(pc, "공성전은 자동선포 처리됩니다.", Lineage.CHATTING_MODE_MESSAGE);
					// 창닫기
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
				}
			} else {
				ChattingController.toChatting(pc, "오직 왕자와 공주만이 공성전을 선포할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
				// 창닫기
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
			}
		
			// 다음공성전 시간 호출
		} else if (action.equalsIgnoreCase("askwartime")) {
			if (kingdom.getWarDay() > 0) {
				list_html.clear();
				list_html.add(String.valueOf(Util.getYear(kingdom.getWarDay()) + 1900));
				list_html.add(String.valueOf(Util.getMonth(kingdom.getWarDay())));
				list_html.add(String.valueOf(Util.getDate(kingdom.getWarDay())));
				list_html.add(String.valueOf(Util.getHours(kingdom.getWarDay())));
				list_html.add(String.valueOf(Util.getMinutes(kingdom.getWarDay())));
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Dwarfcastle1", null, list_html));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Dwarfcastle2"));
			}
		}
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		// 성처리 경비병일경우 같은 소속혈맹이 아닐경우에만 처리하기.
		if (kingdom == null || kingdom.getClanId() == 0 || kingdom.getClanId() != cha.getClanId())
			super.toDamage(cha, dmg, type);
	}

	
	@Override
	protected void toSearchPKer() {
		if (kingdom == null || kingdom.getClanId() == 0) {
			super.toSearchPKer();
			return;
		}

		// 성처리 경비병일경우 같은 소속혈맹이 아닐경우에만 처리하기.
		for (object o : getInsideList()) {
			if (o instanceof PcInstance) {
				PcInstance pc = (PcInstance) o;
				if (kingdom.getClanId() != pc.getClanId()) {
					// 피커 등록.
					if (pc.getPkTime() > 0)
						addAttackList(pc);
					// 공성전 중일경우.
					if (kingdom.isWar()) {
						// 전쟁 선포한 혈맹원들 다 타켓으로 잡기.
						for (String name : kingdom.getListWar()) {
							if (name.equalsIgnoreCase(pc.getClanName()))
								addAttackList(pc);
						}
					}
				}
			}
		}
	}

	@Override
	public void toAiAttack(long time) {
		if (kingdom.getClanId() != 0) {
			list_temp.clear();
			list_temp.addAll(attackList);
			// 전투목록 둘러보면서 같은 혈맹 소속자가 있을경우 제거.
			for (object o : list_temp) {
				if (o.getClanId() == kingdom.getClanId())
					attackList.remove(o);
			}
		}
		super.toAiAttack(time);
	}

}
