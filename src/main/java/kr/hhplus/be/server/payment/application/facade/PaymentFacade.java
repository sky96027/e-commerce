package kr.hhplus.be.server.payment.application.facade;

import kr.hhplus.be.server.coupon.application.usecase.ChangeUserCouponStatusUseCase;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import kr.hhplus.be.server.order.application.dto.OrderDto;
import kr.hhplus.be.server.order.application.dto.OrderItemDto;
import kr.hhplus.be.server.order.application.service.ChangeOrderStatusService;
import kr.hhplus.be.server.order.application.service.FindOrderByOrderIdService;
import kr.hhplus.be.server.order.application.service.FindOrderItemByOrderIdService;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import kr.hhplus.be.server.payment.application.dto.SavePaymentCommand;
import kr.hhplus.be.server.payment.application.usecase.SavePaymentUseCase;
import kr.hhplus.be.server.payment.domain.type.PaymentStatus;
import kr.hhplus.be.server.product.application.usecase.DeductStockUseCase;
import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.DeductUserBalanceUseCase;
import kr.hhplus.be.server.user.application.usecase.FindUserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentFacade {

    private final FindUserUseCase findUserUseCase;
    private final DeductUserBalanceUseCase deductUserBalanceUseCase;
    private final DeductStockUseCase deductStockUseCase;
    private final ChangeUserCouponStatusUseCase changeUserCouponStatusUseCase;
    private final SaveTransactionUseCase saveTransactionUseCase;
    private final SavePaymentUseCase savePaymentUseCase;
    private final ChangeOrderStatusService changeOrderStatusService;
    private final FindOrderByOrderIdService findOrderByOrderIdService;
    private final FindOrderItemByOrderIdService findOrderItemByOrderIdService;

    public PaymentFacade(
            FindUserUseCase findUserUseCase,
            DeductUserBalanceUseCase deductUserBalanceUseCase,
            DeductStockUseCase deductStockUseCase,
            ChangeUserCouponStatusUseCase changeUserCouponStatusUseCase,
            SaveTransactionUseCase saveTransactionUseCase,
            SavePaymentUseCase savePaymentUseCase,
            ChangeOrderStatusService changeOrderStatusService,
            FindOrderByOrderIdService findOrderByOrderIdService,
            FindOrderItemByOrderIdService findOrderItemByOrderIdService
    ) {
        this.findUserUseCase = findUserUseCase;
        this.deductUserBalanceUseCase = deductUserBalanceUseCase;
        this.deductStockUseCase = deductStockUseCase;
        this.changeUserCouponStatusUseCase = changeUserCouponStatusUseCase;
        this.saveTransactionUseCase = saveTransactionUseCase;
        this.savePaymentUseCase = savePaymentUseCase;
        this.changeOrderStatusService = changeOrderStatusService;
        this.findOrderByOrderIdService = findOrderByOrderIdService;
        this.findOrderItemByOrderIdService = findOrderItemByOrderIdService;
    }

    /**
     * 결제 처리 전체 트랜잭션
     */
    @Transactional
    public long processPayment(long orderId) {
        // 1. 주문 조회
        OrderDto order = findOrderByOrderIdService.findById(orderId);
        List<OrderItemDto> orderItems = findOrderItemByOrderIdService.findByOrderId(orderId);

        // 2. 유저 잔액 확인
        UserDto user = findUserUseCase.findById(order.userId());
        long totalAmount = order.totalAmount();
        if (user.balance() < totalAmount) {
            throw new IllegalStateException("잔액 부족");
        }

        // 3. 재고 차감
        for (OrderItemDto item : orderItems) {
            deductStockUseCase.deductStock(item.optionId(), item.quantity());
        }

        // 4. 잔액 차감
        deductUserBalanceUseCase.deductBalance(order.userId(), totalAmount);

        // 5. 거래내역 저장
        saveTransactionUseCase.save(order.userId(), TransactionType.USE, totalAmount);

        // 6. 결제 내역 저장
        long paymentId = savePaymentUseCase.save(new SavePaymentCommand(
                order.orderId(),
                order.userId(),
                totalAmount,
                order.totalDiscountAmount(),
                PaymentStatus.AFTER_PAYMENT
        ));

        // 7. 주문 상태 업데이트
        changeOrderStatusService.changeStatus(orderId, OrderStatus.AFTER_PAYMENT);

        // 8. 쿠폰 상태 변경
        for (OrderItemDto item : orderItems) {
            if (item.userCouponId() != null) {
                changeUserCouponStatusUseCase.changeStatus(item.userCouponId(), UserCouponStatus.USED);
            }
        }

        return paymentId;
    }
}