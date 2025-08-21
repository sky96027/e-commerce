package kr.hhplus.be.server.popularproduct.infrastructure.redis;

import kr.hhplus.be.server.common.redis.cache.events.StockChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class PopularityCollector {

    private final StringRedisTemplate redis;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStockChanged(StockChangedEvent e) {
        if (!StockChangedEvent.DEDUCT.equals(e.changeType())) return; // 판매만 집계

        String bucket = "pop:z:5m:" + floor5m(Instant.now());
        String member = "product:" + e.productId();

        redis.opsForZSet().incrementScore(bucket, member, e.quantity());
        redis.expire(bucket, java.time.Duration.ofHours(25)); // 24h 윈도우 + 여유
    }

    private String floor5m(Instant now) {
        long sec = now.atZone(KST).toEpochSecond();
        long base = (sec / 300) * 300;
        return FMT.format(LocalDateTime.ofEpochSecond(base, 0, KST.getRules().getOffset(now)));
    }
}
