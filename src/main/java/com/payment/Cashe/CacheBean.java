package com.payment.Cashe;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheBean {
    @Bean
    public Cache<Long, ObjectNode> cacheCard() {
        return CacheBuilder.newBuilder()
                .maximumSize(10000)
                .concurrencyLevel(1000)
                .expireAfterAccess(10, java.util.concurrent.TimeUnit.MINUTES)
                .build();
    }
}
