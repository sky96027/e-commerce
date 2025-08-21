package kr.hhplus.be.server.coupon.application.worker;

import kr.hhplus.be.server.coupon.application.dto.ProcessResult;
import kr.hhplus.be.server.coupon.application.usecase.ProcessCouponIssueUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 큐(쿠폰별 ZSET)를 주기적으로 소모해 발급을 처리하는 워커.
 * - 여러 쿠폰 ID를 콤마로 받아 순회 처리
 * - tick마다 쿠폰별 최대 N건만 처리하여 한 사이클 과점 방지
 *
 * 필요: @EnableScheduling (예: @SpringBootApplication 위치 또는 별도 Config)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueWorker {

    private final ProcessCouponIssueUseCase processCouponIssueUseCase;

    /** 처리 대상 쿠폰ID 목록. 예) 1001,1002,1003 */
    @Value("${coupon.issue.worker.coupon-ids:}")
    private String couponIdsProp;

    /** 한 tick에서 쿠폰별 최대 처리 건수 */
    @Value("${coupon.issue.worker.max-drain-per-tick:100}")
    private int maxDrainPerTick;

    /** tick 간격(ms). fixedDelay는 이전 실행 종료 후 대기시간 */
    @Scheduled(fixedDelayString = "${coupon.issue.worker.fixed-delay-ms:10}")
    public void drainQueues() {
        List<Long> couponIds = parseCouponIds(couponIdsProp);
        if (couponIds.isEmpty()) return;

        for (long couponId : couponIds) {
            int drained = 0;
            while (drained < maxDrainPerTick) {
                ProcessResult result = processCouponIssueUseCase.process(couponId);
                switch (result.status()) {
                    case SUCCESS -> {
                        drained++;
                        if (log.isDebugEnabled()) {
                            log.debug("Issued couponId={} rid={} (drained={})", couponId, result.reservationId(), drained);
                        }
                    }
                    case ALREADY_ISSUED -> {
                        drained++;
                        log.debug("Already issued couponId={} rid={}", couponId, result.reservationId());
                    }
                    case SOLD_OUT -> {
                        drained++;
                        log.debug("Sold out couponId={} rid={}", couponId, result.reservationId());
                    }
                    case NOT_FOUND -> {
                        // 해당 쿠폰 큐가 비었음 → 다음 쿠폰으로
                        if (drained == 0 && log.isTraceEnabled()) {
                            log.trace("Queue empty for couponId={}", couponId);
                        }
                        drained = maxDrainPerTick; // break while
                    }
                    case RETRY -> {
                        // 일시 오류 → 과도한 재시도 방지 위해 다음 쿠폰으로 넘어감
                        log.warn("Retry suggested for couponId={} rid={}, msg={}",
                                couponId, result.reservationId(), result.message());
                        drained = maxDrainPerTick; // break while
                    }
                    case FAIL -> {
                        // 비재시도 오류 → 알람/모니터링
                        log.error("Fail processing couponId={} rid={}, msg={}",
                                couponId, result.reservationId(), result.message());
                        drained++;
                    }
                }
            }
        }
    }

    private static List<Long> parseCouponIds(String prop) {
        if (prop == null || prop.isBlank()) return List.of();
        return Arrays.stream(prop.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
