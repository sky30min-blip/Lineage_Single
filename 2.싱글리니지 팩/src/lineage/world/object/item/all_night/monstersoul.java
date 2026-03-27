package lineage.world.object.item.all_night;

import lineage.bean.lineage.Map;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_BlueMessage;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class monstersoul extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new monstersoul();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha != null && cha instanceof PcInstance && cha.getInventory() != null && this != null && getItem() != null) {
			if (World.isSafetyZone(cha.getX(), cha.getY(), cha.getMap()) || World.isCombatZone(cha.getX(), cha.getY(), cha.getMap())) {
				ChattingController.toChatting(cha, "노말존에서 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			String name = getItem().getName().replace("영혼석:", "").trim();
			Map m = World.get_map(cha.getMap());
			
			if (m != null) {
				MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find(name));

				if (mi != null) {
					int x1 = m.locX1;
					int x2 = m.locX2;
					int y1 = m.locY1;
					int y2 = m.locY2;
					int range = Util.random(2, 3);
					
					mi.setHomeX(cha.getX());
					mi.setHomeY(cha.getY());
					mi.setHomeMap(cha.getMap());
					mi.setHeading(Util.random(0, 7));
					
					if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
						if (mi.getMonster().isHaste())
							mi.setSpeed(1);
						if (mi.getMonster().isBravery())
							mi.setBrave(true);
					}

					if (range > 1) {				
						int roop_cnt = 0;
						int x = cha.getX();
						int y = cha.getY();
						int map = cha.getMap();
						int lx = x;
						int ly = y;
						int loc = range;
						// 랜덤 좌표 스폰
						do {
							lx = Util.random(x - loc < x1 ? x1 : x - loc, x + loc > x2 ? x2 : x + loc);
							ly = Util.random(y - loc < y1 ? y1 : y - loc, y + loc > y2 ? y2 : y + loc);
							if (roop_cnt++ > 100) {
								lx = x;
								ly = y;
								break;
							}
						}while(
								!World.isThroughObject(lx, ly+1, map, 0) || 
								!World.isThroughObject(lx, ly-1, map, 4) || 
								!World.isThroughObject(lx-1, ly, map, 2) || 
								!World.isThroughObject(lx+1, ly, map, 6) ||
								!World.isThroughObject(lx-1, ly+1, map, 1) ||
								!World.isThroughObject(lx+1, ly-1, map, 5) || 
								!World.isThroughObject(lx+1, ly+1, map, 7) || 
								!World.isThroughObject(lx-1, ly-1, map, 3) ||
								World.isNotMovingTile(lx, ly, map)
							);
						
						mi.toTeleport(lx, ly, cha.getMap(), false);
					} else {
						mi.toTeleport(cha.getX(), cha.getY(), cha.getMap(), false);
					}			

					AiThread.append(mi);
					World.appendMonster(mi);

					// 아이템 수량 갱신
					cha.getInventory().count(this, getCount() - 1, true);
					
					String msg = String.format("\\fY[영혼석] '%s' 소환되었습니다.", name);
					World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

					// 화면 중앙에 메세지 알리기.
					if (Lineage.is_blue_message)
						World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
				} else {
					ChattingController.toChatting(cha, "몬스터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				ChattingController.toChatting(cha, "해당 맵이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
