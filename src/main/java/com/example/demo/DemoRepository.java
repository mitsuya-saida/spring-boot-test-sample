package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface DemoRepository extends JpaRepository<DemoEntity, String>{
    public DemoEntity findByCode(String code);
}
