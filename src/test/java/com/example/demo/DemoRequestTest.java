package com.example.demo;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Size;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class DemoRequestTest {

    private Validator validator;

    @Before
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void validatorTest_桁あふれ() {
        DemoRequest demoRequest = new DemoRequest();
        demoRequest.setCode("123456");
        Set<ConstraintViolation<DemoRequest>> violationSet = validator.validate(demoRequest);
        assertThat(violationSet.size(), is(1));
        violationSet.forEach(violation -> {
            assertThat(violation.getConstraintDescriptor().getAnnotation(), instanceOf(Size.class));
        });
    }
}
