package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Kingdom;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectCriminal;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.KingdomController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Criminal extends Magic {

	public Criminal(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new Criminal(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffCriminal(true);
		toBuffUpdate(o);
	}

	@Override
	public void toBuffUpdate(object o) {
		if (Lineage.server_version >= 163)
			o.toSender(S_ObjectCriminal.clone(BasePacketPooling.getPool(S_ObjectCriminal.class), o, getTime()), true);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
		
	}

	@Override
	public void toBuffEnd(object o) {
		o.setBuffCriminal(false);
	}

	public static void sendWarMessage(Character cha, object target) {
		String local = Util.getMapName(cha);

		for (PcInstance pc : World.getPcList()) {
			if (!pc.isWarMessage() && local != null)
				pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("\\fY%s \\fU%s \\fY전투중!", local, cha.getName())));
		}
	}

	static public void init(Character cha, object target) {
		Skill s = SkillDatabase.find(206);

		// 전투 메세지 알림
		if (!World.isBattleZone(cha.getX(), cha.getY(), cha.getMap()) && BuffController.find(cha, s) == null && cha instanceof PcInstance && target instanceof PcInstance) {
			Kingdom kingdom = KingdomController.findKingdomLocation(cha);
			Kingdom kingdom1 = KingdomController.findKingdomLocation(target);
			if (((kingdom != null && !kingdom.isWar() && kingdom1 != null && !kingdom1.isWar()) || (kingdom == null && kingdom1 == null)) && cha.getMap() != Lineage.teamBattleMap
					&& target.getMap() != Lineage.teamBattleMap && cha.getMap() != Lineage.BattleRoyalMap && target.getMap() != Lineage.BattleRoyalMap && !cha.getName().equalsIgnoreCase(target.getName())) {
				if (cha.getClanId() > 1 && target.getClanId() > 1 && cha.getClanId() != target.getClanId())
					sendWarMessage(cha, target);
			}
		}

		if (s != null && cha.getMap() != Lineage.teamBattleMap)
			BuffController.append(cha, Criminal.clone(BuffController.getPool(Criminal.class), s, s.getBuffDuration()));
	}
}
