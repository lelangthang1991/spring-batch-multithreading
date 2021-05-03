package com.example.demo.config.example;

import com.example.demo.config.BatchJobConfiguration;
import com.example.demo.dao.TestDTO;
import com.example.demo.processors.TestItemProcessor;
import com.example.demo.readers.TestItemReader;
import com.example.demo.writers.TestItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.Future;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends BatchJobConfiguration {

    @Autowired
    private JobLauncher jobLauncher;

    @Bean
    public ItemReader<String> reader() {
        return new TestItemReader();
    }

    @Bean
    public ItemProcessor<String, TestDTO> processor() {
        return new TestItemProcessor();
    }

    @Bean
    public ItemWriter<TestDTO> writer() {
        return new TestItemWriter();
    }

    @Bean
    public ItemProcessor<String, Future<TestDTO>> asyncItemProcessor(
            @Qualifier("processor") ItemProcessor<String, TestDTO> processor,
            @Qualifier("asyncExecutor") TaskExecutor getAsyncExecutor) {
        AsyncItemProcessor<String, TestDTO> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(processor);
        asyncItemProcessor.setTaskExecutor(getAsyncExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public ItemWriter<Future<TestDTO>> asyncItemWriter(@Qualifier("writer") ItemWriter<TestDTO> writer) {
        AsyncItemWriter<TestDTO> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(writer);
        return asyncItemWriter;
    }

    @Bean
    protected Step step1(@Qualifier("asyncItemProcessor") ItemProcessor<String, Future<TestDTO>> asyncItemProcessor,
                         @Qualifier("asyncItemWriter") ItemWriter<Future<TestDTO>> asyncItemWriter) {
        return this.steps.get("step1")
                .<String, Future<TestDTO>>chunk(Integer.parseInt(core))
                .reader(reader())
                .processor(asyncItemProcessor)
                .writer(asyncItemWriter)
                .build();
    }

    @Bean
    protected Job job1(@Qualifier("step1") Step step1) {
        return this.jobs.get("job1")
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }

    //    @Scheduled(cron = "0 * * * * *", zone =  "America/Sao_Paulo")
    public void schedule(@Qualifier("job1") Job job1) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        jobLauncher.run(job1, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
    }
}
