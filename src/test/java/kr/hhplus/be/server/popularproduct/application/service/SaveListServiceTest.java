package kr.hhplus.be.server.popularproduct.application.service;

import kr.hhplus.be.server.popularproduct.domain.repository.PopularProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SaveListServiceTest {
    @Mock
    private PopularProductRepository popularProductRepository;
    @InjectMocks
    private SaveListService saveListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        saveListService = new SaveListService(popularProductRepository);
    }

    @Test
    @DisplayName("replaceAll 정상 호출")
    void replaceAll_success() {
        // given
        doNothing().when(popularProductRepository).replaceAll();

        // when
        saveListService.replaceAll();

        // then
        verify(popularProductRepository, times(1)).replaceAll();
    }
} 