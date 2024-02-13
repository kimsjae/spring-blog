package shop.mtcoding.blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository // IoC에 new 하는 방법
public class UserRepository {
    /**
     * DB에 접근할 수 있는 매니저 객체
     * 스프링이 만들어서 IoC에 넣어둔다.
     * DI에서 꺼내 쓰기만 하면 된다.
     */
    private EntityManager em;

    // 생성자 주입 코드 (DI 코드)
    public UserRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional // DB에 write할 때는 필수
    public void save(UserRequest.JoinDTO requestDTO) {
        Query query = em.createNativeQuery("insert into user_tb(username, password, email, created_at) values (?, ?, ?, now())");
        query.setParameter(1, requestDTO.getUsername());
        query.setParameter(2, requestDTO.getPassword());
        query.setParameter(3, requestDTO.getEmail());
        query.executeUpdate();
    }

    public User findByUsernameAndPassword(UserRequest.LoginDTO requestDTO) {
        Query query = em.createNativeQuery("select * from user_tb where username=? and password=?", User.class); // User.class를 적을 수 있는 이유는 User 엔티티가 구현되어 있기 때문이다. 이렇게 되면 자동으로 User클래스에 Table 데이터를 파싱해서 담아준다.
        query.setParameter(1, requestDTO.getUsername());
        query.setParameter(2, requestDTO.getPassword());

        try {
            User user = (User) query.getSingleResult();
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public User findByUsername(String username) {
        Query query = em.createNativeQuery("select * from user_tb where username=?", User.class); // User.class를 적을 수 있는 이유는 User 엔티티가 구현되어 있기 때문이다. 이렇게 되면 자동으로 User클래스에 Table 데이터를 파싱해서 담아준다.
        query.setParameter(1, username);

        try {
            User user = (User) query.getSingleResult();
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}
