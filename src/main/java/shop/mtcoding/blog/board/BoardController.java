package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.blog.reply.ReplyRepository;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRequest;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardRepository boardRepository;
    private final HttpSession session;
    private final ReplyRepository replyRepository;

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.UpdateDTO requestDTO) {
        // 부가 로직 - 인증체크, 권한체크
        // 1. 인증 체크 - 로그인 돼 있는 지
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 2. 권한 체크 - 너가 맞는 지
        Board board = boardRepository.findById(id);

        if (board.getUserId() != sessionUser.getId()) {
            return "error/403";
        }

        // 3. 핵심 로직
        boardRepository.update(requestDTO, id);

        return "redirect:/board/" + id;
    }


    // 책임: 데이터 조회해서 게시글 수정 페이지에 뿌림
    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request) {
        // 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 모델 위임 필요 (id로 board 조회) - 게시글 수정화면에 원래 글이 있어야 하기 때문
        Board board = boardRepository.findById(id);

        // 권한 체크 (권한 체크 전에 모델위임이 먼저 필요)
        if (board.getUserId() != sessionUser.getId()) {
            return "error/403";
        }

        // 가방에 담기
        request.setAttribute("board", board);

        return "board/updateForm";
    }


    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request) {
        // 1. 인증 안 되면 나가
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 2. 권한 없으면 나가
        Board board = boardRepository.findById(id);
        if (board.getUserId() != sessionUser.getId()) {
            request.setAttribute("status", 403);
            request.setAttribute("msg", "게시글을 삭제할 권한이 없습니다.");
            return "error/40x";
        }

        boardRepository.delete(id);

        return "redirect:/";
    }

    // localhost:8080?page=0
    @GetMapping({ "/"})
    public String index(HttpServletRequest request,
                        @RequestParam(defaultValue="0") Integer page,
                        @RequestParam(defaultValue = "") String keyword
    ) {
        // isEmpty -> null, 공백 empty
        // isBlank -> null, 공백, 화이트스페이스 blank

        if (keyword.isBlank()) {
            List<Board> boardList = boardRepository.findAll(page);

            // 전체 페이지 개수
            int count = boardRepository.count().intValue();
            int namerge = count % 3 == 0 ? 0 : 1;
            int allPageCount = count / 3 + namerge;

            request.setAttribute("boardList", boardList);
            request.setAttribute("first", page == 0);
            request.setAttribute("last", allPageCount == page+1);
            request.setAttribute("prev", page-1);
            request.setAttribute("next", page+1);
            request.setAttribute("keyword", "");

        } else {
            List<Board> boardList = boardRepository.findAll(page, keyword);

            // 전체 페이지 개수
            int count = boardRepository.count(keyword).intValue();
            int namerge = count % 3 == 0 ? 0 : 1;
            int allPageCount = count / 3 + namerge;

            request.setAttribute("boardList", boardList);
            request.setAttribute("first", page == 0);
            request.setAttribute("last", allPageCount == page+1);
            request.setAttribute("prev", page-1);
            request.setAttribute("next", page+1);
            request.setAttribute("keyword", keyword);
        }
        List<Board> boardList = boardRepository.findAll(page);


        return "index";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        // session 영역에 sessionUser 키값에 user 객체 있는지 체크
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 값이 null이면 로그인 페이지로 리다이렉션
        // 값이 null이 아니면 saveForm으로 이동
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }
        return "board/saveForm";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request) { // 경로 변수
        User sessionUser = (User) session.getAttribute("sessionUser");

        BoardResponse.DetailDTO boardDTO = boardRepository.findByIdWithUser(id);

        boardDTO.isBoardOwner(sessionUser);

        List<BoardResponse.ReplyDTO> replyDTOList = replyRepository.findByBoardId(id, sessionUser);


        request.setAttribute("board", boardDTO);
        request.setAttribute("replyList", replyDTOList);
        //request.setAttribute("pageOwner", pageOwner);
        return "board/detail";
    }

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request) {
        // 1. 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 2. 바디 데이터 확인 및 유효성 검사
        System.out.println(requestDTO);

        if (requestDTO.getTitle().length() > 30) {
            request.setAttribute("msg", "title의 길이가 30자를 초과해서는 안 돼요.");
            request.setAttribute("status", 400);
            return "error/40x"; // BadRequest
        }

        // 3. 모델 위임
        // insert into board_tb(title, content, user_id, created_at) values(?, ?, ?, now());
        boardRepository.save(requestDTO, sessionUser.getId());

        return "redirect:/";
    }

}
