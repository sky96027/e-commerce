### 인덱스 개선안
| 테이블                   | 인덱스 제안                        | 이유         |
| --------------------- | ----------------------------- | ---------- |
| `transaction_history` | `(user_id, transaction_time)` | 유저별 정렬 조회  |
| `order`               | `(user_id, ordered_at)`       | 최근 주문 조회   |
| `order_item`          | `order_id`                    | 주문별 아이템 조회 |
|                       | `(product_id, option_id)`     | 옵션 빠른 조인   |
| `payment`             | `(user_id, status)`           | 유저 결제 필터링  |
| `popular_product`     | `rank`                        | 순위 정렬      |
|                       | `reference_date`              | 날짜별 분석     |
| `user_coupon`         | `(user_id, expired_at)`       | 유저 쿠폰 만료순  |
| `coupon_issue`        | `policy_id`                   | 정책별 필터링    |

