# waggle-was

와글와글(대구·경북 대학생 팀 매칭 플랫폼) 백엔드. Spring Boot 3.5.6, Java 17, Spring Security + JJWT, JPA + QueryDSL(openfeign 7.0), MySQL, Redis, KOMORAN.

## 원격과 PR
- `origin` = `ming920/waggle-was` (fork, 작업 대상), `upstream` = `Wagglex2/waggle-was` (원본, push 차단됨)
- push는 `origin`에만. PR은 fork의 `develop`으로: `gh pr create -R ming920/waggle-was --base develop`
- 원본 레포(`Wagglex2/*`)에는 push·PR·이슈·코멘트를 만들지 않음

## 작업 단위
- 작업은 fork의 GitHub Issues로 관리. 전체 목록은 #25 (waggle 개선 백로그), 프론트 이슈는 `ming920/waggle-web`
- 이슈 하나 = 브랜치 하나 = PR 하나. PR 본문에 `Closes #<번호>`
- 이슈 범위 밖 문제를 발견하면 고치지 말고 PR 본문이나 새 이슈로 남김
- `needs-decision` 라벨 이슈는 이슈에 정책이 확정되기 전까지 구현하지 않음
- 버그 이슈: 재현 테스트를 먼저 작성해 실패를 확인 → 수정 → 통과. 재현이 안 되면 근거를 이슈에 남기고 멈춤
- 성능 이슈: 수정 전 측정값(SQL 로그, EXPLAIN, k6 결과)을 먼저 남기고, 수정 후 같은 방법으로 다시 측정

## 명령
- 테스트: `./gradlew test` (기본 `test` 프로필: H2 + 더미 설정, MySQL·Redis 불필요)
- 빌드: `./gradlew clean bootJar`
- 로컬 Redis·모니터링: `docker compose up -d` (redis, prometheus, influxdb, grafana. MySQL은 포함 안 됨)
- 부하 테스트: `load-test/*.js` (k6)

## 브랜치·커밋
- 브랜치: `fix/<이슈번호>-<요약>`, `feat/...`, `refactor/...`, `perf/...`
- 커밋: `feat:`, `fix:`, `refactor:`, `perf:`, `test:`, `docs:`, `build:`, `ci:` + 한국어 요약 (예: `fix: 회원가입 시 이메일 인증 여부 검증`)
- `.omc/`, `.DS_Store`, `.env`는 커밋하지 않음

## 알아둘 것
- 프로필: 기본값 `dev`(`ddl-auto=update`, `UserDataLoader`가 시드 사용자 투입), `prod`는 `ddl-auto=validate`
- 마이그레이션 도구가 없음(#21) → 엔티티 스키마를 바꾸면 필요한 DDL을 PR 본문에 적음
- H2는 MySQL FULLTEXT(`MATCH ... AGAINST`)를 지원하지 않음 → 검색 쿼리는 H2 테스트로 검증할 수 없음
- `.env`를 `spring.config.import`로 읽음. 시크릿 값은 출력·커밋하지 않음
- `deploy.yml`은 fork에서 수동 비활성화 상태이고 시크릿도 없음. 다시 켜지 않음
- `src/main/generated`의 Q클래스는 커밋된 오래된 파일(#24)
