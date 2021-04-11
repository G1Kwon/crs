package cafe.review.account;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @Test @DisplayName("화원 가입 화면 보이는지 테스트")
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @Test @DisplayName("회원 가입 처리 - 입력값 오류")
    void signIpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "g1kwon")
                .param("email", "g1.kwon@email..")
                .param("password", "12345")
                //securityConfig.java 에서 예외처리를 했지만 폼 데이터가 csrf 토큰이 없는 상태라 아래코드 추가 필요
                .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("account/sign-up"));
    }

    @Test @DisplayName("회원 가입 처리 - 입력값 정상")
    void signIpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "g1kwon")
                .param("email", "g1.kwon@hyundai-autoever.com")
                .param("password", "12345678")
                //securityConfig.java 에서 예외처리를 했지만 폼 데이터가 csrf 토큰이 없는 상태라 아래코드 추가 필요
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
        assertTrue(accountRepository.existsByEmail("g1.kwon@hyundai-autoever.com"));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}