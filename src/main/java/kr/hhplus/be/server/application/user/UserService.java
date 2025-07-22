package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.application.user.dto.TransactionHistoryDto;
import kr.hhplus.be.server.application.user.dto.UserDto;
import kr.hhplus.be.server.domain.transactionhistory.TransactionHistoryEntity;
import kr.hhplus.be.server.domain.transactionhistory.TransactionHistoryRepository;
import kr.hhplus.be.server.domain.transactionhistory.TransactionType;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 유저 도메인에 대한 애플리케이션 서비스
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public UserService(
            UserRepository userRepository,
            TransactionHistoryRepository transactionHistoryRepository
    ) {
        this.userRepository = userRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    // 유저 조회
    public UserDto findById(long userId) {
        UserEntity user = userRepository.selectById(userId);
        return UserDto.from(user);
    }
    // 유저 잔고 충전
    public UserDto chargeBalance(long userId, long amount) {
        UserEntity user = userRepository.selectById(userId);
        UserEntity updated = user.charge(amount);

        TransactionHistoryEntity history = new TransactionHistoryEntity(updated, TransactionType.CHARGE, amount);
        transactionHistoryRepository.save(history);

        UserEntity saved = userRepository.insertOrUpdate(userId, updated.getBalance());
        return UserDto.from(saved);
    }

    // 유저 잔액 거래 내역 조회
    public List<TransactionHistoryDto> getUserBalanceHistories(long userId) {
        return transactionHistoryRepository.selectByUserId(userId).stream()
                .map(TransactionHistoryDto::from)
                .toList();
    }
}
