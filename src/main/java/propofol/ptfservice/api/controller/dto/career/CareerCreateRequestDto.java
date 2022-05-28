package propofol.ptfservice.api.controller.dto.career;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class CareerCreateRequestDto {
    private String title;
    private String content;
    private String startTerm;
    private String endTerm;

}

