package lineage.network.packet.client;

import lineage.bean.lineage.StatDice;
import lineage.network.LineageClient;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_StatDice;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.util.Util;

public class C_StatDice extends ClientBasePacket {
	
	static private int[] addStat = {
		Lineage.royal_stat_dice,
		Lineage.knight_stat_dice,
		Lineage.elf_stat_dice,
		Lineage.wizard_stat_dice
	};
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_StatDice(data, length);
		else
			((C_StatDice)bp).clone(data, length);
		return bp;
	}
	
	public C_StatDice(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(LineageClient c){
		if(PluginController.init(C_StatDice.class, "init", this, c) != null)
			return this;
		
		StatDice sd = c.getStatDice();
		sd.setType(readC());
		
		switch(sd.getType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				sd.setStr(Lineage.royal_stat_str);
				sd.setCon(Lineage.royal_stat_con);
				sd.setDex(Lineage.royal_stat_dex);
				sd.setWis(Lineage.royal_stat_wis);
				sd.setCha(Lineage.royal_stat_cha);
				sd.setInt(Lineage.royal_stat_int);
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				sd.setStr(Lineage.knight_stat_str);
				sd.setCon(Lineage.knight_stat_con);
				sd.setDex(Lineage.knight_stat_dex);
				sd.setWis(Lineage.knight_stat_wis);
				sd.setCha(Lineage.knight_stat_cha);
				sd.setInt(Lineage.knight_stat_int);
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				sd.setStr(Lineage.elf_stat_str);
				sd.setCon(Lineage.elf_stat_con);
				sd.setDex(Lineage.elf_stat_dex);
				sd.setWis(Lineage.elf_stat_wis);
				sd.setCha(Lineage.elf_stat_cha);
				sd.setInt(Lineage.elf_stat_int);
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				sd.setStr(Lineage.wizard_stat_str);
				sd.setCon(Lineage.wizard_stat_con);
				sd.setDex(Lineage.wizard_stat_dex);
				sd.setWis(Lineage.wizard_stat_wis);
				sd.setCha(Lineage.wizard_stat_cha);
				sd.setInt(Lineage.wizard_stat_int);
				break;
		}
		
		for(int i=addStat[sd.getType()] ; i>0 ; --i){
			switch(Util.random(0, 5)){
				case 0:
					sd.setStr(sd.getStr()+1);
					break;
				case 1:
					sd.setDex(sd.getDex()+1);
					if(sd.getDex()>18){
						++i;
						sd.setDex(18);
					}
					break;
				case 2:
					sd.setCon(sd.getCon()+1);
					if(sd.getCon()>18){
						++i;
						sd.setCon(18);
					}
					break;
				case 3:
					sd.setWis(sd.getWis()+1);
					if(sd.getWis()>18){
						++i;
						sd.setWis(18);
					}
					break;
				case 4:
					sd.setInt(sd.getInt()+1);
					if(sd.getInt()>18){
						++i;
						sd.setInt(18);
					}
					break;
				case 5:
					sd.setCha(sd.getCha()+1);
					if(sd.getCha()>18){
						++i;
						sd.setCha(18);
					}
					break;
			}
		}
		
		c.toSender( S_StatDice.clone(BasePacketPooling.getPool(S_StatDice.class), sd) );
		return this;
	}
}
