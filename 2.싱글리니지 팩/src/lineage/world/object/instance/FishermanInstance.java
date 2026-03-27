package lineage.world.object.instance;

import java.sql.Connection;

import lineage.bean.database.FishList;
import lineage.bean.database.Item;
import lineage.bean.lineage.Inventory;
import lineage.database.FishItemListDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.FishingController;
import lineage.world.object.Character;

public class FishermanInstance extends Character {
	private long pc_objectId;
	private int pc_accountUid;
	private String pc_name;
	private int fishTime;
	private Inventory inventory;
	private ItemInstance coin;
	private ItemInstance rice;

	public FishermanInstance(PcInstance pc) {
		this.pc_objectId = pc.getObjectId();
		this.pc_accountUid = pc.getAccountUid();
		this.pc_name = pc.getName() != null ? pc.getName() : pc.getTempName() == null ? "" : pc.getTempName();
		
		this.inventory = pc.getInventory();
		this.fishTime = Lineage.fish_delay;
		this.coin = inventory.find(ItemDatabase.find(Lineage.auto_fish_coin));
		this.rice = inventory.find(ItemDatabase.find(Lineage.fish_rice));

		setObjectId(ServerDatabase.nextEtcObjId());
		setName(pc_name + "의 자동낚시");
		setClanId(0);
		setClanName("");
		setTitle("");
		setLawful(66536);
		setGfx(pc.getClassGfx());
		setGfxMode(pc.getGfxMode());
		setX(pc.getX());
		setY(pc.getY());
		setMap(pc.getMap());
		setHeading(pc.getHeading());
		toTeleport(getX(), getY(), getMap(), false);
		
		FishingController.appendFishRobot(pc_accountUid, this);
	}
	
	public FishermanInstance(long pc_objectId, int pc_accountUid, String pc_name, int gfx, int gfxMode, int x, int y, int map, int heading, int fishTime) {
		this.pc_objectId = pc_objectId;
		this.pc_accountUid = pc_accountUid;
		this.pc_name = pc_name;
		
		this.inventory = new Inventory().clone(this);
		this.fishTime = fishTime;

		setObjectId(ServerDatabase.nextEtcObjId());
		setName(pc_name + "의 자동낚시");
		setClanId(0);
		setClanName("");
		setTitle("");
		setLawful(66536);
		setGfx(gfx);
		setGfxMode(gfxMode);
		setX(x);
		setY(y);
		setMap(map);
		setHeading(heading);
		toTeleport(getX(), getY(), getMap(), false);
		
		FishingController.appendFishRobot(pc_accountUid, this);
	}

	@Override
	public void close() {
		super.close();
		
		BackgroundInstance effect = FishingController.auto_fish_effect_list.get(pc_accountUid);
		FishingController.auto_fish_effect_list.remove(pc_accountUid);
		if (effect != null) {
			effect.clearList(true);
			World.remove(effect);
		}
		clearList(true);
		World.remove(this);
		
		pc_objectId = 0L;
		pc_accountUid = 0;
		pc_name = null;
		fishTime = 0;
		inventory = null;
		coin = rice = null;
	}
	
	public long getPc_objectId() {
		return pc_objectId;
	}

	public void setPc_objectId(long pc_objectId) {
		this.pc_objectId = pc_objectId;
	}
	
	public int getPc_accountUid() {
		return pc_accountUid;
	}

	public void setPc_accountUid(int pc_accountUid) {
		this.pc_accountUid = pc_accountUid;
	}
	
	public String getPc_name() {
		return pc_name == null ? "" : pc_name;
	}

	public void setPc_name(String pc_name) {
		this.pc_name = pc_name;
	}
	
	public int getFishTime() {
		return fishTime;
	}
	
	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	public ItemInstance getCoin() {
		return coin;
	}

	public void setCoin(ItemInstance coin) {
		this.coin = coin;
	}

	public ItemInstance getRice() {
		return rice;
	}

	public void setRice(ItemInstance rice) {
		this.rice = rice;
	}
	
