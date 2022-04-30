package propofol.ptfservice.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.ptfservice.api.common.exception.NotMatchMemberException;
import propofol.ptfservice.api.controller.dto.MemberInfoResponseDto;
import propofol.ptfservice.api.feign.UserServiceFeignClient;
import propofol.ptfservice.domain.exception.NotFoundPortfolioException;
import propofol.ptfservice.domain.portfolio.entity.Archive;
import propofol.ptfservice.domain.portfolio.entity.Career;
import propofol.ptfservice.domain.portfolio.entity.Portfolio;
import propofol.ptfservice.domain.portfolio.entity.Project;
import propofol.ptfservice.domain.portfolio.repository.ArchiveRepository;
import propofol.ptfservice.domain.portfolio.repository.CareerRepository;
import propofol.ptfservice.domain.portfolio.repository.PortfolioRepository;
import propofol.ptfservice.domain.portfolio.repository.ProjectRepository;
import propofol.ptfservice.domain.portfolio.service.dto.ArchiveDto;
import propofol.ptfservice.domain.portfolio.service.dto.CareerDto;
import propofol.ptfservice.domain.portfolio.service.dto.PortfolioDto;
import propofol.ptfservice.domain.portfolio.service.dto.ProjectDto;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserServiceFeignClient userServiceFeignClient;
    private final ArchiveRepository archiveRepository;
    private final CareerRepository careerRepository;
    private final ProjectRepository projectRepository;

    /**
     * 포트폴리오 저장
     */
    public String savePortfolio(Portfolio portfolio) {
        portfolioRepository.save(portfolio);
        return "ok";
    }

    /**
     * 포트폴리오 생성
     */
    public Portfolio createPortfolio(PortfolioDto portfolioDto) {
        Portfolio portfolio = Portfolio.createPortfolio()
                .template(portfolioDto.getTemplate()).build();

        portfolioDto.getCareers().forEach(career -> {
            Career createdCareer = getCareer(career);
            portfolio.addCareer(createdCareer);
        });

        portfolioDto.getProjects().forEach(project -> {
            Project createdProject = getProject(project);
            portfolio.addProject(createdProject);
        });

        portfolioDto.getArchives().forEach(archive -> {
            Archive createdArchive = getArchive(archive);
            portfolio.addArchive(createdArchive);
        });

        return portfolio;
    }

    /**
     * 유저 정보 가져오기
     */
    public MemberInfoResponseDto getMemberInfo(String token) {
        MemberInfoResponseDto memberInfo = userServiceFeignClient.getMemberInfo(token);
        return memberInfo;
    }

    /**
     * 포트폴리오 가져오기 (by CreatedBy)
     */
    public Portfolio getPortfolioInfo(String memberId) {
        Portfolio findPortfolio = portfolioRepository.findPortfolioByCreatedBy(memberId).orElseThrow(() -> {
            throw new NotFoundPortfolioException("포트폴리오를 찾을 수 없습니다.");
        });

        return findPortfolio;
    }


    /**
     * 포트폴리오 수정 - 나중에 수정
     */
    @Transactional
    public String updatePortfolio(Long portfolioId, String memberId, PortfolioDto portfolioDto) {
        Portfolio findPortfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> {
            throw new NotFoundPortfolioException("포트폴리오를 찾을 수 없습니다.");
        });

        // 포트폴리오 작성자가 아니라면
        if(!findPortfolio.getCreatedBy().equals(memberId))
            throw new NotMatchMemberException("권한이 없습니다.");

        List<Archive> archives = new ArrayList<>();
        List<Career> careers = new ArrayList<>();
        List<Project> projects = new ArrayList<>();

        if(portfolioDto.getCareers().size() != 0) {
            portfolioDto.getCareers().forEach(career -> {
                Career createdCareer = getCareer(career);
                careers.add(createdCareer);
            });
        }

        portfolioDto.getProjects().forEach(project -> {
            Project createdProject = getProject(project);
            projects.add(createdProject);
        });

        portfolioDto.getArchives().forEach(archive -> {
            Archive createdArchive = getArchive(archive);
            archives.add(createdArchive);
        });

        findPortfolio.updatePortfolio(portfolioDto.getTemplate(), archives, careers, projects);

        return "ok";
    }

    /**
     * 포트폴리오 삭제
     */
    public String deletePortfolio(Long portfolioId, String memberId) {
        Portfolio findPortfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> {
            throw new NotFoundPortfolioException("포트폴리오를 찾을 수 없습니다.");
        });

        // 포트폴리오 작성자가 아니라면
        if(!findPortfolio.getCreatedBy().equals(memberId))
            throw new NotMatchMemberException("권한이 없습니다.");

        // 관련된 테이블 모두 삭제
        archiveRepository.bulkDeleteAll(portfolioId);
        projectRepository.bulkDeleteAll(portfolioId);
        careerRepository.bulkDeleteAll(portfolioId);

        portfolioRepository.delete(findPortfolio);
        return "ok";
    }


    /**
     * DTO에서 정보를 빼와서 entity 객체로 만들어주는 보조 함수들
     */
    private Archive getArchive(ArchiveDto archive) {
        Archive createdArchive = Archive.createArchive()
                .content(archive.getContent())
                .link(archive.getLink())
                .build();
        return createdArchive;
    }

    private Project getProject(ProjectDto project) {
        Project createdProject = Project.createProject()
                .title(project.getTitle())
                .basicContent(project.getBasicContent())
                .projectLink(project.getProjectLink())
                .detailContent(project.getDetailContent())
                .skill(project.getSkill())
                .startTerm(project.getStartTerm())
                .endTerm(project.getEndTerm())
                .build();
        return createdProject;
    }

    private Career getCareer(CareerDto career) {
        Career createdCareer = Career.createCareer()
                .title(career.getTitle())
                .basicContent(career.getBasicContent())
                .detailContent(career.getDetailContent())
                .startTerm(career.getStartTerm())
                .endTerm(career.getEndTerm())
                .build();
        return createdCareer;
    }

}
