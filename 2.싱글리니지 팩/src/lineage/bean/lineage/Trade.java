package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item_add_log;
import lineage.database.CharactersDatabase;
import lineage.database.ItemDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_TradeAddItem;
import lineage.network.packet.server.S_TradeStatus;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Trade {
	private Character pc;
	private Character use;
	private List<ItemInstance> pc_list;
	private List<ItemInstance> use_list;
	private boolean pc_ok;
	private boolean use_ok;

	public Trade(){
		pc_list = new ArrayList<ItemInstance>();
		use_list = new ArrayList<ItemInstance>();
	}

	public void close() {
		pc = use = null;
		pc_ok = use_ok = false;
		pc_list.clear();
		use_list.clear();
	}
	
	/**
	 * 
	 * @param cha
	 * @param item
	 * @param count
	 * @return
	 */
	synchronized private boolean isAppend(Character cha, ItemInstance item, long count) {
		// 인벤토리에서 제거 가능한지 확인.
		if(cha.getInventory().isTradeRemove(item, count, true, false, false) == false)
			return false;
		// pc인지 use인지 체크.
		boolean isPc = pc.getObjectId()==cha.getObjectId();
		// 무게 체크.
		if(isPc) {
			if(use.getInventory().isWeight( Math.round(item.getItem().getWeight()*count) ) == false) {
				ChattingController.toChatting(pc, "상대 소지품이 무거워 더는 등록할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
		} else {
			if(pc.getInventory().isWeight( Math.round(item.getItem().getWeight()*count) ) == false) {
				ChattingController.toChatting(use, "상대 소지품이 무거워 더는 등록할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
		}
		return true;
	}

	/**
	 * 거래중인 물품에서 아데나 확인하는 함수.
	 *  : BuffRobotInstance.toTimer(long time) 에서 사용중.
	 * @param pc
	 * @param count
	 * @return
	 */
	synchronized public boolean isAden(boolean pc, long count){
		ItemInstance aden = null;
		if(pc){
			for(ItemInstance ii : pc_list){
				if(ii.getItem().getNameIdNumber()==4)
					aden = ii;
			}
		}else{
			for(ItemInstance ii : use_list){
				if(ii.getItem().getNameIdNumber()==4)
					aden = ii;
			}
		}

		return aden!=null && aden.getCount()==count;
	}
	
	public List<ItemInstance> getPcList() {
		return pc_list;
	}
	
	public List<ItemInstance> getUseList() {
		return use_list;
	}

	public Character getPc() {
		return pc;
	}

	public void setPc(Character pc) {
		this.pc = pc;
	}

	public Character getUse() {
		return use;
	}

	public void setUse(Character use) {
		this.use = use;
	}

	/**
	 * 사용자가 거래중 ok를 누르면 호출 되는 메서드.
	 * 거래중인 사용자들이 전부 ok를 누른 상태인지 확인함.
	 */
	public boolean isTrade(){
		return pc_ok && use_ok;
	}

	public void setOk(Character pc){
		if(pc.getObjectId()==this.pc.getObjectId())
			pc_ok = true;
		else
			use_ok = true;
	}

	/**
	 * 거래에 정보를 담는 bean객체에 등록된 승인자 및 요청자의 객체 정보와
	 * 매개변수 객체정보가 일치하는지 확인하는 메서드.
	 */
	public boolean isTrade(Character pc){
		return pc.getObjectId()==this.pc.getObjectId() || pc.getObjectId()==use.getObjectId();
	}

	synchronized public void append(Character pc, ItemInstance item, final long count) {
		// 등록가능한지 확인.
		if(isAppend(pc, item, count)) {
			//
			String item_name = item.toStringDB();
			long item_objid = item.getObjectId();
			String new_name = "";
			long new_objid = 0;
			//
			if(item.getCount()-count<=0){
				// 모두 꺼내는거라면 인벤에서 제거
				pc.getInventory().remove(item, true);
				// 모두 넘기는 아이템에 한해서만 버프가 지정되잇는 것이므로 여기에만 넣음.
				item.toDrop(pc);
			}else{
				// 일부분만 꺼내는거라면 수량변경하고 객체 새로 생성.
				pc.getInventory().count(item, item.getCount()-count, true);
				item = ItemDatabase.newInstance(item);
				item.setCount(count);
				new_name = item.toStringDB();
				new_objid = item.getObjectId();
			}

			// 교환창에 등록.
			if(pc.getObjectId()==this.pc.getObjectId()){
				pc_list.add(item);
				this.pc.toSender(S_TradeAddItem.clone(BasePacketPooling.getPool(S_TradeAddItem.class), item, true));
				use.toSender(S_TradeAddItem.clone(BasePacketPooling.getPool(S_TradeAddItem.class), item, false));
			}else{
				use_list.add(item);
				this.pc.toSender(S_TradeAddItem.clone(BasePacketPooling.getPool(S_TradeAddItem.class), item, false));
				use.toSender(S_TradeAddItem.clone(BasePacketPooling.getPool(S_TradeAddItem.class), item, true));
			}

			// ㅇㅋ 누른거 초기화.
			pc_ok = use_ok = false;
			// log
			Log.appendItem(pc, "type|거래아이템등록", String.format("item_name|%s", item_name), String.format("item_objid|%d", item_objid), String.format("count|%d", count), String.format("new_name|%s", new_name), String.format("new_objid|%d", new_objid));
		}
	}

	/**
	 * 거래가 취소될때 호출해서 사용하는 메서드.
	 * 사용자가 거래중 월드를 나갈때도 호출됨.
	 * 처리는 똑같으니..
	 */
	synchronized public void toCancel(){
		// 아이템 다시 인벤으로 옴기기.
		for( ItemInstance item : pc_list ){
			if(item.getItem()==null)
				continue;

			//
			String item_name = item.toStringDB();
			long item_objid = item.getObjectId();
			String target_name = null;
			long target_objid = 0;
			boolean isDrop = false;
			// 추가가능한지 확인함.
			if(pc.getInventory().isAppendTrade(item, item.getCount())) {
				ItemInstance temp = pc.getInventory().find(item);
				if(temp!=null){
					//
					target_name = temp.toStringDB();
					target_objid = temp.getObjectId();
					//
					pc.getInventory().count(temp, temp.getCount()+item.getCount(), true);
					ItemDatabase.setPool(item);
				}else{
					//
					pc.getInventory().append( item, true );
				}
			}else{
				// 등록이 불가능할때 땅에 드랍하기.
				item.toTeleport(pc.getX(), pc.getY(), pc.getMap(), false);
//				isDrop = true;
			}
			// log
			Log.appendItem(pc, "type|거래취소", String.format("item_name|%s", item_name), String.format("item_objid|%d", item_objid), String.format("target_name|%s", target_name), String.format("target_objid|%d", target_objid), String.format("drop|%s", Boolean.valueOf(isDrop)));
		}
		for( ItemInstance item : use_list ){
			if(item.getItem()==null)
				continue;

			//
			String item_name = item.toStringDB();
			long item_objid = item.getObjectId();
			String target_name = null;
			long target_objid = 0;
			boolean isDrop = false;
			//
			if(use.getInventory().isAppendTrade(item, item.getCount())){
				ItemInstance temp = use.getInventory().find(item);
				if(temp!=null){
					//
					target_name = temp.toStringDB();
					target_objid = temp.getObjectId();
					//
					use.getInventory().count(temp, temp.getCount()+item.getCount(), true);
					ItemDatabase.setPool(item);
				}else{
					//
					use.getInventory().append( item, true );
				}
			}else{
				item.toTeleport(use.getX(), use.getY(), use.getMap(), false);
				isDrop = true;
			}
			// log
			Log.appendItem(use, "type|거래취소", String.format("item_name|%s", item_name), String.format("item_objid|%d", item_objid), String.format("target_name|%s", target_name), String.format("target_objid|%d", target_objid), String.format("drop|%s", Boolean.valueOf(isDrop)));
		}
		// 교환창 닫기.
		pc.toSender(S_TradeStatus.clone(BasePacketPooling.getPool(S_TradeStatus.class), false));
		use.toSender(S_TradeStatus.clone(BasePacketPooling.getPool(S_TradeStatus.class), false));
		// 거래 취소된거 알리기.
		pc.toTradeCancel(use);
		use.toTradeCancel(pc);
	}

	/**
	 * 요청자와 승인자가 모두 승인을 햇을경우 호출되서
	 * 아이템을 서로 교환하도록 처리하는 메서드.
	 */
	synchronized public void toOk(){
		// 아이템 이동
		for( ItemInstance item : pc_list ){
			if(item.getItem()==null)
				continue;

			//
			String use_name = pc.getName();
			long use_objid = pc.getObjectId();
			String item_name = item.toStringDB();
			long item_objid = item.getObjectId();
			String target_name = null;
			long target_objid = 0;
			boolean isDrop = false;
			String itemName = Util.getItemNameToString(item, item.getCount());
			//
			if(use.getInventory().isAppendTrade(item, item.getCount())){
				ItemInstance temp = use.getInventory().find(item);
				if(temp!=null){
					//
					target_name = temp.toStringDB();
					target_objid = temp.getObjectId();
					//
					use.getInventory().count(temp, temp.getCount()+item.getCount(), true);
					ItemDatabase.setPool(item);
					
					
				}else{
					//
					use.getInventory().append( item, true );
					

				}
			}else{
				item.toTeleport(use.getX(), use.getY(), use.getMap(), false);
				isDrop = true;
			}
			// log
			Log.appendItem(use, "type|거래완료", String.format("use_name|%s", use_name), String.format("use_objid|%d", use_objid), String.format("item_name|%s", item_name), String.format("item_objid|%d", item_objid), String.format("target_name|%s", target_name), String.format("target_objid|%d", target_objid), String.format("drop|%s", Boolean.valueOf(isDrop)));
			
			
			
			if (!Common.system_config_console) {
				long time = System.currentTimeMillis();
				String timeString = Util.getLocaleString(time, true);
				String log = String.format("[%s]\t [%s->%s]\t [아이템: %s]\t %s", timeString, pc.getName(), use.getName(), itemName, use_list == null || use_list.size() < 1 ? "<상대방 등록 물품 없음>" : "");
				
				GuiMain.display.asyncExec(new Runnable() {
					public void run() {
						GuiMain.getViewComposite().getTradeComposite().toLog(log);
					}
				});
			}
		}
		for( ItemInstance item : use_list ){
			if(item.getItem()==null)
				continue;

			//
			String use_name = use.getName();
			long use_objid = use.getObjectId();
			String item_name = item.toStringDB();
			long item_objid = item.getObjectId();
			String target_name = null;
			long target_objid = 0;
			boolean isDrop = false;
			String itemName = Util.getItemNameToString(item, item.getCount());
			//
			if(pc.getInventory().isAppendTrade(item, item.getCount())){
				ItemInstance temp = pc.getInventory().find(item);
				if(temp!=null){
					//
					target_name = temp.toStringDB();
					target_objid = temp.getObjectId();
					//
					pc.getInventory().count(temp, temp.getCount()+item.getCount(), true);
					ItemDatabase.setPool(item);
					
			
				}else{
		
					pc.getInventory().append( item, true );
				}
			}else{
				item.toTeleport(pc.getX(), pc.getY(), pc.getMap(), false);
		
				isDrop = true;
			}
			// log
			Log.appendItem(pc, "type|거래완료", String.format("use_name|%s", use_name), String.format("use_objid|%d", use_objid), String.format("item_name|%s", item_name), String.format("item_objid|%d", item_objid), String.format("target_name|%s", target_name), String.format("target_objid|%d", target_objid), String.format("drop|%s", Boolean.valueOf(isDrop)));
			

			
			if (!Common.system_config_console) {
				long time = System.currentTimeMillis();
				String timeString = Util.getLocaleString(time, true);
				String log = String.format("[%s]\t [%s->%s]\t [아이템: %s]\t %s", timeString, use.getName(), pc.getName(), itemName, pc_list == null || pc_list.size() < 1 ? "<상대방 등록 물품 없음>" : "");
				
				GuiMain.display.asyncExec(new Runnable() {
					public void run() {
						GuiMain.getViewComposite().getTradeComposite().toLog(log);
					}
				});
			}
		}
		
		if ((pc_list == null || pc_list.size() == 0) && use_list != null || use_list.size() == 0) {
			
		}
		
		if (use_list == null || use_list.size() == 0) {
			
		}
		
		
		// 거래완료
		pc.toSender(S_TradeStatus.clone(BasePacketPooling.getPool(S_TradeStatus.class), true));
		use.toSender(S_TradeStatus.clone(BasePacketPooling.getPool(S_TradeStatus.class), true));
		// 거래 완료된거 알리기.
		pc.toTradeOk(use);
		use.toTradeOk(pc);
	}

}
