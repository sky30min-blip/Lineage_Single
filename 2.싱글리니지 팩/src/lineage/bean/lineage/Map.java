package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lineage.database.ItemDatabase;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.DwarfInstance;
import lineage.world.object.instance.InnInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PetMasterInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.instance.TeleportInstance;
import lineage.world.object.npc.ClanMaker;
import lineage.world.object.npc.Ellyonne;
import lineage.world.object.npc.Maid;
import lineage.world.object.npc.Sedia;
import lineage.world.object.npc.Siris;
import lineage.world.object.npc.TalkNpc;
import lineage.world.object.npc.background.Racer;
import lineage.world.object.npc.buff.ArmorEnchanter;
import lineage.world.object.npc.buff.Curer;
import lineage.world.object.npc.buff.Hadesty;
import lineage.world.object.npc.buff.Haste;
import lineage.world.object.npc.buff.PolymorphMagician;
import lineage.world.object.npc.buff.WeaponEnchanter;
import lineage.world.object.npc.kingdom.KingdomCastleTop;
import lineage.world.object.npc.kingdom.KingdomChamberlain;
import lineage.world.object.npc.kingdom.KingdomDoor;
import lineage.world.object.npc.kingdom.KingdomDoorman;
import lineage.world.object.npc.quest.GatekeeperAnt;
import lineage.world.object.npc.quest.Richard;
import lineage.world.object.npc.quest.SearchAnt;

public class Map {
	private List<object> list;
	public int mapid;
	public int locX1;
	public int locX2;
	public int locY1;
	public int locY2;
	public int size;
	public byte[] data;
	public byte[] dataDynamic;
	public int data_size;
	public String name;
    private final ReadWriteLock dataDynamicLock = new ReentrantReadWriteLock();

	public Map(){
		list = new ArrayList<object>();
	}
    private int getPosition(int x, int y) {
        return ((locX2 - locX1) * (y - locY1)) + (x - locX1) + (y - locY1);
    }

    public int getMapdynamic(int x, int y, int map) {
        int pos = getPosition(x, y);
        dataDynamicLock.readLock().lock();
        try {
            if (pos < data_size && pos >= 0)
                return dataDynamic[pos];
        } finally {
            dataDynamicLock.readLock().unlock();
        }
        return 0;
    }

    public boolean isMapdynamic(int x, int y, int map) {
        int pos = getPosition(x, y);
        dataDynamicLock.readLock().lock();
        try {
            if (pos < data_size && pos >= 0)
                return dataDynamic[pos] != 0;
        } finally {
            dataDynamicLock.readLock().unlock();
        }
        return false;
    }
    public void update_mapDynamic(int x, int y, int map, boolean plus) {
        int pos = getPosition(x, y);
        dataDynamicLock.writeLock().lock();
        try {
            if (pos < data_size && pos >= 0) {
                if (plus) {
                    dataDynamic[pos] += 1;
                } else {
                    dataDynamic[pos] -= 1;
                    if (dataDynamic[pos] < 0)
                        dataDynamic[pos] = 0;
                }
            }
        } finally {
            dataDynamicLock.writeLock().unlock();
        }
    }
	
	public void append(object o){
		synchronized (list) {
			if(!list.contains(o))
				list.add(o);
		}
		o.setWorldDelete( false );
		
//		lineage.share.System.printf("World (%d) : %d\r\n", mapid, list.size());
	}
	
	public void remove(object o){
		synchronized (list) {
			list.remove(o);
		}
		o.setWorldDelete( true );
		
//		lineage.share.System.printf("World (%d) : %d\r\n", mapid, list.size());
	}
	
	public void getList(object o, int loc, List<object> r_list){
		synchronized (list) {
			for(object oo : list){
				if(o.getObjectId()!=oo.getObjectId() && Util.isDistance(o, oo, loc))
					r_list.add(oo);
			}
		}
	}
	
