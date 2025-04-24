package in.koreatech.batch.domain.bus.repository;

import static org.springframework.data.mongodb.core.BulkOperations.BulkMode.UNORDERED;

import java.util.List;

import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import in.koreatech.batch.domain.bus.model.CityBusTimetable;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CityBusTimetableBatchRepository {

    private final MongoTemplate mongoTemplate;

    public void saveAll(List<CityBusTimetable> timetables) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(UNORDERED, CityBusTimetable.class);

        timetables.forEach(timetable -> {
            Query query = Query.query(Criteria.where("_id").is(timetable.getRouteId()));

            Update update = new Update()
                .set("updatedAt", timetable.getUpdatedAt())
                .set("busInfo", timetable.getBusInfo())
                .set("busTimetables", timetable.getBusTimetables());

            bulkOps.upsert(query, update);
        });

        bulkOps.execute();
    }
}
