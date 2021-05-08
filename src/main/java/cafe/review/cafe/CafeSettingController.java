package cafe.review.cafe;

import cafe.review.account.CurrentAccount;
import cafe.review.cafe.form.CafeDescriptionForm;
import cafe.review.domain.Account;
import cafe.review.domain.Cafe;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/cafe/{path}/settings")
@RequiredArgsConstructor
public class CafeSettingController {

    private final CafeService cafeService;
    private final ModelMapper modelMapper;

    @GetMapping("/description")
    public String viewCafeSetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Cafe cafe = cafeService.getCafeToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(cafe);
        model.addAttribute(modelMapper.map(cafe, CafeDescriptionForm.class));
        return "cafe/settings/description";
    }

    @PostMapping("/description")
    public String updateCafeInfo(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid CafeDescriptionForm cafeDescriptionForm, Errors errors,
                                 Model model, RedirectAttributes redirectAttributes) {
        //Persist 상태
        Cafe cafe = cafeService.getCafeToUpdate(account, path);

        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(cafe);
            return "cafe/settings/description";
        }
        cafeService.updateCafeDescription(cafe, cafeDescriptionForm);
        redirectAttributes.addFlashAttribute("message", "카레 리뷰 소개를 수정했습니다.");
        return "redirect:/cafe/" + getPath(path) + "/settings/description";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
