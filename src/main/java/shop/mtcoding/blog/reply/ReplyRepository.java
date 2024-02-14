package shop.mtcoding.blog.reply;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.mtcoding.blog.board.Board;
import shop.mtcoding.blog.board.BoardRequest;
import shop.mtcoding.blog.board.BoardResponse;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReplyRepository {
    private final EntityManager em;

    @Transactional
    public void save(ReplyRequest.WriteDTO writeDTO, int userId) {
        Query query = em.createNativeQuery("insert into reply_tb(comment, board_id, user_id, created_at) values(?, ?, ?, now())");
        query.setParameter(1, writeDTO.getComment());
        query.setParameter(2, writeDTO.getBoardId());
        query.setParameter(3, userId);

        query.executeUpdate();
    }
}
