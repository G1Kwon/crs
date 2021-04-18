package cafe.review.settings;

import cafe.review.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notifications {

    private boolean cafeCreatedByEmail;
    private boolean cafeCreatedByWeb;
    private boolean cafeEnrollmentResultByEmail;
    private boolean cafeEnrollmentResultByWeb;
    private boolean cafeUpdatedByEmail;
    private boolean cafeUpdatedByWeb;

    public Notifications(Account account) {
        this.cafeCreatedByEmail = account.isCafeCreatedByEmail();
        this.cafeCreatedByWeb = account.isCafeCreatedByWeb();
        this.cafeEnrollmentResultByEmail = account.isCafeEnrollmentResultByEmail();
        this.cafeEnrollmentResultByWeb = account.isCafeEnrollmentResultByWeb();
        this.cafeUpdatedByEmail = account.isCafeUpdatedByEmail();
        this.cafeUpdatedByWeb = account.isCafeUpdatedByWeb();
    }
}
