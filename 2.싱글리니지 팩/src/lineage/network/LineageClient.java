package lineage.network;

import lineage.Main;
import lineage.bean.lineage.StatDice;
import lineage.database.AccountDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.Opcodes;
import lineage.network.packet.client.C_AccountChanger;
import lineage.network.packet.client.C_Ask;
import lineage.network.packet.client.C_BlockName;
import lineage.network.packet.client.C_BoardClick;
import lineage.network.packet.client.C_BoardDelete;
import lineage.network.packet.client.C_BoardPaging;
import lineage.network.packet.client.C_BoardView;
import lineage.network.packet.client.C_BoardWrite;
import lineage.network.packet.client.C_BookAppend;
import lineage.network.packet.client.C_BookRemove;
import lineage.network.packet.client.C_CharacterCreate;
import lineage.network.packet.client.C_CharacterDelete;
import lineage.network.packet.client.C_ClanCreate;
import lineage.network.packet.client.C_ClanInfo;
import lineage.network.packet.client.C_ClanJoin;
import lineage.network.packet.client.C_ClanKin;
import lineage.network.packet.client.C_ClanMarkDownload;
import lineage.network.packet.client.C_ClanMarkUpload;
import lineage.network.packet.client.C_ClanOut;
import lineage.network.packet.client.C_ClanWar;
import lineage.network.packet.client.C_Door;
import lineage.network.packet.client.C_Dungeon;
import lineage.network.packet.client.C_Dustbin;
import lineage.network.packet.client.C_DwarfAndShop;
import lineage.network.packet.client.C_FriendAdd;
import lineage.network.packet.client.C_FriendList;
import lineage.network.packet.client.C_FriendRemove;
import lineage.network.packet.client.C_HyperText;
import lineage.network.packet.client.C_InterfaceSave;
import lineage.network.packet.client.C_ItemClick;
import lineage.network.packet.client.C_ItemDrop;
import lineage.network.packet.client.C_ItemPickup;
import lineage.network.packet.client.C_KingdomWarTimeSelect;
import lineage.network.packet.client.C_KingdomWarTimeSelectFinal;
import lineage.network.packet.client.C_Letter;
import lineage.network.packet.client.C_Login;
import lineage.network.packet.client.C_NoticeOk;
import lineage.network.packet.client.C_ObjectAttack;
import lineage.network.packet.client.C_ObjectAttackBow;
import lineage.network.packet.client.C_ObjectChatting;
import lineage.network.packet.client.C_ObjectChattingOption;
import lineage.network.packet.client.C_ObjectChattingWhisper;
import lineage.network.packet.client.C_ObjectGiveItem;
import lineage.network.packet.client.C_ObjectHeading;
import lineage.network.packet.client.C_ObjectMotion;
import lineage.network.packet.client.C_ObjectMoving;
import lineage.network.packet.client.C_ObjectParty;
import lineage.network.packet.client.C_ObjectPartyList;
import lineage.network.packet.client.C_ObjectPartyOut;
import lineage.network.packet.client.C_ObjectRestart;
import lineage.network.packet.client.C_ObjectSkill;
import lineage.network.packet.client.C_ObjectTalk;
import lineage.network.packet.client.C_ObjectTalkAction;
import lineage.network.packet.client.C_ObjectTitle;
import lineage.network.packet.client.C_ObjectWho;
import lineage.network.packet.client.C_PkCounter;
import lineage.network.packet.client.C_Potal;
import lineage.network.packet.client.C_ServerVersion;
import lineage.network.packet.client.C_SkillBuy;
import lineage.network.packet.client.C_SkillBuyOk;
import lineage.network.packet.client.C_Smith;
import lineage.network.packet.client.C_SmithFinal;
import lineage.network.packet.client.C_StatDice;
import lineage.network.packet.client.C_SummonTargetSelect;
import lineage.network.packet.client.C_TaxGet;
import lineage.network.packet.client.C_TaxPut;
import lineage.network.packet.client.C_TaxSetting;
import lineage.network.packet.client.C_TimeLeft;
import lineage.network.packet.client.C_Trade;
import lineage.network.packet.client.C_TradeCancel;
import lineage.network.packet.client.C_TradeItem;
import lineage.network.packet.client.C_TradeOk;
import lineage.network.packet.client.C_Unknow;
import lineage.network.packet.client.C_UserShop;
import lineage.network.packet.client.C_UserShopList;
import lineage.network.packet.client.C_WorldtoJoin;
import lineage.network.packet.client.C_WorldtoOut;
import lineage.network.util.Encryption;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

