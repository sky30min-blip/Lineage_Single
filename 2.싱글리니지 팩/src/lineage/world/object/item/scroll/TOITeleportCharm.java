package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class TOITeleportCharm extends ItemInstance {

	private int f;
	
	static synchronized public ItemInstance clone(ItemInstance item, int f){
		if(item == null)
			item = new TOITeleportCharm();
		((TOITeleportCharm)item).f = f;
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		switch(f){
			case 11:	// 3시
			case 51:
				if(((cha.getX()>=32780)&&(cha.getX()<=32783))&&((cha.getY()>=32816)&&(cha.getY()<=32819))&&(cha.getMap()==101)){
					if(f == 11)
						cha.toTeleport(32631, 32935, 111, true);
					else
						cha.toTeleport(32631, 32935, 151, true);
				}
				break;
			case 21:	// 6시
			case 61:
				if(((cha.getX()>=32817)&&(cha.getX()<=32820))&&((cha.getY()>=32779)&&(cha.getY()<=32782))&&(cha.getMap()==101)){
					if(f == 21)
						cha.toTeleport(32631, 32935, 121, true);
					else
						cha.toTeleport(32668, 32814, 161, true);
				}
				break;
			case 31:	// 9시
			case 71:
				if(((cha.getX()>=32780)&&(cha.getX()<=32783))&&((cha.getY()>=32779)&&(cha.getY()<=32782))&&(cha.getMap()==101)){
					if(f == 21)
						cha.toTeleport(32631, 32935, 131, true);
					else
						cha.toTeleport(32631, 32935, 171, true);
				}
				break;
			case 41:	// 12시
			case 81:
				if(((cha.getX()>=32816)&&(cha.getX()<=32819))&&((cha.getY()>=32816)&&(cha.getY()<=32819))&&(cha.getMap()==101)){
					if(f == 51)
						cha.toTeleport(32631, 32935, 141, true);
					else
						cha.toTeleport(32631, 32935, 181, true);
				}
				break;
			default:	// 
				if(((cha.getX()>=32706)&&(cha.getX()<=32710))&&((cha.getY()>=32909)&&(cha.getY()<=32913))&&(cha.getMap()==190))
					cha.toTeleport(32631, 32935, 191, true);
				break;
		}
	}

}
