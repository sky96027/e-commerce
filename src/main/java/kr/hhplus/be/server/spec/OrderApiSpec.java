package kr.hhplus.be.server.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.controller.dto.OrderRequest;
import kr.hhplus.be.server.controller.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "주문", description = "주문 관련 API")
public interface OrderApiSpec {

    @Operation(summary = "주문 생성")
    ResponseEntity<OrderResponse.CreateOrderResponse> createOrder(
            @RequestBody OrderRequest.CreateOrderRequest request);
}
