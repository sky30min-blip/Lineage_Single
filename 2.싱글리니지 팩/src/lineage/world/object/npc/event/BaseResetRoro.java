package lineage.world.object.npc.event;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.EventInstance;
import lineage.world.object.instance.PcInstance;

public class BaseResetRoro extends EventInstance {
	
	public BaseResetRoro(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "baseReset"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("ent")){
			/*// 촛불 체크
			ItemInstance item = cha.getInventory().getItemIdType(808, 0);
			if(item==null){
				cha.SendPacket(new MessagePacket(1290), false);
			}else{
				// 촛불 제거
				// 촛불 제거를 get_count 값을 불러와 소지한 촛불을 전부 삭제하는 문제 수정
				// 20081207 by 아더
				cha.getInventory().Controler(item, Config.ITEM_REMOVE, 1);
				// 로로 임시저장
				cha.set_Roro(this);
				// 회상의땅으로 이동
				cha.Teleport(Config.rand(32721, 32814), Config.rand(32785, 32878), 5166);
			}*/
		}
	}
	
	/*
	*//**
	 * 스탯 초기화창 열기
	 * @param cha
	 *//*
	public void statOpen(PcInstance cha){
		// 스탯초기화 창 띄우기
		_list.put(cha.get_name(), new statTemp(cha));
		cha.SendPacket(new BaseResetPacket(1, _list.get(cha.get_name())), false);
	}
	
	*//**
	 * 스탯 재분배 처리 부분
	 * @param cha
	 * @param Str
	 * @param Int
	 * @param Wis
	 * @param Dex
	 * @param Con
	 * @param Cha
	 *//*
	public void Stat(PcInstance cha, int Str, int Int, int Wis, int Dex, int Con, int Cha){
		statTemp stat = _list.get(cha.get_name());
		stat.Str = Str;
		stat.Int = Int;
		stat.Wis = Wis;
		stat.Dex = Dex;
		stat.Con = Con;
		stat.Cha = Cha;
		if((Str+Int+Wis+Dex+Con+Cha)==75){
			cha.SendPacket(new BaseResetPacket(2, stat), false);
		}else{
			Logger.getInstance().badPalyer(cha.get_name(), "  : 스탯초기화시스템. 스탯재분배요청값이 기본값(75) 보다 큼!!!");
			statEnd(cha, null);
		}
	}
	
	*//**
	 * 레벨업 이벤트 발생시 처리 부분
	 * @param cha
	 * @param type
	 *//*
	public void LevelUp(PcInstance cha, int type1){
		statTemp stat = _list.get(cha.get_name());
		
		if(stat.Lev>=stat.LevMax){
			statEnd(cha, stat);
		}else{
			switch(type1){
				case 1:	// str
					++stat.LvStr;
					break;
				case 2:	// int
					++stat.LvInt;
					break;
				case 3:	// wis
					++stat.LvWis;
					break;
				case 4:	// dex
					++stat.LvDex;
					break;
				case 5:	// con
					++stat.LvCon;
					break;
				case 6:	// cha
					++stat.LvCha;
					break;
			}
			for(int i=type1==7?0:9 ; i<10 ; i++){
				stat.Lev += 1;
				stat.Hp += StatusUP(stat, true);
				stat.Mp += StatusUP(stat, false);
				if(stat.Lev>=stat.LevMax) {
					break;
				}
			}
			if(stat.Dex < 11) {
				stat.Ac = 266-stat.Lev/8;
			} else if(stat.Dex < 13) {
				stat.Ac = 266-stat.Lev/7;
			} else if(stat.Dex < 16) {
				stat.Ac = 266-stat.Lev/6;
			} else if(stat.Dex < 18) {
				stat.Ac = 266-stat.Lev/5;
			} else {
				stat.Ac = 266-stat.Lev/4;
			}
			cha.SendPacket(new BaseResetPacket(2, stat), false);
		}
	}
	
	*//**
	 * 종료할때 호출.
	 * @param cha
	 *//*
	public void statEnd(PcInstance cha, statTemp stat){
		// 스탯창 닫기
		cha.set_Roro(null);
		cha.SendPacket(new BaseResetPacket(), false);
		if(stat!=null){
			// 스탯 저장하기
			cha.set_maxHp(stat.Hp);
			cha.set_currentHp(stat.Hp);
			cha.set_maxMp(stat.Mp);
			cha.set_currentMp(stat.Mp);
			cha.set_str(stat.Str);
			cha.set_con(stat.Con);
			cha.set_dex(stat.Dex);
			cha.set_int(stat.Int);
			cha.set_cha(stat.Cha);
			cha.set_wis(stat.Wis);
			if(stat.LevMax>50){
				int totalStat = stat.LvStr+stat.LvDex+stat.LvCon+stat.LvInt+stat.LvWis+stat.LvCha;
				int lvStat = stat.LevMax-50;
				if(totalStat+1 == lvStat){
					cha.setLvStr(stat.LvStr);
					cha.setLvDex(stat.LvDex);
					cha.setLvCon(stat.LvCon);
					cha.setLvWis(stat.LvWis);
					cha.setLvInt(stat.LvInt);
					cha.setLvCha(stat.LvCha);
				}else{
					Logger.getInstance().badPalyer(cha.get_name(), "  : 스탯초기화시스템. 51이상레벨의스탯포인트 값이 레벨의비해 큼!!");
				}
			}
			cha.acDex();
			// 스탯 변경된 사항 업데이트 하기
			cha.SendPacket(new CharStatPacket(cha), false);
		}
		// 글루딘으로 텔레포트
		cha.Teleport(32628, 32798, 4);
		// 아이템 장착된것들 해제
		for(int i=0 ; i<13 ; ++i){
			ItemInstance slot = cha.getInventory().get_slot(i);
			if(slot!=null && slot.is_equipped()){
				if(slot instanceof ItemWeaponInstance){
					ItemWeaponInstance weapon = (ItemWeaponInstance)slot;
					weapon.weaponEquipped(cha);
					// 장착된 무기가 강제로 해제되었습니다.
					cha.SendPacket(new MessagePacket(1027), false);
				}else{
					ItemArmorInstance armor = (ItemArmorInstance)slot;
					armor.armorItemEn(cha);
				}
			}
		}
		_list.remove(cha.get_name());
	}
	
	*//**
	 * hp 와 mp 적절하게 잡아서 리턴.
	 * @param stat
	 * @param HpMp
	 * @return
	 *//*
	public int StatusUP(statTemp stat, boolean HpMp){
		int con = stat.Con;
		int wis = stat.Wis;
		int start_hp = 0;
		int start_mp = 0;
		int temp = 0;
		int HPMP = 0;

		if(HpMp){	// hp
			switch(stat.Class){
				case 0:	// 군주
				case 2:	// 요정
				case 4:	// 다크엘프
					temp = Config.rand(1, 32);
					if(con <= 15) {
						start_hp = 5;
					} else {
						start_hp = con - 10;
					}

					if(temp == 1) {
						start_hp = start_hp;
					} else if(temp <= 6) {
						start_hp += 1;
					} else if(temp <= 16) {
						start_hp += 2;
					} else if(temp <= 26) {
						start_hp += 3;
					} else if(temp <= 31) {
						start_hp += 4;
					} else {
						start_hp += 5;
					}

					HPMP = start_hp;
					break;
				case 1:	// 기사
					temp = Config.rand(1, 64);
					if(con <= 15) {
						start_hp = 6;
					} else {
						start_hp = con - 9;
					}

					if(temp == 1) {
						start_hp = start_hp;
					} else if(temp <= 7) {
						start_hp += 1;
					} else if(temp <= 22) {
						start_hp += 2;
					} else if(temp <= 42) {
						start_hp += 3;
					} else if(temp <= 57) {
						start_hp += 4;
					} else if(temp <= 63) {
						start_hp += 5;
					} else {
						start_hp += 6;
					}

					HPMP = start_hp;
					break;
				case 3:	// 법사
					temp = Config.rand(1, 8);
					if(con <= 15) {
						start_hp = 3;
					} else {
						start_hp = con - 12;
					}

					if(temp == 1) {
						start_hp = start_hp;
					} else if(temp <= 4) {
						start_hp += 1;
					} else if(temp <= 7) {
						start_hp += 2;
					} else {
						start_hp += 3;
					}

					HPMP = start_hp;
					break;
			}
		}else{	// mp
			switch(stat.Class){
				case 0:	// 군주
					if(wis <= 11) {
						start_mp = Config.rand(2, 3);
					} else if(wis >= 12 && wis <= 14){
						temp = Config.rand(1, 4);
						if(temp == 1) {
							start_mp = 2;
						} else if(temp <= 3) {
							start_mp = 3;
						} else {
							start_mp = 4;
						}
					}else if(wis >= 15 && wis <= 17){
						temp = Config.rand(1, 4);
						if(temp == 1) {
							start_mp = 3;
						} else if(temp <= 3) {
							start_mp = 4;
						} else {
							start_mp = 5;
						}
					}else if(wis >= 18 && wis <= 20){
						temp = Config.rand(1, 6);
						if(temp == 1) {
							start_mp = 3;
						} else if(temp <= 3) {
							start_mp = 4;
						} else if(temp <= 5) {
							start_mp = 5;
						} else {
							start_mp = 6;
						}
					}else if(wis >= 21 && wis <= 23){
						temp = Config.rand(1, 10);
						if(temp == 1) {
							start_mp = 3;
						} else if(temp <= 3) {
							start_mp = 4;
						} else if(temp <= 7) {
							start_mp = 5;
						} else if(temp <= 9) {
							start_mp = 6;
						} else {
							start_mp = 7;
						}
					}else if(wis >= 24 && wis <= 26){
						temp = Config.rand(1, 14);
						if(temp == 1) {
							start_mp = 3;
						} else if(temp <= 3) {
							start_mp = 4;
						} else if(temp <= 7) {
							start_mp = 5;
						} else if(temp <= 11) {
							start_mp = 6;
						} else if(temp <= 13) {
							start_mp = 7;
						} else {
							start_mp = 8;
						}
					}else{// if(wis >= 27 && wis <= 29){
						temp = Config.rand(1, 22);
						if(temp == 1) {
							start_mp = 3;
						} else if(temp <= 3) {
							start_mp = 4;
						} else if(temp <= 7) {
							start_mp = 5;
						} else if(temp <= 15) {
							start_mp = 6;
						} else if(temp <= 19) {
							start_mp = 7;
						} else if(temp <= 21) {
							start_mp = 8;
						} else {
							start_mp = 9;
						}
					}

					HPMP = start_mp;
					break;
				case 1:	// 기사
					if(wis <= 9){
						temp = Config.rand(1, 4);
						if(temp == 1) {
							start_mp = 0;
						} else if(temp <= 3) {
							start_mp = 1;
						} else {
							start_mp = 2;
						}
					}else{
						temp = Config.rand(1, 4);
						if(temp == 1) {
							start_mp = 1;
						} else if(temp <= 3) {
							start_mp = 2;
						} else {
							start_mp = 3;
						}
					}

					HPMP = start_mp;
					break;
				case 2:	// 요정
				case 4:	// 다크엘프
					if(wis <= 14){
						temp = Config.rand(1, 6);
						if(temp == 1) {
							start_mp = 3;
						} else if(temp <= 3) {
							start_mp = 4;
						} else if(temp <= 5) {
							start_mp = 5;
						} else {
							start_mp = 6;
						}
					}else if(wis >= 15 && wis <= 17){
						temp = Config.rand(1, 6);
						if(temp == 1) {
							start_mp = 4;
						} else if(temp <= 3) {
							start_mp = 5;
						} else if(temp <= 5) {
							start_mp = 6;
						} else {
							start_mp = 7;
						}
					}else if(wis >= 18 && wis <= 20){
						temp = Config.rand(1, 14);
						if(temp == 1) {
							start_mp = 4;
						} else if(temp <= 3) {
							start_mp = 5;
						} else if(temp <= 7) {
							start_mp = 6;
						} else if(temp <= 11) {
							start_mp = 7;
						} else if(temp <= 13) {
							start_mp = 8;
						} else {
							start_mp = 9;
						}
					}else if(wis >= 21 && wis <= 23){
						temp = Config.rand(1, 30);
						if(temp == 1) {
							start_mp = 4;
						} else if(temp <= 3) {
							start_mp = 5;
						} else if(temp <= 7) {
							start_mp = 6;
						} else if(temp <= 15) {
							start_mp = 7;
						} else if(temp <= 23) {
							start_mp = 8;
						} else if(temp <= 27) {
							start_mp = 9;
						} else if(temp <= 29) {
							start_mp = 10;
						} else {
							start_mp = 11;
						}
					}else if(wis >= 24 && wis <= 26){
						temp = Config.rand(1, 62);
						if(temp == 1) {
							start_mp = 4;
						} else if(temp <= 3) {
							start_mp = 5;
						} else if(temp <= 7) {
							start_mp = 6;
						} else if(temp <= 15) {
							start_mp = 7;
						} else if(temp <= 31) {
							start_mp = 8;
						} else if(temp <= 47) {
							start_mp = 9;
						} else if(temp <= 55) {
							start_mp = 10;
						} else if(temp <= 59) {
							start_mp = 11;
						} else if(temp <= 61) {
							start_mp = 12;
						} else {
							start_mp = 13;
						}
					}else{	// else if(wis >= 27 && wis <= 29)
						temp = Config.rand(1, 126);
						if(temp == 1) {
							start_mp = 4;
						} else if(temp <= 3) {
							start_mp = 5;
						} else if(temp <= 7) {
							start_mp = 6;
						} else if(temp <= 15) {
							start_mp = 7;
						} else if(temp <= 31) {
							start_mp = 8;
						} else if(temp <= 63) {
							start_mp = 9;
						} else if(temp <= 95) {
							start_mp = 10;
						} else if(temp <= 111) {
							start_mp = 11;
						} else if(temp <= 119) {
							start_mp = 12;
						} else if(temp <= 123) {
							start_mp = 13;
						} else if(temp <= 125) {
							start_mp = 14;
						} else {
							start_mp = 15;
						}
					}

					HPMP = start_mp;
					break;
				case 3:	// 법사
					if(wis <= 14){
						temp = Config.rand(1, 10);
						if(temp == 1) {
							start_mp = 4;
						} else if(temp <= 3) {
							start_mp = 5;
						} else if(temp <= 7) {
							start_mp = 6;
						} else if(temp <= 9) {
							start_mp = 7;
						} else {
							start_mp = 8;
						}
					}else if(wis >= 15 && wis <= 17){
						temp = Config.rand(1, 10);
						if(temp == 1) {
							start_mp = 6;
						} else if(temp <= 3) {
							start_mp = 7;
						} else if(temp <= 7) {
							start_mp = 8;
						} else if(temp <= 9) {
							start_mp = 9;
						} else {
							start_mp = 10;
						}
					}else if(wis >= 18 && wis <= 20){
						temp = Config.rand(1, 22);
						if(temp == 1) {
							start_mp = 6;
						} else if(temp <= 3) {
							start_mp = 7;
						} else if(temp <= 7) {
							start_mp = 8;
						} else if(temp <= 15) {
							start_mp = 9;
						} else if(temp <= 19) {
							start_mp = 10;
						} else if(temp <= 21) {
							start_mp = 11;
						} else {
							start_mp = 12;
						}
					}else if(wis >= 21 && wis <= 23){
						temp = Config.rand(1, 46);
						if(temp == 1) {
							start_mp = 6;
						} else if(temp <= 3) {
							start_mp = 7;
						} else if(temp <= 7) {
							start_mp = 8;
						} else if(temp <= 15) {
							start_mp = 9;
						} else if(temp <= 31) {
							start_mp = 10;
						} else if(temp <= 39) {
							start_mp = 11;
						} else if(temp <= 43) {
							start_mp = 12;
						} else if(temp <= 45) {
							start_mp = 13;
						} else {
							start_mp = 14;
						}
					}else if(wis >= 24 && wis <= 26){
						temp = Config.rand(1, 94);
						if(temp == 1) {
							start_mp = 6;
						} else if(temp <= 3) {
							start_mp = 7;
						} else if(temp <= 7) {
							start_mp = 8;
						} else if(temp <= 15) {
							start_mp = 9;
						} else if(temp <= 31) {
							start_mp = 10;
						} else if(temp <= 63) {
							start_mp = 11;
						} else if(temp <= 79) {
							start_mp = 12;
						} else if(temp <= 87) {
							start_mp = 13;
						} else if(temp <= 91) {
							start_mp = 14;
						} else if(temp <= 93) {
							start_mp = 15;
						} else {
							start_mp = 16;
						}
					}else{ // else if(wis >= 27 && wis <= 29)
						temp = Config.rand(1, 190);
						if(temp == 1) {
							start_mp = 6;
						} else if(temp <= 3) {
							start_mp = 7;
						} else if(temp <= 7) {
							start_mp = 8;
						} else if(temp <= 15) {
							start_mp = 9;
						} else if(temp <= 31) {
							start_mp = 10;
						} else if(temp <= 63) {
							start_mp = 11;
						} else if(temp <= 127) {
							start_mp = 12;
						} else if(temp <= 159) {
							start_mp = 13;
						} else if(temp <= 175) {
							start_mp = 14;
						} else if(temp <= 183) {
							start_mp = 15;
						} else if(temp <= 187) {
							start_mp = 16;
						} else if(temp <= 189) {
							start_mp = 17;
						} else {
							start_mp = 18;
						}
					}

					HPMP = start_mp;
					break;
			}
		}

		return HPMP * 2;
	}
	
	*//**
	 * 스탯 임시 저장용 클레스
	 * @author Administrator
	 *//*
	public class statTemp{
		public statTemp(PcInstance cha){
			Class = cha.get_classType();
			Chartemplate charDB = CharTemplate.getInstance().getTemplate(Class);
			Lev = 1;
			LevMax = cha.get_level();
			Hp = charDB.get_Hp();
			Mp = charDB.get_Mp();
			Ac = 266;
		}
		
		public int Str;
		public int Int;
		public int Wis;
		public int Dex;
		public int Con;
		public int Cha;
		public int LvStr;
		public int LvInt;
		public int LvWis;
		public int LvDex;
		public int LvCon;
		public int LvCha;
		public int Hp;
		public int Mp;
		public int Lev;
		public int LevMax;
		public int Ac;
		public int Class;
	}*/
	
}
