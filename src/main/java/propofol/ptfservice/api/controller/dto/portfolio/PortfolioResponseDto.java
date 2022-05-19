package propofol.ptfservice.api.controller.dto.portfolio;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PortfolioResponseDto {
    private String email;
    private String username;
    private String phoneNumber;

    private PortfolioDetailResponseDto portfolio;

}
