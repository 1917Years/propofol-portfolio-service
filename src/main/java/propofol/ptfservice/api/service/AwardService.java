package propofol.ptfservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.ptfservice.api.common.exception.NotMatchMemberException;
import propofol.ptfservice.domain.exception.NotFoundAwardException;
import propofol.ptfservice.domain.exception.NotFoundPortfolioException;
import propofol.ptfservice.domain.portfolio.entity.Award;
import propofol.ptfservice.domain.portfolio.entity.Portfolio;
import propofol.ptfservice.domain.portfolio.repository.AwardRepository;
import propofol.ptfservice.domain.portfolio.repository.PortfolioRepository;
import propofol.ptfservice.domain.portfolio.service.dto.AwardDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwardService {
    private final PortfolioRepository portfolioRepository;
    private final AwardRepository awardRepository;
    private final PortfolioService portfolioService;

    /**
     * 포트폴리오 수정 - 수상 경력 수정
     */

    @Transactional
    public String updateAward(Long memberId, Long awardId, AwardDto awardDto) {
        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);

        // 포트폴리오 작성자가 아니라면
        if(!findPortfolio.getCreatedBy().equals(String.valueOf(memberId)))
            throw new NotMatchMemberException("권한이 없습니다.");

        Award findAward = getAward(awardId);

        Award createdAward = portfolioService.getAward(awardDto);
        findAward.updateAward(createdAward.getName(), createdAward.getDate());
        return "ok";
    }

    /**
     * 포트폴리오 삭제 - 수상 경력 삭제
     */
    @Transactional
    public String deleteAward(Long memberId, Long awardId) {
        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);

        // 포트폴리오 작성자가 아니라면
        if(!findPortfolio.getCreatedBy().equals(String.valueOf(memberId)))
            throw new NotMatchMemberException("권한이 없습니다.");

        Award findAward = getAward(awardId);
        awardRepository.delete(findAward);
        return "ok";
    }


    private Award getAward(Long awardId) {
        Award findAward = awardRepository.findById(awardId).orElseThrow(() -> {
            throw new NotFoundAwardException("수상 경력이 존재하지 않습니다.");
        });
        return findAward;
    }


}
