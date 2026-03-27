package lineage.world.controller;

/**
 * 맵 접근 정책 (GM 연동 오버레이용). 기본은 모든 맵 허용.
 */
public final class MapPolicy {

	private MapPolicy() {
	}

	public static boolean isAllowed(int mapId) {
		return true;
	}

	public static int getSafeX() {
		return 32788;
	}

	public static int getSafeY() {
		return 32797;
	}

	public static int getSafeMapId() {
		return 4;
	}
}
