package propofol.ptfservice.api.controller.dto.award;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AwardResponseDto {
    private Long id;
    private String name;
    private String date;
}
