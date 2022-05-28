package propofol.ptfservice.api.controller.dto.award;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;


@Data
@NoArgsConstructor
public class AwardCreateRequestDto {
    private String name;
    private String date;
}
