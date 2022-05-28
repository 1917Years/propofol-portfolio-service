package propofol.ptfservice.api.controller.dto.project;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProjectCreateRequestDto {
    private String title;
    private String content;
    private String job;
    private String startTerm;
    private String endTerm;

    private List<String> tagId = new ArrayList<>();


}