	public void toWorldJoin(Connection con) {
		FishingController.readInventory(con, this);
		
		coin = inventory.find(ItemDatabase.find(Lineage.auto_fish_coin));
		rice = inventory.find(ItemDatabase.find(Lineage.fish_rice));
	}
	
	public void toWorldOut(boolean isSave) {
		FishingController.toDelete(this);
		
		if (isSave)
			FishingController.saveFishermanInstance(this);
		
		// 인벤토리 초기화
		inventory.close();
		
		FishingController.removeFishRobot(pc_accountUid);
		
		clearList(true);
		World.remove(this);
		close();
	}
	
	@Override
	public void toTimer(long time) {
		if (this != null) {
			if (inventory == null || inventory.getList().size() >= Lineage.inventory_max) {
				toWorldOut(true);
				return;
			}
			
			if (!checkCoin() || !checkRice()) {
				toWorldOut(true);
				return;
			}
			
			if (--fishTime <= 0) {				
				fishTime = Lineage.fish_delay;
				huntFish();
			}
		}
	}
	
	public boolean checkCoin() {
		if (coin != null && coin.getItem() != null && coin.getCount() >= Lineage.auto_fish_expense)
			return true;
		
		return false;
	}
	
	public boolean checkRice() {
		if (rice != null && rice.getItem() != null && rice.getCount() > 0)
			return true;
		
		return false;
	}
	
	/**
	 * 아이템 지급.
	 * 2020-03-11
	 * by connector12@nate.com
	 */
	public void huntFish() {
		try {
			if (inventory != null) {
				Item i = ItemDatabase.find(Lineage.fish_exp);
				// 경험치 지급단
				if (i != null) {
					ItemInstance temp = inventory.find(i.getName(), i.isPiles());
					
					if (temp == null) {
						temp = ItemDatabase.newInstance(i);
						temp.setObjectId(ServerDatabase.nextItemObjId());
						temp.setBless(1);
						temp.setEnLevel(0);
						temp.setCount(1);
						temp.setDefinite(true);
						inventory.append(temp, false);
					} else {
						// 겹치는 아이템이 존재할 경우.
						inventory.count(temp, temp.getCount() + 1, false);
					}
				}
				
				if (FishItemListDatabase.getFishList().size() > 0) {
					// fishing_item_list 테이블의 목록중 랜덤으로 하나 추출
					FishList fishList = FishItemListDatabase.getFishList().get(Util.random(0, FishItemListDatabase.getFishList().size() - 1));
					
					if (fishList != null) {
						Item ii = ItemDatabase.find(fishList.getItemName());
						
						if (ii != null) {
							ItemInstance temp = inventory.find(fishList.getItemName(), fishList.getItemBless(), ii.isPiles());
							int count = Util.random(fishList.getItemCountMin(), fishList.getItemCountMax());
							
							if (temp != null && (temp.getBless() != fishList.getItemBless() || temp.getEnLevel() != fishList.getItemEnchant()))
								temp = null;
							
							if (temp == null) {
								// 겹칠수 있는 아이템이 존재하지 않을경우.
								if (ii.isPiles()) {
									temp = ItemDatabase.newInstance(ii);
									temp.setObjectId(ServerDatabase.nextItemObjId());
									temp.setBless(fishList.getItemBless());
									temp.setEnLevel(fishList.getItemEnchant());
									temp.setCount(count);
									temp.setDefinite(true);
									inventory.append(temp, false);
								} else {
									for (int idx = 0; idx < count; idx++) {
										temp = ItemDatabase.newInstance(ii);
										temp.setObjectId(ServerDatabase.nextItemObjId());
										temp.setBless(fishList.getItemBless());
										temp.setEnLevel(fishList.getItemEnchant());
										temp.setDefinite(true);
										inventory.append(temp, false);
									}
								}
							} else {
								// 겹치는 아이템이 존재할 경우.
								inventory.count(temp, temp.getCount() + count, false);
							}

							inventory.count(coin, coin.getCount() - Lineage.auto_fish_expense, false);
							inventory.count(rice, rice.getCount() - 1, false);
						}				
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : huntFish() 에러. 캐릭터: %s\r\n", FishermanInstance.class.toString(), pc_name);
			lineage.share.System.println(e);
		}
	}
	
}
