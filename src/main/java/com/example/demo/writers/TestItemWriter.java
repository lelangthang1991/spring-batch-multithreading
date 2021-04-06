package com.example.demo.writers;

import com.example.demo.dao.TestDTO;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class TestItemWriter implements ItemWriter<TestDTO> {

    @Override
    public void write(List<? extends TestDTO> items) throws Exception {
        items.forEach(System.out::println);
    }
}
