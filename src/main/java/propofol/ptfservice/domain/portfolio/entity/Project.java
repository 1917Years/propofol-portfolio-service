package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 포트폴리오의 프로젝트 정보
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    // 프로젝트명
    private String title;

    // 개발 날짜 (yyyy.mm 형태)
    private String startTerm;
    private String endTerm;

    // 프로젝트 설명
    private String content;

    // 맡은 직군
    private String job;

    // 사용 기술
//    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
//    private List<Skill> projectSkills = new ArrayList<>();

//    // 프로젝트 이미지
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="image_id")
//    private ProjectImage projectImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="portfolio_id")
    private Portfolio portfolio;

    public void addPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

//    public void addImage(ProjectImage projectImage) {this.projectImage = projectImage;}

//    public void addProjectSkills(Skill skill) {
//        projectSkills.add(skill);
//        skill.addProject(this);
//    }

    @Builder(builderMethodName = "createProject")
    public Project(String title, String startTerm, String endTerm, String content, String job) {
        this.title = title;
        this.startTerm = startTerm;
        this.endTerm = endTerm;
        this.content = content;
        this.job = job;
    }

    public void updateProject(String title, String startTerm, String endTerm, String content, String job) {
        if(title!=null) this.title = title;
        if(startTerm!=null) this.startTerm = startTerm;
        if(endTerm!=null) this.endTerm = endTerm;
        if(content!=null) this.content = content;
        if(job!=null) this.job = job;
    }
}
