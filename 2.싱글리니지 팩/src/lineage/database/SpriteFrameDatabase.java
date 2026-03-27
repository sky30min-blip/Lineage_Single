package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import lineage.bean.database.SpriteFrame;
import lineage.share.TimeLine;
import lineage.world.object.object;

public final class SpriteFrameDatabase {

	static private Map<Integer, SpriteFrame> list;

	static public void init(Connection con) {
		TimeLine.start("SpriteFrameDatabase..");

		list = new HashMap<Integer, SpriteFrame>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM spr_frame");
			rs = st.executeQuery();
			while (rs.next()) {
				int gfx = rs.getInt("gfx");

				SpriteFrame spriteFrame = list.get(gfx);
				
				if (spriteFrame == null) {
					spriteFrame = new SpriteFrame();
					spriteFrame.setGfx(gfx);
					list.put(gfx, spriteFrame);
				}

				spriteFrame.getList().put(rs.getInt("action"), (rs.getInt("frame")));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", SpriteFrameDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	/**
	 * 
	 * @param
	 * @return
	 * 2017-09-03
	 * by all_night.
	 */
	static public void reload() {
		TimeLine.start("spr_frame 테이블 리로드 완료 - ");

		list.clear();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM spr_frame");
			rs = st.executeQuery();
			while (rs.next()) {
				int gfx = rs.getInt("gfx");

				SpriteFrame spriteFrame = list.get(gfx);
				
				if (spriteFrame == null) {
					spriteFrame = new SpriteFrame();
					spriteFrame.setGfx(gfx);
					list.put(gfx, spriteFrame);
				}

				spriteFrame.getList().put(rs.getInt("action"), (rs.getInt("frame")));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", SpriteFrameDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		TimeLine.end();
	}

	/**
	 * gfx에 해당하는action값에 프레임값을 리턴함. 
	 * PC를 제외한 모든 객체가 사용중
	 */
	static public int getGfxFrameTime(object o, int gfx, int action) {
		SpriteFrame spriteFrame = SpriteFrameDatabase.getList().get(gfx);

		if (spriteFrame != null) {
			double frame = 0;
			Integer gfxFrame = spriteFrame.getList().get(action);

			if (gfxFrame != null)
				frame = gfxFrame.intValue();
			else
				return 1000;

			// 일반적인 촐기 용기 안한 상태
			if (o.getSpeed() == 0 && !o.isBrave())
				frame = frame * 41.2;
			// 촐기 또는 용기 상태
			else if ((o.getSpeed() == 1 && !o.isBrave()) || (o.getSpeed() == 0 && o.isBrave()))
				frame = frame * 31;
			// 촐기 용기 둘다
			else if (o.getSpeed() == 1 && o.isBrave())
				frame = frame * 23.2;
			// 슬로우 걸렸을 시, 촐기 용기 안한 상태
			else if (o.getSpeed() == 2 && !o.isBrave())
				frame = frame * 83;
			// 슬로우 걸렸을 시, 촐기 안한 상태
			else if (o.getSpeed() == 2 && o.isBrave())
				frame = frame * 62;

			return (int) (frame);
		}
		// 해당하는 모드가 없을경우 1초로 정의
		return 1000;
	}
	
	/**
	 * gfx에 해당하는action값에 프레임값을 가져와서
	 * 캐릭터의 속도에 따라서 프레임에따른 시간 계산
	 * 스피드핵 체크에서 사용중
	 */
	static public double getSpeedCheckGfxFrameTime(object o, int gfx, int action) {
		SpriteFrame spriteFrame = list.get(gfx);
		// 해당 gfx가 없을경우 10 프레임으로 정의.
		double frame = 10;

		if (spriteFrame != null) {
			Integer gfxFrame = spriteFrame.getList().get(action);

			if (gfxFrame != null)
				frame = gfxFrame.intValue();
		}

		// 일반적인 촐기 용기 안한 상태
				if (o.getSpeed() == 0 && !o.isBrave())
					frame = frame * 41.2;
				// 촐기 또는 용기 상태
				else if ((o.getSpeed() == 1 && !o.isBrave()) || (o.getSpeed() == 0 && o.isBrave()))
					frame = frame * 31;
				// 촐기 용기 둘다
				else if (o.getSpeed() == 1 && o.isBrave())
					frame = frame * 23.2;
				// 슬로우 걸렸을 시, 촐기 용기 안한 상태
				else if (o.getSpeed() == 2 && !o.isBrave())
					frame = frame * 83;
				// 슬로우 걸렸을 시, 촐기 안한 상태
				else if (o.getSpeed() == 2 && o.isBrave())
					frame = frame * 62;
		
		return Math.round(frame);
	}

	/**
	 * gfx에 해당 액션이 있는지 확인
	 * @param
	 * @return
	 * 2017-09-02
	 * by all_night.
	 */
	static public boolean findGfxMode(int gfx, int action) {
		return list.get(gfx).getList().get(action) != null;
	}

	static public int getSize() {
		return list.size();
	}
	public static Map<Integer, SpriteFrame> getList() {
		return list;
	}
	public static void setList(Map<Integer, SpriteFrame> list) {
		SpriteFrameDatabase.list = list;
	}
	static public int find(int gfx, int action) {
		// 뽕데스는 프레임 무시
		if(gfx == 5641)
			return 0;
		//
		SpriteFrame sf = list.get(gfx);
		if(sf != null){
			Integer frame = sf.getList().get(action);
			if(frame != null){
				return frame.intValue();
			}
		}
		// 해당하는 모드가 없을경우 1초로 정의
		return 640; //640
	}
}
