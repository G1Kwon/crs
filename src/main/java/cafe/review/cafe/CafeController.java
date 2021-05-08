package cafe.review.cafe;

import cafe.review.account.CurrentUser;
import cafe.review.cafe.form.CafeForm;
import cafe.review.cafe.validator.CafeFormValidator;
import cafe.review.domain.Account;
import cafe.review.domain.Cafe;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;
    private final CafeRepository cafeRepository;
    private final ModelMapper modelMapper;
    private final CafeFormValidator cafeFormValidator;

    @InitBinder("cafeForm")
    public void cafeFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(cafeFormValidator);
    }

    @GetMapping("/cafe/{path}")
    public String viewCafe(@CurrentUser Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);
        model.addAttribute(cafeRepository.findByPath(path));
        return "cafe/view";
    }

    @GetMapping("/new-cafe")
    public String newCafeForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new CafeForm());
        return "cafe/form";
    }

    @PostMapping("/new-cafe")
    public String newCafeSubmit(@CurrentUser Account account, @Valid CafeForm cafeForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "cafe/form";
        }
        Cafe newCafe = cafeService.createNewCafeReview(modelMapper.map(cafeForm, Cafe.class), account);
        return "redirect:/cafe/" + URLEncoder.encode(newCafe.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/cafe/{path}/members")
    public String viewCafeMembers(@CurrentUser Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);
        model.addAttribute(cafeRepository.findByPath(path));
        return "cafe/members";
    }
}
