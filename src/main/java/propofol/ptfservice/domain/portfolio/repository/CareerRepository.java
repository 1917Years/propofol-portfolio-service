package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.ptfservice.domain.portfolio.entity.Career;

public interface CareerRepository extends JpaRepository<Career, Long> {
}
