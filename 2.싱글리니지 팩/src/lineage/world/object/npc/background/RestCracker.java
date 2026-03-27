package lineage.world.object.npc.background;

import lineage.bean.database.Drop;
import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.database.ItemDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class RestCracker extends object {
	int damage;
	
	public void close() {
		damage = 0;
		clearList(true);
		World.remove(this);
		
		super.close();
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		if (Lineage.is_rest_cracker && (type == Lineage.ATTACK_TYPE_WEAPON || type == Lineage.ATTACK_TYPE_BOW)) {
			if (cha.getInventory() != null) {
				if (cha.getInventory().getSlot(Lineage.SLOT_WEAPON) == null || !cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getName().equalsIgnoreCase(Lineage.rest_cracker_weapon))
					return;
				
				damage += dmg;
				
				if (damage > 0)
					setHeading(++heading);

				if (damage >= Lineage.rest_cracker_hp && Util.isDistance(cha, this, Lineage.SEARCH_LOCATIONRANGE)) {
					damage = 0;
					
					if (Lineage.rest_cracker_exp_min > 0 && Lineage.rest_cracker_exp_max > 0)
						// 경험치 지급.
						cha.toExp(this, Util.random(Lineage.rest_cracker_exp_min, Lineage.rest_cracker_exp_max));
					
					if (Lineage.rest_cracker_aden_min > 0 && Lineage.rest_cracker_aden_max > 0) {
						// 아데나 지급.
						Item i = ItemDatabase.find("아데나");
						
						if (i != null) {
							ItemInstance temp = cha.getInventory().find(i);
							int count = Util.random(Lineage.rest_cracker_aden_min, Lineage.rest_cracker_aden_max);

							if (temp == null) {
								temp = ItemDatabase.newInstance(i);
								temp.setObjectId(ServerDatabase.nextItemObjId());
								temp.setCount(count);
								temp.setDefinite(true);
								cha.getInventory().append(temp, true);
							} else {
								// 겹치는 아이템이 존재할 경우.
								cha.getInventory().count(temp, temp.getCount() + count, true);
							}							
							ChattingController.toChatting(cha, String.format("%s: %s(%d) 획득", Lineage.rest_cracker_name, i.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
						}
					}

					Monster m = MonsterDatabase.find(Lineage.rest_cracker_name);
					
					if (m != null) {
						// 인벤토리에 드랍아이템 등록.
						for (Drop d : m.getDropList()) {
							Item item = ItemDatabase.find(d.getItemName());

							if (item != null) {
								// 기본 찬스
								double chance = d.getChance() + item.getDropChance();

								if (d.getChance() < 1) {
									// 배율 적용.
									chance *= Lineage.rate_drop;
								}

								// 체크.
								if (Math.random() < chance) {
									ItemInstance temp = cha.getInventory().find(item.getName(), d.getItemBress(), item.isPiles());
									int count = Util.random(d.getCountMin() == 0 ? 1 : d.getCountMin(), d.getCountMax() == 0 ? 1 : d.getCountMax());

									if (temp != null && (temp.getBless() != d.getItemBress() || temp.getEnLevel() != d.getItemEn()))
										temp = null;

									if (temp == null) {
										// 겹칠수 있는 아이템이 존재하지 않을경우.
										if (item.isPiles()) {
											temp = ItemDatabase.newInstance(item);
											temp.setObjectId(ServerDatabase.nextItemObjId());
											temp.setBless(d.getItemBress());
											temp.setEnLevel(d.getItemEn());
											temp.setCount(count);
											temp.setDefinite(true);
											cha.getInventory().append(temp, true);
										} else {
											for (int idx = 0; idx < count; idx++) {
												temp = ItemDatabase.newInstance(item);
												temp.setObjectId(ServerDatabase.nextItemObjId());
												temp.setBless(d.getItemBress());
												temp.setEnLevel(d.getItemEn());
												temp.setDefinite(true);
												cha.getInventory().append(temp, true);
											}
										}
									} else {
										// 겹치는 아이템이 존재할 경우.
										cha.getInventory().count(temp, temp.getCount() + count, true);
									}
									
									if (temp.getItem().getType1().equalsIgnoreCase("weapon") || temp.getItem().getType1().equalsIgnoreCase("armor"))
										ChattingController.toChatting(cha, String.format("%s: +%d %s(%d)", Lineage.rest_cracker_name, d.getItemEn(), item.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
									else
										ChattingController.toChatting(cha, String.format("%s: %s(%d)", Lineage.rest_cracker_name, item.getName(), count), Lineage.CHATTING_MODE_MESSAGE);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void setHeading(int heading) {
		if (Lineage.server_version <= 163) {
			if (heading > 2)
				heading = 0;
		}
		super.setHeading(heading);

		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
	}
}
