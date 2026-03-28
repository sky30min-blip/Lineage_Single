autohunt.htm — 클라이언트 pak에 넣을 파일
============================================

1) 사용 중인 클라이언트에서 autopotion.htm 이 있는 위치와 동일한 규칙으로
   이 파일을 "autohunt.htm" 이름으로 넣으세요. (data/html/ 등 빌드마다 다름)

2) 치환 규칙은 본 서버의 powerball1, bossList 등과 같이 %0, %1, … 입니다.
   서버: AutoHuntHtmParams.buildMainList() 순서와 %0~%33 이 1:1 대응합니다.

3) 클라에서 플레이스홀더 문법이 다르면(예: $1) autohunt.htm 만 해당 문법에 맞게 고치면 됩니다.

4) lineage.conf
   autohunt_use_client_htm = false (서버 기본) → 서버가 인라인 HTML 생성 (autohunt.htm 불필요)
   autohunt_use_client_htm = true  → 반드시 클라 pak에 autohunt.htm 추가 후 사용.
      htm 없이 true 이면 대화창은 뜨는데 내용이 안 나올 수 있음(빈 창).

5) 자동 스킬 설정 화면은 여전히 서버 인라인 HTML입니다. 팅기면 별도 autohunt_skill.htm 작업이 필요합니다.
