package propofol.ptfservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.ptfservice.domain.portfolio.entity.Portfolio;
import propofol.ptfservice.domain.portfolio.entity.PortfolioBoard;
import propofol.ptfservice.domain.portfolio.repository.PortfolioBoardRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioBoardService {
    private final PortfolioBoardRepository portfolioBoardRepository;

    @Transactional
    public void saveBoard(String boardId, String title, String content, String date, String recommend, Portfolio portfolio) {

        System.out.println("글 내용 ㅇㅇㅇㅇㅇㅇ");
        System.out.println(content); // 개쌉버그 글자수 왜 적게 들어감???????

        PortfolioBoard createdBoard = PortfolioBoard.createPortfolioBoard()
                .boardId(boardId)
                .title(title)
                .content(content)
                .date(date)
                .recommend(recommend)
                .build();

        createdBoard.changePortfolio(portfolio);
        portfolioBoardRepository.save(createdBoard);

    }

    public List<PortfolioBoard> findByPortfolioId(Long portfolioId) {
        return portfolioBoardRepository.findAllByPortfolioId(portfolioId);
    }

    @Transactional
    public void deleteAllBoards(Long memberId) {
        portfolioBoardRepository.deleteAllBoards(memberId);
    }

}
