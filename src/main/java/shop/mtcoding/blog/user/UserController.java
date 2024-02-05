package shop.mtcoding.blog.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor // final이 붙은 애들에 대한 생성자를 만들어줌
public class UserController {
    // 자바는 final 변수는 반드시 초기화가 되어야 함.
    private final UserRepository userRepository;
    private final HttpSession session;

    @PostMapping("/join")
    public String join(UserRequest.JoinDTO requestDTO) {
        System.out.println(requestDTO);
        userRepository.save(requestDTO); // 모델에 위임하기
        return "redirect:/loginForm";
    }

    @PostMapping("/login")
    /**
     * 왜 조회인데 Post ?
     * 민감한 정보는 body로 보내기 때문에 로그인만 예외로 select인데 Post 사용
     */
    public String login(UserRequest.LoginDTO requestDTO, HttpServletRequest request) {
        System.out.println(requestDTO);

        if (requestDTO.getUsername().length() < 3) {
            return "error/400"; // ViewResolver 설정이 되어 있음.
        }

        User user = userRepository.findByUsernameAndPassword(requestDTO);

        if (user == null) { // 조회 실패 (401)
            return "error/401";
        } else { // 조회 성공 (인증됨)
            session.setAttribute("sessionUser", user); // 락카에 담음 (stateFul)
        }
        return "redirect:/"; // 컨트롤러가 존재하면 무조건 redirect
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/user/updateForm")
    public String updateForm() {
        return "user/updateForm";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }
}
