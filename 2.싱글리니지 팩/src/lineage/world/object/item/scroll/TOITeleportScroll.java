package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.LocationController;
import lineage.world.controller.WantedController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class TOITeleportScroll extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new TOITeleportScroll();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		boolean oman = cha.isOman();
		


		if(LocationController.isTeleportZone(cha, true, !oman) || oman){
			int floor = Integer.valueOf( getItem().getType2().substring( getItem().getType2().indexOf("_")+1 ) );
			switch(floor){
				case 1:
					cha.setHomeX(32796);
					cha.setHomeY(32800);
					cha.setHomeMap(101);
					break;
				case 2:
					cha.setHomeX(32796);
					cha.setHomeY(32800);
					cha.setHomeMap(102);
					break;
				case 3:
					cha.setHomeX(32796);
					cha.setHomeY(32800);
					cha.setHomeMap(103);
					break;
				case 4:
					cha.setHomeX(32670);
					cha.setHomeY(32862);
					cha.setHomeMap(104);
					break;
				case 5:
					cha.setHomeX(32670);
					cha.setHomeY(32863);
					cha.setHomeMap(105);
					break;
				case 6:
					cha.setHomeX(32670);
					cha.setHomeY(32863);
					cha.setHomeMap(106);
					break;
				case 7:
					cha.setHomeX(32670);
					cha.setHomeY(32862);
					cha.setHomeMap(107);
					break;
				case 8:
					cha.setHomeX(32669);
					cha.setHomeY(32862);
					cha.setHomeMap(108);
					break;
				case 9:
					cha.setHomeX(32669);
					cha.setHomeY(32863);
					cha.setHomeMap(109);
					break;
				case 10:
					cha.setHomeX(32797);
					cha.setHomeY(32800);
					cha.setHomeMap(110);
					break;
				default:
					if ((cha.getClanId() == 0 ) || (cha.getClanName().equalsIgnoreCase(Lineage.new_clan_name) && !Lineage.is_new_clan_oman_top)) {
						ChattingController.toChatting(cha, "신규혈맹 또는 혈맹이 없을경우 이동이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					
					if ( cha.getGm() == 0 && !WantedController.checkWantedPc(cha)) {
						ChattingController.toChatting(cha, "[오만의 탑 정상] 수배자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					
					cha.setHomeX(32691);
					cha.setHomeY(32903);
					cha.setHomeMap(200);
					break;
			}
			cha.toTeleport(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap(), true);
			cha.getInventory().count(this, getCount()-1, true);
		}
	}

}
