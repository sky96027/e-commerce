package kr.hhplus.be.server.popularproduct.integration;

import kr.hhplus.be.server.popularproduct.application.dto.PopularProductDto;
import kr.hhplus.be.server.popularproduct.application.service.FindPopularProductSummaryService;
import kr.hhplus.be.server.popularproduct.application.service.SaveListService;
import kr.hhplus.be.server.popularproduct.domain.model.PopularProduct;
import kr.hhplus.be.server.popularproduct.domain.repository.PopularProductRepository;
import kr.hhplus.be.server.popularproduct.infrastructure.entity.PopularProductJpaEntity;
import kr.hhplus.be.server.popularproduct.infrastructure.repository.PopularProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("통합 테스트 - 인기 상품")
public class PopularProductIntegrationTest {


} 