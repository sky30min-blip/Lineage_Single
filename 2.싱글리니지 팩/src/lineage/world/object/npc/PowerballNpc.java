package lineage.world.object.npc;

import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.database.ItemDatabase;
import lineage.database.PowerballDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_HyperText;
import lineage.network.packet.server.S_ShopBuy;
import lineage.network.packet.server.S_ShopSell;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.powerball.PowerBallNpcHandler;
import lineage.world.controller.PowerballController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectTitle;
import lineage.world.World;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 파워볼 미니게임 NPC (상점 형태).
 * NPC 클릭 시 소개창 → 구매/판매 선택 → 구매 시 홀/짝/언더/오버 쿠폰 1장, 금액 입력(5만~500만).
 */
public class PowerballNpc extends ShopInstance {

	private static final long MIN_BET = 50000L;
	private static final long MAX_BET = 5000000L;
	/** 타이머에서 회차/남은시간 갱신용 (NPC는 CharacterController 리스트에 없음) */
	private static final List<PowerballNpc> INSTANCES = new CopyOnWriteArrayList<PowerballNpc>();

	public PowerballNpc(Npc n) {
		super(n);
		INSTANCES.add(this);
	}

	@Override
	public String getName() {
		// 채팅에는 "파워볼진행자"만 표시. 회차/남은시간은 호칭(위)에 표시.
		return getNpc().getName();
	}

