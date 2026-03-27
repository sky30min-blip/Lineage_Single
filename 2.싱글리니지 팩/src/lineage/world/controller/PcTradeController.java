package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import lineage.bean.database.Item;
import lineage.bean.database.PcTrade;
import lineage.database.BackgroundDatabase;
import lineage.database.CharacterMarbleDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectLock;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public final class PcTradeController {
	static public int uid;
	static public final String PC_TRADE = "유저거래";
	static public final String STATE_SALE = "판매중";
	static public final String STATE_BUY = "구매신청";
	static public final String STATE_TRADE = "거래중";
	static public final String STATE_DEPOSIT = "입금완료";
	static public final String STATE_BUY_CANCEL = "구매취소";
	static public final String STATE_COMPLETE = "거래완료";
	static private long checkTime;
	
	static public int getUid() {
		uid++;
		return uid;
	}
	
	/**
	 * 화폐 자릿수 포맷 변환 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public String changePrice(int price) {
		String temp_price;
		
		DecimalFormat dc = new DecimalFormat("#,###,###,###,###");
		temp_price = dc.format(price);
		
		return temp_price;
	}
	
	/**
	 * 아이템 축복여부 포맷 변환 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public String changeBless(int bless) {
		String temp_bless;
		
		temp_bless = bless == 1 ? "" : bless == 0 ? "[축] " : "[저주] ";
		
		return temp_bless;
	}
	
	
	/**
	 * 게시글 제목 포맷 변환 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public String changeSubject(String state, String subject) {
		String temp_subject;
		
		temp_subject = String.format("[%s]%s", state, subject);
		
		return temp_subject;
	}
	
	/**
	 * 게시글 내용 포맷 변환 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public String changeContent(PcTrade pt) {
		String temp_content;
		StringBuilder content = new StringBuilder();
		Item i = ItemDatabase.find(pt.getItem());
		
		content.append(String.format("거래번호: %d\n", pt.getUid()));

		if (Lineage.is_pc_trade_sell_name) {
			content.append(String.format("판매자: %s\n", pt.getSell_name()));
			content.append(String.format("구매자: %s\n", pt.getBuy_name()));
	
		}
		
		content.append(String.format("거래상황: %s\n", pt.getState()));
		content.append(String.format("판매금액: %s원\n", changePrice(pt.getPrice())));

		if (i != null) {
			String itemName = CharacterMarbleDatabase.getItemName(pt.getItem_objId());
			if (itemName != null) {
				content.append(String.format("아 이 템: %s\n", itemName));
			} else {
				if (i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor"))
					content.append(String.format("아 이 템: %s+%d %s\n", changeBless(pt.getBless()), pt.getEnchant(), pt.getItem()));
				else
					content.append(String.format("아 이 템: %s%s\n", changeBless(pt.getBless()), pt.getItem()));
			}
		}

		if (pt.getItem().equalsIgnoreCase("아데나"))
			content.append(String.format("수    량: %s아데나", changePrice((int) pt.getCount())));
		else
			content.append(String.format("수    량: %s개", changePrice((int) pt.getCount())));
		
		temp_content = content.toString();
		
		return temp_content;
	}
	
	/**
	 * 연락처 등록 메소드.
	 * 2018-07-18
	 * by connector12@nate.com
	 */
	static public void insertInfo(object o, StringTokenizer st) {
		try {
			PcInstance pc = (PcInstance) o;
			
			if (accountSaleCount(pc) > 0) {
				ChattingController.toChatting(o, "계정에 판매 중인 게시글이 있을 경우 수정 불가합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			pc.setInfoName(st.nextToken());		
			if (pc.getInfoName().length() < 2) {
				ChattingController.toChatting(o, "이름의 길이가 짧습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			} else if (!Pattern.matches("^[가-힣]*$", pc.getInfoName())) {
				ChattingController.toChatting(o, "이름은 한글만 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
		
			pc.setInfoPhoneNum(st.nextToken());	
			if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", pc.getInfoPhoneNum())) {
				ChattingController.toChatting(o, "핸드폰 번호 형식이 잘못되었습니다. ex) 010-1234-5678", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			pc.setInfoBankName(st.nextToken());
			pc.setInfoBankNum(st.nextToken());
			if (pc.getInfoBankNum().length() < 2) {
				ChattingController.toChatting(o, "계좌번호의 길이가 짧습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			ChattingController.toChatting(o, String.format("등록완료. %s / %s / %s / %s", pc.getInfoName(), pc.getInfoPhoneNum(), pc.getInfoBankName(), pc.getInfoBankNum()), Lineage.CHATTING_MODE_MESSAGE);
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "계좌 홍길동 010-1234-5678 신한 110-365-123456", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 정보들중에 null값이 포함되어있는지 확인하는 메소드.
	 * 2018-07-18
	 * by connector12@nate.com
	 */
	static public boolean checkInfo(PcInstance pc) {
		if (pc.getInfoName() == null || pc.getInfoPhoneNum() == null || pc.getInfoBankName() == null || pc.getInfoBankNum() == null) {
			ChattingController.toChatting(pc, "[" + Lineage.command + "계좌]에서 정보를 등록해주시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		return true;
	}
	
	/**
	 * 등록한 연락처 확인 메소드.
	 * 2018-07-18
	 * by connector12@nate.com
	 */
	static public void enterInfo(object o) {
		try {
			PcInstance pc = (PcInstance) o;

			if (!checkInfo(pc))
				return;

			ChattingController.toChatting(o, String.format("%s / %s / %s / %s", pc.getInfoName(), pc.getInfoPhoneNum(), pc.getInfoBankName(), pc.getInfoBankNum()), Lineage.CHATTING_MODE_MESSAGE);
		} catch (Exception e) {
			ChattingController.toChatting(o, "등록된 정보가 없습니다. " + "[" + Lineage.command + "계좌] 에서 정보를 등록해주시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 현금 거래 게시판 보여주는 메소드.
	 * 2018-07-22
	 * by connector12@nate.com
	 */
	static public void viewBoard(object o) {
		BoardInstance b = BackgroundDatabase.getTradeBoard();
		if (b != null)
			b.toClick((Character) o, null);
	}
	static public void viewBoard2(object o) {
		BoardInstance b = BackgroundDatabase.getNoticeBoard();
		if (b != null)
			b.toClick((Character) o, null);
	}
	static public void viewBoard3(object o) {
		BoardInstance b = BackgroundDatabase.getGuideBoard();
		if (b != null)
			b.toClick((Character) o, null);
	}
	
	static public void viewBoard4(object o) {
		BoardInstance b = BackgroundDatabase.getUpdateBoard();
		if (b != null)
			b.toClick((Character) o, null);
	}
	static public void viewBoard5(object o) {
		BoardInstance b = BackgroundDatabase.getatBoard();
		if (b != null)
			b.toClick((Character) o, null);
	}
	static public void viewBoard6(object o) {
		BoardInstance b = BackgroundDatabase.getRankBoard();
		if (b != null)
			b.toClick((Character) o, null);
	}
	/**
	 * 판매 아이템 등록 메소드.
	 * 2018-07-18
	 * by connector12@nate.com
	 */
	static public void insertItem(object o, StringTokenizer st) {
		if (Lineage.is_pc_trade_sell_only_aden) {
			insertOnlyAden(o, st);
			return;
		}
		
		try {
			PcInstance pc = (PcInstance) o;
			
			if (o.getLevel() < Lineage.pc_trade_sell_level) {
				ChattingController.toChatting(o, String.format("판매 등록은 %d레벨 이상 가능합니다.", Lineage.pc_trade_sell_level), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!checkInfo(pc))
				return;
			
			int count = accountSaleCount(pc);
			if (Lineage.pc_trade_sale_max_count > 0 && Lineage.pc_trade_sale_max_count <= count) {
				ChattingController.toChatting(o, String.format("계정의 최대 판매 등록은 %d개입니다. 현재 판매등록: %d개", Lineage.pc_trade_sale_max_count, count), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			String subject = st.nextToken();
			int price = Integer.valueOf(st.nextToken());
			long itemCount = Long.valueOf(st.nextToken());


	
			
			if (subject == null || subject.length() < 1) {
				ChattingController.toChatting(o, "제목이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			} else if (price < 1 || price > 2000000000) {
				ChattingController.toChatting(o, "가격이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			} else if (itemCount < 1 || itemCount > 2000000000) {
				ChattingController.toChatting(o, "아이템 수량이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			PcTrade trade = new PcTrade();
			trade.setSell_account_uid(pc.getAccountUid());
			trade.setSell_name(pc.getName());
			trade.setSell_objId(pc.getObjectId());
			trade.setState(STATE_SALE);
			trade.setSubject(subject);
			trade.setPrice(price);
			trade.setName(pc.getInfoName());
			trade.setPhone_num(pc.getInfoPhoneNum());
			trade.setBank_name(pc.getInfoBankName());
			trade.setBank_num(pc.getInfoBankNum());
			trade.setCount(itemCount);
			pc.setPcTrade(trade);
			ChattingController.toChatting(o, "\\fY인벤토리에서 판매할 아이템을 더블클릭해주세요.", Lineage.CHATTING_MODE_MESSAGE);
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "판매등록 제목(띄워쓰기 없이) 가격 아이템수량", Lineage.CHATTING_MODE_MESSAGE);
			
			if (Lineage.is_aden_static_price && Lineage.aden_static_unit >= 10000)
				ChattingController.toChatting(o, String.format("아데나는 아이템 수량 1개당 %,d만 아덴으로 등록됩니다.", Lineage.aden_static_unit / 10000), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	static public void insertOnlyAden(object o, StringTokenizer st) {
		try {
			PcInstance pc = (PcInstance) o;
			
			if (o.getLevel() < Lineage.pc_trade_sell_level) {
				ChattingController.toChatting(o, String.format("판매 등록은 %d레벨 이상 가능합니다.", Lineage.pc_trade_sell_level), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!checkInfo(pc))
				return;
			
			int count = accountSaleCount(pc);
			if (Lineage.pc_trade_sale_max_count > 0 && Lineage.pc_trade_sale_max_count <= count) {
				ChattingController.toChatting(o, String.format("계정의 최대 판매 등록은 %d개입니다. 현재 판매등록: %d개", Lineage.pc_trade_sale_max_count, count), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			int price = Integer.valueOf(st.nextToken());
			long itemCount = Long.valueOf(st.nextToken());
			String subject;
			
			if (itemCount >= 100000000) {
			    subject = String.format("%,d억 아덴 팝니다.", itemCount / 100000000);
			} else if (itemCount >= 10000) {
			    subject = String.format("%,d만 아덴 팝니다.", itemCount / 10000);
			} else {
			    subject = String.format("%,d 아덴 팝니다.", itemCount);
			}
			if (subject == null || subject.length() < 1) {
				ChattingController.toChatting(o, "제목이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			} else if (price < 1 || price > 2000000000) {
				ChattingController.toChatting(o, "가격이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			} else if (itemCount < 1 || itemCount > 2000000000) {
				ChattingController.toChatting(o, "아이템 수량이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			PcTrade trade = new PcTrade();
			trade.setSell_account_uid(pc.getAccountUid());
			trade.setSell_name(pc.getName());
			trade.setSell_objId(pc.getObjectId());
			trade.setState(STATE_SALE);
			trade.setSubject(subject);
			trade.setPrice(price);
			trade.setName(pc.getInfoName());
			trade.setPhone_num(pc.getInfoPhoneNum());
			trade.setBank_name(pc.getInfoBankName());
			trade.setBank_num(pc.getInfoBankNum());
			trade.setCount(itemCount);
			pc.setPcTrade(trade);
			ChattingController.toChatting(o, "\\fY인벤토리에서 판매할 아이템을 더블클릭해주세요.", Lineage.CHATTING_MODE_MESSAGE);
		} catch (Exception e) {
			ChattingController.toChatting(o, Lineage.command + "판매등록 가격 아이템수량", Lineage.CHATTING_MODE_MESSAGE);
			
			if (Lineage.is_aden_static_price && Lineage.aden_static_unit >= 10000)
				ChattingController.toChatting(o, String.format("아데나는 아이템 수량 1개당 %,d만 아덴으로 등록됩니다.", Lineage.aden_static_unit / 10000), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 판매 아이템 최종 등록 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public boolean insertItemFinal(PcInstance pc, ItemInstance item, long count) {
		pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
		
		if (pc.getPcTrade() == null)
			return false;
		if (item == null || item.getItem() == null)
			return false;
		
		// 착용 한거 무시.
		if (item.isEquipped()) {
			pc.setPcTrade(null);
			ChattingController.toChatting(pc, "\\fR사용중인 아이템은 등록할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(pc, "\\fR판매등록이 취소되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return true;
		}

		if (Lineage.is_pc_trade_sell_only_aden) {
			// 아데나 등록 못하도록 처리
			if (item.getItem().getNameIdNumber() != 4) {
				pc.setPcTrade(null);
				ChattingController.toChatting(pc, "\\fR아데나만 등록 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, "\\fR판매등록이 취소되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return true;
			}
		} else {
			// 거래 안되는 아이템은 무시.
			if (!item.getItem().isTrade() || item.getBless() < 0 || !item.getItem().isPcTrade()) {
				pc.setPcTrade(null);
				ChattingController.toChatting(pc, "\\fR거래가 불가능한 아이템 입니다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, "\\fR판매등록이 취소되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return true;
			}
		}
		

		if ((item.getItem().getNameIdNumber() == 4 || item.getItem().getName().equalsIgnoreCase("아데나")) && Lineage.is_aden_static_price && Lineage.aden_static_unit > 0 && Lineage.aden_static_unit >= 10000) {
			long adenCount = pc.getPcTrade().getCount() * Lineage.aden_static_unit;

			// 수량확인.
			if (count < adenCount) {
				pc.setPcTrade(null);
				ChattingController.toChatting(pc, "\\fR판매등록할 아이템 수량이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, "\\fR판매등록이 취소되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, String.format("\\fR아데나는 아이템 수량 1개당 %,d만 아덴으로 등록됩니다.", Lineage.aden_static_unit / 10000), Lineage.CHATTING_MODE_MESSAGE);
				return true;
			}

			double percentage = 0.1; // 10%를 나타내는 비율입니다.

			int result = (int) (adenCount * percentage);

			int requiredCoins = result; // 필요한 코인 양은 result 값과 동일
			
			
				if (!pc.getInventory().isAden(adenCount + requiredCoins, false)) {
	
					pc.setPcTrade(null);
					String chatMessage = String.format("아데나가 부족합니다. 수수료: %d", requiredCoins);
					ChattingController.toChatting(pc, "\\fR판매등록이 취소되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(pc, chatMessage, Lineage.CHATTING_MODE_MESSAGE);
					return true;
				}
				try {
	
					pc.getPcTrade().setUid(getUid());
					pc.getPcTrade().setItem_objId(item.getItem().isPiles() ? 0 : item.getObjectId());
					pc.getPcTrade().setItem(item.getItem().getName());
					pc.getPcTrade().setEnchant(item.getEnLevel());
					pc.getPcTrade().setBless(item.getBless());
					pc.getPcTrade().setSubject(String.format("%,d만 아덴 팝니다.", adenCount / 10000));
					pc.getPcTrade().setPrice((int) (pc.getPcTrade().getCount() * Lineage.aden_static_price));
					pc.getPcTrade().setCount(adenCount);
					pc.getPcTrade().setContent(changeContent(pc.getPcTrade()));

					pc.getInventory().count(item, item.getCount() - (pc.getPcTrade().getCount()+requiredCoins), true);

					insertBoard(pc, pc.getPcTrade());
				} catch (Exception e) {
					lineage.share.System.printf("%s : insertItemFinal(PcInstance pc, ItemInstance item, long count)\r\n", PcTradeController.class.toString());
					lineage.share.System.println(e);
				}

			

		} else {
			// 수량확인.
			if (count < pc.getPcTrade().getCount()) {
				pc.setPcTrade(null);
				ChattingController.toChatting(pc, "\\fR판매등록할 아이템 수량이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, "\\fR판매등록이 취소되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return true;
			}

			double percentage = 0.1; // 10%를 나타내는 비율입니다.

			int result = (int) (pc.getPcTrade().getCount() * percentage);

			int requiredCoins = result; // 필요한 코인 양은 result 값과 동일

			if (!pc.getInventory().isAden(pc.getPcTrade().getCount() + requiredCoins, false)) {
				
				pc.setPcTrade(null);
				String chatMessage = String.format("아데나가 부족합니다. 수수료: %d", requiredCoins);
				ChattingController.toChatting(pc, "\\fR판매등록이 취소되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, chatMessage, Lineage.CHATTING_MODE_MESSAGE);
				return true;
			}
				try {
					pc.getPcTrade().setUid(getUid());
					pc.getPcTrade().setItem_objId(item.getItem().isPiles() ? 0 : item.getObjectId());
					pc.getPcTrade().setItem(item.getItem().getName());
					pc.getPcTrade().setEnchant(item.getEnLevel());
					pc.getPcTrade().setBless(item.getBless());
					pc.getPcTrade().setContent(changeContent(pc.getPcTrade()));

					pc.getInventory().count(item, item.getCount() - (pc.getPcTrade().getCount()+requiredCoins), true);

					insertBoard(pc, pc.getPcTrade());
				} catch (Exception e) {
					lineage.share.System.printf("%s : insertItemFinal(PcInstance pc, ItemInstance item, long count)\r\n", PcTradeController.class.toString());
					lineage.share.System.println(e);
				}
			
		}

		return true;
	}
	
	/**
	 * 게시판 등록 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public void insertBoard(PcInstance pc, PcTrade trade) {
		Connection con = null;
		PreparedStatement st = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO pc_trade SET uid=?, sell_account_uid=?, sell_name=?, sell_objId=?, state=?, price=?, item_objId=?, item=?, enchant=?, bless=?, count=?, "
					+ "write_day=?, subject=?, content=?, name=?, phone_num=?, bank_name=?, bank_num=?");
			st.setInt(1, trade.getUid());
			st.setInt(2, trade.getSell_account_uid());
			st.setString(3, trade.getSell_name());
			st.setLong(4, trade.getSell_objId());
			st.setString(5, trade.getState());
			st.setInt(6, trade.getPrice());
			st.setLong(7, trade.getItem_objId());
			st.setString(8, trade.getItem());
			st.setInt(9, trade.getEnchant());
			st.setInt(10, trade.getBless());
			st.setLong(11, trade.getCount());
			st.setTimestamp(12, new java.sql.Timestamp(System.currentTimeMillis()));
			st.setString(13, trade.getSubject());
			st.setString(14, trade.getContent());
			st.setString(15, trade.getName());
			st.setString(16, trade.getPhone_num());
			st.setString(17, trade.getBank_name());
			st.setString(18, trade.getBank_num());
			st.executeUpdate();
			st.close();
			
			BoardInstance tradeBoard = BackgroundDatabase.getTradeBoard();		
			if (tradeBoard != null) {
				try {
					st = con.prepareStatement("INSERT INTO boards SET uid=?, type=?, account_id=?, name=?, days=?, subject=?, memo=?");
					st.setInt(1, trade.getUid());
					st.setString(2, tradeBoard.getType());
					st.setString(3, pc.getClient().getAccountId());
					st.setString(4, "");
					st.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
					st.setString(6, changeSubject(trade.getState(), trade.getSubject()));
					st.setString(7, trade.getContent());
					st.executeUpdate();
					
					// 페이지 보기.
					BoardController.toView(pc, tradeBoard, trade.getUid());
				} catch (Exception e) {
					lineage.share.System.printf("%s : insertBoard(PcInstance pc, PcTrade trade)\r\n", PcTradeController.class.toString());
					lineage.share.System.println(e);
				}
			}

			pc.setPcTrade(null);
		} catch (Exception e) {
			lineage.share.System.printf("%s : insertBoard(PcInstance pc, PcTrade trade)\r\n", PcTradeController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	/**
	 * 계정의 판매신청 게시글 갯수 확인 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public int accountSaleCount(PcInstance pc) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getLineage();

			st = con.prepareStatement("SELECT COUNT(*) as cnt FROM pc_trade WHERE sell_account_uid=? AND NOT state=?");
			st.setLong(1, pc.getAccountUid());
			st.setString(2, STATE_COMPLETE);
			rs = st.executeQuery();
			
			if (rs.next())
				return rs.getInt("cnt");
		} catch (Exception e) {
			lineage.share.System.printf("%s : accountSaleCount(PcInstance pc)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
	
	/**
	 * 계정의 구매신청 갯수 확인 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public int accountBuyCount(PcInstance pc) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getLineage();

			st = con.prepareStatement("SELECT COUNT(*) as cnt FROM pc_trade WHERE buy_account_uid=? AND NOT state=?");
			st.setLong(1, pc.getAccountUid());
			st.setString(2, STATE_COMPLETE);
			rs = st.executeQuery();
			
			if (rs.next())
				return rs.getInt("cnt");
		} catch (Exception e) {
			lineage.share.System.printf("%s : accountBuyCount(PcInstance pc)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
	
	/**
	 * 해당 캐릭터의 판매등록한 물품 갯수 확인 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public int countSale(PcInstance pc) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getLineage();

			st = con.prepareStatement("SELECT COUNT(*) as cnt FROM pc_trade WHERE sell_objId=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			
			if (rs.next())
				return rs.getInt("cnt");
		} catch (Exception e) {
			lineage.share.System.printf("%s : countSale(PcInstance pc)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
	
	/**
	 * 해당 캐릭터의 구매신청 물품 갯수 확인 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public int countBuy(PcInstance pc) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getLineage();

			st = con.prepareStatement("SELECT COUNT(*) as cnt FROM pc_trade WHERE buy_objId=?");
			st.setLong(1, pc.getObjectId());
			rs = st.executeQuery();
			
			if (rs.next())
				return rs.getInt("cnt");
		} catch (Exception e) {
			lineage.share.System.printf("%s : countBuy(PcInstance pc)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
	
	/**
	 * 해당 게시글이 존재하는지 확인하는 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public boolean isTradeUid(int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getLineage();

			// 제거.
			st = con.prepareStatement("SELECT buy_objId FROM pc_trade WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next())
				return true;
		} catch (Exception e) {
			lineage.share.System.printf("%s : isTradeUid(int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		return false;
	}
	
	/**
	 * 계정 정보로 해당 게시글 구매자 확인 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public boolean isTradeBuy(PcInstance pc, int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getLineage();

			// 제거.
			st = con.prepareStatement("SELECT buy_account_uid FROM pc_trade WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next()) {
				if (rs.getInt(1) == pc.getAccountUid())
					return true;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : isTradeBuy(PcInstance pc, int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		return false;
	}
	
	/**
	 * 계정 정보로 해당 게시글 판매자 확인 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public boolean isTradeSell(PcInstance pc, int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getLineage();

			// 제거.
			st = con.prepareStatement("SELECT sell_account_uid FROM pc_trade WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next()) {
				if (rs.getInt(1) == pc.getAccountUid())
					return true;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : isTradeSell(PcInstance pc, int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		return false;
	}
	/**
	 * 거래상태 확인하는 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public String isTradeState(int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String state = null;
		
		try {
			con = DatabaseConnection.getLineage();

			// 제거.
			st = con.prepareStatement("SELECT state FROM pc_trade WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next())
				state = rs.getString(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : isTradeState(int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		return state;
	}
	/**
	 * 판매 게시글 삭제 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public void toDelete(object o, StringTokenizer st) {
		PcInstance pc = (PcInstance) o;
		
		try {
			int uid = Integer.valueOf(st.nextToken());
			
			if (!isTradeUid(uid)) {
				ChattingController.toChatting(pc, "\\fR해당 게시물이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!isTradeSell(pc, uid)) {
				ChattingController.toChatting(pc, String.format("\\fR%d번 판매물품의 판매자가 아닙니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!isTradeState(uid).equalsIgnoreCase(STATE_SALE)) {
				ChattingController.toChatting(pc, "\\fR판매중인 게시글만 삭제 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			toDeleteFinal(pc, uid);
		} catch (Exception e) {
			ChattingController.toChatting(pc, Lineage.command + "판매취소 거래번호", Lineage.CHATTING_MODE_MESSAGE);
		}		
	}
	
	/**
	 * 판매 게시글 최종 삭제 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public void toDeleteFinal(PcInstance pc, int uid) {
		Connection con = null;
		PreparedStatement stt = null;
		ResultSet rs = null;
		
		try {
			con = DatabaseConnection.getLineage();
			
			stt = con.prepareStatement("SELECT * FROM pc_trade WHERE uid=?");
			stt.setInt(1, uid);
			rs = stt.executeQuery();
			
			if (rs.next()) {
				Item i = ItemDatabase.find(rs.getString("item"));
				long objId = rs.getLong("item_objId");
				
				if (objId == 0) {
					objId = ServerDatabase.nextItemObjId();
				}
				
				if (i != null) {
					int en = rs.getInt("enchant");
					int bless = rs.getInt("bless");
					int count = rs.getInt("count");
					
					ItemInstance temp = pc.getInventory().find(i.getName(), bless, i.isPiles());
					
					if (temp == null) {
						// 겹칠수 있는 아이템이 존재하지 않을경우.
						if (i.isPiles()) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(objId);
							temp.setBless(bless);
							temp.setEnLevel(en);
							temp.setCount(count);
							temp.setDefinite(true);
							pc.getInventory().append(temp, true);
						} else {
							for (int idx = 0; idx < count; idx++) {
								temp = ItemDatabase.newInstance(i);
								temp.setObjectId(objId);
								temp.setBless(bless);
								temp.setEnLevel(en);
								temp.setDefinite(true);
								pc.getInventory().append(temp, true);
							}
						}
					} else {
						// 겹치는 아이템이 존재할 경우.
						pc.getInventory().count(temp, temp.getCount() + count, true);
					}
					
					// 알림.
					if (pc != null) {
						if (temp.getItem().getType1().equalsIgnoreCase("weapon") || temp.getItem().getType1().equalsIgnoreCase("armor"))
							ChattingController.toChatting(pc, String.format("\\fY[판매 취소] %s+%d %s(%d) 획득", changeBless(bless), en, i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
						else
							ChattingController.toChatting(pc, String.format("\\fY[판매 취소] %s%s(%d) 획득", changeBless(bless), i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
					}
				}
			}
			stt.close();

			// 제거.
			stt = con.prepareStatement("DELETE FROM pc_trade WHERE uid=?");
			stt.setInt(1, uid);
			stt.executeUpdate();
			stt.close();
			
			stt = con.prepareStatement("DELETE FROM boards WHERE type=? AND uid=?");
			stt.setString(1, BackgroundDatabase.getTradeBoard().getType());
			stt.setInt(2, uid);
			stt.executeUpdate();
			
			ChattingController.toChatting(pc, String.format("\\fR%d번 판매글이 삭제되었습니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
		} catch (Exception e) {
			lineage.share.System.printf("%s : toDeleteFinal(PcInstance pc, int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, stt, rs);
		}
	}
	
	
	/**
	 * 등록한 판매글 목록 확인 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public void saleList(object o) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		PcInstance pc = (PcInstance) o;
		
		try {
			con = DatabaseConnection.getLineage();

			// 제거.
			st = con.prepareStatement("SELECT * FROM pc_trade WHERE sell_account_uid=? AND NOT state=?");
			st.setLong(1, pc.getAccountUid());
			st.setString(2, STATE_COMPLETE);
			rs = st.executeQuery();
			
			ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ판매 신청 목록ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
			
			while (rs.next()) {			
				ChattingController.toChatting(o, String.format("거래번호: [%d] 거래상황: [%s] 가격: [%s원]", 
						rs.getInt("uid"), rs.getString("state"), PcTradeController.changePrice(rs.getInt("price"))), Lineage.CHATTING_MODE_MESSAGE);
				
				if (rs.getInt("enchant") > 0) 
					ChattingController.toChatting(o, String.format("아이템(수량): %s+%d %s(%d) 판매 캐릭터: [%s]", PcTradeController.changeBless(rs.getInt("bless")), rs.getInt("enchant"), rs.getString("item"), rs.getInt("count"), rs.getString("sell_name")), Lineage.CHATTING_MODE_MESSAGE);
				else
					ChattingController.toChatting(o, String.format("아이템(수량): %s%s(%d) 판매 캐릭터: [%s]", PcTradeController.changeBless(rs.getInt("bless")), rs.getString("item"), rs.getInt("count"), rs.getString("sell_name")), Lineage.CHATTING_MODE_MESSAGE);
				
				ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : tradeList(object o)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * 구매신청한 판매글 목록 확인 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public void buyList(object o) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		PcInstance pc = (PcInstance) o;
		
		try {
			con = DatabaseConnection.getLineage();

			// 제거.
			st = con.prepareStatement("SELECT * FROM pc_trade WHERE buy_account_uid=? AND NOT state=?");
			st.setLong(1, pc.getAccountUid());
			st.setString(2, STATE_COMPLETE);
			rs = st.executeQuery();
			
			ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ구매 신청 목록ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
			
			while (rs.next()) {			
				if (rs.getInt("enchant") > 0) 
					ChattingController.toChatting(o, String.format("거래번호: [%d] 가격: [%s원] 아이템(수량): %s+%d %s(%d)", 
							rs.getInt("uid"), PcTradeController.changePrice(rs.getInt("price")), PcTradeController.changeBless(rs.getInt("bless")), rs.getInt("enchant"), rs.getString("item"), rs.getInt("count")), Lineage.CHATTING_MODE_MESSAGE);
				else
					ChattingController.toChatting(o, String.format("거래번호: [%d] 가격: [%s원] 아이템(수량): %s%s(%d)", 
							rs.getInt("uid"), PcTradeController.changePrice(rs.getInt("price")), PcTradeController.changeBless(rs.getInt("bless")), rs.getString("item"), rs.getInt("count")), Lineage.CHATTING_MODE_MESSAGE);
				
				ChattingController.toChatting(o, String.format("구매 신청 캐릭터: [%s]", rs.getString("buy_name")), Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : tradeList(object o)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * 구매신청 처리 메소드.
	 * 2018-07-20
	 * by connector12@nate.com
	 */
	static public void buyItem(object o, StringTokenizer st) {
		PcInstance pc = (PcInstance) o;
		
		try {
			int uid = Integer.valueOf(st.nextToken());
			
			if (o.getLevel() < Lineage.pc_trade_buy_level) {
				ChattingController.toChatting(o, String.format("구매 신청은 %d레벨 이상 가능합니다.", Lineage.pc_trade_buy_level), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!isTradeUid(uid)) {
				ChattingController.toChatting(pc, "\\fR해당 물품이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (isTradeSell(pc, uid)) {
				ChattingController.toChatting(pc, "\\fR자신이 등록한 판매물품은 구매신청할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			int count = accountBuyCount(pc);
			if (Lineage.pc_trade_buy_max_count > 0 && Lineage.pc_trade_buy_max_count <= count) {
				ChattingController.toChatting(o, String.format("구매신청은 최대 %d개 등록 가능합니다. 현재 구매신청: %d개", Lineage.pc_trade_buy_max_count, count), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!isTradeState(uid).equalsIgnoreCase(STATE_SALE)) {
				ChattingController.toChatting(pc, "\\fR판매중인 게시글만 구매신청 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			buyItemFinal(pc, uid);
		} catch (Exception e) {
			ChattingController.toChatting(pc, Lineage.command + "구매신청 거래번호", Lineage.CHATTING_MODE_MESSAGE);
		}		
	}
	
	/**
	 * 해당 물품의 판매자 정보 확인 메소드.
	 * 2018-07-23
	 * by connector12@nate.com
	 */
	static public void buyTradeInfo(object o, StringTokenizer st) {
		PcInstance pc = (PcInstance) o;
		
		try {
			int uid = Integer.valueOf(st.nextToken());
			
			if (!isTradeUid(uid)) {
				ChattingController.toChatting(pc, "\\fR해당 게시물이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (pc.getGm() == 0 && !isTradeBuy(pc, uid)) {
				ChattingController.toChatting(pc, String.format("\\fR%d번 물품의 구매 신청자가 아닙니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			buyTradeInfoFinal(pc, uid);
		} catch (Exception e) {
			ChattingController.toChatting(pc, Lineage.command + "구매정보 거래번호", Lineage.CHATTING_MODE_MESSAGE);
		}		
	}
	
	/**
	 * 해당 물품의 판매자 정보 확인 최종 메소드.
	 * 2018-07-23
	 * by connector12@nate.com
	 */
	static public void buyTradeInfoFinal(PcInstance pc, int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			con = DatabaseConnection.getLineage();

			st = con.prepareStatement("SELECT * FROM pc_trade WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next())		
				ChattingController.toChatting(pc, String.format("거래번호: %d 이름: %s 연락처: %s 은행명: %s 계좌번호: %s", uid, rs.getString("name"), rs.getString("phone_num"), 
						rs.getString("bank_name"), rs.getString("bank_num")), Lineage.CHATTING_MODE_MESSAGE);
		} catch (Exception e) {
			lineage.share.System.printf("%s : buyTradeInfo(object o, StringTokenizer st)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * 구매신청 최종 처리 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public void buyItemFinal(PcInstance pc, int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String content = null;
		String subject = null;

		try {
			con = DatabaseConnection.getLineage();
			
			st = con.prepareStatement("SELECT * FROM pc_trade WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next()) {
				PcTrade pt = new PcTrade();
				
				pt.setUid(uid);
				pt.setSell_name(rs.getString("sell_name"));
				pt.setBuy_name(rs.getString("buy_name"));
				pt.setState(STATE_TRADE);
				pt.setPrice(rs.getInt("price"));
				pt.setBless(rs.getInt("bless"));
				pt.setEnchant(rs.getInt("enchant"));
				pt.setItem_objId(rs.getLong("item_objId"));
				pt.setItem(rs.getString("item"));
				pt.setCount(rs.getLong("count"));
				pt.setSubject(rs.getString("subject"));

				content = changeContent(pt);
				subject = changeSubject(pt.getState(), pt.getSubject());
			}
			st.close();
			
			st = con.prepareStatement("UPDATE pc_trade SET state=?, buy_account_uid=?, buy_name=?, buy_objId=?, buy_apply_day=? WHERE uid=?");
			st.setString(1, STATE_TRADE);
			st.setInt(2, pc.getAccountUid());
			st.setString(3, pc.getName());
			st.setLong(4, pc.getObjectId());
			st.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
			st.setInt(6, uid);
			st.executeUpdate();

			sendLetter(uid, STATE_BUY, false);
			buyItemUpdateBoard(pc, uid, subject, content, STATE_BUY);
		} catch (Exception e) {
			lineage.share.System.printf("%s : buyItemFinal(PcInstance pc, int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * 게시판 거래상황 갱신 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public void buyItemUpdateBoard(PcInstance pc, int uid, String subject, String content, String state) {
		Connection con = null;
		PreparedStatement st = null;
		
		try {
			con = DatabaseConnection.getLineage();
			
			BoardInstance tradeBoard = BackgroundDatabase.getTradeBoard();
			if (Lineage.is_pc_trade_success_delete && state.equalsIgnoreCase(STATE_COMPLETE)) {
				st = con.prepareStatement("DELETE FROM boards WHERE type=? AND uid=?");
				st.setString(1, tradeBoard.getType());
				st.setInt(2, uid);
				st.executeUpdate();
			} else {
				st = con.prepareStatement("UPDATE boards SET subject=?, memo=? WHERE type=? AND uid=?");
				st.setString(1, subject);
				st.setString(2, content);
				st.setString(3, tradeBoard.getType());
				st.setInt(4, uid);
				st.executeUpdate();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : buyItemUpdateBoard(PcInstance pc, int uid, String subject, String content)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
		
		if (pc != null) {
			switch (state) {
			case STATE_BUY:
				ChattingController.toChatting(pc, String.format("%d번 물품 구매신청 완료하였습니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
				break;
			case STATE_BUY_CANCEL:
				ChattingController.toChatting(pc, String.format("%d번 물품 구매취소 완료하였습니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
				break;
			case STATE_DEPOSIT:
				ChattingController.toChatting(pc, String.format("%d번 물품 입금 완료하였습니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
				break;
			case STATE_COMPLETE:
				ChattingController.toChatting(pc, String.format("%d번 물품 거래 완료하였습니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
				break;
			}
		}
	}
	
	/**
	 * 거래번호로 해당 거래내용 가져오는 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public PcTrade getTradeInfo(int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		PcTrade pt = null;

		try {
			con = DatabaseConnection.getLineage();		
			st = con.prepareStatement("SELECT * FROM pc_trade WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next()) {
				pt = new PcTrade();			
				pt.setUid(rs.getInt("uid"));
				pt.setSell_account_uid(rs.getInt("sell_account_uid"));
				pt.setSell_name(rs.getString("sell_name"));
				pt.setSell_objId(rs.getLong("sell_objId"));
				pt.setState(rs.getString("state"));
				pt.setBuy_account_uid(rs.getInt("buy_account_uid"));
				pt.setBuy_name(rs.getString("buy_name"));
				pt.setBuy_objId(rs.getLong("buy_objId"));
				pt.setPrice(rs.getInt("price"));
				pt.setItem_objId(rs.getLong("item_objId"));
				pt.setItem(rs.getString("item"));		
				pt.setEnchant(rs.getInt("enchant"));
				pt.setBless(rs.getInt("bless"));
				pt.setCount(rs.getLong("count"));
				pt.setWrite_day(rs.getTimestamp("write_day").getTime());
				
				if (pt.getState().equalsIgnoreCase(STATE_BUY) || pt.getState().equalsIgnoreCase(STATE_TRADE) || pt.getState().equalsIgnoreCase(STATE_DEPOSIT) || pt.getState().equalsIgnoreCase(STATE_COMPLETE))
					pt.setBuy_apply_day(rs.getTimestamp("buy_apply_day").getTime());
				
				if (pt.getState().equalsIgnoreCase(STATE_COMPLETE))
					pt.setComplete_day(rs.getTimestamp("complete_day").getTime());
				
				pt.setSubject(rs.getString("subject"));
				pt.setContent(rs.getString("content"));
				pt.setName(rs.getString("name"));
				pt.setPhone_num(rs.getString("phone_num"));
				pt.setBank_name(rs.getString("bank_name"));
				pt.setBank_num(rs.getString("bank_num"));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : getTradeInfo(int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		return pt;
	}
	
	/**
	 * 편지 내용 작성 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public String creatLetterContent(PcTrade pt, String state, boolean seller, boolean autoCancel) {
		String temp_content = null;
		StringBuilder content = new StringBuilder();
		Item i = ItemDatabase.find(pt.getItem());
		
		content.append(String.format("거래번호: %d\n", pt.getUid()));
		
		if (Lineage.is_pc_trade_sell_name) {
			content.append(String.format("판매자: %s\n", pt.getSell_name()));
			content.append(String.format("구매자: %s\n", pt.getBuy_name()));
		}
		
		content.append(String.format("거래상황: %s\n", pt.getState()));
		content.append(String.format("판매금액: %s원\n", changePrice(pt.getPrice())));

		if (i != null) {
			String itemName = CharacterMarbleDatabase.getItemName(pt.getItem_objId());
			if (itemName != null) {
				content.append(String.format("아 이 템: %s\n", itemName));
			} else {
				if (i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor"))
					content.append(String.format("아 이 템: %s+%d %s\n", changeBless(pt.getBless()), pt.getEnchant(), pt.getItem()));
				else
					content.append(String.format("아 이 템: %s%s\n", changeBless(pt.getBless()), pt.getItem()));
			}
		}
		
		if (pt.getItem().equalsIgnoreCase("아데나"))
			content.append(String.format("수    량: %s아데나\n", changePrice((int) pt.getCount())));
		else
			content.append(String.format("수    량: %s개\n", changePrice((int) pt.getCount())));
		
		switch (state) {
		case STATE_BUY:			
			if (seller) {
				content.append(String.format("-------------------------\n", pt.getCount()));
				content.append("해당 물품의 구매신청이 접수되었습니다.\n");
				content.append("구매자가 입금하신 경우 [.입금확인]으로 거래를 완료하시기 바랍니다.\n");
				content.append("부득이 하게 [.입금확인]을 할 수 없을경우 운영자에게 연락주시기 바랍니다.\n");
				content.append(String.format("-------------------------\n", pt.getCount()));
			} else {
				content.append(String.format("-------판매자 정보-------\n", pt.getCount()));
				content.append(String.format("이    름: %s\n", pt.getName()));
				content.append(String.format("연 락 처: %s\n", pt.getPhone_num()));
				content.append(String.format("은 행 명: %s\n", pt.getBank_name()));
				content.append(String.format("계좌번호: %s\n", pt.getBank_num()));
				content.append(String.format("-------------------------\n", pt.getCount()));
				
				if (Lineage.pc_trade_buy_deposit_delay > 0)
					content.append(String.format("구매 신청 후 %d분 이내에 [.입금완료]를 하시지 않을 경우 구매가 취소됩니다.", Lineage.pc_trade_buy_deposit_delay));
				else
					content.append("입금을 완료하시면 [.입금완료]를 입력해 주시기 바랍니다.");
			}
			break;
		case STATE_BUY_CANCEL:
			if (seller) {
				if (autoCancel) {
					content.append(String.format("-------------------------\n", pt.getCount()));
					content.append("구매자의 입금완료 시간이 경과하여 거래가 취소되었습니다.\n");
					content.append("취소된 거래는 다시 [판매중]으로 돌아갑니다.\n");
					content.append(String.format("-------------------------\n", pt.getCount()));
				} else {
					content.append(String.format("-------------------------\n", pt.getCount()));
					content.append("해당 물품이 구매 취소되었습니다.\n");
					content.append("취소된 거래는 다시 [판매중]으로 돌아갑니다.\n");
					content.append(String.format("-------------------------\n", pt.getCount()));
				}
			} else {
				if (autoCancel) {
					content.append(String.format("-------------------------\n", pt.getCount()));
					content.append(String.format("구매 신청 후 %d분이 경과하여 구매가 취소되었습니다.", Lineage.pc_trade_buy_deposit_delay));
					content.append(String.format("-------------------------\n", pt.getCount()));
				} else {
					content.append(String.format("-------------------------\n", pt.getCount()));
					content.append("해당 물품 구매 취소 완료되었습니다.\n");
					content.append(String.format("-------------------------\n", pt.getCount()));
				}
			}
			break;
		case STATE_DEPOSIT:
			if (seller) {
				content.append(String.format("-------------------------\n", pt.getCount()));
				content.append("구매자가 입금 완료하였습니다.\n");
				content.append("입금 확인하신 후 [.입금확인]을 해주시기 바랍니다.\n");
				content.append(String.format("-------------------------\n", pt.getCount()));
			} else {
				content.append(String.format("-------------------------\n", pt.getCount()));
				content.append("판매자가 입금확인을 할 경우 거래가 완료됩니다.\n");
				content.append("장시간 입금확인을 하지 않을 경우 운영자에게 문의주시기 바랍니다.\n");
				content.append(String.format("-------------------------\n", pt.getCount()));
			}
			break;
		case STATE_COMPLETE:
			if (seller) {
				content.append(String.format("-------------------------\n", pt.getCount()));
				content.append("거래가 완료되었습니다.\n");
				content.append(String.format("-------------------------\n", pt.getCount()));
			} else {
				content.append(String.format("-------------------------\n", pt.getCount()));
				content.append("거래가 완료되었습니다.\n");
				content.append("해당 물품이 인벤토리에 지급됩니다.\n");
				content.append(String.format("-------------------------\n", pt.getCount()));
			}		
			break;
		}
		
		temp_content = content.toString();
		
		return temp_content;
	}
	
	/**
	 * 거래상황을 편지 보내는 메소드.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public void sendLetter(int uid, String state, boolean autoBuyCancel) {
		PcTrade pt = getTradeInfo(uid);
		
		switch (state) {
		case STATE_BUY:
			LetterController.toLetter(PC_TRADE, pt.getSell_name(), String.format("%d번 물품 구매신청", pt.getUid()), creatLetterContent(pt, STATE_BUY, true, autoBuyCancel), 0);
			LetterController.toLetter(PC_TRADE, pt.getBuy_name(), String.format("%d번 물품 구매신청", pt.getUid()), creatLetterContent(pt, STATE_BUY, false, autoBuyCancel), 0);
			break;
		case STATE_BUY_CANCEL:
			LetterController.toLetter(PC_TRADE, pt.getSell_name(), String.format("%d번 물품 구매취소", pt.getUid()), creatLetterContent(pt, STATE_BUY_CANCEL, true, autoBuyCancel), 0);
			LetterController.toLetter(PC_TRADE, pt.getBuy_name(), String.format("%d번 물품 구매취소", pt.getUid()), creatLetterContent(pt, STATE_BUY_CANCEL, false, autoBuyCancel), 0);
			break;
		case STATE_DEPOSIT:
			LetterController.toLetter(PC_TRADE, pt.getSell_name(), String.format("%d번 물품 입금완료", pt.getUid()), creatLetterContent(pt, STATE_DEPOSIT, true, autoBuyCancel), 0);
			LetterController.toLetter(PC_TRADE, pt.getBuy_name(), String.format("%d번 물품 입금완료", pt.getUid()), creatLetterContent(pt, STATE_DEPOSIT, false, autoBuyCancel), 0);
			break;
		case STATE_COMPLETE:
			LetterController.toLetter(PC_TRADE, pt.getSell_name(), String.format("%d번 물품 거래완료", pt.getUid()), creatLetterContent(pt, STATE_COMPLETE, true, autoBuyCancel), 0);
			LetterController.toLetter(PC_TRADE, pt.getBuy_name(), String.format("%d번 물품 거래완료", pt.getUid()), creatLetterContent(pt, STATE_COMPLETE, false, autoBuyCancel), 0);
			break;
		}
	}
	
	/**
	 * 캐릭터 이름 변경 주문서 사용시 호출.
	 * 2018-07-21
	 * by connector12@nate.com
	 */
	static public void changeName(long objId, String newName) {
		Connection con = null;
		PreparedStatement st = null;
		
		try {
			con = DatabaseConnection.getLineage();

			st = con.prepareStatement("UPDATE pc_trade SET sell_name=? WHERE sell_objId=?");
			st.setString(1, newName);
			st.setLong(2, objId);
			st.executeUpdate();
			st.close();
			
			st = con.prepareStatement("UPDATE pc_trade SET buy_name=? WHERE buy_objId=?");
			st.setString(1, newName);
			st.setLong(2, objId);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : changeName(long objId, String newName)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	/**
	 * 구매취소 메소드.
	 * 2018-07-22
	 * by connector12@nate.com
	 */
	static public void buyCancel(object o, StringTokenizer st) {
		PcInstance pc = (PcInstance) o;
		
		try {
			int uid = Integer.valueOf(st.nextToken());
			
			if (!isTradeUid(uid)) {
				ChattingController.toChatting(pc, "\\fR해당 게시물이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (pc.getGm() == 0 && !isTradeBuy(pc, uid)) {
				ChattingController.toChatting(pc, String.format("\\fR%d번 물품의 구매 신청자가 아닙니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!isTradeState(uid).equalsIgnoreCase(STATE_TRADE) && !isTradeState(uid).equalsIgnoreCase(STATE_DEPOSIT)) {
				ChattingController.toChatting(pc, "\\fR거래중인 게시글만 구매취소 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			buyCancelFinal(pc, uid, false);
		} catch (Exception e) {
			ChattingController.toChatting(pc, Lineage.command + "구매취소 거래번호", Lineage.CHATTING_MODE_MESSAGE);
		}		
	}
	
	/**
	 * 구매취소 최종 메소드.
	 * 2018-07-22
	 * by connector12@nate.com
	 */
	static public void buyCancelFinal(PcInstance pc, int uid, boolean autoCancel) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String content = null;
		String subject = null;

		try {
			con = DatabaseConnection.getLineage();
			
			st = con.prepareStatement("SELECT * FROM pc_trade WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next()) {
				PcTrade pt = new PcTrade();
				
				pt.setUid(uid);
				pt.setSell_name(rs.getString("sell_name"));
				pt.setState(STATE_SALE);
				pt.setPrice(rs.getInt("price"));
				pt.setBless(rs.getInt("bless"));
				pt.setEnchant(rs.getInt("enchant"));
				pt.setItem_objId(rs.getLong("item_objId"));
				pt.setItem(rs.getString("item"));
				pt.setCount(rs.getLong("count"));
				pt.setSubject(rs.getString("subject"));

				content = changeContent(pt);
				subject = changeSubject(pt.getState(), pt.getSubject());
			}
			st.close();
			
			sendLetter(uid, STATE_BUY_CANCEL, autoCancel);
			buyItemUpdateBoard(pc, uid, subject, content, STATE_BUY_CANCEL);
			
			st = con.prepareStatement("UPDATE pc_trade SET state=?, buy_account_uid=?, buy_name=?, buy_objId=?, buy_apply_day=? WHERE uid=?");
			st.setString(1, STATE_SALE);
			st.setInt(2, 0);
			st.setString(3, "");
			st.setLong(4, 0);
			st.setString(5, "0000-00-00 00:00:00");
			st.setInt(6, uid);
			st.executeUpdate();


		} catch (Exception e) {
			lineage.share.System.printf("%s : buyCancelFinal(PcInstance pc, int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * 입금완료 메소드.
	 * 2018-07-22
	 * by connector12@nate.com
	 */
	static public void depositComplete(object o, StringTokenizer st) {
		PcInstance pc = (PcInstance) o;
		
		try {
			int uid = Integer.valueOf(st.nextToken());
			
			if (!isTradeUid(uid)) {
				ChattingController.toChatting(pc, "\\fR해당 게시물이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (pc.getGm() == 0 && !isTradeBuy(pc, uid)) {
				ChattingController.toChatting(pc, String.format("\\fR%d번 물품의 구매 신청자가 아닙니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!isTradeState(uid).equalsIgnoreCase(STATE_TRADE)) {
				ChattingController.toChatting(pc, "\\fR거래중인 게시글만 입금완료 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			depositCompleteFinal(pc, uid);
		} catch (Exception e) {
			ChattingController.toChatting(pc, Lineage.command + "입금완료 거래번호", Lineage.CHATTING_MODE_MESSAGE);
		}		
	}
	
	/**
	 * 입금완료 최종 메소드.
	 * 2018-07-22
	 * by connector12@nate.com
	 */
	static public void depositCompleteFinal(PcInstance pc, int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String content = null;
		String subject = null;

		try {
			con = DatabaseConnection.getLineage();
			
			st = con.prepareStatement("SELECT * FROM pc_trade WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next()) {
				PcTrade pt = new PcTrade();
				
				pt.setUid(uid);
				pt.setSell_name(rs.getString("sell_name"));
				pt.setBuy_name(rs.getString("buy_name"));
				pt.setState(STATE_DEPOSIT);
				pt.setPrice(rs.getInt("price"));
				pt.setBless(rs.getInt("bless"));
				pt.setEnchant(rs.getInt("enchant"));
				pt.setItem_objId(rs.getLong("item_objId"));
				pt.setItem(rs.getString("item"));
				pt.setCount(rs.getLong("count"));
				pt.setSubject(rs.getString("subject"));

				content = changeContent(pt);
				subject = changeSubject(pt.getState(), pt.getSubject());
			}
			st.close();
			
			st = con.prepareStatement("UPDATE pc_trade SET state=?, buy_apply_day=? WHERE uid=?");
			st.setString(1, STATE_DEPOSIT);
			st.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			st.setInt(3, uid);
			st.executeUpdate();

			sendLetter(uid, STATE_DEPOSIT, false);
			buyItemUpdateBoard(pc, uid, subject, content, STATE_DEPOSIT);
		} catch (Exception e) {
			lineage.share.System.printf("%s : depositCompleteFinal(PcInstance pc, int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * 입금확인 메소드.
	 * 2018-07-22
	 * by connector12@nate.com
	 */
	static public void tradeComplete(object o, StringTokenizer st) {
		PcInstance pc = (PcInstance) o;
		
		try {
			int uid = Integer.valueOf(st.nextToken());
			
			if (!isTradeUid(uid)) {
				ChattingController.toChatting(pc, "\\fR해당 게시물이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (pc.getGm() == 0 && !isTradeSell(pc, uid)) {
				ChattingController.toChatting(pc, String.format("\\fR%d번 물품의 판매자가 아닙니다.", uid), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (isTradeState(uid).equalsIgnoreCase(STATE_COMPLETE)) {
				ChattingController.toChatting(pc, "\\fR거래가 완료된 물품입니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!isTradeState(uid).equalsIgnoreCase(STATE_DEPOSIT)) {
				ChattingController.toChatting(pc, "\\fR해당 물품은 입금완료 상태가 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			tradeCompleteFinal(pc, uid);
		} catch (Exception e) {
			ChattingController.toChatting(pc, Lineage.command + "입금확인 거래번호", Lineage.CHATTING_MODE_MESSAGE);
		}		
	}
	
	/**
	 * 입금확인 최종 메소드.
	 * 2018-07-22
	 * by connector12@nate.com
	 */
	static public void tradeCompleteFinal(PcInstance pc, int uid) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String content = null;
		String subject = null;
		String item = null;
		long buyObjId = 0;
		String buyName = null;
		long objId = 0;
		Item i = null;
		PcInstance buy = null;
		int en = 0;
		int bless = 0;
		int count = 0;
		PcTrade pt = null;

		try {
			con = DatabaseConnection.getLineage();
			
			st = con.prepareStatement("SELECT * FROM pc_trade WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			
			if (rs.next()) {
				pt = new PcTrade();
				
				pt.setUid(uid);
				pt.setSell_name(rs.getString("sell_name"));
				pt.setBuy_name(rs.getString("buy_name"));
				pt.setState(STATE_COMPLETE);
				pt.setPrice(rs.getInt("price"));
				pt.setBless(rs.getInt("bless"));
				pt.setEnchant(rs.getInt("enchant"));
				pt.setItem_objId(rs.getLong("item_objId"));
				pt.setItem(rs.getString("item"));
				pt.setCount(rs.getLong("count"));
				pt.setSubject(rs.getString("subject"));
				item = rs.getString("item");
				buyObjId = rs.getLong("buy_objId");
				buyName = rs.getString("buy_name");
				en = rs.getInt("enchant");
				bless = rs.getInt("bless");
				count = rs.getInt("count");

				content = changeContent(pt);
				subject = changeSubject(pt.getState(), pt.getSubject());
			}
			
			rs.close();
			st.close();
					
			st = con.prepareStatement("UPDATE pc_trade SET state=?, complete_day=?, content=? WHERE uid=?");
			st.setString(1, STATE_COMPLETE);
			st.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			st.setString(3, content);
			st.setInt(4, uid);
			st.executeUpdate();
			st.close();
		
			sendLetter(uid, STATE_COMPLETE, false);
			buyItemUpdateBoard(pc, uid, subject, content, STATE_COMPLETE);
			Log.appendPcTrade(getTradeInfo(uid));
			
			i = ItemDatabase.find(item);
			buy = World.findPc(buyObjId);
			
			if (i != null) {
				objId = pt == null || pt.getItem_objId() == 0 ? ServerDatabase.nextItemObjId() : pt.getItem_objId();
				
				if (buy != null) {
					ItemInstance temp = buy.getInventory().find(i.getName(), bless, i.isPiles());
					
					if (temp == null) {
						// 겹칠수 있는 아이템이 존재하지 않을경우.
						if (i.isPiles()) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(objId);
							temp.setBless(bless);
							temp.setEnLevel(en);
							temp.setCount(count);
							temp.setDefinite(true);
							buy.getInventory().append(temp, true);
						} else {
							for (int idx = 0; idx < count; idx++) {
								temp = ItemDatabase.newInstance(i);
								temp.setObjectId(objId);
								temp.setBless(bless);
								temp.setEnLevel(en);
								temp.setDefinite(true);
								buy.getInventory().append(temp, true);
							}
						}
					} else {
						// 겹치는 아이템이 존재할 경우.
						buy.getInventory().count(temp, temp.getCount() + count, true);
					}
					
					// 알림.
					if (buy != null) {
						if (temp.getItem().getType1().equalsIgnoreCase("weapon") || temp.getItem().getType1().equalsIgnoreCase("armor"))
							ChattingController.toChatting(buy, String.format("\\fY[거래 완료] %s+%d %s(%d) 획득", changeBless(bless), en, i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
						else
							ChattingController.toChatting(buy, String.format("\\fY[거래 완료] %s%s(%d) 획득", changeBless(bless), i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
					}
				} else {							
					// 겹칠수 있는 아이템일 경우.
					if (i.isPiles()) {
						st = con.prepareStatement("SELECT * FROM characters_inventory WHERE cha_objId=? AND name=? AND en=? AND bress=?");
						st.setLong(1, buyObjId);
						st.setString(2, item);
						st.setInt(3, en);
						st.setInt(4, bless);
						rs = st.executeQuery();
						
						if (rs.next()) {
							long tempCount = rs.getLong("count");
							
							st.close();
							st = con.prepareStatement("UPDATE characters_inventory SET count=? WHERE cha_objId=? AND name=? AND en=? AND bress=?");
							st.setLong(1, tempCount + count);
							st.setLong(2, buyObjId);
							st.setString(3, item);
							st.setInt(4, en);
							st.setInt(5, bless);
							st.executeUpdate();
						} else {
							st.close();
							st = con.prepareStatement("INSERT INTO characters_inventory SET objId=?, cha_objId=?, cha_name=?, name=?, count=?, en=?, definite=?, bress=?, 구분1=?, 구분2=?");
							st.setLong(1, objId);
							st.setLong(2, buyObjId);
							st.setString(3, buyName);
							st.setString(4, item);
							st.setLong(5, count);
							st.setInt(6, en);
							st.setInt(7, 1);
							st.setInt(8, bless);
							st.setString(9, i.getType1());
							st.setString(10, i.getType2());
							st.executeUpdate();
						}
					} else {
						st = con.prepareStatement("INSERT INTO characters_inventory SET objId=?, cha_objId=?, cha_name=?, name=?, count=?, en=?, definite=?, bress=?, 구분1=?, 구분2=?");
						st.setLong(1, objId);
						st.setLong(2, buyObjId);
						st.setString(3, buyName);
						st.setString(4, item);
						st.setLong(5, count);
						st.setInt(6, en);
						st.setInt(7, 1);
						st.setInt(8, bless);
						st.setString(9, i.getType1());
						st.setString(10, i.getType2());
						st.executeUpdate();
						st.close();
					}
				}
			}
			
			if (Lineage.is_pc_trade_success_delete) {
				st = con.prepareStatement("DELETE FROM pc_trade WHERE uid=?");
				st.setInt(1, uid);
				st.executeUpdate();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : tradeCompleteFinal(PcInstance pc, int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * 일정 기간 경과해도 입금완료 하지 않는 거래 구매취소 처리 메소드. 
	 * 2018-07-23
	 * by connector12@nate.com
	 */
	static public void toTimer(long time) {
		if (checkTime <= time && Lineage.pc_trade_buy_deposit_delay > 0) {
			// 10초 마다 체크
			checkTime = time + (1000 * 10);
			
			Connection con = null;
			PreparedStatement st = null;
			ResultSet rs = null;

			try {
				con = DatabaseConnection.getLineage();
				
				st = con.prepareStatement("SELECT * FROM pc_trade WHERE state=?");
				st.setString(1, STATE_TRADE);
				rs = st.executeQuery();
				
				while (rs.next()) {
					if (rs.getTimestamp("buy_apply_day").getTime() + (1000 * 60 * Lineage.pc_trade_buy_deposit_delay) < time)
						buyCancelFinal(null, rs.getInt("uid"), true);
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : toTimer(long time)\r\n", PcTradeController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}	
		}
	}	
}
