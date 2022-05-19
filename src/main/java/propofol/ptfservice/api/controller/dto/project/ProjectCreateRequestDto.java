package propofol.ptfservice.api.controller.dto.project;

import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.api.controller.dto.SkillRequestDto;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProjectCreateRequestDto {
    private String title;
    private String content;
    private String job;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}", message = "올바른 날짜 형식이 아닙니다.")
    private String startTerm;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}", message = "올바른 날짜 형식이 아닙니다.")
    private String endTerm;

    private List<SkillRequestDto> projectSkills = new ArrayList<>();
    /** TODO 일단빼뒀음*/
//    private String fileName;

//    @Pattern(regexp="^((http(s?))\\:\\/\\/)([0-9a-zA-Z\\-]+\\.)+[a-zA-Z]{2,6}(\\:[0-9]+)?(\\/\\S*)?$",
//            message = "Link 형식이 유효하지 않습니다.")
}
