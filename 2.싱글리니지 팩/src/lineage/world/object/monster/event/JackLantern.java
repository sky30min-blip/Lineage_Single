package lineage.world.object.monster.event;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.database.ItemDatabase;
import lineage.world.controller.EventController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;

public class JackLantern extends MonsterInstance {
	
	private Item event_item;	// 확인할 아이템
	private boolean isDrop;		// 씨를 드랍할지 여부.
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new JackLantern();
		return MonsterInstance.clone(mi, m);
	}
	
	public JackLantern(){
		event_item = ItemDatabase.find("호박 캔디");
	}

	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		// 할로윈 이벤트 관리목록에 등록.
		EventController.appendHalloweenMonster(this);
		// 상태 변경.
		isDrop = false;
		// 스폰.
		super.toTeleport(x, y, map, effect);
	}

	@Override
	public void toGiveItem(object o, ItemInstance item, long count) {
		super.toGiveItem(o, item, count);
		
		// 드랍 승낙 떠러진 상태는 무시.
		if(isDrop)
			return;
		
		// 캔디 확인.
		ItemInstance temp = inv.find(event_item);
		if(temp != null){
			// 드랍 승낙
			isDrop = true;
			// 인벤에서 제거.
			inv.count(temp, 0, false);
		}
	}
	
	@Override
	protected void toAiDead(long time){
		// 드랍 승낙이 떠러지지 않앗을경우 전체 인벤 제거.
		if(!isDrop){
			for(ItemInstance i : inv.getList())
				ItemDatabase.setPool(i);
			inv.clearList();
		}
		// 할로윈 이벤트 관리목록에서 제거.
		EventController.removeHalloweenMonster(this);
		
		super.toAiDead(time);
	}

	@Override
	protected void toAiWalk(long time){
		super.toAiWalk(time);
		// 절대적 투망상태로 변경.
		setTransparent(true);
	}

	@Override
	public void toAiAttack(long time){
		super.toAiAttack(time);
		// 전투 중일땐 해제.
		setTransparent(false);
	}
	
}
