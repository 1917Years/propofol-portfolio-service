package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

// 수상경력 넣는 엔티티
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Award {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="award_id")
    private Long id;

    // 수상한 상 이름
    private String name;

    // 수상 일자
    private String date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="portfolio_id")
    private Portfolio portfolio;

    public void addPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    @Builder(builderMethodName = "createAward")
    public Award(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public void updateAward(String name, String date){
        if(name!=null) this.name = name;
        if(date!=null) this.date = date;
    }


}
