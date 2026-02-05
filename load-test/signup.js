import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 20,
    duration: '5m',
};

// 규칙에 맞는 비밀번호 (영문+숫자+특수문자 포함)
const TEST_PASSWORD = "Test1234!";

function randomUserId() {
    const id = Math.floor(Math.random() * 10000000);
    return `testus${id}`;
}

function randomNickname() {
    const id = Math.floor(Math.random() * 10000000);
    return `ni${id}`;
}

export default function () {
    const username = randomUserId();
    const nickname = randomNickname();

    const payload = JSON.stringify({
        username: username,
        nickname: nickname,
        email: `${username}@yu.ac.kr`,
        password: TEST_PASSWORD,
        passwordConfirm: TEST_PASSWORD
    });

    const res = http.post(
        'http://host.docker.internal:8080/api/v1/auth/sign-up',
        payload,
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(res, {
        'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
        'not conflict': (r) => r.status !== 409,
    });

    sleep(0.2);
}
