package com.wagglex2.waggle.domain.common.util;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KomoranUtil {

    private static final String USER_DIC_PATH = "komoran/dic.user";
    private static final Komoran komoran = createKomoran();
    private static final List<String> whitelist = List.of("c", "웹", "깃", "팀", "앱", "툴");

    private static Komoran createKomoran() {
        Komoran k = new Komoran(DEFAULT_MODEL.LIGHT);
        
        String tempFilePath = extractResourceToTempFile(USER_DIC_PATH);
        if (tempFilePath != null) {
            k.setUserDic(tempFilePath);
        }

        return k;
    }


    private static String extractResourceToTempFile(String resourcePath) {
        InputStream resourceStream = KomoranUtil.class.getClassLoader()
                .getResourceAsStream(resourcePath);

        if (resourceStream == null) {
            return null;
        }

        try {
            File tempFile = File.createTempFile("komoran_dic_", ".user");
            tempFile.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(tempFile);
                 InputStream is = resourceStream) {
                is.transferTo(fos);
            }

            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    public Set<String> getNouns(String target) {
        log.info("[Search] 입력 검색어: '{}'", target);

        // 전처리 - 특수문자 및 초성 제거, 소문자로 통일, 앞뒤 공백 제거
        String preProcessed = target.replaceAll("[^a-zA-Z0-9가-힣\\s]", " ")
                                    .replaceAll("\\s+", " ")
                                    .toLowerCase()
                                    .trim();

        log.info("[Search] 검색어 전처리 결과: '{}'", preProcessed);

        // Komoran 명사 추출
        Set<String> nouns = new HashSet<>(
                komoran.analyze(preProcessed).getNouns()
        );

        log.info("[Search] Komoran 명사 추출 결과 (중복 제거): {}", nouns);

        // 정확도 향상을 위해 원본 검색어 토큰 추가
        List<String> rawTokens = Arrays.stream(preProcessed.split(" ")).toList();
        log.info("[Search] 원본 검색어 토큰 split 결과: {}", rawTokens);

        nouns.addAll(rawTokens);
        log.info("[Search] Komoran + 원본 토큰 병합 결과: {}", nouns);

        // 한 글자 단어 불용어 처리
        Set<String> res = nouns.stream()
                .filter(
                        n -> n.length() > 1
                                || n.matches("[0-9]")
                                || whitelist.contains(n)
                )
                .collect(Collectors.toUnmodifiableSet());

        log.info("[Search] 최종 결과 (불용어 제거 후): {}", res);

        return res;
    }
}
