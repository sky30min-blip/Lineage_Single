package lineage.network.packet.server;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.controller.SkillController;
import lineage.world.object.instance.PcInstance;

public class S_SkillBuyList extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, PcInstance pc){
		if(bp == null)
			bp = new S_SkillBuyList(pc);
		else
			((S_SkillBuyList)bp).toClone(pc);
		return bp;
	}
	
	public S_SkillBuyList(PcInstance pc){
		toClone(pc);
	}
	
	public void toClone(PcInstance pc){
		clear();
		int count = SkillController.getBuySkillCount(pc);
		writeC(Opcodes.S_OPCODE_SKILLBUY);
		writeD(100);
		writeH(count < 0 ? 0 : count);
		if(count>0){
			int idx = SkillController.getBuySkillIdx(pc);
			for(int i=0 ; i<=idx ; ++i){
				Skill s = SkillController.find(pc, i+1, false);
				// 쇼크 스턴 및 버서커스는 마법 구매 목록에서 제외.
				if(s == null) {
					if (i != 15 && i != 22) 
					writeD(i);
				}
			}
		}else{
			writeH(1);
			writeD(120);
		}
	}
	
}
