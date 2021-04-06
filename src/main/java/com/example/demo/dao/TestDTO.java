package com.example.demo.dao;

/**
 * Created by jeias on 6/6/17.
 */
public class TestDTO {

    private String value;

    public TestDTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
