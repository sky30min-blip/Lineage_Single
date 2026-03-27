package lineage.world.object.item.yadolan;


import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.DamageController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.instance.SummonInstance;


public class PenguinHuntingStick extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new PenguinHuntingStick();
		return item;
	}

	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha != null && getItem() != null  && cha.getMap() == 63) {

			
			for (object o : cha.getInsideList()) {

					if (o != null && o.getNowHp() > 0 && o instanceof MonsterInstance&& !(o instanceof SummonInstance) && !(o instanceof PetInstance)
							&& Util.isDistance(cha, o, item.getSmallDmg())) {
							DamageController.toDamage(cha, o, 1500, Lineage.ATTACK_TYPE_MAGIC);
							

						
					}
				}			

				cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class),cha, 762),true);
			
			
		}else{
			ChattingController.toChatting(cha ,"펭귄서식지에서만 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

}
