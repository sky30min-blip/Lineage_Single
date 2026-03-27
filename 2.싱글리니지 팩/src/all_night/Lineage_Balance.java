package all_night;

import java.io.BufferedReader;
import java.io.FileReader;

import lineage.share.TimeLine;

public final class Lineage_Balance {
	// 레벨업시 캐릭터의 HP 증가량 조절.
	static public double level_up_hp_royal;
	static public double level_up_hp_knight;
	static public double level_up_hp_elf;
	static public double level_up_hp_darkelf;
	static public double level_up_hp_wizard;
	// 레벨업시 캐릭터의 MP 증가량 조절.
	static public double level_up_mp_royal;
	static public double level_up_mp_knight;
	static public double level_up_mp_elf;
	static public double level_up_mp_darkelf;
	static public double level_up_mp_wizard;
	
	// 근거리 피격시 군주의 ac 가용률
	static public double pc_hit_ac_royal_percent;
	// 근거리 피격시 기사의 ac 가용률
	static public double pc_hit_ac_knight_percent;
	// 근거리 피격시 요정의 ac 가용률
	static public double pc_hit_ac_elf_percent;
	// 근거리 피격시 다크엘프의 ac 가용률
	static public double pc_hit_ac_darkelf_percent;
	// 근거리 피격시 요정의 ac 가용률
	static public double pc_hit_ac_wizard_percent;
	
	// 원거리 피격시 군주의 ac 가용률
	static public double pc_bow_hit_ac_royal_percent;
	// 원거리 피격시 기사의 ac 가용률
	static public double pc_bow_hit_ac_knight_percent;
	// 원거리 피격시 요정의 ac 가용률
	static public double pc_bow_hit_ac_elf_percent;
	// 원거리 피격시 다크엘프의 ac 가용률
	static public double pc_bow_hit_ac_darkelf_percent;
	// 원거리 피격시 마법사의 ac 가용률
	static public double pc_bow_hit_ac_wizard_percent;
	
	//치명타 사용여부
	static public boolean is_critical;
	//치명타 확율 외부화
	static public int weapon_critical_persent;
	//치명타 최소 데미지 외부화
	static public int critical_Min_Dmg;
	//치명타 최대 데미지 외부화
	static public int critical_Max_Dmg;
	
	//클래스별 대미지 감소
	static public double ROYAL_dmg ;
	static public double KNIGHT_dmg ;
	static public double ELF_dmg ;
	static public double WIZARD_dmg ;
	static public double darkElf_dmg ;
	
	//아머브레이크
	static public double am_probability;

	
	// 트리플 애로우 최종 대미지의 몇% 적용 여부
	static public double triple_arrow_damage;
	// 트리플 동시 공격시 중첩 체크할 시간(초)
	static public double triple_damage_reduction_time;
	// 트리플을 두명이상에게 동시에 공격받을 경우 데미지 감소(%)
	static public double triple_damage_reduction;
	
	// 쇼크스턴 양손검 착용여부
	static public boolean is_stun_twohandsword;

	// 힐올, 네이쳐스 블레싱 중첩 여부
	static public boolean is_heal_damage;
	// 힐올, 네이쳐스 블레싱 중첩 시간(초)
	static public double heal_time;
	// 힐올, 네이쳐스 블레싱 중첩 허용 안할 경우 힐량 감소
	static public double heal_reduction;
	
	// 힐올 차는양 조절
	static public double heal_all_rate;
	// 블레싱 차는양 조절
	static public double blessing_rate;
	
	// 마법 기본 확률(%)
	static public int magic_probability;
	// 사일런스의 확률 감소
	static public double silence_probability;
	// 디케이 포션의 확률 감소
	static public double decay_potion_probability;
	// 턴언데드 요정, 법사 확률
	static public double turn_undead_elf_probability;
	static public double turn_undead_wizard_probability;
	// 커스패럴라이즈 요정, 법사 확률
	static public double curseParalyze_elf_probability;
	static public double curseParalyze_wizard_probability;
	// 켄슬레이션 요정, 법사 확률
	static public double cancellation_elf_probability;
	static public double cancellation_wizard_probability;
	// 마법 확률(%)
	static public double count_barrier_knight;
	static public double count_barrier_elf;
	static public double striker_gale;
	static public double earth_bind;
	static public double pollute_watar;
	static public double entangle;
	static public double area_of_silence;
	
	// 미티어 스트라이크 대미지 중첩 여부
	static public boolean is_meteor_strike_damage;
	// 미티어 스트라이크 대미지 중첩 시간(초)
	static public double meteor_strike_time;
	// 미티어 스트라이크 중첩 허용 안할 경우 대미지 감소율
	static public double meteor_strike_reduction;
	
	// 디스인티그레이트 대미지 중첩 여부
	static public boolean is_this_inti_greate_damage;
	// 디스인티그레이트 대미지 중첩 시간(초)
	static public double this_inti_greate_time;
	// 디스인티그레이트 중첩 허용 안할 경우 대미지 감소율
	static public double this_inti_greate_reduction;
	
	// 좌표에 객체가 2명 이상일 경우(겹치기) PC에게 대미지 적용 여부
	static public boolean is_fusion_attack;
	// 이뮨 투 함 대미지 감소율
	static public double immuneToHarmReduction;
	// 세인트 이뮨 투 함 대미지 감소율
	static public double immuneToHarmReduction2;
	// 임페리얼아머 대미지 감소율
	static public double ipReduction;
	
	// 단검 대미지 감소율
	static public double drReduction;
	
	// mr 100 이하의 마법 대미지 감소
	static public double mr_low_damage_reduce;
	// mr 101 이상의 마법 대미지 감소
	static public double mr_high_damage_reduce;
	
	// 군주 HP틱 밸런스
	static public double royal_hp_tic_figure;
	// 기사 HP틱 밸런스
	static public double knight_hp_tic_figure;
	// 요정 HP틱 밸런스
	static public double elf_hp_tic_figure;
	// 다크엘프 HP틱 밸런스
	static public double darkelf_hp_tic_figure;
	// 마법사 HP틱 밸런스
	static public double wizard_hp_tic_figure;
	
	// 군주 MP틱 밸런스
	static public double royal_mp_tic_figure;
	// 기사 MP틱 밸런스
	static public double knight_mp_tic_figure;
	// 요정 MP틱 밸런스
	static public double elf_mp_tic_figure;
	// 다크엘프 MP틱 밸런스
	static public double darkelf_mp_tic_figure;
	// 마법사 MP틱 밸런스
	static public double wizard_mp_tic_figure;
	
	// PC AC에 따른 근거리 명중
	static public double pc_hit_rate;
	
	static public double pc_hit_rate2;
	
	static public double pc_hit_rate3;
	
	static public double pc_hit_rate4;
	
	static public double pc_hit_rate5;
	// PC AC에 따른 원거리 명중
	static public double pc_bow_hit_rate;
	
	// 군주 마법 대미지 최종 밸런스
	static public double royal_magic_final_damage_figure;
	// 기사 마법 대미지 최종 밸런스
	static public double knight_magic_final_damage_figure;
	// 요정 마법 대미지 최종 밸런스
	static public double elf_magic_final_damage_figure;
	// 다크엘프 마법 대미지 최종 밸런스
	static public double darkelf_magic_final_damage_figure;
	// 마법사 마법 대미지 최종 밸런스
	static public double wizard_magic_final_damage_figure;
	
	// 군주 마법 명중 최종 밸런스
	static public double royal_magic_final_hit_figure;
	// 기사 마법 명중 최종 밸런스
	static public double knight_magic_final_hit_figure;
	// 요정 마법 명중 최종 밸런스
	static public double elf_magic_final_hit_figure;
	// 다크엘프 마법 명중 최종 밸런스
	static public double darkelf_magic_final_hit_figure;
	// 마법사 마법 명중 최종 밸런스
	static public double wizard_magic_final_hit_figure;
		
	// 군주 근거리 대미지 밸런스
	static public double royal_damage_figure;
	// 기사 근거리 대미지 밸런스
	static public double knight_damage_figure;
	// 요정 근거리 대미지 밸런스
	static public double elf_damage_figure;
	// 다크엘프 근거리 대미지 밸런스
	static public double darkelf_damage_figure;
	// 마법사 근거리 대미지 밸런스
	static public double wizard_damage_figure;
	
	// 군주 근거리 명중 밸런스
	static public double royal_hit_figure;
	// 기사 근거리 명중 밸런스
	static public double knight_hit_figure;
	// 요정 근거리 명중 밸런스
	static public double elf_hit_figure;
	// 다크엘프 근거리 명중 밸런스
	static public double darkelf_hit_figure;
	// 마법사 근거리 명중 밸런스
	static public double wizard_hit_figure;
	
	// 군주 근거리 치명타 밸런스
	static public double royal_critical_figure;
	// 기사 근거리 치명타 밸런스
	static public double knight_critical_figure;
	// 요정 근거리 치명타 밸런스
	static public double elf_critical_figure;
	// 다크엘프 근거리 치명타 밸런스
	static public double darkelf_critical_figure;
	// 마법사 근거리 치명타 밸런스
	static public double wizard_critical_figure;
	
	// 군주 원거리 대미지 밸런스
	static public double royal_bow_damage_figure;
	// 기사 원거리 대미지 밸런스
	static public double knight_bow_damage_figure;
	// 요정 원거리 대미지 밸런스
	static public double elf_bow_damage_figure;
	// 다크엘프 원거리 대미지 밸런스
	static public double darkelf_bow_damage_figure;
	// 마법사 원거리 대미지 밸런스
	static public double wizard_bow_damage_figure;
	
	// 군주 원거리 명중 밸런스
	static public double royal_bow_hit_figure;
	// 기사 원거리 명중 밸런스
	static public double knight_bow_hit_figure;
	// 요정 원거리 명중 밸런스
	static public double elf_bow_hit_figure;
	// 다크엘프 원거리 명중 밸런스
	static public double darkelf_bow_hit_figure;
	// 마법사 원거리 명중 밸런스
	static public double wizard_bow_hit_figure;
	
	// 군주 원거리 치명타 밸런스
	static public double royal_bow_critical_figure;
	// 기사 원거리 치명타 밸런스
	static public double knight_bow_critical_figure;
	// 요정 원거리 치명타 밸런스
	static public double elf_bow_critical_figure;
	// 다크엘프 원거리 치명타 밸런스
	static public double darkelf_bow_critical_figure;
	// 마법사 원거리 치명타 밸런스
	static public double wizard_bow_critical_figure;
	
