package propofol.ptfservice.domain.portfolio.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.domain.portfolio.entity.Template;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PortfolioDto {
    @NotEmpty
    private Template template;
    private List<ArchiveDto> archives = new ArrayList<>();
    private List<CareerDto> careers = new ArrayList<>();
    private List<ProjectDto> projects = new ArrayList<>();
}
