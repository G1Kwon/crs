package cafe.review.account;

import cafe.review.domain.Account;
import cafe.review.settings.Notifications;
import cafe.review.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public Account processNewAccount(SignUpForm signUpForm) {
        //detached 상태가 아닌 persist 상태를 만들기 위해 @Transactional 로 만들었다.
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .cafeCreatedByWeb(true)
                .cafeEnrollmentResultByWeb(true)
                .cafeUpdatedByWeb(true)
                .build();
        //persist 상태
        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("카페 리뷰 시스템 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                //account.getNickname(),
                new UserAccount(account), //principal 객체가 되서 로그인 되면 account 객체가 인증된 principal 로 간주됨
                account.getPassword(), //정석은 이게 아니지만 평문 비밀번호는 사용하지 않을거라 이렇게 작성
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account);
    }

    public void completeSignup(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        account.setUrl(profile.getUrl());
        account.setOccupation(profile.getOccupation());
        account.setBio(profile.getBio());
        account.setLocation(profile.getLocation());
        account.setProfileImage(profile.getProfileImage());
        //Detached 객체를 기존과 merge
        accountRepository.save(account);
        // TODO 문제가 하나 더 남았습니다.
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        account.setCafeCreatedByEmail(notifications.isCafeCreatedByEmail());
        account.setCafeCreatedByWeb(notifications.isCafeCreatedByWeb());
        account.setCafeEnrollmentResultByEmail(notifications.isCafeEnrollmentResultByEmail());
        account.setCafeEnrollmentResultByWeb(notifications.isCafeEnrollmentResultByWeb());
        account.setCafeUpdatedByEmail(notifications.isCafeUpdatedByEmail());
        account.setCafeUpdatedByWeb(notifications.isCafeUpdatedByWeb());
        accountRepository.save(account);
    }
}
