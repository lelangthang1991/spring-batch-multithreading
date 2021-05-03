package com.example.demo.writers;

import com.example.demo.dao.TestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class TestItemWriter implements ItemWriter<TestDTO> {

    @Override
    public void write(List<? extends TestDTO> items) {
        items.forEach(x -> log.info(x.toString()));
    }
}
