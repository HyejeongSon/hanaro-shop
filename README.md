# 쇼핑몰 서비스

**과정명** [하나은행] Digital hana 路 금융서비스개발 6기 쇼핑몰 서비스

**진행 기간** 8월 7일(목) ~ 8월 12일(화)

## 쇼핑몰 서비스 – 요구사항

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

## 쇼핑몰 서비스 – 기능 명세

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

## Swagger API 인증 사용법

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

#### 테스트 시나리오
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

### Actuator를 통한 시스템 상태 확인

- `http://localhost:8080/management/health` - 애플리케이션 상태 정보
- `http://localhost:8080/management/metrics` - 메트릭 정보
- `http://localhost:8080/management/env` - 환경 변수
- `http://localhost:8080/management/loggers` - 로그 레벨
- `http://localhost:8080/management/beans` - 스프링 애플리케이션 컨텍스트의 빈


## 구현된 주요 기능 상세

<details>
<summary><strong>보안 및 인증 시스템 (Security & JWT)</strong></summary>

### 📝 Summary
보안 및 인증 시스템 구현, JWT 토큰 기반 인증과 ADMIN/USER 역할 구분을 통한 세밀한 권한 관리 제공

### ✅ 구현된 주요 기능
1. **JWT 토큰 시스템**
    - Access Token: 1시간 만료, API 인증용
    - Refresh Token: 7일 만료, 토큰 재발급용
    - 토큰 검증: HMAC SHA-256 서명 알고리즘
    - Authorization Header: Bearer {token} 방식

2. **사용자 관리 시스템**
    - 회원가입: 강력한 validation 적용 (이메일, 비밀번호 패턴, 필수 필드)
    - 로그인: 이메일/비밀번호 인증
    - 토큰 재발급: 리프레시 토큰을 통한 자동 갱신
    - 로그아웃: 리프레시 토큰 완전 삭제

