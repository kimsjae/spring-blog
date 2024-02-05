package shop.mtcoding.blog.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_tb") // 테이블명을 user로 만들면 키워드여서 안만들어질 수 있다. _tb 컨벤션
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 전략
    private int id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt; // 카멜표기법으로 만들면 DB에서 created_at으로 만들어진다 (언더스코어기법)
}
