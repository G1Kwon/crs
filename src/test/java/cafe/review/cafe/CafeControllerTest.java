package cafe.review.cafe;

import cafe.review.WithAccount;
import cafe.review.account.AccountRepository;
import cafe.review.domain.Account;
import cafe.review.domain.Cafe;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class CafeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired CafeRepository cafeRepository;
    @Autowired CafeService cafeService;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("g1Kwon")
    @DisplayName("리뷰 개설 폼 조회")
    void createCafeForm() throws Exception {
        mockMvc.perform(get("/new-cafe"))
                .andExpect(status().isOk())
                .andExpect(view().name("cafe/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("cafeForm"));
    }

    @Test
    @WithAccount("g1Kwon")
    @DisplayName("리뷰 개설 - 완료")
    void createStudy_success() throws Exception {
        mockMvc.perform(post("/new-cafe")
                .param("path", "test-path")
                .param("title", "cafe title")
                .param("shortDescription", "short description of a cafe")
                .param("fullDescription", "full description of a cafe")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cafe/test-path"));

        Cafe study = cafeRepository.findByPath("test-path");
        assertNotNull(study);
        Account account = accountRepository.findByNickname("g1Kwon");
        assertTrue(study.getReviewers().contains(account));
    }

    @Test
    @WithAccount("g1Kwon")
    @DisplayName("리뷰 개설 - 실패")
    void createReview_fail() throws Exception {
        mockMvc.perform(post("/new-cafe")
                .param("path", "wrong path")
                .param("title", "review title")
                .param("shortDescription", "short description of a cafe")
                .param("fullDescription", "full description of a cafe")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("cafe/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("cafeForm"))
                .andExpect(model().attributeExists("account"));

        Cafe cafe = cafeRepository.findByPath("test-path");
        assertNull(cafe);
    }

    @Test
    @WithAccount("g1Kwon")
    @DisplayName("리뷰 조회")
    void viewReview() throws Exception {
        Cafe cafe = new Cafe();
        cafe.setPath("test-path");
        cafe.setTitle("test study");
        cafe.setShortDescription("short description");
        cafe.setFullDescription("<p>full description</p>");

        Account g1Kwon = accountRepository.findByNickname("g1Kwon");
        cafeService.createNewCafeReview(cafe, g1Kwon);

        mockMvc.perform(get("/cafe/test-path"))
                .andExpect(view().name("cafe/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("cafe"));
    }

}