package propofol.ptfservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.ptfservice.api.common.annotation.Token;
import propofol.ptfservice.api.controller.dto.MemberInfoResponseDto;
import propofol.ptfservice.api.controller.dto.PortfolioCreateRequestDto;
import propofol.ptfservice.api.controller.dto.PortfolioResponseDto;
import propofol.ptfservice.api.controller.dto.PortfolioUpdateRequestDto;
import propofol.ptfservice.domain.portfolio.entity.Portfolio;
import propofol.ptfservice.domain.portfolio.service.PortfolioService;
import propofol.ptfservice.domain.portfolio.service.dto.ArchiveDto;
import propofol.ptfservice.domain.portfolio.service.dto.CareerDto;
import propofol.ptfservice.domain.portfolio.service.dto.PortfolioDto;
import propofol.ptfservice.domain.portfolio.service.dto.ProjectDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final ModelMapper modelMapper;

    /**
     * 포트폴리오 생성
     */
    @PostMapping
    public String createPortfolio (@Validated @RequestBody PortfolioCreateRequestDto requestDto) {
        PortfolioDto portfolioDto = modelMapper.map(requestDto, PortfolioDto.class);
        Portfolio portfolio = portfolioService.createPortfolio(portfolioDto);
        return portfolioService.savePortfolio(portfolio);
    }

    /**
     * 포트폴리오 조회
     */
    @GetMapping("/myPortfolio")
    public PortfolioResponseDto getPortfolio (@RequestParam Integer page,
                                              @RequestHeader(name = "Authorization") String token,
                                              @Token String memberId) {
        PortfolioResponseDto responseDto = new PortfolioResponseDto();

        MemberInfoResponseDto memberInfo = portfolioService.getMemberInfo(token);
        Page<Portfolio> findPortfolio = portfolioService.getPortfolioInfo(page, memberId);

        responseDto.setTotalCount(findPortfolio.getTotalElements());
        responseDto.setPageCount(findPortfolio.getTotalPages());

        responseDto.setEmail(memberInfo.getEmail());
        responseDto.setUsername(memberInfo.getUsername());
        responseDto.setPhoneNumber(memberInfo.getPhoneNumber());
        responseDto.setBirth(memberInfo.getBirth());
        responseDto.setDegree(memberInfo.getDegree());
        responseDto.setScore(memberInfo.getScore());

        List<PortfolioDto> portfolioDtos = new ArrayList<>();

        List<CareerDto> careerDtos = new ArrayList<>();
        List<ProjectDto> projectDtos = new ArrayList<>();
        List<ArchiveDto> archiveDtos = new ArrayList<>();

        findPortfolio.forEach(portfolioInfo -> {
            PortfolioDto portDto = new PortfolioDto();
            portfolioInfo.getCareers().forEach(career -> {
                CareerDto careerDto = modelMapper.map(career, CareerDto.class);
                careerDtos.add(careerDto);
            });
            portfolioInfo.getProjects().forEach(project -> {
                ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
                projectDtos.add(projectDto);
            });
            portfolioInfo.getArchives().forEach(archive -> {
                ArchiveDto archiveDto = modelMapper.map(archive, ArchiveDto.class);
                archiveDtos.add(archiveDto);
            });
            portDto.setTemplate(portfolioInfo.getTemplate());
            portDto.setCareers(careerDtos);
            portDto.setProjects(projectDtos);
            portDto.setArchives(archiveDtos);
            portfolioDtos.add(portDto);
        });

        responseDto.setPortfolios(portfolioDtos);

        return responseDto;
    }

    /**
     * 포트폴리오 수정
     */
    @PostMapping("/{portfolioId}")
    public String updatePortfolio(@PathVariable(value = "portfolioId") Long portfolioId,
                                  @Token String memberId,
                                  @RequestBody PortfolioUpdateRequestDto requestDto
                                  ) {
        PortfolioDto portfolioDto = modelMapper.map(requestDto, PortfolioDto.class);
        return portfolioService.updatePortfolio(portfolioId, memberId, portfolioDto);
    }

    /**
     * 포트폴리오 삭제
     */
    @DeleteMapping("/{portfolioId}")
    public String deletePortfolio(@PathVariable(value = "portfolioId") Long portfolioId,
                                  @Token String memberId) {
        return portfolioService.deletePortfolio(portfolioId, memberId);

    }




}
