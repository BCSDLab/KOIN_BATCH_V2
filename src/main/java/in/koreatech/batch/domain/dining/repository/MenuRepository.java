package in.koreatech.batch.domain.dining.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.koreatech.batch.domain.dining.model.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByDateAndType(LocalDate date, String type);
}
