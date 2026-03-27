package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAdd;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;

public class Doppelganger extends MonsterInstance {

	// 변신을 했는지 판단여부로 사용할 변수.
	private boolean isPoly;

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Doppelganger();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void close() {
		isPoly = false;
	}

	@Override
	protected void toAiDead(long time) {
		super.toAiDead(time);
		// 초기화.
		setName(getMonster().getName());
		setLawful(getMonster().getLawful());
		setTitle(null);
		setClanId(0);
		setClanName(null);
		setGfx(getClassGfx());
		setGfxMode(getClassGfxMode());
	}

	@Override
	public void toChatting(object o, String msg) {
		// 변신한 타켓에 내용만 카피함.
		if (getName().equalsIgnoreCase(o.getName())) {
			// 사용자가 던지는 말 그대로 되받아 치기.
			ChattingController.toChatting(this, msg, Lineage.CHATTING_MODE_NORMAL);
		}
	}

	@Override
	public void toAiAttack(long time) {
		// 공격자 확인.
		object o = findDangerousObject();
		// 객체를 찾지못했다면 무시.
		if (o == null)
			return;

		// 변신
		if (!isPoly || getGfx() != o.getGfx() || getGfxMode() != o.getGfxMode())
			isPoly(o);
		// 처리.
		super.toAiAttack(time);
	}

	/**
	 * 변신 처리.
	 * 
	 * @param o
	 */
	private void isPoly(object o) {
		isPoly = true;
		// 정보 복사 하기.
		setName(o.getName());
		setLawful(o.getLawful());
		setTitle(o.getTitle());
		setClanId(o.getClanId());
		setClanName(o.getClanName());
		setGfx(o.getGfx());
		setGfxMode(o.getGfxMode());
		ItemInstance weapon = o.getInventory() == null ? null : o.getInventory().getSlot(Lineage.SLOT_WEAPON);
		if (weapon != null) {
			// 무기종류에따라 공격거리 지정하기.
			if (weapon.getItem().getType2().equalsIgnoreCase("bow")) {
				dynamic_attack_area = 10;
			} else if (weapon.getItem().getType2().equalsIgnoreCase("spear")) {
				dynamic_attack_area = 2;
			} else {
				dynamic_attack_area = 1;
			}
		}
		// 패킷 처리
		toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, this), false);
		toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), this), false);
	}

}
