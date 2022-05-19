package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.ptfservice.domain.portfolio.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
