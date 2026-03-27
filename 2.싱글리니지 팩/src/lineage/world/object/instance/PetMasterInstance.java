package lineage.world.object.instance;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Summon;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_WareHousePet;
import lineage.share.Lineage;
import lineage.world.controller.SummonController;
import lineage.world.object.object;
import lineage.world.object.item.DogCollar;

public class PetMasterInstance extends object {
	
	private Npc npc;
	
	public PetMasterInstance(Npc npc){
		this.npc = npc;
	}
	
	@Override
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp){
		if(cbp.isRead(1) == false)
			return;
		
		switch(cbp.readC()){
			case 12:
				// 펫 찾기.
				toGetFinal(pc, cbp);
				break;
		}
	}
	
	/**
	 * 펫 맡기기 처리.
	 * @param pc
	 * @return
	 */
	protected boolean toPush(PcInstance pc){
		Summon s = SummonController.find(pc);
		if(s != null){
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				// 모든 펫 저장.
				SummonController.toSave(con, pc);
				// 모든 펫 제거 하면서 펫목걸이도 갱신.
				return s.removeAllPet();
			} catch (Exception e) {
				lineage.share.System.println(PetMasterInstance.class.toString()+" : toPush(PcInstance pc)");
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con);
			}
		}
		return false;
	}
	
	/**
	 * 펫 찾기 처리.
	 * @param pc
	 * @return
	 */
	protected boolean toGet(PcInstance pc){
		// 펫목걸이 찾기.
		List<ItemInstance> temp_list = new ArrayList<ItemInstance>();
		pc.getInventory().findDbNameId(1173, temp_list);
		if(temp_list.size() > 0){
			// 펫 목걸이 중 스폰안된것만 필터링.
			List<DogCollar> temp2_list = new ArrayList<DogCollar>();
			for(ItemInstance ii : temp_list){
				if(ii instanceof DogCollar){
					DogCollar dc = (DogCollar)ii;
					if(!dc.isPetSpawn() && !SummonController.isDeletePet(dc.getPetObjectId()))
						temp2_list.add(dc);
				}
			}
			// 1개 이상일경우 패킷처리.
			if(temp2_list.size() > 0){
				pc.toSender( S_WareHousePet.clone(BasePacketPooling.getPool(S_WareHousePet.class), this, temp2_list) );
				return true;
			}
		}
		return false;
	}
	
	/**
	 * toGet 거쳐서 펫을선택하고 찾기누르면 이곳으로 옴.
	 * @param pc
	 * @param cbp
	 */
	private void toGetFinal(PcInstance pc, ClientBasePacket cbp){
		int count = cbp.readH();
		while(count-->0){
			int inv_id = cbp.readD();
			cbp.readD();
			boolean aden = pc.getInventory().isAden(Lineage.warehouse_pet_price, false);
			boolean pet = aden ? SummonController.toPet(pc, (DogCollar)pc.getInventory().value(inv_id)) : false;
			if( aden && pet ){
				// 아덴 감소
				pc.getInventory().isAden(Lineage.warehouse_pet_price, true);
			}else{
				if(!aden)
					// \f1아데나가 충분치 않습니다.
					pc.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189) );
				else if(!pet)
					// 찾으려고 하는 펫이 너무 많습니다.
					pc.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 489) );
				break;
			}
		}
	}
}
