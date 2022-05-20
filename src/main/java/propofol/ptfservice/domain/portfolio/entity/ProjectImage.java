package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import propofol.ptfservice.domain.portfolio.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectImage extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="image_id")
    private Long id;

    private String uploadFileName; // 업로드된 파일 이름
    private String storeFileName; // 저장 이름
    private String contentType; // 타입

    @OneToOne(mappedBy = "projectImage")
    private Project project;

    public void changeProject(Project project){
        this.project = project;
    }

    @Builder(builderMethodName = "createImage")
    public ProjectImage(String uploadFileName, String storeFileName, String contentType) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.contentType = contentType;
    }
}
