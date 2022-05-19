package propofol.ptfservice.domain.portfolio.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CareerDto {
    private String title;
    private String content;
    private String startTerm;
    private String endTerm;
}
