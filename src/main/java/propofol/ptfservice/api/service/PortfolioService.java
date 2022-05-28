package propofol.ptfservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.ptfservice.api.common.exception.NotMatchMemberException;
import propofol.ptfservice.domain.exception.NotFoundPortfolioException;
import propofol.ptfservice.domain.portfolio.entity.*;
import propofol.ptfservice.domain.portfolio.repository.*;
import propofol.ptfservice.domain.portfolio.service.dto.*;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final AwardRepository awardRepository;
    private final CareerRepository careerRepository;
    private final ProjectRepository projectRepository;
    private final PortfolioTagService portfolioTagService;

    /**
     * 포트폴리오 저장 - 개별 생성
     */
    @Transactional
    public String saveAward(Award award) {
        awardRepository.save(award);
        return "ok";
    }

    @Transactional
    public String saveCareer(Career career) {
        careerRepository.save(career);
        return "ok";
    }

    @Transactional
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }


    /**
     * 포트폴리오 저장 - 한번에 저장할 때
     */
    @Transactional
    public Portfolio savePortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    /**
     * 포트폴리오 태그(스킬) 저장
     */
    @Transactional
    public void saveTags(List<Long> tagIds, Portfolio savedPortfolio) {
        if(tagIds != null) {
            List<PortfolioTag> portfolioTags = new ArrayList<>();
            tagIds.forEach(id -> {
                PortfolioTag tag = PortfolioTag.createTag().tagId(id).build();
                tag.changePortfolio(savedPortfolio);
                portfolioTags.add(tag);
            });

            /** TODO 코드받아서 유저서비스랑 연동도 해주기!!*/
            portfolioTagService.saveAllTags(portfolioTags);
        }
    }


    /**
     * 포트폴리오 수정 - 기본 유저 정보
     */
    @Transactional
    public void updateBasicInfo(String github, String job, String content, Long portfolioId) {
        Portfolio findPortfolio = findPortfolio(portfolioId);
        findPortfolio.updatePortfolio(github, job, content);
    }

    /**
     * 포트폴리오 생성 - 수상 경력 정보
     */
    public Award createAward(AwardDto awardDto, Long portfolioId) {
        Award award = Award.createAward()
                .name(awardDto.getName())
                .date(awardDto.getDate()).build();
        Portfolio portfolio = findPortfolio(portfolioId);
        award.addPortfolio(portfolio);
        return award;
    }

    /**
     * 포트폴리오 생성 - 경력 정보
     */
    public Career createCareer(CareerDto careerDto, Long portfolioId) {
        Career career = Career.createCareer()
                .title(careerDto.getTitle())
                .content(careerDto.getContent())
                .startTerm(careerDto.getStartTerm())
                .endTerm(careerDto.getEndTerm()).build();
        Portfolio portfolio = findPortfolio(portfolioId);
        career.addPortfolio(portfolio);
        return career;
    }

    /**
     * 포트폴리오 생성 - 프로젝트 정보
     */
    public Project createProject(ProjectDto projectDto, Long portfolioId) {
        Project project = Project.createProject()
                .title(projectDto.getTitle())
                .content(projectDto.getContent())
                .job(projectDto.getJob())
                .startTerm(projectDto.getStartTerm())
                .endTerm(projectDto.getEndTerm()).build();

        Portfolio portfolio = findPortfolio(portfolioId);
        project.addPortfolio(portfolio);
        return project;
    }

    /**
     * 포트폴리오 생성
     */
    public Portfolio createPortfolio(PortfolioDto portfolioDto) {
        Portfolio portfolio = Portfolio.createPortfolio()
                .template(portfolioDto.getTemplate())
                .github(portfolioDto.getGithub())
                .job(portfolioDto.getJob())
                .content(portfolioDto.getContent()).build();

        portfolioDto.getCareers().forEach(career -> {
            Career createdCareer = getCareer(career);
            portfolio.addCareer(createdCareer);
        });

        portfolioDto.getAwards().forEach(award -> {
            Award createdAward = getAward(award);
            portfolio.addArchive(createdAward);
        });


        portfolioDto.getProjects().forEach(project -> {
            Project createdProject = getProject(project);
            portfolio.addProject(createdProject);
        });


        return portfolio;
    }


    /**
     * 포트폴리오 가져오기 (by CreatedBy)
     */
    public Portfolio getPortfolioInfo(Long memberId) {
        Portfolio findPortfolio = portfolioRepository.findPortfolioByCreatedBy(String.valueOf(memberId)).orElse(null);
        return findPortfolio;
    }

    /**
     * 포트폴리오 가져오기 (by MemberId)
     */
    public Portfolio getMemberPortfolio(Long memberId) {
        return portfolioRepository.findPortfolioByMemberId(memberId).orElse(null);
    }


    /**
     * 템플릿 수정
     */
    @Transactional
    public String updateTemplate(Long portfolioId, Long memberId, Template template) {
        Portfolio findPortfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> {
            throw new NotFoundPortfolioException("포트폴리오를 찾을 수 없습니다.");
        });

        // 포트폴리오 작성자가 아니라면
        if(!findPortfolio.getCreatedBy().equals(String.valueOf(memberId)))
            throw new NotMatchMemberException("권한이 없습니다.");

        findPortfolio.updateTemplate(template);
        return "ok";
    }


    public Award getAward(AwardDto award) {
        Award createdAward = Award.createAward()
                .name(award.getName())
                .date(award.getDate())
                .build();
        return createdAward;
    }

    public Career getCareer(CareerDto career) {
        Career createdCareer = Career.createCareer()
                .title(career.getTitle())
                .content(career.getContent())
                .startTerm(career.getStartTerm())
                .endTerm(career.getEndTerm())
                .build();
        return createdCareer;
    }

    public Project getProject(ProjectDto project) {
        Project createdProject = Project.createProject()
                .title(project.getTitle())
                .content(project.getContent())
                .job(project.getJob())
                .startTerm(project.getStartTerm())
                .endTerm(project.getEndTerm())
                .build();

        return createdProject;
    }

    private Portfolio findPortfolio(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> {
                    throw new NotFoundPortfolioException("포트폴리오를 찾을 수 없습니다.");
                });
    }

}
