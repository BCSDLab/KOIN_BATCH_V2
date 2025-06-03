package in.koreatech.batch.domain.bus.writer;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.bus.model.CityBusTimetable;
import in.koreatech.batch.domain.bus.repository.CityBusTimetableBatchRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CityBusTimetableWriter implements ItemWriter<CityBusTimetable> {

    private final CityBusTimetableBatchRepository cityBusTimetableBatchRepository;

    @Override
    public void write(Chunk<? extends CityBusTimetable> chunk) {
        if (!chunk.isEmpty()) {
            cityBusTimetableBatchRepository.saveAll((List<CityBusTimetable>)chunk.getItems());
        }
    }
}
