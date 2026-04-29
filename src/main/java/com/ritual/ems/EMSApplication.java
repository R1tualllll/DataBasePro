package com.ritual.ems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(EMSApplication.class, args);
        System.out.println("企业员工管理系统成功启动");
    }
}
