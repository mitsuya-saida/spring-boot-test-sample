package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(DemoClient.class)
public class DemoClientTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private DemoClient demoClient;

    @Test
    public void testGet() {
        mockServer.expect(requestTo("http://test.api.server/v1/demo/api"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"code\":\"123\",\"name\":\"name\"}", MediaType.APPLICATION_JSON));
        DemoApiResponse expected = new DemoApiResponse("123", "name");

        DemoApiResponse actual = demoClient.get();

        assertThat(actual, samePropertyValuesAs(expected));
    }

}
