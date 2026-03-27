package lineage.network.packet;

import lineage.bean.database.ServerOpcodes;
import lineage.database.ServerOpcodesDatabase;

public final class Opcodes {
	
	// client
	static public int C_OPCODE_LOGINPACKET	 	        = 1;	// 계정정보가 담긴 패킷
	static public int C_OPCODE_RETURNTOLOGIN			= 2;	// 다시 로긴창으로 넘어갈때
	static public int C_OPCODE_NEWCHAR					= 3;	// 케릭 생성
	static public int C_OPCODE_LOGINTOSERVER			= 4;	// 케릭 접속 시도
	static public int C_OPCODE_CHANGEACCOUNT			= 5;	// 비밀번호 변경
	static public int C_OPCODE_DELETECHAR				= 6;	// 케릭터 삭제
	static public int C_OPCODE_DUNGEON					= 7;	// 던전이동 요청 
	static public int C_OPCODE_REQUESTHEADING			= 8;	// 방향 전환 부분
	static public int C_OPCODE_REQUESTMOVECHAR	        = 9;	// 이동요청 부분
	static public int C_OPCODE_ITEMPICKUP				= 10;	// 아이템 줍기
	static public int C_OPCODE_ITEMDROP					= 11;	// 아이템 떨구기
	static public int C_OPCODE_CHATTINGOPTION			= 12;	// 전체채팅 켬/끔등..
	static public int C_OPCODE_DOORS					= 13;	// 문짝 클릭 부분
	static public int C_OPCODE_QUITGAME					= 14;	// 겜 종료할때
	static public int C_OPCODE_REQUESTCHARSELETE		= 15;	// 겜중에 리셋버튼 눌럿을 경우
	static public int C_OPCODE_GIVEITEM					= 16;	// 강제로 해당 객체에게 아이템 넘기기
	static public int C_OPCODE_REQUESTCHAT		        = 17;	// 일반 채팅
	static public int C_OPCODE_USESKILL					= 18;	// 스킬 사용 부분
	static public int C_OPCODE_CLANOUT					= 19;	// /탈퇴
	static public int C_OPCODE_ATTACK					= 20;	// 공격요청 부분
	static public int C_OPCODE_ATTACKBOW				= 21;	// 활 공격 부분
	static public int C_OPCODE_REQUESTWHO				= 22;	// /누구 명령어
	static public int C_OPCODE_REQUESTCHATWHISPER		= 23;	// 귓속 채팅
	static public int C_OPCODE_USEITEM					= 24;	// 아이템 사용 부분
	static public int C_OPCODE_AUTOSAVE					= 25;	// 자동저장 요청.
	static public int C_OPCODE_NONE_1					= 26;	// 소나무막대 더블클릭하면 옴.
	static public int C_OPCODE_PING						= 27;	// 1분마다 한번씩 옴
	static public int C_OPCODE_CLANMAKE					= 28;	// 혈맹창설
	static public int C_OPCODE_CLANJOIN					= 29;	// /가입 명령어
	static public int C_OPCODE_ASK						= 30;	// 상대초청의 Yes를 했을때
	static public int C_OPCODE_BLOCK_NAME				= 31;	// /차단 햇을때
	static public int C_OPCODE_NPCACTION				= 32;	// Npc 대화 액션 부분
	static public int C_OPCODE_SHOP						= 33;	// 상점 부분
	static public int C_OPCODE_NPCTALK					= 34;	// Npc와 대화부분
	static public int C_OPCODE_TITLE					= 35;	// /호칭 명령어
	static public int C_OPCODE_BOOKMARK					= 36;	// 기억 추가 부분
	static public int C_OPCODE_BOOKMARKDELETE			= 37;	// 기억 삭제 부분
	static public int C_OPCODE_CLANMARKUPLOAD			= 38;	// 문장을 서버로 업로드함
	static public int C_OPCODE_CLANMARKDOWNLOAD			= 39;	// 문장데이타를 서버에 요청함
	static public int C_OPCODE_ClanWar					= 40;	// 전쟁 관련
	static public int C_OPCODE_CLANKIN					= 41;	// 혈맹원 추방
	static public int C_OPCODE_TRADE					= 42;	// 교환 부분
	static public int C_OPCODE_TREADCANCEL				= 43;	// 교환 취소 부분
	static public int C_OPCODE_TREADOK					= 44;	// 교환 확인 부분
	static public int C_OPCODE_TREADADDITEM				= 45;	// 교환 아이템 업데이트
	static public int C_OPCODE_TAXSETTING				= 46;	// 세율 설정
	static public int C_OPCODE_TaxTotalGet				= 47;	// 공금 인출
	static public int C_OPCODE_STATDICE					= 48;	// 스탯 주사위 클릭시	
	static public int C_OPCODE_SMITH_FINAL				= 49;	// 무기수리 요청
	static public int C_OPCODE_SMITH					= 50;	// 무기수리할 목록 요청
	static public int C_OPCODE_TIMELEFTOK				= 51;	// 계정남은시간 서버로부터 받고 클라가 응답하는 부분
	static public int C_OPCODE_TaxTotalPut				= 52;	// 공금 입금
	static public int C_OPCODE_CLANINFO					= 53;	// 혈맹원 리스트 요청		
	static public int C_OPCODE_SKILLBUY					= 54;	// 스킬 상점 BUY
	static public int C_OPCODE_SKILLBUYOK				= 55;	// 스킬 상점 BUY_OK
	static public int C_OPCODE_KINGDOMWARTIMESELECT		= 56;	// 공성전 시간 설정 목록.
	static public int C_OPCODE_KINGDOMWARTIMESELECTFINAL= 57;	// 공성전 시간 선택
	static public int C_OPCODE_POTALOK					= 58;	// 포탈 ㅇㅋ
	static public int C_OPCODE_TargetSelect				= 59;	// 펫을이용한 타켓지정 명령 리턴값
	static public int C_OPCODE_REQUESTHYPERTEXT			= 60;	// S_OPCODE_HYPERTEXTINPUT 에 대한 응답.
	static public int C_OPCODE_BOARDWRITE				= 61;	// 게시판 쓰기
	static public int C_OPCODE_BOARDNEXT				= 62;	// 게시판 next
	static public int C_OPCODE_BOARDREAD				= 63;	// 게시판 읽기
	static public int C_OPCODE_BOARDDELETE				= 64;	// 게시판 삭제
	static public int C_OPCODE_PARTY					= 65;	// /초대 명령어
	static public int C_OPCODE_PARTYOUT					= 66;	// 파티탈퇴 부분
	static public int C_OPCODE_PARTYLIST				= 67;	// 파티원 리스트 요청
	static public int C_OPCODE_BOARDCLICK				= 68;	// 게시판 클릭
	static public int C_OPCODE_NEWCHAR_STAT				= 69;	// 케릭 생성 스탯 임의설정된것
	static public int C_OPCODE_POTAL_POINTER			= 70;	// 던전이동기능중 지정이동 요청.
	static public int C_OPCODE_DUSTBIN					= 71;	// 휴지통 기능
	static public int C_OPCODE_REQUESTCHATGLOBAL		= 72;	// 전체 채팅
	static public int C_OPCODE_REQUESTRESTART			= 73;	// 겜중에 죽어서 리셋 눌럿을때
	static public int C_OPCODE_UNKNOW					= 74;	// 월드진입후 이거 답변 안보내면 계속 보내다가 게임가드 발견댓다고 함..
	static public int C_OPCODE_NOTICEOK					= 75;	// 공지사항 확인
	static public int C_OPCODE_LETTER					= 76;	// 편지관련
	static public int C_OPCODE_SERVER_VERSION			= 77;	// 버전 요청.
	static public int C_OPCODE_PKCOUNT					= 78;	// pk 횟수 요청.
	static public int C_OPCODE_GROUPCHATTING			= 79;	// 대화 포함 요청.
	static public int C_OPCODE_USERSHOP					= 80;	// 개인 상점
	static public int C_OPCODE_USERSHOPLIST				= 81;	// 개인상점 buy, sell
	static public int C_OPCODE_FRIENDLIST				= 82;	// 친구목록 요청
	static public int C_OPCODE_FRIENDADD				= 83;	// 친구추가
	static public int C_OPCODE_FRIENDDEL				= 84;	// 친구삭제
	static public int C_OPCODE_INTERFACESAVE			= 85;	// 인벤 및 인터페이스등 화면 위치값 저장
	static public int C_OPCODE_SKILLBUY_ELF				= 86;	// 
	static public int C_OPCODE_MOTION					= 87;	// 
	
