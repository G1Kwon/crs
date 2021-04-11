package cafe.review.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        //model.addAttribute("signUpForm", new SignUpForm()); 클래스 이름의 캐멀캐이스를 속성으로 들어가면 생략 가능
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

}
