package com.example.jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JdbcexampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdbcexampleApplication.class, args);
        System.out.println("Hello JDBC Example");
    }

}