	// server
	static public int S_OPCODE_SERVERVERSION			= 1;	// 서버 버전
	static public int S_OPCODE_LOGINFAILS				= 2;	// 로그인 실패시 메세지안내.
	static public int S_OPCODE_CHARAMOUNT				= 3;	// 소유하고있는 케릭터 갯수.
	static public int S_OPCODE_CHARLIST					= 4;	// 케릭터리스트창에 잇는 케릭정보
	static public int S_OPCODE_NEWCHARPACK				= 5;	// 케릭 새로 만든거 보내기
	static public int S_OPCODE_DETELECHAROK				= 6;	// 케릭 지우기
	static public int S_OPCODE_UNKNOWN1					= 7;	// 쓰레기값?? ;;; 나도모름..
	static public int S_OPCODE_BlindPotion				= 8;	// 눈멀기 효과
	static public int S_OPCODE_CHARPACK					= 9;	// 오브젝트 그리기
	static public int S_OPCODE_OWNCHARSTATUS			= 10;	// 케릭 스탯 정보
	static public int S_OPCODE_HPUPDATE					= 11;	// HP 업데이트
	static public int S_OPCODE_ItemBressChange			= 12;	// 아이템 브레스 변경하기
	static public int S_OPCODE_GLOBALCHAT				= 13;	// 전체 채팅
	static public int S_OPCODE_SERVERMSG				= 14;	// 서버 메세지 코드
	static public int S_OPCODE_RESTORE					= 15;	// 부활
	static public int S_OPCODE_MOVEOBJECT				= 16;	// 해당 오브젝트 움직이게 하는 부분
	static public int S_OPCODE_NORMALCHAT				= 17;	// 일반 채팅
	static public int S_OPCODE_WHISPERCHAT				= 18;	// 귓속말
	static public int S_OPCODE_DELETEOBJECT				= 19;	// 해당 오브젝트 지우기
	static public int S_OPCODE_ITEMADD					= 20;	// 아이템 추가
	static public int S_OPCODE_ITEMDELETE				= 21;	// 아이템 삭제
	static public int S_OPCODE_ITEMEQUIP				= 22;	// 아이템 착용			- 이상없음.
	static public int S_OPCODE_ITEMCOUNT				= 23;	// 아이템 수량 변경.
	static public int S_OPCODE_OBJLIGHT					= 24;	// 밝기 조절
	static public int S_OPCODE_CHANGEHEADING			= 25;	// 방향 전환 부분
	static public int S_OPCODE_OBJECTMODE				= 26;	// 해당 오브젝트 모드 바꾸기
	static public int S_OPCODE_SKILLINV					= 27;	// 스킬 추가
	static public int S_OPCODE_SKILLDELETE				= 28;	// 스킬 제거
	static public int S_OPCODE_DOACTION					= 29;	// 액션 부분			-이상없음.
	static public int S_OPCODE_GAMETIME					= 30;	// 게임 시간
	static public int S_OPOCDE_ATTRIBUTE				= 31;	// 위치값을 이동가능&불가능 조작 부분
	static public int S_OPCODE_AttackPacket				= 32;	// 공격 표현하는 부분
	static public int S_OPCODE_YES_NO					= 33;	// Yes Or No 메세지
	static public int S_OPCODE_SetClientLock			= 34;	// 클라이언트의 행동 입력을 제한 - 패럴라이즈 효과
	static public int S_OPCODE_ABILITY					= 35;	// 이반, 소반, 인프라비전 쓸때
	static public int S_OPCODE_POLY						= 36;	// 변신
	static public int S_OPCODE_OBJECTMAP				= 37;	// 맵 아이디
	static public int S_OPCODE_SKILLHASTE				= 38;	// 헤이스트
	static public int S_OPCODE_SHOWHTML					= 39;	// npc와 대화 하는 부분
	static public int S_OPCODE_SHOPBUY					= 40;	// 상점 구입 부분
	static public int S_OPCODE_SHOPSELL					= 41;	// 상점 판매 부분
	static public int S_OPCODE_SHOTSAY					= 42;	// 샤우팅 글
	static public int S_OPCODE_TITLECHANGE				= 43;	// 호칭 변경
	static public int S_OPCODE_BOOKMARKS				= 44;	// 기억 리스트
	static public int S_OPCODE_WAREHOUSE				= 45;	// 창고 리스트
	static public int S_OPCODE_PoisonAndLock			= 46;	// 독과 굳은 상태 표현
	static public int S_OPCODE_LINEAGEWEATHER			= 47;	// 날씨 조작하기
	static public int S_OPCODE_OBJECTINVISA				= 48;	// 투명 처리 효과 부분
	static public int S_OPCODE_DownLoadMark				= 49;	// 문장데이타를 클라로 전송함
	static public int S_OPCODE_CLANWAR					= 50;	// 전쟁시 사용하는거
	static public int S_OPCODE_EFFECT					= 51;	// 이팩트 부분
	static public int S_OPCODE_MagicAttackPacket		= 52;	// 범위마법 공격 표현하는 부분
	static public int S_OPCODE_TRADE					= 53;	// 거래창 부분
	static public int S_OPCODE_TRADEADDITEM				= 54;	// 거래창 아이템 추가 부분
	static public int S_OPCODE_TRADESTATUS				= 55;	// 거래 취소, 완료
	static public int S_OPCODE_ITEMIDENTIFY				= 56;	// 아이템 확인주문서 코드
	static public int S_OPCODE_KINGDOMGUARDSELECT		= 57;	// 배치할 용병을 선택하여 주십시오.
	static public int S_OPCODE_CASTLETAXRATIO			= 58;	// 세율
	static public int S_OPCODE_CASTLETAXOUT				= 59;	// 공금인출
	static public int S_OPCODE_SOUNDEFFECT				= 60;	// 사운드 이팩트
	static public int S_OPCODE_CASTLETAXIN				= 61;	// 공금 입금
	static public int S_OPCODE_CASTLEMASTER				= 62;	// 성소유목록 세팅
	static public int S_OPCODE_STATDICE					= 63;	// 랜덤 스탯
	static public int S_OPCODE_SMITH					= 64;	// 손상무기 목록 창
	static public int S_OPCODE_TIMELEFT					= 65;	// 계정 남은 시간
	static public int S_OPCODE_MPUPDATE					= 66;	// MP 업데이트
	static public int S_OPCODE_SKILLBUY					= 67;	// 스킬 구입 창
	static public int S_OPCODE_SUMMON_OWN_CHANGE		= 68;	// 객체이름위에잇는 xx의 정보 변경
	static public int S_OPCODE_EXP						= 69;	// EXP 부분.
	static public int S_OPCODE_EFFECTLOC				= 70;	// 좌표값을 참고로하는 이팩
	static public int S_OPCODE_CASTLEWARTIME			= 71;	// 전쟁 시간
	static public int S_OPCODE_POTAL					= 72;	// 던전이동하는 포탈
	static public int S_OPCODE_ChangeSpMr				= 73;	// sp와 mr변경
	static public int S_OPCODE_PromptTargetSelect		= 74;	// 개의 공격명령들 클릭으로 타겟 입력 받음
	static public int S_OPCODE_SetObjectName			= 75;	// 필드 오브젝트의 이름 변경
	static public int S_OPCODE_DISPOSITION				= 76;	// 라우풀 부분
	static public int S_OPCODE_HYPERTEXTINPUT			= 77;	// 제작창 구성 패킷
	static public int S_OPCODE_LETTERREAD				= 78;	// 편지지 읽기
	static public int S_OPCODE_BOARDLIST				= 79;	// 게시판 리스트
	static public int S_OPCODE_BOARDREAD				= 80;	// 게시판 내용 출력
	static public int S_OPCODE_SKILLBRAVE				= 81;	// 용기
	static public int S_OPCODE_RETURNTOCHARSELECTE		= 82;	// 강제로 케릭선택창으로 이동시키기.
	static public int S_OPCODE_DISCONNECT				= 83;	// 해당 케릭 강제 종료 시키기
	static public int S_OPCODE_HITRATIO					= 84;	// 미니 hp표현 부분
	static public int S_OPCODE_CRIMINAL					= 85;	// 보라돌이
	static public int S_OPCODE_MAGICSTR					= 86;	// 마법 힘업
	static public int S_OPCODE_MAGICDEX					= 87;	// 마법 덱스업
	static public int S_OPCODE_SHIELD					= 88;	// 마법 쉴드
	static public int S_OPCODE_ITEMSTATUS				= 89;	// 아이템 정보 변경.
	static public int S_OPCODE_Agit_List				= 90;	// 아지트 리스트
	static public int S_OPCODE_Agit_Map					= 91;	// 아지트 맵
	static public int S_OPCODE_AQUABREATH				= 92;	// 수중 숨쉬기
	static public int S_OPCODE_TRUETARGET				= 93;	// 군주스킬 트루타켓
	static public int S_OPCODE_UNKNOWN2					= 94;	// 쓰레기값?? ;;; 나도모름..
	static public int S_OPCODE_CRYPTKEY					= 95;	// 블로우피쉬
	static public int S_OPCODE_NOTICE					= 96;	// 공지사항
	static public int S_OPCODE_STATUS_AC				= 97;	// AC설정 부분
	static public int S_OPCODE_ITEMLIST					= 98;	// 인벤토리에 아이템리스트
	static public int S_OPCODE_UNKNOWN3					= 99;	// C_OPCODE_UNKNOW 대한 응답
	static public int S_OPCODE_MINIMAP					= 100;	// 미니맵
	static public int S_OPCODE_KingdomSoldierSpawn		= 101;	// 선택된 용병 배치할 갯수 설정.
	static public int S_OPCODE_RetreivePravateShop		= 102;	// 개인상점 목록 연람하기
	static public int S_OPCODE_KingdomSoldierBuy		= 103;	// 용병 고용.
	static public int S_OPCODE_BLUEMESSAGE 				= 114;
	
