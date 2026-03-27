package lineage.world.controller;

import lineage.database.ServerDatabase;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public final class ShipController {

	static private boolean isUpdateIsland;
	static private boolean isUpdateGludio;
	static private boolean isUpdateHeine;

	// 선착장에서 내릴 타이밍 분을 지정.
	static private int getoff_minute = 59;
	
	static public void init(){
		TimeLine.start("ShipController..");
		
		isUpdateIsland = isUpdateGludio = isUpdateHeine = true;
		
		TimeLine.end();
	}
	
	/**
	 * 타이머가 주기적으로 호출함.
	 */
	static public void toTimer(){
		int hour = ServerDatabase.getLineageTimeHour();
		int min = ServerDatabase.getLineageTimeMinute();

		// 말하는섬 선착장.
		onTalkingIsland(hour, min);
		// 글루디오 선착장.
		onGludio(hour, min);
		// 하이네 선착장.
		onHeine(hour, min);
	}

	/**
	 * 말하는선착장에서 탑승 및 글루디오로 하차
	 *  : hour값에 따라 탑승 및 하차좌표필드 값 변경하기.
	 *  : hour값에 따라 탑승된 유저들 글루디오선착장으로 텔레포트 시키기.
	 *    단, 글루디행표를 가지고있을 경우.
	 */
	static private void onTalkingIsland(int hour, int minute){
		// 필드 변경 : 매초마다 호출되는 메서드인데 매초마다 변경하는것은 비효율적이므로 bool 변수를 활용해서 한번만 수정하도록 함.
		if(hour==7 || hour==11 || hour==15 || hour==19){
			if(!isUpdateIsland){
				World.set_map(32630, 32983, 0, 127);
				World.set_map(32631, 32983, 0, 127);
				World.set_map(32632, 32983, 0, 127);
				World.set_map(32732, 32796, 5, 127);
				World.set_map(32733, 32796, 5, 127);
				World.set_map(32734, 32796, 5, 127);
				isUpdateIsland = true;
			}
		}else{
			if(isUpdateIsland){
				World.set_map(32630, 32983, 0, 0);
				World.set_map(32631, 32983, 0, 0);
				World.set_map(32632, 32983, 0, 0);
				World.set_map(32732, 32796, 5, 0);
				World.set_map(32733, 32796, 5, 0);
				World.set_map(32734, 32796, 5, 0);
				isUpdateIsland = false;
			}
		}

		// 배에서 내리기
		if(minute == getoff_minute){
			if(hour==8 || hour==12 || hour==17 || hour==20){
				toTeleport(5, 314, Util.random(32559, 32564), Util.random(32720, 32733), 4);
			}
		}
	}

	/**
	 * 글루디오선착장에서 탑승 및 말하는섬으로 하차
	 */
	static private void onGludio(int hour, int minute){
		if(hour==9 || hour==13 || hour==17 || hour==22){
			if(!isUpdateGludio){
				World.set_map(32540, 32728, 4, 127);
				World.set_map(32542, 32728, 4, 127);
				World.set_map(32543, 32728, 4, 127);
				World.set_map(32544, 32728, 4, 127);
				World.set_map(32735, 32794, 6, 127);
				World.set_map(32735, 32794, 6, 127);
				World.set_map(32735, 32794, 6, 127);
				isUpdateGludio = true;
			}
		}else{
			if(isUpdateGludio){
				World.set_map(32540, 32728, 4, 0);
				World.set_map(32542, 32728, 4, 0);
				World.set_map(32543, 32728, 4, 0);
				World.set_map(32544, 32728, 4, 0);
				World.set_map(32735, 32794, 6, 0);
				World.set_map(32735, 32794, 6, 0);
				World.set_map(32735, 32794, 6, 0);
				isUpdateGludio = false;
			}
		}

		if(minute == getoff_minute){
			if(hour==6 || hour==10 || hour==14 || hour==18){
				toTeleport(6, 315, Util.random(32630, 32632), Util.random(32971, 32983), 0);
			}
		}
	}

	/**
	 * 하이네선착장에서 탑승 및 잊혀진섬으로 하차
	 */
	static private void onHeine(int hour, int minute){
		if(hour==5 || hour==9 || hour==13 || hour==17){
//		if(hour==8 || hour==12 || hour==16 || hour==20){
			if(!isUpdateHeine){
				World.set_map(33423, 33502, 4, 127);
				World.set_map(33424, 33502, 4, 127);
				World.set_map(33425, 33502, 4, 127);
				World.set_map(33426, 33502, 4, 127);
				World.set_map(32733, 32794, 83, 127);
				World.set_map(32734, 32794, 83, 127);
				World.set_map(32735, 32794, 83, 127);
				World.set_map(32736, 32794, 83, 127);
				isUpdateHeine = true;
			}
		}else{
			if(isUpdateHeine){
				World.set_map(33423, 33502, 4, 0);
				World.set_map(33424, 33502, 4, 0);
				World.set_map(33425, 33502, 4, 0);
				World.set_map(33426, 33502, 4, 0);
				World.set_map(32733, 32794, 83, 0);
				World.set_map(32734, 32794, 83, 0);
				World.set_map(32735, 32794, 83, 0);
				World.set_map(32736, 32794, 83, 0);
				isUpdateHeine = false;
			}
		}

		if(minute == getoff_minute){
			if(hour==6 || hour==10 || hour==14 || hour==18){
				toTeleport(83, 1930, Util.random(32935, 32937), Util.random(33052, 33058), 70);
			}
		}
	}

	/**
	 * 배에 탑승중인 사용자 모두 텔레포트 시키기.
	 */
	static private void toTeleport(int ship_map, int item_nameid, int x, int y, int map){
		for( PcInstance pc : World.getPcList() ){
			if(pc.getMap()==ship_map){
				ItemInstance item = pc.getInventory().findDbNameId(item_nameid);
				if(item != null) {
					// 표가 잇으면 제거
					pc.getInventory().remove( item, true );
				}
				// 선착장으로 텔레포트
				pc.toTeleport(x, y, map, false);
			}
		}
	}
	
}