	/** TimeThread에서 1초마다 호출: 파워볼진행자 NPC 호칭에 회차/남은시간 표시 (이름은 짧게 유지) */
	public static void updateAllTitles() {
		int roundId = PowerballController.getCurrentRound();
		int displayRound = PowerballController.getTodayRoundDisplay(roundId); // 1~288
		Calendar cal = Calendar.getInstance(Locale.KOREA);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		String sub;
		if (PowerballController.isWithin30SecOfRoundClose()) {
			int remainSec = 60 - sec;
			if (remainSec < 0) remainSec = 0;
			int m = remainSec / 60, s = remainSec % 60;
			sub = String.format("%d차 %02d:%02d (마감)", displayRound, m, s);
		} else {
			int remainSec = (5 - (min % 5)) * 60 - sec;
			if (remainSec < 0) remainSec = 0;
			int m = remainSec / 60, s = remainSec % 60;
			sub = String.format("%d차 %02d:%02d", displayRound, m, s);
		}
		for (PowerballNpc npc : INSTANCES) {
			if (npc == null || npc.isDead()) continue;
			npc.setTitle(sub);
			int map = npc.getMap();
			for (PcInstance pc : World.getPcList()) {
				if (pc != null && !pc.isDead() && pc.getMap() == map)
					pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), npc));
			}
			npc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), npc), false);
		}
	}

	/** 월드에 살아있는 파워볼진행자 NPC 1개 반환 */
	public static PowerballNpc findAnyAliveInstance() {
		for (PowerballNpc npc : INSTANCES) {
			if (npc != null && !npc.isDead())
				return npc;
		}
		return null;
	}

	/** 첫 클릭: 오림과 동일하게 "elmina" 키만 전송 (다른 HTML 전송 시 이 클라이언트에서 튕김) */
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "elmina"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		// 구매 클릭 시 상점 구매창(S_ShopBuy) 표시 → 홀/짝 쿠폰 선택 후 toBuy에서 금액 입력창(HyperText)
		boolean isBuy = action != null && (action.equalsIgnoreCase("buy") || action.contains("_buy") || (action.contains("bypass") && action.toLowerCase().contains("buy")));
		if (isBuy) {
			pc.toSender(S_ShopBuy.clone(BasePacketPooling.getPool(S_ShopBuy.class), this));
			return;
		}
		// 파워볼 보드(신규): 베팅 UI 표시 및 powerball_bet 액션 처리 (bypass 포함)
		if (action != null && action.toLowerCase().contains("powerball_bet ")) {
			PowerBallNpcHandler.handleAction(pc, action);
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, PowerBallNpcHandler.getBettingHtml()));
			return;
		}
		if (action != null && (action.equalsIgnoreCase("powerball_board") || action.equalsIgnoreCase("powerball"))) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, PowerBallNpcHandler.getBettingHtml()));
			return;
		}

		boolean isSell = action != null && (action.equalsIgnoreCase("sell") || action.contains("_sell") || action.contains("bypass") && action.toLowerCase().contains("sell"));
		if (isSell) {
			List<ItemInstance> sell_list = new ArrayList<ItemInstance>();
			if (getNpc() != null && getNpc().getShop_list() != null) {
			for (Shop s : getNpc().getShop_list()) {
				if (s.isItemSell()) {
					List<ItemInstance> search_list = new ArrayList<ItemInstance>();
					pc.getInventory().findDbName(s.getItemName(), search_list);
					for (ItemInstance item : search_list) {
						if (!item.isEquipped() && item.getItem().isSell() && !sell_list.contains(item))
							sell_list.add(item);
					}
				}
			}
			}
			if (sell_list.isEmpty())
				ChattingController.toChatting(pc, "[파워볼] 매입할 수 있는 쿠폰이 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			else
				pc.toSender(S_ShopSell.clone(BasePacketPooling.getPool(S_ShopSell.class), this, sell_list));
			return;
		}
		// 그 외 링크 클릭 시 소개창 다시 표시
		toTalk(pc, cbp);
	}

	@Override
	protected void toBuy(PcInstance pc, ClientBasePacket cbp) {
		long count = cbp.readH();
		if (count <= 0 || count > 100) return;
		long item_idx = cbp.readD();
		long item_count = cbp.readD();
		if (item_count <= 0 || item_count > 1000) return;
		Shop s = getNpc().findShop(item_idx);
		if (s == null) return;
		String itemName = s.getItemName();
		if (itemName == null) return;
		// 홀/짝 쿠폰: 구매창에서 선택 시 금액 입력창(HyperText)만 띄움 (아이템은 doBet에서 지급)
		if (itemName.equals("홀 쿠폰") || itemName.equals("짝 쿠폰")
				|| itemName.equals("언더 쿠폰") || itemName.equals("오버 쿠폰")) {
			if (item_count != 1) {
				ChattingController.toChatting(pc, "[파워볼] 쿠폰은 1장만 구매할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			String act;
			if (itemName.equals("홀 쿠폰")) act = "hol";
			else if (itemName.equals("짝 쿠폰")) act = "jjak";
			else if (itemName.equals("언더 쿠폰")) act = "under";
			else act = "over";
			pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this,
				"powerball", act, 0, 100000, (int) MIN_BET, MAX_BET, null));
			return;
		}
		// 그 외 아이템은 일반 상점 처리하지 않음 (파워볼은 쿠폰만)
		ChattingController.toChatting(pc, "[파워볼] 구매: " + Lineage.command + "파워볼홀·짝·언더·오버 금액 (" + String.format("%,d~%,d 아데나", MIN_BET, MAX_BET) + ")", Lineage.CHATTING_MODE_MESSAGE);
	}


	@Override
	public void toHyperText(PcInstance pc, ClientBasePacket cbp) {
		if (!cbp.isRead(4))
			return;
		int amount = cbp.readD();
		cbp.readC();
		String action = cbp.readS();
		if (action == null)
			return;
		int pickType = -1;
		if (action.equalsIgnoreCase("hol"))
			pickType = 1;
		else if (action.equalsIgnoreCase("jjak"))
			pickType = 0;
		else if (action.equalsIgnoreCase("under"))
			pickType = 2;
		else if (action.equalsIgnoreCase("over"))
			pickType = 3;
		if (pickType < 0) {
			ChattingController.toChatting(pc, "[파워볼] 잘못된 요청입니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		PowerballController.doBet(pc, pickType, (long) amount);
	}

	@Override
	protected void toSell(PcInstance pc, ClientBasePacket cbp) {
		if (Lineage.open_wait && pc.getGm() == 0) {
			ChattingController.toChatting(pc, "[오픈 대기] 상점을 이용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		int count = cbp.readH();
		if (count <= 0) return;
		for (int i = 0; i < count; i++) {
			int inv_id = cbp.readD();
			long item_count = cbp.readD();
			ItemInstance temp = pc.getInventory().value(inv_id);
			if (temp == null || temp.isEquipped() || item_count <= 0 || temp.getCount() < item_count) continue;
			String name = temp.getItem().getName();
			if (name.equals("홀 쿠폰") || name.equals("짝 쿠폰")
					|| name.equals("언더 쿠폰") || name.equals("오버 쿠폰")) {
				int couponPickType;
				if (name.equals("홀 쿠폰")) couponPickType = 1;
				else if (name.equals("짝 쿠폰")) couponPickType = 0;
				else if (name.equals("언더 쿠폰")) couponPickType = 2;
				else couponPickType = 3;
				int roundId = -1;
				try {
					String tk = temp.getItemTimek();
					if (tk != null && !tk.isEmpty()) {
						int colon = tk.indexOf(':');
						roundId = colon >= 0 ? Integer.parseInt(tk.substring(0, colon).trim()) : Integer.parseInt(tk.trim());
					}
				} catch (NumberFormatException e) { continue; }
				if (roundId <= 0) continue;

				int resultType = PowerballDatabase.getResultForRound(roundId);
				int underOverType = PowerballDatabase.getUnderOverForRound(roundId);
				// 발표 직후 DB 가시성/커밋 지연으로 결과가 안 보일 수 있음 → 동일·직전 회차면 짧은 간격으로 재조회
				int currentRound = PowerballController.getCurrentRound();
				if (resultType < 0 && roundId <= currentRound) {
					for (int r = 0; r < 5 && resultType < 0; r++) {
						try { Thread.sleep(200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
						resultType = PowerballDatabase.getResultForRound(roundId);
						underOverType = PowerballDatabase.getUnderOverForRound(roundId);
					}
				}

				// 서버 다운 등으로 결과가 없는 과거 회차 → 구매 금액 전액 환불(무료 처리)
				if (resultType < 0 && roundId < PowerballController.getCurrentRound()) {
					long[] bet = PowerballDatabase.getBetByCharRoundForRefund((int) pc.getObjectId(), roundId, couponPickType);
					if (bet == null) {
						ChattingController.toChatting(pc, "[파워볼] 해당 회차 기록을 찾을 수 없거나 이미 수령한 쿠폰입니다.", Lineage.CHATTING_MODE_MESSAGE);
						continue;
					}
					long refundAmount = bet[1] * item_count;
					PowerballDatabase.setClaimed((long) bet[0]);
					pc.getInventory().count(temp, temp.getCount() - item_count, true);
					addAdenaToPc(pc, refundAmount);
					ChattingController.toChatting(pc, String.format("[파워볼] 해당 회차는 당첨 확인이 어려워 구매 금액(%,d 아데나)으로 매입했습니다.", refundAmount), Lineage.CHATTING_MODE_MESSAGE);
					continue;
				}

				if (resultType < 0) {
					ChattingController.toChatting(pc, "[파워볼] 아직 결과가 나오지 않은 회차입니다.", Lineage.CHATTING_MODE_MESSAGE);
					continue;
				}

				// 결과가 나왔으면 is_processed 없이 바로 매입 (쿠폰 종류와 pick_type 일치 행만)
				long[] bet = PowerballDatabase.getBetByCharRoundForRefund((int) pc.getObjectId(), roundId, couponPickType);
				if (bet == null)
					bet = PowerballDatabase.getBetByCharRound((int) pc.getObjectId(), roundId, couponPickType);
				if (bet == null) {
					ChattingController.toChatting(pc, "[파워볼] 해당 회차 결과가 나오지 않았거나 이미 수령한 쿠폰입니다.", Lineage.CHATTING_MODE_MESSAGE);
					continue;
				}
				long betAmount = bet[1];
				int pickType = (int) bet[2];
				// 배당률 = Lineage.powerball_payout_rate. 홀/짝은 result_type, 언더/오버는 under_over_type(0=언더,1=오버)
				boolean win;
				if (pickType <= 1)
					win = (pickType == resultType);
				else if (pickType == 2)
					win = (underOverType == 0);
				else if (pickType == 3)
					win = (underOverType == 1);
				else
					win = false;
				if (underOverType < 0 && resultType >= 0 && (pickType == 2 || pickType == 3)) {
					ChattingController.toChatting(pc, "[파워볼] DB에 언더/오버 결과가 없습니다. DB 마이그레이션(powerball_under_over_migration.sql)을 적용한 뒤 다시 시도하세요.", Lineage.CHATTING_MODE_MESSAGE);
					continue;
				}
				long payout = win ? Math.round(betAmount * Lineage.powerball_payout_rate) : 0L;
				long payoutAmount = payout * item_count;
				PowerballDatabase.setClaimed(bet[0]);
				// 쿠폰을 먼저 제거한 뒤 당첨금 지급 (클라이언트 인벤 갱신 순서로 인해 당첨금이 덮어써지지 않도록)
				pc.getInventory().count(temp, temp.getCount() - item_count, true);
				addAdenaToPc(pc, payoutAmount);
				if (payout > 0)
					ChattingController.toChatting(pc, String.format("[파워볼] 당첨 쿠폰 매입 완료. %,d 아데나 지급.", payoutAmount), Lineage.CHATTING_MODE_MESSAGE);
				else
					ChattingController.toChatting(pc, "[파워볼] 미당첨 쿠폰 매입 완료.", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				// 그 외 아이템은 파워볼 NPC에서 매입 안 함
			}
		}
	}

	/** 당첨금/환불금 지급. 인벤에 아데나가 없어도(0원으로 다 쓴 경우) 새 아데나 아이템을 만들어 지급. */
	private void addAdenaToPc(PcInstance pc, long amount) {
		if (pc == null || amount <= 0) return;
		ItemInstance aden = pc.getInventory().findAden();
		if (aden != null) {
			pc.getInventory().count(aden, aden.getCount() + amount, true);
		} else {
			ItemInstance newAden = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
			if (newAden != null) {
				newAden.setObjectId(ServerDatabase.nextItemObjId());
				newAden.setCount(amount);
				pc.getInventory().append(newAden, true);
			}
		}
	}
}
