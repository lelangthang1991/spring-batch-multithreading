package com.example.demo.processors;

import com.example.demo.dao.CustomerInfoDAO;
import com.example.demo.model.CustomerInfo;
import com.example.demo.repository.CustomerInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.swagger.models.Model;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import springfox.documentation.swagger2.mappers.ModelMapper;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class CustomerInfoProcessor implements ItemProcessor<CustomerInfoDAO, CustomerInfo> {

    @Autowired
    private CustomerInfoRepository customerInfoRepository;

    @Override
    public CustomerInfo process(CustomerInfoDAO s) throws Exception {
        CustomerInfo customerInfo = new Gson().fromJson(new Gson().toJson(s), CustomerInfo.class);
        log.info(Thread.currentThread().getName()+" ---> "+ s.toString());
        return customerInfoRepository.saveAndFlush(customerInfo);
    }
}
