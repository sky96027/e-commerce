-- 재귀 깊이 확장
SET SESSION cte_max_recursion_depth = 100000000;

-- 외래 키 제약 조건 일시적으로 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- 유저 테이블 생성
CREATE TABLE IF NOT EXISTS user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance BIGINT NOT NULL
);

-- exporter 계정 생성
CREATE USER IF NOT EXISTS '${MYSQL_EXPORTER_USER}'@'%' IDENTIFIED BY '${MYSQL_EXPORTER_PASSWORD}';
GRANT PROCESS, REPLICATION CLIENT, SELECT ON *.* TO '${MYSQL_EXPORTER_USER}'@'%';
FLUSH PRIVILEGES;

-- 거래 내역 테이블 생성
CREATE TABLE IF NOT EXISTS transaction_history (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    transaction_time DATETIME NOT NULL,
    amount BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
    );

-- 상품 테이블 생성
CREATE TABLE IF NOT EXISTS product (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    expired_at DATETIME
    );

-- 상품 옵션 테이블 생성
CREATE TABLE IF NOT EXISTS product_option (
    option_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    content VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    price BIGINT,
    stock INT,
    created_at DATETIME,
    expired_at DATETIME,
    FOREIGN KEY (product_id) REFERENCES product(product_id)
    );

-- 인기 상품 테이블 생성
CREATE TABLE IF NOT EXISTS popular_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    total_sold_quantity INT NOT NULL,
    `rank` INT NOT NULL,
    reference_date DATE NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(product_id)
    );

-- 결제 테이블 생성
CREATE TABLE IF NOT EXISTS payment (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    total_amount_snapshot BIGINT NOT NULL,
    total_discount_amount_snapshot BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
    );

-- 주문 테이블 생성
CREATE TABLE IF NOT EXISTS `order` (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount BIGINT NOT NULL,
    total_discount_amount BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    ordered_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
    );

-- 주문 아이템 테이블 생성
CREATE TABLE IF NOT EXISTS order_item (
    order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_price BIGINT NOT NULL,
    discount_amount BIGINT NOT NULL,
    user_coupon_id BIGINT,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `order`(order_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id),
    FOREIGN KEY (option_id) REFERENCES product_option(option_id)
    );

-- 쿠폰 정책 테이블 생성
CREATE TABLE IF NOT EXISTS coupon_policy (
    policy_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    discount_rate FLOAT,
    usage_period INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL
    );

-- 쿠폰 발행 테이블 생성
CREATE TABLE IF NOT EXISTS coupon_issue (
    coupon_issue_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id BIGINT NOT NULL,
    total_issued INT NOT NULL,
    remaining INT NOT NULL,
    issue_start_date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    discount_rate_snapshot FLOAT,
    usage_period_snapshot INT NOT NULL,
    type_snapshot VARCHAR(50) NOT NULL,
    FOREIGN KEY (policy_id) REFERENCES coupon_policy(policy_id)
    );

-- 유저 쿠폰 테이블 생성
CREATE TABLE IF NOT EXISTS user_coupon (
    user_coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coupon_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    policy_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    type_snapshot VARCHAR(50) NOT NULL,
    discount_rate_snapshot FLOAT,
    usage_period_snapshot INT NOT NULL,
    expired_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (policy_id) REFERENCES coupon_policy(policy_id)
    );

-- 거래 내역: 유저 기준 조회 + 시간 정렬
CREATE INDEX idx_tx_user_time ON transaction_history(user_id, transaction_time);

-- 주문: 유저 기준 조회 + 시간 정렬
CREATE INDEX idx_order_user_time ON `order`(user_id, ordered_at);

-- 주문 아이템: 조인 및 필터 효율화
CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_order_item_prod_opt ON order_item(product_id, option_id);

-- 결제: 유저 + 상태 조회
CREATE INDEX idx_payment_user_status ON payment(user_id, status);

