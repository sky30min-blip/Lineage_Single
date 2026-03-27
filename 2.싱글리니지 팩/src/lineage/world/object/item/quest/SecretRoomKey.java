package lineage.world.object.item.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.npc.shop.Orim;

public class SecretRoomKey extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SecretRoomKey();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 전방에 오림이 있는지 확인.
		if(searchOrim(cha)){
			cha.toTeleport(32815, 32810, 13, false);
		}else{
			// \f1아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
		}
	}

	/**
	 * 맞은편에 오림이 존재하는지 확인.
	 */
	private boolean searchOrim(Character cha){
		int locx = cha.getX();
		int locy = cha.getY();
		switch(cha.getHeading()){
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
		List<object> list = new ArrayList<object>();
		cha.findInsideList(locx, locy, list);
		for(object o : list){
			if(o instanceof Orim){
				// 3방향일때 +4하면 7방향이됨. 결과적으로 서로 맞우보게 됨.
				int h = o.getHeading() + 4;
				// 방향 7 이상일경우 -8을해서 0부터 시작되도록 함.
				if(h>7)
					h -= 8;
				// 서로 마주보고있는지 확인.
				return h == cha.getHeading();
			}
		}
		return false;
	}

}