	static public int S_OP[] = {
		      0x01,                                                 0x0a,       0x0c,       0x0e, 0x0f,
		                        0x14,                                                 0x1d,
		0x20,       0x22,             0x25, 0x26,                         0x2b, 
		      0x31,                   0x35,
		      0x41,       0x43,             0x46, 0x47,
		                                                                        0x5c,       0x5e, 0x5f,
		0x60,                               0x66,       0x68,       0x6a,                   0x6e, 0x6f,
		      0x71,                         0x76,                         0x7b,                   0x7f,
	//	0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x8b, 0x8c, 0x8d, 0x8e, 0x8f,
	//	0x90, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0x9b, 0x9c, 0x9d, 0x9e, 0x9f,
	// 0x65 펫목걸이 생기면서 펫으로 변함.
	};
	
	static public int S_OP_OLD[] = {
		1, 8, 9, 25, 46, 58, 59, 64, 65, 67, 68, 80, 82, 90, 92, 93, 97, 99, 100, 103, 105, 110, 112, 113, 114, 117, 118,
		120, 121, 
	};

	/*
	280
	 : [S] 69 - 화살표나오면서 외치기하는 옵코드.
	 */
	static public int S_OP_280[] = {
		             5,
		11,
		21,      24,            29,
		41,
		         54,               60,
		61,62,   64,
		71,72,
		81,      84,         88,
		      93,         97,98,   100,
		101,                        108,
		                115,    117,
		                    126,127,128,129,130,
//		131,132,133,134,135,136,137,138,139,140,
//		141,142,143,144,145,146,147,148,149,150,
//		151,152,153,154,155,156,157,158,159,160,
	};
	
	static public int S_OP_280_[] = {
		4, 20, 74, 30, 41, 55, 79, 94, 103, 105, 114
	};
	
/*
20
 : weight
74
 : gfx 변경
30
 : buy 창
41
 : 공격패킷
55
 : buy 창
79
 : 울렁울렁~ 술취한 효과
91
 : 손가락이 펴지고 클라가 126 를 던짐.
94
 : 상점목록 창이 뜸.. ok, cancel 버튼 뿐.
103
 : 케릭 가운데 파란 글씨로 Account hos just logged in from 이라 함..
105
 : 세율 조절창 뜸.
114
 : 해당 객체를 펫객체로 변경시킴.
125
 : npc이름 옆에 펫목걸이 생김.
 */
	
	
	
	
	
	
	// 24와 11은 같은 기능을함.
	static public int S_OP_310[] = {
		                5,    7,
		      12,     15,      18,
		      22,23,  25,26,27,28,29,
		30,31,32,33,34,35,36,37,38,39,
		40,41,42,43,   45,46,47,48,49,
		50,51,52,53,54,55,56,57,58,59,
		60,61,62,63,64,65,66,67,68,69,
		70,71,   73,74,75,76,77,78,79,
		80,81,82,83,84,85,86,87,88,89,
		90,91,92,93,94,95,96,97,98,99,
		100,101,102,103,104,105,106,107,108,109,
	};
	
