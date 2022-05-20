package propofol.ptfservice.api.controller.dto.portfolio;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.api.controller.dto.SkillRequestDto;
import propofol.ptfservice.api.controller.dto.award.AwardCreateRequestDto;
import propofol.ptfservice.api.controller.dto.career.CareerCreateRequestDto;
import propofol.ptfservice.api.controller.dto.project.ProjectCreateRequestDto;
import propofol.ptfservice.domain.portfolio.entity.Template;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PortfolioCreateRequestDto {
    @NotNull
    private Template template;

    @Pattern(regexp="^((http(s?))\\:\\/\\/)([0-9a-zA-Z\\-]+\\.)+[a-zA-Z]{2,6}(\\:[0-9]+)?(\\/\\S*)?$",
            message = "Link 형식이 유효하지 않습니다.")
    private String github;
    private String job;
    private String content;

    @Valid
    private List<AwardCreateRequestDto> awards = new ArrayList<>();
    @Valid
    private List<CareerCreateRequestDto> careers = new ArrayList<>();
    @Valid
    private List<ProjectCreateRequestDto> projects = new ArrayList<>();

}
