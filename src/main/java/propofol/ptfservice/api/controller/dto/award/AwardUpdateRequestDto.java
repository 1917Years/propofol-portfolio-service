package propofol.ptfservice.api.controller.dto.award;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AwardUpdateRequestDto {
    private String name;
    private String date;
}