3. **권한 기반 접근 제어**
    - ROLE_ADMIN: 관리자 전용 API (/api/admin/**)
    - ROLE_USER: 일반 사용자 API (/api/user/**)
    - Public: 인증 불필요 API (/api/auth/**, Swagger, Actuator)

### 📁 구현된 파일 목록
- **Domain Layer**: Member.java, RefreshToken.java, Role.java
- **Security Layer**: SecurityConfig.java, JwtTokenProvider.java, JwtAuthenticationFilter.java
- **API Layer**: AuthController.java, MemberController.java, AuthService.java
- **DTO Layer**: SignUpRequest.java, SignInRequest.java, TokenResponse.java

### 🔧 API 엔드포인트
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/signin` - 로그인
- `POST /api/auth/refresh` - 토큰 재발급
- `POST /api/auth/signout` - 로그아웃
- `GET /api/auth/me` - 내 정보 조회
</details>

<details>
<summary><strong>도메인 엔티티 설계</strong></summary>

### 📝 Summary
쇼핑몰 서비스의 9개 핵심 비즈니스 엔티티 구현 (통계 엔티티 포함)
- 상품, 장바구니, 주문, 배송, 통계 관리를 위한 도메인 모델 구축
- 비즈니스 로직과 데이터 무결성을 보장하는 엔티티 설계

### 구현된 엔티티 및 관계

#### 상품 & 이미지
- `Product (1) — (N) ProductImage`
    - 상품 정보, 재고 및 활성화 관리
    - 상품 이미지, 메인/썸네일 구분

#### 장바구니
- `Member (1) — (1) Cart`
- `Cart (1) — (N) CartItem`
- `CartItem (N) — (1) Product`
    - 회원별 장바구니
    - 장바구니 상품 수량 및 가격 계산

#### 주문
- `Member (1) — (N) Order`
- `Order (1) — (N) OrderItem`
- `OrderItem (N) — (1) Product`
    - 주문 정보 및 상태 관리
    - 주문 상품, 자동 재고 차감 및 총 가격 계산

#### 배송
- `Order (1) — (1) Delivery`
    - 배송 정보, 상태별 시간 기록 및 상태 변경 자동 처리

#### 통계
- `DailySalesStatistics` - 일별 매출 통계
- `DailyProductStatistics` - 상품별 일별 통계

### 주요 비즈니스 로직
- 재고 부족 시 주문 생성 제한
- 주문 취소 시 자동 재고 복구
- 배송 상태 변경 시 시간 자동 기록
- 장바구니 총액 및 수량 자동 계산
</details>

<details>
<summary><strong>상품 관리 시스템</strong></summary>

### 📝 Summary
하나로 쇼핑몰의 상품 관리 시스템 구현
- 상품 생명주기 관리, 다중 이미지 처리, 파일 시스템, 검색/필터링, 관리자/사용자 API 분리까지 포함한 시스템

### ✅ 핵심 구현 내용
1. **상품 CRUD 시스템**
    - CREATE: 상품 등록 + 다중 이미지 업로드 (@ModelAttribute 방식)
    - READ: 단일 조회, 목록 조회(페이징), 카테고리별 조회, 키워드 검색
    - UPDATE: 전체 수정 + 이미지 교체, 재고만 수정, 상태 변경(활성화/비활성화)
    - DELETE: 완전 삭제 (이미지 파일까지 물리적 삭제)

2. **이미지 처리 시스템**
    - 다중 파일 업로드 지원 (최대 3MB, 개별 512KB)
    - 자동 썸네일 생성 (200x200px, Thumbnailator 사용)
    - UUID 기반 파일명으로 중복 방지
    - 날짜별 폴더 구조 (yyyy/MM/dd)
    - 완전한 URL 제공: `http://localhost:8080/upload/2025/08/10/uuid_file.jpg`

3. **관리자/사용자 API 명확한 분리**
    - 패키지 기반 물리적 분리: `controller/`, `controller/admin/`

### 📁 아키텍처 & 파일 구조
- **Controller**: AdminProductController, ProductController
- **Service**: ProductService, FileUploadService
- **Repository**: ProductRepository (최적화된 DB 조회 메서드)
- **DTO**: ProductRequest, ProductResponse, ProductImageResponse
- **Mapper**: ProductMapper (MapStruct)
- **Config**: WebConfig (정적 리소스 매핑)

### 🔧 API 엔드포인트
#### 관리자 전용 API (/api/admin/products)
- `POST /api/admin/products` - 상품 등록
- `PUT /api/admin/products/{id}` - 상품 수정
- `PATCH /api/admin/products/{id}/stock` - 재고 수정
- `DELETE /api/admin/products/{id}` - 상품 삭제

#### 공용 API (/api/products)
- `GET /api/products/{id}` - 상품 상세 조회
- `GET /api/products` - 상품 목록 조회 (페이징)
- `GET /api/products/category/{category}` - 카테고리별 조회
- `GET /api/products/search?keyword={keyword}` - 키워드 검색
</details>

<details>
<summary><strong>장바구니 시스템</strong></summary>

### 📝 Summary
장바구니 시스템 구현 (CRUD + 고급 기능)
- JWT 인증 기반 개인 장바구니 관리
- 지연 생성(Lazy Creation) 패턴으로 최적화된 장바구니 생성
- 재고 검증, 썸네일 표시, 상품 증감 기능 포함

### ✅ 핵심 구현 내용
1. **기본 CRUD**
    - 장바구니 조회: 개인 장바구니 및 상품 목록 조회 (썸네일 이미지 포함)
    - 상품 추가: 재고 검증 후 장바구니 상품 추가/기존 상품 수량 자동 증가
    - 수량 변경: 장바구니 내 특정 상품 수량 절대값 수정
    - 상품 제거: 장바구니에서 개별 상품 완전 삭제
    - 전체 비우기: 장바구니 모든 상품 제거

2. **고급 기능**
    - 상품 수량 증가/감소: +/- 버튼 대응 API
    - 상품 개수/종류 조회: UI 뱃지용 통계 정보
    - 요약 정보 조회: 총 수량, 종류 수, 총 금액 한 번에 제공

### 📁 아키텍처 & 파일 구조
- **Controller**: CartController (사용자 전용 API)
- **Service**: CartService, CartServiceImpl (지연 생성 패턴)
- **Repository**: CartRepository, CartItemRepository (LEFT JOIN FETCH 최적화)
- **DTO**: CartItemRequest, CartResponse, CartItemResponse
- **Mapper**: CartMapper (썸네일 URL 자동 생성)

### 🔧 API 엔드포인트
#### 기본 CRUD (/api/cart)
- `GET /api/cart` - 내 장바구니 조회
- `POST /api/cart/items` - 장바구니에 상품 추가
- `PUT /api/cart/items/{productId}` - 수량 변경
- `DELETE /api/cart/items/{productId}` - 상품 제거
- `DELETE /api/cart` - 전체 비우기

#### 통계 및 증감 API
- `GET /api/cart/count` - 상품 개수 조회
- `GET /api/cart/types` - 상품 종류 개수 조회
- `PATCH /api/cart/items/{productId}/increase` - 수량 증가
- `PATCH /api/cart/items/{productId}/decrease` - 수량 감소

### 보안 & 최적화
- @PreAuthorize("hasRole('USER')") 권한 검증
- 지연 생성 패턴으로 리소스 절약
- LEFT JOIN FETCH로 N+1 문제 해결
- 스마트 수량 감소 (0이 되면 자동 제거)
</details>

<details>
<summary><strong>주문 관리 시스템</strong></summary>

### 📝 Summary
장바구니 기반 주문 관리 시스템 구현
- 사용자는 장바구니에서 선택한 상품들로 주문을 생성
- 관리자는 주문 조회/검색/통계 분석 가능

### ✅ 핵심 구현 내용
1. **주문 생성 및 관리**
    - 장바구니→주문 변환: 선택된 장바구니 아이템들로 주문 생성
    - 재고 관리 연동: 주문 시 자동 재고 차감, 취소 시 재고 복구
    - 주문 취소: 사용자/관리자 주문 취소 기능
    - 배송 정보 연동: 주문과 배송 정보 1:1 매핑

2. **관리자 기능**
    - 주문 검색: 주문번호, 회원 이메일, 상태별 검색
    - 주문 통계: 전체/오늘 매출 통계 (유효 주문 기준)
    - 주문 관리: 전체 주문 조회 및 관리자 권한 취소

3. **데이터 검증 강화**
    - 장바구니 아이템 검증: 존재하지 않는 아이템 ID 처리
    - 권한 검증: 본인 장바구니/주문만 접근 가능
    - 상품 상태 검증: 비활성화된 상품 주문 방지

### 📁 아키텍처 & 파일 구조
- **Controller**: OrderController, AdminOrderController, DeliveryController
- **Service**: OrderService, DeliveryService
- **Repository**: OrderRepository, OrderItemRepository, DeliveryRepository
- **DTO**: OrderCreateRequest, OrderSearchRequest, OrderResponse, DeliveryResponse
- **Mapper**: OrderMapper

### 🔧 API 엔드포인트
#### 사용자 API (/api/orders)
- `POST /api/orders/cart` - 장바구니에서 주문 생성
- `GET /api/orders/{orderId}` - 주문 상세 조회
- `GET /api/orders` - 내 주문 목록 조회
- `DELETE /api/orders/{orderId}` - 주문 취소

#### 관리자 API (/api/admin/orders)
- `GET /api/admin/orders` - 전체 주문 조회
- `GET /api/admin/orders/search` - 주문 검색
- `GET /api/admin/orders/summary` - 주문 통계
- `DELETE /api/admin/orders/{orderId}` - 관리자 주문 취소

#### 배송 API (/api/deliveries)
- `GET /api/deliveries/{orderId}` - 배송 정보 조회

### 주요 비즈니스 로직
#### 주문 생성 프로세스
1. 선택된 장바구니 아이템 조회 및 검증
2. 사용자 권한 및 상품 활성화 상태 확인
3. 재고 부족 확인 및 차감
4. 주문 및 배송 정보 생성
5. 장바구니에서 주문된 아이템 삭제

#### 통계 계산 로직
- 유효 주문: OrderStatus.ORDERED 상태만 집계
- 취소 주문: 매출 통계에서 제외
- 일별 구분: 오늘/전체 주문 통계 분리
</details>

<details>
<summary><strong>배송 스케줄링 시스템</strong></summary>

### 📝 Summary
배송 상태 자동 업데이트 배치 작업 구현
- `PENDING` → `PREPARING` (5분마다)
- `PREPARING` → `SHIPPING` (15분마다, 운송장 번호 자동 생성)
- `SHIPPING` → `COMPLETED` (1시간마다, 배송 완료 시간 기록)
- JPA `@Modifying` 쿼리로 일괄 상태 변경 최적화

### ✅ 핵심 구현 내용
1. **스케줄링 구현**
   ```java
   @Scheduled(cron = "0 */5 * * * *")   // 5분마다 실행
   @Scheduled(cron = "0 */15 * * * *")  // 15분마다 실행  
   @Scheduled(cron = "0 0 * * * *")     // 1시간마다 실행
   ```

2. **배송 상태 자동 업데이트**
    - PENDING → PREPARING: 결제 완료된 주문을 배송 준비 상태로 변경
    - PREPARING → SHIPPING: 운송장 번호 자동 생성 및 출고 시간 기록
    - SHIPPING → COMPLETED: 배송 완료 시간 기록

3. **성능 최적화**
    - Repository 단 일괄 업데이트 쿼리
    - @Modifying + @Query로 벌크 연산 처리
    - 처리 건수 로깅으로 정상 동작 확인

### 📁 구현 파일
- **Service**: SchedulerService, SchedulerServiceImpl
- **Repository**: DeliveryRepository (배치용 일괄 업데이트 쿼리)

### 🔧 스케줄링 설정
```java
@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10); // 10개 스레드 풀
    scheduler.setThreadNamePrefix("hanaro-scheduler-");
}
```
</details>

<details>
<summary><strong>주문 통계 배치 시스템</strong></summary>

### 📝 Summary
관리자용 일별 매출·상품 통계 시스템 구현
- 매일 자정 전날 주문 데이터 기반 자동 통계 집계 및 저장

### ✅ 핵심 구현 내용
1. **일별 통계 자동 생성 배치**
   ```java
   @Scheduled(cron = "10 0 0 * * *") // 매일 자정 10초에 실행
   public void generateDailySalesStatistics()
   ```

2. **통계 생성 프로세스**
    - 처리 대상: 전날(`LocalDate.now().minusDays(1)`) 주문 데이터
    - 중복 방지: 기존 통계 존재 시 생성 건너뛰기
    - Native Query 활용: Repository에서 DB 레벨 집계 처리

3. **일별 매출 통계 생성**
    - 총 매출액 (유효 주문 기준)
    - 총 주문 수 / 총 상품 수
    - 취소 주문 수 / 취소 금액
    - 평균 주문 금액 자동 계산

4. **상품별 통계 생성**
    - 상품별 판매량, 매출, 주문건수
    - Product 엔티티 연관관계 매핑
    - Batch 처리로 일괄 저장

### 📁 아키텍처 & 파일 구조
- **Entity**: DailySalesStatistics, DailyProductStatistics
- **Controller**: AdminStatisticsController
- **Service**: BatchService, StatisticsService
- **Repository**: DailySalesStatisticsRepository, DailyProductStatisticsRepository
- **DTO**: DailySalesStatisticsResponse, DailyProductStatisticsResponse, ProductSummaryResponse
- **Mapper**: StatisticsMapper

### 🔧 API 엔드포인트 (/api/admin/statistics)
- `GET /daily?page=0&size=10` - 일별 매출 통계 페이징 조회
- `GET /daily/{date}` - 특정 날짜 매출 통계
- `GET /products/{date}` - 특정 날짜 상품별 통계
- `GET /products?startDate={start}&endDate={end}` - 기간별 상품별 통계

### 데이터 안정성
- Null 안전 처리: 모든 집계 값에 대한 null 체크
- 상품 누락 처리: 삭제된 상품 ID 발견 시 로그 기록 후 제외
- 트랜잭션 관리: 일별/상품별 통계 하나의 트랜잭션으로 처리
- 상세 로깅: 성공/경고/에러 상황별 로그 기록
</details>

<details>
<summary><strong>예외처리 시스템</strong></summary>

### 📝 Summary
체계적인 예외처리 아키텍처 구축
- 일관된 에러 응답 형식 제공
- 보안 강화된 인증/인가 예외처리

### ✅ 핵심 구현 내용
1. **예외처리 인프라 구축**
    - ErrorCode enum: 40개 체계적 에러 코드와 HTTP 상태 정의
    - BusinessException: 커스텀 비즈니스 예외 클래스
    - CustomJwtException: JWT 관련 전용 예외
    - ErrorResponse: 타임스탬프 포함 통일된 에러 응답 DTO
    - GlobalExceptionHandler: @ControllerAdvice 전역 예외 처리기

2. **보안 강화**
    - 로그인 실패 시 일관된 INVALID_PASSWORD 메시지로 계정 열거 공격 차단
    - JWT 토큰 파싱 실패 시 적절한 예외 처리
    - 파일 업로드 시 타입/크기 검증 강화

3. **Service Layer 예외처리 적용**
    - ProductServiceImpl: 상품 CRUD, 재고 관리 예외
    - CartServiceImpl: 장바구니 로직, 재고 검증 예외
    - AuthServiceImpl: 인증/인가, 토큰 관리 예외
    - FileUploadServiceImpl: 파일 업로드 검증 예외

### 에러 코드 분류 예시
```java
// 인증/인가: A001~A004
UNAUTHORIZED, ACCESS_DENIED, INVALID_TOKEN, EXPIRED_TOKEN