	public void clearWorldItem(){
		List<object> list_temp = new ArrayList<object>();
		// 추출.
		synchronized (list) {
			for(object o : list){
				if(o instanceof ItemInstance)
					list_temp.add(o);
			}
		}
		// 삭제.
		for(object o : list_temp){
			ItemInstance item = (ItemInstance)o;
			remove(item);
			item.clearList(true);
			// 특정케릭이 인벤에 가지고있을 가능성이 있기때문에 반드시 관리중인 사용자가 없을때만 처리.
			if(item.getCharacter() == null)
				ItemDatabase.setPool((ItemInstance)o);
		}
		list_temp.clear();
		list_temp = null;
	}
	
	public void clearWorldItem(long time){
		List<object> list_temp = new ArrayList<object>();
		// 추출.
		synchronized (list) {
			for(object o : list){
				if(o instanceof ItemInstance){
					ItemInstance item = (ItemInstance)o;
					if(item.getItem().getNameIdNumber()!=762 && item.getTimeDrop()+Lineage.world_item_delay<=time){
						list_temp.add(o);
					}
				}
			}
		}
		// 삭제.
		for(object o : list_temp){
			ItemInstance item = (ItemInstance)o;
			remove(item);
			item.clearList(true);
			// 특정케릭이 인벤에 가지고있을 가능성이 있기때문에 반드시 관리중인 사용자가 없을때만 처리.
			if(item.getCharacter() == null)
				ItemDatabase.setPool((ItemInstance)o);
		}
		list_temp.clear();
		list_temp = null;
	}
	
	public int getSize(){
		return list.size();
	}

	/** 월드 전체 검색용: 이 맵에 있는 모든 객체를 out에 추가 */
	public void collectAllObjects(java.util.List<object> out) {
		if (out == null)
			return;
		synchronized (list) {
			out.addAll(list);
		}
	}
	
	/**
	 * 해당 맵에 스폰된 객체 분류해서 리턴함.
	 * @param player
	 * @param monster
	 * @param npc
	 */
	public void searchObject(List<object> r_list, boolean item, boolean npc, boolean monster, boolean background, boolean player, boolean shop){
		synchronized (list) {
			for(object o : list){
				if(npc){
					if(o instanceof NpcInstance)
						r_list.add(o);
					if(o instanceof ArmorEnchanter)
						r_list.add(o);
					if(o instanceof ClanMaker)
						r_list.add(o);
					if(o instanceof Curer)
						r_list.add(o);
					if(o instanceof DwarfInstance)
						r_list.add(o);
					if(o instanceof Ellyonne)
						r_list.add(o);
					if(o instanceof GatekeeperAnt)
						r_list.add(o);
					if(o instanceof Hadesty)
						r_list.add(o);
					if(o instanceof Haste)
						r_list.add(o);
					if(o instanceof InnInstance)
						r_list.add(o);
					if(o instanceof KingdomChamberlain)
						r_list.add(o);
					if(o instanceof KingdomDoorman)
						r_list.add(o);
					if(o instanceof Maid)
						r_list.add(o);
					if(o instanceof PetMasterInstance)
						r_list.add(o);
					if(o instanceof PolymorphMagician)
						r_list.add(o);
					if(o instanceof Richard)
						r_list.add(o);
					if(o instanceof SearchAnt)
						r_list.add(o);
					if(o instanceof Sedia)
						r_list.add(o);
					if(o instanceof Siris)
						r_list.add(o);
					if(o instanceof Racer)
						r_list.add(o);
					if(o instanceof TalkNpc)
						r_list.add(o);
					if(o instanceof TeleportInstance)
						r_list.add(o);
					if(o instanceof WeaponEnchanter)
						r_list.add(o);
					if(o instanceof KingdomCastleTop)
						r_list.add(o);
					if(o instanceof KingdomDoor)
						r_list.add(o);
				}
				if(shop){
					if(o instanceof ShopInstance)
						r_list.add(o);
				}
				if(monster){
					if(o instanceof MonsterInstance && !(o instanceof SummonInstance))
						r_list.add(o);
				}
				if(player){
					if(o instanceof PcInstance)
						r_list.add(o);
					if(o instanceof SummonInstance)
						r_list.add(o);
				}
				if(item){
					if(o instanceof ItemInstance)
						r_list.add(o);
				}
				if(background){
					if(o instanceof BackgroundInstance)
						r_list.add(o);
				}
			}
		}
	}
	
}
