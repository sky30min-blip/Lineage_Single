package lineage.network.packet.client;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.controller.PartyController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class C_ObjectParty extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectParty(data, length);
		else
			((C_ObjectParty)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectParty(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		PcInstance use = getPlayer(pc);
		if(use!=null && (pc.getGm()>0 || !pc.isTransparent()))
			PartyController.toParty(pc, use);
		
		return this;
	}

	/**
	 * 맞은편에 있는 사용자 찾아서 리턴.
	 */
	private PcInstance getPlayer(PcInstance pc){
		int locx = pc.getX();
		int locy = pc.getY();
		switch(pc.getHeading()){
			case 0:
				locy--;
				break;
			case 1:
				locx++;
				locy--;
				break;
			case 2:
				locx++;
				break;
			case 3:
				locx++;
				locy++;
				break;
			case 4:
				locy++;
				break;
			case 5:
				locx--;
				locy++;
				break;
			case 6:
				locx--;
				break;
			default:
				locx--;
				locy--;
				break;
		}
		final List<object> list = new ArrayList<object>();
		pc.findInsideList(locx, locy, list);
		for(object o : list){
			if(o instanceof PcInstance){
				// 3방향일때 +4하면 7방향이됨. 결과적으로 서로 맞우보게 됨.
				int h = o.getHeading() + 4;
				// 방향 7 이상일경우 -8을해서 0부터 시작되도록 함.
				if(h>7)
					h -= 8;
				// 서로 마주보고있다면 리턴하기.
				if(h == pc.getHeading()){
					return (PcInstance)o;
				}else{
					//91 \f1%0%s 당신을 보고 있지 않습니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 91, o.getName()));
					return null;
				}
			}
		}
		// 초대할 상대방이 없습니다.
		pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 421));
		return null;
	}
	
}
