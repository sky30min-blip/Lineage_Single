package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Trade;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_TradeStart;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.item.Letter;

public final class TradeController {

	private static List<Trade> list;
	private static List<Trade> pool;
	
	static public void init(){
		TimeLine.start("TradeController..");
		
		list = new ArrayList<Trade>();
		pool = new ArrayList<Trade>();
		
		TimeLine.end();
	}
	
	static public void toWorldJoin(Character pc){
		// 할거 없음.
	}
	
	static public void toWorldOut(Character pc){
		try {
			toTradeCancel( find(pc) );
		} catch (Exception e) { }
	}
	
	/**
	 * 종료 처리 함수.
	 */
	static public void close(){
		synchronized (list) {
			for(Trade t : list)
				t.toCancel();
			list.clear();
		}
		synchronized (pool) {
			pool.clear();
		}
	}
	
//	/**
//	 * 거래 요청 처리 함수.
//	 * @param pc
//	 * @param use
//	 */
//	static public void toTrade(Character pc, Character use){
//		if (use != null && ((pc.getMap() == Lineage.teamBattleMap || use.getMap() == Lineage.teamBattleMap) || 
//				(pc.getMap() == Lineage.BattleRoyalMap || use.getMap() == Lineage.BattleRoyalMap)))
//			return;
//		
//		if(use!=null && !use.isDead()){
//			Trade p_t = find(pc);
//			Trade u_t = find(use);
//			if(p_t!=null || u_t!=null){
//				if(p_t != null)
//					// \f1당신은 이미 다른 사람과 거래중입니다.
//					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 258));
//				if(u_t != null)
//					// \f1그는 이미 다른 사람과 거래중입니다.
//					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 259));
//			}else{
//				p_t = getPool();
//				p_t.setPc(pc);
//				p_t.setUse(use);
//				use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 252, pc.getName()));
//				
//				synchronized (list) {
//					if(!list.contains(p_t))
//						list.add(p_t);
//				}
//			}
//		}
//	}
//
//	/**
//	 * 거래 요청에 대한 응답처리 메서드.
//	 */
//	static public void toTrade(Character pc, boolean yes){
//		Trade t = find(pc);
//		if(t != null){
//			if(yes){
//				// %0와의 거래를 시작합니다...
//				t.getPc().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 254, t.getUse().getName()));
//				t.getUse().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 254, t.getPc().getName()));
//				// 교환창 띄우기
//				t.getPc().toSender(S_TradeStart.clone(BasePacketPooling.getPool(S_TradeStart.class), t.getUse()));
//				t.getUse().toSender(S_TradeStart.clone(BasePacketPooling.getPool(S_TradeStart.class), t.getPc()));
//			}else{
//				// %0%d 당신과의 거래를 거절하였습니다.
//				t.getPc().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 253, t.getUse().getName()));
//				toTradeCancel(t);
//			}
//		}
//	}
	
	/**
	 * 거래 요청 처리 함수.
	 * @param pc
	 * @param use
	 */
	static public void toTrade(Character pc, Character use){
		if (use != null && ((pc.getMap() == Lineage.teamBattleMap || use.getMap() == Lineage.teamBattleMap) || 
				(pc.getMap() == Lineage.BattleRoyalMap || use.getMap() == Lineage.BattleRoyalMap)))
			return;
		
		if(use!=null && !use.isDead()){
			Trade p_t = find(pc);
			Trade u_t = find(use);
			if(p_t!=null || u_t!=null){
				if(p_t != null)
					// \f1당신은 이미 다른 사람과 거래중입니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 258));
				if(u_t != null)
					// \f1그는 이미 다른 사람과 거래중입니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 259));
			}else{
				p_t = getPool();
				p_t.setPc(pc);
				p_t.setUse(use);
				use.trade = p_t;
				use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 252, pc.getName()));
			}
		}
	}

	/**
	 * 거래 요청에 대한 응답처리 메서드.
	 */
	static public void toTrade(Character pc, boolean yes) {
		Trade t = pc.trade;
		
		if (t != null) {
			if (yes) {
				synchronized (list) {
					if (!list.contains(t))
						list.add(t);
				}
				
				// %0와의 거래를 시작합니다...
				t.getPc().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 254, t.getUse().getName()));
				t.getUse().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 254, t.getPc().getName()));
				// 교환창 띄우기
				t.getPc().toSender(S_TradeStart.clone(BasePacketPooling.getPool(S_TradeStart.class), t.getUse()));
				t.getUse().toSender(S_TradeStart.clone(BasePacketPooling.getPool(S_TradeStart.class), t.getPc()));
			} else {
				// %0%d 당신과의 거래를 거절하였습니다.
				t.getPc().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 253, t.getUse().getName()));
				toTradeCancel(t);
			}
		}
		
		pc.trade = null;
	}

	/**
	 * 사용자가 거래취소를 하거명 호출됨.
	 * 또 그의외에 제거가 필요할때 호출해서 사용.
	 */
	static public void toTradeCancel(Character pc){
		toTradeCancel( find(pc) );
	}

	/**
	 * 사용자가 거래 확인을 눌렀을때 호출됨.
	 * @param pc
	 */
	static public void toTradeOk(Character pc){
		Trade t = find(pc);
		if(t != null){
			t.setOk(pc);
			if(t.isTrade()){
				t.toOk();
				synchronized (list) {
					list.remove(t);
				}
				setPool(t);
			}
		}
	}
	
	/**
	 * 거래중인 목록에 아이템 추가처리하는 함수.
	 * @param pc
	 * @param item
	 * @param count
	 */
	static public void toTradeAddItem(Character pc, ItemInstance item, long count) {
		Trade t = find(pc);
		if(t!=null && item!=null) {
			try {
				// 유저 현금 거래 관련 편지는 거래/드랍 안됨. 
				if (item instanceof Letter) {
					Letter temp = (Letter) item;
					
					if (temp.getFrom() != null && temp.getFrom().equalsIgnoreCase(PcTradeController.PC_TRADE))
						return;
				}
				
				t.append(pc, item, count);
			} catch (Exception e) {
				//lineage.share.System.println(TradeController.class+" : toTradeAddItem(Character pc, ItemInstance item, long count)");
				//lineage.share.System.println(e);
			}
		}
	}

	/**
	 * 사용자가 거래취소를 하거나 월드를 나갈때 호출됨.
	 * 또 그의외에 제거가 필요할때 호출해서 사용.
	 */
	static private void toTradeCancel(Trade t){
		if(t != null){
			try {
				t.toCancel();
			} catch (Exception e) {
				lineage.share.System.println(TradeController.class+" : toTradeCancel(Trade t)");
				lineage.share.System.println(e);
			}
			synchronized (list) {
				list.remove(t);
			}
			setPool(t);
		}
	}

	/**
	 * 매개변수의 pc가 현재 거래에 쓰이고있는 bean이 등록되어 있는지 확인하는 메서드.
	 *  : BuffRobotInstance.toTimer(long time) 에서 사용중.
	 */
	static public Trade find(Character pc){
		synchronized (list) {
			for( Trade t : list ){
				if(t.isTrade(pc))
					return t;
			}
			return null;
		}
	}
	
	static private Trade getPool(){
		Trade t = null;
		synchronized (pool) {
			if(pool.size()>0){
				t = pool.get(0);
				pool.remove(0);
			}else{
				t = new Trade();
			}
		}
		return t;
	}
	
	static private void setPool(Trade t){
		if(t != null){
			t.close();
			synchronized (pool) {
				if(!pool.contains(t))
					pool.add(t);
			}
		}
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
}
