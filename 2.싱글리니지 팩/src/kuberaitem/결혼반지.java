package kuberaitem;

import lineage.bean.database.Item;
import lineage.bean.lineage.Kingdom;
import lineage.bean.lineage.Wedding;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.LocationController;
import lineage.world.controller.WeddingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.WeddingRing;

public class 결혼반지 extends WeddingRing {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new 결혼반지();
		return item;
	}

	private int useCount = 100;
	@Override
	public ItemInstance clone(Item item) {
		this.item = item;
		name = item.getNameId();
		gfx = item.getGroundGfx();
		if(item.getLimitTime() > 0)
			limitTime = System.currentTimeMillis() + (item.getLimitTime() * 1000);

		return this;
	}
	
	public int getUseCount() {
		return this.useCount;
	}
	public void initUseCount() {
		this.useCount = 100;
	}
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		Wedding w = WeddingController.find(cha.getObjectId());
		// 아무일도 일어나지 않았습니다.
		if (w == null) {
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
			return;
		}
		//
		if (!cha.getInventory().isAden(300000, false)) {
			ChattingController.toChatting(cha, "아데나가 부족합니다. (1회 30만)", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		//
		if (!LocationController.isTeleportZone(cha, true, true)) {
			ChattingController.toChatting(cha, "현재 위치는 이동이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		//
		switch(cha.getMap()) {
			    case 1:
				// 말하는섬던전 1층
				case 2:
				// 말하는섬던전 2층
				case 7:
				// 본토던전 1층
				case 8:
				// 본토던전 2층
				case 9:
				// 본토던전 3층
				case 10:
				// 본토던전 4층
				case 11:
				// 본토던전 5층
				case 12:
				// 본토던전 6층
				case 13:
				// 본토던전 7층
				case 14:
				// 지하통로
				case 15:
				// 켄트내성
				case 19:
				// 요정족 던전 1층
				case 20:
				// 요정족 던전 2층
				case 21:
				// 요정족 던전 3층
				case 23:
				// 윈다우드 던전 1층
				case 24:
				// 윈다우드 던전 2층
				case 25:
				// 수련던전 1층
				case 26:
				// 수련던전 2층
				case 27:
				// 수련던전 3층
				case 28:
				// 수련던전 4층
				case 29:
				// 윈성내성
				case 30:
				// 용의 던전 1층
				case 31:
				// 용의 던전 2층
				case 32:
				// 용의 던전 3층
				case 33:
				// 용의 던전 4층
				case 34:
				// 크레이의 방
				case 35:
				// 용의 던전 5층
				case 36:
				// 용의 던전 6층
				case 37:
				// 안타라스 둥지
				case 43:
				case 44:
				case 45:
				case 46:
				// 개미굴 1층
				case 47:
				case 48:
				case 49:
				case 50:
				// 개미굴 2층
				case 51:
				// 개미굴 3층
				case 53:
				// 기란감옥 1층
				case 54:
				// 기란감옥 2층
				case 55:
				// 기란감옥 3층
				case 56:
				// 기란감옥 4층
				case 59:
				// 에바의 왕국 1층
				case 60:
				// 에바의 왕국 2층
				case 61:
				// 에바의 왕국 3층
				case 62:
				// 에바의 신전
				case 63:
				// 에바의 왕국 4층
				case 64:
				// 하이네내성
				case 65:
				// 파푸리온 둥지
				case 66:
				// 지저성
				case 67:
				// 발라카스 둥지
				case 70:
				// 잊혀진 섬
				case 72:
				// 얼음여왕의 성 1층
				case 73:
				// 얼음여왕의 성 2층
				case 74:
				// 얼음여왕의 성 3층
				case 75:
				// 상아탑 1층
				case 76:
				// 상아탑 2층
				case 77:
				// 상아탑 3층
				case 78:
				// 상아탑 4층
				case 79:
				// 상아탑 5층
				case 80:
				// 상아탑 6층
				case 81:
				// 상아탑 7층
				case 82:
				// 상아탑 8층
				case 99:
				// 감옥
				case 101:
				//오만의 탑 01층
				case 102:
				//오만의 탑 02층
				case 103:
				//오만의 탑 03층
				case 104:
				// 오만의 탑 04층
				case 105:
				// 오만의 탑 05층
				case 106:
				// 오만의 탑 06층
				case 107:
				// 오만의 탑 07층
				case 108:
				// 오만의 탑 08층
				case 109:
				// 오만의 탑 09층
				case 110:
				// 오만의 탑 10층
				case 111:
				// 오만의 탑 11층
				case 112:
				// 오만의 탑 12층
				case 113:
				// 오만의 탑 13층
				case 114:
				// 오만의 탑 14층
				case 115:
				// 오만의 탑 15층
				case 116:
				// 오만의 탑 16층
				case 117:
				// 오만의 탑 17층
				case 118:
				// 오만의 탑 18층
				case 119:
				// 오만의 탑 19층
				case 120:
				// 오만의 탑 20층
				case 121:
				// 오만의 탑 21층
				case 122:
				// 오만의 탑 22층
				case 123:
				// 오만의 탑 23층
				case 124:
				// 오만의 탑 24층
				case 125:
				// 오만의 탑 25층
				case 126:
				// 오만의 탑 26층
				case 127:
				// 오만의 탑 27층
				case 128:
				// 오만의 탑 28층
				case 129:
				// 오만의 탑 29층
				case 130:
				// 오만의 탑 30층
				case 131:
				// 오만의 탑 31층
				case 132:
				// 오만의 탑 32층
				case 133:
				// 오만의 탑 33층
				case 134:
				// 오만의 탑 34층
				case 135:
				// 오만의 탑 35층
				case 136:
				// 오만의 탑 36층
				case 137:
				// 오만의 탑 37층
				case 138:
				// 오만의 탑 38층
				case 139:
				// 오만의 탑 39층
				case 140:
				// 오만의 탑 40층
				case 141:
				// 오만의 탑 41층
				case 142:
				// 오만의 탑 42층
				case 143:
				// 오만의 탑 43층
				case 144:
				// 오만의 탑 44층
				case 145:
				// 오만의 탑 45층
				case 146:
				// 오만의 탑 46층
				case 147:
				// 오만의 탑 47층
				case 148:
				// 오만의 탑 48층
				case 149:
				// 오만의 탑 49층
				case 150:
				// 오만의 탑 만50층
				case 151:
				// 오만의 탑 51층
				case 152:
				// 오만의 탑 52층
				case 153:
				// 오만의 탑 53층
				case 154:
				// 오만의 탑 54층
				case 155:
				// 오만의 탑 55층
				case 156:
				// 오만의 탑 56층
				case 157:
				// 오만의 탑 57층
				case 158:
				// 오만의 탑 58층
				case 159:
				// 오만의 탑 59층
				case 160:
				// 오만의 탑 60층
				case 161:
				// 오만의 탑 61층
				case 162:
				// 오만의 탑 62층
				case 163:
				// 오만의 탑 63층
				case 164:
				// 오만의 탑 64층
				case 165:
				// 오만의 탑 65층
				case 166:
				// 오만의 탑 66층
				case 167:
				// 오만의 탑 67층
				case 168:
				// 오만의 탑 68층
				case 169:
				// 오만의 탑 69층
				case 170:
				// 오만의 탑 70층
				case 171:
				// 오만의 탑 71층
				case 172:
				// 오만의 탑 72층
				case 173:
				// 오만의 탑 
				case 174:
				// 오만의 탑 74층
				case 175:
				// 오만의 탑 75층
				case 176:
				// 오만의 탑 76층
				case 177:
				// 오만의 탑 77층
				case 178:
				// 오만의 탑 78층
				case 179:
				// 오만의 탑 79층
				case 180:
				// 오만의 탑 80층
				case 181:
				// 오만의 탑 81층
				case 182:
				// 오만의 탑 82층
				case 183:
				// 오만의 탑 83층
				case 184:
				// 오만의 탑 84층
				case 185:
				// 오만의 탑 85층
				case 186:
				// 오만의 탑 86층
				case 187:
				// 오만의 탑 87층
				case 188:
				// 오만의 탑 88층
				case 189:
				// 오만의 탑 89층
				case 190:
				// 오만의 탑 90층
				case 191:
				// 오만의 탑 91층
				case 192:
				// 오만의 탑 92층
				case 193:
				// 오만의 탑 93층
				case 194:
				// 오만의 탑 94층
				case 195:
				// 오만의 탑 95층
				case 196:
				// 오만의 탑 96층
				case 197:
				// 오만의 탑 97층
				case 198:
				// 오만의 탑 98층
				case 199:
				// 오만의 탑 99층
				case 200:
				// 오만의 탑 100층
				case 509:
				// 결투장
				case 240:
				// 켄트성던전1층
				case 241:
				// 켄트성던전2층
				case 242:
				// 켄트성던전3층
				case 243:
				// 켄트성던전4층
				case 244:
				// 오염된 축복의 땅
				case 248:
				// 기란성 던전1층
				case 249:
				// 기란성 던전 2층
				case 250:
				// 기란성 던전 3층
				case 251:
				// 기란성 던전 4층
				case 252:
				// 하이네성 던전 1층
				case 253:
				// 하이네성 던전 2층
				case 254:
				// 하이네성 던전 3층
				case 255:
				// 지저성 던전 1층
				case 256:
				// 지저성 던전 2층
				case 257:
				// 아덴성 던전 1층
				case 258:
				// 아덴성 던전 2층
				case 259:
				// 아덴성 던전 3층
				case 300:
				// 아덴내성
				case 301:
				// 오만의 탑 지하 수로
				case 303:
				// 몽환의섬
				case 304:
				// 침묵의 동굴
				case 307:
				// 지하 침공로 1층
				case 308:
				// 지하 침공로 2층
				case 309:
				// 지하 침공로 3층
				case 310:
				// 오움 던전
				case 320:
				// 디아드 요새
				case 330:
				// 광물 동굴
				case 400:
				// 대공동 저항군지역
				case 401:
				// 대공동 은둔자지역
				case 410:
				// 마족신전
				case 420:
				// 지저호수
				case 430:
				// 정령의 무덤
				case 440:
				// 해적섬 (전반부)
				case 441:
				// 해적섬 던전 1층
				case 442:
				// 해적섬 던전 1층
				case 443:
				// 해적섬 던전 1층
				case 450:
				//라스타바드 정문
				case 451:
				//라스타바드 1층 집회장
				case 452:
				//라스타바드 1층 돌격대 훈련장
				case 453:
				//라스타바드 1층 마수군왕의 집무실
				case 454:
				//라스타바드 1층 야수 조련실
				case 455:
				//라스타바드 1층 야수 훈련장
				case 456:
				//라스타바드 1층 마수 소환실
				case 457:
				//라스타바드 1층 어둠의 결계
				case 460:
				//라스타바드 2층 흑마법 수련장
				case 461:
				//라스타바드 2층 흑마법 연구실
				case 462:
				//라스타바드 2층 마령군왕의 집무실
				case 463:
				//라스타바드 2층 마령군왕의 서재
				case 464:
				//라스타바드 2층 정령 소환실
				case 465:
				//라스타바드 2층 정령 서식지
				case 466:
				//라스타바드 2층 암흑정령 연구실
				case 467:
				//라스타바드 2층 암흑의 결계
				case 468:
				//라스타바드 2층 암흑의 결계
				case 470:
				//라스타바드 3층 악령제단
				case 471:
				// 라스타바드 3층 데빌로드 제단
				case 472:
				// 라스타바드 3층 용병 훈련장
				case 473:
				// 라스타바드 3층 명법군의 훈련장
				case 474:
				// 라스타바드 3층 오움 실험실
				case 475:
				// 라스타바드 3층 명법군왕의 집무실
				case 476:
				// 라스타바드 3층 중앙 통제실
				case 477:
				// 라스타바드 3층 데빌로드 용병실
				case 478:
				// 라스타바드 3층 통제구역
				case 490:
				// 라스타바드 지하 훈련장
				case 491:
				// 라스타바드 지하 통로
				case 492:
				// 라스타바드 암살군왕의 집무실
				case 493:
				// 라스타바드 지하 통제실
				case 494:
				// 라스타바드 지하 처형장
				case 495:
				// 라스타바드 지하 결투장
				case 496:
				// 라스타바드 지하 감옥
				case 480:
				// 해적섬 (후반부)
				case 481:
				// 카밀라의 방
				case 482:
				// 프랑코의 미로
				case 483:
				// 디에고의 폐쇄감옥
				case 484:
				// 호세의 지하감옥
				case 521:
				// 그림자 신전 외곽
				case 522:
				// 그림자 신전 1층
				case 523:
				// 그림자 신전 2층
				case 524:
				// 그림자 신전 3층
				case 535:
				// 다크엘프 성지
				case 536:
				// 3층 암흑의 결계
				case 550:
				// 선박의 무덤 수면
				case 551:
				// 선박의 무덤 선내
				case 552:
				// 선박의 무덤 선내
				case 553:
				// 선박의 무덤 선내
				case 554:
				// 선박의 무덤 선내
				case 555:
				// 선박의 무덤 선내
				case 556:
				// 선박의 무덤 선내
				case 557:
				// 선박의 무덤 선내
				case 558:
				// 선박의 무덤 심해
				case 600:
				// 욕망의 동굴 외곽
				case 601:
				// 욕망의 동굴 로비
				case 602:
				// 발록의 알현소
				case 603:
				// 발록의 아지트
				case 604:
				// 파도의 방
				case 605:
				// 화염의 방
				case 606:
				// 폭풍의 방
				case 607:
				// 지진의 방
				case 608:
				// 야히의 연구실
				case 610:
				// 벛꽃 마을
				case 613:
				// 신비한 깃털마을
				case 777:
				// 버림받은 자들의 땅
				case 778:
				// 버림받은 자들의 땅 (욕망)
				case 779:
				// 버림받은 자들의 땅(물속)
				case 666:
				// 지옥
				ChattingController.toChatting(cha, "현재 위치는 이동이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
		}
		//
		PcInstance use = World.findPc(cha.getClassSex() == 1 ? w.getGirlObjectId() : w.getManObjectId());
		if (use == null) {
			ChattingController.toChatting(cha, "당신의 파트너는 지금 게임을 하고 있지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else {
			if (!LocationController.isTeleportVerrYedHoraeZone(use, true)) {
				ChattingController.toChatting(cha, "현재 배우자 위치는 이동이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			Kingdom k = KingdomController.findKingdomLocation(use);
			if(k!=null && k.isWar()) {
				ChattingController.toChatting(cha, "현재 배우자 위치는 이동이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			if(useCount <= 0) {
				ChattingController.toChatting(cha, "이동가능 횟수를 모두 소진하였습니다", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			switch(use.getMap()) {
				    case 1:
					// 말하는섬던전 1층
					case 2:
					// 말하는섬던전 2층
					case 7:
					// 본토던전 1층
					case 8:
					// 본토던전 2층
					case 9:
					// 본토던전 3층
					case 10:
					// 본토던전 4층
					case 11:
					// 본토던전 5층
					case 12:
					// 본토던전 6층
					case 13:
					// 본토던전 7층
					case 14:
					// 지하통로
					case 15:
					// 켄트내성
					case 19:
					// 요정족 던전 1층
					case 20:
					// 요정족 던전 2층
					case 21:
					// 요정족 던전 3층
					case 23:
					// 윈다우드 던전 1층
					case 24:
					// 윈다우드 던전 2층
					case 25:
					// 수련던전 1층
					case 26:
					// 수련던전 2층
					case 27:
					// 수련던전 3층
					case 28:
					// 수련던전 4층
					case 29:
					// 윈성내성
					case 30:
					// 용의 던전 1층
					case 31:
					// 용의 던전 2층
					case 32:
					// 용의 던전 3층
					case 33:
					// 용의 던전 4층
					case 34:
					// 크레이의 방
					case 35:
					// 용의 던전 5층
					case 36:
					// 용의 던전 6층
					case 37:
					// 안타라스 둥지
					case 43:
					case 44:
					case 45:
					case 46:
					// 개미굴 1층
					case 47:
					case 48:
					case 49:
					case 50:
					// 개미굴 2층
					case 51:
					// 개미굴 3층
					case 53:
					// 기란감옥 1층
					case 54:
					// 기란감옥 2층
					case 55:
					// 기란감옥 3층
					case 56:
					// 기란감옥 4층
					case 59:
					// 에바의 왕국 1층
					case 60:
					// 에바의 왕국 2층
					case 61:
					// 에바의 왕국 3층
					case 62:
					// 에바의 신전
					case 63:
					// 에바의 왕국 4층
					case 64:
					// 하이네내성
					case 65:
					// 파푸리온 둥지
					case 66:
					// 지저성
					case 67:
					// 발라카스 둥지
					case 70:
					// 잊혀진 섬
					case 72:
					// 얼음여왕의 성 1층
					case 73:
					// 얼음여왕의 성 2층
					case 74:
					// 얼음여왕의 성 3층
					case 75:
					// 상아탑 1층
					case 76:
					// 상아탑 2층
					case 77:
					// 상아탑 3층
					case 78:
					// 상아탑 4층
					case 79:
					// 상아탑 5층
					case 80:
					// 상아탑 6층
					case 81:
					// 상아탑 7층
					case 82:
					// 상아탑 8층
					case 99:
					// 감옥
					case 101:
					//오만의 탑 01층
					case 102:
					//오만의 탑 02층
					case 103:
					//오만의 탑 03층
					case 104:
					// 오만의 탑 04층
					case 105:
					// 오만의 탑 05층
					case 106:
					// 오만의 탑 06층
					case 107:
					// 오만의 탑 07층
					case 108:
					// 오만의 탑 08층
					case 109:
					// 오만의 탑 09층
					case 110:
					// 오만의 탑 10층
					case 111:
					// 오만의 탑 11층
					case 112:
					// 오만의 탑 12층
					case 113:
					// 오만의 탑 13층
					case 114:
					// 오만의 탑 14층
					case 115:
					// 오만의 탑 15층
					case 116:
					// 오만의 탑 16층
					case 117:
					// 오만의 탑 17층
					case 118:
					// 오만의 탑 18층
					case 119:
					// 오만의 탑 19층
					case 120:
					// 오만의 탑 20층
					case 121:
					// 오만의 탑 21층
					case 122:
					// 오만의 탑 22층
					case 123:
					// 오만의 탑 23층
					case 124:
					// 오만의 탑 24층
					case 125:
					// 오만의 탑 25층
					case 126:
					// 오만의 탑 26층
					case 127:
					// 오만의 탑 27층
					case 128:
					// 오만의 탑 28층
					case 129:
					// 오만의 탑 29층
					case 130:
					// 오만의 탑 30층
					case 131:
					// 오만의 탑 31층
					case 132:
					// 오만의 탑 32층
					case 133:
					// 오만의 탑 33층
					case 134:
					// 오만의 탑 34층
					case 135:
					// 오만의 탑 35층
					case 136:
					// 오만의 탑 36층
					case 137:
					// 오만의 탑 37층
					case 138:
					// 오만의 탑 38층
					case 139:
					// 오만의 탑 39층
					case 140:
					// 오만의 탑 40층
					case 141:
					// 오만의 탑 41층
					case 142:
					// 오만의 탑 42층
					case 143:
					// 오만의 탑 43층
					case 144:
					// 오만의 탑 44층
					case 145:
					// 오만의 탑 45층
					case 146:
					// 오만의 탑 46층
					case 147:
					// 오만의 탑 47층
					case 148:
					// 오만의 탑 48층
					case 149:
					// 오만의 탑 49층
					case 150:
					// 오만의 탑 만50층
					case 151:
					// 오만의 탑 51층
					case 152:
					// 오만의 탑 52층
					case 153:
					// 오만의 탑 53층
					case 154:
					// 오만의 탑 54층
					case 155:
					// 오만의 탑 55층
					case 156:
					// 오만의 탑 56층
					case 157:
					// 오만의 탑 57층
					case 158:
					// 오만의 탑 58층
					case 159:
					// 오만의 탑 59층
					case 160:
					// 오만의 탑 60층
					case 161:
					// 오만의 탑 61층
					case 162:
					// 오만의 탑 62층
					case 163:
					// 오만의 탑 63층
					case 164:
					// 오만의 탑 64층
					case 165:
					// 오만의 탑 65층
					case 166:
					// 오만의 탑 66층
					case 167:
					// 오만의 탑 67층
					case 168:
					// 오만의 탑 68층
					case 169:
					// 오만의 탑 69층
					case 170:
					// 오만의 탑 70층
					case 171:
					// 오만의 탑 71층
					case 172:
					// 오만의 탑 72층
					case 173:
					// 오만의 탑 
					case 174:
					// 오만의 탑 74층
					case 175:
					// 오만의 탑 75층
					case 176:
					// 오만의 탑 76층
					case 177:
					// 오만의 탑 77층
					case 178:
					// 오만의 탑 78층
					case 179:
					// 오만의 탑 79층
					case 180:
					// 오만의 탑 80층
					case 181:
					// 오만의 탑 81층
					case 182:
					// 오만의 탑 82층
					case 183:
					// 오만의 탑 83층
					case 184:
					// 오만의 탑 84층
					case 185:
					// 오만의 탑 85층
					case 186:
					// 오만의 탑 86층
					case 187:
					// 오만의 탑 87층
					case 188:
					// 오만의 탑 88층
					case 189:
					// 오만의 탑 89층
					case 190:
					// 오만의 탑 90층
					case 191:
					// 오만의 탑 91층
					case 192:
					// 오만의 탑 92층
					case 193:
					// 오만의 탑 93층
					case 194:
					// 오만의 탑 94층
					case 195:
					// 오만의 탑 95층
					case 196:
					// 오만의 탑 96층
					case 197:
					// 오만의 탑 97층
					case 198:
					// 오만의 탑 98층
					case 199:
					// 오만의 탑 99층
					case 200:
					// 오만의 탑 100층
					case 509:
					// 결투장
					case 240:
					// 켄트성던전1층
					case 241:
					// 켄트성던전2층
					case 242:
					// 켄트성던전3층
					case 243:
					// 켄트성던전4층
					case 244:
					// 오염된 축복의 땅
					case 248:
					// 기란성 던전1층
					case 249:
					// 기란성 던전 2층
					case 250:
					// 기란성 던전 3층
					case 251:
					// 기란성 던전 4층
					case 252:
					// 하이네성 던전 1층
					case 253:
					// 하이네성 던전 2층
					case 254:
					// 하이네성 던전 3층
					case 255:
					// 지저성 던전 1층
					case 256:
					// 지저성 던전 2층
					case 257:
					// 아덴성 던전 1층
					case 258:
					// 아덴성 던전 2층
					case 259:
					// 아덴성 던전 3층
					case 300:
					// 아덴내성
					case 301:
					// 오만의 탑 지하 수로
					case 303:
					// 몽환의섬
					case 304:
					// 침묵의 동굴
					case 307:
					// 지하 침공로 1층
					case 308:
					// 지하 침공로 2층
					case 309:
					// 지하 침공로 3층
					case 310:
					// 오움 던전
					case 320:
					// 디아드 요새
					case 330:
					// 광물 동굴
					case 400:
					// 대공동 저항군지역
					case 401:
					// 대공동 은둔자지역
					case 410:
					// 마족신전
					case 420:
					// 지저호수
					case 430:
					// 정령의 무덤
					case 440:
					// 해적섬 (전반부)
					case 441:
					// 해적섬 던전 1층
					case 442:
					// 해적섬 던전 1층
					case 443:
					// 해적섬 던전 1층
					case 450:
					//라스타바드 정문
					case 451:
					//라스타바드 1층 집회장
					case 452:
					//라스타바드 1층 돌격대 훈련장
					case 453:
					//라스타바드 1층 마수군왕의 집무실
					case 454:
					//라스타바드 1층 야수 조련실
					case 455:
					//라스타바드 1층 야수 훈련장
					case 456:
					//라스타바드 1층 마수 소환실
					case 457:
					//라스타바드 1층 어둠의 결계
					case 460:
					//라스타바드 2층 흑마법 수련장
					case 461:
					//라스타바드 2층 흑마법 연구실
					case 462:
					//라스타바드 2층 마령군왕의 집무실
					case 463:
					//라스타바드 2층 마령군왕의 서재
					case 464:
					//라스타바드 2층 정령 소환실
					case 465:
					//라스타바드 2층 정령 서식지
					case 466:
					//라스타바드 2층 암흑정령 연구실
					case 467:
					//라스타바드 2층 암흑의 결계
					case 468:
					//라스타바드 2층 암흑의 결계
					case 470:
					//라스타바드 3층 악령제단
					case 471:
					// 라스타바드 3층 데빌로드 제단
					case 472:
					// 라스타바드 3층 용병 훈련장
					case 473:
					// 라스타바드 3층 명법군의 훈련장
					case 474:
					// 라스타바드 3층 오움 실험실
					case 475:
					// 라스타바드 3층 명법군왕의 집무실
					case 476:
					// 라스타바드 3층 중앙 통제실
					case 477:
					// 라스타바드 3층 데빌로드 용병실
					case 478:
					// 라스타바드 3층 통제구역
					case 490:
					// 라스타바드 지하 훈련장
					case 491:
					// 라스타바드 지하 통로
					case 492:
					// 라스타바드 암살군왕의 집무실
					case 493:
					// 라스타바드 지하 통제실
					case 494:
					// 라스타바드 지하 처형장
					case 495:
					// 라스타바드 지하 결투장
					case 496:
					// 라스타바드 지하 감옥
					case 480:
					// 해적섬 (후반부)
					case 481:
					// 카밀라의 방
					case 482:
					// 프랑코의 미로
					case 483:
					// 디에고의 폐쇄감옥
					case 484:
					// 호세의 지하감옥
					case 521:
					// 그림자 신전 외곽
					case 522:
					// 그림자 신전 1층
					case 523:
					// 그림자 신전 2층
					case 524:
					// 그림자 신전 3층
					case 535:
					// 다크엘프 성지
					case 536:
					// 3층 암흑의 결계
					case 550:
					// 선박의 무덤 수면
					case 551:
					// 선박의 무덤 선내
					case 552:
					// 선박의 무덤 선내
					case 553:
					// 선박의 무덤 선내
					case 554:
					// 선박의 무덤 선내
					case 555:
					// 선박의 무덤 선내
					case 556:
					// 선박의 무덤 선내
					case 557:
					// 선박의 무덤 선내
					case 558:
					// 선박의 무덤 심해
					case 600:
					// 욕망의 동굴 외곽
					case 601:
					// 욕망의 동굴 로비
					case 602:
					// 발록의 알현소
					case 603:
					// 발록의 아지트
					case 604:
					// 파도의 방
					case 605:
					// 화염의 방
					case 606:
					// 폭풍의 방
					case 607:
					// 지진의 방
					case 608:
					// 야히의 연구실
					case 610:
					// 벛꽃 마을
					case 613:
					// 신비한 깃털마을
					case 777:
					// 버림받은 자들의 땅
					case 778:
					// 버림받은 자들의 땅 (욕망)
				    case 779:
					// 버림받은 자들의 땅(물속)
					case 666:
					// 지옥
					ChattingController.toChatting(cha, "현재 배우자 위치는 이동이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
			}
			useCount--;
			//
			cha.getInventory().isAden(300000, true);
			cha.toPotal(use.getX(), use.getY(), use.getMap());
			
		}
	}

}
