package com.yowyob.common.config;

import com.yowyob.common.constant.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Configuration de l'exécution asynchrone
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Configure les thread pools pour tâches asynchrones
 * Permet l'exécution parallèle optimisée
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(AppConstants.DEFAULT_THREAD_POOL_SIZE);
        executor.setMaxPoolSize(AppConstants.MAX_THREAD_POOL_SIZE);
        executor.setQueueCapacity(AppConstants.THREAD_POOL_QUEUE_CAPACITY);
        executor.setThreadNamePrefix("async-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        log.info("ThreadPoolTaskExecutor initialisé: core={}, max={}, queue={}",
                AppConstants.DEFAULT_THREAD_POOL_SIZE,
                AppConstants.MAX_THREAD_POOL_SIZE,
                AppConstants.THREAD_POOL_QUEUE_CAPACITY);

        return executor;
    }

    @Bean(name = "eventExecutor")
    public Executor eventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("event-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        log.info("Event ThreadPoolTaskExecutor initialisé");

        return executor;
    }
}