package com.wagglex2.waggle.domain.common.util;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KomoranUtil {

    private static final String USER_DIC_PATH = "src/main/resources/komoran/dic.user";
    private static final Komoran komoran = createKomoran();
    private static final List<String> whitelist = List.of("c", "웹", "깃", "팀", "앱", "툴");

    private static Komoran createKomoran() {
        Komoran k = new Komoran(DEFAULT_MODEL.LIGHT);
        k.setUserDic(USER_DIC_PATH);

        return k;
    }

    public Set<String> getNouns(String target) {
        log.info("[Search] 입력 검색어: '{}'", target);

        String lowerCase = target.toLowerCase();  // Komoran 추출 편의를 위해 소문자(영어)로 통일
        log.debug("[Search] lowerCase 변환: '{}'", lowerCase);

        Set<String> nouns = new HashSet<>(
                komoran.analyze(lowerCase).getNouns()
        );

        log.info("[Search] Komoran 명사 추출 결과 (중복 제거): {}", nouns);

        // 정확도 향상을 위해 실제 입력 데이터 토큰 추가
        List<String> rawTokens = Arrays.stream(lowerCase.split("\\s+")).toList();
        log.info("[Search] 원본 검색어 토큰 split 결과: {}", rawTokens);

        nouns.addAll(rawTokens);
        log.info("[Search] Komoran + 원본 토큰 병합 결과: {}", nouns);

        // 한 글자 단어 불용어 처리
        Set<String> res = nouns.stream()
                .filter(n -> n.length() > 1 || whitelist.contains(n))
                .collect(Collectors.toUnmodifiableSet());

        log.info("[Search] 최종 결과 (불용어 제거 후): {}", res);

        return res;
    }
}
