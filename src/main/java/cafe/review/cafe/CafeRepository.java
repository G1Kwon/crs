package cafe.review.cafe;

import cafe.review.domain.Cafe;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CafeRepository extends JpaRepository<Cafe, Long> {

    boolean existsByPath(String path);

    @EntityGraph(value = "Cafe.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Cafe findByPath(String path);

    @EntityGraph(value = "Cafe.withTagsAndReviewers", type = EntityGraph.EntityGraphType.FETCH)
    Cafe findAccountWithTagsByPath(String path);

    @EntityGraph(value = "Cafe.withZonesAndReviewers", type = EntityGraph.EntityGraphType.FETCH)
    Cafe findAccountWithZonesByPath(String path);

    @EntityGraph(value = "Cafe.withReviewers", type = EntityGraph.EntityGraphType.FETCH)
    Cafe findCafeWithReviewersByPath(String path);
}
