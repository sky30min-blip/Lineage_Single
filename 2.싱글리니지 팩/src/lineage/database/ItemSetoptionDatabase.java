package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemSetoption;
import lineage.bean.lineage.Inventory;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.Wafer;
import lineage.world.object.magic.movingacceleratic;

public final class ItemSetoptionDatabase {

	static private List<ItemSetoption> list;

	static public void init(Connection con) {
		TimeLine.start("ItemSetopionDatabase..");

		list = new ArrayList<ItemSetoption>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item_setoption");
			rs = st.executeQuery();
			while (rs.next()) {
				ItemSetoption i = new ItemSetoption();
				i.setUid(rs.getInt("uid"));
				i.setName(rs.getString("name"));
				i.setCount(rs.getInt("count"));
				i.setAdd_dagmage(rs.getInt("add_damage"));
				i.setAdd_dagmage_bow(rs.getInt("add_damage_bow"));
				i.setAdd_reduction(rs.getInt("add_reduction"));
				i.setAdd_hp(rs.getInt("add_hp"));
				i.setAdd_mp(rs.getInt("add_mp"));
				i.setAdd_str(rs.getInt("add_str"));
				i.setAdd_dex(rs.getInt("add_dex"));
				i.setAdd_con(rs.getInt("add_con"));
				i.setAdd_int(rs.getInt("add_int"));
				i.setAdd_wis(rs.getInt("add_wis"));
				i.setAdd_cha(rs.getInt("add_cha"));
				i.setAdd_ac(rs.getInt("add_ac"));
				i.setAdd_mr(rs.getInt("add_mr"));
				i.setTic_hp(rs.getInt("tic_hp"));
				i.setTic_mp(rs.getInt("tic_mp"));
				i.setPolymorph(rs.getInt("polymorph"));
				i.setWindress(rs.getInt("windress"));
				i.setWateress(rs.getInt("wateress"));
				i.setFireress(rs.getInt("fireress"));
				i.setEarthress(rs.getInt("earthress"));
				i.setGm(rs.getInt("gm"));
				i.setHaste(rs.getString("haste").equalsIgnoreCase("true"));
				i.setBrave(rs.getString("brave").equalsIgnoreCase("true"));

				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemSetoptionDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public ItemSetoption find(int uid) {
		for (ItemSetoption is : list) {
			if (is.getUid() == uid)
				return is;
		}
		return null;
	}

	static public void setting(Character cha, ItemSetoption i, boolean equipped, boolean sendPacket) {
		Inventory inv = cha.getInventory();
		if (inv != null) {
			// 적용
			if (equipped) {
				cha.setDynamicHp(cha.getDynamicHp() + i.getAdd_hp());
				cha.setDynamicMp(cha.getDynamicMp() + i.getAdd_mp());
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + i.getAdd_dagmage());
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + i.getAdd_dagmage_bow());
				cha.setDynamicReduction(cha.getDynamicReduction() + i.getAdd_reduction());
				cha.setDynamicStr(cha.getDynamicStr() + i.getAdd_str());
				cha.setDynamicDex(cha.getDynamicDex() + i.getAdd_dex());
				cha.setDynamicCon(cha.getDynamicCon() + i.getAdd_con());
				cha.setDynamicInt(cha.getDynamicInt() + i.getAdd_int());
				cha.setDynamicWis(cha.getDynamicWis() + i.getAdd_wis());
				cha.setDynamicCha(cha.getDynamicCha() + i.getAdd_cha());
				cha.setDynamicAc(cha.getDynamicAc() + i.getAdd_ac());
				cha.setDynamicMr(cha.getDynamicMr() + i.getAdd_mr());
				cha.setDynamicTicHp(cha.getDynamicTicHp() + i.getTic_hp());
				cha.setDynamicTicMp(cha.getDynamicTicMp() + i.getTic_mp());
				cha.setGm(cha.getGm() + i.getGm());
			} else {
				cha.setDynamicHp(cha.getDynamicHp() - i.getAdd_hp());
				cha.setDynamicMp(cha.getDynamicMp() - i.getAdd_mp());
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - i.getAdd_dagmage());
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - i.getAdd_dagmage_bow());
				cha.setDynamicReduction(cha.getDynamicReduction() - i.getAdd_reduction());
				cha.setDynamicStr(cha.getDynamicStr() - i.getAdd_str());
				cha.setDynamicDex(cha.getDynamicDex() - i.getAdd_dex());
				cha.setDynamicCon(cha.getDynamicCon() - i.getAdd_con());
				cha.setDynamicInt(cha.getDynamicInt() - i.getAdd_int());
				cha.setDynamicWis(cha.getDynamicWis() - i.getAdd_wis());
				cha.setDynamicCha(cha.getDynamicCha() - i.getAdd_cha());
				cha.setDynamicAc(cha.getDynamicAc() - i.getAdd_ac());
				cha.setDynamicMr(cha.getDynamicMr() - i.getAdd_mr());
				cha.setDynamicTicHp(cha.getDynamicTicHp() - i.getTic_hp());
				cha.setDynamicTicMp(cha.getDynamicTicMp() - i.getTic_mp());
				cha.setGm(cha.getGm() - i.getGm() < 0 ? 0 : cha.getGm() - i.getGm());
			}
			
			if (cha.getMap() != Lineage.teamBattleMap) {
				if (equipped && i.getPolymorph() > 0) {
					ShapeChange.onBuff(cha, cha, PolyDatabase.getPolyGfx(i.getPolymorph()), -1, sendPacket);
					cha.isSetPoly = true;
				} else if (!equipped && i.getPolymorph() > 0) {
					BuffController.remove(cha, ShapeChange.class);
					cha.isSetPoly = false;
				}
			}
		
			if (equipped && i.isHaste())
				Haste.init(cha, -1, true);
			else if (!equipped && i.isHaste())
				BuffController.remove(cha, Haste.class);
			
			if (equipped && i.isBrave()) {
				Bravery.init(cha, -1, true);
				Wafer.init(cha, -1, true);
				HolyWalk.init(cha, -1);
				movingacceleratic.init(cha, -1);
			} else if (!equipped && i.isBrave()) {
				BuffController.remove(cha, Bravery.class);
				BuffController.remove(cha, Wafer.class);
				BuffController.remove(cha, HolyWalk.class);
				BuffController.remove(cha, movingacceleratic.class);
			}
			
			if (i.getGm() > 0) {
				for (object o : cha.getInsideList()) {
					if (o != null && o instanceof Character && !o.isWorldDelete() && o.getMaxHp() > 1) {
						cha.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), (Character) o, equipped));
					}
				}
			}

			if (sendPacket) {
				cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
				cha.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), cha));
			}
		}
	}

	static public int getSize() {
		return list.size();
	}
}
