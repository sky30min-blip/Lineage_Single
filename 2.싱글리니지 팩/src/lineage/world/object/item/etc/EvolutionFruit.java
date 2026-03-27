package lineage.world.object.item.etc;

import lineage.bean.database.Monster;
import lineage.database.MonsterDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PetInstance;

public class EvolutionFruit extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new EvolutionFruit();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PetInstance) {
			PetInstance pet = (PetInstance) cha;
			// gfx변로 분리하여 하이펫 객체 불러오기
			Monster mon = getHighPet(pet.getClassGfx());
			// 펫 진화하기.
			if (mon != null) {
				pet.setMonster(mon);
				if (pet.getName().startsWith("$"))
					pet.setName(mon.getNameId());
				pet.setMaxHp(pet.getMaxHp() / 2);
				pet.setMaxMp(pet.getMaxMp() / 2);
				pet.setNowHp(pet.getMaxHp());
				pet.setNowMp(pet.getMaxMp());
				pet.setLevel(1);
				pet.setExp(1);
				pet.setGfx(mon.getGfx());
				pet.setGfxMode(mon.getGfxMode());
				pet.setClassGfx(mon.getGfx());
				pet.setClassGfxMode(mon.getGfxMode());
				pet.toTeleport(pet.getX(), pet.getY(), pet.getMap(), false);
				// 펫 목걸이 변경하기.
				pet.getCollar().toUpdate(pet);
				// 목걸이 정보 다시 갱신.
				if (Lineage.server_version <= 144) {
					pet.getSummon().getMaster().toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), pet.getCollar()));
					pet.getSummon().getMaster().toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), pet.getCollar()));
				} else {
					pet.getSummon().getMaster().toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), pet.getCollar()));
				}
				
				ChattingController.toChatting(cha, String.format("'%s' 로 진화하였습니다!", mon.getName()), Lineage.CHATTING_MODE_NORMAL);
			} else {
				ChattingController.toChatting(cha, "진화를 할 수 없습니다.", Lineage.CHATTING_MODE_NORMAL);
			}
		} else {
			super.toClick(cha, cbp);
		}
	}

	private Monster getHighPet(int gfx) {
		switch (gfx) {
		case 6325: // 캥거루 
			return MonsterDatabase.find("불꽃의 캥거루");
		case 6310: // 판다곰 
			return MonsterDatabase.find("공포의 판다곰");
		case 5065: // 아기진돗개 
			return MonsterDatabase.find("진돗개");
		case 96: // 늑대
			return MonsterDatabase.find("하이 울프");
		case 931: // 도베르만
			return MonsterDatabase.find("하이 도베르만");
		case 936: // 세퍼드
			return MonsterDatabase.find("하이 세퍼드");
		case 938: // 비글
			return MonsterDatabase.find("하이 비글");
		case 2145: // 허스키
			return MonsterDatabase.find("하이 허스키");
		case 929: // 세인트 버나드
			return MonsterDatabase.find("하이 세인트 버나드");
		case 934: // 콜리
			return MonsterDatabase.find("하이 콜리");
		case 2734: // 열혈토끼
			return MonsterDatabase.find("하이 래빗");
		case 1642: // 곰
			return MonsterDatabase.find("하이 베어");
		case 1540: // 여우
			return MonsterDatabase.find("하이 폭스");
		case 3134: // 고양이
			return MonsterDatabase.find("하이 캣");
		case 4038: // 라쿤
			return MonsterDatabase.find("하이 라쿤");
		case 4542: // 호랑이
			return MonsterDatabase.find("배틀 타이거");
		}
		return null;
	}
}
