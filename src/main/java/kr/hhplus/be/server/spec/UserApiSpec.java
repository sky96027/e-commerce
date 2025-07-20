package kr.hhplus.be.server.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.controller.dto.UserRequest;
import kr.hhplus.be.server.controller.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "유저", description = "유저 관련 API")
public interface UserApiSpec {

    @Operation(summary = "유저 잔액 조회")
    ResponseEntity<UserResponse.GetUserInfo> getUserInfo(@PathVariable("id") Long userId);

    @Operation(summary = "유저 잔액 충전")
    ResponseEntity<Void> chargeUserBalance(@RequestParam UserRequest.ChargeUserBalance request);

    @Operation(summary = "유저 거래 내역 조회")
    ResponseEntity<List<UserResponse.GetUserTransactions>> getUserTransactions(@PathVariable("id") Long userId);

}
