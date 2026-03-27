package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Clan;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectTitle;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.instance.PcInstance;

public final class GiranClanLordController {
	
	static public void init(){
		TimeLine.start("GiranClanLordController..");	
		TimeLine.end();
	}
	
	// 버그 방지를 위해 가입요청자 담을 변수
	private static List<PcInstance> tempList = new ArrayList<PcInstance>();
	
	static public void toAsk(PcInstance pc){
		setTempList(pc);
		pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 772, "신규"));
	}
	
	public static void toAsk(PcInstance pc, boolean yes) {
		if (yes && isTempCheck(pc)) {
			Clan c = ClanController.find(Lineage.new_clan_name);
			pc.setClanId(c.getUid());
			pc.setClanName(c.getName());
			// 신규혈맹 가입시 호칭
			pc.setTitle("신규");
			pc.setClanGrade(0);
			// 패킷 처리
			pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), pc), true);
			//94 \f1%0%o 혈맹의 일원으로 받아들였습니다.
			c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 94, pc.getName()));
			//95 \f1%0 혈맹에 가입하였습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 95, c.getName()));
			// 혈맹 관리목록 갱신
			c.appendMemberList(pc.getName());
			if (!pc.isWorldDelete())
				c.appendList(pc);
			removeTempList(pc);
		}
	}

	public static void setTempList(PcInstance pc) {
		if (!tempList.contains(pc))
			tempList.add(pc);
	}

	public static boolean isTempCheck(PcInstance pc) {
		if (tempList.contains(pc))
			return true;
		return false;
	}

	public static void removeTempList(PcInstance pc) {
		if (tempList.contains(pc))
			tempList.remove(pc);
	}
}
