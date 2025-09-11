import http from 'k6/http';
import { check, sleep } from 'k6';

// 동작 확인
/*export const options = {
    vus: 50,
    duration: '30s',
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.01'],
    },
};*/

// 점진적 증가 (Ramp-up Stress Test)
export const options = {
    stages: [
        { duration: '1m', target: 500 },
        { duration: '1m', target: 1000 },
        { duration: '1m', target: 2000 },
        { duration: '2m', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'],
        http_req_failed: ['rate<0.05'],
    },
};

// Spike Test
/*export const options = {
    stages: [
        { duration: '10s', target: 1000 }, // 갑자기 1000명 동접
        { duration: '30s', target: 1000 }, // 30초 유지
        { duration: '20s', target: 0 },    // 급격히 감소
    ],
};*/

// 내구성
/*export const options = {
    vus: 200,
    duration: '30m',   // 30분 동안 계속 부하
};*/

export default function () {
    const url = 'http://host.docker.internal:8080/coupon/issue';

    const payload = JSON.stringify({
        userId: Math.floor(Math.random() * 100000) + 1, // 랜덤 유저 ID
        couponId: 104,
        policyId: 21,
        typeSnapshot: "FIXED",
        discountRateSnapshot: 10.0,
        discountAmountSnapshot: 1000,
        minimumOrderAmountSnapshot: 5000,
        usagePeriodSnapshot: 30,
        expiredAt: "2025-12-31T23:59:59"
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status is 202': (r) => r.status === 202,
    });

    sleep(1); // 유저 think time
}