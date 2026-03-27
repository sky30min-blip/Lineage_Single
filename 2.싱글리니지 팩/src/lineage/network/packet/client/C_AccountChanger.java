package lineage.network.packet.client;

import lineage.database.AccountDatabase;
import lineage.network.LineageClient;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_LoginFail;

public class C_AccountChanger extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_AccountChanger(data, length);
		else
			((C_AccountChanger)bp).clone(data, length);
		return bp;
	}
	
	public C_AccountChanger(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(LineageClient c){
		String id = readS();
		String pw = readS();
		String npw = readS();
		if(id==null || pw==null || npw==null || id.length()<=0 || pw.length()<=0 || npw.length()<=0)
			return this;
		
		int uid = AccountDatabase.getUid(id);
		if(AccountDatabase.isAccount(uid, id, pw)){
			// 패스워드 변경.
			AccountDatabase.changePw(uid, npw);
			c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.RETURN) );
		}else{
			// 패스워드가 일치하지 않음.
			c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.LOGIN_USER_OR_PASS_WRONG) );
		}
		
		return this;
	}
}
