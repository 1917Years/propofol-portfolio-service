package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PortfolioBoard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_board_id")
    private Long id;

    private String title;
    @Column(length = 50000)
    private String content;
    private String recommend;
    private String date;
    private String boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Builder(builderMethodName = "createPortfolioBoard")
    public PortfolioBoard(String boardId, String title, String content, String date, String recommend) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.date = date;
        this.recommend = recommend;
    }

    public void changePortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}
