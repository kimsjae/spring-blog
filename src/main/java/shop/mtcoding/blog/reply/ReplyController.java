package shop.mtcoding.blog.reply;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.mtcoding.blog.user.User;

// 댓글쓰기, 댓글삭제, 해당게시글 댓글목록보기
@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final HttpSession session;
    private final ReplyRepository replyRepository;

    @PostMapping("/reply/{id}/delete")
    public String delete(@PathVariable int id) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        Reply reply = replyRepository.findById(id);

        // 댓글 없거나, 댓글 주인이 아니거나, 댓글 주인이거나
        if (reply == null) {
            return "error/404";
        }

        if (reply.getUserId() != sessionUser.getId()) {
            return "error/403";
        }

        replyRepository.deleteById(id);

        return "redirect:/board/" + reply.getBoardId();
    }

    @PostMapping("/reply/save")
    public String write(ReplyRequest.WriteDTO writeDTO) {
        System.out.println(writeDTO);

        User sessionUser = (User) session.getAttribute("sessionUser");


        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 유효성 검사
//        if () {
//
//        }

        // 핵심 코드
        replyRepository.save(writeDTO, sessionUser.getId());

        return "redirect:/board/" + writeDTO.getBoardId();
    }
}
