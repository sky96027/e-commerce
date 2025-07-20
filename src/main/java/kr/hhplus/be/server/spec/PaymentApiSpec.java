package kr.hhplus.be.server.spec;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.controller.dto.PaymentRequest;
import kr.hhplus.be.server.controller.dto.PaymentResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "결제", description = "결제 관련 API")
public interface PaymentApiSpec {
    @Operation(summary = "인기 상품 조회")
    ResponseEntity<PaymentResponse.GetPaymentResponse> payment (@RequestBody PaymentRequest.CreatePaymentRequest request);
}
