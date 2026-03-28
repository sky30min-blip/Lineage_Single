package lineage.world.controller;

import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.instance.ItemInstance;

/**
 * 싱글 팩 전용: 클라 htm 없이 서버에서 자동사냥 대화 HTML 전체를 만든다.
 * 전송 형식은 {@link lineage.share.Lineage#autohunt_html_dialog_mode} 로 결정된다.
 */
public final class AutoHuntHtmlBuilder {

	private AutoHuntHtmlBuilder() {
	}

	private static String esc(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
	}

	/**
	 * 일부 클라는 대화 HTML을 printf/wsprintf 계열로 한 번 더 거친다.
	 * {@code %} 는 {@code %%} 보다 HTML 엔티티 {@code &#37;} 가 파싱·표시 모두 안전한 편이다.
	 */
	private static String safeHtmlForClient(String html) {
		if (html == null)
			return "";
		return html.replace("%", "&#37;");
	}

	private static void btn(StringBuilder sb, String label, String bypassBody) {
		if (bypassBody == null)
			bypassBody = "";
		if (Lineage.autohunt_html_use_button) {
			// 파워볼 NPC 등과 동일 계열 — Ep8 일부에서 <a> 인라인만 불안정한 경우가 있음
			String v = esc(label == null ? "" : label);
			if (v.length() > 28)
				v = v.substring(0, 25) + "...";
			sb.append("<button value=\"").append(v).append("\" action=\"bypass ").append(bypassBody)
					.append("\" width=110 height=21></button> ");
		} else {
			sb.append("<a action=\"bypass ").append(bypassBody).append("\">").append(esc(label)).append("</a> ");
		}
	}

	private static String timeLine(PcInstance pc) {
		if (!Lineage.is_auto_hunt_time)
			return "[남은 시간: 무제한]";
		long time = Lineage.is_auto_hunt_time_account ? pc.auto_hunt_account_time : pc.auto_hunt_time;
		if (time > 0) {
			if (time / 3600 > 0)
				return String.format("[남은 시간: %d시간 %d분 %d초]", time / 3600, time % 3600 / 60, time % 3600 % 60);
			if (time % 3600 / 60 > 0)
				return String.format("[남은 시간: %d분 %d초]", time % 3600 / 60, time % 3600 % 60);
			return String.format("[남은 시간: %d초]", time % 3600 % 60);
		}
		return "[남은 시간: 없음]";
	}

	/**
	 * 메인 화면 전용 초소형 HTML. {@code autohunt_html_dialog_mode=minimal} 일 때만 사용.
	 * 태그·길이를 최소화해 클라 팅김이 HTML/패킷 중 어디인지 구분할 때 쓴다.
	 */
	public static String buildMinimal(PcInstance pc) {
		if (pc == null)
			return "<html><body></body></html>";
		StringBuilder sb = new StringBuilder(220);
		sb.append("<html><body>");
		sb.append("<a action=\"bypass autohunt-on\">ON</a> ");
		sb.append("<a action=\"bypass autohunt-off\">OFF</a> ");
		sb.append("<a action=\"bypass autohunt-refresh\">R</a>");
		sb.append("</body></html>");
		return sb.toString();
	}

	private static void appendMainNav(StringBuilder sb) {
		sb.append("<br><font color=\"888888\">");
		btn(sb, "1 기본", "autohunt-page-1");
		btn(sb, "2 버프·물약", "autohunt-page-2");
		btn(sb, "3 변신·기타", "autohunt-page-3");
		sb.append("</font>");
	}

