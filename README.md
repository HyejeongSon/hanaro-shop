# 쇼핑몰 서비스

**과정명** [하나은행] Digital hana 路 금융서비스개발 6기 쇼핑몰 서비스

**진행 기간** 8월 7일(목) ~ 8월 12일(화)

## 🛒 쇼핑몰 서비스 – 요구사항

### 관리자(Admin) 기능

1. 상품 등록 / 조회 / 수정 / 삭제 (이미지 포함)
2. 재고 수량 조정
3. 주문 내역 조회 (검색)
4. 매출 통계 조회
5. 회원 관리 (회원 목록 조회, 삭제 등)

### 일반 사용자(User) 기능

1. 회원 가입 / 로그인
2. 상품 목록 조회 (검색)
3. 상품 상세 보기
4. 장바구니 담기 / 수정 / 삭제
5. 주문 생성 (장바구니 기반)
6. 내 주문 내역 확인

## ⚙️ 쇼핑몰 서비스 – 기능 명세

| 기능 | 내용 |
|------|------|
| 인증/인가 | Spring Security 기반 인증 (ADMIN vs. USER)<br/>- ADMIN 아이디/비밀번호 → `hanaro/12345678`<br/>- 로그인 시 JWT 발급 후 사용자 인증<br/>- ROLE 기반 접근 제어 (`ROLE_ADMIN`, `ROLE_USER`) |
| 파일 업로드 | - 원본 위치: `/resources/static/origin`<br/>- 업로드 위치: `/resources/static/upload`<br/>- 업로드된 날짜별 디렉토리 자동 생성<br/>예: 2025년 07월 25일 업로드한 경우 → `/resources/static/upload/2025/07/25`<br/>- 파일명 중복 방지<br/>- 최대 크기: 1개 512KB, 총 3MB 제한 |
| 스케줄링 | - 결제가 완료되고 → 배송 준비 (매 5분마다 결제 완료된 주문을 배송 준비 상태로)<br/>- 배송 준비 → 배송 중 (매 15분마다 배송 준비 상태를 배송 중 상태로)<br/>- 배송 중 → 배송 완료 (매 1시간마다 배송 중 상태를 배송 완료 상태로) 변환 |
| 배치 Job | 매일 자정에 실행<br/>- 일별 매출 통계 저장<br/>- 주문 통계 집계하여 매출액, 주문 수, 상품별 통계 데이터 생성<br/>- 별도의 테이블에 저장 (통계 테이블) |
| 로그 | 주요 이벤트 로깅<br/>- console: 표준 콘솔 출력<br/>- 상품 관리 및 주문 관련 비즈니스 로직을 각각 별도의 로그 파일로 출력 (`business_product.log`, `business_order.log`)<br/>- rolling: 일일 단위 파일 롤링<br/>- 프로젝트 내부 `logs` 폴더에 보존 |
| Swagger (OpenAPI 3) | - 관리자/사용자용 API 명확히 분리 및 문서화<br/>- Swagger UI로 테스트 가능 |
| Validation | - 회원가입, 상품 등록 시 모든 입력 필드에 유효성 검사 적용 |
| Actuator | - 시스템 헬스(`health`), 빈(`bean`) 정보, 환경(`env`) 정보, 메트릭(`metric`) 정보 확인용 엔드포인트 제공 |



## 초기 데이터 설정

### data.sql을 이용한 환경 구성

**설정 방법**

1. `application.yml`에서 `spring.sql.init.mode: always`로 변경
2. 애플리케이션 실행 시 `resources/data/data.sql` 자동 실행

**포함된 테스트 데이터**

- **회원**: 관리자 1명(`hanaro/12345678`), 일반사용자 15명
- **상품**: 3개 상품 (갤럭시S25, 이펙티브자바, 고구마) + 이미지 8개
- **주문**: 4건 주문 (3건 완료, 1건 취소)
- **배송**: 4건 배송 정보 (3건 완료, 1건 취소)
- **통계**: 2025-08-11 매출 통계 (총매출 278만원, 평균주문 69만원)

**매출 현황 (2025-08-11)**

- 총 매출: 2,780,600원 (유효주문 3건)
- 인기 상품: 갤럭시S25(1,299,000원), 이펙티브자바(90,000원), 고구마(47,600원)

## Swagger API 문서 구조

### API 그룹 분리

**1. 관리자 API (`Admin API`)**

- **경로**: `/api/admin/**`
- **SignIn - 관리자 로그인**: `POST /api/admin/auth/signin` ⚠️ **관리자 계정: `hanaro/12345678`**
- **Admin - 회원 관리**: 회원 목록, 삭제, 검색
- **Admin - 상품 관리**: 상품 CRUD, 재고 관리, 상태 변경
- **Admin - 주문 관리**: 전체 주문 조회, 검색, 통계, 관리자 취소
- **Order Statistics - 주문 통계 관리**: 일별/상품별 매출 통계

**2. 사용자 API (`User API`)**

- **경로**: `/api/cart/**`, `/api/orders/**`
- **Cart - 장바구니**: CRUD, 수량 증감, 전체 비우기
- **Order - 주문 관리**: 장바구니 기반 주문, 내 주문 조회/취소

**3. 공통 API (`Public API`)**

- **경로**: `/api/auth/**`, `/api/members/**`, `/api/products/**`
- **Authentication**: 회원가입, 로그인, 토큰 관리
- **Member - 회원 정보**: 프로필 조회/수정, 비밀번호 변경
- **Product - 상품 조회**: 상품 목록, 상세, 카테고리별, 검색

