package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ServerBasePacket;

public class S_Character extends ServerBasePacket {

	static synchronized public BasePacket clone(
			BasePacket bp,
			int op,
			String name,
			String clan,
			int type,
			int sex,
			int lawful,
			int hp,
			int mp,
			int ac,
			int level,
			int _str,
			int _dex,
			int _con,
			int _wis,
			int _cha,
			int _int,
			String birthday	){
		if(bp == null)
			bp = new S_Character(op, name, clan, type, sex, lawful, hp, mp, ac, level, _str, _dex, _con, _wis, _cha, _int, birthday);
		else
			((S_Character)bp).clone(op, name, clan, type, sex, lawful, hp, mp, ac, level, _str, _dex, _con, _wis, _cha, _int, birthday);
		return bp;
	}
	
	public S_Character( 
			int op,
			String name,
			String clan,
			int type,
			int sex,
			int lawful,
			int hp,
			int mp,
			int ac,
			int level,
			int _str,
			int _dex,
			int _con,
			int _wis,
			int _cha,
			int _int,
			String birthday	){
		clone(op, name, clan, type, sex, lawful, hp, mp, ac, level, _str, _dex, _con, _wis, _cha, _int, birthday);
	}
	
	public void clone(
			int op,
			String name,
			String clan,
			int type,
			int sex,
			int lawful,
			int hp,
			int mp,
			int ac,
			int level,
			int _str,
			int _dex,
			int _con,
			int _wis,
			int _cha,
			int _int,
			String birthday	){
		clear();
		writeC(op);
		writeS(name);
		writeS(clan);
		writeC(type);
		writeC(sex);
		writeH(lawful);
		writeH(hp);
		writeH(mp);
		writeC(266-ac);
		writeC(level);
		writeC(_str);
		writeC(_dex);
		writeC(_con);
		writeC(_wis);
		writeC(_cha);
		writeC(_int);
		writeC(0);
		if(birthday != null)
			writeD(Integer.valueOf( birthday.replaceAll("-", "") ));
	}
	
}
