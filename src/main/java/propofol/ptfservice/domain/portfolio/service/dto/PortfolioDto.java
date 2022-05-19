package propofol.ptfservice.domain.portfolio.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.domain.portfolio.entity.Template;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PortfolioDto {
    private Template template;
    private String github;
    private String job;
    private String content;

    private List<AwardDto> awards = new ArrayList<>();
    private List<CareerDto> careers = new ArrayList<>();
    private List<ProjectDto> projects = new ArrayList<>();

    public PortfolioDto(Template template, String github, String job, String content,
                        List<AwardDto> awards, List<CareerDto> careers, List<ProjectDto> projects) {
        this.template = template;
        this.github = github;
        this.job = job;
        this.content = content;
        this.awards = awards;
        this.careers = careers;
        this.projects = projects;
    }
}
