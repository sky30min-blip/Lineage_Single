package lineage.network.packet.server;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Useshop;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class S_UserShopList extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, PcInstance use, Useshop us, boolean buy){
		if(bp == null)
			bp = new S_UserShopList(use, us, buy);
		else
			((S_UserShopList)bp).clone(use, us, buy);
		return bp;
	}
	
	public S_UserShopList(PcInstance use, Useshop us, boolean buy){
		clone(use, us, buy);
	}
	
	public void clone(PcInstance use, Useshop us, boolean buy){
		clear();

		writeC(Opcodes.S_OPCODE_RetreivePravateShop);
		writeC(buy ? 0 : 1);
		writeD(use.getObjectId());
		
		if(buy){
			writeH(us.getBuy().size());	// 총갯수
			for(int i=0 ; i<us.getBuy().size() ; ++i){
				ItemInstance ii = us.getBuy().get(i);
				writeC(i);									// 순번
				writeC(ii.getBless());						// 축 보통 저주
				writeH(ii.getItem().getInvGfx());			// gfx
				writeD(ii.getUsershopBuyCount());		// 갯수
				writeD(ii.getUsershopBuyPrice());		// 가격
				writeS(getName(ii));
				writeC(0x00);
			}
			
		}else{
			// 초기화.
			final List<ItemInstance> list = new ArrayList<ItemInstance>();
			// 추출.
			for(int i=0 ; i<us.getSell().size() ; ++i){
				ItemInstance ii = us.getSell().get(i);
				// 판매수량이 남앗을 경우만.
				if(ii.getUsershopSellCount() > 0){
					// 아이템 찾아보기.
					ItemInstance use_ii = use.getInventory().find(ii.getItem().getName(), ii.getEnLevel(), ii.getBless());
					// 존재할경우만.
					if(use_ii!=null && !use_ii.isEquipped()){
						int count = (int)(ii.getUsershopSellCount()>use_ii.getCount() ? use_ii.getCount() : ii.getUsershopSellCount());
						use_ii.setUsershopIdx(i);
						use_ii.setUsershopSellCount(count);
						use_ii.setUsershopSellPrice(ii.getUsershopSellPrice());
						
						list.add(use_ii);
					}
				}
			}
			// 처리.
			writeH(list.size());
			for(ItemInstance ii : list){
				writeC(ii.getUsershopIdx());		// 순번
				writeD(ii.getObjectId());			// 아이템아이디
				writeD(ii.getUsershopSellCount());	// 갯수
				writeD(ii.getUsershopSellPrice());	// 가격
			}
			list.clear();
		}
		
	}

}
