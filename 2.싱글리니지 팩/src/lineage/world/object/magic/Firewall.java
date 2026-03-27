package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.bean.event.DeleteObject;
import lineage.bean.lineage.BuffInterface;
import lineage.database.BackgroundDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.thread.EventThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;

public class Firewall extends Magic {

	private int x;
	private int y;
	private List<BackgroundInstance> list;

	public Firewall(Skill skill) {
		super(null, skill);
		list = new ArrayList<BackgroundInstance>();
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time, int x, int y) {
		if (bi == null)
			bi = new Firewall(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		((Firewall) bi).setX(x);
		((Firewall) bi).setY(y);
		return bi;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public void toBuffStart(object o) {
		synchronized (list) {
			list.clear();
			// 추가
			int nx = o.getX();
			int ny = o.getY();
			int h = 0;
			int cnt = skill.getDistance();
			while ((nx != x || ny != y) && --cnt >= 0) {
				h = Util.calcheading(nx, ny, x, y);
				nx += Util.getXY(h, true);
				ny += Util.getXY(h, false);

				BackgroundInstance fire = lineage.world.object.npc.background.Firewall.clone(BackgroundDatabase.getPool(lineage.world.object.npc.background.Firewall.class));
				fire.setObjectId(ServerDatabase.nextEtcObjId());
				fire.setGfx(skill.getCastGfx());
				fire.setLight(6);
				fire.toTeleport(nx, ny, o.getMap(), false);

				list.add(fire);
			}
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		synchronized (list) {
			for (BackgroundInstance bi : list) {
				bi.clearList(true);
				World.remove(bi);
				EventThread.append(DeleteObject.clone(EventThread.getPool(DeleteObject.class), bi));
			}
		}
	}

	@Override
	public void toBuff(object o) {
		if (o instanceof Character) {
			Character cha = (Character) o;
			// 1셀 내에 있는 객체 체력 감소시키기
			synchronized (list) {
				for (BackgroundInstance bi : list) {
					for (object oo : bi.getInsideList()) {
						if (!oo.isDead() && cha.getObjectId() != oo.getObjectId() && oo instanceof Character && oo.getX() == bi.getX() && oo.getY() == bi.getY()) {
							// 자신의 소환수는 무시.
							if (oo.getSummon() != null && oo.getSummon().getMasterObjectId() == cha.getObjectId())
								continue;
							// 데미지 처리
							DamageController.toDamage(cha, oo, SkillController.getDamage(cha, oo, oo, skill, 0, skill.getElement()), Lineage.ATTACK_TYPE_MAGIC);
						}
					}
				}
			}
		}
	}

	static public void init(Character cha, Skill skill, int x, int y) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);

		if (SkillController.isMagic(cha, skill, true)) {
			BuffController.remove(cha, Firewall.class);
			BuffController.append(cha, Firewall.clone(BuffController.getPool(Firewall.class), skill, skill.getBuffDuration(), x, y));
		}
	}
}
