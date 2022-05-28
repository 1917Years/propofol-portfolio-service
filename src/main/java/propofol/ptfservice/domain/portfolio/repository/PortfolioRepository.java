package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.ptfservice.domain.portfolio.entity.Portfolio;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @Query("select p from Portfolio p where p.createdBy =:memberId")
    Optional<Portfolio> findPortfolioByCreatedBy(@Param(value = "memberId") String memberId);

    @Query("select p from Portfolio p where p.memberId =:memberId")
    Optional<Portfolio> findPortfolioByMemberId(@Param(value = "memberId") Long memberId);

}
