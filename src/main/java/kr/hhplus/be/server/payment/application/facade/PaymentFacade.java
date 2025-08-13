package kr.hhplus.be.server.payment.application.facade;

import kr.hhplus.be.server.coupon.application.usecase.ChangeUserCouponStatusUseCase;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import kr.hhplus.be.server.order.application.dto.OrderDto;
import kr.hhplus.be.server.order.application.dto.OrderItemDto;
import kr.hhplus.be.server.order.application.service.ChangeOrderStatusService;
import kr.hhplus.be.server.order.application.service.FindOrderByOrderIdService;
import kr.hhplus.be.server.order.application.service.FindOrderItemByOrderIdService;
import kr.hhplus.be.server.order.application.usecase.ChangeOrderStatusUseCase;
import kr.hhplus.be.server.order.application.usecase.FindOrderByOrderIdUseCase;
import kr.hhplus.be.server.order.application.usecase.FindOrderItemByOrderIdUseCase;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import kr.hhplus.be.server.payment.application.dto.SavePaymentCommand;
import kr.hhplus.be.server.payment.application.usecase.SavePaymentUseCase;
import kr.hhplus.be.server.payment.domain.type.PaymentStatus;
import kr.hhplus.be.server.product.application.usecase.AddStockUseCase;
import kr.hhplus.be.server.product.application.usecase.DeductStockUseCase;
import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.ChargeUserBalanceUseCase;
import kr.hhplus.be.server.user.application.usecase.DeductUserBalanceUseCase;
import kr.hhplus.be.server.user.application.usecase.FindUserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentFacade {

    private final FindUserUseCase findUserUseCase;
    private final DeductUserBalanceUseCase deductUserBalanceUseCase;
    private final DeductStockUseCase deductStockUseCase;
    private final ChangeUserCouponStatusUseCase changeUserCouponStatusUseCase;
    private final SaveTransactionUseCase saveTransactionUseCase;
    private final SavePaymentUseCase savePaymentUseCase;
    private final AddStockUseCase addStockUseCase;
    private final ChangeOrderStatusUseCase changeOrderStatusUseCase;
    private final FindOrderByOrderIdUseCase findOrderByOrderIdUseCase;
    private final FindOrderItemByOrderIdUseCase findOrderItemByOrderIdUseCase;
    private final ChargeUserBalanceUseCase chargeUserBalanceUseCase;

    public PaymentFacade(
            FindUserUseCase findUserUseCase,
            DeductUserBalanceUseCase deductUserBalanceUseCase,
            DeductStockUseCase deductStockUseCase,
            ChangeUserCouponStatusUseCase changeUserCouponStatusUseCase,
            SaveTransactionUseCase saveTransactionUseCase,
            SavePaymentUseCase savePaymentUseCase,
            AddStockUseCase addStockUseCase,
            ChangeOrderStatusService changeOrderStatusUseCase,
            FindOrderByOrderIdService findOrderByOrderIdUseCase,
            FindOrderItemByOrderIdService findOrderItemByOrderIdUseCase,
            ChargeUserBalanceUseCase chargeUserBalanceUseCase) {
        this.findUserUseCase = findUserUseCase;
        this.deductUserBalanceUseCase = deductUserBalanceUseCase;
        this.deductStockUseCase = deductStockUseCase;
        this.changeUserCouponStatusUseCase = changeUserCouponStatusUseCase;
        this.saveTransactionUseCase = saveTransactionUseCase;
        this.savePaymentUseCase = savePaymentUseCase;
        this.addStockUseCase = addStockUseCase;
        this.changeOrderStatusUseCase = changeOrderStatusUseCase;
        this.findOrderByOrderIdUseCase = findOrderByOrderIdUseCase;
        this.findOrderItemByOrderIdUseCase = findOrderItemByOrderIdUseCase;
        this.chargeUserBalanceUseCase = chargeUserBalanceUseCase;
    }

    /**
     * 결제 처리 전체 트랜잭션
     */
    @Transactional
    public long processPayment(long orderId) {
        List<OrderItemDto> orderItems = null;
        OrderDto order = null;
        long totalAmount = 0L;
        long paymentId = -1L;
        boolean stockDeducted = false;
        boolean balanceDeducted = false;
        List<Long> usedCouponIds = new ArrayList<>();

        try {
            // 1. 주문 조회
            order = findOrderByOrderIdUseCase.findById(orderId);
            orderItems = findOrderItemByOrderIdUseCase.findByOrderId(orderId);

            // 2. 유저 잔액 확인
            UserDto user = findUserUseCase.findById(order.userId());
            totalAmount = order.totalAmount();
            if (user.balance() < totalAmount) {
                throw new IllegalStateException("잔액 부족");
            }

            // 3. 재고 차감
            for (OrderItemDto item : orderItems) {
                deductStockUseCase.deductStock(item.optionId(), item.quantity());
            }
            stockDeducted = true;

            // 4. 잔액 차감
            deductUserBalanceUseCase.deduct(order.userId(), totalAmount);
            balanceDeducted = true;

            // 5. 거래내역 저장
            saveTransactionUseCase.save(order.userId(), TransactionType.USE, totalAmount);

            // 6. 쿠폰 상태 변경
            for (OrderItemDto item : orderItems) {
                if (item.userCouponId() != null) {
                    changeUserCouponStatusUseCase.changeStatus(item.userCouponId(), UserCouponStatus.USED);
                    usedCouponIds.add(item.userCouponId());
                }
            }

            // 7. 주문 상태 업데이트
            changeOrderStatusUseCase.changeStatus(orderId, OrderStatus.AFTER_PAYMENT);

            // 8. 결제 내역 저장
            paymentId = savePaymentUseCase.save(new SavePaymentCommand(
                    order.orderId(),
                    order.userId(),
                    totalAmount,
                    order.totalDiscountAmount(),
                    PaymentStatus.AFTER_PAYMENT
            ));




            return paymentId;
        } catch (Exception e) {
            // 1. 재고 복원
            if (stockDeducted && orderItems != null) {
                for (OrderItemDto item : orderItems) {
                    try {
                        addStockUseCase.addStock(item.optionId(), item.quantity());
                    } catch (Exception ex) {

                    }
                }
            }

            // 2. 잔액 복원
            if (balanceDeducted) {
                try {
                    chargeUserBalanceUseCase.charge(order.userId(), totalAmount);
                } catch (Exception ex) {

                }
            }

            // 3. 쿠폰 상태 복원
            for (Long couponId : usedCouponIds) {
                try {
                    changeUserCouponStatusUseCase.changeStatus(couponId, UserCouponStatus.ISSUED);
                } catch (Exception ex) {

                }
            }

            try{
                changeOrderStatusUseCase.changeStatus(orderId, OrderStatus.BEFORE_PAYMENT);
            } catch (Exception ex) {

            }

            throw e;
        }
    }
}