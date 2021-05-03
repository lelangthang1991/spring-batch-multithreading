package com.example.demo.processors;

import com.example.demo.dao.TestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class TestItemProcessor implements ItemProcessor<String, TestDTO> {

    @Override
    public TestDTO process(String item) throws Exception {
        log.info("------- PROCESSING O ITEM : " + item);
        Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 21000));
        log.info("------- ITEM : " + item + "HAS BEEN PROCESSED!");
        return new TestDTO("CPF: " + item + " PROCESSED SUCCESSFULLY!");
    }
}
