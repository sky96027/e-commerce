[Redis 기반의 캐싱 전략 적용](https://www.notion.so/Redis-24a13a3e1bfc80db8140e507c89754c1?pvs=21)

이전에 Redis Hash로 동작하게 한 재고 증가 서비스, 재고 감소 서비스에 정합성을 위한 복구 코드를 추가하고 인기 상품을 Redis의 자료 구조인 ZSet을 이용하여 구현하여야 한다.

이 페이지의 작업은 Redis를 이용하며 DB의 부하를 감소시키고 성능의 증가를 노리기 위함이다.

### 보상 코드

기존 코드 (Redis Hash로 1차 반영 → DB에 2차 반영)에서 Hash에 저장하는 로직에 검증을 덧붙히고 Hash에 반영이 성공하면 DB로 Write-Through 하는 로직과 DB에 저장이 실패하면 Hash에 보상(복구)하는 코드를 추가하였다.

```java
@Service
@RequiredArgsConstructor
public class DeductStockService implements DeductStockUseCase {
    private final ProductOptionRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final StockCounter stockCounter; 

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deductStock(long optionId, int quantity) {
        ProductOption.requirePositive(quantity);
        
        // 상품 ID 조회
        ProductOption productOption = repository.findOptionByOptionId(optionId);
        long productId = productOption.getProductId();
        
        // Redis에서 재고 차감 시도 (1차 소스)
        long remainingStock = stockCounter.tryDeductHash(productId, optionId, quantity);
        boolean redisDecremented = (remainingStock != -1);

        try {
            if (!redisDecremented) {
                // Redis 재고 부족 시 DB에서 재확인 및 차감
                repository.decrementStock(optionId, quantity);
                // DB 성공 시 Redis 재고 동기화
                long fresh = repository.findOptionByOptionId(optionId).getStock();
                stockCounter.initStockHash(productId, optionId, fresh);
            } else {
                // Redis 차감 성공 -> DB도 Write-Through
                repository.decrementStock(optionId, quantity);
            }
        } catch (RuntimeException e) {
            // 보상 코드
            if (redisDecremented) {
                stockCounter.compensateHash(productId, optionId, quantity);
            }
            throw e;
        }
        
        // 재고 변경 이벤트 발행
        eventPublisher.publishEvent(new StockChangedEvent(
            productId, 
            optionId, 
            StockChangedEvent.DEDUCT, 
            quantity
        ));
    }
}
```

### 인기 상품 구현

기존 DB를 소스로 구현된 인기 상품 기능을 Redis ZSet을 소스로 이용하게 리팩토링을 진행한다.

리팩토링의 이유는 다음과 같다.

1. **성능, 확장성**

   증분 집계가 가벼움 : 판매 시 `ZINCRBY` 한 번이면 끝 → DB에서 `GROUP BY/ORDER BY` 의 부담이 제거

   스파이크 내성 : 순간 트래픽에도 DB 락/컨텐션 없이 흡수

   수평 확장 용이 : Redis Cluster로 키 범위를 분산

2. **인기 상품이라는 기능에 최적화**

   랭킹에 최적화 된 자료 구조 : 점수 기반 정렬 구조라 인덱스/정렬 비용이 없음.

   최근성 반영 쉬움 : 5분 버킷 + `ZUNIONSTORE` 로 지금 기준 지난 24시간을 손쉽게 재계산.

   멀티 뷰 : 1h/24h/7d, 카테고리, 브랜드별 키만 추가하면 다양한 랭킹 동시 제공.

3. **운영 단순화**

   TTL로 자동 청소 : 버킷 키에 25시간 TTL → 스케쥴 실패/재기동 시 다음 주기에 자동 복구.

   DB 부하 감소 : 랭킹 계산을 Redis가 담당


1. **비용**

   쿼리 비용 감소 : 무거운 집계 SQL 제거


**커밋 링크**

https://github.com/sky96027/e-commerce/commit/e8f751750f6a7a9948ec76bcfbe8f052054b32ad

**Redis 관리 키**

1. 실시간 버킷 키
- 키 : `pop:z:5m:{yyyyMMddHHmm}`
- 타입 : ZSet
- 멤버 : `"product:{productId}”`
- 스코어 : 해당 5분 구간 판매 수량 합계
- TTL : 25시간 (24+1 여유)

1. 24시간 랭킹 키
- 키 : `pop:rank:24h`
- 타입 : ZSet
- 멤버 : `"product:{productId}”`
- 스코어 : 최근 24시간(5분 * 288버킷)의 합계
- TTL : 없음
- 메모 : 실시간 버킷 키들을 합산(ZUNIONSTORE) 하여 새로 구성한 별도의 ZSet

**주요 변경점**

1. `PopularityCollector` , `PopularRankScheduler`  추가

```java
//PopularityCollector 

if (!StockChangedEvent.DEDUCT.equals(e.changeType())) return; // 판매만 집계

        String bucket = "pop:z:5m:" + floor5m(Instant.now());
        String member = "product:" + e.productId();

        redis.opsForZSet().incrementScore(bucket, member, e.quantity());
        redis.expire(bucket, java.time.Duration.ofHours(25));
```

```java
@Scheduled(cron = "0 */5 * * * *")
    public void build24hRanking() {
        List<String> buckets = lastBuckets(288); // 24h = 5m x 288
        if (buckets.isEmpty()) return;

        redis.execute((RedisConnection con) -> {
            byte[][] keys = buckets.stream().map(k -> k.getBytes(StandardCharsets.UTF_8)).toArray(byte[][]::new);
            con.zUnionStore(DEST.getBytes(StandardCharsets.UTF_8), keys);
            return null;
        });

        Long total = redis.opsForZSet().zCard(DEST);
        if (total != null && total > 1000) {
            redis.opsForZSet().removeRange(DEST, 0, total - 1001);
        }
    }

    private List<String> lastBuckets(int n) {
        List<String> keys = new ArrayList<>(n);
        Instant now = Instant.now();
        long sec = now.atZone(KST).toEpochSecond();
        long base = (sec / 300) * 300;
        for (int i = 0; i < n; i++) {
            long t = base - (long) i * 300;
            keys.add("pop:z:5m:" + LocalDateTime.ofEpochSecond(t, 0, KST.getRules().getOffset(now)).format(F));
        }
        return keys;
    }
```

### 고민한 부분, 메모

**만약 내가 이 프로젝트를 실제로 서비스 중이고 다중 인스턴스로 구성된 환경이라면?**

만약 이 환경이라면 분산락으로 5분마다 동시에 도는 스케줄링 프로세스에 동시성 문제가 생길 것.

그를 방지하기 위해 Redis 분산 락을 써야할까…
고민을 하다가 사이드 프로젝트인 이 프로젝트에 더 간단하면서 적합하다고 생각해 application-scheduler.yml만 생성하기로 결정

```java
@ConditionalOnProperty(
        prefix = "popular.rank",
        name = "scheduler-enabled",
        havingValue = "true",
        matchIfMissing = false
)
```

스케쥴러에 위 어노테이션을 주고

```markdown
// scheduler

spring:
  config:
    activate:
      on-profile: scheduler
popular:
  rank:
    scheduler-enabled: true
    

// 공통
popular:
  rank:
     scheduler-enabled: false
```

공통에 스케쥴러 false, 스케쥴링을 할 인스턴스만 true로 주면 됨.

마치고 보니 분산락을 거는 방법이 좋았을까 생각을 남기게 됨. 결국 인간이 다루는 프로젝트이고 실수가 생길 수 있으니까

**DB에 쓰지 않고 ZSet 만으로 구현한 이유**

인기 상품이라는 기능 상 이전 시점의 인기 상품 리스트가 필요할까? 라는 생각에 “아닐 것” 이라는 판단 하에 DB는 사용하지 않기로 결정.

<aside>
💡

메모) SaveListUseCase, SaveListService 클래스와 RepositoryImpl의 replaceAll() 메서드는 사용할 일이 없어졌지만 만약에 DB에 스냅샷을 기록하는 방식으로 전환할 경우를 위해 남겨둠. 사용할 일이 전혀 없을 것 같을 때 JPA 종속을 제거하기 위해 리팩토링을 하는 것은 어떨지

</aside>