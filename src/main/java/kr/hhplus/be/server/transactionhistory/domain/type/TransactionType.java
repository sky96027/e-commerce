package kr.hhplus.be.server.transactionhistory.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 거래 내역 유형을 나타내는 enum
 */
public enum TransactionType {
    CHARGE("CHARGE"), // 충전
    USE("USE");       // 사용
    
    private final String value;
    
    TransactionType(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
}