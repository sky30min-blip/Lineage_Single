package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.ItemSetoption;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_InventoryAdd;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryDelete;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.plugin.PluginController;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.PartyController;
import lineage.world.controller.PcTradeController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;
import lineage.world.object.item.Aden;
import lineage.world.object.item.Candle;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.Letter;
import lineage.world.object.item.RaceTicket;
import lineage.world.object.item.ThrowingKnife;
import lineage.world.object.item.all_night.EnchantRecovery;
import lineage.world.object.item.all_night.ClassChangeTicket;
import lineage.world.object.item.ring.RingSummonControl;
import lineage.world.object.item.ring.RingTeleportControl;
import lineage.world.object.item.weapon.Arrow;
import lineage.world.object.item.yadolan.HuntingZoneTeleportationBook;
import lineage.world.object.magic.AbsoluteBarrier;
import lineage.world.object.npc.kingdom.KingdomCrown;

public class Inventory {
	// 인벤토리아이템 목록
	private List<ItemInstance> list;
	// 셋트아이템 목록
	private List<ItemSetoption> setitem_list;
	// 착용된 아이템 묵음
	private ItemInstance slot[];
	private Character cha;
	private double weight;
	static public int  attack = 0 ;
	
	// 임시 저장 변수
	public ItemInstance changeName; // 이름변경 스크롤 사용시 사용됨.
	public ItemInstance characterInventory; // 인벤확인 스크롤 사용시 사용됨.

	public Inventory() {
		setitem_list = new ArrayList<ItemSetoption>();
		list = new ArrayList<ItemInstance>();
		slot = new ItemInstance[Lineage.SLOT_ARROW + 1];
	}

	public Inventory clone(Character cha) {
		this.cha = cha;
		return this;
	}
	   public void count(ItemInstance item, ItemInstance temp, long count, boolean packet) {
			int realcount = (int)count;
			if(realcount < 0 || realcount > 2000000000) {
				return;
			}
		      //
		 		count(item, count, packet);
		   }
	/**
	 * 다시 풀에 객체가 들어갈때 호출됨. : 관리중인 아이템 객체을 아이템관리쪽 풀에 넣기위함. 그외 메모리 관리 처리 담당.
	 */
	public void close() {
		synchronized (slot) {
			for (int i = 0; i <= Lineage.SLOT_ARROW; ++i)
				slot[i] = null;
		}
		synchronized (list) {
			for (ItemInstance i : list)
				ItemDatabase.setPool(i);
			list.clear();
		}
		synchronized (setitem_list) {
			setitem_list.clear();
		}
		cha = null;
		
		changeName = characterInventory = null;
	}

	public void clearList() {
		synchronized (list) {
			list.clear();
		}
	}

	/**
	 * 관리중인 아이템 리턴.
	 * 
	 * @return
	 */
	public List<ItemInstance> getList() {
		synchronized (list) {
			return new ArrayList<ItemInstance>(list);
		}
	}
	
	public Collection<ItemInstance> getListColl() {
		synchronized (list) {
			return new ArrayList<ItemInstance>(list);
		}
	}
	
	public void setList(List<ItemInstance> tempList) {
		synchronized (list) {
			list = tempList;
		}
	}

	public ItemInstance getList(int idx) {
		synchronized (list) {
			return list.get(idx);
		}
	}

	public void removeList(ItemInstance ii) {
		synchronized (list) {
			list.remove(ii);
		}
	}

	public void removeList(List<ItemInstance> item_list) {
		synchronized (list) {
			list.removeAll(item_list);
		}
	}

	public void appendList(ItemInstance ii) {
		synchronized (list) {
			if (!list.contains(ii))
				list.add(ii);
		}
	}

	/**
	 * 착용중인 슬롯내에 아이템 리턴.
	 */
	public ItemInstance getSlot(int slot) {
		synchronized (this.slot) {
			return this.slot[slot];
		}
	}

	public void setSlot(int slot, ItemInstance item) {
		synchronized (this.slot) {
			this.slot[slot] = item;
		}
	}
	public ItemInstance findByObjId(long objid) {
		for (ItemInstance item : getList()) {
			if (item.getObjectId() == objid) {
				return item;
			}
		}
		return null;
	}
	/**
	 * 아이템 드랍시 처리되는 메서드.
	 */
	public void toDrop(ItemInstance item, final long count, int x, int y, boolean packet) {
		final int realcount = (int)count;
		
		if (!cha.isInvis() && isRemove(item, realcount, packet, false, false)) {
			// 로그용 변수.
			String item_name = item.toStringDB();
			long item_count = item.getCount();
			long item_objid = item.getObjectId();
			long item_new_objid = 0;
			
			
			if(item_count < 0 || item_count > 2000000000) {
				return;
			}
			if(realcount < 0 || realcount > 2000000000) {
				return;
			}
			
			if (item.getCount() - realcount <= 0) {
				remove(item, true);
			} else {
				count(item, item.getCount() - realcount, true);
				item = ItemDatabase.newInstance(item);
				item.setCount(realcount);
				item_new_objid = item.getObjectId();
			}
			
			// 유저 현금 거래 관련 편지는 거래/드랍 안됨. 
			if (item instanceof Letter) {
				Letter temp = (Letter) item;
				
				if (temp.getFrom() != null && temp.getFrom().equalsIgnoreCase(PcTradeController.PC_TRADE))
					return;
			}
			
			// 월드에 등록.
			item.toTeleport(x, y, cha.getMap(), false);
			// 드랍됫다는거 알리기.
			item.toDrop(cha);

			Log.appendItem(cha, "type|드랍", String.format("item_name|%s", item_name), String.format("name_objid|%d", item_objid), String.format("item_new_objid|%d", item_new_objid),
					String.format("item_count|%d", item_count), String.format("count|%d", count));
			
			// gui 로그
			if (!Common.system_config_console && !(cha instanceof PcRobotInstance) && cha instanceof PcInstance) {
				long time = System.currentTimeMillis();
				String timeString = Util.getLocaleString(time, true);
				String log = String.format("[%s]\t [드랍]\t [캐릭터: %s]\t [아이템: %s]\t %s\t [좌표 %d, %d, %d]", timeString, cha.getName(), Util.getItemNameToString(item, count), Util.getMapName(cha), cha.getX(), cha.getY(), cha.getMap());

				GuiMain.display.asyncExec(new Runnable() {
					public void run() {
						GuiMain.getViewComposite().getGiveComposite().toLog(log);
					}
				});
			}
		}
	}

