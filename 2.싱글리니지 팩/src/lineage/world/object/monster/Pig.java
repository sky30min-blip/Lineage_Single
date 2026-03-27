package lineage.world.object.monster;

import java.util.List;

import lineage.bean.database.Monster;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Pig extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Pig();
		return MonsterInstance.clone(mi, m);
	}
	
	public void toAiAttack(long time) {
		// 도망모드로 전환.
		setAiStatus(Lineage.AI_STATUS_ESCAPE);
	}
	
	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		setDead(true);
		setAiStatus(Lineage.AI_STATUS_DEAD);

		// 라우풀 깍기
		cha.setLawful(cha.getLawful() - 10);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "pig1"));
	}

	private int remove_item_time;
	
	@Override
	public void toAi(long time) {
		super.toAi(time);

		// 인벤토리에 있는 아이템 소화시키기.
		if (++remove_item_time >= 60) {
			remove_item_time = 0;
			List<ItemInstance> list = inv.getList();
			if (list.size() > 0) {
				ItemInstance temp = list.get(0);
				if (temp != null)
					inv.count(temp, 0, false);
			}
		}
	}
}