powerball_buy.html
==================
서버는 S_Html 로 HTML 키 "powerball_buy" 와 치환 문자열 3개(쿠폰명, 누적 금액, 소지 아데나)를 보냅니다.
실제 대화창에 쓰이는 파일은 클라이언트 쪽 HTML 캐시(버전에 따라 datacake/html, system 등)에
동일 이름으로 넣어야 합니다. 이 폴더의 파일은 레포 백업·동기화용입니다.

확정 구매는 PowerballController.doBet (5만~500만 아데나, 회차당 1회) 규칙을 따릅니다.
