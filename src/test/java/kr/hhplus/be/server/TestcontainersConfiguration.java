package kr.hhplus.be.server;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import kr.hhplus.be.server.common.redis.cache.StockCounter;
import kr.hhplus.be.server.common.redis.lock.RedisDistributedLockManager;

import static org.mockito.Mockito.mock;

@TestConfiguration
class TestcontainersConfiguration {

	public static final MySQLContainer<?> MYSQL_CONTAINER;

	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("hhplus")
			.withUsername("test")
			.withPassword("test");
		MYSQL_CONTAINER.start();

		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
	}

	@PreDestroy
	public void preDestroy() {
		if (MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
	}

	/**
	 * 테스트용 Redis Mock 설정
	 * Redis가 제대로 동작하지 않는 테스트 환경에서 사용
	 */
	@Bean
	@Primary
	@Profile("test")
	public StockCounter mockStockCounter() {
		return mock(StockCounter.class);
	}

	@Bean
	@Primary
	@Profile("test")
	public RedisDistributedLockManager mockLockManager() {
		return mock(RedisDistributedLockManager.class);
	}
}