public final class LineageClient {
	// 클라가 요청한 패킷.
	private ChannelBuffer buffer;
	// 클라가 요청한 전체 패킷사이즈
	private Long packet_size;
	private Long packet_send_size;
	// 패킷처리에 사용되는 소켓객체
	protected Channel socket;
	// 패킷 암호화 및 복호화에 쓰이는 객체
	private Encryption cryption;
	// 핑 타임 기록용.
	private long ping_time;
	//
	private Object sync_client = new Object();
	// 리니지와 연관있는 변수들
	private int account_uid;
	private String account_id;
	private int account_time;
	private String account_ip;
	private int account_notice_uid;
	private int account_notice_static_uid;
	private PcInstance pc;
	private StatDice stat_dice;
	// 로그 기록용
	private int recv_length; // 해당 클라가 받은 패킷 량
	private int send_length; // 해당 클라가 보낸 패킷 량

	public LineageClient() {
		buffer = ChannelBuffers.dynamicBuffer();
		cryption = new Encryption();
		pc = new PcInstance(this);
		stat_dice = new StatDice();
	}

	/**
	 * 클라이언트가 접속할때 해당 함수를 호출하여 필요한 변수들을 초기화함.
	 * 
	 * @param socket
	 * @param key
	 */
	public void update(Channel socket, Long key) {
		this.socket = socket;
		ping_time = System.currentTimeMillis();
		cryption.initKeys(key);
		packet_size = packet_send_size = Long.valueOf(0);
		account_time = account_uid = account_notice_uid = account_notice_static_uid = recv_length = send_length = 0;
		account_ip = socket.getRemoteAddress().toString();
		account_ip = account_ip.substring(1, account_ip.indexOf(":"));
		buffer.clear();
	}

	public void close() {
		if (socket == null)
			return;
		//
		Channel temp = null;
		synchronized (socket) {
			temp = socket;
			socket = null;
		}
		// 사용자 정보 처리.
		BasePacketPooling.setPool(C_WorldtoOut.clone(BasePacketPooling.getPool(C_WorldtoOut.class), null, 0).init(getPc()));
		// 로그인된 상태라면 계정정보 갱신.
		if (Lineage.flat_rate && account_uid > 0)
			AccountDatabase.updateTime(account_uid, account_time);
		//
		if (temp.isConnected())
			temp.close();
	}

	/*
	 * public void fishingClose() { if(socket == null) return; // Channel temp = null; synchronized (socket) { temp = socket; socket = null; }
	 * 
	 * if(temp.isConnected()) temp.close(); }
	 */

	/**
	 * 패킷 전송처리 요청 함수.
	 * 
	 * @param o
	 */
	public void toSender(Object o) {
		if (socket != null) {
			synchronized (socket) {
				if (socket != null && socket.isConnected()) {
					socket.write(o);
					return;
				}
			}
		}

		if (o instanceof BasePacket)
			BasePacketPooling.setPool((BasePacket) o);
	}

	/**
	 * 클라이언트 객체를 지워도 되는지 확인해주는 함수. : ping이 4분이상 오바됫을경우 true 리턴.
	 * 
	 * @return
	 */
	public boolean isDelete(long time) {
		// 핑 오바되엇는지 확인.
		boolean is_ping = time - ping_time >= 1000 * Lineage.client_ping_time;
		// 계정시간이 다됫는지 확인.
		boolean is_account_time = Lineage.flat_rate && account_uid > 0 ? --account_time <= 0 : false;
		// 둘중 하나라도 참이라면 true.
		return is_ping || is_account_time;
	}

	private void setPing() {
		ping_time = System.currentTimeMillis();
	}

	public ChannelBuffer getBuffer() {
		return buffer;
	}

	public Long getPacketSize() {
		return packet_size;
	}

	public void setPacketSize(Long size) {
		packet_size = size;
	}

	public Long getPacketSendSize() {
		return packet_send_size;
	}

	public void setPakcetSendSize(Long size) {
		packet_send_size = size;
	}

	public Channel getSocket() {
		return socket;
	}

	public Encryption getEncryption() {
		return cryption;
	}

