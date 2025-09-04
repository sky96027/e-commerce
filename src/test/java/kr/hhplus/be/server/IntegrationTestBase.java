package kr.hhplus.be.server;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    // MySQL 8
    static final MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0.36")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    // Redis 7
    static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
                    .withExposedPorts(6379);

    // Kafka
    static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

    @BeforeAll
    static void startAll() {
        mysql.start();
        redis.start();
        kafka.start();
    }

    @DynamicPropertySource
    static void bind(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", mysql::getJdbcUrl);
        r.add("spring.datasource.username", mysql::getUsername);
        r.add("spring.datasource.password", mysql::getPassword);

        r.add("spring.data.redis.host", redis::getHost);
        r.add("spring.data.redis.port", () -> redis.getMappedPort(6379));

        r.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);}
}
