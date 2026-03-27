package lineage.network.packet.server;

import java.util.List;

import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class S_ObjectAttackMagic extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, object t, List<object> list, boolean none, int action, int dmg, int gfx, int x, int y) {
		if (bp == null)
			bp = new S_ObjectAttackMagic(o, t, list, none, action, dmg, gfx, x, y);
		else
			((S_ObjectAttackMagic) bp).clone(o, t, list, none, action, dmg, gfx, x, y);
		return bp;
	}

	public S_ObjectAttackMagic(object o, object t, List<object> list, boolean none, int action, int dmg, int gfx, int x, int y) {
		clone(o, t, list, none, action, dmg, gfx, x, y);
	}

	public void clone(object o, object t, List<object> list, boolean none, int action, int dmg, int gfx, int x, int y) {
		if (!o.isCriticalMagicEffect() && o instanceof PcInstance  && (action == Lineage.GFX_MODE_SPELL_DIRECTION || action == Lineage.GFX_MODE_SPELL_NO_DIRECTION || action == Lineage.GFX_MODE_WAND)) {
			PcInstance pc = (PcInstance) o;
			pc.isFrameSpeed(action);
		    // 마법 사용 모션 및 피격 모션 밀림 현상 개선. 쿠베라
//		    if (SpriteFrameDatabase.findGfxMode(o.getGfx(), action)) {
//		        long time = System.currentTimeMillis();
//		        long frameTime = SpriteFrameDatabase.getGfxFrameTime(pc, pc.getGfx(), action);
//		        pc.ai_Time = time + frameTime + Math.max(0, pc.ai_Time - time);
//		    }//모션렉 개선 2 방안
//			if (SpriteFrameDatabase.findGfxMode(o.getGfx(), action)) {
//			    long frameTime = SpriteFrameDatabase.getGfxFrameTime(pc, pc.getGfx(), action);
//			    pc.ai_Time = System.currentTimeMillis() + frameTime;
//			}
			 //모션렉 개선 3 방안
			if (SpriteFrameDatabase.findGfxMode(o.getGfx(), action)) {
			    long frameTime = SpriteFrameDatabase.getGfxFrameTime(pc, pc.getGfx(), action);
			    long currentTime = System.currentTimeMillis();
			    long nextActionTime = currentTime + frameTime;

			    if (nextActionTime > pc.ai_Time) {
			        pc.ai_Time = nextActionTime;
			    } else {
			        pc.ai_Time += frameTime;
			    }
			}
		}


		clear();

		writeC(Opcodes.S_OPCODE_MagicAttackPacket);
		
		if (o.isCriticalMagicEffect() && action == Lineage.GFX_MODE_SPELL_DIRECTION && (o.getGfx() == Lineage.wizard_male_gfx || o.getGfx() == Lineage.wizard_female_gfx)) {
			// 마법사 기본 gfx일 경우 마법 크리티컬이 터지면 특정 모션 
			writeC(Lineage.GFX_MODE_SEPLL_DIRECTION_EXTRA);
		} else {
			writeC(action);
		}
		
		writeD(o.getObjectId());
		if (gfx == 170 || gfx == 171 || gfx == 758 || gfx == 757 || gfx == 1812 || gfx == 3932 || gfx == 1819 || gfx == 3933 || gfx == 2546 || gfx == 1183 || gfx == 13240 || gfx == 2552 || gfx == 4469
				|| gfx == 7725 || gfx == 7875 || gfx == 4160) {
			writeH(o.getX());
			writeH(o.getY());
		} else {
			writeH(x);
			writeH(y);
		}
		
		if (t != null)
			writeC(Util.calcheading(o, t.getX(), t.getY()));
		else
			writeC(o.getHeading());
		
		writeD(ServerDatabase.nextEtcObjId());
		writeH(gfx);
		if (none) {
			if (list != null && list.size() > 0 && Lineage.server_version >= 163) {
				writeC(0x00);
				writeH(0);
				writeH(list.size());
				for (object oo : list) {
					if (oo == null)
						continue;
					writeD(oo.getObjectId());
					writeC(1);
				}
			} else {
				writeC(0x00);
				writeH(64715);
				writeH(0);
				writeC(0);
			}
		} else {
			writeC(0x08);
			writeH(0);
			writeH(list.size() + 1);
			writeD(t.getObjectId());
			writeC(dmg);
			for (object oo : list) {
				if (oo == null)
					continue;
				writeD(oo.getObjectId());
				writeC(1);
			}
		}
	}

}
