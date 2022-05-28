package propofol.ptfservice.api.controller.dto.project;

import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.api.controller.dto.TagResponseDto;

import java.util.*;

@Data
@NoArgsConstructor
public class ProjectResponseDto {
    private Long id;
    private String title;
    private String content;
    private String job;
    private String startTerm;
    private String endTerm;

    private List<TagResponseDto> tagId = new LinkedList<>();

    private String imageBytes;
    private String imageType;
}
