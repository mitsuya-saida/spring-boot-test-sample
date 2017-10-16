package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class DemoControllerTest {

    private static String GREET = "hoge";

    @Mock
    private DemoRepository demoRepository;

    @Mock
    private DemoService demoService;

    @InjectMocks
    private DemoController demoController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void 順番の担保() {
        DemoEntity demoEntity = DemoEntity.builder()
                .code("hoge")
                .value("fuga")
                .updateAt(null)
                .build();
        when(demoRepository.findByCode(anyObject())).thenReturn(demoEntity);
        when(demoService.greeting(anyObject())).thenReturn("hello");

        DemoRequest demoRequest = new DemoRequest();
        demoRequest.setCode(GREET);
        String expected = demoController.demo(demoRequest);

        verify(demoRepository, times(1)).findByCode(GREET);
        verify(demoService, times(1)).greeting("fuga");

        assertThat("hello", is(expected));
    }

}
