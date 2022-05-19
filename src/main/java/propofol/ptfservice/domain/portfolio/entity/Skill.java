package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id")
    private Project project;

    public void addProject(Project project) {
        this.project = project;
    }

    @Builder(builderMethodName = "createSkill")
    public Skill(String name) {
        this.name = name;
    }
}
