package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.database.NpcSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectTitle;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CommandController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class BuffNpc extends object {
	private List<object> list1;
	private List<object> list2;
	private int count = 5;
	
	public BuffNpc() {
		list1 = new ArrayList<object>();
		list2 = new ArrayList<object>();
		
		if (list1.size() == 0) {
			for (int i = 0; i < count; i++) {
				object buff = new lineage.world.object.npc.background.ShockStun();
								
				// 여법사
				buff.setObjectId(ServerDatabase.nextEtcObjId());
				buff.setGfx(1186);
				buff.setName("버프사");
				
				list1.add(buff);
				NpcSpawnlistDatabase.appendList(buff);
			}
		}

		if (list2.size() == 0) {
			for (int i = 0; i < count; i++) {
				object buff = new lineage.world.object.npc.background.ShockStun();

				// 여요정
				buff.setObjectId(ServerDatabase.nextEtcObjId());
				buff.setGfx(37);
				buff.setName("버프사");
				
				list2.add(buff);
				NpcSpawnlistDatabase.appendList(buff);
			}
		}		
	}
	
	@Override
	public void toTeleport(int x, int y, int map, boolean effect) {
		reloadTitle(true);
		int temp_x;
		int temp_y;
		super.toTeleport(x, y, map, effect);
		
		if (!Lineage.buffNpcList.contains(this)) {
			Lineage.buffNpcList.add(this);
		}		
		
		for (int i = 0; i < count; i++) {
			list1.get(i).setHeading(getHeading());
			list2.get(i).setHeading(getHeading());
			
			switch (getHeading()) {
			case 0:
				temp_x = x - 2;
				temp_y = y + 2;
				list1.get(i).toTeleport(temp_x + i, temp_y, map, false);
				list2.get(i).toTeleport(temp_x + i, temp_y + 1, map, false);
				break;
			case 1:
				temp_x = x - 5;
				temp_y = y;
				list1.get(i).toTeleport(temp_x + 1 + i, temp_y + i, map, false);
				list2.get(i).toTeleport(temp_x + i, temp_y + 1 + i, map, false);
				break;
			case 2:
				temp_x = x - 3;
				temp_y = y - 2;
				list1.get(i).toTeleport(temp_x + 1, temp_y + i, map, false);
				list2.get(i).toTeleport(temp_x, temp_y + i, map, false);
				break;
			case 3:
				temp_x = x - 1;
				temp_y = y - 4;
				list1.get(i).toTeleport(temp_x + 1 - i, temp_y + 1 + i, map, false);
				list2.get(i).toTeleport(temp_x - i, temp_y + i, map, false);
				break;
			case 4:
				temp_x = x + 2;
				temp_y = y - 2;
				list1.get(i).toTeleport(temp_x - i, temp_y - i, map, false);
				list2.get(i).toTeleport(temp_x - i, temp_y - 1 - i, map, false);
				break;
			case 5:
				temp_x = x + 4;
				temp_y = y;
				list1.get(i).toTeleport(temp_x - i, temp_y - i, map, false);
				list2.get(i).toTeleport(temp_x + 1 - i, temp_y - 1 - i, map, false);
				break;
			case 6:
				temp_x = x + 2;
				temp_y = y - 2;
				list1.get(i).toTeleport(temp_x, temp_y + i, map, false);
				list2.get(i).toTeleport(temp_x + 1, temp_y + i, map, false);
				break;
			case 7:
				temp_x = x;
				temp_y = y + 3;
				list1.get(i).toTeleport(temp_x - 1 + i, temp_y - i, map, false);
				list2.get(i).toTeleport(temp_x + i, temp_y + 1 - i, map, false);
				break;
			}
		}
	}

	public void toDamage(Character cha) {		
		if (Util.isDistance(this, cha, 5)) {	
			if (cha.getLevel() <= Lineage.buff_max_level || cha.getInventory().isAden(Lineage.buff_aden, true)) {			
				for (object b : list1)
					b.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), b, Lineage.GFX_MODE_SPELL_NO_DIRECTION), false);
				
				for (object b : list2)
					b.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), b, Lineage.GFX_MODE_SPELL_NO_DIRECTION), false);
				
				CommandController.toBuff(cha);
				
				ChattingController.toChatting(cha, String.format("버프: %s아데나 소모.", Util.changePrice(Lineage.buff_aden)), Lineage.CHATTING_MODE_MESSAGE);
				
				PcInstance pc = (PcInstance) cha;
				
				if (pc.isAutoAttack)
					pc.cancelAutoAttack();
			} else {
				ChattingController.toChatting(cha, String.format("버프는 %s아데나가 필요합니다.", Util.changePrice(Lineage.buff_aden)), Lineage.CHATTING_MODE_MESSAGE);
			}
		} else {
			ChattingController.toChatting(cha, String.format("%s가 너무 멀리 있습니다.", getName()), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	public void reloadTitle(boolean isTeleport) {
		setTitle(String.format("\\f=%,d아데나", Lineage.buff_aden));
		
		if (!isTeleport)
			toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), this), true);
	}
}
