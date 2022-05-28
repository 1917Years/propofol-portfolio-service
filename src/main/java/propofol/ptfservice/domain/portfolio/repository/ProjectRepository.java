package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import propofol.ptfservice.domain.portfolio.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
