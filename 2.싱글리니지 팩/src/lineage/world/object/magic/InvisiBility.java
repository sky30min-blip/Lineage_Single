package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class InvisiBility extends Magic {

	public InvisiBility(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new InvisiBility(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setInvis(true);
		o.setBuffInvisiBility(true);
	}

	@Override
	public void toBuffStop(object o){
		// 강제적으로 종료를 요청하는 부분이므로 투망체크 안함.
		// 캔슬이나 디텍션 같은거..
		o.setInvis(false);
		o.setBuffInvisiBility(false);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setBuffInvisiBility(false);
		if(o.getInventory() != null){
			// 투명망토착용상태일경우에는 투명해제 안해도됨.
			ItemInstance item = o.getInventory().getSlot(Lineage.SLOT_CLOAK);
			if(item==null || (item.getItem().getNameIdNumber()!=180 ||item.getItem().getName().equalsIgnoreCase("발록의핏빛망토")))
				o.setInvis(false);
		}else{
			o.setInvis(false);
		}
	}
	
	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true))
			BuffController.append(cha, InvisiBility.clone(BuffController.getPool(InvisiBility.class), skill, skill.getBuffDuration()));
	}
	static public void init2(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
	
			BuffController.append(cha, InvisiBility.clone(BuffController.getPool(InvisiBility.class), skill, skill.getBuffDuration()));
	
	}
	static public void init(Character cha, int time){
		BuffController.append(cha, InvisiBility.clone(BuffController.getPool(InvisiBility.class), SkillDatabase.find(8, 3), time));
	}
	
}
