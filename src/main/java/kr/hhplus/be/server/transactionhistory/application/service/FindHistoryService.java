package kr.hhplus.be.server.transactionhistory.application.service;

import jakarta.annotation.PostConstruct;
import kr.hhplus.be.server.common.cache.CacheKeyUtil;
import kr.hhplus.be.server.coupon.application.dto.UserCouponDto;
import kr.hhplus.be.server.order.application.dto.OrderDto;
import kr.hhplus.be.server.transactionhistory.application.dto.TransactionHistoryDto;
import kr.hhplus.be.server.transactionhistory.application.usecase.FindHistoryUseCase;
import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [UseCase 구현체]
 * FindHistoryUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 TransactionHistoryRepository 사용하여 거래 내역 데이터를 조회하고,
 * 그 결과를 TransactionHistoryDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "거래 내역 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
@RequiredArgsConstructor
public class FindHistoryService implements FindHistoryUseCase {
    private final TransactionHistoryRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<TransactionHistoryDto> findAllByUserId(long userId) {
        String key = CacheKeyUtil.transactionRecentKey(userId);

        List<TransactionHistoryDto> cached = (List<TransactionHistoryDto>) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        List<TransactionHistoryDto> histories = repository.findAllByUserId(userId).stream()
                .map(TransactionHistoryDto::from)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, histories, Duration.ofMinutes(10));

        return histories;
    }
}
