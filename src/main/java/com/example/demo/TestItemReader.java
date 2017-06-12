package com.example.demo;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public class TestItemReader implements ItemReader<String>, StepExecutionListener {

    private List<String> cpfs;

    /**
     * Próximo CPF a ser retornado.
     */
    private int next;

    /**
     * Inicializando a lista
     */
    private void init(){
        cpfs = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        next = 0;
    }


    @Override
    public void beforeStep(StepExecution stepExecution) {
        init();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public String read() {

        if(CollectionUtils.isEmpty(cpfs) || next >= cpfs.size()){
            return null;
        }

        return cpfs.get(next++);
    }
}
