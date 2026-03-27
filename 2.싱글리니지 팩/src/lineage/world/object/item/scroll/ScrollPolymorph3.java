package lineage.world.object.item.scroll;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.RankController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ScrollPolymorph3 extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollPolymorph3();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!cha.isFishing()) {
			List<String> quickPolymorph = new ArrayList<String>();
			int allRank = RankController.getAllRank(cha.getObjectId());
			int classRank = RankController.getClassRank(cha.getObjectId(), cha.getClassType());
			
			quickPolymorph.clear();
			quickPolymorph.add(cha.getQuickPolymorph() == null || cha.getQuickPolymorph().equalsIgnoreCase("") || cha.getQuickPolymorph().length() < 1 ? "빠른 변신 목록 없음" : cha.getQuickPolymorph());
			
			cha.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 180));
			
			if (Lineage.is_rank_poly) {
				if ((((allRank > 0 && allRank <= Lineage.rank_poly_all) || (classRank > 0 && classRank <= Lineage.rank_poly_class)) && cha.getLevel() >= Lineage.rank_min_level) ||
					Lineage.event_rank_poly || cha.getMap() == Lineage.teamBattleMap || cha.getGm() > 0)
					cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), cha, "monlist", null, quickPolymorph));
				else
					cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), cha, "monlist", null, quickPolymorph));
			} else {
				cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), cha, "monlist", null, quickPolymorph));
			}
			
			((PcInstance) cha).setTempPoly(true);
			((PcInstance) cha).setTempPolyScroll(this);
		} else {
			ChattingController.toChatting(cha, "낚시중에는 변신할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
