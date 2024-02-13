package shop.mtcoding.blog.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncodeTest {
    @Test
    public void encode_test() {
        BCryptPasswordEncoder en = new BCryptPasswordEncoder();
        String rawPassword = "1234";

        String encPassword = en.encode(rawPassword);
        System.out.println(encPassword);
        // $2a$10$FZHY3e154SGfQyfQC0BVMeE8JnaOL5h6mfBmLO2PttIHRwBGeisFe
        // $2a$10$Ot2VCWoaht35JqBZK7WIPeCDQBxjf086EN7edqGT87Y1XsflW6QM6
    }
}
