package kr.hhplus.be.server.coupon.presentation.web;

import kr.hhplus.be.server.coupon.application.dto.CouponPolicyDto;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.dto.UserCouponDto;
import kr.hhplus.be.server.coupon.application.facade.CouponFacade;
import kr.hhplus.be.server.coupon.application.usecase.FindCouponPolicyUseCase;
import kr.hhplus.be.server.coupon.application.usecase.FindUserCouponSummaryUseCase;
import kr.hhplus.be.server.coupon.application.usecase.SaveUserCouponUseCase;
import kr.hhplus.be.server.coupon.presentation.contract.CouponApiSpec;
import kr.hhplus.be.server.coupon.presentation.dto.CouponRequest;
import kr.hhplus.be.server.coupon.presentation.dto.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController implements CouponApiSpec {

    private final FindUserCouponSummaryUseCase findUserCouponSummaryUseCase;
    private final SaveUserCouponUseCase saveUserCouponUseCase;
    private final CouponFacade couponFacade;
    private final FindCouponPolicyUseCase findCouponPolicyUseCase;

    /**
     * 유저의 쿠폰 목록 조회
     */
    @GetMapping("/{userId}/coupons")
    @Override
    public ResponseEntity<List<CouponResponse.GetUserCoupon>> getCouponsByUserId(@PathVariable long userId) {
        List<UserCouponDto> couponList = findUserCouponSummaryUseCase.findSummary(userId);

        List<CouponResponse.GetUserCoupon> response = couponList.stream()
                .map(dto -> new CouponResponse.GetUserCoupon(
                        dto.userCouponId(),
                        dto.couponId(),
                        dto.userId(),
                        dto.policyId(),
                        dto.status(),
                        dto.typeSnapshot(),
                        dto.discountRateSnapshot(),
                        dto.usagePeriodSnapshot(),
                        dto.expiredAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 유저에게 쿠폰 발급
     */
    @PostMapping("/issue")
    @Override
    public ResponseEntity<Void> issueCouponToUser(@RequestBody CouponRequest.IssueCouponRequest request) {
        SaveUserCouponCommand command = new SaveUserCouponCommand(
                request.couponId(),
                request.userId(),
                request.policyId(),
                request.typeSnapshot(),
                request.discountRateSnapshot(),
                request.usagePeriodSnapshot(),
                request.expiredAt()
        );
        couponFacade.issueToUser(command);
        return ResponseEntity.ok().build();
    }

    /**
     * 쿠폰 정책 목록 조회
     */
    @GetMapping("/policies")
    public ResponseEntity<List<CouponPolicyDto>> getCouponPolicies() {
        // TODO: FindAllCouponPolicyUseCase 구현 후 주입하여 사용
        // List<CouponPolicyDto> policies = findAllCouponPolicyUseCase.findAll();
        return ResponseEntity.ok(List.of()); // 임시 응답
    }

    /**
     * 특정 쿠폰 정책 조회
     */
    @GetMapping("/policies/{policyId}")
    public ResponseEntity<CouponPolicyDto> getCouponPolicy(@PathVariable long policyId) {
        CouponPolicyDto policy = findCouponPolicyUseCase.findById(policyId);
        
        if (policy == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(policy);
    }
}