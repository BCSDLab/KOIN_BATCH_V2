package in.koreatech.batch.domain.dining.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DiningCrawlingJobConfig {

    private final DiningCrawlingTasklet diningCrawlingTasklet;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job diningCrawlingJob() {
        return new JobBuilder("diningCrawlingJob", jobRepository)
            .start(diningCrawlingStep())
            .build();
    }

    @Bean
    public Step diningCrawlingStep() {
        return new StepBuilder("diningCrawlingStep", jobRepository)
            .tasklet(diningCrawlingTasklet, transactionManager)
            .build();
    }
}
