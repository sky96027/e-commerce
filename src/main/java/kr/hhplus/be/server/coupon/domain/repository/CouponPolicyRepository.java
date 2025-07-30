package kr.hhplus.be.server.coupon.domain.repository;

import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.model.CouponPolicy;

/**
 * 쿠폰 정책 정보를 조회, 저장하기 위한 도메인 저장소 인터페이스
 * 구현체는 인프라 계층에 위치함
 */
public interface CouponPolicyRepository {
    CouponPolicy findById(long couponPolicyId);
    CouponPolicy update(CouponPolicy CouponPolicy);
}
