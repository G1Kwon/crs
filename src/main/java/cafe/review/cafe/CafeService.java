package cafe.review.cafe;

import cafe.review.cafe.form.CafeDescriptionForm;
import cafe.review.domain.Account;
import cafe.review.domain.Cafe;
import cafe.review.domain.Tag;
import cafe.review.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cafe.review.cafe.form.CafeForm.VALID_PATH_PATTERN;

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
        checkIfReviewer(account, cafe);
        return cafe;
    }

    public Cafe getCafe(String path) {
        Cafe cafe = this.cafeRepository.findByPath(path);
        checkIfExistingCafe(path, cafe);
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

    public void addTag(Cafe cafe, Tag tag) {
        cafe.getTags().add(tag);
    }

    public void removeTag(Cafe cafe, Tag tag) {
        cafe.getTags().remove(tag);
    }

    public void addZone(Cafe cafe, Zone zone) {
        cafe.getZones().add(zone);
    }

    public void removeZone(Cafe cafe, Zone zone) {
        cafe.getZones().remove(zone);
    }

    public Cafe getCafeToUpdateTag(Account account, String path) {
        Cafe cafe = cafeRepository.findAccountWithTagsByPath(path);
        checkIfExistingCafe(path, cafe);
        checkIfReviewer(account, cafe);
        return cafe;
    }

    public Cafe getCafeToUpdateZone(Account account, String path) {
        Cafe cafe = cafeRepository.findAccountWithZonesByPath(path);
        checkIfExistingCafe(path, cafe);
        checkIfReviewer(account, cafe);
        return cafe;
    }

    public Cafe getCafeToUpdateStatus(Account account, String path) {
        Cafe cafe = cafeRepository.findCafeWithReviewersByPath(path);
        checkIfExistingCafe(path, cafe);
        checkIfReviewer(account, cafe);
        return cafe;
    }

    private void checkIfReviewer(Account account, Cafe cafe) {
        if (!account.isReviewerOf(cafe)){
            throw new AccessDeniedException("?????? ????????? ????????? ??? ????????????.");
        }
    }

    private void checkIfExistingCafe(String path, Cafe cafe) {
        if (cafe == null) {
            throw new IllegalArgumentException(path + "??? ???????????? ?????? ????????? ????????????.");
        }
    }

    public void publish(Cafe cafe) {
        cafe.publish();
    }

    public void close(Cafe cafe) {
        cafe.close();
    }

    public void startRecruit(Cafe cafe) {
        cafe.startRecruit();
    }

    public void stopRecruit(Cafe cafe) {
        cafe.stopRecruit();
    }

    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }

        return !cafeRepository.existsByPath(newPath);
    }

    public void updateCafePath(Cafe cafe, String newPath) {
        cafe.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateCafeTitle(Cafe cafe, String newTitle) {
        cafe.setTitle(newTitle);
    }

    public void remove(Cafe cafe) {
        if (cafe.isRemovable()) {
            cafeRepository.delete(cafe);
        } else {
            throw new IllegalArgumentException("??????????????? ????????? ??? ????????????.");
        }
    }

    public void addMember(Cafe cafe, Account account) {
        cafe.addMember(account);
    }

    public void removeMember(Cafe cafe, Account account) {
        cafe.removeMember(account);
    }
}
