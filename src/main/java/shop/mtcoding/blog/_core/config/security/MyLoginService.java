package shop.mtcoding.blog._core.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRepository;

// POST, /login, x-www-form-urlencoded, 키값이 username, password
@RequiredArgsConstructor
@Service // 컴퍼넌트 스캔, 덮어씌움, 기존에 있는게 무력화돼서 아래 메서드가 실행됨
public class MyLoginService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername : " + username);

        User user = userRepository.findByUsername(username);

        if (user == null) {
            System.out.println("user는 null");
            return null;
        } else {
            System.out.println("user를 찾았어요");
            return new MyLoginUser(user);
        }
    }
}
