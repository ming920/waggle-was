package com.wagglex2.waggle.domain.common.util;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
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
        String lowerCase = target.toLowerCase();  // Komoran 추출 편의를 위해 소문자(영어)로 통일
        Set<String> nouns = new HashSet<>(
                komoran.analyze(lowerCase).getNouns()
        );

        // 정확도 향상을 위해 실제 입력 데이터 토큰 추가
        nouns.addAll(
                Arrays.stream(lowerCase.split("\\s+")).toList()
        );

        // 한 글자 단어 불용어 처리
        return nouns.stream()
                .filter(n -> n.length() > 1 || whitelist.contains(n))
                .collect(Collectors.toUnmodifiableSet());
    }
}
