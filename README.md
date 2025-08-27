### 프로젝트 구조

```text
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
```
---
### 아키텍처 설명

본 프로젝트는 레이어드 아키텍처와 인터페이스 아키텍처를 기반으로 시작하여, 클린 아키텍처, DDD(Domain-Driven Design), MSA 설계 원칙을 반영하는 구조를 지향합니다.

---
### 의존성 흐름

```text
[ Presentation ]        → HTTP API 진입점
       ↓
[ Application ]         → 유스케이스 정의 및 실행
       ↓
[ Domain ]              → 순수 도메인 모델 및 핵심 로직
       ↑
[ Infrastructure ]      → JPA, 외부 API 연동, 데이터 접근 구현
```
모든 의존성은 안쪽 계층을 향해 흐릅니다.

Infrastructure는 Domain 및 Application 계층의 구현을 담당하되, 의존하지 않습니다.

---

### 도메인 모델과 JPA Entity 분리 이유
본 프로젝트에서는 도메인 모델(domain.model)과 JPA Entity(infrastructure.entity)를 명확히 분리하였습니다. 그 이유는 다음과 같습니다:

비즈니스 로직 순수성 유지
도메인 모델은 @Entity, @Column 등 JPA 기술에 전혀 의존하지 않도록 설계되었습니다.

이를 통해 순수한 객체 지향 설계와 단위 테스트가 가능해지며, 유지보수성도 높아집니다.

유연한 영속성 교체
도메인 모델은 JPA뿐 아니라 MongoDB, Redis, 외부 API 등 다양한 영속성 기술과 연동 가능하도록 설계됩니다.

JPA Entity는 단순히 DB 저장을 위한 구조로써만 사용되며, 도메인 모델과 분리되어 변경에 강합니다.

테스트 용이성
도메인 객체는 JPA 영속성 컨텍스트 없이도 테스트할 수 있어, 외부 환경에 의존하지 않는 순수 단위 테스트 작성이 가능합니다.

TDD 실천에 유리하며, 도메인 로직을 독립적으로 검증할 수 있습니다.

관심사 분리
도메인 모델은 **무엇을 해야 하는가(비즈니스 행위)**에만 집중합니다.

JPA Entity는 **어떻게 데이터를 저장하고 조회할 것인가(기술적 세부사항)**에 집중합니다.

이를 통해 가독성과 책임 분리가 명확해지고, 향후 구조 확장에도 유연하게 대응할 수 있습니다.

---

### 그래도 JPA Entity 로 병합을 고려해야 하는 이유
"DIP를 적용하는 주된 이유는 저수준 구현이 변경되더라도 고수준이 영향을 받지 않도록 하기 위함이다.

하지만, 리포지터리와 도메인 모델의 구현 기술은 거의 바뀌지 않는다.

필자는 JPA로 구현한 리포지터리 구현 기술을 마이바티스나 다른 기술로 변경한 적이 없고,

RDBMS를 사용하다 몽고DB로 변경한 적도 없다. 이렇게 변경이 거의 없는 상황에서 변경을 미리 대비하는 것은 과하다고 생각한다."

-최범균, 저서 도메인 주도 개발-


하지만 학습을 위해 현재의 분리된 구조로 진행하기로 결정했습니다. 

---

### 설계 철학 요약
도메인 중심 설계(DDD): 핵심 비즈니스 개념과 로직을 명확하게 표현.

클린 아키텍처 지향: 의존성 방향을 안쪽으로, 내부 규칙 보호.

인터페이스 기반 분리: 구현보다 추상에 의존.

MSA 대응 가능 구조: 각 도메인을 모듈처럼 나눌 수 있도록 설계.


 