/**
 * 몬스터 스폰 관리 로직
 * 서버 테이블: monster_spawnlist (uid, name, monster, random, count, loc_size, spawn_x, spawn_y, spawn_map, re_spawn_min, re_spawn_max, groups, monster_1~4, monster_1_count~4)
 * pages/ 에서 열 때는 HTML에서 window.GM_TOOL_BASE = '..' 설정 필요
 */
let selectedMonster = null;
const UID_BASE = 900000; // 생성되는 INSERT의 uid 시작값 (기존 데이터와 겹치지 않게 조정 가능)
function getApiBase() { return (typeof window !== 'undefined' && window.GM_TOOL_BASE) ? (window.GM_TOOL_BASE + '/').replace(/\/+$/, '/') : ''; }

// API에서 불러온 몬스터 또는 기본 목록
function getMonsterList() { return window.effectiveMonsterData || (typeof monsterData !== 'undefined' ? monsterData : []); }

document.addEventListener('DOMContentLoaded', function() {
    buildMapSelect();
    fetchMonstersThenLoad();
    // 1번 맵 선택 시 5번 스폰 목록 갱신 (인라인 onchange 대신 JS 바인딩으로 확실히 연결)
    var mapSel = document.getElementById('mapSelect');
    if (mapSel) mapSel.addEventListener('change', function() { loadMapSpawns(); });
    // 초기 상태: 맵 미선택 시 5번 안내 문구 표시
    loadMapSpawns();
});

function buildMapSelect() {
    var sel = document.getElementById('mapSelect');
    if (!sel || typeof MAP_LIST === 'undefined') return;
    var firstOpt = sel.querySelector('option');
    sel.innerHTML = '';
    if (firstOpt) sel.appendChild(firstOpt);
    MAP_LIST.forEach(function(g) {
        var og = document.createElement('optgroup');
        og.label = g.group;
        (g.maps || []).forEach(function(m) {
            var opt = document.createElement('option');
            opt.value = m[0];
            opt.textContent = m[1];
            og.appendChild(opt);
        });
        sel.appendChild(og);
    });
}

function fetchMonstersThenLoad() {
    var base = getApiBase();
    var apiUrl = base + 'api/spawn-api.php';
    var listEl = document.getElementById('monsterList');
    if (listEl) listEl.innerHTML = '<div style="padding:12px;color:#666;">몬스터 목록 불러오는 중...</div>';

    var opts = { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: 'action=list_monsters' };
    fetch(apiUrl, opts)
        .then(function(r) {
            if (!r.ok) throw new Error('API ' + r.status);
            return r.json();
        })
        .then(function(data) {
            if (data.success && data.monsters && Array.isArray(data.monsters)) {
                window.effectiveMonsterData = data.monsters.map(function(m) {
                    return { id: m.name, name: m.name, level: parseInt(m.level, 10) || 0, type: 'normal' };
                });
            } else {
                window.effectiveMonsterData = null;
            }
            loadMonsterList();
        })
        .catch(function(err) {
            window.effectiveMonsterData = null;
            loadMonsterList();
        });
}

