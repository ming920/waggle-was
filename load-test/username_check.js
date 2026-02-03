import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 50,
    duration: '3m',
};

function randomUsername() {
    return `user_${Math.random().toString(36).substring(2, 12)}`;
}

export default function () {
    const payload = JSON.stringify({
        username: randomUsername(),
    });

    const res = http.post(
        'http://host.docker.internal:8080/api/v1/users/username/check',
        payload,
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(0.2);
}