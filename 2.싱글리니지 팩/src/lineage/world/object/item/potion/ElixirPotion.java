package lineage.world.object.item.potion;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ElixirPotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ElixirPotion();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!isClick(cha))
			return;

		if (cha instanceof PcInstance) {
			PcInstance pc = (PcInstance) cha;
			
			if (pc.getLevelUpStat() > 0 || pc.getResetBaseStat() > 0 || pc.getResetLevelStat() > 0) {
				ChattingController.toChatting(pc, "스탯 포인트를 소모하신 후 사용하시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (pc.getLevel() < Lineage.elixir_min_level) {
				ChattingController.toChatting(pc, String.format("%d레벨 이상 사용 가능합니다.", Lineage.elixir_min_level), Lineage.CHATTING_MODE_MESSAGE);
				return;
			} else {
/*				// 본섭 시스템 적용
				if (pc.getElixir() == 1 && pc.getLevel() < 55) {
					ChattingController.toChatting(pc, String.format("%d레벨 이상 사용 가능합니다.", Lineage.elixir_min_level), Lineage.CHATTING_MODE_MESSAGE);
					return;
				} else if (pc.getElixir() == 2 && pc.getLevel() < 60) {
					ChattingController.toChatting(pc, String.format("%d레벨 이상 사용 가능합니다.", Lineage.elixir_min_level), Lineage.CHATTING_MODE_MESSAGE);
					return;
				} else if (pc.getElixir() == 3 && pc.getLevel() < 65) {
					ChattingController.toChatting(pc, String.format("%d레벨 이상 사용 가능합니다.", Lineage.elixir_min_level), Lineage.CHATTING_MODE_MESSAGE);
					return;
				} else if (pc.getElixir() == 4 && pc.getLevel() < 70) {
					ChattingController.toChatting(pc, String.format("%d레벨 이상 사용 가능합니다.", Lineage.elixir_min_level), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}*/	
			}
			
			// 최대 엘릭서값보다 낮을때만 처리.
			if (pc.getElixir() < Lineage.item_elixir_max) {
				String space = "        ";
				
				// 스탯 상승.
				switch (getItem().getNameIdNumber()) {
				case 2530: // str
					if (pc.getStr() + pc.getLvStr() < Lineage.stat_str) {
						pc.setLvStr(cha.getLvStr() + 1);
						
						ChattingController.toChatting(pc, String.format("%s\\fSStr:%d  \\fVDex:%d  Con:%d  Int:%d  Wis:%d  Cha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("능력치의 최대값은 %d 입니다. 사용할 수 없습니다.", Lineage.stat_str), Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					break;
				case 2532: // dex
					if (pc.getDex() + pc.getLvDex() < Lineage.stat_dex) {
						pc.setLvDex(cha.getLvDex() + 1);

						ChattingController.toChatting(pc, String.format("%s\\fVStr:%d  \\fSDex:%d  \\fVCon:%d  Int:%d  Wis:%d  Cha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("능력치의 최대값은 %d 입니다. 사용할 수 없습니다.", Lineage.stat_dex), Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					break;
				case 2531: // con
					if (pc.getCon() + pc.getLvCon() < Lineage.stat_con) {
						pc.setLvCon(cha.getLvCon() + 1);

						ChattingController.toChatting(pc, String.format("%s\\fVStr:%d  Dex:%d  \\fSCon:%d  \\fVInt:%d  Wis:%d  Cha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("능력치의 최대값은 %d 입니다. 사용할 수 없습니다.", Lineage.stat_con), Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					break;
				case 2534: // wis
					if (pc.getWis() + pc.getLvWis() < Lineage.stat_wis) {
						pc.setLvWis(cha.getLvWis() + 1);

						ChattingController.toChatting(pc, String.format("%s\\fVStr:%d  Dex:%d  Con:%d  Int:%d  \\fSWis:%d  \\fVCha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("능력치의 최대값은 %d 입니다. 사용할 수 없습니다.", Lineage.stat_wis), Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					break;
				case 2533: // int
					if (pc.getInt() + pc.getLvInt() < Lineage.stat_int) {
						pc.setLvInt(cha.getLvInt() + 1);

						ChattingController.toChatting(pc, String.format("%s\\fVStr:%d  Dex:%d  Con:%d  \\fSInt:%d  \\fVWis:%d  Cha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("능력치의 최대값은 %d 입니다. 사용할 수 없습니다.", Lineage.stat_int), Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					break;
				case 2535: // cha
					if (pc.getCha() + pc.getLvCha() < Lineage.stat_cha) {
						pc.setLvCha(cha.getLvCha() + 1);

						ChattingController.toChatting(pc, String.format("%s\\fVStr:%d  Dex:%d  Con:%d  Int:%d  Wis:%d  \\fSCha:%d", space, pc.getStr() + pc.getLvStr(), pc.getDex() + pc.getLvDex(),
								pc.getCon() + pc.getLvCon(), pc.getInt() + pc.getLvInt(), pc.getWis() + pc.getLvWis(), pc.getCha() + pc.getLvCha()), Lineage.CHATTING_MODE_MESSAGE);
					} else {
						ChattingController.toChatting(pc, String.format("능력치의 최대값은 %d 입니다. 사용할 수 없습니다.", Lineage.stat_cha), Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					break;
				}
				// 엘릭서 값 상승
				pc.setElixir(pc.getElixir() + 1);
				// 스탯 갱신을위해 패킷 전송.
				pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
				// 아이템 수량 갱신
				cha.getInventory().count(this, getCount() - 1, true);
			} else {
				ChattingController.toChatting(pc, String.format("엘릭서는 최대 %d개 사용 가능합니다.", Lineage.item_elixir_max), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

}
