package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.ptfservice.domain.portfolio.entity.Project;
import propofol.ptfservice.domain.portfolio.entity.ProjectImage;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<ProjectImage, Long> {

    @Query("select i from ProjectImage i where i.project.id =:projectId")
    Optional<ProjectImage> findImageByProjectId(@Param(value = "projectId") Long projectId);

    Optional<ProjectImage> findImageByStoreFileName(String storeFileName);

    List<ProjectImage> findAllByProjectId(Long projectId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ProjectImage i where i.project.id = :projectId")
    void deleteImage(@Param("projectId") Long projectId);
}
