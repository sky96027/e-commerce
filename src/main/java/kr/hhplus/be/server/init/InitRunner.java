package kr.hhplus.be.server.init;

import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitRunner implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            User saved = userRepository.insert(11000L);
            System.out.println("유저 저장 완료: " + saved.getUserId() + ", 잔액: " + saved.getBalance());
        }
    }
}