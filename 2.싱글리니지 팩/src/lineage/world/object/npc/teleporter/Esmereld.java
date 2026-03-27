package lineage.world.object.npc.teleporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CharacterController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Esmereld extends TeleportInstance {
	
	// 이동될 좌표들 - 본섭에서 다 확인한 좌표.
	static private int[][] location = {
			{33597, 33239, 4},
			{33448, 32753, 4},
			{33743, 32277, 4},
			{32643, 32954, 0},
			{34249, 33452, 4},
			{32879, 32652, 4},
			{33127, 32940, 4},
			{32732, 32814, 62},
			{32742, 32679, 63},
			{32771, 32797, 77},
			{33937, 33345, 4},
			{33051, 32340, 4},
			{32870, 33255, 4},
			{33972, 33363, 4},
			{33429, 32813, 4}
	};
	// 관리 객체
	private List<PcInstance> list;
	private Map<PcInstance, Long> list_time;
	private List<PcInstance> list_temp;
	
	public Esmereld(Npc npc){
		super(npc);
		
		list_temp = new ArrayList<PcInstance>();
		list = new ArrayList<PcInstance>();
		list_time = new HashMap<PcInstance, Long>();
		
		CharacterController.toWorldJoin(this);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "esmereld"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("journey")){
			// 설정
			pc.setNpcEsmereld(this);
			pc.setTransparent(true);
			// 관리목록에 등록.
			list.add(pc);
			list_time.put(pc, System.currentTimeMillis()+(1000 * 1));
			// 텔레포트
			int[] loc = location[Util.random(0, location.length)];
			pc.toTeleport(loc[0], loc[1], loc[2], false);
		}
	}

	@Override
	public void toTimer(long time){
		list_temp.clear();
		// 검색
		for(PcInstance pc : list){
			Long pc_time = list_time.get(pc);
			if(pc_time <= time)
				list_temp.add(pc);
		}
		// 제거
		for(PcInstance pc : list_temp)
			toTeleport(pc);
	}
	
	/**
	 * 해당 사용자 에스메랄다 앞으로 텔레포트 시키기.
	 * @param pc
	 */
	public void toTeleport(PcInstance pc){
		try {
			// 설정
			pc.setNpcEsmereld(null);
			pc.setTransparent(false);
			// 관리목록에서 제거
			list.remove(pc);
			list_time.remove(pc);
			// 텔레포트
			pc.toTeleport(getX()-2, getY()+2, getMap(), false);
		} catch (Exception e) {
			lineage.share.System.printf("%s : toTeleport(PcInstance pc)\r\n", Esmereld.class.toString());
			lineage.share.System.println(e);
		}
	}
	
}