	// 60 - 케릭창으로 강제 이동.
	static public int S_OP_270[] = {
		1, 3, 5,  6, 9,
		5, 10, 16, 
		22, 27,  28, 
		32, 34,
		48, 
		56, 58, 
		60, 61, 64, 
		71, 72, 73, 77, 78, 
		86, 88, 
		93, 94, 
		104, 106, 108, 
		110, 111, 113, 114, 117, 
		121, 128, 129,
		130
	};

/*
4
 : ok, cancel 아이템 목록창 뜸.
13
 : 파란글씨로 영어로 숄라숄라 됨..
8
 : 배우기 위해 지불할 재료가 부족합니다.
30 - 포탈 아님.
 : 0x55 로 클라가 응답
47
 : buy, cancel 거래창 뜸.
48
 : 타이틀과 혈마크가 다 사라짐..
 : 정보 초기화 및 재 설정 하는거 인듯.
50
 : buy, cancel
53
 : 핑타임 출력
58
 : 케릭터 가운데에 바란색 글씨로 표현할 수 있음. (메세지 숫자로)
83 - poly 인거같은데 패킷구조가 다른듯.
 : gfx 변경
90
 : 군주로 변함
97
 : 포탈용으로 쓰이는 거인가? 크라가 18을 던짐
104
 : 공격맞는 모션이 나옴.
123
 : 울렁울렁~
*/
	// 본서버용 270
	static public int S_OP_270_bon[] = {
		1, 7, 9, 
		10, 11, 16, 18, 
		23, 26, 29, 
		31, 
		46, 
		76, 
		95, 
		108, 
		110, 113, 115, 
		125, 128, 129, 
		130
	};
	
	
	
	
	
	
	
	
	/**
	 * 버전별 옵코드 변경을 위한 초기화 함수.
	 */
	static public void init(){
		C_OPCODE_LOGINPACKET 				= ServerOpcodesDatabase.find(C_OPCODE_LOGINPACKET, ServerOpcodes.TYPE.Client);
		C_OPCODE_RETURNTOLOGIN 				= ServerOpcodesDatabase.find(C_OPCODE_RETURNTOLOGIN, ServerOpcodes.TYPE.Client);
		C_OPCODE_NEWCHAR 					= ServerOpcodesDatabase.find(C_OPCODE_NEWCHAR, ServerOpcodes.TYPE.Client);
		C_OPCODE_LOGINTOSERVER				= ServerOpcodesDatabase.find(C_OPCODE_LOGINTOSERVER, ServerOpcodes.TYPE.Client);
		C_OPCODE_CHANGEACCOUNT				= ServerOpcodesDatabase.find(C_OPCODE_CHANGEACCOUNT, ServerOpcodes.TYPE.Client);
		C_OPCODE_DELETECHAR					= ServerOpcodesDatabase.find(C_OPCODE_DELETECHAR, ServerOpcodes.TYPE.Client);
		C_OPCODE_DUNGEON					= ServerOpcodesDatabase.find(C_OPCODE_DUNGEON, ServerOpcodes.TYPE.Client);
		C_OPCODE_REQUESTHEADING				= ServerOpcodesDatabase.find(C_OPCODE_REQUESTHEADING, ServerOpcodes.TYPE.Client);
		C_OPCODE_REQUESTMOVECHAR			= ServerOpcodesDatabase.find(C_OPCODE_REQUESTMOVECHAR, ServerOpcodes.TYPE.Client);  
		C_OPCODE_ITEMPICKUP					= ServerOpcodesDatabase.find(C_OPCODE_ITEMPICKUP, ServerOpcodes.TYPE.Client);
		C_OPCODE_ITEMDROP					= ServerOpcodesDatabase.find(C_OPCODE_ITEMDROP, ServerOpcodes.TYPE.Client);
		C_OPCODE_CHATTINGOPTION				= ServerOpcodesDatabase.find(C_OPCODE_CHATTINGOPTION, ServerOpcodes.TYPE.Client);
		C_OPCODE_DOORS						= ServerOpcodesDatabase.find(C_OPCODE_DOORS, ServerOpcodes.TYPE.Client);
		C_OPCODE_QUITGAME					= ServerOpcodesDatabase.find(C_OPCODE_QUITGAME, ServerOpcodes.TYPE.Client);
		C_OPCODE_REQUESTCHARSELETE			= ServerOpcodesDatabase.find(C_OPCODE_REQUESTCHARSELETE, ServerOpcodes.TYPE.Client);
		C_OPCODE_GIVEITEM					= ServerOpcodesDatabase.find(C_OPCODE_GIVEITEM, ServerOpcodes.TYPE.Client);
		C_OPCODE_REQUESTCHAT				= ServerOpcodesDatabase.find(C_OPCODE_REQUESTCHAT, ServerOpcodes.TYPE.Client);  
		C_OPCODE_USESKILL					= ServerOpcodesDatabase.find(C_OPCODE_USESKILL, ServerOpcodes.TYPE.Client);
		C_OPCODE_CLANOUT					= ServerOpcodesDatabase.find(C_OPCODE_CLANOUT, ServerOpcodes.TYPE.Client);
		C_OPCODE_ATTACK						= ServerOpcodesDatabase.find(C_OPCODE_ATTACK, ServerOpcodes.TYPE.Client);
		C_OPCODE_ATTACKBOW					= ServerOpcodesDatabase.find(C_OPCODE_ATTACKBOW, ServerOpcodes.TYPE.Client);
		C_OPCODE_REQUESTWHO					= ServerOpcodesDatabase.find(C_OPCODE_REQUESTWHO, ServerOpcodes.TYPE.Client);
		C_OPCODE_REQUESTCHATWHISPER			= ServerOpcodesDatabase.find(C_OPCODE_REQUESTCHATWHISPER, ServerOpcodes.TYPE.Client);
		C_OPCODE_USEITEM					= ServerOpcodesDatabase.find(C_OPCODE_USEITEM, ServerOpcodes.TYPE.Client);
		C_OPCODE_AUTOSAVE					= ServerOpcodesDatabase.find(C_OPCODE_AUTOSAVE, ServerOpcodes.TYPE.Client);
		C_OPCODE_NONE_1						= ServerOpcodesDatabase.find(C_OPCODE_NONE_1, ServerOpcodes.TYPE.Client);
		C_OPCODE_PING						= ServerOpcodesDatabase.find(C_OPCODE_PING, ServerOpcodes.TYPE.Client);
		C_OPCODE_CLANMAKE					= ServerOpcodesDatabase.find(C_OPCODE_CLANMAKE, ServerOpcodes.TYPE.Client);
		C_OPCODE_CLANJOIN					= ServerOpcodesDatabase.find(C_OPCODE_CLANJOIN, ServerOpcodes.TYPE.Client);
		C_OPCODE_ASK						= ServerOpcodesDatabase.find(C_OPCODE_ASK, ServerOpcodes.TYPE.Client);
		C_OPCODE_BLOCK_NAME					= ServerOpcodesDatabase.find(C_OPCODE_BLOCK_NAME, ServerOpcodes.TYPE.Client);
		C_OPCODE_NPCACTION					= ServerOpcodesDatabase.find(C_OPCODE_NPCACTION, ServerOpcodes.TYPE.Client);
		C_OPCODE_SHOP						= ServerOpcodesDatabase.find(C_OPCODE_SHOP, ServerOpcodes.TYPE.Client);
		C_OPCODE_NPCTALK					= ServerOpcodesDatabase.find(C_OPCODE_NPCTALK, ServerOpcodes.TYPE.Client);
		C_OPCODE_TITLE						= ServerOpcodesDatabase.find(C_OPCODE_TITLE, ServerOpcodes.TYPE.Client);
		C_OPCODE_BOOKMARK					= ServerOpcodesDatabase.find(C_OPCODE_BOOKMARK, ServerOpcodes.TYPE.Client);
		C_OPCODE_BOOKMARKDELETE				= ServerOpcodesDatabase.find(C_OPCODE_BOOKMARKDELETE, ServerOpcodes.TYPE.Client);
		C_OPCODE_CLANMARKUPLOAD				= ServerOpcodesDatabase.find(C_OPCODE_CLANMARKUPLOAD, ServerOpcodes.TYPE.Client);
		C_OPCODE_CLANMARKDOWNLOAD			= ServerOpcodesDatabase.find(C_OPCODE_CLANMARKDOWNLOAD, ServerOpcodes.TYPE.Client);
		C_OPCODE_ClanWar					= ServerOpcodesDatabase.find(C_OPCODE_ClanWar, ServerOpcodes.TYPE.Client);
		C_OPCODE_CLANKIN					= ServerOpcodesDatabase.find(C_OPCODE_CLANKIN, ServerOpcodes.TYPE.Client);
		C_OPCODE_TRADE						= ServerOpcodesDatabase.find(C_OPCODE_TRADE, ServerOpcodes.TYPE.Client);
		C_OPCODE_TREADCANCEL				= ServerOpcodesDatabase.find(C_OPCODE_TREADCANCEL, ServerOpcodes.TYPE.Client);
		C_OPCODE_TREADOK					= ServerOpcodesDatabase.find(C_OPCODE_TREADOK, ServerOpcodes.TYPE.Client);
		C_OPCODE_TREADADDITEM				= ServerOpcodesDatabase.find(C_OPCODE_TREADADDITEM, ServerOpcodes.TYPE.Client);
		C_OPCODE_TAXSETTING					= ServerOpcodesDatabase.find(C_OPCODE_TAXSETTING, ServerOpcodes.TYPE.Client);
		C_OPCODE_TaxTotalGet				= ServerOpcodesDatabase.find(C_OPCODE_TaxTotalGet, ServerOpcodes.TYPE.Client);
		C_OPCODE_STATDICE					= ServerOpcodesDatabase.find(C_OPCODE_STATDICE, ServerOpcodes.TYPE.Client);
		C_OPCODE_SMITH_FINAL				= ServerOpcodesDatabase.find(C_OPCODE_SMITH_FINAL, ServerOpcodes.TYPE.Client);
		C_OPCODE_SMITH						= ServerOpcodesDatabase.find(C_OPCODE_SMITH, ServerOpcodes.TYPE.Client);
		C_OPCODE_TIMELEFTOK					= ServerOpcodesDatabase.find(C_OPCODE_TIMELEFTOK, ServerOpcodes.TYPE.Client);
		C_OPCODE_TaxTotalPut				= ServerOpcodesDatabase.find(C_OPCODE_TaxTotalPut, ServerOpcodes.TYPE.Client);
		C_OPCODE_CLANINFO					= ServerOpcodesDatabase.find(C_OPCODE_CLANINFO, ServerOpcodes.TYPE.Client);
		C_OPCODE_SKILLBUY					= ServerOpcodesDatabase.find(C_OPCODE_SKILLBUY, ServerOpcodes.TYPE.Client);
		C_OPCODE_SKILLBUYOK					= ServerOpcodesDatabase.find(C_OPCODE_SKILLBUYOK, ServerOpcodes.TYPE.Client);
		C_OPCODE_KINGDOMWARTIMESELECT		= ServerOpcodesDatabase.find(C_OPCODE_KINGDOMWARTIMESELECT, ServerOpcodes.TYPE.Client);
		C_OPCODE_KINGDOMWARTIMESELECTFINAL	= ServerOpcodesDatabase.find(C_OPCODE_KINGDOMWARTIMESELECTFINAL, ServerOpcodes.TYPE.Client);
		C_OPCODE_POTALOK					= ServerOpcodesDatabase.find(C_OPCODE_POTALOK, ServerOpcodes.TYPE.Client);
		C_OPCODE_TargetSelect				= ServerOpcodesDatabase.find(C_OPCODE_TargetSelect, ServerOpcodes.TYPE.Client);
		C_OPCODE_REQUESTHYPERTEXT			= ServerOpcodesDatabase.find(C_OPCODE_REQUESTHYPERTEXT, ServerOpcodes.TYPE.Client);
		C_OPCODE_BOARDWRITE				    = ServerOpcodesDatabase.find(C_OPCODE_BOARDWRITE, ServerOpcodes.TYPE.Client);
		C_OPCODE_BOARDNEXT				    = ServerOpcodesDatabase.find(C_OPCODE_BOARDNEXT, ServerOpcodes.TYPE.Client);
		C_OPCODE_BOARDREAD				    = ServerOpcodesDatabase.find(C_OPCODE_BOARDREAD, ServerOpcodes.TYPE.Client);
		C_OPCODE_BOARDDELETE				= ServerOpcodesDatabase.find(C_OPCODE_BOARDDELETE, ServerOpcodes.TYPE.Client);
		C_OPCODE_PARTY					    = ServerOpcodesDatabase.find(C_OPCODE_PARTY, ServerOpcodes.TYPE.Client);
		C_OPCODE_PARTYOUT					= ServerOpcodesDatabase.find(C_OPCODE_PARTYOUT, ServerOpcodes.TYPE.Client);
		C_OPCODE_PARTYLIST				    = ServerOpcodesDatabase.find(C_OPCODE_PARTYLIST, ServerOpcodes.TYPE.Client);
		C_OPCODE_BOARDCLICK				    = ServerOpcodesDatabase.find(C_OPCODE_BOARDCLICK, ServerOpcodes.TYPE.Client);
		C_OPCODE_NEWCHAR_STAT				= ServerOpcodesDatabase.find(C_OPCODE_NEWCHAR_STAT, ServerOpcodes.TYPE.Client);
		C_OPCODE_POTAL_POINTER			    = ServerOpcodesDatabase.find(C_OPCODE_POTAL_POINTER, ServerOpcodes.TYPE.Client);
		C_OPCODE_DUSTBIN					= ServerOpcodesDatabase.find(C_OPCODE_DUSTBIN, ServerOpcodes.TYPE.Client);
		C_OPCODE_REQUESTCHATGLOBAL		    = ServerOpcodesDatabase.find(C_OPCODE_REQUESTCHATGLOBAL, ServerOpcodes.TYPE.Client);
		C_OPCODE_REQUESTRESTART			    = ServerOpcodesDatabase.find(C_OPCODE_REQUESTRESTART, ServerOpcodes.TYPE.Client);
		C_OPCODE_UNKNOW					    = ServerOpcodesDatabase.find(C_OPCODE_UNKNOW, ServerOpcodes.TYPE.Client);
		C_OPCODE_NOTICEOK					= ServerOpcodesDatabase.find(C_OPCODE_NOTICEOK, ServerOpcodes.TYPE.Client);
		C_OPCODE_LETTER					    = ServerOpcodesDatabase.find(C_OPCODE_LETTER, ServerOpcodes.TYPE.Client);
		C_OPCODE_SERVER_VERSION			    = ServerOpcodesDatabase.find(C_OPCODE_SERVER_VERSION, ServerOpcodes.TYPE.Client);
		C_OPCODE_PKCOUNT					= ServerOpcodesDatabase.find(C_OPCODE_PKCOUNT, ServerOpcodes.TYPE.Client);
		C_OPCODE_GROUPCHATTING			    = ServerOpcodesDatabase.find(C_OPCODE_GROUPCHATTING, ServerOpcodes.TYPE.Client);
		C_OPCODE_USERSHOP					= ServerOpcodesDatabase.find(C_OPCODE_USERSHOP, ServerOpcodes.TYPE.Client);
		C_OPCODE_USERSHOPLIST				= ServerOpcodesDatabase.find(C_OPCODE_USERSHOPLIST, ServerOpcodes.TYPE.Client);
		C_OPCODE_FRIENDLIST					= ServerOpcodesDatabase.find(C_OPCODE_FRIENDLIST, ServerOpcodes.TYPE.Client);
		C_OPCODE_FRIENDADD					= ServerOpcodesDatabase.find(C_OPCODE_FRIENDADD, ServerOpcodes.TYPE.Client);
		C_OPCODE_FRIENDDEL					= ServerOpcodesDatabase.find(C_OPCODE_FRIENDDEL, ServerOpcodes.TYPE.Client);
		C_OPCODE_INTERFACESAVE				= ServerOpcodesDatabase.find(C_OPCODE_INTERFACESAVE, ServerOpcodes.TYPE.Client);
		C_OPCODE_SKILLBUY_ELF				= ServerOpcodesDatabase.find(C_OPCODE_SKILLBUY_ELF, ServerOpcodes.TYPE.Client);
		C_OPCODE_MOTION						= ServerOpcodesDatabase.find(C_OPCODE_MOTION, ServerOpcodes.TYPE.Client);
		
		S_OPCODE_SERVERVERSION				= ServerOpcodesDatabase.find(S_OPCODE_SERVERVERSION, ServerOpcodes.TYPE.Server);
		S_OPCODE_LOGINFAILS					= ServerOpcodesDatabase.find(S_OPCODE_LOGINFAILS, ServerOpcodes.TYPE.Server);
		S_OPCODE_CHARAMOUNT					= ServerOpcodesDatabase.find(S_OPCODE_CHARAMOUNT, ServerOpcodes.TYPE.Server);
		S_OPCODE_CHARLIST					= ServerOpcodesDatabase.find(S_OPCODE_CHARLIST, ServerOpcodes.TYPE.Server);
		S_OPCODE_NEWCHARPACK				= ServerOpcodesDatabase.find(S_OPCODE_NEWCHARPACK, ServerOpcodes.TYPE.Server);
		S_OPCODE_DETELECHAROK				= ServerOpcodesDatabase.find(S_OPCODE_DETELECHAROK, ServerOpcodes.TYPE.Server);
		S_OPCODE_UNKNOWN1					= ServerOpcodesDatabase.find(S_OPCODE_UNKNOWN1, ServerOpcodes.TYPE.Server);
		S_OPCODE_BlindPotion				= ServerOpcodesDatabase.find(S_OPCODE_BlindPotion, ServerOpcodes.TYPE.Server);
		S_OPCODE_CHARPACK					= ServerOpcodesDatabase.find(S_OPCODE_CHARPACK, ServerOpcodes.TYPE.Server);
		S_OPCODE_OWNCHARSTATUS				= ServerOpcodesDatabase.find(S_OPCODE_OWNCHARSTATUS, ServerOpcodes.TYPE.Server);
		S_OPCODE_HPUPDATE					= ServerOpcodesDatabase.find(S_OPCODE_HPUPDATE, ServerOpcodes.TYPE.Server);
		S_OPCODE_ItemBressChange			= ServerOpcodesDatabase.find(S_OPCODE_ItemBressChange, ServerOpcodes.TYPE.Server);
		S_OPCODE_GLOBALCHAT					= ServerOpcodesDatabase.find(S_OPCODE_GLOBALCHAT, ServerOpcodes.TYPE.Server);
		S_OPCODE_SERVERMSG					= ServerOpcodesDatabase.find(S_OPCODE_SERVERMSG, ServerOpcodes.TYPE.Server);
		S_OPCODE_RESTORE					= ServerOpcodesDatabase.find(S_OPCODE_RESTORE, ServerOpcodes.TYPE.Server);
		S_OPCODE_MOVEOBJECT					= ServerOpcodesDatabase.find(S_OPCODE_MOVEOBJECT, ServerOpcodes.TYPE.Server);
		S_OPCODE_NORMALCHAT					= ServerOpcodesDatabase.find(S_OPCODE_NORMALCHAT, ServerOpcodes.TYPE.Server);
		S_OPCODE_WHISPERCHAT				= ServerOpcodesDatabase.find(S_OPCODE_WHISPERCHAT, ServerOpcodes.TYPE.Server);
		S_OPCODE_DELETEOBJECT				= ServerOpcodesDatabase.find(S_OPCODE_DELETEOBJECT, ServerOpcodes.TYPE.Server);
		S_OPCODE_ITEMADD					= ServerOpcodesDatabase.find(S_OPCODE_ITEMADD, ServerOpcodes.TYPE.Server);
		S_OPCODE_ITEMDELETE					= ServerOpcodesDatabase.find(S_OPCODE_ITEMDELETE, ServerOpcodes.TYPE.Server);
		S_OPCODE_ITEMEQUIP					= ServerOpcodesDatabase.find(S_OPCODE_ITEMEQUIP, ServerOpcodes.TYPE.Server);
		S_OPCODE_ITEMCOUNT					= ServerOpcodesDatabase.find(S_OPCODE_ITEMCOUNT, ServerOpcodes.TYPE.Server);
		S_OPCODE_OBJLIGHT					= ServerOpcodesDatabase.find(S_OPCODE_OBJLIGHT, ServerOpcodes.TYPE.Server);
		S_OPCODE_CHANGEHEADING				= ServerOpcodesDatabase.find(S_OPCODE_CHANGEHEADING, ServerOpcodes.TYPE.Server);
		S_OPCODE_OBJECTMODE					= ServerOpcodesDatabase.find(S_OPCODE_OBJECTMODE, ServerOpcodes.TYPE.Server);
		S_OPCODE_SKILLINV					= ServerOpcodesDatabase.find(S_OPCODE_SKILLINV, ServerOpcodes.TYPE.Server);
		S_OPCODE_SKILLDELETE				= ServerOpcodesDatabase.find(S_OPCODE_SKILLDELETE, ServerOpcodes.TYPE.Server);
		S_OPCODE_DOACTION					= ServerOpcodesDatabase.find(S_OPCODE_DOACTION, ServerOpcodes.TYPE.Server);
		S_OPCODE_GAMETIME					= ServerOpcodesDatabase.find(S_OPCODE_GAMETIME, ServerOpcodes.TYPE.Server);
		S_OPOCDE_ATTRIBUTE					= ServerOpcodesDatabase.find(S_OPOCDE_ATTRIBUTE, ServerOpcodes.TYPE.Server);
		S_OPCODE_AttackPacket				= ServerOpcodesDatabase.find(S_OPCODE_AttackPacket, ServerOpcodes.TYPE.Server);
		S_OPCODE_YES_NO						= ServerOpcodesDatabase.find(S_OPCODE_YES_NO, ServerOpcodes.TYPE.Server);
		S_OPCODE_SetClientLock				= ServerOpcodesDatabase.find(S_OPCODE_SetClientLock, ServerOpcodes.TYPE.Server);
		S_OPCODE_ABILITY					= ServerOpcodesDatabase.find(S_OPCODE_ABILITY, ServerOpcodes.TYPE.Server);
		S_OPCODE_POLY						= ServerOpcodesDatabase.find(S_OPCODE_POLY, ServerOpcodes.TYPE.Server);
		S_OPCODE_OBJECTMAP					= ServerOpcodesDatabase.find(S_OPCODE_OBJECTMAP, ServerOpcodes.TYPE.Server);
		S_OPCODE_SKILLHASTE					= ServerOpcodesDatabase.find(S_OPCODE_SKILLHASTE, ServerOpcodes.TYPE.Server);
		S_OPCODE_SHOWHTML					= ServerOpcodesDatabase.find(S_OPCODE_SHOWHTML, ServerOpcodes.TYPE.Server);
		S_OPCODE_SHOPBUY					= ServerOpcodesDatabase.find(S_OPCODE_SHOPBUY, ServerOpcodes.TYPE.Server);
		S_OPCODE_SHOPSELL					= ServerOpcodesDatabase.find(S_OPCODE_SHOPSELL, ServerOpcodes.TYPE.Server);
		S_OPCODE_SHOTSAY					= ServerOpcodesDatabase.find(S_OPCODE_SHOTSAY, ServerOpcodes.TYPE.Server);
		S_OPCODE_TITLECHANGE				= ServerOpcodesDatabase.find(S_OPCODE_TITLECHANGE, ServerOpcodes.TYPE.Server);
		S_OPCODE_BOOKMARKS					= ServerOpcodesDatabase.find(S_OPCODE_BOOKMARKS, ServerOpcodes.TYPE.Server);
		S_OPCODE_WAREHOUSE					= ServerOpcodesDatabase.find(S_OPCODE_WAREHOUSE, ServerOpcodes.TYPE.Server);
		S_OPCODE_PoisonAndLock				= ServerOpcodesDatabase.find(S_OPCODE_PoisonAndLock, ServerOpcodes.TYPE.Server);
		S_OPCODE_LINEAGEWEATHER				= ServerOpcodesDatabase.find(S_OPCODE_LINEAGEWEATHER, ServerOpcodes.TYPE.Server);
		S_OPCODE_OBJECTINVISA				= ServerOpcodesDatabase.find(S_OPCODE_OBJECTINVISA, ServerOpcodes.TYPE.Server);
		S_OPCODE_DownLoadMark				= ServerOpcodesDatabase.find(S_OPCODE_DownLoadMark, ServerOpcodes.TYPE.Server);
		S_OPCODE_CLANWAR					= ServerOpcodesDatabase.find(S_OPCODE_CLANWAR, ServerOpcodes.TYPE.Server);
		S_OPCODE_EFFECT						= ServerOpcodesDatabase.find(S_OPCODE_EFFECT, ServerOpcodes.TYPE.Server);
		S_OPCODE_MagicAttackPacket			= ServerOpcodesDatabase.find(S_OPCODE_MagicAttackPacket, ServerOpcodes.TYPE.Server);
		S_OPCODE_TRADE					    = ServerOpcodesDatabase.find(S_OPCODE_TRADE, ServerOpcodes.TYPE.Server);
		S_OPCODE_TRADEADDITEM				= ServerOpcodesDatabase.find(S_OPCODE_TRADEADDITEM, ServerOpcodes.TYPE.Server);
		S_OPCODE_TRADESTATUS				= ServerOpcodesDatabase.find(S_OPCODE_TRADESTATUS, ServerOpcodes.TYPE.Server);
		S_OPCODE_ITEMIDENTIFY				= ServerOpcodesDatabase.find(S_OPCODE_ITEMIDENTIFY, ServerOpcodes.TYPE.Server);
		S_OPCODE_KINGDOMGUARDSELECT		    = ServerOpcodesDatabase.find(S_OPCODE_KINGDOMGUARDSELECT, ServerOpcodes.TYPE.Server);
		S_OPCODE_CASTLETAXRATIO			    = ServerOpcodesDatabase.find(S_OPCODE_CASTLETAXRATIO, ServerOpcodes.TYPE.Server);
		S_OPCODE_CASTLETAXOUT				= ServerOpcodesDatabase.find(S_OPCODE_CASTLETAXOUT, ServerOpcodes.TYPE.Server);
		S_OPCODE_SOUNDEFFECT				= ServerOpcodesDatabase.find(S_OPCODE_SOUNDEFFECT, ServerOpcodes.TYPE.Server);
		S_OPCODE_CASTLETAXIN				= ServerOpcodesDatabase.find(S_OPCODE_CASTLETAXIN, ServerOpcodes.TYPE.Server);
		S_OPCODE_CASTLEMASTER				= ServerOpcodesDatabase.find(S_OPCODE_CASTLEMASTER, ServerOpcodes.TYPE.Server);
		S_OPCODE_STATDICE					= ServerOpcodesDatabase.find(S_OPCODE_STATDICE, ServerOpcodes.TYPE.Server);
		S_OPCODE_SMITH					    = ServerOpcodesDatabase.find(S_OPCODE_SMITH, ServerOpcodes.TYPE.Server);
		S_OPCODE_TIMELEFT					= ServerOpcodesDatabase.find(S_OPCODE_TIMELEFT, ServerOpcodes.TYPE.Server);
		S_OPCODE_MPUPDATE					= ServerOpcodesDatabase.find(S_OPCODE_MPUPDATE, ServerOpcodes.TYPE.Server);
		S_OPCODE_SKILLBUY					= ServerOpcodesDatabase.find(S_OPCODE_SKILLBUY, ServerOpcodes.TYPE.Server);
		S_OPCODE_SUMMON_OWN_CHANGE		    = ServerOpcodesDatabase.find(S_OPCODE_SUMMON_OWN_CHANGE, ServerOpcodes.TYPE.Server);
		S_OPCODE_EXP						= ServerOpcodesDatabase.find(S_OPCODE_EXP, ServerOpcodes.TYPE.Server);
		S_OPCODE_EFFECTLOC				    = ServerOpcodesDatabase.find(S_OPCODE_EFFECTLOC, ServerOpcodes.TYPE.Server);
		S_OPCODE_CASTLEWARTIME			    = ServerOpcodesDatabase.find(S_OPCODE_CASTLEWARTIME, ServerOpcodes.TYPE.Server);
		S_OPCODE_POTAL					    = ServerOpcodesDatabase.find(S_OPCODE_POTAL, ServerOpcodes.TYPE.Server);
		S_OPCODE_ChangeSpMr				    = ServerOpcodesDatabase.find(S_OPCODE_ChangeSpMr, ServerOpcodes.TYPE.Server);
		S_OPCODE_PromptTargetSelect		    = ServerOpcodesDatabase.find(S_OPCODE_PromptTargetSelect, ServerOpcodes.TYPE.Server);
		S_OPCODE_SetObjectName			    = ServerOpcodesDatabase.find(S_OPCODE_SetObjectName, ServerOpcodes.TYPE.Server);
		S_OPCODE_DISPOSITION				= ServerOpcodesDatabase.find(S_OPCODE_DISPOSITION, ServerOpcodes.TYPE.Server);
		S_OPCODE_HYPERTEXTINPUT			    = ServerOpcodesDatabase.find(S_OPCODE_HYPERTEXTINPUT, ServerOpcodes.TYPE.Server);
		S_OPCODE_LETTERREAD				    = ServerOpcodesDatabase.find(S_OPCODE_LETTERREAD, ServerOpcodes.TYPE.Server);
		S_OPCODE_BOARDLIST				    = ServerOpcodesDatabase.find(S_OPCODE_BOARDLIST, ServerOpcodes.TYPE.Server);
		S_OPCODE_BOARDREAD				    = ServerOpcodesDatabase.find(S_OPCODE_BOARDREAD, ServerOpcodes.TYPE.Server);
		S_OPCODE_SKILLBRAVE				    = ServerOpcodesDatabase.find(S_OPCODE_SKILLBRAVE, ServerOpcodes.TYPE.Server);
		S_OPCODE_RETURNTOCHARSELECTE		= ServerOpcodesDatabase.find(S_OPCODE_RETURNTOCHARSELECTE, ServerOpcodes.TYPE.Server);
		S_OPCODE_DISCONNECT				    = ServerOpcodesDatabase.find(S_OPCODE_DISCONNECT, ServerOpcodes.TYPE.Server);
		S_OPCODE_HITRATIO					= ServerOpcodesDatabase.find(S_OPCODE_HITRATIO, ServerOpcodes.TYPE.Server);
		S_OPCODE_CRIMINAL					= ServerOpcodesDatabase.find(S_OPCODE_CRIMINAL, ServerOpcodes.TYPE.Server);
		S_OPCODE_MAGICSTR					= ServerOpcodesDatabase.find(S_OPCODE_MAGICSTR, ServerOpcodes.TYPE.Server);
		S_OPCODE_MAGICDEX					= ServerOpcodesDatabase.find(S_OPCODE_MAGICDEX, ServerOpcodes.TYPE.Server);
		S_OPCODE_SHIELD					    = ServerOpcodesDatabase.find(S_OPCODE_SHIELD, ServerOpcodes.TYPE.Server);
		S_OPCODE_ITEMSTATUS				    = ServerOpcodesDatabase.find(S_OPCODE_ITEMSTATUS, ServerOpcodes.TYPE.Server);
		S_OPCODE_Agit_List				    = ServerOpcodesDatabase.find(S_OPCODE_Agit_List, ServerOpcodes.TYPE.Server);
		S_OPCODE_Agit_Map					= ServerOpcodesDatabase.find(S_OPCODE_Agit_Map, ServerOpcodes.TYPE.Server);
		S_OPCODE_AQUABREATH				    = ServerOpcodesDatabase.find(S_OPCODE_AQUABREATH, ServerOpcodes.TYPE.Server);
		S_OPCODE_TRUETARGET				    = ServerOpcodesDatabase.find(S_OPCODE_TRUETARGET, ServerOpcodes.TYPE.Server);
		S_OPCODE_UNKNOWN2					= ServerOpcodesDatabase.find(S_OPCODE_UNKNOWN2, ServerOpcodes.TYPE.Server);
		S_OPCODE_CRYPTKEY					= ServerOpcodesDatabase.find(S_OPCODE_CRYPTKEY, ServerOpcodes.TYPE.Server);
		S_OPCODE_NOTICE					    = ServerOpcodesDatabase.find(S_OPCODE_NOTICE, ServerOpcodes.TYPE.Server);
		S_OPCODE_STATUS_AC				    = ServerOpcodesDatabase.find(S_OPCODE_STATUS_AC, ServerOpcodes.TYPE.Server);
		S_OPCODE_ITEMLIST					= ServerOpcodesDatabase.find(S_OPCODE_ITEMLIST, ServerOpcodes.TYPE.Server);
		S_OPCODE_UNKNOWN3					= ServerOpcodesDatabase.find(S_OPCODE_UNKNOWN3, ServerOpcodes.TYPE.Server);
		S_OPCODE_MINIMAP					= ServerOpcodesDatabase.find(S_OPCODE_MINIMAP, ServerOpcodes.TYPE.Server);
		S_OPCODE_KingdomSoldierSpawn		    = ServerOpcodesDatabase.find(S_OPCODE_KingdomSoldierSpawn, ServerOpcodes.TYPE.Server);
		S_OPCODE_RetreivePravateShop		= ServerOpcodesDatabase.find(S_OPCODE_RetreivePravateShop, ServerOpcodes.TYPE.Server);
		S_OPCODE_KingdomSoldierBuy		= ServerOpcodesDatabase.find(S_OPCODE_KingdomSoldierBuy, ServerOpcodes.TYPE.Server);
	}
}
