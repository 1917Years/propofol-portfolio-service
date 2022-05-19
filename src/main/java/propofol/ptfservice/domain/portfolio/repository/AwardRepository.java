package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.ptfservice.domain.portfolio.entity.Award;

public interface AwardRepository extends JpaRepository<Award, Long> {

}
