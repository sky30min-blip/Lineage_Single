package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class ChattingClose extends Magic {

	public ChattingClose(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new ChattingClose(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffChattingClose(true);
	}
	
	@Override
	public void toBuffUpdate(object o) {
		o.setBuffChattingClose(true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		// 당신은 이제 채팅을 할 수 있습니다.
		o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 288));
		o.setBuffChattingClose(false);
	}
	
	static public void init(Character cha, int time){
		// \f3게임에 적합하지 않은 행동으로 인해 앞으로 %0분간 채팅이 금지됩니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 286, String.valueOf(time)));
		
		BuffController.append(cha, ChattingClose.clone(BuffController.getPool(ChattingClose.class), SkillDatabase.find(303), time*60));
	}
	static public void init(Character cha){

		BuffController.append(cha, ChattingClose.clone(BuffController.getPool(ChattingClose.class), SkillDatabase.find(303), 10));
	}
	static public void init(Character cha, int time, boolean message){
		if(message)
			// 현재 채팅 금지중입니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));

		BuffController.append(cha, ChattingClose.clone(BuffController.getPool(ChattingClose.class), SkillDatabase.find(303), time));
	}

}
