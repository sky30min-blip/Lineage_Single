package lineage.world.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.lineage.Useshop;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_UserShopList;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public final class UserShopController {

	static private List<Useshop> pool;
	static private Map<PcInstance, Useshop> list;
	
	static public void init(){
		TimeLine.start("UserShopController..");
		
		pool = new ArrayList<Useshop>();
		list = new HashMap<PcInstance, Useshop>();
		
		TimeLine.end();
	}
	
	/**
	 * 개인상점 시작 처리 함수.
	 * @param pc
	 * @param cbp
	 */
	static public void toStart(PcInstance pc, ClientBasePacket cbp){
		// 상점 상태인데 다시 상점시작 요청을 할수 있으므로 그에 따른 버그 예방.
		toStop(pc);
		// 맵 확인.
		if(isMap(pc)){
			Useshop us = getPool();
			// 판매 목록 추출.
			for(int i=cbp.readH() ; i>0 ; --i){
				ItemInstance item = pc.getInventory().value(cbp.readD());
				if(item!=null && !item.isEquipped() && item.getBless()>=0 && item.getBless()<128){
					item.setUsershopBuyPrice(cbp.readD());
					item.setUsershopBuyCount(cbp.readD());
					if(item.getUsershopBuyPrice()>0 && item.getUsershopBuyCount()>0 && item.getCount() >= item.getUsershopBuyCount())
						us.getBuy().add(item);
				}
			}
			// 구입 목록 추출.
			for(int i=cbp.readH() ; i>0 ; --i){
				ItemInstance item = pc.getInventory().value(cbp.readD());
				if(item!=null && !item.isEquipped() && item.getBless()>=0 && item.getBless()<128){
					item.setUsershopSellPrice(cbp.readD());
					item.setUsershopSellCount(cbp.readD());
					if(item.getUsershopSellPrice()>0 && item.getUsershopSellCount()>0)
						us.getSell().add(item);
				}
			}
			// 아이템등록됫는지 체크
			if(us.getBuy().size()>0 || us.getSell().size()>0){
				// 착용중인 아이템 해제.
				if(pc.getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
					pc.getInventory().getSlot(Lineage.SLOT_WEAPON).toClick(pc, null);
				// 변신상태 해제.
				BuffController.remove(pc, ShapeChange.class);
				// 처리.
				us.setMsg(cbp.readB());
				pc.setGfxMode(70);
				pc.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), pc, 3), true);
				pc.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), pc, us), true);
				
				synchronized (list) {
					if(!list.containsKey(pc))
						list.put(pc, us);
				}
				
			}else{
				// 개인 상점에 등록된 아이템이 없습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 908));
				setPool(us);
			}
		}else{
			// 이 곳에서는 개인 상점을 열 수 없습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 876));
		}
	}
	
	/**
	 * 개인상점 종료 처리 함수.
	 * @param pc
	 */
	static public void toStop(PcInstance pc){
		Useshop us = find(pc);
		if(us != null){
			try {
				// 초기화 및 풀에 재등록.
				setPool(us);
				synchronized (list) {
					list.remove(pc);
				}
				// 상태 변경
				pc.setGfxMode(pc.getClassGfxMode());
				// 패킷 처리
				pc.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), pc, 3), true);
			} catch (Exception e) {
				lineage.share.System.println(UserShopController.class+" : toStop(PcInstance pc)");
				lineage.share.System.println(e);
			}
		}
	}
	
	/**
	 * 개인상점 buy 및 sell 목록 보기 함수.
	 * @param pc
	 * @param use
	 * @param buy
	 */
	static public void toList(PcInstance pc, PcInstance use, boolean buy){
		Useshop us = find(use);
		if(us != null)
			pc.toSender(S_UserShopList.clone(BasePacketPooling.getPool(S_UserShopList.class), use, us, buy));
	}
	
	static public Useshop find(PcInstance pc){
		synchronized (list) {
			return list.get(pc);
		}
	}
	
	/**
	 * 해당 객체가 시장맵에 있는지 확인해주는 함수.
	 * @param o
	 * @return
	 */
	static public boolean isMap(object o){
		return o.getMap()==340 || o.getMap()==350 || o.getMap()==360 || o.getMap()==370;
	}
	
	static private Useshop getPool(){
		Useshop us = null;
		synchronized (pool) {
			if(pool.size()>0){
				us = pool.get(0);
				pool.remove(0);
			}else{
				us = new Useshop();
			}
		}
		return us;
	}
	
	static private void setPool(Useshop us){
		us.close();
		synchronized (pool) {
			if(!pool.contains(us))
				pool.add(us);
		}
	}
	
	static public int getPoolSize(){
		synchronized (pool) {
			return pool.size();
		}
	}
}
