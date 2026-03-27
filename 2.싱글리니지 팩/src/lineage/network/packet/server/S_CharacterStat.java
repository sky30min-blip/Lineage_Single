package lineage.network.packet.server;

import lineage.bean.database.Exp;
import lineage.database.ExpDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.Character;

public class S_CharacterStat extends ServerBasePacket {
	
	static public BasePacket clone(BasePacket bp, Character cha){
		if(bp == null)
			bp = new S_CharacterStat(cha);
		else
			((S_CharacterStat)bp).toClone(cha);
		return bp;
	}

//	static synchronized public BasePacket clone(BasePacket bp, Character cha){
//		if(bp == null)
//			bp = new S_CharacterStat(cha);
//		else
//			((S_CharacterStat)bp).toClone(cha);
//		return bp;
//	}
	
	public S_CharacterStat(Character cha){
		toClone(cha);
	}
	
	public S_CharacterStat() {
		//
	}
	
	public void toClone(Character cha){
		clear();
		writeC(Opcodes.S_OPCODE_OWNCHARSTATUS);
		writeD(cha.getObjectId());
		writeC(cha.getLevel());
		
		if(Lineage.server_version<=144){
			int exp = 0;
			Exp e = ExpDatabase.find(cha.getLevel());
			if(e!=null && e.getBonus()-cha.getExp()>0){
				double a = e.getExp()-(e.getBonus()-cha.getExp());
				double b = e.getExp();
				exp = (int)((a/b)*30);
			}
			writeC(exp);	// 경험치게이지바 30이 100%
		}else{
			writeD((int)cha.getExp());
		}
		
		writeC(cha.getTotalStr());
		writeC(cha.getTotalInt());
		writeC(cha.getTotalWis());
		writeC(cha.getTotalDex());
		writeC(cha.getTotalCon());
		writeC(cha.getTotalCha());
		writeH(cha.getNowHp());
		writeH(cha.getTotalHp());
		writeH(cha.getNowMp());
		writeH(cha.getTotalMp());
		writeC(266-cha.getTotalAc() < 128 ? 128 : 266-cha.getTotalAc());
		writeD(ServerDatabase.LineageWorldTime);	// 시간
		if(Lineage.server_version>=270){
			writeC(cha.getFood());	// 배고품
		}else{
			int food = (int)(((double)cha.getFood()/(double)Lineage.MAX_FOOD)*29);
			writeC(food);	// 30이 100%
		}
		writeC(cha.getInventory()==null ? 0 : (int)cha.getInventory().getWeightPercent());	// 무게
		writeH(cha.getLawful());
		// 속성 저항력
		if(Lineage.server_version>144){
			writeC(cha.getTotalFireress());
			writeC(cha.getTotalWaterress());
			writeC(cha.getTotalWindress());
			writeC(cha.getTotalEarthress());
		}
	}
	
}
