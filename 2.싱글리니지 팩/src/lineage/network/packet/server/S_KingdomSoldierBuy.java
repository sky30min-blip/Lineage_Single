package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_KingdomSoldierBuy extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_KingdomSoldierBuy(o);
		else
			((S_KingdomSoldierBuy)bp).toClone(o);
		return bp;
	}
	
	public S_KingdomSoldierBuy(object o){
		toClone(o);
	}
	
	public void toClone(object o){
		clear();
		writeC(Opcodes.S_OPCODE_KingdomSoldierBuy);
		writeD(o.getObjectId());
		writeD(1);		// getPublicMoney
		writeH(0);	// 성 고유값
		// -- 반복
		writeH(0);	// 위치값
		writeS("가나다라마바사");	// 용병 이름.
		writeH(10000);	// ?
	}

}
