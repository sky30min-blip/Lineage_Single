package lineage.world.object.npc.background.door;

import lineage.bean.lineage.Agit;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.AgitController;
import lineage.world.controller.CharacterController;
import lineage.world.object.Character;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.ItemInstance;

public class Door extends BackgroundInstance {
	
	private Agit agit;
	private int item_nameid;	// 문열때 사용되는 아이템
	private int item_count;		// 문열때 사용될 아이템 갯수.
	private int timer_cnt;		// toTimer 에서 사용되는 변수.
	private boolean item_remove;	// 아이템 사용후 해당 아이템을 제거할지에 대한 부분.
	
	public Agit getAgit() {
		return agit;
	}
	
	public void setKey(int item_nameid, int item_count, boolean item_remove){
		this.item_nameid = item_nameid;
		this.item_count = item_count;
		this.item_remove = item_remove;
		
		// 아이템이 지정된 문일경우 타이머에서 호출할수 있도록 등록하기. 문 자동으로 닫히게 하기위해.
		if(item_nameid>0)
			CharacterController.toWorldJoin(this);
	}
	
	/**
	 * 문열때 사용되어지는 키 리턴.
	 * @return
	 */
	public int getKey(){
		return item_nameid;
	}

	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		super.toTeleport(x, y, map, effect);
		
		// 아지트쪽에 영향을 주는 문인지 확인.
		agit = AgitController.find("door", x, y);
		if(agit != null)
			agit.append(this);
		
		// 스폰된 위치에 타일값 기록.
		homeTile[0] = World.get_map(x, y, map);
		switch(heading){
			case 2:
			case 6:
				homeTile[1] = World.get_map(x-1, y, map);
				break;
			case 4:	// 6방향으로 증가.
				homeTile[1] = World.get_map(x, y+1, map);
				break;
		}
	}
	
	@Override
	public boolean isDoorClose(){
		return gfxMode==29;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 아지트쪽에 영향을 주는 문일경우 클랜아이디를 확인해서 같을경우에만 처리할 수 있도록 유도하기.
		if(agit!=null && cha!=null && (agit.getClanId()==0 || agit.getClanId()!=cha.getClanId()))
			return;
		// 문열때 아이템 이 필요한거라면 확인하기.
		if(cha!=null && item_nameid>0 && item_count>0 && cha.getInventory()!=null){
			ItemInstance ii = cha.getInventory().findDbNameId(item_nameid);
			if(ii==null || ii.getCount()<item_count)
				return;
			if(item_remove)
				cha.getInventory().count(ii, ii.getCount()-ii.getCount(), true);
		}
		// 문에 사용되는 아이템값이 0이하일경우 절대적으로 사용자가 열수 없는 문으로 판단.
		if(item_nameid<0)
			return;
		
		if(gfxMode == 28)
			toClose();
		else
			toOpen();
		toSend();
	}

	public void toClose(){
		gfxMode = 29;
	}

	public void toOpen(){
		gfxMode = 28;
	}

	public void toSend(){
		toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this), false);
		toDoorSend(null);
	}
	
	@Override
	public void toTimer(long time){
		// 문이 열려잇을경우 자동으로 닫히게 하기위해.
		if(gfxMode == 28){
			if(++timer_cnt%Lineage.door_open_delay == 0){
				timer_cnt = 0;
				toClick(null, null);
			}
		}
	}
	
}
