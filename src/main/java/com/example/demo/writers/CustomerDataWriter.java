package com.example.demo.writers;

import com.example.demo.model.CustomerInfo;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class CustomerDataWriter implements ItemWriter<CustomerInfo> {
    @Override
    public void write(List<? extends CustomerInfo> list) throws Exception {

    }
}
