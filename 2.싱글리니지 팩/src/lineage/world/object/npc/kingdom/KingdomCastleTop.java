package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterHp;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.network.packet.server.S_ObjectRevival;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.GuardInstance;

public class KingdomCastleTop extends Character {

	private Npc npc;
	private Kingdom kingdom;
	private int gfxModeTemp;
	
	public KingdomCastleTop(Npc npc, Kingdom kingdom){
		this.npc = npc;
		this.kingdom = kingdom;
	}

	public Npc getNpc() {
		return npc;
	}

	public void setNpc(Npc npc) {
		this.npc = npc;
	}

	public Kingdom getKingdom() {
		return kingdom;
	}

	public void setKingdom(Kingdom kingdom) {
		this.kingdom = kingdom;
	}

	@Override
	public void setDead(boolean dead) {
		super.setDead(dead);

		// 모든 수호탑이 다 부서졌다면
		if(kingdom.isCastleTopDead()){
			// 면류관 그리기.
			kingdom.getCrownVisual().toTeleport(x, y, map, false);
			// 픽업용 면류관 그리기.
			kingdom.getCrown().toTeleport(x, y, map, false);
		}
	}
	
	@Override
	public int getGfxMode() {
		return isDead() ? 35 : gfxMode;
	}
	
	@Override
	public void setNowHp(int nowHp) {
		// 공성전 중일경우에만 수호탑 hp 세팅. 그리고 죽지 않앗을때.
		if (isWorldDelete() || (kingdom.isWar() && !isDead())) {
			super.setNowHp(nowHp);

			for (object o : getInsideList()) {
				if (o.getGm() > 0)
					o.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), this, true));
			}
			toSender(S_CharacterHp.clone(BasePacketPooling.getPool(S_CharacterHp.class), this));

			// 부서졌을경우.
			if (isDead()) {
				toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, getGfxMode()), false);
			} else {
				toGfxMode();
				// gfxmode값이 변경됫을경우 전송.
				if (getGfxMode() != gfxModeTemp) {
					setGfxMode(gfxModeTemp);
					toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, getGfxMode()), false);
				}
			}
		}
	}
	
	@Override
	public void toRevival(object o){
		if(o != null)
			return;
		
		super.toReset(false);
		
		// 다이상태 풀기.
		setDead(false);
		// 체력 채우기.
		setNowHp(getTotalHp());
		// 패킷 처리.
		setGfxMode(getClassGfxMode());
		toSender(S_ObjectRevival.clone(BasePacketPooling.getPool(S_ObjectRevival.class), temp_object_1, this), false);
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object...opt){
		// 같은 혈맹 소속은 무시.
		if(kingdom.getClanId()!=0 && kingdom.getClanId()==cha.getClanId())
			return;
		// 처리.
		super.toDamage(cha, dmg, type);
		// 주변 경비병에게 알리기.
		for(object o : getInsideList()){
			if(o instanceof GuardInstance)
				((GuardInstance) o).addAttackList(cha);
		}
	}
	
	/**
	 * hp 값에 따른 gfxmode 변경 처리 함수.
	 */
	private void toGfxMode(){
		int hp = (int)Math.round(((double)getNowHp()/(double)getTotalHp())*100);
		if(hp > 50)
			gfxModeTemp = 32;
		else if(hp > 20)
			gfxModeTemp = 33;
		else if(hp > 40)
			gfxModeTemp = 33;
		else
			gfxModeTemp = 34;
	}
}
