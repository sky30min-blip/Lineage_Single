package lineage.world.controller;

import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;

/**
 * 주요 성 — 외성({@link Lineage#KINGDOMLOCATION} 1~5·7번: 켄트·오크·윈다·기란·하이네·아덴 영지) 및
 * 내성 맵(15,29,52,64,300)에 대해 「밖에 있을 때 순간이동으로 안으로 들어오지 못하게」 처리.
 * (6번 아비스·8번 등은 제외)
 */
public final class CastleTeleportBlock {

	private CastleTeleportBlock() {}

	/** {@link Lineage#KINGDOMLOCATION} 행 번호 — 외성/성 영지 사각형 */
	private static final int[] KINGDOM_OUTER_ROWS = { 1, 2, 3, 4, 5, 7 };

	/** KINGDOMLOCATION[행]의 map 열과 좌표가 일치하면 해당 성 영지 안 */
	public static boolean isKingdomOuterOnMainland(int x, int y, int mapId) {
		for (int idx : KINGDOM_OUTER_ROWS) {
			if (idx < 0 || idx >= Lineage.KINGDOMLOCATION.length)
				continue;
			int[] loc = Lineage.KINGDOMLOCATION[idx];
			if (loc.length < 5)
				continue;
			if (mapId != loc[4])
				continue;
			if (x >= loc[0] && x <= loc[1] && y >= loc[2] && y <= loc[3])
				return true;
		}
		return false;
	}

	/** 클래식 내성 맵 — 켄트15, 윈다29, 기란52, 하이네64, 아덴300 */
	public static boolean isInnerCastleMap(int mapId) {
		return mapId == 15 || mapId == 29 || mapId == 52 || mapId == 64 || mapId == 300;
	}

	public static boolean isRestrictedCastleArea(int x, int y, int mapId) {
		return isInnerCastleMap(mapId) || isKingdomOuterOnMainland(x, y, mapId);
	}

	/**
	 * 일반 유저만. 현재 위치는 제한 구역 밖인데 목적지가 제한 구역 안이면 true (차단해야 함).
	 */
	public static boolean shouldBlockTeleportFromOutside(PcInstance pc, int destX, int destY, int destMap) {
		if (pc == null || pc.getGm() != 0)
			return false;
		if (!isRestrictedCastleArea(destX, destY, destMap))
			return false;
		return !isRestrictedCastleArea(pc.getX(), pc.getY(), pc.getMap());
	}
}
