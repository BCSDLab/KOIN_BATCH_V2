package in.koreatech.batch.domain.dining.job;

import java.time.Clock;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import in.koreatech.batch.domain.dining.model.CrawledDiningMenu;
import in.koreatech.batch.domain.dining.model.Dining;
import in.koreatech.batch.domain.dining.reader.TodayDiningCrawlerReader;
import in.koreatech.batch.domain.dining.repository.DiningRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TodayDiningCrawlingBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DiningRepository diningRepository;
    private final TodayDiningCrawlerReader todayDiningCrawlerReader;

    @Bean
    public Job TodayDiningCrawlingJob() {
        return new JobBuilder("todayDiningCrawlingBatch", jobRepository)
            .start(TodayDiningCrawlingStep())
            .build();
    }

    @Bean
    public Step TodayDiningCrawlingStep() {
        return new StepBuilder("todayDiningCrawlingBatch", jobRepository)
            .<CrawledDiningMenu, Dining> chunk(5, platformTransactionManager)
            .reader(todayDiningCrawlerReader)
            .build();
    }
}
