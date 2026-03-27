package lineage.network.packet.client;

import lineage.database.CharactersDatabase;
import lineage.network.LineageClient;
import lineage.network.LineageServer;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Notice;
import lineage.network.packet.server.S_Unknow;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

public class C_WorldtoJoin extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_WorldtoJoin(data, length);
		else
			((C_WorldtoJoin)bp).clone(data, length);
		return bp;
	}
	
	public C_WorldtoJoin(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(LineageClient c){
		String name = readS();
		
		if(name==null || name.length()<=0)
			return this;
		
		if(CharactersDatabase.isCharacter(c.getAccountUid(), name) && World.findPc(name)==null){
			//쿠베라 잔상처리
			PcInstance pc = World.findPc(name);
			
			if (pc != null) {
				pc.toWorldOut();
			}

			if (!LineageServer.findWorldPc(c.getAccountIp())) {
				c.toSender(S_Notice.clone(BasePacketPooling.getPool(S_Notice.class), String.format("아이피당 %d개 캐릭터까지 접속 가능합니다.", Lineage.ip_in_game_count)));
				c.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
				return this;
			}
			
		
			// 접속 성공 알림
			c.toSender( S_Unknow.clone(BasePacketPooling.getPool(S_Unknow.class)) );
			// pc객체 정보 추출
			pc = CharactersDatabase.readCharacter(c, name);
			// 고정 멤버 유무 
			CharactersDatabase.readMember(c, pc);
			// 월드 접속한 날자 업데이트
			CharactersDatabase.updateCharacterJoinTimeStamp(name);
			// 월드 진입 알리기.
			if(pc != null)
				pc.toWorldJoin();
		}else{
			c.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
		}
		
		return this;
	}
}
