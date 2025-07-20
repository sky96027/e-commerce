package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.controller.dto.UserRequest;
import kr.hhplus.be.server.controller.dto.UserResponse;
import kr.hhplus.be.server.spec.UserApiSpec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController implements UserApiSpec {

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<UserResponse.GetUserInfo> getUserInfo(@PathVariable("id") Long id) {
        UserResponse.GetUserInfo response = new UserResponse.GetUserInfo(
                id,
                1000L
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/charge")
    @Override
    public ResponseEntity<Void> chargeUserBalance(@RequestParam UserRequest.ChargeUserBalance request) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/transactions")
    @Override
    public ResponseEntity<List<UserResponse.GetUserTransactions>> getUserTransactions(@PathVariable("id") Long userId) {
        List<UserResponse.GetUserTransactions> response = List.of(
                new UserResponse.GetUserTransactions(
                        1L,
                        userId,
                        "CHARGE",
                        LocalDateTime.now().minusDays(1),
                        10000L
                ),
                new UserResponse.GetUserTransactions(
                        2L,
                        userId,
                        "USE",
                        LocalDateTime.now().minusHours(3),
                        5000L
                )
        );
        return ResponseEntity.ok(response);
    }
}