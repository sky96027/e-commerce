## 목차

---

1. [유저](#유저)
2. [상품](#상품)
3. [쿠폰](#쿠폰)
4. [주문](#주문)
5. [결제](#결제)
6. [인기 상품](#인기-상품)


## 유저

---

1. **잔액 조회**

```mermaid
sequenceDiagram
    actor User as 사용자
    participant BalanceAPI as 잔액 API
    participant UserDB as 유저 DB

    User ->> BalanceAPI: 잔액 조회 요청 (userId)
    activate BalanceAPI
    BalanceAPI ->> UserDB: 현재 잔액 조회
    activate UserDB

    alt 옳지 않은 userId
        UserDB -->> BalanceAPI: 조회 실패 반환
        BalanceAPI -->> User: 조회 실패 반환
    else
        UserDB -->> BalanceAPI: 잔액 정보 반환
        deactivate UserDB
        BalanceAPI -->> User: 잔액 정보 반환
    end
    deactivate BalanceAPI
```

2. **잔액 충전**

```mermaid
sequenceDiagram
    Actor User as 사용자
    participant BalanceAPI as 잔액 API
    participant TradeHistoriesAPI as 거래 내역 API
    participant UserDB as 유저 DB
    participant TradeHistoriesDB as 거래내역 DB

    User->>BalanceAPI: 잔액 충전 요청 (userId, amount, 현재 시간)
    activate BalanceAPI
   
    BalanceAPI->>UserDB: 현재 잔액 조회
    activate UserDB
    UserDB-->>BalanceAPI: 현재 잔액 반환
    deactivate UserDB

    BalanceAPI->>BalanceAPI: 유효성 검사 (양수 여부, 최대 한도 미만인지)
     alt 유효성 통과
        BalanceAPI->>UserDB: 잔액 업데이트 (기존 + 충전금액)
        activate UserDB
        UserDB-->>BalanceAPI: 저장 성공 메시지
        deactivate UserDB

        BalanceAPI->>TradeHistoriesAPI: 거래 내역 기록 요청 (거래 내역Id, userId, type=충전, amount, 현재 시간 등)
        activate TradeHistoriesAPI
        TradeHistoriesAPI ->> TradeHistoriesDB: 거래 내역 저장
        activate TradeHistoriesDB
        TradeHistoriesDB -->> TradeHistoriesAPI: 거래 내역 저장 완료 메시지 
        deactivate TradeHistoriesDB
        TradeHistoriesAPI -->> BalanceAPI: 기록 완료 메시지
        deactivate TradeHistoriesAPI

        BalanceAPI-->>User: 충전 성공 반환
    else 유효성 실패
        BalanceAPI-->>User: 충전 실패 예외 반환
    end
    deactivate BalanceAPI
```

3. **거래 내역 조회**

```mermaid
sequenceDiagram
   actor User as 사용자
   participant BalanceAPI as 거래 내역 API
   participant TransactionHistoryDB as 거래 내역 DB

   User ->> BalanceAPI: 거래 내역 조회 요청 (userId)
   activate BalanceAPI

   BalanceAPI ->> TransactionHistoryDB: 거래 내역 조회 (userId 기준)
   activate TransactionHistoryDB
   TransactionHistoryDB -->> BalanceAPI: 거래 내역 리스트 반환
   deactivate TransactionHistoryDB

   BalanceAPI -->> User: 거래 내역 JSON 응답
   deactivate BalanceAPI
```



### 상품

---

1. 상품 목록 조회

```mermaid
sequenceDiagram
    Actor User as 사용자
    participant ProductAPI as 상품 API
    participant ProductDB as 상품 DB

    User ->> ProductAPI: 상품 목록 조회 요청
    activate ProductAPI
    ProductAPI ->> ProductDB: (재고 > 0인 상품 옵션 갯수) > 0인 상품 조회
    activate ProductDB
    ProductDB -->> ProductAPI: 상품 리스트 반환
    deactivate ProductDB
    ProductAPI -->> User: 리스트(상품Id, 상품 이름, 가격) 반환
    deactivate ProductAPI
```

1. 상품 상세 조회

```mermaid
sequenceDiagram
    actor User as 사용자
    participant ProductAPI as 상품 API
    participant ProductDB as 상품 DB
    participant ProductOptionDB as 상품 옵션 DB

    User ->> ProductAPI: 상품 상세 조회 요청 (상품 Id)
    activate ProductAPI
    ProductAPI ->> ProductDB: 상품 정보 조회 (상품 Id)
    activate ProductDB
    ProductDB -->> ProductAPI: 상품 (상품Id, 상품 이름 등) 데이터 반환
    deactivate ProductDB
    ProductAPI ->> ProductOptionDB: 해당 상품 옵션 목록 조회 (상품 Id)
    activate ProductOptionDB
    ProductOptionDB -->> ProductAPI: 옵션 (상품 옵션 Id, 상품 옵션 내용, 상품 가격, 상품 재고) 반환
    deactivate ProductOptionDB
    alt 모든 옵션 재고가 0 (타아밍 이슈 예외 케이스)
        ProductAPI -->> User: 품절 알림 
    else 재고 있는 옵션 존재
        ProductAPI -->> User: 상품 + 옵션 정보 반환
    end
    deactivate ProductAPI
```

### 쿠폰

---

1. 쿠폰 조회

```mermaid
sequenceDiagram
    actor User as 사용자
    participant CouponAPI as 쿠폰 API
    participant UserCouponDB as 유저 쿠폰 DB 

    User ->> CouponAPI: 쿠폰 조회 요청 (userId)
    activate CouponAPI
    CouponAPI ->> UserCouponDB: 해당 유저의 유효한 쿠폰 목록 조회
    activate UserCouponDB
    UserCouponDB -->> CouponAPI: 쿠폰 데이터(couponId, 쿠폰 이름, 할인율, 만료기간, userId, 사용여부 등) 반환
    deactivate UserCouponDB
    CouponAPI -->> User: 유효한 쿠폰 리스트 반환
    deactivate CouponAPI
```

1. 쿠폰 발급

```mermaid
sequenceDiagram
    actor User as 사용자
    participant CouponAPI as 쿠폰 API (HTTP)
    participant Kafka as Kafka 토픽(coupon-issue)
    participant Consumer as CouponIssueConsumer(컨슈머)
    participant Redis as Redis 재고카운터
    participant CouponDB as 쿠폰 이슈 DB
    participant UserCouponDB as 유저 쿠폰 DB

    User ->> CouponAPI: 쿠폰 발급 요청 (couponId, userId, policyId, ...)
    activate CouponAPI

    %% 1) API는 즉시 이벤트 발행
    CouponAPI ->> Kafka: publish CouponIssueRequestedEvent(rid, userId, couponId, ...)
    CouponAPI -->> User: 202 Accepted + reservationId(rid)
    deactivate CouponAPI

    %% 2) 컨슈머가 이벤트를 비동기 처리
    Kafka -->> Consumer: CouponIssueRequestedEvent(rid, ...)
    activate Consumer

    %% 2-1) 재고 선점 (원자 감소)
    Consumer ->> Redis: tryDecrement(couponId, 1)
    alt 재고 없음(-1) 또는 미초기화(-2)
        Consumer -->> Kafka: 실패 로그/메트릭(rid, couponId)
    else 재고 선점 성공
        %% 2-2) DB write-through
        Consumer ->> CouponDB: decrementRemaining(couponId) where remaining > 0
        alt DB 차감 실패(경쟁으로 0 이하)
            Consumer ->> Redis: compensate(+1)
            Consumer -->> Kafka: 보상 처리 로그(rid)
        else DB 차감 성공
            %% 2-3) 유저 쿠폰 저장 (유니크 인덱스로 중복 방지)
            Consumer ->> UserCouponDB: insertOrUpdate(userId, couponId, policySnapshot, expiredAt ...)
            alt 중복 등으로 INSERT 실패
                Consumer ->> Redis: compensate(+1)
                Consumer -->> Kafka: 실패/보상 로그(rid)
            else 저장 성공
                Consumer -->> Kafka: 성공 로그(rid, userId, couponId)
            end
        end
    end
    deactivate Consumer
```

### 주문

---

1. 주문

```mermaid
sequenceDiagram
    actor User as 사용자
    participant OrderAPI as 주문 API
    participant ProductOptionDB as 상품 옵션 DB
    participant OrderDB as 주문 DB
    participant UserCouponDB as 쿠폰 DB

    User ->> OrderAPI: 주문 요청 (상품ID, 옵션ID, 수량, 쿠폰ID, userId)
    activate OrderAPI

    %% 1. 재고 확인
    OrderAPI ->> ProductOptionDB: 옵션 재고 확인 (옵션ID)
    alt 재고 부족
        OrderAPI -->> User: 주문 실패 (재고 부족)
    else
        %% 2. 쿠폰 유효성 확인
        OrderAPI ->> UserCouponDB: 쿠폰 조회 및 유효성 확인 (유저 쿠폰ID, userId)
        alt 쿠폰 무효
            OrderAPI -->> User: 쿠폰 적용 실패 (쿠폰 사용 불가)
        else

            %% 3. 상품 정보 조회 및 금액 계산
            loop 상품별
                OrderAPI ->> ProductOptionDB: 상품 정보 조회 (상품ID, 옵션ID)
                activate ProductOptionDB
                ProductOptionDB -->> OrderAPI: 상품 정보 반환 (가격 등)
                deactivate ProductOptionDB
            end

            OrderAPI ->> OrderAPI: 총 금액 계산 (총 금액 - 쿠폰할인(특정 상품 대상))

            %% 4. 주문 저장
            OrderAPI ->> OrderDB: 주문 저장 요청 (주문ID, userId, {상품ID, 옵션ID, 수량, 쿠폰ID}, 총 금액, status)
            activate OrderDB
            OrderDB -->> OrderAPI: 저장 완료
            deactivate OrderDB

            OrderAPI -->> User: 주문 생성 완료 응답
        end
    end
    deactivate OrderAPI
```

### 결제

---

1. 결제

```mermaid
sequenceDiagram
    actor User as 사용자
    participant PaymentAPI as 결제 API (PaymentFacade)
    participant OrderDB as 주문 DB
    participant ProductSvc as 상품 옵션/재고
    participant UserDB as 유저 잔액
    participant UserCouponDB as 유저 쿠폰 DB
    participant PaymentDB as 결제 DB
    participant Kafka as Kafka 토픽(payment-completed)
    participant TxConsumer as TransactionHistoryConsumer
    participant TxDB as 거래내역 DB
    participant DataConsumer as ExternalDataConsumer
    participant DataPlatform as 데이터 플랫폼(외부)

    User ->> PaymentAPI: 결제 요청 (orderId)
    activate PaymentAPI

    PaymentAPI ->> OrderDB: 주문/주문아이템 조회 (orderId)
    OrderDB -->> PaymentAPI: 주문/아이템/금액 반환

    alt 주문 무효
        PaymentAPI -->> User: 결제 실패 (유효하지 않은 주문)
    else 주문 유효
        loop 옵션별
            PaymentAPI ->> ProductSvc: 재고 차감(optionId, qty)
            alt 재고 부족
                PaymentAPI -->> User: 결제 실패 (재고 부족)
            else 재고 차감 완료
                ProductSvc -->> PaymentAPI: OK
            end
        end

        PaymentAPI ->> UserDB: 잔액 차감(userId, totalAmount)
        alt 잔액 부족
            loop 옵션별
                PaymentAPI ->> ProductSvc: 재고 복원(+qty)
            end
            PaymentAPI -->> User: 결제 실패 (잔액 부족)
        else 잔액 차감 성공
            loop 쿠폰별(존재 시)
                PaymentAPI ->> UserCouponDB: 상태 변경(ISSUED -> USED)
            end
            PaymentAPI ->> OrderDB: 상태 변경(BEFORE_PAYMENT -> AFTER_PAYMENT)
            PaymentAPI ->> PaymentDB: 결제 저장(...)
            PaymentDB -->> PaymentAPI: paymentId
            PaymentAPI ->> Kafka: publish PaymentCompletedEvent(...)
            PaymentAPI -->> User: 결제 성공 (paymentId)

            par 거래내역 적재
                Kafka -->> TxConsumer: PaymentCompletedEvent
                TxConsumer ->> TxDB: 거래내역 저장(...)
            and 외부 데이터 플랫폼 전송
                Kafka -->> DataConsumer: PaymentCompletedEvent
                DataConsumer ->> DataPlatform: send(...)
            end
        end
    end
    deactivate PaymentAPI

```

### 인기 상품

1. 인기 상품 저장 및 조회

```mermaid
sequenceDiagram
    actor Scheduler as 배치 스케줄러
    participant OrderDB as 주문 DB
    participant PopularDB as 인기 상품 DB
    participant API as 인기 상품 API
    actor User as 사용자

    %% 1. 배치 스케줄러 실행 (매일 00시 등)
    Scheduler ->> OrderDB: 최근 3일간 결제 성공 주문 조회 (where status = SUCCEEDED)
    activate OrderDB
    OrderDB -->> Scheduler: 주문 리스트 반환 ({상품ID, 수량})
    deactivate OrderDB
    Scheduler ->> Scheduler: 상품별 총 판매 수량 집계
    Scheduler ->> PopularDB: 인기 상품 테이블 갱신 (정렬 및 저장)

    %% 2. 사용자 인기 상품 조회
    User ->> API: 인기 상품 조회 요청
    activate API
    API ->> PopularDB: 인기 상품 리스트 조회
    activate PopularDB
    PopularDB -->> API: 인기 상품 리스트 반환
    deactivate PopularDB
    API -->> User: 인기 상품 리스트 응답
    deactivate API
```