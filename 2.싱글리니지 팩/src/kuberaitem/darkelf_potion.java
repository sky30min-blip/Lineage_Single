package kuberaitem;

import lineage.database.CharactersDatabase;
import lineage.network.LineageServer;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.share.Lineage;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class darkelf_potion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new darkelf_potion();
		return item;
	}
	
	public void toClick(Character cha, ClientBasePacket cbp){
	
		cha.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 774));

	}
	
	public void toAsk(PcInstance pc, boolean yes){
		if (yes) {
			long objId = pc.getObjectId();
			int sex = pc.getClassSex();
			int classGfx = pc.getClassGfx();
			

			if(pc.getClassSex() == 0){


				pc.setClassType(Lineage.LINEAGE_CLASS_DARKELF);
				pc.setGfx(Lineage.darkelf_male_gfx);
				pc.setClassGfx(Lineage.darkelf_male_gfx);
				pc.setClassSex(0);
				classGfx = Lineage.darkelf_male_gfx;
				sex = 0;

			}else{

				pc.setClassType(Lineage.LINEAGE_CLASS_DARKELF);
				pc.setGfx(Lineage.darkelf_female_gfx);
				pc.setClassGfx(Lineage.darkelf_female_gfx);
				pc.setClassSex(1);
				classGfx = Lineage.darkelf_female_gfx;
				sex = 1;
			}
			for (ItemInstance i : pc.getInventory().getList()) {
				if (i != null && i.isEquipped()) {
					i.toClick(pc, null);
				}
		
			}
			try {

				Thread.sleep(1000); //1초 대기

				pc.getInventory().count(this, getCount() - 1, true);

				CharacterController.toResetStat(pc, classType);
				
				// 사용자 강제종료 시키기.
				pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
				LineageServer.close(pc.getClient());
				
				CharactersDatabase.classChange(objId, sex, 4, classGfx);	

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

		}
	}
}
