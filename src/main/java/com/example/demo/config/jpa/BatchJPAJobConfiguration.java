package com.example.demo.config.jpa;

import com.example.demo.config.BatchJobConfiguration;
import com.example.demo.dao.CustomerInfoDAO;
import com.example.demo.model.CustomerInfo;
import com.example.demo.processors.CustomerInfoProcessor;
import com.example.demo.writers.CustomerDataWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.Future;

@Configuration
@Profile("importDBFromCSV")
@EnableBatchProcessing
public class BatchJPAJobConfiguration extends BatchJobConfiguration {

    @Bean
    protected Job importDBFromCSV(@Qualifier("importData") Step importData) {
        return this.jobs.get("importDBFromCSV")
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(importData)
                .build();
    }

    @Bean
    protected Step importData(
            @Qualifier("csvReader") FlatFileItemReader<CustomerInfoDAO> csvReader,
            @Qualifier("processCustomerDataAsync") ItemProcessor<CustomerInfoDAO, Future<CustomerInfo>> processCustomerDataAsync,
            @Qualifier("writeCustomerDataAsync") ItemWriter<Future<CustomerInfo>> writeCustomerDataAsync) {
        return this.steps.get("importData")
                .<CustomerInfoDAO, Future<CustomerInfo>>chunk(Integer.parseInt(chunk))
                .reader(csvReader)
                .processor(processCustomerDataAsync)
                .writer(writeCustomerDataAsync)
                .build();

    }

    @Bean
    public ItemWriter<Future<CustomerInfo>> writeCustomerDataAsync(
            @Qualifier("writeCustomerData") ItemWriter<CustomerInfo> writeCustomerData) {
        AsyncItemWriter<CustomerInfo> wr = new AsyncItemWriter<>();
        wr.setDelegate(writeCustomerData);
        return wr;
    }

    @Bean
    public ItemWriter<CustomerInfo> writeCustomerData() {
        return new CustomerDataWriter();
    }

    @Bean
    public ItemProcessor<CustomerInfoDAO, Future<CustomerInfo>> processCustomerDataAsync(
            @Qualifier("processCustomerData") ItemProcessor<CustomerInfoDAO, CustomerInfo> processCustomerData,
            @Qualifier("asyncExecutor") TaskExecutor getAsyncExecutor) {
        AsyncItemProcessor<CustomerInfoDAO, CustomerInfo> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(processCustomerData);
        asyncItemProcessor.setTaskExecutor(getAsyncExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public ItemProcessor<CustomerInfoDAO, CustomerInfo> processCustomerData() {
        return new CustomerInfoProcessor();
    }

    @Bean
    public FlatFileItemReader<CustomerInfoDAO> csvReader() {

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(
                "seriesReference",
                "period",
                "dataValue",
                "serialStatus",
                "units",
                "subject",
                "serialGroup",
                "seriesTitle1",
                "seriesTitle2",
                "seriesTitle3",
                "seriesTitle4",
                "seriesTitle5"
        );
        lineTokenizer.setDelimiter(",");

        BeanWrapperFieldSetMapper<CustomerInfoDAO> fm = new BeanWrapperFieldSetMapper<>();
        fm.setTargetType(CustomerInfoDAO.class);
        DefaultLineMapper<CustomerInfoDAO> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fm);

        FlatFileItemReader<CustomerInfoDAO> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(inputFile));
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper);

        return reader;
    }
}
