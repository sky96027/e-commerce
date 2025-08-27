### 작업 이유

1. DB 핫스팟 완화 및 처리량 확장.
2. 선착순 공정성(요청 순서 보장)과 백프레셔 확보.
3. 장애 시 회복 가능한 데이터 유지.

### 설계

- ZSet 대기열

  Redis ZSet은 score를 서버 시간 등으로 한다면 랭킹을 표현하는 것 뿐만이 아닌 강력한 요청 순서 보장과 백프레셔 기능을 할 수 있음.

- Hash 재고 관리 (Lua Script)

  Redis 내부에서 한 RTT(Round Trip Time)으로 원자적 차감 + 중복 발급 차단 처리로 경쟁 상태 제거, 지연을 최소화 함

- 락 축소

  ZSet을 queue로 삼으면 락으로 동시성 제어를 할 필요가 없어짐 따라서 락을 얇게 하거나 제거해 처리량 증가, 코드 단순화할 수 있음

- DB는 Write-Through로 유지함

  발급/취소/정산 대응을 위해 감사 가능 기록이 필요. 장애/재시작 후에도 근거 데이터를 보존해야 함. Write-Through로 일관성을 갖추고 실패 시 보상 로직을 구성해야 함.

- 분산 환경을 고려해 Redis 서버의 시계를 기준으로 ZSet의 score를 구성


- Queue 구성 후 스케쥴링된 worker를 이용해 Queue Drain

고려 사항 : 초 고QPS(Query Per Second)라고 가정할 경우 Write-Back으로 최저 지연, 폭주 트래픽 흡수

### **Queue , Drain 구조 구현**

https://github.com/sky96027/e-commerce/commit/22e1dd7f14f953da11158c0ac6120b13ee5415cd

### **쿠폰 발급에 Hash 자료구조 구현**

https://github.com/sky96027/e-commerce/commit/af88c412bf78e52ccc38120380555a6fc493f89f

**리뷰**

- 상품  재고 감소에서 쓰이는 Hash 구조를 쿠폰 발급에도 도입
- 상품에서 쓰이는 LuaScript를 공용으로 전환
- 공용 ScriptConfig Bean을 상품과 쿠폰 감소에 적용