function loadMonsterList() {
    const listContainer = document.getElementById('monsterList');
    if (!listContainer) return;
    listContainer.innerHTML = '';
    var list = getMonsterList();
    if (list.length === 0) {
        var msg = window.effectiveMonsterData && window.effectiveMonsterData.length === 0
            ? 'DB monster 테이블에 등록된 몬스터가 없습니다.'
            : '몬스터 목록을 불러오지 못했습니다. PHP 서버(http://localhost:8765)로 이 페이지를 열면 DB 전체 몬스터가 표시됩니다.';
        listContainer.innerHTML = '<div style="padding:12px;color:#666;">' + msg + '</div>';
        return;
    }

    list.forEach(monster => {
        const item = document.createElement('div');
        item.className = 'monster-item';
        item.dataset.monsterId = String(monster.id);
        item.onclick = function() { selectMonster(monster); };

        const typeColor = (typeof typeColors !== 'undefined' && typeColors[monster.type]) ? typeColors[monster.type] : '#999';
        const typeLabel = (typeof typeNames !== 'undefined' && typeNames[monster.type]) ? typeNames[monster.type] : '일반';

        item.innerHTML = '<div class="monster-item-name">' + escapeHtml(monster.name) + '</div>' +
            '<div class="monster-item-id">Lv.' + monster.level + ' <span style="color:' + typeColor + ';">' + typeLabel + '</span></div>';

        listContainer.appendChild(item);
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function escapeSql(str) {
    if (str == null) return '';
    return String(str).replace(/'/g, "''");
}

function filterMonsters() {
    const searchText = document.getElementById('monsterSearch').value.toLowerCase();
    const items = document.querySelectorAll('.monster-item');

    items.forEach(item => {
        const name = item.querySelector('.monster-item-name').textContent.toLowerCase();
        item.style.display = name.includes(searchText) ? 'block' : 'none';
    });
}

function selectMonster(monster) {
    selectedMonster = monster;
    document.querySelectorAll('.monster-item').forEach(item => {
        item.classList.remove('selected');
    });
    var sid = String(monster.id).replace(/\\/g, '\\\\').replace(/"/g, '\\"');
    var el = document.querySelector('[data-monster-id="' + sid + '"]');
    if (el) el.classList.add('selected');
    generateSQL();
}

function generateSQL() {
    const mapId = document.getElementById('mapSelect').value;
    const locationName = document.getElementById('locationName').value || '스폰 지점';
    const count = parseInt(document.getElementById('spawnCount').value, 10) || 10;
    const respawnTime = parseInt(document.getElementById('respawnTime').value, 10) || 300;
    const moveDistance = parseInt(document.getElementById('moveDistance').value, 10) || 10;
    const heading = parseInt(document.getElementById('heading').value, 10) || 0;
    const baseX = parseInt(document.getElementById('spawnX').value, 10) || 33000;
    const baseY = parseInt(document.getElementById('spawnY').value, 10) || 33000;
    const rangeX = parseInt(document.getElementById('spawnRangeX').value, 10) || 20;
    const rangeY = parseInt(document.getElementById('spawnRangeY').value, 10) || 20;

    if (!mapId || !selectedMonster) {
        document.getElementById('sqlPreview').innerHTML = `
            <div class="preview-title">⚠️ 맵과 몬스터를 선택해주세요</div>
        `;
        window.generatedQueries = [];
        return;
    }

    const monsterName = escapeSql(selectedMonster.name);
    const locName = escapeSql(locationName);
    const sqlQueries = [];

    for (let i = 0; i < count; i++) {
        const offsetX = Math.floor((Math.random() - 0.5) * rangeX * 2);
        const offsetY = Math.floor((Math.random() - 0.5) * rangeY * 2);
        const x = baseX + offsetX;
        const y = baseY + offsetY;
        const uid = UID_BASE + i + 1;
        const locSize = moveDistance;
        // monster_spawnlist: 서버가 groups, monster_1~4 등 확장 컬럼을 쓰면 아래 전체 INSERT 사용
        const sql = `INSERT INTO monster_spawnlist (uid, name, monster, \`random\`, count, loc_size, spawn_x, spawn_y, spawn_map, re_spawn_min, re_spawn_max, groups, monster_1, monster_1_count, monster_2, monster_2_count, monster_3, monster_3_count, monster_4, monster_4_count) VALUES (${uid}, '${locName}', '${monsterName}', 'true', 1, ${locSize}, ${x}, ${y}, '${escapeSql(mapId)}', ${respawnTime}, ${respawnTime}, 'false', '', 0, '', 0, '', 0, '', 0);`;
        sqlQueries.push(sql);
    }

    const preview = document.getElementById('sqlPreview');
    preview.innerHTML = `
        <div class="preview-title">-- 실행될 SQL 쿼리 (${count}개)</div>
        <div class="preview-title">-- 몬스터: ${escapeHtml(selectedMonster.name)} (name 값으로 사용)</div>
        <div class="preview-title">-- 맵 ID: ${mapId}</div>
        <br>
        ${sqlQueries.slice(0, 3).map(q => escapeHtml(q)).join('\n\n')}
        ${count > 3 ? `\n\n-- ... 외 ${count - 3}개 더` : ''}
    `;

    window.generatedQueries = sqlQueries;
}

function executeSQL() {
    if (!window.generatedQueries || window.generatedQueries.length === 0) {
        alert('먼저 "쿼리 생성"을 눌러 쿼리를 생성해주세요!');
        return;
    }

    if (!confirm(`${window.generatedQueries.length}개의 스폰을 DB에 추가하시겠습니까?\n\n(API가 없으면 SQL이 클립보드에 복사됩니다)`)) {
        return;
    }

    const apiUrl = getApiBase() + 'api/spawn-api.php';
    const body = 'action=execute_spawn&queries=' + encodeURIComponent(JSON.stringify(window.generatedQueries));

    fetch(apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('✅ 스폰이 성공적으로 추가되었습니다!');
            loadMapSpawns();
        } else {
            alert('❌ 오류: ' + (data.error || '알 수 없음'));
        }
    })
    .catch(function() {
        const allQueries = window.generatedQueries.join('\n\n');
        if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(allQueries).then(() => {
                alert('✅ API가 없어 SQL 쿼리를 클립보드에 복사했습니다.\n\nMySQL 클라이언트에서 붙여넣기 후 실행하세요.');
            }).catch(() => {
                showSqlFallback(allQueries);
            });
        } else {
            showSqlFallback(allQueries);
        }
    });
}

function showSqlFallback(allQueries) {
    const w = window.open('', '_blank');
    if (w) {
        w.document.write('<pre style="font-size:12px;">' + allQueries.replace(/</g, '&lt;').replace(/>/g, '&gt;') + '</pre>');
        w.document.close();
        alert('✅ 새 창에 SQL을 표시했습니다. 복사 후 MySQL에서 실행하세요.');
    } else {
        alert('SQL 쿼리가 생성되었습니다. 미리보기 영역 내용을 복사해 MySQL에서 실행하세요.');
    }
}

function loadMapSpawns() {
    const mapId = document.getElementById('mapSelect').value;
    const tbody = document.getElementById('spawnTableBody');

    if (!mapId) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #999;">맵을 선택하면 스폰 목록이 표시됩니다</td></tr>';
        return;
    }

    const apiUrl = getApiBase() + 'api/spawn-api.php';
    const body = 'action=list_spawns&map_id=' + encodeURIComponent(mapId);

    fetch(apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body
    })
    .then(function(response) {
        return response.text().then(function(text) {
            try { return JSON.parse(text); } catch (e) { return { success: false, error: '응답 형식 오류' }; }
        });
    })
    .then(function(data) {
        if (data.success === false) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #c00;">API 오류: ' + escapeHtml(data.error || '알 수 없음') + '</td></tr>' +
                '<tr><td colspan="6" style="text-align: center; font-size: 11px; color: #999;">PHP 서버로 이 페이지를 연 뒤 gm_tool/api/config.php 에 DB 정보를 설정하세요.</td></tr>';
            return;
        }
        if (data.spawns && data.spawns.length > 0) {
            tbody.innerHTML = data.spawns.map(function(s) {
                return '<tr>' +
                    '<td>' + escapeHtml(s.name || '-') + '</td>' +
                    '<td>' + escapeHtml(s.monster || '-') + '</td>' +
                    '<td>' + (s.count != null ? s.count : '-') + '</td>' +
                    '<td>' + (s.spawn_x != null && s.spawn_y != null ? s.spawn_x + ', ' + s.spawn_y : '-') + '</td>' +
                    '<td>' + (s.re_spawn_min != null ? s.re_spawn_min + '초' : '-') + '</td>' +
                    '<td><button type="button" class="delete-btn" onclick="deleteSpawn(' + s.uid + ')">삭제</button></td>' +
                    '</tr>';
            }).join('');
        } else {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #999;">이 맵에 등록된 스폰이 없습니다. 위에서 몬스터를 선택하고 스폰을 추가해 보세요.</td></tr>';
        }
    })
    .catch(function() {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #c00;">API에 연결할 수 없습니다.</td></tr>' +
            '<tr><td colspan="6" style="text-align: center; font-size: 11px; color: #999;">이 페이지를 PHP 서버에서 열어주세요 (예: http://localhost:8765/gm_tool/pages/...). gm_tool/api/config.php 에 DB 설정을 확인하세요.</td></tr>';
    });
}

