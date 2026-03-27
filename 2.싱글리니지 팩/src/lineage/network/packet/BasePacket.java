package lineage.network.packet;

import lineage.network.LineageClient;
import lineage.world.object.instance.PcInstance;

public interface BasePacket {

	/**
	 * 패킷 처리 함수.
	 */
	public BasePacket init(LineageClient c);
	public BasePacket init(PcInstance pc);
	
}
