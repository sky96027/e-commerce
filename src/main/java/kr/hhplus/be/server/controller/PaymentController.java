package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.controller.dto.PaymentRequest;
import kr.hhplus.be.server.controller.dto.PaymentResponse;
import kr.hhplus.be.server.spec.PaymentApiSpec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController implements PaymentApiSpec {

    @PostMapping
    public ResponseEntity<PaymentResponse.GetPaymentResponse> payment (@RequestBody PaymentRequest.CreatePaymentRequest request) {
        PaymentResponse.GetPaymentResponse response = new PaymentResponse.GetPaymentResponse(
                1L,
                request.orderId(),
                request.userId(),
                35600L,
                9500L,
                "SUCCEEDED"
        );
        return ResponseEntity.ok(response);
    }
}