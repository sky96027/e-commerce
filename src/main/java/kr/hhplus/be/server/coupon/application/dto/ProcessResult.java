package kr.hhplus.be.server.coupon.application.dto;

/**
 * 큐에서 1건 처리 결과 DTO
 */
public record ProcessResult(Status status, String reservationId, String message) {

    public enum Status {
        SUCCESS,        // 정상 발급 완료
        SOLD_OUT,       // 잔량 소진
        ALREADY_ISSUED, // 중복 발급(유니크 제약 충돌 등)
        NOT_FOUND,      // 예약ID/커맨드 없음
        RETRY,          // 일시 오류(재시도 요망)
        FAIL            // 비재시도 오류
    }

    public static ProcessResult success(String rid) {
        return new ProcessResult(Status.SUCCESS, rid, null);
    }
    public static ProcessResult soldOut(String rid) {
        return new ProcessResult(Status.SOLD_OUT, rid, "sold out");
    }
    public static ProcessResult alreadyIssued(String rid) {
        return new ProcessResult(Status.ALREADY_ISSUED, rid, "already issued");
    }
    public static ProcessResult notFound(String rid) {
        return new ProcessResult(Status.NOT_FOUND, rid, "reservation/command not found");
    }
    public static ProcessResult retry(String rid, String msg) {
        return new ProcessResult(Status.RETRY, rid, msg);
    }
    public static ProcessResult fail(String rid, String msg) {
        return new ProcessResult(Status.FAIL, rid, msg);
    }
}
