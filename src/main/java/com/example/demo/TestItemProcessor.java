package com.example.demo;

import org.springframework.batch.item.ItemProcessor;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;

public class TestItemProcessor implements ItemProcessor<String, TestDTO> {

    @Override
    public TestDTO process(String item) throws Exception {
        System.out.println("------- PROCESSANDO O ITEM : " + item);
        Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 21000));
        System.out.println("------- ITEM : " + item + "FOI PROCESSADO!");
        return new TestDTO("CPF: " + item + " PROCESSADO COM SUCESSO!");
    }
}
