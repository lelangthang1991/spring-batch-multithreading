package com.example.demo;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class TestItemWriter implements ItemWriter<TestDTO> {

    @Override
    public void write(List<? extends TestDTO> items) throws Exception {
        items.forEach(System.out::println);
    }
}
