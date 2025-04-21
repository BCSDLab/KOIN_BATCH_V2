package in.koreatech.batch.domain.dining.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import in.koreatech.batch.domain.dining.model.Dining;

public interface DiningRepository extends CrudRepository<Dining, Integer> {
    void saveAll(List<Dining> dinings);
}
