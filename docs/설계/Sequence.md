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
    participant CouponAPI as 쿠폰 API
    participant CouponBatchDB as 쿠폰 DB
    participant UserCouponDB as 유저 쿠폰 DB

    User ->> CouponAPI: 쿠폰 발급 요청 (couponId, userId)
    activate CouponAPI

    %% 1. 발급 가능 기간 확인
    CouponAPI ->> CouponBatchDB: 쿠폰 정보 확인 (couponId)

    alt 발급 기간 아님
        CouponAPI -->> User: 발급 실패 (발급 기간이 아님)
    else 발급 가능
        %% 2. 중복 발급 확인
        CouponAPI ->> UserCouponDB: 중복 발급 확인 (couponId, userId)

        alt 이미 발급함
            CouponAPI -->> User: 발급 실패 (이미 발급한 쿠폰)
        else 발급 가능
            CouponAPI ->> CouponBatchDB: 수량 선점 시도 (LOCK AND DECREMENT) 
            alt 수량 초과
                CouponAPI -->> User: 발급 실패 (발급량 초과)
            else 수량 가능
                CouponAPI ->> UserCouponDB: 유저 쿠폰 저장 (userCouponId, couponId, userId 등)
                activate UserCouponDB
                UserCouponDB -->> CouponAPI: 저장 완료
                deactivate UserCouponDB
                CouponAPI -->> User: 쿠폰 발급 완료 메시지
            end
        end
    end
    deactivate CouponAPI
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
    participant PaymentAPI as 결제 API
    participant OrderDB as 주문 DB
    participant UserDB as 유저 DB
    participant ProductOptionDB as 상품 옵션 DB
    participant UserCouponDB as 유저 쿠폰 DB
    participant PaymentDB as 결제 DB
    participant DataPlatform as 데이터 플랫폼 (외부)

    User ->> PaymentAPI: 결제 요청 (orderId)
    activate PaymentAPI

    %% 1. 주문 유효성 확인
    PaymentAPI ->> OrderDB: 주문서 정보 조회 요청 (orderId)
    activate OrderDB
    OrderDB -->> PaymentAPI: 주문서 정보 반환 (주문ID, userId, {상품ID, 옵션ID, 수량, 쿠폰ID})
    deactivate OrderDB
    PaymentAPI ->> PaymentAPI: 주문서 유효성 검사 (상품 판매 종료, 옵션 삭제, 사용자와 주문서 불일치 등 유효하지 않은 주문)
    alt 주문 무효
        PaymentAPI -->> User: 결제 실패 (유효하지 않은 주문, 상품 판매 종료, 재고 없음 등)
    else 주문 유효
        %% 2. 결제 금액 확인
        

        %% 3. 잔액 확인
        PaymentAPI ->> UserDB: 사용자 잔액 조회 (userId)
        activate UserDB
        UserDB -->> PaymentAPI: 사용자 잔액 반환
        deactivate UserDB
        PaymentAPI ->> PaymentAPI: 잔액 > 주문 총 금액 확인
        alt 잔액 부족
            PaymentAPI -->> User: 결제 실패 (잔액 부족)
        else 잔액 충분
            %% 4. 재고 차감
            loop 상품별
                PaymentAPI ->> ProductOptionDB: 재고 확인 및 차감 (옵션ID, 수량)
                activate ProductOptionDB
                alt 재고 부족
                    PaymentAPI -->> User: 결제 실패 (재고 부족)
                else 재고 충분
                    ProductOptionDB -->> PaymentAPI: 재고 차감 완료
                end
            end
            deactivate ProductOptionDB
						%% 5. 잔액 차감
            PaymentAPI ->> UserDB: 잔액 차감 (UPDATE)
						
            %% 6. 결제 내역 저장
            PaymentAPI ->> PaymentDB: 결제 내역 저장 (결제ID, orderId, userId, 총 금액, 총 할인 금액, 상태)
            
            %% 7. 주문 정보 저장
            PaymentAPI ->> OrderDB: 주문 데이터 저장(orderID, status:AFTER_PAYMENT)

            %% 8. 쿠폰 사용 처리
            loop 상품별
                PaymentAPI ->> UserCouponDB: 쿠폰 사용 처리 UPDATE (UserCouponId, status: USED)
            end

            %% 9. 데이터 플랫폼 전송
            PaymentAPI ->> DataPlatform: 주문 정보 전송 (orderId)

            PaymentAPI -->> User: 결제 성공 응답
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