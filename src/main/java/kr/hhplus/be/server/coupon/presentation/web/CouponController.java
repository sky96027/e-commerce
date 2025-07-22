package kr.hhplus.be.server.coupon.presentation.web;

import kr.hhplus.be.server.coupon.presentation.dto.CouponRequest;
import kr.hhplus.be.server.coupon.presentation.dto.CouponResponse;
import kr.hhplus.be.server.coupon.presentation.contract.CouponApiSpec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/coupon")
public class CouponController implements CouponApiSpec {

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<List<CouponResponse.GetUserCoupon>> getCouponsByUserId(@PathVariable Long userId) {
        List<CouponResponse.GetUserCoupon> response = List.of(
                new CouponResponse.GetUserCoupon(
                        1L,
                        1L,
                        1L,
                        1L,
                        "ISSUED",
                        "RATE",
                        15f,
                        null,
                        null,
                        LocalDateTime.parse("2028-06-17T21:00")
                ),
                new CouponResponse.GetUserCoupon(
                        2L,
                        2L,
                        1L,
                        2L,
                        "ISSUED",
                        "FIXED",
                        null,
                        20000L,
                        100000L,
                        LocalDateTime.parse("2028-06-17T21:00")
                )
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/issue")
    @Override
    public ResponseEntity<Void> issueCouponToUser(@RequestParam CouponRequest.IssueCouponRequest requst) {
        return ResponseEntity.ok().build();
    }
}