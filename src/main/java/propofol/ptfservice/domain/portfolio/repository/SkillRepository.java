package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.ptfservice.domain.portfolio.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    @Modifying
    @Query(value = "delete from Skill s where s.project.id =:projectId")
    int deleteBulkSkills(@Param(value = "projectId") Long projectId);
}
