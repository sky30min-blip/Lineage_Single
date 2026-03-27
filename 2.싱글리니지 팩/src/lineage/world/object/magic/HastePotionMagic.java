package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class HastePotionMagic extends Magic {

	public HastePotionMagic(Skill skill) {
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time, boolean restart) {
		if (bi == null)
			bi = new HastePotionMagic(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setSpeed(1);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 0, o.getSpeed(), getTime()), true);
		// \f1갑자기 빠르게 움직입니다.
		o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 184));
	}

	@Override
	public void toBuffUpdate(object o) {
		o.setSpeed(1);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 0, o.getSpeed(), getTime()), true);
		// \f1다리에 새 힘이 솟습니다.
		o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 183));
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o.isWorldDelete())
			return;
		o.setSpeed(0);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 0, o.getSpeed(), 0), true);
		// \f1느려지는 것을 느낍니다.
		o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 185));
	}
	
	@Override
	public void toBuff(object o) {
		if (getTime() == 1)
			o.speedCheck = System.currentTimeMillis() + 2000;
	}

	static public void init(Character cha, int time, boolean restart) {
		if (cha.getSpeed() == 2) {
			// 슬로우 제거.
			BuffController.remove(cha, Slow.class);
			return;
		}
		
		// 무기중 광전사의 도끼를 착용하고 있을경우 처리를 하지 않는다.
		// 방패중 에바의 방패역시 처리하지 않는다.
		ItemInstance item1 = cha.getInventory()!=null ? cha.getInventory().getSlot(Lineage.SLOT_WEAPON) : null;
		ItemInstance item2 = cha.getInventory()!=null ? cha.getInventory().getSlot(Lineage.SLOT_SHIELD) : null;
		if( (item1!=null && item1.getItem().getNameIdNumber()==418) ||
			(item2!=null && item2.getItem().getNameIdNumber()==419)	){
			// 무시..
			return;
		}
		
		BuffController.remove(cha, Haste.class);
		
		// 버프 시간 중첩
		if (!restart)
			time = BuffController.addBuffTime(cha, SkillDatabase.find(311), time);
		
		BuffController.append(cha, HastePotionMagic.clone(BuffController.getPool(HastePotionMagic.class), SkillDatabase.find(311), time, restart));
	}
	
}