	public void setAccountUid(int account_uid) {
		this.account_uid = account_uid;
	}

	public int getAccountUid() {
		return account_uid;
	}

	public String getAccountId() {
		return account_id;
	}

	public void setAccountId(String account_id) {
		this.account_id = account_id;
	}

	public int getAccountTime() {
		return account_time;
	}

	public void setAccountTime(int account_time) {
		this.account_time = account_time;
	}

	public int getAccountNoticeUid() {
		return account_notice_uid;
	}

	public void setAccountNoticeUid(int account_notice_uid) {
		this.account_notice_uid = account_notice_uid;
	}

	public int getAccountNoticeStaticUid() {
		return account_notice_static_uid;
	}

	public void setAccountNoticeStaticUid(int account_notice_static_uid) {
		this.account_notice_static_uid = account_notice_static_uid;
	}

	public PcInstance getPc() {
		return pc;
	}

	public StatDice getStatDice() {
		return stat_dice;
	}

	public String getAccountIp() {
		return account_ip;
	}

	public int getRecvLength() {
		return recv_length;
	}

	public void setRecvLength(int recv_length) {
		this.recv_length = recv_length;
	}

	public int getSendLength() {
		return send_length;
	}

	public void setSendLength(int send_length) {
		this.send_length = send_length;
	}

