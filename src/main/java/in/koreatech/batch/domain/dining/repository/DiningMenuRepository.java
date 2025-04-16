package in.koreatech.batch.domain.dining.repository;

import org.springframework.data.repository.CrudRepository;

import in.koreatech.batch.domain.dining.model.DiningMenu;

public interface DiningMenuRepository extends CrudRepository<DiningMenu, Integer> {
}