function deleteSpawn(uid) {
    if (!confirm('이 스폰을 삭제하시겠습니까?')) return;
    const apiUrl = getApiBase() + 'api/spawn-api.php';
    const body = 'action=delete_spawn&uid=' + encodeURIComponent(uid);
    fetch(apiUrl, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: body })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                alert('삭제되었습니다.');
                loadMapSpawns();
            } else {
                alert('오류: ' + (data.error || '삭제 실패'));
            }
        })
        .catch(() => alert('API 호출 실패'));
}

function openCustomMonsterModal() {
    var modal = document.getElementById('customMonsterModal');
    if (modal) modal.classList.add('show');
}

function closeCustomMonsterModal() {
    var modal = document.getElementById('customMonsterModal');
    if (modal) modal.classList.remove('show');
}

function submitCustomMonster() {
    var name = (document.getElementById('customName') && document.getElementById('customName').value || '').trim();
    if (!name) {
        alert('몬스터 이름을 입력하세요.');
        return;
    }
    var nameId = (document.getElementById('customNameId') && document.getElementById('customNameId').value || '$0').trim();
    var gfx = parseInt(document.getElementById('customGfx') && document.getElementById('customGfx').value || 0, 10);
    var level = parseInt(document.getElementById('customLevel') && document.getElementById('customLevel').value || 1, 10);
    var hp = parseInt(document.getElementById('customHp') && document.getElementById('customHp').value || 50, 10);
    var mp = parseInt(document.getElementById('customMp') && document.getElementById('customMp').value || 10, 10);
    var exp = parseInt(document.getElementById('customExp') && document.getElementById('customExp').value || 0, 10);

    var apiUrl = getApiBase() + 'api/spawn-api.php';
    var form = new FormData();
    form.append('action', 'insert_monster');
    form.append('name', name);
    form.append('name_id', nameId || '$0');
    form.append('gfx', gfx);
    form.append('level', level);
    form.append('hp', hp);
    form.append('mp', mp);
    form.append('exp', exp);

    fetch(apiUrl, { method: 'POST', body: form })
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (data.success) {
                alert(data.message || '커스텀 몬스터가 추가되었습니다.');
                closeCustomMonsterModal();
                fetchMonstersThenLoad();
            } else {
                alert('오류: ' + (data.error || '추가 실패'));
            }
        })
        .catch(function() { alert('API 호출 실패. config.php 및 spawn-api.php 연동을 확인하세요.'); });
}

function clearForm() {
    document.getElementById('mapSelect').value = '';
    document.getElementById('locationName').value = '';
    document.getElementById('spawnCount').value = '10';
    document.getElementById('respawnTime').value = '300';
    document.getElementById('moveDistance').value = '10';
    document.getElementById('heading').value = '0';
    document.getElementById('spawnX').value = '33000';
    document.getElementById('spawnY').value = '33000';
    document.getElementById('spawnRangeX').value = '20';
    document.getElementById('spawnRangeY').value = '20';
    selectedMonster = null;
    document.querySelectorAll('.monster-item').forEach(item => {
        item.classList.remove('selected');
    });
    document.getElementById('sqlPreview').innerHTML = `
        <div class="preview-title">-- SQL 쿼리가 여기에 표시됩니다</div>
        위에서 설정을 완료한 뒤 "쿼리 생성"을 누르면 실행될 쿼리가 표시됩니다.
    `;
    window.generatedQueries = [];
}
