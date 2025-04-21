package in.koreatech.batch.domain.dining.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.dining.model.Dining;
import in.koreatech.batch.domain.dining.repository.DiningRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DiningWriter implements ItemWriter<Dining> {

    private final DiningRepository diningRepository;

    @Override
    public void write(Chunk<? extends Dining> chunk) {
        diningRepository.saveAll(chunk.getItems());
    }
}
