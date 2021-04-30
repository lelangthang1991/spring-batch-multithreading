package com.example.demo.processors;

import com.example.demo.dao.TestDTO;
import org.springframework.batch.item.ItemProcessor;

import java.util.concurrent.ThreadLocalRandom;

public class TestItemProcessor implements ItemProcessor<String, TestDTO> {

    @Override
    public TestDTO process(String item) throws Exception {
        System.out.println("------- PROCESSING O ITEM : " + item);
        Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 21000));
        System.out.println("------- ITEM : " + item + "HAS BEEN PROCESSED!");
        return new TestDTO("CPF: " + item + " PROCESSED SUCCESSFULLY!");
    }
}
