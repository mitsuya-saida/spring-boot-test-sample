package com.example.demo;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(
        listeners = {
                TransactionalTestExecutionListener.class,
                DbUnitTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@DbUnitConfiguration(
        dataSetLoader = ReplacementCsvDataSetLoader.class
)
public class DemoApiFunctionalTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @DatabaseSetup(value = "/DemoController/functionalTest/")
    @Test
    public void demoGetTest_正常系() {

        // 実行
        ResponseEntity actual = testRestTemplate.exchange(
                "/v1/demo?code=hoge", HttpMethod.GET, new HttpEntity<>(null, null), String.class);
        assertThat(actual.getBody().toString(), is("hello"));
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void demoGetTest_ValidationError() {
        // 実行
        ResponseEntity actual = testRestTemplate.exchange(
                "/v1/demo?code=012345", HttpMethod.GET, new HttpEntity<>(null, null), String.class);
        assertThat(actual.getBody().toString(), is("validation error"));
        assertThat(actual.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }
}