	// 군주 마법 대미지 밸런스
	static public double royal_magic_damage_figure;
	// 기사 마법 대미지 밸런스
	static public double knight_magic_damage_figure;
	// 요정 마법 대미지 밸런스
	static public double elf_magic_damage_figure;
	// 다크엘프 마법 대미지 밸런스
	static public double darkelf_magic_damage_figure;
	// 마법사 마법 대미지 밸런스
	static public double wizard_magic_damage_figure;
	
	// 군주 마법 명중 밸런스
	static public double royal_magic_hit_figure;
	// 기사 마법 명중 밸런스
	static public double knight_magic_hit_figure;
	// 요정 마법 명중 밸런스
	static public double elf_magic_hit_figure;
	// 다크엘프 마법 명중 밸런스
	static public double darkelf_magic_hit_figure;
	// 마법사 마법 명중 밸런스
	static public double wizard_magic_hit_figure;
	
	// 군주 마법 치명타 밸런스
	static public double royal_magic_critical_figure;
	// 기사 마법 치명타 밸런스
	static public double knight_magic_critical_figure;
	// 요정 마법 치명타 밸런스
	static public double elf_magic_critical_figure;
	// 다크엘프 마법 치명타 밸런스
	static public double darkelf_magic_critical_figure;
	// 마법사 마법 치명타 밸런스
	static public double wizard_magic_critical_figure;
	
	// 군주 마법 치명타 밸런스
	static public double royal_magic_bonus_figure;
	// 기사 마법 치명타 밸런스
	static public double knight_magic_bonus_figure;
	// 요정 마법 치명타 밸런스
	static public double elf_magic_bonus_figure;
	// 다크엘프 마법 치명타 밸런스
	static public double darkelf_magic_bonus_figure;
	// 마법사 마법 치명타 밸런스
	static public double wizard_magic_bonus_figure;
	
	// 나비켓의 하급, 중급, 상급, 최상급 보스들은 대상의 마방 무시하고 monster_skill 테이블의 고정뎀지로 줄지 여부.
	static public boolean is_boss_monster_mr_dmg;
	// 몬스터 HP/MP 배율
	static public double monster_hp_rate;
	static public double monster_mp_rate;
	
	// 몬스터 레벨에 따른 대미지
	static public double monster_level_min_damage_rate;
	static public double monster_level_max_damage_rate;
	
	// 몬스터 명중 배율
	static public double monster_hit_rate;
	static public double monster_bow_hit_rate;
	
	// 서먼몬스터 레벨에 따른 대미지
	static public double summon_level_min_damage_rate;
	static public double summon_level_max_damage_rate;
	
	// 서먼몬스터 명중 배율
	static public double summon_hit_rate;
	static public double summon_bow_hit_rate;
	
	// 펫 레벨에 따른 대미지
	static public double pet_level_min_damage_rate;
	static public double pet_level_max_damage_rate;
	
	// 파우스트 등장시 스폰 알림 여부
	static public boolean faust_spawn_msg;
	
	// 깜짝 상자 스폰 확률
	static public double event_b_spawn_probability;
	
	// 깜짝 대박상자 스폰 확률
	static public double event_b2_spawn_probability;
	
	// 깜짝 상자 스폰 확률
	static public double event_a_spawn_probability;
	
	// 깜짝 대박상자 스폰 확률
	static public double event_a2_spawn_probability;
	
	
	// 깜짝 상자 스폰 확률
	static public double event_s_spawn_probability;
	
	// 깜짝 대박상자 스폰 확률
	static public double event_s2_spawn_probability;
	
	// 돌발성 보스몬스터의 스폰 확률
	static public double faust_spawn_probability;
	// 감시자 리퍼 등장시 스폰 알림 여부
	static public boolean grimreaper_spawn_msg;
	// 오만의 탑 몬스터가 감시자 리퍼로 변신할 확률
	static public double grimreaper_spawn_probability;
	// 오만의 탑 각층 보스 등장시 스폰 알림 여부
	static public boolean oman_spawn_msg;
	// 감시자 리퍼가 사라지고 오만의 탑 해당층 보스가 스폰될 확률
	static public double oman_spawn_probability;
	// 감시자 리퍼의 현재 체력이 최대 체력의 %이하 일때 확률 체크할 여부
	static public double oman_spawn_hp_min;
	static public double oman_spawn_hp_max;
	
	// 1단계 마법인형 합성 확률
	static public double magicDoll_class_1_probability;
	// 1단계 마법인형 합성 대성공 확률
	static public double magicDoll_class_1_perfect_probability;
	// 2단계 마법인형 합성 확률
	static public double magicDoll_class_2_probability;
	// 2단계 마법인형 합성 대성공 확률
	static public double magicDoll_class_2_perfect_probability;
	// 3단계 마법인형 합성 확률
	static public double magicDoll_class_3_probability;
	// 3단계 마법인형 합성 대성공 확률
	static public double magicDoll_class_3_perfect_probability;
	// 4단계 마법인형 합성 확률
	static public double magicDoll_class_4_probability;
	
	// 용 마법인형 합성 확률
	static public double magicDoll_class_6_probability;
	// 특수합성 성공 확률
	static public double magicDoll_class_5_probability;
	
	// 흑장로 마법인형 최소 대미지
	static public int magicDoll_black_elder_min_damage;
	// 흑장로 마법인형 최대 대미지
	static public int magicDoll_black_elder_max_damage;
	// 데스 나이트 마법인형 최소 대미지
	static public int magicDoll_death_knight_min_damage;
	// 데스 나이트 마법인형 최대 대미지
	static public int magicDoll_death_knight_max_damage;
	
	// 축복 부여 주문서 확률(%)
	static public double bless_change_probability1;
	static public double bless_change_probability2;
	static public double bless_change_probability3;
	static public double bless_change_probability4;
	static public double bless_change_probability5;
	static public double bless_change_probability6;
	
	// 안전인챈트 0 무기 0 -> 1 확률(%)
	static public double weapon_safe_enchant0_0_probability;
	// 안전인챈트 0 무기 1 -> 2 확률(%)
	static public double weapon_safe_enchant0_1_probability;
	// 안전인챈트 0 무기 2 -> 3 확률(%)
	static public double weapon_safe_enchant0_2_probability;
	// 안전인챈트 0 무기 3 -> 4 확률(%)
	static public double weapon_safe_enchant0_3_probability;
	// 안전인챈트 0 무기 4 -> 5 확률(%)
	static public double weapon_safe_enchant0_4_probability;
	// 안전인챈트 0 무기 5 -> 6 확률(%)
	static public double weapon_safe_enchant0_5_probability;
	// 안전인챈트 0 무기 6 -> 7 확률(%)
	static public double weapon_safe_enchant0_6_probability;
	// 안전인챈트 0 무기 7 -> 8 확률(%)
	static public double weapon_safe_enchant0_7_probability;
	// 안전인챈트 0 무기 8 -> 9 확률(%)
	static public double weapon_safe_enchant0_8_probability;
	// 안전인챈트 0 무기 9 이상 확률(%)
	static public double weapon_safe_enchant0_9_probability;
	
	// 안전인챈트 6 무기 6 -> 7 확률(%)
	static public double weapon_safe_enchant6_6_probability;
	// 안전인챈트 6 무기 7 -> 8 확률(%)
	static public double weapon_safe_enchant6_7_probability;
	// 안전인챈트 6 무기 8 -> 9 확률(%)
	static public double weapon_safe_enchant6_8_probability;
	// 안전인챈트 6 무기 9 이상 확률(%)
	static public double weapon_safe_enchant6_9_probability;
	
	// 안전인챈트 0 방어구 0 -> 1 확률(%)
	static public double armor_safe_enchant0_0_probability;
	// 안전인챈트 0 방어구 1 -> 2 확률(%)
	static public double armor_safe_enchant0_1_probability;
	// 안전인챈트 0 방어구 2 -> 3 확률(%)
	static public double armor_safe_enchant0_2_probability;
	// 안전인챈트 0 방어구 3 -> 4 확률(%)
	static public double armor_safe_enchant0_3_probability;
	// 안전인챈트 0 방어구 4 -> 5 확률(%)
	static public double armor_safe_enchant0_4_probability;
	// 안전인챈트 0 방어구 5 -> 6 확률(%)
	static public double armor_safe_enchant0_5_probability;
	// 안전인챈트 0 방어구 6 -> 7 확률(%)
	static public double armor_safe_enchant0_6_probability;
	// 안전인챈트 0 방어구 7 -> 8 확률(%)
	static public double armor_safe_enchant0_7_probability;
	// 안전인챈트 0 방어구 8 -> 9 확률(%)
	static public double armor_safe_enchant0_8_probability;
	// 안전인챈트 0 방어구 9 이상 확률(%)
	static public double armor_safe_enchant0_9_probability;
	
	// 안전인챈트 4 방어구 4 -> 5 확률(%)
	static public double armor_safe_enchant4_4_probability;
	// 안전인챈트 4 방어구 5 -> 6 확률(%)
	static public double armor_safe_enchant4_5_probability;
	// 안전인챈트 4 방어구 6 -> 7 확률(%)
	static public double armor_safe_enchant4_6_probability;
	// 안전인챈트 4 방어구 7 -> 8 확률(%)
	static public double armor_safe_enchant4_7_probability;
	// 안전인챈트 4 방어구 8 -> 9 확률(%)
	static public double armor_safe_enchant4_8_probability;
	// 안전인챈트 4 방어구 9 이상 확률(%)
	static public double armor_safe_enchant4_9_probability;
	
	// 안전인챈트 6 방어구 6 -> 7 확률(%)
	static public double armor_safe_enchant6_6_probability;
	// 안전인챈트 6 방어구 7 -> 8 확률(%)
	static public double armor_safe_enchant6_7_probability;
	// 안전인챈트 6 방어구 8 -> 9 확률(%)
	static public double armor_safe_enchant6_8_probability;
	// 안전인챈트 6 방어구 9 이상 확률(%)
	static public double armor_safe_enchant6_9_probability;
	
	// 장신구 0 -> 1 확률(%)
	static public double accessories_0_probability;
	// 장신구 1 -> 2 확률(%)
	static public double accessories_1_probability;
	// 장신구 2 -> 3 확률(%)
	static public double accessories_2_probability;
	// 장신구 3 -> 4 확률(%)
	static public double accessories_3_probability;
	// 장신구 4 -> 5 확률(%)
	static public double accessories_4_probability;
	// 장신구 5 -> 6 확률(%)
	static public double accessories_5_probability;
	// 장신구 6 -> 7 확률(%)
	static public double accessories_6_probability;
	// 장신구 7 -> 8 확률(%)
	static public double accessories_7_probability;
	// 장신구 8 -> 9 확률(%)
	static public double accessories_8_probability;
	// 장신구 9 이상 확률(%)
	static public double accessories_9_probability;
	// 실패시 인챈트 -1될 확률(%)
	static public double accessories_nothing_probability;
	
