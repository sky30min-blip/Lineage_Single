package lineage.network.packet.server;

import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class S_ObjectAttack extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object me, object target, int action, int dmg,
			int effect, boolean bow, boolean visual, int x, int y) {
		
		if (bp == null)
			bp = new S_ObjectAttack(me, target, action, dmg, effect, bow, visual, x, y);
		else
			((S_ObjectAttack) bp).clone(me, target, action, dmg, effect, bow, visual, x, y);
		return bp;
	}

	static synchronized public BasePacket clone(BasePacket bp, object me, int action, int effect, int x, int y) {
		if (bp == null)
			bp = new S_ObjectAttack(me, action, effect, x, y);
		else
			((S_ObjectAttack) bp).clone(me, action, effect, x, y);
		return bp;
	}

	public S_ObjectAttack(object me, object target, int action, int dmg, int effect, boolean bow, boolean visual, int x,
			int y) {
		clone(me, target, action, dmg, effect, bow, visual, x, y);
	}

	public S_ObjectAttack(object me, int action, int effect, int x, int y) {
		clone(me, action, effect, x, y);
	}

	public void clone(object me, int action, int effect, int x, int y) {
		clear();
		writeC(Opcodes.S_OPCODE_AttackPacket);
		writeC(action); // 공격 모션 부분
		writeD(me.getObjectId()); // 유저 오브젝
		writeD(0); // 대상 객체가 없기때문에 0
		if (Lineage.server_version < 300)
			writeC(0); // 들어간 데미지 부분
		else
			writeH(0); // 들어간 데미지 부분
		writeC(me.getHeading()); // 방향

		writeD(ServerDatabase.nextEtcObjId());
		writeH(effect);
		writeC(0x06); // 타켓지정:6, 범위&타켓지정:8, 범위:0
		writeH(me.getX());
		writeH(me.getY());
		writeH(x);
		writeH(y);
		writeH(0);
	}

	public void clone(object me, object target, int action, int dmg, int effect, boolean bow, boolean visual, int x,
			int y) {
		
		  if (me instanceof PcInstance && (action == Lineage.GFX_MODE_SPELL_DIRECTION || action == Lineage.GFX_MODE_SPELL_NO_DIRECTION || action == Lineage.GFX_MODE_WAND)) {
		        PcInstance pc = (PcInstance) me;
		        pc.isFrameSpeed(action);
		    // 마법 사용 모션 및 피격 모션 밀림 현상 개선. 쿠베라
//		    if (SpriteFrameDatabase.findGfxMode(pc.getGfx(), action)){
//		        long time = System.currentTimeMillis();
//		        long frameTime = SpriteFrameDatabase.getGfxFrameTime(pc, pc.getGfx(), action);
//		        pc.ai_Time = time + frameTime + Math.max(0, pc.ai_Time - time);
//		    }  //모션렉 개선 2 방안
//			if (SpriteFrameDatabase.findGfxMode(pc.getGfx(), action)) {
//			    long frameTime = SpriteFrameDatabase.getGfxFrameTime(pc, pc.getGfx(), action);
//			    pc.ai_Time = System.currentTimeMillis() + frameTime;
//			}
		  //모션렉 개선 2 방안
		    if (SpriteFrameDatabase.findGfxMode(pc.getGfx(), action)) {
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
		writeC(Opcodes.S_OPCODE_AttackPacket);

		if (me.isCriticalMagicEffect() && action == Lineage.GFX_MODE_SPELL_DIRECTION && (me.getGfx() == Lineage.wizard_male_gfx || me.getGfx() == Lineage.wizard_female_gfx)) {
	    // 마법사 기본 gfx일 경우 마법 크리티컬이 터지면 특정 모션
	    writeC(Lineage.GFX_MODE_SEPLL_DIRECTION_EXTRA);
	} else {
	    writeC(action); // 공격 모션 부분
	}

	writeD(me.getObjectId()); // 유저 오브젝
	if (target != null)
	    writeD(target.getObjectId()); // 타켓 오브젝
	else
	    writeD(0);
	if (Lineage.server_version < 300) {
	    if (dmg > 0 && !SpriteFrameDatabase.findGfxMode(target.getGfx(), target.getGfxMode() + Lineage.GFX_MODE_DAMAGE))
	        dmg = 0;

	    
	    if(target instanceof PcInstance){
			PcInstance pc = (PcInstance)target;
			long time = System.currentTimeMillis();
			if(pc.damage_action_Time > time){
				writeC(0);
			}
			else{
				writeC(dmg);
				pc.damage_action_Time = time + SpriteFrameDatabase.getGfxFrameTime(target, target.getGfx(), target.getGfxMode() + Lineage.GFX_MODE_DAMAGE);
			}
		}else{
			writeC(dmg);
		}
	    
	    /*if (me instanceof MonsterInstance && target instanceof PcInstance) {
	        PcInstance pc = (PcInstance)target;
	        long currentTime = System.currentTimeMillis();
	        if(pc.damage_action_Time > currentTime)
	            writeC(0);
	        else
	            writeC(dmg);
	    } else {
	        writeC(dmg);
	    }

	    if (target instanceof PcInstance && SpriteFrameDatabase.findGfxMode(target.getGfx(), target.getGfxMode() + Lineage.GFX_MODE_DAMAGE)) {
	        PcInstance pc = (PcInstance)target;
	        long currentTime = System.currentTimeMillis();
	        long frameTime = SpriteFrameDatabase.getGfxFrameTime(target, target.getGfx(), target.getGfxMode() + Lineage.GFX_MODE_DAMAGE);
	        frameTime = (long) (frameTime * Lineage.speed_check_no_dir_magic_frame_rate);
	        long nextActionTime = currentTime + frameTime;

	        if (nextActionTime > pc.damage_action_Time) {
	            pc.damage_action_Time = nextActionTime;
	        } else {
	            pc.damage_action_Time += frameTime;
	        }
	    }*/
	    
	} else {
	    writeH(dmg);

	}
	if (target == null)
		writeC(me.getHeading());
	else
		writeC(Util.calcheading(me, target.getX(), target.getY()));

	if (bow) {
		toBow(me, target, effect, visual, x, y);
	} else {
		toWeapon(me, target, effect, x, y);
	   }
	}

	private void toBow(object me, object target, int effect, boolean visual, int x, int y) {
		if (visual) {
			writeD(ServerDatabase.nextEtcObjId());
			writeH(effect);
			writeC(0x00);
			writeH(me.getX());
			writeH(me.getY());

			if (target != null) {
				writeH(target.getX());
				writeH(target.getY());
			} else {
				// 장로 변신중일 경우 좌표를 지정한 위치로 넣기.
				if (me.getGfx() == 3879) {
					writeH(x);
					writeH(y);
				} else {
					// 일팩에서 가져온 소스.
					final byte HEADING_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
					final byte HEADING_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };
					float disX = Math.abs(me.getX() - x);
					float disY = Math.abs(me.getY() - y);
					float dis = Math.max(disX, disY);
					float avgX = 0;
					float avgY = 0;

					if (dis == 0) {
						avgX = HEADING_X[me.getHeading()];
						avgY = HEADING_Y[me.getHeading()];
					} else {
						avgX = disX / dis;
						avgY = disY / dis;
					}

					int addX = (int) Math.floor((avgX * 15) + 0.59f);
					int addY = (int) Math.floor((avgY * 15) + 0.59f);

					if (me.getX() > x) {
						addX *= -1;
					}
					if (me.getY() > y) {
						addY *= -1;
					}

					writeH(x + addX);
					writeH(y + addY);
				}
			}
			writeH(0);
			writeC(0);
		} else {
			writeD(0);
			writeC(0);
		}
	}

	private void toWeapon(object me, object target, int effect, int x, int y) {
		if (effect > 0) {
			if (me.isCriticalEffect()) {
				writeD(ServerDatabase.nextEtcObjId());
				writeH(effect);
				writeC(0x06); // 타켓지정:6, 범위&타켓지정:8, 범위:0
				writeH(target != null ? target.getX() : x);
				writeH(target != null ? target.getY() : y);
				writeH(0);
			} else {
				writeD(ServerDatabase.nextEtcObjId());
				writeH(effect);
				writeC(0x06); // 타켓지정:6, 범위&타켓지정:8, 범위:0
				writeH(me.getX());
				writeH(me.getY());
				writeH(target != null ? target.getX() : x);
				writeH(target != null ? target.getY() : y);
				writeH(0);
			}
		} else {
			writeD(0);
			writeC(0); // 0:none 2:크로우 4:이도류 0x08:CounterMirror
		}
	}

}
