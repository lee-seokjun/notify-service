package com.example.notifyservice.config;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.TransactionConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.util.concurrent.IsolationLevel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.TimeUnit;


@Configuration
public class CacheConfig {
    private Environment env;

    public CacheConfig(Environment environment) {
        this.env = environment;
    }
    @Bean("cacheManager")
    public EmbeddedCacheManager cacheManager(){
        String SPRING_PROFILES_ACTIVE = env.getProperty("SPRING_PROFILES_ACTIVE","local");
        GlobalConfigurationBuilder global =new GlobalConfigurationBuilder()
                .transport().defaultTransport()
                .clusterName("foobar-api-" + SPRING_PROFILES_ACTIVE )
                .defaultCacheName("default-cache");
        return new DefaultCacheManager(new ConfigurationBuilderHolder(Thread.currentThread().getContextClassLoader(), global), true);

    }

    @Bean("sseEmitterCache")
    public Cache<String, SseEmitter> sseEmitterCache(@Qualifier("cacheManager") EmbeddedCacheManager cacheManager ){
        TransactionConfigurationBuilder config = new ConfigurationBuilder()
                .expiration().lifespan(10, TimeUnit.MINUTES)
                .clustering().cacheMode(CacheMode.LOCAL)
                .locking().isolationLevel(IsolationLevel.READ_COMMITTED)
                .useLockStriping(false)
                .lockAcquisitionTimeout(10,TimeUnit.SECONDS)
                .transaction()
                .lockingMode(LockingMode.OPTIMISTIC)
                .transactionMode(TransactionMode.NON_TRANSACTIONAL);
        cacheManager.defineConfiguration("sse-emitter-cache",config.build());
        return cacheManager.getCache("sse-emitter-cache");
    }
    @Bean("sseEventCache")
    public Cache<String, String> sseEventCache(@Qualifier("cacheManager") EmbeddedCacheManager cacheManager){
        TransactionConfigurationBuilder config = new ConfigurationBuilder()
                .expiration().lifespan(1,TimeUnit.MINUTES)
                .clustering().cacheMode(CacheMode.REPL_ASYNC)
                .locking()
                .isolationLevel(IsolationLevel.READ_COMMITTED)
                .useLockStriping(false)
                .lockAcquisitionTimeout(10,TimeUnit.SECONDS)
                .transaction()
                .lockingMode(LockingMode.OPTIMISTIC)
                .transactionMode(TransactionMode.NON_TRANSACTIONAL);
        cacheManager.defineConfiguration("sse-event-cache",config.build());
        return cacheManager.getCache("sse-event-cache");
    }
}
