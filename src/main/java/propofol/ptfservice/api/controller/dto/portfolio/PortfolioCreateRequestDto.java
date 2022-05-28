package propofol.ptfservice.api.controller.dto.portfolio;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.api.controller.dto.award.AwardCreateRequestDto;
import propofol.ptfservice.api.controller.dto.career.CareerCreateRequestDto;
import propofol.ptfservice.api.controller.dto.project.ProjectCreateRequestDto;
import propofol.ptfservice.domain.portfolio.entity.Template;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PortfolioCreateRequestDto {
    @NotNull
    private Template template;

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
