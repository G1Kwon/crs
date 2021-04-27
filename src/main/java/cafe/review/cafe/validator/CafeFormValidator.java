package cafe.review.cafe.validator;

import cafe.review.cafe.CafeRepository;
import cafe.review.cafe.form.CafeForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CafeFormValidator implements Validator {

    private final CafeRepository cafeRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return CafeForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CafeForm studyForm = (CafeForm) target;
        if (cafeRepository.existsByPath(studyForm.getPath())) {
            errors.rejectValue("path", "wrong.path", "해당 리뷰 경로값을 사용할 수 없습니다.");
        }
    }
}
