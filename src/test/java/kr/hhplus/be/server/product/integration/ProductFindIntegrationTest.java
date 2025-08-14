package kr.hhplus.be.server.product.integration;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.product.application.dto.ProductDetailDto;
import kr.hhplus.be.server.product.application.dto.ProductDto;
import kr.hhplus.be.server.product.application.dto.ProductOptionDto;
import kr.hhplus.be.server.product.application.dto.ProductSummaryDto;
import kr.hhplus.be.server.product.application.facade.ProductFacade;
import kr.hhplus.be.server.product.application.service.FindDetailService;
import kr.hhplus.be.server.product.application.service.FindProductOptionsService;
import kr.hhplus.be.server.product.application.service.FindProductSummaryService;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import kr.hhplus.be.server.product.domain.type.ProductStatus;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import kr.hhplus.be.server.product.infrastructure.entity.ProductOptionJpaEntity;
import kr.hhplus.be.server.product.infrastructure.repository.ProductJpaRepository;
import kr.hhplus.be.server.product.infrastructure.repository.ProductOptionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@DisplayName("통합 테스트 - 상품 조회")
public class ProductFindIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private FindProductSummaryService findProductSummaryService;

    @Autowired
    private FindDetailService findDetailService;

    @Autowired
    private FindProductOptionsService findProductOptionsService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private ProductOptionJpaRepository productOptionJpaRepository;

    private Product savedProduct;
    private ProductOption savedOption1;
    private ProductOption savedOption2;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        createTestData();
    }

    private void createTestData() {
        // 상품 생성
        Product product = new Product(
                null,
                "테스트 상품",
                ProductStatus.ON_SALE,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );

        ProductJpaEntity productEntity = new ProductJpaEntity(
                null,
                product.getProductName(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getExpiredAt()
        );
        ProductJpaEntity savedProductEntity = productJpaRepository.save(productEntity);

        savedProduct = new Product(
                savedProductEntity.getProductId(),
                savedProductEntity.getProductName(),
                savedProductEntity.getStatus(),
                savedProductEntity.getCreatedAt(),
                savedProductEntity.getExpiredAt()
        );

        ProductOption option1 = new ProductOption(
                null,
                savedProduct.getProductId(),
                "옵션 1",
                ProductOptionStatus.ON_SALE,
                10000L,
                100,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );

        ProductOption option2 = new ProductOption(
                null,
                savedProduct.getProductId(),
                "옵션 2",
                ProductOptionStatus.ON_SALE,
                15000L,
                50,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );

        ProductOptionJpaEntity option1Entity = new ProductOptionJpaEntity(
                null,
                option1.getProductId(),
                option1.getContent(),
                option1.getStatus(),
                option1.getPrice(),
                option1.getStock(),
                option1.getCreatedAt(),
                option1.getExpiredAt()
        );
        ProductOptionJpaEntity option2Entity = new ProductOptionJpaEntity(
                null,
                option2.getProductId(),
                option2.getContent(),
                option2.getStatus(),
                option2.getPrice(),
                option2.getStock(),
                option2.getCreatedAt(),
                option2.getExpiredAt()
        );

        ProductOptionJpaEntity savedOption1Entity = productOptionJpaRepository.save(option1Entity);
        ProductOptionJpaEntity savedOption2Entity = productOptionJpaRepository.save(option2Entity);

        savedOption1 = new ProductOption(
                savedOption1Entity.getOptionId(),
                savedOption1Entity.getProductId(),
                savedOption1Entity.getContent(),
                savedOption1Entity.getStatus(),
                savedOption1Entity.getPrice(),
                savedOption1Entity.getStock(),
                savedOption1Entity.getCreatedAt(),
                savedOption1Entity.getExpiredAt()
        );

        savedOption2 = new ProductOption(
                savedOption2Entity.getOptionId(),
                savedOption2Entity.getProductId(),
                savedOption2Entity.getContent(),
                savedOption2Entity.getStatus(),
                savedOption2Entity.getPrice(),
                savedOption2Entity.getStock(),
                savedOption2Entity.getCreatedAt(),
                savedOption2Entity.getExpiredAt()
        );
    }

    @Test
    @DisplayName("상품 목록을 정상적으로 조회한다")
    void findProductSummary_success() {
        // when
        List<ProductDto> result = findProductSummaryService.findSummary();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        
        // 생성된 테스트 상품이 목록에 포함되어 있는지 확인
        boolean hasTestProduct = result.stream()
                .anyMatch(product -> product.productName().equals("테스트 상품"));
        assertThat(hasTestProduct).isTrue();
    }

    @Test
    @DisplayName("상품 상세 정보를 정상적으로 조회한다")
    void findProductDetail_success() {
        // given
        long productId = savedProduct.getProductId();

        // when
        ProductDto result = findDetailService.findById(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.productId()).isEqualTo(productId);
        assertThat(result.productName()).isEqualTo("테스트 상품");
        assertThat(result.status()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.expiredAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 조회 시 예외 발생")
    void findProductDetail_notFound_throwsException() {
        // given
        long productId = 999L;

        // when & then
        assertThatThrownBy(() -> findDetailService.findById(productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("상품 옵션을 정상적으로 조회한다")
    void findProductOptions_success() {
        // given
        long productId = savedProduct.getProductId();

        // when
        List<ProductOptionDto> result = findProductOptionsService.findByProductId(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        // 옵션 내용 확인
        List<String> optionContents = result.stream()
                .map(ProductOptionDto::content)
                .toList();
        assertThat(optionContents).contains("옵션 1", "옵션 2");
        
        // 가격 확인
        List<Long> prices = result.stream()
                .map(ProductOptionDto::price)
                .toList();
        assertThat(prices).contains(10000L, 15000L);
    }

    @Test
    @DisplayName("존재하지 않는 상품의 옵션 조회 시 빈 리스트 반환")
    void findProductOptions_notFound_returnsEmptyList() {
        // given
        long productId = 999L;

        // when
        List<ProductOptionDto> result = findProductOptionsService.findByProductId(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ProductFacade를 통한 상품 요약 정보 조회")
    void getProductSummaries_success() {
        // when
        List<ProductSummaryDto> result = productFacade.getProductSummaries();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        
        // 생성된 테스트 상품이 목록에 포함되어 있는지 확인
        boolean hasTestProduct = result.stream()
                .anyMatch(product -> product.productName().equals("테스트 상품"));
        assertThat(hasTestProduct).isTrue();
        
        // 최소 가격이 설정되어 있는지 확인
        ProductSummaryDto testProductSummary = result.stream()
                .filter(product -> product.productName().equals("테스트 상품"))
                .findFirst()
                .orElse(null);
        assertThat(testProductSummary).isNotNull();
        assertThat(testProductSummary.minPrice()).isEqualTo(10000L); // 가장 낮은 옵션 가격
    }

    @Test
    @DisplayName("ProductFacade를 통한 상품 상세 정보 조회")
    void getProductDetail_success() {
        // given
        long productId = savedProduct.getProductId();

        // when
        ProductDetailDto result = productFacade.getProductDetail(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.productId()).isEqualTo(productId);
        assertThat(result.productName()).isEqualTo("테스트 상품");
        assertThat(result.status()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(result.options()).isNotNull();
        assertThat(result.options()).hasSize(2);
        
        // 옵션 상세 정보 확인
        ProductOptionDto option1 = result.options().stream()
                .filter(option -> option.content().equals("옵션 1"))
                .findFirst()
                .orElse(null);
        assertThat(option1).isNotNull();
        assertThat(option1.price()).isEqualTo(10000L);
        assertThat(option1.stock()).isEqualTo(100);
        
        ProductOptionDto option2 = result.options().stream()
                .filter(option -> option.content().equals("옵션 2"))
                .findFirst()
                .orElse(null);
        assertThat(option2).isNotNull();
        assertThat(option2.price()).isEqualTo(15000L);
        assertThat(option2.stock()).isEqualTo(50);
    }

    @Test
    @DisplayName("ProductFacade를 통한 존재하지 않는 상품 조회 시 예외 발생")
    void getProductDetail_notFound_throwsException() {
        // given
        long productId = 999L;

        // when & then
        assertThatThrownBy(() -> productFacade.getProductDetail(productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }
} 