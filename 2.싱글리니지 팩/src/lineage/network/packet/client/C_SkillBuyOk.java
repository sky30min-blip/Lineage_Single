package lineage.network.packet.client;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.controller.SkillController;
import lineage.world.object.instance.PcInstance;

public class C_SkillBuyOk extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_SkillBuyOk(data, length);
		else
			((C_SkillBuyOk)bp).clone(data, length);
		return bp;
	}
	
	public C_SkillBuyOk(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		int count = readH();

		if(count>0 && count<=24){
			// 초기화
			final List<Skill> list = new ArrayList<Skill>();
			int price_total = 0;
			// 검색
			for(int i=count-1 ; i>=0 ; --i){
				Skill s = SkillDatabase.find( readD()+1 );
				if(s != null && s.getUid() <= 23 && s.getUid() != 16 && s.getUid() != 23){
					list.add(s);
					price_total += s.getPrice();
				}
			}
			// 처리
			if(list.size()>0){
				if(pc.getInventory().isAden(price_total, true)) {
					List<Skill> pc_list = SkillController.find(pc);
					for( Skill s : list )
						pc_list.add(s);
					SkillController.sendList(pc);
				} else {
					// \f1아데나가 충분치 않습니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
				}
			}
		}
		
		return this;
	}

}
