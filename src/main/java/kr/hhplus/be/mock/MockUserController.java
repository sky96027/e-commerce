package kr.hhplus.be.mock;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mock")
public class MockUserController {

    private static final Map<Long, Long> mockUser = new HashMap<>();
    private static final Map<Long, List<TransactionHistory>> transactionHistoryStore = new HashMap<>();
    private static long transactionIdSeq = 1L;

    static {
        mockUser.put(1L, 10000L);
        mockUser.put(2L, 5000L);
        mockUser.put(3L, 0L);
    }

    @GetMapping("/user/{id}")
    public UserResponse getUserInfo(@PathVariable("id") Long userId) {
        long balance = mockUser.getOrDefault(userId, 0L);
        return new UserResponse(userId, balance);
    }

    @PostMapping("/user/{id}/charge")
    public UserResponse chargeUserBalance(@PathVariable("id") Long userId,
                                          @RequestParam Long amount) {
        long current = mockUser.getOrDefault(userId, 0L);
        long updated = current + amount;
        mockUser.put(userId, updated);
        return new UserResponse(userId, updated);
    }

    @GetMapping("/user/{id}/transactions")
    public List<TransactionHistory> getUserTransactions(@PathVariable("id") Long userId) {
        return transactionHistoryStore.getOrDefault(userId, new ArrayList<>());
    }

    static class UserResponse {
        private Long userId;
        private Long balance;

        public UserResponse(Long userId, Long balance) {
            this.userId = userId;
            this.balance = balance;
        }

        public Long getUserId() {
            return userId;
        }

        public Long getBalance() {
            return balance;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public void setBalance(Long balance) {
            this.balance = balance;
        }
    }


    static class TransactionHistory {
        private Long transactionId;
        private Long userId;
        private String type;
        private LocalDateTime transactionTime;
        private Long amount;

        public TransactionHistory(Long transactionId, Long userId, String type, LocalDateTime transactionTime, Long amount) {
            this.transactionId = transactionId;
            this.userId = userId;
            this.type = type;
            this.transactionTime = transactionTime;
            this.amount = amount;
        }

        public Long getTransactionId() {
            return transactionId;
        }

        public Long getUserId() {
            return userId;
        }

        public String getType() {
            return type;
        }

        public LocalDateTime getTransactionTime() {
            return transactionTime;
        }

        public Long getAmount() {
            return amount;
        }

        public void setTransactionId(Long transactionId) {
            this.transactionId = transactionId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setTransactionTime(LocalDateTime transactionTime) {
            this.transactionTime = transactionTime;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }
    }
}