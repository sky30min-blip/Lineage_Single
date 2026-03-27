package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.guard.ElvenGuard;

public class Pan extends ElvenGuard {

	private int collect_item_1_max;
	private int collect_item_1; // 판의 갈기털
	private long collect_time; // 채집이 불가능했던 마지막 시간저장 변수.
	private object flute_o; // 마법의 플룻을 부른 객체 임시 저장 변수.

	public Pan(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("판의 갈기털");
		if (i != null) {
			craft_list.put("1", i);

			List<Craft> l = new ArrayList<Craft>();
			list.put(i, l);
		}

		i = ItemDatabase.find("미스릴 판금");
		if (i != null) {
			craft_list.put("request mithril plate", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("미스릴"), 50));
			l.add(new Craft(ItemDatabase.find("아라크네의 허물"), 1));
			list.put(i, l);
		}

		i = ItemDatabase.find("오리하루콘 판금");
		if (i != null) {
			craft_list.put("request oriharukon plate", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("오리하루콘"), 30));
			l.add(new Craft(ItemDatabase.find("아라크네의 허물"), 1));
			list.put(i, l);
		}

		i = ItemDatabase.find("판의 뿔");
		if (i != null) {
			craft_list.put("request pan's horn", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("마법의 플룻"), 1));
			list.put(i, l);
		}

		collect_item_1_max = 100;
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		super.toTalk(pc, cbp);

		if (pc.getClassType() == Lineage.LINEAGE_CLASS_ELF) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "pane1"));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "panm1"));
		}
	}

	@Override
	protected void toGatherUp(PcInstance pc) {
		if (collect_item_1_max <= collect_item_1) {
			// 갈기털이 남아나질 않겠다! 좀 있다해!
			ChattingController.toChatting(this, "$824", Lineage.CHATTING_MODE_SHOUT);

		} else {
			if (Util.random(0, 100) < 30) {
				// 판의 갈기털 지급
				CraftController.toCraft(this, pc, craft_list.get("1"), 6, true);
				collect_item_1 += 6;
			}
		}
	}

	@Override
	public void toTimer(long time) {
		// 채집 불가능한 시점에 시간을 확인하고 저장하는 부분.
		if (collect_item_1_max <= collect_item_1) {
			if (collect_time == 0)
				collect_time = time;

			// 채집이 다시 가능한 시간이 됫을경우.
			if (time - collect_time >= Lineage.elf_gatherup_time) {
				collect_time = 0;
				collect_item_1 = 0;
			}
		}
	}

	@Override
	protected void toAiWalk(long time) {
		if (flute_o == null || flute_o.isDead() || flute_o.isWorldDelete() || flute_o.isInvis()) {
			flute_o = null;
			super.toAiWalk(time);
			return;
		}

		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_WALK);

		if (Util.isDistance(this, flute_o, npc.getAtkRange())) {
			// 마법의 플룻을 부른객체에게 다가가서 html 창 띄우고 잠시 휴식.
			flute_o.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "panEv1"));
			flute_o = null;
			ai_walk_stay_count = Lineage.npc_talk_stay_time;
			ai_talk = true;

		} else {
			toMoving(flute_o.getX(), flute_o.getY(), 0);

		}
	}

	public void setFluteObject(object flute_o) {
		this.flute_o = flute_o;
		// !
		ChattingController.toChatting(this, "!", Lineage.CHATTING_MODE_SHOUT);
	}
}
