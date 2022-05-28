package propofol.ptfservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.ptfservice.api.common.exception.NotMatchMemberException;
import propofol.ptfservice.domain.exception.NotFoundPortfolioException;
import propofol.ptfservice.domain.exception.NotFoundProjectException;
import propofol.ptfservice.domain.portfolio.entity.Portfolio;
import propofol.ptfservice.domain.portfolio.entity.Project;
import propofol.ptfservice.domain.portfolio.entity.ProjectTag;
import propofol.ptfservice.domain.portfolio.repository.ImageRepository;
import propofol.ptfservice.domain.portfolio.repository.PortfolioRepository;
import propofol.ptfservice.domain.portfolio.repository.ProjectRepository;
import propofol.ptfservice.domain.portfolio.service.dto.ProjectDto;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final PortfolioRepository portfolioRepository;
    private final ProjectRepository projectRepository;
    private final ImageService imageService;
    private final ProjectTagService projectTagService;

    /**
     * 포트폴리오 삭제 - 프로젝트 정보 삭제
     */
    @Transactional
    public String deleteProject(Long portfolioId, Long projectId, Long memberId) {
        Portfolio findPortfolio = getPortfolio(portfolioId);

        // 포트폴리오 작성자가 아니라면
        if(!findPortfolio.getCreatedBy().equals(String.valueOf(memberId)))
            throw new NotMatchMemberException("권한이 없습니다.");

        Project findProject = getProject(projectId);
        imageService.deleteAllImages(projectId);
        projectTagService.deleteAllTags(projectId);
        projectRepository.delete(findProject);
        return "ok";
    }

    /**
     * 프로젝트 태그 저장
     */
    @Transactional
    public void saveTags(List<Long> tagIds, Project savedProject) {
        if(tagIds != null) {
            List<ProjectTag> projectTags = new ArrayList<>();
            tagIds.forEach(id -> {
                ProjectTag tag = ProjectTag.createTag().tagId(id).build();
                tag.changeProject(savedProject);
                projectTags.add(tag);
            });
            projectTagService.saveAllTags(projectTags);
        }
    }

    private Portfolio getPortfolio(Long portfolioId) {
        Portfolio findPortfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> {
            throw new NotFoundPortfolioException("포트폴리오를 찾을 수 없습니다.");
        });
        return findPortfolio;
    }

    private Project getProject(Long projectId) {
        Project findProject = projectRepository.findById(projectId).orElseThrow(() -> {
            throw new NotFoundProjectException("프로젝트를 찾을 수 없습니다.");
        });
        return findProject;
    }


}
