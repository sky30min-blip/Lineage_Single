package lineage.network.packet.server;

import java.util.List;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.world.object.instance.PetMasterInstance;
import lineage.world.object.item.DogCollar;

public class S_WareHousePet extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, PetMasterInstance pmi, List<DogCollar> list){
		if(bp == null)
			bp = new S_WareHousePet(pmi, list);
		else
			((S_WareHousePet)bp).toClone(pmi, list);
		return bp;
	}
	
	public S_WareHousePet(PetMasterInstance pmi, List<DogCollar> list){
		toClone(pmi, list);
	}
	
	public void toClone(PetMasterInstance pmi, List<DogCollar> list){
		clear();
		

		writeC(Opcodes.S_OPCODE_WAREHOUSE);
		writeD(pmi.getObjectId());	// NPC 오브젝트
		writeH(list.size());
		writeC(12);	// 타입부분
		// Type 02 = PUT		03 = GET	창고
		// Type 00 = BUY		01 = SELL	상점
		// Type 12 = 펫찾기
		for(DogCollar dc : list){
			writeD(dc.getObjectId());	// 번호
			if(dc.getBless()==0)
				writeC(dc.getItem().getAction2());
			else
				writeC(dc.getItem().getAction1());
			writeH(dc.getItem().getInvGfx());
			writeC(dc.getBressPacket());
			writeD((int)dc.getCount());
			writeC(dc.isDefinite() ? 1 : 0);
			writeS(getName(dc));
		}
		writeD(Lineage.warehouse_pet_price);	// 가격
	}
}
