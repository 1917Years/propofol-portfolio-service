package propofol.ptfservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.ptfservice.domain.portfolio.entity.ProjectTag;
import propofol.ptfservice.domain.portfolio.repository.ProjectTagRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTagService {

    private final ProjectTagRepository projectTagRepository;

    @Transactional
    public void saveAllTags(List<ProjectTag> tags) {
        projectTagRepository.saveAll(tags);
    }

    public List<ProjectTag> findAllByProjectId(Long projectId) {
        return projectTagRepository.findAllByProjectId(projectId);
    }

    @Transactional
    public void deleteAllTags(Long projectId) {
        projectTagRepository.deleteAllTags(projectId);
    }
}
