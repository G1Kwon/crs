package cafe.review.cafe;

import cafe.review.domain.Account;
import cafe.review.domain.Cafe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;

    public Cafe createNewCafeReview(Cafe cafe, Account account) {
        Cafe newCafe = cafeRepository.save(cafe);
        newCafe.addReviewer(account);
        return newCafe;
    }
}
