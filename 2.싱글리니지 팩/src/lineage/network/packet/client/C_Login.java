package lineage.network.packet.client;

import lineage.database.AccountDatabase;
import lineage.network.LineageClient;
import lineage.network.LineageServer;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Login;
import lineage.network.packet.server.S_LoginFail;
import lineage.network.packet.server.S_TimeLeft;
import lineage.share.Lineage;

public class C_Login extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_Login(data, length);
		else
			((C_Login)bp).clone(data, length);
		return bp;
	}
	
	public C_Login(byte[] data, int length){
		clone(data, length);
	}
	
	public BasePacket Login() {
		
		return this;
	}
	
	@Override
	public BasePacket init(LineageClient c){
		String id = readS();
		String pw = readS();
		//야도란 로그인
		if(id==null || pw==null || id.length()<=0 || pw.length()<=0)
			return this;
		int uid = AccountDatabase.getUid(id);
		if(uid>0){
			if((AccountDatabase.isAccount(uid, id, pw) || AccountDatabase.isAccountOld(uid, id, pw)) && !AccountDatabase.isBlock(uid)){
				LineageClient find_c = LineageServer.find(uid);
				if(find_c!=null){
					// 다른사람이 해당 계정을 사용중일경우.
					c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.LOGIN_USER_ON) );
					// 해당 클라에게 종료 패킷 전송.
					find_c.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x16));
					// 해당클로 강제 종료 처리.
					LineageServer.close(find_c);
				}else{
					// 패스워드 기록.
					AccountDatabase.changePw(uid, pw);
					// 처리
					toLogin(c, id, uid);
				}
			}else{
				// 패스워드가 일치하지 않음.
				c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.LOGIN_USER_OR_PASS_WRONG) );
			}
		}else{
			if(Lineage.account_auto_create && AccountDatabase.isAccountCount(c.getAccountIp())){
				// 계정 자동생성 처리.
				AccountDatabase.insert(id, pw);
				// uid 재 추출
				uid = AccountDatabase.getUid(id);
				// 로그인 처리.
				toLogin(c, id, uid);
			}else{
				// 계정이 존재하지 않음.
				c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.LOGIN_USER_OR_ID_AND_PASS_WRONG) );
			}
		}
		return this;
	}
	
	private void toLogin(LineageClient c, String id, int uid){
		int time = 0;
		if(Lineage.flat_rate && Lineage.server_version>=163){
			// 남은 시간 추출
			time = AccountDatabase.getTime(uid);
			if(time <= 0){
				// 정액 시간이 완료됨.
				c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.REASON_ACCESS_END) );
				return;
			}
			c.setAccountTime(time);
		}

		// 로그인 처리.
		c.setAccountUid(uid);
		c.setAccountId(id);
		c.setAccountNoticeStaticUid(0);
		c.setAccountNoticeUid( AccountDatabase.getNoticeUid(uid) );
		// 로그인한 아이피 갱신.
		AccountDatabase.updateIp(c.getAccountUid(), c.getAccountIp());
		// 로그이한 시간 갱신.
		AccountDatabase.updateLoginsDate(c.getAccountUid());
		// 패킷 처리.
		if(time > 0){
			if(Lineage.server_version < 300)
				c.toSender( S_Login.clone(BasePacketPooling.getPool(S_Login.class), S_LoginFail.REASON_ACCESS_OK, time) );
			c.toSender( S_TimeLeft.clone(BasePacketPooling.getPool(S_TimeLeft.class)) );
		}else{
			BasePacketPooling.setPool( C_NoticeOk.clone(BasePacketPooling.getPool(C_NoticeOk.class), null, 0).init(c) );
		}
	}
}
