package lineage.network.packet.client;

import java.sql.Connection;
import java.sql.PreparedStatement;

import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.LineageClient;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterDelete;
import lineage.network.packet.server.S_Notice;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.world.controller.FishingController;
import lineage.world.controller.PcMarketController;

public class C_CharacterDelete extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_CharacterDelete(data, length);
		else
			((C_CharacterDelete)bp).clone(data, length);
		return bp;
	}
	
	public C_CharacterDelete(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(LineageClient c){
		String name = readS();
		
		// 플러그인 확인.
		if(PluginController.init(C_CharacterDelete.class, "init", name) != null)
			return this;
		
		if(Lineage.character_delete == false) {
			c.toSender(S_Notice.clone(BasePacketPooling.getPool(S_Notice.class), "케릭터를 삭제할 수 없습니다."));
			return this;
		}
		
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			
			if(CharactersDatabase.isCharacter(c.getAccountUid(), name)){
				if(!CharactersDatabase.isInvalidName(con, name)){
					// 삭제하려는 케릭터에 생성시간값 추출.
					long key = CharactersDatabase.getCharacterRegisterDate(con, name);
					// 삭제하려는 객체 오브젝트 아이디 추출.
					int obj_id = CharactersDatabase.getCharacterObjectId(con, name);
					// 삭제하려는 객체 계정 uid 추출.
					int accountId = CharactersDatabase.getAccountUid(con, obj_id);
					// 로그 기록.
					if(Log.isLog(null))
						Log.appendConnect(key, c.getAccountIp(), c.getAccountId(), name, "삭제");
					// 디비 제거.
					PreparedStatement st = null;
					try {
						// 케릭터 제거
						st = con.prepareStatement("DELETE FROM characters WHERE objID=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 기억 제거
						st = con.prepareStatement("DELETE FROM characters_book WHERE objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 버프 제거
						st = con.prepareStatement("DELETE FROM characters_buff WHERE objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 친구 제거
						st = con.prepareStatement("DELETE FROM characters_friend WHERE object_id=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 인벤토리 제거
						st = con.prepareStatement("DELETE FROM characters_inventory WHERE cha_objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 퀘스트 제거
						st = con.prepareStatement("DELETE FROM characters_quest WHERE objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 스킬 제거
						st = con.prepareStatement("DELETE FROM characters_skill WHERE cha_objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 스왑 제거
						st = con.prepareStatement("DELETE FROM characters_swap WHERE cha_objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 개인 상점 제거
						PcMarketController.removeCharacter(con, obj_id);
						// 자동 낚시 제거
						FishingController.removeCharacter(con, obj_id, accountId);
					} catch (Exception e) {
						lineage.share.System.printf("%s : init(Client c) 2\r\n", C_CharacterDelete.class.toString());
						lineage.share.System.println(e);
					} finally {
						DatabaseConnection.close(st);
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Client c) 1\r\n", C_CharacterDelete.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
		
		c.toSender( S_CharacterDelete.clone(BasePacketPooling.getPool(S_CharacterDelete.class)) );
		return this;
	}
}