	public void toPacket(byte[] data) {
		// 서버 종료될땐 무시.
		if (Main.running == false)
			return;
		
		synchronized (sync_client) {
			// 패킷 처리.
			int op = data[0] & 0xff;

			if (op == Opcodes.C_OPCODE_PING) {
				setPing();

			} else if (op == Opcodes.C_OPCODE_SERVER_VERSION) {
				if (getAccountUid() == 0 && getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ServerVersion.clone(BasePacketPooling.getPool(C_ServerVersion.class), data, data.length).init(this));

			} else if (op == Opcodes.C_OPCODE_QUITGAME) {
				LineageServer.close(this);

			} else if (op == Opcodes.C_OPCODE_CHANGEACCOUNT) {
				if (getAccountUid() == 0 && getPc().isWorldDelete())
					BasePacketPooling.setPool(C_AccountChanger.clone(BasePacketPooling.getPool(C_AccountChanger.class), data, data.length).init(this));

			} else if (op == Opcodes.C_OPCODE_LOGINPACKET) {
				if (getAccountUid() == 0 && getPc().isWorldDelete())
					BasePacketPooling.setPool( C_Login.clone(BasePacketPooling.getPool(C_Login.class), data, data.length).init(this) );
				
			} else if (op == Opcodes.C_OPCODE_TIMELEFTOK) {
				if (getAccountUid() != 0 && getPc().isWorldDelete())
					BasePacketPooling.setPool(C_TimeLeft.clone(BasePacketPooling.getPool(C_TimeLeft.class), data, data.length).init(this));

			} else if (op == Opcodes.C_OPCODE_NOTICEOK) {
				if (getAccountUid() != 0 && getPc().isWorldDelete())
					BasePacketPooling.setPool(C_NoticeOk.clone(BasePacketPooling.getPool(C_NoticeOk.class), data, data.length).init(this));

			} else if (op == Opcodes.C_OPCODE_RETURNTOLOGIN) {
				if (getAccountUid() != 0 && getPc().isWorldDelete()) {
					setAccountUid(0);
					setAccountId(null);
				}

			} else if (op == Opcodes.C_OPCODE_NEWCHAR || op == Opcodes.C_OPCODE_NEWCHAR_STAT) {
				if (getAccountUid() != 0 && getPc().isWorldDelete())
					BasePacketPooling.setPool(C_CharacterCreate.clone(BasePacketPooling.getPool(C_CharacterCreate.class), data, data.length).init(this));

			} else if (op == Opcodes.C_OPCODE_DELETECHAR) {
				if (getAccountUid() != 0 && getPc().isWorldDelete())
					BasePacketPooling.setPool(C_CharacterDelete.clone(BasePacketPooling.getPool(C_CharacterDelete.class), data, data.length).init(this));

			} else if (op == Opcodes.C_OPCODE_LOGINTOSERVER) {
				if (getAccountUid() != 0 && getPc().isWorldDelete())
					BasePacketPooling.setPool(C_WorldtoJoin.clone(BasePacketPooling.getPool(C_WorldtoJoin.class), data, data.length).init(this));

			} else if (op == Opcodes.C_OPCODE_STATDICE) {
				if (getAccountUid() != 0 && getPc().isWorldDelete())
					BasePacketPooling.setPool(C_StatDice.clone(BasePacketPooling.getPool(C_StatDice.class), data, data.length).init(this));

			} else if (op == Opcodes.C_OPCODE_REQUESTCHARSELETE) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_WorldtoOut.clone(BasePacketPooling.getPool(C_WorldtoOut.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_REQUESTMOVECHAR) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectMoving.clone(BasePacketPooling.getPool(C_ObjectMoving.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_DOORS) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_Door.clone(BasePacketPooling.getPool(C_Door.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_BOOKMARK) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_BookAppend.clone(BasePacketPooling.getPool(C_BookAppend.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_BOOKMARKDELETE) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_BookRemove.clone(BasePacketPooling.getPool(C_BookRemove.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_PING) {
				setPing();

			} else if (op == Opcodes.C_OPCODE_REQUESTCHAT || op == Opcodes.C_OPCODE_REQUESTCHATGLOBAL) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectChatting.clone(BasePacketPooling.getPool(C_ObjectChatting.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_REQUESTWHO) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectWho.clone(BasePacketPooling.getPool(C_ObjectWho.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_REQUESTCHATWHISPER) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectChattingWhisper.clone(BasePacketPooling.getPool(C_ObjectChattingWhisper.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_CHATTINGOPTION) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectChattingOption.clone(BasePacketPooling.getPool(C_ObjectChattingOption.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_CLANMAKE) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ClanCreate.clone(BasePacketPooling.getPool(C_ClanCreate.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_CLANOUT) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ClanOut.clone(BasePacketPooling.getPool(C_ClanOut.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_CLANKIN) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ClanKin.clone(BasePacketPooling.getPool(C_ClanKin.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_CLANJOIN) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ClanJoin.clone(BasePacketPooling.getPool(C_ClanJoin.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_ASK) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_Ask.clone(BasePacketPooling.getPool(C_Ask.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_CLANMARKUPLOAD) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ClanMarkUpload.clone(BasePacketPooling.getPool(C_ClanMarkUpload.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_CLANMARKDOWNLOAD) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ClanMarkDownload.clone(BasePacketPooling.getPool(C_ClanMarkDownload.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_REQUESTHEADING) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectHeading.clone(BasePacketPooling.getPool(C_ObjectHeading.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_USEITEM) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ItemClick.clone(BasePacketPooling.getPool(C_ItemClick.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_ITEMDROP) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ItemDrop.clone(BasePacketPooling.getPool(C_ItemDrop.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_ITEMPICKUP) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ItemPickup.clone(BasePacketPooling.getPool(C_ItemPickup.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_ATTACK) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectAttack.clone(BasePacketPooling.getPool(C_ObjectAttack.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_ATTACKBOW) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectAttackBow.clone(BasePacketPooling.getPool(C_ObjectAttackBow.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_USESKILL) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectSkill.clone(BasePacketPooling.getPool(C_ObjectSkill.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_NPCTALK) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectTalk.clone(BasePacketPooling.getPool(C_ObjectTalk.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_NPCACTION) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectTalkAction.clone(BasePacketPooling.getPool(C_ObjectTalkAction.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_SHOP) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_DwarfAndShop.clone(BasePacketPooling.getPool(C_DwarfAndShop.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_TRADE) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_Trade.clone(BasePacketPooling.getPool(C_Trade.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_TREADCANCEL) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_TradeCancel.clone(BasePacketPooling.getPool(C_TradeCancel.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_TREADOK) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_TradeOk.clone(BasePacketPooling.getPool(C_TradeOk.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_TREADADDITEM) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_TradeItem.clone(BasePacketPooling.getPool(C_TradeItem.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_POTALOK) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_Potal.clone(BasePacketPooling.getPool(C_Potal.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_LETTER) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_Letter.clone(BasePacketPooling.getPool(C_Letter.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_UNKNOW) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_Unknow.clone(BasePacketPooling.getPool(C_Unknow.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_REQUESTRESTART) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectRestart.clone(BasePacketPooling.getPool(C_ObjectRestart.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_PARTY) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectParty.clone(BasePacketPooling.getPool(C_ObjectParty.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_PARTYOUT) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectPartyOut.clone(BasePacketPooling.getPool(C_ObjectPartyOut.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_PARTYLIST) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectPartyList.clone(BasePacketPooling.getPool(C_ObjectPartyList.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_GIVEITEM) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectGiveItem.clone(BasePacketPooling.getPool(C_ObjectGiveItem.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_TITLE) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectTitle.clone(BasePacketPooling.getPool(C_ObjectTitle.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_DUSTBIN) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_Dustbin.clone(BasePacketPooling.getPool(C_Dustbin.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_REQUESTHYPERTEXT) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_HyperText.clone(BasePacketPooling.getPool(C_HyperText.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_SKILLBUY || op == Opcodes.C_OPCODE_SKILLBUY_ELF) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_SkillBuy.clone(BasePacketPooling.getPool(C_SkillBuy.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_SKILLBUYOK) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_SkillBuyOk.clone(BasePacketPooling.getPool(C_SkillBuyOk.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_CLANINFO) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ClanInfo.clone(BasePacketPooling.getPool(C_ClanInfo.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_BOARDCLICK) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_BoardClick.clone(BasePacketPooling.getPool(C_BoardClick.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_BOARDWRITE) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_BoardWrite.clone(BasePacketPooling.getPool(C_BoardWrite.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_BOARDREAD) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_BoardView.clone(BasePacketPooling.getPool(C_BoardView.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_BOARDDELETE) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_BoardDelete.clone(BasePacketPooling.getPool(C_BoardDelete.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_BOARDNEXT) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_BoardPaging.clone(BasePacketPooling.getPool(C_BoardPaging.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_TAXSETTING) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_TaxSetting.clone(BasePacketPooling.getPool(C_TaxSetting.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_TaxTotalPut) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_TaxPut.clone(BasePacketPooling.getPool(C_TaxPut.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_TaxTotalGet) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_TaxGet.clone(BasePacketPooling.getPool(C_TaxGet.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_KINGDOMWARTIMESELECT) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_KingdomWarTimeSelect.clone(BasePacketPooling.getPool(C_KingdomWarTimeSelect.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_KINGDOMWARTIMESELECTFINAL) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_KingdomWarTimeSelectFinal.clone(BasePacketPooling.getPool(C_KingdomWarTimeSelectFinal.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_ClanWar) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ClanWar.clone(BasePacketPooling.getPool(C_ClanWar.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_BLOCK_NAME) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_BlockName.clone(BasePacketPooling.getPool(C_BlockName.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_PKCOUNT) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_PkCounter.clone(BasePacketPooling.getPool(C_PkCounter.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_USERSHOP) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_UserShop.clone(BasePacketPooling.getPool(C_UserShop.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_USERSHOPLIST) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_UserShopList.clone(BasePacketPooling.getPool(C_UserShopList.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_SMITH) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_Smith.clone(BasePacketPooling.getPool(C_Smith.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_SMITH_FINAL) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_SmithFinal.clone(BasePacketPooling.getPool(C_SmithFinal.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_FRIENDLIST) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_FriendList.clone(BasePacketPooling.getPool(C_FriendList.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_FRIENDADD) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_FriendAdd.clone(BasePacketPooling.getPool(C_FriendAdd.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_FRIENDDEL) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_FriendRemove.clone(BasePacketPooling.getPool(C_FriendRemove.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_DUNGEON) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_Dungeon.clone(BasePacketPooling.getPool(C_Dungeon.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_INTERFACESAVE) {
				if (getAccountUid() != 0)
					BasePacketPooling.setPool(C_InterfaceSave.clone(BasePacketPooling.getPool(C_InterfaceSave.class), data, data.length).init(getPc()));

			} else if (op == Opcodes.C_OPCODE_TargetSelect) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_SummonTargetSelect.clone(BasePacketPooling.getPool(C_SummonTargetSelect.class), data, data.length).init(getPc()));
			} else if (op == Opcodes.C_OPCODE_MOTION) {
				if (getAccountUid() != 0 && !getPc().isWorldDelete())
					BasePacketPooling.setPool(C_ObjectMotion.clone(BasePacketPooling.getPool(C_ObjectMotion.class), data, data.length).init(getPc()));
			}
		}
	}

}
