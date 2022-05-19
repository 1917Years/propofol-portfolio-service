package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import propofol.ptfservice.domain.portfolio.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Portfolio extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="portfolio_id")
    private Long id;

    private String github; // 깃허브 주소
    private String job; // 직무명
    private String content; // 한줄소개

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Template template; // 포트폴리오 템플릿

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.PERSIST)
    private List<Award> awards = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.PERSIST)
    private List<Career> careers = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.PERSIST)
    private List<Project> projects = new ArrayList<>();


    /**TODO 스킬은 나중에 til이랑 연동해야 함. 지금은 임시로 만들어둠! */
    /**TODO discovery-server에 ptf-service 등록해주기*/

    public void addArchive(Award award) {
        awards.add(award);
        award.addPortfolio(this);
    }

    public void addCareer(Career career) {
        careers.add(career);
        career.addPortfolio(this);
    }

    public void addProject(Project project) {
        projects.add(project);
        project.addPortfolio(this);
    }

    @Builder(builderMethodName = "createPortfolio")
    public Portfolio(String github, String job, String content, Template template) {
        this.github = github;
        this.job = job;
        this.content = content;
        this.template = template;
    }

    public void updatePortfolio(String github, String job, String content) {
        if(github!=null) this.github = github;
        if(job!=null) this.job = job;
        if(content!=null) this.content = content;
    }

    public void updateTemplate(Template template) {
        if(template!=null) this.template = template;
    }


}
