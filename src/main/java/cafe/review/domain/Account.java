package cafe.review.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String liveAround;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean cafeCreatedByEmail;

    private boolean cafeCreatedByWeb;

    private boolean cafeEnrollmentResultByEmail;

    private boolean cafeEnrollmentResultByWeb;

    private boolean cafeUpdatedByEmail;

    private boolean cafeUpdatedByWeb;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }
}
