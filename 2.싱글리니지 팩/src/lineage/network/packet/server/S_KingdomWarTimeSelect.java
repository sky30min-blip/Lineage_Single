package lineage.network.packet.server;

import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.util.Util;

public class S_KingdomWarTimeSelect extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Kingdom k){
		if(bp == null)
			bp = new S_KingdomWarTimeSelect(k);
		else
			((S_KingdomWarTimeSelect)bp).toClone(k);
		return bp;
	}
	
	public S_KingdomWarTimeSelect(Kingdom k){
		toClone(k);
	}
	
	public void toClone(Kingdom k){
		clear();
		
		// 표현부분
		writeC(Opcodes.S_OPCODE_CASTLEWARTIME);
		writeH(6);
		writeS("KST");

		long time = 0;
		for(int i=0; i<6 ; ++i){
			writeC(i);
			time = k.getListWarday().get(i);
			writeD(toTime(Util.getYear(time), Util.getMonth(time), Util.getDate(time)+1, Util.getHours(time), 0));
		}
	}
	
	private int toTime(int year, int month, int date, int hour, int minute){
		// 360(1분)
		
		int t = 0;
		// 분 계산
		t += 360 * minute;
		// 시 계산
		t += 360 * 60 * (hour-17);
		// 월일 계산
		t += 360 * 60 * 24 * MonthDate(year, month, date);
		// 년 계산. 2000년으로 표현하기위해 107을뺌.. 원랜 97을 빼야됨.
		t += 360 * 60 * 24 * 365 * (year-107);
		return t;
	}
	
	private int MonthDate(int year, int month, int date){
		// 매월 작성된 일수.
		int [] arr = {
				31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
				};
		// 4년, 100년, 400년 주기로 2월은 29일임.
		// 그외에는 28일
		if ( (year%4==0) && !(year%100==0) || (year%400==0) ){ 
			arr[1] = 29; 
		}else{
			arr[1] = 28;
		}
		
		int m = 0;
		for(int i=0 ; i<month-1 ; ++i){
			m += arr[i];
		}
		return m + date - 1;
	}

}
