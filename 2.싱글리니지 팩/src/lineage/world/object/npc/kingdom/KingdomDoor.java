package lineage.world.object.npc.kingdom;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterHp;
import lineage.network.packet.server.S_Door;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class KingdomDoor extends Character {

	private Npc npc;
	private Kingdom kingdom;
	private int field_pos;
	private int field_size;
	
	public KingdomDoor(Npc npc, Kingdom kingdom){
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

	public int getFieldPos() {
		return field_pos;
	}

	public void setFieldPos(int field_pos) {
		this.field_pos = field_pos;
	}

	public int getFieldSize() {
		return field_size;
	}

	public void setFieldSize(int field_size) {
		this.field_size = field_size;
	}

	public void toClose(){
		toGfxMode();
		toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this), false);
		toDoorSend(null);
	}

	public void toOpen(){
		gfxMode = 28;
		toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this), false);
		toDoorSend(null);
	}
	
	/**
	 * 현재 hp의 100분율로 리턴함.
	 * @return
	 */
	public int getHp(){
		return (int)Math.round(((double)getNowHp()/(double)getTotalHp())*100);
	}

	@Override
	public boolean isDoorClose(){
		return isDead() ? false : gfxMode!=28;
	}
	
	@Override
	public void toDoorSend(object o){
		int x = getX();
		int y = getY();

		if(heading == 4)
			x = field_pos;
		else
			y = field_pos;
		for(int i=0 ; i<field_size ; ++i){
			if(o == null){
				if(x == getX())
					toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x, y+i, heading, isDoorClose()), false);
				else
					toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x+i, y, heading, isDoorClose()), false);
			}else{
				if(x == getX())
					o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x, y+i, heading, isDoorClose()));
				else
					o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x+i, y, heading, isDoorClose()));
			}
		}
		
		x = getX();
		y = getY();

		// 타일 변경.
		switch (getHeading()) {
		case 2:
		case 6:
			if (x == 33655 && y == 32677 && getMap() == 4) {
				World.set_map(x - 1, y, map, isDoorClose() ? 16 : homeTile[0]);
				World.set_map(x - 1, y + 1, map, isDoorClose() ? 16 : homeTile[0]);
			} else {
				World.set_map(x, y, map, isDoorClose() ? 16 : homeTile[0]);
				World.set_map(x, y + 1, map, isDoorClose() ? 16 : homeTile[0]);
			}
			break;
		case 4: // 6방향으로 증가.
			World.set_map(x, y, map, isDoorClose() ? 16 : homeTile[0]);
			World.set_map(x - 1, y, map, isDoorClose() ? 16 : homeTile[0]);
			break;
		}
	}
	
	/**
	 * hp 값에 따른 gfxmode 변경 처리 함수.
	 */
	private void toGfxMode(){
		int hp = getHp();
		if(hp > 80)
			setGfxMode(29);
		else if(hp > 60)
			setGfxMode(33);
		else if(hp > 40)
			setGfxMode(34);
		else if(hp > 20)
			setGfxMode(35);
		else
			setGfxMode(36);
	}
	
	@Override
	public void toDamage(Character cha, int dmg, int type, Object...opt){
		// 초기화 안된건 무시.
		if(kingdom==null || dmg<=0 || cha.getGm()>0)
			return;
		// 같은 혈맹이라면 무시.
		if(cha.getClanId()!=0 && kingdom.getClanId()==cha.getClanId())
			return;
		// 주변 성경비에게 도움 요청.
		for(object inside : getInsideList()){
			if(inside instanceof KingdomGuard && inside.getClanId()==getClanId())
				inside.toDamage(cha, 0, Lineage.ATTACK_TYPE_DIRECT);
		}
		// 손상 처리.
		if(type==Lineage.ATTACK_TYPE_WEAPON && cha.isBuffSoulOfFlame()==false){
			ItemInstance weapon = cha.getInventory().getSlot(Lineage.SLOT_WEAPON);
			if(weapon!=null && weapon.getItem().isCanbedmg() && Util.random(0, 100)<10){
				weapon.setDurability(weapon.getDurability() + 1);
				if(Lineage.server_version >= 160)
					cha.toSender( S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), weapon) );
				cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 268, weapon.toString()) );
			}
		}
	}
	

	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		super.toTeleport(x, y, map, effect);
		
		// 스폰된 위치에 타일값 기록.
		homeTile[0] = World.get_map(x, y, map);
		switch(heading){
			case 2:
			case 6:
				homeTile[1] = World.get_map(x-1, y, map);
				break;
			case 4:	// 6방향으로 증가.
				homeTile[1] = World.get_map(x, y+1, map);
				break;
		}
	}
	
	@Override
	public void setNowHp(int nowHp){
		if(!isDead()){
			// 값 임시저장
			int mode = getGfxMode();
			// hp 처리
			super.setNowHp(nowHp);
			// hp상태에따른 mode변경.
			toGfxMode();
			
			if (!worldDelete) {
				for (object o : getInsideList()) {
					if (o.getGm() > 0)
						o.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), this, true));
				}
				
				toSender(S_CharacterHp.clone(BasePacketPooling.getPool(S_CharacterHp.class), this));
			}
			
			// mode값이 변경됫을경우 표현.
			if(isDead() || mode!=getGfxMode()){
				if(isDead()){
					toDoorSend(null);
					toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 328), false);
				}else{
					toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 327), false);
				}
				toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this), false);
			}
		}
	}
	
	@Override
	public int getGfxMode() {
		return isDead() ? 37 : gfxMode;
	}
	
	@Override
	public void toReset(boolean world_out){
		super.toReset(world_out);
		// 다이상태 풀기.
		setDead(false);
		// 체력 채우기.
		setNowHp(getMaxHp());
		// 성문 닫기.
		toClose();
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		sb.append(" : hp(");
		sb.append(getHp());
		sb.append("%)");
		return sb.toString();
	}
}
