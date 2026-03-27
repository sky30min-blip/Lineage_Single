package lineage.network.packet.server;

import java.util.List;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_HyperText extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, String request, String action, int price, int def, int min, long max, List<String> list){
		if(bp == null)
			bp = new S_HyperText(o, request, action, price, def, min, max, list);
		else
			((S_HyperText)bp).clone(o, request, action, price, def, min, max, list);
		return bp;
	}

	public S_HyperText(object o, String request, String action, int price, int def, int min, long max, List<String> list){
		clone(o, request, action, price, def, min, max, list);
	}
	
	public void clone(object o, String request, String action, int price, int def, int min, long max, List<String> list){
		clear();

		writeC(Opcodes.S_OPCODE_HYPERTEXTINPUT);
		writeD(o.getObjectId());	// npc오브젝트 아이디
		writeD(price);				// 금액
		writeD(def);				// 기본 셋팅 갯수
		writeD(min);				// 총 만들수 있는 최소 갯수
		writeD((int)max);			// 총 만들수 있는 최대 갯수
		writeH(0);					// ?
		writeS(request);			// 종류
		writeS(action);				// 액션 메세지
		if(list != null){
			writeH(list.size());
			for(String msg : list)
				writeS(msg);
		}else{
			writeH(0);
		}
	}
	
}
