package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// 포트폴리오 경력 정보
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Career {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="career_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    // 경력명
    private String title;

    // 경력 설명
    private String content;

    // 경력 기간 - yyyy.mm 형태로 받을 예정
    private String startTerm;
    private String endTerm;

    public void addPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    @Builder(builderMethodName = "createCareer")
    public Career(String title, String content, String startTerm, String endTerm) {
        this.title = title;
        this.content = content;
        this.startTerm = startTerm;
        this.endTerm = endTerm;
    }

    public void updateCareer(String title, String content, String startTerm, String endTerm) {
        if(title != null) this.title = title;
        if(content!=null) this.content = content;
        if(startTerm != null) this.startTerm = startTerm;
        if(endTerm != null) this.endTerm = endTerm;
    }
}
