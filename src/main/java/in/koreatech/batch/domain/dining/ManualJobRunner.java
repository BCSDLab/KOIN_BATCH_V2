package in.koreatech.batch.domain.dining;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ManualJobRunner implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job crawlDiningMenusJob;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("manualRunAt", LocalDateTime.now().toString())
                .toJobParameters();

        jobLauncher.run(crawlDiningMenusJob, jobParameters);
    }
}
