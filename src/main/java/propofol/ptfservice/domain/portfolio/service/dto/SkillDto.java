package propofol.ptfservice.domain.portfolio.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SkillDto {
    private String name;

    public SkillDto(String name) {
        this.name = name;
    }
}