	/**
	 * 아이템 픽업시 처리하는 메서드.
	 */
	public void toPickup(object o, final long count) {		

		
		// 면류관 픽업은 따로 처리.
		if (o instanceof KingdomCrown) {
			o.toPickup(cha);
			return;
		}
		// 낚시중 일때 픽업 못하게 처리
		if (cha.isFishing()) {
			return;
		}
		final int realcount = (int)count;
		
		if (cha.isBuffAbsoluteBarrier())
			BuffController.remove(cha, AbsoluteBarrier.class);
		
		// 추가 가능여부 확인.
		if (!cha.isInvis() && o instanceof ItemInstance && isAppend((ItemInstance) o, realcount, false)) {
			ItemInstance item = (ItemInstance) o;
			cha.setHeading(Util.calcheading(cha, item.getX(), item.getY()));
			
			// 픽업 모션이 없을경우 줍기 모션 패킷 보내지않음
			if (SpriteFrameDatabase.findGfxMode(cha.getGfx(), Lineage.GFX_MODE_GET))
				cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_GET), true);

			//
			ItemInstance temp = find(item);
			String item_name = item.toStringDB();
			long item_objid = item.getObjectId();
			long item_count = item.getCount();
			long item_new_objid = 0;
			String target_name = temp == null ? null : temp.toStringDB();
			long target_objid = temp == null ? 0 : temp.getObjectId();
			long target_count = temp == null ? 0 : temp.getCount();

			if(item_count < 0 || item_count > 2000000000) {
				return;
			}
			
			if(realcount < 0 || realcount > 2000000000) {
				return;
			}

			PluginController.init(Inventory.class, "toPickup", cha, item, count);

