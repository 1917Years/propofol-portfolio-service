package propofol.ptfservice.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.domain.portfolio.entity.Archive;
import propofol.ptfservice.domain.portfolio.entity.Career;
import propofol.ptfservice.domain.portfolio.entity.Project;
import propofol.ptfservice.domain.portfolio.entity.Template;
import propofol.ptfservice.domain.portfolio.service.dto.ArchiveDto;
import propofol.ptfservice.domain.portfolio.service.dto.CareerDto;
import propofol.ptfservice.domain.portfolio.service.dto.ProjectDto;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PortfolioCreateRequestDto {
    private Template template;
    private List<ArchiveDto> archives = new ArrayList<>();
    private List<CareerDto> careers = new ArrayList<>();
    private List<ProjectDto> projects = new ArrayList<>();

}
