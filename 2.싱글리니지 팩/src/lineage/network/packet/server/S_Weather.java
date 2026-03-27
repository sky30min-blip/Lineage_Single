package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Weather extends ServerBasePacket {

	// 날씨종류
	static public final int WEATHER_FAIR		 = 0x00;	// 맑음
	static public final int WEATHER_SNOW_1		 = 0x01;	// 눈 조금
	static public final int WEATHER_SNOW_2		 = 0x02;	// 눈 많이
	static public final int WEATHER_SNOW_3		 = 0x03;	// 눈 펑펑
	static public final int WEATHER_RAIN_1		 = 0x11;	// 비 조금
	static public final int WEATHER_RAIN_2		 = 0x12;	// 비 많이
	static public final int WEATHER_RAIN_3		 = 0x13;	// 비 폭우
	
	static synchronized public BasePacket clone(BasePacket bp, int weather){
		if(bp == null)
			bp = new S_Weather(weather);
		else
			((S_Weather)bp).toClone(weather);
		return bp;
	}
	
	public S_Weather(int weather){
		toClone(weather);
	}
	
	public void toClone(int weather){
		clear();
		writeC(Opcodes.S_OPCODE_LINEAGEWEATHER);
		// 0 : 맑음
		// 1 : 눈 조금
		// 2 : 눈 많이
		// 3 : 눈 펑펑
		// 17 : 비 조금
		// 18 : 비 많이
		// 19 : 폭우
		writeH(weather);
	}
}
