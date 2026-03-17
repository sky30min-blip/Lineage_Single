/**
 * 몬스터 데이터 (서버 monster 테이블의 name 값과 일치해야 함)
 * GM 툴에서 사용하는 ID는 표시용이며, 실제 DB INSERT 시에는 name(몬스터 이름)을 사용합니다.
 */
const monsterData = [
    { id: 45001, name: '오크', level: 15, type: 'normal' },
    { id: 45002, name: '오크 전사', level: 20, type: 'normal' },
    { id: 45003, name: '오크 궁수', level: 18, type: 'normal' },
    { id: 45010, name: '오크 보스', level: 35, type: 'boss' },
    { id: 45100, name: '슬라임', level: 5, type: 'normal' },
    { id: 45101, name: '큰 슬라임', level: 10, type: 'normal' },
    { id: 45200, name: '좀비', level: 25, type: 'undead' },
    { id: 45201, name: '구울', level: 30, type: 'undead' },
    { id: 45300, name: '늑대', level: 8, type: 'animal' },
    { id: 45301, name: '검은 늑대', level: 15, type: 'animal' },
    { id: 45400, name: '개미', level: 10, type: 'insect' },
    { id: 45401, name: '병정개미', level: 18, type: 'insect' },
    { id: 45450, name: '스켈레톤', level: 22, type: 'undead' },
    { id: 45500, name: '고블린', level: 12, type: 'normal' },
    { id: 45501, name: '오크 그런트', level: 18, type: 'normal' },
    { id: 45600, name: '다크엘프', level: 28, type: 'normal' },
    { id: 45700, name: '스톤골렘', level: 35, type: 'normal' },
    { id: 45800, name: '데어', level: 3, type: 'animal' },
    { id: 45801, name: '멧돼지', level: 8, type: 'animal' },
    { id: 90001, name: '[파워볼 NPC]', level: 1, type: 'special' },
    { id: 90002, name: '[전광판 공1]', level: 1, type: 'special' }
];

const typeColors = {
    'normal': '#4CAF50',
    'boss': '#F44336',
    'undead': '#9C27B0',
    'animal': '#795548',
    'insect': '#FF9800',
    'special': '#00BCD4'
};

const typeNames = {
    'normal': '일반',
    'boss': '보스',
    'undead': '언데드',
    'animal': '동물',
    'insect': '곤충',
    'special': '특수'
};
