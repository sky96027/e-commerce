package kr.hhplus.be.server.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class RedisPubSubWaitRegistry implements InitializingBean {

    private final RedisMessageListenerContainer container;

    // 채널별 대기자 리스트
    private final ConcurrentMap<String, CopyOnWriteArrayList<CompletableFuture<String>>> waiters = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() {
        container.addMessageListener((message, pattern) -> {
            String channel = new String(message.getChannel(), java.nio.charset.StandardCharsets.UTF_8);
            String payload = new String(message.getBody(), java.nio.charset.StandardCharsets.UTF_8);

            var list = waiters.remove(channel); // 채널에 걸린 모든 대기자 깨우기
            if (list != null) list.forEach(f -> f.complete(payload));
        }, new org.springframework.data.redis.listener.PatternTopic("lock:*:ch")); // 항상-켜진 pSub
    }

    /** 특정 채널 알림을 기다림 */
    public CompletableFuture<String> await(String channel) {
        var f = new CompletableFuture<String>();
        waiters.compute(channel, (ch, cur) -> {
            if (cur == null) cur = new CopyOnWriteArrayList<>();
            cur.add(f);
            return cur;
        });
        return f;
    }

    /** 타임아웃/취소 시 대기자 제거 */
    public void cancel(String channel, CompletableFuture<String> f) {
        var list = waiters.get(channel);
        if (list != null) {
            list.remove(f);
            if (list.isEmpty()) waiters.remove(channel, list);
        }
    }
}