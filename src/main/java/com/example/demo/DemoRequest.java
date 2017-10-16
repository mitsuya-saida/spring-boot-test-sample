package com.example.demo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class DemoRequest {

    @NotNull
    @Size(max = 5)
    private String code;
}
