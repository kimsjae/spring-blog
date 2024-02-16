package shop.mtcoding.blog.board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepository {
    private final EntityManager em;

    public Long count() {
        Query query = em.createNativeQuery("select count(*) from board_tb");
        return (Long) query.getSingleResult();
    }

    public Long count(String keyword) {
        Query query = em.createNativeQuery("select count(*) from board_tb where title like ?");
        query.setParameter(1, "%"+keyword+"%");
        return (Long) query.getSingleResult();
    }



    @Transactional
    public void delete(int id) {
        Query query = em.createNativeQuery("delete from board_tb where id = ?");
        query.setParameter(1, id);
        query.executeUpdate();
    }

    public Board findById(int id) {
        Query query = em.createNativeQuery("select * from board_tb where id = ?", Board.class);
        query.setParameter(1, id);

        Board board = (Board) query.getSingleResult();
        return board;
    }



    public List<Board> findAll(Integer page) {
        Query query = em.createNativeQuery("select * from board_tb order by id desc limit ?,3", Board.class); // 시작번호, 개수
        query.setParameter(1, page*3);
        return query.getResultList();
    }

    public List<Board> findAll(Integer page, String keyword) {
        Query query = em.createNativeQuery("select * from board_tb where title like ? order by id desc limit ?,3", Board.class); // 시작번호, 개수
        query.setParameter(1, "%"+keyword+"%");
        query.setParameter(2, page*3);
        return query.getResultList();
    }


    public BoardResponse.DetailDTO findByIdWithUser(int idx) {
        Query query = em.createNativeQuery("select b.id, b.title, b.content, b.user_id, u.username from board_tb b inner join user_tb u on b.user_id = u.id where b.id = ?");
        query.setParameter(1, idx);

        Object[] row = (Object[]) query.getSingleResult();

        Integer id = (Integer) row[0];
        String title = (String) row[1];
        String content = (String) row[2];
        int userId = (Integer) row[3];
        String username = (String) row[4];

        System.out.println("id : "+id);
        System.out.println("title : "+title);
        System.out.println("content : "+content);
        System.out.println("userId : "+userId);
        System.out.println("username : "+username);

        BoardResponse.DetailDTO responseDTO = new BoardResponse.DetailDTO();
        responseDTO.setId(id);
        responseDTO.setTitle(title);
        responseDTO.setContent(content);
        responseDTO.setUserId(userId);
        responseDTO.setUsername(username);

        return responseDTO;
    }

    @Transactional
    public void save(BoardRequest.SaveDTO requestDTO, int userId) {
        Query query = em.createNativeQuery("insert into board_tb(title, content, user_id, created_at) values(?, ?, ?, now())");
        query.setParameter(1, requestDTO.getTitle());
        query.setParameter(2, requestDTO.getContent());
        query.setParameter(3, userId);

        query.executeUpdate();
    }

    @Transactional
    public void update(BoardRequest.UpdateDTO requestDTO, int id) {
        Query query = em.createNativeQuery("update board_tb set title = ?, content = ? where id = ?");
        query.setParameter(1, requestDTO.getTitle());
        query.setParameter(2, requestDTO.getContent());
        query.setParameter(3, id);
        query.executeUpdate(); // 로우의 행을 리턴
    }
}
