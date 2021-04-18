package cafe.review.settings;

import cafe.review.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //기본 생성자 필요 NPE 방지 위함
public class Profile {
    private String bio;
    private String url;
    private String occupation;
    private String location;

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
