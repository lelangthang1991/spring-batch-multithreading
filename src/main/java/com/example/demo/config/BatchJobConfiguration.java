package com.example.demo.config;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public abstract class BatchJobConfiguration {
    @Value("${reader.input.file}")
    protected String inputFile;
    @Value("${async.thread.max.pool}")
    protected String maxThread;
    @Value("${async.thread.core.pool}")
    protected String core;
    @Value("${async.thread.queue}")
    protected String queue;
    @Value("${job.chunk.size}")
    protected String chunk;
    @Autowired
    protected JobBuilderFactory jobs;

    @Autowired
    protected StepBuilderFactory steps;

    @Bean(name = "asyncExecutor")
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Integer.parseInt(core));
        executor.setMaxPoolSize(Integer.parseInt(core));
        executor.setQueueCapacity(Integer.parseInt(queue));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("AsyncExecutor-");
        return executor;
    }
}