-- 유저 쿠폰: 유저 기준 정렬
CREATE INDEX idx_user_coupon_user_exp ON user_coupon(user_id, expired_at);

-- 쿠폰 발행: 정책 기준 빠른 조회
CREATE INDEX idx_coupon_issue_policy ON coupon_issue(policy_id);

-- 인기 상품: 랭킹 + 날짜 필터
CREATE INDEX idx_popular_rank_date ON popular_product(`rank`, reference_date);



-- 더미 유저 100,000명 삽입
INSERT INTO user (user_id, balance)
WITH RECURSIVE cte_users (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_users WHERE n < 100000
)
SELECT
    n,
    FLOOR(RAND() * 100000)
FROM cte_users;

-- 더미 상품 1,000건 삽입
INSERT INTO product (product_id, product_name, status, created_at, expired_at)
WITH RECURSIVE cte_product (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_product WHERE n <= 1000
)
SELECT
    n,
    CONCAT('상품_', LPAD(n, 5, '0')),
    ELT(FLOOR(RAND()*3)+1, 'ON_SALE', 'OUT_OF_STOCK', 'EXPIRED'),
    NOW() - INTERVAL FLOOR(RAND() * 100) DAY,
    IF(FLOOR(RAND()*3) = 0, NOW() + INTERVAL FLOOR(RAND()*100) DAY, NULL)
FROM cte_product;

-- 더미 거래 내역 1,000,000건 삽입 (유저 1~100000 대상)
INSERT INTO transaction_history (user_id, type, transaction_time, amount)
WITH RECURSIVE cte_tx (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_tx WHERE n < 1000000
)
SELECT
    FLOOR(RAND() * 100000) + 1,
    ELT(FLOOR(RAND() * 2) + 1, 'CHARGE', 'USE'),
    NOW() - INTERVAL FLOOR(RAND() * 365) DAY,
    FLOOR(RAND() * 50000) + 1000
FROM cte_tx;

-- 더미 옵션 5,000건 삽입 (상품 1개당 평균 5개 옵션)
INSERT INTO product_option (
    option_id, product_id, content, status, price, stock, created_at, expired_at
)
WITH RECURSIVE cte_option (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_option WHERE n <= 5000
)
SELECT
    n,
    FLOOR(RAND() * 1000) + 1,
    CONCAT('옵션_', LPAD(n, 5, '0')),
    ELT(FLOOR(RAND()*3)+1, 'ON_SALE', 'OUT_OF_STOCK', 'EXPIRED'),
    FLOOR(RAND() * 50000) + 1000,
    FLOOR(RAND() * 50),
    NOW() - INTERVAL FLOOR(RAND() * 100) DAY,
    IF(FLOOR(RAND()*3) = 0, NOW() + INTERVAL FLOOR(RAND()*100) DAY, NULL)
FROM cte_option;

-- 더미 인기 상품 1,000건 삽입
INSERT INTO popular_product (
    id, product_id, total_sold_quantity, `rank`, reference_date, created_at
)
WITH RECURSIVE cte_popular (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_popular WHERE n <= 1000
)
SELECT
    n,
    FLOOR(RAND() * 1000) + 1,
    FLOOR(RAND() * 1000) + 1,
    n,
    CURDATE() - INTERVAL FLOOR(RAND()*30) DAY,
    NOW()
FROM cte_popular;

-- 더미 결제 10,000건 삽입
INSERT INTO payment (
    payment_id, order_id, user_id, total_amount_snapshot, total_discount_amount_snapshot, status
)
WITH RECURSIVE cte_payment (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_payment WHERE n <= 10000
)
SELECT
    n,
    n,
    FLOOR(RAND() * 100000) + 1,
    FLOOR(RAND() * 50000) + 1000,
    FLOOR(RAND() * 5000),
    ELT(FLOOR(RAND() * 2) + 1, 'BEFORE_PAYMENT', 'AFTER_PAYMENT')
FROM cte_payment;

