## E-commerce Service

---

**📕 E-commerce 설계 문서**

- [설계 개요](https://github.com/sky96027/e-commerce/wiki/%5B%EC%84%A4%EA%B3%84%5D-Overview)
- [요구사항 분석](https://github.com/sky96027/e-commerce/blob/main/docs/%EC%84%A4%EA%B3%84/Requirements.md)
- [ERD 문서](https://github.com/sky96027/e-commerce/blob/main/docs/%EC%84%A4%EA%B3%84/ERD.md)
- [시퀀스 다이어그램](https://github.com/sky96027/e-commerce/blob/main/docs/%EC%84%A4%EA%B3%84/Sequence.md)

**🗄️ 데이터베이스 및 성능 최적화 문서**

- [쿼리 튜닝, 인덱스 추가](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/DB%20%EC%84%B1%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%AC%B8%EC%84%9C.md)
- [Spring Cache 적용 문서](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/Spring%20Cache%20%EC%A0%81%EC%9A%A9%20%EB%B0%8F%20%EC%84%B1%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%AC%B8%EC%84%9C.md)
- [Redis Cache 전환](https://www.notion.so/Redis-Cache-25b13a3e1bfc807e950bd724459b5801?pvs=21) (구현 완료 문서 미완료)
- [인기 상품 Redis ZSet](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/%EC%9D%B8%EA%B8%B0%20%EC%83%81%ED%92%88%20Redis%20ZSet%20%EA%B5%AC%ED%98%84%20%EB%AC%B8%EC%84%9C.md)

**🔐 대규모 트래픽, 동시성 제어 문서 (적용 순서)**

- [비관적 락](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/%EB%8F%99%EC%8B%9C%EC%84%B1%20%EB%AC%B8%EC%A0%9C%20%ED%95%B4%EA%B2%B0%20%EB%B0%A9%EC%95%88%20%EB%AC%B8%EC%84%9C.md)
- [Redis Spin Lock](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/Redis%20%EA%B8%B0%EB%B0%98%20Spin%20Lock%20%EC%A0%81%EC%9A%A9%20%EB%AC%B8%EC%84%9C.md)
- [Redis Pub/Sub Lock](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/Redis%20%EA%B8%B0%EB%B0%98%20%EB%B0%9C%ED%96%89%20%EA%B5%AC%EB%8F%85%20Lock%20%EC%A0%81%EC%9A%A9%20%EB%AC%B8%EC%84%9C.md)
- [Redis Hash, ZSet(Queue)](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/%EC%BF%A0%ED%8F%B0%20%EB%8F%84%EB%A9%94%EC%9D%B8%20Redis%20Hash%2C%20Redis%20ZSet(Queue)%20%EC%A0%81%EC%9A%A9%20%EB%AC%B8%EC%84%9C.md)
- [Kafka](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/kafka%20%EA%B5%AC%EC%84%B1%20%EB%AC%B8%EC%84%9C.md)

**🧪 트랜잭션 관련 작업 문서**

- [Saga 패턴 적용 전 고려 사항](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/Saga%ED%8C%A8%ED%84%B4%20%EC%A0%81%EC%9A%A9%20%EC%8B%9C%20%EB%B0%9C%EC%83%9D%20%EB%AC%B8%EC%A0%9C%20%EC%98%88%EC%83%81%20%EB%AC%B8%EC%84%9C.md)
- [Saga 패턴 적용](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/Saga%ED%8C%A8%ED%84%B4%20%EC%A0%81%EC%9A%A9%20%EB%AC%B8%EC%84%9C.md)

**📁 테스트 관련 문서**

- [부하 테스트](https://github.com/sky96027/e-commerce/blob/main/docs/%EA%B8%B0%EB%8A%A5%20%EA%B0%9C%EC%84%A0%20%EB%B3%B4%EA%B3%A0%EC%84%9C/%EB%B6%80%ED%95%98%20%ED%85%8C%EC%8A%A4%ED%8A%B8%20%EB%B0%8F%20%EA%B0%9C%EC%84%A0%20%EC%82%AC%ED%95%AD%20%EB%AC%B8%EC%84%9C.md)
