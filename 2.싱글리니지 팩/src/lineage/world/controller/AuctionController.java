package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Agit;
import lineage.bean.lineage.Auction;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_HyperText;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.CharacterControlThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;

public final class AuctionController {

	static public List<Auction> list;
	static private int timer_counter;			// 타이머 1분마다 처리되도록 하기위해 사용되는 변수.
	static private long agitSaveTime;
	
	static public void init(Connection con){
		TimeLine.start("AuctionController..");
		
		timer_counter = 0;
		agitSaveTime = 0L;
		list = new ArrayList<Auction>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM boards_auction");
			rs = st.executeQuery();
			while(rs.next()){
				Auction a = new Auction();
				a.setAgitId(rs.getInt("uid"));
				a.setType(rs.getString("type"));
				a.setLoc(rs.getString("loc"));
				a.setSize(rs.getInt("size"));
				a.setSell(rs.getString("sell").equalsIgnoreCase("true"));
				a.setAgent(rs.getString("agent"));
				a.setBidder(rs.getString("bidder"));
				a.setPrice(rs.getInt("price"));
				a.setDefaultPrice(rs.getInt("default_price"));
				try { a.setDay(rs.getTimestamp("day").getTime()); } catch (Exception e) { }
				
				// 등록
				list.add(a);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", AuctionController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public void close(Connection con){
		for(Auction a : list)
			update(con, a);
		list.clear();
	}
	
	/**
	 * 해당 마을에 아지트 판매목록 만들어서 리턴.
	 * @param type		: 어떤 마을인지 구분
	 * @param r_list	: 리턴될 목록을 담을 변수
	 */
	static public void getList(String type, List<Auction> r_list){
		for(Auction a : list){
			if(a.isSell() && a.getType().equalsIgnoreCase(type))
				r_list.add(a);
		}
	}
	
	/**
	 * 아이트 값을 이용한 해당 정보 추출.
	 * @param uid
	 * @return
	 */
	static public Auction find(int uid){
		for(Auction a : list){
			if(a.getAgitId() == uid)
				return a;
		}
		return null;
	}
	
	/**
	 * 경매 입찰 요청 처리 함수.
	 * @param pc
	 * @param uid
	 */
	static public void toApply(BoardInstance bi, PcInstance pc, int uid){
		Auction a = find(uid);
		// 오류들 무시하기.
		if(a==null || !a.isSell())
			return;
		
		if((pc.getClassType()!=Lineage.LINEAGE_CLASS_ROYAL && pc.getClanGrade() != 3) || pc.getClanId()==0){
			// 이 명령은 혈맹 군주만이 이용할 수 있습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 518));
			return;
		}
		if(pc.getLevel() < 15){
			// 레벨 15 미만의 군주는 경매에 참여할 수 없습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 519));
			return;
		}
		if(isBidder(pc.getName())){
			// 이미 다른 집 경매에 참여하셨습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 523));
			return;
		}
		if(KingdomController.find(pc) != null){
			// 이미 성을 소유하고 있습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 520));
			return;
		}
		if(AgitController.find(pc) != null){
			// 이미 집을 소유하고 있습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 521));
			return;
		}
		
		// 현재 입찰 금액 추출.
		int price = a.getPrice() + 1;
		// 아데나 확인후 입찰금액 작성하는 창 띄움.
		if(price>0 && pc.getInventory().isAden(price, false))
			pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), bi, "agapply", String.valueOf(uid), 0, price, price, pc.getInventory().findAden().getCount(), null));
		else
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
	}
	
	/**
	 * 경매 입찰신청 마지막 처리 구간 함수.
	 * @param bi
	 * @param pc
	 * @param uid
	 * @param count
	 */
	static public void toApplyFinal(BoardInstance bi, PcInstance pc, int uid, int count){
		Auction a = find(uid);
		// 오류들 무시하기.
		if(bi==null || pc==null || uid==0 || count<=0 || a==null || !a.isSell())
			return;
		
		if(pc.getInventory().isAden(count, true)){
			// 이전입찰자에게 편지로 통보.
			if(a.getBidder()!=null && a.getBidder().length()>0)
				LetterController.toLetter("경매 관리자", a.getBidder(), "알려드립니다.", String.format("귀하가 제시하신 금액보다 더 많은 금액을 제시하신 분이 나타나 안타깝게도 입찰에 실패하게 되었습니다.\r\n귀하가 경매를 위해 예치하셨던 %s아데나를 반환해 드립니다.\r\n감사합니다.", Util.changePrice(a.getPrice())), a.getPrice());
			// 입찰자 정보 변경.
			a.setBidder(pc.getName());
			a.setPrice(count);
			ChattingController.toChatting(pc, String.format("%s아데나에 경매를 입찰하였습니다.", Util.changePrice(count)), Lineage.CHATTING_MODE_MESSAGE);
		}else{
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
		}
	}
	
	/**
	 * 다른 경매 물품 목록중 입찰자 명단에 해당 이름이 존재하는지 확인하는 함수.
	 * @param name
	 * @return
	 */
	static private boolean isBidder(String name){
		for(Auction a : list){
			if(a.isSell() && a.getBidder().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	/**
	 * 아지트 객체 정보 디비에 갱신처리 하는 함수.
	 * @param con
	 * @param a
	 */
	static public void update(Connection con, Auction a){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE boards_auction SET sell=?,agent=?,bidder=?,price=?,day=? WHERE uid=?");
			st.setString(1, a.isSell() ? "true" : "false");
			st.setString(2, a.getAgent() == null ? "" : a.getAgent());
			st.setString(3, a.getBidder() == null ? "" : a.getBidder());
			st.setInt(4, a.getPrice());
			st.setTimestamp(5, new java.sql.Timestamp(a.getDay()));
			st.setInt(6, a.getAgitId());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : update(Connection con, Auction a)\r\n", AuctionController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	static public void update(Auction a){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE boards_auction SET sell=?,agent=?,bidder=?,price=?,day=? WHERE uid=?");
			st.setString(1, a.isSell() ? "true" : "false");
			st.setString(2, a.getAgent());
			st.setString(3, a.getBidder());
			st.setInt(4, a.getPrice());
			st.setTimestamp(5, new java.sql.Timestamp(a.getDay()));
			st.setInt(6, a.getAgitId());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : update(Auction a)\r\n", AuctionController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	/**
	 * 타이머에서 주기적으로 호출.
	 * @param time
	 */
	static public void toTimer(long time){
		// 아지트 주기적으로 저장
		if (agitSaveTime == 0 || agitSaveTime <= System.currentTimeMillis()) {
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();
				
				if (AgitController.list != null) {
					for (Agit agit : AgitController.list)
						AgitController.update(con, agit);
				}
				
				if (AuctionController.list != null) {
					for (Auction auction : AuctionController.list)
						AuctionController.update(con, auction);
				}			
			} catch (Exception e) {
				lineage.share.System.printf("%s : 아지트 저장 에러.\r\n", AuctionController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con);
			}
	
			// 딜레이(초)
			agitSaveTime = System.currentTimeMillis() + (1000 * 30);
		}
		
		// 1분마다 처리하기 위해.
		if(++timer_counter%60 != 0)
			return;
		timer_counter = 0;
		
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			// 경매 완료되는 물품 확인처리.
			for(Auction a : list){
				// 경매 종료 처리 시간이 됫을경우.
				if(a.isSell() && a.getDay()<time){
					
					// 낙찰자 존재여부 확인.
					if(a.getBidder()!=null && a.getBidder().length()>0){
						// 옥션 낙찰에 대한 처리를 진행해도 된다면.
						if( isAuction(con, a.getBidder()) ){
							// 이전 소유자 확인.
							if(a.getAgent()!=null && a.getAgent().length()>0){
								// 편지지 발송.
								int aden = (int)(a.getPrice() - (a.getPrice()*0.1));
								LetterController.toLetter("경매 관리자", a.getAgent(), "알려드립니다.", String.format("귀하가 소유하고 있던 집이 최종가격 %s아데나에 낙찰되었습니다.\n수수료 10%%를 뺀 나머지 금액 %s아데나를 드리겠습니다.\n감사합니다.", Util.changePrice(a.getPrice()), Util.changePrice(a.getPrice())), aden);
							}
							
							// 축하 편지지 발송.
							LetterController.toLetter("경매 관리자", a.getBidder(), "축하합니다.", String.format("축하합니다.\n귀하께서 참여하신 경매는 최종가격 %s아데나의 가격으로 낙찰되었습니다.\n이제 귀하가 구매하신 집으로 가서 여러 가지 시설을 이용하실 수 있습니다.\n감사합니다.", Util.changePrice(a.getPrice())), 0);
							// 아지트 소유권 이전.
							a.setAgent(a.getBidder());
							a.setBidder("");
							a.setSell(false);
							a.setPrice(0);
							// 아지트객체에 사용자 정보 업데이트
							CharactersDatabase.updateAgit(con, a.getAgent(), AgitController.find(a.getAgitId()));
							continue;
//----------------------------------------------------------------
							
						}else{
							// 낙찰자의 상태가 불적합하여 낙찰 실패 처리 구간.
							LetterController.toLetter("경매 관리자", a.getBidder(), "알려드립니다.", "귀하가 참여하신 경매에 성공하셨으나 현재 집을 소유할 수 없는 상태에 있습니다.따라서 부득이하게 귀하의 소유권을 박탈하니, 양해해 주시기 바랍니다.\n감사합니다.", 0);
						}
					}
					
					// 실패했을경우 처리 구간.
					// 이전소유자가 있을경우.
					if(a.getAgent()!=null && a.getAgent().length()>0){
						if( isAuction(con, a.getAgent()) ){
							// 안내 편지지 발송.
							LetterController.toLetter("경매 관리자", a.getAgent(), "알려드립니다.", "귀하께서 신청하신 경매는 경매 기간동안 제시하신 금액 이상으로 지불의사를 밝히신 분이 나타나지 않아 결국 취소되었습니다.\n따라서, 소유권이 귀하에게로 돌아갔음을 알려드립니다.\n감사합니다.", 0);
							// 정보 초기화.
							a.setPrice(0);
							a.setBidder("");
							a.setSell(false);
							continue;
//----------------------------------------------------------------
							
						}
						// 안내 편지지 발송.
						LetterController.toLetter("경매 관리자", a.getAgent(), "알려드립니다.", "귀하께서 신청하신 경매는 경매 기간동안 제시하신 금액 이상으로 지불의사를 밝히신 분이 나타나지 않아 결국 취소되었습니다.\n하지만, 귀하께서는 현재 집을 소유할 수 없는 상태에 있기 때문에, 집의 소유권을 반환해 드릴수 없습니다.\n양해해 주시기 바랍니다.", 0);
						// 정보 초기화.
						a.setAgent("");
					}

					// 정보 초기화.
					a.setPrice(a.getDefaultPrice());
					a.setBidder("");
					// 경매 시간을 설정
					if(Lineage.auction_delay > 0)
						a.setDay(System.currentTimeMillis() + Lineage.auction_delay);
					else
						a.setDay(System.currentTimeMillis());
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : toTimer(long time)\r\n", AuctionController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
	}
	
	/**
	 * 경매 낙찰 처리하기전에 해당 함수를 호출.<br/>
	 *  : 낙찰 요청자의상태를 확인하여 정상 처리를 해도되는지 확인하기위해 체크해주는 함수.
	 * @param con
	 * @param name
	 * @return
	 */
	static private boolean isAuction(Connection con, String name){
		// 케릭터 존재여부 상태 확인.
		if(CharactersDatabase.isCharacterName(con, name) == false)
			return false;
		PcInstance use = World.findPc(name);
		if(use == null){
			// 디비에서 확인.
			// 클레스 확인.
			if(CharactersDatabase.getCharacterClass(con, name) != 0)
				return false;
			// 레벨 15이상인지 확인.
			if(CharactersDatabase.getCharacterLevel(con, name) < 15)
				return false;
			// 혈맹 확인.
			if(CharactersDatabase.getCharacterClanId(con, name) == 0)
				return false;
		}else{
			// 메모리에서 확인.
			// 클레스 확인.
			if(use.getClassType() != Lineage.LINEAGE_CLASS_ROYAL)
				return false;
			// 레벨 15이상인지 확인.
			if(use.getLevel() < 15)
				return false;
			// 혈맹 확인.
			if(use.getClanId() == 0)
				return false;
		}
		// 성 소유상태 확인.
		if(KingdomController.find(name)!=null)
			return false;
		return true;
	}
	
}
