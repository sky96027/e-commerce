package kr.hhplus.be.server.user.domain.model;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.user.exception.UserErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("유저 생성 시 필드가 정확히 설정된다")
    void constructor_initializesFieldsCorrectly() {
        // given
        long userId = 1L;
        long balance = 1000L;

        // when
        User user = new User(userId, balance);

        // then
        assertThat(user.getUserId()).isEqualTo(userId);
        assertThat(user.getBalance()).isEqualTo(balance);
    }

    @Test
    @DisplayName("empty 유저는 잔액이 0이다")
    void emptyUser_hasZeroBalance() {
        // when
        User user = User.empty(1L);

        // then
        assertThat(user.getUserId()).isEqualTo(1L);
        assertThat(user.getBalance()).isEqualTo(0L);
    }

    @Test
    @DisplayName("양수 금액 충전 성공")
    void charge_positiveAmount_success() {
        // given
        User user = new User(1L, 1000L);

        // when
        User updated = user.charge(500L);

        // then
        assertThat(updated.getBalance()).isEqualTo(1500L);
    }

    // db update 로 책임 이동
    /*@Test
    @DisplayName("음수 금액 충전 시 예외 발생")
    void charge_negativeAmount_throwsException() {
        User user = new User(1L, 1000L);

        assertThatThrownBy(() -> user.charge(-100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 금액은 음수일 수 없습니다.");
    }*/

    @Test
    @DisplayName("정상 금액 차감 성공")
    void deduct_validAmount_success() {
        // given
        User user = new User(1L, 1000L);

        // when
        User updated = user.deduct(500L);

        // then
        assertThat(updated.getBalance()).isEqualTo(500L);
    }

    // db update 로 책임 이동
    /*@Test
    @DisplayName("음수 금액 차감 시 예외 발생")
    void deduct_negativeAmount_throwsException() {
        User user = new User(1L, 1000L);

        assertThatThrownBy(() -> user.deduct(-300L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("차감 금액은 음수일 수 없습니다.");
    }*/

    @Test
    @DisplayName("잔액 부족 시 차감 예외 발생")
    void deduct_exceedingAmount_throwsException() {
        User user = new User(1L, 500L);

        assertThatThrownBy(() -> user.deduct(600L))
                .isInstanceOf(RestApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NOT_ENOUGH_BALANCE_ERROR);
    }
}