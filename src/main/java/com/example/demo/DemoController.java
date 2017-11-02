package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final DemoService demoService;

    private final DemoRepository demoRepository;

    private final DemoClient demoClient;

    @GetMapping
    public String demo(@Valid DemoRequest demoRequest) {
        DemoEntity demoEntity = demoRepository.findByCode(demoRequest.getCode());
        String value = demoEntity.getValue();
        String ret = demoService.greeting(value);
        return ret;
    }

    @GetMapping("/client")
    public DemoApiResponse clientDemo() {
        return demoClient.get();
    }

    @GetMapping("/api")
    public DemoApiResponse api() {
        return new DemoApiResponse("0123","namae");
    }

}
