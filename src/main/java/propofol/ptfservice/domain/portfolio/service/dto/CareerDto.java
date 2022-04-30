package propofol.ptfservice.domain.portfolio.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CareerDto {
    private String title;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}", message = "올바른 날짜 형식이 아닙니다.")
    private String startDate;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}", message = "올바른 날짜 형식이 아닙니다.")
    private String endDate;
    private String basicContent;
    private String detailContent;
}
