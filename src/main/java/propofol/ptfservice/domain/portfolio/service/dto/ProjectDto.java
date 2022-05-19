package propofol.ptfservice.domain.portfolio.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProjectDto {
    private String title;
    private String content;
    private String job;
    private String startTerm;
    private String endTerm;

    private List<SkillDto> projectSkills = new ArrayList<>();
}
