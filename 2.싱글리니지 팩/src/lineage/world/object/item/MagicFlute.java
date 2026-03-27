package lineage.world.object.item;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_SoundEffect;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.npc.craft.Pan;

public class MagicFlute extends ItemInstance {
	
	protected long time_end;	// 버프 종료될 시간값.
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new MagicFlute();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 지속될 시간값 기록.
		time_end = System.currentTimeMillis() + (5*1000);
		// 버프 등록
		BuffController.append(cha, this);
	}

	@Override
	public Skill getSkill() {
		return null;
	}

	@Override
	public void setTime(int time) { }

	@Override
	public int getTime() { return 0; }

	@Override
	public void setCharacter(Character cha) { }

	@Override
	public boolean isBuff(long time) {
		return time_end>time;
	}

	@Override
	public void toBuffStart(object o) {
		// 패킷 처리.
		o.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), item.getEffect()), true);
		// 주변에 판(npc)이 있는지 확인.
		// 존재한다면 근처로 오게한후 html창 띄워서 제작진행하도록 유도하기.
		for(object oo : o.getInsideList()){
			if(oo instanceof Pan){
				Pan p = (Pan)oo;
				p.setFluteObject(o);
				break;
			}
		}
		// 주변몬스터 공격목록에 추가하기. 동물형 몬스터만.
	}

	@Override
	public void toBuffUpdate(object o) { }

	@Override
	public void toBuff(object o) { }

	@Override
	public void toBuffStop(object o) { }

	@Override
	public void toBuffEnd(object o) { }

}
