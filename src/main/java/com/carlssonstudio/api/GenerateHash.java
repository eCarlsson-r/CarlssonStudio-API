package com.carlssonstudio.api;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder(12);
        String hash = encoder.encode("Admin@123");
        System.out.println(hash);
    }
}