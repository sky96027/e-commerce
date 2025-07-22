package kr.hhplus.be.server.controller.user;

import kr.hhplus.be.server.application.user.UserService;
import kr.hhplus.be.server.application.user.dto.TransactionHistoryDto;
import kr.hhplus.be.server.application.user.dto.UserDto;
import kr.hhplus.be.server.controller.user.dto.UserRequest;
import kr.hhplus.be.server.controller.user.dto.UserResponse;
import kr.hhplus.be.server.controller.spec.UserApiSpec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController implements UserApiSpec {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 유저 정보 조회
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<UserResponse.findById> findById(@PathVariable("id") long userId) {
        UserDto user = userService.findById(userId);
        UserResponse.findById response = new UserResponse.findById(
                user.userId(),
                user.balance()
        );
        return ResponseEntity.ok(response);
    }

    // 유저 잔액 충전
    @PostMapping("/{id}/charge")
    @Override
    public ResponseEntity<Void> chargeBalance(@RequestBody UserRequest.ChargeBalance request) {
        userService.chargeBalance(request.userId(), request.amount());
        return ResponseEntity.ok().build();
    }

    // 유저 거래 내역 조회
    @GetMapping("/{id}/transactions")
    @Override
    public ResponseEntity<List<UserResponse.getUserTransactionHistories>> getUserTransactionHistories(@PathVariable("id") long userId) {
        List<TransactionHistoryDto> histories = userService.getUserBalanceHistories(userId);

        List<UserResponse.getUserTransactionHistories> response = histories.stream()
                .map(dto -> new UserResponse.getUserTransactionHistories(
                        dto.transactionId(),
                        dto.userId(),
                        dto.type().name(),
                        dto.transactionTime(),
                        dto.amount()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}