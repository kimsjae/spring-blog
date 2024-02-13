package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.mtcoding.blog._core.config.security.MyLoginUser;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRequest;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardRepository boardRepository;
    private final HttpSession session;

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.UpdateDTO requestDTO, @AuthenticationPrincipal MyLoginUser myLoginUser) {

        // 2. 권한 체크 - 너가 맞는 지
        Board board = boardRepository.findById(id);

        if (board.getUserId() != myLoginUser.getUser().getId()) {
            return "error/403";
        }

        // 3. 핵심 로직
        boardRepository.update(requestDTO, id);

        return "redirect:/board/" + id;
    }


    // 책임: 데이터 조회해서 게시글 수정 페이지에 뿌림
    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {


        // 모델 위임 필요 (id로 board 조회) - 게시글 수정화면에 원래 글이 있어야 하기 때문
        Board board = boardRepository.findById(id);

        // 권한 체크 (권한 체크 전에 모델위임이 먼저 필요)
        if (board.getUserId() != myLoginUser.getUser().getId()) {
            return "error/403";
        }

        // 가방에 담기
        request.setAttribute("board", board);

        return "board/updateForm";
    }


    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {

        Board board = boardRepository.findById(id);
        if (board.getUserId() != myLoginUser.getUser().getId()) {
            request.setAttribute("status", 403);
            request.setAttribute("msg", "게시글을 삭제할 권한이 없습니다.");
            return "error/40x";
        }

        boardRepository.delete(id);

        return "redirect:/";
    }

    @GetMapping({ "/"})
    public String index(HttpServletRequest request) {
        List<Board> boardList = boardRepository.findAll();
        request.setAttribute("boardList", boardList);
        return "index";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        return "board/saveForm";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) { // 경로 변수
        // 1. 모델 진입 - 상세보기 데이터 가져오기
        BoardResponse.DetailDTO detailDTO = boardRepository.findByIdWithUser(id);


        User sessionUser = (User) session.getAttribute("sessionUser");
        boolean pageOwner = false;

        if (sessionUser != null && detailDTO.getUserId() == sessionUser.getId()) {
            pageOwner = true;
        }

        request.setAttribute("board", detailDTO);
        request.setAttribute("pageOwner", pageOwner);
        return "board/detail";
    }

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {

        // 2. 바디 데이터 확인 및 유효성 검사
        System.out.println(requestDTO);

        if (requestDTO.getTitle().length() > 30) {
            request.setAttribute("msg", "title의 길이가 30자를 초과해서는 안 돼요.");
            request.setAttribute("status", 400);
            return "error/40x"; // BadRequest
        }

        // 3. 모델 위임
        // insert into board_tb(title, content, user_id, created_at) values(?, ?, ?, now());
        boardRepository.save(requestDTO, myLoginUser.getUser().getId());

        return "redirect:/";
    }

}