	// 축오림 장신구 마법 주문서 최소 인첸
	static public int bless_orim_acc_min_en;
	// 장신구 축오림 주문서 0 -> 1 확률(%)
	static public double accessories_bless_0_probability;
	// 장신구 축오림 주문서 1 -> 2 확률(%)
	static public double accessories_bless_1_probability;
	// 장신구 축오림 주문서 2 -> 3 확률(%)
	static public double accessories_bless_2_probability;
	// 장신구 축오림 주문서 3 -> 4 확률(%)
	static public double accessories_bless_3_probability;
	// 장신구 축오림 주문서 4 -> 5 확률(%)
	static public double accessories_bless_4_probability;
	// 장신구 축오림 주문서 5 -> 6 확률(%)
	static public double accessories_bless_5_probability;
	// 장신구 축오림 주문서 6 -> 7 확률(%)
	static public double accessories_bless_6_probability;
	// 장신구 축오림 주문서 7 -> 8 확률(%)
	static public double accessories_bless_7_probability;
	// 장신구 축오림 주문서 8 -> 9 확률(%)
	static public double accessories_bless_8_probability;
	// 장신구 축오림 주문서 9 이상 확률(%)
	static public double accessories_bless_9_probability;

	// 오림의 무기 마법 주문서 최소 인첸
	static public int orim_weapon_min_en;
	// 안전 인첸 0 오림의 무기 마법 주문서 0 -> 1 확률(%)
	static public double orim_weapon_0_0_probability;
	// 안전 인첸 0 오림의 무기 마법 주문서 1 -> 2 확률(%)
	static public double orim_weapon_0_1_probability;
	// 안전 인첸 0 오림의 무기 마법 주문서 2 -> 3 확률(%)
	static public double orim_weapon_0_2_probability;
	// 안전 인첸 0 오림의 무기 마법 주문서 3 -> 4 확률(%)
	static public double orim_weapon_0_3_probability;
	// 안전 인첸 0 오림의 무기 마법 주문서 4 -> 5 확률(%)
	static public double orim_weapon_0_4_probability;
	// 안전 인첸 0 오림의 무기 마법 주문서 5 -> 6 확률(%)
	static public double orim_weapon_0_5_probability;
	// 안전 인첸 0 오림의 무기 마법 주문서 6 -> 7 확률(%)
	static public double orim_weapon_0_6_probability;
	// 안전 인첸 0 오림의 무기 마법 주문서 7 -> 8 확률(%)
	static public double orim_weapon_0_7_probability;
	// 안전 인첸 0 오림의 무기 마법 주문서 8 -> 9 확률(%)
	static public double orim_weapon_0_8_probability;
	// 안전 인첸 0 오림의 무기 마법 주문서 9 이상 확률(%)
	static public double orim_weapon_0_9_probability;
	
	// 축 안전 인첸 0 오림의 무기 마법 주문서 0 -> 1 확률(%)
	static public double orim_bless_weapon_0_0_probability;
	// 축 안전 인첸 0 오림의 무기 마법 주문서 1 -> 2 확률(%)
	static public double orim_bless_weapon_0_1_probability;
	// 축 안전 인첸 0 오림의 무기 마법 주문서 2 -> 3 확률(%)
	static public double orim_bless_weapon_0_2_probability;
	// 축 안전 인첸 0 오림의 무기 마법 주문서 3 -> 4 확률(%)
	static public double orim_bless_weapon_0_3_probability;
	// 축 안전 인첸 0 오림의 무기 마법 주문서 4 -> 5 확률(%)
	static public double orim_bless_weapon_0_4_probability;
	// 축 안전 인첸 0 오림의 무기 마법 주문서 5 -> 6 확률(%)
	static public double orim_bless_weapon_0_5_probability;
	// 축 안전 인첸 0 오림의 무기 마법 주문서 6 -> 7 확률(%)
	static public double orim_bless_weapon_0_6_probability;
	// 축 안전 인첸 0 오림의 무기 마법 주문서 7 -> 8 확률(%)
	static public double orim_bless_weapon_0_7_probability;
	// 축 안전 인첸 0 오림의 무기 마법 주문서 8 -> 9 확률(%)
	static public double orim_bless_weapon_0_8_probability;
	// 축 안전 인첸 0 오림의 무기 마법 주문서 9 이상 확률(%)
	static public double orim_bless_weapon_0_9_probability;
	
	// 오림의 무기 마법 주문서 6 -> 7 확률(%)
	static public double orim_weapon_6_probability;
	// 오림의 무기 마법 주문서 7 -> 8 확률(%)
	static public double orim_weapon_7_probability;
	// 오림의 무기 마법 주문서 8 -> 9 확률(%)
	static public double orim_weapon_8_probability;
	// 오림의 무기 마법 주문서 9 -> 10 확률(%)
	static public double orim_weapon_9_probability;
	// 오림의 무기 마법 주문서 10 -> 11 확률(%)
	static public double orim_weapon_10_probability;
	// 오림의 무기 마법 주문서 11 -> 12 확률(%)
	static public double orim_weapon_11_probability;
	// 오림의 무기 마법 주문서 12 -> 13 확률(%)
	static public double orim_weapon_12_probability;
	// 오림의 무기 마법 주문서 13 -> 14 확률(%)
	static public double orim_weapon_13_probability;
	// 오림의 무기 마법 주문서 14 -> 15 확률(%)
	static public double orim_weapon_14_probability;
	// 오림의 무기 마법 주문서 15 이상 확률(%)
	static public double orim_weapon_15_probability;
	// 오림 무기 마법 주문서 실패시 -0될 확률(%)
	static public double orim_scroll_weapon_nothing_probability;
	
	// 축 오림의 무기 마법 주문서 6 -> 7 확률(%)
	static public double orim_bless_weapon_6_probability;
	// 축 오림의 무기 마법 주문서 7 -> 8 확률(%)
	static public double orim_bless_weapon_7_probability;
	// 축 오림의 무기 마법 주문서 8 -> 9 확률(%)
	static public double orim_bless_weapon_8_probability;
	// 축 오림의 무기 마법 주문서 9 -> 10 확률(%)
	static public double orim_bless_weapon_9_probability;
	// 축 오림의 무기 마법 주문서 10 -> 11 확률(%)
	static public double orim_bless_weapon_10_probability;
	// 축 오림의 무기 마법 주문서 11 -> 12 확률(%)
	static public double orim_bless_weapon_11_probability;
	// 축 오림의 무기 마법 주문서 12 -> 13 확률(%)
	static public double orim_bless_weapon_12_probability;
	// 축 오림의 무기 마법 주문서 13 -> 14 확률(%)
	static public double orim_bless_weapon_13_probability;
	// 축 오림의 무기 마법 주문서 14 -> 15 확률(%)
	static public double orim_bless_weapon_14_probability;
	// 축 오림의 무기 마법 주문서 15 이상 확률(%)
	static public double orim_bless_weapon_15_probability;
	
	// 오림의 갑옷 마법 주문서 최소 인첸
	static public int orim_armor_min_en;
	// 안전 인첸 0 오림의 방어구 마법 주문서 0 -> 1 확률(%)
	static public double orim_armor_0_0_probability;
	// 안전 인첸 0 오림의 방어구 마법 주문서 1 -> 2 확률(%)
	static public double orim_armor_0_1_probability;
	// 안전 인첸 0 오림의 방어구 마법 주문서 2 -> 3 확률(%)
	static public double orim_armor_0_2_probability;
	// 안전 인첸 0 오림의 방어구 마법 주문서 3 -> 4 확률(%)
	static public double orim_armor_0_3_probability;
	// 안전 인첸 0 오림의 방어구 마법 주문서 4 -> 5 확률(%)
	static public double orim_armor_0_4_probability;
	// 안전 인첸 0 오림의 방어구 마법 주문서 5 -> 6 확률(%)
	static public double orim_armor_0_5_probability;
	// 안전 인첸 0 오림의 방어구 마법 주문서 6 -> 7 확률(%)
	static public double orim_armor_0_6_probability;
	// 안전 인첸 0 오림의 방어구 마법 주문서 7 -> 8 확률(%)
	static public double orim_armor_0_7_probability;
	// 안전 인첸 0 오림의 방어구 마법 주문서 8 -> 9 확률(%)
	static public double orim_armor_0_8_probability;
	// 안전 인첸 0 오림의 방어구 마법 주문서 9 이상 확률(%)
	static public double orim_armor_0_9_probability;
	
	// 축 안전 인첸 0 오림의 방어구 마법 주문서 0 -> 1 확률(%)
	static public double orim_bless_armor_0_0_probability;
	// 축 안전 인첸 0 오림의 방어구 마법 주문서 1 -> 2 확률(%)
	static public double orim_bless_armor_0_1_probability;
	// 축 안전 인첸 0 오림의 방어구 마법 주문서 2 -> 3 확률(%)
	static public double orim_bless_armor_0_2_probability;
	// 축 안전 인첸 0 오림의 방어구 마법 주문서 3 -> 4 확률(%)
	static public double orim_bless_armor_0_3_probability;
	// 축 안전 인첸 0 오림의 방어구 마법 주문서 4 -> 5 확률(%)
	static public double orim_bless_armor_0_4_probability;
	// 축 안전 인첸 0 오림의 방어구 마법 주문서 5 -> 6 확률(%)
	static public double orim_bless_armor_0_5_probability;
	// 축 안전 인첸 0 오림의 방어구 마법 주문서 6 -> 7 확률(%)
	static public double orim_bless_armor_0_6_probability;
	// 축 안전 인첸 0 오림의 방어구 마법 주문서 7 -> 8 확률(%)
	static public double orim_bless_armor_0_7_probability;
	// 축 안전 인첸 0 오림의 방어구 마법 주문서 8 -> 9 확률(%)
	static public double orim_bless_armor_0_8_probability;
	// 축 안전 인첸 0 오림의 방어구 마법 주문서 9 이상 확률(%)
	static public double orim_bless_armor_0_9_probability;
	
