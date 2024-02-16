package shop.mtcoding.blog.reply;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.mtcoding.blog.board.Board;
import shop.mtcoding.blog.board.BoardRequest;
import shop.mtcoding.blog.board.BoardResponse;
import shop.mtcoding.blog.user.User;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class ReplyRepository {
    private final EntityManager em;

    public List<BoardResponse.ReplyDTO> findByBoardId(int boardId, User sessionUser) {
        String q = """
                select rt.id, rt.user_id, rt.comment, ut.username from reply_tb rt inner join user_tb ut on rt.user_id = ut.id where rt.board_id = ?
                """;
        Query query = em.createNativeQuery(q);
        query.setParameter(1, boardId);

        List<Object[]> rows = query.getResultList();

        return rows.stream().map(row -> new BoardResponse.ReplyDTO(row, sessionUser)).toList();
    }

    @Transactional
    public void save(ReplyRequest.WriteDTO writeDTO, int userId) {
        Query query = em.createNativeQuery("insert into reply_tb(comment, board_id, user_id, created_at) values(?, ?, ?, now())");
        query.setParameter(1, writeDTO.getComment());
        query.setParameter(2, writeDTO.getBoardId());
        query.setParameter(3, userId);

        query.executeUpdate();
    }
}
