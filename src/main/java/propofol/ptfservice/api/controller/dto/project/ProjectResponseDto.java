package propofol.ptfservice.api.controller.dto.project;

import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.api.controller.dto.SkillResponseDto;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProjectResponseDto {
    private String title;
    private String content;
    private String job;
    private String startTerm;
    private String endTerm;

    private List<SkillResponseDto> projectSkills = new ArrayList<>();

    private String imageBytes;
    private String imageType;
}
