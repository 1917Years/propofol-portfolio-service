package propofol.ptfservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.domain.portfolio.entity.Archive;
import propofol.ptfservice.domain.portfolio.entity.Career;
import propofol.ptfservice.domain.portfolio.entity.Project;
import propofol.ptfservice.domain.portfolio.entity.Template;
import propofol.ptfservice.domain.portfolio.service.dto.ArchiveDto;
import propofol.ptfservice.domain.portfolio.service.dto.CareerDto;
import propofol.ptfservice.domain.portfolio.service.dto.PortfolioDto;
import propofol.ptfservice.domain.portfolio.service.dto.ProjectDto;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PortfolioResponseDto {
    private Long totalCount;
    private Integer pageCount;

    private List<PortfolioDto> portfolios;

    private String email;
    private String username;
    private String phoneNumber;
    private LocalDate birth;
    private String degree;
    private String score;

}
