package com.example.demo.config;

import com.example.demo.dao.CustomerInfoDAO;
import com.example.demo.model.CustomerInfo;
import com.example.demo.processors.CustomerInfoProcessor;
import com.example.demo.writers.CustomerDataWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableBatchProcessing
public class BatchJPAJobConfiguration {

    @Value("${reader.input.file}")
    String inputFile;

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    protected Job importDBFromCSV() {
        return this.jobs.get("importDBFromCSV")
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(importData())
                .build();
    }

    @Bean
    protected Step importData() {
        return this.steps.get("importData")
                .<CustomerInfoDAO, Future<CustomerInfo>>chunk(50)
                .reader(csvReader())
                .processor(processCustomerDataAsync())
                .writer(writeCustomerDataAsync())
                .build();

    }

    @Bean
    public ItemWriter<Future<CustomerInfo>> writeCustomerDataAsync() {
        AsyncItemWriter<CustomerInfo> wr = new AsyncItemWriter<>();
        wr.setDelegate(writeCustomerData());
        return wr;
    }

    @Bean
    public ItemWriter<CustomerInfo> writeCustomerData() {
        return new CustomerDataWriter();
    }

    @Bean
    public ItemProcessor<CustomerInfoDAO, Future<CustomerInfo>> processCustomerDataAsync() {
        AsyncItemProcessor<CustomerInfoDAO, CustomerInfo> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(processCustomerData());
        asyncItemProcessor.setTaskExecutor(getAsyncExecutorCustomerInfo());
        return asyncItemProcessor;
    }

    @Bean
    public ItemProcessor<CustomerInfoDAO, CustomerInfo> processCustomerData() {
        return new CustomerInfoProcessor();
    }

    @Bean(name = "asyncExecutorCustomerInfo")
    public TaskExecutor getAsyncExecutorCustomerInfo() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("AsyncExecutor-");
        return executor;
    }

    @Bean
    public FlatFileItemReader<CustomerInfoDAO> csvReader() {

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[]{"seriesReference", "period", "dataValue", "serialStatus", "units", "subject", "serialGroup", "seriesTitle1", "seriesTitle2", "seriesTitle3", "seriesTitle4", "seriesTitle5"});
        lineTokenizer.setDelimiter(",");

        BeanWrapperFieldSetMapper<CustomerInfoDAO> fm = new BeanWrapperFieldSetMapper<>();
        fm.setTargetType(CustomerInfoDAO.class);
        DefaultLineMapper<CustomerInfoDAO> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fm);
//        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<CustomerInfoDAO>() {
//            @Override
//            public void setTargetType(Class<? extends CustomerInfoDAO> type) {
//                super.setTargetType(type);
//            }
//        });

        FlatFileItemReader<CustomerInfoDAO> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(inputFile));
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper);

        return reader;
    }
}
