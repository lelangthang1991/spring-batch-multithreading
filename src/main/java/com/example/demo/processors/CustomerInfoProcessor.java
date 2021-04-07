package com.example.demo.processors;

import com.example.demo.dao.CustomerInfoDAO;
import com.example.demo.model.CustomerInfo;
import com.example.demo.repository.CustomerInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CustomerInfoProcessor implements ItemProcessor<CustomerInfoDAO, CustomerInfo> {

    @Autowired
    private CustomerInfoRepository customerInfoRepository;

    @Override
    public CustomerInfo process(CustomerInfoDAO s) throws Exception {
        CustomerInfo customerInfo = new CustomerInfo();
        BeanUtils.copyProperties(s, customerInfo);
        log.info(Thread.currentThread().getName() + " ---> " + s.getDataValue());
        return customerInfoRepository.save(customerInfo);
    }
}
