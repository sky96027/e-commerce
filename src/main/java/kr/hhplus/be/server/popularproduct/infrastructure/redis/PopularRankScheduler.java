package kr.hhplus.be.server.popularproduct.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(
        prefix = "popular.rank",
        name = "scheduler-enabled",
        havingValue = "true",
        matchIfMissing = false
)
@Component
@RequiredArgsConstructor
public class PopularRankScheduler {

    private final StringRedisTemplate redis;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final String DEST = "pop:rank:24h";

    @Scheduled(cron = "0 */5 * * * *")
    public void build24hRanking() {
        List<String> buckets = lastBuckets(288); // 24h = 5m x 288
        if (buckets.isEmpty()) return;

        redis.execute((RedisConnection con) -> {
            byte[][] keys = buckets.stream().map(k -> k.getBytes(StandardCharsets.UTF_8)).toArray(byte[][]::new);
            con.zUnionStore(DEST.getBytes(StandardCharsets.UTF_8), keys);
            return null;
        });

        Long total = redis.opsForZSet().zCard(DEST);
        if (total != null && total > 1000) {
            redis.opsForZSet().removeRange(DEST, 0, total - 1001);
        }
    }

    private List<String> lastBuckets(int n) {
        List<String> keys = new ArrayList<>(n);
        Instant now = Instant.now();
        long sec = now.atZone(KST).toEpochSecond();
        long base = (sec / 300) * 300;
        for (int i = 0; i < n; i++) {
            long t = base - (long) i * 300;
            keys.add("pop:z:5m:" + LocalDateTime.ofEpochSecond(t, 0, KST.getRules().getOffset(now)).format(F));
        }
        return keys;
    }
}
