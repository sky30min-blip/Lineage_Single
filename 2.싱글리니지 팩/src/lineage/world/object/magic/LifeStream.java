package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;

public class LifeStream extends Magic {
	
	private BackgroundInstance lifeStream;
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new LifeStream(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
	
	public LifeStream(Skill skill){
		super(null, skill);
		
		lifeStream = new lineage.world.object.npc.background.LifeStream();
		lifeStream.setGfx(2231);
		lifeStream.setLight(6);
	}
		
	@Override
	public void toBuffStart(object o){
		lifeStream.setObjectId(ServerDatabase.nextEtcObjId());
		lifeStream.toTeleport(o.getX(), o.getY(), o.getMap(), false);
		ChattingController.toChatting(o, "라이프 스트림: 일정 범위 안의 캐릭터 HP회복량이 증가", Lineage.CHATTING_MODE_MESSAGE);
	}

	@Override
	public void toBuff(object o) {
		// 2셀 내에 있는 객체 체력 상승 해주기
		for(object oo : lifeStream.getInsideList()){
			if(!oo.isDead() && oo instanceof Character && Util.isDistance(lifeStream, oo, 2) && !lifeStream.isContainsList(oo)){
				Character cha = (Character) oo;
				lifeStream.appendList(oo);
				cha.setDynamicTicHp(cha.getDynamicTicHp() + 10);
				ChattingController.toChatting(cha, "라이프 스트림: HP의 회복량 증가", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
		
		for (object oo : lifeStream.getList()) {
			if (!Util.isDistance(lifeStream, oo, 2)) {
				Character cha = (Character) oo;
				lifeStream.removeList(oo);
				cha.setDynamicTicHp(cha.getDynamicTicHp() - 10);
				ChattingController.toChatting(cha, "\\fY라이프 스트림 종료", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
		
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY라이프 스트림: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	@Override
	public void toBuffUpdate(object o) {
		lifeStream.toTeleport(o.getX(), o.getY(), o.getMap(), false);
	}

	@Override
	public void toBuffEnd(object o){
		for (object oo : lifeStream.getList()) {
			Character cha = (Character) oo;
			lifeStream.removeList(oo);
			cha.setDynamicTicHp(cha.getDynamicTicHp() - 10);
			ChattingController.toChatting(cha, "\\fY라이프 스트림 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
		lifeStream.clearList(true);
		World.remove(lifeStream);
	}
	
	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}
	
	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		
		if(SkillController.isMagic(cha, skill, true)){
			// 버프 등록
			BuffController.append(cha, LifeStream.clone(BuffController.getPool(LifeStream.class), skill, skill.getBuffDuration()));
		}
	}
	
}
