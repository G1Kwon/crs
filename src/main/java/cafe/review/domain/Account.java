package cafe.review.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter@Setter@EqualsAndHashCode(of = "id")
@Builder@AllArgsConstructor@NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname; //String -> varchar

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String liveAround;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean cafeCreatedByEmail;

    private boolean cafeCreatedByWeb = true;

    private boolean cafeEnrollmentResultByEmail;

    private boolean cafeEnrollmentResultByWeb = true;

    private boolean cafeUpdatedByEmail;

    private boolean cafeUpdatedByWeb = true;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}
