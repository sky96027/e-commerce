package kr.hhplus.be.server.coupon.domain.repository;

public interface CouponIssueQueueRepository {
    //서버(=Redis) 시각 + 시퀀스로 ZSET에 넣고 score 반환
    long enqueue(long couponId, long userId, String reservationId);

    // 다음 항목 하나를 꺼내옴(없으면 null) -> ZPOPMIN 사용
    String popNext(long couponId);

    // 대기열 길이 조회(모니터링 용)
    long size(long couponId);

    PopResult popNextSafe(long couponId);

    // 결과 래퍼
    record PopResult(Type type, String member) {
        public enum Type { EMPTY, OK, MISSING }
        public static PopResult empty() { return new PopResult(Type.EMPTY, null); }
        public static PopResult ok(String m) { return new PopResult(Type.OK, m); }
        public static PopResult missing(String m) { return new PopResult(Type.MISSING, m); }
    }
}
