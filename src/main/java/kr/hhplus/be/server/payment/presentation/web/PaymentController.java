package kr.hhplus.be.server.payment.presentation.web;

import kr.hhplus.be.server.payment.presentation.contract.PaymentApiSpec;
import kr.hhplus.be.server.payment.presentation.dto.PaymentRequest;
import kr.hhplus.be.server.payment.presentation.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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