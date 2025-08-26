package kr.hhplus.be.server.common.cache.events;

/**
 * 재고 변경을 알리는 이벤트
 * 
 * @param productId 상품 ID
 * @param optionId 옵션 ID
 * @param changeType 변경 타입 (DEDUCT, INCREMENT, SET)
 * @param quantity 변경된 수량
 */
public record StockChangedEvent(long productId, long optionId, String changeType, int quantity) {
    
    public static final String DEDUCT = "DEDUCT";
    public static final String INCREMENT = "INCREMENT";
    public static final String SET = "SET";
}
