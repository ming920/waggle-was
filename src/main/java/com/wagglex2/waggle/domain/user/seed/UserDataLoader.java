package com.wagglex2.waggle.domain.user.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class UserDataLoader implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final int TOTAL_SIZE = 100_000;
    private static final int CHUNK_SIZE = 10_000;

    @Override
    public void run(String... args) {

        Integer seedCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username LIKE 'seedUser%'",
                Integer.class
        );

        if (seedCount != null && seedCount > 0) {
            System.out.println("이미 seed 유저가 존재합니다.");
            return;
        }

        String encodedPassword = passwordEncoder.encode("Test1234!");

        String sql = """
            INSERT INTO users (username, password, nickname, email, university, role, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
        """;

        System.out.println("=== 더미 유저 데이터 삽입 시작 ===");

        for (int start = 0; start < TOTAL_SIZE; start += CHUNK_SIZE) {

            final int offset = start;

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {

                    int id = offset + i + 1;

                    ps.setString(1, "seedUser" + id);
                    ps.setString(2, encodedPassword);
                    ps.setString(3, "seedUser" + id);
                    ps.setString(4, "seedUser" + id + "@test.com");
                    ps.setString(5, "YEUNGNAM_UNIV");
                    ps.setString(6, "ROLE_USER");
                    ps.setString(7, "ACTIVE");
                    // created_at, updated_at은 NOW()로 자동 처리
                }

                @Override
                public int getBatchSize() {
                    return Math.min(CHUNK_SIZE, TOTAL_SIZE - offset);
                }
            });

            System.out.printf("진행률: %d / %d%n", Math.min(start + CHUNK_SIZE, TOTAL_SIZE), TOTAL_SIZE);
        }

        System.out.println("=== 더미 유저 데이터 삽입 완료 ===");
    }
}

