package com.example.demo.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String password = "123456";

        String hashedPassword = encoder.encode(password);

        System.out.println("Mat khau goc: " + password);
        System.out.println("BCrypt: " + hashedPassword);

        System.out.println(
                "Kiem tra: " +
                encoder.matches(password, hashedPassword)
        );
    }
}