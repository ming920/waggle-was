package com.wagglex2.waggle.domain.common.querydsl;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

/**
 * MySQL FULLTEXT 검색을 위해 {@code MATCH...AGAINST} 쿼리를 Hibernate에 등록하는 클래스다.
 *
 * <p>이 클래스는 Hibernate의 FunctionRegistry에 "MATCH_AGAINST"라는 이름으로
 * 커스텀 함수를 등록한다.
 *
 * <p>등록되는 패턴은 2개의 컬럼(공고 제목, 본문)과 1개의 검색어를 지원한다:
 * <pre>
 * {@code MATCH (?1, ?2) AGAINST (?3 IN BOOLEAN MODE)}
 * </pre>
 *
 * <p>사용 예시 (QueryDSL):
 * <pre>{@code
 * // 두 컬럼과 검색어를 이용한 FULLTEXT 검색 조건 생성
 * Expressions.numberTemplate(
 *      Double.class,
 *      "FUNCTION('MATCH_AGAINST', {0}, {1}, {2})",
 *      col1, col2, keyword
 * ).gt(0);
 * }</pre>
 *
 * @see RecruitmentSearchMatcher
 */
public class MatchFunctionContributor implements FunctionContributor {

    /**
     * 등록할 커스텀 함수의 이름
     */
    private static final String FUNCTION_NAME = "MATCH_AGAINST";

    /**
     * MATCH_AGAINST 함수의 SQL 패턴 (컬럼 2개 + 검색어 1개)
     */
    private static final String FUNCTION_PATTERN = "MATCH (?1, ?2) AGAINST (?3 IN BOOLEAN MODE)";

    /**
     * MATCH_AGAINST 함수를 등록
     */
    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry()
                .registerPattern(
                        FUNCTION_NAME,
                        FUNCTION_PATTERN,
                        functionContributions.getTypeConfiguration()
                                .getBasicTypeRegistry()
                                .resolve(StandardBasicTypes.DOUBLE)
                );
    }
}
