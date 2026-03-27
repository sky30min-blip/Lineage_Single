package lineage.network.packet.server;

import lineage.bean.lineage.Useshop;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class S_ObjectAction extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectAction(o);
		else
			((S_ObjectAction)bp).clone(o);
		return bp;
	}

	static synchronized public BasePacket clone(BasePacket bp, object o, int action){
		if(bp == null)
			bp = new S_ObjectAction(o, action);
		else
			((S_ObjectAction)bp).clone(o, action);
		return bp;
	}

	static synchronized public BasePacket clone(BasePacket bp, object o, Useshop us){
		if(bp == null)
			bp = new S_ObjectAction(o, us);
		else
			((S_ObjectAction)bp).clone(o, us);
		return bp;
	}
	
	public S_ObjectAction(object o){
		clone(o);
	}
	
	public S_ObjectAction(object o, int action){
		clone(o, action);
	}
	
	public S_ObjectAction(object o, Useshop us){
		clone(o, us);
	}
	
	public void clone(object o){
		clear();
		writeC(Opcodes.S_OPCODE_DOACTION);
		writeD(o.getObjectId());
		writeC(o.getGfxMode());
	}
	
	public void clone(object o, int action){
		if (o instanceof PcInstance && (action == Lineage.GFX_MODE_SPELL_DIRECTION || action == Lineage.GFX_MODE_SPELL_NO_DIRECTION)) {
		    PcInstance pc = (PcInstance) o;
		    pc.isFrameSpeed(action);
		    //모션렉 개선 1 방안
		    // 마법 사용 모션 및 피격 모션 밀림 현상 개선. 쿠베라
//		    if (SpriteFrameDatabase.findGfxMode(o.getGfx(), action)) {
//		        long time = System.currentTimeMillis();
//		        long frameTime = SpriteFrameDatabase.getGfxFrameTime(pc, pc.getGfx(), action);
//		        pc.ai_Time = time + frameTime + Math.max(0, pc.ai_Time - time);
//		    }
		    //모션렉 개선 2 방안
//		    if (SpriteFrameDatabase.findGfxMode(o.getGfx(), action)) {
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
		writeC(Opcodes.S_OPCODE_DOACTION);
		writeD(o.getObjectId());
		writeC(action);
	}
	
	public void clone(object o, Useshop us){
		clear();
		writeC(Opcodes.S_OPCODE_DOACTION);
		writeD(o.getObjectId());
		writeC(o.getGfxMode());
		if(us.getMsg() != null)
			writeB(us.getMsg());
	}
}
