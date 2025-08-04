# DB 성능 개선 보고서

## 개요

현재 서비스의 주요 테이블에 대해 **실제 사용되는 쿼리 패턴**과 **데이터 접근 흐름**을 분석한 결과, 일부 쿼리에서 **불필요한 테이블 풀 스캔**, **N+1 문제**, **정렬 및 필터링 시 성능 저하** 가능성이 확인되었습니다.  
이에 따라 각 테이블에 대한 인덱스 재구성 및 연관 관계 최적화 제안을 아래와 같이 정리합니다.

---

## 연관 쿼리 최적화 개선

### 상품 - 상품옵션 관계, 주문 - 주문상품 관계 (N+1 문제 해결)

`ProductJpaEntity`와 `ProductOptionJpaEntity` 사이의 연관관계가 ID(Long) 기반으로만 구성되어 있어, 상품 목록을 조회하고 옵션을 개별 조회하는 구조로 인해 **N+1 문제**가 발생하고 있었습니다.
`OrderJpaEntity`와 `OrderItemJpaEntity`도 마찬가지의 문제가 발생합니다.


### 개선 조치안

- `ProductOptionJpaEntity`에 `@ManyToOne` 연관관계를 명시적으로 추가
- `ProductJpaEntity`에 `@OneToMany(mappedBy = "product")` 추가
- 아래 JPQL을 활용하여 연관 데이터를 **한 번의 쿼리로 가져오도록 개선**

```java
@Query("SELECT DISTINCT p FROM ProductJpaEntity p JOIN FETCH p.options")
List<ProductJpaEntity> findAllWithOptions();
```

---

## 인덱스 개선안
| 테이블                   | 인덱스 제안                      | 이유         |
| --------------------- | --------------------------- | ---------- |
| `transaction_history` | `(user_id, transaction_time)` | 유저별 정렬 조회  |
| `order`               | `(user_id, order_at)`       | 최근 주문 조회   |
| `order_item`          | `order_id`                  | 주문별 아이템 조회 |
|                       | `(product_id, option_id)`   | 옵션 빠른 조인   |
| `payment`             | `(user_id, status)`         | 유저 결제 필터링  |
| `popular_product`     | `rank`                      | 순위 정렬      |
|                       | `reference_date`            | 날짜별 분석     |
| `user_coupon`         | `(user_id, expired_at)`     | 유저 쿠폰 만료순  |
| `coupon_issue`        | `policy_id`                 | 정책별 필터링    |

**인덱스 생성**
```sql
CREATE INDEX idx_orders_user_created_at 
ON order(user_id, created_at DESC);
```

**기존 쿼리**
```sql
SELECT * FROM order
WHERE user_id = ?
ORDER BY created_at DESC
LIMIT 20;
```

**개선된 형태**
```sql
SELECT order_id, status, created_at
FROM order
WHERE user_id = ?
ORDER BY created_at DESC
LIMIT 20;
```

>목적: 인덱스 커버리지 향상 + I/O 부하 감소

## 5. EXPLAIN 실행 계획 비교 (transaction_history 기준)

### 인덱스 적용 전 (총 데이터 100,000건)
```sql
EXPLAIN SELECT * FROM transaction_history
WHERE user_id = 1
  AND created_at BETWEEN '2025-01-01' AND '2025-07-30'
ORDER BY created_at DESC
LIMIT 50;
```

| 항목 | 내용 | 해석 |
|------|------|------|
| type | ALL | 테이블 전체 탐색 (Full Table Scan) 발생 |
| key | NULL | 인덱스 전혀 미사용 |
| rows | 100000 | 전체 레코드 탐색으로 성능 저하 |
| Extra | Using where; Using filesort | 정렬 시 디스크 기반 정렬 발생, I/O 부하 유발 |

---

###  인덱스 적용 후 (복합 인덱스 활용)
```sql
CREATE INDEX idx_transaction_history_userid_createdat
ON transaction_history(user_id, created_at DESC);
```

| 항목 | 내용 | 개선 효과 |
|------|------|------------|
| type | range | 조건 기반 인덱스 범위 조회로 변경 |
| key | idx_transaction_history_userid_createdat | 인덱스 정상 활용 |
| rows | 50~100 | 조건 만족 범위만 탐색 |
| Extra | Using index condition; Using where | 인덱스 필터링 + 정렬 최적화로 Filesort 제거 가능 |

---

##  요약

- `transaction_history`는 사용자별 거래가 지속적으로 누적되는 로그성 테이블로, **10만 건 이상의 데이터가 저장**되어 있음
- 기존 쿼리는 전체 탐색 + 정렬 수행으로 매우 비효율적
- `user_id`, `created_at DESC` 복합 인덱스를 적용하면 **범위 조건 + 정렬까지 커버**
- 결과적으로 `LIMIT N` 쿼리에 최적화된 인덱스를 구성할 수 있음
- `filesort` 제거로 I/O 비용 감소, 쿼리 응답 시간 단축 효과




