package propofol.ptfservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicInfoCreateRequestDto {
    private String github;
    private String job;
    private String content;
}
