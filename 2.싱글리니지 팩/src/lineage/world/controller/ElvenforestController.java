package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.database.ItemDatabase;
import lineage.database.NpcDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.item.ElementalStone;
import lineage.world.object.npc.craft.Fairy;
import lineage.world.object.npc.craft.FairyQueen;

public final class ElvenforestController {

	// 페어리 무리와 퀸 정보
	static private List<object> list_fairy;			// 스폰할 페어리 목록
	static private boolean fairy_spawn;				// 스폰 여부
	static private List<ElementalStone> list_stone;	// 정령의돌 스폰된거 목록.
	static private long spawn_stone_time;			// 정령의돌 다음 스폰될 시간 임시 저장 변수.
	
	static public void init(){
		TimeLine.start("ElvenforestController..");
		
		fairy_spawn = false;
		list_fairy = new ArrayList<object>();
		list_stone = new ArrayList<ElementalStone>();
		
		TimeLine.end();
	}
	
	static public void toTimer(long time){
		if(Lineage.server_version<144)
			return;
		
		// 페어리퀸 스폰시간 확인.
		switch(ServerDatabase.getLineageTimeHour()){
			case 0:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13://
			case 14:
			case 15:
			case 16://
			case 17://
			case 18://
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
				if(fairy_spawn == false){
					fairy_spawn = true;
					// 페어리 퀸
					list_fairy.add( FairyQueen.clone(NpcSpawnlistDatabase.getPool(FairyQueen.class), NpcDatabase.find("페어리 퀸")) );

					for(object o : list_fairy){
						//페리어퀸 이름 출력되게
						o.setTitle( "페어리 퀸" );
						o.setHomeX( 33158 );
						o.setHomeY( 32276 );
						o.setHomeMap( 4 );
						o.setHeading(5);
						o.toTeleport(o.getHomeX()+Util.random(-4, 4), o.getHomeY()+Util.random(-4, 4), 4, false);
						AiThread.append(o);
					}
				}
				break;
			default:
				if(fairy_spawn == true){
					fairy_spawn = false;
					for(object o : list_fairy)
						o.toAiThreadDelete();
					list_fairy.clear();
				}
				break;
		}
		
		// 정령의 돌 스폰.
		if (list_stone.size() < Lineage.elvenforest_elementalstone_spawn_count && spawn_stone_time <= time) {
			spawn_stone_time = time + Lineage.elvenforest_elementalstone_spawn_time;
			ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("정령의 돌"));
			if (ii != null) {
				ii.setObjectId(ServerDatabase.nextItemObjId());
				ii.setCount(Util.random(Lineage.elvenforest_elementalstone_min_count, Lineage.elvenforest_elementalstone_max_count));
				if (ii.getCount() <= 0)
					ii.setCount(1);

				int lx = Util.random(32954, 33146);
				int ly = Util.random(32220, 32495);
				// 랜덤 좌표 스폰
				do {
					lx = Util.random(32954, 33146);
					ly = Util.random(32220, 32495);

				} while (!World.isThroughObject(lx, ly + 1, 4, 0) || !World.isThroughObject(lx, ly - 1, 4, 4) || !World.isThroughObject(lx - 1, ly, 4, 2) || !World.isThroughObject(lx + 1, ly, 4, 6)
						|| !World.isThroughObject(lx - 1, ly + 1, 4, 1) || !World.isThroughObject(lx + 1, ly - 1, 4, 5) || !World.isThroughObject(lx + 1, ly + 1, 4, 7) || !World.isThroughObject(lx - 1, ly - 1, 4, 3));

				ii.toTeleport(lx, ly, 4, false);
				ii.toDrop(null);
			}
		}
	}
	
	/**
	 * 정령의돌 관리목록에서 제거 함수.
	 * @param es
	 */
	static public void removeStone(ElementalStone es){
		if (list_stone != null) {
			synchronized (list_stone) {
				list_stone.remove(es);
			}
		}
	}
	
	/**
	 * 엄마나무 근처인지 확인해주는 함수.
	 * @param o
	 * @return
	 */
	static public boolean isTreeZone(object o) {
		return o.getX()>=Lineage.TREEX1 && o.getX()<=Lineage.TREEX2 && o.getY()>=Lineage.TREEY1 && o.getY()<=Lineage.TREEY2;
	}
	
}
