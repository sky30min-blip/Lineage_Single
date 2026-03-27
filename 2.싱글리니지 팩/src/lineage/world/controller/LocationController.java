package lineage.world.controller;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectLock;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class LocationController {

	static public void init(){
		
	}
	
	/**
	 * 귀환 가능한 맵인지 확인해주는 함수.
	 * 축순 및 이반도 확인함.
	 * @param o
	 * @param packet
	 * @return
	 */
	static public boolean isTeleportVerrYedHoraeZone(object o, boolean packet){
		//
		if(PluginController.init(LocationController.class, "isTeleportVerrYedHoraeZone", o, packet) != null)
			return false;
		//
		for(int i=0 ; i<Lineage.TeleportHomeImpossibilityMapLength ; ++i){
			if(Lineage.TeleportHomeImpossibilityMap[i] == o.getMap()){
				// 주변의 에너지가 순간 이동을 방해하고 있습니다. 여기에서 순간 이동은 사용할 수 없습니다.
				if(packet){
					o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 647));
					o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
				}
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 텔레포트 가능한 맵인지 확인해주는 함수.
	 * @param o
	 * @param packet
	 * @return
	 */
	static public boolean isTeleportZone(object o, boolean packet, boolean ment){
		for(int i=0 ; i<Lineage.TeleportPossibleMapLength ; ++i){
			if(Lineage.TeleportPossibleMap[i] == o.getMap())
				return true;
		}
		
		// 오만의 탑 지배 부적
		switch (o.getMap()) {
		case 101:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 1층 지배 부적"))
					return true;
			}
			break;
		case 102:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 2층 지배 부적"))
					return true;
			}
			break;
		case 103:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 3층 지배 부적"))
					return true;
			}
			break;
		case 104:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 4층 지배 부적"))
					return true;
			}
			break;
		case 105:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 5층 지배 부적"))
					return true;
			}
			break;
		case 106:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 6층 지배 부적"))
					return true;
			}
			break;
		case 107:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 7층 지배 부적"))
					return true;
			}
			break;
		case 108:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 8층 지배 부적"))
					return true;
			}
			break;
		case 109:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 9층 지배 부적"))
					return true;
			}
			break;
		case 110:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 10층 지배 부적"))
					return true;
			}
			break;
		case 200:
			for (ItemInstance item : o.getInventory().getList()) {
				if (item.getItem().getName().equalsIgnoreCase("오만의 탑 정상 지배 부적"))
					return true;
			}
			break;
		}
		
		// 주변의 에너지가 순간 이동을 방해하고 있습니다. 여기에서 순간 이동은 사용할 수 없습니다.
		if(packet){
			if(ment)
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 647));
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
		}
		return false;
	}

	/**
	 * 근처 마을로 좌표변경하는 메서드.
	 */
	static public void toHome(object o){
		switch(o.getMap()){
			case 0 : // 말섬
			case 1 : // 말섬던전 1층
			case 2 : // 말섬던전 2층
			case 3 : // 말섬 군터
			case 5 : // 글루딘행 배
			case 14 : // 지하통로
			case 91:	// 콜롯세움
			case 201:	// 마법사 30퀘 던전
			case 221:	// 게라드의 시험 던전
			case 222:	// 게라드의 시험 던전
			case 223:	// 게라드의 시험 던전
			case 224:	// 게라드의 시험 던전
			case 225:	// 게라드의 시험 던전
			case 226:	// 게라드의 시험 던전
			case 227:	// 게라드의 시험 던전
			case 228:	// 게라드의 시험 던전
			case 17408:	// 말하는섬 여관
				toTalkingIsland(o);
				break;
			case 15 : // 켄트내성
				toKent(o);
				break;
			case 6 : // 말하는섬행 배
			case 7 : // 본토던전 1층
			case 8 : // 본토던전 2층
			case 9 : // 본토던전 3층
			case 10 : // 본토던전 4층
			case 11 : // 본토던전 5층
			case 12 : // 본토던전 6층
			case 13 : // 본토던전 7층
			case 340:	// 시장
				toGludio(o);
				break;
			case 4 : // 본토
				if((o.getX()<32960)&&(o.getX()>32512)&&(o.getY()<33023)&&(o.getY()>32537)){
					// 글루딘
					toGludio(o);
				}else if( 	((o.getX()<33280)&&(o.getX()>32960)&&(o.getY()<33023)&&(o.getY()>32511)) ||
						((o.getX()<33280)&&(o.getX()>33088)&&(o.getY()<33087)&&(o.getY()>33023)) ){
					// 켄트
					toKent(o);
				}else if((o.getX()<32960)&&(o.getX()>32511)&&(o.getY()<32537)&&(o.getY()>32191)){
					//화민촌
					toOrcishForest(o);
				}else if((o.getX()<33216)&&(o.getX()>32960)&&(o.getY()<32511)&&(o.getY()>32191)){
					// 요숲
					if(o.getClassType()==Lineage.LINEAGE_CLASS_ELF)
						toElvenForest(o);
					else
						toOrcishForest(o);
					break;
				}else if((o.getX()<33472)&&(o.getX()>33216)&&(o.getY()<32511)&&(o.getY()>32191)){
					// 용의계곡
					toGiran(o);
				}else if(	((o.getX()<33856)&&(o.getX()>33472)&&(o.getY()<32511)&&(o.getY()>32191)) ||
						((o.getX()<33856)&&(o.getX()>33536)&&(o.getY()<32575)&&(o.getY()>32511)) ){
					// 발라카스 둥지
					toWelldone(o);
				}else if((o.getX()<32960)&&(o.getX()>32512)&&(o.getY()<33535)&&(o.getY()>33023)){
					//윈다우드
					toWindawood(o);
				}else if((o.getX()<33792)&&(o.getX()>33280)&&(o.getY()<33535)&&(o.getY()>33023)){
					// 하이네
					toHeine(o);
				}else if(	((o.getX()<33088)&&(o.getX()>32960)&&(o.getY()<33087)&&(o.getY()>33023)) ||
						((o.getX()<33280)&&(o.getX()>32959)&&(o.getY()<33535)&&(o.getY()>33087)) ){
					// 은기사
					toSilverknightTown(o);
				}else if(	((o.getX()<33536)&&(o.getX()>33280)&&(o.getY()<33023)&&(o.getY()>32511)) ||
						((o.getX()<33920)&&(o.getX()>33536)&&(o.getY()<33023)&&(o.getY()>32575)) ){
					// 기란
					toGiran(o);
				}else if(	((o.getX()<33920)&&(o.getX()>33856)&&(o.getY()<32575)&&(o.getY()>32191)) ||
						((o.getX()<34304)&&(o.getX()>33920)&&(o.getY()<32739)&&(o.getY()>32127)) ){
					// 오렌
					toOren(o);
				}else{
					// 아덴
					toAden(o);
				}
				break;
			case 16:	// 하딘에 연구소
			case 17:	// 네루파의 동굴
			case 18:	// 듀펠게넌 던전
			case 19:	// 요정숲 던전1층
			case 20:	// 요정숲 던전2층
			case 21:	// 요정숲 던전3층
			case 209:	// 다크엘프 던전 (요정 30레벨 퀘)
			case 210:
			case 211:
			case 212:
			case 213:
			case 214:
			case 215:
			case 216:
				if(o.getClassType()==Lineage.LINEAGE_CLASS_ELF){
					toElvenForest(o);
				}else{
					toOrcishForest(o);
				}
				break;
			case 23:	// 윈다우드성 던전
			case 24:	// 윈다우드성 던전
			case 29:	// 윈다우드 내성
			case 43:	// 개미굴
			case 44:	// 개미굴
			case 45:	// 개미굴
			case 46:	// 개미굴
			case 47:	// 개미굴
			case 48:	// 개미굴
			case 49:	// 개미굴
			case 50:	// 개미굴
			case 51:	// 개미굴
			case 217:	// 변종개미던전
			case 430:	// 정령의무덤
			case 541:	// 개미굴
			case 542:	// 개미굴
			case 543:	// 개미굴
				toWindawood(o);
				break;
			case 22:	// 게라드의 시험던전
			case 25:	// 수련던전 1층
			case 26:
			case 27:
			case 28:	// 수련던전 4층
			case 370:	// 시장
				toSilverknightTown(o);
				break;
			case 30:	// 용의계곡 던전1층
			case 31:	// 용의계곡 던전2층
			case 32:	// 용의계곡 던전3층
			case 33:	// 용의계곡 던전4층
			case 35:	// 용의계곡 던전5층
			case 36:	// 용의계곡 던전6층
			case 37:	// 안타라스 둥지
			case 350:	// 시장
			case 52:	// 기란 내성
			case 53:	// 기란 던전1층
			case 54:	// 기란 던전2층
			case 55:	// 기란 던전3층
			case 56:	// 기란 던전4층
				toGiran(o);
				break;
			case 59:	// 에바왕국 던전1층
			case 60:	// 에바왕국 던전2층
			case 61:	// 에바왕국 던전3층
			case 62:	// 에바의 성지
			case 63:	// 에바왕국
			case 64:	// 하이네 내성
			case 65:	// 파푸리온 둥지
			case 83:	// 잊혀진 섬행 배
				toHeine(o);
				break;
			case 66:	// 드워프 동굴
			case 67:	// 발라카스 둥지
				toWelldone(o);
				break;
			case 68:	// 노래하는 섬
			case 85:	// 노래하는 섬 던전
				toSingingisland(o);
				break;
			case 69:	// 숨겨진 계곡
			case 86:	// 숨겨진 계곡 던전
				toHiddenvalley(o);
				break;
			case 70:	// 잊혀진섬
			case 84:	// 하이네행 배
				toLostLand(o);
				break;
			case 72:	// 얼음 수정 동굴
			case 73:	// 얼음 여왕의 성
			case 74:	// 얼음 여왕의 성
			case 75:	// 상아탑 1층
			case 76:	// 상아탑 2층
			case 77:	// 상아탑 3층
			case 78:	// 상아탑 4층
			case 79:	// 상아탑 5층
			case 80:	// 상아탑 6층
			case 81:	// 상아탑 7층
			case 82:	// 상아탑 8층
			case 360:	// 시장
				toOren(o);
				break;
			case 237:	// 짐 던전
				if(o.getClassType()==Lineage.LINEAGE_CLASS_ELF){
					toElvenForest(o);
				}else{
					toGiran(o);
				}
				break;
			case 300:	// 아덴 내성
			case 301:	// 오만의 탑 지하수로
				toAden(o);
				break;
			case 304:	// 침묵의 동굴
				toSilenceCave(o);
				break;
			case 666:	// 지옥
				toHell(o);
				break;
			default :
				// 오만의 탑 101~200
				if(o.getMap()>100 && o.getMap()<201){
					toAden(o);
				}else{
					// 그외에는 그냥 군터로..
					o.setHomeMap( 3 );
					o.setHomeX( 32673 );
					o.setHomeY( 32792 );
				}
				break;
		}
	}

	/**
	 * 지옥 좌표설정.
	 */
	static private void toHell(object o){
		o.setHomeMap( 666 );
		switch(Util.random(1, 5)){
			case 1:
				o.setHomeX( 32696 );
				o.setHomeY( 32755 );
				break;
			case 2:
				o.setHomeX( 32785 );
				o.setHomeY( 32799 );
				break;
			case 3:
				o.setHomeX( 32721 );
				o.setHomeY( 32852 );
				break;
			case 4:
				o.setHomeX( 32722 );
				o.setHomeY( 32774 );
				break;
			case 5:
				o.setHomeX( 32687 );
				o.setHomeY( 32846 );
				break;
		}
	}

	/**
	 * 말하는섬 마을로 좌표설정.
	 */
	static public void toTalkingIsland(object o){
		o.setHomeMap( 0 );
		switch(Util.random(0, 4)){
			case 0:
				o.setHomeX( 32596 );
				o.setHomeY( 32916 );
				break;
			case 1:
				o.setHomeX( 32583 );
				o.setHomeY( 32931 );
				break;
			case 2:
				o.setHomeX( 32587 );
				o.setHomeY( 32947 );
				break;
			case 3:
				o.setHomeX( 32566 );
				o.setHomeY( 32952 );
				break;
			case 4:
				o.setHomeX( 32561 );
				o.setHomeY( 32973 );
				break;
		}
	}

	/**
	 * 켄트 마을로 좌표설정.
	 */
	static public void toKent(object o){
		o.setHomeMap( 4 );
		switch(Util.random(0, 4)){
			case 0:
				o.setHomeX( 33060 );
				o.setHomeY( 32745 );
				break;
			case 1:
				o.setHomeX( 33045 );
				o.setHomeY( 32757 );
				break;
			case 2:
				o.setHomeX( 33060 );
				o.setHomeY( 32770 );
				break;
			case 3:
				o.setHomeX( 33049 );
				o.setHomeY( 32789 );
				break;
			case 4:
				o.setHomeX( 33058 );
				o.setHomeY( 32806 );
				break;
		}
	}

	/**
	 * 글루디오 마을로 좌표설정.
	 */
	static public void toGludio(object o){
		o.setHomeMap( 4 );
		// 맵 리소스는 유지한 채, 글루디오 귀환 좌표만 구버전 중심좌표로 고정.
		o.setHomeX( 32606 );
		o.setHomeY( 32758 );
	}

	/**
	 * 오크숲 마을로 좌표설정
	 */
	static public void toOrcishForest(object o){
		o.setHomeMap( 4 );
		switch(Util.random(0, 3)){
			case 0:
				o.setHomeX( 32741 );
				o.setHomeY( 32436 );
				break;
			case 1:
				o.setHomeX( 32749 );
				o.setHomeY( 32446 );
				break;
			case 2:
				o.setHomeX( 32738 );
				o.setHomeY( 32452 );
				break;
			case 3:
				o.setHomeX( 32750 );
				o.setHomeY( 32435 );
				break;
		}
	}

	/**
	 * 요정숲 마을로 좌표설정
	 */
	static public void toElvenForest(object o){
		o.setHomeMap( 4 );
		switch(Util.random(0, 4)){
			case 0:
				o.setHomeX( 33068 );
				o.setHomeY( 32336 );
				break;
			case 1:
				o.setHomeX( 33076 );
				o.setHomeY( 32324 );
				break;
			case 2:
				o.setHomeX( 33052 );
				o.setHomeY( 32313 );
				break;
			case 3:
				o.setHomeX( 33071 );
				o.setHomeY( 32314 );
				break;
			case 4:
				o.setHomeX( 33030 );
				o.setHomeY( 32370 );
				break;
		}
	}

	/**
	 * 기란 마을로 좌표 설정.
	 */
	static public void toGiran(object o){
		o.setHomeMap( 4 );
		switch(Util.random(0, 4)){
			case 0:
				o.setHomeX( 33428 );
				o.setHomeY( 32823 );
				break;
			case 1:
				o.setHomeX( 33418 );
				o.setHomeY( 32818 );
				break;
			case 2:
				o.setHomeX( 33439 );
				o.setHomeY( 32817 );
				break;
			case 3:
				o.setHomeX( 33435 );
				o.setHomeY( 32803 );
				break;
			case 4:
				o.setHomeX( 33432 );
				o.setHomeY( 32824 );
				break;
		}
	}

	/**
	 * 웰던 마을로 좌표설정.
	 */
	static public void toWelldone(object o){
		o.setHomeMap( 4 );
		switch(Util.random(0, 5)){
			case 0:
				o.setHomeX( 33723 );
				o.setHomeY( 32512 );
				break;
			case 1:
				o.setHomeX( 33693 );
				o.setHomeY( 32513 );
				break;
			case 2:
				o.setHomeX( 33696 );
				o.setHomeY( 32498 );
				break;
			case 3:
				o.setHomeX( 33702 );
				o.setHomeY( 32492 );
				break;
			case 4:
				o.setHomeX( 33746 );
				o.setHomeY( 32499 );
				break;
			case 5:
				o.setHomeX( 33710 );
				o.setHomeY( 32521 );
				break;
		}
	}

	/**
	 * 윈다우드 마을로 좌표설정.
	 */
	static public void toWindawood(object o){
		o.setHomeMap( 4 );
		switch(Util.random(0, 2)){
			case 0:
				o.setHomeX( 32608 );
				o.setHomeY( 33178 );
				break;
			case 1:
				o.setHomeX( 32638 );
				o.setHomeY( 33203 );
				break;
			case 2:
				o.setHomeX( 32630 );
				o.setHomeY( 33179 );
				break;
		}
	}

	/**
	 * 하이네 마을 좌표 설정
	 */
	static public void toHeine(object o){
		o.setHomeMap( 4 );
		switch(Util.random(0, 3)){
			case 0:
				o.setHomeX( 33599 );
				o.setHomeY( 33252 );
				break;
			case 1:
				o.setHomeX( 33610 );
				o.setHomeY( 33241 );
				break;
			case 2:
				o.setHomeX( 33604 );
				o.setHomeY( 33236 );
				break;
			case 3:
				o.setHomeX( 33593 );
				o.setHomeY( 33242 );
				break;
		}
	}

	/**
	 * 은기사 마을 좌표 설정
	 */
	static public void toSilverknightTown(object o){
		o.setHomeMap( 4 );
		switch(Util.random(0, 4)){
			case 0:
				o.setHomeX( 33110 );
				o.setHomeY( 33365 );
				break;
			case 1:
				o.setHomeX( 33071 );
				o.setHomeY( 33402 );
				break;
			case 2:
				o.setHomeX( 33085 );
				o.setHomeY( 33402 );
				break;
			case 3:
				o.setHomeX( 33091 );
				o.setHomeY( 33396 );
				break;
			case 4:
				o.setHomeX( 33097 );
				o.setHomeY( 33366 );
				break;
		}
	}

	/**
	 * 오렌 마을로 좌표 설정.
	 */
	static public void toOren(object o){
		o.setHomeMap( 4 );
		switch(Util.random(0, 4)){
			case 0:
				o.setHomeX( 34054 );
				o.setHomeY( 32272 );
				break;
			case 1:
				o.setHomeX( 34052 );
				o.setHomeY( 32310 );
				break;
			case 2:
				o.setHomeX( 34081 );
				o.setHomeY( 32264 );
				break;
			case 3:
				o.setHomeX( 34052 );
				o.setHomeY( 32241 );
				break;
			case 4:
				o.setHomeX( 34025 );
				o.setHomeY( 32266 );
				break;
		}
	}

	/**
	 * 아덴 마을로 좌표 설정.
	 */
	static public void toAden(object o){
		o.setHomeMap( 4 );
		o.setHomeX( 33962 );
		o.setHomeY( 33259 );
	}

	/**
	 * 잊혀진섬 좌표 설정.
	 */
	static private void toLostLand(object o){
		o.setHomeMap( 70 );
		o.setHomeX( 32826 );
		o.setHomeY( 32851 );
	}

	static public void toSingingisland(object o){
		o.setHomeMap( 68 );
		o.setHomeX( 32770 );
		o.setHomeY( 32789 );
	}

	static public void toHiddenvalley(object o){
		o.setHomeMap( 69 );
		o.setHomeX( 32706 );
		o.setHomeY( 32877 );
	}
	
	/**
	 * 침묵에 동굴 
	 * @param o
	 */
	static public void toSilenceCave(object o){
		o.setHomeMap( 304 );
		o.setHomeX( 32807 );
		o.setHomeY( 32916 );
	}
}