-- 더미 주문 10,000건 삽입
INSERT INTO `order` (
    order_id, user_id, total_amount, total_discount_amount, status, ordered_at
)
WITH RECURSIVE cte_order (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_order WHERE n <= 10000
)
SELECT
    n,
    FLOOR(RAND() * 100000) + 1,
    FLOOR(RAND() * 100000) + 1000,
    FLOOR(RAND() * 5000),
    ELT(FLOOR(RAND() * 2) + 1, 'BEFORE_PAYMENT', 'AFTER_PAYMENT'),
    NOW() - INTERVAL FLOOR(RAND() * 30) DAY
FROM cte_order;

-- 더미 주문 아이템 30,000건 삽입 (주문 1건당 평균 3개 아이템)
INSERT INTO order_item (
    order_item_id, order_id, product_id, option_id,
    product_name, product_price, discount_amount, user_coupon_id, quantity
)
WITH RECURSIVE cte_order_item (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_order_item WHERE n <= 30000
)
SELECT
    n,
    FLOOR(RAND() * 10000) + 1,
    FLOOR(RAND() * 1000) + 1,
    FLOOR(RAND() * 5000) + 1,
    CONCAT('상품_', LPAD(FLOOR(RAND() * 1000) + 1, 5, '0')),
    FLOOR(RAND() * 50000) + 1000,
    FLOOR(RAND() * 5000),
    IF(FLOOR(RAND() * 4) = 0, NULL, FLOOR(RAND() * 10000) + 1),
    FLOOR(RAND() * 5) + 1
FROM cte_order_item;

-- 더미 쿠폰 정책 100건 삽입
INSERT INTO coupon_policy (policy_id, discount_rate, usage_period, type, status)
WITH RECURSIVE cte_policy (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_policy WHERE n <= 100
)
SELECT
    n,
    ROUND(RAND() * 50, 2),
    FLOOR(RAND() * 30) + 1,
    ELT(FLOOR(RAND() * 2) + 1, 'RATE', 'FIXED'),
    ELT(FLOOR(RAND() * 2) + 1, 'ENABLED', 'DISABLED')
FROM cte_policy;

-- 더미 쿠폰 발행 1,000건 삽입
INSERT INTO coupon_issue (
    coupon_issue_id, policy_id, total_issued, remaining,
    issue_start_date, status, discount_rate_snapshot,
    usage_period_snapshot, type_snapshot
)
WITH RECURSIVE cte_issue (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_issue WHERE n <= 1000
)
SELECT
    n,
    FLOOR(RAND() * 100) + 1,
    FLOOR(RAND() * 1000) + 100,
    FLOOR(RAND() * 1000),
    NOW() - INTERVAL FLOOR(RAND()*30) DAY,
    ELT(FLOOR(RAND() * 4) + 1, 'NOT_STARTED', 'ISSUABLE', 'EXHAUSTED', 'EXPIRED'),
    ROUND(RAND() * 50, 2),
    FLOOR(RAND() * 30) + 1,
    'FIXED'
FROM cte_issue;

-- 더미 유저 쿠폰 10,000건 삽입
INSERT INTO user_coupon (
    user_coupon_id, coupon_id, user_id, policy_id,
    status, type_snapshot, discount_rate_snapshot,
    usage_period_snapshot, expired_at
)
WITH RECURSIVE cte_user_coupon (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte_user_coupon WHERE n <= 10000
)
SELECT
    n,
    FLOOR(RAND() * 1000) + 1,
    FLOOR(RAND() * 100000) + 1,
    FLOOR(RAND() * 100) + 1,
    ELT(FLOOR(RAND() * 3) + 1, 'ISSUED', 'USED', 'EXPIRED'),
    ELT(FLOOR(RAND() * 2) + 1, 'RATE', 'FIXED'),
    ROUND(RAND() * 50, 2),
    FLOOR(RAND() * 30) + 1,
    NOW() + INTERVAL FLOOR(RAND()*60) DAY
FROM cte_user_coupon;
