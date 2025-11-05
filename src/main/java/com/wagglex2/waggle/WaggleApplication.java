package com.wagglex2.waggle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.retry.annotation.EnableRetry;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
// DTO 기준으로 페이징 결과를 직렬화하도록 Spring Data Web 지원 활성화
// 엔티티를 직접 노출하지 않고, DTO 리스트 + 페이징 정보만 반환
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableRetry(proxyTargetClass = true)  // 낙관적 락 충돌 시 재시도를 위함
public class WaggleApplication {
	public static void main(String[] args) {
        SpringApplication.run(WaggleApplication.class, args);
	}

}
