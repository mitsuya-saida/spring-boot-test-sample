package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/demo")
@Validated
public class DemoController {

    @Autowired
    private DemoService demoService;

    @Autowired
    private DemoRepository demoRepository;

    @GetMapping
    public String demo(@Valid DemoRequest demoRequest) { // BindException?
        DemoEntity demoEntity = demoRepository.findByCode(demoRequest.getCode());
        String value = demoEntity.getValue();
        String ret = demoService.greeting(value);
        return ret;
    }

}
