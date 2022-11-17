package com.ay.exchange.exchange.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExchangeQueryRepository {
    private final JPAQueryFactory queryFactory;


}
