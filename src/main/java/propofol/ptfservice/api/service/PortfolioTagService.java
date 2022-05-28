package propofol.ptfservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.ptfservice.domain.portfolio.entity.PortfolioTag;
import propofol.ptfservice.domain.portfolio.repository.PortfolioTagRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioTagService {

    private final PortfolioTagRepository portfolioTagRepository;

    @Transactional
    public void saveAllTags(List<PortfolioTag> tags) {
        portfolioTagRepository.saveAll(tags);
    }

    public List<PortfolioTag> findAllByPortfolioId(Long portfolioId) {
        return portfolioTagRepository.findAllByPortfolioId(portfolioId);
    }

    @Transactional
    public void deleteAllTags(Long portfolioId) {
        portfolioTagRepository.deleteAllTags(portfolioId);
    }
}
