package propofol.ptfservice.api.controller.dto.portfolio;

import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.api.controller.dto.SkillResponseDto;
import propofol.ptfservice.api.controller.dto.award.AwardResponseDto;
import propofol.ptfservice.api.controller.dto.career.CareerResponseDto;
import propofol.ptfservice.api.controller.dto.project.ProjectResponseDto;
import propofol.ptfservice.domain.portfolio.entity.Template;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PortfolioDetailResponseDto {
    private Template template;
    private String github;
    private String job;
    private String content;

    private List<AwardResponseDto> awards = new ArrayList<>();
    private List<CareerResponseDto> careers = new ArrayList<>();
    private List<ProjectResponseDto> projects = new ArrayList<>();
}