// 회원 관리: M001~M003
MEMBER_NOT_FOUND, EMAIL_ALREADY_EXISTS, INVALID_PASSWORD

// 상품 관리: P001~P003
PRODUCT_NOT_FOUND, PRODUCT_OUT_OF_STOCK, PRODUCT_INACTIVE

// 장바구니: C001~C003
CART_NOT_FOUND, CART_ITEM_NOT_FOUND, INVALID_QUANTITY
```

### 📁 구현된 파일들
- **Exception**: ErrorCode, BusinessException, CustomJwtException, ErrorResponse, GlobalExceptionHandler

### 주요 개선사항
- IllegalArgumentException → BusinessException 전환
- HTTP 상태 코드와 비즈니스 로직 연계
- 프론트엔드 친화적 에러 메시지 (한글 + 에러코드)
- 로깅을 통한 디버깅 편의성 향상
- 모든 예외의 일관된 JSON 응답 형식
</details>

<details>
<summary><strong>로깅 시스템</strong></summary>

### 📝 Summary
주요 이벤트 로깅 시스템 구현
- rolling: 일일 단위 파일 롤링
- 비즈니스 로직별 분리 로깅

### ✅ 핵심 구현 내용
1. **로깅 설정 (logback-spring.xml)**
    - Console 출력: 표준 콘솔 로그
    - business_product.log: 상품 관리 비즈니스 로그 분리
    - business_order.log: 주문/장바구니/배송/스케줄러/배치 관리 비즈니스 로그 분리
    - 일일 롤링: `business_product.2025-08-12.log` 형태로 자동 롤링
    - 보존 정책: 30일 보존, 최대 1GB 용량 제한

2. **비즈니스 로깅 태그 체계**
    - **상품 관리**: [PRODUCT_CREATE], [PRODUCT_UPDATE], [PRODUCT_STOCK], [PRODUCT_DELETE], [PRODUCT_STATUS]
    - **주문 관리**: [ORDER_CREATE], [ORDER_CANCEL], [ORDER_ADMIN_CANCEL], [ORDER_VIEW], [ORDER_LIST]
    - **장바구니 관리**: [CART_ADD], [CART_UPDATE], [CART_REMOVE], [CART_CLEAR], [CART_INCREASE], [CART_DECREASE]
    - **배송 스케줄러**: [DELIVERY_SCHEDULER] (상태 변경별 처리 건수 기록)
    - **매출 통계 배치**: [BATCH_STATISTICS] (통계 생성 성공/실패 로그)

### 📁 로그 파일 위치 및 구조
```
hanaro/logs/
├── business_product.log          # 오늘의 상품 관리 로그
├── business_product.2025-08-12.log  # 과거 상품 관리 로그
├── business_order.log            # 오늘의 주문/장바구니/배송/스케줄러/배치 로그
├── business_order.2025-08-12.log    # 과거 주문 관련 로그
└── ...
```

### 로그 롤링 정책
- **롤링 주기**: 매일 자정에 자동 롤링
- **파일명 형식**: `business_*.yyyy-MM-dd.log`
- **보존 기간**: 30일간 보관
- **용량 제한**: 전체 로그 파일 최대 1GB
- **동시 출력**: 파일 저장 + 콘솔 출력

### 📁 새로 생성된 파일들
- **Config**: logback-spring.xml (로깅 설정)
- **Directory**: logs/ (로그 파일 저장 디렉토리)
</details>
