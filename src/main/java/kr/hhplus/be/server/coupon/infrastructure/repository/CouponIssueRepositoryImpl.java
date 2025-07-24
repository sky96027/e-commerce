package kr.hhplus.be.server.coupon.infrastructure.repository;

import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * In-memory CouponIssueRepository 구현체
 */
@Repository
public class CouponIssueRepositoryImpl implements CouponIssueRepository {

    private final Map<Long, CouponIssue> table = new HashMap<>();

    /**
     * 쿠폰 발급 정보 ID로 조회
     */
    @Override
    public CouponIssue selectById(long couponIssueId) {
        throttle(200);
        CouponIssue issue = table.get(couponIssueId);
        if (issue == null) {
            throw new IllegalArgumentException("해당 쿠폰 발급 정보가 존재하지 않습니다. id=" + couponIssueId);
        }
        return issue;
    }

    /**
     * 쿠폰 발급 정보를 업데이트 (예: remaining 감소)
     */
    @Override
    public void update(CouponIssue couponIssue) {
        throttle(200);
        table.put(couponIssue.getCouponIssueId(), couponIssue);
    }

    /**
     * 테스트용 초기 데이터 삽입
     */
    public void insertMockIssue(CouponIssue issue) {
        table.put(issue.getCouponIssueId(), issue);
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {
        }
    }
}