	// 안전 인첸 4 오림의 갑옷 마법 주문서 4 -> 5 확률(%)
	static public double orim_armor_4_4_probability;
	// 안전 인첸 4 오림의 갑옷 마법 주문서 5 -> 6 확률(%)
	static public double orim_armor_4_5_probability;
	// 안전 인첸 4 오림의 갑옷 마법 주문서 6 -> 7 확률(%)
	static public double orim_armor_4_6_probability;
	// 안전 인첸 4 오림의 갑옷 마법 주문서 7 -> 8 확률(%)
	static public double orim_armor_4_7_probability;
	// 안전 인첸 4 오림의 갑옷 마법 주문서 8 -> 9 확률(%)
	static public double orim_armor_4_8_probability;
	// 안전 인첸 4 오림의 갑옷 마법 주문서 9 -> 10 확률(%)
	static public double orim_armor_4_9_probability;
	// 안전 인첸 4 오림의 갑옷 마법 주문서 10 -> 11 확률(%)
	static public double orim_armor_4_10_probability;
	// 안전 인첸 4 오림의 갑옷 마법 주문서 11 -> 12 확률(%)
	static public double orim_armor_4_11_probability;
	// 안전 인첸 4 오림의 갑옷 마법 주문서 12 -> 13 확률(%)
	static public double orim_armor_4_12_probability;
	// 안전 인첸 4 오림의 갑옷 마법 주문서 13 이상 확률(%)
	static public double orim_armor_4_13_probability;

	// 안전 인첸 4 축 오림의 갑옷 마법 주문서 4 -> 5 확률(%)
	static public double orim_bless_armor_4_4_probability;
	// 안전 인첸 4 축 오림의 갑옷 마법 주문서 5 -> 6 확률(%)
	static public double orim_bless_armor_4_5_probability;
	// 안전 인첸 4 축 오림의 갑옷 마법 주문서 6 -> 7 확률(%)
	static public double orim_bless_armor_4_6_probability;
	// 안전 인첸 4 축 오림의 갑옷 마법 주문서 7 -> 8 확률(%)
	static public double orim_bless_armor_4_7_probability;
	// 안전 인첸 4 축 오림의 갑옷 마법 주문서 8 -> 9 확률(%)
	static public double orim_bless_armor_4_8_probability;
	// 안전 인첸 4 축 오림의 갑옷 마법 주문서 9 -> 10 확률(%)
	static public double orim_bless_armor_4_9_probability;
	// 안전 인첸 4 축 오림의 갑옷 마법 주문서 10 -> 11 확률(%)
	static public double orim_bless_armor_4_10_probability;
	// 안전 인첸 4 축 오림의 갑옷 마법 주문서 11 -> 12 확률(%)
	static public double orim_bless_armor_4_11_probability;
	// 안전 인첸 4 축 오림의 갑옷 마법 주문서 12 -> 13 확률(%)
	static public double orim_bless_armor_4_12_probability;
	// 안전 인첸 4 축 오림의 갑옷 마법 주문서 13 이상 확률(%)
	static public double orim_bless_armor_4_13_probability;
	
	// 오림의 갑옷 마법 주문서 6 -> 7 확률(%)
	static public double orim_armor_6_probability;
	// 오림의 갑옷 마법 주문서 7 -> 8 확률(%)
	static public double orim_armor_7_probability;
	// 오림의 갑옷 마법 주문서 8 -> 9 확률(%)
	static public double orim_armor_8_probability;
	// 오림의 갑옷 마법 주문서 9 -> 10 확률(%)
	static public double orim_armor_9_probability;
	// 오림의 갑옷 마법 주문서 10 -> 11 확률(%)
	static public double orim_armor_10_probability;
	// 오림의 갑옷 마법 주문서 11 -> 12 확률(%)
	static public double orim_armor_11_probability;
	// 오림의 갑옷 마법 주문서 12 -> 13 확률(%)
	static public double orim_armor_12_probability;
	// 오림의 갑옷 마법 주문서 13 -> 14 확률(%)
	static public double orim_armor_13_probability;
	// 오림의 갑옷 마법 주문서 14 -> 15 확률(%)
	static public double orim_armor_14_probability;
	// 오림의 갑옷 마법 주문서 15 이상 확률(%)
	static public double orim_armor_15_probability;
	// 오림 갑옷 마법 주문서 실패시 -0될 확률(%)
	static public double orim_scroll_armor_nothing_probability;
	
	// 축 오림의 갑옷 마법 주문서 6 -> 7 확률(%)
	static public double orim_bless_armor_6_probability;
	// 축 오림의 갑옷 마법 주문서 7 -> 8 확률(%)
	static public double orim_bless_armor_7_probability;
	// 축 오림의 갑옷 마법 주문서 8 -> 9 확률(%)
	static public double orim_bless_armor_8_probability;
	// 축 오림의 갑옷 마법 주문서 9 -> 10 확률(%)
	static public double orim_bless_armor_9_probability;
	// 축 오림의 갑옷 마법 주문서 10 -> 11 확률(%)
	static public double orim_bless_armor_10_probability;
	// 축 오림의 갑옷 마법 주문서 11 -> 12 확률(%)
	static public double orim_bless_armor_11_probability;
	// 축 오림의 갑옷 마법 주문서 12 -> 13 확률(%)
	static public double orim_bless_armor_12_probability;
	// 축 오림의 갑옷 마법 주문서 13 -> 14 확률(%)
	static public double orim_bless_armor_13_probability;
	// 축 오림의 갑옷 마법 주문서 14 -> 15 확률(%)
	static public double orim_bless_armor_14_probability;
	// 축 오림의 갑옷 마법 주문서 15 이상 확률(%)
	static public double orim_bless_armor_15_probability;
	
	// +9 이상 무기 인챈트시 성공할 확률(%)
	static public double weapon_enchant_9_success_probability;
	// +9 이상 무기 인챈트시 아무일도 일어나지 않을 확률(%)
	static public double weapon_enchant_9_nothing_probability;
	// 장인의 무기마법 주문서 누적 횟수
	static public int weapon_enchant_9_use_count_1;
	// 장인의 무기마법 주문서 누적 횟수
	static public int weapon_enchant_9_use_count_2;
	// 장인의 무기마법 주문서 누적 횟수
	static public int weapon_enchant_9_use_count_3;
	// 장인의 무기마법 주문서 누적 사용횟수가 몇장 미만일 경우 확률(%)
	static public double weapon_enchant_9_use_count_1_probability;
	// 장인의 무기마법 주문서 누적 사용횟수가 몇장 미만일 경우 확률(%)
	static public double weapon_enchant_9_use_count_2_probability;
	// 장인의 무기마법 주문서 누적 사용횟수가 몇장 미만일 경우 확률(%)
	static public double weapon_enchant_9_use_count_3_probability;
	// 장인의 무기 마법 주문서 누적 사용횟수가 설정값 이상일 경우 확률(%)
	static public double weapon_enchant_9_scroll_probability;

	// 양손무기 추가 대미지
	static public double two_handsword_damage;
	
	// +7 인챈트 무기 추가 대미지 증가 배율
	static public double weapon_en_7_damage;
	// +8 인챈트 무기 추가 대미지 증가 배율
	static public double weapon_en_8_damage;
	// +9 인챈트 무기 추가 대미지 증가 배율
	static public double weapon_en_9_damage;
	// +10 인챈트 무기 추가 대미지 증가 배율
	static public double weapon_en_10_damage;
	// +11 인챈트 무기 추가 대미지 증가 배율
	static public double weapon_en_11_damage;
	// +12 인챈트 무기 추가 대미지 증가 배율
	static public double weapon_en_12_damage;
	// +13 인챈트 무기 추가 대미지 증가 배율
	static public double weapon_en_13_damage;
	// +14 인챈트 무기 추가 대미지 증가 배율
	static public double weapon_en_14_damage;
	// +15 인챈트 무기 추가 대미지 증가 배율
	static public double weapon_en_15_damage;
	
	// 몬스터 대미지 조절
	static public double monster_damage_rate;
	// 서먼 몬스터 대미지 조절
	static public double summon_damage_rate;
	// 펫 대미지 조절
	static public double pet_damage_rate;
	
	// 스턴 확률 조절
	static public double stun_percent_rate;
	
	// 안전인챈트 0 무기 0 -> 2 확률(%)
	static public double weapon_safe_enchant0_0_2_probability;
	// 안전인챈트 0 무기 0 -> 3 확률(%)
	static public double weapon_safe_enchant0_0_3_probability;
	// 안전인챈트 0 무기 1 -> 3 확률(%)
	static public double weapon_safe_enchant0_1_3_probability;
	// 안전인챈트 0 무기 1 -> 4 확률(%)
	static public double weapon_safe_enchant0_1_4_probability;
	// 안전인챈트 0 무기 2 -> 4 확률(%)
	static public double weapon_safe_enchant0_2_4_probability;
	// 안전인챈트 0 무기 2 -> 5 확률(%)
	static public double weapon_safe_enchant0_2_5_probability;
	// 안전인챈트 0 무기 3 -> 5 확률(%)
	static public double weapon_safe_enchant0_3_5_probability;
	// 안전인챈트 0 무기 3 -> 6 확률(%)
	static public double weapon_safe_enchant0_3_6_probability;
	// 안전인챈트 0 무기 4 -> 6 확률(%)
	static public double weapon_safe_enchant0_4_6_probability;
	// 안전인챈트 0 무기 4 -> 7 확률(%)
	static public double weapon_safe_enchant0_4_7_probability;
	// 안전인챈트 0 무기 5 -> 7 확률(%)
	static public double weapon_safe_enchant0_5_7_probability;
	// 안전인챈트 0 무기 5 -> 8 확률(%)
	static public double weapon_safe_enchant0_5_8_probability;
	// 안전인챈트 0 무기 6이상 2 확률(%)
	static public double weapon_safe_enchant0_6_enchant2_probability;
	// 안전인챈트 0 무기 6이상 3 확률(%)
	static public double weapon_safe_enchant0_6_enchant3_probability;
	
