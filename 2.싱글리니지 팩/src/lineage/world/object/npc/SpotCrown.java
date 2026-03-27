package lineage.world.object.npc;

import java.util.List;

import lineage.bean.database.FirstInventory;
import lineage.bean.database.Item;
import lineage.bean.lineage.Clan;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BlueMessage;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class SpotCrown extends object {
	private SpotTower spotTower;
	
	public SpotCrown(SpotTower spotTower){
		this.spotTower = spotTower;
	}

//	@Override
//	public void toPickup(Character cha) {
//		Clan c = ClanController.find(cha.getClanName());
//		
//		if (cha.getGm() == 0) {
//			if (c == null)
//				return;
//		}
//
//		// 처리 가능여부 확인
//		if (cha.isDead() || cha.isInvis() || (cha.getGm() == 0 && cha.getClanId() == 0) || 
//			cha.getClanName().equalsIgnoreCase(Lineage.teamBattle_A_team) || cha.getClanName().equalsIgnoreCase(Lineage.teamBattle_B_team) || cha.getClanName().equalsIgnoreCase(Lineage.new_clan_name))
//			return;
//		
//		// 줍기 표현.
//		cha.setHeading( Util.calcheading(cha, x, y) );
//		
//		if (SpriteFrameDatabase.findGfxMode(cha.getGfx(), Lineage.GFX_MODE_GET))
//			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_GET), true);
//
//		String clanName = c == null ? "" : c.getName();
//		SpotController.spotClanId = c == null ? 0 : c.getUid();
//		String msg = String.format("\\fY    ***** %s혈맹이 스팟을 차지하였습니다. *****", clanName);
//		World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
//		World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
//		
//		if (c != null) {
//			for (PcInstance pc : c.getList())
//				pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 15846), true);
//		}
//		
//		// 면류관 표현 제거.
//		spotTower.getCrown().clearList(true);
//		spotTower.getCrownVisual().clearList(true);
//		World.remove(spotTower.getCrown());
//		World.remove(spotTower.getCrownVisual());
//		
//		spotTower.end(false);
//	}
	
	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		if (cha instanceof PcInstance && cha.getInventory() != null && Util.isDistance(this, cha, 1)) {

			
			Clan c = ClanController.find(cha.getClanName());
			
			if (cha.getGm() == 0) {
				if (c == null)
					return;
			}

			// 처리 가능여부 확인
			if (cha.isDead() || cha.isInvis() || (cha.getGm() == 0 && cha.getClanId() == 0) || 
				cha.getClanName().equalsIgnoreCase(Lineage.teamBattle_A_team) || cha.getClanName().equalsIgnoreCase(Lineage.teamBattle_B_team) || cha.getClanName().equalsIgnoreCase(Lineage.new_clan_name))
				return;
			
			// 줍기 표현.
			cha.setHeading( Util.calcheading(cha, x, y) );
			
			if (SpriteFrameDatabase.findGfxMode(cha.getGfx(), Lineage.GFX_MODE_GET))
				cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_GET), true);

			String clanName = c == null ? "" : c.getName();
			String msg = String.format("\\fY    ***** %s혈맹이 스팟을 차지하였습니다. *****", clanName);
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
			
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
			
			if (c != null) {
				for (PcInstance pc : c.getList())
					pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 15846), true);
			}
			
			// 면류관 표현 제거.
			spotTower.getCrown().clearList(true);
			spotTower.getCrownVisual().clearList(true);
			World.remove(spotTower.getCrown());
			World.remove(spotTower.getCrownVisual());
			
			spotTower.end(false);

			if (c != null) {
				List<PcInstance> list = World.getPcList();

				if (list != null) {
					for (PcInstance pc : list) {
						try {
							if (pc != null && !pc.isWorldDelete() && pc.getInventory() != null && pc.getClanId() == c.getUid()) {
								for (FirstInventory fi : Lineage.spot_item) {
									Item i = ItemDatabase.find(fi.getName());

									if (i != null) {
										ItemInstance temp = pc.getInventory().find(i.getName(), 1, i.isPiles());

										if (temp != null && (temp.getBless() != 1 || temp.getEnLevel() != 0))
											temp = null;

										if (temp == null) {
											// 겹칠수 있는 아이템이 존재하지 않을경우.
											if (i.isPiles()) {
												temp = ItemDatabase.newInstance(i);
												temp.setObjectId(ServerDatabase.nextItemObjId());
												temp.setBless(1);
												temp.setEnLevel(0);
												temp.setCount(fi.getCount());
												temp.setDefinite(true);
												pc.getInventory().append(temp, true);
											} else {
												for (int idx = 0; idx < fi.getCount(); idx++) {
													temp = ItemDatabase.newInstance(i);
													temp.setObjectId(ServerDatabase.nextItemObjId());
													temp.setBless(1);
													temp.setEnLevel(0);
													temp.setDefinite(true);
													pc.getInventory().append(temp, true);
												}
											}
										} else {
											// 겹치는 아이템이 존재할 경우.
											pc.getInventory().count(temp, temp.getCount() + fi.getCount(), true);
										}

										ChattingController.toChatting(pc, String.format("스팟 쟁탈전 보상: %s(%,d) 획득.", i.getName(), fi.getCount()), Lineage.CHATTING_MODE_MESSAGE);
									}
								}
							}
						} catch (Exception e) {
							lineage.share.System.println(String.format("[스팟 보상 지급 에러] 캐릭터:[%s]", pc.getName()));
							lineage.share.System.println(e);
						}
					}
				}
			}
		}
	}
}
