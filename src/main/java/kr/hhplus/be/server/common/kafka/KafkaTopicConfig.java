package kr.hhplus.be.server.common.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * 예상되는 트래픽대로 파티션 수 변경,
 * producer 에는 @KafkaListener 어노테이션에 concurrency = "파티션 수" 추가
 */
@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name("payment-completed")
            .partitions(12)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic couponIssueTopic() {
        return TopicBuilder.name("coupon-issue")
                .partitions(12)
                .replicas(3)
                .build();
    }
}
