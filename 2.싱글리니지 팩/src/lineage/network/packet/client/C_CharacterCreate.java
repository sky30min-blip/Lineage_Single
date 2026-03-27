package lineage.network.packet.client;

import java.sql.Connection;

import lineage.bean.database.FirstSpawn;
import lineage.bean.lineage.StatDice;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ServerDatabase;
import lineage.network.LineageClient;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.server.S_Character;
import lineage.network.packet.server.S_LoginFail;
import lineage.network.packet.server.S_Notice;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.util.Util;
import lineage.world.controller.RobotController;

public class C_CharacterCreate extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_CharacterCreate(data, length);
		else
			((C_CharacterCreate)bp).clone(data, length);
		return bp;
	}
	
	public C_CharacterCreate(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(LineageClient c){
		String name = readS().replaceAll(" ", "").trim();
		// 클레스 종류
		int type = readC();
		// 성별
		int sex = readC();
		// 기본정보
		int Str;
		int Dex;
		int Con;
		int Wis;
		int Cha;
		int Int;
		int Hp;
		int Mp;
		int gfx;
		FirstSpawn fs = null;
		
		if(Lineage.server_version<170){
			StatDice sd = c.getStatDice();
			Str = sd.getStr();
			Dex = sd.getDex();
			Con = sd.getCon();
			Wis = sd.getWis();
			Cha = sd.getCha();
			Int = sd.getInt();
		}else{
			Str = readC();
			Dex = readC();
			Con = readC();
			Wis = readC();
			Cha = readC();
			Int = readC();
		}
		switch(type){
			case Lineage.LINEAGE_CLASS_ROYAL:
				Hp = Lineage.royal_hp;
				Mp = Lineage.royal_mp;
				gfx = sex==0 ? Lineage.royal_male_gfx : Lineage.royal_female_gfx;
				if(Lineage.royal_spawn.size()>0)
					fs = Lineage.royal_spawn.get(Util.random(0, Lineage.royal_spawn.size()-1));
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				Hp = Lineage.knight_hp;
				Mp = Lineage.knight_mp;
				gfx = sex==0 ? Lineage.knight_male_gfx : Lineage.knight_female_gfx;
				if(Lineage.knight_spawn.size()>0)
					fs = Lineage.knight_spawn.get(Util.random(0, Lineage.knight_spawn.size()-1));
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				Hp = Lineage.elf_hp;
				Mp = Lineage.elf_mp;
				gfx = sex==0 ? Lineage.elf_male_gfx : Lineage.elf_female_gfx;
				if(Lineage.elf_spawn.size()>0)
					fs = Lineage.elf_spawn.get(Util.random(0, Lineage.elf_spawn.size()-1));
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				Hp = Lineage.wizard_hp;
				Mp = Lineage.wizard_mp;
				gfx = sex==0 ? Lineage.wizard_male_gfx : Lineage.wizard_female_gfx;
				if(Lineage.wizard_spawn.size()>0)
					fs = Lineage.wizard_spawn.get(Util.random(0, Lineage.wizard_spawn.size()-1));
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				Hp = Lineage.darkelf_hp;
				Mp = Lineage.darkelf_mp;
				gfx = sex==0 ? Lineage.darkelf_male_gfx : Lineage.darkelf_female_gfx;
				if(Lineage.darkelf_spawn.size()>0)
					fs = Lineage.darkelf_spawn.get(Util.random(0, Lineage.darkelf_spawn.size()-1));
				break;
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
				Hp = Lineage.dragonknight_hp;
				Mp = Lineage.dragonknight_mp;
				gfx = sex==0 ? Lineage.dragonknight_male_gfx : Lineage.dragonknight_female_gfx;
				if(Lineage.dragonknight_spawn.size()>0)
					fs = Lineage.dragonknight_spawn.get(Util.random(0, Lineage.dragonknight_spawn.size()-1));
				break;
			default:
				Hp = Lineage.blackwizard_hp;
				Mp = Lineage.blackwizard_mp;
				gfx = sex==0 ? Lineage.blackwizard_male_gfx : Lineage.blackwizard_female_gfx;
				if(Lineage.blackwizard_spawn.size()>0)
					fs = Lineage.blackwizard_spawn.get(Util.random(0, Lineage.blackwizard_spawn.size()-1));
				break;
		}
		
		if (CharactersDatabase.isCharacterCount(c.getAccountIp())) {
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();
				
				if(!CharactersDatabase.isCharacterName(con, name) && !RobotController.isName(name)){
					if(!CharactersDatabase.isInvalidName(con, name)){
						if(Str+Dex+Con+Wis+Int+Cha==75){
							long obj_id = ServerDatabase.nextPcObjId();
							long time = System.currentTimeMillis();
							// 동일한 오브젝트아이디가 디비에 존재하는지 확인. 없을때까지 무한반복.
							while(CharactersDatabase.isCharacterObjectId(con, obj_id)){
								obj_id = ServerDatabase.nextPcObjId();
							}
							// 디비에 등록.
							CharactersDatabase.insertCharacter(con, obj_id, name, type, sex, Hp, Mp, Str, Dex, Con, Wis, Cha, Int, gfx, fs, c.getAccountId(), c.getAccountUid(), time);
							// 초기지급 아이템 등록.
							CharactersDatabase.insertInventory(con, obj_id, name, type);
							// 초기지급 스킬 등록.
							CharactersDatabase.insertSkill(con, obj_id, name, type);
							// 생성 성공 패킷 전송.
							c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.REASON_OK) );
							// 생성한 케릭패킷 전송.
							c.toSender( S_Character.clone(BasePacketPooling.getPool(S_Character.class), Opcodes.S_OPCODE_NEWCHARPACK, name, null, type, sex, 65536, Hp, Mp, 0, 1, Str, Dex, Con, Wis, Cha, Int, Util.getLocaleString(time, false)) );
							// 로그 기록.
							if(Log.isLog(null))
								Log.toConnect(c.getAccountIp(), c.getAccountId(), name, time);
							// 플러그인 알리기.
							PluginController.init(C_CharacterCreate.class, "init", obj_id, name);
						}else{
							// 스탯합이 잘못 됬을때.
							c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.REASON_WRONG_AMOUNT) );
						}
					}else{
						// 생성할 수 없는 이름이라면.
						c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.REASON_INVALID_NAME) );
					}
				}else{
					// 같은 이름이 이미 존재한다면.
					c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.REASON_ALREADY_EXSISTS) );
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : init(Client c)\r\n", C_CharacterCreate.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con);
			}
		} else {
			c.toSender(S_Notice.clone(BasePacketPooling.getPool(S_Notice.class), String.format("한아이피당 케릭터는 최대 %d개 생성가능합니다.", Lineage.ip_character_count)));
		}
		
		return this;
	}
}
