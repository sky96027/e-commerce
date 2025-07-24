package kr.hhplus.be.server.order.application.dto;

import java.util.List;

/**
 * 주문 저장 요청용 Command DTO
 */
public record SaveOrderCommand(
        long userId,
        List<SaveOrderItemCommand> items
) {}
