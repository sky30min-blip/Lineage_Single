package lineage.network.packet.server;

import java.util.List;

import lineage.bean.lineage.Auction;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.util.Util;
import lineage.world.controller.AgitController;
import lineage.world.object.instance.BoardInstance;

public class S_BoardAuctionList extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, BoardInstance bi, List<Auction> list){
		if(bp == null)
			bp = new S_BoardAuctionList(bi, list);
		else
			((S_BoardAuctionList)bp).toClone(bi, list);
		return bp;
	}
	
	public S_BoardAuctionList(BoardInstance bi, List<Auction> list){
		toClone(bi, list);
	}
	
	public void toClone(BoardInstance bi, List<Auction> list){
		clear();
		
		writeC(Opcodes.S_OPCODE_Agit_List);
		writeD(bi.getObjectId());
		writeH(list.size());
		for(Auction a : list){
			writeD(a.getAgitId());	// 고유아지트 번호
			writeS(AgitController.find(a.getAgitId()).getAgitName());	// 이름
			writeH(a.getSize());										// 아지트 크기
			writeC(Util.getMonth(a.getDay()));							// 마감 월
			writeC(Util.getDate(a.getDay()));							// 마감 일
			writeD(a.getPrice());										// 가격
		}
	}
}
