package cafe.review.settings;

import lombok.Data;

@Data
public class Notifications {
    private boolean cafeCreatedByEmail;
    private boolean cafeCreatedByWeb;
    private boolean cafeEnrollmentResultByEmail;
    private boolean cafeEnrollmentResultByWeb;
    private boolean cafeUpdatedByEmail;
    private boolean cafeUpdatedByWeb;
}
