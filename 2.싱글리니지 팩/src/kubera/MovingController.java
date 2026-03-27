package kubera;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import lineage.bean.database.SpriteFrame;
import lineage.database.SpriteFrameDatabase;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.background.door.Door;
import lineage.world.object.npc.kingdom.KingdomDoor;

public class MovingController {
	private static Map<Integer, Integer> checksumStorage = new HashMap<>();
	private static final int MIN_FRAME_TIME_THRESHOLD = 50; 
															
	public static boolean isMoveValid(PcInstance pc, long lastMovingTime, int targetX, int targetY) {
	    long frame = (long) (getGfxFrameTime(pc, pc.getGfx(), Lineage.GFX_MODE_WALK) * Lineage.speed_check_walk_frame_rate);
	    long currentTime = System.currentTimeMillis();

	    if (pc.isLock()) {
	        return false;
	    }
	    long timeSinceLastMoving = currentTime - lastMovingTime;
	    long adjustedFrame = adjustFrameTime(pc, frame, timeSinceLastMoving);

	    if (adjustedFrame < MIN_FRAME_TIME_THRESHOLD) {
	        return false;
	    }

	    return true;
	}

	private static long adjustFrameTime(PcInstance pc, long frame, long timeSinceLastMoving) {
	    double speedFactor = 1.0;
	    if (pc.getSpeed() == 0 && !pc.isBrave())
	        speedFactor = 42.0;
	    else if ((pc.getSpeed() == 1 && !pc.isBrave()) || (pc.getSpeed() == 0 && pc.isBrave()))
	        speedFactor = 31.0;
	    else if (pc.getSpeed() == 1 && pc.isBrave())
	        speedFactor = 23.5;
	    else if (pc.getSpeed() == 2 && !pc.isBrave())
	        speedFactor = 81.0;
	    else if (pc.getSpeed() == 2 && pc.isBrave())
	        speedFactor = 61.0;

	    double timeFactor = 1.0 + (timeSinceLastMoving / 1000.0); 

	    double adjustedFrame = frame * speedFactor * timeFactor;

	    return (long) adjustedFrame;
	}

	public static int getGfxFrameTime(Object o, int gfx, int action) {
	    SpriteFrame spriteFrame = SpriteFrameDatabase.getList().get(gfx);

	    if (spriteFrame != null) {
	        double frame = 0;
	        Integer gfxFrame = spriteFrame.getList().get(action);

	        if (gfxFrame != null)
	            frame = gfxFrame.intValue();
	        else
	            return 1000;

	        frame = calculateFrameTime(o, frame);

	        int checksum = calculateChecksum(gfx, action);
	        if (checksum != getPredefinedChecksum(gfx, action)) {
	            return 1000;
	        }

	        return (int) frame;
	    }
	    return 1000;
	}

	private static int calculateChecksum(int gfx, int action) {
	    return (gfx + action) % 1000;
	}

	private static int getPredefinedChecksum(int gfx, int action) {
	    int checksum = checksumStorage.getOrDefault(gfx, 0);
	    if (checksum == 0) {
	        checksum = calculateChecksum(gfx, action);
	        checksumStorage.put(gfx, checksum);
	    }
	    return checksum;
	}

	private static double calculateFrameTime(Object o, double frame) {
	    double speedFactor;
	    PcInstance pc = (PcInstance) o; 

	    if (pc.getSpeed() == 0 && !pc.isBrave())
	        speedFactor = 42.0;
	    else if ((pc.getSpeed() == 1 && !pc.isBrave()) || (pc.getSpeed() == 0 && pc.isBrave()))
	        speedFactor = 31.0;
	    else if (pc.getSpeed() == 1 && pc.isBrave())
	        speedFactor = 23.5;
	    else if (pc.getSpeed() == 2 && !pc.isBrave())
	        speedFactor = 81.0;
	    else if (pc.getSpeed() == 2 && pc.isBrave())
	        speedFactor = 61.0;
	    else
	        speedFactor = 1.0;

	    frame *= speedFactor;
	    return frame;
	}
	
	
	
}