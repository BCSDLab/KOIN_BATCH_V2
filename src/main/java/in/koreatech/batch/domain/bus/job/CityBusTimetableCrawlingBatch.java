package in.koreatech.batch.domain.bus.job;

import static in.koreatech.batch.domain.bus.dto.CityBusRouteInfoApiResponse.CityBusRouteInfo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import in.koreatech.batch.domain.bus.model.CityBusTimetable;
import in.koreatech.batch.domain.bus.processor.CityBusTimetableProcessor;
import in.koreatech.batch.domain.bus.reader.RouteIdReader;
import in.koreatech.batch.domain.bus.writer.CityBusTimetableWriter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CityBusTimetableCrawlingBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final RouteIdReader routeIdReader;
    private final CityBusTimetableWriter cityBusTimetableWriter;
    private final CityBusTimetableProcessor cityBusTimetableProcessor;

    @Bean
    public Job cityBusTimetableCrawlingBatchJob() {
        return new JobBuilder("cityBusTimetableCrawlingBatchJob", jobRepository)
            .start(cityBusTimetableCrawlingStep())
            .build();
    }

    @Bean
    public Step cityBusTimetableCrawlingStep() {
        return new StepBuilder("cityBusTimetableCrawlingBatchJob", jobRepository)
            .<CityBusRouteInfo, CityBusTimetable> chunk(10, platformTransactionManager)
            .reader(routeIdReader)
            .processor(cityBusTimetableProcessor)
            .writer(cityBusTimetableWriter)
            .build();
    }
}
