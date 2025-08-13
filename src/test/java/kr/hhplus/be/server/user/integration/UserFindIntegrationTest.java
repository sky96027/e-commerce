package kr.hhplus.be.server.user.integration;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.service.FindUserService;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("통합 테스트 - 사용자 조회")
public class UserFindIntegrationTest extends IntegrationTestBase {

    @Autowired
    private FindUserService findUserService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 ID로 정상 조회")
    void findById_success() {
        // given
        User user = userRepository.insert(10000L);
        long userId = user.getUserId();

        // when
        UserDto result = findUserService.findById(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.balance()).isEqualTo(10000L);
    }
}
