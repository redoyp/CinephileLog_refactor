# 🎬 CinephileLog

> 영화 마니아들을 위한 리뷰 & 커뮤니티 플랫폼  
> TMDB API 기반 검색, 리뷰, 칼럼, 채팅, 플레이리스트 등 다양한 소셜 기능 제공

🍿 https://cinephilog.duckdns.org/

---

## 📑 목차
- [1. 프로젝트 소개](#1-프로젝트-소개)
- [2. 팀원](#2-팀원)
- [3. 프로젝트 요구사항 체크리스트](#3-프로젝트-요구사항-체크리스트)
- [4. 서비스 특징](#4-서비스-특징)
- [5. 주요 기능](#5-주요-기능)
- [6. 기술 스택](#6-기술-스택)
- [7. 프로젝트 구조](#7-프로젝트-구조)
- [8. 테이블 리스트](#8-테이블-리스트)
- [9. 기능 명세서](#9-기능-명세서)
- [10. 화면 설계](#10-화면-설계)
- [11. API 명세서](#11-api-명세서)

---

<details>
<summary>1. 프로젝트 소개</summary>  

---

> ‘ 아.. 오늘은 영화 뭐 보지? ‘  
> ‘ 다른 사람들은 결말을 어떻게 해석했을까? 얘기 나눌 곳 어디 없나? ‘  
>
> 고민하고 계신가요?  
>
> **"CinephileLog"**는 영화 추천부터 소통까지 영화에 대한 모든 것을 제공하는 커뮤니티 플랫폼입니다.  
> 생각과 감정을 공유하고, 같은 취향을 가진 사람들과 소통하며 즐거움을 키워보세요!
>
> ✔ 인기 영화 / 리뷰  
> ✔ 상위 등급 유저는 리뷰 노출 & 이벤트 응모  
> ✔ 칼럼 & 리뷰 작성  
> ✔ 영화별 실시간 채팅  
> ✔ 나만의 플레이리스트 기능  

---

</details>

<details>
<summary>2. 팀원</summary>  

---

| 이름 | GitHub |
|------|----------------|
| 박동재 | [ehdwo13](https://github.com/ehdwo13) |
| 이동엽 | [redoyp](https://github.com/redoyp) |
| 변규리 | [gyuri0124](https://github.com/gyuri0124) |
| Elini | [elini-ng](https://github.com/elini-ng) |
| 오시완 | [ohsiwan](https://github.com/ohsiwan) |

---

</details>

<details>
<summary>3. 프로젝트 요구사항 체크리스트</summary>  

<details>
<summary>🔧 0단계: 환경 구성 및 협업</summary>  

- [✅] Github Organizations 을 통한 협업
- [✅] Git Flow 전략 사용
- [✅] `feature/기능명` 브랜치 전략 사용
- [✅] Main/Develop 브랜치 보호규칙 -> Pull Request 기반 Merge 진행
- [✅] 디스코드 봇을 통한 Commit/Push/Pull Request 알림
- [✅] 관계형 DB (MySQL) 사용
- [✅] REST API 설계 및 문서화
- [✅] ERD 설계 (erdcloud.com)
- [✅] Thymeleaf 기반 Front-End 구현
- [✅] BE/FE 통합 프로젝트 구조

</details>

<details>
<summary> 📌 1단계: 게시판 (칼럼 기능으로 구현)</summary>  

- [✅] 글 목록 보기 `/column`
- [✅] 글 상세 보기 `/column/{id}`
- [✅] 글 작성 (4등급 이상만 가능)
- [✅] 글 수정 (작성자만)
- [✅] 글 삭제 (작성자만)
- [✅] 수정 시간 기록 및 출력


</details>

<details>
<summary> 👤 2단계: 회원 기능</summary>  

- [✅] 회원가입 : Oauth를 통한 소셜로그인
- [✅] 로그인 / 로그아웃
- [✅] 회원 탈퇴
- [✅] 회원 정보 수정 : 닉네임 변경
- [✅] 회원 등급 시스템 (jelly → coke → nachos → hotdog → popcorn)

</details>

<details>
<summary>🔐 3단계: 등급별 기능 제한</summary>  

- [✅] 나만의 플레이리스트 : 2등급(coke) 이상만 가능
- [✅] 영화 채팅방 접근 : 3등급(nachos) 이상만 가능
- [✅] 칼럼 작성: 4등급(hotdog) 이상만 가능
- [✅] 등급별 UI/기능 제한 (버튼 비활성화 처리 및 Alert)

</details>

<details>
<summary> 🛠️ 4단계: 관리자 페이지</summary>  

- [✅] 유저 조회 (가입일, 접속 정보 등)
- [✅] 유저 권한/등급 수정
- [✅] 유저/리뷰 목록 검색 & 정렬
- [✅] 리뷰 숨기기 기능

</details>

<details>
<summary>🚀 5단계: 서비스 배포</summary>  

- [✅] AWS EC2 기반 배포
  - Amazon Linux 2023 + Spring Boot 실행
  - Nginx를 이용한 리버스 프록시 구성
- [✅] AWS RDS (MySQL) 사용
  - 보안 그룹 구성
- [✅] 도메인 주소로 서비스 접속 가능
  - DuckDNS 무료 도메인 사용: `https://cinephilog.duckdns.org`
  - Nginx에서 도메인 연결 및 리디렉션 처리
- [✅] HTTPS 인증서 발급 및 적용 (Let's Encrypt)
  - certbot standalone 방식으로 SSL 인증서 발급
  - Nginx 설정에 인증서 적용 및 HTTP → HTTPS 리디렉션 처리
- [✅] Redis, ElasticSearch 등 외부 서비스 연동 완료
  - Redis: 세션/캐시 관리
  - ElasticSearch: 영화 검색 자동완성 처리
- [✅] 소셜 로그인 3종 연동
  - Google / Kakao / Facebook OAuth 설정
  - `secret.properties`에서 HTTPS 리디렉션 URI 명시
  - Spring Boot와 외부 콘솔 설정 정확히 매칭

</details>
</details>

<details>
<summary>4. 서비스 특징</summary>  

<details>
<summary>🗃️ TMDB 영화 데이터 수집 (배치 시스템)</summary>  

- Spring Batch 기반으로 영화 ID 범위를 파티셔닝하여 병렬로 TMDB API 호출
- 각 파티션은 고유한 ID 범위와 API 키를 할당받아 비동기 방식으로 처리
- `TmdbApiClient`는 아래 세 가지 요청을 병렬 수행하여 영화 정보를 수집
  - 영화 상세 정보 (ko-KR)
  - 영화 상세 정보 (en-US)
  - 출연진 및 제작진 정보 (credits)
- 중복된 movieId는 DB 조회로 필터링하여 저장
- 응답 데이터를 `Movie` 엔티티로 가공 후 JPA를 통해 저장
- WebClient를 Reactor Netty 기반으로 구성하여 TMDB API를 비동기/병렬로 안정적으로 호출
- 커넥션 풀, 연결 타임아웃, 응답 타임아웃, 재시도 설정으로 네트워크 신뢰성 보장

</details>

<details>
<summary>🔍 영화 검색 시스템 (Elasticsearch 기반)</summary>  

- **Elasticsearch** 기반으로 검색 인덱스를 구축하여 빠르고 정확한 검색 기능 제공
- **자동완성(Auto-complete)** 기능:  
  - 영화 제목을 기준으로 한 실시간 자동완성 구현
  - **한글은 2자**, **영어는 3자** 입력부터 검색 수행
  - Elasticsearch와 연동된 API를 통해 효율적인 데이터 스트리밍 구현
- 검색 결과는 영화 포스터, 제목, 개요를 포함하며 각 항목은 `movieDetail/{movieId}` 링크로 연결
- Elasticsearch 인덱싱은 배치 수집 시 자동으로 동기화됨

</details>

<details>
<summary>💿 영화 정보 조회 (Redis)</summary>  

- **Redis** 를 사용하여 DB 부하를 줄이고 응답 속도를 높임
- movieId에 해당하는 영화 정보 조회 
	- 첫 조회 시에는 DB에서 데이터를 가져와 Redis 캐시로 저장
	- 이후 동일한 요청은 캐시를 통해 빠르게 응답
	- 캐시 데이터는 60분(Time-To-Live) 동안 유지, 이후 자동으로 만료
- Key-Value 형식으로 Key: movieId - Value: movieId에 해당하는 영화 정보를 캐시 저장
- 서버 부하 감소와 응답 속도 개선이라는 이점이 있음

</details>

<details>
<summary>🔑 소셜 로그인/로그아웃 (OAuth2)</summary>  

- **OAuth2** 를 사용하여 각자 provider의 client id 하고 secret code로 로그인/로그아웃 
  - 로그인 성공하면 회원 정보 (이메일, provider)를 저장
  - 로그아웃은 OAuth2 하고 Spring Security에 로그아웃
  - Provider: Kakao, Google, Facebook
- 회원 탈퇴한후에 OAuth2 하고 Spring Security에 로그아웃

</details>

<details>
<summary>🔒 관리자 페이지</summary>  

- `/admin/**` 경로를 통해 접근 가능한 관리자 인터페이스를 제공하여 운영 및 관리에 필요한 주요 기능 관리
- **회원 관리** 기능:
	- 등록된 모든 회원 목록 조회
	- 특정 회원의 정보 수정
	- 특정 회원의 계정 삭제
	- 닉네임 기반으로 회원 검색 
- **리뷰 관리** 기능:
	- 등록된 모든 리뷰 목록 조회
	- 닉네임, 리뷰 내용, 영화 제목 기반으로 리뷰 검색
	- 특정 리뷰 상세 정보 
	- 특정 리뷰를 숨김 처리하여 회원에게 보이지 않도록 설정
	- 숨김 처리된 리뷰를 다시 보이도록 해제
- 회원에게 등급(Grade)을 부여하고 권한(Role)을 설정해 서비스 접근 권한 관리
- 로그인한 회원이 관리자 권한(ROLE_ADMIN)을 가지고 있는지 확인하여 관리자 전용 기능 접근 제어

</details>

</details>

<details>
<summary>5. 주요 기능</summary>  


- 🔍 **영화 자동완성 검색** (ElasticSearch)
- 📝 **리뷰 작성 및 좋아요**
- 💿 **영화 정보 조회** (Redis)
- 📰 **칼럼 작성** (4등급 이상 유저만 가능)
- 💬 **실시간 채팅방** (WebSocket 기반)
- 💾 **영화 플레이리스트 저장** (하트 + 리스트 선택/생성)
- 🎬 **인기영화 정보 조회**
- 🔑 **소셜 로그인/로그아웃** (OAuth2)
- 🧑🏻 **프로필 정보 수정**
- 🎯 **등급 시스템** (jelly → coke → nachos → hotdog → popcorn)
- 🧩 **Spring Batch 기반 TMDB API 연동** (API Key 병렬 처리 및 배치작업을 통한 RDS 저장)
- 🗃️ **관리자 페이지** (회원, 리뷰 관리)

</details>

<details>
<summary>6. 기술 스택</summary>  


| 영역 | 기술 |
|---|---|
| Backend | Spring Boot, Spring Security, JPA, MyBatis, Spring Batch, WebSocket, WebFlux, OAuth2 |
| Frontend | Thymeleaf + Bootstrap, jQuery, JS, CSS |
| Search | AWS Elasticsearch |
| DB | AWS RDS(MySQL), Redis |
| Infra | AWS EC2, Nginx, Let's Encrypt |
| 인증 | OAuth2 (Google, Kakao, Facebook) |
| 외부 API | TMDB API |
| 공통 | Lombok, SLF4J, ERD(erdcloud.com), REST API 설계 |

</details>

<details>
<summary>7. 프로젝트 구조</summary>  


```
CinephileLog/
├── column/         # 칼럼 도메인 (작성, 조회 등)
├── config/         # Redis, Elasticsearch 등 외부 설정
├── configuration/  # Spring Security 설정
├── controller/     # 메인 화면, 검색, 유저, 채팅 등 API/view 컨트롤러
├── domain/         # 공통 도메인 (User, Playlist, Grade, Chat 등)
├── dto/            # 요청/응답 DTO 클래스
├── external/
│   ├── batch/      # TMDB 연동 Spring Batch 처리
│   └── service/    # TMDB API Client 로직
├── handler/        # 전역 예외 처리 핸들러
├── mapper/         # MyBatis 매퍼 인터페이스
├── movie/          # 영화 도메인
├── repository/     # JPA/QueryDSL 등 저장소 레이어
├── review/         # 리뷰 도메인
└── service/        # 비즈니스 로직 처리 서비스 클래스
```

</details>

<details>
<summary>8. 테이블 리스트</summary>  


<details>
<summary>👦🏻 user</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| user_id | BIGINT | 유저 아이디 |
| provider | VARCHAR(20) | 프로바이더 |
| email | VARCHAR(300) | 이메일 |
| nickname | VARCHAR(100) | 닉네임 |
| role | ENUM | 권한 |
| grade_id | BIGINT | 등급 아이디 |
| point | BIGINT | 포인트 |
| is_active | VARCHAR(1) | 가입 상태 |
| register_date | TIMESTAMP | 가입날짜 |
| update_date | TIMESTAMP | 수정날짜 |
| last_login | TIMESTAMP | 최종 접속 날짜 |
| login_count | BIGINT | 접속 횟수 |

</details>

<details>
<summary>🎬 movie</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| movie_id | BIGINT | 영화 아이디 |
| title | VARCHAR(250) | 영화 이름 |
| title_original | VARCHAR(250) | 오리지널 영화 이름 |
| release_date | DATE | 개봉 날짜 |
| genres | TEXT | 장르 |
| poster_url | VARCHAR(250) | 영화 포스터 |
| rating | INT | 평점 |
| synopsis | TEXT | 개요 |
| synopsis_original | TEXT | 오리지널 개요 |
| director | TEXT | 감독 |
| director_original | TEXT | 오리지널 감독 |
| cast | TEXT | 배우 |
| cast_original | TEXT | 오리지널 배우 |

</details>

<details>
<summary>🎞 column_article</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| column_id | BIGINT | 칼럼 아이디 |
| title | VARCHAR(300) | 제목 |
| content | TEXT | 내용 |
| created_date | TIMESTAMP | 생성 날짜 |
| updated_date | TIMESTAMP | 수정 날짜 |
| is_deleted | BOOLEAN | 삭제 상태 |
| view_count | BIGINT | 조회수 |
| user_id | BIGINT | 유저 아이디 |
| movie_id | BIGINT | 영화 아이디 |

</details>

<details>
<summary>📜 playlist</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| playlist_id | BIGINT | 플레이리스트 아이디 |
| user_id | BIGINT | 유저 아이디 |
| name | BIGINT | 이름 |
| description | TEXT | 설명 |
| created_date | TIMESTAMP | 생성 날짜 |

</details>

<details>
<summary>📽 playlist_movie</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| playlist_id | BIGINT | 플레이리스트 아이디 |
| movie_id | BIGINT | 영화 아이디 |

</details>

<details>
<summary>🎖 grade</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| grade_id | BIGINT | 등급 아이디 |
| grade_name | VARCHAR(100) | 등급 이름 |

</details>

<details>
<summary>📝 user_score</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| user_id | BIGINT | 유저 아이디 |
| review_count | INT | 리뷰수 |
| like_count | INT | 좋아수 |
| weighted_score | DOUBLE | 점수 |
| updated_at | TIMESTAMP | 수정 날짜 |

</details>

<details>
<summary>👓 review</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| review_id | BIGINT | 리뷰 아이디 |
| user_id | BIGINT | 유저 아이디 |
| movie_id | BIGINT | 영화 아이디 |
| rating | DECIMAL(3,1) | 별점 |
| content | TEXT | 내용 |
| created_date | TIMESTAMP | 리뷰 작성일 |
| updated_date | TIMESTAMP | 수정 날짜 |
| like_count | BIGINT | 추천수(좋아요수) |
| blinded | BOOLEAN | 블라인드 |

</details>

<details>
<summary>❤ review_like</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| like_id | BIGINT | 좋아요 아이디 |
| user_id | BIGINT | 유저 아이디 |
| review_id | BIGINT | 리뷰 아이디 |

</details>

<details>
<summary>📱 chatting_room</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| room_id | BIGINT | 채팅방 아이디 |
| movie_id | BIGINT | 영화 아이디 |
| room_name | VARCHAR(250) | 채팅방 이름 |
| created_date | TIMESTAMP | 생성 날짜 |

</details>

<details>
<summary>🖥 user_chatting_room</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| user_id | BIGINT | 유저 아이디 |
| room_id | BIGINT | 채팅방 아이디 |

</details>

<details>
<summary>💬 message</summary>  

| Column Name | Type | Description |
| --- | --- | --- |
| message_id | BIGINT | 메시지 아이디 |
| room_id | BIGINT | 채팅방 아이디 |
| user_id | BIGINT | 유저 아이디 |
| sent_time | TIMESTAMP | 보낸 시간 |
| content | TEXT | 내용 |

</details>

</details>

<details>
<summary>9. 기능 명세서</summary>  

<details>
<summary>✔️ 회원 관리</summary>  

| **No** | **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- | --- |
| 1 | 회원가입 | 소셜 로그인을 통한 회원가입 | google, facebook, kakao 계정을 통한 회원가입 |
| 2 |  | 닉네임 설정 | 회원가입 시 초기 닉네임 설정 (중복 불가) |
| 3 | 로그인 | 소셜 로그인 | 소셜 로그인을 통한 로그인 |
| 4 |  | 로그아웃 | 로그아웃 버튼을 통해 메인 페이지 연결 |
| 5 | 내 프로필 | 등급 확인 | 등급에 따른 프로필 뱃지 확인 |
| 6 |  | 회원 정보 변경 | 로그인 상태에서 수정 클릭 시 닉네임 변경 (중복 불가) |
| 7 |  | 회원 탈퇴 | 탈퇴 버튼을 통해 탈퇴 후 메인 페이지 연결 |
| 8 | 플레이리스트 | 나만의 영화 리스트(내 플레이리스트) | 내 플레이리스트 확인 |

</details>

<details>
<summary>🎬 영화 상세 페이지</summary>  

| **No** | **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- | --- |
| 9 | 상세 정보 | 영화 정보 확인 | 해당 영화의 제목, 세부 정보, 줄거리 확인 |
| 10 | 플레이리스트 | 나만의 영화 리스트(내 플레이리스트) | 영화 제목 우측의 하트 버튼을 통해 내 플레이리스트에 추가/ 목록 확인 / 삭제 |
| 11 | 리뷰 | 리뷰 수정 | 이미 작성한 리뷰에 대하여 수정 |
| 12 |  | 리뷰 정렬 | 작성된 리뷰를 오래된 순, 최신순, 좋아요 많은 순, 좋아요 적은 순으로 정렬 |
| 13 |  | 별점 부여 | 0.0 ~ 10.0 사이의 별점 부여 |
| 14 |  | 리뷰 작성 | 해당 영화에 대한 리뷰를 한 줄로 작성 |
| 15 |  | 리뷰 삭제 | 이미 작성한 리뷰에 대하여 삭제 |
| 16 |  | 좋아요  | 자신의 리뷰를 포함하여 모든 리뷰에 좋아요 가능 |
| 17 | 채팅방 | 채팅방 이동 | 우측 하단의 말풍선 버튼을 통해 채팅방 연결 |

</details>

<details>
<summary>📄 영화 칼럼 페이지</summary>  

| **No** | **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- | --- |
| 18 | 칼럼 | 글쓰기 | 한 줄이 아닌 긴 칼럼 작성 (핫도그 등급부터 가능) |
| 19 |  | 칼럼 검색 | 검색 기능을 통해 키워드로 특정 칼럼 검색 |
| 20 |  | 칼럼 조회 | 특정 칼럼을 클릭해 열람 |
| 21 |  | 칼럼 정렬 | 10개 또는 20개/ 최신순 또는 조회순/ 제목 또는 작성자 정렬 |
| 22 |  | 칼럼 삭제 | 이미 작성한 칼럼에 대해 삭제 |

</details>

<details>
<summary>💬 채팅방</summary>  

| **No** | **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- | --- |
| 23 | 채팅방 | 영화 정보 확인 | 좌측 포스터에 마우스 오버 후 영화 제목, 별점, 감독, 배우 정보 확인  |
| 24 |  | 채팅 | 해당 채팅방에 입장한 유저들과 실시간 소통 (나쵸 등급부터 가능) |

</details>

<details>
<summary>📊 비회원</summary>  

| **No** | **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- | --- |
| 25 |  | 리뷰 보기 | 영화 상세 페이지로 이동시 리뷰 조회 가능 |

</details>

<details>
<summary>🔐 관리자 기능</summary>  

| **No** | **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- | --- |
| 26 | 운영 | 관리자 홈  | 유저와 리뷰 관리 페이지 연결 |
| 27 |  | 유저 목록 확인 | 유저 정보(닉네임, 등급, 권한, 포인트, 가입여부, 가입일, 수정일, 최종 접속 시간, 접속 횟수)를 확인하는 페이지. 닉네임, 관리 버튼으로 유저 관리 페이지 연결 |
| 28 |  | 유저 검색 | 닉네임으로 검색 |
| 29 |  | 유저 관리  | 유저 정보를 수정하는 페이지. 정보를 수정하여 저장하거나 유저를 삭제하거나 유저 목록으로 되돌아갈 수 있음 |
| 30 |  | 리뷰 목록 확인  | 리뷰 정보(닉네임, 내용, 작성일, 작성일, 숨김 상태)를 확인하는 페이지. 관리 버튼으로 리뷰 관리 페이지 연결 |
| 31 |  | 리뷰 검색 | 닉네임, 리뷰 내용, 영화 제목으로 검색 |
| 32 |  | 리뷰 관리 | 리뷰 정보를 숨기거나 숨긴 리뷰를 숨김해제하는 페이지. 정보를 수정하고 리뷰 목록으로 되돌아갈 수 있음 |

</details>

<details>
<summary>🏠 메인페이지 기능</summary>  

| **No** | **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- | --- |
| 33 | 분류 | 영화 칼럼 게시판 이동 | 영화 칼럼 게시판 연결 |
| 34 |  | 등급 소개 페이지 이동 | 등급 소개 페이지 연결 |
| 35 |  | 인기 영화/리뷰 조회 | 영화 api를 통해 인기 영화와 인기 리뷰 조회 |
| 36 |  | 내 프로필 페이지 이동 | 내 프로필 페이지 연결 |
| 37 |  | 관리자 페이지 이동 | 관리자 권한이 부여된 경우, 관리자 페이지로 이동 가능 |

</details>

<details>
<summary>📌 네비게이션 바</summary>  

| **No** | **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- | --- |
| 38 | 분류 | 메인 페이지 이동 | 로고나 사이트 이름 클릭시 메인 페이지 이동 |
| 39 |  | 영화 검색 | 이름으로 영화 검색 |
| 40 |  | 닉네임/뱃지 확인 | 닉네임, 뱃지를 통해 자신의 등급 확인 |
| 41 |  | 내 프로필 페이지 이동 | 내 프로필 버튼을 통해 내 프로필 페이지 이동 |
| 42 |  | 로그아웃 | 로그아웃 버튼을 통해 로그아웃 후 메인 페이지 이동 |
| 43 |  | 등급 소개 페이지 이동 | 등급 소개 버튼을 통해 등급 소개 페이지 이동 |

</details>

</details>

<details>
<summary>10. 화면 설계</summary>


### 🏡Main Page1

![image.png](attachment:f966541b-1f29-4a65-b5b5-3e62f54f495c:image.png)

### 🏡Main Page2

![image.png](attachment:8a5bc256-ed59-4532-be48-75beda5bc4fa:image.png)

### 🎬Movie Column

![image.png](attachment:3b7d3646-3388-44f7-869e-7f002116fe71:image.png)

### 📱Chat Room

![image.png](attachment:563ac217-756d-4efd-98cc-4d3b2795b756:image.png)

### 🎖Grade Description Page

![image.png](attachment:4651969d-8f23-49d8-98f1-e16de694192f:image.png)

### 📔My Profile

![image.png](attachment:9511eae5-30c1-4768-bdee-699943a702a3:image.png)

### 📘Admin - User Management Page

![image.png](attachment:c7f77fe5-3bc1-4230-9c1f-51ec0963b498:image.png)

### 📘Admin - Review Management Page

![image.png](attachment:f76fc9a3-5419-43a6-8a5f-9779922f816e:image.png)

### 📝Sign Up - Set Nickname

![image.png](attachment:ad7f750c-1445-427f-94cd-1f7e61a16b3f:image.png)

### 🔒Log In Page

![image.png](attachment:5efde4fd-9e35-4801-9c09-5235cc65830f:image.png)

### 📢Movie Column Create

![image.png](attachment:b7054bc5-0915-4865-99c1-5bd2023a8333:image.png)

### 👓Movie Review

![image.png](attachment:33ed91e5-cd1f-4e67-8484-1e6c147af1b5:image.png)

### 👨🏻‍✈️Admin Main Page

![image.png](attachment:1ce6e239-b0da-47d3-b13b-91209c2278af:image.png)

### Admin - User Management Modification Page

![image.png](attachment:c1b334a5-a24a-419b-a894-f2c2a12e8a99:image.png)

### Admin - Review Management Modification Page

![image.png](attachment:d24bcaea-305d-422a-adf9-94508fd0c3cd:image.png)

</details>

<details>
<summary>11. API 명세서</summary>  

### 📁 User

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| updateUser | PUT | api/user/{id} | 유저 데이터 수정 |
| checkNickname | POST | api/checkNickname | 닉네임 중복 여부 체크 |

### 📁 Review

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| create review | POST | /movies/{movieId}/reviews | 로그인 된 유저 리뷰 작성 |
| reviews(movie) | GET | /movies/{movieId}/reviews | 해당 영화의 모든 리뷰 조회 |
| reviews(user) | GET | /users/{userId}/reviews | 해당 유저가 작성한 모든 리뷰 조회 |
| review(movie, user) | GET | /movies/{movieId}/reviews/{userId} | 해당 영화에 특정 유저가 작성한 리뷰 조회 |
| delete review | DELETE | /movies/{movieId}/reviews | 로그인 된 유저가 본인이 작성한 리뷰 삭제 |
| update review | PUT | /movies/{movieId}/reviews | 로그인 된 유저 리뷰 수정 |
| review like | POST | /reviews/{reviewId}/like | 좋아요 추가/삭제 |

### 📁 Movie

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| movie detail view | GET | /movieDetail/{movieId} | 영화 상세 페이지 이동 |
| all movies | GET | /movies | 모든 영화 정보 조회 |
| movie | GET | /movies/{movieId} | 특정 영화 정보 조회 |

### 📁 Search

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| search movies | GET | /search | 영화 검색 (2자 이상 입력 필요) |

### 📁 Autocomplete

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| autocomplete | GET | /autocomplete | 영화 제목 자동완성 (2자 이상 입력 시) |

### 📁 Chat

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| enter chat room | GET | /chatroom/{movieId} | 영화 ID 기준으로 채팅방 입장 및 정보 로딩 |
| send message | WS | /chat/send/{roomId} | 채팅방에 메시지 전송 (WebSocket) |
| enter room notice | WS | /chat/enter/{roomId} | 채팅방 입장 알림 메시지 전송 (WebSocket, SYSTEM) |
| leave room notice | WS | /chat/leave/{roomId} | 채팅방 퇴장 알림 메시지 전송 (WebSocket, SYSTEM) |

### 📁 ChatMessage

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| get messages | GET | /api/chat/messages | 채팅방 메시지 페이지별 조회 |

### 📁 Playlist

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| create playlist | POST | /playlists | 새로운 플레이리스트 생성 |
| get my playlists | GET | /playlists | 로그인 유저의 플레이리스트 전체 조회 |
| get playlist detail | GET | /playlists/{playlistId} | 특정 플레이리스트 상세 정보 및 영화 목록 조회 |
| add movie to list | POST | /playlists/{playlistId}/movies | 플레이리스트에 영화 추가 |
| remove movie | DELETE | /playlists/{playlistId}/movies/{movieId} | 플레이리스트에서 영화 제거 |
| delete playlist | DELETE | /playlists/{playlistId} | 플레이리스트 삭제 |

### 📁 Admin

| 🏷NAME | ⚙METHOD | 📎URL | 📖DESCRIPTION |
| --- | --- | --- | --- |
| admin home | GET | /admin | 관리자 페이지를 보여줌 |
| list users | GET | /admin/users | 모든 유저의 목록 조회 |
| edit user form | GET | /admin/users/edit/{userId} | 특정 userId를 가진 유저의 수정 폼을 보여주기 위한 정보 조회 |
| update user | POST | /admin/users/update/{userId} | 특정 userId를 가진 유저 정보 수정 |
| delete user | POST | /admin/users/delete/{userId} | 특정 userId를 가진 유저 삭제 |
| search users | GET | /admin/users/search | keyword를 사용하여 유저 검색. keyword가 없으면 모든 유저 목록 조회 |
| list reviews | GET | /admin/reviews | 모든 리뷰 목록을 조회하거나 keyword를 사용하여 리뷰 검색. keyword가 없으면 모든 리뷰 목록 조회 |
| review detail | GET | /admin/reviews/{id} | 특정 id 가진 리뷰의 상세 정보 조회 |
| blind review | POST | /admin/reviews/{reviewId}/blind | 특정 reviewId를 가진 리뷰를 블라인드 처리 |
| unblind review | POST | /admin/reviews/{reviewId}/unblind | 특정 reviewId를 가진 리뷰의 블라인드 처리 해제 |

</details>

---
