package cafe.review.cafe;

import cafe.review.domain.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CafeRepository extends JpaRepository<Cafe, Long> {
    boolean existsByPath(String path);
}
