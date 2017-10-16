package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Table(name = "demo")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DemoEntity {

    @Id
    private String code;
    private String value;
    private ZonedDateTime updateAt;
}
