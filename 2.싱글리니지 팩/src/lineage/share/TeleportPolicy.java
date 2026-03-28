package lineage.share;

/**
 * 맵 구역별 순간이동·랜덤텔 제한용 플래그.
 * {@link Lineage#init(boolean)} 에서 lineage.conf 를 읽을 때 갱신된다.
 */
public final class TeleportPolicy {

	/** 성 외성(외곽) 내부 — true 이면 텔 허용 */
	public static boolean kingdomOuterTeleport = false;
	/** 기란 혈맹 아지트 — true 이면 텔 허용 */
	public static boolean giranAgitTeleport = false;

	private TeleportPolicy() {
	}
}
