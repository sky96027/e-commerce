package kr.hhplus.be.server.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.controller.dto.CouponRequest;
import kr.hhplus.be.server.controller.dto.CouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "쿠폰", description = "쿠폰 관련 API")
public interface CouponApiSpec {

    @Operation(summary = "쿠폰 조회")
    ResponseEntity<List<CouponResponse.GetUserCoupon>> getCouponsByUserId(@PathVariable Long userId);

    @Operation(summary = "쿠폰 발급")
    ResponseEntity<Void> issueCouponToUser(@RequestParam CouponRequest.IssueCouponRequest requst);
}
