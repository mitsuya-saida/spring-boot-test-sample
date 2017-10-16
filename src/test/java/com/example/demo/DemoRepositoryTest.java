package com.example.demo;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DbUnitConfiguration(
        dataSetLoader = ReplacementCsvDataSetLoader.class
)
@Transactional
public class DemoRepositoryTest {

    private static final String DATA_FILE_PATH = "/DemoRepository/";

    @Autowired
    private DemoRepository demoRepository;

    @DatabaseSetup(value = DATA_FILE_PATH + "findByCode/")
    @Test
    public void findByCodeTest() {
        DemoEntity expected = DemoEntity.builder()
                .code("hoge")
                .value("fuga")
                .updateAt(ZonedDateTime.parse("2017-01-01T00:00:00+09:00:00[Asia/Tokyo]"))
                .build();
        DemoEntity actual = demoRepository.findByCode("hoge");
        assertThat(actual, samePropertyValuesAs(expected));
    }
}
