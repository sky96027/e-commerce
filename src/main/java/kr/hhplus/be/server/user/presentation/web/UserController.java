package kr.hhplus.be.server.user.presentation.web;

import kr.hhplus.be.server.transactionhistory.application.dto.TransactionHistoryDto;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.facade.UserFacade;
import kr.hhplus.be.server.user.application.usecase.FindUserUseCase;
import kr.hhplus.be.server.user.presentation.contract.UserApiSpec;
import kr.hhplus.be.server.user.presentation.dto.UserRequest;
import kr.hhplus.be.server.user.presentation.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController implements UserApiSpec {

    private final FindUserUseCase findUserUseCase;
    private final UserFacade userFacade;

    public UserController(FindUserUseCase findUserUseCase, UserFacade userFacade) {
        this.findUserUseCase = findUserUseCase;
        this.userFacade = userFacade;
    }

    // 유저 정보 조회
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<UserResponse.GetUserCoupon> findById(@PathVariable("id") long userId) {
        UserDto user = findUserUseCase.findById(userId);
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
        userFacade.chargeWithHistory(request.userId(), request.amount());
        return ResponseEntity.ok().build();
    }

    // 유저 거래 내역 조회
    @GetMapping("/{id}/transactions")
    @Override
    public ResponseEntity<List<UserResponse.getUserTransactionHistories>> getUserTransactionHistories(@PathVariable("id") long userId) {
        List<TransactionHistoryDto> histories = userFacade.findUserHistories(userId);

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