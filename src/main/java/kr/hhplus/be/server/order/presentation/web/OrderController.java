package kr.hhplus.be.server.order.presentation.web;

import kr.hhplus.be.server.order.application.dto.OrderDto;
import kr.hhplus.be.server.order.application.dto.SaveOrderCommand;
import kr.hhplus.be.server.order.application.dto.SaveOrderItemCommand;
import kr.hhplus.be.server.order.application.usecase.FindOrderByOrderIdUseCase;
import kr.hhplus.be.server.order.application.usecase.SaveOrderUseCase;
import kr.hhplus.be.server.order.presentation.contract.OrderApiSpec;
import kr.hhplus.be.server.order.presentation.dto.OrderRequest;
import kr.hhplus.be.server.order.presentation.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController implements OrderApiSpec {

    private final SaveOrderUseCase saveOrderUseCase;
    private final FindOrderByOrderIdUseCase findOrderByOrderIdUseCase;

    public OrderController(
            SaveOrderUseCase saveOrderUseCase,
            FindOrderByOrderIdUseCase findOrderByOrderIdUseCase
    ) {
        this.saveOrderUseCase = saveOrderUseCase;
        this.findOrderByOrderIdUseCase = findOrderByOrderIdUseCase;
    }

    @PostMapping("/create")
    @Override
    public ResponseEntity<OrderResponse.CreateOrderResponse> createOrder(
            @RequestBody OrderRequest.CreateOrderRequest request) {

        // 1. 요청 DTO → 커맨드 변환
        List<SaveOrderItemCommand> itemCommands = request.items().stream()
                .map(item -> new SaveOrderItemCommand(
                        item.productId(),
                        item.optionId(),
                        item.productName(),
                        item.productPriceSnapshot(),
                        item.userCouponId(),
                        item.quantity()
                ))
                .toList();

        SaveOrderCommand command = new SaveOrderCommand(
                request.userId(),
                itemCommands
        );

        // 2. 주문 저장
        long orderId = saveOrderUseCase.save(command);
        OrderDto orderDto = findOrderByOrderIdUseCase.findById(orderId);

        // 4. 응답 DTO 매핑
        List<OrderResponse.OrderItem> items = orderDto.items().stream()
                .map(item -> new OrderResponse.OrderItem(
                        item.productId(),
                        item.productName(),
                        item.productPrice(),
                        item.optionId(),
                        item.quantity(),
                        item.discountAmount(),
                        item.userCouponId() // null일 수 있음
                ))
                .toList();

        OrderResponse.CreateOrderResponse response = new OrderResponse.CreateOrderResponse(
                orderDto.orderId(),
                orderDto.userId(),
                orderDto.totalAmount(),
                orderDto.totalDiscountAmount(),
                orderDto.status(),
                orderDto.orderAt(),
                items
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 주문 ID로 주문 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse.CreateOrderResponse> getOrderById(@PathVariable long orderId) {
        OrderDto orderDto = findOrderByOrderIdUseCase.findById(orderId);
        
        if (orderDto == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<OrderResponse.OrderItem> items = orderDto.items().stream()
                .map(item -> new OrderResponse.OrderItem(
                        item.productId(),
                        item.productName(),
                        item.productPrice(),
                        item.optionId(),
                        item.quantity(),
                        item.discountAmount(),
                        item.userCouponId() // null일 수 있음
                ))
                .toList();

        OrderResponse.CreateOrderResponse response = new OrderResponse.CreateOrderResponse(
                orderDto.orderId(),
                orderDto.userId(),
                orderDto.totalAmount(),
                orderDto.totalDiscountAmount(),
                orderDto.status(),
                orderDto.orderAt(),
                items
        );

        return ResponseEntity.ok(response);
    }
}