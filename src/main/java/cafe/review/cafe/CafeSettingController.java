package cafe.review.cafe;

import cafe.review.account.CurrentAccount;
import cafe.review.cafe.form.CafeDescriptionForm;
import cafe.review.domain.Account;
import cafe.review.domain.Cafe;
import cafe.review.domain.Tag;
import cafe.review.domain.Zone;
import cafe.review.settings.form.TagForm;
import cafe.review.settings.form.ZoneForm;
import cafe.review.tag.TagRepository;
import cafe.review.tag.TagService;
import cafe.review.zone.ZoneRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cafe/{path}/settings")
@RequiredArgsConstructor
public class CafeSettingController {

    private final CafeService cafeService;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @GetMapping("/description")
    public String viewCafeSetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Cafe cafe = cafeService.getCafeToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(cafe);
        model.addAttribute(modelMapper.map(cafe, CafeDescriptionForm.class));
        return "cafe/settings/description";
    }

    @PostMapping("/description")
    public String updateCafeInfo(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid CafeDescriptionForm cafeDescriptionForm, Errors errors,
                                 Model model, RedirectAttributes redirectAttributes) {
        //Persist 상태
        Cafe cafe = cafeService.getCafeToUpdate(account, path);

        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(cafe);
            return "cafe/settings/description";
        }
        cafeService.updateCafeDescription(cafe, cafeDescriptionForm);
        redirectAttributes.addFlashAttribute("message", "카레 리뷰 소개를 수정했습니다.");
        return "redirect:/cafe/" + getPath(path) + "/settings/description";
    }

    @GetMapping("/banner")
    public String cafeImageForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Cafe cafe = cafeService.getCafeToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(cafe);
        return "cafe/settings/banner";
    }

    @PostMapping("/banner")
    public String cafeImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                  String image, RedirectAttributes redirectAttributes) {
        Cafe cafe = cafeService.getCafeToUpdate(account, path);
        cafeService.updateCafeImage(cafe, image);
        redirectAttributes.addFlashAttribute("message", "카페리뷰 이미지를 수정했습니다.");
        return "redirect:/cafe/" + getPath(path) + "/settings/banner";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @PostMapping("/banner/enable")
    public String enableCafeBanner(@CurrentAccount Account account, @PathVariable String path) {
        Cafe cafe = cafeService.getCafeToUpdate(account, path);
        cafeService.enableCafeBanner(cafe);
        return "redirect:/cafe/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableCafeBanner(@CurrentAccount Account account, @PathVariable String path) {
        Cafe cafe = cafeService.getCafeToUpdate(account, path);
        cafeService.disableCafeBanner(cafe);
        return "redirect:/cafe/" + getPath(path) + "/settings/banner";
    }

    @GetMapping("/tags")
    public String cafeTagsForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Cafe cafe = cafeService.getCafeToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(cafe);

        model.addAttribute("tags", cafe.getTags().stream()
                .map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTagTitles = tagRepository.findAll().stream()
                .map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));
        return "cafe/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag (@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody TagForm tagForm) {
        // cafe 객체는 persistent 상태이다. 영속성 컨텍스트가 열려 있는 상태이기에
        Cafe cafe = cafeService.getCafeToUpdateTag(account, path);
        // tag 객체는 persistent 상태이다. 영속성 컨텍스트가 열려 있는 상태이기에
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        cafeService.addTag(cafe, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @PathVariable String path,
                                   @RequestBody TagForm tagForm) {
        Cafe cafe = cafeService.getCafeToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        cafeService.removeTag(cafe, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String cafeZonesForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Cafe cafe = cafeService.getCafeToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(cafe);
        model.addAttribute("zones", cafe.getZones().stream()
                .map(Zone::toString).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "cafe/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone (@CurrentAccount Account account, @PathVariable String path,
                                   @RequestBody ZoneForm zoneForm) {
        Cafe cafe = cafeService.getCafeToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        cafeService.addZone(cafe, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentAccount Account account, @PathVariable String path,
                                    @RequestBody ZoneForm zoneForm) {
        Cafe cafe = cafeService.getCafeToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        cafeService.removeZone(cafe, zone);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/cafe")
    public String cafeSettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Cafe cafe = cafeService.getCafeToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(cafe);
        return "cafe/settings/cafe";
    }

    @PostMapping("/cafe/publish")
    public String publishCafe(@CurrentAccount Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        Cafe cafe = cafeService.getCafeToUpdateStatus(account, path);
        cafeService.publish(cafe);
        attributes.addFlashAttribute("message", "카페리뷰를 공개했습니다.");
        return "redirect:/cafe/" + getPath(path) + "/settings/cafe";
    }

    @PostMapping("/cafe/close")
    public String closeCafe(@CurrentAccount Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        Cafe cafe = cafeService.getCafeToUpdateStatus(account, path);
        cafeService.close(cafe);
        attributes.addFlashAttribute("message", "카페리뷰를 종료했습니다.");
        return "redirect:/cafe/" + getPath(path) + "/settings/cafe";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                               RedirectAttributes attributes) {
        Cafe cafe = cafeService.getCafeToUpdateStatus(account, path);
        if (!cafe.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/cafe/" + getPath(path) + "/settings/cafe";
        }

        cafeService.startRecruit(cafe);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
        return "redirect:/cafe/" + getPath(path) + "/settings/cafe";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                              RedirectAttributes attributes) {
        Cafe cafe = cafeService.getCafeToUpdate(account, path);
        if (!cafe.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/cafe/" + getPath(path) + "/settings/cafe";
        }

        cafeService.stopRecruit(cafe);
        attributes.addFlashAttribute("message", "인원 모집을 종료합니다.");
        return "redirect:/cafe/" + getPath(path) + "/settings/cafe";
    }
}