## 🔐 Swagger API 인증 사용법

### 1. 회원가입 및 로그인

- **일반 사용자**: `POST /api/auth/signup` → `POST /api/auth/signin`
- **관리자**: 직접 `POST /api/admin/auth/signin` (계정: `hanaro/12345678`)

### 2. JWT 토큰 설정 방법

1. 로그인 API 호출하여 `accessToken` 획득
2. Swagger UI 상단의 🔒 **Authorize** 버튼 클릭
3. Value 입력란에 **accessToken 값만 입력** (Bearer 접두사 불필요)
4. **Authorize** 버튼 클릭하여 인증 완료

### 3. API 권한별 분류

#### 인증 불필요 (Public)

- **회원가입/로그인**: `/api/auth/**`, `/api/admin/auth/signin`
- **시스템 정보**: `/management/**` (Actuator), `/swagger-ui/**`

#### 인증 필요 (토큰만 있으면 됨)

- **상품 조회**: `/api/products/**` - 목록, 상세, 검색, 카테고리별 (USER/ADMIN 둘 다 접근 가능)
- **개인정보**: `/api/members/**` - 프로필 조회/수정, 비밀번호 변경

#### USER 권한 필요

- **장바구니**: `/api/cart/**` - 조회, 추가, 수정, 삭제, 증감
- **주문 관리**: `/api/orders/**` - 주문 생성, 조회, 취소
- **배송 조회**: `/api/deliveries/**` - 내 주문의 배송 상태 확인

#### ADMIN 권한 필요

- **회원 관리**: `/api/admin/members/**` - 전체 회원 조회, 삭제, 검색
- **상품 관리**: `/api/admin/products/**` - 상품 CRUD, 재고 관리, 상태 변경
- **주문 관리**: `/api/admin/orders/**` - 전체 주문 조회, 검색, 통계, 관리자 취소
- **매출 통계**: `/api/admin/statistics/**` - 일별/상품별 매출 통계 조회

### 4. 토큰 관리

- **토큰 갱신**: `POST /api/auth/refresh` (refreshToken 사용)
- **로그아웃**: `POST /api/auth/signout` (refreshToken 무효화)

### ⚠️ 관리자 로그인 주의사항

**관리자 계정 로그인은 반드시 `SignIn - 관리자 로그인` 그룹에서 진행**

- **URL**: `POST /api/admin/auth/signin`
- **계정**: `hanaro` / `12345678`
- **특징**: 이메일 validation 미적용 (관리자용 특별 계정)

### 배치작업 및 스케줄러 동작 확인 : 실시간 로그 모니터링을 통한 검증

**2025-08-11 23:31분~39분 사이에 실제 주문 데이터를 생성**하여 배치작업과 스케줄러의 정상 동작을 확인

#### 📅 테스트 시나리오
- **주문 생성 시점**: 2025-08-11 23:31분~39분
- **관찰 포인트**: 23:40분, 23:45분, 자정(00:00:10) 이후
- **확인 대상**: 배송 상태 자동 변경, 매출 통계 자동 생성

#### 스케줄러 동작 시점

1. 매 5분마다 실행 (PENDING → PREPARING)

   ```java
   @Scheduled(cron = "0 */5 * * * *") // 5분마다 실행
   public void updateDeliveryStatusToPreparing() { }
   ```

2. 매 15분마다 실행 (PREPARING → SHIPPING)

   ```java
   @Scheduled(cron = "0 */15 * * * *") // 15분마다 실행
   public void updateDeliveryStatusToShipping() { }
   ```

3. 매 1시간마다 실행 (SHIPPING → COMPLETED)

   ```java
   @Scheduled(cron = "0 0 * * * *") // 1시간마다 실행
   public void updateDeliveryStatusToCompleted() { }
   ```

#### 배치 작업 시점 (매일 자정 00:00:10)

- 매출 통계 자동 생성

   ```java
   @Scheduled(cron = "10 0 0 * * *") // 매일 자정에 실행
   public void generateDailySalesStatistics() { }
   ```

#### 로그 확인

- 로그 파일 위치

  - 배송/주문 스케줄러: `logs/business_order.log`
  - 상품 관리: `logs/business_product.log`
  - 과거 로그: `logs/business_*.yyyy-MM-dd.log` (30일 보관)

- 배송 스케줄러 로그

   ```
   [DELIVERY_SCHEDULER] PENDING to PREPARING: 2 deliveries updated
   [DELIVERY_SCHEDULER] PREPARING to SHIPPING: 3 deliveries updated, tracking numbers assigned
   [DELIVERY_SCHEDULER] SHIPPING to COMPLETED: 1 deliveries updated at 2025-08-12T00:00:08
   ```

- 매출 통계 배치 로그

   ```
   [BATCH_STATISTICS] Starting daily sales statistics generation for date: 2025-08-11
   [BATCH_STATISTICS] Daily sales statistics generated successfully: totalSales=2780600, totalOrders=4
   [BATCH_STATISTICS] Daily product statistics generated successfully: 3 products processed
   ```

### 🖥️ Actuator를 통한 시스템 상태 확인

- `http://localhost:8080/management/health` - 애플리케이션 상태 정보
- `http://localhost:8080/management/metrics` - 메트릭 정보
- `http://localhost:8080/management/env` - 환경 변수
- `http://localhost:8080/management/loggers` - 로그 레벨
- `http://localhost:8080/management/beans` - 스프링 애플리케이션 컨텍스트의 빈