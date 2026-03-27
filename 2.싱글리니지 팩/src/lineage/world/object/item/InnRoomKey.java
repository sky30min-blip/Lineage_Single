package lineage.world.object.item;

import lineage.world.object.instance.ItemInstance;

public class InnRoomKey extends ItemInstance {

	private long key;
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new InnRoomKey();
		return item;
	}

	@Override
	public void close(){
		super.close();
		// 메모리 초기화 함수.
		key = 0;
	}
	
	@Override
	public long getInnRoomKey(){
		return key;
	}
	
	@Override
	public void setInnRoomKey(final long key){
		this.key = key;
	}

}
