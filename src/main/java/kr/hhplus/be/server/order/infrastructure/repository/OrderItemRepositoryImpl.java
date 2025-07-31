package kr.hhplus.be.server.order.infrastructure.repository;

import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.order.infrastructure.entity.OrderItemJpaEntity;
import kr.hhplus.be.server.order.infrastructure.mapper.OrderItemMapper;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory OrderItemRepository 구현체
 */
@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository jpaRepository;
    private final OrderItemMapper mapper;

    public OrderItemRepositoryImpl(OrderItemJpaRepository jpaRepository, OrderItemMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<OrderItem> findAllByOrderId(long orderId) {
        return jpaRepository.findAllByOrderId(orderId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<OrderItem> items) {
        List<OrderItemJpaEntity> entities = items.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());

        jpaRepository.saveAll(entities);
    }
}