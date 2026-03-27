package lineage.world.object.npc.background;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.CharacterController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.npc.background.door.Door;

public class Switch extends BackgroundInstance {

	// 스위치 온오프 상태.
	private boolean status;
	private boolean status_temp;
	private int gfxMode_temp;
	private int timer_cnt;		// toTimer 에서 사용되는 변수.
	
	public Switch(){
		CharacterController.toWorldJoin(this);
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 닫혀있을때만 처리.
		if(gfxMode == 29){
			// 스위치 변화.
			toOn();
			toSend();
			// 문 찾아서 열기.
			Door d = searchDoor();
			if(d != null){
				d.toOpen();
				d.toSend();
			}
		}
	}
	
	@Override
	public void toTimer(long time){
		switch(getMap()){
			case 201:	// 마법사 30레벨 퀘스트
				// 현재상태 임시 저장.
				status_temp = status;
				// 좌표값에 객체존재여부따라 gfxmode 변경.
				if(World.isMapdynamic(x, y, map))
					toOn();
				else
					toOff();
				// 임시저장값과 다르면 패킷처리.
				if(status != status_temp){
					toSend();
					// 주변에 문을 찾아서 해당 문에 카운팅값을 증가 및 감소.
					// 카운팅값이 4이상이라면 문을 오픈. 이하라면 클로즈.
					Door d = searchDoor();
					if(d != null){
						// 현재값 임시 저장
						gfxMode_temp = d.getGfxMode();
						// 스위치 상태에따라 카운팅
						if( status )
							d.setTempCount( d.getTempCount() + 1 );
						else
							d.setTempCount( d.getTempCount() - 1 );
						
						// 카운팅값 확인해서 문 열고 닫기.
						if(d.getTempCount()>3)
							d.toOpen();
						else
							d.toClose();
						// 변화가 이뤄졌다면 패킷 처리.
						if(gfxMode_temp != d.getGfxMode())
							d.toSend();
					}
				}
				break;
			case 2:		// 말섬던전2층 스위치.
				// 문이 열려잇을경우 자동으로 닫히게 하기위해.
				if(gfxMode == 28){
					if(++timer_cnt%Lineage.door_open_delay == 0){
						// 스위치 변화.
						toOff();
						toSend();
						// 문찾아서 닫기.
						Door d = searchDoor();
						if(d != null){
							d.toClose();
							d.toSend();
						}
					}
				}
				break;
		}
	}
	
	public void toOn(){
		setGfxMode( 28 );
		status = true;
	}
	
	public void toOff(){
		setGfxMode( 29 );
		status = false;
	}
	
	public void toSend(){
		toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
	}
	
	private Door searchDoor(){
		for(object o : getInsideList()){
			if(o instanceof Door){
				Door d = (Door)o;
				// 수동조작 불가능한 문만 찾아서 리턴.
				if(d.getKey() < 0)
					return d;
			}
		}
		return null;
	}
}
