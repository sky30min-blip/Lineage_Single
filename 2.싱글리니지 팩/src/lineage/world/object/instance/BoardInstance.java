package lineage.world.object.instance;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Auction;
import lineage.bean.lineage.Board;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_BoardAuctionList;
import lineage.network.packet.server.S_BoardAuctionMap;
import lineage.network.packet.server.S_BoardList;
import lineage.network.packet.server.S_Html;
import lineage.util.Util;
import lineage.world.controller.AgitController;
import lineage.world.controller.AuctionController;
import lineage.world.controller.BoardController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class BoardInstance extends object {

	private String type;
	private List<Auction> list_auction;
	private List<String> list_html;

	public BoardInstance(){
		list_auction = new ArrayList<Auction>();
		list_html = new ArrayList<String>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		// 목록 추출.
		AuctionController.getList(getType(), list_auction);
		if(list_auction.size()>0){
			// 패킷 처리
			pc.toSender(S_BoardAuctionList.clone(BasePacketPooling.getPool(S_BoardAuctionList.class), this, list_auction));
			// 메모리 정리.
			list_auction.clear();
		}else{
			// 패킷 처리
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "agnolist"));
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("select")){
			// 선택된 경매 물품 정보 추출.
			Auction a = AuctionController.find(Integer.valueOf(type));
			if(a != null){
				list_html.clear();
				list_html.add(AgitController.find(a.getAgitId()).getAgitName());
				list_html.add(a.getLoc());
				list_html.add(String.valueOf(a.getSize()));
				list_html.add(a.getAgent());
				list_html.add(a.getBidder());
				list_html.add(String.valueOf(a.getPrice()));
				list_html.add(String.valueOf(Util.getMonth(a.getDay())));
				list_html.add(String.valueOf(Util.getDate(a.getDay())));
				list_html.add(String.valueOf(Util.getHours(a.getDay())));
				// 경매물품 내용 보기
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "agsel", type, list_html));
			}

		}else if(action.equalsIgnoreCase("map")){
			pc.toSender(S_BoardAuctionMap.clone(BasePacketPooling.getPool(S_BoardAuctionMap.class), this, Integer.valueOf(type)));

		}else if(action.equalsIgnoreCase("apply")){
			AuctionController.toApply(this, pc, Integer.valueOf(type));

		}
	}

	@Override
	public void toHyperText(PcInstance pc, ClientBasePacket cbp){
		int count = cbp.readD(); 
		cbp.readC(); 
		int uid = Integer.valueOf(cbp.readS());
		AuctionController.toApplyFinal(this, pc, uid, count);
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		List<Board> list = new ArrayList<Board>();
		// 해당 게시판의 목록 추출.
		BoardController.getList(getType(), BoardController.getMaxUid(getType()), list, 8);
		// 패킷 처리
		cha.toSender(S_BoardList.clone(BasePacketPooling.getPool(S_BoardList.class), this, list));
		// 메모리 정리.
		for(Board b : list)
			BoardController.setPool(b);
		list.clear();
	}

	/**
	 * 원하는 페이지를 볼때 사용.
	 * @param pc
	 * @param bi
	 * @param page
	 */
	public void toPage(PcInstance pc, int page){
		List<Board> list = new ArrayList<Board>();
		// 해당 게시판의 목록 추출.
		BoardController.getList(getType(), page, list, 8);
		// 패킷 처리
		pc.toSender(S_BoardList.clone(BasePacketPooling.getPool(S_BoardList.class), this, list));
		// 메모리 정리.
		for(Board b : list)
			BoardController.setPool(b);
	}

}
