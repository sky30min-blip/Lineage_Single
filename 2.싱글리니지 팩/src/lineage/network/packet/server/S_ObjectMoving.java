package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectMoving extends ServerBasePacket {

    static synchronized public BasePacket clone(BasePacket bp, object o) {
        if (bp == null)
            bp = new S_ObjectMoving(o);
        else
            ((S_ObjectMoving) bp).clone(o);
        return bp;
    }

    public S_ObjectMoving(object o) {
        clone(o);
    }

    public void clone(object o) {
        clear();
        int x = o.getX();
        int y = o.getY();
        int heading = o.getHeading();

        x += getDeltaX(heading);
        y += getDeltaY(heading);

        if (heading < 0 || heading > 7) {
            return;
        }

        writeC(Opcodes.S_OPCODE_MOVEOBJECT);
        writeD(o.getObjectId());
        writeH(x);
        writeH(y);
        writeC(o.getHeading());
    }

    private int getDeltaX(int heading) {
        switch (heading) {
            case 1:
            case 2:
            case 3:
                return -1;
            case 5:
            case 6:
            case 7:
                return 1;
            default:
                return 0;
        }
    }

    private int getDeltaY(int heading) {
        switch (heading) {
            case 0:
            case 1:
            case 7:
                return 1;
            case 3:
            case 4:
            case 5:
                return -1;
            default:
                return 0;
        }
    }
}