	// 안전인챈트 6 무기 0 -> 2 확률(%)
	static public double weapon_safe_enchant6_0_2_probability;
	// 안전인챈트 6 무기 0 -> 3 확률(%)
	static public double weapon_safe_enchant6_0_3_probability;
	// 안전인챈트 6 무기 1 -> 3 확률(%)
	static public double weapon_safe_enchant6_1_3_probability;
	// 안전인챈트 6 무기 1 -> 4 확률(%)
	static public double weapon_safe_enchant6_1_4_probability;
	// 안전인챈트 6 무기 2 -> 4 확률(%)
	static public double weapon_safe_enchant6_2_4_probability;
	// 안전인챈트 6 무기 2 -> 5 확률(%)
	static public double weapon_safe_enchant6_2_5_probability;
	// 안전인챈트 6 무기 3 -> 5 확률(%)
	static public double weapon_safe_enchant6_3_5_probability;
	// 안전인챈트 6 무기 3 -> 6 확률(%)
	static public double weapon_safe_enchant6_3_6_probability;
	// 안전인챈트 6 무기 4 -> 6 확률(%)
	static public double weapon_safe_enchant6_4_6_probability;
	// 안전인챈트 6 무기 4 -> 7 확률(%)
	static public double weapon_safe_enchant6_4_7_probability;
	// 안전인챈트 6 무기 5 -> 7 확률(%)
	static public double weapon_safe_enchant6_5_7_probability;
	// 안전인챈트 6 무기 5 -> 8 확률(%)
	static public double weapon_safe_enchant6_5_8_probability;
	// 안전인챈트 6 무기 6이상 2 확률(%)
	static public double weapon_safe_enchant6_6_enchant2_probability;
	// 안전인챈트 6 무기 6이상 3 확률(%)
	static public double weapon_safe_enchant6_6_enchant3_probability;
	
	// 안전인챈트 0 방어구 0 -> 2 확률(%)
	static public double armor_safe_enchant0_0_2_probability;
	// 안전인챈트 0 방어구 0 -> 3 확률(%)
	static public double armor_safe_enchant0_0_3_probability;
	// 안전인챈트 0 방어구 1 -> 3 확률(%)
	static public double armor_safe_enchant0_1_3_probability;
	// 안전인챈트 0 방어구 1 -> 4 확률(%)
	static public double armor_safe_enchant0_1_4_probability;
	// 안전인챈트 0 방어구 2 -> 4 확률(%)
	static public double armor_safe_enchant0_2_4_probability;
	// 안전인챈트 0 방어구 2 -> 5 확률(%)
	static public double armor_safe_enchant0_2_5_probability;
	// 안전인챈트 0 방어구 3 -> 5 확률(%)
	static public double armor_safe_enchant0_3_5_probability;
	// 안전인챈트 0 방어구 3 -> 6 확률(%)
	static public double armor_safe_enchant0_3_6_probability;
	// 안전인챈트 0 방어구 4 -> 6 확률(%)
	static public double armor_safe_enchant0_4_6_probability;
	// 안전인챈트 0 방어구 4 -> 7 확률(%)
	static public double armor_safe_enchant0_4_7_probability;
	// 안전인챈트 0 방어구 5 -> 7 확률(%)
	static public double armor_safe_enchant0_5_7_probability;
	// 안전인챈트 0 방어구 5 -> 8 확률(%)
	static public double armor_safe_enchant0_5_8_probability;
	// 안전인챈트 0 방어구 6이상 2 확률(%)
	static public double armor_safe_enchant0_6_enchant2_probability;
	// 안전인챈트 0 방어구 6이상 3 확률(%)
	static public double armor_safe_enchant0_6_enchant3_probability;
	
	// 안전인챈트 4 방어구 0 -> 2 확률(%)
	static public double armor_safe_enchant4_0_2_probability;
	// 안전인챈트 4 방어구 0 -> 3 확률(%)
	static public double armor_safe_enchant4_0_3_probability;
	// 안전인챈트 4 방어구 1 -> 3 확률(%)
	static public double armor_safe_enchant4_1_3_probability;
	// 안전인챈트 4 방어구 1 -> 4 확률(%)
	static public double armor_safe_enchant4_1_4_probability;
	// 안전인챈트 4 방어구 2 -> 4 확률(%)
	static public double armor_safe_enchant4_2_4_probability;
	// 안전인챈트 4 방어구 2 -> 5 확률(%)
	static public double armor_safe_enchant4_2_5_probability;
	// 안전인챈트 4 방어구 3 -> 5 확률(%)
	static public double armor_safe_enchant4_3_5_probability;
	// 안전인챈트 4 방어구 3 -> 6 확률(%)
	static public double armor_safe_enchant4_3_6_probability;
	// 안전인챈트 4 방어구 4 -> 6 확률(%)
	static public double armor_safe_enchant4_4_6_probability;
	// 안전인챈트 4 방어구 4 -> 7 확률(%)
	static public double armor_safe_enchant4_4_7_probability;
	// 안전인챈트 4 방어구 5 -> 7 확률(%)
	static public double armor_safe_enchant4_5_7_probability;
	// 안전인챈트 4 방어구 5 -> 8 확률(%)
	static public double armor_safe_enchant4_5_8_probability;
	// 안전인챈트 4 방어구 6이상 2 확률(%)
	static public double armor_safe_enchant4_6_enchant2_probability;
	// 안전인챈트 4 방어구 6이상 3 확률(%)
	static public double armor_safe_enchant4_6_enchant3_probability;
	
	// 안전인챈트 6 방어구 0 -> 2 확률(%)
	static public double armor_safe_enchant6_0_2_probability;
	// 안전인챈트 6 방어구 0 -> 3 확률(%)
	static public double armor_safe_enchant6_0_3_probability;
	// 안전인챈트 6 방어구 1 -> 3 확률(%)
	static public double armor_safe_enchant6_1_3_probability;
	// 안전인챈트 6 방어구 1 -> 4 확률(%)
	static public double armor_safe_enchant6_1_4_probability;
	// 안전인챈트 6 방어구 2 -> 4 확률(%)
	static public double armor_safe_enchant6_2_4_probability;
	// 안전인챈트 6 방어구 2 -> 5 확률(%)
	static public double armor_safe_enchant6_2_5_probability;
	// 안전인챈트 6 방어구 3 -> 5 확률(%)
	static public double armor_safe_enchant6_3_5_probability;
	// 안전인챈트 6 방어구 3 -> 6 확률(%)
	static public double armor_safe_enchant6_3_6_probability;
	// 안전인챈트 6 방어구 4 -> 6 확률(%)
	static public double armor_safe_enchant6_4_6_probability;
	// 안전인챈트 6 방어구 4 -> 7 확률(%)
	static public double armor_safe_enchant6_4_7_probability;
	// 안전인챈트 6 방어구 5 -> 7 확률(%)
	static public double armor_safe_enchant6_5_7_probability;
	// 안전인챈트 6 방어구 5 -> 8 확률(%)
	static public double armor_safe_enchant6_5_8_probability;
	// 안전인챈트 6 방어구 6이상 2 확률(%)
	static public double armor_safe_enchant6_6_enchant2_probability;
	// 안전인챈트 6 방어구 6이상 3 확률(%)
	static public double armor_safe_enchant6_6_enchant3_probability;
	
	// 인형 진화 주문서 확률
	static public double doll_upgrade_percent;
	
	
	static public boolean dmg_limit;
	static public boolean dmg_limit_out;
	static public int dmg_limit_count;
	static public int dmg_limit_sturn;
	
	static public int royalmaxdmg;
	static public int knightmaxdmg;
	static public int elfmaxdmg;
	static public int wizardmaxdmg;
	
	static public int royalskillmaxdmg;
	static public int knightskillmaxdmg;
	static public int elfskillmaxdmg;
	static public int wizardskillmaxdmg;
	
	
	// 일반 변신카드 합성
	static public double polycard_1_probability;
	// 일반 변신카드 합성 대성공 확률
	static public double polycard_1_perfect_probability;
	// 고급 변신카드 합성 확률
	static public double polycard_2_probability;
	// 고급 변신카드 합성 대성공 확률
	static public double polycard_2_perfect_probability;
	// 희귀 변신카드 합성 확률
	static public double polycard_3_probability;
	// 영웅 변신카드 합성 확률
	static public double polycard_4_probability;
	// 전설 변신카드 합성 확률
	static public double polycard_5_probability;
	// 신화 변신카드 합성 확률
	static public double polycard_6_probability;
	
