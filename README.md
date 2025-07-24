### 프로젝트 트리

kr.hhplus.be.server
├── config
├── coupon                           # 쿠폰 도메인
├── order                            # 주문 도메인
├── payment                          # 결제 도메인
│   ├── application                  # Application 계층: 유스케이스(비즈니스 흐름) 구현
│   │   ├── dto                      # Application 계층용 Command / Response DTO
│   │   │   ├── PaymentDto
│   │   │   └── SavePaymentCommand
│   │   ├── facade                   # 복합 유스케이스 조합을 담당하는 파사드 계층
│   │   │   └── PaymentFacade
│   │   ├── service                  # 유스케이스 구현체 (Service)
│   │   │   ├── FindByOrderIdService
│   │   │   └── SavePaymentService
│   │   └── usecase                  # 유스케이스 인터페이스
│   │       ├── FindByOrderIdUseCase
│   │       └── SavePaymentUseCase
│   ├── domain                       # Domain 계층 (도메인 모델, 비즈니스 로직)
│   │   ├── model                    # 도메인 모델 (불변 객체 중심)
│   │   │   └── Payment
│   │   ├── repository               # 도메인 저장소 인터페이스
│   │   │   └── PaymentRepository
│   │   └── type                     # 도메인 내 Enum 타입
│   │       └── PaymentStatus
│   ├── infrastructure              # 인프라 계층 (JPA, DB, 외부 연동 등)
│   │   ├── entity                   # JPA 엔티티 (DB 매핑 전용)
│   │   │   └── PaymentJpaEntity
│   │   ├── mapper                   # 도메인 <-> 엔티티 매핑 클래스
│   │   │   └── PaymentMapper
│   │   └── repository               # 저장소 구현체
│   │       ├── PaymentJpaRepository      # Spring Data JPA 리포지토리 인터페이스
│   │       └── PaymentRepositoryImpl    # PaymentRepository 구현체
│   └── presentation                # Presentation 계층 (HTTP API 진입점)
│       ├── contract                # API 스펙 인터페이스 (Swagger 등)
│       │   └── PaymentApiSpec
│       ├── dto                     # 요청/응답 DTO
│       │   ├── PaymentRequest
│       │   └── PaymentResponse
│       └── web                     # 실제 Controller
│           └── PaymentController
├── popularproduct                  # 인기상품 도메인
├── product                         # 상품 도메인
├── transactionhistory              # 거래내역 도메인
└── user                            # 유저 도메인

### 아키텍처 설명

레이어드 + 인터페이스 아키텍처에서 시작해 클린 아키텍처, DDD, MSA 원칙을 반영하기를 지향한 아키텍처.

### 의존성 흐름

[ Presentation ]
↓        (유스케이스 호출)
[ Application ]
↓        (도메인 모델 로직 실행)
[ Domain ]
↑        (리포지토리 구현 주입)
[ Infrastructure ]
 