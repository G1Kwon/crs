package cafe.review.cafe;

import cafe.review.cafe.form.CafeDescriptionForm;
import cafe.review.domain.Account;
import cafe.review.domain.Cafe;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
    private final ModelMapper modelMapper;

    public Cafe createNewCafeReview(Cafe cafe, Account account) {
        Cafe newCafe = cafeRepository.save(cafe);
        newCafe.addReviewer(account);
        return newCafe;
    }

    public Cafe getCafeToUpdate(Account account, String path) {
        Cafe cafe = this.getCafe(path);
        if (!account.isReviewerOf(cafe)){
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
        return cafe;
    }

    public Cafe getCafe(String path) {
        Cafe cafe = this.cafeRepository.findByPath(path);
        if (cafe == null) {
            throw new IllegalArgumentException(path + "에 해당하는 카페 리뷰가 없습니다.");
        }
        return cafe;
    }

    public void updateCafeDescription(Cafe cafe, CafeDescriptionForm cafeDescriptionForm) {
        modelMapper.map(cafeDescriptionForm, cafe);
    }

    public void updateCafeImage(Cafe cafe, String image) {
        cafe.setImage(image);
    }

    public void enableCafeBanner(Cafe cafe) {
        cafe.setUseBanner(true);
    }

    public void disableCafeBanner(Cafe cafe) {
        cafe.setUseBanner(false);
    }
}
