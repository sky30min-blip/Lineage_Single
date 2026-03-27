package lineage.world.object.instance;

import lineage.bean.database.Monster;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_HtmlSummon;
import lineage.world.controller.CharacterController;

public class SoldierInstance extends SummonInstance {

	static synchronized public SoldierInstance clone(SoldierInstance si, Monster m, int time){
		if(si == null)
			si = new SoldierInstance();
		si.setMonster(m);
		// 휴식모드로 전환
		si.setSummonMode(SUMMON_MODE.Rest);
		// 자연회복을 위해 등록.
		CharacterController.toWorldJoin(si);
		// 소환된 시간과 종료될 시간 처리하기.
		si.setSummonTimeStart(System.currentTimeMillis());
		si.setSummonTimeEnd(si.getSummonTimeStart() + (1000*time));
		// 기본 정보 초기화.
		si.setElemental(false);
		return si;
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(summon != null) {
			// 마스터는 펫 다루는 창으로.
			if(summon.getMasterObjectId() == pc.getObjectId())
				pc.toSender(S_HtmlSummon.clone(BasePacketPooling.getPool(S_HtmlSummon.class), this));
			// 그외에는 경고 창으로
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "patguard"));
		}
	}
	
}
