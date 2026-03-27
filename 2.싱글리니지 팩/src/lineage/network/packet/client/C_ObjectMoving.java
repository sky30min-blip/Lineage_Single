package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.PcInstance;

import java.awt.Point;

public class C_ObjectMoving extends ClientBasePacket {

    static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length) {
        if (bp == null)
            bp = new C_ObjectMoving(data, length);
        else
            ((C_ObjectMoving) bp).clone(data, length);
        return bp;
    }

    public C_ObjectMoving(byte[] data, int length) {
        clone(data, length);
    }

    @Override
    public BasePacket init(PcInstance pc) {
        if (pc == null || pc.isDead() || !isRead(5) || pc.isWorldDelete()) {
            return this;
        }

		int locx = readH();
		int locy = readH();
		int heading = readC();
		Point newLocation = getNewLocation(locx, locy, heading);

		pc.triplepart1();


		pc.toMoving(newLocation.x, newLocation.y, heading);
		
		if (pc.isAutoHunt) {
			pc.endAutoHunt(false, false);
		}

        return this;
    }

    private Point getNewLocation(int locx, int locy, int heading) {
        switch (heading) {
            case 0:
                locy--;
                break;
            case 1:
                locx++;
                locy--;
                break;
            case 2:
                locx++;
                break;
            case 3:
                locx++;
                locy++;
                break;
            case 4:
                locy++;
                break;
            case 5:
                locx--;
                locy++;
                break;
            case 6:
                locx--;
                break;
            case 7:
                locx--;
                locy--;
                break;
            default:
                heading = 0;
                locy--;
                break;
        }
        return new Point(locx, locy);
    }
}