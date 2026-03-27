package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.magic.monster.CurseGhoul;

public class Cancellation {

	static public void init(Character cha, Skill skill, int object_id){
		// 초기화
		object o = null;
		// 타겟 찾기
		if(object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true)) {
				// 투망상태 해제
				Detection.onBuff(cha);
				// 공격당한거 알리기.
				o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC, Cancellation.class);
				
				if(SkillController.isFigure(cha, o, skill, true, SkillController.isClan(cha, o))) {
					onBuff(o, skill);
				} else
					// \f1마법이 실패했습니다.
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
				
			}
		}
	}
	
	static public void init(MonsterInstance mi, object o, MonsterSkill ms){					
		// 처리
		if(o != null){
			if(SkillController.isMagic(mi, ms, true)) {
				// 공격당한거 알리기.
				o.toDamage(mi, 0, Lineage.ATTACK_TYPE_MAGIC, Cancellation.class);
				
				if(SkillController.isFigure(mi, o, ms.getSkill(), false, false)) 
					onBuff(o, ms.getSkill());			
			}
		}
	}
	
	static public void onBuff(object o, Skill skill){
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);	
		// 라이트
		BuffController.remove(o, Light.class);
		// 쉴드
		BuffController.remove(o, Shield.class);
		// 커스:포이즌
		BuffController.remove(o, CursePoison.class);
		// 디크리즈 웨이트
		BuffController.remove(o, DecreaseWeight.class);
		// 커스: 블라인드
		// 다크니스
		BuffController.remove(o, CurseBlind.class);
		// 버서커스
		BuffController.remove(o, Berserks.class);
		// 피지컬 인챈트: DEX
		BuffController.remove(o, EnchantDexterity.class);
		// 슬로우, 인탱글
		BuffController.remove(o, Slow.class);
		// 카운터 매직
		BuffController.remove(o, CounterMagic.class);
		// 메디테이션
		BuffController.remove(o, Meditation.class);
		// 커스: 패럴라이즈
		// 괴물눈 석화
		// 석화
		BuffController.remove(o, CurseParalyze.class);
		// 피지컬 인챈트: STR
		BuffController.remove(o, EnchantMighty.class);
		// 헤이스트
		if(o.getInventory()==null || !o.getInventory().isSetOptionHaste())
			BuffController.remove(o, Haste.class);
		// 용기
		if(o.getInventory()==null || !o.getInventory().isSetOptionBrave()) {
			BuffController.remove(o, Bravery.class);
			BuffController.remove(o, Wafer.class);
			BuffController.remove(o, HolyWalk.class);
		}
		// 그레이터 헤이스트
		BuffController.remove(o, GreaterHaste.class);
		// 아이스 랜스
		BuffController.remove(o, IceLance.class);
		// 디지즈
		BuffController.remove(o, Disease.class);
		// 변신
		if (BuffController.find(o, SkillDatabase.find(208)) != null) {
			if (!o.isSetPoly)
				BuffController.remove(o, ShapeChange.class);
		}
		// 이뮨 투 함
		BuffController.remove(o, ImmuneToHarm.class);
		// 디케이 포션
		BuffController.remove(o, DecayPotion.class);
		// 사일런스
		// 에어리어 오브 사일런스
		BuffController.remove(o, Silence.class);
		// 포그 오브 슬리핑
		BuffController.remove(o, FogOfSleeping.class);
		// 인비지
		BuffController.remove(o, InvisiBility.class);
		// 리덕션 아머
		BuffController.remove(o, ReductionArmor.class);
		// 솔리드 캐리지
		BuffController.remove(o, SolidCarriage.class);
		// 카운터 배리어
		BuffController.remove(o, CounterBarrier.class);
		// 글로잉 웨폰
		BuffController.remove(o, GlowingWeapon.class);
		// 샤이닝 실드
		BuffController.remove(o, ShiningShield.class);
		// 글로잉 웨폰
		BuffController.remove(o, GlowingWeapon.class);
		// 브레이브 멘탈
		BuffController.remove(o, BraveMental.class);
		// 브레이브 아바타
		BuffController.remove(o, BraveAvatar.class);
		// 레지스트 매직
		BuffController.remove(o, ResistMagic.class);
		// 클리어 마인드
		BuffController.remove(o, ClearMind.class);
		// 레지스트 엘리멘트
		BuffController.remove(o, ResistElemental.class);
		// 이글 아이
		BuffController.remove(o, EagleEye.class);
		// 아쿠아 프로텍트
		BuffController.remove(o, AquaProtect.class);
		// 폴루트 워터
		BuffController.remove(o, PolluteWater.class);
		// 스트라이커 게일
		BuffController.remove(o, StrikerGale.class);
		// 이레이즈 매직
		BuffController.remove(o, EraseMagic.class);
		// 버닝웨폰
		BuffController.remove(o, BurningWeapon.class);
		// 엘리멘탈 파이어
		BuffController.remove(o, ElementalFire.class);
		// 아이 오브 스톰
		BuffController.remove(o, EyeOfStorm.class);
		// 네이쳐스 터치
		BuffController.remove(o, NaturesTouch.class);
		// 어스 가디언
		BuffController.remove(o, EarthGuardian.class);
		// 어디셔널 파이어
		BuffController.remove(o, AdditionalFire.class);
		// 워터 라이프
		BuffController.remove(o, WaterLife.class);
		// 어스바인드
		BuffController.remove(o, EarthBind.class);
		// 스톰샷
		BuffController.remove(o, StormShot.class);
		// 소울 오브 프레임
		BuffController.remove(o, SoulOfFlame.class);
		// 아이언스킨
		BuffController.remove(o, IronSkin.class);
		// 구울 독
		BuffController.remove(o, CurseGhoul.class);		
		// 파이어웨폰
		BuffController.remove(o, FireWeapon.class);
		// 윈드샷
		BuffController.remove(o, WindShot.class);
		// 이글 아이
		BuffController.remove(o, EagleEye.class);
		// 브레스 오브 파이어
		BuffController.remove(o, BlessOfFire.class);
		// 초록 물약
		BuffController.remove(o, HastePotionMagic.class);
		// 지혜물약
		BuffController.remove(o, Wisdom.class);
		// 마력회복물약
		BuffController.remove(o, Blue.class);
	}	
}
