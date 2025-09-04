## 리팩토링 배경

현재 쿠폰 발급의 로직은 Redis ZSet으로 Queue를 구성하고 Drain 하는 방식으로 구현되어 있다.
[쿠폰에 Redis 자료 구조 적용하기](https://www.notion.so/Redis-25413a3e1bfc8098a3b6f09a7f1f71f7?pvs=21)

또한 결제 로직은 주요 로직 → 이벤트 발행 → 부가 로직(거래 내역 저장, 외부 데이터 플랫폼에 데이터 전송) 으로 이루어져 있다.

쿠폰 발급에 Kafka를 도입할 경우 순간 트래픽이 많을 것(버스트)으로 예상되는 쿠폰 발급에는 병렬 처리로 인한 처리량 확장과 순서 보장의 안정성이 증가될 것으로 예상된다.

결제 로직에 kafka를 도입할 경우 현재 이미 부가 로직을 비동기로 돌려 동기 로직을 최소환 시킨 상태지만 kafka로 전환하면 외부 플랫폼이 다운되거나 장애 상황에도 토픽이 완충 역할을 하여 핵심 결제 로직을 보호한다.

## 구성 요소

- **Kafka 브로커 × 3**: `confluentinc/cp-kafka:8.0.0` 이미지, 각 노드가 *broker + controller* 역할을 동시에 수행(KRaft)
- **Prometheus + Grafana. Prometheus :** `kafka-exporter:9308`(Kafka Exporter) 등에서 메트릭을 스크랩.

## 네트워킹 & 포트 매핑

| 브로커 | 컨테이너 이름 | EXTERNAL(컨테이너) | 호스트 포트 매핑 | INTERNAL(브로커 간) | CONTROLLER |
| --- | --- | --- | --- | --- | --- |
| 1 | kafka-1 | 0.0.0.0:9092 | localhost:19092 → 9092 | kafka-1:29092 | kafka-1:29093 |
| 2 | kafka-2 | 0.0.0.0:9092 | localhost:29092 → 9092 | kafka-2:29092 | kafka-2:29093 |
| 3 | kafka-3 | 0.0.0.0:9092 | localhost:39092 → 9092 | kafka-3:29092 | kafka-3:29093 |

## Spring Boot 애플리케이션 설정

```r
spring:
application:
name: hhplus
datasource:
driver-class-name: com.mysql.cj.jdbc.Driver
url: jdbc:mysql://localhost:3306/hhplus?characterEncoding=UTF-8&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
username: application
password: application
hikari:
maximum-pool-size: 50
minimum-idle: 10
connection-timeout: 30000
max-lifetime: 60000

jpa:
generate-ddl: true
show-sql: true
hibernate:
ddl-auto: none
properties:
hibernate.timezone.default_storage: NORMALIZE_UTC
hibernate.jdbc.time_zone: UTC
hibernate:
format_sql: true
show_sql: true
use_sql_comments: true

data:
redis:
host: localhost
port: 6379

kafka:
bootstrap-servers: localhost:19092,localhost:29092,localhost:39092
consumer:
group-id: my-group
auto-offset-reset: earliest
key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
producer:
key-serializer: org.apache.kafka.common.serialization.StringSerializer
value-serializer: org.apache.kafka.common.serialization.StringSerializer

popular:
rank:
scheduler-enabled: false
```

## 모니터링

### Prometheus 설정

```r
global:
scrape_interval: 15s

scrape_configs:
- job_name: "prometheus"
static_configs:
- targets: ["prometheus:9090"]

- job_name: "redis"
static_configs:
- targets: ["redis-exporter:9121"]

- job_name: "kafka"
static_configs:
- targets: ["kafka-exporter:9308"]
```

### Prometheus/Grafana Compose 설정

```r
services:
prometheus:
image: prom/prometheus:latest
container_name: prometheus
volumes:
- ./docker/prometheus.yml:/etc/prometheus/prometheus.yml
- prometheus_data:/prometheus
ports: ["9090:9090"]
networks: [monitor]

grafana:
image: grafana/grafana:latest
container_name: grafana
environment:
- GF_SECURITY_ADMIN_USER=admin
- GF_SECURITY_ADMIN_PASSWORD=admin
volumes:
- grafana_data:/var/lib/grafana
ports: ["3000:3000"]
networks: [monitor]
depends_on: [prometheus]

volumes:
prometheus_data:
grafana_data:

networks:
monitor:
driver: bridge
```

## 변경된 시퀀스 다이어그램

쿠폰 발급

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

결제

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