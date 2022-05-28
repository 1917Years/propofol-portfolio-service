package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.ptfservice.domain.portfolio.entity.PortfolioTag;
import propofol.ptfservice.domain.portfolio.entity.ProjectTag;

import java.util.List;

public interface PortfolioTagRepository extends JpaRepository<PortfolioTag, Long> {

    List<PortfolioTag> findAllByPortfolioId(Long portfolioId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PortfolioTag t where t.portfolio.id =:portfolioId")
    void deleteAllTags(@Param(value = "portfolioId") Long portfolioId);

}
