package kr.hhplus.be.server.user.presentation.contract;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.user.presentation.dto.UserRequest;
import kr.hhplus.be.server.user.presentation.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "유저", description = "유저 관련 API")
public interface UserApiSpec {

    @Operation(summary = "유저 잔액 조회")
    ResponseEntity<UserResponse.findById> findById(@PathVariable("id") long userId);

    @Operation(summary = "유저 잔액 충전")
    ResponseEntity<Void> chargeBalance(@RequestParam UserRequest.ChargeBalance request);

    @Operation(summary = "유저 거래 내역 조회")
    ResponseEntity<List<UserResponse.getUserTransactionHistories>> getUserTransactionHistories(@PathVariable("id") long userId);

}