			if (item.getCount() == realcount) {
				// 모두 주울때

				//----- 방식 1
				// 월드에서 제거.
				World.remove(item);
				item.clearList(true);
	
				//
				if (temp != null) {
					// 겹칠 수 있는 아이템이 존재한다면.
					// 수량 증가.
					count(temp, temp.getCount() + item.getCount(), true);
					// 픽업된거 알리기
					temp.toPickup(cha);
					// 메모리 정리
					ItemDatabase.setPool(item);
				} else {
					// 겹칠 수 없는 아이템일 경우.
					// 추가.
					append(item, true);
				}

				//----- 방식 2 (픽업할때 버프적용된 item객체가 날라가는 문제가 생김. 그래서 방식1 사용함.)
				/*
				 * ItemInstance temp = append(item, count); // 양초 때문에 넣은 소스. 픽업후에도 켜짐상태 유지하기 위해. if(temp.isEquipped()){ temp.setEquipped(false); temp.toClick(cha, null); }
				 * 
				 * // 메모리 정리 ItemDatabase.setPool(item);
				 */

			} else {
				// 일부분만 주울때.
				
				return;
			}
			// 파티원들에게 표현하기.
			if (Lineage.party_pickup_item_print && cha instanceof PcInstance) {
				Party p = PartyController.find((PcInstance) cha);
				if (p != null) {
					item_name = item_name.lastIndexOf("(") > 0 ? String.format("%s(%d)", item_name.substring(0, item_name.lastIndexOf("(")), count) : item_name;
					String msg = String.format("%s 획득: %s", item_name, cha.getName());
					p.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_PARTY, msg));
				}
			}
			//
			Log.appendItem(cha, "type|아이템줍기", String.format("item_name|%s", item_name), String.format("item_objid|%d", item_objid), String.format("item_new_objid|%d", item_new_objid),
					String.format("item_count|%d", item_count), String.format("count|%d", count), String.format("target_name|%s", target_name), String.format("target_objid|%d", target_objid),
					String.format("target_count|%d", target_count));
			

			
			// gui 로그
			if (!Common.system_config_console && !(cha instanceof PcRobotInstance) && cha instanceof PcInstance) {
				long time = System.currentTimeMillis();
				String timeString = Util.getLocaleString(time, true);
				String log = String.format("[%s]\t [줍기]\t [캐릭터: %s]\t [아이템: %s]\t %s\t [좌표 %d, %d, %d]", timeString, cha.getName(), Util.getItemNameToString(item, count), Util.getMapName(cha), cha.getX(), cha.getY(), cha.getMap());

				GuiMain.display.asyncExec(new Runnable() {
					public void run() {
						GuiMain.getViewComposite().getGiveComposite().toLog(log);
					}
				});
			}
		}
	}

	/**
	 * 오브젝트아이디로 해당하는 아이템 찾는 함수.
	 * 
	 * @param object_id
	 * @return
	 */
	public ItemInstance value(long object_id) {
		synchronized (list) {
			for (ItemInstance i : list) {
				if (i.getObjectId() == object_id)
					return i;
			}
			return null;
		}
	}

	/**
	 * 인벤토리에 아이템 등록처리하는 함수. : 겹쳐지는 아이템 존재하는지 확인해서 처리도 함.
	 * 참고용으로 사용된 item 객체는 반드시 메모리 해제처리를 고려해야함.
	 * 
	 * @param ii
	 */
	public ItemInstance append(ItemInstance item, long count, Object... opt) {
		int realcount = (int)count;
		
		if(realcount < 0 || realcount > 2000000000) {
			return null;
		}
		String type = null;
		String npc_name = null;
		long npc_objid = 0;
		String item_name = item.toStringDB();
		long item_objid = item.getObjectId();
		String target_name = null;
		long target_objid = 0;
		if (opt != null && opt.length > 0) {
			type = (String) opt[0];
			if (type.equalsIgnoreCase("type|제작지급") && opt[1] != null) {
				object o = (object) opt[1];
				npc_name = o.getName();
				npc_objid = o.getObjectId();
			}
		}
		//
		ItemInstance temp = null;
		if (item.getItem().isPiles())
			// 겹쳐지는 아이템일경우 같은 종류에 아이템이 존재하는지 확인 및 추출.
			temp = find(item);
		if (temp != null) {
			//
			target_name = temp.toStringDB();
			target_objid = temp.getObjectId();
			temp.setInvDolloptionA(item.getInvDolloptionA());
			temp.setInvDolloptionB(item.getInvDolloptionB());
			temp.setInvDolloptionC(item.getInvDolloptionC());
			temp.setInvDolloptionD(item.getInvDolloptionD());
			temp.setInvDolloptionE(item.getInvDolloptionE());
			// 수량 증가.
			count(temp, item, temp.getCount() + realcount, true);
			// 픽업된거 알리기
			temp.toPickup(cha);
			// log
			if (type != null)
				Log.appendItem(cha, type, String.format("npc_name|%s", npc_name), String.format("npc_objid|%d", npc_objid), String.format("item_name|%s", item_name),
						String.format("item_objid|%s", item_objid), String.format("target_name|%s", target_name), String.format("target_objid|%s", target_objid), String.format("count|%d", count));
		} else {
			if (item.getItem().isPiles()) {
				// 객체 생성.
				temp = ItemDatabase.newInstance(item);
				// 갯수 갱신
				temp.setCount(realcount);
				//
				item_name = temp.toStringDB();
				item_objid = temp.getObjectId();
				// 추가.
				append(temp, true);
				// log
				if (type != null)
					Log.appendItem(cha, type, String.format("npc_name|%s", npc_name), String.format("npc_objid|%d", npc_objid), String.format("item_name|%s", item_name),
							String.format("item_objid|%s", item_objid), String.format("target_name|%s", target_name), String.format("target_objid|%s", target_objid), String.format("count|%d", count));
			} else {
				for (int i = 0; i < realcount; ++i) {
					// 객체 생성.
					temp = ItemDatabase.newInstance(item);
					// 갯수 갱신
					temp.setBless(1);
					temp.setDefinite(true);
					temp.setCount(1);
					//
					item_name = temp.toStringDB();
					item_objid = temp.getObjectId();
					// 추가.
					temp.setInvDolloptionA(item.getInvDolloptionA());
					temp.setInvDolloptionB(item.getInvDolloptionB());
					temp.setInvDolloptionC(item.getInvDolloptionC());
					temp.setInvDolloptionD(item.getInvDolloptionD());
					temp.setInvDolloptionE(item.getInvDolloptionE());
					append(temp, true);
					// log
					if (type != null)
						Log.appendItem(cha, type, String.format("npc_name|%s", npc_name), String.format("npc_objid|%d", npc_objid), String.format("item_name|%s", item_name),
								String.format("item_objid|%s", item_objid), String.format("target_name|%s", target_name), String.format("target_objid|%s", target_objid),
								String.format("count|%d", count));
				}
			}
		}

		return temp;
	}

	/**
	 * 아이템 인벤에 추가하는 함수
	 * 
	 * @param item
	 *            : 등록할 아이템
	 * @param packet
	 *            : 패킷 전송 여부
	 */
	public void append(ItemInstance item, boolean packet) {
		if (item != null && item.getCount() >= 0) {		
			appendList(item);
			if (packet && cha != null) {
				if (cha instanceof PcInstance)
					cha.toSender(S_InventoryAdd.clone(BasePacketPooling.getPool(S_InventoryAdd.class), item));
				// 사용자가 월드 접속할때에도 해당 메서드를 사용함. 그렇기 때문에 월드에 접속을 다 완료한후 그러니깐
				// 일반 필드에서 아이템 픽업하거나 할때에만 해당 사항을 수행하도록 유도.
				if (!cha.isWorldDelete()) {
					updateWeight();
					if (cha instanceof PcInstance)
						cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
				}
			}
			// 픽업된거 알리기.
			item.toPickup(cha);
		
		}
	}

	/**
	 * 인벤토리에 있는 아이템 제거.
	 */
	public void remove(ItemInstance item, boolean packet) {
		if (item != null) {
			// 인벤에 제거.
			removeList(item);
			if (packet && cha != null) {
				if (cha instanceof PcInstance) {
					if (item instanceof Arrow) {
						setSlot(Lineage.SLOT_ARROW, null);
						if (item.isEquipped())
							ChattingController.toChatting(cha, String.format("%s 이 없습니다.  ", item.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
					}
					
					cha.toSender(S_InventoryDelete.clone(BasePacketPooling.getPool(S_InventoryDelete.class), item));
				}
				if (!cha.isWorldDelete()) {
					updateWeight();
					if (cha instanceof PcInstance)
						cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
				}
			}
		}
	}

	/**
	 * 해당 아이템 갯수 변환.
	 * 
	 * @param item
	 * @param packet
	 */
	public void count(ItemInstance item, long count, boolean packet) {
		final int realcount = (int)count;
		if(realcount < 0 || realcount > 2000000000) {
			return;
		}
		if (item == null)
			return;
		if (KingdomController.isKingdomWarRemoveItem(cha, item))
			return;
		item.setCount(realcount);
		if (count <= 0) {
			remove(item, packet);
			ItemDatabase.setPool(item);
		} else {
			if (packet && cha != null && !cha.isWorldDelete()) {		
				updateWeight();
				if (cha instanceof PcInstance) {
					if (Lineage.server_version <= 144) {
						cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), item));
						cha.toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), item));
					} else {
						cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
					}
					cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
				}
			}
		}
	}

	/**
	 * 아이템을 제거하기전 조건검색하는 메서드.
	 */
	public boolean isRemove(ItemInstance item, long count, boolean packet, boolean dustbin, boolean dwarf) {
		int realcount = (int)count;
		if(realcount < 0 || realcount > 2000000000) {
			return false;
		}
		
		if (item == null || item.getCount() < count || count <= 0 || item.getItem() == null)
			return false;

		if (item.isEquipped()) {
			// 양초 등잔 랜턴 화살은 무시하기.
			if (!(item instanceof Candle) && !(item instanceof Arrow)) {
				if (packet && cha != null && cha instanceof PcInstance)
					// \f1착용하고 있는 것을 버릴수 없습니다.
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 125));
				return false;
			}
		}
		if (!dustbin && (!item.getItem().isDrop() || !item.getItem().isTrade())) {
			if (packet && cha != null && cha instanceof PcInstance)
				// \f1%0%d 버리거나 남에게 줄 수 없습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 210, item.toString()));
			return false;
		}
		// 펫목걸이 확인처리 구간.
		if (item instanceof DogCollar) {
			DogCollar dc = (DogCollar) item;
			// 현재 펫이 스폰된 상태일경우 무시하기.
			if (dc.isPetSpawn()) {
				// \f1%0%d 버리거나 남에게 줄 수 없습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 210, item.toString()));
				return false;
			}
		}
		// 봉인된거 무시.
		if (!dwarf && item.getBless() < 0)
			return false;
		return true;
	}
	
	/**
	 * 아이템을 제거하기전 조건검색하는 메서드.
	 */
	public boolean isTradeRemove(ItemInstance item, long count, boolean packet, boolean dustbin, boolean dwarf) {
		if (item == null || item.getCount() < count || count <= 0 || item.getItem() == null)
			return false;

		if (item.isEquipped()) {
			// 양초 등잔 랜턴 화살은 무시하기.
			if (!(item instanceof Candle) && !(item instanceof Arrow)) {
				if (packet && cha != null && cha instanceof PcInstance)
					// \f1착용하고 있는 것을 버릴수 없습니다.
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 125));
				return false;
			}
		}
		if (!dustbin && !item.getItem().isTrade()) {
			if (packet && cha != null && cha instanceof PcInstance)
				// \f1%0%d 버리거나 남에게 줄 수 없습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 210, item.toString()));
			return false;
		}
		// 펫목걸이 확인처리 구간.
		if (item instanceof DogCollar) {
			DogCollar dc = (DogCollar) item;
			// 현재 펫이 스폰된 상태일경우 무시하기.
			if (dc.isPetSpawn()) {
				// \f1%0%d 버리거나 남에게 줄 수 없습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 210, item.toString()));
				return false;
			}
		}
		// 봉인된거 무시.
		if (!dwarf && item.getBless() < 0)
			return false;
		return true;
	}

	/**
	 * 아이템을 등록하기전에 해당 아이템을 추가가 가능한지 확인하는 메서드. 패킷 처리도 함께함.
	 */
	public boolean isAppend(Item item, long count, long addCount) {
	   final int realCount = (int) count;

	    // Validate count range
	    if (realCount <= 0 || realCount > 2000000000) {
	        return false;
	    }

	    // Check inventory and weight limits
	    if (exceedsInventoryLimit(addCount) && handleInventoryLimitExceeded()) {
	        return false;
	    }

	    if (item != null && exceedsWeightLimit(item, realCount) && handleWeightLimitExceeded()) {
	        return false;
	    }

	    return true;
	}

	private boolean exceedsInventoryLimit(long addCount) {
	    return list.size() + addCount > Lineage.inventory_max;
	}

	private boolean handleInventoryLimitExceeded() {
	    if (cha instanceof PcInstance) {
	        // \f1한 캐릭터가 들고 다닐 수 있는 아이템의 최대 가짓수는 180개입니다.
	        cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 263));
	        return true;
	    } else if (cha instanceof MonsterInstance) {
	        // If a monster, remove the last registered item
	        removeLastItem();
	        return true;
	    }
	    return false;
	}

	private boolean exceedsWeightLimit(Item item, int realCount) {
	    return cha instanceof PcInstance && !isWeight(item.getWeight() * realCount);
	}

	private boolean handleWeightLimitExceeded() {
	    if (cha instanceof PcInstance) {
	        // 소지품이 너무 무거워서 더 들 수 없습니다.
	        cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 82));
	        return true;
	    }
	    return false;
	}

	private void removeLastItem() {
	    ItemInstance lastItem = getList(list.size() - 1);
	    if (lastItem != null) {
	        removeList(lastItem);
	        ItemDatabase.setPool(lastItem);
	    }
	}
	
	/**
	 * 아이템을 등록하기전에 해당 아이템을 추가가 가능한지 확인하는 메서드. 패킷 처리도 함께함.
	 */
	public boolean isAppend(Item item, long count, long addCount, boolean packet) {
		//쿠베라 버그 수정
		final int realcount = (int)count;
		if(realcount < 0 || realcount > 2000000000) {
			return false;
		}
		if (realcount <= 0)
			return false;
		if (list.size() + addCount > Lineage.inventory_max) {
			if (cha instanceof PcInstance) {
				// \f1한 캐릭터가 들고 다닐 수 있는 아이템의 최대 가짓수는 180개입니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 263));
				return false;
			} else if (cha instanceof MonsterInstance) {
				// 몬스터라면 마지막에 등록한 아이템 제거하기.
				ItemInstance ii = getList(list.size() - 1);
				if (ii != null) {
					removeList(ii);
					ItemDatabase.setPool(ii);
				}
			}
		}
		if (item != null && !isWeight(item.getWeight() * realcount)) {
			// 사용자만 무게 체크하기.
			if (cha instanceof PcInstance) {
				// 소지품이 너무 무거워서 더 들 수 없습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 82));
				return false;
			}
		}
		return true;
	}

	/**
	 * 아이템을 등록하기전에 해당 아이템을 추가가 가능한지 확인하는 메서드. 패킷 처리도 함께함.
	 */
	public boolean isAppend(ItemInstance item, long count, boolean shop) {
		final int realcount = (int)count;
		if(realcount < 0 || realcount > 2000000000) {
			return false;
		}
		if (item == null || item.getItem() == null)
			return false;
		if (!shop && item.getCount() < realcount) {
			// 갯수가 잘못 됫을때.
			return false;
		}
		return isAppend(item.getItem(), realcount, item.getItem().isPiles() ? 1 : realcount);
	}
	
	/**
	 * 교환 전용 체크 메소드.
	 * 거래 완료 또는 취소시 무게가 넘쳐도 인벤에 등록하는 메소드.
	 * 2018-08-03
	 * by connector12@nate.com
	 */
	public boolean isAppendTrade(ItemInstance item, long count) {
		final int realcount = (int)count;
		if(realcount < 0 || realcount > 2000000000) {
			return false;
		}
		if (item != null && item.getCount() < realcount)
			// 갯수가 잘못 됫을때.
			return false;
		
		return true;
	}

	/**
	 * 인벤토리에 최대허용할수 있는 무게를 확인하는 메서드.
	 */
	public boolean isWeight(double weight) {
		weight += this.weight;
		int percent = Lineage.server_version >= 270 ? 238 : 29;
		return (weight / getMaxWeight()) * percent <= percent;
	}

	/**
	 * 백분율로 잡고 그값을 리턴함.
	 */
	public double getWeightPercent() {
		updateWeight();
		double percent = Lineage.server_version >= 270 ? 238 : 29;
		double p = (weight / getMaxWeight()) * percent;	
		
		if (p > percent)
			p = percent;
		return p;
	}
	
	public double getNowWeight() {
		return weight;
	}

	/**
	 * 현재 인벤토리무게가 원하는 퍼센트 범위안에 있는지 확인후 리턴함.
	 * 
	 * @param percent
	 *            : 퍼센트값. 100분율
	 * @return
	 */
	public boolean isWeightPercent(int percent) {
		int a = (int) Math.floor((getWeightPercent() / (Lineage.server_version >= 270 ? 240D : 30D)) * 100D);
		return a <= percent;
	}

	/**
	 * 적용된 셋트옵션 리턴처리 함수.
	 * 
	 * @return
	 */
	public List<ItemSetoption> getSetitemList() {
		synchronized (setitem_list) {
			return new ArrayList<ItemSetoption>(setitem_list);
		}
	}

	public void appendSetoption(ItemSetoption is) {
		synchronized (setitem_list) {
			setitem_list.add(is);
		}
	}

	public void removeSetoption(ItemSetoption is) {
		synchronized (setitem_list) {
			setitem_list.remove(is);
		}
	}

	public boolean isSetoption(ItemSetoption is) {
		synchronized (setitem_list) {
			for (ItemSetoption i : setitem_list) {
				if (i.getUid() == is.getUid())
					return true;
			}
			return false;
		}
	}

	/**
	 * 현재 소유하고있는 아이템들의 전체 무게를 갱신한다.
	 */
	private void updateWeight() {
		synchronized (list) {
			weight = 0;
			for (ItemInstance item : list)
				weight += item.getWeight();
		}
	}
	
	/**
	 * 서먼, 펫용.
	 * 현재 무게에 아이템을 추가 할 수 있는지 여부 체크.
	 * 2018-09-12
	 * by connector12@nate.com 
	 */
	public boolean isAppendItem(ItemInstance item, double count) {
		updateWeight();
		
		if (cha.getLevel() * 25 < weight + (item.getItem().getWeight() * count))
			return false;
		
		return true;
	}

	/**
	 * 아이템을 들수있는 최대값을 추출.
	 */
	public double getMaxWeight() {
		double max_weight = 0;
		if (cha != null) {
			if (cha instanceof PcInstance) {
				max_weight = getClassMaxWeight(cha) + CharacterController.toStatStr(cha, "getMaxWeight") + CharacterController.toStatCon(cha, "getMaxWeight") + cha.getItemWeight();
			} else if (cha instanceof NpcInstance)
				max_weight = 2000;
			// 디크리즈웨이트 마법에 따른 최대소지무게 연산
			if (cha.isBuffDecreaseWeight())
				max_weight += 180;
			// 버그베어 마법인형 최대소지무게+500
			if (cha.isMagicdollBugBear())
				max_weight += 500;
			// 설정파일에 대한 최대무게 연산
			max_weight += Lineage.inventory_weight_max;
		}
			
		return max_weight;
	}
	
	// 클래스별 최대 소지 무게
	public double getClassMaxWeight(Character cha) {
		double max_weight = 0;
		
		switch (cha.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			max_weight += 2200;
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			max_weight += 2600;
			break;
		case Lineage.LINEAGE_CLASS_DARKELF:
			max_weight += 2300;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			max_weight += 2100;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			max_weight += 2000;
			break;
		}		
		
		return max_weight;
	}

	/**
	 * 객체와 같은 클레스 찾아서 리턴.
	 * 
	 * @param c
	 * @return
	 */
	public ItemInstance find(Class<?> c) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getClass().equals(c))
					return item;
			}
			return null;
		}
	}

	/**
	 * 겹쳐지는 아이템 찾기. : 중복코드 방지용.
	 * 
	 * @param ii
	 * @return
	 */
	public ItemInstance find(ItemInstance ii) {
		if (ii instanceof RaceTicket) {
			RaceTicket ticket = (RaceTicket) ii;
			return findRaceTicket(ticket.getRaceUid(), ticket.getRacerIdx(), ticket.getRacerType());
		}
		// 파워볼 쿠폰: 회차·금액(itemTimek)이 달라도 겹침 DB가 true였을 때 합쳐지지 않도록 항상 별도 슬롯
		if (ii != null && ii.getItem() != null && isPowerballCouponName(ii.getItem().getName()))
			return null;
		return find(ii.getItem().getName(), ii.getBless(), ii.getItem().isPiles());
	}

	/** 홀/짝/언더/오버 쿠폰 — 겹치지 않는 1장 단위 */
	private static boolean isPowerballCouponName(String name) {
		if (name == null)
			return false;
		return "홀 쿠폰".equals(name) || "짝 쿠폰".equals(name) || "언더 쿠폰".equals(name) || "오버 쿠폰".equals(name);
	}

	/**
	 * 해당 객체와 같은 아이템 찾아서 리턴.
	 * 
	 * @param i
	 * @return
	 */
	public ItemInstance find(Item i) {
		if (i == null)
			return null;
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getItem().getName().equalsIgnoreCase(i.getName()))
					return item;
			}
			return null;
		}
	}

	/**
	 * 디비에 이름을 가진 아이템이 존재하는지 확인하여 리턴하는 함수.
	 * 
	 * @param name
	 * @return
	 */
	public ItemInstance find(String name, boolean piles) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getItem().getName().equalsIgnoreCase(name)) {
					if (piles) {
						if (item.getItem().isPiles())
							return item;
					} else {
						return item.getItem().isPiles() ? item : null;
					}
				}
			}
			return null;
		}
	}
	
	public ItemInstance find(String name) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getItem().getName().equalsIgnoreCase(name))
					return item;
			}
			return null;
		}
	}

	/**
	 * 겹쳐지는 아이템이 존재하는지 체크
	 */
	public ItemInstance find(String name, int bless, boolean piles) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getItem().getName().equalsIgnoreCase(name) && item.getBless() == bless) {
					if (piles) {
						if (item.getItem().isPiles())
							return item;
					} else {
						return item.getItem().isPiles() ? item : null;
					}
				}
			}
			return null;
		}
	}

	/**
	 * 아이템이 존재하는지 체크
	 */
	public ItemInstance find(String name, int enLev, int bress) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getItem().getName().equalsIgnoreCase(name) && item.getBless() == bress && item.getEnLevel() == enLev)
					return item;
			}
			return null;
		}
	}
	
	/**
	 * 오브젝트 아이디로 해당 아이템 검색.
	 * 2019-08-01
	 * by connector12@nate.com
	 */
	public ItemInstance find(long objId) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getObjectId() == objId)
					return item;
			}
			return null;
		}
	}

	/**
	 * 인벤토리 목록에서 화살을 찾은후 리턴하는 메서드.
	 */
	public ItemInstance findArrow() {
		ItemInstance arrow = getSlot(Lineage.SLOT_ARROW);

		if (arrow != null && arrow.getObjectId() > 0)
			return arrow;

		for (ItemInstance item : getList())
			if (item instanceof Arrow)
				return item;

		return null;
	}

	/**
	 * 인벤토리 목록에서 스팅을 찾아 리턴하는 함수.
	 * 
	 * @return
	 */
	public ItemInstance findThrowingKnife() {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item instanceof ThrowingKnife)
					return item;
			}
			return null;
		}
	}

	/**
	 * 아데나를 찾아서 리턴하는 메서드.
	 */
	public ItemInstance findAden() {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item instanceof Aden)
					return item;
			}
			return null;
		}
	}

	/**
	 * 네임아이디넘버로 일치하는 아이템 찾기.
	 * 
	 * @param name_id
	 * @return
	 */
	public ItemInstance findDbNameId(int name_id) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getItem().getNameIdNumber() == name_id)
					return item;
			}
			return null;
		}
	}

	public void findDbNameId(int name_id, List<ItemInstance> r_list) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getItem().getNameIdNumber() == name_id)
					r_list.add(item);
			}
		}
	}
	
	public void cratfFindDbName(String name, List<ItemInstance> r_list) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getItem().getName().equalsIgnoreCase(name))
					r_list.add(item);
			}
		}
	}

	public void findClass(Class<?> c, List<ItemInstance> r_list) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getClass().equals(c))
					r_list.add(item);
			}
		}
	}

	/**
	 * 이름이 동일한 아이템 찾아서 list에 넣음.
	 * 
	 * @param name
	 * @param list
	 */
	public void findDbName(String name, List<ItemInstance> r_list) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getItem().getName().equalsIgnoreCase(name))
					r_list.add(item);
			}
		}
	}

	/**
	 * 밝기 값이 존재하는 아이템들 찾아서 리턴.
	 * 
	 * @param list
	 */
	public void findLighter(List<ItemInstance> r_list) {
		synchronized (list) {
			for (ItemInstance item : list) {
				if (item.getLight() > 0)
					r_list.add(item);
			}
		}
	}

	/**
	 * 슬라임 레이스티켓 같은거 찾아서 리턴.
	 * 
	 * @param item
	 * @return
	 */
	public RaceTicket findRaceTicket(int uid, int idx, String type) {
		synchronized (list) {
			for (ItemInstance ii : list) {
				if (ii instanceof RaceTicket) {
					RaceTicket ticket = (RaceTicket) ii;
					if (ticket.getRaceUid() == uid && ticket.getRacerIdx() == idx && ticket.getRacerType().equalsIgnoreCase(type))
						return ticket;
				}
			}
			return null;
		}
	}

	/**
	 * 슬라임 레이스티켓 같은거 찾아서 리턴.
	 * 
	 * @param item
	 * @return
	 */
	public void findRaceTicket(int uid, int idx, String type, List<ItemInstance> r_list) {
		synchronized (list) {
			for (ItemInstance ii : list) {
				if (ii instanceof RaceTicket) {
					RaceTicket ticket = (RaceTicket) ii;
					if (ticket.getRaceUid() == uid && ticket.getRacerIdx() == idx && ticket.getRacerType().equalsIgnoreCase(type))
						r_list.add(ticket);
				}
			}
		}
	}

	/**
	 * 인벤토리에 있는 아이템목록중 원하는 인첸트와 일치하는 아이템갯수 리턴.
	 * 
	 * @param en
	 * @param isWeapon
	 * @return
	 */
	public int getEnchantCount(int en, boolean isWeapon) {
		synchronized (list) {
			int cnt = 0;
			for (ItemInstance ii : list) {
				if (isWeapon && ii instanceof ItemWeaponInstance && ii.getEnLevel() == en)
					cnt += 1;
				if (!isWeapon && ii instanceof ItemArmorInstance && ii.getEnLevel() == en)
					cnt += 1;
			}
			return cnt;
		}
	}

	/**
	 * 인벤토리에서 미스릴의 수량을 확인하는 메서드.
	 * 
	 * @param count
	 * @param remove
	 * @return
	 */
	public boolean isMeterial(long count, boolean remove) {
		if (count <= 0)
			return true;

		ItemInstance meterial = findDbNameId(767);
		if (meterial != null) {
			if (meterial.getCount() >= count) {
				if (remove)
					count(meterial, meterial.getCount() - count, true);
				return true;
			}
		}

		return false;
	}

	/**
	 * 인벤토리에 아데나의 수량을 확인하는 메서드.
	 */
	public boolean isAden(long count, boolean remove) {
		int realcount = (int)count;
		if(realcount < 0 || realcount > 2000000000) {
			return false;
		}
		return isAden("아데나", realcount, remove);
	}

	/**
	 * 이름과 일치하는 아이템을 찾은후 그아이템에 갯수가 확인하려는 갯수만큼 존재하는지 확인하는 함수. : 변수값에 따라 처리함. : 아덴 이름이 다를 수 도 있기때문에 name매개변수 사용함.
	 * 
	 * @param name
	 * @param count
	 * @param remove
	 * @return
	 */
	public boolean isAden(String name, long count, boolean remove) {
		// 버그방지.
		if (name == null)
			return false;
		
		int realcount = (int)count;
		if(realcount < 0 || realcount > 2000000000) {
			return false;
		}
		
		if (count <= 0)
			return true;
		
		Object o = PluginController.init(Inventory.class, "isAden", cha, name, count, remove);
		if (o != null)
			return (Boolean) o;

		ItemInstance aden = find(name, true);
		if (aden != null) {
			if (aden.getCount() >= count) {
				if (remove) {
					//
					String item_name = aden.toStringDB();
					long item_objid = aden.getObjectId();
					long item_count = aden.getCount();
					//
					count(aden, aden.getCount() - realcount, true);
					//

					Log.appendItem(cha, "type|isAden", String.format("item_name|%s", item_name), String.format("name_objid|%d", item_objid), String.format("item_count|%d", item_count),
							String.format("count|%d", count));
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * 소환 조종반지 착용여부 리턴
	 */
	public boolean isRingOfSummonControl() {
		ItemInstance r1 = getSlot(Lineage.SLOT_RING_LEFT);
		ItemInstance r2 = getSlot(Lineage.SLOT_RING_RIGHT);
		return (r1 != null && r1 instanceof RingSummonControl) || (r2 != null && r2 instanceof RingSummonControl);
	}

	/**
	 * 변신 조종반지 착용여부 리턴
	 */
	public boolean isRingOfPolymorphControl() {
		ItemInstance r1 = getSlot(Lineage.SLOT_RING_LEFT);
		ItemInstance r2 = getSlot(Lineage.SLOT_RING_RIGHT);
		if ((r1 != null && r1.getItem().getName().equalsIgnoreCase("변신 조종 반지")) || (r2 != null && r2.getItem().getName().equalsIgnoreCase("변신 조종 반지"))) {
			return true;
		}
		return false;
	}

	/**
	 * 순간이동 조종반지 착용여부 리턴
	 */
	public boolean isRingOfTeleportControl() {
		ItemInstance r1 = getSlot(Lineage.SLOT_RING_LEFT);
		ItemInstance r2 = getSlot(Lineage.SLOT_RING_RIGHT);
		return (r1 != null && r1 instanceof RingTeleportControl) || (r2 != null && r2 instanceof RingTeleportControl);
	}
	public boolean isRingOfTeleportControl2() {
		ItemInstance r1 = getSlot(Lineage.SLOT_RING_LEFT);
		ItemInstance r2 = getSlot(Lineage.SLOT_RING_RIGHT);
		if ((r1 != null && r1.getItem().getName().equalsIgnoreCase("순간이동 지배 반지")) || (r2 != null && r2.getItem().getName().equalsIgnoreCase("순간이동 지배 반지"))) {
			return true;
		}
		return false;
	}

	/**
	 * 수중에서 숨쉴수 있는 아이템 착용중인지 확인해주는 함수.
	 * 
	 * @return
	 */
	public boolean isAquaEquipped() {
		for (int slot = Lineage.SLOT_HELM; slot < Lineage.SLOT_NONE; ++slot) {
			ItemInstance ii = getSlot(slot);
			if (ii != null && ii.getItem().isAqua())
				return true;
		}
		return false;
	}

	/**
	 * 착용중인 아이템들중 셋트아이템옵션에서 헤이스트가 적용된게 있는지 확인해주는 함수.
	 * 
	 * @return
	 */
	public boolean isSetOptionHaste() {
		synchronized (setitem_list) {
			for (ItemSetoption is : setitem_list) {
				if (is.isHaste())
					return true;
			}
			return false;
		}
	}

	/**
	 * 착용중인 아이템들중 셋트아이템옵션에서 2단가속(용기)이 적용된게 있는지 확인해주는 함수.
	 * 
	 * @return
	 */
	public boolean isSetOptionBrave() {
		synchronized (setitem_list) {
			for (ItemSetoption is : setitem_list) {
				if (is.isBrave())
					return true;
			}
			return false;
		}
	}
	
	/**
	 * 인벤토리의 아데나를 체크하여 20억 이상일 경우 1억아데나 생성.
	 * 2019-06-27
	 * by connector12@nate.com
	 */
	public void checkAden(ItemInstance aden) {
		if (aden != null && aden.getItem() != null && (aden.getItem().getName().equalsIgnoreCase("아데나") || aden.getItem().getNameId().equalsIgnoreCase("$4")) && aden.getCount() > Common.MAX_COUNT) {
			if (aden.getCount() - Common.MAX_COUNT >= Common.ONE_HUNDRED_MILLION) {
				int count = (int) ((aden.getCount() - Common.MAX_COUNT) / Common.ONE_HUNDRED_MILLION);
				
				if (count > 0) {
					Item i = ItemDatabase.find("1억 아데나");
					
					if (i != null) {
						for (int idx = 0; idx < count; idx++) {
							ItemInstance temp = find(i.getName(), 1, i.isPiles());

							if (temp != null && (temp.getBless() != 1))
								temp = null;

							if (temp == null) {
								// 겹칠수 있는 아이템이 존재하지 않을경우.
								if (i.isPiles()) {
									temp = ItemDatabase.newInstance(i);
									temp.setObjectId(ServerDatabase.nextItemObjId());
									temp.setBless(1);
									temp.setEnLevel(0);
									temp.setCount(1);
									temp.setDefinite(true);
									append(temp, true);
								}
							} else {
								// 겹치는 아이템이 존재할 경우.
								count(temp, temp.getCount() + 1, true);
							}

							count(aden, aden.getCount() - Common.ONE_HUNDRED_MILLION, true);
						}
						// 알림.
						ChattingController.toChatting(cha, String.format("[20억 아데나 초과 소지] %s(%d) 획득.", i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
					}
				}
			}
		}
	}
	
	public EnchantRecovery is인첸트복구주문서(PcInstance pc, long objId) {
		if (this != null) {
			for (ItemInstance i : getList()) {
				if (i != null && i.getObjectId() == objId && i instanceof EnchantRecovery) {
					return (EnchantRecovery) i;
				}
			}
		}
		return null;
	}
	public kuberaitem.RandomDollOption is부여주문서(PcInstance pc, long objId) {
		if (this != null) {
			for (ItemInstance i : getList()) {
				if (i != null && i.getObjectId() == objId && i instanceof kuberaitem.RandomDollOption) {
					return (kuberaitem.RandomDollOption) i;
				}
			}
		}
		return null;
	}
	public kuberaitem.ItemChange is아이템변경주문서(PcInstance pc, long objId) {
		if (this != null) {
			for (ItemInstance i : getList()) {
				if (i != null && i.getObjectId() == objId && i instanceof kuberaitem.ItemChange) {
					return (kuberaitem.ItemChange) i;
				}
			}
		}
		return null;
	}
	public ClassChangeTicket is클래스변경주문서(PcInstance pc, long objId) {
		if (this != null) {
			for (ItemInstance i : getList()) {
				if (i != null && i.getObjectId() == objId && i instanceof ClassChangeTicket) {
					return (ClassChangeTicket) i;
				}
			}
		}
		return null;
	}
	
	/**
	 * 자동 사냥시 인벤토리 갯수, 무게 확인
	 */
	public boolean isAutoHuntInventory() {
		if (list.size() >= Lineage.inventory_max) {
			ChattingController.toChatting(cha, String.format("             \\fY인벤토리 자리가 꽉찼습니다. (%d칸) ", Lineage.inventory_max), Lineage.CHATTING_MODE_MESSAGE);

			
			return false;
		}
		
		if (!isWeightPercent(82)) {
			ChattingController.toChatting(cha, "             \\fY공격 가능한 무게를 초과하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
			

			return false;
		}
		
		return true;
	}
	public HuntingZoneTeleportationBook istellbook(PcInstance pc, long objId) {
		if (this != null) {
			for (ItemInstance i : getList()) {
				if (i != null && i.getObjectId() == objId && i instanceof HuntingZoneTeleportationBook) {
					return (HuntingZoneTeleportationBook) i;
				}
			}
		}
		return null;
	}
	public boolean 활장착여부() {		
		return getSlot(Lineage.SLOT_WEAPON) == null ? false : getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("bow");
	}
}
