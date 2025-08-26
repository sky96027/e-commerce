package kr.hhplus.be.server.common.redis.cache;

public class CacheKeyUtil {
    private static final String GLOBAL_PREFIX = "cache:v1:";


    // === Product ===
    public static String productDetailKey(long productId) {
        return GLOBAL_PREFIX + "product:detail:" + productId;
    }

    public static String productOptionsKey(long productId) {
        return GLOBAL_PREFIX + "product:options:" + productId;
    }

    // === Stock ===
    public static String stockHashKey(long productId) {
        return GLOBAL_PREFIX + "stock:product:" + productId;
    }

    // === Order ===
    public static String orderSummaryKey(long orderId) {
        return GLOBAL_PREFIX + "order:summary:" + orderId;
    }

    // === Coupon ===
    public static String couponUserSummaryKey(long userId) {
        return GLOBAL_PREFIX + "coupon:userSummary:" + userId;
    }

    public static String couponPolicyKey(long policyId) {
        return GLOBAL_PREFIX + "coupon:policy:" + policyId;
    }

    // === Transaction ===
    public static String transactionRecentKey(long userId) {
        return GLOBAL_PREFIX + "tx:recent:" + userId;
    }

    // === Popular ===
    public static String popularTopKey() {
        return GLOBAL_PREFIX + "popular:top";
    }

}
