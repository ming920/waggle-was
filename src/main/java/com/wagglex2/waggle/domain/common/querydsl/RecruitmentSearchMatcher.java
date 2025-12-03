package com.wagglex2.waggle.domain.common.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;

import java.util.Set;

/**
 * 공고 검색용 FULLTEXT MATCH 헬퍼 클래스다.
 *
 * <p>MySQL의 MATCH...AGAINST 쿼리를 사용하여 제목(title)과 내용(content)에서
 * 키워드를 검색하는 QueryDSL 조건을 생성한다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * BooleanExpression condition = RecruitmentSearchMatcher.match(
 *      Set.of("스프링", "부트"),
 *      project.title,
 *      project.content
 * );
 * queryFactory.selectFrom(project)
 *             .where(condition)
 *             .fetch();
 * }</pre>
 *
 * @see MatchFunctionContributor
 */
public class RecruitmentSearchMatcher {

    /**
     * 제목(title)과 내용(content)에서 주어진 키워드가 포함된 행을 찾는
     * BooleanExpression을 생성한다.
     *
     * <p>키워드가 null이면 null을 반환하고,
     * 의미 있는 토큰이 없으면 항상 false 조건을 반환한다.
     *
     * @param keywords 검색할 키워드
     * @param title 검색 대상 제목 컬럼
     * @param content 검색 대상 내용 컬럼
     * @return 키워드가 포함된 행을 찾는 BooleanExpression, 키워드가 없으면 null을 반환
     */
    public static BooleanExpression match(Set<String> keywords, StringPath title, StringPath content) {
        // 검색어 입력이 없으므로 모든 데이터 조회
        if (keywords == null) {
            return null;
        }

        // 검색어가 공백이거나 의미 있는 토큰이 없으므로 조회 결과 없음
        if (keywords.isEmpty()) {
            return Expressions.booleanTemplate("1 = 0");
        }

        /*
          FULLTEXT 조건 생성

          - 예시: "사과* 바나나*"
          - 의미: "사과" 또는 "바나나"가 포함되는 열을 찾되,
                  조사나 접미사 등이 붙은 경우도 허용 ("사과는", "바나나가")
        */
        StringBuilder sb = new StringBuilder();
        keywords.forEach(keyword -> sb.append(keyword).append("* "));

        String fullTextCondition = sb.toString().trim();

        // Boolean Mode FULLTEXT 검색
        // 제목(title) 또는 내용(content)에 키워드 중 하나라도 포함되는 행을 찾는다.
        return Expressions.numberTemplate(
                Double.class,
                "FUNCTION('MATCH_AGAINST', {0}, {1}, {2})",
                title, content, fullTextCondition
        ).gt(0);
    }
}
