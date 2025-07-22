package kr.hhplus.be.server.controller.order;

import kr.hhplus.be.server.controller.order.dto.OrderRequest;
import kr.hhplus.be.server.controller.order.dto.OrderResponse;
import kr.hhplus.be.server.controller.spec.OrderApiSpec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController implements OrderApiSpec {

    @PostMapping
    @Override
    public ResponseEntity<OrderResponse.CreateOrderResponse> createOrder(
            @RequestBody OrderRequest.CreateOrderRequest request) {

        List<OrderResponse.OrderItem> items = request.items().stream()
                .map(item -> new OrderResponse.OrderItem(
                        item.productId(),
                        item.productName(),
                        5000L,
                        item.optionId(),
                        item.quantity(),
                        1000L,
                        item.couponId()
                ))
                .toList();

        OrderResponse.CreateOrderResponse response =
                new OrderResponse.CreateOrderResponse(
                        1L,
                        request.userId(),
                        55000L,
                        13000L,
                        "BEFORE_PAYMENT",
                        LocalDateTime.parse("2025-07-17T21:00"),
                        items
                );

        return ResponseEntity.ok(response);
    }
}