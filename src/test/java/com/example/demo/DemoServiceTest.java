package com.example.demo;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DemoServiceTest {

    private DemoService demoService;

    @Before
    public void setUp() {
        demoService = new DemoService();
    }

    @Test
    public void greeting_あいさつがないとき() {
        assertThat(demoService.greeting(null), is("Say something..."));
    }

    @Test
    public void greeting_あいさつがあるとき() {
        assertThat(demoService.greeting("something"), is("hello"));
    }
}
