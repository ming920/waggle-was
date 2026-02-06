import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '1m', target: 30 },
        { duration: '2m', target: 30 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<1000'], // 95%가 1000ms 이하
        http_req_failed: ['rate<0.01'],   // 에러율 1% 미만
    },
};

// 규칙에 맞는 비밀번호 (영문+숫자+특수문자 포함)
const TEST_PASSWORD = "Test1234!";

function randomUserId() {
    const id = Math.floor(Math.random() * 100000) + 1;
    return `seedUser${id}`;
}

export default function () {
    const username = randomUserId();

    const payload = JSON.stringify({
        username: username,
        password: TEST_PASSWORD,
    });

    const res = http.post(
        'http://host.docker.internal:8080/api/v1/auth/sign-in',
        payload,
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(res, {
        'status is 200': (r) => {
            if (r.status !== 200) {
                console.log(`Failed: ${username}, Status: ${r.status}, Body: ${r.body}`);
            }
            return r.status === 200;
        }
    });

    sleep(0.2);
}