	/**
	 * 리니지 밸런스에 사용되는 변수 초기화.
	 * 2017-10-05
	 * by all-night
	 */
	static public void init() {
		TimeLine.start("Lineage_Balance..");
		String line = null;
		
		try {
			BufferedReader lnrr = new BufferedReader(new FileReader("lineage_balance.conf"));
			while ((line = lnrr.readLine()) != null) {
				if (line.startsWith("#"))
					continue;

				int pos = line.indexOf("=");
				if (pos > 0) {
					String key = line.substring(0, pos).trim();
					String value = line.substring(pos + 1, line.length()).trim();
					
					if (value.contains("%"))
						value = value.replace("%", "");

					if (key.equalsIgnoreCase("monster_level_min_damage_rate"))
						monster_level_min_damage_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("monster_level_max_damage_rate"))
						monster_level_max_damage_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("monster_hit_rate"))
						monster_hit_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("monster_bow_hit_rate"))
						monster_bow_hit_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("is_boss_monster_mr_dmg"))
						is_boss_monster_mr_dmg = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("monster_hp_rate"))
						monster_hp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("monster_mp_rate"))
						monster_mp_rate = Double.valueOf(value);		
					else if (key.equalsIgnoreCase("summon_level_min_damage_rate"))
						summon_level_min_damage_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("summon_level_max_damage_rate"))
						summon_level_max_damage_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("pet_level_min_damage_rate"))
						pet_level_min_damage_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("pet_level_max_damage_rate"))
						pet_level_max_damage_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("summon_hit_rate"))
						summon_hit_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("summon_bow_hit_rate"))
						summon_bow_hit_rate = Double.valueOf(value);
					
					else if (key.equalsIgnoreCase("faust_spawn_msg"))
						faust_spawn_msg = value.equalsIgnoreCase("true");		
					else if (key.equalsIgnoreCase("faust_spawn_probability"))
						faust_spawn_probability = Double.valueOf(value) * 0.01;	
					
					else if (key.equalsIgnoreCase("event_b_spawn_probability"))
						event_b_spawn_probability = Double.valueOf(value) * 0.01;	
					else if (key.equalsIgnoreCase("event_b2_spawn_probability"))
						event_b2_spawn_probability = Double.valueOf(value) * 0.01;	
					else if (key.equalsIgnoreCase("event_a_spawn_probability"))
						event_a_spawn_probability = Double.valueOf(value) * 0.01;	
					else if (key.equalsIgnoreCase("event_a2_spawn_probability"))
						event_a2_spawn_probability = Double.valueOf(value) * 0.01;	
					else if (key.equalsIgnoreCase("event_s_spawn_probability"))
						event_s_spawn_probability = Double.valueOf(value) * 0.01;	
					else if (key.equalsIgnoreCase("event_s2_spawn_probability"))
						event_s2_spawn_probability = Double.valueOf(value) * 0.01;	
					else if (key.equalsIgnoreCase("grimreaper_spawn_msg"))
						grimreaper_spawn_msg = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("grimreaper_spawn_probability"))
						grimreaper_spawn_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("oman_spawn_msg"))
						oman_spawn_msg = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("oman_spawn_probability"))
						oman_spawn_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("oman_spawn_hp_min"))
						oman_spawn_hp_min = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("oman_spawn_hp_max"))
						oman_spawn_hp_max = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("magicDoll_class_1_probability"))
						magicDoll_class_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("magicDoll_class_1_perfect_probability"))
						magicDoll_class_1_perfect_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("magicDoll_class_2_probability"))
						magicDoll_class_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("magicDoll_class_2_perfect_probability"))
						magicDoll_class_2_perfect_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("magicDoll_class_3_probability"))
						magicDoll_class_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("magicDoll_class_3_perfect_probability"))
						magicDoll_class_3_perfect_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("magicDoll_class_4_probability"))
						magicDoll_class_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("magicDoll_class_5_probability"))
						magicDoll_class_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("magicDoll_class_6_probability"))
						magicDoll_class_6_probability = Double.valueOf(value) * 0.01;	
					
					
					else if (key.equalsIgnoreCase("polycard_1_probability"))
						polycard_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("polycard_1_perfect_probability"))
						polycard_1_perfect_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("polycard_2_probability"))
						polycard_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("polycard_2_perfect_probability"))
						polycard_2_perfect_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("polycard_3_probability"))
						polycard_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("polycard_4_probability"))
						polycard_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("polycard_5_probability"))
						polycard_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("polycard_6_probability"))
						polycard_6_probability = Double.valueOf(value) * 0.01;
		
					
					
					else if (key.equalsIgnoreCase("royal_damage_figure"))
						royal_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_damage_figure"))
						knight_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_damage_figure"))
						elf_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_damage_figure"))
						darkelf_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_damage_figure"))
						wizard_damage_figure = Double.valueOf(value);	
					else if (key.equalsIgnoreCase("royal_hit_figure"))
						royal_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_hit_figure"))
						knight_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_hit_figure"))
						elf_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_hit_figure"))
						darkelf_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_hit_figure"))
						wizard_hit_figure = Double.valueOf(value);		
					else if (key.equalsIgnoreCase("royal_critical_figure"))
						royal_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_critical_figure"))
						knight_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_critical_figure"))
						elf_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_critical_figure"))
						darkelf_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_critical_figure"))
						wizard_critical_figure = Double.valueOf(value);			
					else if (key.equalsIgnoreCase("royal_bow_damage_figure"))
						royal_bow_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_bow_damage_figure"))
						knight_bow_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_bow_damage_figure"))
						elf_bow_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_bow_damage_figure"))
						darkelf_bow_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_bow_damage_figure"))
						wizard_bow_damage_figure = Double.valueOf(value);	
					else if (key.equalsIgnoreCase("royal_bow_hit_figure"))
						royal_bow_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_bow_hit_figure"))
						knight_bow_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_bow_hit_figure"))
						elf_bow_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_bow_hit_figure"))
						darkelf_bow_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_bow_hit_figure"))
						wizard_bow_hit_figure = Double.valueOf(value);		
					else if (key.equalsIgnoreCase("royal_bow_critical_figure"))
						royal_bow_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_bow_critical_figure"))
						knight_bow_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_bow_critical_figure"))
						elf_bow_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_bow_critical_figure"))
						darkelf_bow_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_bow_critical_figure"))
						wizard_bow_critical_figure = Double.valueOf(value);			
					else if (key.equalsIgnoreCase("royal_magic_damage_figure"))
						royal_magic_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_magic_damage_figure"))
						knight_magic_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_magic_damage_figure"))
						elf_magic_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_magic_damage_figure"))
						darkelf_magic_damage_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_magic_damage_figure"))
						wizard_magic_damage_figure = Double.valueOf(value);	
					else if (key.equalsIgnoreCase("royal_magic_hit_figure"))
						royal_magic_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_magic_hit_figure"))
						knight_magic_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_magic_hit_figure"))
						elf_magic_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_magic_hit_figure"))
						darkelf_magic_hit_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_magic_hit_figure"))
						wizard_magic_hit_figure = Double.valueOf(value);		
					else if (key.equalsIgnoreCase("royal_magic_critical_figure"))
						royal_magic_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_magic_critical_figure"))
						knight_magic_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_magic_critical_figure"))
						elf_magic_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_magic_critical_figure"))
						darkelf_magic_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_magic_critical_figure"))
						elf_magic_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_magic_critical_figure"))
						wizard_magic_critical_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("royal_magic_bonus_figure"))
						royal_magic_bonus_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_magic_bonus_figure"))
						knight_magic_bonus_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_magic_bonus_figure"))
						elf_magic_bonus_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_magic_bonus_figure"))
						darkelf_magic_bonus_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_magic_bonus_figure"))
						wizard_magic_bonus_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("mr_low_damage_reduce"))
						mr_low_damage_reduce = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("mr_high_damage_reduce"))
						mr_high_damage_reduce = Double.valueOf(value) * 0.01;			
					else if (key.equalsIgnoreCase("royal_magic_final_damage_figure"))
						royal_magic_final_damage_figure = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("knight_magic_final_damage_figure"))
						knight_magic_final_damage_figure = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("elf_magic_final_damage_figure"))
						elf_magic_final_damage_figure = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("darkelf_magic_final_damage_figure"))
						darkelf_magic_final_damage_figure = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("wizard_magic_final_damage_figure"))
						wizard_magic_final_damage_figure = Double.valueOf(value) * 0.01;			
					else if (key.equalsIgnoreCase("royal_magic_final_hit_figure"))
						royal_magic_final_hit_figure = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("knight_magic_final_hit_figure"))
						knight_magic_final_hit_figure = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("elf_magic_final_hit_figure"))
						elf_magic_final_hit_figure = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("darkelf_magic_final_hit_figure"))
						darkelf_magic_final_hit_figure = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("wizard_magic_final_hit_figure"))
						wizard_magic_final_hit_figure = Double.valueOf(value) * 0.01;					
					else if (key.equalsIgnoreCase("royal_hp_tic_figure"))
						royal_hp_tic_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_hp_tic_figure"))
						knight_hp_tic_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_hp_tic_figure"))
						elf_hp_tic_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_hp_tic_figure"))
						darkelf_hp_tic_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_hp_tic_figure"))
						wizard_hp_tic_figure = Double.valueOf(value);					
					else if (key.equalsIgnoreCase("royal_mp_tic_figure"))
						royal_mp_tic_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("knight_mp_tic_figure"))
						knight_mp_tic_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("elf_mp_tic_figure"))
						elf_mp_tic_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_mp_tic_figure"))
						darkelf_mp_tic_figure = Double.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_mp_tic_figure"))
						wizard_mp_tic_figure = Double.valueOf(value);				
					else if (key.equalsIgnoreCase("pc_hit_rate"))
						pc_hit_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("pc_hit_rate2"))
						pc_hit_rate2 = Double.valueOf(value);
					else if (key.equalsIgnoreCase("pc_hit_rate3"))
						pc_hit_rate3 = Double.valueOf(value);
					else if (key.equalsIgnoreCase("pc_hit_rate4"))
						pc_hit_rate4 = Double.valueOf(value);
					else if (key.equalsIgnoreCase("pc_hit_rate5"))
						pc_hit_rate5 = Double.valueOf(value);
					else if (key.equalsIgnoreCase("pc_bow_hit_rate"))
						pc_bow_hit_rate = Double.valueOf(value);	
					else if (key.equalsIgnoreCase("magicDoll_black_elder_min_damage"))
						magicDoll_black_elder_min_damage = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("magicDoll_black_elder_max_damage"))
						magicDoll_black_elder_max_damage = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("magicDoll_death_knight_min_damage"))
						magicDoll_death_knight_min_damage = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("magicDoll_death_knight_max_damage"))
						magicDoll_death_knight_max_damage = Integer.valueOf(value);
					
					else if (key.equalsIgnoreCase("immuneToHarmReduction"))
						immuneToHarmReduction = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("immuneToHarmReduction2"))
						immuneToHarmReduction2 = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("ipReduction"))
						ipReduction = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("drReduction"))
						drReduction = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("is_fusion_attack"))
						is_fusion_attack = value.equalsIgnoreCase("true");	
					
					else if (key.equalsIgnoreCase("bless_change_probability1"))
						bless_change_probability1 = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("bless_change_probability2"))
						bless_change_probability2 = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("bless_change_probability3"))
						bless_change_probability3 = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("bless_change_probability4"))
						bless_change_probability4 = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("bless_change_probability5"))
						bless_change_probability5 = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("bless_change_probability6"))
						bless_change_probability6 = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_0_probability"))
						weapon_safe_enchant0_0_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_1_probability"))
						weapon_safe_enchant0_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_2_probability"))
						weapon_safe_enchant0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_3_probability"))
						weapon_safe_enchant0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_4_probability"))
						weapon_safe_enchant0_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_5_probability"))
						weapon_safe_enchant0_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_6_probability"))
						weapon_safe_enchant0_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_7_probability"))
						weapon_safe_enchant0_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_8_probability"))
						weapon_safe_enchant0_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_9_probability"))
						weapon_safe_enchant0_9_probability = Double.valueOf(value) * 0.01;			
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_6_probability"))
						weapon_safe_enchant6_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_7_probability"))
						weapon_safe_enchant6_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_8_probability"))
						weapon_safe_enchant6_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_9_probability"))
						weapon_safe_enchant6_9_probability = Double.valueOf(value) * 0.01;					
					else if (key.equalsIgnoreCase("weapon_enchant_9_success_probability"))
						weapon_enchant_9_success_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_enchant_9_nothing_probability"))
						weapon_enchant_9_nothing_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_enchant_9_scroll_probability"))
						weapon_enchant_9_scroll_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_0_probability"))
						armor_safe_enchant0_0_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_1_probability"))
						armor_safe_enchant0_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_2_probability"))
						armor_safe_enchant0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_3_probability"))
						armor_safe_enchant0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_4_probability"))
						armor_safe_enchant0_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_5_probability"))
						armor_safe_enchant0_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_6_probability"))
						armor_safe_enchant0_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_7_probability"))
						armor_safe_enchant0_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_8_probability"))
						armor_safe_enchant0_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_9_probability"))
						armor_safe_enchant0_9_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant4_4_probability"))
						armor_safe_enchant4_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_5_probability"))
						armor_safe_enchant4_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_6_probability"))
						armor_safe_enchant4_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_7_probability"))
						armor_safe_enchant4_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_8_probability"))
						armor_safe_enchant4_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_9_probability"))
						armor_safe_enchant4_9_probability = Double.valueOf(value) * 0.01;		
					else if (key.equalsIgnoreCase("armor_safe_enchant6_6_probability"))
						armor_safe_enchant6_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_7_probability"))
						armor_safe_enchant6_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_8_probability"))
						armor_safe_enchant6_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_9_probability"))
						armor_safe_enchant6_9_probability = Double.valueOf(value) * 0.01;					
					else if (key.equalsIgnoreCase("accessories_0_probability"))
						accessories_0_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_1_probability"))
						accessories_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_2_probability"))
						accessories_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_3_probability"))
						accessories_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_4_probability"))
						accessories_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_5_probability"))
						accessories_5_probability = Double.valueOf(value) * 0.01;		
					else if (key.equalsIgnoreCase("accessories_6_probability"))
						accessories_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_7_probability"))
						accessories_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_8_probability"))
						accessories_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_9_probability"))
						accessories_9_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_nothing_probability"))
						accessories_nothing_probability	 = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("is_this_inti_greate_damage"))
						is_this_inti_greate_damage = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("this_inti_greate_time"))
						this_inti_greate_time = Double.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("this_inti_greate_reduction"))
						this_inti_greate_reduction = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("is_meteor_strike_damage"))
						is_meteor_strike_damage = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("meteor_strike_time"))
						meteor_strike_time = Double.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("meteor_strike_reduction"))
						meteor_strike_reduction = Double.valueOf(value) * 0.01;			
					else if (key.equalsIgnoreCase("is_stun_twohandsword"))
						is_stun_twohandsword = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_heal_damage"))
						is_heal_damage = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("heal_time"))
						heal_time = Double.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("heal_reduction"))
						heal_reduction = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("heal_all_rate"))
						heal_all_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("blessing_rate"))
						blessing_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("triple_arrow_damage"))
						triple_arrow_damage = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("triple_damage_reduction_time"))
						triple_damage_reduction_time = Double.valueOf(value) * 1000;					
					else if (key.equalsIgnoreCase("triple_damage_reduction"))
						triple_damage_reduction = Double.valueOf(value) * 0.01;					
					else if (key.equalsIgnoreCase("two_handsword_damage"))
						two_handsword_damage = Double.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_en_7_damage"))
						weapon_en_7_damage = Double.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_en_8_damage"))
						weapon_en_8_damage = Double.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_en_9_damage"))
						weapon_en_9_damage = Double.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_en_10_damage"))
						weapon_en_10_damage = Double.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_en_11_damage"))
						weapon_en_11_damage = Double.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_en_12_damage"))
						weapon_en_12_damage = Double.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_en_13_damage"))
						weapon_en_13_damage = Double.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_en_14_damage"))
						weapon_en_14_damage = Double.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_en_15_damage"))
						weapon_en_15_damage = Double.valueOf(value);			
					else if (key.equalsIgnoreCase("magic_probability"))
						magic_probability = Integer.valueOf(value);
	
					else if (key.equalsIgnoreCase("silence_probability"))
						silence_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("decay_potion_probability"))
						decay_potion_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("turn_undead_elf_probability"))
						turn_undead_elf_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("turn_undead_wizard_probability"))
						turn_undead_wizard_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("curseParalyze_elf_probability"))
						curseParalyze_elf_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("curseParalyze_wizard_probability"))
						curseParalyze_wizard_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("cancellation_elf_probability"))
						cancellation_elf_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("cancellation_wizard_probability"))
						cancellation_wizard_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("count_barrier_knight"))
						count_barrier_knight = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("count_barrier_elf"))
						count_barrier_elf = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("striker_gale"))
						striker_gale = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("earth_bind"))
						earth_bind = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("pollute_watar"))
						pollute_watar = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("entangle"))
						entangle = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("area_of_silence"))
						area_of_silence = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("level_up_hp_royal"))
						level_up_hp_royal = Double.valueOf(value);
					else if (key.equalsIgnoreCase("level_up_hp_knight"))
						level_up_hp_knight = Double.valueOf(value);
					else if (key.equalsIgnoreCase("level_up_hp_elf"))
						level_up_hp_elf = Double.valueOf(value);
					else if (key.equalsIgnoreCase("level_up_hp_darkelf"))
						level_up_hp_darkelf = Double.valueOf(value);
					else if (key.equalsIgnoreCase("level_up_hp_wizard"))
						level_up_hp_wizard = Double.valueOf(value);			
					else if (key.equalsIgnoreCase("level_up_mp_royal"))
						level_up_mp_royal = Double.valueOf(value);
					else if (key.equalsIgnoreCase("level_up_mp_knight"))
						level_up_mp_knight = Double.valueOf(value);
					else if (key.equalsIgnoreCase("level_up_mp_elf"))
						level_up_mp_elf = Double.valueOf(value);
					else if (key.equalsIgnoreCase("level_up_mp_darkelf"))
						level_up_mp_darkelf = Double.valueOf(value);
					else if (key.equalsIgnoreCase("level_up_mp_wizard"))
						level_up_mp_wizard = Double.valueOf(value);		
					else if (key.equalsIgnoreCase("monster_damage_rate"))
						monster_damage_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("summon_damage_rate"))
						summon_damage_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("pet_damage_rate"))
						pet_damage_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("pc_hit_ac_royal_percent"))
						pc_hit_ac_royal_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("pc_hit_ac_knight_percent"))
						pc_hit_ac_knight_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("pc_hit_ac_elf_percent"))
						pc_hit_ac_elf_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("pc_hit_ac_darkelf_percent"))
						pc_hit_ac_darkelf_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("pc_hit_ac_wizard_percent"))
						pc_hit_ac_wizard_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("pc_bow_hit_ac_royal_percent"))
						pc_bow_hit_ac_royal_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("pc_bow_hit_ac_knight_percent"))
						pc_bow_hit_ac_knight_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("pc_bow_hit_ac_elf_percent"))
						pc_bow_hit_ac_elf_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("pc_bow_hit_ac_darkelf_percent"))
						pc_bow_hit_ac_darkelf_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("pc_bow_hit_ac_wizard_percent"))
						pc_bow_hit_ac_wizard_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("stun_percent_rate"))
						stun_percent_rate = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("ROYAL_dmg"))
						ROYAL_dmg = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("KNIGHT_dmg"))
						KNIGHT_dmg = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("ELF_dmg"))
						ELF_dmg = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("darkElf_dmg"))
						darkElf_dmg = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("WIZARD_dmg"))
						WIZARD_dmg = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("bless_orim_acc_min_en"))
						bless_orim_acc_min_en = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("accessories_bless_0_probability"))
						accessories_bless_0_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_bless_1_probability"))
						accessories_bless_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_bless_2_probability"))
						accessories_bless_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_bless_3_probability"))
						accessories_bless_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_bless_4_probability"))
						accessories_bless_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_bless_5_probability"))
						accessories_bless_5_probability = Double.valueOf(value) * 0.01;		
					else if (key.equalsIgnoreCase("accessories_bless_6_probability"))
						accessories_bless_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_bless_7_probability"))
						accessories_bless_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_bless_8_probability"))
						accessories_bless_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("accessories_bless_9_probability"))
						accessories_bless_9_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("weapon_enchant_9_use_count_1"))
						weapon_enchant_9_use_count_1 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_enchant_9_use_count_2"))
						weapon_enchant_9_use_count_2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_enchant_9_use_count_3"))
						weapon_enchant_9_use_count_3 = Integer.valueOf(value);					
					else if (key.equalsIgnoreCase("weapon_enchant_9_use_count_1_probability"))
						weapon_enchant_9_use_count_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_enchant_9_use_count_2_probability"))
						weapon_enchant_9_use_count_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_enchant_9_use_count_3_probability"))
						weapon_enchant_9_use_count_3_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_0_2_probability"))
						weapon_safe_enchant0_0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_0_3_probability"))
						weapon_safe_enchant0_0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_1_3_probability"))
						weapon_safe_enchant0_1_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_1_4_probability"))
						weapon_safe_enchant0_1_4_probability = Double.valueOf(value) * 0.01;			
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_2_4_probability"))
						weapon_safe_enchant0_2_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_2_5_probability"))
						weapon_safe_enchant0_2_5_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_3_5_probability"))
						weapon_safe_enchant0_3_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_3_6_probability"))
						weapon_safe_enchant0_3_6_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_4_6_probability"))
						weapon_safe_enchant0_4_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_4_7_probability"))
						weapon_safe_enchant0_4_7_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_5_7_probability"))
						weapon_safe_enchant0_5_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_5_8_probability"))
						weapon_safe_enchant0_5_8_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_6_enchant2_probability"))
						weapon_safe_enchant0_6_enchant2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant0_6_enchant3_probability"))
						weapon_safe_enchant0_6_enchant3_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_0_2_probability"))
						weapon_safe_enchant6_0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_0_3_probability"))
						weapon_safe_enchant6_0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_1_3_probability"))
						weapon_safe_enchant6_1_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_1_4_probability"))
						weapon_safe_enchant6_1_4_probability = Double.valueOf(value) * 0.01;			
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_2_4_probability"))
						weapon_safe_enchant6_2_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_2_5_probability"))
						weapon_safe_enchant6_2_5_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_3_5_probability"))
						weapon_safe_enchant6_3_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_3_6_probability"))
						weapon_safe_enchant6_3_6_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_4_6_probability"))
						weapon_safe_enchant6_4_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_4_7_probability"))
						weapon_safe_enchant6_4_7_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_5_7_probability"))
						weapon_safe_enchant6_5_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_5_8_probability"))
						weapon_safe_enchant6_5_8_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_6_enchant2_probability"))
						weapon_safe_enchant6_6_enchant2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("weapon_safe_enchant6_6_enchant3_probability"))
						weapon_safe_enchant6_6_enchant3_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("armor_safe_enchant0_0_2_probability"))
						armor_safe_enchant0_0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_0_3_probability"))
						armor_safe_enchant0_0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_1_3_probability"))
						armor_safe_enchant0_1_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_1_4_probability"))
						armor_safe_enchant0_1_4_probability = Double.valueOf(value) * 0.01;			
					else if (key.equalsIgnoreCase("armor_safe_enchant0_2_4_probability"))
						armor_safe_enchant0_2_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_2_5_probability"))
						armor_safe_enchant0_2_5_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant0_3_5_probability"))
						armor_safe_enchant0_3_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_3_6_probability"))
						armor_safe_enchant0_3_6_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant0_4_6_probability"))
						armor_safe_enchant0_4_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_4_7_probability"))
						armor_safe_enchant0_4_7_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant0_5_7_probability"))
						armor_safe_enchant0_5_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_5_8_probability"))
						armor_safe_enchant0_5_8_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant0_6_enchant2_probability"))
						armor_safe_enchant0_6_enchant2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant0_6_enchant3_probability"))
						armor_safe_enchant0_6_enchant3_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("armor_safe_enchant4_0_2_probability"))
						armor_safe_enchant4_0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_0_3_probability"))
						armor_safe_enchant4_0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_1_3_probability"))
						armor_safe_enchant4_1_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_1_4_probability"))
						armor_safe_enchant4_1_4_probability = Double.valueOf(value) * 0.01;			
					else if (key.equalsIgnoreCase("armor_safe_enchant4_2_4_probability"))
						armor_safe_enchant4_2_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_2_5_probability"))
						armor_safe_enchant4_2_5_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant4_3_5_probability"))
						armor_safe_enchant4_3_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_3_6_probability"))
						armor_safe_enchant4_3_6_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant4_4_6_probability"))
						armor_safe_enchant4_4_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_4_7_probability"))
						armor_safe_enchant4_4_7_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant4_5_7_probability"))
						armor_safe_enchant4_5_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_5_8_probability"))
						armor_safe_enchant4_5_8_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant4_6_enchant2_probability"))
						armor_safe_enchant4_6_enchant2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant4_6_enchant3_probability"))
						armor_safe_enchant4_6_enchant3_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("armor_safe_enchant6_0_2_probability"))
						armor_safe_enchant6_0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_0_3_probability"))
						armor_safe_enchant6_0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_1_3_probability"))
						armor_safe_enchant6_1_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_1_4_probability"))
						armor_safe_enchant6_1_4_probability = Double.valueOf(value) * 0.01;			
					else if (key.equalsIgnoreCase("armor_safe_enchant6_2_4_probability"))
						armor_safe_enchant6_2_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_2_5_probability"))
						armor_safe_enchant6_2_5_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant6_3_5_probability"))
						armor_safe_enchant6_3_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_3_6_probability"))
						armor_safe_enchant6_3_6_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant6_4_6_probability"))
						armor_safe_enchant6_4_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_4_7_probability"))
						armor_safe_enchant6_4_7_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant6_5_7_probability"))
						armor_safe_enchant6_5_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_5_8_probability"))
						armor_safe_enchant6_5_8_probability = Double.valueOf(value) * 0.01;				
					else if (key.equalsIgnoreCase("armor_safe_enchant6_6_enchant2_probability"))
						armor_safe_enchant6_6_enchant2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("armor_safe_enchant6_6_enchant3_probability"))
						armor_safe_enchant6_6_enchant3_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("orim_weapon_min_en"))
						orim_weapon_min_en = Integer.valueOf(value);				
					else if (key.equalsIgnoreCase("orim_weapon_0_0_probability"))
						orim_weapon_0_0_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_0_1_probability"))
						orim_weapon_0_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_0_2_probability"))
						orim_weapon_0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_0_3_probability"))
						orim_weapon_0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_0_4_probability"))
						orim_weapon_0_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_0_5_probability"))
						orim_weapon_0_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_0_6_probability"))
						orim_weapon_0_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_0_7_probability"))
						orim_weapon_0_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_0_8_probability"))
						orim_weapon_0_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_0_9_probability"))
						orim_weapon_0_9_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("orim_bless_weapon_0_0_probability"))
						orim_bless_weapon_0_0_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_0_1_probability"))
						orim_bless_weapon_0_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_0_2_probability"))
						orim_bless_weapon_0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_0_3_probability"))
						orim_bless_weapon_0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_0_4_probability"))
						orim_bless_weapon_0_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_0_5_probability"))
						orim_bless_weapon_0_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_0_6_probability"))
						orim_bless_weapon_0_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_0_7_probability"))
						orim_bless_weapon_0_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_0_8_probability"))
						orim_bless_weapon_0_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_0_9_probability"))
						orim_bless_weapon_0_9_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("orim_weapon_6_probability"))
						orim_weapon_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_7_probability"))
						orim_weapon_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_8_probability"))
						orim_weapon_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_9_probability"))
						orim_weapon_9_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_10_probability"))
						orim_weapon_10_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_11_probability"))
						orim_weapon_11_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_12_probability"))
						orim_weapon_12_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_13_probability"))
						orim_weapon_13_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_14_probability"))
						orim_weapon_14_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_weapon_15_probability"))
						orim_weapon_15_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_scroll_weapon_nothing_probability"))
						orim_scroll_weapon_nothing_probability = Double.valueOf(value) * 0.01;	
					
					else if (key.equalsIgnoreCase("orim_bless_weapon_6_probability"))
						orim_bless_weapon_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_7_probability"))
						orim_bless_weapon_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_8_probability"))
						orim_bless_weapon_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_9_probability"))
						orim_bless_weapon_9_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_10_probability"))
						orim_bless_weapon_10_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_11_probability"))
						orim_bless_weapon_11_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_12_probability"))
						orim_bless_weapon_12_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_13_probability"))
						orim_bless_weapon_13_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_14_probability"))
						orim_bless_weapon_14_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_weapon_15_probability"))
						orim_bless_weapon_15_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("orim_armor_min_en"))
						orim_armor_min_en = Integer.valueOf(value);		
					else if (key.equalsIgnoreCase("orim_armor_0_0_probability"))
						orim_armor_0_0_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_0_1_probability"))
						orim_armor_0_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_0_2_probability"))
						orim_armor_0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_0_3_probability"))
						orim_armor_0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_0_4_probability"))
						orim_armor_0_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_0_5_probability"))
						orim_armor_0_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_0_6_probability"))
						orim_armor_0_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_0_7_probability"))
						orim_armor_0_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_0_8_probability"))
						orim_armor_0_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_0_9_probability"))
						orim_armor_0_9_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("orim_bless_armor_0_0_probability"))
						orim_bless_armor_0_0_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_0_1_probability"))
						orim_bless_armor_0_1_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_0_2_probability"))
						orim_bless_armor_0_2_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_0_3_probability"))
						orim_bless_armor_0_3_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_0_4_probability"))
						orim_bless_armor_0_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_0_5_probability"))
						orim_bless_armor_0_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_0_6_probability"))
						orim_bless_armor_0_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_0_7_probability"))
						orim_bless_armor_0_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_0_8_probability"))
						orim_bless_armor_0_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_0_9_probability"))
						orim_bless_armor_0_9_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("orim_armor_4_4_probability"))
						orim_armor_4_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_4_5_probability"))
						orim_armor_4_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_4_6_probability"))
						orim_armor_4_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_4_7_probability"))
						orim_armor_4_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_4_8_probability"))
						orim_armor_4_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_4_9_probability"))
						orim_armor_4_9_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_4_10_probability"))
						orim_armor_4_10_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_4_11_probability"))
						orim_armor_4_11_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_4_12_probability"))
						orim_armor_4_12_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_4_13_probability"))
						orim_armor_4_13_probability = Double.valueOf(value) * 0.01;	
					
					else if (key.equalsIgnoreCase("orim_bless_armor_4_4_probability"))
						orim_bless_armor_4_4_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_4_5_probability"))
						orim_bless_armor_4_5_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_4_6_probability"))
						orim_bless_armor_4_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_4_7_probability"))
						orim_bless_armor_4_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_4_8_probability"))
						orim_bless_armor_4_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_4_9_probability"))
						orim_bless_armor_4_9_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_4_10_probability"))
						orim_bless_armor_4_10_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_4_11_probability"))
						orim_bless_armor_4_11_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_4_12_probability"))
						orim_bless_armor_4_12_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_4_13_probability"))
						orim_bless_armor_4_13_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("orim_armor_6_probability"))
						orim_armor_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_7_probability"))
						orim_armor_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_8_probability"))
						orim_armor_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_9_probability"))
						orim_armor_9_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_10_probability"))
						orim_armor_10_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_11_probability"))
						orim_armor_11_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_12_probability"))
						orim_armor_12_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_13_probability"))
						orim_armor_13_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_14_probability"))
						orim_armor_14_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_armor_15_probability"))
						orim_armor_15_probability = Double.valueOf(value) * 0.01;	
					else if (key.equalsIgnoreCase("orim_scroll_armor_nothing_probability"))
						orim_scroll_armor_nothing_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("orim_bless_armor_6_probability"))
						orim_bless_armor_6_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_7_probability"))
						orim_bless_armor_7_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_8_probability"))
						orim_bless_armor_8_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_9_probability"))
						orim_bless_armor_9_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_10_probability"))
						orim_bless_armor_10_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_11_probability"))
						orim_bless_armor_11_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_12_probability"))
						orim_bless_armor_12_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_13_probability"))
						orim_bless_armor_13_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_14_probability"))
						orim_bless_armor_14_probability = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("orim_bless_armor_15_probability"))
						orim_bless_armor_15_probability = Double.valueOf(value) * 0.01;
					
					else if (key.equalsIgnoreCase("doll_upgrade_percent"))
						doll_upgrade_percent = Double.valueOf(value) * 0.01;

					else if (key.equalsIgnoreCase("is_critical"))
						is_critical = value.equalsIgnoreCase("true");	
					else if (key.equalsIgnoreCase("weapon_critical_persent"))
						weapon_critical_persent = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("critical_Min_Dmg"))
						critical_Min_Dmg = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("critical_Max_Dmg"))
						critical_Max_Dmg = Integer.valueOf(value);
					
					else if (key.equalsIgnoreCase("dmg_limit"))
						dmg_limit = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("dmg_limit_out"))
						dmg_limit_out = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("dmg_limit_count"))
						dmg_limit_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dmg_limit_sturn"))
						dmg_limit_sturn = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royalmaxdmg"))
						royalmaxdmg = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knightmaxdmg"))
						knightmaxdmg = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elfmaxdmg"))
						elfmaxdmg = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizardmaxdmg"))
						wizardmaxdmg = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royalskillmaxdmg"))
						royalskillmaxdmg = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knightskillmaxdmg"))
						knightskillmaxdmg = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elfskillmaxdmg"))
						elfskillmaxdmg = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizardskillmaxdmg"))
						wizardskillmaxdmg = Integer.valueOf(value);	
					
					else if (key.equalsIgnoreCase("am_probability"))
						am_probability = Double.valueOf(value) * 0.01;
				}
			}

			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Lineage_Balance.class.toString());
			lineage.share.System.println(String.format("에러 라인 -> [%s]", line == null ? "라인 없음" : line));
			lineage.share.System.println(e);
		}
		
		TimeLine.end();
	}

}
