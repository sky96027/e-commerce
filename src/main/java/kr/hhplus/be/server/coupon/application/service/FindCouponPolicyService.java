package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.dto.CouponPolicyDto;
import kr.hhplus.be.server.coupon.application.dto.UserCouponDto;
import kr.hhplus.be.server.coupon.application.usecase.FindCouponPolicyUseCase;
import kr.hhplus.be.server.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindCouponPolicyService implements FindCouponPolicyUseCase {

    private final CouponPolicyRepository couponPolicyRepository;

    @Override
    @Cacheable(cacheNames = "coupon:policy", key = "#policyId")
    public CouponPolicyDto findById(long policyId) {
        CouponPolicy policy = couponPolicyRepository.findById(policyId);
        return CouponPolicyDto.from(policy);
    }
}