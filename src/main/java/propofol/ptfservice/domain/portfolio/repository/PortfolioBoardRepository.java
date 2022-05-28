package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.ptfservice.domain.portfolio.entity.PortfolioBoard;

import java.util.List;

public interface PortfolioBoardRepository extends JpaRepository<PortfolioBoard, Long> {

    List<PortfolioBoard> findAllByPortfolioId(Long portfolioId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PortfolioBoard p where p.portfolio.id =:portfolioId")
    void deleteAllBoards(@Param(value = "portfolioId") Long portfolioId);
}
