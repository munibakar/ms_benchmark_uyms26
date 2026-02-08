package com.microservice.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Dedicated Thread Pool for REST Parallel Calls
 * Prevents starvation in the common ForkJoinPool
 */
@Configuration
public class AsyncExecutorConfig {

    @Bean(name = "restExecutor")
    public Executor restExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 50 users * 4 parallel calls = 200 tasks.
        // 100 threads should be a good balance for I/O waits.
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("RestParallel-");
        executor.initialize();
        return executor;
    }
}