	/** 한 화면 전체(길다). {@link Lineage#autohunt_html_paginate} false 일 때만 */
	private static String buildMainMonolithic(PcInstance pc) {
		StringBuilder sb = new StringBuilder(4096);
		sb.append("<html><body><center>");
		sb.append("<font color=\"LEVEL\">자동 사냥 설정</font></center><br>");
		sb.append("<font color=\"AAAAAA\">").append(esc(timeLine(pc))).append("</font><br><br>");

		sb.append("<font color=\"B09878\">■ 상태</font><br>");
		sb.append("자동사냥: <font color=\"FFFF99\">").append(pc.isAutoHunt ? "ON" : "OFF").append("</font> ");
		btn(sb, "시작", "autohunt-on");
		btn(sb, "종료", "autohunt-off");
		sb.append("<br><br>");

		sb.append("<font color=\"B09878\">■ 귀환 체력</font> (현재: ");
		sb.append(pc.auto_return_home_hp < 1 ? "설정 X" : pc.auto_return_home_hp + "%").append(")<br>");
		for (int i = 0; i < Lineage.auto_hunt_home_hp_list.size() && i < 7; i++) {
			int v = Lineage.auto_hunt_home_hp_list.get(i);
			btn(sb, v + "%", "autohunt-" + (i + 1));
		}
		sb.append("<br><br>");

		sb.append("<font color=\"B09878\">■ 버프 / 물약</font><br>");
		sb.append("자동버프: ").append(pc.is_auto_buff ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-buffon");
		btn(sb, "OFF", "autohunt-buffoff");
		sb.append("<br>");
		sb.append("자동물약: ").append(pc.isAutoPotion ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-potionon");
		btn(sb, "OFF", "autohunt-potionoff");
		sb.append(" 구매: ").append(pc.is_auto_potion_buy ? "ON" : "OFF").append(" ");
		btn(sb, "구매ON", "autohunt-potionbuy");
		btn(sb, "구매OFF", "autohunt-potionnobuy");
		sb.append("<br>");
		sb.append("물약 기준 HP: ")
				.append(pc.autoPotionPercent < 1 ? "설정 X" : pc.autoPotionPercent + "% 이하").append(" ");
		for (int i = 0; i < Lineage.auto_hunt_potion_hp_list.size() && i < 7; i++) {
			btn(sb, Lineage.auto_hunt_potion_hp_list.get(i) + "%", "autohunt-potion-percent-" + (i + 1));
		}
		sb.append("<br>");
		sb.append("선택 물약: <font color=\"99CCFF\">")
				.append(esc(pc.autoPotionName == null || pc.autoPotionName.length() < 1 ? "설정 X" : pc.autoPotionName))
				.append("</font> ");
		btn(sb, "물약 선택", "autohunt-potion");
		sb.append("<br><br>");

		sb.append("<font color=\"B09878\">■ 변신</font><br>");
		sb.append("우선: ").append(pc.is_auto_poly_select ? "랭변" : "일반").append(" ");
		btn(sb, "일반", "autohunt-poly-nomal");
		btn(sb, "랭변", "autohunt-poly-rank");
		sb.append("<br>");
		sb.append("자동랭변: ").append(pc.is_auto_rank_poly ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-poly-rank-on");
		btn(sb, "OFF", "autohunt-poly-rank-off");
		sb.append(" 랭변구매: ").append(pc.is_auto_rank_poly_buy ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-poly-rank-buy");
		btn(sb, "OFF", "autohunt-poly-rank-nobuy");
		sb.append("<br>");
		sb.append("자동변신: ").append(pc.is_auto_poly ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-polyon");
		btn(sb, "OFF", "autohunt-polyoff");
		sb.append(" 변줌구매: ").append(pc.is_auto_poly_buy ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-polybuy");
		btn(sb, "OFF", "autohunt-polynobuy");
		sb.append("<br>");
		sb.append("퀵 변신: <font color=\"99CCFF\">")
				.append(esc(pc.getQuickPolymorph() == null || pc.getQuickPolymorph().length() < 1 ? "설정 X"
						: pc.getQuickPolymorph()))
				.append("</font><br><br>");

		sb.append("<font color=\"B09878\">■ 기타</font><br>");
		sb.append("자동텔포: ").append(pc.is_auto_teleport ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-teleporton");
		btn(sb, "OFF", "autohunt-teleportoff");
		sb.append("<br>");
		sb.append("자동용기: ").append(pc.is_auto_bravery ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-braveryon");
		btn(sb, "OFF", "autohunt-braveryoff");
		sb.append(" 구매: ").append(pc.is_auto_bravery_buy ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-braverybuy");
		btn(sb, "OFF", "autohunt-braverynobuy");
		sb.append("<br>");
		sb.append("자동촐기: ").append(pc.is_auto_haste ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-hasteon");
		btn(sb, "OFF", "autohunt-hasteoff");
		sb.append(" 구매: ").append(pc.is_auto_haste_buy ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-hastebuy");
		btn(sb, "OFF", "autohunt-hastenobuy");
		sb.append("<br>");
		sb.append("화살 구매: ").append(pc.is_auto_arrow_buy ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-arrowbuy");
		btn(sb, "OFF", "autohunt-arrowbuynobuy");
		sb.append("<br><br>");

		btn(sb, "자동 스킬 설정", "autohunt-skill");
		sb.append("</body></html>");
		return safeHtmlForClient(sb.toString());
	}

	private static String buildMainPage1(PcInstance pc) {
		StringBuilder sb = new StringBuilder(1800);
		sb.append("<html><body><center>");
		sb.append("<font color=\"LEVEL\">자동 사냥 (1/3)</font></center><br>");
		sb.append("<font color=\"AAAAAA\">").append(esc(timeLine(pc))).append("</font><br><br>");
		sb.append("<font color=\"B09878\">■ 상태</font><br>");
		sb.append("자동사냥: <font color=\"FFFF99\">").append(pc.isAutoHunt ? "ON" : "OFF").append("</font> ");
		btn(sb, "시작", "autohunt-on");
		btn(sb, "종료", "autohunt-off");
		sb.append("<br><br>");
		sb.append("<font color=\"B09878\">■ 귀환 체력</font> (현재: ");
		sb.append(pc.auto_return_home_hp < 1 ? "설정 X" : pc.auto_return_home_hp + "%").append(")<br>");
		for (int i = 0; i < Lineage.auto_hunt_home_hp_list.size() && i < 7; i++) {
			int v = Lineage.auto_hunt_home_hp_list.get(i);
			btn(sb, v + "%", "autohunt-" + (i + 1));
		}
		appendMainNav(sb);
		sb.append("</body></html>");
		return safeHtmlForClient(sb.toString());
	}

	private static String buildMainPage2(PcInstance pc) {
		StringBuilder sb = new StringBuilder(2200);
		sb.append("<html><body><center>");
		sb.append("<font color=\"LEVEL\">자동 사냥 (2/3)</font></center><br><br>");
		sb.append("<font color=\"B09878\">■ 버프 / 물약</font><br>");
		sb.append("자동버프: ").append(pc.is_auto_buff ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-buffon");
		btn(sb, "OFF", "autohunt-buffoff");
		sb.append("<br>");
		sb.append("자동물약: ").append(pc.isAutoPotion ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-potionon");
		btn(sb, "OFF", "autohunt-potionoff");
		sb.append(" 구매: ").append(pc.is_auto_potion_buy ? "ON" : "OFF").append(" ");
		btn(sb, "구매ON", "autohunt-potionbuy");
		btn(sb, "구매OFF", "autohunt-potionnobuy");
		sb.append("<br>");
		sb.append("물약 기준 HP: ")
				.append(pc.autoPotionPercent < 1 ? "설정 X" : pc.autoPotionPercent + "% 이하").append(" ");
		for (int i = 0; i < Lineage.auto_hunt_potion_hp_list.size() && i < 7; i++) {
			btn(sb, Lineage.auto_hunt_potion_hp_list.get(i) + "%", "autohunt-potion-percent-" + (i + 1));
		}
		sb.append("<br>");
		sb.append("선택 물약: <font color=\"99CCFF\">")
				.append(esc(pc.autoPotionName == null || pc.autoPotionName.length() < 1 ? "설정 X" : pc.autoPotionName))
				.append("</font> ");
		btn(sb, "물약 선택", "autohunt-potion");
		appendMainNav(sb);
		sb.append("</body></html>");
		return safeHtmlForClient(sb.toString());
	}

	private static String buildMainPage3(PcInstance pc) {
		StringBuilder sb = new StringBuilder(2200);
		sb.append("<html><body><center>");
		sb.append("<font color=\"LEVEL\">자동 사냥 (3/3)</font></center><br><br>");
		sb.append("<font color=\"B09878\">■ 변신</font><br>");
		sb.append("우선: ").append(pc.is_auto_poly_select ? "랭변" : "일반").append(" ");
		btn(sb, "일반", "autohunt-poly-nomal");
		btn(sb, "랭변", "autohunt-poly-rank");
		sb.append("<br>");
		sb.append("자동랭변: ").append(pc.is_auto_rank_poly ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-poly-rank-on");
		btn(sb, "OFF", "autohunt-poly-rank-off");
		sb.append(" 랭변구매: ").append(pc.is_auto_rank_poly_buy ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-poly-rank-buy");
		btn(sb, "OFF", "autohunt-poly-rank-nobuy");
		sb.append("<br>");
		sb.append("자동변신: ").append(pc.is_auto_poly ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-polyon");
		btn(sb, "OFF", "autohunt-polyoff");
		sb.append(" 변줌구매: ").append(pc.is_auto_poly_buy ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-polybuy");
		btn(sb, "OFF", "autohunt-polynobuy");
		sb.append("<br>");
		sb.append("퀵 변신: <font color=\"99CCFF\">")
				.append(esc(pc.getQuickPolymorph() == null || pc.getQuickPolymorph().length() < 1 ? "설정 X"
						: pc.getQuickPolymorph()))
				.append("</font><br><br>");
		sb.append("<font color=\"B09878\">■ 기타</font><br>");
		sb.append("자동텔포: ").append(pc.is_auto_teleport ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-teleporton");
		btn(sb, "OFF", "autohunt-teleportoff");
		sb.append("<br>");
		sb.append("자동용기: ").append(pc.is_auto_bravery ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-braveryon");
		btn(sb, "OFF", "autohunt-braveryoff");
		sb.append(" 구매: ").append(pc.is_auto_bravery_buy ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-braverybuy");
		btn(sb, "OFF", "autohunt-braverynobuy");
		sb.append("<br>");
		sb.append("자동촐기: ").append(pc.is_auto_haste ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-hasteon");
		btn(sb, "OFF", "autohunt-hasteoff");
		sb.append(" 구매: ").append(pc.is_auto_haste_buy ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-hastebuy");
		btn(sb, "OFF", "autohunt-hastenobuy");
		sb.append("<br>");
		sb.append("화살 구매: ").append(pc.is_auto_arrow_buy ? "ON" : "OFF").append(" ");
		btn(sb, "ON", "autohunt-arrowbuy");
		btn(sb, "OFF", "autohunt-arrowbuynobuy");
		sb.append("<br><br>");
		btn(sb, "자동 스킬 설정", "autohunt-skill");
		appendMainNav(sb);
		sb.append("</body></html>");
		return safeHtmlForClient(sb.toString());
	}

	/** 메인 자동사냥 설정 (직업 공통) */
	public static String buildMain(PcInstance pc) {
		if (!Lineage.autohunt_html_paginate)
			return buildMainMonolithic(pc);
		int p = pc.autohuntMenuPage;
		if (p < 1 || p > 3)
			p = 1;
		if (p == 1)
			return buildMainPage1(pc);
		if (p == 2)
			return buildMainPage2(pc);
		return buildMainPage3(pc);
	}

	public static String buildPotion(PcInstance pc) {
		StringBuilder sb = new StringBuilder(2048);
		sb.append("<html><body><center><font color=\"LEVEL\">자동 물약 선택</font></center><br>");
		sb.append("기준 HP: ")
				.append(pc.autoPotionPercent < 1 ? "설정 X" : pc.autoPotionPercent + "% 이하 복용").append("<br>");
		sb.append("현재 선택: <font color=\"99CCFF\">")
				.append(esc(pc.autoPotionName == null || pc.autoPotionName.length() < 2 ? "설정 X" : pc.autoPotionName))
				.append("</font><br><br>");

		if (pc.getInventory() == null) {
			sb.append("인벤토리 없음");
			sb.append("</body></html>");
			return safeHtmlForClient(sb.toString());
		}

		int idx = 0;
		for (ItemInstance potion : pc.getInventory().getList()) {
			if (potion == null || potion.getItem() == null || !(potion instanceof HealingPotion))
				continue;
			String nm = potion.getItem().getName();
			String line = nm + " (" + potion.getCount() + ")";
			btn(sb, line.length() > 32 ? line.substring(0, 29) + "..." : line, "autohunt-potion-item-" + idx);
			if ((idx + 1) % 2 == 0)
				sb.append("<br>");
			idx++;
		}
		if (idx == 0)
			sb.append("<font color=\"FF6666\">인벤토리에 물약이 없습니다.</font><br>");

		sb.append("<br>");
		btn(sb, "← 메인으로", "autohunt-refresh");
		sb.append("</body></html>");
		return safeHtmlForClient(sb.toString());
	}

	private static void skillManaButtons(StringBuilder sb, PcInstance pc) {
		sb.append("<font color=\"B09878\">MP % (1차 목록)</font><br>");
		for (int i = 0; i < 7 && i < Lineage.auto_hunt_mp_list.size(); i++) {
			int v = Lineage.auto_hunt_mp_list.get(i);
			btn(sb, (pc.autoMPPercent == v ? "[" : "") + v + "%" + (pc.autoMPPercent == v ? "]" : ""), "autohunt-skill-mana-" + (i + 1));
		}
		sb.append("<br><font color=\"B09878\">MP % (2차 목록·요정 등)</font><br>");
		for (int i = 0; i < 7 && i < Lineage.auto_hunt_mp_list2.size(); i++) {
			int v = Lineage.auto_hunt_mp_list2.get(i);
			int key = i + 8;
			btn(sb, (pc.autoMPPercent2 == v ? "[" : "") + v + "%" + (pc.autoMPPercent2 == v ? "]" : ""), "autohunt-skill-mana-" + key);
		}
		sb.append("<br>");
	}

	private static void toggle(StringBuilder sb, String label, boolean on, String onBypass, String offBypass) {
		sb.append(esc(label)).append(": ").append(on ? "ON" : "OFF").append(" ");
		btn(sb, "ON", onBypass);
		btn(sb, "OFF", offBypass);
		sb.append("<br>");
	}

	public static String buildSkill(PcInstance pc) {
		StringBuilder sb = new StringBuilder(6144);
		sb.append("<html><body><center><font color=\"LEVEL\">자동 스킬 설정</font></center><br>");
		toggle(sb, "자동스킬 전체", pc.is_auto_skill, "autohunt-skill-on", "autohunt-skill-off");
		skillManaButtons(sb, pc);
		sb.append("<br>");

		int cls = pc.getClassType();
		if (cls == Lineage.LINEAGE_CLASS_KNIGHT || cls == Lineage.LINEAGE_CLASS_DRAGONKNIGHT) {
			toggle(sb, "리덕션 아머", pc.is_auto_reductionarmor, "autohunt-skill-reductionarmoron", "autohunt-skill-reductionarmoroff");
			toggle(sb, "솔리드 캐리지", pc.is_auto_solidcarriage, "autohunt-skill-solidcarriageon", "autohunt-skill-solidcarriageoff");
			toggle(sb, "카운터 배리어", pc.is_auto_counterbarrier, "autohunt-skill-counterbarrieron", "autohunt-skill-counterbarrieroff");
		} else if (cls == Lineage.LINEAGE_CLASS_ROYAL) {
			toggle(sb, "글로잉 웨폰", pc.is_auto_glowingweapon, "autohunt-skill-glowingweaponon", "autohunt-skill-glowingweaponoff");
			toggle(sb, "샤이닝 실드", pc.is_auto_shiningshieldon, "autohunt-skill-shiningshieldon", "autohunt-skill-shiningshieldoff");
			toggle(sb, "브레이브 멘탈", pc.is_auto_bravemental, "autohunt-skill-bravementalon", "autohunt-skill-bravementaloff");
			toggle(sb, "브레이브 아바타", pc.is_auto_braveavatar, "autohunt-skill-braveavataron", "autohunt-skill-braveavataroff");
		} else if (cls == Lineage.LINEAGE_CLASS_WIZARD || cls == Lineage.LINEAGE_CLASS_BLACKWIZARD) {
			toggle(sb, "턴 언데드", pc.is_turnundead, "autohunt-skill-turnundeadon", "autohunt-skill-turnundeadoff");
			toggle(sb, "스네이크 바이트", pc.is_snakebite, "autohunt-skill-snakebiteon", "autohunt-skill-snakebiteoff");
			toggle(sb, "이럽션", pc.is_eruption, "autohunt-skill-eruptionon", "autohunt-skill-eruptionoff");
			toggle(sb, "선버스트", pc.is_sunburst, "autohunt-skill-sunburston", "autohunt-skill-sunburstoff");
			toggle(sb, "버서커스", pc.is_berserkers, "autohunt-skill-berserkerson", "autohunt-skill-berserkersoff");
			toggle(sb, "이뮨", pc.is_Immunity, "autohunt-skill-Immunityon", "autohunt-skill-Immunityoff");
		} else if (cls == Lineage.LINEAGE_CLASS_DARKELF) {
			toggle(sb, "인챈트 베놈", pc.is_enchantvenom, "autohunt-skill-enchantvenomon", "autohunt-skill-enchantvenomoff");
			toggle(sb, "번링 스피릿", pc.is_burningspirits, "autohunt-skill-burningspiritson", "autohunt-skill-burningspiritsoff");
			toggle(sb, "쉐도우 아머", pc.is_shadowarmor, "autohunt-skill-shadowarmoron", "autohunt-skill-shadowarmoroff");
			toggle(sb, "더블 브레이크", pc.is_doublebrake, "autohunt-skill-doublebrakeon", "autohunt-skill-doublebrakeoff");
			toggle(sb, "쉐도우 팡", pc.is_shadowpong, "autohunt-skill-shadowpongon", "autohunt-skill-shadowpongoff");
			toggle(sb, "언캐니 닷지", pc.is_uncannydodge, "autohunt-skill-uncannydodgeon", "autohunt-skill-uncannydodgeoff");
			toggle(sb, "드레스 마이티", pc.is_dressmighty, "autohunt-skill-dressmightyon", "autohunt-skill-dressmightyoff");
			toggle(sb, "드레스 덱스", pc.is_dressdexterity, "autohunt-skill-dressdexterityon", "autohunt-skill-dressdexterityoff");
			toggle(sb, "드레스 이베이전", pc.is_dressevasion, "autohunt-skill-dressevasionon", "autohunt-skill-dressevasionoff");
		} else if (cls == Lineage.LINEAGE_CLASS_ELF) {
			toggle(sb, "블러드 투 소울", pc.is_auto_bloodtosoul, "autohunt-skill-bloodtosoulon", "autohunt-skill-bloodtosouloff");
			toggle(sb, "트리플 애로우", pc.is_auto_triplearrow, "autohunt-skill-triplearrowon", "autohunt-skill-triplearrowoff");
			toggle(sb, "레지스트 매직", pc.is_auto_resistmagic, "autohunt-skill-resistmagicon", "autohunt-skill-resistmagicoff");
			toggle(sb, "클리어 마인드", pc.is_auto_clearmind, "autohunt-skill-clearmindon", "autohunt-skill-clearmindoff");
			toggle(sb, "레지스트 엘리먼트", pc.is_auto_resistelement, "autohunt-skill-resistelementon", "autohunt-skill-resistelementoff");
		} else {
			sb.append("<font color=\"888888\">이 직업용 자동스킬 토글은 별도 정의가 없습니다.</font><br>");
		}

		sb.append("<br>");
		btn(sb, "← 메인으로", "autohunt-refresh");
		sb.append("</body></html>");
		return safeHtmlForClient(sb.toString());
	}
}
