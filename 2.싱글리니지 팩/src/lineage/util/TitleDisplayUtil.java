package lineage.util;

/**
 * PC 머리 위 호칭.
 * <p>
 * 클라이언트는 S_ObjectAdd(캐릭터 팩)의 lawful 등으로 이름·호칭 색을 잡는 경우가 많아,
 * 색 코드 없이내도 성향색으로 호칭이 보일 수 있다. 그때는 {@code \\fR} 접두로 흰색(일반)에 가깝게 고정한다.
 */
public final class TitleDisplayUtil {

	private TitleDisplayUtil() {
	}

	public static String stripLineageColorCodes(String title) {
		if (title == null)
			return "";
		String t = title.trim();
		if (t.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder(t.length());
		for (int i = 0; i < t.length();) {
			if (i + 2 < t.length() && t.charAt(i) == '\\' && t.charAt(i + 1) == 'f') {
				i += 3;
				continue;
			}
			sb.append(t.charAt(i++));
		}
		return sb.toString().trim();
	}

	/**
	 * 호칭 저장·브로드캐스트용: 사용자가 넣은 {@code \\fX} 제거 후 {@code \\fR}(흰/기본) 접두.
	 * 빈 호칭은 그대로 빈 문자열.
	 */
	public static String forPcTitleClient(String title) {
		String plain = stripLineageColorCodes(title);
		if (plain.isEmpty())
			return "";
		return "\\fR" + plain;
	}
}
