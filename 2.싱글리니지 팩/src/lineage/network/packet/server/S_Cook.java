package lineage.network.packet.server;

import lineage.bean.database.Item;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.item.CookBook;

public class S_Cook extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, CookBook cb){
		if(bp == null)
			bp = new S_Cook(o, cb);
		else
			((S_Cook)bp).toClone(o, cb);
		return bp;
	}

	static synchronized public BasePacket clone(BasePacket bp, Character cha, Item item, int time){
		if(bp == null)
			bp = new S_Cook(cha, item, time);
		else
			((S_Cook)bp).toClone(cha, item, time);
		return bp;
	}
	
	public S_Cook(object o, CookBook cb){
		toClone(o, cb);
	}
	
	public S_Cook(Character cha, Item item, int time){
		toClone(cha, item, time);
	}
	
	public void toClone(object o, CookBook cb) {
		clear();
		
		int lv = cb.getItem().getNameIdNumber() - 4922;
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x34);
		writeD(o.getObjectId());
		writeC(lv>0 ? 1 : 0);	// 1단계
		writeC(lv>1 ? 1 : 0);	// 2단계
	}
	
	public void toClone(Character cha, Item item, int time) {
		clear();

		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x35);
		
		writeC(cha.getTotalStr());
		writeC(cha.getTotalInt());
		writeC(cha.getTotalWis());
		writeC(cha.getTotalDex());
		writeC(cha.getTotalCon());
		writeC(cha.getTotalCha());
		
		writeH(2000);					// food
		writeC(item.getSmallDmg());		// 요리 구분자  1단계0~15 , 2단계16~32
		writeC(0x24);					// ?
		writeH(time);					// 시간
		writeH(cha.getInventory()==null ? 0 : (int)cha.getInventory().getWeightPercent());
	}

}
